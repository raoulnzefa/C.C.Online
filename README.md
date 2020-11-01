# C.C.Online
online shopping mall, Centro comercial-ventas online.
- El proyecto se realiza con front-end y back-end separados programando con el link de RESTful API modo.
- El portal de la página web se usa vue.js y live server 
- El management para administrador se aplica SPA(Single-Page Application) basado en vue.js y vuetify y empaquetado con webpack.
- Leyou Mall es una página web de e-commerce shopping website (B2C), que incluye una categoría completa.
- Los usuarios pueden buscar productos, hacer compras on line, añadir artículos al carrito, hacer pedidos y pagarlos. También se pueden consultar el seguimiento y dejar comentarios.
- Los administradores pueden manejar el alta y baja de productos, CRUD categorías, marcas, productos y especificaciones.
- Se aplican micro-servicios para soportar una gran cantidad de visitas.
  
La estructura del proyecto:
-
![The project structure is:](https://github.com/Nereitay/C.C.Online/blob/master/image/Project%20Structure.jpg)

El proyecto se practican con 12 microservicios:
-
![microservicios:](https://github.com/Nereitay/C.C.Online/blob/master/image/microservicios.png)

Zuul gateway-service:
- 
![zuul](https://github.com/Nereitay/C.C.Online/blob/master/image/zuul.png)

JWT＆RSA　Auth-service:
-
![auth](https://github.com/Nereitay/C.C.Online/blob/master/image/Rsa.png)

Cesta　Cart-service:
-
![cart](https://github.com/Nereitay/C.C.Online/blob/master/image/cart.png)

La Interpretacion de tecnología:
- La combinación de tecnologías anterior puede resolver los siguientes problemas típicos en el e-commerce proyecto.
- El nodo. js y Vue.js se aplican para realizar el desarrollo de front-end y back-end por separado.
- Se basa en SpringBoot2.0 y la versión de SpringCloud finchley.rc1 para realizar los microservicios.
- Se utiliza los conceptos de SPU y SKU del sector de e-commerce para mejorar la gestión de productos.
- Resuelve el problema del almacenamiento de archivos distribuido basado en FastDFS,
- Búsqueda de filtro inteligente para productos básicos basada en la función de agregación avanzada de Elasticsearch.
- Logre estadísticas complejas e informe de resultados de negocios de ventas basados en la función de agregación avanzada de Elasticsearch.
- Guarda el carrito de compras del cliente sin iniciar la sección basado en Local Storage con el motivo de reducir la presión sobre el servidor.
- Realice un inicio de sesión único sin estado basado en la tecnología JWT y el cifrado asimétrico RSA.
- Combinado con el cifrado asimétrico JWT y RSA, el filtro Feign personalizado puede realizar la autenticación automática entre servicios y resolver el problema de seguridad de la exposición del servicio.
- Basado en la función de SMS servicio de Alidayu, para resolver el problema de la notificación por SMS.
- se implementa un servicio de mensajes confiable basado en RabbitMQ para resolver el problema de las transacciones distribuidas y de la comunicación entre servicios.
- WeChat SDK se utiliza para realizar el pago de escaneo de WeChat.
- Cree un clúster de alta disponibilidad basado en Redis y realice un servicio de almacenamiento en caché confiable, es decir, almacenamiento de "hot data".
- Basado en Redis y Mq para escenarios de alta disponibilidad y alta concurrencia.
- Realice una plantilla de página estática basada en Thymeleaf, mejorar la velocidad de respuesta de la página y la capacidad de concurrencia.
- Balance de carga de solicitud preliminar y limitación de flujo de solicitud basado en Nginx.


Administración:
-
![manage:](https://github.com/Nereitay/C.C.Online/blob/master/image/manage.png)



Search service:
-
![search:](https://github.com/Nereitay/C.C.Online/blob/master/image/search.png)
![search2:](https://github.com/Nereitay/C.C.Online/blob/master/image/search2.png)
Static page web:
-
![goods:](https://github.com/Nereitay/C.C.Online/blob/master/image/goods.png)

Login & Register:
-
![login:](https://github.com/Nereitay/C.C.Online/blob/master/image/Login.png)
![register:](https://github.com/Nereitay/C.C.Online/blob/master/image/register.png)

Mi cesta:
-
![cesta:](https://github.com/Nereitay/C.C.Online/blob/master/image/cesta.png)

Mi pedido:
-
![pedido:](https://github.com/Nereitay/C.C.Online/blob/master/image/pedido.png)

Modificar y añadir dirección de entrega:
-
![direccion:](https://github.com/Nereitay/C.C.Online/blob/master/image/direccion.png)

pagado:
-
![pagado:](https://github.com/Nereitay/C.C.Online/blob/master/image/pagado.png)

Mi cuenta:
-
![cuenta:](https://github.com/Nereitay/C.C.Online/blob/master/image/cuenta.png)

El detalle del pedido:
-
![detalle:](https://github.com/Nereitay/C.C.Online/blob/master/image/detalle.png)



