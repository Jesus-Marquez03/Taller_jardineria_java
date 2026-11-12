package com.taller.infrastructure.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

record SimpleDocumentListener(Runnable action) implements DocumentListener {
    @Override public void insertUpdate(DocumentEvent e){action.run();}
    @Override public void removeUpdate(DocumentEvent e){action.run();}
    @Override public void changedUpdate(DocumentEvent e){action.run();}
}
