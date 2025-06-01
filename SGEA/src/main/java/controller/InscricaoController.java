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

        // Regra de Negócio 1: Capacidade máxima atingida
        if (evento.isLotado()) {
            throw new IllegalStateException("Evento '" + evento.getNome() + "' atingiu a capacidade máxima.");
        }

        Optional<Inscricao> inscricaoExistente = inscricaoRepository.findByParticipanteIdAndEventoId(participanteId, eventoId);
        if (inscricaoExistente.isPresent() && inscricaoExistente.get().getStatus() == StatusInscricao.ATIVA) {
            throw new IllegalStateException("Participante já está inscrito ativamente neste evento.");
        }

        Inscricao novaInscricao = new Inscricao(LocalDate.now(), evento, participante);
        evento.adicionarInscricaoInterna(novaInscricao); 
        eventoRepository.save(evento);

        return inscricaoRepository.save(novaInscricao);
    }

    public void cancelarInscricao(String inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição com ID " + inscricaoId + " não encontrada."));

        // Regra de Negócio 4: O cancelamento de inscrição só é permitido até X dias antes da data de início do evento (X a ser definido, ex: 2 dias).
        if (!inscricao.podeCancelar(LocalDate.now())) {
            throw new IllegalStateException("Prazo para cancelamento da inscrição expirado ou inscrição não está ativa.");
        }

        inscricao.setStatus(StatusInscricao.CANCELADA);
        inscricaoRepository.save(inscricao);
    }

    public Inscricao confirmarPresenca(String inscricaoId, String organizadorId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
            .orElseThrow(() -> new IllegalArgumentException("Inscrição com ID " + inscricaoId + " não encontrada."));
        
        Evento evento = inscricao.getEvento();
        if (!evento.getOrganizadorResponsavel().getId().equals(organizadorId)) {
             throw new IllegalStateException("Apenas o organizador responsável pelo evento pode confirmar presenças.");
        }

        // Regra de Negócio 5: Certificados de participação só são emitidos após a data de término do evento e com confirmação de presença.
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
