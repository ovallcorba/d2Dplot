package com.vava33.d2dplot.auxi;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.ImagePanel;
import com.vava33.d2dplot.ConvertTo1DXRD;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public final class ImgFileUtils {
    private static final String className = "ImgFileUtils";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    public static enum SupportedReadExtensions {
        BIN, IMG, SPR, GFRM, EDF, D2D, TIF, CBF;
    }

    public static enum SupportedWriteExtensions {
        D2D, EDF, IMG, BIN, TIF;
    }

    //canvi HashMap per LinkedHashMap per tal de mantenir l'ordre d'inserció (sino era depenent del hash")
    public static final Map<String, String> formatInfo; 
    static {
        formatInfo = new LinkedHashMap<String, String>(); // ext, description
        formatInfo.put("d2d", "D2Dplot D2D Data file (*.d2d)");
        formatInfo.put("edf", "EDF Data file (*.edf)");
        formatInfo.put("img", "IMG Data file (*.img)");
        formatInfo.put("tif", "TIFF image format (*.tif)");
        formatInfo.put("bin", "D2Dplot BIN Data file (*.bin)");
        formatInfo.put("spr", "Spreadsheet (ascii) data file (*.spr)");
        formatInfo.put("gfrm", "Bruker (GADDS) data file (*.gfrm)");
        formatInfo.put("cbf", "Dectris Pilatus image format (*.cbf)");
   }

    public static FileNameExtensionFilter[] getExtensionFilterWrite() {
        // mirem quins formats som capaços de salvar segons ImgFileUtils
        Iterator<String> itrformats = ImgFileUtils.formatInfo.keySet()
                .iterator();
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[ImgFileUtils.SupportedWriteExtensions
                .values().length];
        int nfiltre = 0;
        while (itrformats.hasNext()) {
            String frm = itrformats.next();
            // this line returns the FORMAT in the ENUM or NULL
            ImgFileUtils.SupportedWriteExtensions wfrm = FileUtils.searchEnum(
                    ImgFileUtils.SupportedWriteExtensions.class, frm);
            if (wfrm != null) {
                // afegim filtre
                filter[nfiltre] = new FileNameExtensionFilter(
                        ImgFileUtils.formatInfo.get(frm), frm);
                nfiltre = nfiltre + 1;
            }
        }
        return filter;
    }

    public static FileNameExtensionFilter[] getExtensionFilterRead() {
        // mirem quins formats som capaços de salvar segons ImgFileUtils
        Iterator<String> itrformats = ImgFileUtils.formatInfo.keySet()
                .iterator();
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[ImgFileUtils.SupportedReadExtensions
                .values().length + 1]; // +1 for all image formats
        String[] frmStrings = new String[ImgFileUtils.SupportedReadExtensions
                .values().length];
        int nfiltre = 1; //comença a 1 perque el zero el reservem per tots els formats
        while (itrformats.hasNext()) {
            String frm = itrformats.next();
            // this line returns the FORMAT in the ENUM or NULL
            ImgFileUtils.SupportedReadExtensions wfrm = FileUtils.searchEnum(
                    ImgFileUtils.SupportedReadExtensions.class, frm);
            if (wfrm != null) {
                // afegim filtre
                filter[nfiltre] = new FileNameExtensionFilter(
                        ImgFileUtils.formatInfo.get(frm), frm);
                frmStrings[nfiltre-1] = frm; //-1 perque he començat a 1
                nfiltre = nfiltre + 1;
            }
        }
        // afegim filtre de tots els formats com a primera opció
        filter[0] = new FileNameExtensionFilter(
                "All 2D-XRD supported formats", frmStrings);
        return filter;
    }

    //returns the new value if line matches, otherwise the previous one
    private static int getIntKeyword(String line, String keyword, String separator, int corrLastCharacter, int currentValue) {
        //retorna un enter despres d'una keyword donada i el simbol de separacio, treu l'ultim caracter si corrLastCharacter es 1 (aplicarà -1)
        if (FileUtils.containsIgnoreCase(line, keyword)) {
            int iigual = line.trim().indexOf(separator) + 1;
            return Integer.parseInt(line.trim().substring(iigual, line.trim().length() - corrLastCharacter).trim());
        }
        return currentValue;
    }

    //returns the new value if line matches, otherwise the previous one
    private static float getFloatKeyword(String line, String keyword, String separator, int corrLastCharacter, float currentValue) {
        //retorna un enter despres d'una keyword donada i el simbol de separacio, treu l'ultim caracter si corrLastCharacter es 1 (aplicarà -1)
        if (FileUtils.containsIgnoreCase(line, keyword)) {
            int iigual = line.trim().indexOf(separator) + 1;
            line=line.replace(",",".");
            return Float.parseFloat(line.trim().substring(iigual, line.trim().length() - corrLastCharacter).trim());
        }
        return currentValue;
    }
    
    private static String getStringKeyword(String line, String keyword, String separator, int corrLastCharacter, String currentValue) {
        //retorna un enter despres d'una keyword donada i el simbol de separacio, treu l'ultim caracter si corrLastCharacter es 1 (aplicarà -1)
        if (FileUtils.containsIgnoreCase(line, keyword)) {
            int iigual = line.trim().indexOf(separator) + 1;
            return line.trim().substring(iigual, line.trim().length() - corrLastCharacter).trim();
        }
        return currentValue;
    }

    // OBERTURA DELS DIFERENTS FORMATS DE DADES2D
    public static Pattern2D readPatternFile(File d2File, boolean exzConfirm) {
        Pattern2D patt2D = null;
        // comprovem extensio
        log.debug(d2File.toString());
        String ext = FileUtils.getExtension(d2File).trim();
    
        // this line returns the FORMAT in the ENUM or NULL
        SupportedReadExtensions format = FileUtils.searchEnum(SupportedReadExtensions.class, ext);
        if (format != null) {
            log.debug("Format=" + format.toString());
        }
    
        if (format == null) {
            SupportedReadExtensions[] possibilities = SupportedReadExtensions.values();
            SupportedReadExtensions s = (SupportedReadExtensions) JOptionPane
                    .showInputDialog(null, "Input format:", "Read File",
                            JOptionPane.PLAIN_MESSAGE, null, possibilities,
                            possibilities[0]);
            if (s == null) {
                return null;
            }
            format = s;
        }
    
        switch (format) {
            case BIN:
                if (isNewBIN(d2File)) {
                    patt2D = ImgFileUtils.readBIN(d2File);
                } else {
                    patt2D = ImgFileUtils.readBINold(d2File);
                }
                break;
            case D2D:
                patt2D = ImgFileUtils.readD2D(d2File);
                break;
            case EDF:
                patt2D = ImgFileUtils.readEDF(d2File);
                break;
            case GFRM:
                patt2D = ImgFileUtils.readGFRM(d2File);
                break;
            case IMG:
                patt2D = ImgFileUtils.readIMG(d2File);
                break;
            case SPR:
                patt2D = ImgFileUtils.readSPR(d2File);
                break;
            case TIF:
                patt2D = ImgFileUtils.readTIFF(d2File);
                break;
            case CBF:
                patt2D = ImgFileUtils.readCBF(d2File);
                break;
            default:
                break;
    
        }
        patt2D.setFileFormat(format);
        
        if (patt2D != null) {
            // operacions generals despres d'obrir
            patt2D.calcMeanI();
            patt2D.setImgfile(d2File);
            ImgFileUtils.readEXZ(patt2D, null,exzConfirm);
            
            if (D2Dplot_global.isKeepCalibration()){
                patt2D.setCentrX(D2Dplot_global.getCentX());
                patt2D.setCentrY(D2Dplot_global.getCentY());
                patt2D.setDistMD(D2Dplot_global.getDistMD());
                patt2D.setTiltDeg(D2Dplot_global.getTilt());
                patt2D.setRotDeg(D2Dplot_global.getRot());
            }
            
            // debug:
            log.debug("meanI= " + patt2D.getMeanI());
            log.debug("sdevI= " + patt2D.getSdevI());
        }
        return patt2D;
    }

    public static File writePatternFile(File d2File, Pattern2D patt2D, boolean forceExt) {
        // comprovem extensio
        log.debug(d2File.toString());
        String ext = FileUtils.getExtension(d2File).trim();
    
        // this line returns the FORMAT in the ENUM or NULL
        SupportedWriteExtensions format = FileUtils.searchEnum(SupportedWriteExtensions.class, ext);
        if (format != null) {
            log.debug("Format=" + format.toString());
        }
    
        if (format == null) {
            SupportedWriteExtensions[] possibilities = SupportedWriteExtensions.values();
            SupportedWriteExtensions s = (SupportedWriteExtensions) JOptionPane
                    .showInputDialog(null, "Output format:", "Save File",
                            JOptionPane.PLAIN_MESSAGE, null, possibilities,
                            possibilities[0]);
            if (s == null) {
                return null;
            }
            format = s;
        }
    
        File fout = null;
    
        switch (format) {
            case BIN:
                fout = writeBIN(d2File, patt2D, forceExt);
                break;
            case EDF:
                fout = writeEDF(d2File, patt2D, forceExt);
                break;
            case D2D:
                fout = writeD2D(d2File, patt2D, forceExt);
                break;
            case IMG:
                fout = writeIMG(d2File, patt2D, forceExt);
                break;
            case TIF:
                fout = writeTIFF(d2File, patt2D, forceExt);
                break;
            default:
                log.warning("Unknown format to write");
                return null;
        }
        return fout;
    }

    private static Pattern2D readBIN(File d2File) {
        Pattern2D patt2D = null;
        try {
            long start = System.nanoTime(); // control temps
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff4 = new byte[4]; // real
            float scale = -1;
            float centrX, centrY, pixlx, pixly, sepod, wl, omeIni, omeFin, acqTime;
            int dimX, dimY;
            int totalHeaderBytes = 60;
    
            // !FITXER BIN (23/05/2013):
            // !- Capçalera fixa de 60 bytes. De moment hi ha 9 valors (la resta és "buida"):
            // ! Int*4 NXMX(cols)
            // ! Int*4 NYMX(rows)
            // ! Real*4 SCALE
            // ! Real*4 CENTX
            // ! Real*4 CENTY
            // ! Real*4 PIXLX
            // ! Real*4 PIXLY
            // ! Real*4 DISTOD
            // ! Real*4 WAVEL
            // ! Real*4 OME/PHI ini (degrees)
            // ! Real*4 OME/PHI final (degrees)
            // ! REAL*4 ACQTIME
    
            // !- Llista Int*2 (amb signe.. -32,768 to 32,767) amb ordre files-columnes, es a dir,
            // ! l'index rapid es el de les columnes (files,columnes): (1,1) (2,1) (3,1) ...
    
            in.read(buff4);
            ByteBuffer bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimX = bb.getInt();
    
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimY = bb.getInt();
    
            in.read(buff4);
            scale = FileUtils.B4toFloat(buff4);
    
            in.read(buff4);
            centrX = FileUtils.B4toFloat(buff4);
    
            in.read(buff4);
            centrY = FileUtils.B4toFloat(buff4);
    
            in.read(buff4);
            pixlx = FileUtils.B4toFloat(buff4) / 1000.f; // passem a mm
    
            in.read(buff4);
            pixly = FileUtils.B4toFloat(buff4) / 1000.f;
    
            in.read(buff4);
            sepod = FileUtils.B4toFloat(buff4);
    
            in.read(buff4);
            wl = FileUtils.B4toFloat(buff4);
    
            in.read(buff4);
            omeIni = FileUtils.B4toFloat(buff4);
    
            in.read(buff4);
            omeFin = FileUtils.B4toFloat(buff4);
    
            in.read(buff4);
            acqTime = FileUtils.B4toFloat(buff4);
    
            in.close();
    
            patt2D = new Pattern2D(dimX, dimY, centrX, centrY, 0, 999999999, scale);
            patt2D.setExpParam(pixlx, pixly, sepod, wl);
            patt2D.setScanParameters(omeIni, omeFin, acqTime);
    
            patt2D = readBINdata(d2File,patt2D,totalHeaderBytes);
            
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading BIN");
            return null;
        }
        return patt2D; // tot correcte
    }

    private static Pattern2D readBINold(File d2File) {
        Pattern2D patt2D = null;
        try {
            long start = System.nanoTime(); // control temps
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff4 = new byte[4]; // real
            float scale = -1;
            int dimX, dimY, centrX, centrY;
            int totalHeaderBytes = 20;
    
            // !FITXER BIN (30/01/2013):
            // !- 4 valors: Int*4 Int*4 Real*4 Int*4 Int*4 corresponents a: COLS(X) FILES(Y) ESCALA COLCENTRE(XC) FILACENTRE(YC)
            // !- Llista Int*2 (amb signe.. -32,768 to 32,767) amb ordre files-columnes, es a dir, l'index rapid es el de les columnes (columnes,files): (1,1) (2,1) (3,1) ...
            in.read(buff4);
            ByteBuffer bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimX = bb.getInt();
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimY = bb.getInt();
            in.read(buff4);
            scale = FileUtils.B4toFloat(buff4);
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            centrX = bb.getInt();
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            centrY = bb.getInt();
    
            in.close();
            
            patt2D = new Pattern2D(dimX, dimY, centrX, centrY, 0, 999999999,scale);
            patt2D = readBINdata(d2File,patt2D,totalHeaderBytes);
            
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading BIN");
            return null;
        }
        return patt2D; // tot correcte
    }

    private static boolean isNewBIN(File d2File) {
        // primer mirem la capçalera
        int dimX = 0, dimY = 0;
        byte[] buff4 = new byte[4]; // real
    
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
    
            in.read(buff4);
            ByteBuffer bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimX = bb.getInt();
    
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimY = bb.getInt();
    
            in.close();
        } catch (Exception e1) {
            if (D2Dplot_global.isDebug()) e1.printStackTrace();
            log.warning("Error in check newBIN");
        }
    
        // File size (bytes):
        // NX*NY*2 + 20 = old
        // NX*NY*2 + 60 = new
        int limit = dimX * dimY * 2 + 20;
        InputStream stream = null;
        int bytes = 0;
        try {
            URL url = d2File.toURI().toURL();
            stream = url.openStream();
            bytes = stream.available();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error in check newBIN");
        }
        if (bytes > limit) {
            log.debug(bytes + " bytes --> format NOU");
            return true;
        } else {
            log.debug(bytes + " bytes --> format ANTIC");
            return false;
        }
    
    }

    private static Pattern2D readBINdata(File d2File,Pattern2D patt2D, int headerSizeBytes) {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[2];
            int count = 0;
            in.read(new byte[headerSizeBytes]);
            
            //NO APLIQUEM CAP ESCALA JA QUE AL BIN LA SATURACIO ES 32000 i no seria correcte del tot
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    in.read(buff);
                    patt2D.setInten(i, j,(int) (FileUtils.ByteArrayToInt_signed(buff, true)));//estava malament, era B2toInt_LE_unsigned(buff))
                    count = count + 1;
                    // nomes considerem valors superiors a zero pel minI
                    if (patt2D.getInten(i, j) >= 0) {
                        if (patt2D.getInten(i, j) > patt2D.getMaxI()) patt2D.setMaxI(patt2D.getInten(i, j));
                        if (patt2D.getInten(i, j) < patt2D.getMinI()) patt2D.setMinI(patt2D.getInten(i, j));
                    }
                }
            }
            in.close();
            patt2D.setPixCount(count);
            patt2D.setSaturValue(D2Dplot_global.satur32); //aqui en principi el saturat era 32000...
            
        }catch(Exception ex) {
            if (D2Dplot_global.isDebug()) ex.printStackTrace();
            log.warning("Error reading BIN");
            return null;
        }
        
        return patt2D;
    }
    
    private static Pattern2D readGFRM(File d2File) {
        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps
        int headerSize = 0;
        float distOD = 0;
        int noverflow = 0;
        int nbytePerPixel = 1;
        int dimX, dimY, minI, maxI;
        float centrX, centrY;
        dimX = dimY = minI = maxI = 0;
        centrX = centrY = 0;

        try {
            // llegir "LINIA" amb tots els parametres...
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
            String line = scD2file.nextLine();
            log.debug(line);
            // treurem la informaci� d'aquesta linia.
            // 0 1 2 3 4 5 6 7 8
            String[] llista = { "HDRBLKS:", "NROWS  :", "NCOLS  :", "CENTER :",
                    "DISTANC:", "NOVERFL:", "MINIMUM:", "MAXIMUM:", "NPIXELB:" };
            for (int i = 0; i < llista.length; i++) {
                Scanner scLine = new Scanner(line);
                scLine.useDelimiter(llista[i]);
                if (scLine.hasNext()) {
                    // String temp = scLine.findInLine(llista[i]);
                    scLine.findInLine(llista[i]);
                    // ara hem de llegir els 71 caracters seg�ents:
                    StringBuffer item = new StringBuffer(72);
                    for (int j = 0; j < 71; j++) {
                        item.append(scLine.findInLine(".").charAt(0));
                    }
                    if (i == 0)
                        headerSize = Integer.parseInt(item.toString().trim());
                    if (i == 1)
                        dimY = Integer.parseInt(item.toString().trim());
                    if (i == 2)
                        dimX = Integer.parseInt(item.toString().trim());
                    if (i == 3) {
                        String[] s = item.toString().trim().split("\\s+");
                        centrX = Float.parseFloat(s[0]);
                        centrY = Float.parseFloat(s[1]);
                    }
                    if (i == 4)
                        distOD = Float.parseFloat(item.toString().trim());
                    if (i == 5)
                        noverflow = Integer.parseInt(item.toString().trim());
                    if (i == 6)
                        minI = Integer.parseInt(item.toString().trim());
                    if (i == 7)
                        maxI = Integer.parseInt(item.toString().trim());
                    if (i == 8)
                        nbytePerPixel = Integer
                                .parseInt(item.toString().trim());
                }
                scLine.close();
            }

            if (nbytePerPixel > 2) {
                JOptionPane.showMessageDialog(null,
                        "format not supported (more than 2byte per pixel)",
                        "GFRM file error", JOptionPane.ERROR_MESSAGE);
                scD2file.close();
                return null;
            }

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[nbytePerPixel];
            byte[] header = new byte[headerSize * 512];
            patt2D = new Pattern2D(dimX, dimY, centrX, centrY, maxI, minI, -1);
            int count = 0;
            in.read(header);

            // si la intensitat m�xima supera el short escalem -- aixo no caldria... no??
//            patt2D.setScale(FastMath.max(maxI / (float) D2Dplot_global.satur65,1.000f));
            patt2D.setScale(1.0f);

            // llegim els bytes
            
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    in.read(buff);
                    ByteBuffer buffer = ByteBuffer.wrap(buff);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    int valorLlegit = 0;
                    switch (nbytePerPixel) {
                        case 1:
                            // valorLlegit= buffer.get();
                            valorLlegit = FileUtils.B1toInt_unsigned(buff[0]);
                            break;
                        case 2:
                            valorLlegit = FileUtils.B2toInt_LE_unsigned(buff);
                            break;
                    }
                    
                    patt2D.setInten(i, j,valorLlegit);
                    count = count + 1;
                    
                }
            }

            // llegim els overflow
            /*
             * The overflow table is stored as ASCII values. Each overflow entry
             * is 16 characters, comprising a 9-character intensity and a
             * 7-character pixel # offset. The table is padded to a multiple of
             * 512 bytes. In an 8-bit frame, any pixel with a value of 255 in
             * the image needs to be looked up in the overflow table to
             * determine its true value (even if the true value is 255, to allow
             * overflow table validity checks, which could not otherwise be
             * made). In a similar manner, any pixel in a 16-bit frame with a
             * value of 65535 must be looked up in the overflow table to
             * determine its true value. To look up a pixel value, compute its
             * pixel displacement (for example, in a 512x512 frame, 512*j + k,
             * where j is the zero-based row number and k is the zero-based
             * column number), and compare the displacement with that of each
             * overflow table entry until a match is found. While the overflow
             * table is normally sorted on displacement, it is not guaranteed to
             * be sorted, so we recommend that you search the whole table until
             * you find a match.
             */
            patt2D.setSaturValue(D2Dplot_global.satur65);
            if (noverflow > 0) {
                for (int i = 0; i < noverflow; i++) {
                    byte[] buffchar = new byte[16];
                    in.read(buffchar);

                    StringBuffer sb = new StringBuffer(10);
                    for (int j = 0; j < 9; j++) {
                        int c = FileUtils.B1toInt_unsigned(buffchar[j]);
                        sb.append((char) c);
                    }
                    int intensity = Integer.parseInt(sb.toString().trim());

                    sb = new StringBuffer(8);
                    for (int j = 9; j < 16; j++) {
                        int c = FileUtils.B1toInt_unsigned(buffchar[j]);
                        sb.append((char) c);
                    }
                    int offset = Integer.parseInt(sb.toString().trim());

                    // ara calculem quin pixel es
                    int fila = offset / dimX; // LA Y
                    int col = (offset % dimX); // +1 //LA X

                    patt2D.setInten(col, fila, intensity);
                }
            }
            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
            scD2file.close();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading GFRM");
        }
        // parametres instrumentals
        patt2D.setDistMD(distOD);
        patt2D.setPixSx(0.105f); // per defecte gadds
        patt2D.setPixSy(0.105f);

        return patt2D; // correcte
    }

    
    private static Pattern2D readIMG(File d2File) {
        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps
        int headerSize = 0;
        float pixSize = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;

        int maxLinesHeader = 200;
        int maxLineLength =1000;
        boolean headerFinished =false;
        
        try {
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
            log.debug(scD2file.toString());
            int headerLine = 0;
            while(!headerFinished) {
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
            		headerLine = headerLine +1;
                    log.debug("img line="+line);
                    
                    if (line.trim().endsWith("}")) {
                    	headerFinished=true;
                    	continue;
                    }
                    if (line.trim().length()>maxLineLength) {
                    	headerFinished=true;
                    	continue;
                    }
                    if (headerLine>maxLinesHeader) {
                    	headerFinished=true;
                    	continue;
                    }
                    
                    headerSize = getIntKeyword(line,"HEADER_BYTES","=",1,headerSize);
                    dimX = getIntKeyword(line,"SIZE1","=",1,dimX);
                    dimY = getIntKeyword(line,"SIZE2","=",1,dimY);
                    beamCX = getFloatKeyword(line,"BEAM_CENTER_X","=",1,beamCX);
                    beamCY = getFloatKeyword(line,"BEAM_CENTER_Y","=",1,beamCY);
                    pixSize = getFloatKeyword(line,"PIXEL_SIZE","=",1,pixSize);
                    distOD = getFloatKeyword(line,"DISTANCE","=",1,distOD);
                    wl = getFloatKeyword(line,"WAVELENGTH","=",1,wl);
                }
            }

            // calculem el pixel central
            beamCX = beamCX / pixSize;
            beamCY = beamCY / pixSize;

            scD2file.close();

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[2];
            byte[] header = new byte[headerSize];
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI,-1.0f);
            int count = 0;
            in.read(header);

            //en principi si es byte2 el satur es 65000, no cal escalar res
            patt2D.setSaturValue(D2Dplot_global.satur65);
            patt2D.setScale(1.0f);
            
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    in.read(buff);
                    int valorLlegit = FileUtils.B2toInt_LE_unsigned(buff);
                    count = count + 1;
                    patt2D.setInten(i, j, FileUtils.B2toInt_LE_unsigned(buff));
                    if (valorLlegit >= 0) { // fem >= o > directament sense considerar els zeros??!
                        if (valorLlegit > patt2D.getMaxI()) patt2D.setMaxI(valorLlegit);
                        if (valorLlegit < patt2D.getMinI()) patt2D.setMinI(valorLlegit);
                    }
                }
            }
            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading IMG");
            return null;
        }

        // parametres instrumentals
        patt2D.setExpParam(pixSize, pixSize, distOD, wl);

        return patt2D; // correcte
    }
    
    public static class EdfHeaderPatt2D{
        int headerSize;
        boolean endiness;
        String dataType;
        Pattern2D patt2D;
        public EdfHeaderPatt2D(int header_size,boolean endianess, String data_type, Pattern2D patt2D) {
            this.headerSize=header_size;
            this.endiness=endianess;
            this.dataType=data_type;
            this.patt2D=patt2D;
        }
        public int getHeaderSize() {return headerSize;}
        public boolean getEndiness() {return endiness;}
        public String getDataType() {return dataType;}
        public Pattern2D getPatt2D() {return this.patt2D;}
    }
    
    
    //returns headersize
    public static EdfHeaderPatt2D readEDFheaderOnly(File d2File) {
        float pixSizeX = 0, pixSizeY = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;
        float omeIni = 0, omeFin = 0, acqTime = -1;
        float tilt = 0, rot = 0;
        boolean littleEndian = true;
        String dataType = "UnsignedShort";
        int binSize = 0;
        
        int maxLinesHeader = 200;
        int maxLineLength =1000;
        boolean headerFinished =false;
        
        // primer treiem la info de les linies de text
        try {
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
            log.debug(scD2file.toString());
            int headerLine = 0;
            while(!headerFinished) {
            	if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
            		headerLine = headerLine +1;
                    log.debug("edf line="+line);
                    
                    if (line.trim().endsWith("}")) {
                    	headerFinished=true;
                    	continue;
                    }
                    if (line.trim().length()>maxLineLength) {
                    	headerFinished=true;
                    	continue;
                    }
                    if (headerLine>maxLinesHeader) {
                    	headerFinished=true;
                    	continue;
                    }
                    
                    int iigual = line.indexOf("=") + 1;
                    
                    if (FileUtils.containsIgnoreCase(line, "ByteOrder =")) {
                        String endiness = line.substring(iigual, line.trim().length() - 1).trim();
                        if (!endiness.contains("LowByte")) {
                            littleEndian = false;
                        }
                    }
                    
                    if (FileUtils.containsIgnoreCase(line, "DataType =")) {
                        dataType = line.substring(iigual, line.trim().length() - 1).trim();
                    }
                    
                    binSize = getIntKeyword(line,"Size =","=",1,binSize);
                    dimX = getIntKeyword(line,"Dim_1","=",1,dimX);
                    dimY = getIntKeyword(line,"Dim_2","=",1,dimY);
                    beamCY = getFloatKeyword(line,"beam_center_y","=",1,beamCY);
                    beamCX = getFloatKeyword(line,"beam_center_x","=",1,beamCX);
                    pixSizeX = getFloatKeyword(line,"pixel_size_x","=",1,pixSizeX);
                    pixSizeX = getFloatKeyword(line,"pixelsize_x","=",1,pixSizeX);
                    pixSizeY = getFloatKeyword(line,"pixel_size_y","=",1,pixSizeY);
                    pixSizeY = getFloatKeyword(line,"pixelsize_y","=",1,pixSizeY);
                    distOD = getFloatKeyword(line,"ref_distance","=",1,distOD);
                    wl = getFloatKeyword(line,"ref_wave","=",1,wl);
                    tilt = getFloatKeyword(line,"ref_tilt","=",1,tilt);
                    rot = getFloatKeyword(line,"ref_rot","=",1,rot);
                    
                    try {
                        // scan_type = mar_scan ('hp_som', -5.0, 5.0, 2.0) ;
                        // scan_type = mar_ct (1.0,) ;
                        if (FileUtils.containsIgnoreCase(line, "scan_type")) {
                            String line2 = line.substring(iigual,
                                    line.trim().length() - 1).trim();
                            if (FileUtils.containsIgnoreCase(line2, "mar_scan")) {
                                String[] values = line2.split(",");
                                omeIni = Float.parseFloat(values[1]);
                                omeFin = Float.parseFloat(values[2]);
                                acqTime = Float.parseFloat(values[3].split("\\)")[0]);
                            }
                            if (FileUtils.containsIgnoreCase(line2, "mar_ct")) {
                                omeIni = 0;
                                omeFin = 0;
                                String[] values = line2.split(",");
                                acqTime = Float.parseFloat(values[0].split("\\(")[1]);
                            }
                        }
    
                    } catch (Exception ex) {
                        log.warning("Could not read the scan type from image header");
                    }
    
                }            	
            }
            
            scD2file.close();
            //fora el loop
            pixSizeX = pixSizeX / 1000.f;
            pixSizeY = pixSizeY / 1000.f;
    
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading EDF header");
            return null;
        }
        Pattern2D patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI,-1.0f);
        patt2D.setExpParam(pixSizeX, pixSizeY, distOD, wl,tilt,rot);
        patt2D.setScanParameters(omeIni, omeFin, acqTime);
        
        int headerSize = (int) (d2File.length() - binSize);
        log.config("EDF header size (bytes)=" + headerSize);
        log.writeNameNumPairs("CONFIG", true, "dimX,dimY,beamCX,beamCY,pixSizeX,distOD,wl", dimX, dimY, beamCX, beamCY, pixSizeX, distOD, wl);
        log.writeNameNumPairs("CONFIG", true, "binsize,d2fileLength", binSize, d2File.length());
        
        return new EdfHeaderPatt2D(headerSize,littleEndian,dataType,patt2D);
    }

    private static Pattern2D readEDF(File d2File) {
        long start = System.nanoTime(); // control temps
        EdfHeaderPatt2D edfinf = readEDFheaderOnly(d2File);
        Pattern2D patt2D = edfinf.getPatt2D();
        patt2D.setSaturValue(D2Dplot_global.satur65);
        
        // primer treiem la info de les linies de text
        try {
            if (edfinf.getHeaderSize()<0)throw new Exception();
            
            // ARA LLEGIREM ELS BYTES
            
            if(edfinf.getDataType().contains("SignedInt")) {
                log.debug("EDF SIGNED INT");
                DataInputStream in = new DataInputStream(new FileInputStream(d2File));
                byte[] buff = new byte[4];
                byte[] header = new byte[edfinf.getHeaderSize()];
//                patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI,-1.0f);
                int count = 0;
                in.read(header);

                patt2D.setScale(1);
                for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                    for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                        in.read(buff);
                        int valorLlegit = FileUtils.B4toInt_LE_signed(buff);
                        count = count + 1;
                        patt2D.setInten(i, j,valorLlegit);
                        if (valorLlegit >= 0) { // fem >= o > directament sense considerar els zeros??!
                            if (valorLlegit > patt2D.getMaxI()) patt2D.setMaxI(valorLlegit);
                            if (valorLlegit < patt2D.getMinI()) patt2D.setMinI(valorLlegit);
                        }
                    }
                }
                
                in.close();
                long end = System.nanoTime();
                patt2D.setMillis((float) ((end - start) / 1000000d));
                patt2D.setPixCount(count);
                
            }else { //by default UNSIGNED SHORT (MSPD)
                log.debug("EDF UNSIGNED SHORT");
                InputStream in = new BufferedInputStream(new FileInputStream(d2File));
                byte[] buff = new byte[2];
                byte[] header = new byte[edfinf.getHeaderSize()];
//                patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI,-1.0f);
                int count = 0;
                in.read(header);

                //passo de l'escala tambe
                patt2D.setScale(1);
                for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                    for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                        in.read(buff);
                        int valorLlegit = FileUtils.B2toInt_LE_unsigned(buff);
                        patt2D.setInten(i, j,valorLlegit);
                        count = count + 1;
                        // nomes considerem valors superiors a zero pel minI
                        if (valorLlegit >= 0) { // fem >= o > directament sense considerar els zeros??!
                            if (valorLlegit > patt2D.getMaxI()) patt2D.setMaxI(valorLlegit);
                            if (valorLlegit < patt2D.getMinI()) patt2D.setMinI(valorLlegit);
                        }
                    }
                }
                in.close();
                long end = System.nanoTime();
                patt2D.setMillis((float) ((end - start) / 1000000d));
                patt2D.setPixCount(count);
            }
            

        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading EDF");
            return null;
        }

        return patt2D; // correcte
    }
    
    private static EdfHeaderPatt2D readD2Dheader(File d2File) {
        Pattern2D patt2D = null;
        int headerSize = 0;
        int binSize = 0;
        float pixSizeX = 0, pixSizeY = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;
        float tilt = 0, rot = 0;
        int margin = 0, thresh = 0, dcircle = 0;
        ArrayList<PolyExZone> polyExzones = new ArrayList<PolyExZone>();
        ArrayList<ArcExZone> arcExzones = new ArrayList<ArcExZone>();
        ArrayList<Point> exzPoints = new ArrayList<Point>();
        float omeIni = 0, omeFin = 0, acqTime = -1;
        
        int maxLinesHeader = 200;
        int maxLineLength =1000;
        boolean headerFinished =false;
        
        // primer treiem la info de les linies de text
        try {
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
            log.debug(scD2file.toString());
            int headerLine = 0;
            while(!headerFinished) {
            	
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
            		headerLine = headerLine +1;
                    log.debug("d2d line="+line);
                    
                    if (line.trim().endsWith("}")) {
                    	headerFinished=true;
                    	continue;
                    }
                    if (line.trim().length()>maxLineLength) {
                    	headerFinished=true;
                    	continue;
                    }
                    if (headerLine>maxLinesHeader) {
                    	headerFinished=true;
                    	continue;
                    }
                    
                    
                    if (line.trim().endsWith(";")) {
                        line = line.substring(0, line.trim().length() - 1);
                    }
                    int iigual = line.indexOf("=") + 1;
                    
                    binSize = getIntKeyword(line,"Size =","=",0,binSize); //"size =" for legacy
                    binSize = getIntKeyword(line,"DataSize","=",0,binSize);
                    dimX = getIntKeyword(line,"Dim_1","=",0,dimX);
                    dimY = getIntKeyword(line,"Dim_2","=",0,dimY);
                    beamCY = getFloatKeyword(line,"Beam_center_y","=",0,beamCY);
                    beamCX = getFloatKeyword(line,"Beam_center_x","=",0,beamCX);
                    pixSizeX = getFloatKeyword(line,"Pixelsize_x","=",0,pixSizeX);
                    pixSizeY = getFloatKeyword(line,"Pixelsize_y","=",0,pixSizeY);
                    distOD = getFloatKeyword(line,"Ref_distance","=",0,distOD);
                    wl = getFloatKeyword(line,"Ref_wave","=",0,wl);
                    tilt = getFloatKeyword(line,"Det_tiltDeg","=",0,tilt);
                    rot = getFloatKeyword(line,"Det_rotDeg","=",0,rot);
                    margin = getIntKeyword(line,"EXZmarg","=",0,margin); 
                    thresh = getIntKeyword(line,"EXZthres","=",0,thresh); 
                    dcircle = getIntKeyword(line,"EXZdetRadius","=",0,dcircle); 
                    omeIni = getFloatKeyword(line,"omegaIni","=",0,omeIni);
                    omeFin = getFloatKeyword(line,"omegaFin","=",0,omeFin);
                    acqTime = getFloatKeyword(line,"acqTime","=",0,acqTime);
                    
                    if (FileUtils.containsIgnoreCase(line, "EXZpol")) {
                        String linia = line.substring(iigual,
                                line.trim().length()).trim();
                        String[] values = linia.split("\\s+");
                        PolyExZone z = new PolyExZone(false);
                        for (int j = 0; j < values.length; j = j + 2) {
                            // parelles x1 y1 x2 y2 .... que son els vertexs
                            z.addPoint(Integer.parseInt(values[j]),
                                    Integer.parseInt(values[j + 1]));
                        }
                        polyExzones.add(z);
                    }
                    
                    if (FileUtils.containsIgnoreCase(line, "EXZarc")) {
                        String linia = line.substring(iigual,line.trim().length()).trim();
                        String[] values = linia.split("\\s+");
                        int ipx = Integer.parseInt(values[0]);
                        int ipy = Integer.parseInt(values[1]);
                        int hradwpx = Integer.parseInt(values[2]);
                        int hazimwdeg = Integer.parseInt(values[3]);
                        arcExzones.add(new ArcExZone(ipx,ipy,hradwpx,hazimwdeg,null)); //need to recalc later
                    }
                    
                    if (FileUtils.containsIgnoreCase(line, "EXZpix")) {
                        String linia = line.substring(iigual, line.trim().length()).trim();
                        String[] values = linia.trim().split("\\s+");
                        log.debug(linia);
                        log.debug(Arrays.toString(values));
                        log.debug(Integer.toString(values.length));
                        if (values.length>1) {
                            for(int j=0;j<values.length;j=j+2) {
                                try {
                                    exzPoints.add(new Point(Integer.parseInt(values[j]),Integer.parseInt(values[j+1])));    
                                }catch(Exception e) {
                                    log.debug("error reading pixel");
                                }
                            }    
                        }
                        continue;
                    }
                    


                }
            }

            pixSizeX = pixSizeX / 1000.f;
            pixSizeY = pixSizeY / 1000.f;
            
            headerSize = (int) (d2File.length() - binSize);
            log.config("D2D header size (bytes)=" + headerSize);
            log.writeNameNumPairs("CONFIG", true, "dimX,dimY,beamCX,beamCY,pixSizeX,distOD,wl,tilt,rot", dimX, dimY, beamCX, beamCY, pixSizeX, distOD, wl, tilt, rot);
            
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI,-1.0f);
            // info especifica d2d format
            patt2D.setExpParam(pixSizeX, pixSizeY, distOD, wl,tilt,rot);
            patt2D.setScanParameters(omeIni, omeFin, acqTime);
            patt2D.setPolyExZones(polyExzones);
            patt2D.setArcExZones(arcExzones); //this is doing automatically patt2D.recalcArcExZones();
            patt2D.setExZpaintPixels(exzPoints);
            patt2D.setExz_margin(margin);
            patt2D.setExz_threshold(thresh);
            patt2D.setExz_detcircle(dcircle);
            patt2D.recalcExcludedPixels();
            scD2file.close();
        
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading D2D");
            return null;
        }

        return new EdfHeaderPatt2D(headerSize,false,"UnsignedShort",patt2D);

    }
    
    
    private static Pattern2D readD2D(File d2File) {
//        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps

        EdfHeaderPatt2D edfinf = readD2Dheader(d2File);
        Pattern2D patt2D = edfinf.getPatt2D();
        patt2D.setSaturValue(D2Dplot_global.satur65);
        
        // primer treiem la info de les linies de text
        try {
            if (edfinf.getHeaderSize()<0)throw new Exception();

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[2];
            byte[] header = new byte[edfinf.getHeaderSize()];

            int count = 0;
            in.read(header);

            // passem de les escales
            patt2D.setScale(1);
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    in.read(buff);
                    int valorLlegit = FileUtils.B2toInt_LE_unsigned(buff);
                    patt2D.setInten(i, j,valorLlegit);
                    count = count + 1;
                    // nomes considerem valors superiors a zero pel minI
                    if (valorLlegit >= 0) { // fem >= o > directament sense considerar els zeros??!
                        if (valorLlegit > patt2D.getMaxI()) patt2D.setMaxI(valorLlegit);
                        if (valorLlegit < patt2D.getMinI()) patt2D.setMinI(valorLlegit);
                    }
                }
            }
            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading D2D");
            return null;
        }
        return patt2D; // correcte
    }

    
    private static Pattern2D readTIFF(File d2File) {
        Opener op = new Opener();
//        ImagePlus imp = op.openImage("/home/ovallcorba/ovallcorba/eclipse_ws/TESTS/ipp6.TIF");
//        ImageProcessor sp = (imp.getProcessor()).convertToShort(false);
        ImagePlus imp = op.openImage(d2File.getAbsolutePath());
        ImageProcessor sp = imp.getProcessor();

        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps
        float pixSizeX = 0, pixSizeY = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;
        
        dimX = imp.getWidth();
        dimY = imp.getHeight();
        Calibration cal = imp.getCalibration();
        String xunit = cal.getXUnit();
        float factor = 1.0f;
        if (FileUtils.containsIgnoreCase(xunit, "inch")){
            factor = 25.4f;
        }
        pixSizeX = (float) (cal.pixelWidth*factor);
        pixSizeY = (float) (cal.pixelHeight*factor);
        
        log.debug(String.format("pxw=%f, pxh=%f, pxd=%f",cal.pixelWidth,cal.pixelHeight,cal.pixelDepth));
        
        patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI,-1.0f);
        patt2D.setExpParam(pixSizeX, pixSizeY, distOD, wl);
        patt2D.setSaturValue(D2Dplot_global.saturTiff_or_Pilatus);
        
        log.debug(String.format("width=%d height=%d",imp.getWidth(),imp.getHeight()));
        
        int count = 0;
        for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                int inten = sp.getPixel(i,j); //TODO Comprovar si es i,j o j,i
                patt2D.setInten(i, j,inten);
                count++;
                if (inten>patt2D.getMaxI())patt2D.setMaxI(inten);
                if (inten<patt2D.getMinI())patt2D.setMinI(inten);
            }
        }
        
        long end = System.nanoTime();
        patt2D.setMillis((float) ((end - start) / 1000000d));
        patt2D.setPixCount(count);
        return patt2D;
    }
    
    
    private static Pattern2D readCBFXalocHeader(File d2File, Pattern2D patt2D) {
        float pixSizeX = 0, pixSizeY = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        float omeIni = 0, omeFin = 0, acqTime = -1;
    	// primer treiem la info de les linies de text
        try {
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
            for (int i = 0; i < 80; i++) {
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
                    if (FileUtils.containsIgnoreCase(line,"CIF-BINARY")) { //"--CIF-BINARY-FORMAT-SECTION--"
                        break;
                    }
                    log.debug(line);

                    if (FileUtils.containsIgnoreCase(line, "Beam_xy")) {
                        //# Beam_xy (1237.95, 1302.57) pixels
                        try{
                            String s1 = line.trim().split(Pattern.quote("("))[1]; //Beam_xy (1237.95, 1302.57) pixels
                            String s2 = s1.trim().split(Pattern.quote(")"))[0];
                            String[] vals = s2.trim().split(",");
                            beamCX = Float.parseFloat(vals[0]);
                            beamCY = Float.parseFloat(vals[1]);
                        }catch(Exception ex){
                            log.warning("Error reading beam centre from cbf");
                            ex.printStackTrace();
                        }
                    }
                    if (FileUtils.containsIgnoreCase(line, "Pixel_size")) {
                        String[] vals = line.trim().split("\\s+");
                        try{
                            pixSizeX=Float.parseFloat(vals[2])*1000; //m to mm
                            pixSizeY=Float.parseFloat(vals[2])*1000;
                        }catch(Exception ex){
                            log.warning("Error reading pixel size from cbf");
                        }
                    }
                    
                    if (FileUtils.containsIgnoreCase(line, "Detector_distance")) {
                        String[] vals = line.trim().split("\\s+");
                        try{
                            distOD=Float.parseFloat(vals[2])*1000; //m to mm
                        }catch(Exception ex){
                            log.warning("Error reading distance from cbf");
                        }
                    }

                    if (FileUtils.containsIgnoreCase(line, "Wavelength")) {
                        String[] vals = line.trim().split("\\s+");
                        try{
                            wl=Float.parseFloat(vals[2]);
                        }catch(Exception ex){
                            log.warning("Error reading wavelength from cbf");
                        }
                    }

                    if (FileUtils.containsIgnoreCase(line, "Start_angle")) {
                        String[] vals = line.trim().split("\\s+");
                        try{
                            omeIni=Float.parseFloat(vals[2]);
                        }catch(Exception ex){
                            log.warning("Error reading start angle from cbf");
                        }
                    }
                    if (FileUtils.containsIgnoreCase(line, "Angle_increment")) {
                        String[] vals = line.trim().split("\\s+");
                        try{
                            float incr = Float.parseFloat(vals[2]);
                            omeFin = omeIni+incr;
                        }catch(Exception ex){
                            log.warning("Error reading angle increment from cbf");
                        }
                        
                    }

                    if (FileUtils.containsIgnoreCase(line, "Exposure_time")) {
                        String[] vals = line.trim().split("\\s+");
                        try{
                            acqTime = Float.parseFloat(vals[2]); 
                        }catch(Exception ex){
                            log.warning("Error reading Exposure_time from cbf");
                        }
                    }
                }
            }
            scD2file.close();
        }catch(Exception ex){
            if (D2Dplot_global.isDebug()) ex.printStackTrace();
            log.warning("Error reading CBF Custom header");
        }
        
        patt2D.setCentrX(beamCX);
        patt2D.setCentrY(beamCY);
        patt2D.setExpParam(pixSizeX, pixSizeY, distOD, wl);
        patt2D.setScanParameters(omeIni, omeFin, acqTime);
        patt2D.setScale(1.0f);
        return patt2D;
    }
    
    
    /*
     * --CIF-BINARY-FORMAT-SECTION--
Content-Type: application/octet-stream;
     conversions="x-CBF_BYTE_OFFSET"
Content-Transfer-Encoding: BINARY
X-Binary-Size: 6226461
X-Binary-ID: 0
X-Binary-Element-Type: "signed 32-bit integer"
X-Binary-Element-Byte-Order: LITTLE_ENDIAN
Content-MD5: ME8K/+3pvmbUDgVjSTdy7A==
X-Binary-Number-of-Elements: 6224001
X-Binary-Size-Fastest-Dimension: 2463
X-Binary-Size-Second-Dimension: 2527
X-Binary-Size-Padding: 128


 The "byte_offset" decompression algorithm is the following:

Start with a base pixel value of 0.

Read the next byte as delta

If -127 ≤ delta ≤ 127, add delta to the base pixel value, make that the new base pixel value, place it on the output array and return to step 2.

If delta is 80 hex, read the next two bytes as a little_endian 16-bit number and make that delta.

If -32767 ≤ delta ≤ 32767, add delta to the base pixel value, make that the new base pixel value, place it on the output array and return to step 2.

If delta is 8000 hex, read the next 4 bytes as a little_endian 32-bit number and make that delta

If -2147483647 ≤ delta ≤ 2147483647, add delta to the base pixel value, make that the new base pixel value, place it on the output array and return to step 2.

If delta is 80000000 hex, read the next 8 bytes as a little_endian 64-bit number and make that delta, add delta to the base pixel value, make that the new base pixel value, place it on the output array and return to step 2. 
     */
    
    private static Pattern2D readCBF(File d2File) {
        
        long start = System.nanoTime(); // control temps
        
        String conversions="";//should be x-CBF_BYTE_OFFSET
        String content_transfer_encoding="";//should be BINARY
        int binary_size=-1;
        String binary_Element_Type="";//should be "signed 32-bit integer"
        String binary_element_byte_order="";//should be little endian
        int binary_nr_elements=-1;
        int binary_size_fastest_dim=-1;
        int binary_size_second_dim=-1;
        int binary_size_padding=-1;
        
        
        try {
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
            for (int i = 0; i < 80; i++) {
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
//                    if (FileUtils.containsIgnoreCase(line,"--CIF-BINARY-FORMAT-SECTION--")) { //"--CIF-BINARY-FORMAT-SECTION--"
//                        break;
//                    }
                    log.debug(line);
//String line, String keyword, String separator, int corrLastCharacter, String currentValue
                    conversions = getStringKeyword(line,"conversion","=",0,conversions);
                    content_transfer_encoding = getStringKeyword(line,"Content-Transfer-Encoding",":",0,content_transfer_encoding);
                    binary_Element_Type = getStringKeyword(line,"Binary-Element-Type",":",0,binary_Element_Type);
                    binary_element_byte_order = getStringKeyword(line,"Binary-Element-Byte-Order",":",0,binary_element_byte_order);
                    binary_size=getIntKeyword(line,"Binary-Size:",":",0,binary_size);
                    binary_nr_elements=getIntKeyword(line,"Binary-Number-of-Elements",":",0,binary_nr_elements);
                    binary_size_fastest_dim=getIntKeyword(line,"Binary-Size-Fastest-Dimension",":",0,binary_size_fastest_dim);
                    binary_size_second_dim=getIntKeyword(line,"Binary-Size-Second-Dimension",":",0,binary_size_second_dim);
                    binary_size_padding=getIntKeyword(line,"-Binary-Size-Padding",":",0,binary_size_padding);
                }
            }
            scD2file.close();
        }catch(Exception ex){
            if (D2Dplot_global.isDebug()) ex.printStackTrace();
            log.warning("Error reading CBF MIME header");
        }
        
        //now we check if format is compatible
        if(!conversions.contains("CBF_BYTE_OFFSET")) {
        	log.warning("CBF file not supported, only CBF_BYTE_OFFSET compression is supported");
        	return null;
        }
        if(!content_transfer_encoding.contains("BINARY")) {
        	log.warning("CBF file not supported, only BINARY Content-Transfer-Encoding is supported");
        	return null;
        }
        if(!binary_Element_Type.contains("signed 32-bit integer")) {
        	log.warning("CBF file not supported, only \"signed 32-bit integer\" X-Binary-Element-Type is supported");
        	return null;
        }
        if(!binary_element_byte_order.contains("LITTLE_ENDIAN")) {
        	log.warning("CBF file not supported, only LITTLE_ENDIAN X-Binary-Element-Byte-Order is supported");
        	return null;
        }
        if(binary_size<=0) {
        	log.warning("Error reading CBF, X-Binary-Size missing");
        	return null;
        }
        if(binary_nr_elements<=0) {
        	log.warning("Error reading CBF, X-Binary-Number-of-Elements");
        	return null;
        }
        if(binary_size_fastest_dim<=0) {
        	log.info("Error reading CBF, X-Binary-Size-Fastest-Dimension");
        	return null;
        }
        if(binary_size_second_dim<=0) {
        	log.warning("Error reading CBF, X-Binary-Size-Second-Dimension");
        	return null;
        }
        if(binary_size_padding<=0) {
        	log.warning("Error reading CBF, X-Binary-Size-Padding");
        	return null;
        }
        
        //now we create the pattern
        Pattern2D patt2D = new Pattern2D(binary_size_fastest_dim, binary_size_second_dim);
        patt2D.setSaturValue(D2Dplot_global.saturTiff_or_Pilatus);
        
        //try to read the binary part
        /*hem de buscar la sequencia aquesta:
         * Octet 	Hex 	Decimal 	Purpose
   		 *	1 	  0C 	  12 	  (ctrl-L) End of Page
   		 *	2 	  1A 	  26 	  (ctrl-Z) Stop listings in MS-DOS
   		 *	3 	  04 	  04 	  (Ctrl-D) Stop listings in UNIX
   		 *	4 	  D5 	  213 	  Binary section begins
   		 * 	5..5+n-1	   	    	  Binary data (n octets) 
         */
        
        byte[] buff1 = new byte[1];
        boolean startBinary = false;
    	int count = 0;
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(d2File));

//        	while(!startBinary) {
//        		in.read(buff1);
//        		if (FileUtils.bytesToHex(buff1) == "0C") {
//        			log.info("0C");
//        			in.read(buff1);
//        			if (FileUtils.bytesToHex(buff1) == "1A") {
//        				log.info("1A");
//        				in.read(buff1);
//        				if (FileUtils.bytesToHex(buff1) == "04") {
//        					log.info("04");
//        					in.read(buff1);
//        					if (FileUtils.bytesToHex(buff1) == "D5") {
//        						log.info("D5");
//        						log.info("start Binary");
//        						startBinary=true;
//        					}	
//        				}        			
//        			}
//        		}
//        	}
            
        	while(!startBinary) {
        		in.read(buff1);
        		int val = buff1[0];
        		if (val == 12) {
        			log.debug("0C");
            		in.read(buff1);
            		val = buff1[0];
        			if (val == 26) {
        				log.debug("1A");
                		in.read(buff1);
                		val = buff1[0];
        				if (val==04) {
        					log.debug("04");
        					in.read(buff1);
                    		val = buff1[0];
        					if (val==-43) {
        						log.debug("D5");
        						log.debug("start Binary");
        						startBinary=true;
        					}	
        				}        			
        			}
        		}
        	}
            

        	//ja estem al principi del binari
        	int basePixel = 0;
        	byte[] buff2 = new byte[2];
        	byte[] buff4 = new byte[4];
        	byte[] buff8 = new byte[8];

        	for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
        		for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)

        			int intensity=0;

        			in.read(buff1);
        			int delta = buff1[0]; //aquest es directe ja que nomes és un byte
        			//pot ser delta o pot ser -128 indicant que llegim els 2 seguents
        			if (delta==-128) {
        				//llegim els dos seguents
        				in.read(buff2);
        				delta = FileUtils.ByteArrayToInt_signed(buff2, true);
        				if (delta==-32768) {
        					//llegim els 4 seguents
        					in.read(buff4);
        					delta = FileUtils.ByteArrayToInt_signed(buff4, true);
        					if (delta==-2147483648) {
        						//llegim els 8 seguents
        						in.read(buff8);
        						delta = FileUtils.ByteArrayToInt_signed(buff8, true);
        					}
        				}
        			}
        			//apliquem delta, actualitzem basepixel i poblem pixel
        			intensity = basePixel + delta;
        			basePixel=intensity;
        			patt2D.setInten(i, j, intensity);
        			count = count +1;
        		}
        	}
        	in.close();
        }catch(Exception ex) {
        	if(D2Dplot_global.isDebug())ex.printStackTrace();
        }

        /* termination sequence:
         * 
         * --CIF-BINARY-FORMAT-SECTION----
         * ;
         * 
         * (no es fa servir...de moment)
         */

        //llegim header custom xaloc
        patt2D = readCBFXalocHeader(d2File,patt2D);
        
        log.writeNameNumPairs("CONFIG", true,"dimX,dimY,beamCX,beamCY,pixSizeX,distOD,wl", patt2D.getDimX(), patt2D.getDimY(), patt2D.getCentrX(), patt2D.getCentrY(), patt2D.getPixSx(), patt2D.getDistMD(),patt2D.getWavel());
        log.writeNameNumPairs("CONFIG", true, "maxI,minI", patt2D.getMaxI(),patt2D.getMinI());
        
        long end = System.nanoTime();
        patt2D.setMillis((float) ((end - start) / 1000000d));
        patt2D.setPixCount(count);
        patt2D.recalcMaxMinI();
        return patt2D;
    }
    
    
    private static Pattern2D readSPR(File d2File) {
        Pattern2D patt2D = null;
        int dimX = 0, dimY = 0;
        try {
            long start = System.nanoTime();
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
            // 1a linia NCOL NFILES ... 
            dimX = Integer.parseInt(scD2file.next().trim()); // num columnes
            dimY = Integer.parseInt(scD2file.next().trim()); // num files
            scD2file.nextLine();
            int count = 0;
    
            patt2D = new Pattern2D(dimX, dimY, -1.0f, -1.0f, 0, 999999999,-1.0f);
    
            // el short arriba a 32,767... per tant maxI/scale=32767
            // si s'ha especificat al fitxer un factor d'escala l'utilitzem, ja
            // que vol dir que son I2(signed)
            // sino el calcularem i l'aplicarem, ja que podria ser que fosin
            // unsigned i superesin el limit
            // Haurem de fer dues passades, una per determinar el maxI, minI i
            // factor d'escala i l'altre per
            // llegir totes les intensitats i aplicar el factor d'escala per
            // encabir-ho a short.
            
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    String r = scD2file.next();
                    count = count + 1;
                    int in = new java.math.BigDecimal(r).intValue();
                    patt2D.setInten(i, j, in );
                    if (in > patt2D.getMaxI()) {patt2D.setMaxI(in);}
                    if (in < patt2D.getMinI()) {patt2D.setMinI(in);}
    
                }
            }
            
            scD2file.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading SPR");
            return null;
        }
        return patt2D; // tot correcte
    }

    

    

//    public static File writeTIF(File d2File, BufferedImage img) {
//        d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".tif"));    
//         try {
//            boolean done = ImageIO.write(img, "TIFF", d2File);
//            log.debug(String.valueOf(done));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        log.debug("tiff saved "+d2File);
//        return d2File;
//    }
    
    private static File writeBIN(File d2File, Pattern2D patt2D, boolean forceExt) {
        // Forcem extensio bin        //CAS MASK.BIN extensio majuscula
        if (FileUtils.getFNameNoExt(d2File.getName()).equalsIgnoreCase("MASK")){
            d2File = FileUtils.canviExtensio(d2File, "BIN");
            d2File = FileUtils.canviNomFitxer(d2File, "MASK");
        }else{
            if (forceExt)d2File = FileUtils.canviExtensio(d2File, "bin");
        }

        int dataHeaderBytes = 48; // bytes de dades en el header
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(d2File));

            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(patt2D.getDimX());
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(patt2D.getDimY());
            output.write(bb.array());
            bb.clear();

            // AQUI HEM DE COMPROVAR L'ESCALA EN CAS QUE HAGUEM DE PASSAR DE INT A SHORT
            float scI2=1.0f;
            scI2 = patt2D.calcScale(D2Dplot_global.satur32);
            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(scI2);
            output.write(bb.array());
            bb.clear();
            
            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getCentrX());
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getCentrY());
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getPixSx() * 1000.f); // passem a micres
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getPixSy() * 1000.f);
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getDistMD());
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getWavel());
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getOmeIni());
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getOmeFin());
            output.write(bb.array());
            bb.clear();

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getAcqTime());
            output.write(bb.array());
            bb.clear();

            output.write(new byte[60 - dataHeaderBytes]);

            log.debug("nr arc zones="+patt2D.getArcExZones().size());
            log.debug("nr pol zones="+patt2D.getPolyExZones().size());
            log.debug("det circle="+patt2D.getExz_detcircle());
            
            // creem imatge normal
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    if (patt2D.isExcluded(i, j)) {
                        bb.putShort((short) -1);
                    }else {
                        bb.putShort((short) (patt2D.getInten(i, j)/scI2)); //apliquem escala per encabir a short
                    }
                    output.write(bb.array());
                    bb.clear();
                }
            }
            output.close();
        } catch (Exception ex) {
            if (D2Dplot_global.isDebug()) ex.printStackTrace();
            log.warning("Error saving BIN file");
            return null;
        }
        return d2File;
    }

    // DATA UNSIGNED SHORT
    private static File writeEDF(File d2File, Pattern2D patt2D,boolean forceExt) {
        // Forcem extnsio edf
        if(forceExt)d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".edf"));

        int binSize = patt2D.getDimX() * patt2D.getDimY() * 2; // considerant 2 bytes per pixel

        try {

            // ESCRIBIM PRIMER LA PART ASCII
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(d2File)));
            // ESCRIBIM AL FITXER:
            output.println("{");
            output.println("HeaderID = EH:000001:000000:000000 ;");
            output.println("ByteOrder = LowByteFirst ;");
            output.println("DataType = UnsignedShort ;");
            output.println("Size = " + binSize + " ;");
            output.println("Image = 0 ;");
            output.println("acq_frame_nb = 0 ;");
            output.println("Dim_1 = " + patt2D.getDimX() + " ;");
            output.println("Dim_2 = " + patt2D.getDimY() + " ;");
            output.println("beam_center_x = " + FileUtils.dfX_2.format(patt2D.getCentrX()) + " ;");
            output.println("beam_center_y = " + FileUtils.dfX_2.format(patt2D.getCentrY()) + " ;");
            output.println("pixelsize_x = " + FileUtils.dfX_2.format(patt2D.getPixSx() * 1000) + " ;");
            output.println("pixelsize_y = " + FileUtils.dfX_2.format(patt2D.getPixSy() * 1000) + " ;");
            output.println("ref_distance = " + FileUtils.dfX_2.format(patt2D.getDistMD()) + " ;");
            output.println("ref_wave = " + FileUtils.dfX_4.format(patt2D.getWavel()) + " ;");
            output.println("ref_tilt = " + FileUtils.dfX_2.format(patt2D.getTiltDeg()) + " ;");
            output.println("ref_rot = " + FileUtils.dfX_2.format(patt2D.getRotDeg()) + " ;");
            // escribim l'scan
            // scan_type = mar_scan ('hp_som', -5.0, 5.0, 2.0) ;
            // scan_type = mar_ct (1.0,) ;
            float omeIni = patt2D.getOmeIni();
            float omeFin = patt2D.getOmeFin();
            float acqTime = patt2D.getAcqTime();
            
            if ((FastMath.round(omeIni*100) == 0) && (FastMath.round(omeFin*100) == 0)) {
                // marct
                output.println("scan_type = mar_ct (" + FileUtils.dfX_1.format(acqTime) + ",) ;");
            }else {
                // marScan
                output.println("scan_type = mar_scan ('hp_som', "
                        + FileUtils.dfX_1.format(omeIni) + ", "
                        + FileUtils.dfX_1.format(omeFin) + ", "
                        + FileUtils.dfX_1.format(acqTime) + ") ;");
            }
            output.println("}");
            output.close();

            // ara mirem quants bytes hem escrit...
            int headerbytes = (int) d2File.length();
            log.config("Write EDF headerbytes=" + headerbytes);

            //mirem escala que hi càpiga en 65000
            float sc65 = patt2D.calcScale(D2Dplot_global.satur65);
            
            OutputStream os = new BufferedOutputStream(new FileOutputStream(d2File, true));
            // byte[] buff = new byte[2];

            // escribim imatge tal cual escalant a 65000
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    if (patt2D.getInten(i, j) > 0) {
                        bb.putChar((char) ((int)((float)patt2D.getInten(i, j)/sc65)));
                    } else {
                        bb.putChar((char) 0);
                    }
                    os.write(bb.array());
                    bb.clear();

                }
            }
            os.close();

        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error saving EDF file");
            return null;
        }
        return d2File;

    }

    // DATA UNSIGNED SHORT
    private static File writeIMG(File d2File, Pattern2D patt2D,boolean forceExt) {
        // Forcem extnsio img
        if(forceExt)d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".img"));
        int headerBytes = 512;
        try {
            // ESCRIBIM PRIMER LA PART ASCII
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(d2File)));
            // ESCRIBIM AL FITXER:
            output.println("{");
            output.println("HEADER_BYTES= " + headerBytes + ";");
            output.println("TYPE=unsigned_short ;");
            output.println("BYTE_ORDER=little_endian;");
            output.println("SIZE1=" + patt2D.getDimX() + ";");
            output.println("SIZE2=" + patt2D.getDimY() + ";");
            output.println("DISTANCE= " + FileUtils.dfX_3.format(patt2D.getDistMD()) + " ;");
            output.println("PIXEL_SIZE= " + FileUtils.dfX_6.format(patt2D.getPixSx()) + " ;");
            output.println("WAVELENGTH=" + FileUtils.dfX_6.format(patt2D.getWavel()) + ";");
            output.println("BEAM_CENTER_X=" + FileUtils.dfX_2.format(patt2D.getCentrX() * patt2D.getPixSx()) + ";");
            output.println("BEAM_CENTER_Y=" + FileUtils.dfX_2.format(patt2D.getCentrY() * patt2D.getPixSy()) + ";");
            output.println("}");
            output.close();

            // ara mirem quants bytes hem escrit...
            int writtenHeaderBytes = (int) d2File.length();
            log.config("Write IMG headerbytes=" + writtenHeaderBytes);

            //mirem escala que hi càpiga en 65000
            float sc65 = patt2D.calcScale(D2Dplot_global.satur65);
            
            OutputStream os = new BufferedOutputStream(new FileOutputStream(d2File, true));

            // cal escriure fins a 512
            os.write(new byte[headerBytes - writtenHeaderBytes]);
            
            // ara ja escribim imatge
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    if (patt2D.getInten(i, j) > 0) {
                        bb.putChar((char) ((int)((float)patt2D.getInten(i, j)/sc65)));
                    } else {
                        bb.putChar((char) 0);
                    }
                    os.write(bb.array());
                    bb.clear();
                }
            }
            os.close();

        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error saving IMG file");
            return null;
        }
        return d2File;
    }

    // DATA UNSIGNED SHORT AMB CAPÇALERA QUE CONTE EXZ, FORMAT PROPI DEL PROGRAMA
    private static File writeD2D(File d2File, Pattern2D patt2D,boolean forceExt) {
        // Forcem extnsio d2d
        if(forceExt)d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".d2d"));

        int binSize = patt2D.getDimX() * patt2D.getDimY() * 2; // considerant 2 bytes per pixel

        try {

            // ESCRIBIM PRIMER LA PART ASCII
            PrintWriter output = new PrintWriter(new BufferedWriter( new FileWriter(d2File)));
            // ESCRIBIM AL FITXER:
            output.println("{");
            output.println("ByteOrder = LowByteFirst");
            output.println("DataType = UnsignedShort");
            output.println("DataSize = " + binSize);
            output.println("Dim_1 = " + patt2D.getDimX());
            output.println("Dim_2 = " + patt2D.getDimY());
            output.println("Beam_center_x = " + FileUtils.dfX_2.format(patt2D.getCentrX()));
            output.println("Beam_center_y = " + FileUtils.dfX_2.format(patt2D.getCentrY()));
            output.println("Pixelsize_x = " + FileUtils.dfX_2.format(patt2D.getPixSx() * 1000));
            output.println("Pixelsize_y = " + FileUtils.dfX_2.format(patt2D.getPixSy() * 1000));
            output.println("Ref_distance = " + FileUtils.dfX_2.format(patt2D.getDistMD()));
            output.println("Ref_wave = " + FileUtils.dfX_4.format(patt2D.getWavel()));
            output.println("Det_tiltDeg = " + FileUtils.dfX_3.format(patt2D.getTiltDeg()));
            output.println("Det_rotDeg = " + FileUtils.dfX_3.format(patt2D.getRotDeg()));
            output.println("Scan_omegaIni = " + FileUtils.dfX_1.format(patt2D.getOmeIni()));
            output.println("Scan_omegaFin = " + FileUtils.dfX_1.format(patt2D.getOmeFin()));
            output.println("Scan_acqTime = " + FileUtils.dfX_1.format(patt2D.getAcqTime()));
            output.println("EXZMargin =" + patt2D.getExz_margin());
            output.println("EXZThreshold =" + patt2D.getExz_threshold());
            output.println("EXZdetRadius =" + patt2D.getExz_detcircle());
            int polcount = 1;
            Iterator<PolyExZone> it = patt2D.getPolyExZones().iterator();
            while (it.hasNext()) {
                PolyExZone p = it.next();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < p.npoints; i++) {
                    sb.append(p.getXYvertex(i)).append(" ");
                }
                output.println("EXZpol" + polcount + " =" + sb.toString().trim());
                polcount = polcount + 1;
            }
            int arccount = 1;
            Iterator<ArcExZone> it2 = patt2D.getArcExZones().iterator();
            while (it2.hasNext()) {
                ArcExZone p = it2.next();
                output.println(String.format("EXZarc%d=%d %d %d %d", arccount,p.getPx(),p.getPy(),p.getHalfRadialWthPx(),p.getHalfAzimApertureDeg()));
                arccount = arccount + 1;
            }
            Iterator<Point> it3 = patt2D.getExZpaintPixels().iterator();
            StringBuilder sb = new StringBuilder();
            while (it3.hasNext()) {
                Point p = it3.next();
                sb.append(String.format("%d %d ", p.x,p.y));
            }
            output.println(String.format("EXZpix=%s",sb.toString().trim()));
            
            output.println("}");
            output.close();

            // ara mirem quants bytes hem escrit...
            int headerbytes = (int) d2File.length();
            log.config("Write D2D headerbytes=" + headerbytes);

            //mirem escala que hi càpiga en 65000
            float sc65 = patt2D.calcScale(D2Dplot_global.satur65);
            log.debug("scale to fit data repr="+sc65);
            
            OutputStream os = new BufferedOutputStream(new FileOutputStream(d2File, true));

            // escribim imatge tal cual
            for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    if (patt2D.getInten(i, j) > 0) {
                        bb.putChar((char) ((int)((float)patt2D.getInten(i, j)/sc65)));
                    } else {
                        bb.putChar((char) 0);
                    }
                    os.write(bb.array());
                    bb.clear();
                }
            }
            os.close();

        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error saving D2D file");
            return null;
        }
        return d2File;
    }

    private static File writeTIFF(File d2File, Pattern2D patt2D,boolean forceExt) {
    	ImageProcessor ip = new FloatProcessor(patt2D.getIntenAsIntArray());
//    	ip.setIntArray(patt2D.getIntenAsIntArray());
    	ImagePlus ipl = new ImagePlus("tiff file",ip.convertToShort(true));
    	
//    	ColorProcessor cp = new ColorProcessor(img);
//    	ImagePlus ip = new ImagePlus("tiff file",cp.convertToByte(false));
    	FileSaver fs = new FileSaver(ipl);
    	
    	
//    	ImagePlus ip = new ImagePlus("tiff file",img);
//    	FileSaver fs = new FileSaver(ip);
    	if(forceExt)d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".tif"));
    	fs.saveAsTiff(d2File.getAbsolutePath());

    	return d2File;
    }

    
    //using imageIO
//	try {
//		ImageIO.write((RenderedImage) img, "TIFF", d2File);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
    

    private static class filtreExz implements FilenameFilter {
        
        private String prefix;
        private String ext;
        public filtreExz(String prefix, String ext){
            super();
            this.prefix=prefix;
            this.ext=ext;
        }
        public boolean accept(File dir, String name) {
            return name.startsWith(prefix) && name.endsWith(ext);
        }
    }
    
    private static File findEXZfile(File imgFile){
        boolean trobat = false;
        
        File exfile = new File(FileUtils.getFNameNoExt(imgFile).concat(".exz"));
        if (exfile.exists())trobat=true;
        
        if (!trobat){
            exfile = new File(FileUtils.getFNameNoExt(imgFile).concat(".EXZ"));
            if (exfile.exists())trobat=true;
        }

        if (!trobat){
            File f = new File(D2Dplot_global.getWorkdir());
            log.debug(f.toString());
            String prefix = imgFile.getName().substring(0, FastMath.min(4,imgFile.getName().length()));
            File[] matchingFiles = f.listFiles(new filtreExz(prefix,"exz"));
            if(matchingFiles.length>0){
                exfile = matchingFiles[0];    
                trobat=true;
            }
            if (exfile.exists())trobat=true;
        }
        
        if (!trobat){
            File f = new File(D2Dplot_global.getWorkdir());
            String prefix = imgFile.getName().substring(0, FastMath.min(4,imgFile.getName().length()));
            File[] matchingFiles = f.listFiles(new filtreExz(prefix,"EXZ"));
            if(matchingFiles.length>0){
                exfile = matchingFiles[0];    
                trobat=true;
            }
            if (exfile.exists())trobat=true;
        }
        
        if (!trobat)return null;
        return exfile;
    }
    
    // if exffile==null es mira el nomb del patt2d
    public static boolean readEXZ(Pattern2D patt2D, File exfile, boolean exzConfirm) {
        File dataFile = patt2D.getImgfile();
        if (exfile == null) {
            exfile = findEXZfile(dataFile);
            if (exfile!=null){
                if (!exfile.exists()) return false;
                if (exzConfirm){
                    boolean readEXZ = FileUtils.YesNoDialog(null,"Excluded Zones file found ("+exfile.getName()+"). Read it?");
                    if (!readEXZ) return false;
                }
            }else{
                return false;
            }
        }
        if(!exfile.exists()){
            //tambe provem de trobarlo
            exfile = findEXZfile(dataFile);
            if (exfile!=null){
                if (!exfile.exists()) return false;
                if (exzConfirm){
                    boolean readEXZ = FileUtils.YesNoDialog(null,"Excluded Zones file found ("+exfile.getName()+"). Read it?");
                    if (!readEXZ) return false;
                }
            }else{
                return false;
            }
        }
        // aqui hauriem de tenir exfile ben assignada, la llegim
        String line;
        try {
            Scanner scExFile = new Scanner(exfile);
            boolean llegint = true;

            while (llegint) {
                if (scExFile.hasNextLine()) {
                    line = scExFile.nextLine();
                } else {
                    scExFile.close();
                    llegint = false;
                    continue;
                }

                if (line.startsWith("!")) {
                    continue;
                }

                int iigual = line.indexOf("=") + 1;

                if (FileUtils.containsIgnoreCase(line, "EXZmarg")) {
                    patt2D.setExz_margin(Integer.parseInt(line.substring(iigual, line.trim().length()).trim()));
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "EXZthres")) {
                    int yth = Integer.parseInt(line.substring(iigual,line.trim().length()).trim());
                    patt2D.setExz_threshold(yth);
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "EXZdet")) {
                    int ydc = Integer.parseInt(line.substring(iigual,line.trim().length()).trim());
                    patt2D.setExz_detcircle(ydc);
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "EXZpol")) {
                    String linia = line.substring(iigual, line.trim().length()).trim();
                    String[] values = linia.split("\\s+");
                    PolyExZone z = new PolyExZone(false);
                    for (int i = 0; i < values.length; i = i + 2) {
                        // parelles x1 y1 x2 y2 .... que son els vertexs
                        z.addPoint(Integer.parseInt(values[i]),Integer.parseInt(values[i + 1]));
                    }
                    if (!patt2D.getPolyExZones().contains(z)) { // NO REPETIM ZONES
                        patt2D.addExZone(z);
                    }
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "EXZarc")) {
                    String linia = line.substring(iigual, line.trim().length())
                            .trim();
                    String[] values = linia.split("\\s+");
                    int ipx = Integer.parseInt(values[0]);
                    int ipy = Integer.parseInt(values[1]);
                    int hradwpx = Integer.parseInt(values[2]);
                    float hazimwdeg = Float.parseFloat(values[3]);
                    ArcExZone a = new ArcExZone(ipx,ipy,hradwpx,hazimwdeg,patt2D);
                    if (!patt2D.getArcExZones().contains(a)) { // NO REPETIM ZONES
                        patt2D.addArcExZone(a);;
                    }
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "EXZpix")) {
                    String linia = line.substring(iigual, line.trim().length()).trim();
                    String[] values = linia.split("\\s+");
                    for(int i=0;i<values.length;i=i+2) {
                        patt2D.addPaintedEXZpixel(new Point(Integer.parseInt(values[i]),Integer.parseInt(values[i+1])));
                        log.debug("adding pixel exz="+values[i]+" "+values[i+1]);
                    }
                    continue;
                }
                
            }
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading EXZ file");
            return false;
        }
        patt2D.recalcExcludedPixels();
        return true;
    }

    //    public static boolean rescale(Pattern2D patt2D, File d2File) {
    //        if (FileUtils.getExtension(d2File).equalsIgnoreCase("img")) {
    //            // rellegim les intensitats del fitxer IMG considerant les zones excloses
    //            int headerSize = 0;
    //            try {
    //                Scanner scD2file = new Scanner(d2File);
    //                for (int i = 0; i < 50; i++) {
    //                    if (scD2file.hasNextLine()) {
    //                        String line = scD2file.nextLine();
    //                        if (FileUtils.containsIgnoreCase(line, "HEADER_BYTES")) {
    //                            headerSize = Integer.parseInt(line.substring(13,
    //                                    line.trim().length() - 1).trim());
    //                        }
    //                    }
    //                }
    //
    //                scD2file.close();
    //
    //                // ARA LLEGIREM ELS BYTES
    //                InputStream in = new BufferedInputStream(new FileInputStream(d2File));
    //                byte[] buff = new byte[2];
    //                byte[] header = new byte[headerSize];
    //                // int maxI=0;
    //                // int minI=99999999;
    //                in.read(header);
    //                // Haurem de fer dues passades, una per determinar el maxI, minI
    //                // i
    //                // factor d'escala i l'altre per
    //                // llegir totes les intensitats i aplicar el factor d'escala per
    //                // encabir-ho a short.
    //                for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila
    //                                                             // (Y)
    //                    for (int j = 0; j < patt2D.getDimX(); j++) { // per cada
    //                                                                 // columna
    //                                                                 // (X)
    //                        in.read(buff);
    //                        // si esta en zona exclosa el saltem
    //                        if (patt2D.isInExZone(j, i)) continue;
    //                        int valorLlegit = FileUtils.B2toInt(buff);
    //                        if (valorLlegit >= 0) { // fem >= o > directament sense
    //                                                // considerar els zeros??!
    //                            if (valorLlegit > patt2D.getMaxI()) {
    //                                patt2D.setMaxI(valorLlegit);
    //                            }
    //                            if (valorLlegit < patt2D.getMinI()) {
    //                                patt2D.setMinI(valorLlegit);
    //                            }
    //                        }
    //                    }
    //                }
    //
    //                // calculem el factor d'escala (valor maxim entre quocient i 1,
    //                // mai
    //                // escalem per sobre)
    //                patt2D.setScale(FastMath.max(patt2D.getMaxI()
    //                        / (float) D2Dplot_global.satur32, 1.000f));
    //                in.close();
    //                in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
    //                                                                           // buffer
    //                                                                           // lectura
    //                in.read(header);
    //
    //                // ara aplico factor escala i guardo on i com toca
    //                for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila
    //                                                             // (Y)
    //                    for (int j = 0; j < patt2D.getDimX(); j++) { // per cada
    //                                                                 // columna
    //                                                                 // (X)
    //                        in.read(buff);
    //                        if (patt2D.isInExZone(j, i)) {
    //                            patt2D.setInten(j, i, -1);
    //                            continue;
    //                        }
    //                        patt2D.setInten(j, i,
    //                                (int) (FileUtils.B2toInt(buff) / patt2D
    //                                        .getScale()));
    //                    }
    //                }
    //                // corregim maxI i minI
    //                patt2D.setMaxI((int) (patt2D.getMaxI() / patt2D.getScale()));
    //                patt2D.setMinI((int) (patt2D.getMinI() / patt2D.getScale()));
    //
    //                in.close();
    //            } catch (Exception e) {
    //                if (D2Dplot_global.isDebug()) e.printStackTrace();
    //                log.warning("Error in rescale");
    //                return false;
    //            }
    //        } else {
    //            // no es img, reescalem igualment (mai m�s intensitat que la
    //            // original)
    //            patt2D.recalcScale();
    //        }
    //
    //        return true; // correcte
    //
    //    }
    
        public static File writeEXZ(File exfile, Pattern2D patt2D,boolean forceExt) {
            // forcem extensio EXZ
            if(forceExt)exfile = new File(FileUtils.getFNameNoExt(exfile).concat(".exz"));
    
//            if (exfile.exists()) {
//                // avisem que es sobreescriurÀ
//                Object[] options = { "Yes", "No" };
//                int n = JOptionPane.showOptionDialog(null,
//                        "EXZ file will be overwritten. Continue?", "File exists",
//                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
//                        null, // do not use a custom Icon
//                        options, // the titles of buttons
//                        options[1]); // default button title
//                if (n != 0) {
//                    return null;
//                } // si s'ha cancelat o tancat la finestra no es segueix salvant
//            }
            
            // creem un printwriter amb el fitxer file (el que estem escribint)
            try {
                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(exfile)));
                // ESCRIBIM AL FITXER:
                output.println("! Excluded zones file for: " + patt2D.getImgfileString());
                output.println("EXZmargin=" + patt2D.getExz_margin());
                output.println("EXZthreshold=" + patt2D.getExz_threshold());
                output.println("EXZdetRadius=" + patt2D.getExz_detcircle());
                int polcount = 1;
                Iterator<PolyExZone> it = patt2D.getPolyExZones().iterator();
                while (it.hasNext()) {
                    PolyExZone p = it.next();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < p.npoints; i++) {
                        sb.append(p.getXYvertex(i)).append(" ");
                    }
                    output.println("EXZpol" + polcount + "=" + sb.toString().trim());
                    polcount = polcount + 1;
                }
                int arccount = 1;
                Iterator<ArcExZone> it2 = patt2D.getArcExZones().iterator();
                while (it2.hasNext()) {
                    ArcExZone p = it2.next();
                    output.println(String.format("EXZarc%d=%d %d %d %.1f", arccount,p.getPx(),p.getPy(),p.getHalfRadialWthPx(),p.getHalfAzimApertureDeg()));
                    arccount = arccount + 1;
                }
                
                Iterator<Point> it3 = patt2D.getExZpaintPixels().iterator();
                StringBuilder sb = new StringBuilder();
                while (it3.hasNext()) {
                    Point p = it3.next();
                    sb.append(String.format("%d %d ", p.x,p.y));
                }
                output.println(String.format("EXZpix=%s",sb.toString().trim()));
                
                // COMENTARIS INFO
                output.println("!");
                output.println("! EXZmargin     Margin of the image in pixels (if any)");
                output.println("! EXZthreshold  Pixels with Y<threshold will be excluded");
                output.println("! EXZdetRadius  To exclude corners of the image in case detection area is circular(radius in px)");
                output.println("! EXZpol#       Sequence of pixels (X1 Y1 X2 Y2 X3 Y3...) defining a polygonal shape");
                output.println("! EXZarc#       Arc-shape defined as: ArcCenterX ArcCenterY ArcHalfRadialWthPx ArcHalfAzimWthDeg");
                output.println("! EXZpix#       List of other excluded pixels (pairs of x y)");
    
                output.close();
    
            } catch (IOException e) {
                if (D2Dplot_global.isDebug()) e.printStackTrace();
                log.warning("Error writting EXZ file");
                return null;
            }
            return exfile;
        }

        public static File writeCALfile(File calfile, Pattern2D patt2D, com.vava33.d2dplot.Calibration c,boolean forceExt){
        	if(forceExt)calfile = new File(FileUtils.getFNameNoExt(calfile).concat(".cal"));
            String tm = D2Dplot_global.getStringTimeStamp("yyyy-MM-dd 'at' HH:mm");
            
            try {
                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(calfile)));
                output.println(String.format("# Calibration for %s on %s", patt2D.getImgfileString(),tm));
                output.println(String.format("CALFIL = %s [d2Dplot]", patt2D.getImgfile().getAbsolutePath()));
                output.println(String.format("X-BEAM CENTRE = %.2f", c.getRefCX()));
                output.println(String.format("Y-BEAM CENTRE = %.2f", c.getRefCY()));
                output.println(String.format("DISTANCE = %.3f", c.getRefMD()));
                output.println(String.format("WAVELENGTH = %.5f", patt2D.getWavel()));
                output.println(String.format("TILT ROTATION = %.2f", c.getRefRotDeg()));
                output.println(String.format("ANGLE OF TILT = %.3f", c.getRefTiltDeg()));
                output.println();
                output.println(String.format("SUBADU = %.1f", -9.5f));
                output.println(String.format("START AZIMUTH = %.1f", 0.f));
                output.println(String.format("END AZIMUTH = %.1f", 360.f));
                output.println(String.format("INNER RADIUS = %d", 0));
                output.println(String.format("OUTER RADIUS = %d", (int)(patt2D.getDimX()/2)-1));
                output.println(String.format("AZIMUTH BINS = %d", 1));
                output.println(String.format("RADIAL BINS = %d", (int)(patt2D.getDimX()/2)-1));
                output.println();
                output.println(String.format("#MASK FILE = %s", D2Dplot_global.getWorkdir()+D2Dplot_global.separator+"MASK.bin"));
                output.println("#Mask file is a zero-intensity image with mask pixels at -1 intensity");
                output.close();
            }catch(Exception ex){
                if (D2Dplot_global.isDebug())ex.printStackTrace();
                log.warning(String.format("Error writting output CAL file: %s",calfile.toString()));
            }
			return calfile;
        }
        
    public static boolean readCALfile(Pattern2D patt2D, File calfile, ConvertTo1DXRD ir){
        String line;
        try {
            Scanner scCALFile = new Scanner(calfile);
            
            //inits
            float inner = 0;
            float outer = FastMath.round((patt2D.getDimX()/2)-1);
            int radBins = (int)outer;
            File mskf = null;
            
            while (scCALFile.hasNextLine()) {
                line = scCALFile.nextLine();
                log.debug(line);
                if (line.startsWith("#")) {
                    continue;
                }

                int iigual = line.indexOf("=") + 1;
                int ipcoma = line.indexOf(";") - 1;
                if (ipcoma<0)ipcoma=line.trim().length();

                if (FileUtils.containsIgnoreCase(line, "X-BEAM")) {
                    patt2D.setCentrX(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "Y-BEAM")) {
                    patt2D.setCentrY(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "DISTANCE")) {
                    patt2D.setDistMD(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "WAVELENGTH")) {
                    patt2D.setWavel(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "ROTATION")) {
                    patt2D.setRotDeg(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                    continue;
                }
                if (FileUtils.containsIgnoreCase(line, "ANGLE")) {
                    patt2D.setTiltDeg(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                    continue;
                }
                if (ir!=null){
                    log.fine("inside ir");
                    if (FileUtils.containsIgnoreCase(line, "SUBADU")) {
                        ir.setTxtSubadu(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                        continue;
                    }
                    if (FileUtils.containsIgnoreCase(line, "START")) {
                        ir.setTxtCakeIn(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                        continue;
                    }
                    if (FileUtils.containsIgnoreCase(line, "END")) {
                        ir.setTxtCakeFin(Float.parseFloat(line.substring(iigual, ipcoma).trim()));
                        continue;
                    }
                    if (FileUtils.containsIgnoreCase(line, "INNER")) {
                        inner = Float.parseFloat(line.substring(iigual, ipcoma).trim());
                        continue;
                    }
                    if (FileUtils.containsIgnoreCase(line, "OUTER")) {
                        outer = Float.parseFloat(line.substring(iigual, ipcoma).trim());
                        continue;
                    }
                    if (FileUtils.containsIgnoreCase(line, "AZIMUTH BINS")) {
                        ir.setTxtAzimbins(Integer.parseInt(line.substring(iigual, ipcoma).trim()));
                        continue;
                    }
                    if (FileUtils.containsIgnoreCase(line, "RADIAL BINS")) {
                        radBins = Integer.parseInt(line.substring(iigual, ipcoma).trim());
                        continue;
                    }
                    if (FileUtils.containsIgnoreCase(line, "MASK")) {
                        mskf = new File(line.substring(iigual, ipcoma).trim());
                        log.debug("mskf="+mskf.toString());
                        continue;
                    }
                }
            }
            if(ir!=null){
                float t2i = (float) patt2D.calc2T(patt2D.getCentrX()+inner, patt2D.getCentrY(), true);
                ir.setTxtT2i(t2i);
                float t2f = (float) patt2D.calc2T(FastMath.min(patt2D.getCentrX()+outer,patt2D.getDimX()), patt2D.getCentrY(), true);
                ir.setTxtT2f(t2f);
                float step = (t2f-t2i)/(float)radBins;
                if(step<patt2D.calcMinStepsizeBy2Theta4Directions())step=patt2D.calcMinStepsizeBy2Theta4Directions();
                ir.setTxtStep(step);
                log.debug(Float.toString(ir.getTxtCakefin()));
                //aplicar mascara
                if(mskf!=null){
                    //apply mask pixels
                    if (mskf.exists())log.fine("mask file exists");
                    log.fine("applying mask");
                    Pattern2D msk = ImgFileUtils.readPatternFile(mskf, false);
//                    patt2D.copyMaskPixelsFromImage(msk); 
                    patt2D.copyExZonesFromImage(msk); //TODO:comprovar que funcioni, he canviat el metode
                    ir.setMaskfile(mskf);
                }else{
                    log.fine("mskf is null");
                }
            }
            scCALFile.close();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading CAL file");
            return false;
        }
        return true;
    }
   
    
    public static File readXDS(File xdsFile, Pattern2D patt2d) {
        // list of: x,y,z,Intensity,(iseg),h,k,l
        // (z is the image number where the centroid of the reflection is... it
        // is FLOAT, does not correspond to exact an image)
        try {
            OrientSolucio.setNumSolucions(1); // num de solucions
            OrientSolucio.setHasFc(0); // 0 sense Fc, 1 amb Fc
            OrientSolucio.setGrainIdent(0);

            patt2d.getSolucions().add(new OrientSolucio(0)); // afegim una
                                                             // solucio
            patt2d.getSolucions().get(0).setGrainNr(0);
            patt2d.getSolucions().get(0).setNrefCoincidents(0.0f); // valor funcio rotacio

            int npunts = 0;

            Scanner scSolfile = new Scanner(xdsFile);
            while (scSolfile.hasNextLine()) {
                String line = scSolfile.nextLine();
                if (line.isEmpty()) continue;
                npunts = npunts + 1;
                log.debug("xdsfileline= " + line);
                String lineS[] = line.trim().split("\\s+");
                patt2d.getSolucions().get(0).addSolPoint(npunts, Float.parseFloat(lineS[0]),
                                Float.parseFloat(lineS[1]),
                                Integer.parseInt(lineS[4]),
                                Integer.parseInt(lineS[5]),
                                Integer.parseInt(lineS[6]), 1.0f,
                                Float.parseFloat(lineS[2]));

            }
            scSolfile.close();
            patt2d.getSolucions().get(0).setNumReflexions(npunts);

        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading XDS file");
        }
        return xdsFile;
    }

    public static File readPCSasSOL(File pcsFile, Pattern2D patt2d){
//        NPEAK     XPIXEL    YPIXEL    RO_VAL        YMAX         FH2     sigma(FH2)    p    CODI INTRAD   D
//        1   1049.50    663.50    361.52      32711.41       65.61        2.66   0.586    2   14    2.7770
        OrientSolucio.setPXY(true);
        String line;
        try {
            Scanner scPCSfile = new Scanner(pcsFile);
            boolean firstLine = false;
            while (!firstLine){
                line = scPCSfile.nextLine();
                if (line.trim().startsWith("NPEAK"))firstLine=true;
            }
            patt2d.getSolucions().add(new OrientSolucio(0));
            OrientSolucio.setPCS(true);
            while (scPCSfile.hasNextLine()){
                line = scPCSfile.nextLine();
                String[] lineS = line.trim().split("\\s+");
                patt2d.getSolucions().get(0)
                .addSolPoint(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]), false);
                int codi = Integer.parseInt(lineS[8]);
                boolean nearmask = false;
                int neixam = 1;
                int nsatur = 0;
                Point2D.Float centre = new Point2D.Float(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]));
                if (codi<-1){
                    nsatur = codi*(-1);
                }
                if (codi == -1){
                    nearmask = true;
                }
                if (codi>1){
                    neixam = codi;
                }
                patt2d.getSolucions().get(0).getPeaksPCS().add(new Peak(centre,neixam,nsatur,nearmask));
            }
            scPCSfile.close();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading SOL file");
        }
        return pcsFile;
    }
    
    public static File writePCS(Pattern2D patt2d, File PCSfile, float delsig,
            float angDeg,boolean autoAngDeg,int zoneR,int minpix,int bkgpt,boolean autoBkgPt,boolean autoazim,boolean forceExt){
    	if(forceExt)PCSfile = new File(FileUtils.getFNameNoExt(PCSfile).concat(".PCS"));
        //TODO MOSTRAR ALERTA DE QUE PRIMER S'HA DE FER EL CALCUL SI NO S'HA FET
        //per exemple amb int nrows = table.getModel().getRowCount();
        
        String eqLine="=============================================================================================================";
        String minLine="-------------------------------------------------------------------------------------------------------------";
    
        // creem un printwriter amb el fitxer file (el que estem escribint)
        try {
            //preparem les files (ho he mogut aqui perque em fan falta alguns calculs per la capçalera
            Iterator<Peak> itrR = patt2d.getPkSearchResult().iterator();
            int maxIntRad = 0;
            float maxAzim = 0.f;
            float minAzim = 99999999.f;
            while (itrR.hasNext()){
                Peak r = itrR.next();
                if(r.getIntRadPx()>maxIntRad)maxIntRad=r.getIntRadPx();
                if(r.getAzimAper()>maxAzim)maxAzim=r.getAzimAper();
                if(r.getAzimAper()<minAzim)minAzim=r.getAzimAper();
            }
            
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(PCSfile)));
            // ESCRIBIM AL FITXER:
            output.println(eqLine);
            output.println("D2Dplot "+D2Dplot_global.version+" peak integration for TTS_INCO");
            output.println(eqLine);
            output.println("Image File= "+patt2d.getImgfileString());
            output.println(String.format("dimX= %d, dimY= %d, centX= %.2f, centY= %.2f",patt2d.getDimX(),patt2d.getDimY(),patt2d.getCentrX(),patt2d.getCentrY()));
            output.println(String.format("pixSize(micron)= %.3f, dist(mm)= %.3f, wave(A)= %.5f",patt2d.getPixSx()*1000,patt2d.getDistMD(),patt2d.getWavel()));
            switch (patt2d.getIscan()){
                case 1:
                    output.println("HORIZONTAL rotation axis");
                    break;
                case 2:
                    output.println("VERTICAL rotation axis");
                    break;
                default:
                    output.println("NO rotation axis");
                    break;
            }
            output.println(String.format("Saturated pixels= %d (sat. value= %d)",patt2d.getnSatur(),patt2d.getSaturValue()));
            output.println(String.format("Saturated peaks= %d",patt2d.getnPkSatur())); 
            output.println(String.format("MeanI= %.1f Sigma(I)= %.3f",patt2d.getMeanI(),patt2d.getSdevI()));
            output.println(String.format("ESD factor = %.2f",delsig));    
            if (autoazim){
                output.println(String.format("Auto azim aperture of the integration (º) in the range %.2f to %.2f",minAzim,maxAzim));
            }else{
                output.println(String.format("Azim aperture of the integration (º)= %.2f",angDeg));
            }
            output.println(String.format("Max radial integration width (pixels)= %d",maxIntRad));
            output.println(String.format("Peak merge zone radius (pixels)= %d",zoneR));
            output.println(String.format("Min pixels for a peak= %d",minpix));
            if (autoBkgPt){
                output.println(String.format("Background pixels determined automatically"));
            }else{
                output.println(String.format("Background pixels= %d",bkgpt));
            }
            output.println(minLine);
            
            output.println(Peak.pcs_header);
            //ara ordenem la llista i l'escribim
            Collections.sort(patt2d.getPkSearchResult());
            Iterator<Peak> itrpcs = patt2d.getPkSearchResult().iterator();
            int npk = 1;
            while (itrpcs.hasNext()){
                Peak pcsr= itrpcs.next();
                output.println(String.format("%6d %s", npk,pcsr.getFormmattedStringPCS()));
                npk=npk+1;
            }
            output.close();
    
        } catch (IOException e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error writting PCS file");
            return null;
        }
        log.info("PCS file written!");
        return PCSfile;
    }

    public static File readSOL(File solFile, Pattern2D patt2d) {
        String line;
        if (FileUtils.getExtension(solFile).equalsIgnoreCase("pxy")) {
            OrientSolucio.setPXY(true);
        }
        try {
            Scanner scSolfile = new Scanner(solFile);
            int nsols = 0;
            while (scSolfile.hasNextLine()){
                line = scSolfile.nextLine();
                log.debug(line);
                
                if (line.contains("NUMBER OF SOLUTIONS")){
                    nsols = scSolfile.nextInt();
                    log.debug("nsols="+nsols);
                    OrientSolucio.setNumSolucions(nsols);
                    continue;
                }
                
                if (line.contains("ATOMS IN")){
                    int natoms = scSolfile.nextInt();
                    if (natoms>0){
                        OrientSolucio.setHasFc(1);
                    }
                    continue;
                }
                
                if (line.contains("GRAIN IDENTI")){
                    OrientSolucio.setGrainIdent(scSolfile.nextInt());
                    continue;
                }
                
                if (line.contains("GRAIN NR")){
                    //COMENCEN LES SOLUCIONS
                    break;
                }
            }
            //ara llegim les solucions
            for (int nsol = 0; nsol < nsols; nsol++){
                OrientSolucio os = new OrientSolucio(nsol);
                os.setGrainNr(scSolfile.nextInt());
                log.debug(scSolfile.nextLine());
                log.debug(scSolfile.nextLine());//CENTRE                
                line = scSolfile.nextLine();
                log.debug(line);
                String[] vals = line.trim().split("\\s+");
                os.setNumReflexions(Integer.parseInt(vals[0]));
                os.setNrefCoincidents(Float.parseFloat(vals[1]));
                if (vals.length>2){ //conte info ORIENT
                    os.setAngR_lon(Float.parseFloat(vals[2]));
                    os.setAngS_lat(Float.parseFloat(vals[3]));
                    os.setAngT_spin(Float.parseFloat(vals[4]));
                }
                log.debug(scSolfile.nextLine());// matriu Rot
                log.debug(scSolfile.nextLine());// matriu Rot
                log.debug(scSolfile.nextLine());// matriu Rot
                
                // ara comencen les reflexions
                boolean endSol = false;
                int npunts = 0;
                while (!endSol) {
                    if (!scSolfile.hasNextLine()) {
                        endSol = true;
                        continue;
                    }
                    line = scSolfile.nextLine();
                    log.debug("bona " + line);
                    if (line.trim().isEmpty()) continue;
                    if (line.trim().startsWith("GRAIN")) {
                        endSol = true;
                        continue;
                    }
                    npunts = npunts + 1;
                    String[] lineS = line.trim().split("\\s+");
                    os.addSolPoint(Integer.parseInt(lineS[0]),
                                    Float.parseFloat(lineS[1]),
                                    Float.parseFloat(lineS[2]),
                                    Integer.parseInt(lineS[3]),
                                    Integer.parseInt(lineS[4]),
                                    Integer.parseInt(lineS[5]),
                                    Float.parseFloat(lineS[6]),
                                    Float.parseFloat(lineS[7]));
                }
                os.setNumReflexions(npunts);
                os.renumberSOLpoints();
                patt2d.addSolucio(os);
            }
            scSolfile.close();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading SOL file");
        }
        return solFile;
    }

    public static File generateTSD(File f, boolean forceExt) {
    	if (forceExt)f = new File(FileUtils.getFNameNoExt(f).concat(".TSD"));
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            output.println("DIOPSID ALBA_25gen14 (no offset) C2/C");
            output.println("CELL");
            output.println(" 9.7354 8.9109 5.2451  90.   106.385   90. ");
            output.println("LATTICE");
            output.println("C-");
            output.println("LAUE");
            output.println("2");
            output.println("SYMMETRY");
            output.println("X, Y, Z ");
            output.println("X,-Y,1/2+Z ");
            output.println("CONTENTS");
            output.println("Ca  SI  Al MG    O");
            output.println(" 4   7   2  3   24");
            output.println("&CONTROL ");
            output.println("SWING=10.,");
            output.println("GRUIX=0.16,");
            output.println("ABSCOF=3.39,");
            output.println("SCAINT=1.0,");
            output.println("DSFOU=1.0,");
            output.println("MULTDOM=0,");
            output.println("IOFF=0,");
            output.println("NSOL=10,");
            output.println("ALON=0.0,");
            output.println("ALAT=0.0,");
            output.println("SPIN=0.0");
            output.println("/");
            output.println("MODEL");
            output.println(" 8");
            output.println("  1  0.0000   0.3019   0.25000   0.50000  CA");
            output.println("  2  0.2874   0.0935   0.22880   0.85000  SI "); 
            output.println("  3  0.2874   0.0935   0.22880   0.15000  AL1");
            output.println("  4  0.0000   0.9080   0.25000   0.35000  MG ");
            output.println("  3  0.0000   0.9080   0.25000   0.15000  AL2");
            output.println("  5  0.1140   0.0870   0.13860   1.00000  O1 ");
            output.println("  5  0.3652   0.2526   0.32060   1.00000  O2 ");
            output.println("  5  0.3517   0.0186   0.99410   1.00000  O3 ");
            output.println("PCS/HKL");
            output.println("1");
            output.println("0, 0.0  ");
            output.close();
        }catch(Exception ex) {
            if (D2Dplot_global.isDebug()) ex.printStackTrace();
            log.warning("Error writting TSD file");
            return null;
        }
        return f;
    }
    
    public static File exportPNG(File f, BufferedImage i,boolean forceExt) {
        // forcem extensio PNG
        if(forceExt)f = new File(FileUtils.getFNameNoExt(f).concat(".png"));
    
        if (i != null) {
            try {
                ImageIO.write(i, "png", f);
                return f;
            } catch (Exception e) {
                if (D2Dplot_global.isDebug()) e.printStackTrace();
                log.warning("Error saving PNG file");
                return null;
            }
        }
        log.warning("No image opened");
        return null;
    }

    public static double getScaleFactorToFit(Dimension original, Dimension toFit) {
        double dScale = 1d;
        if (original != null && toFit != null) {
            double dScaleWidth = getScaleFactor(original.width, toFit.width);
            double dScaleHeight = getScaleFactor(original.height, toFit.height);
            dScale = Math.min(dScaleHeight, dScaleWidth);
        }
        return dScale;
    }

    private static double getScaleFactor(int iMasterSize, int iTargetSize) {
        double dScale = 1;
        if (iMasterSize > iTargetSize) {
            dScale = (double) iTargetSize / (double) iMasterSize;
        } else {
            dScale = (double) iTargetSize / (double) iMasterSize;
        }
        return dScale;
    }

    public static class batchConvertFileWorker extends
            SwingWorker<Integer, Integer> {

        private File[] flist;
//        LogJTextArea taOut;
        ImagePanel ip;

        // distMD & wavel -1 to take the ones from the original image,
        // exzfile=null for the same.
        public batchConvertFileWorker(File[] files, ImagePanel ip) {
            this.flist = files;
//            this.taOut = textAreaOutput;
            this.ip = ip;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            
            int totalfiles = flist.length;
            
            //PREGUNTEM EL FORMAT DE SORTIDA (aixo pot anar aqui o a maniframe)
            //this line returns the FORMAT in the ENUM or NULL
            SupportedWriteExtensions[] possibilities = SupportedWriteExtensions.values();
            SupportedWriteExtensions format = (SupportedWriteExtensions)JOptionPane.showInputDialog(null, "Output format:", "Save Files",
                    JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);
            if (format == null) {
                return -1;
            }
            
            //preguntem si es volen canviar parametres instrumentals per defecte:
            //PREGUNTEM HKL,FSTRUCT AND OSCIL?
            JPanel myPanel = new JPanel();
            JTextField txtDistMD = new JTextField(10);
            JTextField txtWaveL = new JTextField(10);
            txtDistMD.setText("StoD Distance");
            txtWaveL.setText("Wavelength");
            myPanel.add(txtDistMD);
            myPanel.add(txtWaveL);
            Object[] optionsW = {"Yes, apply these!","No, take from source images"};
            int opt = JOptionPane.showOptionDialog(null, myPanel, "Apply Custom Distance and Wavelength?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsW, optionsW[1]);
            
            //intentem treure els inputs:
            float newDistMD=-1.f;
            float newWavel=-1.f;
            
            if(opt==JOptionPane.YES_OPTION) {
                try{
                    newDistMD = Float.parseFloat(txtDistMD.getText());    
                }catch(Exception e){
                    log.info("Could not read sample-detector distance, using the ones from input files");
                }
                try{
                    newWavel = Float.parseFloat(txtWaveL.getText());  
                }catch(Exception e){
                    log.info("Could not read wavelength, using the ones from input files");
                }
            }
            
            //ask for excluded zones files
            File newExZfile = null;
            Object[] options = {"Yes","No"};
            int m = JOptionPane.showOptionDialog(null,
                    "Apply an Excluded Zones file to all images?",
                    "ExZ file",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    options, //the titles of buttons
                    options[1]); //default button title
            if (m==JOptionPane.YES_OPTION){
                newExZfile = FileUtils.fchooserOpen(null,new File(D2Dplot_global.getWorkdir()), null, 0);
            }
            
            //Ask for output folder
            boolean sameDir= FileUtils.YesNoDialog(null,"Save output files in the same folder?");
            File outdir = null;
            if (!sameDir){
                outdir = FileUtils.fchooserOpenDir(null, flist[0], "Select output directory");
                if (outdir==null){sameDir = true;}
            }
            
            
            //ara anem imatge per imatge a guardar-la (mateix nom, diferent extensio, preguntarem si overwrite)
            boolean applyAll=false;
            boolean owrite = false;
            for (int i=0; i<flist.length;i++){
                
                float percent = ((float)i/(float)totalfiles)*100.f;
                setProgress((int) percent);
                
                Pattern2D in = ImgFileUtils.readPatternFile(flist[i],false);
                if (in==null){
                    log.warning("Error reading file "+flist[i].getName()+" ...skipping");
                    continue;
                }
                
                File out = new File(FileUtils.getFNameNoExt(flist[i]).concat("."+format.toString().toLowerCase()));
                if (!sameDir){
                    String nomNewFile = FileUtils.canviExtensio(flist[i], format.toString().toLowerCase()).getName();
                    String fullNewFile = outdir.getPath().concat(FileUtils.getSeparator()).concat(nomNewFile);
                    out = new File(fullNewFile);
                }
                
                if (out.exists()){
                    if (!applyAll){
                        JCheckBox checkbox = new JCheckBox("Apply to all");
                        String message = "Overwrite "+out.getName()+"?";
                        Object[] params = {message, checkbox};
                        int n = JOptionPane.showConfirmDialog(null, params, "Overwrite existing file", JOptionPane.YES_NO_OPTION);
                        applyAll = checkbox.isSelected();
                        if (n == JOptionPane.YES_OPTION) {
                            owrite = true;
                        }else{
                            owrite = false;
                        }
                    }
                    if (!owrite)continue;
                }
                
                //mirem si s'han forçat alguns dels parametres instrumentals:
                if (newDistMD > 0){
                    in.setDistMD(newDistMD);
                }
                if (newWavel > 0){
                    in.setWavel(newWavel);
                }
                
                if (newExZfile != null){
                    ImgFileUtils.readEXZ(in, newExZfile,false);
                }
                
                //podem escriure fitxer out
                File f = null;
                
                //provem d'escriure directament i que s'encarregui el metode write del format
                
                f = ImgFileUtils.writePatternFile(out, in, false);
                
                if (f != null) {
                    log.info(f.toString()+" written!");
                }else{
                    log.warning("Error writting "+out.toString());
                }
            }
            this.setProgress(100);
            return 0;
        }
    }
   
}
