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

    @NotBlank(message = "Merchant name is required")
    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @NotBlank(message = "Merchant address is required")
    @Column(name = "merchant_address", nullable = false)
    private String merchantAddress;

    @NotBlank(message = "Merchant contact number is required")
    @Column(name = "merchant_contact_number", nullable = false)
    private String merchantContactNumber;

    @NotBlank(message = "Merchant email is required")
    @Email(message = "Invalid email format")
    @Column(name = "merchant_email_id", nullable = false, unique = true)
    private String merchantEmailId;

    @NotBlank(message = "Merchant category is required")
    @Column(name = "merchant_category", nullable = false)
    private String merchantCategory;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for extensibility

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}
