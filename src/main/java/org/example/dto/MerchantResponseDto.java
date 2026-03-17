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
    private String merchantName;
    private String merchantAddress;
    private String merchantContactNumber;
    private String merchantEmailId;
    private String merchantCategory;
    private String metadata;
    private Timestamp createdAt;
}
