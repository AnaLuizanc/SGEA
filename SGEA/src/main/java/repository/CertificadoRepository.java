package repository;

import domain.Certificado;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CertificadoRepository extends BaseRepository<Certificado, String> {

    public CertificadoRepository() {
        super(Certificado.class);
    }

    // Métodos específicos
    public Optional<Certificado> findByCodigoValidacao(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return Optional.empty();
        return entities.values().stream()
                .filter(c -> c.getCodigoValidacao().equals(codigo))
                .findFirst();
    }

    public List<Certificado> findAllByParticipanteId(String participanteId) {
        if (participanteId == null) return List.of();
        return entities.values().stream()
                .filter(c -> c.getParticipante().getId().equals(participanteId))
                .collect(Collectors.toList());
    }

    public List<Certificado> findAllByEventoId(String eventoId) {
        if (eventoId == null) return List.of();
        return entities.values().stream()
                .filter(c -> c.getEvento().getId().equals(eventoId))
                .collect(Collectors.toList());
    }
}