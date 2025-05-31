/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

import domain.enums.StatusInscricao;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
/**
 *
 * @author enio1
 */
public class Inscricao {
    private String id;
    private LocalDate dataInscricao;
    private StatusInscricao status;
    private boolean presencaConfirmada; // Simplificação para confirmação de presença

    private Evento evento; // Ligação para o Evento
    private Participante participante; // Ligação para o Participante

    // Prazo para cancelamento (RN4), pode ser configurável
    private static final int DIAS_ANTECEDENCIA_PARA_CANCELAMENTO = 2;

    public Inscricao(LocalDate dataInscricao, Evento evento, Participante participante) {
        this.id = UUID.randomUUID().toString();
        this.dataInscricao = dataInscricao;
        this.status = StatusInscricao.ATIVA; // Inscrição começa ativa
        this.presencaConfirmada = false;
        this.evento = evento;
        this.participante = participante;
    }

    // Getters
    public String getId() { return id; }
    public LocalDate getDataInscricao() { return dataInscricao; }
    public StatusInscricao getStatus() { return status; }
    public boolean isPresencaConfirmada() { return presencaConfirmada; }
    public Evento getEvento() { return evento; }
    public Participante getParticipante() { return participante; }

    // Setters
    public void setStatus(StatusInscricao status) {
        this.status = status;
    }

    public void setPresencaConfirmada(boolean presencaConfirmada) {
        // RN5: Certificados de participação só são emitidos após a data de término do evento e com confirmação de presença.
        // A lógica de quando PODE confirmar presença (ex: após início do evento) fica no Service.
        if (this.status == StatusInscricao.ATIVA) { // Só confirma presença se estiver ativa
             this.presencaConfirmada = presencaConfirmada;
        } else {
            throw new IllegalStateException("Não é possível confirmar presença para inscrição não ativa.");
        }
    }

    // --- Métodos de Negócio (Information Expert) ---
    public boolean podeCancelar(LocalDate dataAtual) {
        // RN4: Cancelamento de inscrição só é permitido até X dias antes da data de início do evento
        if (this.status != StatusInscricao.ATIVA) {
            return false; // Já cancelada ou outro status não cancelável
        }
        LocalDate dataLimiteCancelamento = evento.getDataInicio().minusDays(DIAS_ANTECEDENCIA_PARA_CANCELAMENTO);
        return !dataAtual.isAfter(dataLimiteCancelamento);
    }

    @Override
    public String toString() {
        return "Inscricao ID: " + id +
               ", Data: " + dataInscricao +
               ", Status: " + status +
               ", Presença: " + (presencaConfirmada ? "Confirmada" : "Não Confirmada") +
               ", Evento: '" + evento.getNome() + '\'' +
               ", Participante: '" + participante.getNomeCompleto() + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscricao inscricao = (Inscricao) o;
        return Objects.equals(id, inscricao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
