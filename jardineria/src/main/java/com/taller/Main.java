package com.taller;

import com.taller.infrastructure.config.ApplicationContainer;
import com.taller.infrastructure.config.DatabaseConfig;
import com.taller.infrastructure.ui.MainWindow;
import com.taller.infrastructure.ui.SplashScreen;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }

    private static void startApplication() {
        configureLookAndFeel();
        SplashScreen splash = new SplashScreen();
        splash.start();

        new SwingWorker<ApplicationContainer, Void>() {
            @Override
            protected ApplicationContainer doInBackground() {
                DatabaseConfig config = DatabaseConfig.getInstance();
                if (!config.probarConexion()) {
                    throw new IllegalStateException(
                            "No fue posible conectar con MySQL. Verifica Docker y las variables DB_*.");
                }

                ApplicationContainer container = new ApplicationContainer(config);
                // Confirma que el Dashboard puede consultar datos antes de mostrar la ventana.
                container.dashboard().obtenerResumen();
                return container;
            }

            @Override
            protected void done() {
                try {
                    ApplicationContainer container = get();
                    MainWindow window = new MainWindow(container);
                    splash.finish(() -> window.setVisible(true));
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    splash.showError("La inicialización fue interrumpida.");
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause() == null
                            ? exception : exception.getCause();
                    splash.showError(cause.getMessage());
                } catch (RuntimeException exception) {
                    splash.showError("No se pudo preparar la interfaz: " + exception.getMessage());
                }
            }
        }.execute();
    }

    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing conserva su apariencia predeterminada si la del sistema no está disponible.
        }
    }
}
