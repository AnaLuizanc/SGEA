package repository;
import domain.Evento;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EventoRepository extends BaseRepository<Evento, String> {

    public EventoRepository() {
        super(Evento.class);
    }

    // Métodos específicos
    public List<Evento> findAllDisponiveisParaInscricao(LocalDate hoje) {
        return entities.values().stream()
                .filter(e -> e.getDataInicio().isAfter(hoje))
                .collect(Collectors.toList());
    }
    
    public List<Evento> findAllByOrganizadorId(String organizadorId) {
    if (organizadorId == null) return List.of();
    return entities.values().stream()
            .filter(e -> e.getOrganizadorResponsavel().getId().equals(organizadorId))
            .collect(Collectors.toList());
}
}