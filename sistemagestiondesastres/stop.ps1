# ========================================
# Script de detención - Sistema de Gestión de Desastres
# PowerShell Version
# ========================================

Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Blue
Write-Host "║  🛑 Deteniendo servidor...            ║" -ForegroundColor Blue
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Blue
Write-Host ""

# Leer Job ID guardado
if (Test-Path ".server.pid") {
    $jobId = Get-Content ".server.pid"
    
    $job = Get-Job -Id $jobId -ErrorAction SilentlyContinue
    if ($job) {
        Write-Host "→ Deteniendo Job $jobId..." -ForegroundColor Yellow
        Stop-Job -Id $jobId
        Remove-Job -Id $jobId -Force
        Write-Host "✓ Job detenido" -ForegroundColor Green
    } else {
        Write-Host "⚠ El Job $jobId ya no está corriendo" -ForegroundColor Yellow
    }
    
    Remove-Item ".server.pid" -ErrorAction SilentlyContinue
}

# Buscar y detener procesos en puerto 8080
Write-Host "→ Buscando procesos en puerto 8080..." -ForegroundColor Yellow
$connections = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

if ($connections) {
    $processIds = $connections | Select-Object -ExpandProperty OwningProcess -Unique
    
    foreach ($pid in $processIds) {
        Write-Host "→ Deteniendo proceso $pid en puerto 8080..." -ForegroundColor Yellow
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    }
    
    Start-Sleep -Seconds 2
    Write-Host "✓ Procesos detenidos" -ForegroundColor Green
} else {
    Write-Host "⚠ No se encontró ningún servidor corriendo en puerto 8080" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "✓ Sistema detenido completamente" -ForegroundColor Green
