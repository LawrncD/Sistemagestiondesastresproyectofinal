# ============================================
# Sistema de Gestión de Desastres
# Script Universal para VS Code Terminal
# ============================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sistema de Gestion de Desastres" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Cambiar al directorio del script
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

Write-Host "[1/5] Verificando Java..." -ForegroundColor Blue
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java encontrado" -ForegroundColor Green
} catch {
    Write-Host "✗ Java no encontrado. Instala Java 17+" -ForegroundColor Red
    Write-Host "Descarga: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

Write-Host "[2/5] Verificando Maven..." -ForegroundColor Blue
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "✓ Maven encontrado" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven no encontrado. Instala Maven" -ForegroundColor Red
    Write-Host "Descarga: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

Write-Host "[3/5] Verificando puerto 8080..." -ForegroundColor Blue
$process = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($process) {
    Write-Host "⚠ Puerto 8080 ocupado, liberando..." -ForegroundColor Yellow
    $processId = $process.OwningProcess
    Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}
Write-Host "✓ Puerto 8080 disponible" -ForegroundColor Green

Write-Host "[4/5] Compilando proyecto..." -ForegroundColor Blue
$compileResult = mvn clean compile -q 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error en compilación" -ForegroundColor Red
    Write-Host $compileResult -ForegroundColor Red
    exit 1
}
Write-Host "✓ Compilación exitosa" -ForegroundColor Green

Write-Host ""
Write-Host "╔═══════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  🚀 Iniciando servidor en puerto 8080...     ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "🌐 URL: " -NoNewline -ForegroundColor White
Write-Host "http://localhost:8080" -ForegroundColor Green
Write-Host ""
Write-Host "🔑 CREDENCIALES:" -ForegroundColor White
Write-Host "   Admin:    " -NoNewline -ForegroundColor White
Write-Host "admin@local / admin123" -ForegroundColor Yellow
Write-Host "   Operador: " -NoNewline -ForegroundColor White
Write-Host "oper1@local / op123" -ForegroundColor Yellow
Write-Host ""
Write-Host "═════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "[5/5] Iniciando servidor..." -ForegroundColor Blue
Write-Host "Presiona Ctrl+C para detener" -ForegroundColor Gray
Write-Host ""

# Abrir navegador después de 3 segundos en segundo plano
Start-Job -ScriptBlock {
    Start-Sleep -Seconds 3
    Start-Process "http://localhost:8080"
} | Out-Null

# Iniciar servidor
mvn exec:java "-Dexec.mainClass=co.edu.uniquindio.poo.app.MainServer"

Write-Host ""
Write-Host "Servidor detenido." -ForegroundColor Yellow
