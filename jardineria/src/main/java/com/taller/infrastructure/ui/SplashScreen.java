package com.taller.infrastructure.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicProgressBarUI;

/** Pantalla breve de bienvenida mientras se prepara la aplicación. */
public class SplashScreen extends JWindow {
    private static final int MAX_PROGRESS = 100;
    private static final int WAITING_PROGRESS = 90;

    private final PlantPanel plantPanel = new PlantPanel();
    private final JProgressBar progressBar = new JProgressBar(0, MAX_PROGRESS);
    private final JLabel status = new JLabel("Iniciando sistema...", SwingConstants.CENTER);
    private final Timer animationTimer;

    private int progress;
    private boolean initializationComplete;
    private Runnable completionAction;

    public SplashScreen() {
        setSize(520, 340);
        setLocationRelativeTo(null);
        setContentPane(createContent());

        animationTimer = new Timer(28, event -> advanceAnimation());
        animationTimer.setCoalesce(true);
    }

    public void start() {
        progress = 0;
        initializationComplete = false;
        progressBar.setValue(0);
        plantPanel.setProgress(0);
        status.setText("Iniciando sistema...");
        setVisible(true);
        animationTimer.start();
    }

    public void finish(Runnable afterClose) {
        completionAction = afterClose;
        initializationComplete = true;
        status.setText("Preparando ventana principal...");
    }

    public void showError(String message) {
        animationTimer.stop();
        status.setText("No se pudo iniciar el sistema");
        status.setForeground(Theme.ELIMINAR);
        progressBar.setForeground(Theme.ELIMINAR);
        JOptionPane.showMessageDialog(this, message, "Error de inicialización",
                JOptionPane.ERROR_MESSAGE);
        dispose();
    }

    private JPanel createContent() {
        RoundedPanel content = new RoundedPanel(Theme.FONDO, Theme.BORDE, 24);
        content.setLayout(new BorderLayout(0, 14));
        content.setBorder(BorderFactory.createEmptyBorder(24, 42, 25, 42));

        plantPanel.setPreferredSize(new Dimension(115, 105));
        content.add(plantPanel, BorderLayout.NORTH);

        JLabel title = new JLabel("JARDINERÍA", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 29));
        title.setForeground(Theme.VERDE_OSCURO);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Cultivamos organización", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(Theme.MARRON);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);
        content.add(text, BorderLayout.CENTER);

        progressBar.setUI(new BasicProgressBarUI());
        progressBar.setForeground(Theme.CORAL);
        progressBar.setBackground(Theme.PANEL);
        progressBar.setBorder(BorderFactory.createLineBorder(Theme.BORDE));
        progressBar.setPreferredSize(new Dimension(420, 13));

        status.setFont(Theme.SUBTITULO);
        status.setForeground(Theme.TEXTO_SECUNDARIO);

        JPanel loading = new JPanel(new BorderLayout(0, 9));
        loading.setOpaque(false);
        loading.add(progressBar, BorderLayout.NORTH);
        loading.add(status, BorderLayout.SOUTH);
        content.add(loading, BorderLayout.SOUTH);
        return content;
    }

    private void advanceAnimation() {
        if (!initializationComplete && progress >= WAITING_PROGRESS) {
            status.setText("Cargando información inicial...");
            return;
        }

        progress = Math.min(MAX_PROGRESS, progress + 2);
        progressBar.setValue(progress);
        plantPanel.setProgress(progress);
        updateStatus();

        if (progress >= MAX_PROGRESS && initializationComplete) {
            animationTimer.stop();
            dispose();
            if (completionAction != null) {
                completionAction.run();
            }
        }
    }

    private void updateStatus() {
        if (initializationComplete && progress >= WAITING_PROGRESS) {
            status.setText("Preparando ventana principal...");
        } else if (progress < 35) {
            status.setText("Iniciando sistema...");
        } else if (progress < 70) {
            status.setText("Conectando componentes...");
        } else {
            status.setText("Cargando módulos...");
        }
    }

    private static class PlantPanel extends JPanel {
        private int progress;

        PlantPanel() {
            setOpaque(false);
        }

        void setProgress(int progress) {
            this.progress = progress;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            int baseY = getHeight() - 12;

            graphics2D.setColor(Theme.MARRON);
            graphics2D.fillRoundRect(centerX - 24, baseY - 25, 48, 29, 8, 8);
            graphics2D.setColor(Theme.CORAL);
            graphics2D.fillRoundRect(centerX - 28, baseY - 30, 56, 10, 7, 7);

            double growth = Math.max(0, Math.min(1, (progress - 12) / 70.0));
            int stemHeight = (int) (58 * growth);
            graphics2D.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            graphics2D.setColor(Theme.VERDE_OSCURO);
            graphics2D.drawLine(centerX, baseY - 30, centerX, baseY - 30 - stemHeight);

            if (progress >= 42) {
                int leafWidth = Math.min(31, 12 + (progress - 42) / 2);
                graphics2D.setColor(Theme.VERDE);
                graphics2D.fillOval(centerX - leafWidth, baseY - 68, leafWidth, 23);
            }
            if (progress >= 62) {
                int leafWidth = Math.min(32, 11 + (progress - 62));
                graphics2D.setColor(Theme.CORAL);
                graphics2D.fillOval(centerX, baseY - 85, leafWidth, 23);
            }
            if (progress >= 82) {
                graphics2D.setColor(Theme.VERDE_OSCURO);
                graphics2D.fillOval(centerX - 8, baseY - 96, 16, 16);
            }
            graphics2D.dispose();
        }
    }
}
