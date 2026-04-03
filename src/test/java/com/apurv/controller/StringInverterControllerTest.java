package com.apurv.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class StringInverterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInvertString_WithValidInput_ReturnsInvertedString() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/invert")
                .param("input", "hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("olleh"));
    }

    @Test
    public void testInvertString_WithEmptyInput_ReturnsEmptyString() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/invert")
                .param("input", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    public void testInvertString_WithSpecialCharacters_ReturnsInvertedString() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/invert")
                .param("input", "hello@123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("321@olleh"));
    }
}
