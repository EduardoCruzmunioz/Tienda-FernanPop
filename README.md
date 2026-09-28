# Tienda FernanPop 🛒

Plataforma de E-commerce desarrollada en Java puro, implementando una interfaz de consola con una arquitectura limpia (MVC) y enfocada en los principios de la Programación Orientada a Objetos (POO).

## 🏗️ Arquitectura y Toma de Decisiones

Durante el desarrollo de este proyecto, se han tomado decisiones arquitectónicas clave para asegurar que el código sea escalable, mantenible y profesional:

### 1. Programación Orientada a Objetos Pura (Domain-Driven Design)
Se ha huido del típico "Controlador Monolítico" (un controlador que almacena y gestiona absolutamente todos los datos). En su lugar, se ha distribuido la responsabilidad hacia los modelos:
- El **`Cliente`** es dueño y responsable de su propio `carrito` de compras y de su `historialPedidos`.
- El **`Trabajador`** posee su propia lista de `pedidosAsignados`.
- El **`Pedido`** encapsula su propia colección interna de productos y cantidades compradas.
De esta manera, el `TiendaController` actúa como un verdadero director de orquesta que enlaza las peticiones, delegando la carga de datos en los verdaderos protagonistas.

### 2. Polimorfismo Real
Toda la gestión de usuarios y catálogo de la tienda aprovecha la herencia y el polimorfismo:
- **Catálogo:** Se diferencia entre `ProductoFisico` (peso, coste de envío) y `ProductoDigital` (tamaño, tipo de licencia), pero ambos son tratados como `Producto` por el carrito de la compra y el inventario.
- **Roles:** El sistema trabaja con la clase abstracta `Usuario`, lo que permite que el sistema de Login devuelva de forma transparente a un `Administrador`, `Trabajador` o `Cliente`, otorgando menús y poderes distintos en base a su instancia.

### 3. Persistencia Desacoplada y Volátil
Para el guardado de datos mediante archivos `.csv`, se han creado interfaces separadas (`IPersistenciaUsuarios`, etc.) implementadas por clases "Gestor". Para ahorrar memoria, **estos gestores no son atributos del controlador**. Nacen en el momento exacto en que el servidor arranca (`cargarDatos`) o se apaga (`guardarDatos`), leen o escriben el disco, y se autodestruyen (*Garbage Collection*) liberando RAM durante toda la ejecución de la app.

### 4. Sembrado de Datos Automático (Seeding)
El sistema está diseñado para ser clonado y ejecutado por cualquier persona sin requerir bases de datos previas. Si al arrancar detecta que la carpeta `data/` o los CSVs están vacíos, automáticamente **crea las carpetas necesarias y genera semillas de prueba** (Admin raíz, un trabajador de logística, clientes de prueba y un catálogo mixto de 6 productos) para que la tienda sea 100% funcional en el primer inicio.

### 5. Configuraciones Globales (.properties)
Las rutas estáticas hacia los archivos de persistencia han sido erradicadas del código fuente. Todo el almacenamiento depende de la lectura del archivo en raíz `config.properties`, centralizando las modificaciones a través del Singleton `Config.java`.

### 6. UI/UX en Consola Estricta
Pese a estar en terminal, se implementó un formato estricto de recuadros de **50 caracteres de ancho**, logrando un diseño visualmente atractivo para los listados de productos y los carritos de la compra simulando "tarjetas" UI.

## 🚀 Funcionalidades Destacadas

*   **Lógica de Carrito Blindada**: Se valida el stock antes, durante y después de la compra (Soft-booking y Hard-booking).
*   **Workflow Logístico Inquebrantable**: Un pedido debe viajar del estado *PENDIENTE* pasando obligatoriamente por un ciclo de empaquetado hasta llegar a *ENVIADO*. Una vez enviado, el estado se bloquea en piedra para siempre.
*   **God-Mode del Admin**: El administrador general tiene el poder absoluto de visualizar cualquier pedido de cualquier empleado, arrebatar la asignación y auto-asignárselo, o cambiar a la fuerza un pedido problemático.
*   **Soft Deletes**: Los productos y usuarios dados de baja nunca se eliminan físicamente (para no romper las referencias a los historiales de compra del pasado). Simplemente su booleano `activo` pasa a `false`.

## 🏆 Extra Elegido (Ampliación)

De acuerdo a las opciones planteadas para subir nota, se ha decidido **implementar CUATRO extras**, priorizando la **Estadísticas Avanzadas de Tienda** y el **Sistema de Roles** como ejes funcionales principales:

1. **Estadísticas de Tienda (Streams Avanzados)**:
   *Justificación*: Se ha añadido la "Opción 9" al menú del Administrador que procesa todo el historial de pedidos en tiempo real. Utilizando exclusivamente *Java Streams API*, `flatMap`, y `Collectors.groupingBy`, la aplicación es capaz de cruzar datos relacionales complejos en una sola pasada para determinar:
   - El Producto más vendido de la plataforma.
   - El Ticket Medio (Gasto promedio por pedido).
   - El Usuario con más actividad/pedidos registrados.
   Se ha elegido esta opción porque pone a prueba el uso avanzado de colecciones y lambdas en Java y añade gran valor analítico de negocio.

2. **Sistema de Roles (Herencia)**:
   *Justificación*: Implementado mediante la superclase `Usuario` que se subdivide en `Administrador`, `Trabajador` y `Cliente`. Permite que el sistema ofrezca distintos flujos y permisos de interfaz dependiendo de quién inicia sesión (God-Mode, Operario Logístico, o Consumidor).

3. **Tests Unitarios (JUnit 5)**:
   *Justificación*: Se ha creado una suite de pruebas para el controlador (`TiendaControllerTest`), evaluando toda la lógica de soft-booking y validaciones de carrito/asignación, demostrando solidez técnica.

4. **Gestión del Proyecto con Maven**:
   *Justificación*: Configurado `pom.xml` en la raíz que maneja correctamente el target de compilación, empaquetado y las dependencias (JUnit y Jupiter Engine) haciendo el sistema replicable y estándar.

---
*Desarrollado y estructurado enfocándose en las mejores prácticas de Clean Code y SOLID.*
