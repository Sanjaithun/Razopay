# 🚀 RevRecover — Autonomous Revenue Recovery Platform

[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Razorpay](https://img.shields.io/badge/Payments-Razorpay%20Checkout-blue)](https://razorpay.com/)
[![Database](https://img.shields.io/badge/Database-H2%20In--Memory-orange)](https://www.h2database.com/)
[![Tailwind CSS](https://img.shields.io/badge/Frontend-Tailwind%20CSS-38B2AC)](https://tailwindcss.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

RevRecover is a real-time payment recovery platform engineered to intercept checkout abandonments at the exact millisecond they occur. Built for the Razorpay Buildathon, this platform bridges the gap between payment gateway drop-offs and actionable merchant recovery strategies. By tapping into Razorpay's native checkout hooks and a high-performance Spring Boot API, RevRecover logs failures into a live diagnostic dashboard, empowering merchants to initiate AI-assisted recovery nudges immediately.

---

## 📸 Screenshots

<table>
  <tr>
    <td align="center"><b>🔐 Login / Checkout</b></td>
    <td align="center"><b>💳 Payment Gateway</b></td>
    <td align="center"><b>📊 Merchant Dashboard</b></td>
  </tr>
  <tr>
    <td><img src="./images/page-1.png" alt="Login / Checkout Page" width="280"/></td>
    <td><img src="./images/page-2.png" alt="Payment Gateway" width="280"/></td>
    <td><img src="./images/page-3.png" alt="Merchant Dashboard" width="280"/></td>
  </tr>
</table>

---

## 📑 Table of Contents

- [Problem & Solution](#-problem--solution)
- [System Architecture & Engineering Design](#️-system-architecture--engineering-design)
- [Tech Stack & Infrastructure Specifications](#-tech-stack--infrastructure-specifications)
- [Core Mechanics: Detailed Execution Steps](#️-core-mechanics-detailed-execution-steps)
- [API Reference & Endpoints](#-api-reference--endpoints)
- [Local Deployment & Running Guide](#-local-deployment--running-guide)
- [Future Roadmap: AI Strategy & Autonomous Recovery Engine](#-future-roadmap-ai-strategy--autonomous-recovery-engine)
- [License & Acknowledgements](#-license--acknowledgements)

---

## 🎯 Problem & Solution

**The Core Challenge:**
E-commerce businesses lose significant revenue due to cart abandonment. Up to 70% of online checkout attempts end in drop-offs or failures (network disruptions, user hesitation, closed tabs) without merchant visibility. Standard analytics only indicate that a user left; they do not capture payment intent, context, or actionable follow-up vectors.

**The RevRecover Solution:**
RevRecover introduces a real-time interception layer. Instead of waiting for asynchronous server webhooks from completed transactions, RevRecover actively monitors the Razorpay checkout iframe lifecycle. When a user exits the modal or experiences a failure, the client captures session metadata (Name, Email, Intended Amount, Reason) and posts it to a merchant diagnostic queue.

---

## 🏗️ System Architecture & Engineering Design

### 1. High-Level System Architecture

```mermaid
graph TD
    subgraph Client-Side [Frontend Storefront]
        UI[Checkout UI - index.html]
        JS[checkout.js Event Interceptor]
    end

    subgraph Payment Gateway
        RZP[Razorpay Standard Checkout SDK]
    end

    subgraph Server-Side [Spring Boot API]
        Ctrl[PaymentController]
        Svc[PaymentService]
        Repo[PaymentRepository]
    end

    subgraph Data Persistence
        DB[(H2 In-Memory DB)]
    end
    
    subgraph Merchant Portal
        Dash[Live Dashboard - dashboard.html]
        Poll[Polling Client Service]
    end

    UI -->|1. Triggers Modal| RZP
    RZP -- "2. User Exit (ondismiss)" --> JS
    JS -- "3. POST /api/payments/failure" --> Ctrl
    Ctrl -->|4. Persist Event| Svc --> Repo --> DB
    Poll -- "5. GET /api/payments/failures" --> Ctrl
    Dash -->|6. Render Metrics & Queue| Poll
```

### 2. Comprehensive Class Diagram (UML)

```mermaid
classDiagram
    class PaymentFailure {
        -Long id
        -String customerName
        -String customerEmail
        -Double amount
        -String failureReason
        -LocalDateTime timestamp
        -String status
        +getId() Long
        +getCustomerName() String
        +getCustomerEmail() String
        +getAmount() Double
        +getFailureReason() String
        +getTimestamp() LocalDateTime
        +getStatus() String
        +setStatus(String status) Void
    }

    class PaymentRepository {
        <<interface>>
        +findAllByOrderByTimestampDesc() List~PaymentFailure~
        +save(PaymentFailure entity) PaymentFailure
    }

    class PaymentController {
        -PaymentRepository paymentRepository
        +logFailure(PaymentFailure payload) ResponseEntity~Map~
        +getAllFailures() ResponseEntity~List~PaymentFailure~~
    }

    PaymentController --> PaymentRepository : injects via @Autowired
    PaymentRepository --> PaymentFailure : manages ORM mapping
```

### 3. Complete End-to-End Sequence Flow

```mermaid
sequenceDiagram
    autonumber
    actor Customer
    participant Frontend as Storefront (checkout.js)
    participant Razorpay as Razorpay SDK Modal
    participant Backend as Spring Boot API
    participant DB as H2 Database
    participant Dashboard as Merchant Dashboard

    Customer->>Frontend: Clicks "Complete Secure Payment"
    Frontend->>Razorpay: Initializes options & opens modal
    Customer->>Razorpay: Closes modal (X button)
    Razorpay-->>Frontend: Triggers modal.ondismiss callback
    Frontend->>Backend: POST /api/payments/failure (JSON Payload)
    Backend->>DB: INSERT into payment_failures table
    DB-->>Backend: Confirms row insertion (ID assigned)
    Backend-->>Frontend: 200 OK {"status": "success", "message": "Recorded"}
    Frontend->>Customer: Displays cancellation notice banner
    loop Every 4 Seconds (Polling)
        Dashboard->>Backend: GET /api/payments/failures
        Backend->>DB: SELECT * ORDER BY timestamp DESC
        DB-->>Backend: Returns List<PaymentFailure>
        Backend-->>Dashboard: 200 OK JSON Array
    end
    Dashboard->>Dashboard: Re-calculates Lost Revenue & renders rows
```

---

## 💻 Tech Stack & Infrastructure Specifications

| Layer | Technology | Specification & Role |
| --- | --- | --- |
| **Frontend UI** | HTML5 / Tailwind CSS | Utility-first, responsive interface with no external bundle overhead. |
| **Client Engine** | JavaScript (ES6+) | Handles DOM manipulation, Razorpay SDK lifecycle events, and fetch requests. |
| **Gateway SDK** | Razorpay Standard Checkout | Embedded payment modal exposing `handler` and `modal.ondismiss` hooks. |
| **Backend Server** | Spring Boot 3.x (Java 17+) | REST API engine, JSON serialization, dependency injection, and CORS management. |
| **Persistence** | Spring Data JPA / Hibernate | Object-Relational Mapping (ORM) layer executing SQL generation. |
| **Database** | H2 Database Engine | In-memory DB running at `jdbc:h2:mem:testdb` with built-in web console. |

---

## ⚙️ Core Mechanics: Detailed Execution Steps

### Interception Layer

The standard Razorpay integration listens primarily to successful payment callbacks. RevRecover binds to the `modal.ondismiss` lifecycle handler inside `js/checkout.js`:

```javascript
modal: {
    ondismiss: function () {
        fetch("http://localhost:8080/api/payments/failure", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                customerName: name,
                customerEmail: email,
                amount: 4999.00,
                failureReason: "Payment window closed by user"
            })
        });
    }
}
```

### Ingestion & Persistence Layer

The Spring Boot REST controller intercepts incoming requests, binds them to the `PaymentFailure` entity model, and persists them into H2 storage:

```java
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/failure")
    public ResponseEntity<Map<String, Object>> logFailure(@RequestBody PaymentFailure failure) {
        failure.setTimestamp(LocalDateTime.now());
        PaymentFailure saved = paymentRepository.save(failure);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Payment failure recorded");
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }
}
```

---

## 🔌 API Reference & Endpoints

### 1. Log Payment Failure Event

* **HTTP Method:** `POST`
* **Route:** `/api/payments/failure`
* **Content-Type:** `application/json`

**Sample Request Payload:**

```json
{
    "customerName": "Monika",
    "customerEmail": "jkmonika@gmail.com",
    "amount": 4999.00,
    "failureReason": "Payment window closed by user"
}
```

**Sample Response Payload (200 OK):**

```json
{
    "status": "success",
    "message": "Payment failure recorded",
    "data": {
        "id": 5,
        "customerName": "Monika",
        "customerEmail": "jkmonika@gmail.com",
        "amount": 4999.0,
        "failureReason": "Payment window closed by user",
        "timestamp": "2026-09-05T19:00:00.123456"
    }
}
```

### 2. Fetch All Logged Failures

* **HTTP Method:** `GET`
* **Route:** `/api/payments/failures`
* **Content-Type:** `application/json`

**Sample Response Payload (200 OK):**

```json
[
    {
        "id": 5,
        "customerName": "Monika",
        "customerEmail": "jkmonika@gmail.com",
        "amount": 4999.0,
        "failureReason": "Payment window closed by user",
        "timestamp": "2026-09-05T19:00:00.123456"
    }
]
```

---

## 🚀 Local Deployment & Running Guide

### System Prerequisites

* **Java Development Kit (JDK) 17** or higher installed.
* **Apache Maven 3.8+** configured in environment path.
* A web browser (Google Chrome, Microsoft Edge, or Mozilla Firefox).
* VS Code with **Live Server Extension** (recommended for static file serving).
* Active **Razorpay Test Key ID**.

### Step 1: Repository Cloning

```bash
git clone https://github.com/Sanjaithun/Razopay
cd revenue-recovery-razorpay
```

### Step 2: Key Configuration

1. Open `frontend/js/checkout.js`.
2. Locate the Razorpay options variable.
3. Insert your Razorpay Test Key:
```javascript
key: "rzp_test_YOUR_API_KEY_HERE"
```

### Step 3: Launch Spring Boot Backend

From the root directory containing `pom.xml`, execute:

```bash
# Unix/Linux/macOS
./mvnw spring-boot:run

# Windows Command Prompt / PowerShell
mvnw.cmd spring-boot:run
```

* Server initialized at: `http://localhost:8080`
* H2 Database Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Password: *blank*)

### Step 4: Serve Frontend Applications

1. Open the `/frontend` directory in VS Code.
2. Click **Go Live** on the bottom status bar to serve static files on `http://127.0.0.1:5500`.
3. Open two browser windows:
* **Customer Storefront:** `http://127.0.0.1:5500/index.html`
* **Merchant Dashboard:** `http://127.0.0.1:5500/dashboard.html`

### Step 5: Verify Functional Recovery Workflow

1. Navigate to `index.html`, enter customer information, and click **Complete Secure Payment**.
2. When the Razorpay iframe opens, click the **X** button to simulate user abandonment.
3. Check `dashboard.html` to verify that the failure is ingested, metric cards recalculate, and the incident appears in the queue.

---

## 🔮 Future Roadmap: AI Strategy & Autonomous Recovery Engine

While the core platform establishes the zero-latency interception pipeline, the next phase of **RevRecover** introduces an **AI-driven Autonomous Recovery Engine**. Instead of relying on manual merchant intervention, this system uses predictive analytics and LLM-powered context engines to automatically decide, craft, and dispatch personalized recovery incentives.

---

### 🏗️ Target AI Infrastructure Architecture

```mermaid
graph TD
    subgraph Core Pipeline [Current Baseline]
        A[Razorpay Modal Exit] -->|Interception Event| B[Spring Boot API]
        B -->|Persist Metadata| C[(H2 / PostgreSQL DB)]
    end
    subgraph AI Strategy Engine [Future Phase]
        C -->|Event Stream| D[Behavioral Risk Evaluator]
        D -->|Customer Profile & Intent Score| E[LLM Strategy Router]
        
        subgraph Decision Matrix
            E -->|High Basket Value| F1[Dynamic Discount Generator]
            E -->|Price Hesitation| F2[Free Shipping Nudge]
            E -->|Technical Drop-off| F3[Alternative Payment Vector]
        end
        F1 & F2 & F3 -->|Prompt Context| G[Generative Copy Engine]
    end
    subgraph Autonomous Dispatch
        G -->|Personalized Offer + Direct Pay Link| H[Omnichannel Dispatcher]
        H -->|SMS / WhatsApp / Email| I[Customer]
    end
```

```mermaid
sequenceDiagram
    autonumber
    participant Gateway as Razorpay Interceptor
    participant Engine as RevRecover API
    participant AI as AI Strategy Engine (LLM)
    participant Channel as WhatsApp / SMS Gateway
    participant Customer as Customer
    Gateway->>Engine: Post Abandonment Event (Amount, Session Duration, Customer Info)
    Engine->>AI: Evaluate Event Data + Customer Purchase History
    
    rect rgb(240, 248, 255)
        Note over AI: 1. Predicts Drop-off Reason (e.g., Price Friction)<br/>2. Calculates Max Allowed Margin Discount<br/>3. Drafts Personalized Conversational Copy
    end
    AI-->>Engine: Returns Action Strategy & Customized Payload
    Engine->>Channel: Trigger Recovery Link with Single-Use Coupon Token
    Channel->>Customer: Delivers Personalized WhatsApp Message
    
    alt Customer Completes Payment
        Customer->>Gateway: Clicks One-Tap Recovery Link
        Gateway-->>Engine: Webhook: Payment Recovered
        Engine->>Engine: Update Dashboard Analytics (ROAS & Recovered Revenue)
    end
```

---

## 📜 License & Acknowledgements

* **License:** Distributed under the MIT License.
* **Hackathon:** Developed exclusively for the **Razorpay Buildathon**.