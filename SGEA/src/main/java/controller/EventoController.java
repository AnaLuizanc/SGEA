/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import domain.Evento;
import domain.Participante;
import domain.enums.TipoPerfil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import repository.EventoRepository;
import repository.ParticipanteRepository;

/**
 *
 * @author enio1
 */
public class EventoController {

    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public EventoController(EventoRepository eventoRepository, ParticipanteRepository participanteRepository) {
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    public Evento cadastrarEvento(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim,
            String local, int capacidadeMaxima, String organizadorId,
            LocalDate periodoSubmissaoInicio, LocalDate periodoSubmissaoFim) {

        Participante organizador = participanteRepository.findById(organizadorId)
                .orElseThrow(() -> new IllegalArgumentException("Organizador com ID " + organizadorId + " não encontrado."));

        if (organizador.getTipoPerfil() != TipoPerfil.ORGANIZADOR) {
            throw new IllegalStateException("Participante " + organizador.getNomeCompleto() + " não tem perfil de ORGANIZADOR.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do evento não pode ser vazio.");
        }
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Datas do evento inválidas.");
        }
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Capacidade máxima deve ser positiva.");
        }

        Evento novoEvento = new Evento(nome, descricao, dataInicio, dataFim, local, capacidadeMaxima, organizador);
        if (periodoSubmissaoInicio != null && periodoSubmissaoFim != null) {
            novoEvento.setPeriodoSubmissao(periodoSubmissaoInicio, periodoSubmissaoFim);
        }
        return eventoRepository.save(novoEvento);
    }

    public Optional<Evento> getEventoById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do evento não pode ser nulo.");
        }
        return eventoRepository.findById(id);
    }

    public List<Evento> listarTodosEventos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarEventosDisponiveisParaInscricao(LocalDate hoje) {
        // Delega ao repositório, mas poderia ter lógica adicional aqui
        return eventoRepository.findAllDisponiveisParaInscricao(hoje);
    }

    public Evento definirPeriodoSubmissao(String eventoId, String organizadorIdVerificacao, LocalDate inicio, LocalDate fim) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));

        // Verifica se quem está alterando é o organizador do evento
        if (!evento.getOrganizadorResponsavel().getId().equals(organizadorIdVerificacao)) {
            throw new IllegalStateException("Apenas o organizador responsável pode alterar o período de submissão.");
        }

        evento.setPeriodoSubmissao(inicio, fim);
        return eventoRepository.save(evento);
    }

    public Evento atualizarDetalhesEvento(String eventoId, String organizadorIdVerificacao, String nome, String descricao,
            LocalDate dataInicio, LocalDate dataFim, String local, Integer capacidade) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));

        if (!evento.getOrganizadorResponsavel().getId().equals(organizadorIdVerificacao)) {
            throw new IllegalStateException("Apenas o organizador responsável pode alterar detalhes do evento.");
        }

        if (nome != null && !nome.trim().isEmpty()) {
            evento.setNome(nome);
        }
        if (descricao != null) {
            evento.setDescricao(descricao);
        }
        if (dataInicio != null) {
            evento.setDataInicio(dataInicio); // Adicionar validação de dataFim se dataInicio mudar
        }
        if (dataFim != null) {
            evento.setDataFim(dataFim); // Adicionar validação de dataInicio se dataFim mudar
        }
        if (local != null && !local.trim().isEmpty()) {
            evento.setLocal(local);
        }
        if (capacidade != null && capacidade > 0) {
            evento.setCapacidadeMaxima(capacidade);
        }

        // Validar consistência das datas se ambas foram alteradas
        if (evento.getDataInicio().isAfter(evento.getDataFim())) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
        }

        return eventoRepository.save(evento);
    }

    public List<Evento> listarEventosPorOrganizador(String organizadorId) {
        if (organizadorId == null) {
            throw new IllegalArgumentException("ID do organizador não pode ser nulo.");
        }
        participanteRepository.findById(organizadorId)
                .filter(p -> p.getTipoPerfil() == TipoPerfil.ORGANIZADOR)
                .orElseThrow(() -> new IllegalArgumentException("Organizador com ID " + organizadorId + " não encontrado ou não é organizador."));
        return eventoRepository.findAllByOrganizadorId(organizadorId);
    }
}
