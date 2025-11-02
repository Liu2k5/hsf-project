package com.hsf.hsf_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsSummaryDTO {
    private double totalRevenue;
    private long newOrdersToday;
    private long activeSessions;
}
