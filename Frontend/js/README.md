# 🚀 RevRecover — Autonomous Revenue Recovery Platform

[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Razorpay](https://img.shields.io/badge/Payments-Razorpay%20Checkout-blue)](https://razorpay.com/)
[![Database](https://img.shields.io/badge/Database-H2%20In--Memory-orange)](https://www.h2database.com/)
[![Tailwind CSS](https://img.shields.io/badge/Frontend-Tailwind%20CSS-38B2AC)](https://tailwindcss.com/)

RevRecover is a real-time payment recovery system designed to catch payment abandonments and checkout failures instantly. Built for the Razorpay Buildathon, it logs drop-offs in a live diagnostic dashboard and suggests targeted AI strategies to re-engage lost customers.

---

## 🎯 Problem & Solution

* **The Problem:** Up to 70% of online checkout attempts end in drop-offs or failures without merchant visibility or immediate follow-up.
* **The Solution:** RevRecover listens to Razorpay modal dismissal and failure events, sends real-time diagnostic payloads to a Spring Boot backend, and displays recovery actionable strategies inside a live merchant dashboard.

---

## 🔄 System Architecture & Workflow

```mermaid
sequenceDiagram
    autonumber
    actor Customer
    participant Frontend as Storefront (checkout.js)
    participant Razorpay as Razorpay SDK
    participant Backend as Spring Boot API
    participant DB as H2 Database
    participant Dashboard as Merchant Dashboard

    Customer->>Frontend: Fills Name/Email & Clicks Pay
    Frontend->>Razorpay: Opens Payment Modal
    Customer->>Razorpay: Closes Modal / Abandons
    Razorpay-->>Frontend: Triggers modal.ondismiss
    Frontend->>Backend: POST /api/payments/failure
    Backend->>DB: Saves PaymentFailure Entity
    Backend-->>Frontend: 200 OK (Failure Logged)
    Dashboard->>Backend: GET /api/payments/failures (Polls every 4s)
    Backend-->>Dashboard: Returns Failure Queue Data
    Dashboard->>Customer: Merchant clicks "Trigger Nudge" (WhatsApp/Email)