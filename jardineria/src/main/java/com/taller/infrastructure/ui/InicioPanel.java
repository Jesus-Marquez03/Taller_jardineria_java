package com.taller.infrastructure.ui;

import com.taller.application.services.DashboardService;
import com.taller.domain.model.DashboardData;
import com.taller.domain.model.Pedido;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Pantalla inicial que presenta los indicadores del negocio.
 *
 * <p>Es un JPanel de infraestructura visual. Consulta {@link DashboardService},
 * distribuye tarjetas con LayoutManagers, muestra una JTable de pedidos y
 * dibuja un gráfico sencillo con Java 2D.</p>
 */
public class InicioPanel extends JPanel {
    private final DashboardService service;
    private final KpiCard ingresos = new KpiCard("Ingresos registrados", Theme.CORAL_CLARO);
    private final KpiCard pendientes = new KpiCard("Pedidos pendientes", Theme.PANEL);
    private final KpiCard entregados = new KpiCard("Pedidos entregados", Theme.SUPERFICIE);
    private final KpiCard clientes = new KpiCard("Clientes registrados", Theme.PANEL);
    private final KpiCard stock = new KpiCard("Stock crítico (<= 10)", Theme.CORAL_CLARO);
    private final DefaultTableModel pedidosModel = new DefaultTableModel(
            new String[]{"Código", "Fecha", "Estado", "Cliente"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final StatusChart chart = new StatusChart();
    private final JButton refresh = new JButton("Actualizar indicadores");

    /** Recibe el caso de uso, arma la pantalla y carga los datos iniciales. */
    public InicioPanel(DashboardService service) {
        this.service = service;
        setLayout(new BorderLayout(14, 14));
        setBorder(BorderFactory.createEmptyBorder(24, 26, 22, 26));
        setBackground(Theme.FONDO);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);

        refresh.addActionListener(event -> cargar());
        cargar();
    }

    private JPanel createHeader() {
        JLabel title = new JLabel("Dashboard de Jardinería");
        title.setFont(Theme.TITULO);
        title.setForeground(Theme.VERDE_OSCURO);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Resumen actualizado de la operación del negocio");
        subtitle.setFont(Theme.SUBTITULO);
        subtitle.setForeground(Theme.TEXTO_SECUNDARIO);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(subtitle);

        Theme.styleActionButton(refresh, Theme.CORAL, Theme.CORAL_HOVER,
                Theme.TEXTO);
        refresh.getAccessibleContext().setAccessibleDescription(
                "Vuelve a consultar los indicadores en MySQL");

        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        header.add(text, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);
        return header;
    }

    /** Combina tarjetas, gráfico y tabla usando GridLayout, BorderLayout y JSplitPane. */
    private JPanel createBody() {
        JPanel kpis = new JPanel(new GridLayout(1, 5, 10, 0));
        kpis.setOpaque(false);
        kpis.add(ingresos);
        kpis.add(pendientes);
        kpis.add(entregados);
        kpis.add(clientes);
        kpis.add(stock);

        JTable table = new JTable(pedidosModel);
        Theme.styleTable(table);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Theme.SUPERFICIE);

        JPanel latest = createSection("Últimos pedidos", tableScroll);
        JPanel graph = createSection("Estados de pedidos", chart);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graph, latest);
        split.setResizeWeight(0.42);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(Theme.FONDO);

        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);
        body.add(kpis, BorderLayout.NORTH);
        body.add(split, BorderLayout.CENTER);
        return body;
    }

    /**
     * Usa SwingWorker para consultar MySQL sin congelar botones ni repintado.
     * Desactiva temporalmente Actualizar para impedir solicitudes duplicadas.
     */
    public void cargar() {
        refresh.setEnabled(false);
        refresh.setText("Actualizando...");
        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() {
                return service.obtenerResumen();
            }

            @Override
            protected void done() {
                try {
                    mostrar(get());
                } catch (Exception exception) {
                    Throwable cause = exception.getCause() == null
                            ? exception : exception.getCause();
                    JOptionPane.showMessageDialog(InicioPanel.this, cause.getMessage(),
                            "No se pudo cargar el Dashboard", JOptionPane.ERROR_MESSAGE);
                } finally {
                    refresh.setText("Actualizar indicadores");
                    refresh.setEnabled(true);
                }
            }
        }.execute();
    }

    /** Copia el objeto de resumen a tarjetas, tabla y gráfico. */
    private void mostrar(DashboardData data) {
        NumberFormat number = NumberFormat.getNumberInstance();
        number.setMinimumFractionDigits(2);
        ingresos.set(number.format(data.totalIngresos()), data.cantidadPagos() == 0
                ? "Sin pagos registrados" : "Pagos acumulados: " + data.cantidadPagos());
        pendientes.set(String.valueOf(data.pedidosPendientes()), "Estado Pendiente");
        entregados.set(String.valueOf(data.pedidosEntregados()), "Estado Entregado");
        clientes.set(String.valueOf(data.totalClientes()), "Clientes en la base");
        stock.set(String.valueOf(data.productosStockCritico()), data.totalProductos() == 0
                ? "Sin productos registrados" : "De " + data.totalProductos() + " productos");

        pedidosModel.setRowCount(0);
        for (Pedido pedido : data.ultimosPedidos()) {
            pedidosModel.addRow(new Object[]{pedido.getCodigoPedido(), pedido.getFechaPedido(),
                    pedido.getEstado(), pedido.getCodigoCliente()});
        }
        chart.setData(data.pedidosPorEstado());
    }

    private JPanel createSection(String title, JComponent content) {
        RoundedPanel panel = new RoundedPanel(Theme.SUPERFICIE);
        panel.setLayout(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel label = new JLabel(title);
        label.setFont(Theme.SECCION);
        label.setForeground(Theme.VERDE_OSCURO);
        panel.add(label, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    /** Tarjeta visual que agrupa nombre, valor grande y nota secundaria. */
    private static class KpiCard extends RoundedPanel {
        private final JLabel value = new JLabel("-");
        private final JLabel note = new JLabel("Cargando...");

        KpiCard(String title, Color background) {
            super(background);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
            setPreferredSize(new Dimension(145, 112));

            JLabel name = new JLabel(title);
            name.setFont(Theme.TARJETA);
            name.setForeground(Theme.TEXTO);
            value.setFont(Theme.VALOR_INDICADOR);
            value.setForeground(Theme.VERDE_OSCURO);
            note.setFont(Theme.TEXTO_PEQUENO);
            note.setForeground(Theme.TEXTO_SECUNDARIO);

            add(name);
            add(Box.createVerticalStrut(7));
            add(value);
            add(Box.createVerticalStrut(3));
            add(note);
        }

        void set(String newValue, String newNote) {
            value.setText(newValue);
            note.setText(newNote);
        }
    }

    /** Dibuja barras proporcionales con Graphics2D sin una librería externa. */
    private static class StatusChart extends JPanel {
        private final Color[] barColors = {Theme.CORAL, Theme.MARRON, Theme.VERDE};
        private Map<String, Integer> data = new LinkedHashMap<>();

        StatusChart() {
            setBackground(Theme.SUPERFICIE);
            setPreferredSize(new Dimension(340, 260));
        }

        void setData(Map<String, Integer> value) {
            data = new LinkedHashMap<>(value);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            // Swing entrega Graphics; se crea una copia Graphics2D para dibujar con suavizado.
            super.paintComponent(graphics);
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setFont(Theme.TEXTO_NORMAL);

            if (data.isEmpty()) {
                graphics2D.setColor(Theme.TEXTO_SECUNDARIO);
                graphics2D.drawString("No hay pedidos registrados", 18, 34);
                graphics2D.dispose();
                return;
            }

            int max = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            int y = 30;
            int colorIndex = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                graphics2D.setColor(Theme.TEXTO);
                graphics2D.drawString(entry.getKey() + " (" + entry.getValue() + ")", 14, y);
                int availableWidth = Math.max(40, getWidth() - 145);
                int width = (int) (availableWidth * (entry.getValue() / (double) max));
                graphics2D.setColor(barColors[colorIndex++ % barColors.length]);
                graphics2D.fillRoundRect(120, y - 15, Math.max(5, width), 18, 9, 9);
                y += 42;
            }
            graphics2D.dispose();
        }
    }
}
