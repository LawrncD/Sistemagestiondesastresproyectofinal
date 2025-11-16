# 📦 Package: services

Este package contiene servicios auxiliares y utilidades del sistema.

## Clases

### 1. ValidationService
**Propósito**: Validación de datos de entrada

**Métodos principales**:
- `isValidEmail(String)` - Valida formato de email
- `isValidPhone(String)` - Valida formato de teléfono
- `isValidPassword(String)` - Valida contraseña (mínimo 6 caracteres)
- `isValidColombianCoordinates(double, double)` - Valida coordenadas de Colombia
- `isInRange(double, double, double)` - Valida rango numérico
- `isPositive(int/double)` - Valida valores positivos

**Uso**:
```java
if (!ValidationService.isValidEmail(email)) {
    throw new IllegalArgumentException("Email inválido");
}
```

### 2. SecurityService
**Propósito**: Encriptación y seguridad de contraseñas

**Métodos principales**:
- `generateSalt()` - Genera salt aleatorio
- `hashPassword(String, String)` - Hashea contraseña con salt
- `verifyPassword(String, String, String)` - Verifica contraseña
- `hashPasswordWithSalt(String)` - Genera hash + salt automáticamente

**Uso**:
```java
// Al registrar usuario
String[] hashAndSalt = SecurityService.hashPasswordWithSalt(password);
usuario.setPasswordHash(hashAndSalt[0]);
usuario.setSalt(hashAndSalt[1]);

// Al verificar login
boolean valid = SecurityService.verifyPassword(
    passwordIngresado, 
    usuario.getPasswordHash(), 
    usuario.getSalt()
);
```

### 3. RouteOptimizationService
**Propósito**: Cálculo de rutas óptimas (algoritmo de Dijkstra)

**Métodos principales**:
- `findShortestPath(GrafoDirigido, String, String)` - Encuentra ruta más corta
- `calculatePathDistance(GrafoDirigido, List<String>)` - Calcula distancia total
- `findAlternativePaths(GrafoDirigido, String, String, int)` - Rutas alternativas

**Uso**:
```java
// Encontrar ruta más corta entre dos zonas
List<String> camino = RouteOptimizationService.findShortestPath(
    grafo, 
    "zona-origen-id", 
    "zona-destino-id"
);

// Calcular distancia total
double distancia = RouteOptimizationService.calculatePathDistance(grafo, camino);
```

## Características

✅ **Validaciones robustas** - Patrones regex optimizados  
✅ **Seguridad SHA-256** - Hash de contraseñas con salt  
✅ **Algoritmo Dijkstra** - Optimización de rutas  
✅ **Sin dependencias externas** - Solo Java estándar  
✅ **Métodos estáticos** - Fácil uso sin instancias  

## Integración con Servlets

El nuevo servlet **ApiOptimalRouteServlet** expone el servicio de rutas:

**Endpoint**: `GET /api/optimal-route?origen=ID&destino=ID`

**Respuesta**:
```json
{
  "origen": "zona-1",
  "destino": "zona-3",
  "camino": ["zona-1", "zona-2", "zona-3"],
  "distanciaTotal": 15.5,
  "numeroParadas": 2
}
```

## Próximas mejoras

- [ ] Implementar BCrypt para contraseñas
- [ ] Algoritmo A* con heurística
- [ ] K-shortest paths para rutas alternativas
- [ ] Cache de rutas calculadas
- [ ] Validación de CAPTCHA
- [ ] Rate limiting
