/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import domain.Evento;
import domain.Participante;
import domain.Trabalho;
import domain.enums.StatusTrabalho;
import domain.enums.TipoPerfil;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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
    private final InscricaoRepository inscricaoRepository; // Para verificar inscrição dos autores

    public TrabalhoController(TrabalhoRepository trabalhoRepository, EventoRepository eventoRepository,
                           ParticipanteRepository participanteRepository, InscricaoRepository inscricaoRepository) {
        this.trabalhoRepository = trabalhoRepository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public Trabalho submeterTrabalho(List<String> autoresIds, String eventoId, String titulo, String arquivo) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado."));

        // RN 2: Submissão de trabalhos só é permitida dentro do período de submissão
        if (!evento.isPeriodoSubmissaoAberto(LocalDate.now())) {
            throw new IllegalStateException("Período de submissão para o evento '" + evento.getNome() + "' está fechado.");
        }

        if (autoresIds == null || autoresIds.isEmpty()) {
            throw new IllegalArgumentException("Trabalho deve ter pelo menos um autor.");
        }

        List<Participante> autores = autoresIds.stream()
                .map(id -> participanteRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Autor com ID " + id + " não encontrado.")))
                .collect(Collectors.toList());

        // RN 3: Um participante só pode submeter um trabalho a um evento se estiver inscrito nele.
        // Verifica se pelo menos um dos autores está inscrito ativamente.
        boolean algumAutorInscrito = autores.stream()
                .anyMatch(autor -> evento.isParticipanteInscritoAtivo(autor));
        if (!algumAutorInscrito) {
            throw new IllegalStateException("Pelo menos um dos autores deve estar inscrito ativamente no evento para submeter um trabalho.");
        }

        Trabalho novoTrabalho = new Trabalho(titulo, arquivo, evento, autores);
        evento.adicionarTrabalhoInterno(novoTrabalho);
        // eventoRepository.save(evento); // Se necessário

        return trabalhoRepository.save(novoTrabalho);
    }

    public Trabalho designarAvaliadorParaTrabalho(String trabalhoId, String avaliadorId, String organizadorId) {
        Trabalho trabalho = trabalhoRepository.findById(trabalhoId)
                .orElseThrow(() -> new IllegalArgumentException("Trabalho com ID " + trabalhoId + " não encontrado."));
        Participante avaliador = participanteRepository.findById(avaliadorId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliador com ID " + avaliadorId + " não encontrado."));
        Participante organizador = participanteRepository.findById(organizadorId)
                 .orElseThrow(() -> new IllegalArgumentException("Organizador com ID " + organizadorId + " não encontrado."));

        if (organizador.getTipoPerfil() != TipoPerfil.ORGANIZADOR || 
            !trabalho.getEvento().getOrganizadorResponsavel().getId().equals(organizadorId)) {
            throw new IllegalStateException("Apenas o organizador responsável pelo evento pode designar avaliadores.");
        }
        if (avaliador.getTipoPerfil() != TipoPerfil.AVALIADOR) {
            throw new IllegalStateException("Participante " + avaliador.getNomeCompleto() + " não tem perfil de AVALIADOR.");
        }
        // Lógica para associar avaliador ao trabalho (pode ser uma lista em Trabalho ou uma entidade de Associação)
        // Para este exemplo, vamos assumir que a Avaliacao será criada quando o avaliador submeter seu parecer.
        // Aqui, poderíamos apenas marcar o trabalho como "EM_AVALIACAO" se não estiver, e talvez registrar o avaliador designado.
        // Por ora, a criação da Avaliacao lida com a ligação.

        if (trabalho.getStatus() == StatusTrabalho.SUBMETIDO) {
            trabalho.setStatus(StatusTrabalho.EM_AVALIACAO);
        }
        // Poderia haver uma lista de `avaliadoresDesignados` em `Trabalho`
        // Ex: trabalho.adicionarAvaliadorDesignado(avaliador);

        return trabalhoRepository.save(trabalho);
    }

    public Trabalho atualizarStatusTrabalho(String trabalhoId, StatusTrabalho novoStatus, String responsavelId) {
        // Responsável pode ser um organizador ou o sistema após avaliações.
        Trabalho trabalho = trabalhoRepository.findById(trabalhoId)
                .orElseThrow(() -> new IllegalArgumentException("Trabalho com ID " + trabalhoId + " não encontrado."));
        
        // Adicionar verificação se o responsavelId tem permissão
        // Ex: se for organizador do evento do trabalho

        trabalho.setStatus(novoStatus);
        return trabalhoRepository.save(trabalho);
    }
    
    public List<Trabalho> listarTrabalhosPorEvento(String eventoId) {
        return trabalhoRepository.findAllByEventoId(eventoId);
    }

    public List<Trabalho> listarTrabalhosPorAvaliadorDesignado(String avaliadorId) {
        // Implementação dependeria de como avaliadores são designados.
        // Se for via Avaliacao (mesmo antes de preenchida), ou uma lista em Trabalho.
        // Exemplo simplificado (assumindo que todas as avaliações para um avaliador indicam trabalhos designados):
        // List<Avaliacao> avaliacoesDoAvaliador = avaliacaoRepository.findAllByAvaliadorId(avaliadorId);
        // return avaliacoesDoAvaliador.stream().map(Avaliacao::getTrabalho).distinct().collect(Collectors.toList());
        throw new UnsupportedOperationException("Listar trabalhos por avaliador designado não implementado (requer modelo de designação).");
    }
    
    public List<Trabalho> listarTrabalhosPorAutor(String autorId) {
    if (autorId == null) {
        throw new IllegalArgumentException("ID do autor não pode ser nulo.");
    }
    // Validação se o autor existe pode ser feita aqui se necessário
    // participanteRepository.findById(autorId).orElseThrow(...);
    return trabalhoRepository.findAllByAutorId(autorId);
}
}
