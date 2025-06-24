package com.edutech.estadisticas.controller;

import com.edutech.estadisticas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/progreso-estudiantes")
    public ResponseEntity<Map<String, Object>> generarReporteProgresoEstudiantes(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        // Generar el reporte de progreso de estudiantes
        return ResponseEntity.ok(reporteService.formatearRespuesta(reporteService.generarReporteProgresoEstudiantes(generadoPor, detalleJson)));
    }

    @PostMapping("/rendimiento-secciones")
    public ResponseEntity<Map<String, Object>> generarReporteRendimientoSecciones(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        // Generar el reporte de rendimiento de secciones
        return ResponseEntity.ok(reporteService.formatearRespuesta(reporteService.generarReporteRendimientoSecciones(generadoPor, detalleJson)));
    }

    @PostMapping("/estudiantes-inscritos")
    public ResponseEntity<Map<String, Object>> generarReporteEstudiantesInscritos(
            @RequestParam String generadoPor,
            @RequestBody String detalleJson) {
        // Generar el reporte de estudiantes inscritos
        return ResponseEntity.ok(reporteService.formatearRespuesta(reporteService.generarReporteEstudiantesInscritos(generadoPor, detalleJson)));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarReportes() {
        // Listar todos los reportes
        return ResponseEntity.ok(reporteService.obtenerTodosLosReportes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerReportePorId(@PathVariable Long id) {
        // Obtener un reporte específico por ID
        return reporteService.obtenerReportePorId(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        // Eliminar un reporte por ID
        return reporteService.eliminarReportePorId(id);
    }
}
