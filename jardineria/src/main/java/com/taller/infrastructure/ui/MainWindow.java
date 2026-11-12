package com.taller.infrastructure.ui;

import com.taller.infrastructure.config.ApplicationContainer;
import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {
    private final CardLayout cards=new CardLayout();
    private final JPanel content=new JPanel(cards);

    public MainWindow(ApplicationContainer container){
        super("Jardineria - Sistema de gestion");setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);setMinimumSize(new Dimension(1100,700));setSize(1280,800);setLocationRelativeTo(null);setLayout(new BorderLayout());
        JPanel menu=new JPanel();menu.setPreferredSize(new Dimension(225,0));menu.setBackground(Theme.VERDE_OSCURO);menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));menu.setBorder(BorderFactory.createEmptyBorder(24,14,20,14));
        JLabel marca=new JLabel("JARDINERIA");marca.setForeground(Color.WHITE);marca.setFont(new Font("SansSerif",Font.BOLD,22));marca.setAlignmentX(Component.LEFT_ALIGNMENT);menu.add(marca);menu.add(Box.createVerticalStrut(28));
        content.add(new InicioPanel(container.dashboard()),"inicio");content.add(CrudPanelsFactory.clientes(container),"clientes");content.add(CrudPanelsFactory.pedidos(container),"pedidos");content.add(CrudPanelsFactory.detalles(container),"detalles");content.add(CrudPanelsFactory.pagos(container),"pagos");content.add(CrudPanelsFactory.productos(container),"productos");content.add(CrudPanelsFactory.gamas(container),"gamas");content.add(CrudPanelsFactory.oficinas(container),"oficinas");content.add(CrudPanelsFactory.empleados(container),"empleados");
        addGroup(menu,"Inicio");addButton(menu,"Dashboard","inicio");addGroup(menu,"Gestion comercial");addButton(menu,"Clientes","clientes");addButton(menu,"Pedidos","pedidos");addButton(menu,"Detalles de pedido","detalles");addButton(menu,"Pagos","pagos");addGroup(menu,"Catalogo e inventario");addButton(menu,"Productos","productos");addButton(menu,"Gamas de producto","gamas");addGroup(menu,"Organizacion");addButton(menu,"Oficinas","oficinas");addButton(menu,"Empleados","empleados");menu.add(Box.createVerticalGlue());add(menu,BorderLayout.WEST);add(content,BorderLayout.CENTER);cards.show(content,"inicio");
    }
    private void addGroup(JPanel menu,String text){JLabel label=new JLabel(text.toUpperCase());label.setForeground(Theme.VERDE_CLARO);label.setFont(new Font("SansSerif",Font.BOLD,11));label.setBorder(BorderFactory.createEmptyBorder(12,6,5,0));label.setAlignmentX(Component.LEFT_ALIGNMENT);menu.add(label);}
    private void addButton(JPanel menu,String text,String card){JButton button=new JButton(text);button.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));button.setAlignmentX(Component.LEFT_ALIGNMENT);button.setHorizontalAlignment(SwingConstants.LEFT);button.setForeground(Color.WHITE);button.setBackground(Theme.VERDE_OSCURO);button.setBorder(BorderFactory.createEmptyBorder(8,10,8,8));button.setFocusPainted(false);button.addActionListener(e->cards.show(content,card));menu.add(button);}
}
