package com.taller;

import com.taller.infrastructure.config.ApplicationContainer;
import com.taller.infrastructure.config.DatabaseConfig;
import com.taller.infrastructure.ui.MainWindow;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Swing utiliza su apariencia predeterminada si la del sistema no esta disponible.
            }
            DatabaseConfig config = DatabaseConfig.getInstance();
            MainWindow window = new MainWindow(new ApplicationContainer(config));
            window.setVisible(true);
            if (!config.probarConexion()) {
                JOptionPane.showMessageDialog(window,
                        "No se pudo conectar con MySQL. La aplicacion seguira abierta para que revises Docker y las variables DB_*.",
                        "Conexion no disponible", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
