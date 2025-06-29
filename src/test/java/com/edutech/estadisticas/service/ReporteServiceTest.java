package com.edutech.estadisticas.service;

import com.edutech.estadisticas.model.Reporte;
import com.edutech.estadisticas.model.TipoReporte;
import com.edutech.estadisticas.repository.ReporteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @InjectMocks
    private ReporteService reporteService;
    private Reporte reporteMock;

    @BeforeEach
    void setUp() {
        reporteMock = new Reporte();
        reporteMock.setId(1L);
        reporteMock.setFechaGeneracion(new Date());
        reporteMock.setTipo(TipoReporte.ESTUDIANTES_INSCRITOS);
        reporteMock.setGeneradoPor("testUser");
        reporteMock.setDetalle("{\"test\":\"data\"}");
    }

    // ===================== TESTS PARA generarReporteEstudiantesInscritos =====================

    @Test
    void generarReporteEstudiantesInscritos_DeberiaGenerarReporteCorrectamente() {
        // Given
        String jsonInput = """
            [
                {
                    "nombreCurso": "Matemáticas",
                    "nombreSeccion": "Sección A",
                    "estudiantes": [
                        {"nombre": "Juan Pérez"},
                        {"nombre": "María García"}
                    ]
                },
                {
                    "nombreCurso": "Historia",
                    "nombreSeccion": "Sección B",
                    "estudiantes": [
                        {"nombre": "Carlos López"}
                    ]
                }
            ]
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteEstudiantesInscritos("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
        
        // Verificar que se guardó con el tipo correcto
        verify(reporteRepository).save(argThat(reporte -> 
            reporte.getTipo() == TipoReporte.ESTUDIANTES_INSCRITOS &&
            "testUser".equals(reporte.getGeneradoPor())
        ));
    }

    @Test
    void generarReporteEstudiantesInscritos_ConListaVacia_DeberiaGenerarReporteVacio() {
        // Given
        String jsonInput = "[]";
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteEstudiantesInscritos("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void generarReporteEstudiantesInscritos_ConJsonInvalido_DeberiaLanzarRuntimeException() {
        // Given
        String jsonInvalido = "{ invalid json }";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            reporteService.generarReporteEstudiantesInscritos("testUser", jsonInvalido)
        );

        assertEquals("Error al procesar el reporte de estudiantes inscritos", exception.getMessage());
        verify(reporteRepository, never()).save(any());
    }

    @Test
    void generarReporteEstudiantesInscritos_ConEstudiantesVacios_DeberiaFuncionar() {
        // Given
        String jsonInput = """
            [
                {
                    "nombreCurso": "Matemáticas",
                    "nombreSeccion": "Sección A",
                    "estudiantes": []
                }
            ]
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteEstudiantesInscritos("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    // ===================== TESTS PARA generarReporteRendimientoSecciones =====================

    @Test
    void generarReporteRendimientoSecciones_DeberiaGenerarReporteCorrectamente() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": [
                    {
                        "estudianteId": "001",
                        "nombre": "Juan Pérez",
                        "notas": [80, 85, 90]
                    },
                    {
                        "estudianteId": "002",
                        "nombre": "María García",
                        "notas": [50, 55, 45]
                    }
                ]
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteRendimientoSecciones("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
        verify(reporteRepository).save(argThat(reporte -> 
            reporte.getTipo() == TipoReporte.RENDIMIENTO_SECCIONES
        ));
    }

    @Test
    void generarReporteRendimientoSecciones_ConNotasVacias_DeberiaFuncionar() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": [
                    {
                        "estudianteId": "001",
                        "nombre": "Juan Pérez",
                        "notas": []
                    }
                ]
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteRendimientoSecciones("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void generarReporteRendimientoSecciones_ConEvaluacionesVacias_DeberiaFuncionar() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": []
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteRendimientoSecciones("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void generarReporteRendimientoSecciones_ConJsonInvalido_DeberiaLanzarRuntimeException() {
        // Given
        String jsonInvalido = "{ invalid json }";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            reporteService.generarReporteRendimientoSecciones("testUser", jsonInvalido)
        );

        assertEquals("Error al procesar reporte de rendimiento de secciones", exception.getMessage());
        verify(reporteRepository, never()).save(any());
    }

    // ===================== TESTS PARA generarReporteProgresoEstudiantes =====================

    @Test
    void generarReporteProgresoEstudiantes_DeberiaGenerarReporteCorrectamente() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": [
                    {
                        "estudianteId": "001",
                        "nombre": "Juan Pérez",
                        "notas": [80, 85, 90]
                    },
                    {
                        "estudianteId": "002",
                        "nombre": "María García",
                        "notas": [70, 75]
                    }
                ]
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteProgresoEstudiantes("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
        verify(reporteRepository).save(argThat(reporte -> 
            reporte.getTipo() == TipoReporte.PROGRESO_ESTUDIANTES
        ));
    }

    @Test
    void generarReporteProgresoEstudiantes_ConNotasVacias_DeberiaFuncionar() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": [
                    {
                        "estudianteId": "001",
                        "nombre": "Juan Pérez",
                        "notas": []
                    }
                ]
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteProgresoEstudiantes("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void generarReporteProgresoEstudiantes_ConJsonInvalido_DeberiaLanzarRuntimeException() {
        // Given
        String jsonInvalido = "{ invalid json }";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            reporteService.generarReporteProgresoEstudiantes("testUser", jsonInvalido)
        );

        assertEquals("Error al procesar el reporte de progreso de estudiantes", exception.getMessage());
        verify(reporteRepository, never()).save(any());
    }

    // ===================== TESTS PARA obtenerTodosLosReportes =====================

    @Test
    void obtenerTodosLosReportes_ConReportesExistentes_DeberiaRetornarLista() {
        // Given
        List<Reporte> reportesMock = Arrays.asList(reporteMock);
        when(reporteRepository.findAll()).thenReturn(reportesMock);

        // When
        List<Map<String, Object>> resultado = reporteService.obtenerTodosLosReportes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        Map<String, Object> primerReporte = resultado.get(0);
        assertEquals(1L, primerReporte.get("id"));
        assertEquals(TipoReporte.ESTUDIANTES_INSCRITOS, primerReporte.get("tipo"));
        assertEquals("testUser", primerReporte.get("generadoPor"));
        
        verify(reporteRepository).findAll();
    }

    @Test
    void obtenerTodosLosReportes_ConListaVacia_DeberiaRetornarListaVacia() {
        // Given
        when(reporteRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Map<String, Object>> resultado = reporteService.obtenerTodosLosReportes();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(reporteRepository).findAll();
    }

    @Test
    void obtenerTodosLosReportes_ConJsonInvalidoEnDetalle_DeberiaPonerMensajeError() {
        // Given
        Reporte reporteConJsonInvalido = new Reporte();
        reporteConJsonInvalido.setId(1L);
        reporteConJsonInvalido.setFechaGeneracion(new Date());
        reporteConJsonInvalido.setTipo(TipoReporte.ESTUDIANTES_INSCRITOS);
        reporteConJsonInvalido.setGeneradoPor("testUser");
        reporteConJsonInvalido.setDetalle("{ invalid json }");

        when(reporteRepository.findAll()).thenReturn(Arrays.asList(reporteConJsonInvalido));

        // When
        List<Map<String, Object>> resultado = reporteService.obtenerTodosLosReportes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("ERROR al parsear JSON", resultado.get(0).get("detalle"));
        verify(reporteRepository).findAll();
    }

    // ===================== TESTS PARA obtenerReportePorId =====================

    @Test
    void obtenerReportePorId_ConIdExistente_DeberiaRetornarReporte() {
        // Given
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteMock));

        // When
        ResponseEntity<Map<String, Object>> resultado = reporteService.obtenerReportePorId(1L);

        // Then
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(1L, resultado.getBody().get("id"));
        assertEquals("testUser", resultado.getBody().get("generadoPor"));
        verify(reporteRepository).findById(1L);
    }

    @Test
    void obtenerReportePorId_ConIdInexistente_DeberiaRetornarNotFound() {
        // Given
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Map<String, Object>> resultado = reporteService.obtenerReportePorId(999L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode());
        assertNull(resultado.getBody());
        verify(reporteRepository).findById(999L);
    }

    // ===================== TESTS PARA eliminarReportePorId =====================

    @Test
    void eliminarReportePorId_ConIdExistente_DeberiaEliminarYRetornarNoContent() {
        // Given
        when(reporteRepository.existsById(1L)).thenReturn(true);

        // When
        ResponseEntity<Void> resultado = reporteService.eliminarReportePorId(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());
        verify(reporteRepository).existsById(1L);
        verify(reporteRepository).deleteById(1L);
    }

    @Test
    void eliminarReportePorId_ConIdInexistente_DeberiaRetornarNotFound() {
        // Given
        when(reporteRepository.existsById(999L)).thenReturn(false);

        // When
        ResponseEntity<Void> resultado = reporteService.eliminarReportePorId(999L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode());
        verify(reporteRepository).existsById(999L);
        verify(reporteRepository, never()).deleteById(anyLong());
    }

    // ===================== TESTS PARA formatearRespuesta =====================

    @Test
    void formatearRespuesta_ConReporteValido_DeberiaFormatearCorrectamente() {
        // When
        Map<String, Object> resultado = reporteService.formatearRespuesta(reporteMock);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.get("id"));
        assertEquals(TipoReporte.ESTUDIANTES_INSCRITOS, resultado.get("tipo"));
        assertEquals("testUser", resultado.get("generadoPor"));
        assertNotNull(resultado.get("fechaGeneracion"));
        assertNotNull(resultado.get("detalle"));
    }

    @Test
    void formatearRespuesta_ConJsonInvalidoEnDetalle_DeberiaPonerMensajeError() {
        // Given
        Reporte reporteConJsonInvalido = new Reporte();
        reporteConJsonInvalido.setId(1L);
        reporteConJsonInvalido.setFechaGeneracion(new Date());
        reporteConJsonInvalido.setTipo(TipoReporte.ESTUDIANTES_INSCRITOS);
        reporteConJsonInvalido.setGeneradoPor("testUser");
        reporteConJsonInvalido.setDetalle("{ invalid json }");

        // When
        Map<String, Object> resultado = reporteService.formatearRespuesta(reporteConJsonInvalido);

        // Then
        assertNotNull(resultado);
        assertEquals("ERROR al parsear JSON", resultado.get("detalle"));
    }

    // ===================== TESTS ADICIONALES PARA CASOS EDGE =====================

    @Test
    void generarReporteEstudiantesInscritos_ConCamposNulos_DeberiaFuncionar() {
        // Given - Jackson maneja null values sin problemas
        String jsonInput = """
            [
                {
                    "nombreCurso": null,
                    "nombreSeccion": "Sección A",
                    "estudiantes": []
                }
            ]
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteEstudiantesInscritos("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void generarReporteRendimientoSecciones_ConNotasCero_DeberiaCalcularCorrectamente() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": [
                    {
                        "estudianteId": "001",
                        "nombre": "Juan Pérez",
                        "notas": [0, 0, 0]
                    }
                ]
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteRendimientoSecciones("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void generarReporteProgresoEstudiantes_ConEvaluacionesVacias_DeberiaFuncionar() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": []
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        // When
        Reporte resultado = reporteService.generarReporteProgresoEstudiantes("testUser", jsonInput);

        // Then
        assertNotNull(resultado);
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void generarReporteEstudiantesInscritos_ConJsonMalFormado_DeberiaLanzarException() {
        // Given
        String jsonMalFormado = "{ \"nombreCurso\": \"Math\" }"; // JSON sin array principal

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            reporteService.generarReporteEstudiantesInscritos("testUser", jsonMalFormado)
        );

        assertEquals("Error al procesar el reporte de estudiantes inscritos", exception.getMessage());
        verify(reporteRepository, never()).save(any());
    }

    @Test
    void generarReporteRendimientoSecciones_ConErrorEnRepository_DeberiaLanzarException() {
        // Given
        String jsonInput = """
            {
                "curso": "Matemáticas",
                "seccion": "Sección A",
                "evaluaciones": []
            }
            """;

        when(reporteRepository.save(any(Reporte.class))).thenThrow(new RuntimeException("Error de base de datos"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            reporteService.generarReporteRendimientoSecciones("testUser", jsonInput)
        );

        assertEquals("Error al procesar reporte de rendimiento de secciones", exception.getMessage());
    }
}