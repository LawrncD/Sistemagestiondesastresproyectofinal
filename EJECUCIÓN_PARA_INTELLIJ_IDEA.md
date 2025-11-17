# Configuración de Run Configuration Maven en IntelliJ IDEA

Este documento explica paso a paso cómo crear una configuración de ejecución (Run Configuration) en IntelliJ IDEA para ejecutar un proyecto Maven sin escribir el comando `mvn`.

---

## 1. Abrir las configuraciones de ejecución

1. En IntelliJ IDEA, dirígete a la esquina superior derecha.
2. Haz clic en la lista desplegable junto al botón **Run (▶)**.
3. Selecciona **Edit Configurations…**

---

## 2. Crear una configuración Maven

1. En la ventana que aparece, presiona el botón **+** en la parte superior izquierda.
2. Selecciona la opción **Maven**.

---

## 3. Configurar la ejecución
Dentro de Comand line se debe poner el siguiente comando: 
```
clean compile exec:java -Dexec.mainClass=co.edu.uniquindio.poo.app.MainServer
```


**Nota:** No se debe escribir `mvn clean ...` porque IntelliJ ya sabe que esta configuración es de Maven y ejecuta Maven internamente.

---

## 4. Guardar y ejecutar

1. Haz clic en **Apply**
2. Luego en **OK**
3. Selecciona tu nueva configuración “Run MainServer” en la esquina superior derecha.
4. Presiona **Run (▶)**.

---

## 5. ¿Por qué no se usa `mvn`?

En el terminal de Windows o en un archivo `.bat` se debe escribir:
```
mvn clean compile exec:java -Dexec.mainClass="..."
```

Pero en IntelliJ IDEA, cuando se crea una **Run Configuration tipo Maven**, el IDE ejecuta Maven automáticamente, así que solo necesita los *goals* y parámetros.

---

## Resultado

Con esta configuración podrás iniciar tu servidor Java Maven desde IntelliJ con un solo clic, sin depender de archivos `.bat` ni de tener Maven configurado en el PATH del sistema.

# Reporte de posibles bugs a la hora de ejecutar el proyecto con intellij idea

## 1.Contador de recursos en la pestaña de inicio descontrolado
Es posible que al ver el panel de inicio el cual muestra los datos generales sobre las zonas, rutas, recursos y evacuaciones, en la sección de recursos se evidencie un cambio aleatorio y bastante constante de su rspectivo contador, sin embargo esto no afecta el funcionamiento para todo lo relacionado con los recursos.

## 2.Pestaña de notificación estática
A la hora de iniciar por ejemplo una evacuación, dentro de la pestaña que se muestra al presionar sobre el icono de campana en el panel de inicio, se debe crear una notificación sobre la acción realizada, pero no sucede ningun cambio para este caso.

## Aclaración
El origen de estos bugs se desconoce, pero únicamente sucede si se ejecuta el proyecto dentro de intellij, para visual studio code no se presentan ninguno de los anteriores bugs. 
Por lo mismo se llegó a la conclusión de que probablemente el motivo este dentro de la propia aplicación o dispositivo más no dentro del proyecto creado.

La presencia de estos bugs no afecta en nada al funcionamiento del resto del sistema.