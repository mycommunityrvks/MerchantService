package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseDto {

    private Long merchantId;

    // Business Info
    private String businessName;
    private String businessType;
    private String category;
    private String subCategory;

    // Contact Info
    private String merchantEmail;
    private String primaryPhone;
    private String alternatePhone;

    // Location Info
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
    private String workingDays;
    private String workingHours;

    // Metadata
    private String acquisitionSource;
    private String addedBy;
    private Timestamp dateAdded;
    private String metaData;
}
