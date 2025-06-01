package controller;

import domain.Participante;
import domain.enums.TipoPerfil;
import java.util.List;
import java.util.Optional;
import repository.ParticipanteRepository;
/**
 *
 * @author enio1
 */
public class ParticipanteController {
    private final ParticipanteRepository participanteRepository;

    public ParticipanteController(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public Participante cadastrarParticipante(String nomeCompleto, String email, String instituicao, TipoPerfil tipoPerfil) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do participante não pode ser vazio.");
        }
        if (email == null || !email.contains("@")) { // Validação simples de email
            throw new IllegalArgumentException("Email inválido.");
        }
        boolean emailJaExiste = participanteRepository.findAll().stream() 
                                            .anyMatch(p -> p.getEmail().equalsIgnoreCase(email));
        
        if (emailJaExiste) {
            throw new IllegalArgumentException("Já existe um participante cadastrado com este email.");
        }

        Participante novoParticipante = new Participante(nomeCompleto, email, instituicao, tipoPerfil);
        return participanteRepository.save(novoParticipante);
    }

    public Optional<Participante> getParticipanteById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do participante não pode ser nulo.");
        }
        return participanteRepository.findById(id);
    }

    public List<Participante> listarTodosParticipantes() {
        return participanteRepository.findAll();
    }

    public Participante atualizarParticipante(String id, String nome, String email, String instituicao, TipoPerfil perfil) {
        Participante participante = participanteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Participante com ID " + id + " não encontrado."));

        if (nome != null && !nome.trim().isEmpty()) participante.setNomeCompleto(nome);
        if (email != null && email.contains("@")) participante.setEmail(email); // Validação simples
        if (instituicao != null) participante.setInstituicao(instituicao);
        if (perfil != null) participante.setTipoPerfil(perfil);

        return participanteRepository.save(participante);
    }
}
