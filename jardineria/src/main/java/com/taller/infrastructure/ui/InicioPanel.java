package com.taller.infrastructure.ui;

import java.awt.*;
import javax.swing.*;

public class InicioPanel extends JPanel {
    public InicioPanel(){setLayout(new GridBagLayout());setBackground(Theme.FONDO);JLabel label=new JLabel("Dashboard de Jardineria");label.setFont(Theme.TITULO);label.setForeground(Theme.VERDE_OSCURO);add(label);}
}
