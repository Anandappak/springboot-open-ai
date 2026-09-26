package com.example.opensearchassistant.model;

import java.math.BigDecimal;

public record TempleDashboardSummary(long totalMembers, long totalDonations, long totalEvents,
                                    BigDecimal totalDonationAmount) {
}
