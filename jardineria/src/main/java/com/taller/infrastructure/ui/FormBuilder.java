package com.taller.infrastructure.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.text.JTextComponent;

final class FormBuilder {
    private final JPanel panel = new JPanel(new GridBagLayout());
    private final Map<String, JComponent> fields = new LinkedHashMap<>();
    private int row;

    FormBuilder() {
        panel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        panel.setBackground(Theme.FONDO);
    }

    JTextField text(String key, String label, Object value) {
        JTextField field = new JTextField(value == null ? "" : String.valueOf(value), 28);
        styleTextComponent(field);
        add(key, label, field);
        return field;
    }

    JTextArea area(String key, String label, String value) {
        JTextArea field = new JTextArea(value == null ? "" : value, 4, 28);
        field.setLineWrap(true);
        field.setWrapStyleWord(true);
        styleTextComponent(field);

        JScrollPane scrollPane = new JScrollPane(field);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDE));
        scrollPane.getViewport().setBackground(Theme.SUPERFICIE);
        addComponent(label, scrollPane);
        fields.put(key, field);
        return field;
    }

    <T> JComboBox<T> combo(String key, String label, List<T> values,
                           T selected, boolean nullable) {
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
        if (nullable) {
            model.addElement(null);
        }
        values.forEach(model::addElement);

        JComboBox<T> field = new JComboBox<>(model);
        field.setSelectedItem(selected);
        field.setFont(Theme.TEXTO_NORMAL);
        field.setForeground(Theme.TEXTO);
        field.setBackground(Theme.SUPERFICIE);
        field.setBorder(BorderFactory.createLineBorder(Theme.BORDE));
        add(key, label, field);
        return field;
    }

    void disable(JComponent component) {
        component.setEnabled(false);
        component.setBackground(Theme.PANEL);
        component.setForeground(Theme.TEXTO_DESACTIVADO);
        if (component instanceof JTextField textField) {
            textField.setDisabledTextColor(Theme.TEXTO_DESACTIVADO);
        }
    }

    String value(String key) {
        JComponent component = fields.get(key);
        String text = component instanceof JTextArea area
                ? area.getText() : ((JTextField) component).getText();
        return text.trim();
    }

    String optional(String key) {
        String value = value(key);
        return value.isBlank() ? null : value;
    }

    int intValue(String key) {
        try {
            return Integer.parseInt(value(key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El campo debe contener un número entero.");
        }
    }

    Integer optionalInt(String key) {
        String value = value(key);
        try {
            return value.isBlank() ? null : Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El campo debe contener un número entero.");
        }
    }

    BigDecimal decimal(String key) {
        try {
            return new BigDecimal(value(key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El campo debe contener un número decimal válido.");
        }
    }

    BigDecimal optionalDecimal(String key) {
        String value = value(key);
        try {
            return value.isBlank() ? null : new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El campo debe contener un número decimal válido.");
        }
    }

    LocalDate date(String key) {
        try {
            return LocalDate.parse(value(key));
        } catch (Exception exception) {
            throw new IllegalArgumentException("Usa el formato de fecha AAAA-MM-DD.");
        }
    }

    LocalDate optionalDate(String key) {
        String value = value(key);
        try {
            return value.isBlank() ? null : LocalDate.parse(value);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Usa el formato de fecha AAAA-MM-DD.");
        }
    }

    @SuppressWarnings("unchecked")
    <T> T selected(String key) {
        return (T) ((JComboBox<?>) fields.get(key)).getSelectedItem();
    }

    <T> T show(String title, Supplier<T> constructor) {
        AtomicReference<T> result = new AtomicReference<>();
        Window owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        JDialog dialog = new JDialog(owner, title, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(Theme.FONDO);
        dialog.setLayout(new BorderLayout(0, 10));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.FONDO);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton save = new JButton("Guardar");
        JButton cancel = new JButton("Cancelar");
        Theme.styleActionButton(save, Theme.CORAL, Theme.CORAL_HOVER,
                Theme.TEXTO);
        Theme.styleActionButton(cancel, Theme.VERDE_OSCURO, Theme.VERDE_HOVER);

        save.addActionListener(event -> {
            try {
                result.set(constructor.get());
                dialog.dispose();
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(dialog, exception.getMessage(),
                        "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
            }
        });
        cancel.addActionListener(event -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 8));
        actions.setBackground(Theme.FONDO);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        actions.add(cancel);
        actions.add(save);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(save);

        int height = Math.min(650, 150 + row * 51);
        dialog.setSize(new Dimension(650, Math.max(300, height)));
        dialog.setMinimumSize(new Dimension(610, 300));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        return result.get();
    }

    private void styleTextComponent(JTextComponent component) {
        component.setFont(Theme.TEXTO_NORMAL);
        component.setForeground(Theme.TEXTO);
        component.setBackground(Theme.SUPERFICIE);
        component.setCaretColor(Theme.VERDE_OSCURO);
        component.setSelectionColor(Theme.CORAL_CLARO);
        component.setSelectedTextColor(Theme.TEXTO);
        component.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDE),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)));
    }

    private void add(String key, String label, JComponent field) {
        fields.put(key, field);
        addComponent(label, field);
    }

    private void addComponent(String label, JComponent component) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(Theme.BOTON);
        fieldLabel.setForeground(Theme.TEXTO);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.NORTHWEST;
        labelConstraints.insets = new Insets(8, 4, 8, 14);
        panel.add(fieldLabel, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row++;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(8, 4, 8, 4);
        panel.add(component, fieldConstraints);
    }
}
