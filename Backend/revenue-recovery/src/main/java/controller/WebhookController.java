package com.razorpay.revenue_recovery.controller;

import com.razorpay.revenue_recovery.service.AIRecoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final AIRecoveryService aiRecoveryService;

    public WebhookController(AIRecoveryService aiRecoveryService) {
        this.aiRecoveryService = aiRecoveryService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody Map<String, Object> payload) {
        String event = (String) payload.get("event");

        if ("payment.failed".equals(event)) {
            String strategy = aiRecoveryService.analyzeFailureAndSuggestStrategy("BAD_REQUEST_PAYMENT_TIMED_OUT");
            System.out.println("AI Agent Webhook Executed. Strategy: " + strategy);
        }

        return ResponseEntity.ok("Webhook Processed");
    }
}