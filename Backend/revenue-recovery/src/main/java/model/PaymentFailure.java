package com.razorpay.revenue_recovery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_failures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private Double amount;
    private String failureReason;
    private String aiStrategy;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.aiStrategy == null) {
            this.aiStrategy = "AI Nudge: Send discounted retry link via WhatsApp/Email";
        }
    }
}