package com.gabs.minhasfinancias.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@Builder
public class LancamentoDTO {
    private long id;
    private String descricao;
    private Integer mes;
    private Integer ano;
    private BigDecimal valor;
    private Long usuario;
    private String tipo;
    private String status;
}
