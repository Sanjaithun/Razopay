package com.razorpay.revenue_recovery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailureTransaction {
    @Id
    private String id;
    private String customerName;
    private String email;
    private String amount;
    private String errorCode;
    private String status;
    private String suggestedStrategy;
    private String createdAt;
}