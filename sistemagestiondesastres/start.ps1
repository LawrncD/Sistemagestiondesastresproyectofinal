# ========================================
# Script de inicio - Sistema de Gestión de Desastres
# PowerShell Version
# ========================================

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                                            ║" -ForegroundColor Cyan
Write-Host "║     🌍 SISTEMA DE GESTIÓN DE DESASTRES NATURALES 🌍       ║" -ForegroundColor Cyan
Write-Host "║                                                            ║" -ForegroundColor Cyan
Write-Host "║            Universidad del Quindío - 2025                  ║" -ForegroundColor Cyan
Write-Host "║                                                            ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Verificar Java
Write-Host "[1/5] Verificando Java..." -ForegroundColor Blue
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_ -match '\"(\d+)' | Out-Null; $matches[1] }
    if ([int]$javaVersion -ge 17) {
        Write-Host "✓ Java $javaVersion encontrado" -ForegroundColor Green
    } else {
        Write-Host "✗ Se requiere Java 17 o superior (actual: Java $javaVersion)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Java no encontrado. Por favor instala Java 17+" -ForegroundColor Red
    exit 1
}

# Verificar Maven
Write-Host "[2/5] Verificando Maven..." -ForegroundColor Blue
try {
    $mvnVersion = mvn -version 2>&1 | Select-String "Apache Maven" | ForEach-Object { ($_ -split " ")[2] }
    Write-Host "✓ Maven $mvnVersion encontrado" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven no encontrado. Por favor instala Maven" -ForegroundColor Red
    exit 1
}

# Verificar puerto 8080
Write-Host "[3/5] Verificando puerto 8080..." -ForegroundColor Blue
$portInUse = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($portInUse) {
    Write-Host "⚠ Puerto 8080 en uso. Intentando liberar..." -ForegroundColor Yellow
    $processId = $portInUse.OwningProcess
    Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    $portInUse = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
    if ($portInUse) {
        Write-Host "✗ No se pudo liberar el puerto 8080" -ForegroundColor Red
        exit 1
    } else {
        Write-Host "✓ Puerto 8080 liberado" -ForegroundColor Green
    }
} else {
    Write-Host "✓ Puerto 8080 disponible" -ForegroundColor Green
}

# Crear directorio de logs
if (-not (Test-Path "logs")) {
    New-Item -ItemType Directory -Path "logs" | Out-Null
}

# Compilar proyecto
Write-Host "[4/5] Compilando proyecto..." -ForegroundColor Blue
$compileOutput = mvn clean compile 2>&1 | Out-String
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "✗ Error en compilación:" -ForegroundColor Red
    Write-Host $compileOutput -ForegroundColor Red
    exit 1
}

# Iniciar servidor
Write-Host "[5/5] Iniciando servidor..." -ForegroundColor Blue
Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║  🚀 Servidor iniciando en puerto 8080...                 ║" -ForegroundColor Magenta
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

# Iniciar servidor usando Maven exec
Write-Host "Ejecutando: mvn exec:java..." -ForegroundColor Cyan
$serverJob = Start-Process -FilePath "mvn" -ArgumentList "exec:java","-Dexec.mainClass=co.edu.uniquindio.poo.app.MainServer" -NoNewWindow -PassThru -RedirectStandardOutput "logs/server.log" -RedirectStandardError "logs/server-error.log"

# Esperar a que el servidor inicie
Write-Host "Esperando inicio del servidor..." -ForegroundColor Cyan
for ($i = 1; $i -le 10; $i++) {
    Start-Sleep -Seconds 1
    $serverRunning = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
    if ($serverRunning) {
        break
    }
    Write-Host "." -NoNewline -ForegroundColor Yellow
}
Write-Host ""

# Verificar si el servidor está corriendo
$serverRunning = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($serverRunning) {
    Write-Host "✓ Servidor iniciado correctamente (PID: $($serverJob.Id))" -ForegroundColor Green
    $serverJob.Id | Out-File ".server.pid"
    
    Write-Host ""
    Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
    Write-Host "║                    ✓ SERVIDOR ACTIVO                     ║" -ForegroundColor Green
    Write-Host "╠═══════════════════════════════════════════════════════════╣" -ForegroundColor Magenta
    Write-Host "║                                                           ║" -ForegroundColor Magenta
    Write-Host "║  🌐 URL: http://localhost:8080                           ║" -ForegroundColor Cyan
    Write-Host "║  📁 Logs: logs/server.log                                ║" -ForegroundColor Yellow
    Write-Host "║  🔑 PID: $($serverJob.Id)                                        ║" -ForegroundColor Yellow
    Write-Host "║                                                           ║" -ForegroundColor Magenta
    Write-Host "╠═══════════════════════════════════════════════════════════╣" -ForegroundColor Magenta
    Write-Host "║              CREDENCIALES DE PRUEBA                       ║" -ForegroundColor Cyan
    Write-Host "╠═══════════════════════════════════════════════════════════╣" -ForegroundColor Magenta
    Write-Host "║                                                           ║" -ForegroundColor Magenta
    Write-Host "║  👤 Admin:                                               ║" -ForegroundColor Magenta
    Write-Host "║     Usuario: admin@local                                 ║" -ForegroundColor Green
    Write-Host "║     Contraseña: admin123                                 ║" -ForegroundColor Green
    Write-Host "║                                                           ║" -ForegroundColor Magenta
    Write-Host "║  👨‍💼 Operador:                                            ║" -ForegroundColor Magenta
    Write-Host "║     Usuario: oper1@local                                 ║" -ForegroundColor Green
    Write-Host "║     Contraseña: op123                                    ║" -ForegroundColor Green
    Write-Host "║                                                           ║" -ForegroundColor Magenta
    Write-Host "╠═══════════════════════════════════════════════════════════╣" -ForegroundColor Magenta
    Write-Host "║                 COMANDOS ÚTILES                          ║" -ForegroundColor Yellow
    Write-Host "╠═══════════════════════════════════════════════════════════╣" -ForegroundColor Magenta
    Write-Host "║                                                           ║" -ForegroundColor Magenta
    Write-Host "║  Ver logs:     Get-Content logs/server.log -Wait -Tail 50║" -ForegroundColor Cyan
    Write-Host "║  Detener:      .\stop.ps1                                ║" -ForegroundColor Cyan
    Write-Host "║  Reiniciar:    .\stop.ps1; .\start.ps1                  ║" -ForegroundColor Cyan
    Write-Host "║                                                           ║" -ForegroundColor Magenta
    Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Magenta
    Write-Host ""
    
    # Opción de abrir navegador
    $openBrowser = Read-Host "¿Desea abrir el navegador automáticamente? (s/n)"
    if ($openBrowser -eq "s" -or $openBrowser -eq "S") {
        Start-Process "http://localhost:8080"
    }
    
    # Modo seguimiento de logs
    Write-Host ""
    $viewLogs = Read-Host "¿Desea ver los logs en tiempo real? (s/n)"
    if ($viewLogs -eq "s" -or $viewLogs -eq "S") {
        Write-Host "Mostrando logs (Ctrl+C para salir)..." -ForegroundColor Cyan
        Write-Host ""
        Get-Content "logs/server.log" -Wait -Tail 50
    }
} else {
    Write-Host "✗ Error al iniciar el servidor" -ForegroundColor Red
    Write-Host "Revisa el log para más detalles:" -ForegroundColor Yellow
    if (Test-Path "logs/server.log") {
        Get-Content "logs/server.log" -Tail 30
    }
    if (Test-Path "logs/server-error.log") {
        Write-Host "`nErrores:" -ForegroundColor Red
        Get-Content "logs/server-error.log" -Tail 20
    }
    Stop-Process -Id $serverJob.Id -Force -ErrorAction SilentlyContinue
    exit 1
}
