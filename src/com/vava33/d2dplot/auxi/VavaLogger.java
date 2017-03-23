/**
 * 
 */
package vava33.plot2d.auxi;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.vava33.jutils.FileUtils;

/**
 * @author ovallcorba
 *
 */
public class VavaLogger {

	public static Logger LOG = Logger.getLogger("DEBUG MODE");
	private static SimpleDateFormat fHora = new SimpleDateFormat("[HH:mm]");
	
	public static void initLogger(){
		LOG.setUseParentHandlers(false);
		Handler ch = new ConsoleHandler();
		ch.setFormatter(new FormatterDEBUG());
		LOG.addHandler(ch);
	}
	
	public static void enableLogger(){
		LOG.setLevel(Level.ALL);
//		LOG.info("** LOGGING ENABLED **");
	}
	
	public static void setLogLevel(String level){
        if (level.equalsIgnoreCase("config"))setCONFIG();
        if (level.equalsIgnoreCase("debug"))setCONFIG();
        if (level.equalsIgnoreCase("info"))setINFO();
        if (level.equalsIgnoreCase("warning"))setWARNING();
        if (level.equalsIgnoreCase("severe"))setSEVERE();
        if (level.equalsIgnoreCase("fine"))setFINE();
	}
	
    public static void setINFO(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.INFO);
        }
//        LOG.setLevel(Level.INFO);
//      LOG.info("** LOGGING ENABLED **");
    }
    
    public static void setWARNING(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.WARNING);
        }
    }
    
    public static void setCONFIG(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.CONFIG);
        }
    }
    
    public static void setSEVERE(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.SEVERE);
        }
    }
    
    public static void setFINE(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.FINE);
        }
//        LOG.setLevel(Level.INFO);
//      LOG.info("** LOGGING ENABLED **");
    }
	
	public static void disableLogger(){
//		LOG.info("** LOGGING DISABLED **");
		LOG.setLevel(Level.OFF);
	}
//	public static void info(String s){
//		LOG.info(s);
//	}
//	public static void warning(String s){
//		LOG.warning(s);
//	}
//	public static void config(String s){
//		LOG.config(s);
//	}
	
	
	//it will write a line containing name=value, name2=value2, ...
	// oneline means if it will be written in one line or one variable per line
	public static void writeNameNumPairs(String level, boolean oneline, String namesCommaSeparated, double... numbers){
	    String[] nameslist = namesCommaSeparated.trim().split(",");
	    if (nameslist.length != numbers.length){
	        //print in two groups names = numbers
	        writeNameNums(level, oneline, namesCommaSeparated,numbers);
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
        printmsg(level,msg.toString().trim());
	}
	
	private static void printmsg(String LEVEL, String msg){
        if (LEVEL.equalsIgnoreCase("config"))LOG.config(msg);
        if (LEVEL.equalsIgnoreCase("debug"))LOG.config(msg);
        if (LEVEL.equalsIgnoreCase("info"))LOG.info(msg);
        if (LEVEL.equalsIgnoreCase("warning"))LOG.warning(msg);
        if (LEVEL.equalsIgnoreCase("severe"))LOG.severe(msg);
        if (LEVEL.equalsIgnoreCase("fine"))LOG.fine(msg);
	}
	
    //prints two lines, one with the names, one with the values
	//if oneline=false, names i one line, numbers at the other
    public static void writeNameNums(String level, boolean oneline, String names, double... numbers){
//        LOG.info(names);
        StringBuilder msg = new StringBuilder();
        if (!oneline){
            printmsg(level,names.trim());
        }else{
            msg.append(names);
            msg.append(" = ");
        }
        for (int i=0; i<numbers.length; i++){
            msg.append(FileUtils.dfX_5.format(numbers[i]));
            msg.append(" ");
        }
        printmsg(level,msg.toString().trim());
    }
    
	//prints a list of floats (no names)
	public static void writeFloats(String level, double... numbers){
	    StringBuilder msg = new StringBuilder();
	    for (int i=0; i<numbers.length; i++){
	        msg.append(FileUtils.dfX_5.format(numbers[i]));
	        msg.append(" ");
	    }
	    printmsg(level,msg.toString().trim());
	}
	

	public static class FormatterDEBUG extends Formatter {
		 
	    @Override
	    public String format(LogRecord record) {
	    	String dt = fHora.format(new Date(record.getMillis()));
	        return dt+" "
  	        		+record.getThreadID()+"::"
	        		+record.getSourceClassName()+":"
	                +record.getSourceMethodName()+" ["
	                +record.getLevel()+"] "
	                +record.getMessage()+"\n";
	    }
	 
	}
	
    public static class FormatterUSER extends Formatter {
        
        @Override
        public String format(LogRecord record) {
            String dt = fHora.format(new Date(record.getMillis()));
            return dt+" ["
                    +record.getLevel()+"] "
                    +record.getMessage()+"\n";
        }
     
    }	
	
}
