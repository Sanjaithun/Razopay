package com.razorpay.revenue_recovery.service;

import org.springframework.stereotype.Service;

@Service
public class AIRecoveryService {

    public String analyzeFailureAndSuggestStrategy(String errorCode) {
        if (errorCode == null) return "Generate dynamic single-click Razorpay payment link";

        return switch (errorCode) {
            case "BAD_REQUEST_PAYMENT_TIMED_OUT" -> "Issue instant 5% discount payment link via WhatsApp nudge";
            case "GATEWAY_ERROR_ISSUER_DOWN" -> "Schedule automated payment link retry after bank gateway recovery";
            case "PAYMENT_CANCELLED_BY_USER" -> "Trigger personalized checkout assistance email with active support chat";
            default -> "Generate smart recovery link with fallback UPI payment methods";
        };
    }
}