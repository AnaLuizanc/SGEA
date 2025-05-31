/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import domain.Certificado;
import domain.Evento;
import domain.Inscricao;
import domain.Participante;
import domain.Trabalho;
import domain.enums.TipoCertificado;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import repository.CertificadoRepository;
import repository.EventoRepository;
import repository.InscricaoRepository;
import repository.ParticipanteRepository;
import repository.TrabalhoRepository;
/**
 *
 * @author enio1
 */
public class CertificadoController {
    
    private final CertificadoRepository
            certificadoRepository;
    private final EventoRepository eventoRepository;
    private final InscricaoRepository inscricaoRepository;
    private final TrabalhoRepository trabalhoRepository;
    private final ParticipanteRepository participanteRepository; // Para certificados de organizador/avaliador

    public CertificadoController(CertificadoRepository certificadoRepository, EventoRepository eventoRepository,
                              InscricaoRepository inscricaoRepository, TrabalhoRepository trabalhoRepository,
                              ParticipanteRepository participanteRepository) {
        this.certificadoRepository = certificadoRepository;
        this.eventoRepository = eventoRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.trabalhoRepository = trabalhoRepository;
        this.participanteRepository = participanteRepository;
    }

    public List<Certificado> emitirCertificadosParticipacaoEvento(String eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));

        // RN 5: Certificados de participação só são emitidos após a data de término do evento
        if (LocalDate.now().isBefore(evento.getDataFim().plusDays(1))) { // Considerar 1 dia após o término
            throw new IllegalStateException("Certificados de participação só podem ser emitidos após o término do evento.");
        }

        List<Certificado> certificadosEmitidos = new ArrayList<>();
        List<Inscricao> inscricoesComPresenca = evento.getInscritosComPresencaConfirmada();

        for (Inscricao inscricao : inscricoesComPresenca) {
            // Evitar duplicidade de certificados
            boolean jaEmitido = certificadoRepository.findAllByParticipanteId(inscricao.getParticipante().getId()).stream()
                .anyMatch(c -> c.getEvento().getId().equals(eventoId) && c.getTipo() == TipoCertificado.PARTICIPACAO);

            if (!jaEmitido) {
                Certificado cert = new Certificado(TipoCertificado.PARTICIPACAO, inscricao.getParticipante(), evento);
                certificadosEmitidos.add(certificadoRepository.save(cert));
            }
        }
        return certificadosEmitidos;
    }

    public List<Certificado> emitirCertificadosApresentacaoTrabalhoEvento(String eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));

        // RN 6: Certificados de apresentação de trabalho só são emitidos para trabalhos aprovados e após a data de término do evento.
        if (LocalDate.now().isBefore(evento.getDataFim().plusDays(1))) {
            throw new IllegalStateException("Certificados de apresentação só podem ser emitidos após o término do evento.");
        }

        List<Certificado> certificadosEmitidos = new ArrayList<>();
        List<Trabalho> trabalhosNoEvento = trabalhoRepository.findAllByEventoId(eventoId);

        for (Trabalho trabalho : trabalhosNoEvento) {
            if (trabalho.foiAprovado() && trabalho.foiApresentado()) { // Usando métodos do domínio
                for (Participante autor : trabalho.getAutores()) {
                    // Evitar duplicidade
                    boolean jaEmitido = certificadoRepository.findAllByParticipanteId(autor.getId()).stream()
                        .anyMatch(c -> c.getTrabalho() != null && c.getTrabalho().getId().equals(trabalho.getId()) &&
                                       c.getTipo() == TipoCertificado.APRESENTACAO_TRABALHO);
                    if (!jaEmitido) {
                        Certificado cert = new Certificado(autor, evento, trabalho); // Construtor específico
                        certificadosEmitidos.add(certificadoRepository.save(cert));
                    }
                }
            }
        }
        return certificadosEmitidos;
    }

    public Certificado emitirCertificadoOrganizador(String eventoId, String organizadorId) {
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));
        Participante organizador = participanteRepository.findById(organizadorId)
            .orElseThrow(() -> new IllegalArgumentException("Organizador com ID " + organizadorId + " não encontrado."));

        if (!evento.getOrganizadorResponsavel().getId().equals(organizadorId)) {
            throw new IllegalArgumentException("Participante não é o organizador responsável deste evento.");
        }
         if (LocalDate.now().isBefore(evento.getDataFim().plusDays(1))) {
            throw new IllegalStateException("Certificados de organização só podem ser emitidos após o término do evento.");
        }
        
        boolean jaEmitido = certificadoRepository.findAllByParticipanteId(organizador.getId()).stream()
                .anyMatch(c -> c.getEvento().getId().equals(eventoId) && c.getTipo() == TipoCertificado.ORGANIZACAO);
        if (jaEmitido) {
            throw new IllegalStateException("Certificado de organização já emitido para este organizador neste evento.");
        }

        Certificado cert = new Certificado(TipoCertificado.ORGANIZACAO, organizador, evento);
        return certificadoRepository.save(cert);
    }


    // Similar para Certificado de Avaliador, se necessário.
}
