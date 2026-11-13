package com.taller.infrastructure.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * JPanel que pinta fondo y borde con esquinas redondeadas usando Java 2D.
 * Se reutiliza en tarjetas y secciones sin cambiar su comportamiento de panel.
 */
class RoundedPanel extends JPanel {
    private final Color backgroundColor;
    private final Color borderColor;
    private final int arc;

    RoundedPanel(Color backgroundColor) {
        this(backgroundColor, Theme.BORDE, 18);
    }

    RoundedPanel(Color backgroundColor, Color borderColor, int arc) {
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.arc = arc;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        // El fondo se dibuja manualmente porque setOpaque(false) evita el rectángulo normal.
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(backgroundColor);
        graphics2D.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        graphics2D.setColor(borderColor);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        graphics2D.dispose();
        super.paintComponent(graphics);
    }
}
