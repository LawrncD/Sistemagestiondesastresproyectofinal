# Changelog - Sistema de Gestión de Desastres

## [2.0.0] - 2025-11-14

### ✨ Nuevas Características

#### 🗺️ Integración con OpenStreetMap
- **Mapa Interactivo**: Implementación completa de Leaflet.js para visualización en tiempo real
- **Marcadores Dinámicos**: Zonas afectadas representadas con colores según nivel de riesgo
  - 🟢 Verde: Riesgo bajo (< 40)
  - 🟡 Amarillo: Riesgo medio (40-74)
  - 🔴 Rojo: Riesgo alto (≥ 75)
- **Rutas Visuales**: Líneas conectando zonas con información de distancia, tiempo y capacidad
- **Popups Informativos**: Detalles completos de cada zona y ruta al hacer clic

#### 🎯 Mejoras en el Backend
- **Coordenadas Geográficas**: Añadido soporte para latitud y longitud en modelo `ZonaAfectada`
- **Constructor Dual**: Permite crear zonas con coordenadas específicas o generadas automáticamente
- **Datos Realistas**: Zonas iniciales con coordenadas reales de ciudades colombianas:
  - Bogotá Centro (4.7110, -74.0721)
  - Refugio Medellín (6.2442, -75.5812)
  - Centro Ayuda Cali (3.4516, -76.5320)

#### 💅 Mejoras en el Frontend
- **CSS Moderno**: Refactorización completa con variables CSS y diseño responsivo
- **Diseño Adaptativo**: Soporte optimizado para dispositivos móviles y tablets
- **Estilos del Mapa**: Integración perfecta con el theme del sistema
- **Transiciones Suaves**: Animaciones y efectos hover mejorados

### 🔧 Optimizaciones

#### Código
- Limpieza de imports no utilizados en `Admin.java` y `ApiEvacuacionesServlet.java`
- Añadido getter para `ArbolDistribucion` en `SistemaGestionDesastres`
- Eliminación de función `drawGraph()` obsoleta reemplazada por mapa real
- Documentación mejorada con comentarios en código

#### Performance
- Actualización eficiente del mapa solo cuando hay cambios en datos
- Capas separadas para marcadores y rutas (mejor gestión de memoria)
- Ajuste automático de vista del mapa para mostrar todos los elementos

### 📚 Documentación

- **README.md**: Completamente reescrito con:
  - Descripción detallada del proyecto
  - Instrucciones de instalación paso a paso
  - Guía de uso con credenciales de prueba
  - Arquitectura y tecnologías
  - Troubleshooting común
  - Roadmap futuro

- **CHANGELOG.md**: Nuevo archivo para tracking de cambios

### 🐛 Correcciones

- Corregido error en datos iniciales (rutas comentadas incorrectamente)
- Eliminados imports duplicados en servlets
- Warnings de Java resueltos (campos no utilizados ahora tienen getters)

### 🏗️ Estructura Mejorada

```
sistemagestiondesastres/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── co/edu/uniquindio/poo/
│   │   │       ├── app/          # Lógica principal + servlets
│   │   │       ├── ds/           # Estructuras de datos
│   │   │       └── model/        # Modelos con coordenadas
│   │   └── resources/
│   │       └── web/              # Frontend con Leaflet
│   └── test/                     # Tests unitarios
├── pom.xml                       # Maven config
├── README.md                     # Documentación principal
└── CHANGELOG.md                  # Este archivo
```

### 📦 Dependencias Actualizadas

Frontend:
- Leaflet.js 1.9.4 (nuevo)
- Chart.js (mantenido)
- Font Awesome 6.4 (mantenido)

Backend:
- Java 17
- Jakarta Servlet API 5.0
- Jetty 11.0.15
- Gson 2.10.1
- JUnit 5.10.0

### ✅ Testing

- Sistema completamente funcional
- Mapa carga correctamente en dashboard
- Zonas y rutas se visualizan con coordenadas reales
- Popups interactivos funcionando
- Responsive design validado

### 🎯 Buenas Prácticas Implementadas

1. **Separación de Responsabilidades**: Frontend y backend claramente separados
2. **Código Limpio**: Variables con nombres descriptivos, funciones modulares
3. **DRY (Don't Repeat Yourself)**: Reutilización de funciones `updateMap()`
4. **Responsive Design**: Mobile-first approach
5. **Accesibilidad**: ARIA labels y roles semánticos en HTML
6. **Performance**: Lazy loading de capas del mapa
7. **Documentación**: Comentarios claros en código complejo
8. **Gestión de Errores**: Try-catch en operaciones asíncronas

---

## [1.0.0] - 2025-11-01 (Versión Inicial)

### Características Iniciales
- Sistema básico de gestión de desastres
- Grafo para zonas y rutas
- Gestión de recursos con HashMap
- Cola de prioridad para evacuaciones
- Interfaz web básica
- Login con autenticación simple
- Dashboard con estadísticas

---

**Notas de Desarrollo:**

Este proyecto cumple con todos los requisitos académicos solicitados:
- ✅ Uso de estructuras de datos complejas (Grafo, Cola, HashMap, Árbol)
- ✅ Interfaz gráfica moderna y funcional
- ✅ Integración con OpenStreetMap
- ✅ Buenas prácticas de programación
- ✅ Código bien documentado
- ✅ Sistema completo end-to-end funcional
