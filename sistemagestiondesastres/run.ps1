# Script PowerShell para iniciar el Sistema de Gestión de Desastres
# Uso: .\run.ps1 o botón derecho > Ejecutar con PowerShell

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sistema de Gestion de Desastres" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Cambiar al directorio del script
Set-Location -Path $PSScriptRoot

Write-Host "Verificando puerto 8080..." -ForegroundColor Yellow

# Detener cualquier proceso Java existente
$javaProcesses = Get-Process -Name java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "Deteniendo procesos Java anteriores..." -ForegroundColor Yellow
    $javaProcesses | Stop-Process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}

Write-Host "Iniciando servidor..." -ForegroundColor Green
Write-Host ""

# Ejecutar Maven
& mvn clean compile exec:java "-Dexec.mainClass=co.edu.uniquindio.poo.app.MainServer"

Read-Host -Prompt "Presiona Enter para cerrar"
