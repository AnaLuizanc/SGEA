package domain;

import domain.enums.TipoCertificado;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import repository.Identifiable;
/**
 *
 * @author enio1
 */
public class Certificado implements Identifiable<String>{
       private String id;
    private String codigoValidacao;
    private LocalDate dataEmissao;
    private TipoCertificado tipo;

    private Participante participante; 
    private Evento evento;
    private Trabalho trabalho;

    public Certificado(TipoCertificado tipo, Participante participante, Evento evento) {
        if (tipo == TipoCertificado.APRESENTACAO_TRABALHO) {
            throw new IllegalArgumentException("Use o construtor específico para certificado de apresentação de trabalho.");
        }
        this.id = UUID.randomUUID().toString();
        this.codigoValidacao = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.dataEmissao = LocalDate.now();
        this.tipo = tipo;
        this.participante = participante;
        this.evento = evento;
        this.trabalho = null;
    }

    public Certificado(Participante participante, Evento evento, Trabalho trabalho) {
        this.id = UUID.randomUUID().toString();
        this.codigoValidacao = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.dataEmissao = LocalDate.now();
        this.tipo = TipoCertificado.APRESENTACAO_TRABALHO;
        this.participante = participante;
        this.evento = evento;
        this.trabalho = Objects.requireNonNull(trabalho, "Trabalho não pode ser nulo para certificado de apresentação.");
    }

    // Getters
    @Override
    public String getId() { return id; }
    public String getCodigoValidacao() { return codigoValidacao; }
    public LocalDate getDataEmissao() { return dataEmissao; }
    public TipoCertificado getTipo() { return tipo; }
    public Participante getParticipante() { return participante; }
    public Evento getEvento() { return evento; }
    public Trabalho getTrabalho() { return trabalho; } 

    @Override
    public String toString() {
        String infoTrabalho = (trabalho != null) ? ", Trabalho: '" + trabalho.getTitulo() + '\'' : "";
        return "Certificado ID: " + id +
               ", Código: '" + codigoValidacao + '\'' +
               ", Data Emissão: " + dataEmissao +
               ", Tipo: " + tipo +
               ", Participante: '" + participante.getNomeCompleto() + '\'' +
               ", Evento: '" + evento.getNome() + '\'' +
               infoTrabalho;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Certificado that = (Certificado) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
