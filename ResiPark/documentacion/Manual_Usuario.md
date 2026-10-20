# Manual de usuario — ResiPark

## Inicio
Abra el proyecto en Apache NetBeans y presione **F6**. La aplicación carga los residentes y vehículos desde los archivos de texto disponibles en el directorio de trabajo.

## Residentes y vehículos
1. Escriba un ID único, nombre y casa/lote.
2. Marque “Residente socio” si corresponde y pulse **Registrar residente**.
3. Para agregar un vehículo, coloque el ID del residente y complete placa, marca, modelo, color y tipo; pulse **Agregar vehículo**.
4. Use **Buscar ID** para consultar la información. Puede modificar los datos o eliminar al residente si todos sus vehículos están fuera.
5. Para eliminar un vehículo, escriba su placa en el campo Placa y pulse **Eliminar vehículo**. Solo se permite si está en estado `FUERA`.

## Ingreso de vehículos registrados
En la pestaña **Entrada**, escriba una placa registrada y pulse **Enviar a cola de entrada**. Una de las dos garitas procesa la solicitud. El resultado se observa en el parqueo y en el historial de eventos.

## Ingreso de visitantes
Complete nombre del visitante, placa y el ID del residente al que visita. Pulse **Registrar y enviar**. El ID del residente debe existir. El visitante no se persiste en archivos y se descarta cuando sale o es rechazado.

## Parqueo
Los espacios verdes están libres y los rojos ocupados. Las filas A, B y C pertenecen al área de socios; las filas E, F, G, H e I pertenecen al área general/visitantes. La fila D no existe.

## Solicitar salida
Escriba la placa de un vehículo estacionado en la pestaña **Salida** y pulse **Solicitar salida**. El vehículo espera en la cola sin liberar su espacio. La garita de salida lo libera cuando termina de atenderlo.

## Historial y estado
- **Eventos:** muestra los eventos del más reciente al más antiguo.
- **Garitas / estado:** muestra qué vehículo atiende cada hilo y el tamaño de las colas.
- **Actualizar vista:** actualiza manualmente las vistas si lo necesita.

## Cierre
Cierre la ventana para guardar automáticamente residentes y vehículos. No cierre el proceso forzadamente si desea conservar los cambios.
