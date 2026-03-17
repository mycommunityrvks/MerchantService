package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRequestDto {

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    @NotBlank(message = "Merchant address is required")
    private String merchantAddress;

    @NotBlank(message = "Merchant contact number is required")
    private String merchantContactNumber;

    @NotBlank(message = "Merchant email is required")
    @Email(message = "Invalid email format")
    private String merchantEmailId;

    @NotBlank(message = "Merchant category is required")
    private String merchantCategory;

    private String metadata; // Optional JSON string
}
