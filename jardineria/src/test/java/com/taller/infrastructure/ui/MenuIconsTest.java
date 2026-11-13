package com.taller.infrastructure.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Comprueba que cada icono se pueda pintar dentro del tamaño esperado. */
class MenuIconsTest {
    @Test
    void todosLosIconosTienenTamanoUniformeYPuedenDibujarse() {
        JLabel component = new JLabel();
        component.setForeground(Theme.VERDE_OSCURO);

        for (MenuIcons.Type type : MenuIcons.Type.values()) {
            Icon icon = MenuIcons.create(type);
            assertEquals(16, icon.getIconWidth());
            assertEquals(16, icon.getIconHeight());

            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            icon.paintIcon(component, graphics, 0, 0);
            graphics.dispose();

            assertTrue(hasVisiblePixel(image), "El icono " + type + " debe dibujar contenido");
        }
    }

    private boolean hasVisiblePixel(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                if (color.getAlpha() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
