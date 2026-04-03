package com.apurv.filescanner.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.apurv.controller.FileScannerController;
import com.apurv.filescanner.service.FileScannerService;

@ExtendWith(MockitoExtension.class)
class FileScannerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileScannerService fileScannerService;

    @InjectMocks
    private FileScannerController fileScannerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileScannerController).build();
    }

    @Test
    void whenGetProcessedFiles_thenReturnListOfFiles() throws Exception {
        // Given
        List<String> processedFiles = Arrays.asList("File 1 content", "File 2 content");
        when(fileScannerService.getProcessedFiles()).thenReturn(processedFiles);

        // When & Then
        mockMvc.perform(get("/api/files/processed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("File 1 content")))
                .andExpect(jsonPath("$[1]", is("File 2 content")));
    }

    @Test
    void whenNoFilesProcessed_thenReturnEmptyList() throws Exception {
        // Given
        when(fileScannerService.getProcessedFiles()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/files/processed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // @Test
    void whenServiceThrowsException_thenReturnInternalServerError() throws Exception {
        // Given
        when(fileScannerService.getProcessedFiles()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/files/processed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
