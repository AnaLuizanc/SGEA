package repository;

import domain.Participante;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipanteRepository extends BaseRepository<Participante, String> {

    public ParticipanteRepository() {
        super(Participante.class); 
    }

    public List<Participante> findByNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return findAll(); 
        }
        String nomeLower = nome.toLowerCase();
        return entities.values().stream()
                .filter(p -> p.getNomeCompleto().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }
}