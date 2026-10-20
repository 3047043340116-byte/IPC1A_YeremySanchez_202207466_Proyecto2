# Manual de usuario — ResiPark

## 1. Iniciar el programa
Abre el proyecto en Apache NetBeans y pulsa **Run Project (F6)**. También puedes ejecutar `java -jar dist/ResiPark.jar` desde la carpeta del proyecto. Al iniciar se cargan residentes y vehículos desde los archivos de texto si existen.

## 2. Registrar un residente
1. Abre **Residentes y vehículos**.
2. Escribe ID, nombre y casa/lote.
3. Marca **Residente socio** si corresponde.
4. Pulsa **Registrar residente**.
5. El ID debe ser único. No uses `|` en los campos.

## 3. Buscar, modificar o eliminar un residente
- **Buscar ID**: muestra los datos y vehículos registrados.
- **Modificar datos**: permite cambiar nombre, casa y condición de socio. La condición de socio no se puede cambiar si alguno de sus vehículos está activo.
- **Eliminar residente**: solo se permite si todos sus vehículos están en estado `FUERA`.
- **Listar residentes al revés**: muestra la lista doble desde el último residente hasta el primero.

## 4. Agregar o eliminar vehículos
1. Selecciona un residente escribiendo su ID.
2. Completa placa, marca, modelo y color; selecciona el tipo.
3. Pulsa **Agregar vehículo**. Cada residente puede registrar hasta tres y cada placa debe ser única.
4. Para eliminar, escribe la placa en el campo correspondiente y pulsa **Eliminar vehículo**. Solo se eliminan vehículos en estado `FUERA`.

## 5. Registrar ingreso
- Para un vehículo registrado, en **Entrada** escribe la placa y pulsa **Enviar a cola de entrada**.
- Para un visitante, escribe nombre, placa e ID del residente visitado y pulsa **Registrar y enviar**.
- Dos garitas atienden la misma cola. El sistema busca primero un espacio permitido; si no existe, rechaza el ingreso y registra el evento.

## 6. Consultar el parqueo
En **Parqueo**, verde significa libre y rojo ocupado. El área de socios tiene A1–A25, B1–B25 y C1–C25. El área general tiene E1–E15, F1–F15, G1–G15, H1–H15 e I1–I15.

## 7. Solicitar salida
En **Salida**, escribe la placa de un vehículo estacionado y pulsa **Solicitar salida**. El vehículo se incorpora a la cola de salida y conserva su espacio hasta que la garita lo atienda. El botón **Ver estacionados** permite consultar las placas que están dentro.

## 8. Eventos y estado de garitas
- **Eventos**: muestra los eventos más recientes primero.
- **Garitas / estado**: muestra los vehículos que atienden las dos garitas de entrada y la de salida, además de la cantidad de espacios ocupados y vehículos en cola.
- **Actualizar vista**: refresca la información mostrada.

## 9. Cierre y persistencia
Cierra la ventana principal para guardar residentes y vehículos. Los visitantes no se guardan. Al volver a iniciar, el parqueo estará vacío y los vehículos registrados estarán en estado `FUERA`.

## 10. Evidencias que debe tomar el estudiante
Incluye capturas reales de la ejecución en estas situaciones:
1. Pestaña Parqueo con espacios libres.
2. Pestaña Residentes y vehículos con un residente y sus vehículos.
3. Pestaña Entrada con al menos un vehículo en cola o siendo atendido.
4. Pestaña Salida con un vehículo en cola.
5. Pestaña Eventos con eventos registrados.
6. Pestaña Garitas / estado mostrando los tres procesos.
7. Ejemplo de validación (placa duplicada, cuarto vehículo o salida de una placa no estacionada).

No uses capturas simuladas: toma las imágenes de la versión que ejecutaste.
