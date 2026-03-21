package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "merchants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long merchantId;

    // Business Info
    @NotBlank(message = "Business name is required")
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "business_type")
    private String businessType;

    @NotBlank(message = "Category is required")
    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    // Contact Info
    @NotBlank(message = "Merchant email is required")
    @Email(message = "Invalid email format")
    @Column(name = "merchant_email", nullable = false, unique = true)
    private String merchantEmail;

    @NotBlank(message = "Primary phone is required")
    @Column(name = "primary_phone", nullable = false)
    private String primaryPhone;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "area")
    private String area;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    // Personal Info
    @Column(name = "person_age")
    private Integer personAge;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    // Operational Info
    @Column(name = "whatsapp_available")
    private Boolean whatAppAvailable;

    @Column(name = "home_service_available")
    private Boolean homeServiceAvailable;

    @Column(name = "working_days")
    private String workingDays; // e.g., "Mon,Tue,Wed,Thu,Fri"

    @Column(name = "working_hours")
    private String workingHours; // e.g., "10AM-7PM"

    // Metadata
    @Column(name = "acquisition_source")
    private String acquisitionSource;

    @Column(name = "added_by")
    private String addedBy;

    @CreationTimestamp
    @Column(name = "date_added", updatable = false)
    private Timestamp dateAdded;

    @Column(name = "meta_data", columnDefinition = "TEXT")
    private String metaData; // JSON string for extensibility
}
