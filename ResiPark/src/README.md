# ResiPark — Control de accesos y estacionamiento

Proyecto Java Swing para IPC1. Requiere JDK 17 o superior. No utiliza bases de datos, JavaFX, frameworks externos ni colecciones de `java.util` para sustituir las estructuras solicitadas.

## Abrir en Apache NetBeans
1. Extrae el ZIP completo.
2. Abre Apache NetBeans.
3. Selecciona **File > Open Project** y elige la carpeta `ResiPark_NetBeans`.
4. Comprueba que el proyecto use JDK 17 o superior.
5. Ejecuta con **Run Project (F6)**.
6. Para generar de nuevo el ejecutable, selecciona **Clean and Build**. El archivo se genera en `dist/ResiPark.jar`.

## Ejecutar el JAR
Desde la carpeta del proyecto:

```bash
java -jar dist/ResiPark.jar
```

## Funciones
- Seis pestañas: Parqueo, Residentes y vehículos, Entrada, Salida, Eventos y Garitas/estado.
- 150 espacios: 75 para socios y 75 para el área general.
- Lista doble de residentes con recorrido hacia adelante y hacia atrás.
- Listas simples de vehículos, listas circulares por área, enlaces ortogonales, colas FIFO y pila LIFO, implementadas manualmente.
- Dos hilos de entrada y un hilo de salida, con atención simulada de 1 a 3 segundos.
- Visitantes temporales, validaciones de placas y persistencia UTF-8 en archivos de texto.

## Archivos de datos
`residentes.txt`: `ID|NOMBRE|CASA|SOCIO`

`vehiculos.txt`: `PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE`

Los visitantes y el estado del parqueo no se guardan. Al reiniciar, los vehículos registrados vuelven al estado `FUERA` y el parqueo inicia vacío.

## Pruebas incluidas
El archivo `tests/resipark/PruebasEstructuras.java` contiene comprobaciones básicas de las 150 plazas, enlaces ortogonales, cola FIFO, lista doble, cierre de cola y regla de visitantes. Desde la terminal, en la carpeta del proyecto, ejecuta `ant test`.

## Nota sobre la entrega
Este ZIP no contiene configuración ni instrucciones de GitHub. Antes de entregar, ejecuta las pruebas funcionales de la interfaz, toma capturas reales de cada pestaña y verifica el JAR en tu computadora. La documentación describe la implementación incluida; el informe debe ajustarse a las actividades que realmente realizaste.
