package com.taller.infrastructure.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;

/** Paleta y estilos sencillos compartidos por todas las pantallas Swing. */
final class Theme {
    static final Color FONDO = new Color(0xF7F1E6);
    static final Color PANEL = new Color(0xE8DFCE);
    static final Color SUPERFICIE = new Color(0xFFFDF8);
    static final Color CORAL = new Color(0xF47B50);
    static final Color CORAL_HOVER = new Color(0xEFA17F);
    static final Color CORAL_CLARO = new Color(0xFAD8C8);
    static final Color MARRON = new Color(0x9B532B);
    static final Color MARRON_HOVER = new Color(0x7F4020);
    static final Color VERDE_OSCURO = new Color(0x2F4639);
    static final Color VERDE = new Color(0x52705E);
    static final Color VERDE_HOVER = new Color(0x405B4B);
    static final Color TEXTO = new Color(0x29372F);
    static final Color TEXTO_SECUNDARIO = new Color(0x6F685E);
    static final Color BORDE = new Color(0xD2C4AE);
    static final Color ELIMINAR = new Color(0xB85445);
    static final Color ELIMINAR_HOVER = new Color(0x963F34);
    static final Color DESACTIVADO = new Color(0xD8D1C5);
    static final Color TEXTO_DESACTIVADO = new Color(0x625D56);

    static final Font TITULO = new Font("SansSerif", Font.BOLD, 25);
    static final Font SUBTITULO = new Font("SansSerif", Font.PLAIN, 13);
    static final Font SECCION = new Font("SansSerif", Font.BOLD, 16);
    static final Font TEXTO_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    static final Font BOTON = new Font("SansSerif", Font.BOLD, 13);

    static {
        // Evita que el sistema operativo vuelva invisible el texto desactivado.
        UIManager.put("Button.disabledText", TEXTO_DESACTIVADO);
        UIManager.put("Button.background", VERDE_OSCURO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", VERDE_HOVER);
        UIManager.put("Button.font", BOTON);
        UIManager.put("Table.selectionBackground", CORAL_CLARO);
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("Panel.background", FONDO);
    }

    private Theme() {
    }

    static void styleActionButton(JButton button, Color background, Color hover) {
        styleActionButton(button, background, hover, Color.WHITE);
    }

    static void styleActionButton(JButton button, Color background, Color hover,
                                  Color foreground) {
        prepareButton(button);
        button.putClientProperty("normalBackground", background);
        button.putClientProperty("hoverBackground", hover);
        button.putClientProperty("normalForeground", foreground);
        updateActionButton(button, false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                updateActionButton(button, true);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                updateActionButton(button, false);
            }
        });
        button.addPropertyChangeListener("enabled", event -> updateActionButton(button, false));
    }

    static void styleNavigationButton(JButton button) {
        prepareButton(button);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 40));
        button.putClientProperty("selectedNavigation", false);
        updateNavigationButton(button, false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                updateNavigationButton(button, true);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                updateNavigationButton(button, false);
            }
        });
        button.addPropertyChangeListener("enabled", event -> updateNavigationButton(button, false));
    }

    static void setNavigationSelected(JButton button, boolean selected) {
        button.putClientProperty("selectedNavigation", selected);
        updateNavigationButton(button, false);
    }

    static void styleTable(JTable table) {
        table.setFont(TEXTO_NORMAL);
        table.setForeground(TEXTO);
        table.setBackground(SUPERFICIE);
        table.setSelectionBackground(CORAL_CLARO);
        table.setSelectionForeground(TEXTO);
        table.setGridColor(new Color(0xE4D9C8));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(BOTON);
        table.getTableHeader().setBackground(VERDE_OSCURO);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setReorderingAllowed(false);
    }

    private static void prepareButton(JButton button) {
        button.setUI(new BasicButtonUI());
        button.setFont(BOTON);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setRolloverEnabled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(9, 15, 9, 15));
        button.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
    }

    private static void updateActionButton(JButton button, boolean hovered) {
        if (!button.isEnabled()) {
            button.setBackground(DESACTIVADO);
            button.setForeground(TEXTO_DESACTIVADO);
            button.setCursor(Cursor.getDefaultCursor());
            return;
        }
        Color normal = (Color) button.getClientProperty("normalBackground");
        Color hover = (Color) button.getClientProperty("hoverBackground");
        button.setBackground(hovered ? hover : normal);
        button.setForeground((Color) button.getClientProperty("normalForeground"));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static void updateNavigationButton(JButton button, boolean hovered) {
        if (!button.isEnabled()) {
            button.setBackground(DESACTIVADO);
            button.setForeground(TEXTO_DESACTIVADO);
            button.setCursor(Cursor.getDefaultCursor());
            return;
        }
        boolean selected = Boolean.TRUE.equals(button.getClientProperty("selectedNavigation"));
        if (selected) {
            button.setBackground(CORAL);
            button.setForeground(TEXTO);
        } else if (hovered) {
            button.setBackground(CORAL_CLARO);
            button.setForeground(VERDE_OSCURO);
        } else {
            button.setBackground(FONDO);
            button.setForeground(VERDE_OSCURO);
        }
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
