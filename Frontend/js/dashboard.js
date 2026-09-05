document.addEventListener('DOMContentLoaded', () => {
    fetchFailureData();
    // Poll backend every 4 seconds to reflect live failures
    setInterval(fetchFailureData, 4000);

    // Event delegation for dynamically generated "Trigger Nudge" buttons
    const tableBody = document.getElementById('realtime-failure-feed');
    if (tableBody) {
        tableBody.addEventListener('click', (event) => {
            const btn = event.target.closest('.trigger-nudge-btn');
            if (btn && !btn.disabled) {
                handleTriggerNudge(btn);
            }
        });
    }
});

function fetchFailureData() {
    fetch("http://localhost:8080/api/payments/failures")
        .then(res => res.json())
        .then(failures => {
            updateMetrics(failures);
            renderFailureTable(failures);
        })
        .catch(err => console.error("Error fetching failure diagnostic queue:", err));
}

function updateMetrics(failures) {
    if (!Array.isArray(failures)) return;

    const totalFailed = failures.reduce((sum, item) => sum + (item.amount || 0), 0);
    const failureCount = failures.length;

    const metricFailed = document.getElementById('metric-failed');
    const failureCountEl = document.getElementById('failure-count');
    const metricWorkflows = document.getElementById('metric-workflows');

    if (metricFailed) metricFailed.innerText = `₹${totalFailed.toLocaleString('en-IN')}`;
    if (failureCountEl) failureCountEl.innerText = failureCount;
    if (metricWorkflows) metricWorkflows.innerText = failureCount;
}

function renderFailureTable(failures) {
    const tableBody = document.getElementById('realtime-failure-feed');
    if (!tableBody) return;

    if (!Array.isArray(failures) || failures.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="p-5 text-center text-slate-500">
                    No payment failures recorded yet. Click 'Pay' and exit the popup on checkout.
                </td>
            </tr>`;
        return;
    }

    tableBody.innerHTML = failures.map(f => `
        <tr class="border-b border-slate-700/50 hover:bg-slate-800/50 transition-colors">
            <td class="p-5 font-mono text-xs text-indigo-400">#REV-${f.id || '8080'}</td>
            <td class="p-5">
                <div class="font-medium text-white">${escapeHtml(f.customerName || 'Guest User')}</div>
                <div class="text-xs text-slate-400">${escapeHtml(f.customerEmail || 'guest@example.com')}</div>
            </td>
            <td class="p-5 font-bold text-white">₹${(f.amount || 0).toLocaleString('en-IN')}</td>
            <td class="p-5">
                <span class="px-2 py-1 bg-red-500/10 text-red-400 text-xs font-semibold rounded-md border border-red-500/20">
                    ${escapeHtml(f.failureReason || 'User Abandoned')}
                </span>
            </td>
            <td class="p-5 text-xs text-slate-300">
                ${escapeHtml(f.aiStrategy || 'AI Nudge: Send discounted retry link via WhatsApp/Email')}
            </td>
            <td class="p-5 text-right">
                <button 
                    data-id="${f.id}" 
                    class="trigger-nudge-btn px-3 py-1.5 bg-brand-accent hover:bg-brand-accentHover text-white text-xs font-semibold rounded-lg transition-all active:scale-95"
                >
                    Trigger Nudge
                </button>
            </td>
        </tr>
    `).join('');
}

function handleTriggerNudge(button) {
    const failureId = button.getAttribute('data-id');

    // UI feedback state
    button.disabled = true;
    button.innerText = "Nudge Sent ✓";
    button.className = "px-3 py-1.5 bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-semibold rounded-lg cursor-default transition-all";

    console.log(`Automated recovery nudge dispatched for failure ID: ${failureId}`);
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}