package com.edutech.estadisticas.controller;

import com.edutech.estadisticas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/progreso-estudiantes")
    public ResponseEntity<EntityModel<Map<String, Object>>> generarReporteProgresoEstudiantes(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        Map<String, Object> data = reporteService.formatearRespuesta(
            reporteService.generarReporteProgresoEstudiantes(generadoPor, detalleJson));
        EntityModel<Map<String, Object>> model = EntityModel.of(data,
            linkTo(methodOn(ReporteController.class)
                .generarReporteProgresoEstudiantes(generadoPor, detalleJson)).withSelfRel(),
            linkTo(methodOn(ReporteController.class).listarReportes()).withRel("reportes")
        );
        return ResponseEntity.ok(model);
    }

    @PostMapping("/rendimiento-secciones")
    public ResponseEntity<EntityModel<Map<String, Object>>> generarReporteRendimientoSecciones(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        Map<String, Object> data = reporteService.formatearRespuesta(
            reporteService.generarReporteRendimientoSecciones(generadoPor, detalleJson));
        EntityModel<Map<String, Object>> model = EntityModel.of(data,
            linkTo(methodOn(ReporteController.class)
                .generarReporteRendimientoSecciones(generadoPor, detalleJson)).withSelfRel(),
            linkTo(methodOn(ReporteController.class).listarReportes()).withRel("reportes")
        );
        return ResponseEntity.ok(model);
    }

    @PostMapping("/estudiantes-inscritos")
    public ResponseEntity<EntityModel<Map<String, Object>>> generarReporteEstudiantesInscritos(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        Map<String, Object> data = reporteService.formatearRespuesta(
            reporteService.generarReporteEstudiantesInscritos(generadoPor, detalleJson));
        EntityModel<Map<String, Object>> model = EntityModel.of(data,
            linkTo(methodOn(ReporteController.class)
                .generarReporteEstudiantesInscritos(generadoPor, detalleJson)).withSelfRel(),
            linkTo(methodOn(ReporteController.class).listarReportes()).withRel("reportes")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Map<String, Object>>>> listarReportes() {
        List<Map<String, Object>> raw = reporteService.obtenerTodosLosReportes();
        List<EntityModel<Map<String, Object>>> items = raw.stream()
            .map(item -> EntityModel.of(item,
                linkTo(methodOn(ReporteController.class)
                    .obtenerReportePorId((Long) item.get("id"))).withSelfRel(),
                linkTo(methodOn(ReporteController.class).listarReportes()).withRel("reportes")
            ))
            .toList();
        CollectionModel<EntityModel<Map<String, Object>>> collection = CollectionModel.of(
            items,
            linkTo(methodOn(ReporteController.class).listarReportes()).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, Object>>> obtenerReportePorId(@PathVariable Long id) {
        // Obtener la respuesta raw del servicio
        ResponseEntity<Map<String, Object>> resp = reporteService.obtenerReportePorId(id);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            Map<String, Object> data = resp.getBody();
            EntityModel<Map<String, Object>> model = EntityModel.of(data,
                linkTo(methodOn(ReporteController.class).obtenerReportePorId(id)).withSelfRel(),
                linkTo(methodOn(ReporteController.class).listarReportes()).withRel("reportes")
            );
            return ResponseEntity.ok(model);
        } else {
            return ResponseEntity.status(resp.getStatusCode()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        return reporteService.eliminarReportePorId(id);
    }
}
