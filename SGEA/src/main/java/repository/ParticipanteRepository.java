/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import domain.Participante;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipanteRepository extends BaseRepository<Participante, String> {

    public ParticipanteRepository() {
        super(Participante.class); // Passa a classe da entidade para o construtor base
    }

    // Métodos específicos que utilizam o 'entities' (protected) da classe base
    public List<Participante> findByNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return findAll(); // Ou retornar lista vazia, dependendo da regra de negócio
        }
        String nomeLower = nome.toLowerCase();
        return entities.values().stream()
                .filter(p -> p.getNomeCompleto().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }
}