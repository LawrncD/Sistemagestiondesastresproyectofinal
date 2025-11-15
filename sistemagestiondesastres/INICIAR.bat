@echo off
chcp 65001 >nul
title Sistema de Gestión de Desastres - Servidor

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                                                            ║
echo ║     🌍 SISTEMA DE GESTIÓN DE DESASTRES NATURALES 🌍       ║
echo ║                                                            ║
echo ║            Universidad del Quindío - 2025                  ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

echo [1/4] Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ✗ Java no encontrado. Instala Java 17+
    pause
    exit /b 1
)
echo ✓ Java encontrado

echo [2/4] Verificando Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ✗ Maven no encontrado. Instala Maven
    pause
    exit /b 1
)
echo ✓ Maven encontrado

echo [3/4] Liberando puerto 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    taskkill /F /PID %%a >nul 2>&1
)
echo ✓ Puerto 8080 disponible

echo [4/4] Compilando proyecto...
call mvn clean compile -q
if errorlevel 1 (
    echo ✗ Error en compilación
    pause
    exit /b 1
)
echo ✓ Compilación exitosa

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║  🚀 Iniciando servidor en puerto 8080...                 ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

if not exist logs mkdir logs

echo Servidor iniciando...
echo Presiona Ctrl+C para detener
echo.
echo ════════════════════════════════════════════════════════════
echo.
echo   🌐 URL: http://localhost:8080
echo.
echo   🔑 CREDENCIALES:
echo      Admin:    admin@local / admin123
echo      Operador: oper1@local / op123
echo.
echo ════════════════════════════════════════════════════════════
echo.

REM Abrir navegador después de 5 segundos
start /B timeout /t 5 /nobreak >nul 2>&1 && start http://localhost:8080

REM Iniciar servidor
mvn exec:java "-Dexec.mainClass=co.edu.uniquindio.poo.app.MainServer"

echo.
echo Servidor detenido.
pause
