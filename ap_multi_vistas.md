voy a hacer una prueba tecnica para desarrollador full stack:
Validar tus conocimientos y técnicas en desarrollo de aplicaciones en las tecnologías JAVA, SPRING BOOT y
REACT y despliegue en AWS. No está descrito en la prueba, pero tener en cuenta las buenas prácticas
de desarrollo (Principios SOLID, pruebas unitarias, documentación, entre otros), estilos y patrones de
arquitectura.
Descripción de la prueba: Construir una aplicación que exponga las siguientes vistas:
a) Vista Empresa con un formulario que capture la siguiente información:
• NIT (Llave primaria).
• Nombre de la empresa.
• Dirección.
• Teléfono.

b) Vista de Productos con un formulario que captura la siguiente información:
• Código.
• Nombre del producto.
• Características.
• Precio en varias monedas.
• Empresa.
c) Vista de Inicio de Sesión con un formulario que capture la información del usuario: correo y contraseña.
d) Vista de Inventario con un formulario que permita la descargar de un PDF con la información de esa
tabla y adicional utilizar alguna API de AWS para poder enviar ese PDF a un correo deseado.
e) Deben existir dos tipos de usuarios:
• Administrador: Tiene acceso a las funciones de eliminación, registro y/o edición de una Empresa.
Adicionalmente, este usuario podrá registrar productos por empresa y guardarlos en una tabla
inventario, donde se vean los productos por empresa.
• Externo: Puede visualizar las empresas como visitante.
f) El modelo entidad-relación de la base de datos para guardar la información anterior debe contener:
Empresa, Productos, Categorías, Clientes y Ordenes. Asegúrate que la Base de Datos que plantees cumpla
con los siguientes requisitos:
• Un Producto puede pertenecer a múltiples Categorías.
• Un Cliente puede tener múltiples Órdenes.
• Las ordenes pueden tener múltiples Productos.
g) La contraseña utilizada debe estar encriptada para autenticación del Usuario Administrador.
h) Publique tu aplicación en un servidor en la nube de AWS.
Entregables:
• Enlace de la aplicación desplegada en AWS.
• Usuario y contraseña de los tipos de usuario Administrador y Externo.
• Enlace del repositorio donde está almacenado el código fuente.
• Readme de todo el proyecto