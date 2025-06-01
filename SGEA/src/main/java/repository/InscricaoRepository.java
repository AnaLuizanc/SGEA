package repository;

import domain.Inscricao;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InscricaoRepository extends BaseRepository<Inscricao, String> {

    public InscricaoRepository() {
        super(Inscricao.class);
    }

    // Métodos específicos
    public List<Inscricao> findAllByEventoId(String eventoId) {
        if (eventoId == null) return List.of();
        return entities.values().stream()
                .filter(i -> i.getEvento().getId().equals(eventoId))
                .collect(Collectors.toList());
    }

    public List<Inscricao> findAllByParticipanteId(String participanteId) {
        if (participanteId == null) return List.of();
        return entities.values().stream()
                .filter(i -> i.getParticipante().getId().equals(participanteId))
                .collect(Collectors.toList());
    }

    public Optional<Inscricao> findByParticipanteIdAndEventoId(String participanteId, String eventoId) {
        if (participanteId == null || eventoId == null) return Optional.empty();
        return entities.values().stream()
                .filter(i -> i.getParticipante().getId().equals(participanteId) &&
                              i.getEvento().getId().equals(eventoId))
                .findFirst();
    }
    
    @Override
    public void deleteById(String id) {
        super.deleteById(id);
    }
}