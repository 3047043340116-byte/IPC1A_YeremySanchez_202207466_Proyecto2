# ResiPark — Proyecto IPC1, Segundo Semestre 2026

Proyecto Java Swing para gestionar accesos y parqueo residencial.

## Abrir en Apache NetBeans
1. Descomprima `ResiPark_NetBeans.zip` en una carpeta.
2. Abra Apache NetBeans.
3. Seleccione **File > Open Project** y elija la carpeta `ResiPark_NetBeans`.
4. Verifique que el proyecto use JDK 17 o superior.
5. Ejecute con **Run Project (F6)**.

También puede compilar desde la carpeta del proyecto con `ant jar` si Ant está instalado. El JAR se genera en `dist/ResiPark.jar`.

## Uso básico
- **Residentes y vehículos:** registre un residente usando un ID único; después use ese ID para agregar hasta tres vehículos.
- **Entrada:** escriba la placa de un vehículo registrado y envíelo a la cola. Para un visitante, complete nombre, placa y el ID del residente visitado.
- **Parqueo:** verde indica libre y rojo ocupado. Socios intentan primero el área A–C y, si se llena, el área general E–I. No socios y visitantes usan solamente E–I.
- **Salida:** ingrese la placa de un vehículo estacionado. El espacio se libera cuando lo atiende la garita de salida.
- **Eventos:** muestra el historial más reciente primero.
- Al cerrar la aplicación se guardan `residentes.txt` y `vehiculos.txt` en el directorio de trabajo. Al iniciar se cargan de nuevo; el estado de estacionamiento no se persiste.

## Estructuras y concurrencia
Se implementan manualmente lista doble de residentes, listas simples de vehículos, listas circulares por área, enlaces ortogonales entre espacios, colas FIFO de entrada/salida y pila LIFO de eventos. Dos hilos comparten la cola de entrada y un tercer hilo atiende la cola de salida; las colas usan `wait()`/`notifyAll()` y los hilos simulan la atención entre 1 y 3 segundos. Las actualizaciones de Swing se solicitan con `SwingUtilities.invokeLater`.

## Archivos de persistencia
- `residentes.txt`: `ID|NOMBRE|CASA|SOCIO` (S/N)
- `vehiculos.txt`: `PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE`

Los visitantes son temporales y no se guardan en esos archivos.

## Nota académica
Use este proyecto como base de trabajo: revise el código, pruébelo y asegúrese de poder explicar cada estructura, validación y sincronización durante la revisión. El enunciado exige autoría propia, repositorio con commits incrementales, manual técnico, manual de usuario e informe de desarrollo; el ZIP no sustituye esos entregables.
