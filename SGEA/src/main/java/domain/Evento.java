package domain;

import domain.enums.StatusInscricao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import repository.Identifiable;

/**
 *
 * @author enio1
 */
public class Evento implements Identifiable<String> {
    private final String id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String local;
    private int capacidadeMaxima;
    private LocalDate periodoSubmissaoInicio;
    private LocalDate periodoSubmissaoFim;
    private Participante organizadorResponsavel;

    private final List<Inscricao> inscricoes;
    private final List<Trabalho> trabalhos;

    public Evento(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim,
                  String local, int capacidadeMaxima, Participante organizadorResponsavel) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
        this.organizadorResponsavel = organizadorResponsavel;
        this.inscricoes = new ArrayList<>();
        this.trabalhos = new ArrayList<>();
    }

    // Getters
    @Override
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public String getLocal() { return local; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public LocalDate getPeriodoSubmissaoInicio() { return periodoSubmissaoInicio; }
    public LocalDate getPeriodoSubmissaoFim() { return periodoSubmissaoFim; }
    public Participante getOrganizadorResponsavel() { return organizadorResponsavel; }

    public List<Inscricao> getInscricoes() {
        return Collections.unmodifiableList(inscricoes);
    }
    public List<Trabalho> getTrabalhos() {
        return Collections.unmodifiableList(trabalhos);
    }

    // Setters (para edição de evento)
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public void setLocal(String local) { this.local = local; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    public void setOrganizadorResponsavel(Participante organizadorResponsavel) { this.organizadorResponsavel = organizadorResponsavel; }
    public void setPeriodoSubmissao(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data de início da submissão não pode ser posterior à data de fim.");
        }
        this.periodoSubmissaoInicio = inicio;
        this.periodoSubmissaoFim = fim;
    }

    public boolean isLotado() {
        return inscricoes.stream().filter(i -> i.getStatus() == StatusInscricao.ATIVA).count() >= capacidadeMaxima;
    }

    public boolean isPeriodoSubmissaoAberto(LocalDate dataAtual) {
        if (periodoSubmissaoInicio == null || periodoSubmissaoFim == null) {
            return false; 
        }
        return !dataAtual.isBefore(periodoSubmissaoInicio) && !dataAtual.isAfter(periodoSubmissaoFim);
    }

    public boolean isParticipanteInscritoAtivo(Participante participante) {
        return inscricoes.stream()
                .anyMatch(i -> i.getParticipante().equals(participante) && i.getStatus() == StatusInscricao.ATIVA);
    }

    public void adicionarInscricaoInterna(Inscricao inscricao) { 
        if (inscricao == null) throw new IllegalArgumentException("Inscrição não pode ser nula.");
        this.inscricoes.add(inscricao);
    }

    public void removerInscricaoInterna(Inscricao inscricao) { 
         if (inscricao == null) throw new IllegalArgumentException("Inscrição não pode ser nula.");
         this.inscricoes.remove(inscricao); 
    }

    public void adicionarTrabalhoInterno(Trabalho trabalho) { 
        if (trabalho == null) throw new IllegalArgumentException("Trabalho não pode ser nulo.");
        this.trabalhos.add(trabalho);
    }

    public List<Inscricao> getInscritosComPresencaConfirmada() {
        return this.inscricoes.stream()
                .filter(Inscricao::isPresencaConfirmada)
                .filter(i -> i.getStatus() == StatusInscricao.ATIVA)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        long ativos = inscricoes.stream().filter(i -> i.getStatus() == StatusInscricao.ATIVA).count();
        return "Evento ID: " + id 
                + "\nNome: '" + nome + '\'' 
                + "\nData: " + dataInicio + " até " + dataFim 
                + "\nLocal: '" + local + '\'' 
                + "\nCapacidade: " + capacidadeMaxima  
                +  "\nInscritos Ativos: " + ativos + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
