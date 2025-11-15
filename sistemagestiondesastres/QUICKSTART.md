# Guía de Inicio Rápido - Sistema de Gestión de Desastres

## 🚀 Inicio Rápido

### Windows - Opción 1: Archivo BAT (Recomendado)
```cmd
# Doble clic en el archivo o ejecutar desde cmd
INICIAR.bat
```

### Windows - Opción 2: PowerShell
```powershell
# Si tienes problemas con políticas de ejecución, usa:
powershell -ExecutionPolicy Bypass -File start.ps1

# O habilita la ejecución temporal:
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\start.ps1
```

### Windows - Opción 3: Comando Directo
```cmd
mvn clean compile
mvn exec:java -Dexec.mainClass="co.edu.uniquindio.poo.app.MainServer"
```

### Linux/Mac (Bash)
```bash
# Dar permisos de ejecución (solo primera vez)
chmod +x start.sh stop.sh

# Iniciar servidor
./start.sh

# Detener servidor
./stop.sh
```

### Manual (Maven)
```bash
# Compilar
mvn clean compile

# Ejecutar (RECOMENDADO - usa MainServer)
mvn exec:java -Dexec.mainClass="co.edu.uniquindio.poo.app.MainServer"

# Alternativa con Jetty (no recomendado, más lento)
mvn jetty:run
```

## 🔑 Credenciales de Acceso

### Administrador
- **Usuario:** `admin@local`
- **Contraseña:** `admin123`

### Operador
- **Usuario:** `oper1@local`
- **Contraseña:** `op123`

## 🌐 Acceso

Una vez iniciado el servidor, accede a:
- **URL:** http://localhost:8080
- **Puerto:** 8080

## 📊 Funcionalidades

1. **Dashboard** - Vista general del sistema
2. **Zonas** - Gestión de áreas afectadas
3. **Rutas** - Administración de rutas de evacuación
4. **Recursos** - Control de inventario
5. **Evacuaciones** - Gestión de evacuaciones
6. **Estadísticas** - Análisis y reportes (solo admin)

## 🗺️ Mapa Interactivo

El sistema incluye un mapa de OpenStreetMap que muestra:
- 🟢 Zonas de riesgo bajo (< 40)
- 🟡 Zonas de riesgo medio (40-74)
- 🔴 Zonas de riesgo alto (≥ 75)
- 🔵 Rutas disponibles
- ⚪ Rutas no disponibles

## 🐛 Solución de Problemas

### Política de ejecución de PowerShell bloqueada
```powershell
# Opción 1: Usar archivo BAT
INICIAR.bat

# Opción 2: Bypass temporal
powershell -ExecutionPolicy Bypass -File start.ps1

# Opción 3: Habilitar para usuario actual
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Puerto 8080 ocupado
```powershell
# Windows
Get-NetTCPConnection -LocalPort 8080
Stop-Process -Id <PID>

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Ver logs del servidor
```powershell
# Windows
Get-Content logs/server.log -Wait -Tail 50

# Linux/Mac
tail -f logs/server.log
```

### Reinstalar dependencias
```bash
mvn clean install -U
```

## 📁 Estructura de Archivos

```
sistemagestiondesastres/
├── INICIAR.bat        # Script inicio Windows (BAT) - RECOMENDADO
├── start.ps1          # Script inicio Windows (PowerShell)
├── start.sh           # Script inicio Linux/Mac
├── stop.ps1           # Script detención Windows
├── stop.sh            # Script detención Linux/Mac
├── logs/              # Logs del servidor
│   ├── server.log         # Salida estándar
│   └── server-error.log   # Errores
├── src/               # Código fuente
└── pom.xml            # Configuración Maven
```

## 💡 Consejos

- **Usa INICIAR.bat en Windows** - evita problemas con políticas de PowerShell
- Los scripts automáticos manejan la compilación, puerto ocupado y logs
- El servidor usa `MainServer` con Jetty embebido (más rápido que jetty:run)
- El mapa carga coordenadas reales de ciudades colombianas
- Los cambios en archivos Java requieren reiniciar el servidor
- Los cambios en archivos web (HTML/CSS/JS) solo requieren refrescar el navegador
- El navegador se abre automáticamente al usar INICIAR.bat

## 📞 Soporte

Para más información consulta:
- README.md - Documentación completa
- CHANGELOG.md - Historial de cambios
- logs/server.log - Logs de ejecución
