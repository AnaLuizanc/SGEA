/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

import domain.enums.StatusTrabalho;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 *
 * @author enio1
 */
public class Trabalho {
     private String id;
    private String titulo;
    private String arquivo; // Conteúdo ou caminho do arquivo
    private StatusTrabalho status;
    private LocalDate dataSubmissao;

    private Evento evento; // Evento ao qual o trabalho foi submetido
    private List<Participante> autores; // Lista de participantes autores
    private List<Avaliacao> avaliacoes; // Lista de avaliações recebidas

    public Trabalho(String titulo, String arquivo, Evento evento, List<Participante> autores) {
        if (autores == null || autores.isEmpty()) {
            throw new IllegalArgumentException("Trabalho deve ter pelo menos um autor.");
        }
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.arquivo = arquivo;
        this.status = StatusTrabalho.SUBMETIDO; // Status inicial
        this.dataSubmissao = LocalDate.now();
        this.evento = evento;
        this.autores = new ArrayList<>(autores); // Copia a lista para evitar modificações externas
        this.avaliacoes = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getArquivo() { return arquivo; }
    public StatusTrabalho getStatus() { return status; }
    public LocalDate getDataSubmissao() { return dataSubmissao; }
    public Evento getEvento() { return evento; }
    public List<Participante> getAutores() { return Collections.unmodifiableList(autores); }
    public List<Avaliacao> getAvaliacoes() { return Collections.unmodifiableList(avaliacoes); }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setArquivo(String arquivo) { this.arquivo = arquivo; }
    public void setStatus(StatusTrabalho status) { this.status = status; }
    // Autores e evento geralmente não mudam após criação, mas se necessário:
    // public void setAutores(List<Participante> autores) { this.autores = new ArrayList<>(autores); }

    // --- Métodos de Negócio (Information Expert) ---
    public void adicionarAvaliacaoInterna(Avaliacao avaliacao) { // Chamado pelo AvaliacaoService
        if (avaliacao == null) throw new IllegalArgumentException("Avaliação não pode ser nula.");
        // RN: Um trabalho só pode ser avaliado se estiver SUBMETIDO ou EM_AVALIACAO
        if (this.status != StatusTrabalho.SUBMETIDO && this.status != StatusTrabalho.EM_AVALIACAO) {
            throw new IllegalStateException("Trabalho não pode ser avaliado neste status: " + this.status);
        }
        this.avaliacoes.add(avaliacao);
        // Pode-se adicionar lógica para mudar o status do trabalho, ex: para EM_AVALIACAO
        if (this.status == StatusTrabalho.SUBMETIDO) {
            this.status = StatusTrabalho.EM_AVALIACAO;
        }
    }

    public boolean foiAprovado() {
        // Critério de aprovação pode ser mais complexo (ex: média de notas, decisão de comitê)
        // Simplificação: Aprovado se o status for APROVADO ou APROVADO_COM_RESSALVAS ou APRESENTADO
        return status == StatusTrabalho.APROVADO ||
               status == StatusTrabalho.APROVADO_COM_RESSALVAS ||
               status == StatusTrabalho.APRESENTADO;
    }

     public boolean foiApresentado() {
        return status == StatusTrabalho.APRESENTADO;
    }


    @Override
    public String toString() {
        String nomesAutores = autores.stream()
                                     .map(Participante::getNomeCompleto)
                                     .reduce((a, b) -> a + ", " + b)
                                     .orElse("Nenhum autor");
        return "Trabalho ID: " + id +
               ", Título: '" + titulo + '\'' +
               ", Status: " + status +
               ", Data Submissão: " + dataSubmissao +
               ", Evento: '" + evento.getNome() + '\'' +
               ", Autores: [" + nomesAutores + "]";
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
