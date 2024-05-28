package org.show.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.show.config.security.SecurityConfiguration;
import org.show.controller.dto.ProductDto;
import org.show.controller.dto.ProductNoIdDto;
import org.show.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.show.controller.ProductController.PRODUCTS;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@ActiveProfiles("security")
@Import({SecurityConfiguration.class})
public class ProductControllerIT {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void delete403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCTS + "/1")
                .with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete400() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete(PRODUCTS + "/-2 ").with(csrf())).andExpect(status().isBadRequest()).andReturn();
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void post400() throws Exception {
        ProductNoIdDto productForSave = new ProductNoIdDto("test-product-name", new BigDecimal(3.2));
        ProductDto productDto = new ProductDto(1L, productForSave.getName(), productForSave.getPrice());
        String json = objectMapper.writeValueAsString(productForSave);
        when(productService.save(any())).thenReturn(productDto);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(PRODUCTS).contentType(MediaType.APPLICATION_JSON)
                .content(json).with(csrf())).andExpect(status().isBadRequest()) .andReturn();
        assertNotNull(result.getResponse().getContentAsString());
//        final ProductDto response =objectMapper.readValue(result.getResponse().getContentAsString(), ProductDto.class);
//        assertEquals(productDto.getId(), response.getId());
//        assertEquals(productDto.getName(), response.getName());
//        assertEquals(productDto.getPrice(), response.getPrice());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void delete200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCTS + "/1")
                .with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void post200() throws Exception {
        ProductNoIdDto productForSave = new ProductNoIdDto("test-product-name", new BigDecimal("3.2"));
        ProductDto productDto = new ProductDto(1L, productForSave.getName(), productForSave.getPrice());
        String json = objectMapper.writeValueAsString(productForSave);
        when(productService.save(any())).thenReturn(productDto);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(PRODUCTS).contentType(MediaType.APPLICATION_JSON)
                        .content(json).with(csrf())).andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse().getContentAsString());
        final ProductDto response =objectMapper.readValue(result.getResponse().getContentAsString(), ProductDto.class);
        assertEquals(productDto.getId(), response.getId());
        assertEquals(productDto.getName(), response.getName());
        assertEquals(productDto.getPrice(), response.getPrice());
    }
}
