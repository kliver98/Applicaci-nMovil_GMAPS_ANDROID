# Applicaci-nMovil_GMAPS_ANDROID
Aplicación hecha para el curso de app moviles 2020_1 por Kliver Daniel Girón Castro de la universidad ICESI

## Anotaciones para tener en cuenta. Leer por favor antes de usar.
1.	Para localizar la ubicación de la persona, se debe mantener presionado el botón de + (LongPress)
2.	Como dice que SIEMPRE la pantalla debe estar centrada en la posición del usuario con el gps, no se podría agregar/tocar marcadores fuera del rango donde el usuario esta -> por eso también está la opción (o el switch) para que no se mantenga la pantalla en el centro de la posición del usuario del gps en todo tiempo, solo cuando quiera el usuario. Switch “Seguir” ubicado en la parte inferior derecha
3.	La pantalla se mueve con la ubicación del gps del cliente, pero se demora bastante tiempo con el gps del dispositivo, si es con un gps de simulación lo hace instantáneamente. Después de uno o dos minutos, debe funcionar normal y se mueve la pantalla casi al instante de moverla de posición para centrarla en la ubicación del usuario.
4.	Cuando se cambia el nombre de un marcador, no se porque, lo actualiza después de volver a seleccionar el marcador.
5.	Hay partes (métodos) que parecen asíncronos cuando siquiera, se deberían ejecutar en orden las líneas de instrucciones de los métodos. No sé por qué. Por eso, cuando se aplica un cambio se refleja en la siguiente acción similar. Como lo explique arriba en el punto 4.
6.	El botón de + sirve para agregar un marcador, en la mitad del mapa de la pantalla donde el usuario este. Este nuevo marcador, se agregará en color verde y además se podrá mover (manteniendo sostenido el marcador) a diferencia de los rojos que se añaden por ubicación cuando se buscan en la barra de la parte superior.
7. Esta proyecto esta hecho por un estudiante, no es perfecto y tiene sus defectos por ejemplo, debe tener la ubicación encontrada del celular. Lo cual indica darle todos los permisos y debe asegurarse que le aparece el simbolo del muñeco con el aro abajo que define su posición.

### Link del vídeo de muestra de funcionamiento de la aplicación
https://drive.google.com/open?id=1_QmX8xOyWM3dm8Fgy-vijVVdV_RzyQZs
