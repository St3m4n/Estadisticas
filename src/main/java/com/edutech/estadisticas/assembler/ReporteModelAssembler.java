package com.edutech.estadisticas.assembler;

import com.edutech.estadisticas.controller.ReporteController;
import com.edutech.estadisticas.model.Reporte;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Convierte instancias de Reporte en EntityModel<Reporte> enriquecidos con enlaces HATEOAS.
 */
@Component
public class ReporteModelAssembler implements RepresentationModelAssembler<Reporte, EntityModel<Reporte>> {

    @Override
    public EntityModel<Reporte> toModel(Reporte reporte) {
        return EntityModel.of(
            reporte,
            // enlace a este recurso específico
            linkTo(methodOn(ReporteController.class)
                .obtenerReportePorId(reporte.getId())).withSelfRel(),
            // enlace al listado de todos los reportes
            linkTo(methodOn(ReporteController.class)
                .listarReportes()).withRel("reportes"),
            // enlace para eliminar este reporte
            linkTo(methodOn(ReporteController.class)
                .eliminarReporte(reporte.getId())).withRel("eliminar")
        );
    }
}
