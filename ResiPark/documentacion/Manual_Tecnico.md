# Manual técnico — ResiPark

## 1. Requisitos
- JDK 17 o superior.
- Apache NetBeans con soporte para proyectos Java/Ant.
- Sistema operativo con entorno gráfico para ejecutar Swing.

## 2. Arquitectura
El punto de entrada es `resipark.ResiPark`. La interfaz está construida manualmente con Java Swing y organiza el sistema en seis pestañas: Parqueo, Residentes y vehículos, Entrada, Salida, Eventos y Garitas/estado. La lógica y las estructuras se encuentran implementadas como clases internas para facilitar la navegación del proyecto.

## 3. Estructuras de datos
- `ListaResidentes`: lista doblemente enlazada; cada nodo contiene un residente y referencias anterior/siguiente.
- `ListaVehiculos`: lista simplemente enlazada por residente.
- `ListaCircular`: lista circular de espacios para cada área; cada espacio tiene referencia al siguiente espacio de su área.
- `Espacio`: además de su enlace circular, mantiene enlaces arriba, abajo, izquierda y derecha para representar la distribución ortogonal.
- `ColaVehiculos`: cola FIFO enlazada usada tanto para entrada como para salida. Sus métodos sincronizados usan `wait()` y `notifyAll()`.
- `PilaEventos`: pila LIFO de eventos, mostrando primero el evento más reciente.

No se usan `ArrayList`, `LinkedList`, `Stack`, `Queue`, `Deque` ni `HashMap` para almacenar las estructuras requeridas.

## 4. Concurrencia
Se crean tres hilos con funciones reales: `Garita-Entrada-1`, `Garita-Entrada-2` y `Garita-Salida`. Los dos primeros consumen la misma cola de entrada; el tercero consume la cola de salida. Cada atención simula entre uno y tres segundos. Las asignaciones y liberaciones de espacios están protegidas por sincronización, y las actualizaciones de la interfaz se programan con `SwingUtilities.invokeLater`.

## 5. Reglas de negocio
- Cada residente tiene un ID único, nombre, casa/lote, condición de socio y lista propia de vehículos.
- Un residente puede registrar como máximo tres vehículos.
- La placa se valida como única entre residentes y visitantes activos.
- Un socio busca primero en A–C y, si no hay espacio, puede usar E–I.
- Un no socio y un visitante solo pueden usar E–I.
- Un vehículo estacionado conserva su espacio mientras espera en la cola de salida.
- Los visitantes son temporales y no se guardan en archivos.

## 6. Persistencia
Los archivos se leen al iniciar y se escriben al cerrar la ventana:
- `residentes.txt`: `ID|NOMBRE|CASA|SOCIO` (S/N).
- `vehiculos.txt`: `PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE`.

Los estados de los vehículos no se guardan; al iniciar, todos los vehículos persistidos quedan en estado `FUERA` y el parqueo vacío. Las líneas inválidas o que exceden el límite de vehículos se omiten y se registran en la pila de eventos.

## 7. Compilación
En NetBeans: abrir la carpeta del proyecto y ejecutar **Run Project (F6)**. Para generar el JAR, usar **Clean and Build**. El archivo se genera en `dist/ResiPark.jar`.
