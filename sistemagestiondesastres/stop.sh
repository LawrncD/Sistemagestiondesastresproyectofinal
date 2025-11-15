#!/bin/bash

# ========================================
# Script de detención - Sistema de Gestión de Desastres
# ========================================

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║${NC}  🛑 Deteniendo servidor...            ${BLUE}║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Leer PID guardado
if [ -f .server.pid ]; then
    SERVER_PID=$(cat .server.pid)
    
    if ps -p $SERVER_PID > /dev/null 2>&1; then
        echo -e "${YELLOW}→${NC} Deteniendo proceso $SERVER_PID..."
        kill $SERVER_PID
        sleep 2
        
        # Verificar si sigue corriendo
        if ps -p $SERVER_PID > /dev/null 2>&1; then
            echo -e "${YELLOW}→${NC} Forzando detención..."
            kill -9 $SERVER_PID
            sleep 1
        fi
        
        if ! ps -p $SERVER_PID > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC} Servidor detenido correctamente"
            rm .server.pid
        else
            echo -e "${RED}✗${NC} No se pudo detener el servidor"
            exit 1
        fi
    else
        echo -e "${YELLOW}⚠${NC} El proceso $SERVER_PID ya no está corriendo"
        rm .server.pid
    fi
else
    # Buscar procesos de Java que puedan ser el servidor
    echo -e "${YELLOW}→${NC} Buscando procesos del servidor..."
    PIDS=$(lsof -ti:8080 2>/dev/null)
    
    if [ -z "$PIDS" ]; then
        echo -e "${YELLOW}⚠${NC} No se encontró ningún servidor corriendo en puerto 8080"
    else
        for PID in $PIDS; do
            echo -e "${YELLOW}→${NC} Deteniendo proceso $PID en puerto 8080..."
            kill -9 $PID 2>/dev/null
        done
        sleep 1
        echo -e "${GREEN}✓${NC} Procesos detenidos"
    fi
fi

echo ""
echo -e "${GREEN}✓${NC} Sistema detenido completamente"
