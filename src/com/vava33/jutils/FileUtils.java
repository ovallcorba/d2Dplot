/**
 * FileUtils
 * 
 * Version 131029
 * 
 * Copyright (C) Oriol Vallcorba 2013
 *  
 */
package com.vava33.jutils;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

//import org.apache.commons.math3.stat.descriptive.moment.Mean;
//import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
//import org.apache.commons.math3.util.FastMath;



// TODO: Auto-generated Javadoc
/**
 * Class with static methods regarding file handling and String handling. 
 * Some of them require the use of a custom textArea to output messages.
 * 
 * @author ovallcorba
 * @version %I%, %G%
 * 
 */
public final class FileUtils {

    /** The decimal format #0.0 */
    public static DecimalFormat dfX_1 = new DecimalFormat("#0.0");
    
    /** The decimal format #0.00 */
    public static DecimalFormat dfX_2 = new DecimalFormat("#0.00");
    
    /** The decimal format #0.000 */
    public static DecimalFormat dfX_3 = new DecimalFormat("#0.000");
    
    /** The decimal format #0.0000 */
    public static DecimalFormat dfX_4 = new DecimalFormat("#0.0000");
    
    /** The decimal format #0.00000 */
    public static DecimalFormat dfX_5 = new DecimalFormat("#0.00000");
    
    /** The decimal format #0.00000 */
    public static DecimalFormat dfX_6 = new DecimalFormat("#0.000000");
    
    /** The currentlocale. */
    private static Locale currentlocale = Locale.getDefault();
    
    /** The lowercases. */
    private static char[] lowercases = { '\000', '\001', '\002', '\003',
            '\004', '\005', '\006', '\007', '\010', '\011', '\012', '\013',
            '\014', '\015', '\016', '\017', '\020', '\021', '\022', '\023',
            '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033',
            '\034', '\035', '\036', '\037', '\040', '\041', '\042', '\043',
            '\044', '\045', '\046', '\047', '\050', '\051', '\052', '\053',
            '\054', '\055', '\056', '\057', '\060', '\061', '\062', '\063',
            '\064', '\065', '\066', '\067', '\070', '\071', '\072', '\073',
            '\074', '\075', '\076', '\077', '\100', '\141', '\142', '\143',
            '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153',
            '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163',
            '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\133',
            '\134', '\135', '\136', '\137', '\140', '\141', '\142', '\143',
            '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153',
            '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163',
            '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\173',
            '\174', '\175', '\176', '\177' };

    /** The Decimal Format Symbols for current Locale. */
    private static DecimalFormatSymbols mySymbols = new DecimalFormatSymbols(
            FileUtils.currentlocale);
    
    /** The Operating System. */
    private static String os = "win";

    /** The separator character. */
    private static String separator = System.getProperty("file.separator");

    // M�tode que afegeix un car�cter enmig d'un CharArray
    /**
     * Adds to char array.
     *
     * @param original the original
     * @param index the index
     * @param newChar the new char
     * @return the char[]
     */
    public static char[] addToCharArray(char[] original, int index, char newChar) {
        char[] resultat = new char[original.length + 1];
        for (int i = 0; i < resultat.length; i++) {
            if (i == index) {
                resultat[index] = newChar;
            }
            if (i < index) {
                resultat[i] = original[i];
            }
            if (i > index) {
                resultat[i] = original[i - 1];
            }
        }
        return resultat;
    }

    /**
     * Unsigned Byte[1] to Integer
     *
     * @param b the byte array
     * @return the int
     */
    public static int B1UnsigtoInt(byte b) {
        int result = (0xFF & b);
        return result;
    }

    /**
     * Unsigned Byte[2] to integer (little endian convention)
     *
     * @param b the byte array
     * @return the int
     */
    public static int B2toInt(byte[] b) {
        int result = (((0xFF & b[1]) << 8) | (0xFF & b[0]));
        return result;
    }

    /**
     * Byte[4] to float.
     *
     * @param b the byte array
     * @return the float
     */
    public static float B4toFloat(byte[] b) {
        int asInt = (b[0] & 0xFF) | ((b[1] & 0xFF) << 8)
                | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);
        return Float.intBitsToFloat(asInt);
    }

    /**
     * Canvi extensio. Si no en t� l'afegeix.
     *
     * @param f the file
     * @param newExt the new ext
     * @return the file with new ext
     */
    public static File canviExtensio(File f, String newExt) {
        String path = f.toString(); // cami complert al fitxer
        String fname = f.getName(); // nom del fitxer (amb extensio si en té)
        path = path.substring(0, path.length() - fname.length()); // directori
                                                                  // del fitxer

        int i = fname.lastIndexOf('.');
        if (i > 0) { // si té extensio
            int midaExt = fname.length() - i;
            fname = fname.substring(0, fname.length() - midaExt);
            if (newExt.equals("")) {// volem treure l'extensio, la nova es sense
                                    // ext
                f = new File(path + fname);
            } else {// afegim l'extensio normal
                f = new File(path + fname + "." + newExt);
            }
        } else { // no té extensio
            if (newExt.equals("")) {// volem treure l'extensio, la nova es sense
                                    // ext
                f = new File(path + fname);
            } else {// afegim l'extensio normal
                f = new File(path + fname + "." + newExt);
            }
        }
        return f;
    }

    /**
     * Canvi nom fitxer. Deixa l'extensi� que tenia.
     *
     * @param f the file
     * @param nouNom the nou nom
     * @return the file amb nou nom
     */
    public static File canviNomFitxer(File f, String nouNom) {
        String path = f.toString(); // cami complert al fitxer
        String fname = f.getName(); // nom del fitxer (amb extensio si en te)
        path = path.substring(0, path.length() - fname.length()); // directori
                                                                  // del fitxer

        int i = fname.lastIndexOf('.');
        if (i > 0) { // si te extensio
            String ext = fname.substring(i + 1);
            f = new File(path + nouNom + "." + ext);
        } else { // no té extensio
            f = new File(path + nouNom);
        }
        return f;
    }

    /**
     * Confirm dialog.
     *
     * @param msg the msg
     * @param title the title
     * @return true, if successful
     */
    public static boolean confirmDialog(String msg, String title) {
        int n = JOptionPane.showConfirmDialog(null, msg, title,
                JOptionPane.YES_NO_OPTION);
        if ((n == JOptionPane.NO_OPTION) || (n == JOptionPane.CLOSED_OPTION)) {
            return false;
        }
        return true;
    }


    /**
     * Copy file. Metode que copia un fitxer, torna 0 si correcte o -1 si no 
     * ha anat be. Si borrar es true borra el fitxer origen. Guarda un backup
     * (.BAK) del fitxer dest� si existeix i es sobreescriu (ho pregunta)
     *
     * @param srFile the source file
     * @param dtFile the destination file
     * @param borrar if the source file should be deleted after copying
     * @param outputWin the LogJTextArea to show output messages
     * @return 0 if correct
     */
    public static int copyFile(File srFile, File dtFile, boolean borrar,
            LogJTextArea outputWin) {
        int ret = -1;
        try {
            if (srFile.equals(dtFile)) {
                return 1;
            }

            if (dtFile.exists()) {
                // preguntem si sobreescriure
                int n = JOptionPane.showConfirmDialog(null, "Overwrite "
                        + dtFile.getName() + "?", "File exists",
                        JOptionPane.YES_NO_OPTION);
                if ((n == JOptionPane.NO_OPTION)
                        || (n == JOptionPane.CLOSED_OPTION)) {
                    return -1;
                }
                // fem backup de l'anterior i sobreescribim (el backup
                // sobreescriu anteriors backups)
                File bakFile = new File(FileUtils.getFNameNoExt(dtFile)
                        + ".BAK");
                if (bakFile.exists()) {
                    bakFile.delete();
                }
                FileUtils.copyFile(dtFile, bakFile, false, outputWin);
                dtFile.delete(); // borrem RIB
            }

            InputStream in = new FileInputStream(srFile);

            // For Append the file.
            // OutputStream out = new FileOutputStream(dtFile,true);

            // For Overwrite the file.
            OutputStream out = new FileOutputStream(dtFile);

            byte[] buf = new byte[512];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            // log.addtxt(true,true,srFile.toString()+" copied to: "+dtFile.toString());

            if (dtFile.exists()&&outputWin!=null) {
                String c1 = "\u250C"; // corner sup-esquerra
                String c2 = "\u2514"; // corner inf-esquerra
                // String c3="\u2500"; // guio llarg
                outputWin.afegirText(true, true, c1 + " " + srFile.toString());
                outputWin.afegirText(true, true,
                            c2 + "> copied to: " + dtFile.toString());
            }
            ret = 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        if (ret == 0) {
            if (borrar && dtFile.exists()) {
                srFile.delete();
            }
        }
        return ret;
    }
    
    public static int copyFile(File srFile, File dtFile, boolean borrar) {
    	return copyFile(srFile, dtFile, borrar,null);
    }

    // de moment nomes suportem WIN i LIN. Si no es cap dels dos es tractara com
    // a WINDOWS per defecte
    /**
     * Detect Operating System. De moment nomes suportem WIN i LIN. Si no es cap
     *  dels dos es tractara com a WINDOWS per defecte
     */
    public static void detectOS() {
        // mirem sistema operatiu:
        String ops = System.getProperty("os.name").toLowerCase();
        if (ops.indexOf("win") >= 0) {
            System.out.println("Running on Windows");
            FileUtils.setOS("win");
            // } else if (os.indexOf("mac") >= 0) {
            // System.out.println("This is Mac");
            // os="mac";
        } else if ((ops.indexOf("nix") >= 0) || (ops.indexOf("nux") >= 0)
                || (ops.indexOf("aix") > 0)) {
            System.out.println("Running on Unix or Linux");
            FileUtils.setOS("lin");
            // } else if (os.indexOf("sunos") >= 0) {
            // System.out.println("This is Solaris");
            // os="sol"
        } else {
            System.out.println("Your OS is not supported!!");
        }
    }

    /**
     * Es senar. true if senar.
     *
     * @param iNumero the i numero
     * @return true, if successful
     */
    public static boolean esSenar(int iNumero) {
        if ((iNumero % 2) != 0) {
            return true;
        } else {
            return false;
        }
    }

//    public static File fchooserSimple(Component parent, File startDir, boolean save){
//        return fchooser(parent, startDir, null, 0, save, true);
//    }
    
    /**
     * Fchooser. Obra un File Chooser al directori especificat amb els filtres
     * especificats i retorna el fitxer seleccionat o null. TOTES LES OPCIONS
     *
     * @param startDir the start dir
     * @param filter the FileNameExtensionFilter array
     * @param defaultFilterIndex
     * @param multipleSelection allow multiple selection?
     * @param save is it a save dialog?
     * @param askowrite ask if overwrite?
     * @return the opened file
     */
    public static File fchooser(Component parent, File startDir, FileNameExtensionFilter[] filter, int defaultFilterIndex, boolean save, boolean askowrite) {
        // Creem un filechooser per seleccionar el fitxer obert
        JFileChooser fileChooser = new JFileChooser();
        if(startDir==null){
            startDir=new File(System.getProperty("user.dir"));
        }
        fileChooser.setCurrentDirectory(startDir); // directori inicial: el del
        if (filter != null) {
            for (int i = 0; i < filter.length; i++) {
                fileChooser.addChoosableFileFilter(filter[i]);
            }
            fileChooser.setFileFilter(filter[defaultFilterIndex]);
        }
        int selection;
        if(save){
            selection = fileChooser.showSaveDialog(parent);
        }else{
            selection = fileChooser.showOpenDialog(parent);   
        }
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (askowrite){
                if (f.exists()&& save){
                    int actionDialog = JOptionPane.showConfirmDialog(parent,
                            "Replace existing file?");
                    if (actionDialog == JOptionPane.NO_OPTION)return null;
                }
            }
            return f;
        } else {
            return null;
        }
    }

    /**
     * fchooserMultiple. Obra un File Chooser al directori especificat amb els filtres
     * especificats per llegir multiples fitxers
     *
     * @param startDir the start dir
     * @param filter the FileNameExtensionFilter array
     * @param defaultFilterIndex
     * @return the opened file
     */
    public static File[] fchooserMultiple(Component parent, File startDir, FileNameExtensionFilter[] filter, int defaultFilterIndex) {
        // Creem un filechooser per seleccionar el fitxer obert
        JFileChooser fileChooser = new JFileChooser();
        if(startDir==null){
            startDir=new File(System.getProperty("user.dir"));
        }
        fileChooser.setCurrentDirectory(startDir); // directori inicial: el del
        if (filter != null) {
            for (int i = 0; i < filter.length; i++) {
                fileChooser.addChoosableFileFilter(filter[i]);
            }
            fileChooser.setFileFilter(filter[defaultFilterIndex]);
        }
        int selection;
        fileChooser.setMultiSelectionEnabled(true);
        selection = fileChooser.showOpenDialog(parent);
        
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFiles();
        } else {
            return null;
        }
    }
    
    
    /**
     * Fchooser. Obra un File Chooser al directori especificat amb els filtres
     * especificats i retorna el fitxer seleccionat o null. Igual que anterior
     * per� aquest no demana si sobreescriure el fitxer (interpreta que es far�
     * posteriorment, �til per si es vol fer append).
     *
     * @param startDir the start dir
     * @param filter the FileNameExtensionFilter array
     * @return the opened file
     */
    public static File fchooser(Component parent, File startDir, FileNameExtensionFilter[] filter, boolean save) {
        return fchooser(parent, startDir, filter, 0, save, true);
    }
    
    /**
     * Fchooser. Obra un File Chooser al directori especificat amb els filtres
     * especificats i retorna el fitxer seleccionat o null. Igual que anterior
     * per� aquest no demana si sobreescriure el fitxer (interpreta que es far�
     * posteriorment, �til per si es vol fer append).
     *
     * @param startDir the start dir
     * @param filter the FileNameExtensionFilter array
     * @return the opened file
     */
    public static File fchooserSaveNoAsk(Component parent, File startDir, FileNameExtensionFilter[] filter) {
        return fchooser(parent, startDir, filter, 0, true, false);
    }
    
    public static File fchooserSaveAsk(Component parent, File startDir, FileNameExtensionFilter[] filter) {
        return fchooser(parent, startDir, filter, 0, true, true);
    }
    
    public static File fchooserOpen(Component parent, File startDir, FileNameExtensionFilter[] filter, int defaultFilterIndex) {
        return fchooser(parent, startDir, filter, defaultFilterIndex, false, false);
    }
    
    public static File fchooserOpenDir(Component parent, File startDir, String title) {
        // Creem un filechooser per seleccionar el fitxer obert
        JFileChooser fileChooser = new JFileChooser();
        if(startDir==null){
            startDir=new File(System.getProperty("user.dir"));
        }
        fileChooser.setCurrentDirectory(startDir); // directori inicial: el del

        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int selection;
        selection = fileChooser.showOpenDialog(parent);   
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            return f;
        } else {
            return null;
        }
    }
    
    public static boolean YesNoDialog(Component parent, String question){
        int actionDialog = JOptionPane.showConfirmDialog(parent,
                question);
        if (actionDialog == JOptionPane.YES_OPTION)return true;
        return false;
    }
    
    
    public static void InfoDialog(Component parent, String message, String title){
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    /**
     * Gets the extension of a file.
     *
     * @param f the file
     * @return the extension
     */
    public static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Gets the extension of a file
     *
     * @param s the string path of the file.
     * @return the extension
     */
    public static String getExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');

        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Gets the f name no ext.
     *
     * @param fn the fn
     * @return the f name no ext
     */
    public static String getFNameNoExt(File fn) {
        String ext = FileUtils.getExtension(fn);
        if (ext.length() > 0) {
            return fn.getPath().substring(0,
                    fn.getPath().length() - ext.length() - 1);
        } else {
            return fn.getPath();
        }
    }

    // retorna el nom del fitxer sense extensio
    /**
     * Gets the f name no ext.
     *
     * @param fn the fn
     * @return the f name no ext
     */
    public static String getFNameNoExt(String fn) {
        String ext = FileUtils.getExtension(fn);
        if (ext.length() > 0) {
            return fn.substring(0, fn.length() - ext.length() - 1);
        } else {
            return fn;
        }
    }

    /**
     * Gets the os.
     *
     * @return the os
     */
    public static String getOS() {
        return FileUtils.os;
    }

    /**
     * Gets the separator.
     *
     * @return the separator
     */
    public static String getSeparator() {
        return FileUtils.separator;
    }

    /**
     * Moure fitxer.
     *
     * @param fitxerOrigen the fitxer origen
     * @param dirDesti the dir desti
     * @param nouNom the nou nom
     * @return true, if successful
     */
    public static boolean moureFitxer(File fitxerOrigen, String dirDesti,
            String nouNom) {
        // Destination directory
        File dir = new File(dirDesti);

        // Move file to new directory
        boolean success = fitxerOrigen.renameTo(new File(dir, nouNom));
        if (!success) {
            return false;
        }
        return true;
    }

    /**
     * Random number.
     *
     * @param min the min
     * @param max the max
     * @return the int
     */
    public static int randomNumber(int min, int max) {
        return min + (new Random()).nextInt(max - min);
    }

    // treu els parentesis d'una String
    /**
     * Removes the brk.
     *
     * @param s the s
     * @return the string
     */
    public static String removeBrk(String s) {
        return s.replaceAll("\\(.*?\\)", "");
    }

    // M�tode que elimina un car�cter d'enmig d'un CharArray
    /**
     * Removes the from char array.
     *
     * @param original the original
     * @param index the index
     * @return the char[]
     */
    public static char[] removeFromCharArray(char[] original, int index) {
        char[] resultat = new char[original.length - 1];
        for (int i = 0; i < resultat.length; i++) {
            if (i < index) {
                resultat[i] = original[i];
            }
            if (i >= index) {
                resultat[i] = original[i + 1];
            }
        }
        return resultat;
    }

    /**
     * Sets the locale.
     */
    public static void setLocale() {
        // PER TEST:
        // System.out.println(mySymbols.getDecimalSeparator());
        // mySymbols.setDecimalSeparator('.');
        // System.out.println(mySymbols.getDecimalSeparator());
        // System.out.println(mySymbols.getGroupingSeparator());
        // System.out.println(dfX_3.format(12345678.009921));
        // dfX_3.setDecimalFormatSymbols(mySymbols);
        // System.out.println(dfX_3.format(12345678.009921));

        FileUtils.mySymbols.setDecimalSeparator('.');
        FileUtils.dfX_1.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_2.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_3.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_4.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_5.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_1.setGroupingUsed(false);
        FileUtils.dfX_2.setGroupingUsed(false);
        FileUtils.dfX_3.setGroupingUsed(false);
        FileUtils.dfX_4.setGroupingUsed(false);
        FileUtils.dfX_5.setGroupingUsed(false);

    }

    /**
     * Sets the os.
     *
     * @param op the new os
     */
    public static void setOS(String op) {
        FileUtils.os = op;
    }

    /**
     * Starts with ignore case.
     *
     * @param s the s
     * @param w the w
     * @return true, if successful
     */
    public static boolean startsWithIgnoreCase(String s, String w) {
        if (w == null) {
            return true;
        }

        if ((s == null) || (s.length() < w.length())) {
            return false;
        }

        for (int i = 0; i < w.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = w.charAt(i);
            if (c1 != c2) {
                if (c1 <= 127) {
                    c1 = FileUtils.lowercases[c1];
                }
                if (c2 <= 127) {
                    c2 = FileUtils.lowercases[c2];
                }
                if (c1 != c2) {
                    return false;
                }
            }
        }
        return true;
    }

    // format little endian
    /**
     * To bytes2.
     *
     * @param i the i
     * @return the byte[]
     */
    public static byte[] toBytes2(int i) {
        byte[] result = new byte[2];
        result[1] = (byte) (i >> 8);
        result[0] = (byte) (i >> 0);
        return result;
    }

    // converteix un string amb numeros xxx.xxxxx separats per un o mes espais
    // en un array que conté aquests numeros (també agafarà enters de fins a
    // 8 xifres)
    /**
     * X float string to array.
     *
     * @param linia the linia
     * @return the string[]
     */
    public static String[] xFloatStringToArray(String linia) {
        Scanner scanner = new Scanner(linia);
        Pattern p = Pattern.compile("\\d{1,3}.\\d{1,5}|\\d{1,8}");
        boolean fiLlista = false;
        String[] array = new String[20];
        int i = 0;

        while ((!fiLlista) && (i < 20)) {
            array[i] = scanner.findWithinHorizon(p, 0);
            if (array[i] != null) {
                i++;
            } else {
                fiLlista = true;
            }
        }
        scanner.close();
        return array;
    }

    // converteix un string amb numeros xxx.xxxxx separats per un o mes espais
    // en un array que conté aquests numeros INCLOU EXPONENCIALS
    // p = Pattern.compile("\\(P\\)|\\(C\\)|\\(I\\)|\\(F\\)|\\(R\\)"); | = OR
    /**
     * X float string to array2.
     *
     * @param linia the linia
     * @return the string[]
     */
    public static String[] xFloatStringToArray2(String linia) {
        Scanner scanner = new Scanner(linia);
        Pattern p = Pattern
                .compile("\\d{1,3}.\\d{2,5}[Ee]\\p{Punct}\\d{2}|\\d{1,3}.\\d{1,5}");
        boolean fiLlista = false;
        String[] array = new String[20];
        int i = 0;

        while ((!fiLlista) && (i < 20)) {
            array[i] = scanner.findWithinHorizon(p, 0);
            if (array[i] != null) {
                i++;
            } else {
                fiLlista = true;
            }
        }
        scanner.close();
        return array;
    }
    
    /**
     * Green implementation of regionMatches.
     *
     * @param cs the {@code CharSequence} to be processed
     * @param ignoreCase whether or not to be case insensitive
     * @param thisStart the index to start on the {@code cs} CharSequence
     * @param substring the {@code CharSequence} to be looked for
     * @param start the index to start on the {@code substring} CharSequence
     * @param length character length of the region
     * @return whether the region matched
     */
    public static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
            final CharSequence substring, final int start, final int length)    {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;

            while (tmpLen-- > 0) {
                char c1 = cs.charAt(index1++);
                char c2 = substring.charAt(index2++);

                if (c1 == c2) {
                    continue;
                }

                if (!ignoreCase) {
                    return false;
                }

                // The same check as in String.regionMatches():
                if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                        && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                    return false;
                }
            }

            return true;
        }
    }
    
    /**
     * <p>Checks if CharSequence contains a search CharSequence irrespective of case,
     * handling {@code null}. Case-insensitivity is defined as by
     * {@link String#equalsIgnoreCase(String)}.
     *
     * <p>A {@code null} CharSequence will return {@code false}.</p>
     *
     * <pre>
     * StringUtils.contains(null, *) = false
     * StringUtils.contains(*, null) = false
     * StringUtils.contains("", "") = true
     * StringUtils.contains("abc", "") = true
     * StringUtils.contains("abc", "a") = true
     * StringUtils.contains("abc", "z") = false
     * StringUtils.contains("abc", "A") = true
     * StringUtils.contains("abc", "Z") = false
     * </pre>
     *
     * @param str  the CharSequence to check, may be null
     * @param searchStr  the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence irrespective of
     * case or false if not or {@code null} string input
     * @since 3.0 Changed signature from containsIgnoreCase(String, String) to containsIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration,
            String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }


    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
    
}
