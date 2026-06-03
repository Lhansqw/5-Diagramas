/* ═══════════════════════════════════════════════════
   GestionLabs · app.js
   Lógica de la aplicación: Auth, Laboratorios, Reservas
═══════════════════════════════════════════════════ */

// ─── Estado global ──────────────────────────────────
let currentUser  = null;
let selectedLab  = null;
const API = '/api';

// ─── Referencias del DOM ────────────────────────────
const $ = id => document.getElementById(id);

const loginView       = $('login-view');
const dashboardView   = $('dashboard-view');
const loginForm       = $('login-form');
const loginBtn        = $('login-btn');
const logoutBtn       = $('logout-btn');
const navAvatar       = $('nav-avatar');
const navUserInfo     = $('nav-user-info');
const labsList        = $('labs-list');
const misReservas     = $('mis-reservas-list');
const noLabSelected   = $('no-lab-selected');
const labHeader       = $('lab-header');
const labTitle        = $('lab-title');
const labEquiposCount = $('lab-equipos-count');
const equiposSection  = $('equipos-section');
const equiposGrid     = $('equipos-grid');
const reservaSection  = $('reserva-section');
const reservaForm     = $('reserva-form');
const reservaBtn      = $('reserva-btn');
const selectedLabId   = $('selected-lab-id');
const toast           = $('toast');
const toastMsg        = $('toast-msg');
const toastIcon       = $('toast-icon');

// ─── Toast ──────────────────────────────────────────
let toastTimer;
function showToast(msg, type = 'ok') {
  const isOk = type === 'ok';

  toast.className = `toast ${isOk ? 'toast-ok' : 'toast-error'}`;
  toastMsg.textContent = msg;
  toastIcon.innerHTML = isOk
    ? '<path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>'
    : '<path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>';

  toast.classList.add('show');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove('show'), 3500);
}

// ─── Spinner helpers ────────────────────────────────
const SPIN_SVG = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
  style="animation:spin .8s linear infinite;display:inline-block;vertical-align:middle;margin-right:.4rem;">
  <circle cx="12" cy="12" r="10" stroke-opacity=".25"/>
  <path d="M12 2a10 10 0 0110 10" stroke-linecap="round"/>
</svg>`;

// Inject spin keyframe once
const styleEl = document.createElement('style');
styleEl.textContent = '@keyframes spin{from{transform:rotate(0deg)}to{transform:rotate(360deg)}}';
document.head.appendChild(styleEl);

function setLoading(btn, loading, restoreHTML) {
  if (loading) {
    btn._orig = btn.innerHTML;
    btn.innerHTML = `${SPIN_SVG} Cargando…`;
    btn.disabled = true;
    btn.style.opacity = '.7';
  } else {
    btn.innerHTML = restoreHTML ?? btn._orig;
    btn.disabled = false;
    btn.style.opacity = '1';
  }
}

// ─── Auth: Login ────────────────────────────────────
loginForm.addEventListener('submit', async e => {
  e.preventDefault();
  setLoading(loginBtn, true);

  try {
    const res = await fetch(`${API}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        correo: $('email').value,
        contraseña: $('password').value,
      }),
    });

    if (!res.ok) throw new Error();
    currentUser = await res.json();
    initDashboard();
  } catch {
    showToast('Credenciales incorrectas', 'error');
    setLoading(loginBtn, false);
  }
});

// ─── Auth: Logout ────────────────────────────────────
logoutBtn.addEventListener('click', () => {
  currentUser = null;
  selectedLab = null;
  loginView.style.display  = 'flex';
  dashboardView.style.display = 'none';
  setLoading(loginBtn, false);
});

// ─── Dashboard init ──────────────────────────────────
function initDashboard() {
  loginView.style.display     = 'none';
  dashboardView.style.display = 'flex';

  // Avatar & name
  const initial = (currentUser.nombre || 'U')[0].toUpperCase();
  navAvatar.textContent = initial;
  const [nameLine, rolLine] = navUserInfo.querySelectorAll('p');
  nameLine.textContent = currentUser.nombre;
  rolLine.textContent  = currentUser.rol;
  navUserInfo.style.display = 'block';

  loadLabs();
  loadReservas();
}

// ─── Laboratorios ────────────────────────────────────
async function loadLabs() {
  labsList.innerHTML = renderSkeleton();
  try {
    const res  = await fetch(`${API}/laboratorios`);
    const labs = await res.json();
    renderLabsList(labs);
  } catch {
    labsList.innerHTML = `<p style="font-size:.82rem;color:#ef4444;text-align:center;padding:1rem 0;">Error al cargar laboratorios.</p>`;
  }
}

function renderSkeleton() {
  return Array(3).fill(0).map(() => `
    <div style="background:rgba(255,255,255,.03);border:1px solid rgba(255,255,255,.05);border-radius:14px;padding:1rem;animation:pulse 1.5s infinite;">
      <div style="height:.8rem;background:rgba(255,255,255,.08);border-radius:6px;width:65%;margin-bottom:.5rem;"></div>
      <div style="height:.6rem;background:rgba(255,255,255,.05);border-radius:6px;width:40%;"></div>
    </div>
  `).join('');
}

function renderLabsList(labs) {
  if (!labs.length) {
    labsList.innerHTML = `<p style="font-size:.82rem;color:#475569;text-align:center;padding:1rem 0;font-style:italic;">Sin laboratorios disponibles.</p>`;
    return;
  }
  labsList.innerHTML = '';
  labs.forEach(lab => {
    const el = document.createElement('div');
    el.className = 'lab-card';
    el.dataset.id = lab.id;

    const disponibles = lab.equipos.filter(e => e.estado === 'DISPONIBLE').length;
    el.innerHTML = `
      <div style="font-weight:700;font-size:.92rem;color:#f1f5f9;margin-bottom:.3rem;">${lab.nombre}</div>
      <div style="display:flex;align-items:center;gap:.4rem;font-size:.78rem;color:#64748b;">
        <svg width="12" height="12" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
        </svg>
        ${lab.equipos.length} equipos · <span style="color:#34d399;">${disponibles} disponibles</span>
      </div>
    `;

    el.addEventListener('click', () => {
      document.querySelectorAll('.lab-card').forEach(c => c.classList.remove('active'));
      el.classList.add('active');
      selectLab(lab);
    });

    labsList.appendChild(el);
  });
}

// ─── Seleccionar laboratorio ─────────────────────────
function selectLab(lab) {
  selectedLab = lab;

  // Show sections
  noLabSelected.style.display   = 'none';
  labHeader.style.display       = 'block';
  equiposSection.style.display  = 'block';
  reservaSection.style.display  = 'block';

  // Lab info
  labTitle.textContent = lab.nombre;
  labEquiposCount.textContent = lab.equipos.length;
  selectedLabId.value = lab.id;

  // Default date = today
  const today = new Date().toISOString().split('T')[0];
  $('fecha-turno').min   = today;
  $('fecha-turno').value = today;

  // Render equipos
  renderEquipos(lab.equipos);
}

function renderEquipos(equipos) {
  if (!equipos.length) {
    equiposGrid.innerHTML = `<p style="font-size:.82rem;color:#475569;font-style:italic;">Sin equipos registrados.</p>`;
    return;
  }
  equiposGrid.innerHTML = '';
  equipos.forEach(eq => {
    const dispo   = eq.estado === 'DISPONIBLE';
    const badgeCls = dispo ? 'badge-green' : 'badge-yellow';
    const icon     = dispo
      ? '<path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/>'
      : '<path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>';

    const card = document.createElement('div');
    card.className = 'eq-card';
    card.innerHTML = `
      <div style="display:flex;align-items:center;gap:.7rem;margin-bottom:.6rem;">
        <div style="width:36px;height:36px;background:rgba(99,102,241,.1);border-radius:10px;display:flex;align-items:center;justify-content:center;flex-shrink:0;">
          <svg width="16" height="16" fill="none" stroke="#818cf8" stroke-width="1.8" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
          </svg>
        </div>
        <div style="flex:1;min-width:0;">
          <p style="font-size:.85rem;font-weight:700;color:#f1f5f9;margin:0;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${eq.nombre}</p>
          <p style="font-size:.7rem;color:#334155;margin:0;font-family:monospace;">${eq.id}</p>
        </div>
      </div>
      <span class="badge ${badgeCls}">
        <svg width="10" height="10" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">${icon}</svg>
        ${eq.estado}
      </span>
    `;
    equiposGrid.appendChild(card);
  });
}

// ─── Reservas: Crear ─────────────────────────────────
reservaForm.addEventListener('submit', async e => {
  e.preventDefault();
  setLoading(reservaBtn, true);

  const body = {
    usuarioId:     currentUser.id,
    laboratorioId: selectedLabId.value,
    franjaHoraria: {
      fecha:      $('fecha-turno').value,
      horaInicio: $('hora-inicio').value,
      horaFin:    $('hora-fin').value,
    },
  };

  try {
    const res = await fetch(`${API}/reservas`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(body),
    });

    if (!res.ok) throw new Error();
    showToast('¡Turno reservado con éxito!');
    $('hora-inicio').value = '';
    $('hora-fin').value    = '';
    loadReservas();
  } catch {
    showToast('Error al crear la reserva', 'error');
  } finally {
    setLoading(reservaBtn, false);
  }
});

// ─── Reservas: Listar ────────────────────────────────
async function loadReservas() {
  if (!currentUser) return;
  misReservas.innerHTML = renderSkeleton();

  try {
    const res      = await fetch(`${API}/reservas/usuario/${currentUser.id}`);
    const reservas = await res.json();
    renderReservas(reservas);
  } catch {
    misReservas.innerHTML = `<p style="font-size:.8rem;color:#ef4444;text-align:center;padding:1rem 0;">Error al cargar reservas.</p>`;
  }
}

function renderReservas(reservas) {
  if (!reservas.length) {
    misReservas.innerHTML = `<p style="font-size:.8rem;color:#475569;text-align:center;padding:1rem 0;font-style:italic;">No tienes reservas activas.</p>`;
    return;
  }

  misReservas.innerHTML = '';
  [...reservas].reverse().forEach(r => {
    const badgeCls = r.estado === 'PENDIENTE' ? 'badge-indigo' : r.estado === 'CONFIRMADA' ? 'badge-green' : 'badge-yellow';
    const card = document.createElement('div');
    card.className = 'res-card fade-up';
    card.innerHTML = `
      <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:.4rem;">
        <span style="font-size:.82rem;font-weight:700;color:#f1f5f9;">${r.franjaHoraria?.fecha ?? '—'}</span>
        <span class="badge ${badgeCls}" style="font-size:.65rem;">${r.estado}</span>
      </div>
      <div style="font-size:.78rem;color:#64748b;display:flex;align-items:center;gap:.35rem;">
        <svg width="11" height="11" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        ${r.franjaHoraria?.horaInicio ?? '—'} — ${r.franjaHoraria?.horaFin ?? '—'}
      </div>
      <div style="font-size:.72rem;color:#334155;margin-top:.25rem;font-family:monospace;">
        Lab: ${r.laboratorioId?.substring(0,8)}…
      </div>
    `;
    misReservas.appendChild(card);
  });
}
