package com.edutech.Estadisticas.controller;

import com.edutech.Estadisticas.model.Reporte;
import com.edutech.Estadisticas.service.ReporteService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/progreso-estudiantes")
    public ResponseEntity<Map<String, Object>> generarReporteProgresoEstudiantes(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        Reporte reporte = reporteService.generarReporteProgresoEstudiantes(generadoPor, detalleJson);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", reporte.getId());
        response.put("fechaGeneracion", reporte.getFechaGeneracion());
        response.put("tipo", reporte.getTipo());
        response.put("generadoPor", reporte.getGeneradoPor());

        // Deserializar el String JSON para mostrarlo como objeto en la respuesta
        try {
            ObjectMapper mapper = new ObjectMapper();
            response.put("detalle", mapper.readTree(reporte.getDetalle()));
        } catch (Exception e) {
            response.put("detalle", "ERROR al parsear JSON");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/rendimiento-secciones")
    public ResponseEntity<Map<String, Object>> generarReporteRendimientoSecciones(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        Reporte reporte = reporteService.generarReporteRendimientoSecciones(generadoPor, detalleJson);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", reporte.getId());
        response.put("fechaGeneracion", reporte.getFechaGeneracion());
        response.put("tipo", reporte.getTipo());
        response.put("generadoPor", reporte.getGeneradoPor());

        try {
            ObjectMapper mapper = new ObjectMapper();
            response.put("detalle", mapper.readTree(reporte.getDetalle()));
        } catch (Exception e) {
            response.put("detalle", "ERROR al parsear JSON");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/estudiantes-inscritos")
    public ResponseEntity<Map<String, Object>> generarReporteEstudiantesInscritos(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        Reporte reporte = reporteService.generarReporteEstudiantesInscritos(generadoPor, detalleJson);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", reporte.getId());
        response.put("fechaGeneracion", reporte.getFechaGeneracion());
        response.put("tipo", reporte.getTipo());
        response.put("generadoPor", reporte.getGeneradoPor());

        try {
            ObjectMapper mapper = new ObjectMapper();
            response.put("detalle", mapper.readTree(reporte.getDetalle()));
        } catch (Exception e) {
            response.put("detalle", "ERROR al parsear JSON");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarReportes() {
        List<Reporte> reportes = reporteService.obtenerTodos();

        List<Map<String, Object>> respuesta = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (Reporte r : reportes) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("fechaGeneracion", r.getFechaGeneracion());
            item.put("tipo", r.getTipo());
            item.put("generadoPor", r.getGeneradoPor());

            try {
                item.put("detalle", mapper.readTree(r.getDetalle()));
            } catch (Exception e) {
                item.put("detalle", "ERROR al parsear JSON");
            }

            respuesta.add(item);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerReportePorId(@PathVariable Long id) {
        Reporte reporte = reporteService.obtenerPorId(id);
        if (reporte == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", reporte.getId());
        response.put("fechaGeneracion", reporte.getFechaGeneracion());
        response.put("tipo", reporte.getTipo());
        response.put("generadoPor", reporte.getGeneradoPor());

        try {
            ObjectMapper mapper = new ObjectMapper();
            response.put("detalle", mapper.readTree(reporte.getDetalle()));
        } catch (Exception e) {
            response.put("detalle", "ERROR al parsear JSON");
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        boolean eliminado = reporteService.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

}
