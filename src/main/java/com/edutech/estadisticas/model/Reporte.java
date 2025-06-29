package com.edutech.estadisticas.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGeneracion;

    @Enumerated(EnumType.STRING)
    private TipoReporte tipo;

    private String generadoPor;

    @Column(columnDefinition = "json")
    private String detalle;

}