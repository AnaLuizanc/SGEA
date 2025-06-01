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

        if (trabalho.getStatus() != StatusTrabalho.EM_AVALIACAO && trabalho.getStatus() != StatusTrabalho.SUBMETIDO) {
            throw new IllegalStateException("Trabalho não está aberto para avaliação. Status atual: " + trabalho.getStatus());
        }

        if (nota < 0 && nota > 10) {
            throw new IllegalStateException("Nota enviada fora do intervalo permitido.");
        }

        Avaliacao novaAvaliacao = new Avaliacao(nota, parecer, trabalho, avaliador);
        trabalho.adicionarAvaliacaoInterna(novaAvaliacao);

        trabalhoRepository.save(trabalho);
        return avaliacaoRepository.save(novaAvaliacao);
    }

    public List<Avaliacao> listarAvaliacoesPorTrabalho(String trabalhoId) {
        return avaliacaoRepository.findAllByTrabalhoId(trabalhoId);
    }
}
