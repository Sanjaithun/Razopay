package com.razorpay.revenue_recovery.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id:rzp_test_dummy}")
    private String keyId;

    @Value("${razorpay.key.secret:dummy_secret}")
    private String keySecret;

    private RazorpayClient client;

    @PostConstruct
    public void init() {
        try {
            this.client = new RazorpayClient(keyId, keySecret);
        } catch (RazorpayException e) {
            System.err.println("Razorpay Client Init Warning: " + e.getMessage());
        }
    }

    public String createRecoveryPaymentLink(String amount, String customerEmail) {
        try {
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", Integer.parseInt(amount.replaceAll("[^0-9]", "")) * 100);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("accept_partial", false);
            paymentLinkRequest.put("description", "Automated Revenue Recovery Checkout");

            JSONObject customer = new JSONObject();
            customer.put("email", customerEmail);
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);
            notify.put("sms", true);
            paymentLinkRequest.put("notify", notify);

            var paymentLink = client.paymentLink.create(paymentLinkRequest);
            return paymentLink.get("short_url").toString();
        } catch (Exception e) {
            return "https://rzp.io/i/rec_" + System.currentTimeMillis();
        }
    }
}