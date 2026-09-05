package com.razorpay.revenue_recovery.controller;

import com.razorpay.revenue_recovery.model.FailureTransaction;
import com.razorpay.revenue_recovery.model.FailureTransactionRepository;
import com.razorpay.revenue_recovery.service.AIRecoveryService;
import com.razorpay.revenue_recovery.service.RazorpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/recovery")
public class RecoveryController {

    private final RazorpayService razorpayService;
    private final AIRecoveryService aiRecoveryService;
    private final FailureTransactionRepository repository;

    public RecoveryController(RazorpayService razorpayService,
                              AIRecoveryService aiRecoveryService,
                              FailureTransactionRepository repository) {
        this.razorpayService = razorpayService;
        this.aiRecoveryService = aiRecoveryService;
        this.repository = repository;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        List<FailureTransaction> all = repository.findAll();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalFailedAmount", all.size() * 4999);
        metrics.put("totalRecoveredAmount", 0);
        metrics.put("recoveryRate", "0%");
        metrics.put("activeWorkflows", all.size());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<FailureTransaction>> getTransactions() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/record-failure")
    public ResponseEntity<FailureTransaction> recordFailure(@RequestBody Map<String, String> payload) {
        String name = payload.getOrDefault("name", "Guest User");
        String email = payload.getOrDefault("email", "guest@example.com");
        String errorCode = payload.getOrDefault("errorCode", "BAD_REQUEST_PAYMENT_TIMED_OUT");

        String txId = "pay_" + UUID.randomUUID().toString().substring(0, 8);
        String strategy = aiRecoveryService.analyzeFailureAndSuggestStrategy(errorCode);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        FailureTransaction tx = new FailureTransaction(
                txId, name, email, "₹4,999", errorCode, "Action Required", strategy, timestamp
        );

        return ResponseEntity.ok(repository.save(tx));
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeRecovery(@RequestBody Map<String, String> payload) {
        String txId = payload.get("transactionId");
        String paymentLink = razorpayService.createRecoveryPaymentLink("4999", "customer@example.com");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Recovery workflow initiated for " + txId);
        response.put("recoveryLink", paymentLink);
        return ResponseEntity.ok(response);
    }
}