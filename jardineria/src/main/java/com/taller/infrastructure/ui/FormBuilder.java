package com.taller.infrastructure.ui;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.*;

final class FormBuilder {
    private final JPanel panel = new JPanel(new GridBagLayout());
    private final Map<String,JComponent> fields = new LinkedHashMap<>();
    private int row;

    FormBuilder(){panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));panel.setBackground(Color.WHITE);}

    JTextField text(String key,String label,Object value){JTextField field=new JTextField(value==null?"":String.valueOf(value),28);add(key,label,field);return field;}
    JTextArea area(String key,String label,String value){JTextArea field=new JTextArea(value==null?"":value,4,28);field.setLineWrap(true);field.setWrapStyleWord(true);addComponent(label,new JScrollPane(field));fields.put(key,field);return field;}
    <T> JComboBox<T> combo(String key,String label,List<T> values,T selected,boolean nullable){DefaultComboBoxModel<T> model=new DefaultComboBoxModel<>();if(nullable)model.addElement(null);values.forEach(model::addElement);JComboBox<T> field=new JComboBox<>(model);field.setSelectedItem(selected);add(key,label,field);return field;}
    void disable(JComponent component){component.setEnabled(false);}
    String value(String key){return ((JTextComponentValue)()->{JComponent c=fields.get(key);return c instanceof JTextArea a?a.getText():((JTextField)c).getText();}).get().trim();}
    String optional(String key){String v=value(key);return v.isBlank()?null:v;}
    int intValue(String key){try{return Integer.parseInt(value(key));}catch(NumberFormatException e){throw new IllegalArgumentException("El campo debe contener un numero entero.");}}
    Integer optionalInt(String key){String v=value(key);try{return v.isBlank()?null:Integer.valueOf(v);}catch(NumberFormatException e){throw new IllegalArgumentException("El campo debe contener un numero entero.");}}
    BigDecimal decimal(String key){try{return new BigDecimal(value(key));}catch(NumberFormatException e){throw new IllegalArgumentException("El campo debe contener un numero decimal valido.");}}
    BigDecimal optionalDecimal(String key){String v=value(key);try{return v.isBlank()?null:new BigDecimal(v);}catch(NumberFormatException e){throw new IllegalArgumentException("El campo debe contener un numero decimal valido.");}}
    LocalDate date(String key){try{return LocalDate.parse(value(key));}catch(Exception e){throw new IllegalArgumentException("Usa el formato de fecha AAAA-MM-DD.");}}
    LocalDate optionalDate(String key){String v=value(key);try{return v.isBlank()?null:LocalDate.parse(v);}catch(Exception e){throw new IllegalArgumentException("Usa el formato de fecha AAAA-MM-DD.");}}
    @SuppressWarnings("unchecked") <T>T selected(String key){return (T)((JComboBox<?>)fields.get(key)).getSelectedItem();}

    <T>T show(String title,Supplier<T> constructor){
        JScrollPane scroll=new JScrollPane(panel);scroll.setPreferredSize(new Dimension(570,Math.min(520,80+row*48)));scroll.getVerticalScrollBar().setUnitIncrement(16);
        while(JOptionPane.showConfirmDialog(null,scroll,title,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION){
            try{return constructor.get();}catch(IllegalArgumentException e){JOptionPane.showMessageDialog(null,e.getMessage(),"Revisa el formulario",JOptionPane.WARNING_MESSAGE);}
        }
        return null;
    }
    private void add(String key,String label,JComponent field){fields.put(key,field);addComponent(label,field);}
    private void addComponent(String label,JComponent component){GridBagConstraints l=new GridBagConstraints();l.gridx=0;l.gridy=row;l.anchor=GridBagConstraints.NORTHWEST;l.insets=new Insets(6,4,6,12);panel.add(new JLabel(label),l);GridBagConstraints f=new GridBagConstraints();f.gridx=1;f.gridy=row++;f.weightx=1;f.fill=GridBagConstraints.HORIZONTAL;f.insets=new Insets(6,4,6,4);panel.add(component,f);}
    @FunctionalInterface private interface JTextComponentValue{String get();}
}
