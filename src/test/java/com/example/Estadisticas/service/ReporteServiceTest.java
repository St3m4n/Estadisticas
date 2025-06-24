package com.example.estadisticas.service;

import com.edutech.estadisticas.model.Reporte;
import com.edutech.estadisticas.model.TipoReporte;
import com.edutech.estadisticas.repository.ReporteRepository;
import com.edutech.estadisticas.service.ReporteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class ReporteServiceTest {

    private ReporteRepository reporteRepository;
    private ReporteService reporteService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Mock de la dependencia ReporteRepository
        reporteRepository = mock(ReporteRepository.class);
        // Inicializar ObjectMapper
        objectMapper = new ObjectMapper();
        // Crear una instancia del servicio
        reporteService = new ReporteService(reporteRepository);
    }

    // Test de generarReporteEstudiantesInscritos
    @Test
    void testGenerarReporteEstudiantesInscritos() {
        // Crear JSON de prueba
        String rawJson = "[{\"nombreCurso\":\"Matemáticas\",\"nombreSeccion\":\"A\",\"estudiantes\":[{\"nombre\":\"Juan\"},{\"nombre\":\"Pedro\"}]}]";

        // Mock de la llamada a reporteRepository.save()
        Reporte mockReporte = new Reporte();
        mockReporte.setId(1L);
        mockReporte.setTipo(TipoReporte.ESTUDIANTES_INSCRITOS);
        mockReporte.setGeneradoPor("TestUser");
        mockReporte.setDetalle(rawJson); // Asumimos que el detalle es el mismo rawJson

        when(reporteRepository.save(any(Reporte.class))).thenReturn(mockReporte);

        // Llamada al servicio
        Reporte resultado = reporteService.generarReporteEstudiantesInscritos("TestUser", rawJson);

        // Comprobaciones
        assertNotNull(resultado);
        assertEquals("TestUser", resultado.getGeneradoPor());
        assertEquals(TipoReporte.ESTUDIANTES_INSCRITOS, resultado.getTipo());
    }

    // Test de eliminarReportePorId
    @Test
    void testEliminarReportePorId() {
        // Definir un ID para el reporte
        Long id = 1L;

        // Mock de existencia del reporte en el repositorio
        when(reporteRepository.existsById(id)).thenReturn(true);
        doNothing().when(reporteRepository).deleteById(id);

        // Llamada al servicio
        ResponseEntity<Void> respuesta = reporteService.eliminarReportePorId(id);

        // Verificación
        assertEquals(204, respuesta.getStatusCodeValue()); // Verificar que el código de estado sea 204 No Content
    }

    // Test de eliminarReportePorId cuando no existe
    @Test
    void testEliminarReportePorIdNoExistente() {
        // ID inexistente
        Long id = 999L;

        // Mock de no existencia en el repositorio
        when(reporteRepository.existsById(id)).thenReturn(false);

        // Llamada al servicio
        ResponseEntity<Void> respuesta = reporteService.eliminarReportePorId(id);

        // Verificación
        assertEquals(404, respuesta.getStatusCodeValue()); // Verificar que el código de estado sea 404 Not Found
    }

    // Test de obtenerReportePorId
    @Test
    void testObtenerReportePorId() {
        // Mock del reporte
        Long id = 1L;
        Reporte mockReporte = new Reporte();
        mockReporte.setId(id);
        mockReporte.setTipo(TipoReporte.ESTUDIANTES_INSCRITOS);
        mockReporte.setGeneradoPor("TestUser");
        mockReporte.setDetalle("{\"curso\":\"Matemáticas\"}");

        when(reporteRepository.findById(id)).thenReturn(Optional.of(mockReporte));

        // Llamada al servicio
        ResponseEntity<Map<String, Object>> respuesta = reporteService.obtenerReportePorId(id);

        // Verificación
        assertEquals(200, respuesta.getStatusCodeValue()); // Código de estado 200 OK
        assertNotNull(respuesta.getBody());
        assertEquals(id, respuesta.getBody().get("id"));
        assertEquals("TestUser", respuesta.getBody().get("generadoPor"));
    }

    // Test de obtenerReportePorId cuando no existe
    @Test
    void testObtenerReportePorIdNoExistente() {
        Long id = 999L;

        // Mock de no existencia
        when(reporteRepository.findById(id)).thenReturn(Optional.empty());

        // Llamada al servicio
        ResponseEntity<Map<String, Object>> respuesta = reporteService.obtenerReportePorId(id);

        // Verificación
        assertEquals(404, respuesta.getStatusCodeValue()); // Verificar que se retorna 404 Not Found
    }

    // Test de obtener todos los reportes
    @Test
    void testObtenerTodosLosReportes() {
        // Mock de múltiples reportes
        Reporte reporte1 = new Reporte();
        reporte1.setId(1L);
        reporte1.setTipo(TipoReporte.ESTUDIANTES_INSCRITOS);
        reporte1.setGeneradoPor("TestUser");
        reporte1.setDetalle("{\"curso\":\"Matemáticas\"}");

        Reporte reporte2 = new Reporte();
        reporte2.setId(2L);
        reporte2.setTipo(TipoReporte.RENDIMIENTO_SECCIONES);
        reporte2.setGeneradoPor("TestUser2");
        reporte2.setDetalle("{\"curso\":\"Ciencias\"}");

        List<Reporte> reportes = Arrays.asList(reporte1, reporte2);
        when(reporteRepository.findAll()).thenReturn(reportes);

        // Llamada al servicio
        List<Map<String, Object>> resultado = reporteService.obtenerTodosLosReportes();

        // Verificación
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }
}
