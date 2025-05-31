/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
   private String id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String local; // Ou link se online
    private int capacidadeMaxima;
    private LocalDate periodoSubmissaoInicio;
    private LocalDate periodoSubmissaoFim;
    private Participante organizadorResponsavel; // Um participante com perfil ORGANIZADOR

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

    // Retorna cópias defensivas das listas para proteger o encapsulamento
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

    // --- Métodos de Negócio (Information Expert) ---
    public boolean isLotado() {
        return inscricoes.stream().filter(i -> i.getStatus() == StatusInscricao.ATIVA).count() >= capacidadeMaxima;
    }

    public boolean isPeriodoSubmissaoAberto(LocalDate dataAtual) {
        if (periodoSubmissaoInicio == null || periodoSubmissaoFim == null) {
            return false; // Não configurado para aceitar submissões
        }
        return !dataAtual.isBefore(periodoSubmissaoInicio) && !dataAtual.isAfter(periodoSubmissaoFim);
    }

    public boolean isParticipanteInscritoAtivo(Participante participante) {
        return inscricoes.stream()
                .anyMatch(i -> i.getParticipante().equals(participante) && i.getStatus() == StatusInscricao.ATIVA);
    }

    public void adicionarInscricaoInterna(Inscricao inscricao) { // Chamado pelo InscricaoService
        if (inscricao == null) throw new IllegalArgumentException("Inscrição não pode ser nula.");
        // A validação de lotação e se já está inscrito é feita no InscricaoService antes de chamar este
        this.inscricoes.add(inscricao);
    }

    public void removerInscricaoInterna(Inscricao inscricao) { // Chamado pelo InscricaoService ao cancelar
         if (inscricao == null) throw new IllegalArgumentException("Inscrição não pode ser nula.");
         this.inscricoes.remove(inscricao); // Ou marcar como cancelada, dependendo da lógica de persistência
    }

    public void adicionarTrabalhoInterno(Trabalho trabalho) { // Chamado pelo TrabalhoService
        if (trabalho == null) throw new IllegalArgumentException("Trabalho não pode ser nulo.");
        // A validação de período de submissão e inscrição do autor é feita no TrabalhoService
        this.trabalhos.add(trabalho);
    }

    public List<Inscricao> getInscritosComPresencaConfirmada() {
        return this.inscricoes.stream()
                .filter(Inscricao::isPresencaConfirmada)
                .filter(i -> i.getStatus() == StatusInscricao.ATIVA) // Garante que só ativos com presença
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        long ativos = inscricoes.stream().filter(i -> i.getStatus() == StatusInscricao.ATIVA).count();
        return "Evento ID: " + id + ", Nome: '" + nome + '\'' +
               ", Data: " + dataInicio + " a " + dataFim +
               ", Local: '" + local + '\'' + ", Capacidade: " + capacidadeMaxima +
               ", Inscritos Ativos: " + ativos;
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
