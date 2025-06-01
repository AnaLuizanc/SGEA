package controller;

import domain.Evento;
import domain.Participante;
import domain.Trabalho;
import domain.enums.StatusTrabalho;
import domain.enums.TipoPerfil;
import java.time.LocalDate;
import java.util.List;
import repository.EventoRepository;
import repository.InscricaoRepository;
import repository.ParticipanteRepository;
import repository.TrabalhoRepository;

/**
 *
 * @author enio1
 */
public class TrabalhoController {

    private final TrabalhoRepository trabalhoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;
    private final InscricaoRepository inscricaoRepository;

    public TrabalhoController(TrabalhoRepository trabalhoRepository, EventoRepository eventoRepository,
            ParticipanteRepository participanteRepository, InscricaoRepository inscricaoRepository) {
        this.trabalhoRepository = trabalhoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public Trabalho submeterTrabalho(String idAutor, String eventoId, String titulo, String arquivo) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));

        // Regra de Negócio 2: Submissão de trabalhos só é permitida dentro do período de submissão
        if (!evento.isPeriodoSubmissaoAberto(LocalDate.now())) {
            throw new IllegalStateException("Período de submissão para o evento '" + evento.getNome() + "' está fechado.");
        }

        if (idAutor == null) {
            throw new IllegalArgumentException("Trabalho deve ter pelo menos um autor.");
        }

        Participante autor = participanteRepository.findById(idAutor)
                .orElseThrow(() -> new IllegalArgumentException("Autor com ID " + idAutor + " não encontrado."));

        // Regra de Negócio 3: Um participante só pode submeter um trabalho a um evento se estiver inscrito nele.
        boolean autorInscrito = evento.isParticipanteInscritoAtivo(autor);
        if (!autorInscrito) {
            throw new IllegalStateException("O autor deve estar inscrito ativamente no evento para submeter um trabalho.");
        }

        Trabalho novoTrabalho = new Trabalho(titulo, arquivo, evento, autor);
        novoTrabalho.setStatus(StatusTrabalho.SUBMETIDO);
        evento.adicionarTrabalhoInterno(novoTrabalho);
        // eventoRepository.save(evento);

        return trabalhoRepository.save(novoTrabalho);
    }

    public Trabalho designarAvaliadorParaTrabalho(String trabalhoId, String avaliadorId, String organizadorId) {
        Trabalho trabalho = trabalhoRepository.findById(trabalhoId)
                .orElseThrow(() -> new IllegalArgumentException("Trabalho com ID " + trabalhoId + " não encontrado."));
        Participante avaliador = participanteRepository.findById(avaliadorId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliador com ID " + avaliadorId + " não encontrado."));
        Participante organizador = participanteRepository.findById(organizadorId)
                .orElseThrow(() -> new IllegalArgumentException("Organizador com ID " + organizadorId + " não encontrado."));

        if (organizador.getTipoPerfil() != TipoPerfil.ORGANIZADOR
                || !trabalho.getEvento().getOrganizadorResponsavel().getId().equals(organizadorId)) {
            throw new IllegalStateException("Apenas o organizador responsável pelo evento pode designar avaliadores.");
        }
        if (avaliador.getTipoPerfil() != TipoPerfil.AVALIADOR) {
            throw new IllegalStateException("Participante " + avaliador.getNomeCompleto() + " não tem perfil de AVALIADOR.");
        }

        if (trabalho.getStatus() == StatusTrabalho.SUBMETIDO) {
            trabalho.setStatus(StatusTrabalho.EM_AVALIACAO);
        }
        return trabalhoRepository.save(trabalho);
    }

    public Trabalho atualizarStatusTrabalho(String trabalhoId, StatusTrabalho novoStatus, String responsavelId) {
        Trabalho trabalho = trabalhoRepository.findById(trabalhoId)
                .orElseThrow(() -> new IllegalArgumentException("Trabalho com ID " + trabalhoId + " não encontrado."));

        trabalho.setStatus(novoStatus);
        return trabalhoRepository.save(trabalho);
    }

    public List<Trabalho> listarTrabalhosPorEvento(String eventoId) {
        return trabalhoRepository.findAllByEventoId(eventoId);
    }

    public List<Trabalho> listarTrabalhosPorAvaliadorDesignado(String avaliadorId) {
        // List<Avaliacao> avaliacoesDoAvaliador = avaliacaoRepository.findAllByAvaliadorId(avaliadorId);
        // return avaliacoesDoAvaliador.stream().map(Avaliacao::getTrabalho).distinct().collect(Collectors.toList());
        throw new UnsupportedOperationException("Listar trabalhos por avaliador designado não implementado (requer modelo de designação).");
    }

    public List<Trabalho> listarTrabalhosPorAutor(String autorId) {
        if (autorId == null) {
            throw new IllegalArgumentException("ID do autor não pode ser nulo.");
        }
        return trabalhoRepository.findAllByAutorId(autorId);
    }
}
