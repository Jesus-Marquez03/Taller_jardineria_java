package com.taller.infrastructure.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

            JTable table = new JTable(1, 1);
            Theme.styleTable(table);
            assertEquals(Theme.CORAL_CLARO, table.getSelectionBackground());
            assertEquals(Theme.TEXTO, table.getSelectionForeground());
            assertEquals(Theme.VERDE_OSCURO, table.getTableHeader().getBackground());
            assertEquals(Color.WHITE, table.getTableHeader().getForeground());
        });
    }

    @Test
    void combinacionesPrincipalesSuperanContrasteMinimo() {
        assertTrue(contrast(Theme.TEXTO, Theme.CORAL) >= 4.5);
        assertTrue(contrast(Color.WHITE, Theme.VERDE_OSCURO) >= 4.5);
        assertTrue(contrast(Color.WHITE, Theme.MARRON) >= 4.5);
        assertTrue(contrast(Theme.TEXTO, Theme.FONDO) >= 4.5);
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
