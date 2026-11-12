package com.taller.infrastructure.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CrudPanel<T> extends JPanel {
    private final Supplier<List<T>> listar;
    private final Supplier<T> crearFormulario;
    private final Function<T,T> editarFormulario;
    private final Consumer<T> guardar;
    private final Consumer<T> actualizar;
    private final Consumer<T> eliminar;
    private final Function<T,Object[]> fila;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final TableRowSorter<DefaultTableModel> ordenador;
    private List<T> registros = new ArrayList<>();

    public CrudPanel(String titulo,String[] columnas,Supplier<List<T>> listar,Supplier<T> crearFormulario,
                     Function<T,T> editarFormulario,Consumer<T> guardar,Consumer<T> actualizar,
                     Consumer<T> eliminar,Function<T,Object[]> fila){
        this.listar=listar;this.crearFormulario=crearFormulario;this.editarFormulario=editarFormulario;
        this.guardar=guardar;this.actualizar=actualizar;this.eliminar=eliminar;this.fila=fila;
        setLayout(new BorderLayout(12,12));setBorder(BorderFactory.createEmptyBorder(22,22,22,22));setBackground(Theme.FONDO);
        JLabel encabezado=new JLabel(titulo);encabezado.setFont(Theme.TITULO);encabezado.setForeground(Theme.VERDE_OSCURO);
        JTextField buscar=new JTextField(24);buscar.putClientProperty("JTextField.placeholderText","Buscar...");
        JPanel superior=new JPanel(new BorderLayout(12,12));superior.setOpaque(false);superior.add(encabezado,BorderLayout.WEST);superior.add(buscar,BorderLayout.EAST);add(superior,BorderLayout.NORTH);
        modelo=new DefaultTableModel(columnas,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        tabla=new JTable(modelo);tabla.setRowHeight(26);tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);tabla.getTableHeader().setBackground(Theme.VERDE_CLARO);
        ordenador=new TableRowSorter<>(modelo);tabla.setRowSorter(ordenador);add(new JScrollPane(tabla),BorderLayout.CENTER);
        JPanel acciones=new JPanel(new FlowLayout(FlowLayout.LEFT));acciones.setOpaque(false);
        JButton nuevo=boton("Nuevo"),editar=boton("Editar"),borrar=boton("Eliminar"),refrescar=boton("Actualizar lista");
        acciones.add(nuevo);acciones.add(editar);acciones.add(borrar);acciones.add(refrescar);add(acciones,BorderLayout.SOUTH);
        buscar.getDocument().addDocumentListener(new SimpleDocumentListener(()->filtrar(buscar.getText())));
        nuevo.addActionListener(e->{T x=crearFormulario.get();if(x!=null)ejecutar(()->guardar.accept(x),"Registro guardado.");});
        editar.addActionListener(e->{T seleccionado=seleccionado();if(seleccionado!=null){T x=editarFormulario.apply(seleccionado);if(x!=null)ejecutar(()->actualizar.accept(x),"Registro actualizado.");}});
        borrar.addActionListener(e->{T x=seleccionado();if(x!=null&&JOptionPane.showConfirmDialog(this,"¿Eliminar el registro seleccionado?","Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)ejecutar(()->eliminar.accept(x),"Registro eliminado.");});
        refrescar.addActionListener(e->cargar());
        cargar();
    }

    public void cargar(){
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<List<T>,Void>(){
            @Override protected List<T> doInBackground(){return listar.get();}
            @Override protected void done(){try{registros=get();modelo.setRowCount(0);registros.forEach(x->modelo.addRow(fila.apply(x)));}catch(Exception e){mostrarError(e);}finally{setCursor(Cursor.getDefaultCursor());}}
        }.execute();
    }
    private void ejecutar(Runnable tarea,String mensaje){try{tarea.run();JOptionPane.showMessageDialog(this,mensaje);cargar();}catch(RuntimeException e){mostrarError(e);}}
    private T seleccionado(){int vista=tabla.getSelectedRow();if(vista<0){JOptionPane.showMessageDialog(this,"Selecciona un registro.");return null;}return registros.get(tabla.convertRowIndexToModel(vista));}
    private void filtrar(String texto){ordenador.setRowFilter(texto.isBlank()?null:RowFilter.regexFilter("(?i)"+Pattern.quote(texto)));}
    private JButton boton(String texto){JButton b=new JButton(texto);b.setFocusPainted(false);b.setBackground(Theme.VERDE);b.setForeground(Color.WHITE);return b;}
    private void mostrarError(Exception e){Throwable causa=e instanceof java.util.concurrent.ExecutionException&&e.getCause()!=null?e.getCause():e;JOptionPane.showMessageDialog(this,causa.getMessage(),"No se pudo completar la operacion",JOptionPane.ERROR_MESSAGE);}
}
