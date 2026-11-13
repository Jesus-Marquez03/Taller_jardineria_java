package com.taller.infrastructure.ui;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

/**
 * Fábrica de íconos pequeños dibujados con Java 2D para el menú lateral.
 *
 * <p>No usa PNG ni librerías. Cada {@link Icon} mide 16×16 y toma el color del
 * componente que lo pinta, por eso acompaña los estados del JButton.</p>
 */
final class MenuIcons {
    /** Enumera los nueve dibujos disponibles sin usar textos sueltos. */
    enum Type {
        DASHBOARD, CLIENTES, PEDIDOS, DETALLES, PAGOS,
        PRODUCTOS, GAMAS, OFICINAS, EMPLEADOS
    }

    private MenuIcons() {
    }

    /** @return un Icon inmutable que sabe dibujar el tipo solicitado */
    static Icon create(Type type) {
        return new NavigationIcon(type);
    }

    private static final class NavigationIcon implements Icon {
        private static final int SIZE = 16;
        private final Type type;

        private NavigationIcon(Type type) {
            this.type = type;
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            // Se copia Graphics2D para no alterar la configuración usada por Swing.
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.translate(x, y);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(component.getForeground());
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                switch (type) {
                    case DASHBOARD -> drawDashboard(g);
                    case CLIENTES -> drawClients(g);
                    case PEDIDOS -> drawCart(g);
                    case DETALLES -> drawList(g);
                    case PAGOS -> drawWallet(g);
                    case PRODUCTOS -> drawPlant(g);
                    case GAMAS -> drawLayers(g);
                    case OFICINAS -> drawBuilding(g);
                    case EMPLEADOS -> drawIdCard(g);
                }
            } finally {
                g.dispose();
            }
        }

        private void drawDashboard(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(1.5, 1.5, 5, 5, 1.5, 1.5));
            g.draw(new RoundRectangle2D.Double(9.5, 1.5, 5, 5, 1.5, 1.5));
            g.draw(new RoundRectangle2D.Double(1.5, 9.5, 5, 5, 1.5, 1.5));
            g.draw(new RoundRectangle2D.Double(9.5, 9.5, 5, 5, 1.5, 1.5));
        }

        private void drawClients(Graphics2D g) {
            g.draw(new Ellipse2D.Double(2.5, 2, 4, 4));
            g.draw(new Ellipse2D.Double(9.5, 3, 3.5, 3.5));
            g.draw(new Arc2D.Double(0.8, 6, 8, 8, 18, 144, Arc2D.OPEN));
            g.draw(new Arc2D.Double(7.5, 7, 7.5, 6.5, 20, 140, Arc2D.OPEN));
        }

        private void drawCart(Graphics2D g) {
            g.draw(new Line2D.Double(1, 2.5, 3.2, 2.5));
            Path2D basket = new Path2D.Double();
            basket.moveTo(3.2, 3);
            basket.lineTo(4.5, 10);
            basket.lineTo(12.5, 10);
            basket.lineTo(14.2, 5);
            basket.lineTo(4, 5);
            g.draw(basket);
            g.draw(new Ellipse2D.Double(5, 12, 1.8, 1.8));
            g.draw(new Ellipse2D.Double(11, 12, 1.8, 1.8));
        }

        private void drawList(Graphics2D g) {
            for (int row = 0; row < 3; row++) {
                double top = 2.5 + row * 5;
                g.draw(new RoundRectangle2D.Double(1.5, top, 2.5, 2.5, 0.6, 0.6));
                g.draw(new Line2D.Double(6, top + 1.2, 14.5, top + 1.2));
            }
        }

        private void drawWallet(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(1.5, 3, 13, 10.5, 2, 2));
            g.draw(new Line2D.Double(3, 3, 11.5, 1.5));
            g.draw(new RoundRectangle2D.Double(9, 6.5, 5.5, 4, 1.2, 1.2));
            g.fill(new Ellipse2D.Double(10.5, 8, 1, 1));
        }

        private void drawPlant(Graphics2D g) {
            g.draw(new Line2D.Double(8, 14.5, 8, 6));
            g.draw(new Arc2D.Double(2, 3, 6, 6, 270, 100, Arc2D.OPEN));
            g.draw(new Arc2D.Double(8, 1.5, 6, 6, 170, 100, Arc2D.OPEN));
            g.draw(new Arc2D.Double(3, 11, 10, 4, 20, 140, Arc2D.OPEN));
        }

        private void drawLayers(Graphics2D g) {
            drawLayer(g, 2);
            drawLayer(g, 6);
            drawLayer(g, 10);
        }

        private void drawLayer(Graphics2D g, double top) {
            Path2D layer = new Path2D.Double();
            layer.moveTo(8, top);
            layer.lineTo(14, top + 2.5);
            layer.lineTo(8, top + 5);
            layer.lineTo(2, top + 2.5);
            layer.closePath();
            g.draw(layer);
        }

        private void drawBuilding(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(2, 2.5, 12, 12, 1, 1));
            g.draw(new Line2D.Double(1.5, 2.5, 8, 0.8));
            for (int row = 0; row < 2; row++) {
                for (int column = 0; column < 3; column++) {
                    g.fill(new RoundRectangle2D.Double(4 + column * 3.2,
                            5 + row * 3, 1.2, 1.2, 0.3, 0.3));
                }
            }
            g.draw(new RoundRectangle2D.Double(7, 11, 2.5, 3.5, 0.5, 0.5));
        }

        private void drawIdCard(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(1, 3, 14, 10.5, 2, 2));
            g.draw(new Line2D.Double(6, 1.5, 10, 1.5));
            g.draw(new Line2D.Double(8, 1.5, 8, 3));
            g.draw(new Ellipse2D.Double(3, 5, 3, 3));
            g.draw(new Arc2D.Double(2.2, 8, 4.6, 3.5, 15, 150, Arc2D.OPEN));
            g.draw(new Line2D.Double(9, 6, 13, 6));
            g.draw(new Line2D.Double(9, 9, 13, 9));
        }
    }
}
