# Manual técnico — ResiPark

## 1. Objetivo
Administrar residentes, vehículos, visitantes temporales, espacios de estacionamiento y las colas de acceso/salida de un residencial mediante Java Swing y estructuras de datos implementadas manualmente.

## 2. Requisitos
- JDK 17 o superior.
- Apache NetBeans recomendado.
- Sistema operativo compatible con Java Swing.
- Sin base de datos, JavaFX ni dependencias externas.

## 3. Organización del código
- `src/resipark/ResiPark.java`: ventana principal, pestañas, formularios, validaciones de interacción y actualización visual.
- `src/resipark/ModeloResiPark.java`: entidades, estructuras manuales, reglas de parqueo, hilos, sincronización y persistencia.
- `tests/resipark/PruebasEstructuras.java`: pruebas básicas de estructuras.
- `residentes.txt` y `vehiculos.txt`: persistencia en texto plano.
- `documentacion/`: manuales e informe.

## 4. Estructuras de datos
1. **Lista doble de residentes:** nodos con enlaces anterior y siguiente; permite inserción, búsqueda, eliminación y recorrido inverso.
2. **Lista simple de vehículos:** cada residente mantiene su propia lista; se permiten hasta tres vehículos.
3. **Listas circulares del parqueo:** una lista con 75 espacios de socios y otra con 75 espacios generales. Cada lista recuerda el último espacio asignado y busca a partir del siguiente nodo.
4. **Matriz ortogonal de espacios:** los nodos de cada fila enlazan izquierda/derecha y las filas consecutivas enlazan arriba/abajo.
5. **Colas FIFO:** cola única de entrada compartida por dos garitas y cola de salida atendida por una garita.
6. **Pila LIFO:** historial de eventos; los eventos nuevos se colocan en el tope y se muestran primero.

## 5. Reglas principales
- Los socios intentan primero el área A–C; si no hay espacios disponibles allí, pueden usar el área general E–I.
- No socios y visitantes utilizan únicamente E–I.
- Solo vehículos en `FUERA` pueden solicitar entrada.
- Solo vehículos en `ESTACIONADO` pueden solicitar salida.
- Un vehículo en cola de salida conserva su espacio hasta ser atendido.
- Las placas deben ser únicas entre vehículos registrados y visitantes activos.
- No se puede eliminar un residente con vehículos activos ni cambiar su condición de socio mientras alguno de sus vehículos no esté `FUERA`.

## 6. Concurrencia
Se crean tres hilos: `Garita-Entrada-1`, `Garita-Entrada-2` y `Garita-Salida`. Las colas usan métodos sincronizados, `wait()` en un ciclo `while` y `notifyAll()` al insertar elementos. Cada atención dura entre uno y tres segundos. Las actualizaciones de la interfaz se solicitan mediante `SwingUtilities.invokeLater()`.

## 7. Persistencia
Los archivos usan UTF-8 y separadores `|`:
- `residentes.txt`: `ID|NOMBRE|CASA|SOCIO`.
- `vehiculos.txt`: `PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE`.

La aplicación carga los archivos al iniciar y los guarda al cerrar. Las líneas inválidas se omiten y se registran como eventos. Los estados de vehículos y los visitantes no se persisten.

## 8. Compilación
En NetBeans: abrir proyecto y ejecutar **Clean and Build**. También se puede usar Ant desde la carpeta del proyecto con `ant clean jar`. El resultado es `dist/ResiPark.jar`.

## 9. Pruebas
Compilar y ejecutar `PruebasEstructuras.java` para verificar las 150 plazas, enlaces horizontales/verticales, cola FIFO, lista doble y cierre de cola. Además, ejecutar las pruebas de interfaz descritas en el manual de usuario.

## 10. Diagrama de clases (resumen)
```text
ResiPark (JFrame)
  └── Sistema
       ├── ListaResidentes ⇄ Residente
       │                      └── ListaVehiculos → Vehiculo
       ├── Parqueo
       │    ├── ListaCircular (socios)
       │    └── ListaCircular (general)
       │          └── Espacio ↔ arriba/abajo/izquierda/derecha
       ├── ColaVehiculos (entrada)
       ├── ColaVehiculos (salida)
       └── PilaEventos → Evento
```
