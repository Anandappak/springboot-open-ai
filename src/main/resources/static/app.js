const pageType = document.body.dataset.page || 'donor';
const AUTH_KEY = 'temple-admin-auth';

function escapeHtml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

const summaryEls = {
  totalMembers: document.getElementById('totalMembers'),
  totalDonations: document.getElementById('totalDonations'),
  totalEvents: document.getElementById('totalEvents'),
  totalDonationAmount: document.getElementById('totalDonationAmount')
};

const membersTableBody = document.getElementById('membersTableBody');
const donationsTableBody = document.getElementById('donationsTableBody');
const eventsTableBody = document.getElementById('eventsTableBody');

function getStoredAuth() {
  return localStorage.getItem(AUTH_KEY) || '';
}

function setStoredAuth(value) {
  if (value) {
    localStorage.setItem(AUTH_KEY, value);
  } else {
    localStorage.removeItem(AUTH_KEY);
  }
}

function buildAuthHeader(username, password) {
  return 'Basic ' + btoa(`${username}:${password}`);
}

async function fetchJson(url, options = {}) {
  const storedAuth = getStoredAuth();
  const headers = { 'Content-Type': 'application/json', ...(storedAuth ? { Authorization: storedAuth } : {}) };

  const response = await fetch(url, {
    ...options,
    headers: {
      ...headers,
      ...(options.headers || {})
    }
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || 'Request failed');
  }

  return response.json();
}

function formatCurrency(value) {
  return Number(value || 0).toLocaleString('en-US', {
    style: 'currency',
    currency: 'USD'
  });
}

function renderSummary(summary) {
  if (!summaryEls.totalMembers) return;
  summaryEls.totalMembers.textContent = summary.totalMembers ?? 0;
  summaryEls.totalDonations.textContent = summary.totalDonations ?? 0;
  summaryEls.totalEvents.textContent = summary.totalEvents ?? 0;
  summaryEls.totalDonationAmount.textContent = formatCurrency(summary.totalDonationAmount ?? 0);
}

function renderMembers(items) {
  if (!membersTableBody) return;
  membersTableBody.innerHTML = items.length
    ? items.map(member => `
        <tr>
          <td>${member.name}</td>
          <td>${member.role}</td>
          <td>${member.phoneNumber}</td>
          <td>${member.address}</td>
        </tr>`).join('')
    : '<tr><td colspan="4">No members yet.</td></tr>';
}

function renderDonations(items) {
  if (!donationsTableBody) return;
  donationsTableBody.innerHTML = items.length
    ? items.map(d => {
        const paymentMethod = d.paymentMethod || 'UPI';
        const reference = d.transactionReference || d.upiId || d.accountNumber || 'N/A';
        return `
          <tr>
            <td>${d.donorName}</td>
            <td>${formatCurrency(d.amount)}</td>
            <td>${d.purpose}</td>
            <td>${paymentMethod}</td>
            <td>${reference}</td>
            <td>${d.donationDate}</td>
          </tr>`;
      }).join('')
    : '<tr><td colspan="6">No donations yet.</td></tr>';
}

function renderEvents(items) {
  if (!eventsTableBody) return;
  eventsTableBody.innerHTML = items.length
    ? items.map(event => `
        <tr>
          <td>${event.name}</td>
          <td>${event.eventDate}</td>
          <td>${event.status}</td>
        </tr>`).join('')
    : '<tr><td colspan="3">No events yet.</td></tr>';
}

async function loadDashboard() {
  const storedAuth = getStoredAuth();

  if (!storedAuth) {
    renderSummary({
      totalMembers: 0,
      totalDonations: 0,
      totalEvents: 0,
      totalDonationAmount: 0
    });
    renderMembers([]);
    renderDonations([]);
    renderEvents([]);
    return;
  }

  try {
    const [summary, members, donations, events] = await Promise.all([
      fetchJson('/api/temple/dashboard'),
      fetchJson('/api/temple/members'),
      fetchJson('/api/temple/donations'),
      fetchJson('/api/temple/events')
    ]);

    renderSummary(summary);
    renderMembers(members);
    renderDonations(donations);
    renderEvents(events);
  } catch (error) {
    console.error(error);
    alert('Unable to load temple data. Please check the backend.');
  }
}

function updatePaymentMethodFields() {
  const paymentMethodSelect = document.getElementById('paymentMethod');
  if (!paymentMethodSelect) return;

  const paymentGroups = {
    UPI: document.querySelector('[data-payment-group="upi"]'),
    QR_CODE: document.querySelector('[data-payment-group="qr"]'),
    BANK_TRANSFER: document.querySelector('[data-payment-group="bank"]')
  };

  const selectedMethod = paymentMethodSelect.value;

  Object.entries(paymentGroups).forEach(([method, group]) => {
    if (group) {
      group.style.display = method === selectedMethod ? 'flex' : 'none';
    }
  });
}

async function submitForm(url, payload) {
  await fetchJson(url, {
    method: 'POST',
    body: JSON.stringify(payload)
  });

  if (pageType === 'admin') {
    await loadDashboard();
  }
}

async function handleAssistantQuery(event) {
  event.preventDefault();

  const assistantInput = document.getElementById('assistantInput');
  const assistantStatus = document.getElementById('assistantStatus');
  const assistantAnswer = document.getElementById('assistantAnswer');
  const assistantResults = document.getElementById('assistantResults');

  if (!assistantInput || !assistantStatus || !assistantAnswer || !assistantResults) {
    return;
  }

  const query = assistantInput.value.trim();
  if (!query) {
    return;
  }

  assistantStatus.textContent = 'Searching temple knowledge...';
  assistantAnswer.textContent = 'Thinking...';
  assistantResults.innerHTML = '';

  try {
    const response = await fetchJson('/api/ask', {
      method: 'POST',
      body: JSON.stringify({ query, maxResults: 5 })
    });

    const answer = response.answer || 'No answer available.';
    const results = Array.isArray(response.results) ? response.results : [];

    assistantStatus.textContent = `Results for: "${query}"`;
    assistantAnswer.textContent = answer;

    if (!results.length) {
      assistantResults.innerHTML = '<div class="assistant-empty">No supporting sources were found for this question.</div>';
      return;
    }

    assistantResults.innerHTML = results.map((item, index) => `
      <article class="assistant-result">
        <h4>${index + 1}. ${escapeHtml(item.title || 'Source')}</h4>
        <p>${escapeHtml(item.content || '')}</p>
      </article>
    `).join('');
  } catch (error) {
    assistantStatus.textContent = 'Unable to reach the assistant.';
    assistantAnswer.textContent = error.message || 'Something went wrong while contacting the AI assistant.';
  }
}

const assistantForm = document.getElementById('assistantForm');
if (assistantForm) {
  assistantForm.addEventListener('submit', handleAssistantQuery);
}

if (pageType === 'admin') {
  const refreshBtn = document.getElementById('refreshBtn');
  if (refreshBtn) {
    refreshBtn.addEventListener('click', loadDashboard);
  }

  const adminLoginForm = document.getElementById('adminLoginForm');
  if (adminLoginForm) {
    adminLoginForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const formData = new FormData(event.target);
      const username = formData.get('username');
      const password = formData.get('password');

      try {
        const result = await fetch('/api/admin/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username, password })
        });

        if (!result.ok) {
          throw new Error('Invalid admin credentials');
        }

        setStoredAuth(buildAuthHeader(username, password));
        await loadDashboard();
        alert('Admin login successful.');
      } catch (error) {
        alert(error.message || 'Login failed.');
      }
    });
  }

  const passwordChangeForm = document.getElementById('passwordChangeForm');
  if (passwordChangeForm) {
    passwordChangeForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const formData = new FormData(event.target);
      const currentPassword = formData.get('currentPassword');
      const newPassword = formData.get('newPassword');

      try {
        const username = 'admin';
        const response = await fetch('/api/admin/change-password', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: buildAuthHeader(username, currentPassword)
          },
          body: JSON.stringify({ currentPassword, newPassword })
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || 'Password change failed');
        }

        setStoredAuth(buildAuthHeader(username, newPassword));
        event.target.reset();
        alert('Admin password updated successfully.');
      } catch (error) {
        alert(error.message || 'Password change failed.');
      }
    });
  }

  const memberForm = document.getElementById('memberForm');
  if (memberForm) {
    memberForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const formData = new FormData(event.target);
      await submitForm('/api/temple/members', Object.fromEntries(formData.entries()));
      event.target.reset();
    });
  }

  const eventForm = document.getElementById('eventForm');
  if (eventForm) {
    eventForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const formData = new FormData(event.target);
      await submitForm('/api/temple/events', Object.fromEntries(formData.entries()));
      event.target.reset();
    });
  }

  if (!getStoredAuth()) {
    alert('Please log in with the admin account to access the temple dashboard.');
  }

  loadDashboard();
} else {
  const donationForm = document.getElementById('donationForm');
  if (donationForm) {
    const paymentMethodSelect = document.getElementById('paymentMethod');
    if (paymentMethodSelect) {
      paymentMethodSelect.addEventListener('change', updatePaymentMethodFields);
      updatePaymentMethodFields();
    }

    donationForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const formData = new FormData(event.target);
      const payload = Object.fromEntries(formData.entries());
      payload.amount = Number(payload.amount);
      payload.paymentMethod = payload.paymentMethod || 'UPI';
      payload.upiId = payload.upiId || 'templedonation@upi';
      payload.bankName = payload.bankName || 'State Bank of India';
      payload.accountHolderName = payload.accountHolderName || 'Village Temple Trust';
      payload.accountNumber = payload.accountNumber || '123456789012';
      payload.ifscCode = payload.ifscCode || 'SBIN0001234';
      payload.qrCodeLabel = payload.qrCodeLabel || 'Temple Donation QR';
      payload.paymentStatus = 'PAID';
      await submitForm('/api/public/donations', payload);
      event.target.reset();
      updatePaymentMethodFields();
      alert('Thank you for your temple donation.');
    });
  }
}
