package com.taller.infrastructure.ui;

import com.taller.infrastructure.config.ApplicationContainer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

public class MainWindow extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();

    public MainWindow(ApplicationContainer container) {
        super("Jardinería - Sistema de gestión");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.FONDO);

        configureCards(container);
        add(createSideMenu(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        showCard("inicio");
    }

    private void configureCards(ApplicationContainer container) {
        content.setBackground(Theme.FONDO);
        content.add(new InicioPanel(container.dashboard()), "inicio");
        content.add(CrudPanelsFactory.clientes(container), "clientes");
        content.add(CrudPanelsFactory.pedidos(container), "pedidos");
        content.add(CrudPanelsFactory.detalles(container), "detalles");
        content.add(CrudPanelsFactory.pagos(container), "pagos");
        content.add(CrudPanelsFactory.productos(container), "productos");
        content.add(CrudPanelsFactory.gamas(container), "gamas");
        content.add(CrudPanelsFactory.oficinas(container), "oficinas");
        content.add(CrudPanelsFactory.empleados(container), "empleados");
    }

    private JScrollPane createSideMenu() {
        JPanel menu = new JPanel();
        menu.setBackground(Theme.FONDO);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(24, 18, 18, 18));

        menu.add(createBrand());
        menu.add(Box.createVerticalStrut(20));

        addGroup(menu, "Inicio");
        addButton(menu, "Dashboard", "inicio", MenuIcons.Type.DASHBOARD);

        addGroup(menu, "Gestión comercial");
        addButton(menu, "Clientes", "clientes", MenuIcons.Type.CLIENTES);
        addButton(menu, "Pedidos", "pedidos", MenuIcons.Type.PEDIDOS);
        addButton(menu, "Detalles de pedido", "detalles", MenuIcons.Type.DETALLES);
        addButton(menu, "Pagos", "pagos", MenuIcons.Type.PAGOS);

        addGroup(menu, "Catálogo e inventario");
        addButton(menu, "Productos", "productos", MenuIcons.Type.PRODUCTOS);
        addButton(menu, "Gamas de producto", "gamas", MenuIcons.Type.GAMAS);

        addGroup(menu, "Organización");
        addButton(menu, "Oficinas", "oficinas", MenuIcons.Type.OFICINAS);
        addButton(menu, "Empleados", "empleados", MenuIcons.Type.EMPLEADOS);

        menu.add(Box.createVerticalGlue());
        JLabel footer = new JLabel("Sistema de gestión");
        footer.setFont(Theme.SUBTITULO);
        footer.setForeground(Theme.TEXTO_SECUNDARIO);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(footer);

        JScrollPane scrollPane = new JScrollPane(menu);
        scrollPane.setPreferredSize(new Dimension(255, 0));
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDE));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.getViewport().setBackground(Theme.FONDO);
        return scrollPane;
    }

    private JPanel createBrand() {
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("JARDINERÍA");
        title.setForeground(Theme.VERDE_OSCURO);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Cultivamos organización");
        subtitle.setForeground(Theme.MARRON);
        subtitle.setFont(Theme.SUBTITULO);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(title);
        brand.add(Box.createVerticalStrut(3));
        brand.add(subtitle);
        return brand;
    }

    private void addGroup(JPanel menu, String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setForeground(Theme.MARRON);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setBorder(BorderFactory.createEmptyBorder(14, 8, 6, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(label);
    }

    private void addButton(JPanel menu, String text, String card, MenuIcons.Type iconType) {
        JButton button = new JButton(text);
        button.setIcon(MenuIcons.create(iconType));
        button.setIconTextGap(9);
        Theme.styleNavigationButton(button);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(event -> showCard(card));
        navigationButtons.put(card, button);
        menu.add(button);
        menu.add(Box.createVerticalStrut(3));
    }

    private void showCard(String card) {
        cards.show(content, card);
        navigationButtons.forEach((name, button) ->
                Theme.setNavigationSelected(button, name.equals(card)));
    }
}
