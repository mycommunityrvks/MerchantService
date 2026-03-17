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
        existingMerchant.setMerchantName(requestDto.getMerchantName());
        existingMerchant.setMerchantAddress(requestDto.getMerchantAddress());
        existingMerchant.setMerchantContactNumber(requestDto.getMerchantContactNumber());
        existingMerchant.setMerchantEmailId(requestDto.getMerchantEmailId());
        existingMerchant.setMerchantCategory(requestDto.getMerchantCategory());
        existingMerchant.setMetadata(requestDto.getMetadata());
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
        merchant.setMerchantName(dto.getMerchantName());
        merchant.setMerchantAddress(dto.getMerchantAddress());
        merchant.setMerchantContactNumber(dto.getMerchantContactNumber());
        merchant.setMerchantEmailId(dto.getMerchantEmailId());
        merchant.setMerchantCategory(dto.getMerchantCategory());
        merchant.setMetadata(dto.getMetadata());
        return merchant;
    }

    private MerchantResponseDto mapToResponseDto(Merchant merchant) {
        return new MerchantResponseDto(
                merchant.getMerchantId(),
                merchant.getMerchantName(),
                merchant.getMerchantAddress(),
                merchant.getMerchantContactNumber(),
                merchant.getMerchantEmailId(),
                merchant.getMerchantCategory(),
                merchant.getMetadata(),
                merchant.getCreatedAt()
        );
    }
}
