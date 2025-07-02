package com.edutech.estadisticas.controller;

import com.edutech.estadisticas.model.Reporte;
import com.edutech.estadisticas.service.ReporteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
@DisplayName("Tests para ReporteController con HATEOAS completo")
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    @DisplayName("GET /api/v1/reportes - Debe retornar 200 OK con HAL+JSON y lista embebida")
    void listarReportes_DebeRetornar200() throws Exception {
        List<Map<String, Object>> reportesMock = List.of(
            Map.of("id", 1L, "generadoPor", "usuario@test.com"),
            Map.of("id", 2L, "generadoPor", "admin@test.com")
        );
        when(reporteService.obtenerTodosLosReportes()).thenReturn(reportesMock);

        mockMvc.perform(get("/api/v1/reportes"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._embedded").exists());

        verify(reporteService).obtenerTodosLosReportes();
    }

    @Test
    @DisplayName("GET /api/v1/reportes/{id} - Debe retornar 200 OK con HAL+JSON cuando existe")
    void obtenerReportePorId_Existente() throws Exception {
        Map<String, Object> reporteMock = Map.of("id", 1L, "generadoPor", "u@test.com");
        when(reporteService.obtenerReportePorId(1L))
            .thenReturn(ResponseEntity.ok(reporteMock));

        mockMvc.perform(get("/api/v1/reportes/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.reportes.href").exists());

        verify(reporteService).obtenerReportePorId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/reportes/{id} - Debe retornar 404 Not Found cuando no existe")
    void obtenerReportePorId_NoExiste() throws Exception {
        when(reporteService.obtenerReportePorId(999L))
            .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/api/v1/reportes/999"))
            .andExpect(status().isNotFound());

        verify(reporteService).obtenerReportePorId(999L);
    }

    @Test
    @DisplayName("POST /api/v1/reportes/progreso-estudiantes - Debe retornar 200 OK con HAL+JSON")
    void generarReporteProgresoEstudiantes_DebeRetornar200() throws Exception {
        Map<String, Object> respuestaMock = Map.of("status", "OK", "id", 10L);
        Reporte repo = new Reporte(); repo.setId(10L);
        when(reporteService.generarReporteProgresoEstudiantes(anyString(), anyString()))
            .thenReturn(repo);
        when(reporteService.formatearRespuesta(any()))
            .thenReturn(respuestaMock);

        mockMvc.perform(post("/api/v1/reportes/progreso-estudiantes")
                .param("generadoPor", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dummy\":1}"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.reportes.href").exists());

        verify(reporteService).generarReporteProgresoEstudiantes(eq("user"), anyString());
        verify(reporteService).formatearRespuesta(any());
    }

    @Test
    @DisplayName("POST /api/v1/reportes/rendimiento-secciones - Debe retornar 200 OK con HAL+JSON")
    void generarReporteRendimientoSecciones_DebeRetornar200() throws Exception {
        Map<String, Object> respuestaMock = Map.of("status", "OK", "count", 5);
        Reporte repo = new Reporte(); repo.setId(20L);
        when(reporteService.generarReporteRendimientoSecciones(anyString(), anyString()))
            .thenReturn(repo);
        when(reporteService.formatearRespuesta(any()))
            .thenReturn(respuestaMock);

        mockMvc.perform(post("/api/v1/reportes/rendimiento-secciones")
                .param("generadoPor", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dummy\":1}"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.count").value(5))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.reportes.href").exists());

        verify(reporteService).generarReporteRendimientoSecciones(eq("user"), anyString());
        verify(reporteService).formatearRespuesta(any());
    }

    @Test
    @DisplayName("POST /api/v1/reportes/estudiantes-inscritos - Debe retornar 200 OK con HAL+JSON")
    void generarReporteEstudiantesInscritos_DebeRetornar200() throws Exception {
        Map<String, Object> respuestaMock = Map.of("status", "OK", "total", 100);
        Reporte repo = new Reporte(); repo.setId(30L);
        when(reporteService.generarReporteEstudiantesInscritos(anyString(), anyString()))
            .thenReturn(repo);
        when(reporteService.formatearRespuesta(any()))
            .thenReturn(respuestaMock);

        mockMvc.perform(post("/api/v1/reportes/estudiantes-inscritos")
                .param("generadoPor", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dummy\":1}"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.total").value(100))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.reportes.href").exists());

        verify(reporteService).generarReporteEstudiantesInscritos(eq("user"), anyString());
        verify(reporteService).formatearRespuesta(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/reportes/{id} - Debe retornar 204 No Content cuando se elimina")
    void eliminarReporte_Existente() throws Exception {
        when(reporteService.eliminarReportePorId(1L))
            .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/v1/reportes/1"))
            .andExpect(status().isNoContent());

        verify(reporteService).eliminarReportePorId(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/reportes/{id} - Debe retornar 404 Not Found cuando no existe")
    void eliminarReporte_NoExiste() throws Exception {
        when(reporteService.eliminarReportePorId(999L))
            .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete("/api/v1/reportes/999"))
            .andExpect(status().isNotFound());

        verify(reporteService).eliminarReportePorId(999L);
    }

    @Test
    @DisplayName("GET /api/v1/reportes/{id} - Debe retornar 200 OK sin cuerpo cuando body es null")
    void obtenerReportePorId_BodyNull() throws Exception {
        when(reporteService.obtenerReportePorId(1L))
            .thenReturn(ResponseEntity.ok(null));

        mockMvc.perform(get("/api/v1/reportes/1"))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(reporteService).obtenerReportePorId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/reportes/{id} - Debe propagar código de error cuando no es 2xx")
    void obtenerReportePorId_ErrorPropagado() throws Exception {
        when(reporteService.obtenerReportePorId(2L))
            .thenReturn(ResponseEntity.status(500).build());

        mockMvc.perform(get("/api/v1/reportes/2"))
            .andExpect(status().isInternalServerError());

        verify(reporteService).obtenerReportePorId(2L);
    }
}
