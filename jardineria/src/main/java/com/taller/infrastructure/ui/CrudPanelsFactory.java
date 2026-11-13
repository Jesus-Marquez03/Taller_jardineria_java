package com.taller.infrastructure.ui;

import com.taller.domain.model.*;
import com.taller.infrastructure.config.ApplicationContainer;

/**
 * Fábrica que configura un CrudPanel para cada entidad.
 *
 * <p>Aquí se conectan columnas, formularios, servicios y conversión a filas.
 * Las referencias como {@code c.productos()::guardar} pasan comportamiento sin
 * ejecutar todavía el método. La clase no guarda datos ni contiene SQL.</p>
 */
final class CrudPanelsFactory {
    private CrudPanelsFactory(){ }

    /** Ejemplo completo: enlaza el CRUD genérico con el servicio y formulario de Cliente. */
    static CrudPanel<Cliente> clientes(ApplicationContainer c){return new CrudPanel<>("Clientes",new String[]{"Codigo","Nombre","Contacto","Telefono","Ciudad","Pais","Limite"},c.clientes()::listarTodos,()->EntityForms.cliente(null,c),x->EntityForms.cliente(x,c),c.clientes()::guardar,c.clientes()::actualizar,x->c.clientes().eliminar(x.getCodigoCliente()),x->new Object[]{x.getCodigoCliente(),x.getNombreCliente(),unir(x.getNombreContacto(),x.getApellidoContacto()),x.getTelefono(),x.getCiudad(),x.getPais(),x.getLimiteCredito()});}
    static CrudPanel<Pedido> pedidos(ApplicationContainer c){return new CrudPanel<>("Pedidos",new String[]{"Codigo","Fecha","Esperada","Entrega","Estado","Cliente"},c.pedidos()::listarTodos,()->EntityForms.pedido(null,c),x->EntityForms.pedido(x,c),c.pedidos()::guardar,c.pedidos()::actualizar,x->c.pedidos().eliminar(x.getCodigoPedido()),x->new Object[]{x.getCodigoPedido(),x.getFechaPedido(),x.getFechaEsperada(),x.getFechaEntrega(),x.getEstado(),x.getCodigoCliente()});}
    static CrudPanel<DetallePedido> detalles(ApplicationContainer c){return new CrudPanel<>("Detalles de pedido",new String[]{"Pedido","Producto","Cantidad","Precio unidad","Linea"},c.detalles()::listarTodos,()->EntityForms.detalle(null,c),x->EntityForms.detalle(x,c),c.detalles()::guardar,c.detalles()::actualizar,x->c.detalles().eliminar(x.getId()),x->new Object[]{x.getCodigoPedido(),x.getCodigoProducto(),x.getCantidad(),x.getPrecioUnidad(),x.getNumeroLinea()});}
    static CrudPanel<Pago> pagos(ApplicationContainer c){return new CrudPanel<>("Pagos",new String[]{"Cliente","Forma","Transaccion","Fecha","Total"},c.pagos()::listarTodos,()->EntityForms.pago(null,c),x->EntityForms.pago(x,c),c.pagos()::guardar,c.pagos()::actualizar,x->c.pagos().eliminar(x.getId()),x->new Object[]{x.getCodigoCliente(),x.getFormaPago(),x.getIdTransaccion(),x.getFechaPago(),x.getTotal()});}
    static CrudPanel<Producto> productos(ApplicationContainer c){return new CrudPanel<>("Productos e inventario",new String[]{"Codigo","Nombre","Gama","Stock","Precio venta","Proveedor"},c.productos()::listarTodos,()->EntityForms.producto(null,c),x->EntityForms.producto(x,c),c.productos()::guardar,c.productos()::actualizar,x->c.productos().eliminar(x.getCodigoProducto()),x->new Object[]{x.getCodigoProducto(),x.getNombre(),x.getGama(),x.getCantidadEnStock(),x.getPrecioVenta(),x.getProveedor()});}
    static CrudPanel<GamaProducto> gamas(ApplicationContainer c){return new CrudPanel<>("Gamas de producto",new String[]{"Gama","Descripcion","Imagen"},c.gamas()::listarTodos,()->EntityForms.gama(null),EntityForms::gama,c.gamas()::guardar,c.gamas()::actualizar,x->c.gamas().eliminar(x.getGama()),x->new Object[]{x.getGama(),x.getDescripcionTexto(),x.getImagen()});}
    static CrudPanel<Oficina> oficinas(ApplicationContainer c){return new CrudPanel<>("Oficinas",new String[]{"Codigo","Ciudad","Pais","Region","Telefono","Direccion"},c.oficinas()::listarTodos,()->EntityForms.oficina(null),EntityForms::oficina,c.oficinas()::guardar,c.oficinas()::actualizar,x->c.oficinas().eliminar(x.getCodigoOficina()),x->new Object[]{x.getCodigoOficina(),x.getCiudad(),x.getPais(),x.getRegion(),x.getTelefono(),x.getLineaDireccion1()});}
    static CrudPanel<Empleado> empleados(ApplicationContainer c){return new CrudPanel<>("Empleados",new String[]{"Codigo","Nombre","Apellidos","Oficina","Jefe","Puesto"},c.empleados()::listarTodos,()->EntityForms.empleado(null,c),x->EntityForms.empleado(x,c),c.empleados()::guardar,c.empleados()::actualizar,x->c.empleados().eliminar(x.getCodigoEmpleado()),x->new Object[]{x.getCodigoEmpleado(),x.getNombre(),unir(x.getApellido1(),x.getApellido2()),x.getCodigoOficina(),x.getCodigoJefe(),x.getPuesto()});}
    private static String unir(String a,String b){return (a==null?"":a)+(b==null||b.isBlank()?"":" "+b);}
}
