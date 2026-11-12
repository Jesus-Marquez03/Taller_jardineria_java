package com.taller.infrastructure.ui;

import com.taller.application.services.DashboardService;
import com.taller.domain.model.DashboardData;
import com.taller.domain.model.Pedido;
import java.awt.*;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InicioPanel extends JPanel {
    private final DashboardService service;
    private final KpiCard ingresos=new KpiCard("Ingresos registrados");
    private final KpiCard pendientes=new KpiCard("Pedidos pendientes");
    private final KpiCard entregados=new KpiCard("Pedidos entregados");
    private final KpiCard clientes=new KpiCard("Clientes registrados");
    private final KpiCard stock=new KpiCard("Stock critico (<= 10)");
    private final DefaultTableModel pedidosModel=new DefaultTableModel(new String[]{"Codigo","Fecha","Estado","Cliente"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
    private final StatusChart chart=new StatusChart();
    private final JButton refresh=new JButton("Actualizar indicadores");

    public InicioPanel(DashboardService service){
        this.service=service;setLayout(new BorderLayout(14,14));setBorder(BorderFactory.createEmptyBorder(22,22,22,22));setBackground(Theme.FONDO);
        JLabel title=new JLabel("Dashboard de Jardineria");title.setFont(Theme.TITULO);title.setForeground(Theme.VERDE_OSCURO);JPanel header=new JPanel(new BorderLayout());header.setOpaque(false);header.add(title,BorderLayout.WEST);refresh.setBackground(Theme.VERDE);refresh.setForeground(Color.WHITE);header.add(refresh,BorderLayout.EAST);add(header,BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(14,14));body.setOpaque(false);JPanel kpis=new JPanel(new GridLayout(1,5,12,12));kpis.setOpaque(false);kpis.add(ingresos);kpis.add(pendientes);kpis.add(entregados);kpis.add(clientes);kpis.add(stock);body.add(kpis,BorderLayout.NORTH);
        JTable table=new JTable(pedidosModel);table.setRowHeight(25);JPanel latest=section("Ultimos pedidos",new JScrollPane(table));JPanel graph=section("Estados de pedidos",chart);JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,graph,latest);split.setResizeWeight(.42);split.setBorder(null);body.add(split,BorderLayout.CENTER);add(body,BorderLayout.CENTER);
        refresh.addActionListener(e->cargar());cargar();
    }
    public void cargar(){refresh.setEnabled(false);new SwingWorker<DashboardData,Void>(){@Override protected DashboardData doInBackground(){return service.obtenerResumen();}@Override protected void done(){try{mostrar(get());}catch(Exception e){Throwable causa=e.getCause()==null?e:e.getCause();JOptionPane.showMessageDialog(InicioPanel.this,causa.getMessage(),"No se pudo cargar el dashboard",JOptionPane.ERROR_MESSAGE);}finally{refresh.setEnabled(true);}}}.execute();}
    private void mostrar(DashboardData d){NumberFormat number=NumberFormat.getNumberInstance();number.setMinimumFractionDigits(2);ingresos.set(number.format(d.totalIngresos()),d.cantidadPagos()==0?"Sin pagos registrados":"Pagos acumulados: "+d.cantidadPagos());pendientes.set(String.valueOf(d.pedidosPendientes()),"Estado Pendiente");entregados.set(String.valueOf(d.pedidosEntregados()),"Estado Entregado");clientes.set(String.valueOf(d.totalClientes()),"Clientes en la base");stock.set(String.valueOf(d.productosStockCritico()),d.totalProductos()==0?"Sin productos registrados":"De "+d.totalProductos()+" productos");pedidosModel.setRowCount(0);for(Pedido p:d.ultimosPedidos())pedidosModel.addRow(new Object[]{p.getCodigoPedido(),p.getFechaPedido(),p.getEstado(),p.getCodigoCliente()});chart.setData(d.pedidosPorEstado());}
    private JPanel section(String title,JComponent content){JPanel p=new JPanel(new BorderLayout(8,8));p.setBackground(Color.WHITE);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,225,220)),BorderFactory.createEmptyBorder(12,12,12,12)));JLabel l=new JLabel(title);l.setFont(new Font("SansSerif",Font.BOLD,16));l.setForeground(Theme.TEXTO);p.add(l,BorderLayout.NORTH);p.add(content,BorderLayout.CENTER);return p;}

    private static class KpiCard extends JPanel{
        private final JLabel value=new JLabel("-");private final JLabel note=new JLabel("Cargando...");
        KpiCard(String title){setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));setBackground(Color.WHITE);setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,225,220)),BorderFactory.createEmptyBorder(14,14,14,14)));JLabel name=new JLabel(title);name.setForeground(Theme.TEXTO);value.setFont(new Font("SansSerif",Font.BOLD,24));value.setForeground(Theme.VERDE_OSCURO);note.setForeground(Color.GRAY);add(name);add(Box.createVerticalStrut(8));add(value);add(Box.createVerticalStrut(4));add(note);}
        void set(String v,String n){value.setText(v);note.setText(n);}
    }
    private static class StatusChart extends JPanel{
        private Map<String,Integer> data=new LinkedHashMap<>();
        StatusChart(){setBackground(Color.WHITE);setPreferredSize(new Dimension(360,280));}
        void setData(Map<String,Integer> value){data=new LinkedHashMap<>(value);repaint();}
        @Override protected void paintComponent(Graphics g){super.paintComponent(g);Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);if(data.isEmpty()){g2.setColor(Color.GRAY);g2.drawString("No hay pedidos registrados",20,35);g2.dispose();return;}int max=data.values().stream().mapToInt(Integer::intValue).max().orElse(1);int y=28;for(var entry:data.entrySet()){g2.setColor(Theme.TEXTO);g2.drawString(entry.getKey()+" ("+entry.getValue()+")",16,y);int width=(int)((getWidth()-155)*(entry.getValue()/(double)max));g2.setColor(Theme.VERDE);g2.fillRoundRect(125,y-14,Math.max(4,width),18,8,8);y+=42;}g2.dispose();}
    }
}
