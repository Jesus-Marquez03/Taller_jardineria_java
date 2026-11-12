# Sistema de gestión de Jardinería

Aplicación académica de escritorio para administrar una empresa de jardinería. Está construida con Java 21, Swing, Maven, JDBC y MySQL 8.4 en Docker. El objetivo es practicar programación orientada a objetos, arquitectura hexagonal simplificada, principios SOLID, colecciones, manejo de errores y persistencia relacional sin frameworks empresariales.

## Tecnologías

- **Java 21:** entidades, servicios, JDBC y aplicación Swing.
- **Maven:** compilación, dependencias y pruebas.
- **MySQL 8.4:** base de datos relacional compatible con el script del profesor.
- **Docker Compose:** entorno MySQL reproducible y con volumen persistente.
- **MySQL Connector/J:** controlador JDBC oficial.
- **JUnit 5:** pruebas unitarias e integración optativa.
- **Swing y Java 2D:** interfaz y gráfico sin librerías visuales externas.

No se utilizan Spring Boot, Hibernate, JPA, JavaFX, Lombok ni generadores de interfaz.

## Requisitos

- Windows 10/11 con PowerShell.
- JDK 21 (`java -version`).
- Apache Maven (`mvn -version`).
- Docker Desktop con Docker Compose (`docker compose version`).

## Estructura

```text
Taller_jardineria_java/
├── .gitignore
├── README.md
└── jardineria/
    ├── .env.example
    ├── compose.yaml
    ├── pom.xml
    ├── database/
    │   ├── jardineria.sql
    │   ├── datos-demo.sql
    │   └── requerimientos-proyecto-jardineria.md
    └── src/
        ├── main/
        │   ├── java/com/taller/
        │   │   ├── Main.java
        │   │   ├── domain/
        │   │   │   ├── model/
        │   │   │   ├── exception/
        │   │   │   └── ports/out/
        │   │   ├── application/
        │   │   │   ├── ports/in/
        │   │   │   ├── services/
        │   │   │   └── exception/
        │   │   └── infrastructure/
        │   │       ├── config/
        │   │       ├── persistence/
        │   │       └── ui/
        │   │           ├── Theme.java
        │   │           ├── SplashScreen.java
        │   │           └── componentes Swing
        │   └── resources/application.properties.example
        └── test/java/com/taller/
```

`target/`, `.env`, `application.properties`, archivos `.class` y temporales están excluidos de Git.

## Arquitectura hexagonal simplificada

- **Domain:** entidades puras, identificadores compuestos e interfaces de repositorio. No conoce Swing, JDBC ni MySQL.
- **Application:** servicios que validan reglas y coordinan los puertos de salida.
- **Infrastructure:** configuración, repositorios JDBC, composición de dependencias e interfaz Swing.
- **Main:** punto de entrada que crea la aplicación y muestra la ventana.

La inversión de dependencias se observa porque los servicios reciben interfaces de repositorio. Cada clase mantiene una responsabilidad concreta. `DatabaseConfig` es un Singleton que conserva configuración, pero cada operación solicita y cierra su propia conexión; no existe una conexión global permanente.

## Entidades y relaciones

| Entidad | Clave | Relación principal |
|---|---|---|
| Oficina | `codigo_oficina` | Tiene empleados |
| Empleado | `codigo_empleado` | Pertenece a oficina y puede tener jefe |
| Cliente | `codigo_cliente` | Puede tener representante, pedidos y pagos |
| GamaProducto | `gama` | Clasifica productos |
| Producto | `codigo_producto` | Pertenece a una gama |
| Pedido | `codigo_pedido` | Pertenece a cliente |
| DetallePedido | `codigo_pedido + codigo_producto` | Une pedido y producto |
| Pago | `codigo_cliente + id_transaccion` | Pertenece a cliente |

Los importes usan `BigDecimal`, las fechas `LocalDate` y los campos opcionales aceptan `null`.

## Configuración local

Desde la carpeta `jardineria`:

```powershell
Copy-Item .env.example .env
notepad .env
```

Cambie como mínimo `MYSQL_PASSWORD` y `MYSQL_ROOT_PASSWORD`. No confirme `.env` en Git.

La aplicación Java lee primero las variables `DB_*`. También puede usar un archivo local:

```powershell
Copy-Item .\src\main\resources\application.properties.example .\src\main\resources\application.properties
notepad .\src\main\resources\application.properties
```

Ese archivo también está ignorado. El `.env` de Compose no es cargado automáticamente por Maven; por eso debe definir variables en la sesión o usar `application.properties`.

## MySQL con Docker

```powershell
Set-Location .\jardineria
docker compose config
docker compose up -d
docker compose ps
```

El servicio se llama `mysql`, utiliza `mysql:8.4`, publica `${MYSQL_PORT}:3306` y conserva datos en el volumen `jardineria_mysql_data`.

Si 3306 está ocupado, edite `.env`:

```text
MYSQL_PORT=3307
DB_PORT=3307
```

No cambie el puerto interno 3306. Compruebe antes que 3307 también esté libre.

### Inicialización e importación

`database/jardineria.sql` se ejecuta automáticamente solo la primera vez que se crea el volumen. Para aplicar un script posterior sin borrar datos:

```powershell
Get-Content -Raw .\database\datos-demo.sql |
  docker compose exec -T mysql sh -c 'exec mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"'
```

El mismo patrón permite importar otro script SQL. `docker compose down` detiene el entorno pero conserva el volumen. No use `docker compose down -v` salvo que realmente quiera borrar la base del proyecto.

`datos-demo.sql` es opcional y está etiquetado como demostración. Agrega tres productos, dos detalles y dos pagos coherentes; no modifica ni presenta como reales los datos originales.

## Datos originales y observaciones

El script del profesor contiene las ocho tablas, 9 oficinas, 31 empleados, 5 gamas, 36 clientes y 47 pedidos. No contiene productos, detalles ni pagos, y la aplicación soporta esas tablas vacías mostrando cero en el Dashboard.

Se preservaron las inconsistencias históricas en lugar de corregirlas silenciosamente. Ejemplos:

- Pedidos 45, 46 y 47 tienen fechas esperadas anteriores a la fecha del pedido.
- Pedidos 39, 43 y 44 figuran como pendientes aunque tienen fecha de entrega.
- Pedido 27 figura entregado sin fecha de entrega.

Las reglas de fechas se aplican a nuevas altas y ediciones desde la aplicación; los registros históricos permanecen intactos.

## Compilar, probar y ejecutar

```powershell
Set-Location .\jardineria
mvn clean compile
mvn test
mvn exec:java
```

Prueba de integración JDBC optativa (requiere el contenedor saludable):

```powershell
mvn -Pintegration verify
```

La prueba crea registros relacionados para las ocho entidades con identificadores aleatorios, comprueba guardar/buscar/actualizar/eliminar, consulta el Dashboard y limpia sus propios registros en orden inverso dentro de `finally`. También construye y descarta la ventana principal para verificar su arranque.

Si `mvn` no está en `PATH`, agregue la carpeta `bin` de Maven o invoque su `mvn.cmd` con ruta completa.

## Funcionalidades

- CRUD conectado a MySQL para oficinas, empleados, clientes, gamas, productos, pedidos, detalles y pagos.
- Búsqueda instantánea en las tablas.
- Formularios con desplazamiento y selectores de claves foráneas.
- Validación de obligatorios, duplicados, referencias, dinero, stock, cantidad y fechas.
- Borrado controlado: MySQL impide eliminar registros con dependencias.
- Mensajes claros para errores de negocio, integridad y conexión.
- Consulta de inventario y alerta de stock menor o igual a 10.
- Dashboard actualizable sin bloquear el hilo visual.
- Tema visual cálido y legible centralizado para menús, formularios y tablas.
- Pantalla de bienvenida animada antes de abrir la ventana principal.

### Pantalla de bienvenida

Al ejecutar la aplicación aparece un `JWindow` centrado con el nombre del sistema, un brote dibujado con Java 2D y una barra de progreso. La animación utiliza `javax.swing.Timer`, mientras que la inicialización se realiza con `SwingWorker` para no bloquear el hilo visual. Cuando la conexión y el Dashboard están preparados, la pantalla se cierra y se muestra la ventana principal; si ocurre un error, se presenta un mensaje claro.

### Dashboard

Muestra mediante consultas SQL reales:

- total de ingresos y cantidad de pagos;
- pedidos pendientes y entregados;
- clientes registrados;
- productos con stock crítico;
- últimos pedidos;
- gráfico Java 2D de pedidos por estado.

Cuando productos o pagos están vacíos, muestra cero y el mensaje correspondiente.

## Flujo Git

- `main`: versión estable.
- `develop`: integración.
- `feature/configuracion-docker`
- `feature/modelo-dominio`
- `feature/repositorios-jdbc`
- `feature/servicios`
- `feature/interfaz-swing`
- `feature/dashboard`
- `feature/documentacion-pruebas`
- `feature/redisenio-interfaz`

Los commits posteriores a `first commit` usan Conventional Commits con Gitmojis. Las ramas feature se integran mediante merge sin reescribir el historial. Las ocho entidades se desarrollaron conjuntamente en las ramas de dominio, persistencia, servicios e interfaz porque comparten esas capas; no se crearon ramas artificiales por entidad después de terminar el código.

## Limitaciones conocidas

- No hay autenticación, roles, API web, correo ni reportes PDF porque no forman parte del SRS.
- Registrar un detalle valida el stock disponible, pero no descuenta existencias automáticamente porque el documento no define una política de reserva o despacho.
- Las claves primarias no se editan; para cambiarlas se debe crear un registro nuevo respetando las relaciones.
- El script demo no se ejecuta automáticamente.

## Conceptos para sustentar

Conviene estudiar: encapsulamiento, constructores, composición, interfaces, inversión de dependencias, `Optional`, colecciones, excepciones, `BigDecimal`, `LocalDate`, patrón Repository, Singleton de configuración, `PreparedStatement`, `ResultSet`, `try-with-resources`, claves foráneas/compuestas, layouts de Swing, `JTable`, `CardLayout`, `SwingWorker`, Java 2D y flujo Git con ramas.
