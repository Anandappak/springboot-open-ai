const summaryEls = {
  totalMembers: document.getElementById('totalMembers'),
  totalDonations: document.getElementById('totalDonations'),
  totalEvents: document.getElementById('totalEvents'),
  totalDonationAmount: document.getElementById('totalDonationAmount')
};

const membersTableBody = document.getElementById('membersTableBody');
const donationsTableBody = document.getElementById('donationsTableBody');
const eventsTableBody = document.getElementById('eventsTableBody');
const AUTH_KEY = 'temple-admin-auth';

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
  summaryEls.totalMembers.textContent = summary.totalMembers ?? 0;
  summaryEls.totalDonations.textContent = summary.totalDonations ?? 0;
  summaryEls.totalEvents.textContent = summary.totalEvents ?? 0;
  summaryEls.totalDonationAmount.textContent = formatCurrency(summary.totalDonationAmount ?? 0);
}

function renderMembers(items) {
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
  donationsTableBody.innerHTML = items.length
    ? items.map(d => `
        <tr>
          <td>${d.donorName}</td>
          <td>${formatCurrency(d.amount)}</td>
          <td>${d.purpose}</td>
          <td>${d.donationDate}</td>
        </tr>`).join('')
    : '<tr><td colspan="4">No donations yet.</td></tr>';
}

function renderEvents(items) {
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

async function submitForm(url, payload) {
  await fetchJson(url, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  await loadDashboard();
}

document.getElementById('refreshBtn').addEventListener('click', loadDashboard);

document.getElementById('adminLoginForm').addEventListener('submit', async (event) => {
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

document.getElementById('passwordChangeForm').addEventListener('submit', async (event) => {
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

document.getElementById('memberForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.target);
  await submitForm('/api/temple/members', Object.fromEntries(formData.entries()));
  event.target.reset();
});

document.getElementById('donationForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.target);
  const payload = Object.fromEntries(formData.entries());
  payload.amount = Number(payload.amount);
  await submitForm('/api/temple/donations', payload);
  event.target.reset();
});

document.getElementById('eventForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.target);
  await submitForm('/api/temple/events', Object.fromEntries(formData.entries()));
  event.target.reset();
});

if (!getStoredAuth()) {
  alert('Please log in with the admin account to access the temple dashboard.');
}

loadDashboard();
