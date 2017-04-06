/**
 * 
 */
package com.vava33.jutils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ovallcorba
 *
 */
public final class ConsoleWritter {
    /** Break line character */
    private static final String BL = System.getProperty("line.separator");

    /** timestamp format. */
    private static SimpleDateFormat fHora = new SimpleDateFormat("[HH:mm] ");
    
    /** The separation line width **/
    private static int lineWidth = 80;

    /**
     * Afegir salt linia.
     */
    public static void afegirSaltLinia() {
        System.out.print(BL);
    }
    
    /**
     * Adds a line of a certain character
     */
    public static void afegirLinia(char c){
        StringBuilder sb = new StringBuilder(lineWidth);
        for(int i=0; i<lineWidth;i++){
            sb.append(c);   
        }
        System.out.println(sb.toString());
    }
    
    // missatge amb timestamp i salt linia
    /**
     * Afegir text.
     *
     * @param saltLinia true for a break line after the text
     * @param timeStamp true for printing the time stamp before the text
     * @param text the text
     */
    public static void afegirText(boolean saltLinia, boolean timeStamp, String text) {

        StringBuilder sb = new StringBuilder();
        
        if (timeStamp) {
            sb.append(fHora.format(new Date()) + text);
        } else {
            sb.append(text);
        }
        if (saltLinia) {
            sb.append(BL);
        }
        
        System.out.print(sb.toString());
    }
    
    // Simple missatge amb hora + BreakLine
    /**
     * Stat. Simple message with timestamp and BreakLine
     * 
     * @param t the text
     */
    public static void stat(String t) {
        StringBuilder sb = new StringBuilder();
        sb.append(fHora.format(new Date()) + t);
        sb.append(BL);
        System.out.print(sb.toString());
    }

    
    //it will write a line containing name=value, name2=value2, ...
    // oneline means if it will be written in one line or one variable per line
    public static void writeNameNumPairs(boolean oneline, String namesCommaSeparated, double... numbers){
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
        System.out.println(msg.toString().trim());
    }
    
    //prints two lines, one with the names, one with the values
    //if oneline=false, names i one line, numbers at the other
    public static void writeNameNums(boolean oneline, String names, double... numbers){
//        LOG.info(names);
        StringBuilder msg = new StringBuilder();
        if (!oneline){
            System.out.print(names.trim());
        }else{
            msg.append(names);
            msg.append(" = ");
        }
        for (int i=0; i<numbers.length; i++){
            msg.append(FileUtils.dfX_5.format(numbers[i]));
            msg.append(" ");
        }
        System.out.println(msg.toString().trim());
    }
    
    //prints a list of floats (no names)
    public static void writeFloats(double... numbers){
        StringBuilder msg = new StringBuilder();
        for (int i=0; i<numbers.length; i++){
            msg.append(FileUtils.dfX_5.format(numbers[i]));
            msg.append(" ");
        }
        System.out.println(msg.toString().trim());
    }
    
}
