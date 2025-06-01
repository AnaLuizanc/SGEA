/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    // Serviços
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

        // Inicialização dos Serviços com suas dependências de repositório
        this.participanteController = new ParticipanteController(participanteRepository);
        this.eventoController = new EventoController(eventoRepository, participanteRepository);
        this.inscricaoController = new InscricaoController(inscricaoRepository, eventoRepository, participanteRepository);
        this.trabalhoController = new TrabalhoController(trabalhoRepository, eventoRepository, participanteRepository, inscricaoRepository);
        this.avaliacaoController = new AvaliacaoController(avaliacaoRepository, trabalhoRepository, participanteRepository);
        this.certificadoController = new CertificadoController(certificadoRepository, eventoRepository, inscricaoRepository, trabalhoRepository, participanteRepository);
    }

    // --- Métodos da Fachada ---
    // UC1: O participante realiza seu cadastro no sistema;
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

    public List<Evento> listarTodosEventos() { // Para listagem geral
        return eventoController.listarTodosEventos();
    }

    public Inscricao inscreverEmEvento(String participanteId, String eventoId) {
        return inscricaoController.realizarInscricao(participanteId, eventoId);
    }

    // FR5: Cancelamento de inscrição
    public void cancelarInscricao(String inscricaoId) {
        inscricaoController.cancelarInscricao(inscricaoId);
    }

    public List<Inscricao> listarInscricoesPorParticipante(String participanteId) {
        return inscricaoController.listarInscricoesPorParticipante(participanteId);
    }

    // UC3: O participante submete um trabalho para um evento no qual está inscrito;
    public Trabalho submeterTrabalho(String idAutor, String eventoId, String titulo, String arquivo) {
        return trabalhoController.submeterTrabalho(idAutor, eventoId, titulo, arquivo);
    }

    public List<Trabalho> listarTrabalhosPorEvento(String eventoId) {
        return trabalhoController.listarTrabalhosPorEvento(eventoId);
    }

    public Optional<Trabalho> getTrabalhoById(String trabalhoId) {
        return trabalhoRepository.findById(trabalhoId); // Acesso direto ao repo para simples find
    }

    // UC4: O organizador cadastra um novo evento no sistema;
    public Evento cadastrarEvento(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim,
            String local, int capacidadeMaxima, String organizadorId,
            LocalDate periodoSubmissaoInicio, LocalDate periodoSubmissaoFim) {
        return eventoController.cadastrarEvento(nome, descricao, dataInicio, dataFim, local, capacidadeMaxima, organizadorId, periodoSubmissaoInicio, periodoSubmissaoFim);
    }

    public Optional<Evento> getEventoById(String eventoId) {
        return eventoRepository.findById(eventoId); // Acesso direto ao repo para simples find
    }

    // FR7: Organizado gerencia eventos (editar informações, visualizar inscritos, definir período de submissão)
    public Evento definirPeriodoSubmissaoTrabalhos(String eventoId, String organizadorIdVerificacao, LocalDate inicio, LocalDate fim) {
        return eventoController.definirPeriodoSubmissao(eventoId, organizadorIdVerificacao, inicio, fim);
    }

    public Evento atualizarDetalhesEvento(String eventoId, String organizadorIdVerificacao, String nome, String descricao,
            LocalDate dataInicio, LocalDate dataFim, String local, Integer capacidade) {
        return eventoController.atualizarDetalhesEvento(eventoId, organizadorIdVerificacao, nome, descricao, dataInicio, dataFim, local, capacidade);
    }

    // UC5: O organizador gerencia as inscrições de um evento (visualiza, confirma presença de participantes);
    public List<Inscricao> visualizarInscritosEvento(String eventoId) {
        return inscricaoController.listarInscricoesPorEvento(eventoId);
    }

    public Inscricao confirmarPresenca(String inscricaoId, String organizadorId) {
        return inscricaoController.confirmarPresenca(inscricaoId, organizadorId);
    }

    // UC6: O organizador designa avaliadores para trabalhos submetidos.
    public Trabalho designarAvaliadorParaTrabalho(String trabalhoId, String avaliadorId, String organizadorId) {
        return trabalhoController.designarAvaliadorParaTrabalho(trabalhoId, avaliadorId, organizadorId);
    }

    public List<Trabalho> listarTrabalhosPorAvaliadorDesignado(String avaliadorId) {
        // Este método ainda precisa de uma implementação mais robusta no TrabalhoController
        // dependendo de como a designação é modelada.
        // Por enquanto, pode lançar a exceção ou retornar lista vazia.
        try {
            return trabalhoController.listarTrabalhosPorAvaliadorDesignado(avaliadorId);
        } catch (UnsupportedOperationException e) {
            System.err.println("Aviso: " + e.getMessage());
            return List.of();
        }
    }

    // UC7: O avaliador registra a avaliação de um trabalho.
    public Avaliacao registrarAvaliacao(String trabalhoId, String avaliadorId, double nota, String parecer) {
        return avaliacaoController.registrarAvaliacao(trabalhoId, avaliadorId, nota, parecer);
    }

    public List<Avaliacao> listarAvaliacoesPorTrabalho(String trabalhoId) {
        return avaliacaoController.listarAvaliacoesPorTrabalho(trabalhoId);
    }

    public Trabalho atualizarStatusTrabalho(String trabalhoId, StatusTrabalho novoStatus, String responsavelId) {
        return trabalhoController.atualizarStatusTrabalho(trabalhoId, novoStatus, responsavelId);
    }

    // UC8: O sistema emite certificados de participação e apresentação.
    // FR10: Emissão de certificados de participação
    public List<Certificado> emitirCertificadosParticipacaoEvento(String eventoId) {
        return certificadoController.emitirCertificadosParticipacaoEvento(eventoId);
    }

    // FR11: Emissão de certificados de apresentação de trabalho
    public List<Certificado> emitirCertificadosApresentacaoTrabalhoEvento(String eventoId) {
        return certificadoController.emitirCertificadosApresentacaoTrabalhoEvento(eventoId);
    }

    // Certificado para organizador
    public Certificado emitirCertificadoOrganizadorEvento(String eventoId, String organizadorId) {
        return certificadoController.emitirCertificadoOrganizador(eventoId, organizadorId);
    }

    public Optional<Certificado> getCertificadoByCodigoValidacao(String codigo) {
        return certificadoRepository.findByCodigoValidacao(codigo); // Acesso direto
    }

    public List<Certificado> listarCertificadosPorParticipante(String participanteId) {
        return certificadoRepository.findAllByParticipanteId(participanteId); // Acesso direto
    }

    public List<Trabalho> listarTrabalhosPorAutor(String autorId) {
        return trabalhoController.listarTrabalhosPorAutor(autorId);
    }

    public List<Evento> listarEventosPorOrganizador(String organizadorId) {
        return eventoController.listarEventosPorOrganizador(organizadorId);
    }
}
