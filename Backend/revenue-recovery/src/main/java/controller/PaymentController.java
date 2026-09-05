package com.razorpay.revenue_recovery.controller;

import com.razorpay.revenue_recovery.model.PaymentFailure;
import com.razorpay.revenue_recovery.repository.PaymentFailureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentFailureRepository paymentFailureRepository;

    @PostMapping("/failure")
    public ResponseEntity<?> logFailure(@RequestBody PaymentFailure failureData) {
        PaymentFailure savedFailure = paymentFailureRepository.save(failureData);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Payment failure recorded",
                "data", savedFailure
        ));
    }

    @GetMapping("/failures")
    public ResponseEntity<List<PaymentFailure>> getAllFailures() {
        List<PaymentFailure> failures = paymentFailureRepository.findAllByOrderByIdDesc();
        return ResponseEntity.ok(failures);
    }
}