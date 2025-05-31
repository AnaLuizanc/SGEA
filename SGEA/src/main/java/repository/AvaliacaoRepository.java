/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import domain.Avaliacao;
import java.util.List;
import java.util.stream.Collectors;

public class AvaliacaoRepository extends BaseRepository<Avaliacao, String> {

    public AvaliacaoRepository() {
        super(Avaliacao.class);
    }

    // Métodos específicos
    public List<Avaliacao> findAllByTrabalhoId(String trabalhoId) {
        if (trabalhoId == null) return List.of();
        return entities.values().stream()
                .filter(a -> a.getTrabalho().getId().equals(trabalhoId))
                .collect(Collectors.toList());
    }

    public List<Avaliacao> findAllByAvaliadorId(String avaliadorId) {
        if (avaliadorId == null) return List.of();
        return entities.values().stream()
                .filter(a -> a.getAvaliador().getId().equals(avaliadorId))
                .collect(Collectors.toList());
    }
}