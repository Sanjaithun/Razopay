document.addEventListener('DOMContentLoaded', () => {
    const payBtn = document.getElementById('pay-btn');
    const statusDiv = document.getElementById('payment-status');

    if (!payBtn) return;

    payBtn.addEventListener('click', () => {
        const nameInput = document.getElementById('cust-name');
        const emailInput = document.getElementById('cust-email');

        const name = nameInput && nameInput.value.trim() ? nameInput.value.trim() : "John Doe";
        const email = emailInput && emailInput.value.trim() ? emailInput.value.trim() : "test@example.com";

        const options = {
            key: "rzp_test_TXeDK9C94CB415", // Update with your Razorpay Test Key
            amount: 499900, // Amount in paise (4999.00 INR)
            currency: "INR",
            name: "RevRecover Enterprise",
            description: "Enterprise Pro License",
            prefill: {
                name: name,
                email: email,
                contact: "9999999999"
            },
            theme: { color: "#4F46E5" },
            handler: function (response) {
                if (statusDiv) {
                    statusDiv.className = "text-center mt-3 text-sm text-green-600 font-semibold block";
                    statusDiv.innerText = `Payment Successful! ID: ${response.razorpay_payment_id}`;
                }
            },
            modal: {
                ondismiss: function () {
                    // Logs failure to backend when user closes modal
                    fetch("http://localhost:8080/api/payments/failure", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            customerName: name,
                            customerEmail: email,
                            amount: 4999.00,
                            failureReason: "Payment window closed by user"
                        })
                    })
                    .then(res => res.json())
                    .then(data => {
                        console.log("Failure successfully logged to backend:", data);
                        if (statusDiv) {
                            statusDiv.className = "text-center mt-3 text-sm text-amber-600 font-semibold block";
                            statusDiv.innerText = "Payment cancelled. Recorded in Dashboard.";
                        }
                    })
                    .catch(err => console.error("Failed to log failure event:", err));
                }
            }
        };

        const rzp = new Razorpay(options);
        rzp.open();
    });
});