package com.hsf.hsf_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDTO {
    private Long licenseId;
    private String licenseKey;
    private String username;
    private String productName;
    private boolean enabled;
}
