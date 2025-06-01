package facade;

import controller.AvaliacaoController;
import controller.CertificadoController;
import controller.EventoController;
import controller.InscricaoController;
import controller.ParticipanteController;
import controller.TrabalhoController;
import domain.Avaliacao;
import domain.Certificado;
import domain.Evento;
import domain.Inscricao;
import domain.Participante;
import domain.Trabalho;
import domain.enums.StatusTrabalho;
import domain.enums.TipoPerfil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import repository.AvaliacaoRepository;
import repository.CertificadoRepository;
import repository.EventoRepository;
import repository.InscricaoRepository;
import repository.ParticipanteRepository;
import repository.TrabalhoRepository;

/**
 *
 * @author enio1
 */
public class SGEAFacade {

    // Repositórios
    private final ParticipanteRepository participanteRepository;
    private final EventoRepository eventoRepository;
    private final InscricaoRepository inscricaoRepository;
    private final TrabalhoRepository trabalhoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final CertificadoRepository certificadoRepository;

    // Controllers
    private final ParticipanteController participanteController;
    private final EventoController eventoController;
    private final InscricaoController inscricaoController;
    private final TrabalhoController trabalhoController;
    private final AvaliacaoController avaliacaoController;
    private final CertificadoController certificadoController;

    public SGEAFacade() {
        // Inicialização dos Repositórios
        this.participanteRepository = new ParticipanteRepository();
        this.eventoRepository = new EventoRepository();
        this.inscricaoRepository = new InscricaoRepository();
        this.trabalhoRepository = new TrabalhoRepository();
        this.avaliacaoRepository = new AvaliacaoRepository();
        this.certificadoRepository = new CertificadoRepository();

        // Inicialização dos Controllers com suas dependências de repositório
        this.participanteController = new ParticipanteController(participanteRepository);
        this.eventoController = new EventoController(eventoRepository, participanteRepository);
        this.inscricaoController = new InscricaoController(inscricaoRepository, eventoRepository, participanteRepository);
        this.trabalhoController = new TrabalhoController(trabalhoRepository, eventoRepository, participanteRepository, inscricaoRepository);
        this.avaliacaoController = new AvaliacaoController(avaliacaoRepository, trabalhoRepository, participanteRepository);
        this.certificadoController = new CertificadoController(certificadoRepository, eventoRepository, inscricaoRepository, trabalhoRepository, participanteRepository);
    }

    // --- Métodos da Fachada ---
    public Participante cadastrarParticipante(String nomeCompleto, String email, String instituicao, TipoPerfil tipoPerfil) {
        return participanteController.cadastrarParticipante(nomeCompleto, email, instituicao, tipoPerfil);
    }

    public Optional<Participante> getParticipanteById(String id) {
        return participanteController.getParticipanteById(id);
    }

    public List<Participante> listarTodosParticipantes() {
        return participanteController.listarTodosParticipantes();
    }

    public Participante atualizarParticipante(String id, String nome, String email, String instituicao, TipoPerfil perfil) {
        return participanteController.atualizarParticipante(id, nome, email, instituicao, perfil);
    }

    public List<Evento> listarEventosDisponiveisParaInscricao() {
        return eventoController.listarEventosDisponiveisParaInscricao(LocalDate.now());
    }

    public List<Evento> listarTodosEventos() {
        return eventoController.listarTodosEventos();
    }

    public Inscricao inscreverEmEvento(String participanteId, String eventoId) {
        return inscricaoController.realizarInscricao(participanteId, eventoId);
    }

    public void cancelarInscricao(String inscricaoId) {
        inscricaoController.cancelarInscricao(inscricaoId);
    }

    public List<Inscricao> listarInscricoesPorParticipante(String participanteId) {
        return inscricaoController.listarInscricoesPorParticipante(participanteId);
    }

    public Trabalho submeterTrabalho(String idAutor, String eventoId, String titulo, String arquivo) {
        return trabalhoController.submeterTrabalho(idAutor, eventoId, titulo, arquivo);
    }

    public List<Trabalho> listarTrabalhosPorEvento(String eventoId) {
        return trabalhoController.listarTrabalhosPorEvento(eventoId);
    }

    public Optional<Trabalho> getTrabalhoById(String trabalhoId) {
        return trabalhoRepository.findById(trabalhoId);
    }

    public Evento cadastrarEvento(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim,
            String local, int capacidadeMaxima, String organizadorId,
            LocalDate periodoSubmissaoInicio, LocalDate periodoSubmissaoFim) {
        return eventoController.cadastrarEvento(nome, descricao, dataInicio, dataFim, local, capacidadeMaxima, organizadorId, periodoSubmissaoInicio, periodoSubmissaoFim);
    }

    public Optional<Evento> getEventoById(String eventoId) {
        return eventoRepository.findById(eventoId);
    }

    public Evento definirPeriodoSubmissaoTrabalhos(String eventoId, String organizadorIdVerificacao, LocalDate inicio, LocalDate fim) {
        return eventoController.definirPeriodoSubmissao(eventoId, organizadorIdVerificacao, inicio, fim);
    }

    // UC11: Editar evento
    public Evento atualizarDetalhesEvento(String eventoId, String organizadorIdVerificacao, String nome, String descricao,
            LocalDate dataInicio, LocalDate dataFim, String local, Integer capacidade) {
        return eventoController.atualizarDetalhesEvento(eventoId, organizadorIdVerificacao, nome, descricao, dataInicio, dataFim, local, capacidade);
    }

    // UC10: Visualiazar lista de participantes do evento
    public List<Inscricao> visualizarInscritosEvento(String eventoId) {
        return inscricaoController.listarInscricoesPorEvento(eventoId);
    }

    public Inscricao confirmarPresenca(String inscricaoId, String organizadorId) {
        return inscricaoController.confirmarPresenca(inscricaoId, organizadorId);
    }

    public Trabalho designarAvaliadorParaTrabalho(String trabalhoId, String avaliadorId, String organizadorId) {
        return trabalhoController.designarAvaliadorParaTrabalho(trabalhoId, avaliadorId, organizadorId);
    }

    public Avaliacao registrarAvaliacao(String trabalhoId, String avaliadorId, double nota, String parecer) {
        return avaliacaoController.registrarAvaliacao(trabalhoId, avaliadorId, nota, parecer);
    }

    public List<Avaliacao> listarAvaliacoesPorTrabalho(String trabalhoId) {
        return avaliacaoController.listarAvaliacoesPorTrabalho(trabalhoId);
    }

    public Trabalho atualizarStatusTrabalho(String trabalhoId, StatusTrabalho novoStatus, String responsavelId) {
        return trabalhoController.atualizarStatusTrabalho(trabalhoId, novoStatus, responsavelId);
    }

    public List<Certificado> emitirCertificadosParticipacaoEvento(String eventoId) {
        return certificadoController.emitirCertificadosParticipacaoEvento(eventoId);
    }

    public List<Certificado> emitirCertificadosApresentacaoTrabalhoEvento(String eventoId) {
        return certificadoController.emitirCertificadosApresentacaoTrabalhoEvento(eventoId);
    }

    public Certificado emitirCertificadoOrganizadorEvento(String eventoId, String organizadorId) {
        return certificadoController.emitirCertificadoOrganizador(eventoId, organizadorId);
    }

    public Optional<Certificado> getCertificadoByCodigoValidacao(String codigo) {
        return certificadoRepository.findByCodigoValidacao(codigo);
    }

    public List<Certificado> listarCertificadosPorParticipante(String participanteId) {
        return certificadoRepository.findAllByParticipanteId(participanteId);
    }

    public List<Trabalho> listarTrabalhosPorAutor(String autorId) {
        return trabalhoController.listarTrabalhosPorAutor(autorId);
    }

    public List<Evento> listarEventosPorOrganizador(String organizadorId) {
        return eventoController.listarEventosPorOrganizador(organizadorId);
    }
}
