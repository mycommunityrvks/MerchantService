package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.MerchantRequestDto;
import org.example.dto.MerchantResponseDto;
import org.example.entity.Merchant;
import org.example.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantResponseDto createMerchant(MerchantRequestDto requestDto) {
        Merchant merchant = mapToEntity(requestDto);
        Merchant savedMerchant = merchantRepository.save(merchant);
        return mapToResponseDto(savedMerchant);
    }

    public List<MerchantResponseDto> getAllMerchants() {
        return merchantRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public MerchantResponseDto getMerchantById(Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found with id: " + id));
        return mapToResponseDto(merchant);
    }

    public MerchantResponseDto updateMerchant(Long id, MerchantRequestDto requestDto) {
        Merchant existingMerchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found with id: " + id));
        // Update fields
        existingMerchant.setBusinessName(requestDto.getBusinessName());
        existingMerchant.setMerchantEmail(requestDto.getMerchantEmail());
        existingMerchant.setPrimaryPhone(requestDto.getPrimaryPhone());
        existingMerchant.setAlternatePhone(requestDto.getAlternatePhone());
        existingMerchant.setCategory(requestDto.getCategory());
        existingMerchant.setSubCategory(requestDto.getSubCategory());
        existingMerchant.setBusinessType(requestDto.getBusinessType());
        existingMerchant.setMerchantAddress(requestDto.getAddress());
        existingMerchant.setArea(requestDto.getArea());
        existingMerchant.setLat(requestDto.getLat());
        existingMerchant.setLon(requestDto.getLon());
        existingMerchant.setPersonAge(requestDto.getPersonAge());
        existingMerchant.setYearsOfExperience(requestDto.getYearsOfExperience());
        existingMerchant.setWhatAppAvailable(requestDto.getWhatAppAvailable());
        existingMerchant.setHomeServiceAvailable(requestDto.getHomeServiceAvailable());
        existingMerchant.setWorkingDays(requestDto.getWorkingDays());
        existingMerchant.setWorkingHours(requestDto.getWorkingHours());
        existingMerchant.setAcquisitionSource(requestDto.getAcquisitionSource());
        existingMerchant.setAddedBy(requestDto.getAddedBy());
        existingMerchant.setMetaData(requestDto.getMetaData());
        Merchant updatedMerchant = merchantRepository.save(existingMerchant);
        return mapToResponseDto(updatedMerchant);
    }

    public void deleteMerchant(Long id) {
        if (!merchantRepository.existsById(id)) {
            throw new RuntimeException("Merchant not found with id: " + id);
        }
        merchantRepository.deleteById(id);
    }

    private Merchant mapToEntity(MerchantRequestDto dto) {
        Merchant merchant = new Merchant();
        merchant.setBusinessName(dto.getBusinessName());
        merchant.setMerchantEmail(dto.getMerchantEmail());
        merchant.setPrimaryPhone(dto.getPrimaryPhone());
        merchant.setAlternatePhone(dto.getAlternatePhone());
        merchant.setCategory(dto.getCategory());
        merchant.setSubCategory(dto.getSubCategory());
        merchant.setBusinessType(dto.getBusinessType());
        merchant.setMerchantAddress(dto.getAddress());
        merchant.setArea(dto.getArea());
        merchant.setLat(dto.getLat());
        merchant.setLon(dto.getLon());
        merchant.setPersonAge(dto.getPersonAge());
        merchant.setYearsOfExperience(dto.getYearsOfExperience());
        merchant.setWhatAppAvailable(dto.getWhatAppAvailable());
        merchant.setHomeServiceAvailable(dto.getHomeServiceAvailable());
        merchant.setWorkingDays(dto.getWorkingDays());
        merchant.setWorkingHours(dto.getWorkingHours());
        merchant.setAcquisitionSource(dto.getAcquisitionSource());
        merchant.setAddedBy(dto.getAddedBy());
        merchant.setMetaData(dto.getMetaData());
        return merchant;
    }

    private MerchantResponseDto mapToResponseDto(Merchant merchant) {
        return new MerchantResponseDto(
                merchant.getMerchantId(),
                merchant.getBusinessName(),
                merchant.getBusinessType(),
                merchant.getCategory(),
                merchant.getSubCategory(),
                merchant.getMerchantEmail(),
                merchant.getPrimaryPhone(),
                merchant.getAlternatePhone(),
                merchant.getMerchantAddress(),
                merchant.getArea(),
                merchant.getLat(),
                merchant.getLon(),
                merchant.getPersonAge(),
                merchant.getYearsOfExperience(),
                merchant.getWhatAppAvailable(),
                merchant.getHomeServiceAvailable(),
                merchant.getWorkingDays(),
                merchant.getWorkingHours(),
                merchant.getAcquisitionSource(),
                merchant.getAddedBy(),
                merchant.getDateAdded(),
                merchant.getMetaData()
        );
    }
}
