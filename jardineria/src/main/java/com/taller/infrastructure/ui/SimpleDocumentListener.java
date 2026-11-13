package com.taller.infrastructure.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Adaptador pequeño que convierte los tres eventos de DocumentListener en una acción.
 * Se usa para filtrar JTable al insertar, borrar o cambiar texto de búsqueda.
 *
 * @param action código que debe ejecutarse cuando cambia el documento
 */
record SimpleDocumentListener(Runnable action) implements DocumentListener {
    @Override public void insertUpdate(DocumentEvent e){action.run();}
    @Override public void removeUpdate(DocumentEvent e){action.run();}
    @Override public void changedUpdate(DocumentEvent e){action.run();}
}
