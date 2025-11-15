# 🌍 Sistema de Gestión de Desastres Naturales

Sistema integral de gestión de emergencias y desastres naturales desarrollado con Java, que incluye visualización en tiempo real con OpenStreetMap, gestión de recursos, evacuaciones y análisis de rutas óptimas.

## 📋 Descripción

Este proyecto implementa un sistema completo para la gestión y coordinación de respuestas ante desastres naturales, proporcionando herramientas para:

- **Gestión de Zonas Afectadas**: Registro y monitoreo de áreas impactadas con niveles de riesgo
- **Mapa Interactivo**: Visualización en tiempo real usando OpenStreetMap (Leaflet.js)
- **Gestión de Recursos**: Control de inventario y distribución de recursos de emergencia
- **Planificación de Evacuaciones**: Sistema de priorización basado en niveles de riesgo
- **Cálculo de Rutas**: Algoritmos para encontrar las rutas más cortas y seguras
- **Análisis Estadístico**: Dashboard con gráficas y reportes en tiempo real

## 🏗️ Arquitectura

El proyecto utiliza las siguientes estructuras de datos y patrones:

- **Grafo Dirigido**: Para representar zonas y rutas de evacuación
- **Cola de Prioridad**: Para gestionar evacuaciones por urgencia
- **HashMap**: Para gestión eficiente de recursos por ubicación
- **Árbol de Distribución**: Para optimización de distribución de recursos
- **Patrón Singleton**: Para gestión centralizada del sistema

## 👥 Autores

- Universidad del Quindío - Programa de Programación Orientada a Objetos
- Proyecto académico 2025

## 🛠️ Tecnologías y Herramientas

### Backend
- **Java 17** - [Adoptium OpenJDK](https://adoptium.net/es)
- **Maven** - Gestión de dependencias y construcción
- **Jakarta Servlet API 5.0** - Manejo de peticiones HTTP
- **Jetty 11.0.15** - Servidor embebido
- **Gson 2.10.1** - Serialización JSON
- **JUnit 5.10.0** - Framework de testing

### Frontend
- **HTML5 + CSS3** - Estructura y estilos
- **JavaScript (ES6+)** - Lógica del cliente
- **Leaflet.js 1.9.4** - Integración con OpenStreetMap
- **Chart.js** - Visualización de datos estadísticos
- **Font Awesome 6.4** - Iconografía

## 📦 Instalación y Configuración

### Prerrequisitos

```shell
# Verificar instalación de Java 17
java -version

# Verificar instalación de Maven
mvn -version
```

### Clonar e Instalar

```shell
# Clonar el repositorio
git clone <url-del-repositorio>
cd sistemagestiondesastres

# Instalar dependencias
mvn clean install
```

## 🚀 Ejecución

### ⚡ Método Más Rápido (Nuevo)

#### Windows:
```cmd
# Opción 1: Doble clic en el archivo
run.bat

# Opción 2: Desde CMD o PowerShell
.\run.bat

# Opción 3: PowerShell
.\run.ps1
```

#### Linux/Mac:
```bash
# Primera vez: dar permisos
chmod +x run.sh

# Ejecutar
./run.sh
```

**El script `run.bat`/`run.ps1`/`run.sh`:**
- ✅ Funciona desde cualquier ubicación
- ✅ Se posiciona automáticamente en el directorio correcto
- ✅ Compila y ejecuta en un solo comando
- ✅ Muestra logs en consola
- ✅ Compatible con cualquier PC que tenga el proyecto

### 🎯 Método Alternativo: Scripts Avanzados

#### Windows (PowerShell)
```powershell
# Iniciar servidor (maneja todo automáticamente)
.\start.ps1

# Detener servidor
.\stop.ps1
```

#### Linux/Mac (Bash)
```bash
# Dar permisos de ejecución (solo primera vez)
chmod +x start.sh stop.sh

# Iniciar servidor
./start.sh

# Detener servidor
./stop.sh
```

**Características de los scripts:**
- ✅ Verificación automática de Java 17+ y Maven
- ✅ Detección y liberación del puerto 8080
- ✅ Compilación automática del proyecto
- ✅ Logs organizados en carpeta `logs/`
- ✅ Opción de abrir navegador automáticamente
- ✅ Visualización de logs en tiempo real
- ✅ Banner informativo con credenciales

### Método Manual

#### Compilar el proyecto

```shell
mvn clean compile
```

#### Ejecutar pruebas

```shell
mvn clean test
```

#### Ejecutar el servidor

```shell
# Opción 1: Ejecutar clase Main
mvn exec:java -Dexec.mainClass="co.edu.uniquindio.poo.app.Main"

# Opción 2: Ejecutar con Jetty Maven Plugin
mvn jetty:run

# Opción 3: Ejecutar servidor standalone (RECOMENDADO)
mvn exec:java "-Dexec.mainClass=co.edu.uniquindio.poo.app.MainServer"
```

El servidor estará disponible en: `http://localhost:8080`

### Generar y ejecutar JAR

```shell
# Generar el JAR
mvn clean package

# Ejecutar el JAR
java -jar target/sistemagestiondesastres-1.0.jar
```

## 🎮 Uso del Sistema

### Credenciales de Prueba

**Administrador:**
- Usuario: `admin@local`
- Contraseña: `admin123`

**Operador de Emergencia:**
- Usuario: `oper1@local`
- Contraseña: `op123`

### Funcionalidades Principales

1. **Dashboard**: Vista general con resumen de zonas, recursos y evacuaciones
2. **Zonas**: Gestión de áreas afectadas con visualización en mapa
3. **Rutas**: Administración de rutas de evacuación y cálculo de caminos óptimos
4. **Recursos**: Control de inventario y transferencias entre ubicaciones
5. **Evacuaciones**: Registro y priorización de evacuaciones
6. **Estadísticas**: Análisis visual con gráficas (solo administradores)

## 🗺️ Integración con OpenStreetMap

El sistema incluye un mapa interactivo que muestra:

- **Marcadores de zonas** con colores según nivel de riesgo:
  - 🟢 Verde: Riesgo bajo (< 40)
  - 🟡 Amarillo: Riesgo medio (40-74)
  - 🔴 Rojo: Riesgo alto (≥ 75)
  
- **Rutas de evacuación** dibujadas como líneas:
  - Azul sólido: Rutas disponibles
  - Gris punteado: Rutas no disponibles

- **Popups informativos** con detalles de cada zona y ruta

## 📁 Estructura del Proyecto

```
sistemagestiondesastres/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── co/edu/uniquindio/poo/
│   │   │       ├── app/              # Lógica principal y servlets
│   │   │       ├── ds/               # Estructuras de datos
│   │   │       └── model/            # Modelos de dominio
│   │   └── resources/
│   │       └── web/                  # Recursos web (HTML, CSS, JS)
│   └── test/                         # Pruebas unitarias
├── pom.xml                           # Configuración Maven
└── README.md
```

## 🧪 Testing

El proyecto incluye pruebas unitarias para validar:
- Funcionalidad de estructuras de datos
- Lógica de negocio
- Cálculo de rutas óptimas
- Gestión de recursos

Ejecutar con cobertura:

```shell
mvn clean test jacoco:report
```

## 🔐 Seguridad

- Autenticación basada en sesiones HTTP
- Validación de permisos por rol (Admin/Operador)
- Sanitización de datos en formularios
- CORS configurado para desarrollo local

## 🐛 Troubleshooting

### Puerto 8080 en uso

```shell
# Windows PowerShell
Get-NetTCPConnection | Where-Object {$_.LocalPort -eq 8080}
Stop-Process -Id <PID>
```

### Problemas con Maven

```shell
# Limpiar caché de Maven
mvn dependency:purge-local-repository
mvn clean install
```

## 📈 Roadmap Futuro

- [ ] Persistencia en base de datos (PostgreSQL)
- [ ] API REST completa con documentación Swagger
- [ ] Sistema de notificaciones en tiempo real (WebSockets)
- [ ] Aplicación móvil (React Native)
- [ ] Machine Learning para predicción de riesgos
- [ ] Integración con servicios meteorológicos

## 📄 Licencia

Proyecto académico - Universidad del Quindío © 2025

---

**Desarrollado con ❤️ para ayudar en la gestión de emergencias**
