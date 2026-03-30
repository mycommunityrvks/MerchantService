package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.MerchantRequestDto;
import org.example.dto.MerchantResponseDto;
import org.example.exception.DuplicateResourceException;
import org.example.service.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MerchantController.class)
@WithMockUser(username = "admin", password = "password", roles = "USER")
public class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MerchantService merchantService;

    @Autowired
    private ObjectMapper objectMapper;

    private MerchantRequestDto requestDto;
    private MerchantResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new MerchantRequestDto();
        requestDto.setPrimaryPhone("+1234567890");
        requestDto.setAlternatePhone("+0987654321");
        requestDto.setMerchantEmail("merchant@test.com");
        requestDto.setWhatAppAvailable(true);
        requestDto.setBusinessName("Test Business");
        requestDto.setBusinessType("Retail");
        requestDto.setCategory("Electronics");
        requestDto.setSubCategory("Mobile");
        requestDto.setPersonAge(30);
        requestDto.setYearsOfExperience(5);
        requestDto.setArea("Downtown");
        requestDto.setAddress("123 Test St");
        requestDto.setLat(12.345);
        requestDto.setLon(67.890);
        requestDto.setHomeServiceAvailable(true);
        requestDto.setWorkingDays("Mon,Tue,Wed,Thu,Fri");
        requestDto.setWorkingHours("10AM-7PM");
        requestDto.setAcquisitionSource("Online");
        requestDto.setAddedBy("Admin");
        requestDto.setMetaData("{\"key\": \"value\"}");

        responseDto = new MerchantResponseDto();
        responseDto.setMerchantId(1L);
        responseDto.setPrimaryPhone("+1234567890");
        responseDto.setAlternatePhone("+0987654321");
        responseDto.setMerchantEmail("merchant@test.com");
        responseDto.setWhatAppAvailable(true);
        responseDto.setBusinessName("Test Business");
        responseDto.setBusinessType("Retail");
        responseDto.setCategory("Electronics");
        responseDto.setSubCategory("Mobile");
        responseDto.setPersonAge(30);
        responseDto.setYearsOfExperience(5);
        responseDto.setArea("Downtown");
        responseDto.setAddress("123 Test St");
        responseDto.setLat(12.345);
        responseDto.setLon(67.890);
        responseDto.setHomeServiceAvailable(true);
        responseDto.setWorkingDays("Mon,Tue,Wed,Thu,Fri");
        responseDto.setWorkingHours("10AM-7PM");
        responseDto.setAcquisitionSource("Online");
        responseDto.setDateAdded(Timestamp.valueOf("2023-01-01 10:00:00"));
        responseDto.setAddedBy("Admin");
        responseDto.setMetaData("{\"key\": \"value\"}");
    }

    @Test
    void createMerchant_ShouldReturnCreatedMerchant() throws Exception {
        when(merchantService.createMerchant(any(MerchantRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.merchantId").value(1L))
                .andExpect(jsonPath("$.businessName").value("Test Business"))
                .andExpect(jsonPath("$.merchantEmail").value("merchant@test.com"));

        verify(merchantService, times(1)).createMerchant(any(MerchantRequestDto.class));
    }

    @Test
    void createMerchant_ShouldReturnCreatedMerchant_WhenEmailIsAbsent() throws Exception {
        MerchantRequestDto requestWithoutEmail = new MerchantRequestDto();
        requestWithoutEmail.setBusinessName("Test Business");
        requestWithoutEmail.setCategory("Electronics");
        requestWithoutEmail.setPrimaryPhone("+1234567890");
        requestWithoutEmail.setAddress("123 Test St");

        MerchantResponseDto responseWithoutEmail = new MerchantResponseDto();
        responseWithoutEmail.setMerchantId(2L);
        responseWithoutEmail.setBusinessName("Test Business");
        responseWithoutEmail.setCategory("Electronics");
        responseWithoutEmail.setPrimaryPhone("+1234567890");
        responseWithoutEmail.setAddress("123 Test St");
        responseWithoutEmail.setMerchantEmail(null);

        when(merchantService.createMerchant(any(MerchantRequestDto.class))).thenReturn(responseWithoutEmail);

        mockMvc.perform(post("/api/merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithoutEmail))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.merchantId").value(2L))
                .andExpect(jsonPath("$.merchantEmail").value(nullValue()));

        verify(merchantService, times(1)).createMerchant(any(MerchantRequestDto.class));
    }

    @Test
    void createMerchant_ShouldReturnConflict_WhenPhoneAlreadyExists() throws Exception {
        when(merchantService.createMerchant(any(MerchantRequestDto.class)))
                .thenThrow(new DuplicateResourceException("Merchant with phone number +1234567890 already exists"));

        mockMvc.perform(post("/api/merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()))
                .andExpect(status().isConflict());

        verify(merchantService, times(1)).createMerchant(any(MerchantRequestDto.class));
    }

    @Test
    void createMerchant_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        MerchantRequestDto invalidRequest = new MerchantRequestDto();
        invalidRequest.setBusinessName(""); // Invalid: blank name

        mockMvc.perform(post("/api/merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(merchantService, never()).createMerchant(any(MerchantRequestDto.class));
    }

    @Test
    void getAllMerchants_ShouldReturnListOfMerchants() throws Exception {
        List<MerchantResponseDto> merchants = Arrays.asList(responseDto);
        when(merchantService.getAllMerchants()).thenReturn(merchants);

        mockMvc.perform(get("/api/merchants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].merchantId").value(1L))
                .andExpect(jsonPath("$[0].businessName").value("Test Business"));

        verify(merchantService, times(1)).getAllMerchants();
    }

    @Test
    void getMerchantById_ShouldReturnMerchant() throws Exception {
        when(merchantService.getMerchantById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/merchants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantId").value(1L))
                .andExpect(jsonPath("$.businessName").value("Test Business"));

        verify(merchantService, times(1)).getMerchantById(1L);
    }

    @Test
    void getMerchantById_ShouldReturnNotFound_WhenMerchantDoesNotExist() throws Exception {
        when(merchantService.getMerchantById(1L)).thenThrow(new RuntimeException("Merchant not found with id: 1"));

        mockMvc.perform(get("/api/merchants/1"))
                .andExpect(status().isNotFound());

        verify(merchantService, times(1)).getMerchantById(1L);
    }

    @Test
    void updateMerchant_ShouldReturnUpdatedMerchant() throws Exception {
        when(merchantService.updateMerchant(eq(1L), any(MerchantRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/merchants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantId").value(1L))
                .andExpect(jsonPath("$.businessName").value("Test Business"));

        verify(merchantService, times(1)).updateMerchant(eq(1L), any(MerchantRequestDto.class));
    }

    @Test
    void updateMerchant_ShouldReturnNotFound_WhenMerchantDoesNotExist() throws Exception {
        when(merchantService.updateMerchant(eq(1L), any(MerchantRequestDto.class)))
                .thenThrow(new RuntimeException("Merchant not found with id: 1"));

        mockMvc.perform(put("/api/merchants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(merchantService, times(1)).updateMerchant(eq(1L), any(MerchantRequestDto.class));
    }

    @Test
    void updateMerchant_ShouldReturnConflict_WhenPhoneAlreadyUsedByAnotherMerchant() throws Exception {
        when(merchantService.updateMerchant(eq(1L), any(MerchantRequestDto.class)))
                .thenThrow(new DuplicateResourceException("Merchant with phone number +1234567890 already exists"));

        mockMvc.perform(put("/api/merchants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()))
                .andExpect(status().isConflict());

        verify(merchantService, times(1)).updateMerchant(eq(1L), any(MerchantRequestDto.class));
    }

    @Test
    void updateMerchant_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        MerchantRequestDto invalidRequest = new MerchantRequestDto();
        invalidRequest.setBusinessName(""); // Invalid

        mockMvc.perform(put("/api/merchants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(merchantService, never()).updateMerchant(any(Long.class), any(MerchantRequestDto.class));
    }

    @Test
    void deleteMerchant_ShouldReturnNoContent() throws Exception {
        doNothing().when(merchantService).deleteMerchant(1L);

        mockMvc.perform(delete("/api/merchants/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(merchantService, times(1)).deleteMerchant(1L);
    }

    @Test
    void deleteMerchant_ShouldReturnNotFound_WhenMerchantDoesNotExist() throws Exception {
        doThrow(new RuntimeException("Merchant not found with id: 1")).when(merchantService).deleteMerchant(1L);

        mockMvc.perform(delete("/api/merchants/1")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(merchantService, times(1)).deleteMerchant(1L);
    }
}
