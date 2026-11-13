package com.taller.infrastructure.ui;

import com.taller.domain.model.*;
import com.taller.infrastructure.config.ApplicationContainer;
import java.time.LocalDate;
import java.util.List;

/**
 * Define los campos y constructores de los ocho formularios de entidades.
 *
 * <p>Cada método usa {@link FormBuilder}. Si recibe {@code null}, crea un alta;
 * si recibe una entidad, carga sus valores para editar y bloquea la clave
 * primaria. Los JComboBox se llenan desde servicios para elegir claves foráneas
 * existentes.</p>
 */
final class EntityForms {
    private EntityForms(){ }

    /** Construye el formulario de Oficina y devuelve la entidad aceptada. */
    static Oficina oficina(Oficina x){
        FormBuilder f=new FormBuilder();var id=f.text("id","Codigo*",x==null?null:x.getCodigoOficina());if(x!=null)f.disable(id);
        f.text("ciudad","Ciudad*",x==null?null:x.getCiudad());f.text("pais","Pais*",x==null?null:x.getPais());f.text("region","Region",x==null?null:x.getRegion());f.text("postal","Codigo postal*",x==null?null:x.getCodigoPostal());f.text("telefono","Telefono*",x==null?null:x.getTelefono());f.text("dir1","Direccion 1*",x==null?null:x.getLineaDireccion1());f.text("dir2","Direccion 2",x==null?null:x.getLineaDireccion2());
        return f.show(x==null?"Nueva oficina":"Editar oficina",()->new Oficina(f.value("id"),f.value("ciudad"),f.value("pais"),f.optional("region"),f.value("postal"),f.value("telefono"),f.value("dir1"),f.optional("dir2")));
    }

    /** Incluye selectores de oficina y jefe para respetar sus relaciones. */
    static Empleado empleado(Empleado x,ApplicationContainer c){
        FormBuilder f=new FormBuilder();var id=f.text("id","Codigo*",x==null?null:x.getCodigoEmpleado());if(x!=null)f.disable(id);
        f.text("nombre","Nombre*",x==null?null:x.getNombre());f.text("apellido1","Primer apellido*",x==null?null:x.getApellido1());f.text("apellido2","Segundo apellido",x==null?null:x.getApellido2());f.text("extension","Extension*",x==null?null:x.getExtension());f.text("email","Correo*",x==null?null:x.getEmail());
        List<String> oficinas=c.oficinas().listarTodos().stream().map(Oficina::getCodigoOficina).toList();f.combo("oficina","Oficina*",oficinas,x==null?null:x.getCodigoOficina(),false);
        List<Integer> jefes=c.empleados().listarTodos().stream().map(Empleado::getCodigoEmpleado).toList();f.combo("jefe","Jefe",jefes,x==null?null:x.getCodigoJefe(),true);f.text("puesto","Puesto",x==null?null:x.getPuesto());
        return f.show(x==null?"Nuevo empleado":"Editar empleado",()->new Empleado(f.intValue("id"),f.value("nombre"),f.value("apellido1"),f.optional("apellido2"),f.value("extension"),f.value("email"),f.selected("oficina"),f.selected("jefe"),f.optional("puesto")));
    }

    static Cliente cliente(Cliente x,ApplicationContainer c){
        FormBuilder f=new FormBuilder();var id=f.text("id","Codigo*",x==null?null:x.getCodigoCliente());if(x!=null)f.disable(id);
        f.text("nombre","Nombre del cliente*",x==null?null:x.getNombreCliente());f.text("contactoNombre","Nombre de contacto",x==null?null:x.getNombreContacto());f.text("contactoApellido","Apellido de contacto",x==null?null:x.getApellidoContacto());f.text("telefono","Telefono*",x==null?null:x.getTelefono());f.text("fax","Fax*",x==null?null:x.getFax());f.text("dir1","Direccion 1*",x==null?null:x.getLineaDireccion1());f.text("dir2","Direccion 2",x==null?null:x.getLineaDireccion2());f.text("ciudad","Ciudad*",x==null?null:x.getCiudad());f.text("region","Region",x==null?null:x.getRegion());f.text("pais","Pais",x==null?null:x.getPais());f.text("postal","Codigo postal",x==null?null:x.getCodigoPostal());
        List<Integer> empleados=c.empleados().listarTodos().stream().map(Empleado::getCodigoEmpleado).toList();f.combo("empleado","Representante",empleados,x==null?null:x.getCodigoEmpleadoRepVentas(),true);f.text("limite","Limite de credito",x==null?null:x.getLimiteCredito());
        return f.show(x==null?"Nuevo cliente":"Editar cliente",()->new Cliente(f.intValue("id"),f.optional("nombre"),f.optional("contactoNombre"),f.optional("contactoApellido"),f.value("telefono"),f.value("fax"),f.value("dir1"),f.optional("dir2"),f.value("ciudad"),f.optional("region"),f.optional("pais"),f.optional("postal"),f.selected("empleado"),f.optionalDecimal("limite")));
    }

    static GamaProducto gama(GamaProducto x){
        FormBuilder f=new FormBuilder();var id=f.text("gama","Gama*",x==null?null:x.getGama());if(x!=null)f.disable(id);f.area("texto","Descripcion",x==null?null:x.getDescripcionTexto());f.area("html","Descripcion HTML",x==null?null:x.getDescripcionHtml());f.text("imagen","Ruta de imagen",x==null?null:x.getImagen());
        return f.show(x==null?"Nueva gama":"Editar gama",()->new GamaProducto(f.value("gama"),f.optional("texto"),f.optional("html"),f.optional("imagen")));
    }

    /** Incluye un selector con las gamas que existen en la base. */
    static Producto producto(Producto x,ApplicationContainer c){
        FormBuilder f=new FormBuilder();var id=f.text("id","Codigo*",x==null?null:x.getCodigoProducto());if(x!=null)f.disable(id);f.text("nombre","Nombre*",x==null?null:x.getNombre());List<String> gamas=c.gamas().listarTodos().stream().map(GamaProducto::getGama).toList();f.combo("gama","Gama*",gamas,x==null?null:x.getGama(),false);f.text("dimensiones","Dimensiones",x==null?null:x.getDimensiones());f.text("proveedor","Proveedor",x==null?null:x.getProveedor());f.area("descripcion","Descripcion",x==null?null:x.getDescripcion());f.text("stock","Cantidad en stock*",x==null?0:x.getCantidadEnStock());f.text("venta","Precio de venta*",x==null?"0.00":x.getPrecioVenta());f.text("proveedorPrecio","Precio del proveedor",x==null?null:x.getPrecioProveedor());
        return f.show(x==null?"Nuevo producto":"Editar producto",()->new Producto(f.value("id"),f.value("nombre"),f.selected("gama"),f.optional("dimensiones"),f.optional("proveedor"),f.optional("descripcion"),f.intValue("stock"),f.decimal("venta"),f.optionalDecimal("proveedorPrecio")));
    }

    static Pedido pedido(Pedido x,ApplicationContainer c){
        FormBuilder f=new FormBuilder();var id=f.text("id","Codigo*",x==null?null:x.getCodigoPedido());if(x!=null)f.disable(id);f.text("fechaPedido","Fecha pedido*",x==null?LocalDate.now():x.getFechaPedido());f.text("fechaEsperada","Fecha esperada*",x==null?LocalDate.now():x.getFechaEsperada());f.text("fechaEntrega","Fecha entrega",x==null?null:x.getFechaEntrega());f.text("estado","Estado*",x==null?"Pendiente":x.getEstado());f.area("comentarios","Comentarios",x==null?null:x.getComentarios());List<Integer> clientes=c.clientes().listarTodos().stream().map(Cliente::getCodigoCliente).toList();f.combo("cliente","Cliente*",clientes,x==null?null:x.getCodigoCliente(),false);
        return f.show(x==null?"Nuevo pedido":"Editar pedido",()->new Pedido(f.intValue("id"),f.date("fechaPedido"),f.date("fechaEsperada"),f.optionalDate("fechaEntrega"),f.value("estado"),f.optional("comentarios"),f.selected("cliente")));
    }

    /** Permite elegir pedido y producto; al editar bloquea ambas partes de la clave. */
    static DetallePedido detalle(DetallePedido x,ApplicationContainer c){
        FormBuilder f=new FormBuilder();List<Integer> pedidos=c.pedidos().listarTodos().stream().map(Pedido::getCodigoPedido).toList();var pedido=f.combo("pedido","Pedido*",pedidos,x==null?null:x.getCodigoPedido(),false);List<String> productos=c.productos().listarTodos().stream().map(Producto::getCodigoProducto).toList();var producto=f.combo("producto","Producto*",productos,x==null?null:x.getCodigoProducto(),false);if(x!=null){f.disable(pedido);f.disable(producto);}f.text("cantidad","Cantidad*",x==null?1:x.getCantidad());f.text("precio","Precio unidad*",x==null?"0.00":x.getPrecioUnidad());f.text("linea","Numero de linea*",x==null?1:x.getNumeroLinea());
        return f.show(x==null?"Nuevo detalle":"Editar detalle",()->new DetallePedido(f.selected("pedido"),f.selected("producto"),f.intValue("cantidad"),f.decimal("precio"),f.intValue("linea")));
    }

    /** Permite elegir cliente y bloquea la clave compuesta durante edición. */
    static Pago pago(Pago x,ApplicationContainer c){
        FormBuilder f=new FormBuilder();List<Integer> clientes=c.clientes().listarTodos().stream().map(Cliente::getCodigoCliente).toList();var cliente=f.combo("cliente","Cliente*",clientes,x==null?null:x.getCodigoCliente(),false);f.text("forma","Forma de pago*",x==null?null:x.getFormaPago());var id=f.text("id","Id transaccion*",x==null?null:x.getIdTransaccion());if(x!=null){f.disable(cliente);f.disable(id);}f.text("fecha","Fecha de pago*",x==null?LocalDate.now():x.getFechaPago());f.text("total","Total*",x==null?"0.00":x.getTotal());
        return f.show(x==null?"Nuevo pago":"Editar pago",()->new Pago(f.selected("cliente"),f.value("forma"),f.value("id"),f.date("fecha"),f.decimal("total")));
    }
}
