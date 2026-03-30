package org.example.service;

import org.example.dto.MerchantRequestDto;
import org.example.dto.MerchantResponseDto;
import org.example.entity.Merchant;
import org.example.exception.DuplicateResourceException;
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

        merchant = new Merchant();
        merchant.setMerchantId(1L);
        merchant.setPrimaryPhone("+1234567890");
        merchant.setAlternatePhone("+0987654321");
        merchant.setMerchantEmail("merchant@test.com");
        merchant.setWhatAppAvailable(true);
        merchant.setBusinessName("Test Business");
        merchant.setBusinessType("Retail");
        merchant.setCategory("Electronics");
        merchant.setSubCategory("Mobile");
        merchant.setPersonAge(30);
        merchant.setYearsOfExperience(5);
        merchant.setArea("Downtown");
        merchant.setAddress("123 Test St");
        merchant.setLat(12.345);
        merchant.setLon(67.890);
        merchant.setHomeServiceAvailable(true);
        merchant.setWorkingDays("Mon,Tue,Wed,Thu,Fri");
        merchant.setWorkingHours("10AM-7PM");
        merchant.setAcquisitionSource("Online");
        merchant.setDateAdded(Timestamp.valueOf("2023-01-01 10:00:00"));
        merchant.setAddedBy("Admin");
        merchant.setMetaData("{\"key\": \"value\"}");

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
    void createMerchant_ShouldReturnCreatedMerchant() {
        when(merchantRepository.existsByPrimaryPhone(requestDto.getPrimaryPhone())).thenReturn(false);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        MerchantResponseDto result = merchantService.createMerchant(requestDto);

        assertNotNull(result);
        assertEquals("Test Business", result.getBusinessName());
        assertEquals("merchant@test.com", result.getMerchantEmail());
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    void createMerchant_ShouldReturnCreatedMerchant_WhenEmailIsAbsent() {
        Merchant merchantWithoutEmail = new Merchant();
        merchantWithoutEmail.setMerchantId(2L);
        merchantWithoutEmail.setBusinessName("Test Business");
        merchantWithoutEmail.setCategory("Electronics");
        merchantWithoutEmail.setPrimaryPhone("+1234567890");
        merchantWithoutEmail.setAddress("123 Test St");

        MerchantRequestDto requestWithoutEmail = new MerchantRequestDto();
        requestWithoutEmail.setBusinessName("Test Business");
        requestWithoutEmail.setCategory("Electronics");
        requestWithoutEmail.setPrimaryPhone("+1234567890");
        requestWithoutEmail.setAddress("123 Test St");

        when(merchantRepository.existsByPrimaryPhone("+1234567890")).thenReturn(false);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantWithoutEmail);

        MerchantResponseDto result = merchantService.createMerchant(requestWithoutEmail);

        assertNotNull(result);
        assertEquals("Test Business", result.getBusinessName());
        assertNull(result.getMerchantEmail());
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    void createMerchant_ShouldThrowException_WhenPhoneAlreadyExists() {
        when(merchantRepository.existsByPrimaryPhone(requestDto.getPrimaryPhone())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            merchantService.createMerchant(requestDto);
        });

        assertEquals("Merchant with phone number " + requestDto.getPrimaryPhone() + " already exists", exception.getMessage());
        verify(merchantRepository, times(1)).existsByPrimaryPhone(requestDto.getPrimaryPhone());
        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    void getAllMerchants_ShouldReturnListOfMerchants() {
        List<Merchant> merchants = Arrays.asList(merchant);
        when(merchantRepository.findAll()).thenReturn(merchants);

        List<MerchantResponseDto> result = merchantService.getAllMerchants();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Business", result.get(0).getBusinessName());
        verify(merchantRepository, times(1)).findAll();
    }

    @Test
    void getMerchantById_ShouldReturnMerchant() {
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));

        MerchantResponseDto result = merchantService.getMerchantById(1L);

        assertNotNull(result);
        assertEquals("Test Business", result.getBusinessName());
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
        when(merchantRepository.findByPrimaryPhone(requestDto.getPrimaryPhone())).thenReturn(Optional.of(merchant));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        MerchantResponseDto result = merchantService.updateMerchant(1L, requestDto);

        assertNotNull(result);
        assertEquals("Test Business", result.getBusinessName());
        verify(merchantRepository, times(1)).findById(1L);
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    void updateMerchant_ShouldThrowException_WhenPhoneAlreadyUsedByAnotherMerchant() {
        Merchant anotherMerchant = new Merchant();
        anotherMerchant.setMerchantId(99L);
        anotherMerchant.setPrimaryPhone(requestDto.getPrimaryPhone());

        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));
        when(merchantRepository.findByPrimaryPhone(requestDto.getPrimaryPhone())).thenReturn(Optional.of(anotherMerchant));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            merchantService.updateMerchant(1L, requestDto);
        });

        assertEquals("Merchant with phone number " + requestDto.getPrimaryPhone() + " already exists", exception.getMessage());
        verify(merchantRepository, never()).save(any(Merchant.class));
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
