package com.taller;

import com.taller.infrastructure.config.ApplicationContainer;
import com.taller.infrastructure.config.DatabaseConfig;
import com.taller.infrastructure.ui.MainWindow;
import com.taller.infrastructure.ui.SplashScreen;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación de escritorio.
 *
 * <p>Su responsabilidad es preparar Swing, mostrar el Splash Screen, comprobar
 * MySQL y construir las dependencias. No contiene reglas del negocio. Separa el
 * trabajo lento en un {@link SwingWorker} para no bloquear el Event Dispatch
 * Thread (EDT), que es el hilo encargado de dibujar y responder a los botones.</p>
 */
public class Main {
    /**
     * Inicia Swing en su hilo seguro mediante {@code invokeLater}.
     * @param args argumentos de consola; esta aplicación no necesita ninguno
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }

    /** Coordina la pantalla de bienvenida y la carga real de la aplicación. */
    private static void startApplication() {
        configureLookAndFeel();
        SplashScreen splash = new SplashScreen();
        splash.start();

        new SwingWorker<ApplicationContainer, Void>() {
            @Override
            protected ApplicationContainer doInBackground() {
                // Este bloque corre fuera del EDT porque conectarse y consultar puede tardar.
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
                // done() vuelve al EDT: aquí ya es seguro crear y mostrar componentes Swing.
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

    /** Intenta adoptar la apariencia del sistema operativo sin hacerla obligatoria. */
    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing conserva su apariencia predeterminada si la del sistema no está disponible.
        }
    }
}
