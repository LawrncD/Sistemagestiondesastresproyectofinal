#!/bin/bash

# ========================================
# Script de inicio - Sistema de Gestión de Desastres
# Versión: 2.0.0
# ========================================

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Banner
echo -e "${CYAN}"
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                                                            ║"
echo "║     🌍 SISTEMA DE GESTIÓN DE DESASTRES NATURALES 🌍       ║"
echo "║                                                            ║"
echo "║            Universidad del Quindío - 2025                  ║"
echo "║                                                            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# Función para mostrar spinner
spinner() {
    local pid=$1
    local delay=0.1
    local spinstr='⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏'
    while ps -p $pid > /dev/null 2>&1; do
        local temp=${spinstr#?}
        printf " [%c]  " "$spinstr"
        local spinstr=$temp${spinstr%"$temp"}
        sleep $delay
        printf "\b\b\b\b\b\b"
    done
    printf "    \b\b\b\b"
}

# Verificar Java
echo -e "${BLUE}[1/5]${NC} Verificando Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo -e "${GREEN}✓${NC} Java $JAVA_VERSION encontrado"
    else
        echo -e "${RED}✗${NC} Se requiere Java 17 o superior (actual: Java $JAVA_VERSION)"
        exit 1
    fi
else
    echo -e "${RED}✗${NC} Java no encontrado. Por favor instala Java 17+"
    exit 1
fi

# Verificar Maven
echo -e "${BLUE}[2/5]${NC} Verificando Maven..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    echo -e "${GREEN}✓${NC} Maven $MVN_VERSION encontrado"
else
    echo -e "${RED}✗${NC} Maven no encontrado. Por favor instala Maven"
    exit 1
fi

# Verificar puerto 8080
echo -e "${BLUE}[3/5]${NC} Verificando puerto 8080..."
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo -e "${YELLOW}⚠${NC} Puerto 8080 en uso. Intentando liberar..."
    PID=$(lsof -ti:8080)
    kill -9 $PID 2>/dev/null
    sleep 2
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo -e "${RED}✗${NC} No se pudo liberar el puerto 8080"
        exit 1
    else
        echo -e "${GREEN}✓${NC} Puerto 8080 liberado"
    fi
else
    echo -e "${GREEN}✓${NC} Puerto 8080 disponible"
fi

# Compilar proyecto
echo -e "${BLUE}[4/5]${NC} Compilando proyecto..."
mvn clean compile > /tmp/maven-compile.log 2>&1 &
COMPILE_PID=$!
spinner $COMPILE_PID
wait $COMPILE_PID
COMPILE_EXIT=$?

if [ $COMPILE_EXIT -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Compilación exitosa"
else
    echo -e "${RED}✗${NC} Error en compilación. Ver /tmp/maven-compile.log"
    tail -n 20 /tmp/maven-compile.log
    exit 1
fi

# Iniciar servidor
echo -e "${BLUE}[5/5]${NC} Iniciando servidor..."
echo ""
echo -e "${PURPLE}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${PURPLE}║${NC}  🚀 Servidor iniciando en puerto 8080...                 ${PURPLE}║${NC}"
echo -e "${PURPLE}╚═══════════════════════════════════════════════════════════╝${NC}"
echo ""

# Crear directorio de logs si no existe
mkdir -p logs

# Iniciar servidor en background
nohup mvn exec:java -Dexec.mainClass="co.edu.uniquindio.poo.app.MainServer" > logs/server.log 2>&1 &
SERVER_PID=$!

# Esperar a que el servidor inicie
echo -e "${CYAN}Esperando inicio del servidor...${NC}"
sleep 5

# Verificar si el servidor está corriendo
if ps -p $SERVER_PID > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Servidor iniciado correctamente (PID: $SERVER_PID)"
    echo ""
    
    # Guardar PID
    echo $SERVER_PID > .server.pid
    
    # Información de acceso
    echo -e "${PURPLE}╔═══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${PURPLE}║${NC}                    ${GREEN}✓ SERVIDOR ACTIVO${NC}                     ${PURPLE}║${NC}"
    echo -e "${PURPLE}╠═══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${PURPLE}║${NC}                                                           ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  🌐 URL: ${CYAN}http://localhost:8080${NC}                         ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  📁 Logs: ${YELLOW}logs/server.log${NC}                             ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  🔑 PID: ${YELLOW}$SERVER_PID${NC}                                      ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}                                                           ${PURPLE}║${NC}"
    echo -e "${PURPLE}╠═══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${PURPLE}║${NC}              ${CYAN}CREDENCIALES DE PRUEBA${NC}                     ${PURPLE}║${NC}"
    echo -e "${PURPLE}╠═══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${PURPLE}║${NC}                                                           ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  👤 Admin:                                               ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}     Usuario: ${GREEN}admin@local${NC}                               ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}     Contraseña: ${GREEN}admin123${NC}                              ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}                                                           ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  👨‍💼 Operador:                                            ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}     Usuario: ${GREEN}oper1@local${NC}                               ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}     Contraseña: ${GREEN}op123${NC}                                  ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}                                                           ${PURPLE}║${NC}"
    echo -e "${PURPLE}╠═══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${PURPLE}║${NC}                 ${YELLOW}COMANDOS ÚTILES${NC}                        ${PURPLE}║${NC}"
    echo -e "${PURPLE}╠═══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${PURPLE}║${NC}                                                           ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  Ver logs:     ${CYAN}tail -f logs/server.log${NC}                 ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  Detener:      ${CYAN}./stop.sh${NC} o ${CYAN}kill $SERVER_PID${NC}          ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}  Reiniciar:    ${CYAN}./stop.sh && ./start.sh${NC}                 ${PURPLE}║${NC}"
    echo -e "${PURPLE}║${NC}                                                           ${PURPLE}║${NC}"
    echo -e "${PURPLE}╚═══════════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    # Opción de abrir navegador
    read -p "¿Desea abrir el navegador automáticamente? (s/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[SsYy]$ ]]; then
        if command -v xdg-open &> /dev/null; then
            xdg-open http://localhost:8080
        elif command -v open &> /dev/null; then
            open http://localhost:8080
        elif command -v start &> /dev/null; then
            start http://localhost:8080
        else
            echo -e "${YELLOW}⚠${NC} No se pudo abrir el navegador automáticamente"
        fi
    fi
    
    # Modo seguimiento de logs
    echo ""
    read -p "¿Desea ver los logs en tiempo real? (s/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[SsYy]$ ]]; then
        echo -e "${CYAN}Mostrando logs (Ctrl+C para salir)...${NC}"
        echo ""
        tail -f logs/server.log
    fi
    
else
    echo -e "${RED}✗${NC} Error al iniciar el servidor"
    echo -e "${YELLOW}Revisa el log para más detalles:${NC}"
    cat logs/server.log
    exit 1
fi
