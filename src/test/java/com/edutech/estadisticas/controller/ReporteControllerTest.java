package com.edutech.estadisticas.controller;

import com.edutech.estadisticas.model.Reporte;
import com.edutech.estadisticas.service.ReporteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
@DisplayName("Tests para ReporteController")
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    @DisplayName("POST /api/v1/reportes/progreso-estudiantes - Debe retornar 200 OK")
    void generarReporteProgresoEstudiantes_DebeRetornar200() throws Exception {
        // Arrange
        Reporte reporteMock = new Reporte();
        reporteMock.setId(1L);
        reporteMock.setGeneradoPor("usuario@test.com");
        
        Map<String, Object> respuestaMock = Map.of("resultado", "exitoso", "id", 1L);
        
        when(reporteService.generarReporteProgresoEstudiantes(anyString(), anyString()))
            .thenReturn(reporteMock);
        when(reporteService.formatearRespuesta(any(Reporte.class)))
            .thenReturn(respuestaMock);

        // Act & Assert
        mockMvc.perform(post("/api/v1/reportes/progreso-estudiantes")
                .param("generadoPor", "usuario@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"curso\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verify interactions
        verify(reporteService).generarReporteProgresoEstudiantes(eq("usuario@test.com"), anyString());
        verify(reporteService).formatearRespuesta(any(Reporte.class));
    }

    @Test
    @DisplayName("POST /api/v1/reportes/rendimiento-secciones - Debe retornar 200 OK")
    void generarReporteRendimientoSecciones_DebeRetornar200() throws Exception {
        // Arrange
        Reporte reporteMock = new Reporte();
        reporteMock.setId(2L);
        reporteMock.setGeneradoPor("usuario@test.com");
        
        Map<String, Object> respuestaMock = Map.of("resultado", "exitoso", "id", 2L);
        
        when(reporteService.generarReporteRendimientoSecciones(anyString(), anyString()))
            .thenReturn(reporteMock);
        when(reporteService.formatearRespuesta(any(Reporte.class)))
            .thenReturn(respuestaMock);

        // Act & Assert
        mockMvc.perform(post("/api/v1/reportes/rendimiento-secciones")
                .param("generadoPor", "usuario@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seccion\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verify interactions
        verify(reporteService).generarReporteRendimientoSecciones(eq("usuario@test.com"), anyString());
        verify(reporteService).formatearRespuesta(any(Reporte.class));
    }

    @Test
    @DisplayName("POST /api/v1/reportes/estudiantes-inscritos - Debe retornar 200 OK")
    void generarReporteEstudiantesInscritos_DebeRetornar200() throws Exception {
        // Arrange
        Reporte reporteMock = new Reporte();
        reporteMock.setId(3L);
        reporteMock.setGeneradoPor("usuario@test.com");
        
        Map<String, Object> respuestaMock = Map.of("resultado", "exitoso", "id", 3L);
        
        when(reporteService.generarReporteEstudiantesInscritos(anyString(), anyString()))
            .thenReturn(reporteMock);
        when(reporteService.formatearRespuesta(any(Reporte.class)))
            .thenReturn(respuestaMock);

        // Act & Assert
        mockMvc.perform(post("/api/v1/reportes/estudiantes-inscritos")
                .param("generadoPor", "usuario@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"carrera\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // Verify interactions
        verify(reporteService).generarReporteEstudiantesInscritos(eq("usuario@test.com"), anyString());
        verify(reporteService).formatearRespuesta(any(Reporte.class));
    }

    @Test
    @DisplayName("GET /api/v1/reportes - Debe retornar 200 OK")
    void listarReportes_DebeRetornar200() throws Exception {
        // Arrange
        List<Map<String, Object>> reportesMock = List.of(
            Map.of("id", 1L, "generadoPor", "usuario@test.com"),
            Map.of("id", 2L, "generadoPor", "admin@test.com")
        );
        when(reporteService.obtenerTodosLosReportes()).thenReturn(reportesMock);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reportes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        
        // Verify interactions
        verify(reporteService).obtenerTodosLosReportes();
    }

    @Test
    @DisplayName("GET /api/v1/reportes/{id} - Debe retornar 200 OK cuando existe el reporte")
    void obtenerReportePorId_DebeRetornar200_CuandoExiste() throws Exception {
        // Arrange
        Map<String, Object> reporteMock = Map.of(
            "id", 1L, 
            "generadoPor", "usuario@test.com",
            "fechaGeneracion", "2024-01-15"
        );
        when(reporteService.obtenerReportePorId(1L))
            .thenReturn(ResponseEntity.ok(reporteMock));

        // Act & Assert
        mockMvc.perform(get("/api/v1/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
        
        // Verify interactions
        verify(reporteService).obtenerReportePorId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/reportes/{id} - Debe retornar 404 NOT FOUND cuando no existe el reporte")
    void obtenerReportePorId_DebeRetornar404_CuandoNoExiste() throws Exception {
        // Arrange
        when(reporteService.obtenerReportePorId(999L))
            .thenReturn(ResponseEntity.notFound().build());

        // Act & Assert
        mockMvc.perform(get("/api/v1/reportes/999"))
                .andExpect(status().isNotFound());
        
        // Verify interactions
        verify(reporteService).obtenerReportePorId(999L);
    }

    @Test
    @DisplayName("DELETE /api/v1/reportes/{id} - Debe retornar 200 OK cuando se elimina exitosamente")
    void eliminarReporte_DebeRetornar200_CuandoSeEliminaExitosamente() throws Exception {
        // Arrange
        when(reporteService.eliminarReportePorId(1L))
            .thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/reportes/1"))
                .andExpect(status().isOk());
        
        // Verify interactions
        verify(reporteService).eliminarReportePorId(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/reportes/{id} - Debe retornar 404 NOT FOUND cuando no existe el reporte")
    void eliminarReporte_DebeRetornar404_CuandoNoExiste() throws Exception {
        // Arrange
        when(reporteService.eliminarReportePorId(999L))
            .thenReturn(ResponseEntity.notFound().build());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/reportes/999"))
                .andExpect(status().isNotFound());
        
        // Verify interactions
        verify(reporteService).eliminarReportePorId(999L);
    }

    @Test
    @DisplayName("POST /api/v1/reportes/progreso-estudiantes - Debe retornar 400 BAD REQUEST sin parámetro generadoPor")
    void generarReporteProgresoEstudiantes_DebeRetornar400_SinParametroGeneradoPor() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/reportes/progreso-estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"curso\":\"test\"}"))
                .andExpect(status().isBadRequest());
        
        // Verify no service interactions since request should fail validation
        verifyNoInteractions(reporteService);
    }

    @Test
    @DisplayName("POST /api/v1/reportes/progreso-estudiantes - Debe retornar 400 BAD REQUEST sin body")
    void generarReporteProgresoEstudiantes_DebeRetornar400_SinBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/reportes/progreso-estudiantes")
                .param("generadoPor", "usuario@test.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        // Verify no service interactions since request should fail validation
        verifyNoInteractions(reporteService);
    }

    @Test
    @DisplayName("POST /api/v1/reportes/rendimiento-secciones - Debe retornar 400 BAD REQUEST con parámetros inválidos")
    void generarReporteRendimientoSecciones_DebeRetornar400_ConParametrosInvalidos() throws Exception {
        // Act & Assert - sin parámetro generadoPor
        mockMvc.perform(post("/api/v1/reportes/rendimiento-secciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seccion\":\"test\"}"))
                .andExpect(status().isBadRequest());

        // Act & Assert - sin body
        mockMvc.perform(post("/api/v1/reportes/rendimiento-secciones")
                .param("generadoPor", "usuario@test.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        // Verify no service interactions
        verifyNoInteractions(reporteService);
    }

    @Test
    @DisplayName("POST /api/v1/reportes/estudiantes-inscritos - Debe retornar 400 BAD REQUEST con parámetros inválidos")
    void generarReporteEstudiantesInscritos_DebeRetornar400_ConParametrosInvalidos() throws Exception {
        // Act & Assert - sin parámetro generadoPor
        mockMvc.perform(post("/api/v1/reportes/estudiantes-inscritos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"carrera\":\"test\"}"))
                .andExpect(status().isBadRequest());

        // Act & Assert - sin body
        mockMvc.perform(post("/api/v1/reportes/estudiantes-inscritos")
                .param("generadoPor", "usuario@test.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        // Verify no service interactions
        verifyNoInteractions(reporteService);
    }
}