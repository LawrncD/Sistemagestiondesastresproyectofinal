# 🚀 Guía de Ejecución - Sistema de Gestión de Desastres

## 📋 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

1. **Java 17 o superior**
   ```bash
   java -version
   ```
   Si no está instalado: https://adoptium.net/

2. **Maven 3.6 o superior**
   ```bash
   mvn -version
   ```
   Si no está instalado: https://maven.apache.org/download.cgi

## 🎯 Formas de Ejecutar el Proyecto

### **Método 1: Script Universal PowerShell (Recomendado para VS Code)** ⭐

Desde la terminal de Visual Studio Code (PowerShell):

```powershell
cd sistemagestiondesastres
.\run-universal.ps1
```

**Ventajas:**
- ✅ Funciona en cualquier PC con PowerShell
- ✅ Verifica automáticamente Java y Maven
- ✅ Libera el puerto 8080 si está ocupado
- ✅ Compila el proyecto automáticamente
- ✅ Abre el navegador automáticamente
- ✅ Muestra credenciales de acceso

### **Método 2: Script Batch (Windows)** 

Doble clic en el archivo:
```
INICIAR.bat
```

O desde terminal CMD:
```cmd
cd sistemagestiondesastres
INICIAR.bat
```

### **Método 3: Comando Maven Directo**

Para desarrollo rápido (si ya está compilado):

```bash
cd sistemagestiondesastres
mvn exec:java
```

Con compilación completa:
```bash
cd sistemagestiondesastres
mvn clean compile exec:java
```

### **Método 4: Scripts Alternativos**

**PowerShell (Windows):**
```powershell
.\start.ps1
```

**Bash (Linux/Mac):**
```bash
./start.sh
```

**Batch (Windows):**
```cmd
run.bat
```

## 🌐 Acceso a la Aplicación

Una vez iniciado el servidor, accede desde tu navegador:

```
http://localhost:8080
```

### 🔑 Credenciales de Acceso

**Administrador:**
- Usuario: `admin@local`
- Contraseña: `admin123`

**Operador de Emergencia:**
- Usuario: `oper1@local`
- Contraseña: `op123`

## 🛑 Detener el Servidor

- **Desde terminal:** Presiona `Ctrl + C`
- **Script de detención:** 
  - Windows: `stop.bat` o `stop.ps1`
  - Linux/Mac: `./stop.sh`

## 🔧 Solución de Problemas

### Error: "Puerto 8080 ocupado"

**Opción 1:** Usar el script universal (libera el puerto automáticamente)
```powershell
.\run-universal.ps1
```

**Opción 2:** Liberar manualmente el puerto
```powershell
# Ver qué proceso usa el puerto 8080
netstat -ano | findstr :8080

# Detener el proceso (reemplaza PID con el número del proceso)
taskkill /F /PID <PID>
```

### Error: "Java no encontrado"

Instala Java 17+:
1. Descargar de https://adoptium.net/
2. Agregar Java al PATH del sistema
3. Verificar con: `java -version`

### Error: "Maven no encontrado"

Instala Maven:
1. Descargar de https://maven.apache.org/download.cgi
2. Extraer y agregar `bin` al PATH del sistema
3. Verificar con: `mvn -version`

### Error de compilación

Limpiar y recompilar:
```bash
mvn clean compile
mvn exec:java
```

## 📁 Estructura del Proyecto

```
sistemagestiondesastres/
├── INICIAR.bat           # Script principal (Windows)
├── run-universal.ps1     # Script universal para VS Code
├── run.bat              # Script alternativo
├── start.ps1            # Inicio PowerShell
├── stop.ps1             # Detener servidor
├── pom.xml              # Configuración Maven
├── src/                 # Código fuente
│   ├── main/
│   │   ├── java/        # Clases Java
│   │   └── resources/   # Recursos web
│   └── test/            # Pruebas
└── target/              # Archivos compilados
```

## 💡 Consejos

1. **Primera ejecución:** Usa `INICIAR.bat` o `run-universal.ps1` para verificar todo
2. **Desarrollo rápido:** Usa `mvn exec:java` si ya está compilado
3. **VS Code Terminal:** Usa `run-universal.ps1` para mejor integración
4. **Producción:** Considera usar `mvn package` para crear un WAR

## 🎓 Comandos Útiles

```bash
# Compilar sin ejecutar
mvn compile

# Limpiar proyecto
mvn clean

# Ejecutar tests
mvn test

# Crear paquete WAR
mvn package

# Ver dependencias
mvn dependency:tree
```

## 📞 Soporte

Para más información, consulta:
- `README.md` - Documentación general
- `QUICKSTART.md` - Inicio rápido
- `REQUERIMIENTOS.md` - Requisitos del sistema
