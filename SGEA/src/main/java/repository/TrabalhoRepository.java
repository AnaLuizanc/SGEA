/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import domain.Trabalho;
import domain.enums.StatusTrabalho;
import java.util.List;
import java.util.stream.Collectors;

public class TrabalhoRepository extends BaseRepository<Trabalho, String> {

    public TrabalhoRepository() {
        super(Trabalho.class);
    }

    // Métodos específicos
    public List<Trabalho> findAllByEventoId(String eventoId) {
        if (eventoId == null) return List.of();
        return entities.values().stream()
                .filter(t -> t.getEvento().getId().equals(eventoId))
                .collect(Collectors.toList());
    }

    public List<Trabalho> findAllByAutorId(String autorId) {
        if (autorId == null) return List.of();
        return entities.values().stream()
                .filter(t -> t.getAutores().stream().anyMatch(autor -> autor.getId().equals(autorId)))
                .collect(Collectors.toList());
    }

    public List<Trabalho> findAllByStatusAndEventoId(StatusTrabalho status, String eventoId) {
        if (status == null || eventoId == null) return List.of();
        return entities.values().stream()
                .filter(t -> t.getEvento().getId().equals(eventoId) && t.getStatus() == status)
                .collect(Collectors.toList());
    }
}
