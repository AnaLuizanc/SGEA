/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import domain.Evento;
import domain.Inscricao;
import domain.Participante;
import domain.enums.StatusInscricao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import repository.EventoRepository;
import repository.InscricaoRepository;
import repository.ParticipanteRepository;
/**
 *
 * @author enio1
 */
public class InscricaoController {
    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public InscricaoController(InscricaoRepository inscricaoRepository, EventoRepository eventoRepository, ParticipanteRepository participanteRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    public Inscricao realizarInscricao(String participanteId, String eventoId) {
        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + participanteId + " não encontrado."));
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));

        // RN 1: Capacidade máxima
        if (evento.isLotado()) {
            throw new IllegalStateException("Evento '" + evento.getNome() + "' atingiu a capacidade máxima.");
        }

        // RN: Participante não pode se inscrever múltiplas vezes no mesmo evento (se inscrição ativa)
        Optional<Inscricao> inscricaoExistente = inscricaoRepository.findByParticipanteIdAndEventoId(participanteId, eventoId);
        if (inscricaoExistente.isPresent() && inscricaoExistente.get().getStatus() == StatusInscricao.ATIVA) {
            throw new IllegalStateException("Participante já está inscrito ativamente neste evento.");
        }
        
        // RN: Verificar se período de inscrição do evento está aberto (se essa regra existir e for implementada em Evento)
        // Ex: if (!evento.isPeriodoInscricaoAberto(LocalDate.now())) {
        // throw new IllegalStateException("Período de inscrição para o evento '" + evento.getNome() + "' está fechado.");
        // }


        Inscricao novaInscricao = new Inscricao(LocalDate.now(), evento, participante);
        // Adiciona a inscrição à lista interna do evento (Information Expert)
        // O save do evento aqui é opcional dependendo de como a persistência de coleções é gerenciada.
        // Para repositórios em memória simples, modificar o objeto evento pode ser suficiente se for a mesma instância.
        // Em JPA, o @OneToMany faria a gestão.
        evento.adicionarInscricaoInterna(novaInscricao); 
        // eventoRepository.save(evento); // Se necessário para persistir a mudança na coleção de inscrições do evento

        return inscricaoRepository.save(novaInscricao);
    }

    public void cancelarInscricao(String inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição com ID " + inscricaoId + " não encontrada."));

        // RN 4: Prazo de cancelamento
        if (!inscricao.podeCancelar(LocalDate.now())) {
            throw new IllegalStateException("Prazo para cancelamento da inscrição expirado ou inscrição não está ativa.");
        }

        inscricao.setStatus(StatusInscricao.CANCELADA);
        // O evento não precisa ser "notificado" diretamente aqui para remover,
        // pois o status da inscrição já reflete isso. Contagens (isLotado) no Evento usam o status.
        inscricaoRepository.save(inscricao);
    }

    public Inscricao confirmarPresenca(String inscricaoId, String organizadorId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
            .orElseThrow(() -> new IllegalArgumentException("Inscrição com ID " + inscricaoId + " não encontrada."));
        
        Evento evento = inscricao.getEvento();
        if (!evento.getOrganizadorResponsavel().getId().equals(organizadorId)) {
             throw new IllegalStateException("Apenas o organizador responsável pelo evento pode confirmar presenças.");
        }

        // RN 5: Certificados de participação só são emitidos após a data de término do evento e com confirmação de presença.
        // A confirmação de presença pode ocorrer durante ou logo após o evento.
        // A regra de emissão do certificado é verificada no CertificadoService.
        if (LocalDate.now().isBefore(evento.getDataInicio())) {
             throw new IllegalStateException("Ainda não é possível confirmar presença antes do início do evento.");
        }
        if (inscricao.getStatus() != StatusInscricao.ATIVA) {
            throw new IllegalStateException("Não é possível confirmar presença para inscrição não ativa.");
        }

        inscricao.setPresencaConfirmada(true);
        return inscricaoRepository.save(inscricao);
    }

    public List<Inscricao> listarInscricoesPorEvento(String eventoId) {
        return inscricaoRepository.findAllByEventoId(eventoId);
    }
     public List<Inscricao> listarInscricoesPorParticipante(String participanteId) {
        return inscricaoRepository.findAllByParticipanteId(participanteId);
    }
}
