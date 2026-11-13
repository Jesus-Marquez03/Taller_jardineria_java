package com.taller.infrastructure.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de contraste, estados, encabezados y tipografía visual. */
class ThemeTest {
    @Test
    void botonCoralMantieneTextoLegibleEnTodosSusEstados() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JButton button = new JButton("Guardar");
            Theme.styleActionButton(button, Theme.CORAL, Theme.CORAL_HOVER, Theme.TEXTO);

            assertEquals(Theme.CORAL, button.getBackground());
            assertEquals(Theme.TEXTO, button.getForeground());

            MouseEvent entered = new MouseEvent(button, MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), 0, 1, 1, 0, false);
            for (var listener : button.getMouseListeners()) {
                listener.mouseEntered(entered);
            }
            assertEquals(Theme.CORAL_HOVER, button.getBackground());

            button.setEnabled(false);
            assertEquals(Theme.DESACTIVADO, button.getBackground());
            assertEquals(Theme.TEXTO_DESACTIVADO, button.getForeground());
        });
    }

    @Test
    void navegacionYTablaTienenSeleccionVisible() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JButton navigation = new JButton("Clientes");
            Theme.styleNavigationButton(navigation);
            Theme.setNavigationSelected(navigation, true);
            assertEquals(Theme.CORAL, navigation.getBackground());
            assertEquals(Theme.TEXTO, navigation.getForeground());

            JTable table = new JTable(new DefaultTableModel(
                    new Object[][]{{1, "Planta"}}, new String[]{"Código", "Nombre"}));
            Theme.styleTable(table);
            assertEquals(Theme.CORAL_CLARO, table.getSelectionBackground());
            assertEquals(Theme.TEXTO, table.getSelectionForeground());
            assertEquals(Theme.VERDE_OSCURO, table.getTableHeader().getBackground());
            assertEquals(Color.WHITE, table.getTableHeader().getForeground());
            assertEquals(36, table.getTableHeader().getPreferredSize().height);

            for (int column = 0; column < table.getColumnCount(); column++) {
                var renderer = table.getTableHeader().getDefaultRenderer();
                var component = renderer.getTableCellRendererComponent(table,
                        table.getColumnName(column), false, false, -1, column);
                assertTrue(component instanceof JLabel);
                JLabel label = (JLabel) component;
                assertEquals(table.getColumnName(column), label.getText());
                assertEquals(Theme.VERDE_OSCURO, label.getBackground());
                assertEquals(Color.WHITE, label.getForeground());
                assertTrue(label.isOpaque());
            }
        });
    }

    @Test
    void combinacionesPrincipalesSuperanContrasteMinimo() {
        assertTrue(contrast(Theme.TEXTO, Theme.CORAL) >= 4.5);
        assertTrue(contrast(Color.WHITE, Theme.VERDE_OSCURO) >= 4.5);
        assertTrue(contrast(Color.WHITE, Theme.MARRON) >= 4.5);
        assertTrue(contrast(Theme.TEXTO, Theme.FONDO) >= 4.5);
    }

    @Test
    void tipografiaUsaLaPrimeraFamiliaDisponibleYRespetaLosTamanos() {
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        String expected = Arrays.stream(new String[]{"Trebuchet MS", "Segoe UI", "SansSerif"})
                .filter(preferred -> Arrays.stream(available)
                        .anyMatch(name -> name.equalsIgnoreCase(preferred)))
                .findFirst()
                .orElse(Font.SANS_SERIF);

        assertEquals(expected, Theme.FONT_FAMILY);
        assertEquals(24, Theme.MARCA.getSize());
        assertEquals(21, Theme.TITULO.getSize());
        assertEquals(14, Theme.BOTON.getSize());
        assertEquals(13, Theme.TABLA.getSize());
        assertEquals(13, Theme.TABLA_ENCABEZADO.getSize());
        assertEquals(Font.BOLD, Theme.TABLA_ENCABEZADO.getStyle());
        assertEquals(Theme.FONT_FAMILY, UIManager.getFont("Label.font").getFamily());
        assertEquals(Theme.FONT_FAMILY, UIManager.getFont("OptionPane.messageFont").getFamily());
        assertEquals(Theme.FONT_FAMILY, UIManager.getFont("OptionPane.buttonFont").getFamily());
    }

    private double contrast(Color first, Color second) {
        double lighter = Math.max(luminance(first), luminance(second));
        double darker = Math.min(luminance(first), luminance(second));
        return (lighter + 0.05) / (darker + 0.05);
    }

    private double luminance(Color color) {
        double red = channel(color.getRed());
        double green = channel(color.getGreen());
        double blue = channel(color.getBlue());
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    private double channel(int value) {
        double normalized = value / 255.0;
        return normalized <= 0.03928
                ? normalized / 12.92
                : Math.pow((normalized + 0.055) / 1.055, 2.4);
    }
}
