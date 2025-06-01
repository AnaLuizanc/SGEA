package domain;

import domain.enums.TipoPerfil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import repository.Identifiable;
/**
 *
 * @author enio1
 */
public class Participante  implements Identifiable<String> {
    private String id;
    private String nomeCompleto;
    private String email;
    private String instituicao;
    private TipoPerfil tipoPerfil;

    public Participante(String nomeCompleto, String email, String instituicao, TipoPerfil tipoPerfil) {
        this.id = UUID.randomUUID().toString();
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.instituicao = instituicao;
        this.tipoPerfil = tipoPerfil;
    }

    // Getters
    @Override
    public String getId() {
        return id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    // Setters 
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    @Override
    public String toString() {
        return "Participante ID: " + id +
               ", Nome: '" + nomeCompleto + '\'' +
               ", Email: '" + email + '\'' +
               ", Instituição: '" + instituicao + '\'' +
               ", Perfil: " + tipoPerfil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participante that = (Participante) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
