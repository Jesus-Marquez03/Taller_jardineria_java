package com.taller.integration;

import com.taller.infrastructure.config.ApplicationContainer;
import com.taller.infrastructure.config.DatabaseConfig;
import com.taller.infrastructure.ui.MainWindow;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Prueba de integración visual que construye MainWindow, navega sus tarjetas y
 * comprueba íconos, tablas y renderers sin modificar información.
 */
class SwingStartupIT {
    private static final Set<String> NAVIGATION_LABELS = Set.of(
            "Dashboard", "Clientes", "Pedidos", "Detalles de pedido", "Pagos",
            "Productos", "Gamas de producto", "Oficinas", "Empleados");

    @Test
    void ventanaPrincipalConstruyeYNavegaPorTodosLosModulos() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        AtomicReference<MainWindow> reference = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> reference.set(new MainWindow(
                new ApplicationContainer(DatabaseConfig.getInstance()))));

        try {
            MainWindow window = reference.get();
            assertEquals("Jardinería - Sistema de gestión", window.getTitle());

            List<JButton> navigation = findButtons(window.getContentPane()).stream()
                    .filter(button -> NAVIGATION_LABELS.contains(button.getText()))
                    .toList();
            assertEquals(NAVIGATION_LABELS.size(), navigation.size());
            for (JButton button : navigation) {
                assertNotNull(button.getIcon());
                assertEquals(16, button.getIcon().getIconWidth());
                assertEquals(16, button.getIcon().getIconHeight());
                assertEquals(9, button.getIconTextGap());
            }

            JPanel cardPanel = findCardPanel(window.getContentPane());
            assertNotNull(cardPanel);
            for (JButton button : navigation) {
                SwingUtilities.invokeAndWait(button::doClick);
                assertTrue(button.isOpaque());
                assertEquals(1, visibleComponents(cardPanel));
            }

            List<JTable> tables = findTables(window.getContentPane());
            assertEquals(9, tables.size()); // Ocho CRUD y la tabla del Dashboard.
            for (JTable table : tables) {
                assertTrue(table.getColumnCount() > 0);
                for (int column = 0; column < table.getColumnCount(); column++) {
                    var component = table.getTableHeader().getDefaultRenderer()
                            .getTableCellRendererComponent(table, table.getColumnName(column),
                                    false, false, -1, column);
                    assertTrue(component instanceof JLabel);
                    JLabel label = (JLabel) component;
                    assertFalse(label.getText().isBlank());
                    assertEquals(new Color(0x2F4639), label.getBackground());
                    assertEquals(Color.WHITE, label.getForeground());
                }
            }
        } finally {
            SwingUtilities.invokeAndWait(reference.get()::dispose);
        }
    }

    private List<JButton> findButtons(Container container) {
        List<JButton> buttons = new ArrayList<>();
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button) {
                buttons.add(button);
            }
            if (component instanceof Container child) {
                buttons.addAll(findButtons(child));
            }
        }
        return buttons;
    }

    private JPanel findCardPanel(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JPanel panel && panel.getLayout() instanceof CardLayout
                    && panel.getComponentCount() == NAVIGATION_LABELS.size()) {
                return panel;
            }
            if (component instanceof Container child) {
                JPanel found = findCardPanel(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private List<JTable> findTables(Container container) {
        List<JTable> tables = new ArrayList<>();
        for (Component component : container.getComponents()) {
            if (component instanceof JTable table) {
                tables.add(table);
            }
            if (component instanceof Container child) {
                tables.addAll(findTables(child));
            }
        }
        return tables;
    }

    private long visibleComponents(Container container) {
        return List.of(container.getComponents()).stream()
                .filter(Component::isVisible)
                .count();
    }
}
