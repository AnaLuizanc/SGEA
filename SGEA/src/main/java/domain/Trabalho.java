package domain;

import domain.enums.StatusTrabalho;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import repository.Identifiable;


/**
 *
 * @author enio1
 */
public class Trabalho  implements Identifiable<String>{
     private String id;
    private String titulo;
    private String arquivo; // Conteúdo ou caminho do arquivo
    private StatusTrabalho status;
    private LocalDate dataSubmissao;

    private Evento evento; // Evento ao qual o trabalho foi submetido
    private Participante autor; // Lista de participantes autores
    private List<Avaliacao> avaliacoes; // Lista de avaliações recebidas

    public Trabalho(String titulo, String arquivo, Evento evento, Participante autor) {
        if (autor == null) {
            throw new IllegalArgumentException("Trabalho deve ter  autor.");
        }
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.arquivo = arquivo;
        this.status = StatusTrabalho.SUBMETIDO; // Status inicial
        this.dataSubmissao = LocalDate.now();
        this.evento = evento;
        this.autor = autor;
        this.avaliacoes = new ArrayList<>();
    }

    // Getters
    @Override
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getArquivo() { return arquivo; }
    public StatusTrabalho getStatus() { return status; }
    public LocalDate getDataSubmissao() { return dataSubmissao; }
    public Evento getEvento() { return evento; }
    public Participante getAutor() { return autor; }
    public List<Avaliacao> getAvaliacoes() { return Collections.unmodifiableList(avaliacoes); }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setArquivo(String arquivo) { this.arquivo = arquivo; }
    public void setStatus(StatusTrabalho status) { this.status = status; }

    // --- Métodos de Negócio (Information Expert) ---
    public void adicionarAvaliacaoInterna(Avaliacao avaliacao) { // Chamado pelo AvaliacaoService
        if (avaliacao == null) throw new IllegalArgumentException("Avaliação não pode ser nula.");
        if (this.status != StatusTrabalho.SUBMETIDO && this.status != StatusTrabalho.EM_AVALIACAO) {
            throw new IllegalStateException("Trabalho não pode ser avaliado neste status: " + this.status);
        }
        this.avaliacoes.add(avaliacao);
        if (this.status == StatusTrabalho.SUBMETIDO) {
            this.status = StatusTrabalho.EM_AVALIACAO;
        }
    }

    public boolean foiAprovado() {
        // Critério de aprovação pode ser mais complexo (ex: média de notas, decisão de comitê)
        // Simplificação: Aprovado se o status for APROVADO ou APROVADO_COM_RESSALVAS ou APRESENTADO
        return status == StatusTrabalho.APROVADO ||
               status == StatusTrabalho.APROVADO_COM_RESSALVAS;
    }

     public boolean foiApresentado() {
        return status == StatusTrabalho.APRESENTADO;
    }


    @Override
    public String toString() {
        return "Trabalho ID: " + id +
               ", Título: '" + titulo + '\'' +
               ", Status: " + status +
               ", Data Submissão: " + dataSubmissao +
               ", Evento: '" + evento.getNome() + '\'' +
               ", Autor: " + autor + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trabalho trabalho = (Trabalho) o;
        return Objects.equals(id, trabalho.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
