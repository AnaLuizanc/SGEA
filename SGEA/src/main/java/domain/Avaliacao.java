package domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import repository.Identifiable;
/**
 *
 * @author enio1
 */
public class Avaliacao implements Identifiable<String> {
     private String id;
    private double nota; 
    private String comentarios;
    private LocalDate dataAvaliacao;

    private Trabalho trabalho; 
    private Participante avaliador;

    public Avaliacao(double nota, String comentarios, Trabalho trabalho, Participante avaliador) {
        this.id = UUID.randomUUID().toString();
        this.nota = nota;
        this.comentarios = comentarios;
        this.dataAvaliacao = LocalDate.now();
        this.trabalho = trabalho;
        this.avaliador = avaliador;
    }

    // Getters
    @Override
    public String getId() { return id; }
    public double getNota() { return nota; }
    public String getComentarios() { return comentarios; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public Trabalho getTrabalho() { return trabalho; }
    public Participante getAvaliador() { return avaliador; }

    @Override
    public String toString() {
        return "Avaliação ID: " + id +
               ", Nota: " + nota +
               ", Comentários: '" + comentarios + '\'' +
               ", Data: " + dataAvaliacao +
               ", Trabalho: '" + trabalho.getTitulo() + '\'' +
               ", Avaliador: '" + avaliador.getNomeCompleto() + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(id, avaliacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
