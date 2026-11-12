package com.taller.integration;

import com.taller.infrastructure.config.ApplicationContainer;
import com.taller.infrastructure.config.DatabaseConfig;
import com.taller.infrastructure.ui.MainWindow;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class SwingStartupIT {
    @Test
    void ventanaPrincipalSeConstruyeCorrectamente() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        AtomicReference<MainWindow> reference = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> reference.set(new MainWindow(
                new ApplicationContainer(DatabaseConfig.getInstance()))));
        try {
            assertEquals("Jardineria - Sistema de gestion", reference.get().getTitle());
        } finally {
            SwingUtilities.invokeAndWait(reference.get()::dispose);
        }
    }
}
