package org.example.service;

import org.example.dto.MerchantRequestDto;
import org.example.dto.MerchantResponseDto;
import org.example.entity.Merchant;
import org.example.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private MerchantService merchantService;

    private MerchantRequestDto requestDto;
    private Merchant merchant;
    private MerchantResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new MerchantRequestDto();
        requestDto.setMerchantName("Test Merchant");
        requestDto.setMerchantAddress("123 Test St");
        requestDto.setMerchantContactNumber("+1234567890");
        requestDto.setMerchantEmailId("test@example.com");
        requestDto.setMerchantCategory("Retail");
        requestDto.setMetadata("{\"key\": \"value\"}");

        merchant = new Merchant();
        merchant.setMerchantId(1L);
        merchant.setMerchantName("Test Merchant");
        merchant.setMerchantAddress("123 Test St");
        merchant.setMerchantContactNumber("+1234567890");
        merchant.setMerchantEmailId("test@example.com");
        merchant.setMerchantCategory("Retail");
        merchant.setMetadata("{\"key\": \"value\"}");
        merchant.setCreatedAt(Timestamp.valueOf("2023-01-01 10:00:00"));

        responseDto = new MerchantResponseDto();
        responseDto.setMerchantId(1L);
        responseDto.setMerchantName("Test Merchant");
        responseDto.setMerchantAddress("123 Test St");
        responseDto.setMerchantContactNumber("+1234567890");
        responseDto.setMerchantEmailId("test@example.com");
        responseDto.setMerchantCategory("Retail");
        responseDto.setMetadata("{\"key\": \"value\"}");
        responseDto.setCreatedAt(Timestamp.valueOf("2023-01-01 10:00:00"));
    }

    @Test
    void createMerchant_ShouldReturnCreatedMerchant() {
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        MerchantResponseDto result = merchantService.createMerchant(requestDto);

        assertNotNull(result);
        assertEquals("Test Merchant", result.getMerchantName());
        assertEquals("test@example.com", result.getMerchantEmailId());
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    void getAllMerchants_ShouldReturnListOfMerchants() {
        List<Merchant> merchants = Arrays.asList(merchant);
        when(merchantRepository.findAll()).thenReturn(merchants);

        List<MerchantResponseDto> result = merchantService.getAllMerchants();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Merchant", result.get(0).getMerchantName());
        verify(merchantRepository, times(1)).findAll();
    }

    @Test
    void getMerchantById_ShouldReturnMerchant() {
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));

        MerchantResponseDto result = merchantService.getMerchantById(1L);

        assertNotNull(result);
        assertEquals("Test Merchant", result.getMerchantName());
        verify(merchantRepository, times(1)).findById(1L);
    }

    @Test
    void getMerchantById_ShouldThrowException_WhenMerchantNotFound() {
        when(merchantRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            merchantService.getMerchantById(1L);
        });

        assertEquals("Merchant not found with id: 1", exception.getMessage());
        verify(merchantRepository, times(1)).findById(1L);
    }

    @Test
    void updateMerchant_ShouldReturnUpdatedMerchant() {
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        MerchantResponseDto result = merchantService.updateMerchant(1L, requestDto);

        assertNotNull(result);
        assertEquals("Test Merchant", result.getMerchantName());
        verify(merchantRepository, times(1)).findById(1L);
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    void updateMerchant_ShouldThrowException_WhenMerchantNotFound() {
        when(merchantRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            merchantService.updateMerchant(1L, requestDto);
        });

        assertEquals("Merchant not found with id: 1", exception.getMessage());
        verify(merchantRepository, times(1)).findById(1L);
        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    void deleteMerchant_ShouldDeleteSuccessfully() {
        when(merchantRepository.existsById(1L)).thenReturn(true);
        doNothing().when(merchantRepository).deleteById(1L);

        assertDoesNotThrow(() -> merchantService.deleteMerchant(1L));

        verify(merchantRepository, times(1)).existsById(1L);
        verify(merchantRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMerchant_ShouldThrowException_WhenMerchantNotFound() {
        when(merchantRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            merchantService.deleteMerchant(1L);
        });

        assertEquals("Merchant not found with id: 1", exception.getMessage());
        verify(merchantRepository, times(1)).existsById(1L);
        verify(merchantRepository, never()).deleteById(1L);
    }
}
