package com.edutech.estadisticas.service;

import com.edutech.estadisticas.model.Reporte;
import com.edutech.estadisticas.model.TipoReporte;
import com.edutech.estadisticas.repository.ReporteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Método para generar el reporte de progreso de estudiantes
    public Reporte generarReporteEstudiantesInscritos(String generadoPor, String rawJson) {
    try {
        JsonNode secciones = objectMapper.readTree(rawJson);

        int totalEstudiantes = 0;
        int totalCursos = 0;

        List<Map<String, Object>> detallePorCurso = new ArrayList<>();

        for (JsonNode seccion : secciones) {
            String curso = seccion.get("nombreCurso").asText();
            String nombreSeccion = seccion.get("nombreSeccion").asText();
            JsonNode estudiantes = seccion.get("estudiantes");

            int inscritos = estudiantes.size();
            totalEstudiantes += inscritos;
            totalCursos++;

            List<String> nombresEstudiantes = new ArrayList<>();
            for (JsonNode estudiante : estudiantes) {
                nombresEstudiantes.add(estudiante.get("nombre").asText());
            }

            Map<String, Object> detalleSeccion = new LinkedHashMap<>();
            detalleSeccion.put("curso", curso);
            detalleSeccion.put("seccion", nombreSeccion);
            detalleSeccion.put("inscritos", inscritos);
            detalleSeccion.put("estudiantes", nombresEstudiantes);

            detallePorCurso.add(detalleSeccion);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("totalCursos", totalCursos);
        detalle.put("totalEstudiantes", totalEstudiantes);
        detalle.put("fechaGeneracion", sdf.format(new Date()));
        detalle.put("detallePorCurso", detallePorCurso);

        String detalleJson = objectMapper.writeValueAsString(detalle);

        Reporte reporte = new Reporte();
        reporte.setFechaGeneracion(new Date());
        reporte.setTipo(TipoReporte.ESTUDIANTES_INSCRITOS); // Asegúrate de usar el tipo correcto
        reporte.setGeneradoPor(generadoPor);
        reporte.setDetalle(detalleJson);

        return reporteRepository.save(reporte);

    } catch (Exception e) {
        throw new RuntimeException("Error al procesar el reporte de estudiantes inscritos", e);
    }
}

public Reporte generarReporteRendimientoSecciones(String generadoPor, String rawJson) {
    try {
        JsonNode input = objectMapper.readTree(rawJson);

        String curso = input.get("curso").asText();
        String seccion = input.get("seccion").asText();
        JsonNode evaluaciones = input.get("evaluaciones");

        List<Map<String, Object>> estudiantesRendimiento = new ArrayList<>();
        int aprobados = 0;
        int reprobados = 0;
        double sumaPromedios = 0;

        for (JsonNode estudiante : evaluaciones) {
            String id = estudiante.get("estudianteId").asText();
            String nombre = estudiante.get("nombre").asText();
            JsonNode notas = estudiante.get("notas");

            int totalNotas = notas.size();
            double sumaNotas = 0;
            for (JsonNode nota : notas) {
                sumaNotas += nota.asDouble();
            }

            double promedio = totalNotas > 0 ? (sumaNotas / totalNotas) : 0.0;
            sumaPromedios += promedio;

            boolean aprobado = promedio >= 60;
            if (aprobado) {
                aprobados++;
            } else {
                reprobados++;
            }

            Map<String, Object> estudianteDetalle = new LinkedHashMap<>();
            estudianteDetalle.put("estudianteId", id);
            estudianteDetalle.put("nombre", nombre);
            estudianteDetalle.put("promedio", Math.round(promedio * 100.0) / 100.0);
            estudianteDetalle.put("aprobado", aprobado);

            estudiantesRendimiento.add(estudianteDetalle);
        }

        int totalEstudiantes = evaluaciones.size();
        double promedioGeneral = totalEstudiantes > 0 ? (sumaPromedios / totalEstudiantes) : 0.0;

        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("curso", curso);
        detalle.put("seccion", seccion);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        detalle.put("fechaGeneracion", sdf.format(new Date()));
        detalle.put("cantidadEstudiantes", totalEstudiantes);
        detalle.put("aprobados", aprobados);
        detalle.put("reprobados", reprobados);
        detalle.put("promedioGeneral", Math.round(promedioGeneral * 100.0) / 100.0);
        detalle.put("rendimientoEstudiantes", estudiantesRendimiento);

        String detalleJson = objectMapper.writeValueAsString(detalle);

        Reporte reporte = new Reporte();
        reporte.setFechaGeneracion(new Date());
        reporte.setTipo(TipoReporte.RENDIMIENTO_SECCIONES); // Asegúrate de usar el tipo correcto
        reporte.setGeneradoPor(generadoPor);
        reporte.setDetalle(detalleJson);

        return reporteRepository.save(reporte);

    } catch (Exception e) {
        throw new RuntimeException("Error al procesar reporte de rendimiento de secciones", e);
    }
}

public Reporte generarReporteProgresoEstudiantes(String generadoPor, String rawJson) {
    try {
        JsonNode input = objectMapper.readTree(rawJson);
        String curso = input.get("curso").asText();
        String seccion = input.get("seccion").asText();
        JsonNode evaluaciones = input.get("evaluaciones");

        List<Map<String, Object>> progresoEstudiantes = new ArrayList<>();
        for (JsonNode estudiante : evaluaciones) {
            String id = estudiante.get("estudianteId").asText();
            String nombre = estudiante.get("nombre").asText();
            JsonNode notas = estudiante.get("notas");

            int totalNotas = notas.size();
            double suma = 0;

            for (JsonNode nota : notas) {
                suma += nota.asDouble();
            }

            double promedio = totalNotas > 0 ? (suma / totalNotas) : 0.0;

            Map<String, Object> progreso = new LinkedHashMap<>();
            progreso.put("estudianteId", id);
            progreso.put("nombre", nombre);
            progreso.put("evaluacionesTotales", totalNotas);
            progreso.put("promedio", Math.round(promedio * 100.0) / 100.0);

            progresoEstudiantes.add(progreso);
        }

        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("curso", curso);
        detalle.put("seccion", seccion);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        detalle.put("fechaGeneracion", sdf.format(new Date()));
        detalle.put("progresoEstudiantes", progresoEstudiantes);

        Reporte reporte = new Reporte();
        reporte.setFechaGeneracion(new Date());
        reporte.setTipo(TipoReporte.PROGRESO_ESTUDIANTES); // Asegúrate de usar el tipo correcto
        reporte.setGeneradoPor(generadoPor);
        reporte.setDetalle(objectMapper.writeValueAsString(detalle));

        return reporteRepository.save(reporte);

    } catch (Exception e) {
        throw new RuntimeException("Error al procesar el reporte de progreso de estudiantes", e);
    }
}


    // Método para obtener todos los reportes
    public List<Map<String, Object>> obtenerTodosLosReportes() {
        List<Reporte> reportes = reporteRepository.findAll();

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

        return respuesta;
    }

    // Método para obtener un reporte por ID
    public ResponseEntity<Map<String, Object>> obtenerReportePorId(Long id) {
        Optional<Reporte> reporte = reporteRepository.findById(id);
        return reporte.map(r -> ResponseEntity.ok(formatearRespuesta(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Método para eliminar un reporte por ID
    public ResponseEntity<Void> eliminarReportePorId(Long id) {
        if (reporteRepository.existsById(id)) {
            reporteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método que prepara la respuesta de un reporte
    public Map<String, Object> formatearRespuesta(Reporte reporte) {
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

        return response;
    }
}
