# 🚀 RevRecover — Autonomous Revenue Recovery Platform

[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Razorpay](https://img.shields.io/badge/Payments-Razorpay%20Checkout-blue)](https://razorpay.com/)
[![Database](https://img.shields.io/badge/Database-H2%20In--Memory-orange)](https://www.h2database.com/)
[![Tailwind CSS](https://img.shields.io/badge/Frontend-Tailwind%20CSS-38B2AC)](https://tailwindcss.com/)

> **[IMAGE 1 INSTRUCTION: Hero Banner]**  
> *Where to put it:* Right here, below the badges.  
> *What to add:* Create a high-quality 1200x400px banner image named `01-hero-banner.png` showing your project logo, the Razorpay logo, and a tagline like "Turning Drop-offs into Dollars."  
> *Markdown code:* `![RevRecover Banner](./docs/images/01-hero-banner.png)`

RevRecover is a real-time payment recovery system designed to capture checkout abandonments the exact millisecond they happen. Built specifically for the Razorpay Buildathon, this platform bridges the critical gap between payment gateway drop-offs and actionable merchant recovery strategies. By leveraging Razorpay's native event hooks and a robust Spring Boot backend, RevRecover logs failures into a live diagnostic dashboard, allowing merchants to trigger AI-driven recovery nudges instantly.

---

## 🎯 The Problem & Our Solution

**The Industry Problem:**  
E-commerce businesses lose billions annually to cart abandonment. Up to 70% of online checkout attempts end in drop-offs or failures (network issues, user hesitation, closed tabs) without the merchant ever gaining visibility. Standard analytics tools only show that a user left, but they do not capture the exact payment context or provide immediate follow-up mechanisms.

**The RevRecover Solution:**  
We built a deterministic interception layer. Instead of waiting for a webhook from a completed payment, RevRecover actively listens to the Razorpay checkout iframe lifecycle. If a user clicks the "X" to close the modal or their payment fails, our system instantly captures the session context (Name, Email, Intended Amount) and pipes it to a live merchant dashboard. 

> **[IMAGE 2 INSTRUCTION: The Storefront UI]**  
> *Where to put it:* Here, to visually introduce the user journey.  
> *What to add:* A screenshot named `02-storefront.png` showing your clean, Tailwind-styled checkout form before the user clicks pay.  
> *Markdown code:* `![Storefront UI](./docs/images/02-storefront.png)`

---

## 🏗️ System Architecture & Design

The system follows a lightweight, decoupled client-server architecture designed for rapid event ingestion, zero-latency persistence, and real-time merchant monitoring.

### 1. High-Level Component Diagram

This flow illustrates how data moves from the client's browser through the Razorpay ecosystem, into our custom ingestion API, and finally to the merchant's screen.

```mermaid
graph TD
    subgraph Client-Side [Frontend Storefront]
        UI[Checkout UI]
        JS[checkout.js Event Listener]
    end

    subgraph Payment Gateway
        RZP[Razorpay Standard Checkout SDK]
    end

    subgraph Server-Side [Spring Boot API]
        Ctrl[PaymentController]
        Svc[PaymentService]
        Repo[PaymentRepository]
    end

    subgraph Data Layer
        DB[(H2 In-Memory DB)]
    end
    
    subgraph Merchant Operations
        Dash[Live Dashboard UI]
        Poll[Polling Service]
    end

    UI -->|Triggers| RZP
    RZP -- "User Exit (ondismiss)" --> JS
    JS -- "POST /api/payments/failure" --> Ctrl
    Ctrl --> Svc --> Repo --> DB
    Poll -- "GET /api/payments/failures" --> Ctrl
    Dash --> Poll