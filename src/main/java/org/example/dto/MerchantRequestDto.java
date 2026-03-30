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

    // Business Info
    @NotBlank(message = "Business name is required")
    private String businessName;

    private String businessType;

    @NotBlank(message = "Category is required")
    private String category;

    private String subCategory;

    // Contact Info
    @Email(message = "Invalid email format")
    private String merchantEmail;

    @NotBlank(message = "Primary phone is required")
    private String primaryPhone;

    private String alternatePhone;

    // Location Info
    @NotBlank(message = "Address is required")
    private String address;

    private String area;

    private Double lat;

    private Double lon;

    // Personal Info
    private Integer personAge;

    private Integer yearsOfExperience;

    // Operational Info
    private Boolean whatAppAvailable;

    private Boolean homeServiceAvailable;

    private String workingDays; // e.g., "Mon,Tue,Wed,Thu,Fri"

    private String workingHours; // e.g., "10AM-7PM"

    // Metadata
    private String acquisitionSource;

    private String addedBy;

    private String metaData; // Optional JSON string
}
