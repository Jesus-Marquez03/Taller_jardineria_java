# Guía de sustentación — Sistema de Jardinería

Esta guía sirve para recorrer el proyecto mientras explicas el código. No intentes memorizar cada línea: aprende el camino que sigue una operación y el propósito de cada capa.

## 1. Idea general del proyecto

Es una aplicación de escritorio para administrar oficinas, empleados, clientes, gamas, productos, pedidos, detalles y pagos. La interfaz está construida con Swing y los datos se guardan en MySQL mediante JDBC.

El programa sigue una arquitectura hexagonal simplificada:

```text
Swing → servicios → interfaces de repositorio ← repositorios JDBC → MySQL
```

Las flechas muestran quién llama a quién. La regla importante es que el dominio y los servicios no conocen Swing ni MySQL.

## 2. Estructura del proyecto

```text
com.taller
├── Main.java
├── domain
│   ├── model                 Entidades y validaciones básicas
│   ├── ports/out             Interfaces de repositorio
│   └── exception             Error de persistencia
├── application
│   ├── ports/in              Contrato CRUD de entrada
│   ├── services              Casos de uso y reglas del negocio
│   └── exception             Error de regla de negocio
└── infrastructure
    ├── config                Conexión y composición de dependencias
    ├── persistence           Adaptadores JDBC
    └── ui                    Ventanas, paneles y formularios Swing
```

### Domain

Es el centro del sistema. Contiene objetos que representan conceptos reales del negocio y puertos que expresan qué persistencia necesita la aplicación. No importa si los datos vienen de MySQL, un archivo o una prueba.

### Application

Contiene los casos de uso. Un servicio decide si una operación es válida y luego delega el almacenamiento a una interfaz de repositorio.

### Infrastructure

Contiene detalles externos: JDBC, MySQL y Swing. Los repositorios JDBC adaptan las interfaces del dominio a SQL. La interfaz Swing adapta clics y formularios a llamadas de servicios.

## 3. Clases principales

| Clase | Responsabilidad |
|---|---|
| `Main` | Inicia Swing, muestra el Splash y prepara la aplicación sin bloquear el EDT. |
| `DatabaseConfig` | Singleton que lee configuración y abre conexiones JDBC nuevas. |
| `ApplicationContainer` | Crea repositorios y los inyecta en los servicios. |
| `MainWindow` | Ventana principal y navegación con `CardLayout`. |
| `CrudPanel<T>` | Pantalla CRUD reutilizable para las ocho entidades. |
| `EntityForms` | Define los campos reales de cada formulario. |
| `FormBuilder` | Alinea controles y convierte texto a tipos Java. |
| `InicioPanel` | Consulta y muestra Dashboard, tabla y gráfico. |
| `Theme` | Centraliza colores, fuentes y estilos Swing. |
| `SplashScreen` | Anima el inicio con `Timer` mientras `Main` carga con `SwingWorker`. |
| `ProductoService` | Valida duplicado y gama antes de persistir un producto. |
| `ProductoJdbcRepository` | Ejecuta el SQL CRUD de productos. |

## 4. Recorrido completo: guardar un producto

Este es el flujo más importante para entender arquitectura hexagonal.

### Paso 1: el botón Nuevo

`CrudPanelsFactory.productos(...)` configura un `CrudPanel<Producto>` y le entrega funciones reales:

```java
c.productos()::listarTodos
c.productos()::guardar
c.productos()::actualizar
```

El `CrudPanel` no sabe qué es un producto. Solo sabe ejecutar las funciones que recibió.

### Paso 2: abrir el formulario

El `ActionListener` del botón Nuevo llama al proveedor del formulario:

```java
T entity = crearFormulario.get();
if (entity != null) {
    ejecutar(() -> guardar.accept(entity), "Registro guardado.");
}
```

`EntityForms.producto(null, container)` crea los campos. La gama se muestra en un `JComboBox` cargado con `c.gamas().listarTodos()`. Así el usuario elige una clave foránea existente.

### Paso 3: construir la entidad

Al presionar Guardar, `FormBuilder.show(...)` ejecuta el `Supplier<Producto>`:

```java
new Producto(
    f.value("id"),
    f.value("nombre"),
    f.selected("gama"),
    f.optional("dimensiones"),
    f.optional("proveedor"),
    f.optional("descripcion"),
    f.intValue("stock"),
    f.decimal("venta"),
    f.optionalDecimal("proveedorPrecio")
)
```

El constructor llama a setters. `Validaciones` impide texto vacío, stock negativo y precios negativos. En este punto todavía no se ha ejecutado SQL.

### Paso 4: reglas del servicio

`ProductoService.guardar(Producto x)` realiza dos controles:

1. Busca el código para impedir duplicados.
2. Comprueba que la gama elegida exista.

Después llama al puerto:

```java
repository.guardar(x);
```

`repository` está declarado como `ProductoRepository`, no como `ProductoJdbcRepository`. Esta es inversión de dependencias.

### Paso 5: adaptador JDBC

El objeto real inyectado por `ApplicationContainer` es `ProductoJdbcRepository`. Su método `guardar`:

1. Obtiene una conexión con `databaseConfig.getConnection()`.
2. Crea un `PreparedStatement` con nueve signos `?`.
3. `asignar(...)` coloca cada atributo en su posición.
4. `executeUpdate()` ejecuta el INSERT.
5. try-with-resources cierra la sentencia y la conexión.

### Paso 6: resultado visual

`CrudPanel.ejecutar(...)` muestra un mensaje y llama a `cargar()`. El `SwingWorker` vuelve a consultar la lista y actualiza `DefaultTableModel`, que alimenta la `JTable`.

## 5. JDBC explicado con el proyecto

### Connection

Representa una sesión abierta con MySQL. `DatabaseConfig.getConnection()` crea una conexión por operación. No existe una conexión global abierta permanentemente.

### DriverManager

Recibe URL, usuario y contraseña y solicita al conector oficial una conexión:

```java
DriverManager.getConnection(url, user, password)
```

### PreparedStatement

Es una consulta preparada. Los signos `?` se reemplazan con métodos tipados:

```java
PreparedStatement p = c.prepareStatement(
        "SELECT * FROM producto WHERE codigo_producto=?");
p.setString(1, id);
```

Es mejor que concatenar texto porque separa SQL y datos del usuario.

### executeQuery y ResultSet

`executeQuery()` se usa con SELECT y devuelve `ResultSet`. El cursor comienza antes de la primera fila; `next()` avanza:

```java
try (ResultSet r = p.executeQuery()) {
    while (r.next()) {
        datos.add(mapear(r));
    }
}
```

`mapear(ResultSet r)` convierte columnas SQL en un objeto Java.

### executeUpdate

Se usa con INSERT, UPDATE y DELETE. Devuelve cuántas filas fueron afectadas:

```java
return p.executeUpdate() > 0;
```

### try-with-resources

Todo lo declarado entre paréntesis se cierra automáticamente, incluso si hay una excepción:

```java
try (Connection c = databaseConfig.getConnection();
     PreparedStatement p = c.prepareStatement(sql)) {
    // operación
}
```

### SQLException

Los adaptadores capturan `SQLException`. `JdbcRepositorySupport.error(...)` la traduce a `PersistenciaException`, conserva la causa y agrega una explicación para errores de integridad o formato.

### Transacciones

Los CRUD actuales ejecutan una escritura por método, así que cada sentencia puede confirmarse normalmente. El Dashboard realiza varias lecturas y no modifica datos. Por eso el código actual no llama manualmente a `commit()` o `rollback()`. Una transacción explícita sería necesaria si un caso de uso hiciera varias escrituras que debieran completarse todas o ninguna.

## 6. Singleton y Repository

### Singleton en DatabaseConfig

```java
private static final DatabaseConfig INSTANCE = new DatabaseConfig();

private DatabaseConfig() { ... }

public static DatabaseConfig getInstance() {
    return INSTANCE;
}
```

- La instancia estática se crea una vez.
- El constructor privado impide `new DatabaseConfig()` desde otra clase.
- `getInstance()` entrega la misma configuración.
- El Singleton guarda configuración, no una conexión permanente.

### Patrón Repository

`ProductoRepository` dice qué operaciones necesita el negocio. `ProductoJdbcRepository implements ProductoRepository` explica cómo realizarlas con MySQL.

`implements` significa que la clase promete cumplir todos los métodos declarados por la interfaz.

Beneficios reales:

- El servicio no contiene SQL.
- Swing no contiene SQL.
- Se puede probar un servicio con otra implementación del puerto.
- Cada clase mantiene una responsabilidad concreta.

## 7. SOLID que sí aparece

### Responsabilidad única

- `Producto`: conserva datos válidos.
- `ProductoService`: aplica reglas de negocio.
- `ProductoJdbcRepository`: ejecuta SQL.
- `CrudPanel`: presenta e inicia acciones.

### Inversión de dependencias

`ProductoService` depende de `ProductoRepository`, una abstracción del dominio. `ApplicationContainer` decide que la implementación real será `ProductoJdbcRepository`.

### Segregación de interfaces

Los puertos están separados por entidad y `DashboardRepository` solo contiene la lectura especial del Dashboard. No existe una interfaz gigante con operaciones que algunas clases no usarían.

No es necesario afirmar que todo el proyecto demuestra herencia o polimorfismo. La herencia se usa de forma puntual en componentes Swing y en `JdbcRepositorySupport`; la separación principal se logra con interfaces y composición.

## 8. Swing explicado

### Event Dispatch Thread

Swing debe crearse y modificarse en el EDT. `Main.main` utiliza:

```java
SwingUtilities.invokeLater(Main::startApplication);
```

### JFrame y JPanel

`MainWindow extends JFrame` es la ventana superior. Las pantallas como `CrudPanel` e `InicioPanel` extienden `JPanel` y se colocan dentro de ella.

### LayoutManagers

- `BorderLayout`: norte, sur, este, oeste y centro.
- `CardLayout`: varias pantallas ocupan el centro; muestra una a la vez.
- `BoxLayout`: menú y textos en dirección vertical.
- `GridLayout`: tarjetas del Dashboard en una cuadrícula uniforme.
- `GridBagLayout`: etiquetas y campos alineados en formularios.

### ActionListener

Conecta un clic con una acción:

```java
button.addActionListener(event -> showCard(card));
```

La lambda recibe el evento y llama al método correspondiente.

### JTable

`DefaultTableModel` guarda filas y columnas. `JTable` las muestra. `TableRowSorter` permite ordenar y filtrar. El renderer de `Theme` usa `DefaultTableCellRenderer` y `BasicLabelUI` para que Windows no oculte los encabezados.

### SwingWorker

`doInBackground()` ejecuta consultas fuera del EDT. `done()` vuelve al EDT y actualiza la interfaz. Esto evita ventanas congeladas.

### Timer y Splash Screen

`javax.swing.Timer` genera pulsos cortos. Cada pulso incrementa progreso y repinta la planta. No usa `Thread.sleep()`, que bloquearía Swing. Si la inicialización tarda, la animación espera en 90%. Cuando `Main` termina, llama a `finish(...)`.

### Java 2D

`SplashScreen.PlantPanel`, `StatusChart`, `RoundedPanel` y `MenuIcons` reciben `Graphics`, crean una copia `Graphics2D`, activan antialiasing y dibujan formas básicas.

## 9. Las ocho entidades

### Oficina

Clave: `codigoOficina`. Es padre de empleados. Región y segunda dirección pueden ser null.

### Empleado

Clave: `codigoEmpleado`. Tiene clave foránea hacia oficina y una relación opcional hacia otro empleado como jefe. `EmpleadoService` impide que sea su propio jefe.

### Cliente

Clave: `codigoCliente`. Puede tener representante de ventas. Se relaciona con pedidos y pagos. `limiteCredito` usa `BigDecimal` y puede ser null.

### GamaProducto

Clave: `gama`. Agrupa productos. Sus descripciones e imagen pueden ser null.

### Producto

Clave: `codigoProducto`. Pertenece a una gama. Controla stock y precios. No permite stock ni importes negativos.

### Pedido

Clave: `codigoPedido`. Pertenece a un cliente. Usa `LocalDate`. `fechaEntrega` puede ser null. El servicio valida el orden de las fechas.

### DetallePedido

Clave compuesta: `codigoPedido + codigoProducto`. Une pedido y producto. Valida cantidad, precio y stock disponible.

### Pago

Clave compuesta: `codigoCliente + idTransaccion`. Pertenece a un cliente. Usa `LocalDate` y `BigDecimal`.

## 10. Conceptos de POO para explicar

### Clase y objeto

Una clase es el molde, por ejemplo `Producto`. Un objeto es una instancia concreta creada con `new Producto(...)`.

### Atributo

Es información que pertenece al objeto, como `cantidadEnStock`.

### Constructor

Inicializa un objeto. En las entidades llama a setters para que el objeto nazca válido.

### Encapsulamiento

Los atributos son privados. El exterior utiliza getters y setters, que controlan el acceso y pueden validar cambios.

### null

Significa ausencia de valor. Solo se acepta en campos que la base y el negocio consideran opcionales. Para enteros opcionales se usa `Integer`, no `int`, porque `int` nunca puede ser null.

### BigDecimal

Representa dinero sin los errores binarios típicos de `double`. Se compara con `signum()` o `compareTo()`, no con operadores como `<`.

### LocalDate

Representa fecha sin hora. Coincide con las columnas DATE de MySQL.

## 11. Preguntas frecuentes del profesor

### ¿Por qué no hay SQL en los botones?

Porque Swing pertenece a infraestructura de entrada. Llama servicios; los servicios usan puertos; los adaptadores JDBC contienen SQL. Esto evita mezclar responsabilidades.

### ¿Qué diferencia hay entre entidad y servicio?

La entidad protege su propio estado. El servicio coordina reglas que necesitan repositorios u otras entidades, como comprobar una gama o comparar una clave foránea.

### ¿Por qué Optional en buscarPorId?

Porque una búsqueda puede no encontrar fila. `Optional.empty()` expresa esa posibilidad sin devolver un objeto falso.

### ¿Por qué PreparedStatement?

Permite asignar datos con tipos correctos y evita construir SQL concatenando texto del usuario.

### ¿Por qué se cierra una conexión en cada operación?

Las conexiones son recursos externos. Abrirlas solo durante la operación evita fugas y conexiones inválidas guardadas durante toda la ejecución.

### ¿Cómo se respetan las claves foráneas?

Los servicios comprueban referencias para mostrar mensajes claros y MySQL vuelve a proteger la integridad. Por ejemplo, `ProductoService` consulta la gama antes del INSERT.

### ¿Qué ocurre al eliminar un registro con hijos?

El servicio intenta eliminar y MySQL rechaza la operación por la clave foránea. `JdbcRepositorySupport` transforma el error en un mensaje comprensible.

### ¿Por qué CrudPanel es genérico?

Porque el flujo visual CRUD es igual para las ocho entidades. El parámetro `T` permite reutilizarlo sin perder el tipo real.

### ¿Dónde está la inversión de dependencias?

En servicios como `ProductoService`, cuyo atributo es `ProductoRepository`. El adaptador JDBC depende de esa interfaz al implementarla; el servicio no depende del adaptador.

### ¿Qué consulta el Dashboard?

`DashboardJdbcRepository` suma pagos, cuenta estados, clientes y productos, busca stock crítico, obtiene últimos pedidos y agrupa pedidos por estado.

### ¿Cómo se evita bloquear Swing?

Las consultas visuales se ejecutan en `SwingWorker.doInBackground()`. La actualización de componentes ocurre en `done()`.

## 12. Fragmentos que debes saber reconstruir

### 1. Validación de texto obligatorio

```java
static String textoRequerido(String valor, String campo) {
    if (valor == null || valor.isBlank()) {
        throw new IllegalArgumentException(campo + " es obligatorio.");
    }
    return valor.trim();
}
```

### 2. Puerto Repository

```java
public interface ProductoRepository {
    void guardar(Producto producto);
    List<Producto> listarTodos();
    Optional<Producto> buscarPorId(String codigoProducto);
    boolean actualizar(Producto producto);
    boolean eliminarPorId(String codigoProducto);
}
```

### 3. Inyección por constructor

```java
public ProductoService(ProductoRepository repository,
                       GamaProductoRepository gamas) {
    this.repository = repository;
    this.gamas = gamas;
}
```

### 4. Guardar con reglas

```java
if (repository.buscarPorId(x.getCodigoProducto()).isPresent()) {
    throw new ReglaNegocioException("Ya existe ese producto.");
}
validar(x);
repository.guardar(x);
```

### 5. INSERT con try-with-resources

```java
try (Connection c = databaseConfig.getConnection();
     PreparedStatement p = c.prepareStatement(
             "INSERT INTO producto VALUES (?,?,?,?,?,?,?,?,?)")) {
    asignar(p, x);
    p.executeUpdate();
} catch (SQLException e) {
    throw error("guardar el producto", e);
}
```

### 6. Buscar y devolver Optional

```java
try (ResultSet r = p.executeQuery()) {
    return r.next() ? Optional.of(mapear(r)) : Optional.empty();
}
```

### 7. Mapear ResultSet

Debes recordar la idea: leer cada columna por nombre y llamar al constructor de la entidad. Revisa `ProductoJdbcRepository.mapear(...)`.

### 8. Clave compuesta con record

```java
public record DetallePedidoId(Integer codigoPedido, String codigoProducto) {
}
```

En el proyecto real el constructor compacto valida ambas partes.

### 9. Navegación con CardLayout

```java
cards.show(content, card);
navigationButtons.forEach((name, button) ->
        Theme.setNavigationSelected(button, name.equals(card)));
```

### 10. SwingWorker

```java
new SwingWorker<List<T>, Void>() {
    protected List<T> doInBackground() {
        return listar.get();
    }

    protected void done() {
        registros = get();
    }
}.execute();
```

## 13. Orden recomendado para estudiar

1. `Producto.java`
2. `ProductoRepository.java`
3. `ProductoService.java`
4. `ProductoJdbcRepository.java`
5. `ApplicationContainer.java`
6. `CrudPanelsFactory.java`
7. `CrudPanel.java`
8. `EntityForms.java`
9. `DatabaseConfig.java`
10. `Main.java`

Cuando entiendas ese recorrido, las otras siete entidades siguen el mismo patrón con reglas y claves diferentes.
