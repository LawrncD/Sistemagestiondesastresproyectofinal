#!/bin/bash
# Script Bash para iniciar el Sistema de Gestión de Desastres
# Uso: ./run.sh

echo ""
echo "========================================"
echo "  Sistema de Gestion de Desastres"
echo "========================================"
echo ""
echo "Iniciando servidor..."
echo ""

# Cambiar al directorio del script
cd "$(dirname "$0")"

# Ejecutar Maven
mvn clean compile exec:java -Dexec.mainClass="co.edu.uniquindio.poo.app.MainServer"

read -p "Presiona Enter para cerrar"
