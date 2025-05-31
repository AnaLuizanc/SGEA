/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import domain.Avaliacao;
import domain.Participante;
import domain.Trabalho;
import domain.enums.StatusTrabalho;
import domain.enums.TipoPerfil;
import java.util.List;
import repository.AvaliacaoRepository;
import repository.ParticipanteRepository;
import repository.TrabalhoRepository;
/**
 *
 * @author enio1
 */
public class AvaliacaoController {
     private final AvaliacaoRepository avaliacaoRepository;
    private final TrabalhoRepository trabalhoRepository;
    private final ParticipanteRepository participanteRepository;

    public AvaliacaoController(AvaliacaoRepository avaliacaoRepository, TrabalhoRepository trabalhoRepository, ParticipanteRepository participanteRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.trabalhoRepository = trabalhoRepository;
        this.participanteRepository = participanteRepository;
    }

    public Avaliacao registrarAvaliacao(String trabalhoId, String avaliadorId, double nota, String parecer) {
        Trabalho trabalho = trabalhoRepository.findById(trabalhoId)
                .orElseThrow(() -> new IllegalArgumentException("Trabalho com ID " + trabalhoId + " não encontrado."));
        Participante avaliador = participanteRepository.findById(avaliadorId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliador com ID " + avaliadorId + " não encontrado."));

        if (avaliador.getTipoPerfil() != TipoPerfil.AVALIADOR) {
            throw new IllegalStateException("Participante " + avaliador.getNomeCompleto() + " não tem perfil de AVALIADOR.");
        }
        
        // Verificar se este avaliador foi designado para este trabalho (lógica a ser implementada se houver designação explícita)
        // Ex: if (!trabalho.isAvaliadorDesignado(avaliador)) {
        // throw new IllegalStateException("Avaliador não designado para este trabalho.");
        // }

        if (trabalho.getStatus() != StatusTrabalho.EM_AVALIACAO && trabalho.getStatus() != StatusTrabalho.SUBMETIDO) {
            // Permitir avaliar SUBMETIDO para mudar para EM_AVALIACAO na primeira avaliação
            throw new IllegalStateException("Trabalho não está aberto para avaliação. Status atual: " + trabalho.getStatus());
        }

        Avaliacao novaAvaliacao = new Avaliacao(nota, parecer, trabalho, avaliador);
        trabalho.adicionarAvaliacaoInterna(novaAvaliacao); // Isso também muda o status do trabalho para EM_AVALIACAO se for a primeira.
        
        trabalhoRepository.save(trabalho); // Salva o trabalho com o novo status e a avaliação associada implicitamente
        return avaliacaoRepository.save(novaAvaliacao);
    }
    
    public List<Avaliacao> listarAvaliacoesPorTrabalho(String trabalhoId) {
        return avaliacaoRepository.findAllByTrabalhoId(trabalhoId);
    }
}
