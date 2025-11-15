@echo off
REM Script para iniciar el Sistema de Gestión de Desastres
REM Uso: Doble clic en este archivo o ejecutar "run.bat" desde la terminal

echo.
echo ========================================
echo   Sistema de Gestion de Desastres
echo ========================================
echo.

REM Cambiar al directorio del script
cd /d "%~dp0"

echo Verificando puerto 8080...
echo.

REM Detener cualquier proceso Java existente en el puerto 8080
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo Deteniendo proceso anterior en puerto 8080...
    taskkill /F /PID %%a >nul 2>&1
)

echo Iniciando servidor...
echo.

REM Ejecutar Maven
call mvn clean compile exec:java -Dexec.mainClass="co.edu.uniquindio.poo.app.MainServer"

pause
