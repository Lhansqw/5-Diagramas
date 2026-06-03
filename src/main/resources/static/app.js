// ─── CONFIG ────────────────────────────────────────────────────────────────
const API = '/api/tableros';
let tableroActual = null;
let draggedTaskId = null;
let draggedFromColId = null;

// ─── UTILIDADES ─────────────────────────────────────────────────────────────
function toast(msg, tipo = 'ok') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = `fixed bottom-6 right-6 z-50 px-4 py-3 rounded-xl border text-sm font-medium shadow-lg fade-in ${
    tipo === 'ok'
      ? 'bg-green/10 border-green/30 text-green-400'
      : 'bg-coral/10 border-coral/30 text-[#f97066]'
  }`;
  el.classList.remove('hidden');
  setTimeout(() => el.classList.add('hidden'), 3000);
}

function setStatus(conectado) {
  const dot  = document.getElementById('status-dot');
  const text = document.getElementById('status-text');
  dot.className  = `w-2 h-2 rounded-full ${conectado ? 'bg-green-400 animate-pulse' : 'bg-red-500'}`;
  text.textContent = conectado ? 'Conectado a MongoDB' : 'Sin conexión';
}

async function apiFetch(url, opts = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...opts
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error || `HTTP ${res.status}`);
  }
  return res.json();
}

// ─── RENDER ──────────────────────────────────────────────────────────────────
function prioridadBadge(p) {
  const map = { ALTA: 'badge-ALTA', MEDIA: 'badge-MEDIA', BAJA: 'badge-BAJA' };
  return `<span class="text-[10px] font-mono font-medium px-2 py-0.5 rounded-full ${map[p] || 'badge-MEDIA'}">${p}</span>`;
}

function renderTarjeta(tarea, columnaId) {
  const etiquetas = (tarea.etiquetas || [])
    .map(e => `<span class="text-[10px] px-2 py-0.5 rounded-full bg-white/5 border border-white/10 text-white/50 font-mono">${e}</span>`)
    .join('');

  const fecha = tarea.fechaVencimiento
    ? `<span class="text-[10px] font-mono text-white/30">📅 ${tarea.fechaVencimiento}</span>`
    : '';

  const asignado = tarea.asignadoA
    ? `<span class="text-[10px] font-mono text-white/40">👤 ${tarea.asignadoA}</span>`
    : '';

  return `
    <div class="task-card bg-bg3 border border-white/[0.07] rounded-xl p-3.5 mb-3 select-none
                hover:border-accent/30 hover:shadow-lg hover:shadow-black/30 group"
         draggable="true"
         data-task-id="${tarea.id}"
         data-col-id="${columnaId}">
      <div class="flex items-start justify-between gap-2 mb-2">
        <p class="text-sm font-medium leading-snug flex-1">${tarea.titulo}</p>
        ${prioridadBadge(tarea.prioridad || 'MEDIA')}
      </div>
      ${tarea.descripcion ? `<p class="text-xs text-white/40 mb-2 leading-relaxed">${tarea.descripcion}</p>` : ''}
      ${etiquetas ? `<div class="flex flex-wrap gap-1 mb-2">${etiquetas}</div>` : ''}
      <div class="flex items-center justify-between mt-1">
        <div class="flex gap-3">${fecha}${asignado}</div>
        <button class="btn-eliminar-tarea opacity-0 group-hover:opacity-100 transition text-white/20 hover:text-[#f97066] text-xs"
                data-task-id="${tarea.id}" title="Eliminar tarea">✕</button>
      </div>
    </div>`;
}

const COLUMN_COLORS = ['#7c6fff', '#60a5fa', '#2dd4bf', '#4ade80', '#fbbf24', '#f472b6'];

function renderTablero(tablero) {
  tableroActual = tablero;
  const board = document.getElementById('board');
  const emptyEl = document.getElementById('board-empty');
  if (emptyEl) emptyEl.style.display = 'none';

  board.innerHTML = tablero.columnas
    .sort((a, b) => a.orden - b.orden)
    .map((col, i) => {
      const color = COLUMN_COLORS[i % COLUMN_COLORS.length];
      const wip = col.limiteWIP
        ? `<span class="text-[10px] font-mono px-1.5 py-0.5 rounded ${col.tareas.length >= col.limiteWIP ? 'bg-[#f97066]/15 text-[#f97066] border border-[#f97066]/30' : 'bg-white/5 text-white/30 border border-white/10'}">
            ${col.tareas.length}/${col.limiteWIP}
           </span>`
        : `<span class="text-[10px] font-mono text-white/30">${col.tareas.length}</span>`;

      const tarjetas = col.tareas.map(t => renderTarjeta(t, col.id)).join('');

      return `
        <div class="kanban-col flex flex-col min-w-[280px] max-w-[280px]"
             data-col-id="${col.id}">
          <!-- Header columna -->
          <div class="flex items-center justify-between mb-4 px-1">
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full flex-shrink-0" style="background:${color}"></span>
              <h2 class="text-sm font-semibold">${col.nombre}</h2>
            </div>
            ${wip}
          </div>

          <!-- Drop zone -->
          <div class="drop-zone flex-1 rounded-2xl border border-dashed border-white/[0.07] bg-bg2/50 p-3 transition-all min-h-[100px]"
               data-col-id="${col.id}">
            ${tarjetas}
          </div>

          <!-- Botón añadir tarea -->
          <button class="btn-add-task mt-3 w-full py-2 rounded-xl border border-dashed border-white/[0.07]
                         text-xs text-white/30 hover:text-white/60 hover:border-accent/30 hover:bg-accent/5
                         transition-all font-mono"
                  data-col-id="${col.id}">
            + añadir tarea
          </button>
        </div>`;
    })
    .join('');

  bindBoardEvents();
}

// ─── EVENTOS DEL BOARD ──────────────────────────────────────────────────────
function bindBoardEvents() {
  // ── Drag & Drop ──────────────────────────────────────────────────────────
  document.querySelectorAll('.task-card').forEach(card => {
    card.addEventListener('dragstart', e => {
      draggedTaskId    = card.dataset.taskId;
      draggedFromColId = card.dataset.colId;
      // Guardamos en dataTransfer también (más fiable entre navegadores)
      e.dataTransfer.setData('taskId', draggedTaskId);
      e.dataTransfer.setData('fromColId', draggedFromColId);
      e.dataTransfer.effectAllowed = 'move';
      // Pequeño delay para que el ghost aparezca antes de oscurecer la tarjeta
      setTimeout(() => card.classList.add('dragging'), 0);
    });

    card.addEventListener('dragend', () => {
      card.classList.remove('dragging');
      // Limpiamos cualquier drop-zone que haya quedado marcada
      document.querySelectorAll('.drop-zone').forEach(z => z.classList.remove('drag-over'));
    });
  });

  document.querySelectorAll('.drop-zone').forEach(zone => {
    // Contador para ignorar dragleave causados por entrar en elementos hijos
    let dragCounter = 0;

    zone.addEventListener('dragenter', e => {
      e.preventDefault();
      dragCounter++;
      zone.classList.add('drag-over');
    });

    zone.addEventListener('dragover', e => {
      e.preventDefault();
      e.dataTransfer.dropEffect = 'move';
    });

    zone.addEventListener('dragleave', () => {
      dragCounter--;
      if (dragCounter <= 0) {
        dragCounter = 0;
        zone.classList.remove('drag-over');
      }
    });

    zone.addEventListener('drop', async e => {
      e.preventDefault();
      dragCounter = 0;
      zone.classList.remove('drag-over');

      // Leemos de dataTransfer (más fiable que variables globales en algunos browsers)
      const taskId   = e.dataTransfer.getData('taskId') || draggedTaskId;
      const fromCol  = e.dataTransfer.getData('fromColId') || draggedFromColId;
      const destColId = zone.dataset.colId;

      if (!taskId || destColId === fromCol) return;
      await moverTarea(taskId, destColId);
    });
  });

  // ── Botones "añadir tarea" ────────────────────────────────────────────────
  document.querySelectorAll('.btn-add-task').forEach(btn => {
    btn.addEventListener('click', () => abrirModalTarea(btn.dataset.colId));
  });

  // ── Botones eliminar ─────────────────────────────────────────────────────
  document.querySelectorAll('.btn-eliminar-tarea').forEach(btn => {
    btn.addEventListener('click', async e => {
      e.stopPropagation();
      if (!confirm('¿Eliminar esta tarea?')) return;
      await eliminarTarea(btn.dataset.taskId);
    });
  });
}


// ─── API CALLS ───────────────────────────────────────────────────────────────
async function cargarTableros() {
  try {
    const tableros = await apiFetch(API);
    setStatus(true);
    const sel = document.getElementById('selector-tablero');
    sel.innerHTML = '<option value="">— seleccionar —</option>' +
      tableros.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('');
    if (tableros.length > 0) {
      sel.value = tableros[0].id;
      await cargarTablero(tableros[0].id);
    }
  } catch (err) {
    setStatus(false);
    toast('No se pudo conectar con el servidor: ' + err.message, 'err');
  }
}

async function cargarTablero(id) {
  try {
    const t = await apiFetch(`${API}/${id}`);
    document.getElementById('board-desc').textContent = t.descripcion || '';
    renderTablero(t);
  } catch (err) {
    toast('Error al cargar el tablero: ' + err.message, 'err');
  }
}

async function moverTarea(tareaId, colDestinoId) {
  try {
    const t = await apiFetch(`${API}/${tableroActual.id}/tareas/${tareaId}/mover`, {
      method: 'PUT',
      body: JSON.stringify({ columnaDestinoId: colDestinoId })
    });
    renderTablero(t);
    toast('Tarea movida ✓');
  } catch (err) {
    toast(err.message, 'err');
    await cargarTablero(tableroActual.id);
  }
}

async function eliminarTarea(tareaId) {
  try {
    const t = await apiFetch(`${API}/${tableroActual.id}/tareas/${tareaId}`, { method: 'DELETE' });
    renderTablero(t);
    toast('Tarea eliminada ✓');
  } catch (err) {
    toast(err.message, 'err');
  }
}

// ─── MODAL TAREA ─────────────────────────────────────────────────────────────
function abrirModalTarea(columnaId) {
  document.getElementById('form-columna-id').value = columnaId;
  document.getElementById('form-tablero-id').value = tableroActual.id;
  document.getElementById('form-titulo').value = '';
  document.getElementById('form-descripcion').value = '';
  document.getElementById('form-prioridad').value = 'MEDIA';
  document.getElementById('form-fecha').value = '';
  document.getElementById('form-etiquetas').value = '';
  document.getElementById('form-asignado').value = '';
  document.getElementById('modal-tarea').classList.remove('hidden');
  document.getElementById('form-titulo').focus();
}

document.getElementById('btn-cancelar-tarea').addEventListener('click', () =>
  document.getElementById('modal-tarea').classList.add('hidden'));

document.getElementById('btn-guardar-tarea').addEventListener('click', async () => {
  const titulo = document.getElementById('form-titulo').value.trim();
  if (!titulo) { toast('El título es obligatorio', 'err'); return; }

  const tableroId = document.getElementById('form-tablero-id').value;
  const columnaId = document.getElementById('form-columna-id').value;
  const etiquetasStr = document.getElementById('form-etiquetas').value.trim();

  const dto = {
    titulo,
    descripcion: document.getElementById('form-descripcion').value.trim(),
    prioridad:   document.getElementById('form-prioridad').value,
    fechaVencimiento: document.getElementById('form-fecha').value || null,
    asignadoA:   document.getElementById('form-asignado').value.trim() || null,
    etiquetas:   etiquetasStr ? etiquetasStr.split(',').map(s => s.trim()) : []
  };

  try {
    await apiFetch(`${API}/${tableroId}/columnas/${columnaId}/tareas`, {
      method: 'POST', body: JSON.stringify(dto)
    });
    document.getElementById('modal-tarea').classList.add('hidden');
    await cargarTablero(tableroId);
    toast('Tarea creada ✓');
  } catch (err) {
    toast(err.message, 'err');
  }
});

// ─── MODAL TABLERO ────────────────────────────────────────────────────────────
document.getElementById('btn-nuevo-tablero').addEventListener('click', () =>
  document.getElementById('modal-tablero').classList.remove('hidden'));

document.getElementById('btn-cancelar-tablero').addEventListener('click', () =>
  document.getElementById('modal-tablero').classList.add('hidden'));

document.getElementById('btn-guardar-tablero').addEventListener('click', async () => {
  const nombre = document.getElementById('new-board-nombre').value.trim();
  if (!nombre) { toast('El nombre del tablero es obligatorio', 'err'); return; }

  try {
    const t = await apiFetch(API, {
      method: 'POST',
      body: JSON.stringify({
        nombre,
        descripcion: document.getElementById('new-board-desc').value.trim(),
        propietarioId: 'estudiante-01'
      })
    });
    document.getElementById('modal-tablero').classList.add('hidden');
    await cargarTableros();
    document.getElementById('selector-tablero').value = t.id;
    await cargarTablero(t.id);
    toast('Tablero creado ✓');
  } catch (err) {
    toast(err.message, 'err');
  }
});

// ─── SELECTOR TABLERO ─────────────────────────────────────────────────────────
document.getElementById('selector-tablero').addEventListener('change', async (e) => {
  if (e.target.value) await cargarTablero(e.target.value);
});

// ─── INIT ─────────────────────────────────────────────────────────────────────
cargarTableros();
