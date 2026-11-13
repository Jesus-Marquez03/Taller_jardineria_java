package com.taller.infrastructure.ui;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/** Verifica en el EDT que el Splash termine, ejecute su acción y se cierre. */
class SplashScreenTest {
    @Test
    void animacionTerminaYCierraLaVentana() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        AtomicReference<SplashScreen> reference = new AtomicReference<>();
        CountDownLatch completed = new CountDownLatch(1);
        long start = System.currentTimeMillis();

        SwingUtilities.invokeAndWait(() -> {
            SplashScreen splash = new SplashScreen();
            reference.set(splash);
            splash.start();
            splash.finish(completed::countDown);
        });

        assertTrue(completed.await(4, TimeUnit.SECONDS));
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration >= 1_000, "El Splash debe permanecer visible aproximadamente un segundo.");
        assertTrue(duration < 4_000, "El Splash no debe prolongarse innecesariamente.");
        assertFalse(reference.get().isVisible());
    }
}
