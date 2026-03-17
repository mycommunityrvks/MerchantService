package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.MerchantRequestDto;
import org.example.dto.MerchantResponseDto;
import org.example.service.MerchantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public ResponseEntity<MerchantResponseDto> createMerchant(@Valid @RequestBody MerchantRequestDto requestDto) {
        MerchantResponseDto response = merchantService.createMerchant(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MerchantResponseDto>> getAllMerchants() {
        List<MerchantResponseDto> merchants = merchantService.getAllMerchants();
        return ResponseEntity.ok(merchants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchantResponseDto> getMerchantById(@PathVariable Long id) {
        MerchantResponseDto merchant = merchantService.getMerchantById(id);
        return ResponseEntity.ok(merchant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MerchantResponseDto> updateMerchant(@PathVariable Long id, @Valid @RequestBody MerchantRequestDto requestDto) {
        MerchantResponseDto updatedMerchant = merchantService.updateMerchant(id, requestDto);
        return ResponseEntity.ok(updatedMerchant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
        return ResponseEntity.noContent().build();
    }
}
