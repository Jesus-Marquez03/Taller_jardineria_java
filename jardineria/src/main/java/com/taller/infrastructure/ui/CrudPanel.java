package com.taller.infrastructure.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Panel Swing reutilizable para listar, buscar, crear, editar y eliminar entidades.
 *
 * <p>{@code T} representa cualquiera de las ocho entidades. En vez de escribir
 * ocho paneles casi iguales, el constructor recibe funciones que indican cómo
 * listar, abrir formularios, guardar y convertir una entidad en fila. JTable
 * muestra los datos y JScrollPane permite desplazarlos.</p>
 */
public class CrudPanel<T> extends JPanel {
    private final Supplier<List<T>> listar;
    private final Supplier<T> crearFormulario;
    private final Function<T, T> editarFormulario;
    private final Consumer<T> guardar;
    private final Consumer<T> actualizar;
    private final Consumer<T> eliminar;
    private final Function<T, Object[]> fila;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final TableRowSorter<DefaultTableModel> ordenador;
    private final JLabel recordCount = new JLabel("Cargando registros...");
    private final JButton refreshButton = new JButton("Actualizar lista");
    private List<T> registros = new ArrayList<>();

    /**
     * Recibe el título, columnas y operaciones específicas de cada entidad.
     * Supplier produce datos sin parámetros; Consumer recibe una entidad y
     * Function transforma un valor en otro.
     */
    public CrudPanel(String titulo, String[] columnas, Supplier<List<T>> listar,
                     Supplier<T> crearFormulario, Function<T, T> editarFormulario,
                     Consumer<T> guardar, Consumer<T> actualizar, Consumer<T> eliminar,
                     Function<T, Object[]> fila) {
        this.listar = listar;
        this.crearFormulario = crearFormulario;
        this.editarFormulario = editarFormulario;
        this.guardar = guardar;
        this.actualizar = actualizar;
        this.eliminar = eliminar;
        this.fila = fila;

        setLayout(new BorderLayout(14, 14));
        setBorder(BorderFactory.createEmptyBorder(24, 26, 22, 26));
        setBackground(Theme.FONDO);

        add(createHeader(titulo), BorderLayout.NORTH);

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Theme.styleTable(tabla);
        ordenador = new TableRowSorter<>(modelo);
        tabla.setRowSorter(ordenador);
        add(createTableCard(), BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);

        cargar();
    }

    /** Crea el título y un campo cuyo DocumentListener filtra mientras se escribe. */
    private JPanel createHeader(String title) {
        JLabel heading = new JLabel(title);
        heading.setFont(Theme.TITULO);
        heading.setForeground(Theme.VERDE_OSCURO);

        JLabel searchLabel = new JLabel("Buscar");
        searchLabel.setFont(Theme.BOTON);
        searchLabel.setForeground(Theme.TEXTO);

        JTextField search = new JTextField(22);
        search.setFont(Theme.TEXTO_NORMAL);
        search.setForeground(Theme.TEXTO);
        search.setBackground(Theme.SUPERFICIE);
        search.setCaretColor(Theme.VERDE_OSCURO);
        search.setSelectionColor(Theme.CORAL_CLARO);
        search.setSelectedTextColor(Theme.TEXTO);
        search.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDE),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        search.getAccessibleContext().setAccessibleName("Buscar registros de " + title);
        search.getDocument().addDocumentListener(
                new SimpleDocumentListener(() -> filtrar(search.getText())));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchLabel);
        searchPanel.add(search);

        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);
        header.add(heading, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createTableCard() {
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.SUPERFICIE);

        RoundedPanel card = new RoundedPanel(Theme.SUPERFICIE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    /**
     * Crea botones y ActionListener. Cada listener obtiene el formulario o la
     * selección y luego llama a la función recibida por el constructor.
     */
    private JPanel createActions() {
        JButton newButton = new JButton("Nuevo");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Eliminar");

        Theme.styleActionButton(newButton, Theme.CORAL, Theme.CORAL_HOVER,
                Theme.TEXTO);
        Theme.styleActionButton(editButton, Theme.MARRON, Theme.MARRON_HOVER);
        Theme.styleActionButton(deleteButton, Theme.ELIMINAR, Theme.ELIMINAR_HOVER);
        Theme.styleActionButton(refreshButton, Theme.VERDE_OSCURO, Theme.VERDE_HOVER);

        newButton.addActionListener(event -> {
            T entity = crearFormulario.get();
            if (entity != null) {
                ejecutar(() -> guardar.accept(entity), "Registro guardado.");
            }
        });
        editButton.addActionListener(event -> {
            T selected = seleccionado();
            if (selected != null) {
                T entity = editarFormulario.apply(selected);
                if (entity != null) {
                    ejecutar(() -> actualizar.accept(entity), "Registro actualizado.");
                }
            }
        });
        deleteButton.addActionListener(event -> {
            T entity = seleccionado();
            if (entity != null && JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el registro seleccionado?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                ejecutar(() -> eliminar.accept(entity), "Registro eliminado.");
            }
        });
        refreshButton.addActionListener(event -> cargar());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        buttons.setOpaque(false);
        buttons.add(newButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        recordCount.setFont(Theme.SUBTITULO);
        recordCount.setForeground(Theme.TEXTO_SECUNDARIO);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(buttons, BorderLayout.WEST);
        actions.add(recordCount, BorderLayout.EAST);
        return actions;
    }

    /**
     * Consulta los registros fuera del EDT mediante SwingWorker.
     * {@code done()} actualiza modelo y etiquetas cuando termina la consulta.
     */
    public void cargar() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        refreshButton.setEnabled(false);
        recordCount.setText("Actualizando registros...");
        new SwingWorker<List<T>, Void>() {
            @Override
            protected List<T> doInBackground() {
                return listar.get();
            }

            @Override
            protected void done() {
                try {
                    registros = get();
                    modelo.setRowCount(0);
                    registros.forEach(entity -> modelo.addRow(fila.apply(entity)));
                    recordCount.setText(registros.size() + (registros.size() == 1
                            ? " registro" : " registros"));
                } catch (Exception exception) {
                    recordCount.setText("No se pudieron cargar los registros");
                    mostrarError(exception);
                } finally {
                    refreshButton.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    /** Ejecuta una operación CRUD, muestra resultado y recarga la tabla. */
    private void ejecutar(Runnable task, String message) {
        try {
            task.run();
            JOptionPane.showMessageDialog(this, message, "Operación completada",
                    JOptionPane.INFORMATION_MESSAGE);
            cargar();
        } catch (RuntimeException exception) {
            mostrarError(exception);
        }
    }

    /**
     * Convierte el índice visual (posiblemente ordenado) al índice del modelo.
     * @return entidad seleccionada o {@code null} si no se eligió una fila
     */
    private T seleccionado() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un registro.",
                    "Registro requerido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return registros.get(tabla.convertRowIndexToModel(viewRow));
    }

    /** Aplica una expresión segura e insensible a mayúsculas a todas las columnas. */
    private void filtrar(String text) {
        ordenador.setRowFilter(text.isBlank() ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
    }

    /** Extrae la causa de SwingWorker y presenta un mensaje legible. */
    private void mostrarError(Exception exception) {
        Throwable cause = exception instanceof java.util.concurrent.ExecutionException
                && exception.getCause() != null ? exception.getCause() : exception;
        JOptionPane.showMessageDialog(this, cause.getMessage(),
                "No se pudo completar la operación", JOptionPane.ERROR_MESSAGE);
    }
}
