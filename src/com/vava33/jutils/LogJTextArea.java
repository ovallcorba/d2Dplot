/**
 * LogJTextArea
 * 
 * Version 131029
 * 
 * Copyright (C) Oriol Vallcorba 2013
 *  
 */
package com.vava33.jutils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

//import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

/**
 * Custom JTextArea for logging purposes.
 *
 * @author ovallcorba
 * @version %I%, %G%
 */
public class LogJTextArea extends JTextArea implements ActionListener {

    /** Autoscroll. */
    private static boolean autoscroll = true;

    /** Background color */
    private static Color back = Color.BLACK;
    
    /** Break line character */
    private static final String BL = System.getProperty("line.separator");
    
    /** The clear screen message to show */
    private static String clsMSG = "";
    
    /** timestamp format. */
    private static SimpleDateFormat fHora = new SimpleDateFormat("[HH:mm] ");
    
    /** Foreground Color */
    private static Color fore = Color.YELLOW;
    
    /** The Font size */
    private static float fsize;
    
    /** The increment to the font size. */
    private static float incFSize = 1.0f;
    
    /** The Font type. */
    private static Font lletra = new Font("Monospaced", Font.BOLD, 14);
    
    /** The maximum font size. */
    private static float maxFSize = 22.0f;
    
    /** The minimum font size. */
    private static float minFSize = 6.0f;
    
    /** The popup menu */
    private static JPopupMenu popup;
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The separation line width **/
    private static int lineWidth = 80;

    /**
     * Instantiates a new LogJTextArea
     */
    public LogJTextArea() {
        super();
        this.setFont(LogJTextArea.lletra);
        this.setMargin(new Insets(5, 5, 5, 5));
        this.setTabSize(4);
        this.setLineWrap(false);
        this.setWrapStyleWord(true);
        this.setForeground(LogJTextArea.fore);
        this.setBackground(LogJTextArea.back);
        this.setBorder(null);
//        this.setBorder(BorderFactory.createEmptyBorder());
        LogJTextArea.fsize = this.getFont().getSize();
        this.createPopupMenu();
        LogJTextArea.autoscroll = true;
    }

    /**
     * Gets the background color.
     *
     * @return the background color
     */
    public static Color getBack() {
        return LogJTextArea.back;
    }

    /**
     * Gets the BreakLine character.
     *
     * @return the BreakLine character
     */
    public static String getBl() {
        return LogJTextArea.BL;
    }

    /**
     * Gets the clear area message
     *
     * @return the clear area message
     */
    public static String getClsMSG() {
        return LogJTextArea.clsMSG;
    }

    /**
     * Gets the TimeStamp format.
     *
     * @return the TimeStamp format
     */
    public static SimpleDateFormat getfHora() {
        return LogJTextArea.fHora;
    }

    /**
     * Gets the foreground color.
     *
     * @return the foreground color
     */
    public static Color getFore() {
        return LogJTextArea.fore;
    }

    /**
     * Gets the increment to the Font Size.
     *
     * @return the increment to the Font Size
     */
    public static float getIncFSize() {
        return LogJTextArea.incFSize;
    }

    /**
     * Gets the Font type.
     *
     * @return the Font type
     */
    public static Font getLletra() {
        return LogJTextArea.lletra;
    }

    // *************** POP UP MENU ***************

    /**
     * Gets the max Font Size.
     *
     * @return the max Font Size
     */
    public static float getMaxFSize() {
        return LogJTextArea.maxFSize;
    }

    /**
     * Gets the min Font Size.
     *
     * @return the min Font Size
     */
    public static float getMinFSize() {
        return LogJTextArea.minFSize;
    }

    /**
     * Checks if autoscroll is on or off.
     *
     * @return the autoscroll
     */
    public static boolean isAutoscroll() {
        return LogJTextArea.autoscroll;
    }

    /**
     * Sets the autoscroll on/off.
     *
     * @param autoscroll    Set autoscroll on/off
     */
    public static void setAutoscroll(boolean autoscroll) {
        LogJTextArea.autoscroll = autoscroll;
    }

    /**
     * Sets the Background color.
     *
     * @param back  Background color
     */
    public static void setBack(Color back) {
        LogJTextArea.back = back;
    }

    /**
     * Sets the clear text area message.
     *
     * @param clsMSG    clear text area message
     */
    public static void setClsMSG(String clsMSG) {
        LogJTextArea.clsMSG = clsMSG;
    }

    /**
     * Sets the timestamp format.
     *
     * @param fHora     timestamp format
     */
    public static void setfHora(SimpleDateFormat fHora) {
        LogJTextArea.fHora = fHora;
    }

    /**
     * Sets the foreground color.
     *
     * @param fore      foreground color
     */
    public static void setFore(Color fore) {
        LogJTextArea.fore = fore;
    }

    /**
     * Sets the font size increment step.
     *
     * @param incFSize      font size increment step
     */
    public static void setIncFSize(float incFSize) {
        LogJTextArea.incFSize = incFSize;
    }

    /**
     * Sets the font type.
     *
     * @param lletra    font type
     */
    public static void setLletra(Font lletra) {
        LogJTextArea.lletra = lletra;
    }

    /**
     * Sets the maxiumum font size.
     *
     * @param maxFSize      maxiumum font size
     */
    public static void setMaxFSize(float maxFSize) {
        LogJTextArea.maxFSize = maxFSize;
    }

    /**
     * Sets the minimum font size.
     *
     * @param minFSize      minimum font size
     */
    public static void setMinFSize(float minFSize) {
        LogJTextArea.minFSize = minFSize;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) (e.getSource());
        if (source.getText().trim().startsWith("Copy")) {
            this.copy();
        }
        if (source.getText().trim().startsWith("Increase")) {
            this.incFontSize();
        }
        if (source.getText().trim().startsWith("Decrease")) {
            this.decFontSize();
        }
        if (source.getText().trim().startsWith("Clear")) {
            this.cls();
        }
        if (source.getText().trim().startsWith("Disable autoscroll")) {
            LogJTextArea.autoscroll = false;
            this.setCaretPosition(this.getDocument().getLength());
        }
        if (source.getText().trim().startsWith("Enable autoscroll")) {
            LogJTextArea.autoscroll = true;
        }
        if (source.getText().trim().startsWith("Disable line wrap")) {
            this.setLineWrap(false);
        }
        if (source.getText().trim().startsWith("Enable line wrap")) {
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
        }
        if (source.getText().trim().startsWith("Save to file")) {
            this.saveToFile();
        }
        this.createPopupMenu(); // tornem a crear (per si cal canviar etiqueta)
    }

    /**
     * Addtxt. Adds formatted text to the textArea
     *
     * @param saltLinia true for a break line after the text
     * @param timeStamp true for printing the time stamp before the text
     * @param text the text
     */
    public void addtxt(boolean saltLinia, boolean timeStamp, String text) {
        this.afegirText(saltLinia, timeStamp, text);
    }

    /**
     * Afegir salt linia.
     */
    public void afegirSaltLinia() {
        this.saltL();
    }

    /**
     * Adds a line of a certain character
     */
    public void afegirLinia(char c){
    	StringBuilder sb = new StringBuilder(lineWidth);
    	for(int i=0; i<lineWidth;i++){
    		sb.append(c);	
    	}
    	this.ln(sb.toString());
    }
    
    // missatge amb timestamp i salt linia
    /**
     * Afegir text.
     *
     * @param saltLinia true for a break line after the text
     * @param timeStamp true for printing the time stamp before the text
     * @param text the text
     */
    public void afegirText(boolean saltLinia, boolean timeStamp, String text) {

        if (timeStamp) {
            this.append(LogJTextArea.fHora.format(new Date()) + text);
        } else {
            this.append(text);
        }
        if (saltLinia) {
            this.append(LogJTextArea.BL);
        }
        this.posicionaCaret();
    }

    /**
     * Aplicar mida lletra.
     *
     * @param fsize the font size to apply
     */
    public void aplicarMidaLletra(float fsize) {
        this.setFont(this.getFont().deriveFont(fsize));
    }

    // neteja text
    /**
     * Cleans the textArea contents and prints the clsMSG
     */
    public void cls() {
        this.setText(LogJTextArea.clsMSG);
    }

    // ------ LLETRA
    /**
     * Decrease font size.
     */
    public void decFontSize() {
        if (LogJTextArea.fsize > LogJTextArea.minFSize) {
            LogJTextArea.fsize -= LogJTextArea.incFSize;
            this.setFont(this.getFont().deriveFont(LogJTextArea.fsize));
        }
    }

    /**
     * Gets the font size.
     *
     * @return the font size
     */
    public float getFsize() {
        return LogJTextArea.fsize;
    }

    /**
     * Gets the mida lletra.
     *
     * @return the mida lletra
     */
    public float getMidaLletra() {
        return this.getFont().getSize();
    }
    
    /**
     * Sets the mida lletra.
     *
     * @return the mida lletra
     */
    public void setMidaLletra(float size) {
        this.setFont(this.getFont().deriveFont(size));
    }

    // ------

    /**
     * Increase font size.
     */
    public void incFontSize() {
        if (LogJTextArea.fsize < LogJTextArea.maxFSize) {
            LogJTextArea.fsize += LogJTextArea.incFSize;
            this.setFont(this.getFont().deriveFont(LogJTextArea.fsize));
        }
    }

    // Simple missatge + BreakLine
    /**
     * Ln. Simple message + BreakLine
     *
     * @param t the text
     */
    public void ln(String t) {
        this.append(t);
        this.append(LogJTextArea.BL);
        this.posicionaCaret();
    }

    /**
     * Salt l. Adds Break line
     */
    public void saltL() {
        this.append(LogJTextArea.BL);
        this.posicionaCaret();
    }

    /**
     * Sets the font size.
     *
     * @param fsize the new font size
     */
    public void setFsize(float fsize) {
        LogJTextArea.fsize = fsize;
    }

    // Simple missatge amb hora + BreakLine
    /**
     * Stat. Simple message with timestamp and BreakLine
     * 
     * @param t the text
     */
    public void stat(String t) {
        this.append(LogJTextArea.fHora.format(new Date()) + t);
        this.append(LogJTextArea.BL);
        this.posicionaCaret();
    }

    
    //it will write a line containing name=value, name2=value2, ...
    // oneline means if it will be written in one line or one variable per line
    public void writeNameNumPairs(boolean oneline, String namesCommaSeparated, double... numbers){
        String[] nameslist = namesCommaSeparated.trim().split(",");
        if (nameslist.length != numbers.length){
            //print in two groups names = numbers
            writeNameNums(oneline, namesCommaSeparated,numbers);
            return;
        }
        //farem les parelles
        StringBuilder msg = new StringBuilder();
        for (int i=0; i<numbers.length; i++){
            msg.append(nameslist[i]+"="+FileUtils.dfX_5.format(numbers[i]));
            if (oneline){
                msg.append(" ");
            }else{
                msg.append("\n");
            }
         }
        this.ln(msg.toString().trim());
    }
    
    //prints two lines, one with the names, one with the values
    //if oneline=false, names i one line, numbers at the other
    public void writeNameNums(boolean oneline, String names, double... numbers){
//        LOG.info(names);
        StringBuilder msg = new StringBuilder();
        if (!oneline){
            this.ln(names.trim());
        }else{
            msg.append(names);
            msg.append(" = ");
        }
        for (int i=0; i<numbers.length; i++){
            msg.append(FileUtils.dfX_5.format(numbers[i]));
            msg.append(" ");
        }
        this.ln(msg.toString().trim());
    }
    
    //prints a list of floats (no names)
    public void writeFloats(double... numbers){
        StringBuilder msg = new StringBuilder();
        for (int i=0; i<numbers.length; i++){
            msg.append(FileUtils.dfX_5.format(numbers[i]));
            msg.append(" ");
        }
        this.ln(msg.toString().trim());
    }
    
    
    /**
     * Txt area_get line.TORNA UN STRING AMB LA LINIA ESPCEIFICADA I SI 
     * AQUESTA NO EXISTEIX AMB LA �LTIMA LINIA i si tampoc EXISTEIX TORNA "0"
     *
     * @param lineNumber the line number
     * @return the string
     */
    public String txtArea_getLine(int lineNumber) {
        int startIndex;
        try {
            startIndex = this.getLineStartOffset(lineNumber);
        } catch (Exception e) {
            // Aix� passara si lineNumber no es linia
            try {
                startIndex = this.getLineStartOffset(this.getLineCount() - 1);
            } catch (Exception e2) {
                return "0";
            }
        }
        String line = this.getText().substring(startIndex);
        return line;
    }

    /**
     * Creates the popup menu.
     */
    private void createPopupMenu() {
        JMenuItem menuItem;

        // Create the popup menu.
        LogJTextArea.popup = new JPopupMenu();
        menuItem = new JMenuItem("Copy");
        menuItem.addActionListener(this);
        LogJTextArea.popup.add(menuItem);
        menuItem = new JMenuItem("Clear");
        menuItem.addActionListener(this);
        LogJTextArea.popup.add(menuItem);
        LogJTextArea.popup.addSeparator();
        menuItem = new JMenuItem("Increase Font Size");
        menuItem.addActionListener(this);
        LogJTextArea.popup.add(menuItem);
        menuItem = new JMenuItem("Decrease Font Size");
        menuItem.addActionListener(this);
        LogJTextArea.popup.add(menuItem);
        LogJTextArea.popup.addSeparator();
        if (LogJTextArea.autoscroll) {
            menuItem = new JMenuItem("Disable autoscroll");
        } else {
            menuItem = new JMenuItem("Enable autoscroll");
        }
        menuItem.addActionListener(this);
        LogJTextArea.popup.add(menuItem);
        if (this.getLineWrap()) {
            menuItem = new JMenuItem("Disable line wrap");
        } else {
            menuItem = new JMenuItem("Enable line wrap");
        }
        menuItem.addActionListener(this);
        LogJTextArea.popup.add(menuItem);
        LogJTextArea.popup.addSeparator();
        menuItem = new JMenuItem("Save to file");
        menuItem.addActionListener(this);
        LogJTextArea.popup.add(menuItem);


        // Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(LogJTextArea.popup);
        this.addMouseListener(popupListener);
    }

    /**
     * Posiciona caret. If autoscroll is true, sets the caret position to the
     * end of the text
     */
    private void posicionaCaret() {
        if (LogJTextArea.autoscroll) {
            this.setCaretPosition(this.getDocument().getLength());
        }
    }
    
    /**
     * Saves the text to a file (fchooser)
     */
    private void saveToFile(){
    	File f = FileUtils.fchooser(null, new File(System.getProperty("user.dir")), null, true);
    	if (f!=null){
    		try{
                BufferedWriter out = new BufferedWriter(new FileWriter(f));
                out.write(this.getText());
                out.close();
                this.ln("File saved: "+f.toString());
            }catch(Exception e){
                 System.err.println("Error: " + e.getMessage());
                 e.printStackTrace();
                 this.ln("Error saving file: "+f.toString());
            }
    	}
    }
    

    /**
     * The listener interface for receiving popup events.
     * The class that is interested in processing a popup
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addPopupListener<code> method. When
     * the popup event occurs, that object's appropriate
     * method is invoked.
     *
     * @see PopupEvent
     */
    class PopupListener extends MouseAdapter {
        
        /** The popup. */
        JPopupMenu popup;

        /**
         * Instantiates a new popup listener.
         *
         * @param popupMenu the popup menu
         */
        PopupListener(JPopupMenu popupMenu) {
            this.popup = popupMenu;
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            this.maybeShowPopup(e);
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            this.maybeShowPopup(e);
        }

        /**
         * Maybe show popup.
         *
         * @param e the mouseEvent
         */
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                this.popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
