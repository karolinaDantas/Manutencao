package br.com.aweb.manutencao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do item que precisa de manutenção")
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String item;

    @NotBlank(message = "Informe o nome do solicitante")
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String solicitante;

    @NotBlank(message = "Descreva o problema encontrado")
    @Size(min = 10, max = 500)
    @Column(length = 500, nullable = false)
    private String descricaoProblema;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime dataEncerramento;

    /**
     * Regra de negócio: uma solicitação é considerada concluída quando
     * possui data de encerramento preenchida. Usado na view para
     * habilitar/desabilitar ações (ex.: th:if="${solicitacao.concluida}").
     */
    public boolean isConcluida() {
        return dataEncerramento != null;
    }
}
