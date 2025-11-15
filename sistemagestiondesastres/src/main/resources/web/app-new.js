// ===== GLOBAL STATE =====
let mapInstance = null;
let markersLayer = null;
let routesLayer = null;

let zonesData = [];
let routesData = [];
let resourcesData = {};
let evacuationsData = [];
let usersData = [];
let equiposData = [];

let currentUser = { email: '', nombre: '', rol: '' };

let resourcesChart = null;
let evacuationsChart = null;

// Previous stats for trend calculation
let previousStats = {
    zones: 0,
    routes: 0,
    resources: 0,
    evacuations: 0
};

// ===== TOAST NOTIFICATIONS =====
function showToast(type, title, message, duration = 4000) {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type} show`;
    
    const icons = {
        success: 'fa-check-circle',
        error: 'fa-times-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };
    
    toast.innerHTML = `
        <div class="toast-icon">
            <i class="fas ${icons[type] || icons.info}"></i>
        </div>
        <div class="toast-content">
            <div class="toast-title">${title}</div>
            <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    container.appendChild(toast);
    
    // Auto-remove after duration
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

// ===== SEARCH AND FILTER =====
function initializeSearch() {
    // Search zones with enhanced features
    const zonesSearchInput = document.getElementById('zonesSearch');
    const clearZonesBtn = document.getElementById('clearZonesSearch');
    const zonesCount = document.getElementById('zonesSearchCount');
    
    if (zonesSearchInput && clearZonesBtn && zonesCount) {
        zonesSearchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            const count = filterTableEnhanced('zones-table', query, [1, 2, 3], zonesCount);
            
            // Show/hide clear button
            clearZonesBtn.style.display = query ? 'flex' : 'none';
            clearZonesBtn.classList.toggle('show', query.length > 0);
        });
        
        clearZonesBtn.addEventListener('click', () => {
            zonesSearchInput.value = '';
            filterTableEnhanced('zones-table', '', [1, 2, 3], zonesCount);
            clearZonesBtn.style.display = 'none';
            zonesSearchInput.focus();
        });
    }
    
    // Search routes with enhanced features
    const routesSearchInput = document.getElementById('routesSearch');
    const clearRoutesBtn = document.getElementById('clearRoutesSearch');
    const routesCount = document.getElementById('routesSearchCount');
    
    if (routesSearchInput && clearRoutesBtn && routesCount) {
        routesSearchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            const count = filterTableEnhanced('routes-table', query, [1, 2, 3], routesCount);
            
            clearRoutesBtn.style.display = query ? 'flex' : 'none';
            clearRoutesBtn.classList.toggle('show', query.length > 0);
        });
        
        clearRoutesBtn.addEventListener('click', () => {
            routesSearchInput.value = '';
            filterTableEnhanced('routes-table', '', [1, 2, 3], routesCount);
            clearRoutesBtn.style.display = 'none';
            routesSearchInput.focus();
        });
    }
    
    // Search evacuations with enhanced features
    const evacuationsSearchInput = document.getElementById('evacuationsSearch');
    const clearEvacuationsBtn = document.getElementById('clearEvacuationsSearch');
    const evacuationsCount = document.getElementById('evacuationsSearchCount');
    
    if (evacuationsSearchInput && clearEvacuationsBtn && evacuationsCount) {
        evacuationsSearchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            const count = filterTableEnhanced('evacuations-table', query, [1, 2, 3], evacuationsCount);
            
            clearEvacuationsBtn.style.display = query ? 'flex' : 'none';
            clearEvacuationsBtn.classList.toggle('show', query.length > 0);
        });
        
        clearEvacuationsBtn.addEventListener('click', () => {
            evacuationsSearchInput.value = '';
            filterTableEnhanced('evacuations-table', '', [1, 2, 3], evacuationsCount);
            clearEvacuationsBtn.style.display = 'none';
            evacuationsSearchInput.focus();
        });
    }
}

function filterTableEnhanced(tableId, query, columnIndices, countElement) {
    const table = document.getElementById(tableId);
    if (!table) return 0;
    
    const tbody = table.querySelector('tbody');
    if (!tbody) return 0;
    
    const rows = tbody.getElementsByTagName('tr');
    let visibleCount = 0;
    let totalCount = 0;
    const queryLower = query.toLowerCase();
    
    for (let row of rows) {
        const cells = row.getElementsByTagName('td');
        
        // Skip empty rows or loading rows
        if (cells.length === 0 || cells[0].colSpan > 1) {
            continue;
        }
        
        totalCount++;
        let found = !query; // Si no hay query, mostrar todo
        
        if (query) {
            for (let index of columnIndices) {
                if (cells[index] && cells[index].textContent.toLowerCase().includes(queryLower)) {
                    found = true;
                    break;
                }
            }
        }
        
        if (found) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    }
    
    // Update counter
    if (countElement) {
        if (query) {
            countElement.textContent = `${visibleCount} de ${totalCount}`;
            countElement.classList.add('show');
            countElement.classList.toggle('empty', visibleCount === 0);
        } else {
            countElement.classList.remove('show');
        }
    }
    
    return visibleCount;
}

// ===== STATS TREND CALCULATION =====
function updateStatTrends() {
    const currentStats = {
        zones: zonesData.length,
        routes: routesData.length,
        resources: Object.values(resourcesData).reduce((sum, recursos) => sum + recursos.length, 0),
        evacuations: evacuationsData.length
    };
    
    // Load previous stats from localStorage
    const stored = localStorage.getItem('previousStats');
    if (stored) {
        previousStats = JSON.parse(stored);
    }
    
    // Update zones trend
    updateTrendElement('stat-zones-trend', currentStats.zones, previousStats.zones, 'zonas');
    
    // Update routes trend
    updateTrendElement('stat-routes-trend', currentStats.routes, previousStats.routes, 'rutas');
    
    // Update resources trend
    updateTrendElement('stat-resources-trend', currentStats.resources, previousStats.resources, 'unidades');
    
    // Update evacuations trend
    updateTrendElement('stat-evacuations-trend', currentStats.evacuations, previousStats.evacuations, 'evacuaciones');
    
    // Save current as previous
    localStorage.setItem('previousStats', JSON.stringify(currentStats));
    previousStats = currentStats;
}

function updateTrendElement(elementId, current, previous, unit) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const diff = current - previous;
    
    if (diff > 0) {
        element.className = 'stat-change positive';
        element.innerHTML = `<i class="fas fa-arrow-up"></i> +${diff} ${unit}`;
    } else if (diff < 0) {
        element.className = 'stat-change negative';
        element.innerHTML = `<i class="fas fa-arrow-down"></i> ${diff} ${unit}`;
    } else {
        element.className = 'stat-change neutral';
        element.innerHTML = `<i class="fas fa-minus"></i> Sin cambios`;
    }
}

// ===== INITIALIZATION =====
document.addEventListener('DOMContentLoaded', async () => {
    console.log('🚀 Sistema de Gestión de Desastres iniciado');
    
    // Verificar sesión activa primero
    await checkSession();
    
    // Inicializar mapa
    initializeMap();
    
    // Cargar datos iniciales
    await loadAllData();
    
    // Inicializar gráficos
    initializeCharts();
    
    // Actualizar stats
    updateStats();
    
    // Inicializar búsqueda
    initializeSearch();
    
    // Actualizar tendencias
    updateStatTrends();
    
    console.log('✅ Sistema listo. Zonas cargadas:', zonesData.length);
});

// ===== SESSION CHECK =====
async function checkSession() {
    try {
        console.log('🔐 Verificando sesión activa...');
        const response = await fetch('/api/session', {
            method: 'GET',
            credentials: 'same-origin',
            cache: 'no-cache'
        });
        
        console.log('📡 Respuesta de /api/session:', response.status);
        
        if (!response.ok) {
            console.log('❌ Respuesta no OK:', response.status);
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log('📦 Datos recibidos:', data);
        
        if (data.authenticated && data.user) {
            // Actualizar usuario actual con datos de la sesión
            currentUser = {
                email: data.user.email,
                nombre: data.user.nombre,
                rol: data.user.rol
            };
            console.log('✅ Sesión activa encontrada:', currentUser);
            console.log('🔍 DEBUG - Rol recibido del backend:', data.user.rol);
            console.log('🔍 DEBUG - currentUser completo:', JSON.stringify(currentUser));
            updateUserHeader();
        } else {
            console.log('❌ No hay sesión activa - redirigiendo al login');
            window.location.replace('login-new.html');
        }
    } catch (error) {
        console.error('❌ Error verificando sesión:', error);
        console.error('❌ Error details:', error.message);
        // Redirigir al login si no hay sesión válida
        window.location.replace('login-new.html');
    }
}

// ===== UPDATE USER HEADER =====
function updateUserHeader() {
    const userAvatar = document.getElementById('userAvatar');
    const userName = document.getElementById('userName');
    const roleBadge = document.getElementById('roleBadge');
    
    if (userAvatar && userName && roleBadge && currentUser && currentUser.nombre && currentUser.rol) {
        const initials = currentUser.nombre.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
        userAvatar.textContent = initials;
        userName.textContent = currentUser.nombre;
        
        // Determinar rol y clase
        const isAdmin = currentUser.rol.toUpperCase() === 'ADMIN';
        roleBadge.textContent = isAdmin ? 'ADMIN' : 'OPERADOR';
        roleBadge.className = 'role-badge ' + (isAdmin ? 'admin' : 'operador');
        
        console.log('✅ Usuario:', currentUser.nombre, '| Rol:', roleBadge.textContent);
    }
    
    // Aplicar restricciones visuales según el rol
    applyRoleRestrictions();
}

// ===== APPLY ROLE RESTRICTIONS =====
function applyRoleRestrictions() {
    const isAdmin = currentUser.rol && currentUser.rol.toUpperCase() === 'ADMIN';
    
    // Controlar botón de registrar usuario
    const btnRegisterUser = document.getElementById('btnRegisterUser');
    if (btnRegisterUser) {
        if (!isAdmin) {
            btnRegisterUser.disabled = true;
            btnRegisterUser.style.opacity = '0.5';
            btnRegisterUser.style.cursor = 'not-allowed';
            btnRegisterUser.title = 'Solo los administradores pueden registrar usuarios';
        } else {
            btnRegisterUser.disabled = false;
            btnRegisterUser.style.opacity = '1';
            btnRegisterUser.style.cursor = 'pointer';
            btnRegisterUser.title = 'Registrar nuevo usuario';
        }
    }
    
    console.log(isAdmin ? '✅ Usuario con privilegios de ADMIN' : 'ℹ️ Usuario con rol de OPERADOR (funciones limitadas)');
}

// ===== OPEN REGISTER USER =====
function openRegisterUser() {
    const isAdmin = currentUser.rol && currentUser.rol.toUpperCase() === 'ADMIN';
    
    if (!isAdmin) {
        showToast('warning', 'Acceso Denegado', 'Solo los administradores pueden registrar nuevos usuarios en el sistema.');
        return;
    }
    
    window.open('register-new.html', '_blank', 'width=600,height=800');
}

// ===== MAP FUNCTIONS =====
function initializeMap() {
    try {
        mapInstance = L.map('map').setView([4.7110, -74.0721], 6);
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors',
            maxZoom: 19
        }).addTo(mapInstance);
        
        markersLayer = L.layerGroup().addTo(mapInstance);
        routesLayer = L.layerGroup().addTo(mapInstance);
        
        console.log('✅ Mapa inicializado');
    } catch (error) {
        console.error('❌ Error al inicializar mapa:', error);
    }
}

function refreshMap() {
    if (mapInstance) {
        markersLayer.clearLayers();
        routesLayer.clearLayers();
        updateMapMarkers();
        updateMapRoutes();
    }
}

function centerMap() {
    if (mapInstance) {
        mapInstance.setView([4.7110, -74.0721], 6);
    }
}

function updateMapMarkers() {
    if (!markersLayer || !zonesData) return;
    
    markersLayer.clearLayers();
    
    zonesData.forEach(zone => {
        // Soportar ambos formatos: zone.lat/lng O zone.coordenadas.lat/lng
        const lat = zone.lat || (zone.coordenadas && zone.coordenadas.lat);
        const lng = zone.lng || (zone.coordenadas && zone.coordenadas.lng);
        const nivelRiesgo = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
        const evacuada = zone.evacuada || false;
        
        if (lat && lng) {
            // Si está evacuada, usar verde claro; si no, usar colores por riesgo
            const color = evacuada ? '#10b981' : (nivelRiesgo > 70 ? '#ef4444' : nivelRiesgo > 40 ? '#f59e0b' : '#10b981');
            const icon = evacuada ? '✅' : (nivelRiesgo > 70 ? '🔴' : nivelRiesgo > 40 ? '🟡' : '🟢');
            const intensidadPulso = evacuada ? 'pulse-evacuada' : (nivelRiesgo > 70 ? 'pulse-high' : nivelRiesgo > 40 ? 'pulse-medium' : 'pulse-low');
            
            // Calcular recursos disponibles en la zona
            const recursosZona = resourcesData[zone.nombre] || {};
            const totalRecursos = Object.values(recursosZona).reduce((sum, val) => sum + val, 0);
            const necesitaRecursos = !evacuada && totalRecursos < 10;
            
            // Tamaño del marcador según población y riesgo (más pequeño si evacuada)
            const markerSize = evacuada ? 50 : Math.min(80, 40 + (nivelRiesgo / 10));
            const opacityMarker = evacuada ? 0.7 : 1;
            
            // Marcador con ícono personalizado mejorado
            const customIcon = L.divIcon({
                className: 'custom-zone-marker',
                html: `
                    <div style="
                        position: relative;
                        width: ${markerSize}px;
                        height: ${markerSize}px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    ">
                        <!-- Pulso animado más intenso para zonas críticas -->
                        <div class="${intensidadPulso}" style="
                            position: absolute;
                            width: ${markerSize}px;
                            height: ${markerSize}px;
                            background: ${color};
                            border-radius: 50%;
                            opacity: 0.4;
                        "></div>
                        <!-- Segundo pulso para zonas críticas -->
                        ${nivelRiesgo > 70 ? `
                        <div class="pulse-high" style="
                            position: absolute;
                            width: ${markerSize}px;
                            height: ${markerSize}px;
                            background: ${color};
                            border-radius: 50%;
                            opacity: 0.3;
                            animation-delay: 1s;
                        "></div>
                        ` : ''}
                        <!-- Círculo principal -->
                        <div style="
                            position: relative;
                            width: ${markerSize * 0.65}px;
                            height: ${markerSize * 0.65}px;
                            background: ${color};
                            border: 4px solid white;
                            border-radius: 50%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.4);
                            font-size: ${markerSize * 0.35}px;
                            z-index: 1;
                        ">
                            ${icon}
                        </div>
                        <!-- Indicador de falta de recursos -->
                        ${necesitaRecursos ? `
                        <div style="
                            position: absolute;
                            top: -5px;
                            right: -5px;
                            width: 24px;
                            height: 24px;
                            background: #dc2626;
                            border: 3px solid white;
                            border-radius: 50%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 12px;
                            z-index: 2;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.3);
                            animation: bounce 1.5s infinite;
                        ">
                            ⚠️
                        </div>
                        ` : ''}
                    </div>
                    <style>
                        @keyframes pulse-high {
                            0%, 100% { transform: scale(1); opacity: 0.5; }
                            50% { transform: scale(1.5); opacity: 0.1; }
                        }
                        @keyframes pulse-medium {
                            0%, 100% { transform: scale(1); opacity: 0.4; }
                            50% { transform: scale(1.3); opacity: 0.1; }
                        }
                        @keyframes pulse-low {
                            0%, 100% { transform: scale(1); opacity: 0.3; }
                            50% { transform: scale(1.2); opacity: 0.05; }
                        }
                        @keyframes pulse-evacuada {
                            0%, 100% { transform: scale(1); opacity: 0.2; }
                            50% { transform: scale(1.1); opacity: 0.05; }
                        }
                        @keyframes bounce {
                            0%, 100% { transform: translateY(0); }
                            50% { transform: translateY(-5px); }
                        }
                        .pulse-high { animation: pulse-high 1.5s infinite; }
                        .pulse-medium { animation: pulse-medium 2s infinite; }
                        .pulse-low { animation: pulse-low 2.5s infinite; }
                        .pulse-evacuada { animation: pulse-evacuada 3s infinite; }
                    </style>
                `,
                iconSize: [markerSize, markerSize],
                iconAnchor: [markerSize / 2, markerSize / 2]
            });
            
            const marker = L.marker([lat, lng], {
                icon: customIcon,
                zIndexOffset: nivelRiesgo > 70 ? 1000 : nivelRiesgo > 40 ? 500 : 0
            }).addTo(markersLayer);
            
            // Popup mejorado con información de recursos
            marker.bindPopup(`
                <div style="min-width: 300px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
                    <div style="
                        background: linear-gradient(135deg, ${color} 0%, ${color}dd 100%);
                        color: white;
                        padding: 14px 18px;
                        margin: -12px -16px 16px -16px;
                        border-radius: 8px 8px 0 0;
                        font-weight: 700;
                        font-size: 17px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    ">
                        <span style="font-size: 24px;">${icon}</span>
                        <span>${zone.nombre}</span>
                    </div>
                    
                    <div style="padding: 8px 0;">
                        <!-- Información principal -->
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 12px;">
                            <div style="
                                background: #f8fafc;
                                padding: 10px;
                                border-radius: 8px;
                                text-align: center;
                                border-left: 3px solid #667eea;
                            ">
                                <div style="font-size: 11px; color: #64748b; font-weight: 600; margin-bottom: 4px;">👥 POBLACIÓN</div>
                                <div style="font-size: 18px; font-weight: 700; color: #0f172a;">${zone.poblacion.toLocaleString()}</div>
                            </div>
                            <div style="
                                background: #f8fafc;
                                padding: 10px;
                                border-radius: 8px;
                                text-align: center;
                                border-left: 3px solid ${color};
                            ">
                                <div style="font-size: 11px; color: #64748b; font-weight: 600; margin-bottom: 4px;">⚠️ RIESGO</div>
                                <div style="font-size: 18px; font-weight: 700; color: ${color};">${nivelRiesgo}%</div>
                            </div>
                        </div>
                        
                        <!-- Barra de riesgo -->
                        <div style="margin-bottom: 12px;">
                            <div style="
                                width: 100%;
                                height: 10px;
                                background: #e5e7eb;
                                border-radius: 5px;
                                overflow: hidden;
                                box-shadow: inset 0 2px 4px rgba(0,0,0,0.1);
                            ">
                                <div style="
                                    width: ${nivelRiesgo}%;
                                    height: 100%;
                                    background: linear-gradient(90deg, ${color}, ${color}dd);
                                    border-radius: 5px;
                                    transition: width 0.3s ease;
                                    box-shadow: 0 0 10px ${color}80;
                                "></div>
                            </div>
                        </div>
                        
                        <!-- Estado y recursos -->
                        <div style="
                            background: ${nivelRiesgo > 70 ? '#fef2f2' : nivelRiesgo > 40 ? '#fffbeb' : '#f0fdf4'};
                            padding: 10px 12px;
                            border-radius: 8px;
                            border-left: 4px solid ${color};
                            font-size: 13px;
                            margin-bottom: 12px;
                        ">
                            <strong style="color: ${color};">Estado:</strong>
                            <span style="color: #475569;">
                                ${nivelRiesgo > 70 
                                    ? ' 🚨 Crítico - Evacuación inmediata necesaria' 
                                    : nivelRiesgo > 40 
                                    ? ' ⚠️ Moderado - Monitoreo continuo requerido' 
                                    : ' ✅ Estable - Situación bajo control'}
                            </span>
                        </div>
                        
                        <!-- Recursos disponibles -->
                        <div style="
                            background: ${necesitaRecursos ? '#fef2f2' : '#f0fdf4'};
                            padding: 10px 12px;
                            border-radius: 8px;
                            border-left: 4px solid ${necesitaRecursos ? '#dc2626' : '#10b981'};
                            font-size: 13px;
                        ">
                            <strong style="color: ${necesitaRecursos ? '#dc2626' : '#10b981'};">
                                📦 Recursos:
                            </strong>
                            <span style="color: #475569;">
                                ${totalRecursos} unidades disponibles
                                ${necesitaRecursos ? ' ⚠️ Se requieren más recursos' : ' ✅'}
                            </span>
                            ${totalRecursos > 0 ? `
                            <div style="margin-top: 6px; font-size: 11px; color: #64748b;">
                                ${Object.entries(recursosZona).map(([tipo, cant]) => 
                                    `<span style="background: white; padding: 2px 6px; border-radius: 4px; margin-right: 4px;">${tipo}: ${cant}</span>`
                                ).join('')}
                            </div>
                            ` : ''}
                        </div>
                    </div>
                    
                    <div style="
                        margin-top: 14px;
                        padding-top: 12px;
                        border-top: 1px solid #e5e7eb;
                        display: flex;
                        gap: 8px;
                    ">
                        <button onclick="alert('Ver detalles de ${zone.nombre}')" style="
                            flex: 1;
                            padding: 10px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            border: none;
                            border-radius: 8px;
                            font-weight: 600;
                            cursor: pointer;
                            font-size: 13px;
                            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
                            transition: transform 0.2s;
                        " onmouseover="this.style.transform='translateY(-2px)'" onmouseout="this.style.transform='translateY(0)'">
                            📊 Ver Detalles
                        </button>
                    </div>
                </div>
            `, {
                maxWidth: 340,
                className: 'custom-popup'
            });
            
            // Círculo de área de influencia proporcional a población y riesgo
            const radioInfluencia = Math.sqrt(zone.poblacion) * 5 * (1 + nivelRiesgo / 100);
            L.circle([lat, lng], {
                radius: radioInfluencia,
                fillColor: color,
                color: color,
                weight: 2,
                opacity: 0.4,
                fillOpacity: 0.15,
                dashArray: nivelRiesgo > 70 ? '5, 5' : '10, 10'
            }).addTo(markersLayer);
        }
    });
}

function updateMapRoutes() {
    if (!routesLayer || !routesData) {
        console.log('⚠️ No se puede actualizar rutas en el mapa: faltan datos');
        return;
    }
    
    routesLayer.clearLayers();
    console.log('🗺️ Dibujando', routesData.length, 'rutas en el mapa...');
    
    let routasVisibles = 0;
    
    routesData.forEach((route, index) => {
        // Verificar que la ruta tenga coordenadas válidas
        if (!route.coordenadasOrigen || !route.coordenadasDestino) {
            console.log(`⚠️ Ruta ${index} sin coordenadas, saltando...`);
            return;
        }
        
        const { lat: latOrigen, lng: lngOrigen } = route.coordenadasOrigen;
        const { lat: latDestino, lng: lngDestino } = route.coordenadasDestino;
        
        if (!latOrigen || !lngOrigen || !latDestino || !lngDestino) {
            console.log(`⚠️ Coordenadas inválidas en ruta ${index}`);
            return;
        }
        
        const disponible = route.disponible !== false;
        const color = disponible ? '#10b981' : '#ef4444';
        const distancia = route.distancia || 0;
        const tiempo = route.tiempo || 0;
        const capacidad = route.capacidad || 100;
        
        // Línea de ruta simple y eficiente
        const line = L.polyline(
            [[latOrigen, lngOrigen], [latDestino, lngDestino]],
            {
                color: color,
                weight: 4,
                opacity: disponible ? 0.7 : 0.4,
                dashArray: disponible ? null : '10, 10',
                className: 'route-line'
            }
        ).addTo(routesLayer);
        
        // Popup con información al hacer clic
        const popupContent = `
            <div style="font-family: system-ui, -apple-system, sans-serif; min-width: 200px;">
                <div style="font-weight: 700; font-size: 14px; margin-bottom: 8px; color: ${color};">
                    🛣️ ${route.nombreOrigen || 'Origen'} → ${route.nombreDestino || 'Destino'}
                </div>
                <div style="font-size: 12px; color: #64748b; line-height: 1.6;">
                    📏 Distancia: <strong>${distancia.toFixed(1)} km</strong><br>
                    ⏱️ Tiempo: <strong>${tiempo.toFixed(1)} hrs</strong><br>
                    👥 Capacidad: <strong>${capacidad} personas</strong><br>
                    ${disponible 
                        ? '<span style="color: #10b981;">✅ Disponible</span>' 
                        : '<span style="color: #ef4444;">❌ Bloqueada</span>'}
                </div>
            </div>
        `;
        
        line.bindPopup(popupContent);
        
        // Marcador en el punto medio (opcional, más ligero)
        const midLat = (latOrigen + latDestino) / 2;
        const midLng = (lngOrigen + lngDestino) / 2;
        
        const midIcon = L.divIcon({
            className: 'route-mid-marker',
            html: `
                <div style="
                    width: 24px;
                    height: 24px;
                    background: ${color};
                    border: 2px solid white;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 12px;
                    color: white;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
                ">→</div>
            `,
            iconSize: [24, 24],
            iconAnchor: [12, 12]
        });
        
        L.marker([midLat, midLng], { icon: midIcon })
            .bindPopup(popupContent)
            .addTo(routesLayer);
        
        routasVisibles++;
    });
    
    console.log(`✅ ${routasVisibles} rutas dibujadas en el mapa`);
}

// ===== DATA LOADING =====
async function loadAllData() {
    try {
        console.log('🔄 Iniciando carga de datos...');
        
        // Primero cargar zonas (necesarias para enriquecer rutas)
        await loadZones();
        
        // Luego cargar rutas y enriquecerlas con coordenadas de zonas
        await loadRoutes();
        
        // Cargar el resto de datos en paralelo
        await Promise.all([
            loadResources(),
            loadEvacuations(),
            loadUsers(),
            loadEquipos()
        ]);
        
        console.log('✅ Todos los datos cargados');
        
        // Actualizar header con usuario actual
        updateUserHeader();
        
        // Actualizar estadísticas después de cargar datos
        updateStats();
        
        // Actualizar gráficos
        if (resourcesChart) updateResourcesChart();
        if (evacuationsChart) updateEvacuationsChart();
    } catch (error) {
        console.error('❌ Error cargando datos:', error);
    }
}

async function loadZones() {
    try {
        console.log('📍 Cargando zonas desde /api/zones...');
        const res = await fetch('/api/zones');
        
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        
        const rawData = await res.json();
        
        // Asegurar que todas las zonas tengan la estructura correcta
        zonesData = rawData.map(zone => ({
            ...zone,
            nivelDeRiesgo: zone.nivelDeRiesgo || zone.nivelRiesgo || 0,
            lat: zone.lat || (zone.coordenadas && zone.coordenadas.lat) || null,
            lng: zone.lng || (zone.coordenadas && zone.coordenadas.lng) || null
        }));
        
        console.log('✅ Zonas procesadas:', zonesData.length, 'zonas cargadas');
        console.log('Zonas en memoria:', zonesData);
        
        updateZonesTable();
        updateMapMarkers();
        
        return zonesData;
    } catch (error) {
        console.error('❌ Error loading zones:', error);
        zonesData = [];
        return [];
    }
}

async function loadRoutes() {
    try {
        console.log('🛣️ Cargando rutas desde /api/routes...');
        const res = await fetch('/api/routes');
        
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        
        routesData = await res.json();
        console.log('✅ Rutas cargadas:', routesData.length, routesData);
        
        // Si no hay rutas, agregar datos quemados de ejemplo
        if (!routesData || routesData.length === 0) {
            console.log('ℹ️ No hay rutas en el backend, usando datos de ejemplo');
            routesData = [];
        }
        
        // Enriquecer rutas con coordenadas de las zonas
        enrichRoutesWithCoordinates();
        
        updateRoutesTable();
        updateMapRoutes();
    } catch (error) {
        console.error('❌ Error loading routes:', error);
        routesData = [];
        updateRoutesTable();
    }
}

// Función para enriquecer rutas con coordenadas de zonas
function enrichRoutesWithCoordinates() {
    if (!routesData || !zonesData || routesData.length === 0 || zonesData.length === 0) {
        console.log('⚠️ No se pueden enriquecer rutas: faltan datos');
        return;
    }
    
    console.log('🔄 Enriqueciendo rutas con coordenadas...');
    
    routesData = routesData.map(route => {
        // Buscar zona origen
        const zonaOrigen = zonesData.find(z => 
            z.id === route.origenId || 
            z.nombre === route.nombreOrigen ||
            z.nombre === route.origen
        );
        
        // Buscar zona destino
        const zonaDestino = zonesData.find(z => 
            z.id === route.destinoId || 
            z.nombre === route.nombreDestino ||
            z.nombre === route.destino
        );
        
        // Agregar coordenadas si se encontraron las zonas
        if (zonaOrigen && zonaDestino) {
            const enrichedRoute = {
                ...route,
                coordenadasOrigen: {
                    lat: zonaOrigen.lat || zonaOrigen.coordenadas?.lat,
                    lng: zonaOrigen.lng || zonaOrigen.coordenadas?.lng
                },
                coordenadasDestino: {
                    lat: zonaDestino.lat || zonaDestino.coordenadas?.lat,
                    lng: zonaDestino.lng || zonaDestino.coordenadas?.lng
                },
                nombreOrigen: route.nombreOrigen || zonaOrigen.nombre,
                nombreDestino: route.nombreDestino || zonaDestino.nombre
            };
            
            console.log(`✅ Ruta enriquecida: ${enrichedRoute.nombreOrigen} → ${enrichedRoute.nombreDestino}`);
            return enrichedRoute;
        } else {
            console.warn(`⚠️ No se encontraron zonas para ruta:`, route.origenId, '→', route.destinoId);
            return route;
        }
    });
    
    // Filtrar rutas que tienen coordenadas
    const validRoutes = routesData.filter(r => r.coordenadasOrigen && r.coordenadasDestino);
    console.log(`✅ ${validRoutes.length} rutas tienen coordenadas válidas de ${routesData.length} totales`);
}

async function loadResources() {
    try {
        console.log('📦 Cargando recursos desde /api/resources...');
        const res = await fetch('/api/resources');
        
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        
        resourcesData = await res.json();
        console.log('✅ Recursos cargados:', resourcesData);
        
        updateResourcesTable();
        
        // Actualizar gráfico de recursos si ya existe
        if (resourcesChart) {
            updateResourcesChart();
        }
    } catch (error) {
        console.error('❌ Error loading resources:', error);
        resourcesData = {};
    }
}

async function loadEvacuations() {
    try {
        console.log('🚑 Cargando evacuaciones desde /api/evacuations...');
        const res = await fetch('/api/evacuations');
        
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        
        evacuationsData = await res.json();
        console.log('✅ Evacuaciones cargadas:', evacuationsData.length, evacuationsData);
        
        updateEvacuationsTable();
        
        // Actualizar gráfico de evacuaciones si ya existe
        if (evacuationsChart) {
            updateEvacuationsChart();
        }
    } catch (error) {
        console.error('❌ Error loading evacuations:', error);
        evacuationsData = [];
    }
}

async function loadUsers() {
    try {
        console.log('👥 Cargando usuarios desde /api/users...');
        const res = await fetch('/api/users');
        
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        
        usersData = await res.json();
        console.log('✅ Usuarios cargados:', usersData.length, usersData);
        
        updateUsersTable();
    } catch (error) {
        console.error('❌ Error loading users:', error);
        usersData = [];
    }
}

// Función para recargar solo las zonas (llamada desde zone-register.html)
async function reloadZonesData() {
    console.log('🔄 Recargando datos de zonas...');
    await loadZones();
    updateStats();
    
    // Si el modal de rutas está abierto, recargar los selectores
    const routeModal = document.getElementById('route-modal');
    if (routeModal && routeModal.classList.contains('active')) {
        loadZonasIntoSelects();
    }
    
    console.log('✅ Zonas actualizadas en el mapa y selectores');
}

// ===== STATS UPDATE =====
function updateStats() {
    // Animar contador de zonas
    animateCounter('stat-zones', zonesData.length);
    
    // Animar contador de rutas
    animateCounter('stat-routes', routesData.length);
    
    // Calcular total de recursos con animación
    let totalResources = 0;
    Object.values(resourcesData).forEach(location => {
        Object.values(location).forEach(qty => totalResources += qty);
    });
    animateCounter('stat-resources', totalResources);
    
    // Animar contador de evacuaciones
    animateCounter('stat-evacuations', evacuationsData.length);
}

function animateCounter(elementId, target) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const current = parseInt(element.textContent.replace(/,/g, '')) || 0;
    const increment = Math.ceil((target - current) / 30);
    
    if (current === target) return;
    
    const timer = setInterval(() => {
        const value = parseInt(element.textContent.replace(/,/g, '')) || 0;
        if ((increment > 0 && value < target) || (increment < 0 && value > target)) {
            element.textContent = (value + increment).toLocaleString();
        } else {
            element.textContent = target.toLocaleString();
            clearInterval(timer);
        }
    }, 30);
}

// ===== ZONE FUNCTIONS =====
function openZoneRegister() {
    window.open('zone-register.html', '_blank', 'width=1200,height=800');
}

// ===== TABLE UPDATES =====
function updateZonesTable() {
    const tbody = document.querySelector('#zones-table tbody');
    
    if (zonesData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                    <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                        <i class="fas fa-map-marked-alt" style="font-size: 3rem; opacity: 0.3;"></i>
                        <div>
                            <h3 style="margin-bottom: 0.5rem; color: var(--text-primary);">No hay zonas registradas</h3>
                            <p style="font-size: 0.875rem;">Haz clic en "Nueva Zona" para comenzar</p>
                        </div>
                    </div>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = zonesData.map(zone => {
        const nivelRiesgo = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
        const evacuada = zone.evacuada || false;
        
        // Si está evacuada, cambiar colores a gris/verde
        const riskColor = evacuada ? '#10b981' : (nivelRiesgo > 70 ? '#ef4444' : nivelRiesgo > 40 ? '#f59e0b' : '#10b981');
        const riskBadge = evacuada ? 'badge-success' : (nivelRiesgo > 70 ? 'badge-danger' : nivelRiesgo > 40 ? 'badge-warning' : 'badge-success');
        const riskText = evacuada ? '✅ Evacuada' : (nivelRiesgo > 70 ? '🔴 Alto' : nivelRiesgo > 40 ? '🟡 Medio' : '🟢 Bajo');
        const iconoZona = evacuada ? '✅' : '📍';
        const opacityStyle = evacuada ? 'opacity: 0.6;' : '';
        
        return `
        <tr style="transition: all 0.3s ease; ${opacityStyle}">
            <td>
                <code style="
                    background: #f1f5f9;
                    padding: 4px 8px;
                    border-radius: 6px;
                    font-size: 0.75rem;
                    font-weight: 600;
                    color: #667eea;
                ">${zone.id}</code>
            </td>
            <td>
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <div style="
                        width: 40px;
                        height: 40px;
                        background: linear-gradient(135deg, ${riskColor}, ${riskColor}dd);
                        border-radius: 10px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 18px;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                    ">
                        ${iconoZona}
                    </div>
                    <div>
                        <strong style="font-size: 0.95rem; color: #0f172a;">${zone.nombre}</strong>
                        ${evacuada ? '<span style="color: #10b981; font-size: 0.75rem; font-weight: 600; margin-left: 0.5rem;">🎯 EVACUADA</span>' : ''}
                        ${(zone.lat && zone.lng) || (zone.coordenadas && zone.coordenadas.lat) ? `
                        <div style="font-size: 0.75rem; color: #64748b; margin-top: 2px;">
                            <i class="fas fa-map-pin" style="font-size: 10px;"></i>
                            ${(zone.lat || zone.coordenadas.lat).toFixed(4)}, ${(zone.lng || zone.coordenadas.lng).toFixed(4)}
                        </div>
                        ` : ''}
                    </div>
                </div>
            </td>
            <td>
                <div style="display: flex; align-items: center; gap: 0.5rem;">
                    <i class="fas fa-users" style="color: ${evacuada ? '#10b981' : '#667eea'}; font-size: 14px;"></i>
                    <div>
                        <strong style="font-size: 1rem; ${evacuada ? 'text-decoration: line-through; color: #64748b;' : ''}">${zone.poblacion.toLocaleString()}</strong>
                        ${evacuada ? '<div style="font-size: 0.75rem; color: #10b981; font-weight: 600;">✅ Todos evacuados</div>' : ''}
                        ${!evacuada && zone.poblacionInicial && zone.poblacion < zone.poblacionInicial ? `<div style="font-size: 0.75rem; color: #f59e0b;">(${((zone.poblacion / zone.poblacionInicial) * 100).toFixed(0)}% restante)</div>` : ''}
                    </div>
                </div>
            </td>
            <td>
                <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                        <div style="
                            flex: 1;
                            height: 10px;
                            background: #e5e7eb;
                            border-radius: 5px;
                            overflow: hidden;
                            position: relative;
                        ">
                            <div style="
                                width: ${nivelRiesgo}%;
                                height: 100%;
                                background: linear-gradient(90deg, ${riskColor}, ${riskColor}dd);
                                border-radius: 5px;
                                transition: width 0.5s ease;
                                box-shadow: 0 0 8px ${riskColor}66;
                            "></div>
                        </div>
                        <span style="
                            font-weight: 700;
                            font-size: 0.875rem;
                            min-width: 45px;
                            text-align: right;
                            color: ${riskColor};
                        ">${nivelRiesgo}%</span>
                    </div>
                </div>
            </td>
            <td>
                <span class="badge ${riskBadge}" style="
                    font-size: 0.8rem;
                    padding: 6px 12px;
                    display: inline-flex;
                    align-items: center;
                    gap: 0.25rem;
                ">
                    ${riskText}
                </span>
            </td>
            <td>
                <div style="display: flex; gap: 0.5rem; justify-content: center;">
                    <button class="btn btn-secondary btn-icon" onclick="viewZone('${zone.id}')" title="Ver detalles" style="
                        transition: all 0.3s ease;
                    " onmouseover="this.style.background='#667eea'; this.style.color='white';" 
                       onmouseout="this.style.background=''; this.style.color='';">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-secondary btn-icon" onclick="editZone('${zone.id}')" title="Editar" style="
                        transition: all 0.3s ease;
                    " onmouseover="this.style.background='#f59e0b'; this.style.color='white';" 
                       onmouseout="this.style.background=''; this.style.color='';">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-secondary btn-icon" onclick="if(confirm('¿Eliminar zona ${zone.nombre}?')) deleteZone('${zone.id}')" title="Eliminar" style="
                        transition: all 0.3s ease;
                    " onmouseover="this.style.background='#ef4444'; this.style.color='white';" 
                       onmouseout="this.style.background=''; this.style.color='';">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `}).join('');
}

function updateRoutesTable() {
    const tbody = document.querySelector('#routes-table tbody');
    
    if (routesData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-secondary);">
                    <i class="fas fa-inbox" style="font-size: 2rem; display: block; margin-bottom: 1rem;"></i>
                    No hay rutas registradas
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = routesData.map((route, idx) => {
        const routeId = route.id || route.origenId + '-' + route.destinoId || `ruta-${idx}`;
        const origen = route.nombreOrigen || route.origen || route.origenId || 'N/A';
        const destino = route.nombreDestino || route.destino || route.destinoId || 'N/A';
        const distancia = route.distancia || 0;
        const disponible = route.disponible !== undefined ? route.disponible : true;
        
        return `
        <tr>
            <td><code>${routeId}</code></td>
            <td>${origen}</td>
            <td>${destino}</td>
            <td>${distancia.toFixed(2)} km</td>
            <td>
                <span class="badge ${disponible ? 'badge-success' : 'badge-danger'}">
                    ${disponible ? '✅ Disponible' : '❌ Bloqueada'}
                </span>
            </td>
            <td>
                <button class="btn btn-secondary btn-icon" onclick="viewRoute('${routeId}')">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-secondary btn-icon" onclick="editRoute('${routeId}')">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        </tr>
    `}).join('');
}

// Global array para almacenar info de recursos para transferencia
window.transferResourcesInfo = [];

function updateResourcesTable() {
    const tbody = document.querySelector('#resources-table tbody');
    const rows = [];
    
    // Resetear el array global
    window.transferResourcesInfo = [];
    
    let index = 0;
    Object.entries(resourcesData).forEach(([location, resources]) => {
        Object.entries(resources).forEach(([type, qty]) => {
            // Guardar info en array global
            window.transferResourcesInfo[index] = { location, type };
            
            rows.push(`
                <tr>
                    <td><strong>${location}</strong></td>
                    <td>${type}</td>
                    <td><strong>${qty.toLocaleString()}</strong></td>
                    <td>
                        <span class="badge ${qty > 100 ? 'badge-success' : qty > 50 ? 'badge-warning' : 'badge-danger'}">
                            ${qty > 100 ? 'Suficiente' : qty > 50 ? 'Medio' : 'Bajo'}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-secondary btn-icon" onclick="openTransferModal(${index})">
                            <i class="fas fa-exchange-alt"></i>
                        </button>
                    </td>
                </tr>
            `);
            index++;
        });
    });
    
    if (rows.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; padding: 2rem; color: var(--text-secondary);">
                    <i class="fas fa-inbox" style="font-size: 2rem; display: block; margin-bottom: 1rem;"></i>
                    No hay recursos registrados
                </td>
            </tr>
        `;
    } else {
        tbody.innerHTML = rows.join('');
    }
}

function updateEvacuationsTable() {
    const tbody = document.querySelector('#evacuations-table tbody');
    
    if (!evacuationsData || evacuationsData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-secondary);">
                    <i class="fas fa-inbox" style="font-size: 2rem; display: block; margin-bottom: 1rem;"></i>
                    No hay evacuaciones registradas
                </td>
            </tr>
        `;
        return;
    }
    
    // Ordenar por prioridad (riesgo descendente)
    const sortedEvacuations = [...evacuationsData].sort((a, b) => {
        const prioridadA = a.prioridad || a.nivelDeRiesgo || a.nivelRiesgo || 0;
        const prioridadB = b.prioridad || b.nivelDeRiesgo || b.nivelRiesgo || 0;
        return prioridadB - prioridadA;
    });
    
    tbody.innerHTML = sortedEvacuations.map((evac, idx) => {
        const zona = evac.zonaNombre || evac.nombre || evac.zonaOrigen || evac.zona || 'N/A';
        const personas = evac.personas || evac.poblacion || evac.numeroPersonas || 0;
        const prioridad = evac.prioridad || evac.nivelDeRiesgo || evac.nivelRiesgo || 0;
        const estado = evac.estado || 'PENDIENTE';
        const poblacionTotal = evac.poblacionTotal || evac.poblacion || personas;
        
        // Determinar badge de prioridad
        let badgeClass = 'badge-info';
        let prioridadText = 'Baja';
        if (prioridad > 70) {
            badgeClass = 'badge-danger';
            prioridadText = '🔴 Alta';
        } else if (prioridad > 40) {
            badgeClass = 'badge-warning';
            prioridadText = '🟡 Media';
        } else {
            badgeClass = 'badge-success';
            prioridadText = '🟢 Baja';
        }
        
        // Badge de estado
        let estadoBadge = 'badge-warning';
        let estadoText = estado;
        let isCompletada = false;
        
        if (estado === 'COMPLETADA') {
            estadoBadge = 'badge-success';
            estadoText = '✅ Completada';
            isCompletada = true;
        } else if (estado === 'EN_PROGRESO') {
            estadoBadge = 'badge-info';
            estadoText = '🔄 En Progreso';
        } else {
            estadoBadge = 'badge-warning';
            estadoText = '⏳ Pendiente';
        }
        
        // Posición en la cola (ordenado por prioridad)
        const posicion = idx + 1;
        
        // Estilo especial para evacuaciones completadas
        const rowStyle = isCompletada 
            ? 'background: #f0fdf4; opacity: 0.7;' 
            : (posicion <= 3 ? 'background: #fef3c7;' : '');
        
        return `
        <tr style="${rowStyle}">
            <td>
                <strong style="font-size: 1.1rem; color: ${isCompletada ? '#16a34a' : posicion === 1 ? '#dc2626' : posicion === 2 ? '#f59e0b' : posicion === 3 ? '#16a34a' : '#64748b'};">
                    ${isCompletada ? '✓' : '#' + posicion}
                </strong>
            </td>
            <td><strong style="${isCompletada ? 'text-decoration: line-through; color: #6b7280;' : ''}">${zona}</strong></td>
            <td>${personas.toLocaleString()} / ${poblacionTotal.toLocaleString()}</td>
            <td>
                <span class="badge ${badgeClass}">
                    ${prioridadText} (${prioridad}%)
                </span>
            </td>
            <td>
                <span class="badge ${estadoBadge}">
                    ${estadoText}
                </span>
            </td>
            <td>
                ${isCompletada 
                    ? '<span style="color: #16a34a; font-size: 1.5rem;">✓</span>' 
                    : `<button class="btn btn-success btn-icon" onclick="processEvacuation(${idx})" title="Procesar evacuación">
                        <i class="fas fa-ambulance"></i>
                    </button>`
                }
            </td>
        </tr>
    `}).join('');
}

function updateUsersTable() {
    const tbody = document.querySelector('#users-table tbody');
    if (!tbody) {
        console.warn('⚠️ No se encontró la tabla de usuarios');
        return;
    }
    
    if (!usersData || usersData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; padding: 2rem;">
                    <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
                        <i class="fas fa-users" style="font-size: 3rem; opacity: 0.3;"></i>
                        <div>
                            <h3 style="margin-bottom: 0.5rem; color: var(--text-primary);">No hay usuarios registrados</h3>
                            <p style="font-size: 0.875rem;">Haz clic en "Registrar Usuario" para comenzar</p>
                        </div>
                    </div>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = usersData.map((user, idx) => {
        const rolBadge = user.rol === 'ADMIN' 
            ? '<span class="badge badge-danger">👑 Admin</span>' 
            : '<span class="badge badge-info">👤 Operador</span>';
        
        const estadoBadge = user.estado === 'ACTIVO'
            ? '<span class="badge badge-success">✓ Activo</span>'
            : '<span class="badge badge-secondary">⊗ Inactivo</span>';
        
        return `
        <tr style="transition: all 0.3s ease;">
            <td>
                <code style="
                    background: #f1f5f9;
                    padding: 4px 8px;
                    border-radius: 6px;
                    font-size: 0.75rem;
                    font-weight: 600;
                    color: #667eea;
                ">${user.id.substring(0, 16)}...</code>
            </td>
            <td>
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <div style="
                        width: 40px;
                        height: 40px;
                        background: linear-gradient(135deg, ${user.rol === 'ADMIN' ? '#ef4444' : '#3b82f6'}, ${user.rol === 'ADMIN' ? '#dc2626' : '#2563eb'});
                        border-radius: 10px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 18px;
                        color: white;
                        font-weight: 700;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                    ">
                        ${user.nombre.charAt(0).toUpperCase()}
                    </div>
                    <div>
                        <strong style="font-size: 0.95rem; color: #0f172a;">${user.nombre}</strong>
                        <div style="font-size: 0.75rem; color: #64748b; margin-top: 2px;">
                            <i class="fas fa-envelope" style="font-size: 10px;"></i>
                            ${user.email}
                        </div>
                    </div>
                </div>
            </td>
            <td>${rolBadge}</td>
            <td>${estadoBadge}</td>
            <td>
                <button class="btn btn-primary btn-icon" onclick="viewUser('${user.id}')" title="Ver detalles">
                    <i class="fas fa-eye"></i>
                </button>
            </td>
        </tr>
    `}).join('');
}

function viewUser(userId) {
    const user = usersData.find(u => u.id === userId);
    if (!user) return alert('Usuario no encontrado');
    
    alert(`👤 Usuario: ${user.nombre}\n📧 Email: ${user.email}\n🎭 Rol: ${user.rol}\n✓ Estado: ${user.estado}`);
}

// ===== USER HEADER UPDATE =====
// Función eliminada - se usa la definida en la línea 309

// ===== CHARTS =====
function initializeCharts() {
    initResourcesChart();
    initEvacuationsChart();
}

function initResourcesChart() {
    const ctx = document.getElementById('resourcesChart');
    if (!ctx) return;
    
    const labels = [];
    const data = [];
    const colors = ['#667eea', '#764ba2', '#10b981', '#f59e0b', '#ef4444'];
    
    const resourceTypes = {};
    Object.values(resourcesData).forEach(location => {
        Object.entries(location).forEach(([type, qty]) => {
            resourceTypes[type] = (resourceTypes[type] || 0) + qty;
        });
    });
    
    Object.entries(resourceTypes).forEach(([type, qty]) => {
        labels.push(type);
        data.push(qty);
    });
    
    resourcesChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors,
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

function initEvacuationsChart() {
    const ctx = document.getElementById('evacuationsChart');
    if (!ctx) return;
    
    const priorities = { Alta: 0, Media: 0, Baja: 0 };
    evacuationsData.forEach(evac => {
        if (evac.prioridad > 70) priorities.Alta++;
        else if (evac.prioridad > 40) priorities.Media++;
        else priorities.Baja++;
    });
    
    evacuationsChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Alta', 'Media', 'Baja'],
            datasets: [{
                label: 'Evacuaciones',
                data: [priorities.Alta, priorities.Media, priorities.Baja],
                backgroundColor: ['#ef4444', '#f59e0b', '#3b82f6'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// Función para actualizar el gráfico de evacuaciones
function updateEvacuationsChart() {
    if (!evacuationsChart) return;
    
    const priorities = { Alta: 0, Media: 0, Baja: 0 };
    evacuationsData.forEach(evac => {
        if (evac.prioridad > 70) priorities.Alta++;
        else if (evac.prioridad > 40) priorities.Media++;
        else priorities.Baja++;
    });
    
    evacuationsChart.data.datasets[0].data = [priorities.Alta, priorities.Media, priorities.Baja];
    evacuationsChart.update();
}

// Función para actualizar el gráfico de recursos
function updateResourcesChart() {
    if (!resourcesChart) return;
    
    const labels = [];
    const data = [];
    const colors = ['#667eea', '#764ba2', '#10b981', '#f59e0b', '#ef4444'];
    
    const resourceTypes = {};
    Object.values(resourcesData).forEach(location => {
        Object.entries(location).forEach(([type, qty]) => {
            resourceTypes[type] = (resourceTypes[type] || 0) + qty;
        });
    });
    
    Object.entries(resourceTypes).forEach(([type, qty]) => {
        labels.push(type);
        data.push(qty);
    });
    
    resourcesChart.data.labels = labels;
    resourcesChart.data.datasets[0].data = data;
    resourcesChart.update();
}

// ===== TAB NAVIGATION =====
function switchTab(tabName) {
    // Update tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.closest('.tab-btn').classList.add('active');
    
    // Update content sections
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(`${tabName}-tab`).classList.add('active');
    
    // Refresh map if dashboard
    if (tabName === 'dashboard' && mapInstance) {
        setTimeout(() => {
            mapInstance.invalidateSize();
            refreshMap();
        }, 100);
    }
}

// ===== MODAL FUNCTIONS =====
async function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
    
    // Si es el modal de rutas, cargar las zonas DIRECTAMENTE
    if (modalId === 'route-modal') {
        const debugStatus = document.getElementById('debug-status');
        const debugCount = document.getElementById('debug-count');
        const debugApi = document.getElementById('debug-api');
        const origenSelect = document.getElementById('routeOrigen');
        const destinoSelect = document.getElementById('routeDestino');
        
        // Actualizar estado de debug
        if (debugStatus) debugStatus.textContent = 'Cargando zonas...';
        if (debugApi) debugApi.textContent = 'Consultando /api/zones...';
        
        try {
            // Fetch DIRECTO sin funciones intermedias
            const response = await fetch('/api/zones');
            if (debugApi) debugApi.textContent = `HTTP ${response.status} ${response.ok ? '✅' : '❌'}`;
            
            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }
            
            const zones = await response.json();
            if (debugCount) debugCount.textContent = zones.length;
            if (debugStatus) debugStatus.textContent = `${zones.length} zonas recibidas`;
            
            // Validar que hay zonas
            if (!zones || zones.length === 0) {
                if (debugStatus) debugStatus.textContent = '⚠️ No hay zonas disponibles';
                if (origenSelect) origenSelect.innerHTML = '<option value="">⚠️ No hay zonas - Crea una primero</option>';
                if (destinoSelect) destinoSelect.innerHTML = '<option value="">⚠️ No hay zonas - Crea una primero</option>';
                return;
            }
            
            // INLINE: Poblar selectores DIRECTAMENTE sin funciones auxiliares
            let origenHTML = '<option value="">-- Seleccionar zona de origen --</option>';
            let destinoHTML = '<option value="">-- Seleccionar zona de destino --</option>';
            
            zones.forEach(zone => {
                const riesgo = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
                const option = `<option value="${zone.id}">${zone.nombre} (${riesgo}% riesgo)</option>`;
                origenHTML += option;
                destinoHTML += option;
            });
            
            if (origenSelect) origenSelect.innerHTML = origenHTML;
            if (destinoSelect) destinoSelect.innerHTML = destinoHTML;
            
            if (debugStatus) debugStatus.textContent = `✅ ${zones.length} zonas cargadas en selectores`;
            
            // Actualizar global también
            zonesData = zones.map(zone => ({
                ...zone,
                nivelDeRiesgo: zone.nivelDeRiesgo || zone.nivelRiesgo || 0
            }));
            
        } catch (error) {
            console.error('❌ Error cargando zonas:', error);
            if (debugStatus) debugStatus.textContent = `❌ Error: ${error.message}`;
            if (debugApi) debugApi.textContent = `❌ ${error.message}`;
            if (origenSelect) origenSelect.innerHTML = '<option value="">❌ Error al cargar zonas</option>';
            if (destinoSelect) destinoSelect.innerHTML = '<option value="">❌ Error al cargar zonas</option>';
        }
    }
    
    // Si es el modal de ruta óptima, también cargar zonas
    if (modalId === 'optimal-route-modal') {
        const statusSpan = document.getElementById('optimal-status');
        const countSpan = document.getElementById('optimal-count');
        const origenSelect = document.getElementById('optimalOrigen');
        const destinoSelect = document.getElementById('optimalDestino');
        const resultDiv = document.getElementById('optimal-result');
        
        // Ocultar resultado anterior
        if (resultDiv) resultDiv.style.display = 'none';
        
        if (statusSpan) statusSpan.textContent = 'Cargando zonas...';
        
        try {
            const response = await fetch('/api/zones');
            const zones = await response.json();
            
            if (countSpan) countSpan.textContent = zones.length;
            if (statusSpan) statusSpan.textContent = zones.length > 0 ? `✅ ${zones.length} zonas disponibles` : '⚠️ No hay zonas';
            
            if (!zones || zones.length === 0) {
                if (origenSelect) origenSelect.innerHTML = '<option value="">⚠️ No hay zonas disponibles</option>';
                if (destinoSelect) destinoSelect.innerHTML = '<option value="">⚠️ No hay zonas disponibles</option>';
                return;
            }
            
            // Poblar selectores
            let optionsHTML = '<option value="">-- Seleccionar zona --</option>';
            zones.forEach(zone => {
                const riesgo = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
                optionsHTML += `<option value="${zone.id}">${zone.nombre} (${riesgo}% riesgo)</option>`;
            });
            
            if (origenSelect) origenSelect.innerHTML = optionsHTML;
            if (destinoSelect) destinoSelect.innerHTML = optionsHTML;
            
            // Actualizar zonesData global
            zonesData = zones.map(zone => ({
                ...zone,
                nivelDeRiesgo: zone.nivelDeRiesgo || zone.nivelRiesgo || 0
            }));
            
        } catch (error) {
            console.error('❌ Error:', error);
            if (statusSpan) statusSpan.textContent = `❌ Error: ${error.message}`;
        }
    }
    
    // Si es el modal de recursos, cargar zonas y recursos actuales
    if (modalId === 'resource-modal') {
        const statusSpan = document.getElementById('resource-status');
        const countSpan = document.getElementById('resource-zones-count');
        const zonaSelect = document.getElementById('resourceZona');
        
        if (statusSpan) statusSpan.textContent = 'Cargando zonas y recursos...';
        
        try {
            // Cargar zonas
            const zonesResponse = await fetch('/api/zones');
            const zones = await zonesResponse.json();
            
            if (countSpan) countSpan.textContent = zones.length;
            if (statusSpan) statusSpan.textContent = zones.length > 0 ? `✅ ${zones.length} zonas disponibles` : '⚠️ No hay zonas';
            
            if (!zones || zones.length === 0) {
                if (zonaSelect) zonaSelect.innerHTML = '<option value="">⚠️ No hay zonas disponibles</option>';
                return;
            }
            
            // Poblar selector de zonas
            let optionsHTML = '<option value="">-- Seleccionar zona --</option>';
            zones.forEach(zone => {
                const riesgo = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
                optionsHTML += `<option value="${zone.id}" data-nombre="${zone.nombre}">${zone.nombre} (${riesgo}% riesgo)</option>`;
            });
            
            if (zonaSelect) zonaSelect.innerHTML = optionsHTML;
            
            // Actualizar zonesData global
            zonesData = zones.map(zone => ({
                ...zone,
                nivelDeRiesgo: zone.nivelDeRiesgo || zone.nivelRiesgo || 0
            }));
            
            // Cargar recursos actuales
            await loadResources();
            
            // Agregar listener para mostrar recursos de la zona seleccionada
            if (zonaSelect) {
                zonaSelect.addEventListener('change', showCurrentZoneResources);
            }
            
        } catch (error) {
            console.error('❌ Error:', error);
            if (statusSpan) statusSpan.textContent = `❌ Error: ${error.message}`;
        }
    }
    
    // Si es el modal de evacuaciones, cargar zonas
    if (modalId === 'evacuation-modal') {
        const statusSpan = document.getElementById('evacuation-status');
        const countSpan = document.getElementById('evacuation-zones-count');
        const zonaSelect = document.getElementById('evacuationZona');
        const zoneInfo = document.getElementById('zone-evacuation-info');
        
        if (zoneInfo) zoneInfo.style.display = 'none';
        if (statusSpan) statusSpan.textContent = 'Cargando zonas...';
        
        try {
            // Cargar zonas
            const zonesResponse = await fetch('/api/zones');
            const zones = await zonesResponse.json();
            
            if (countSpan) countSpan.textContent = zones.length;
            if (statusSpan) statusSpan.textContent = zones.length > 0 ? `✅ ${zones.length} zonas disponibles` : '⚠️ No hay zonas';
            
            if (!zones || zones.length === 0) {
                if (zonaSelect) zonaSelect.innerHTML = '<option value="">⚠️ No hay zonas disponibles</option>';
                return;
            }
            
            // Poblar selector de zonas ordenadas por riesgo (descendente)
            zones.sort((a, b) => {
                const riesgoA = a.nivelDeRiesgo || a.nivelRiesgo || 0;
                const riesgoB = b.nivelDeRiesgo || b.nivelRiesgo || 0;
                return riesgoB - riesgoA;
            });
            
            let optionsHTML = '<option value="">-- Seleccionar zona a evacuar --</option>';
            zones.forEach(zone => {
                const riesgo = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
                const poblacion = zone.poblacion || 0;
                const prioridadText = riesgo > 70 ? '🔴 ALTA' : riesgo > 40 ? '🟡 MEDIA' : '🟢 BAJA';
                optionsHTML += `<option value="${zone.id}" data-nombre="${zone.nombre}" data-riesgo="${riesgo}" data-poblacion="${poblacion}">${zone.nombre} (${riesgo}% - ${prioridadText})</option>`;
            });
            
            if (zonaSelect) {
                zonaSelect.innerHTML = optionsHTML;
                
                // Agregar listener para mostrar info de la zona
                zonaSelect.addEventListener('change', showEvacuationZoneInfo);
            }
            
            // Actualizar zonesData global
            zonesData = zones.map(zone => ({
                ...zone,
                nivelDeRiesgo: zone.nivelDeRiesgo || zone.nivelRiesgo || 0
            }));
            
        } catch (error) {
            console.error('❌ Error:', error);
            if (statusSpan) statusSpan.textContent = `❌ Error: ${error.message}`;
        }
    }
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

async function createRoute(event) {
    event.preventDefault();
    
    const origenId = document.getElementById('routeOrigen').value;
    const destinoId = document.getElementById('routeDestino').value;
    const distancia = parseFloat(document.getElementById('routeDistancia').value);
    const tiempo = parseFloat(document.getElementById('routeTiempo').value);
    const capacidad = parseInt(document.getElementById('routeCapacidad').value);
    const disponible = document.getElementById('routeDisponible').value === 'true';
    
    if (!origenId || !destinoId) {
        showToast('warning', 'Campos Incompletos', 'Debes seleccionar zona de origen y destino');
        return;
    }
    
    if (origenId === destinoId) {
        showToast('warning', 'Error de Validación', 'El origen y destino no pueden ser la misma zona');
        return;
    }
    
    // Obtener nombres de las zonas para mostrar mensaje
    const zonaOrigen = zonesData.find(z => z.id === origenId);
    const zonaDestino = zonesData.find(z => z.id === destinoId);
    
    try {
        console.log('🛣️ Creando nueva ruta:', {
            origenId,
            destinoId,
            nombreOrigen: zonaOrigen?.nombre,
            nombreDestino: zonaDestino?.nombre,
            distancia,
            tiempo,
            capacidad
        });
        
        const response = await fetch('/api/routes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                origenId,
                destinoId,
                distancia,
                tiempo,
                capacidad
            })
        });
        
        const data = await response.json();
        console.log('📥 Respuesta del servidor:', data);
        
        if (data.ok || response.ok) {
            const origen = zonaOrigen?.nombre || origenId;
            const destino = zonaDestino?.nombre || destinoId;
            showToast('success', 'Ruta Creada', `${origen} → ${destino} | ${distancia} km | ${tiempo}h | ${capacidad} personas`);
            closeModal('route-modal');
            
            // Limpiar formulario
            document.getElementById('newRouteForm').reset();
            
            // Recargar rutas
            await loadRoutes();
            updateStats();
            updateStatTrends();
        } else {
            showToast('error', 'Error al Crear Ruta', data.msg || data.message || 'No se pudo crear la ruta');
        }
    } catch (error) {
        console.error('❌ Error creando ruta:', error);
        showToast('error', 'Error de Conexión', 'No se pudo conectar con el servidor. Verifica que esté funcionando.');
    }
}

// ===== ACTION FUNCTIONS =====
function viewZone(id) {
    const zone = zonesData.find(z => z.id === id);
    if (!zone) {
        showToast('error', 'Error', 'Zona no encontrada');
        return;
    }
    
    const lat = zone.lat || (zone.coordenadas && zone.coordenadas.lat);
    const lng = zone.lng || (zone.coordenadas && zone.coordenadas.lng);
    const nivelRiesgo = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
    const riskColor = nivelRiesgo > 70 ? '#ef4444' : nivelRiesgo > 40 ? '#f59e0b' : '#10b981';
    const riskText = nivelRiesgo > 70 ? '🔴 CRÍTICO' : nivelRiesgo > 40 ? '🟡 MEDIO' : '🟢 BAJO';
    
    // Obtener recursos disponibles en la zona
    const recursos = resourcesData[zone.nombre] || {};
    const recursosHTML = Object.entries(recursos).length > 0 ?
        Object.entries(recursos)
            .filter(([tipo, cant]) => cant > 0)
            .map(([tipo, cant]) => {
                const icon = tipo === 'ALIMENTO' ? '🍞' : tipo === 'AGUA' ? '💧' : tipo === 'MEDICINA' ? '💊' : '📦';
                const badge = cant > 100 ? 'badge-success' : cant > 50 ? 'badge-warning' : 'badge-danger';
                return `
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem; background: #f8fafc; border-radius: 8px; margin-bottom: 0.5rem;">
                        <span style="font-weight: 600;">${icon} ${tipo}</span>
                        <span class="badge ${badge}" style="font-size: 0.9rem;">${cant.toLocaleString()} unidades</span>
                    </div>
                `;
            }).join('') :
        '<div style="padding: 1rem; background: #fef2f2; border-radius: 8px; color: #991b1b; text-align: center;">⚠️ Sin recursos disponibles</div>';
    
    // Obtener equipos asignados
    const equiposAsignados = zone.equiposAsignados || [];
    const equiposHTML = equiposAsignados.length > 0 ?
        equiposAsignados.map((equipo, idx) => {
            const tipoIcons = {
                'MEDICO': '🏥',
                'BOMBERO': '🚒',
                'POLICIA': '👮',
                'VOLUNTARIO': '🤝'
            };
            return `
                <div style="padding: 0.75rem; background: #f0f9ff; border-left: 4px solid #3b82f6; border-radius: 8px; margin-bottom: 0.5rem;">
                    <div style="font-weight: 600; margin-bottom: 0.25rem;">
                        ${tipoIcons[equipo.tipo] || '👥'} ${equipo.tipo}
                    </div>
                    <div style="font-size: 0.85rem; color: #64748b;">
                        <span style="margin-right: 1rem;">👤 ${equipo.miembros} miembros</span>
                        <span>🎯 ${equipo.especialidades.join(', ')}</span>
                    </div>
                </div>
            `;
        }).join('') :
        '<div style="padding: 1rem; background: #fffbeb; border-radius: 8px; color: #92400e; text-align: center;">⚠️ Sin equipos de rescate asignados</div>';
    
    // Encontrar rutas conectadas
    const rutasConectadas = routesData.filter(r => 
        r.origenId === zone.id || r.destinoId === zone.id ||
        r.nombreOrigen === zone.nombre || r.nombreDestino === zone.nombre
    );
    
    const rutasHTML = rutasConectadas.length > 0 ?
        rutasConectadas.map(ruta => {
            const esOrigen = ruta.origenId === zone.id || ruta.nombreOrigen === zone.nombre;
            const otraZona = esOrigen ? (ruta.nombreDestino || ruta.destinoId) : (ruta.nombreOrigen || ruta.origenId);
            const direccion = esOrigen ? '→' : '←';
            const statusBadge = ruta.disponible ? 
                '<span class="badge badge-success" style="font-size: 0.75rem;">✅ Disponible</span>' :
                '<span class="badge badge-danger" style="font-size: 0.75rem;">❌ Bloqueada</span>';
            
            return `
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem; background: #f8fafc; border-radius: 8px; margin-bottom: 0.5rem;">
                    <div>
                        <span style="font-weight: 600;">${zone.nombre} ${direccion} ${otraZona}</span>
                        <div style="font-size: 0.8rem; color: #64748b; margin-top: 0.25rem;">
                            📏 ${ruta.distancia} km • ⏱️ ${ruta.tiempoEstimado} hrs
                        </div>
                    </div>
                    ${statusBadge}
                </div>
            `;
        }).join('') :
        '<div style="padding: 1rem; background: #fef2f2; border-radius: 8px; color: #991b1b; text-align: center;">⚠️ Sin rutas conectadas</div>';
    
    const content = `
        <div style="padding: 0.5rem;">
            <!-- Información básica -->
            <div style="background: linear-gradient(135deg, ${riskColor}15, ${riskColor}05); border-left: 4px solid ${riskColor}; padding: 1.5rem; border-radius: 12px; margin-bottom: 1.5rem;">
                <h3 style="margin: 0 0 1rem 0; color: #0f172a; font-size: 1.5rem; display: flex; align-items: center; gap: 0.75rem;">
                    📍 ${zone.nombre}
                    <span class="badge" style="background: ${riskColor}; color: white; font-size: 0.85rem;">${riskText}</span>
                </h3>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
                    <div style="background: white; padding: 1rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                        <div style="color: #64748b; font-size: 0.85rem; margin-bottom: 0.25rem;">👥 Población</div>
                        <div style="font-size: 1.5rem; font-weight: 700; color: #667eea;">${zone.poblacion.toLocaleString()}</div>
                    </div>
                    <div style="background: white; padding: 1rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                        <div style="color: #64748b; font-size: 0.85rem; margin-bottom: 0.25rem;">⚠️ Nivel de Riesgo</div>
                        <div style="font-size: 1.5rem; font-weight: 700; color: ${riskColor};">${nivelRiesgo}%</div>
                    </div>
                </div>
                ${lat && lng ? `
                    <div style="margin-top: 1rem; padding: 0.75rem; background: white; border-radius: 8px; font-size: 0.85rem;">
                        <span style="color: #64748b;">📍 Coordenadas:</span>
                        <code style="margin-left: 0.5rem; background: #f1f5f9; padding: 4px 8px; border-radius: 4px; color: #667eea;">${lat.toFixed(4)}, ${lng.toFixed(4)}</code>
                        <button onclick="centerMapOnZone('${zone.id}')" class="btn btn-secondary" style="margin-left: 1rem; padding: 4px 12px; font-size: 0.8rem;">
                            <i class="fas fa-map-marked-alt"></i> Ver en mapa
                        </button>
                    </div>
                ` : ''}
            </div>
            
            <!-- Recursos -->
            <div style="margin-bottom: 1.5rem;">
                <h4 style="margin: 0 0 1rem 0; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                    <i class="fas fa-boxes" style="color: #667eea;"></i> Recursos Disponibles
                </h4>
                ${recursosHTML}
            </div>
            
            <!-- Equipos de rescate -->
            <div style="margin-bottom: 1.5rem;">
                <h4 style="margin: 0 0 1rem 0; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                    <i class="fas fa-users-cog" style="color: #667eea;"></i> Equipos de Rescate (${equiposAsignados.length})
                </h4>
                ${equiposHTML}
            </div>
            
            <!-- Rutas conectadas -->
            <div>
                <h4 style="margin: 0 0 1rem 0; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                    <i class="fas fa-route" style="color: #667eea;"></i> Rutas Conectadas (${rutasConectadas.length})
                </h4>
                ${rutasHTML}
            </div>
        </div>
    `;
    
    document.getElementById('viewZoneContent').innerHTML = content;
    openModal('view-zone-modal');
}

// Función auxiliar para centrar mapa en zona
function centerMapOnZone(zoneId) {
    const zone = zonesData.find(z => z.id === zoneId);
    if (!zone) return;
    
    const lat = zone.lat || (zone.coordenadas && zone.coordenadas.lat);
    const lng = zone.lng || (zone.coordenadas && zone.coordenadas.lng);
    
    if (lat && lng && mapInstance) {
        mapInstance.setView([lat, lng], 13);
        switchTab('dashboard');
        closeModal('view-zone-modal');
        showToast('success', 'Mapa Actualizado', `Centrando en ${zone.nombre}`);
    }
}

function editZone(id) {
    const zone = zonesData.find(z => z.id === id);
    if (!zone) {
        alert('❌ Zona no encontrada');
        return;
    }
    
    // Cargar datos en el formulario
    document.getElementById('editZoneId').value = zone.id;
    document.getElementById('editZoneNombre').value = zone.nombre;
    document.getElementById('editZonePoblacion').value = zone.poblacion;
    document.getElementById('editZoneRiesgo').value = zone.nivelDeRiesgo || zone.nivelRiesgo || 0;
    document.getElementById('editZoneLat').value = zone.lat || 0;
    document.getElementById('editZoneLng').value = zone.lng || 0;
    
    // Abrir modal
    openModal('edit-zone-modal');
}

async function saveEditedZone(event) {
    event.preventDefault();
    
    const zoneId = document.getElementById('editZoneId').value;
    const nombre = document.getElementById('editZoneNombre').value;
    const poblacion = parseInt(document.getElementById('editZonePoblacion').value);
    const nivelDeRiesgo = parseInt(document.getElementById('editZoneRiesgo').value);
    const lat = parseFloat(document.getElementById('editZoneLat').value);
    const lng = parseFloat(document.getElementById('editZoneLng').value);
    
    try {
        const response = await fetch('/api/zones', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: zoneId,
                nombre: nombre,
                poblacion: poblacion,
                nivelDeRiesgo: nivelDeRiesgo,
                lat: lat,
                lng: lng
            })
        });
        
        if (!response.ok) {
            throw new Error('Error al actualizar la zona');
        }
        
        // Recargar datos
        await loadZones();
        updateStats();
        updateStatTrends();
        
        closeModal('edit-zone-modal');
        showToast('success', 'Zona Actualizada', 'Los datos de la zona se actualizaron correctamente');
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Error al Actualizar', 'No se pudo actualizar la zona: ' + error.message);
    }
}

function deleteZone(id) {
    console.log('Eliminar zona:', id);
    // Aquí iría la llamada al API para eliminar
    alert(`🗑️ Función de eliminación en desarrollo para: ${id}`);
}

function viewRoute(id) {
    const route = routesData.find(r => r.id === id || (r.origenId + '-' + r.destinoId) === id);
    if (!route) {
        showToast('error', 'Error', 'Ruta no encontrada');
        return;
    }
    
    const origen = route.nombreOrigen || route.origen || route.origenId || 'N/A';
    const destino = route.nombreDestino || route.destino || route.destinoId || 'N/A';
    const distancia = parseFloat(route.distancia) || 0;
    const tiempo = parseFloat(route.tiempoEstimado) || 0;
    const capacidad = route.capacidadPersonas || 0;
    const disponible = route.disponible !== undefined ? route.disponible : true;
    
    // Obtener información de las zonas
    const zonaOrigen = zonesData.find(z => z.id === route.origenId || z.nombre === origen);
    const zonaDestino = zonesData.find(z => z.id === route.destinoId || z.nombre === destino);
    
    const statusColor = disponible ? '#10b981' : '#ef4444';
    const statusText = disponible ? '✅ DISPONIBLE' : '❌ BLOQUEADA';
    const statusBg = disponible ? '#f0fdf4' : '#fef2f2';
    
    // Calcular velocidad promedio
    const velocidadPromedio = tiempo > 0 ? (distancia / tiempo).toFixed(1) : 'N/A';
    
    // Información de zonas
    const zonaOrigenHTML = zonaOrigen ? `
        <div style="padding: 1rem; background: #f0f9ff; border-radius: 8px; border-left: 4px solid #3b82f6;">
            <div style="font-weight: 600; color: #0c4a6e; margin-bottom: 0.5rem;">📍 ${zonaOrigen.nombre}</div>
            <div style="font-size: 0.85rem; color: #64748b;">
                <div>👥 Población: <strong>${zonaOrigen.poblacion.toLocaleString()}</strong></div>
                <div>⚠️ Riesgo: <strong style="color: ${zonaOrigen.nivelDeRiesgo > 70 ? '#ef4444' : zonaOrigen.nivelDeRiesgo > 40 ? '#f59e0b' : '#10b981'}">${zonaOrigen.nivelDeRiesgo}%</strong></div>
            </div>
        </div>
    ` : `<div style="padding: 1rem; background: #f8fafc; border-radius: 8px; color: #64748b;">📍 ${origen}</div>`;
    
    const zonaDestinoHTML = zonaDestino ? `
        <div style="padding: 1rem; background: #fef3c7; border-radius: 8px; border-left: 4px solid #f59e0b;">
            <div style="font-weight: 600; color: #92400e; margin-bottom: 0.5rem;">🎯 ${zonaDestino.nombre}</div>
            <div style="font-size: 0.85rem; color: #64748b;">
                <div>👥 Población: <strong>${zonaDestino.poblacion.toLocaleString()}</strong></div>
                <div>⚠️ Riesgo: <strong style="color: ${zonaDestino.nivelDeRiesgo > 70 ? '#ef4444' : zonaDestino.nivelDeRiesgo > 40 ? '#f59e0b' : '#10b981'}">${zonaDestino.nivelDeRiesgo}%</strong></div>
            </div>
        </div>
    ` : `<div style="padding: 1rem; background: #f8fafc; border-radius: 8px; color: #64748b;">🎯 ${destino}</div>`;
    
    // Rutas alternativas
    const rutasAlternativas = routesData.filter(r => 
        r.id !== route.id &&
        ((r.origenId === route.origenId || r.nombreOrigen === origen) &&
         (r.destinoId === route.destinoId || r.nombreDestino === destino))
    );
    
    const alternativasHTML = rutasAlternativas.length > 0 ? `
        <div style="margin-top: 1.5rem;">
            <h4 style="margin: 0 0 1rem 0; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                <i class="fas fa-random" style="color: #667eea;"></i> Rutas Alternativas (${rutasAlternativas.length})
            </h4>
            ${rutasAlternativas.map(r => `
                <div style="padding: 0.75rem; background: #f8fafc; border-radius: 8px; margin-bottom: 0.5rem; display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <span style="font-weight: 600;">${r.nombreOrigen || r.origenId} → ${r.nombreDestino || r.destinoId}</span>
                        <div style="font-size: 0.8rem; color: #64748b; margin-top: 0.25rem;">
                            📏 ${r.distancia} km • ⏱️ ${r.tiempoEstimado} hrs
                        </div>
                    </div>
                    <span class="badge ${r.disponible ? 'badge-success' : 'badge-danger'}" style="font-size: 0.75rem;">
                        ${r.disponible ? '✅ Disponible' : '❌ Bloqueada'}
                    </span>
                </div>
            `).join('')}
        </div>
    ` : '';
    
    const content = `
        <div style="padding: 0.5rem;">
            <!-- Estado de la ruta -->
            <div style="background: ${statusBg}; border-left: 4px solid ${statusColor}; padding: 1.5rem; border-radius: 12px; margin-bottom: 1.5rem; text-align: center;">
                <div style="font-size: 1.75rem; font-weight: 700; color: ${statusColor}; margin-bottom: 0.5rem;">
                    ${statusText}
                </div>
                <div style="font-size: 1rem; color: #64748b;">
                    Esta ruta ${disponible ? 'está operativa y puede ser utilizada' : 'no está disponible actualmente'}
                </div>
            </div>
            
            <!-- Origen y Destino -->
            <div style="margin-bottom: 1.5rem;">
                <h4 style="margin: 0 0 1rem 0; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                    <i class="fas fa-map-signs" style="color: #667eea;"></i> Recorrido
                </h4>
                <div style="display: grid; grid-template-columns: 1fr auto 1fr; gap: 1rem; align-items: center;">
                    ${zonaOrigenHTML}
                    <div style="text-align: center; color: #667eea; font-size: 1.5rem;">→</div>
                    ${zonaDestinoHTML}
                </div>
            </div>
            
            <!-- Estadísticas -->
            <div style="margin-bottom: 1.5rem;">
                <h4 style="margin: 0 0 1rem 0; color: #0f172a; display: flex; align-items: center; gap: 0.5rem;">
                    <i class="fas fa-chart-line" style="color: #667eea;"></i> Estadísticas de la Ruta
                </h4>
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
                    <div style="background: white; padding: 1.25rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); text-align: center;">
                        <div style="color: #64748b; font-size: 0.85rem; margin-bottom: 0.5rem;">📏 Distancia</div>
                        <div style="font-size: 1.75rem; font-weight: 700; color: #667eea;">${distancia.toFixed(1)}</div>
                        <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.25rem;">kilómetros</div>
                    </div>
                    <div style="background: white; padding: 1.25rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); text-align: center;">
                        <div style="color: #64748b; font-size: 0.85rem; margin-bottom: 0.5rem;">⏱️ Tiempo Estimado</div>
                        <div style="font-size: 1.75rem; font-weight: 700; color: #f59e0b;">${tiempo.toFixed(1)}</div>
                        <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.25rem;">horas</div>
                    </div>
                    <div style="background: white; padding: 1.25rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); text-align: center;">
                        <div style="color: #64748b; font-size: 0.85rem; margin-bottom: 0.5rem;">👥 Capacidad</div>
                        <div style="font-size: 1.75rem; font-weight: 700; color: #10b981;">${capacidad}</div>
                        <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.25rem;">personas</div>
                    </div>
                    <div style="background: white; padding: 1.25rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); text-align: center;">
                        <div style="color: #64748b; font-size: 0.85rem; margin-bottom: 0.5rem;">🚗 Velocidad Promedio</div>
                        <div style="font-size: 1.75rem; font-weight: 700; color: #8b5cf6;">${velocidadPromedio}</div>
                        <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.25rem;">km/h</div>
                    </div>
                </div>
            </div>
            
            ${alternativasHTML}
            
            <!-- Botón para ver en mapa -->
            ${route.coordenadasOrigen && route.coordenadasDestino ? `
                <div style="margin-top: 1.5rem; text-align: center;">
                    <button onclick="centerMapOnRoute('${route.id || (route.origenId + '-' + route.destinoId)}')" class="btn btn-primary" style="padding: 0.75rem 2rem;">
                        <i class="fas fa-map-marked-alt"></i> Ver Ruta en el Mapa
                    </button>
                </div>
            ` : ''}
        </div>
    `;
    
    document.getElementById('viewRouteContent').innerHTML = content;
    openModal('view-route-modal');
}

// Función auxiliar para centrar mapa en ruta
function centerMapOnRoute(routeId) {
    const route = routesData.find(r => r.id === routeId || (r.origenId + '-' + r.destinoId) === routeId);
    if (!route) return;
    
    if (route.coordenadasOrigen && route.coordenadasDestino && mapInstance) {
        const bounds = L.latLngBounds(
            [route.coordenadasOrigen.lat, route.coordenadasOrigen.lng],
            [route.coordenadasDestino.lat, route.coordenadasDestino.lng]
        );
        mapInstance.fitBounds(bounds, { padding: [50, 50] });
        switchTab('dashboard');
        closeModal('view-route-modal');
        showToast('success', 'Mapa Actualizado', 'Mostrando ruta en el mapa');
    }
}

function editRoute(id) {
    const route = routesData.find(r => r.id === id || `${r.origenId}-${r.destinoId}` === id);
    if (!route) {
        alert('❌ Ruta no encontrada');
        return;
    }
    
    // Cargar datos en el formulario
    document.getElementById('editRouteId').value = route.id || `${route.origenId}-${route.destinoId}`;
    document.getElementById('editRouteOrigen').value = route.nombreOrigen || route.origen || route.origenId;
    document.getElementById('editRouteDestino').value = route.nombreDestino || route.destino || route.destinoId;
    document.getElementById('editRouteDistancia').value = route.distancia || 0;
    document.getElementById('editRouteTiempo').value = route.tiempo || 0;
    document.getElementById('editRouteCapacidad').value = route.capacidad || 100;
    document.getElementById('editRouteDisponible').value = route.disponible ? 'true' : 'false';
    
    // Abrir modal
    openModal('edit-route-modal');
}

async function saveEditedRoute(event) {
    event.preventDefault();
    
    const routeId = document.getElementById('editRouteId').value;
    const distancia = parseFloat(document.getElementById('editRouteDistancia').value);
    const tiempo = parseFloat(document.getElementById('editRouteTiempo').value);
    const capacidad = parseInt(document.getElementById('editRouteCapacidad').value);
    const disponible = document.getElementById('editRouteDisponible').value === 'true';
    
    try {
        const response = await fetch('/api/routes', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: routeId,
                distancia: distancia,
                tiempo: tiempo,
                capacidad: capacidad,
                disponible: disponible
            })
        });
        
        if (!response.ok) {
            throw new Error('Error al actualizar la ruta');
        }
        
        // Recargar datos
        await loadRoutes();
        updateStats();
        updateStatTrends();
        
        closeModal('edit-route-modal');
        showToast('success', 'Ruta Actualizada', 'Los datos de la ruta se actualizaron correctamente');
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Error al Actualizar', 'No se pudo actualizar la ruta: ' + error.message);
    }
}

function transferResource(location, type) {
    console.log('🚀 transferResource LLAMADA - Parámetros:', { location, type });
    console.log('📦 resourcesData completo:', resourcesData);
    
    // Encontrar el recurso
    if (!resourcesData[location]) {
        console.error('❌ Location no encontrada:', location);
        console.log('📍 Locations disponibles:', Object.keys(resourcesData));
        showToast('error', 'Error', `Ubicación no encontrada: ${location}`);
        return;
    }
    
    if (!resourcesData[location][type]) {
        console.error('❌ Tipo no encontrado:', type);
        console.log('📦 Tipos disponibles en', location, ':', Object.keys(resourcesData[location]));
        showToast('error', 'Error', `Tipo de recurso no encontrado: ${type}`);
        return;
    }
    
    const available = resourcesData[location][type];
    console.log('✅ Recurso encontrado. Disponible:', available);
    
    // Cargar datos en el modal
    document.getElementById('transferSourceLocation').value = location;
    document.getElementById('transferResourceType').value = type;
    document.getElementById('transferSourceName').textContent = location;
    document.getElementById('transferTypeName').textContent = type;
    document.getElementById('transferAvailable').textContent = available;
    document.getElementById('transferQuantity').max = available;
    document.getElementById('transferQuantity').value = '';
    
    console.log('📝 Datos cargados en modal');
    
    // Cargar zonas disponibles (excluyendo la actual)
    const destinationSelect = document.getElementById('transferDestination');
    destinationSelect.innerHTML = '<option value="">Seleccione una zona...</option>';
    
    // Obtener todas las zonas únicas de resourcesData
    const allLocations = Object.keys(resourcesData).filter(loc => loc !== location);
    
    allLocations.forEach(loc => {
        const option = document.createElement('option');
        option.value = loc;
        option.textContent = loc;
        destinationSelect.appendChild(option);
    });
    
    console.log('🎯 Zonas de destino cargadas:', allLocations.length);
    console.log('🚪 Abriendo modal transfer-resource-modal...');
    
    // Abrir modal
    openModal('transfer-resource-modal');
    
    console.log('✅ Modal abierto');
}

// Función global para abrir modal de transferencia
window.openTransferModal = function(index) {
    console.log('🔵 openTransferModal llamada con index:', index);
    const info = window.transferResourcesInfo[index];
    
    if (!info) {
        console.error('❌ No se encontró info para index:', index);
        showToast('error', 'Error', 'No se pudo cargar la información del recurso');
        return;
    }
    
    console.log('📦 Info del recurso:', info);
    transferResource(info.location, info.type);
}

async function saveTransferResource(event) {
    event.preventDefault();
    
    const sourceLocation = document.getElementById('transferSourceLocation').value;
    const resourceType = document.getElementById('transferResourceType').value;
    const destination = document.getElementById('transferDestination').value;
    const quantity = parseInt(document.getElementById('transferQuantity').value);
    
    console.log('🔄 DEBUG saveTransferResource - Iniciando transferencia:');
    console.log('   sourceLocation:', sourceLocation);
    console.log('   resourceType:', resourceType);
    console.log('   destination:', destination);
    console.log('   quantity:', quantity);
    
    const available = resourcesData[sourceLocation][resourceType];
    
    // Validar cantidad
    if (quantity > available) {
        showToast('warning', 'Cantidad No Disponible', `Máximo disponible: ${available} unidades`);
        return;
    }
    
    if (quantity <= 0) {
        showToast('warning', 'Cantidad Inválida', 'La cantidad debe ser mayor a 0');
        return;
    }
    
    // Mapeo de tipos de recursos al enum del backend
    const tipoEnumMap = {
        'ALIMENTO': 'ALIMENTO',
        'ALIMENTOS': 'ALIMENTO',
        'MEDICINA': 'MEDICINA',
        'MEDICINAS': 'MEDICINA',
        'AGUA': 'AGUA',
        'AGUAS': 'AGUA'
    };
    
    const tipoNormalizado = tipoEnumMap[resourceType.toUpperCase()] || resourceType.toUpperCase();
    console.log('   tipo normalizado:', tipoNormalizado);
    
    try {
        const response = await fetch('/api/resources/transfer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                origen: sourceLocation,
                destino: destination,
                tipo: tipoNormalizado,
                cantidad: quantity
            })
        });
        
        const data = await response.json();
        console.log('📥 Respuesta del servidor:', data);
        
        if (!response.ok || !data.ok) {
            throw new Error(data.msg || 'Error al transferir recursos');
        }
        
        // Recargar datos
        await loadResources();
        updateStats();
        updateStatTrends();
        if (resourcesChart) updateResourcesChart();
        
        closeModal('transfer-resource-modal');
        showToast('success', 'Transferencia Exitosa', `${quantity} unidades de ${resourceType} transferidas de ${sourceLocation} a ${destination}`);
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Error en Transferencia', error.message);
    }
}

async function processEvacuation(idx) {
    const evac = evacuationsData[idx];
    if (!evac) {
        alert('❌ Evacuación no encontrada');
        return;
    }
    
    // Verificar si ya está completada
    if (evac.estado === 'COMPLETADA') {
        alert('✅ Esta evacuación ya ha sido procesada');
        return;
    }
    
    const zonaNombre = evac.zonaNombre || evac.nombre || evac.zonaOrigen || 'Zona desconocida';
    const personas = evac.personas || evac.poblacion || evac.numeroPersonas || 0;
    const prioridad = evac.prioridad || evac.nivelDeRiesgo || 0;
    const evacuacionId = evac.id;
    
    // Confirmar procesamiento
    const confirmar = confirm(
        `🚑 ¿Procesar esta evacuación?\n\n` +
        `📍 Zona: ${zonaNombre}\n` +
        `👥 Personas: ${personas.toLocaleString()}\n` +
        `⚠️ Prioridad: ${prioridad}% (${prioridad > 70 ? 'ALTA 🔴' : prioridad > 40 ? 'MEDIA 🟡' : 'BAJA 🟢'})\n` +
        `📊 Posición en cola: #${idx + 1}\n\n` +
        `Esta acción procesará la evacuación y la marcará como completada.`
    );
    
    if (!confirmar) return;
    
    try {
        console.log('🚑 Procesando evacuación:', evacuacionId);
        
        const response = await fetch('/api/evacuations', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                accion: 'procesar',
                evacuacionId: evacuacionId
            })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.msg || 'Error al procesar evacuación');
        }
        
        console.log('✅ Evacuación procesada:', data);
        
        // Recargar evacuaciones
        await loadEvacuations();
        
        // Actualizar estadísticas
        updateStats();
        
        // Mensaje de éxito
        alert(
            `✅ Evacuación procesada exitosamente!\n\n` +
            `📍 Zona: ${data.zona}\n` +
            `👥 Personas evacuadas: ${data.personas.toLocaleString()}\n` +
            `📊 Estado: ${data.estado}\n\n` +
            `💡 La evacuación ha sido completada y removida de la cola de prioridad.`
        );
        
    } catch (error) {
        console.error('❌ Error procesando evacuación:', error);
        alert(`❌ Error al procesar evacuación:\n\n${error.message}`);
    }
}

// ===== LOGOUT =====
async function logout() {
    try {
        // Limpiar estado local primero
        currentUser = { email: '', nombre: '', rol: '' };
        localStorage.removeItem('user');
        sessionStorage.removeItem('user');
        
        // Llamar al servidor para invalidar la sesión
        await fetch('/logout', { method: 'POST' });
        
        // Redirigir a la página de login correcta usando replace
        window.location.replace('login-new.html');
    } catch (error) {
        console.error('Error durante logout:', error);
        // Aún si hay error, limpiar y redirigir
        currentUser = { email: '', nombre: '', rol: '' };
        localStorage.removeItem('user');
        sessionStorage.removeItem('user');
        window.location.replace('login-new.html');
    }
}

// ===== CALCULAR RUTA ÓPTIMA =====
async function calculateOptimalRoute(event) {
    event.preventDefault();
    
    const origenId = document.getElementById('optimalOrigen').value;
    const destinoId = document.getElementById('optimalDestino').value;
    const statusSpan = document.getElementById('optimal-status');
    const resultDiv = document.getElementById('optimal-result');
    
    // Validaciones
    if (!origenId || !destinoId) {
        alert('❌ Debes seleccionar zona de origen y destino');
        return;
    }
    
    if (origenId === destinoId) {
        alert('❌ La zona de origen y destino deben ser diferentes');
        return;
    }
    
    // Mostrar estado de carga
    if (statusSpan) statusSpan.textContent = '🔄 Calculando ruta óptima con Dijkstra...';
    if (resultDiv) resultDiv.style.display = 'none';
    
    try {
        // Llamar al API de ruta óptima
        const response = await fetch(`/api/optimal-route?origen=${origenId}&destino=${destinoId}`);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || 'Error al calcular ruta');
        }
        
        console.log('✅ Ruta óptima calculada:', data);
        
        // Actualizar estado
        if (statusSpan) statusSpan.textContent = '✅ Ruta óptima calculada exitosamente';
        
        // Mostrar resultados
        document.getElementById('result-distancia').textContent = data.distanciaTotal;
        document.getElementById('result-tiempo').textContent = data.tiempoTotal;
        document.getElementById('result-capacidad').textContent = data.capacidadMinima;
        document.getElementById('result-segmentos').textContent = data.numeroSegmentos;
        
        // Construir el camino
        const pathDiv = document.getElementById('result-path');
        let pathHTML = `<strong>${data.origenNombre}</strong>`;
        
        if (data.segmentos && data.segmentos.length > 0) {
            data.segmentos.forEach(seg => {
                pathHTML += ` → <strong>${seg.destinoNombre}</strong>`;
            });
        } else {
            pathHTML += ` → <strong>${data.destinoNombre}</strong>`;
        }
        
        pathDiv.innerHTML = pathHTML;
        
        // Mostrar detalles de segmentos
        const segmentsDiv = document.getElementById('result-segments');
        if (data.segmentos && data.segmentos.length > 0) {
            let segmentsHTML = '';
            data.segmentos.forEach((seg, index) => {
                segmentsHTML += `
                    <div style="background: rgba(255,255,255,0.15); padding: 0.75rem; border-radius: 6px; display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <strong>${index + 1}.</strong> ${seg.origenNombre} → ${seg.destinoNombre}
                        </div>
                        <div style="display: flex; gap: 1rem; font-size: 0.85rem;">
                            <span>📏 ${seg.distancia} km</span>
                            <span>⏱️ ${seg.tiempo} hrs</span>
                            <span>👥 ${seg.capacidad} pers.</span>
                        </div>
                    </div>
                `;
            });
            segmentsDiv.innerHTML = segmentsHTML;
        } else {
            segmentsDiv.innerHTML = '<p style="opacity: 0.8;">Ruta directa sin segmentos intermedios</p>';
        }
        
        // Mostrar el div de resultados
        if (resultDiv) {
            resultDiv.style.display = 'block';
            resultDiv.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }
        
        // Mensaje de éxito
        alert(`✅ Ruta óptima calculada!\n\n📏 Distancia: ${data.distanciaTotal} km\n⏱️ Tiempo: ${data.tiempoTotal} hrs\n👥 Capacidad mínima: ${data.capacidadMinima} personas\n🔗 Segmentos: ${data.numeroSegmentos}`);
        
    } catch (error) {
        console.error('❌ Error calculando ruta óptima:', error);
        
        if (statusSpan) statusSpan.textContent = `❌ Error: ${error.message}`;
        
        alert(`❌ Error al calcular ruta óptima:\n\n${error.message}\n\nAsegúrate de que existan rutas conectando las zonas seleccionadas.`);
        
        if (resultDiv) resultDiv.style.display = 'none';
    }
}

// ===== FUNCIONES DE RECURSOS =====

// Función para mostrar recursos actuales de la zona seleccionada
function showCurrentZoneResources() {
    const zonaSelect = document.getElementById('resourceZona');
    const selectedOption = zonaSelect.options[zonaSelect.selectedIndex];
    const zonaNombre = selectedOption.getAttribute('data-nombre');
    
    const currentResourcesDiv = document.getElementById('current-resources');
    const resourcesList = document.getElementById('current-resources-list');
    
    if (!zonaNombre || zonaNombre === '') {
        if (currentResourcesDiv) currentResourcesDiv.style.display = 'none';
        return;
    }
    
    // Buscar recursos de esta zona en resourcesData
    const zoneResources = resourcesData[zonaNombre] || {};
    
    if (Object.keys(zoneResources).length === 0) {
        if (resourcesList) {
            resourcesList.innerHTML = '<span style="color: var(--text-secondary); font-size: 0.875rem;">No hay recursos asignados a esta zona</span>';
        }
        if (currentResourcesDiv) currentResourcesDiv.style.display = 'block';
        return;
    }
    
    // Mostrar recursos
    const resourceIcons = {
        'ALIMENTO': '🍞',
        'MEDICINA': '💊',
        'AGUA': '💧',
        'COMBUSTIBLE': '⛽',
        'EQUIPO': '🔧'
    };
    
    let resourcesHTML = '';
    for (const [tipo, cantidad] of Object.entries(zoneResources)) {
        const icon = resourceIcons[tipo] || '📦';
        resourcesHTML += `
            <div style="background: white; padding: 0.5rem 1rem; border-radius: 6px; border: 1px solid #d1d5db; display: inline-flex; align-items: center; gap: 0.5rem;">
                <span style="font-size: 1.25rem;">${icon}</span>
                <span><strong>${cantidad}</strong> ${tipo.toLowerCase()}</span>
            </div>
        `;
    }
    
    if (resourcesList) resourcesList.innerHTML = resourcesHTML;
    if (currentResourcesDiv) currentResourcesDiv.style.display = 'block';
}

// Función para asignar recursos
async function assignResource(event) {
    event.preventDefault();
    
    const zonaSelect = document.getElementById('resourceZona');
    const zonaId = zonaSelect.value;
    const selectedOption = zonaSelect.options[zonaSelect.selectedIndex];
    const zonaNombre = selectedOption.getAttribute('data-nombre');
    const tipo = document.getElementById('resourceTipo').value;
    const cantidad = parseInt(document.getElementById('resourceCantidad').value);
    const statusSpan = document.getElementById('resource-status');
    
    // Validaciones
    if (!zonaId) {
        alert('❌ Debes seleccionar una zona');
        return;
    }
    
    if (!tipo) {
        alert('❌ Debes seleccionar un tipo de recurso');
        return;
    }
    
    if (!cantidad || cantidad <= 0) {
        alert('❌ La cantidad debe ser mayor a 0');
        return;
    }
    
    if (statusSpan) statusSpan.textContent = '🔄 Asignando recurso...';
    
    try {
        const response = await fetch('/api/resources', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                accion: 'agregar',
                ubicacion: zonaId,
                tipo: tipo,
                cantidad: cantidad
            })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.msg || 'Error al asignar recurso');
        }
        
        console.log('✅ Recurso asignado:', data);
        
        if (statusSpan) statusSpan.textContent = '✅ Recurso asignado exitosamente';
        
        // Recargar recursos
        await loadResources();
        
        // Actualizar estadísticas y gráficos
        updateStats();
        if (resourcesChart) updateResourcesChart();
        
        // Actualizar la vista de recursos actuales
        showCurrentZoneResources();
        
        // Limpiar formulario
        document.getElementById('resourceTipo').value = '';
        document.getElementById('resourceCantidad').value = '';
        
        alert(`✅ Recurso asignado exitosamente!\n\n📦 ${cantidad} unidades de ${tipo.toLowerCase()}\n📍 Asignadas a: ${zonaNombre}`);
        
    } catch (error) {
        console.error('❌ Error asignando recurso:', error);
        
        if (statusSpan) statusSpan.textContent = `❌ Error: ${error.message}`;
        
        alert(`❌ Error al asignar recurso:\n\n${error.message}`);
    }
}

// ===== FUNCIONES DE EVACUACIONES =====

// Función para mostrar información de la zona seleccionada para evacuación
function showEvacuationZoneInfo() {
    const zonaSelect = document.getElementById('evacuationZona');
    const selectedOption = zonaSelect.options[zonaSelect.selectedIndex];
    
    const zonaNombre = selectedOption.getAttribute('data-nombre');
    const riesgo = parseInt(selectedOption.getAttribute('data-riesgo')) || 0;
    const poblacion = parseInt(selectedOption.getAttribute('data-poblacion')) || 0;
    
    const zoneInfo = document.getElementById('zone-evacuation-info');
    const poblacionSpan = document.getElementById('zone-poblacion');
    const riesgoSpan = document.getElementById('zone-riesgo');
    const prioridadSpan = document.getElementById('zone-prioridad-text');
    
    if (!zonaNombre || zonaNombre === 'undefined') {
        if (zoneInfo) zoneInfo.style.display = 'none';
        return;
    }
    
    // Actualizar información
    if (poblacionSpan) poblacionSpan.textContent = poblacion.toLocaleString();
    if (riesgoSpan) riesgoSpan.textContent = riesgo;
    
    if (prioridadSpan) {
        if (riesgo > 70) {
            prioridadSpan.textContent = '🔴 ALTA';
            prioridadSpan.style.color = '#dc2626';
        } else if (riesgo > 40) {
            prioridadSpan.textContent = '🟡 MEDIA';
            prioridadSpan.style.color = '#f59e0b';
        } else {
            prioridadSpan.textContent = '🟢 BAJA';
            prioridadSpan.style.color = '#16a34a';
        }
    }
    
    // Sugerir número de personas basado en riesgo
    const personasInput = document.getElementById('evacuationPersonas');
    if (personasInput && !personasInput.value) {
        if (riesgo > 70) {
            personasInput.value = poblacion; // Evacuar toda la población si es alto riesgo
        } else if (riesgo > 40) {
            personasInput.value = Math.floor(poblacion * 0.5); // 50% si es medio
        } else {
            personasInput.value = Math.floor(poblacion * 0.25); // 25% si es bajo
        }
    }
    
    if (zoneInfo) zoneInfo.style.display = 'block';
}

// Función para registrar una evacuación
async function registerEvacuation(event) {
    event.preventDefault();
    
    const zonaSelect = document.getElementById('evacuationZona');
    const zonaId = zonaSelect.value;
    const selectedOption = zonaSelect.options[zonaSelect.selectedIndex];
    const zonaNombre = selectedOption.getAttribute('data-nombre');
    const riesgo = parseInt(selectedOption.getAttribute('data-riesgo')) || 0;
    const personas = parseInt(document.getElementById('evacuationPersonas').value);
    const statusSpan = document.getElementById('evacuation-status');
    
    // Validaciones
    if (!zonaId) {
        alert('❌ Debes seleccionar una zona');
        return;
    }
    
    if (!personas || personas <= 0) {
        alert('❌ El número de personas debe ser mayor a 0');
        return;
    }
    
    if (statusSpan) statusSpan.textContent = '🔄 Registrando evacuación...';
    
    try {
        const response = await fetch('/api/evacuations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                zonaId: zonaId,
                personas: personas
            })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.msg || 'Error al registrar evacuación');
        }
        
        console.log('✅ Evacuación registrada:', data);
        
        if (statusSpan) statusSpan.textContent = '✅ Evacuación registrada exitosamente';
        
        // Recargar evacuaciones
        await loadEvacuations();
        
        // Actualizar estadísticas y gráficos
        updateStats();
        if (evacuationsChart) updateEvacuationsChart();
        
        // Limpiar formulario
        zonaSelect.value = '';
        document.getElementById('evacuationPersonas').value = '';
        document.getElementById('zone-evacuation-info').style.display = 'none';
        
        // Cerrar modal
        closeModal('evacuation-modal');
        
        const prioridadText = riesgo > 70 ? 'ALTA 🔴' : riesgo > 40 ? 'MEDIA 🟡' : 'BAJA 🟢';
        alert(`✅ Evacuación registrada exitosamente!\n\n🚑 Zona: ${zonaNombre}\n👥 Personas: ${personas}\n⚠️ Prioridad: ${prioridadText} (${riesgo}%)\n\n💡 La evacuación ha sido agregada a la cola según su prioridad.`);
        
    } catch (error) {
        console.error('❌ Error registrando evacuación:', error);
        
        if (statusSpan) statusSpan.textContent = `❌ Error: ${error.message}`;
        
        alert(`❌ Error al registrar evacuación:\n\n${error.message}`);
    }
}

// ===== EQUIPOS DE RESCATE =====

// Cargar equipos de rescate desde el backend
async function loadEquipos() {
    try {
        const response = await fetch('/api/equipos');
        if (!response.ok) throw new Error('Error al cargar equipos');
        
        equiposData = await response.json();
        console.log('✅ Equipos cargados:', equiposData);
        updateEquiposTable();
    } catch (error) {
        console.error('❌ Error cargando equipos:', error);
        showToast('error', 'Error', 'No se pudieron cargar los equipos de rescate');
    }
}

// Actualizar tabla de equipos
function updateEquiposTable() {
    const tbody = document.querySelector('#equipos-table tbody');
    if (!tbody) return;
    
    if (!equiposData || equiposData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-secondary);">
                    <i class="fas fa-inbox" style="font-size: 2rem; opacity: 0.3;"></i>
                    <p style="margin-top: 0.5rem;">No hay equipos de rescate disponibles</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = equiposData.map(equipo => {
        const tipoIcons = {
            'MEDICO': '🏥',
            'BOMBERO': '🚒',
            'POLICIA': '👮',
            'VOLUNTARIO': '🤝'
        };
        
        const estadoBadge = equipo.disponible 
            ? '<span class="badge badge-success">Disponible</span>'
            : '<span class="badge badge-secondary">Asignado</span>';
        
        const ubicacion = equipo.ubicacionActual 
            ? (zonesData.find(z => z.id === equipo.ubicacionActual)?.nombre || equipo.ubicacionActual)
            : '-';
        
        return `
            <tr>
                <td><code>${equipo.id.substring(0, 8)}</code></td>
                <td>${tipoIcons[equipo.tipo] || '👥'} ${equipo.tipo}</td>
                <td>${equipo.miembros}</td>
                <td><small>${equipo.especialidades.join(', ')}</small></td>
                <td>${estadoBadge}</td>
                <td>${ubicacion}</td>
            </tr>
        `;
    }).join('');
}

// Calcular ruta más corta entre dos zonas usando el algoritmo de Dijkstra simplificado
function calcularRutaMasCorta(origenId, destinoId) {
    if (!origenId || !destinoId) return null;
    
    // Buscar ruta directa
    const rutaDirecta = routesData.find(r => 
        (r.origenId === origenId && r.destinoId === destinoId) ||
        (r.nombreOrigen === origenId && r.nombreDestino === destinoId)
    );
    
    if (rutaDirecta) {
        return {
            distancia: rutaDirecta.distancia,
            tiempo: rutaDirecta.tiempoEstimado,
            rutas: [rutaDirecta]
        };
    }
    
    // Si no hay ruta directa, buscar ruta con una parada
    const rutasDesdeOrigen = routesData.filter(r => 
        r.origenId === origenId || r.nombreOrigen === origenId
    );
    
    let mejorRuta = null;
    let menorDistancia = Infinity;
    
    rutasDesdeOrigen.forEach(ruta1 => {
        const intermedioId = ruta1.destinoId || ruta1.nombreDestino;
        const ruta2 = routesData.find(r => 
            (r.origenId === intermedioId || r.nombreOrigen === intermedioId) &&
            (r.destinoId === destinoId || r.nombreDestino === destinoId)
        );
        
        if (ruta2) {
            const distanciaTotal = parseFloat(ruta1.distancia) + parseFloat(ruta2.distancia);
            if (distanciaTotal < menorDistancia) {
                menorDistancia = distanciaTotal;
                mejorRuta = {
                    distancia: distanciaTotal.toFixed(1),
                    tiempo: (parseFloat(ruta1.tiempoEstimado) + parseFloat(ruta2.tiempoEstimado)).toFixed(1),
                    rutas: [ruta1, ruta2]
                };
            }
        }
    });
    
    return mejorRuta;
}

// Calcular prioridad de zona para asignación
function calcularPrioridadZona(zona) {
    let prioridad = 0;
    
    // Factor 1: Nivel de riesgo (0-100 puntos)
    prioridad += zona.nivelDeRiesgo || 0;
    
    // Factor 2: Población afectada (normalizado, max 50 puntos)
    const poblacionNormalizada = Math.min((zona.poblacion || 0) / 50, 50);
    prioridad += poblacionNormalizada;
    
    // Factor 3: Equipos ya asignados (restar 20 puntos por cada equipo)
    const equiposAsignados = (zona.equiposAsignados || []).length;
    prioridad -= (equiposAsignados * 20);
    
    // Factor 4: Recursos disponibles (restar puntos si tiene recursos)
    const tieneRecursos = resourcesData[zona.nombre] && 
        Object.values(resourcesData[zona.nombre]).some(cant => cant > 0);
    if (tieneRecursos) prioridad -= 10;
    
    return Math.max(0, prioridad);
}

// Abrir modal para asignar equipo a zona
function openAssignTeamModal() {
    const modal = document.getElementById('assign-team-modal');
    const equipoSelect = document.getElementById('assignTeamEquipo');
    const zonaSelect = document.getElementById('assignTeamZona');
    
    if (!modal || !equipoSelect || !zonaSelect) return;
    
    // Limpiar selects
    equipoSelect.innerHTML = '<option value="">Seleccione un equipo...</option>';
    zonaSelect.innerHTML = '<option value="">Seleccione una zona...</option>';
    
    // Llenar equipos disponibles
    equiposData.filter(e => e.disponible).forEach(equipo => {
        const tipoIcons = {
            'MEDICO': '🏥',
            'BOMBERO': '🚒',
            'POLICIA': '👮',
            'VOLUNTARIO': '🤝'
        };
        equipoSelect.innerHTML += `
            <option value="${equipo.id}">
                ${tipoIcons[equipo.tipo] || '👥'} ${equipo.tipo} - ${equipo.miembros} miembros (${equipo.especialidades.join(', ')})
            </option>
        `;
    });
    
    // Ordenar zonas por prioridad (más críticas primero)
    const zonasOrdenadas = [...zonesData].sort((a, b) => {
        return calcularPrioridadZona(b) - calcularPrioridadZona(a);
    });
    
    // Llenar zonas con indicador de prioridad
    zonasOrdenadas.forEach((zona, index) => {
        const prioridad = calcularPrioridadZona(zona);
        let indicador = '';
        
        if (index === 0 && prioridad > 70) {
            indicador = '🔴 CRÍTICA - ';
        } else if (prioridad > 60) {
            indicador = '🟠 ALTA - ';
        } else if (prioridad > 40) {
            indicador = '🟡 MEDIA - ';
        }
        
        zonaSelect.innerHTML += `
            <option value="${zona.id}" data-prioridad="${prioridad}">
                ${indicador}${zona.nombre} (Riesgo: ${zona.nivelDeRiesgo}%, Equipos: ${(zona.equiposAsignados || []).length})
            </option>
        `;
    });
    
    // Mostrar recomendación inteligente
    mostrarRecomendacionInteligente(zonasOrdenadas);
    
    // Ocultar info del equipo y zona
    const teamInfoContainer = document.getElementById('teamInfoContainer');
    const zoneInfoContainer = document.getElementById('zoneInfoContainer');
    if (teamInfoContainer) teamInfoContainer.style.display = 'none';
    if (zoneInfoContainer) zoneInfoContainer.style.display = 'none';
    
    // Abrir modal con la clase correcta
    modal.classList.add('active');
    console.log('✅ Modal de asignación de equipos abierto');
}

// Mostrar recomendación inteligente
function mostrarRecomendacionInteligente(zonasOrdenadas) {
    const recommendationDiv = document.getElementById('smartRecommendation');
    const contentDiv = document.getElementById('recommendationContent');
    
    if (!recommendationDiv || !contentDiv || zonasOrdenadas.length === 0) return;
    
    const zonaMasCritica = zonasOrdenadas[0];
    const prioridad = calcularPrioridadZona(zonaMasCritica);
    
    if (prioridad > 60) {
        const equiposAsignados = (zonaMasCritica.equiposAsignados || []).length;
        const recursos = resourcesData[zonaMasCritica.nombre];
        const tieneRecursos = recursos && Object.values(recursos).some(cant => cant > 0);
        
        contentDiv.innerHTML = `
            <div style="margin-bottom: 0.5rem;">
                <strong style="font-size: 1.05rem;">📍 ${zonaMasCritica.nombre}</strong> requiere atención prioritaria
            </div>
            <ul style="margin: 0.5rem 0; padding-left: 1.5rem; line-height: 1.6;">
                <li><strong>Nivel de riesgo:</strong> ${zonaMasCritica.nivelDeRiesgo}% ${zonaMasCritica.nivelDeRiesgo > 75 ? '🔴 CRÍTICO' : zonaMasCritica.nivelDeRiesgo > 50 ? '🟠 ALTO' : '🟡 MEDIO'}</li>
                <li><strong>Población afectada:</strong> ${zonaMasCritica.poblacion} personas</li>
                <li><strong>Equipos asignados:</strong> ${equiposAsignados} ${equiposAsignados === 0 ? '⚠️ ¡Sin equipos!' : ''}</li>
                <li><strong>Recursos:</strong> ${tieneRecursos ? '✅ Disponibles' : '❌ Sin recursos'}</li>
            </ul>
            <div style="margin-top: 0.75rem; padding: 0.5rem; background: rgba(255,255,255,0.5); border-radius: 4px; font-weight: 600;">
                💡 Se recomienda asignar un equipo a esta zona de inmediato
            </div>
        `;
        recommendationDiv.style.display = 'block';
    } else {
        recommendationDiv.style.display = 'none';
    }
}

// Actualizar información de la zona seleccionada
function updateZoneInfo() {
    const zonaSelect = document.getElementById('assignTeamZona');
    const zoneInfoContainer = document.getElementById('zoneInfoContainer');
    const zoneInfoContent = document.getElementById('zoneInfoContent');
    
    if (!zonaSelect || !zoneInfoContainer || !zoneInfoContent) return;
    
    const zonaId = zonaSelect.value;
    
    if (!zonaId) {
        zoneInfoContainer.style.display = 'none';
        return;
    }
    
    const zona = zonesData.find(z => z.id === zonaId);
    if (!zona) return;
    
    const equiposAsignados = zona.equiposAsignados || [];
    const recursos = resourcesData[zona.nombre] || {};
    const recursosHTML = Object.entries(recursos)
        .filter(([tipo, cant]) => cant > 0)
        .map(([tipo, cant]) => `<li>${tipo}: ${cant} unidades</li>`)
        .join('') || '<li style="color: #991b1b;">⚠️ Sin recursos disponibles</li>';
    
    // Calcular ruta más corta desde almacén central
    const rutaInfo = calcularRutaMasCorta('almacen-central', zona.id);
    let rutaHTML = '';
    if (rutaInfo) {
        rutaHTML = `
            <div style="margin-top: 0.75rem; padding: 0.5rem; background: #dbeafe; border-radius: 4px;">
                <strong>📍 Ruta óptima desde almacén:</strong><br>
                <span style="font-size: 0.85rem;">
                    ${rutaInfo.distancia} km • ${rutaInfo.tiempo} hrs estimadas
                    ${rutaInfo.rutas.length > 1 ? '<br>⚠️ Requiere parada intermedia' : ''}
                </span>
            </div>
        `;
    }
    
    zoneInfoContent.innerHTML = `
        <p style="margin: 0.5rem 0; line-height: 1.6;">
            <strong>Población:</strong> ${zona.poblacion} personas<br>
            <strong>Nivel de Riesgo:</strong> ${zona.nivelDeRiesgo}%<br>
            <strong>Equipos asignados:</strong> ${equiposAsignados.length}<br>
            ${equiposAsignados.length > 0 ? `<span style="font-size: 0.85rem; color: #059669;">• ${equiposAsignados.map(e => e.tipo).join(', ')}</span><br>` : ''}
            <strong>Recursos disponibles:</strong>
        </p>
        <ul style="margin: 0.25rem 0 0.5rem 1.5rem; font-size: 0.85rem;">
            ${recursosHTML}
        </ul>
        ${rutaHTML}
    `;
    
    zoneInfoContainer.style.display = 'block';
}

// Actualizar información del equipo seleccionado
function updateTeamInfo() {
    const equipoSelect = document.getElementById('assignTeamEquipo');
    const infoContainer = document.getElementById('teamInfoContainer');
    
    if (!equipoSelect || !infoContainer) {
        console.warn('⚠️ No se encontraron elementos para actualizar info del equipo');
        return;
    }
    
    const equipoId = equipoSelect.value;
    
    if (!equipoId) {
        infoContainer.style.display = 'none';
        return;
    }
    
    const equipo = equiposData.find(e => e.id === equipoId);
    if (!equipo) {
        console.warn('⚠️ Equipo no encontrado:', equipoId);
        return;
    }
    
    const tipoIcons = {
        'MEDICO': '🏥',
        'BOMBERO': '🚒',
        'POLICIA': '👮',
        'VOLUNTARIO': '🤝'
    };
    
    const tipoElement = document.getElementById('teamInfoTipo');
    const miembrosElement = document.getElementById('teamInfoMiembros');
    const especialidadesElement = document.getElementById('teamInfoEspecialidades');
    const estadoElement = document.getElementById('teamInfoEstado');
    
    if (tipoElement) tipoElement.textContent = `${tipoIcons[equipo.tipo] || '👥'} ${equipo.tipo}`;
    if (miembrosElement) miembrosElement.textContent = equipo.miembros;
    if (especialidadesElement) especialidadesElement.textContent = equipo.especialidades.join(', ');
    if (estadoElement) estadoElement.innerHTML = equipo.disponible 
        ? '<span class="badge badge-success">Disponible</span>'
        : '<span class="badge badge-secondary">Asignado</span>';
    
    infoContainer.style.display = 'block';
    console.log('✅ Info del equipo actualizada:', equipo.tipo);
    
    // También actualizar info de zona si hay una seleccionada
    updateZoneInfo();
}

// Guardar asignación de equipo
async function saveAssignTeam(event) {
    event.preventDefault();
    
    const equipoId = document.getElementById('assignTeamEquipo').value;
    const zonaId = document.getElementById('assignTeamZona').value;
    
    console.log('🔄 Intentando asignar equipo:', { equipoId, zonaId });
    
    if (!equipoId || !zonaId) {
        showToast('warning', 'Advertencia', 'Debe seleccionar un equipo y una zona');
        return;
    }
    
    const equipo = equiposData.find(e => e.id === equipoId);
    const zona = zonesData.find(z => z.id === zonaId);
    
    if (!equipo || !zona) {
        showToast('error', 'Error', 'Equipo o zona no encontrados');
        return;
    }
    
    try {
        console.log('📡 Enviando petición POST a /api/equipos...');
        
        const response = await fetch('/api/equipos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                equipoId: equipoId,
                zonaId: zonaId
            })
        });
        
        console.log('📡 Respuesta recibida:', response.status);
        
        const data = await response.json();
        console.log('📦 Datos de respuesta:', data);
        
        if (!response.ok) {
            throw new Error(data.message || 'Error al asignar equipo');
        }
        
        console.log('✅ Equipo asignado exitosamente');
        
        showToast('success', 'Equipo Asignado', 
            `El equipo ${equipo.tipo} (${equipo.miembros} miembros) ha sido asignado a ${zona.nombre}`);
        
        // Recargar datos
        console.log('🔄 Recargando datos...');
        await Promise.all([loadEquipos(), loadZones()]);
        
        // Actualizar mapa y estadísticas
        updateMapMarkers();
        updateStats();
        
        // Cerrar modal y limpiar formulario
        closeModal('assign-team-modal');
        const form = document.getElementById('assignTeamForm');
        if (form) form.reset();
        
        const teamInfoContainer = document.getElementById('teamInfoContainer');
        if (teamInfoContainer) teamInfoContainer.style.display = 'none';
        
        console.log('✅ Proceso completado');
        
    } catch (error) {
        console.error('❌ Error asignando equipo:', error);
        showToast('error', 'Error al Asignar', error.message || 'No se pudo asignar el equipo');
    }
}

// Hacer la función global
window.openAssignTeamModal = openAssignTeamModal;
window.updateTeamInfo = updateTeamInfo;
window.saveAssignTeam = saveAssignTeam;

// =====================================================
// SISTEMA DE NOTIFICACIONES
// =====================================================

let notificationsData = [];
let notificationsInterval = null;

/**
 * Inicializar sistema de notificaciones
 */
function initNotifications() {
    console.log('🔔 Inicializando sistema de notificaciones...');
    
    // Cargar notificaciones iniciales
    loadNotifications();
    
    // Polling cada 10 segundos
    notificationsInterval = setInterval(() => {
        loadNotifications();
    }, 10000);
    
    // Cerrar dropdown al hacer clic fuera
    document.addEventListener('click', (e) => {
        const dropdown = document.getElementById('notificationsDropdown');
        const bell = document.getElementById('notificationsBell');
        
        if (dropdown && bell && !dropdown.contains(e.target) && !bell.contains(e.target)) {
            dropdown.classList.remove('show');
        }
    });
}

/**
 * Cargar notificaciones desde el servidor
 */
async function loadNotifications() {
    try {
        const response = await fetch('/api/notificaciones');
        
        if (!response.ok) {
            console.error('Error al cargar notificaciones:', response.status);
            return;
        }
        
        const data = await response.json();
        notificationsData = data.notificaciones || [];
        
        // Actualizar badge con notificaciones no leídas
        updateNotificationsBadge(data.noLeidas || 0);
        
        // Renderizar lista de notificaciones
        renderNotifications();
        
    } catch (error) {
        console.error('Error cargando notificaciones:', error);
    }
}

/**
 * Actualizar badge de notificaciones no leídas
 */
function updateNotificationsBadge(count) {
    const badge = document.getElementById('notificationsBadge');
    const bell = document.getElementById('notificationsBell');
    
    if (!badge || !bell) return;
    
    if (count > 0) {
        badge.textContent = count > 99 ? '99+' : count;
        badge.style.display = 'flex';
        bell.classList.add('has-unread');
    } else {
        badge.style.display = 'none';
        bell.classList.remove('has-unread');
    }
}

/**
 * Renderizar lista de notificaciones
 */
function renderNotifications() {
    const listContainer = document.getElementById('notificationsList');
    
    if (!listContainer) return;
    
    if (notificationsData.length === 0) {
        listContainer.innerHTML = `
            <div class="no-notifications">
                <i class="fas fa-bell-slash"></i>
                <p>No hay notificaciones</p>
            </div>
        `;
        return;
    }
    
    listContainer.innerHTML = notificationsData.map(notif => `
        <div class="notification-item ${notif.leida ? '' : 'unread'}" 
             onclick="markNotificationAsRead(${notif.id})">
            <div class="notification-icon ${notif.tipoClase}">
                ${notif.tipoIcono}
            </div>
            <div class="notification-content">
                <div class="notification-message">${notif.mensaje}</div>
                <div class="notification-time">${notif.timestamp}</div>
            </div>
        </div>
    `).join('');
}

/**
 * Toggle dropdown de notificaciones
 */
function toggleNotifications() {
    const dropdown = document.getElementById('notificationsDropdown');
    
    if (!dropdown) return;
    
    dropdown.classList.toggle('show');
}

/**
 * Marcar notificación como leída
 */
async function markNotificationAsRead(notifId) {
    try {
        const response = await fetch('/api/notificaciones', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ id: notifId })
        });
        
        if (!response.ok) {
            console.error('Error al marcar notificación:', response.status);
            return;
        }
        
        const data = await response.json();
        
        // Actualizar badge
        updateNotificationsBadge(data.noLeidas || 0);
        
        // Recargar notificaciones
        await loadNotifications();
        
    } catch (error) {
        console.error('Error marcando notificación:', error);
    }
}

/**
 * Marcar todas las notificaciones como leídas
 */
async function markAllNotificationsAsRead() {
    try {
        const response = await fetch('/api/notificaciones', {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            console.error('Error al marcar todas:', response.status);
            return;
        }
        
        // Actualizar badge
        updateNotificationsBadge(0);
        
        // Recargar notificaciones
        await loadNotifications();
        
        showToast('success', 'Notificaciones', 'Todas las notificaciones marcadas como leídas');
        
    } catch (error) {
        console.error('Error marcando todas las notificaciones:', error);
        showToast('error', 'Error', 'No se pudieron marcar las notificaciones');
    }
}

// Hacer funciones globales
window.toggleNotifications = toggleNotifications;
window.markNotificationAsRead = markNotificationAsRead;
window.markAllNotificationsAsRead = markAllNotificationsAsRead;

// Inicializar notificaciones cuando cargue la página
document.addEventListener('DOMContentLoaded', () => {
    initNotifications();
});
