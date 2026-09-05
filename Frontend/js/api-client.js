// js/api-client.js
const API_CONFIG = {
    USE_MOCK: false, // Toggle to false when connecting Spring Boot
    BASE_URL: 'http://localhost:8080/api'
};

const MOCK_METRICS = {
    totalFailedAmount: 145000,
    totalRecoveredAmount: 92000,
    recoveryRate: "63.4%",
    activeWorkflows: 18
};

const MOCK_FAILED_TRANSACTIONS = [
    {
        id: "pay_N8xK91aB",
        customerName: "Rahul Sharma",
        email: "rahul@example.com",
        amount: "₹4,999",
        errorCode: "BAD_REQUEST_PAYMENT_TIMED_OUT",
        status: "Action Required",
        suggestedStrategy: "Send 5% discount payment link via WhatsApp",
        createdAt: "2026-09-03 14:22"
    },
    {
        id: "pay_M3yL12cC",
        customerName: "Priya Patel",
        email: "priya@example.com",
        amount: "₹12,499",
        errorCode: "GATEWAY_ERROR_ISSUER_DOWN",
        status: "Auto-Retrying",
        suggestedStrategy: "Delayed retry trigger after bank clearance",
        createdAt: "2026-09-03 15:10"
    }
];

export const ApiClient = {
    async getDashboardMetrics() {
        if (API_CONFIG.USE_MOCK) return MOCK_METRICS;
        const res = await fetch(`${API_CONFIG.BASE_URL}/recovery/metrics`);
        return await res.json();
    },

    async getFailedTransactions() {
        if (API_CONFIG.USE_MOCK) return MOCK_FAILED_TRANSACTIONS;
        const res = await fetch(`${API_CONFIG.BASE_URL}/recovery/transactions`);
        return await res.json();
    },

    async triggerRecovery(transactionId) {
        if (API_CONFIG.USE_MOCK) {
            return { success: true, message: `Recovery initiated for ${transactionId}` };
        }
        const res = await fetch(`${API_CONFIG.BASE_URL}/recovery/execute`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ transactionId })
        });
        return await res.json();
    }
};