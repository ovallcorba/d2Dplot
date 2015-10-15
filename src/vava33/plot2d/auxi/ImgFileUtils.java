package vava33.plot2d.auxi;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.FileUtils;

import vava33.plot2d.auxi.VavaLogger;
import vava33.plot2d.MainFrame;

public final class ImgFileUtils {

    public static Pattern2D openBinaryFile(File d2File) {
        Pattern2D patt2D = null;
        try {
            long start = System.nanoTime(); // control temps
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[2];
            byte[] buff4 = new byte[4]; // real
            float scale = -1;
            float centrX, centrY, pixlx, pixly, sepod, wl;
            int dimX, dimY;
            int dataHeaderBytes = 36; // bytes de dades en el header

            // !FITXER BIN (23/05/2013):
            // !- Cap�alera fixa de 60 bytes. De moment hi ha 9 valors (la resta �s "buida"):
            // ! Int*4 NXMX(cols)
            // ! Int*4 NYMX(rows)
            // ! Real*4 SCALE
            // ! Real*4 CENTX
            // ! Real*4 CENTY
            // ! Real*4 PIXLX
            // ! Real*4 PIXLY
            // ! Real*4 DISTOD
            // ! Real*4 WAVEL
            // !- Llista Int*2 (amb signe.. -32,768 to 32,767) amb ordre files-columnes, es a dir,
            // ! l'index rapid es el de les columnes (files,columnes): (1,1)  (2,1) (3,1) ...
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

            in.read(new byte[60 - dataHeaderBytes]);

            patt2D = new Pattern2D(dimX, dimY, centrX, centrY, 0, 999999999, scale, false);
            patt2D.setExpParam(pixlx, pixly, sepod, wl);
            int count = 0;

            // el short arriba a 32,767... per tant maxI/scale=32767
            // si s'ha especificat al fitxer un factor d'escala l'utilitzem, ja
            // que vol dir que son I2(signed)
            // sino el calcularem i l'aplicarem, ja que podria ser que fosin
            // unsigned i superesin el limit
            if (patt2D.getScale() > 0) {
                // utilitzem l'escala, es llegeix directe
                for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila
                                                             // (Y)
                    for (int j = 0; j < patt2D.getDimX(); j++) { // per cada
                                                                 // columna (X)
                        in.read(buff);
                        patt2D.setInten(j, i, FileUtils.B2toInt(buff));
                        count = count + 1;
                        // nomes considerem valors superiors a zero pel minI
                        if (patt2D.getInten(j, i) >= 0) {
                            if (patt2D.getInten(j, i) > patt2D.getMaxI()) {
                                patt2D.setMaxI(patt2D.getInten(j, i));
                            }
                            if (patt2D.getInten(j, i) < patt2D.getMinI()) {
                                patt2D.setMinI(patt2D.getInten(j, i));
                            }
                        }
                    }
                }
            } else {
                // Haurem de fer dues passades, una per determinar el maxI, minI
                // i factor d'escala i l'altre per
                // llegir totes les intensitats i aplicar el factor d'escala per
                // encabir-ho a short.
                for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila
                                                             // (Y)
                    for (int j = 0; j < patt2D.getDimX(); j++) { // per cada
                                                                 // columna (X)
                        in.read(buff);
                        int valorLlegit = FileUtils.B2toInt(buff);
                        count = count + 1;
                        // nomes considerem valors superiors a zero pel minI
                        if (valorLlegit >= 0) {
                            if (valorLlegit > patt2D.getMaxI()) {
                                patt2D.setMaxI(valorLlegit);
                            }
                            if (valorLlegit < patt2D.getMinI()) {
                                patt2D.setMinI(valorLlegit);
                            }
                        }
                    }
                }

                // calculem el factor d'escala
                patt2D.setScale(patt2D.getMaxI() / (float)MainFrame.satur32); //els bin tenen maxim 32

                in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                           // buffer
                                                                           // lectura
                in.read(new byte[60]);

                // ara aplico factor escala i guardo on i com toca (incl�s els
                // valors -1 de mascara)
                for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila
                                                             // (Y)
                    for (int j = 0; j < patt2D.getDimX(); j++) { // per cada
                                                                 // columna (X)
                        in.read(buff);
                        patt2D.setInten(j, i, (int) (FileUtils.B2toInt(buff) / patt2D.getScale()));
                    }
                }
                // corregim maxI i minI
                patt2D.setMaxI(FastMath.round(patt2D.getMaxI() / patt2D.getScale()));
                patt2D.setMinI(FastMath.round(patt2D.getMinI() / patt2D.getScale()));
            }

            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return patt2D; // tot correcte
    }

    public static Pattern2D openBinaryFileOLD(File d2File){
            Pattern2D patt2D = null;
        try{
            long start = System.nanoTime(); //control temps
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff=new byte[2];
            byte[] buff4=new byte[4]; //real
            float scale=-1;
            int dimX,dimY,centrX,centrY;
            
//          !FITXER BIN (30/01/2013):
//          !- 4 valors:        Int*4    Int*4     Real*4  Int*4          Int*4
//          !  corresponents a: COLS(X)  FILES(Y)  ESCALA  COLCENTRE(XC)  FILACENTRE(YC)
//          !- Llista Int*2 (amb signe.. -32,768 to 32,767) amb ordre files-columnes, es a dir,
//          !  l'index rapid es el de les columnes (columnes,files): (1,1) (2,1) (3,1) ...
            in.read(buff4);
            ByteBuffer bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimX=bb.getInt();
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            dimY=bb.getInt();
            in.read(buff4);
            scale=FileUtils.B4toFloat(buff4);
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            centrX=bb.getInt();
            in.read(buff4);
            bb = ByteBuffer.wrap(buff4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            centrY=bb.getInt();

            patt2D = new Pattern2D(dimX, dimY, centrX, centrY, 0, 999999999, scale, false);
            int count = 0;
            
            //el short arriba a 32,767... per tant maxI/scale=32767
            //si s'ha especificat al fitxer un factor d'escala l'utilitzem, ja que vol dir que son I2(signed)
            //sino el calcularem i l'aplicarem, ja que podria ser que fosin unsigned i superesin el limit
            if(patt2D.getScale()>0){
                //utilitzem l'escala, es llegeix directe
                for (int i=0;i<patt2D.getDimY();i++){ //per cada fila (Y)
                    for (int j=0;j<patt2D.getDimX();j++){  //per cada columna (X)
                        in.read(buff);
                        patt2D.setInten(j,i,(int)((float)FileUtils.B2toInt(buff)));
                        count = count+1;
                        //nomes considerem valors superiors a zero pel minI
                        if(patt2D.getInten(j,i)>=0){
                            if(patt2D.getInten(j,i)>patt2D.getMaxI()){patt2D.setMaxI(patt2D.getInten(j,i));}
                            if(patt2D.getInten(j,i)<patt2D.getMinI()){patt2D.setMinI(patt2D.getInten(j,i));}
                        }
                    }
                }   
            }else{
                //Haurem de fer dues passades, una per determinar el maxI, minI i factor d'escala i l'altre per
                //llegir totes les intensitats i aplicar el factor d'escala per encabir-ho a short.
                for (int i=0;i<patt2D.getDimY();i++){ //per cada fila (Y)
                    for (int j=0;j<patt2D.getDimX();j++){  //per cada columna (X)
                        in.read(buff);
                        int valorLlegit=FileUtils.B2toInt(buff);
                        count = count+1;
                        //nomes considerem valors superiors a zero pel minI
                        if(valorLlegit>=0){
                            if(valorLlegit>patt2D.getMaxI()){patt2D.setMaxI(valorLlegit);}
                            if(valorLlegit<patt2D.getMinI()){patt2D.setMinI(valorLlegit);}    
                        }
                    }
                }     
                
                //calculem el factor d'escala
                patt2D.setScale((float)patt2D.getMaxI()/(float)MainFrame.satur32);    

                in = new BufferedInputStream(new FileInputStream(d2File)); //reiniciem buffer lectura
                in.read(buff); //dimx
                in.read(buff); //dimy
                in.read(buff4); //scale
                in.read(buff); //cenx
                in.read(buff); //ceny
                
                //ara aplico factor escala i guardo on i com toca (incl�s els valors -1 de mascara)
                for (int i=0;i<patt2D.getDimY();i++){ //per cada fila (Y)
                    for (int j=0;j<patt2D.getDimX();j++){  //per cada columna (X)
                        in.read(buff);
                        patt2D.setInten(j,i,(int)((float)FileUtils.B2toInt(buff)/patt2D.getScale()));
                    }
                }   
                //corregim maxI i minI
                patt2D.setMaxI((int)((float)patt2D.getMaxI()/patt2D.getScale()));
                patt2D.setMinI((int)((float)patt2D.getMinI()/patt2D.getScale()));
            }
                
            
            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return patt2D; //tot correcte
    }
    
    public static Pattern2D openGFRMfile(File d2File) {
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
            Scanner scD2file = new Scanner(d2File);
            String line = scD2file.nextLine();
            VavaLogger.LOG.info(line);
            // treurem la informaci� d'aquesta linia.
            // 0 1 2 3 4 5 6 7 8
            String[] llista = { "HDRBLKS:", "NROWS  :", "NCOLS  :", "CENTER :", "DISTANC:", "NOVERFL:", "MINIMUM:",
                    "MAXIMUM:", "NPIXELB:" };
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
                        nbytePerPixel = Integer.parseInt(item.toString().trim());
                }
                scLine.close();
            }

            if (nbytePerPixel > 2) {
                JOptionPane.showMessageDialog(null, "format not supported (more than 2byte per pixel)",
                        "GFRM file error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[nbytePerPixel];
            byte[] header = new byte[headerSize * 512];
            patt2D = new Pattern2D(dimX, dimY, centrX, centrY, maxI, minI, -1, true);
            int count = 0;
            in.read(header);

            // si la intensitat m�xima supera el short escalem
            patt2D.setScale(FastMath.max(maxI / (float)MainFrame.satur65, 1.000f));

            // llegim els bytes
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    ByteBuffer buffer = ByteBuffer.wrap(buff);
                    buffer.order(ByteOrder.LITTLE_ENDIAN); // if you want
                                                           // little-endian
                    int valorLlegit = 0;
                    switch (nbytePerPixel) {
                        case 1:
                            // valorLlegit= buffer.get();
                            valorLlegit = FileUtils.B1UnsigtoInt(buff[0]);
                            break;
                        case 2:
                            valorLlegit = FileUtils.B2toInt(buff);
                            break;
                    // case 4:
                    // valorLlegit=buffer.getInt();
                    // break;
                    }
                    patt2D.setInten(j, i, (int)(valorLlegit / patt2D.getScale()));
                    count = count + 1;
                }
            }

            // llegim els overflow
            /*
             * The overflow table is stored as ASCII values. Each overflow entry is 16 characters, comprising a
             * 9-character intensity and a 7-character pixel # offset. The table is padded to a multiple of 512 bytes.
             * In an 8-bit frame, any pixel with a value of 255 in the image needs to be looked up in the overflow table
             * to determine its true value (even if the true value is 255, to allow overflow table validity checks,
             * which could not otherwise be made). In a similar manner, any pixel in a 16-bit frame with a value of
             * 65535 must be looked up in the overflow table to determine its true value. To look up a pixel value,
             * compute its pixel displacement (for example, in a 512x512 frame, 512*j + k, where j is the zero-based row
             * number and k is the zero-based column number), and compare the displacement with that of each overflow
             * table entry until a match is found. While the overflow table is normally sorted on displacement, it is
             * not guaranteed to be sorted, so we recommend that you search the whole table until you find a match.
             */
            if (noverflow > 0) {
                for (int i = 0; i < noverflow; i++) {
                    byte[] buffchar = new byte[16];
                    in.read(buffchar);

                    StringBuffer sb = new StringBuffer(10);
                    for (int j = 0; j < 9; j++) {
                        int c = FileUtils.B1UnsigtoInt(buffchar[j]);
                        sb.append((char) c);
                    }
                    int intensity = Integer.parseInt(sb.toString().trim());

                    sb = new StringBuffer(8);
                    for (int j = 9; j < 16; j++) {
                        int c = FileUtils.B1UnsigtoInt(buffchar[j]);
                        sb.append((char) c);
                    }
                    int offset = Integer.parseInt(sb.toString().trim());

                    // ara calculem quin pixel es
                    int fila = offset / dimX; // LA Y
                    int col = (offset % dimX); // +1 //LA X

                    patt2D.setInten(col, fila, (int) (intensity / patt2D.getScale()));
                }
            }
            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // parametres instrumentals
        patt2D.setDistMD(distOD);
        patt2D.setPixSx(0.105f); // per defecte gadds
        patt2D.setPixSy(0.105f);

        return patt2D; // correcte
    }

    public static Pattern2D openIMGfile(File d2File) {
        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps
        int headerSize = 0;
        float pixSize = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;

        try {
            Scanner scD2file = new Scanner(d2File);
            for (int i = 0; i < 50; i++) {
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
                    if (line.contains("HEADER_BYTES")) {
                        headerSize = Integer.parseInt(line.substring(13, line.trim().length() - 1).trim());
                    }
                    if (line.contains("SIZE1")) {
                        dimX = Integer.parseInt(line.substring(6, line.trim().length() - 1).trim());
                    }
                    if (line.contains("SIZE2")) {
                        dimY = Integer.parseInt(line.substring(6, line.trim().length() - 1).trim());
                    }
                    if (line.contains("BEAM_CENTER_X")) {
                        beamCX = Float.parseFloat(line.substring(14, line.trim().length() - 1).trim());
                    }
                    if (line.contains("BEAM_CENTER_Y")) {
                        beamCY = Float.parseFloat(line.substring(14, line.trim().length() - 1).trim());
                    }
                    if (line.contains("PIXEL_SIZE")) {
                        pixSize = Float.parseFloat(line.substring(11, line.trim().length() - 1).trim());
                    }
                    if (line.contains("DISTANCE")) {
                        distOD = Float.parseFloat(line.substring(9, line.trim().length() - 1).trim());
                    }
                    if (line.contains("WAVELENGTH")) {
                        wl = Float.parseFloat(line.substring(11, line.trim().length() - 1).trim());
                    }
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
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI, -1.0f, true);
            int count = 0;
            in.read(header);

            // Haurem de fer dues passades, una per determinar el maxI, minI i
            // factor d'escala i l'altre per
            // llegir totes les intensitats i aplicar el factor d'escala per
            // encabir-ho a short.
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    int valorLlegit = FileUtils.B2toInt(buff);
                    count = count + 1;
                    // nomes considerem valors superiors a zero pel minI
                    if (valorLlegit >= 0) {  //fem >= o > directament sense considerar els zeros??!
                        if (valorLlegit > patt2D.getMaxI()) {
                            patt2D.setMaxI(valorLlegit);
                        }
                        if (valorLlegit < patt2D.getMinI()) {
                            patt2D.setMinI(valorLlegit);
                        }
                    }
                }
            }

            // calculem el factor d'escala (valor maxim entre quocient i 1, mai
            // escalem per sobre)
            patt2D.setScale(FastMath.max(patt2D.getMaxI() / (float)MainFrame.satur65, 1.000f));

            in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                       // buffer
                                                                       // lectura
            in.read(header);

            // ara aplico factor escala i guardo on i com toca
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    patt2D.setInten(j, i, (int) (FileUtils.B2toInt(buff) / patt2D.getScale()));
                }
            }
            // corregim maxI i minI
            patt2D.setMaxI((int) (patt2D.getMaxI() / patt2D.getScale()));
            patt2D.setMinI((int) (patt2D.getMinI() / patt2D.getScale()));

            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // parametres instrumentals
        patt2D.setExpParam(pixSize, pixSize, distOD, wl);

        return patt2D; // correcte
    }

    public static Pattern2D openEDFfile(File d2File) {
        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps
        int headerSize = 0;
        int binSize = 0;
        float pixSize = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;

        //primer treiem la info de les linies de text
        try {
            Scanner scD2file = new Scanner(d2File);
            for (int i = 0; i < 50; i++) {
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
                    int iigual=line.indexOf("=")+1;
                    if (line.contains("Size =")) {
                        binSize = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Dim_1")) {
                        dimX = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Dim_2")) {
                        dimY = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("beam_center_x")) {
                        beamCX = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("beam_center_y")) {
                        beamCY = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("pixel_size_x") || line.contains("pixelsize_x")) {
                        pixSize = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                        pixSize = pixSize/1000.f;
                    }
                    if (line.contains("ref_distance")) {
                        distOD = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("ref_wave")) {
                        wl = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                }
            }
            headerSize = (int) (d2File.length()-binSize);
            
            VavaLogger.LOG.config("EDF header size (bytes)="+headerSize);
            VavaLogger.writeNameNumPairs("CONFIG", true, "dimX,dimY,beamCX,beamCY,pixSize,distOD,wl", dimX,dimY,beamCX,beamCY,pixSize,distOD,wl);
            VavaLogger.writeNameNumPairs("CONFIG",true, "binsize,d2fileLength",binSize,d2File.length());
            // calculem el pixel central
//            beamCX = beamCX / pixSize;
//            beamCY = beamCY / pixSize;

            scD2file.close();

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[2];
            byte[] header = new byte[headerSize];
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI, -1.0f, true);
            int count = 0;
            in.read(header);

            // Haurem de fer dues passades, una per determinar el maxI, minI i
            // factor d'escala i l'altre per
            // llegir totes les intensitats i aplicar el factor d'escala per
            // encabir-ho a short.
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    int valorLlegit = FileUtils.B2toInt(buff);
                    count = count + 1;
                    // nomes considerem valors superiors a zero pel minI
                    if (valorLlegit >= 0) {  //fem >= o > directament sense considerar els zeros??!
                        if (valorLlegit > patt2D.getMaxI()) {
                            patt2D.setMaxI(valorLlegit);
                        }
                        if (valorLlegit < patt2D.getMinI()) {
                            patt2D.setMinI(valorLlegit);
                        }
                    }
                }
            }
            in.close();
            
            // calculem el factor d'escala (valor maxim entre quocient i 1, mai
            // escalem per sobre)
            patt2D.setScale(FastMath.max(patt2D.getMaxI() / (float)MainFrame.satur65, 1.000f));
            
            in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                       // buffer
                                                                       // lectura
            in.read(header);

            // ara aplico factor escala i guardo on i com toca
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    patt2D.setInten(j, i, (int) (FileUtils.B2toInt(buff) / patt2D.getScale()));
                }
            }
            // corregim maxI i minI
            patt2D.setMaxI((int) (patt2D.getMaxI() / patt2D.getScale()));
            patt2D.setMinI((int) (patt2D.getMinI() / patt2D.getScale()));

            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // parametres instrumentals
        patt2D.setExpParam(pixSize, pixSize, distOD, wl);

        return patt2D; // correcte
    }
    
    public static Pattern2D readD2D(File d2File) {
        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps
        int headerSize = 0;
        int binSize = 0;
        float pixSize = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;
        float tilt = 0, rot = 0;
        int margin = 0, thresh =0;
        ArrayList<PolyExZone> exzones = new ArrayList<PolyExZone>(); 

        //primer treiem la info de les linies de text
        try {
            Scanner scD2file = new Scanner(d2File);
            for (int i = 0; i < 50; i++) {
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
                    int iigual=line.indexOf("=")+1;;
                    if (line.contains("Size =")) {
                        binSize = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Dim_1")) {
                        dimX = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Dim_2")) {
                        dimY = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Beam_center_x")) {
                        beamCX = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Beam_center_y")) {
                        beamCY = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Pixel_size_x") || line.contains("pixelsize_x")) {
                        pixSize = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                        pixSize = pixSize/1000.f;
                    }
                    if (line.contains("Ref_distance")) {
                        distOD = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Ref_wave")) {
                        wl = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Det_tiltDeg")) {
                        tilt = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("Det_rotDeg")) {
                        rot = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("EXZmarg")) {
                        margin = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("EXZthres")) {
                        thresh = Integer.parseInt(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if(line.trim().startsWith("EXZpol")){
                        String linia = line.substring(iigual, line.trim().length()).trim();
                        String[] values = linia.split("\\s+");
                        PolyExZone z = new PolyExZone(false);
                        for (int j = 0; j < values.length; j=j+2) {
                            //parelles x1 y1 x2 y2 .... que son els vertexs
                            z.addPoint(Integer.parseInt(values[j]), Integer.parseInt(values[j+1]));
                        }
                        exzones.add(z);
                    }
                }
            }
            
            headerSize = (int) (d2File.length()-binSize);
            
            VavaLogger.LOG.config("D2D header size (bytes)="+headerSize);
            
            // calculem el pixel central
//            beamCX = beamCX / pixSize;
//            beamCY = beamCY / pixSize;

            scD2file.close();

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[2];
            byte[] header = new byte[headerSize];
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI, -1.0f, true);
            //info especifica d2d format
            patt2D.setTiltDeg(tilt);
            patt2D.setRotDeg(rot);
            patt2D.setExZones(exzones);
            
            int count = 0;
            in.read(header);

            // Haurem de fer dues passades, una per determinar el maxI, minI i
            // factor d'escala i l'altre per
            // llegir totes les intensitats i aplicar el factor d'escala per
            // encabir-ho a short.
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    int valorLlegit = FileUtils.B2toInt(buff);
                    count = count + 1;
                    // nomes considerem valors superiors a zero pel minI
                    if (valorLlegit >= 0) {  //fem >= o > directament sense considerar els zeros??!
                        if (valorLlegit > patt2D.getMaxI()) {
                            patt2D.setMaxI(valorLlegit);
                        }
                        if (valorLlegit < patt2D.getMinI()) {
                            patt2D.setMinI(valorLlegit);
                        }
                    }
                }
            }
            in.close();
            
            // calculem el factor d'escala (valor maxim entre quocient i 1, mai
            // escalem per sobre)
            patt2D.setScale(FastMath.max(patt2D.getMaxI() / (float)MainFrame.satur65, 1.000f));
            
            in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                       // buffer
                                                                       // lectura
            in.read(header);

            // ara aplico factor escala i guardo on i com toca
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    patt2D.setInten(j, i, (int) (FileUtils.B2toInt(buff) / patt2D.getScale()));
                }
            }
            // corregim maxI i minI
            patt2D.setMaxI((int) (patt2D.getMaxI() / patt2D.getScale()));
            patt2D.setMinI((int) (patt2D.getMinI() / patt2D.getScale()));

            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // parametres instrumentals
        patt2D.setExpParam(pixSize, pixSize, distOD, wl);

        return patt2D; // correcte
    }
    
    // OBERTURA DELS DIFERENTS FORMATS DE DADES2D
    public static Pattern2D openPatternFile(File d2File) {
        Pattern2D patt2D = null;
        // comprovem extensio
        VavaLogger.LOG.info(d2File.toString());
        String ext = FileUtils.getExtension(d2File).trim();
        if (!ext.equalsIgnoreCase("bin") && !ext.equalsIgnoreCase("img") && !ext.equalsIgnoreCase("spr")
                && !ext.equalsIgnoreCase("gfrm")  && !ext.equalsIgnoreCase("edf")) {
            Object[] possibilities = { "BIN", "IMG", "SPR", "GFRM", "EDF" };
            String s = (String) JOptionPane.showInputDialog(null, "Input format:", "Open File",
                    JOptionPane.PLAIN_MESSAGE, null, possibilities, "BIN");
            if (s == null || s.length() < 3) {
                return null;
            }
            ext = s;
        }

        if (ext.equalsIgnoreCase("BIN")) {
            if(isNewBIN(d2File)){
                patt2D = ImgFileUtils.openBinaryFile(d2File);    
            }else{
                patt2D = ImgFileUtils.openBinaryFileOLD(d2File);
                patt2D.oldBIN=true;
            }
        }
        if (ext.equalsIgnoreCase("IMG")) {
            patt2D = ImgFileUtils.openIMGfile(d2File);
        }
        if (ext.equalsIgnoreCase("SPR")) {
            patt2D = ImgFileUtils.openSPRfile(d2File);
        }
        if (ext.equalsIgnoreCase("GFRM")) {
            patt2D = ImgFileUtils.openGFRMfile(d2File);
        }
        if (ext.equalsIgnoreCase("EDF")) {
            patt2D = ImgFileUtils.openEDFfile(d2File);
        }

        //operacions generals despres d'obrir
        patt2D.calcMeanI();
        patt2D.setImgfile(d2File);
        ImgFileUtils.readEXZ(patt2D,null);

        //debug:
        VavaLogger.LOG.info("meanI= "+patt2D.getMeanI());
        VavaLogger.LOG.info("sdevI= "+patt2D.getSdevI());
        
        return patt2D;
    }

    public static Pattern2D openSPRfile(File d2File) {
        Pattern2D patt2D = null;
        int dimX = 0, dimY = 0;
        try {
            long start = System.nanoTime();
            Scanner scD2file = new Scanner(d2File);
            // 1a linia NCOL NFILES ...
            dimX = Integer.parseInt(scD2file.next().trim()); // num columnes
            dimY = Integer.parseInt(scD2file.next().trim()); // num files
            scD2file.nextLine();
            int count = 0;

            patt2D = new Pattern2D(dimX, dimY, -1.0f, -1.0f, 0, 999999999, -1.0f, true);

            // el short arriba a 32,767... per tant maxI/scale=32767
            // si s'ha especificat al fitxer un factor d'escala l'utilitzem, ja
            // que vol dir que son I2(signed)
            // sino el calcularem i l'aplicarem, ja que podria ser que fosin
            // unsigned i superesin el limit
            // Haurem de fer dues passades, una per determinar el maxI, minI i
            // factor d'escala i l'altre per
            // llegir totes les intensitats i aplicar el factor d'escala per
            // encabir-ho a short.
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    String r = scD2file.next();
                    count = count + 1;
                    int in = new java.math.BigDecimal(r).intValue();
                    if (in > patt2D.getMaxI()) {
                        patt2D.setMaxI(in);
                    }
                    if (in < patt2D.getMinI()) {
                        patt2D.setMinI(in);
                    }
                }
            }
            // calculem el factor d'escala
            patt2D.setScale(FastMath.max(patt2D.getMaxI() / (float)MainFrame.satur65, 1.000f));

            scD2file = new Scanner(d2File);
            scD2file.next();// x
            scD2file.next();// y
            scD2file.nextLine(); // salta linia

            // ara aplico factor escala i guardo on i com toca (incl�s els
            // valors -1 de mascara)
            // EDIT130417: pinto amb la Y invertida directament
            for (int i = patt2D.getDimY() - 1; i >= 0; i--) { // per cada fila
                                                              // (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    String r = scD2file.next();
                    int in = new java.math.BigDecimal(r).intValue();
                    patt2D.setInten(j, i, (int) (in / patt2D.getScale()));
                }
            }
            // corregim maxI i minI
            patt2D.setMaxI((int) (patt2D.getMaxI() / patt2D.getScale()));
            patt2D.setMinI((int) (patt2D.getMinI() / patt2D.getScale()));

            scD2file.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return patt2D; // tot correcte
    }

    /*
     * QUE HAURIEM DE FER.
     * - Com que a l'obrir un fitxer probablement s'haur� escalat, haur�em de tornar
     *   a llegir les intensitats del fitxer original considerant les zones excloses
     *   per tenir els maxI i minI correctes. Aleshores escalar i guardar el BIN.
     *   Potser millor que fem un m�tode nou.
     *   -- hem fet que al llegir img es tinguin en compte zones excloses, rellegint-lo
     *      abans de guardar el bin ja n'hi ha prou.
     *      No estaria de mes per� fer un m�tode que recalcul�s l'escala. (a Pattern2D)
     */
    public static File writeBIN(File d2File, Pattern2D patt2D) {
        // Forcem extensio bin
        d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".bin"));

        int dataHeaderBytes = 36; // bytes de dades en el header
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

            //AQUI HEM DE COMPROVAR L'ESCALA EN CAS QUE HAGUEM DE PASSAR DE INT A SHORT
            if(patt2D.isB4inten()){
                float scI2 = patt2D.calcScaleI2();
                bb = ByteBuffer.allocate(4);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                bb.putFloat(scI2);
                output.write(bb.array());
                bb.clear();  
                
            }else{
                bb = ByteBuffer.allocate(4);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                bb.putFloat(patt2D.getScale());
                output.write(bb.array());
                bb.clear();    
            }
            
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

            output.write(new byte[60 - dataHeaderBytes]);

            // creem imatge normal
            for (int i = 0; i < patt2D.getDimY(); i++) {
                for (int j = 0; j < patt2D.getDimX(); j++) {
                    bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    if (patt2D.isInExZone(j, i)) {
                        bb.putShort((short) -1);
                    } else {
                        if((patt2D.getY0toMask()==1)&&(patt2D.getInten(j, i)==0)){
                            bb.putShort((short) -1);
                        }else{
                            bb.putShort((short)patt2D.getInten(j, i));
                        }
                    }
                    output.write(bb.array());
                    bb.clear();
                }
            }

            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            VavaLogger.LOG.info("(FileUtils)Error saving BIN file");
            return null;
        }
        return d2File;
    }

    //DATA UNSIGNED SHORT
    public static File writeEDF(File d2File, Pattern2D patt2D){
        // Forcem extnsio edf
        d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".edf"));
        
        int binSize = patt2D.getDimX()*patt2D.getDimY()*2; //considerant 2 bytes per pixel
        
        try{
            
            //ESCRIBIM PRIMER LA PART ASCII
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(d2File)));
            // ESCRIBIM AL FITXER:
            output.println("{");
            output.println("HeaderID = EH:000001:000000:000000 ;");
            output.println("ByteOrder = LowByteFirst ;");
            output.println("DataType = UnsignedShort ;");
            output.println("Size = "+binSize+" ;");
            output.println("Dim_1 = "+patt2D.getDimX()+" ;");
            output.println("Dim_2 = "+patt2D.getDimY()+" ;");
            output.println("beam_center_x = "+FileUtils.dfX_2.format(patt2D.getCentrX())+" ;");
            output.println("beam_center_y = "+FileUtils.dfX_2.format(patt2D.getCentrY())+" ;");
            output.println("pixelsize_x = "+FileUtils.dfX_2.format(patt2D.getPixSx()*1000)+" ;");
            output.println("pixelsize_y = "+FileUtils.dfX_2.format(patt2D.getPixSy()*1000)+" ;");
            output.println("ref_distance = "+FileUtils.dfX_2.format(patt2D.getDistMD())+" ;");
            output.println("ref_wave = "+FileUtils.dfX_4.format(patt2D.getWavel())+" ;");
            output.println("}");
            output.close();
            
            //ara mirem quants bytes hem escrit...
            int headerbytes = (int)d2File.length();
            VavaLogger.LOG.config("Write EDF headerbytes="+headerbytes);
            
            OutputStream os = new BufferedOutputStream(new FileOutputStream(d2File,true));
//            byte[] buff = new byte[2];
            
            //escribim imatge tal cual
            for (int i = 0; i < patt2D.getDimY(); i++) {
                for (int j = 0; j < patt2D.getDimX(); j++) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    if (patt2D.getInten(j, i)>0){
                        bb.putChar((char) patt2D.getInten(j, i));    
                    }else{
                        bb.putChar((char) 0);
                    }
                    os.write(bb.array());
                    bb.clear();
                }
            }
            
            os.close();
            
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return d2File;
        
    }
    
    //DATA UNSIGNED SHORT
    public static File writeIMG(File d2File, Pattern2D patt2D){
        // Forcem extnsio edf
        d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".img"));
        int headerBytes = 512;
        try{
            
            //ESCRIBIM PRIMER LA PART ASCII
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(d2File)));
            // ESCRIBIM AL FITXER:
            output.println("{");
            output.println("HEADER_BYTES= "+headerBytes+";");
            output.println("TYPE=unsigned_short ;");
            output.println("BYTE_ORDER=little_endian;");
            output.println("SIZE1="+patt2D.getDimX()+";");
            output.println("SIZE2="+patt2D.getDimY()+";");
            output.println("DISTANCE= "+FileUtils.dfX_3.format(patt2D.getDistMD())+" ;");
            output.println("PIXEL_SIZE= "+FileUtils.dfX_6.format(patt2D.getPixSx())+" ;");
            output.println("WAVELENGTH="+FileUtils.dfX_6.format(patt2D.getWavel())+";");
            output.println("BEAM_CENTER_X="+FileUtils.dfX_2.format(patt2D.getCentrX()*patt2D.getPixSx())+";");
            output.println("BEAM_CENTER_Y="+FileUtils.dfX_2.format(patt2D.getCentrY()*patt2D.getPixSy())+";");
            output.println("}");
            output.close();
            
            //ara mirem quants bytes hem escrit...
            int writtenHeaderBytes = (int)d2File.length();
            VavaLogger.LOG.config("Write IMG headerbytes="+writtenHeaderBytes);
            
            OutputStream os = new BufferedOutputStream(new FileOutputStream(d2File,true));
            
            //cal escriure fins a 512
            os.write(new byte[headerBytes - writtenHeaderBytes]);

            //ara ja escribim imatge tal cual
            for (int i = 0; i < patt2D.getDimY(); i++) {
                for (int j = 0; j < patt2D.getDimX(); j++) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    bb.putChar((char) patt2D.getInten(j, i));
                    os.write(bb.array());
                    bb.clear();
                }
            }
            
            os.close();
            
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return d2File;
    }
    
    //DATA UNSIGNED SHORT AMB CAPÇALERA QUE CONTE EXZ, FORMAT PROPI DEL PROGRAMA
    public static File writeD2D(File d2File, Pattern2D patt2D){
        // Forcem extnsio edf
        d2File = new File(FileUtils.getFNameNoExt(d2File).concat(".d2d"));
        
        int binSize = patt2D.getDimX()*patt2D.getDimY()*2; //considerant 2 bytes per pixel
        
        try{
            
            //ESCRIBIM PRIMER LA PART ASCII
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(d2File)));
            // ESCRIBIM AL FITXER:
            output.println("{");
            output.println("ByteOrder = LowByteFirst ;");
            output.println("DataType = UnsignedShort ;");
            output.println("Size = "+binSize+" ;");
            output.println("Dim_1 = "+patt2D.getDimX()+" ;");
            output.println("Dim_2 = "+patt2D.getDimY()+" ;");
            output.println("Beam_center_x = "+FileUtils.dfX_2.format(patt2D.getCentrX())+" ;");
            output.println("Beam_center_y = "+FileUtils.dfX_2.format(patt2D.getCentrY())+" ;");
            output.println("Pixelsize_x = "+FileUtils.dfX_2.format(patt2D.getPixSx()*1000)+" ;");
            output.println("Pixelsize_y = "+FileUtils.dfX_2.format(patt2D.getPixSy()*1000)+" ;");
            output.println("Ref_distance = "+FileUtils.dfX_2.format(patt2D.getDistMD())+" ;");
            output.println("Ref_wave = "+FileUtils.dfX_4.format(patt2D.getWavel())+" ;");
            output.println("Det_tiltDeg = "+FileUtils.dfX_3.format(patt2D.getTiltDeg())+" ;");
            output.println("Det_rotDeg = "+FileUtils.dfX_3.format(patt2D.getRotDeg())+" ;");
            output.println("EXZMargin="+ patt2D.getMargin());
            output.println("EXZThreshold="+ patt2D.getY0toMask());
            int polcount = 1;
            Iterator<PolyExZone> it = patt2D.getExZones().iterator();
            while (it.hasNext()) {
                PolyExZone p = it.next();
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<p.npoints;i++){
                    sb.append(p.getXYvertex(i)).append(" ");
                }
                output.println("EXZpol"+polcount+"=" + sb.toString().trim());
                polcount = polcount +1;
            }
            output.println("}");
            output.close();
            
            //ara mirem quants bytes hem escrit...
            int headerbytes = (int)d2File.length();
            VavaLogger.LOG.config("Write D2D headerbytes="+headerbytes);
            
            OutputStream os = new BufferedOutputStream(new FileOutputStream(d2File,true));
//            byte[] buff = new byte[2];
            
            //escribim imatge tal cual
            for (int i = 0; i < patt2D.getDimY(); i++) {
                for (int j = 0; j < patt2D.getDimX(); j++) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    bb.putChar((char) patt2D.getInten(j, i));
                    os.write(bb.array());
                    bb.clear();
                }
            }
            
            os.close();
            
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return d2File;
        
    }
    
    public static File savePNG(File f, BufferedImage i) {
        // forcem extensio PNG
        f = new File(FileUtils.getFNameNoExt(f).concat(".png"));

        if (i != null) {
            try {
                ImageIO.write(i, "png", f);
                return f;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    private static boolean isNewBIN(File d2File){
        //primer mirem la cap�alera
        int dimX=0, dimY=0;
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
            e1.printStackTrace();
        }
        
        //File size (bytes):
        //NX*NY*2 + 20 = old
        //NX*NY*2 + 60 = new
        int limit = dimX*dimY*2 + 20;
        InputStream stream = null;
        int bytes=0;
        try {
            URL url = d2File.toURI().toURL();
            stream = url.openStream();
            bytes = stream.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bytes>limit){
            VavaLogger.LOG.info(bytes+" bytes --> format NOU");
            return true; 
        }else{
            VavaLogger.LOG.info(bytes+" bytes --> format ANTIC");
            return false;
        }
        
    }
    
    public static boolean rescale(Pattern2D patt2D, File d2File){
          if (FileUtils.getExtension(d2File).equalsIgnoreCase("img")){
              //rellegim les intensitats del fitxer IMG considerant les zones excloses
              int headerSize=0;
              try {
                  Scanner scD2file = new Scanner(d2File);
                  for (int i = 0; i < 50; i++) {
                      if (scD2file.hasNextLine()) {
                          String line = scD2file.nextLine();
                          if (line.contains("HEADER_BYTES")) {
                              headerSize = Integer.parseInt(line.substring(13, line.trim().length() - 1).trim());
                          }
                      }
                  }

                  scD2file.close();

                  // ARA LLEGIREM ELS BYTES
                  InputStream in = new BufferedInputStream(new FileInputStream(d2File));
                  byte[] buff = new byte[2];
                  byte[] header = new byte[headerSize];
                  int maxI=0;
                  int minI=99999999;
                  in.read(header);
                  // Haurem de fer dues passades, una per determinar el maxI, minI i
                  // factor d'escala i l'altre per
                  // llegir totes les intensitats i aplicar el factor d'escala per
                  // encabir-ho a short.
                  for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                      for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                                   // (X)
                          in.read(buff);
                          //si esta en zona exclosa el saltem
                          if(patt2D.isInExZone(j, i))continue;
                          int valorLlegit = FileUtils.B2toInt(buff);
                          if (valorLlegit >= 0) {  //fem >= o > directament sense considerar els zeros??!
                              if (valorLlegit > patt2D.getMaxI()) {
                                  patt2D.setMaxI(valorLlegit);
                              }
                              if (valorLlegit < patt2D.getMinI()) {
                                  patt2D.setMinI(valorLlegit);
                              }
                          }
                      }
                  }

                  // calculem el factor d'escala (valor maxim entre quocient i 1, mai
                  // escalem per sobre)
                  patt2D.setScale(FastMath.max(patt2D.getMaxI() / (float)MainFrame.satur32, 1.000f));

                  in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                             // buffer
                                                                             // lectura
                  in.read(header);

                  // ara aplico factor escala i guardo on i com toca
                  for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                      for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                                   // (X)
                          in.read(buff);
                          if(patt2D.isInExZone(j, i)){
                              patt2D.setInten(j, i, -1);
                              continue;
                          }
                          patt2D.setInten(j, i, (int) (FileUtils.B2toInt(buff) / patt2D.getScale()));
                      }
                  }
                  // corregim maxI i minI
                  patt2D.setMaxI((int) (patt2D.getMaxI() / patt2D.getScale()));
                  patt2D.setMinI((int) (patt2D.getMinI() / patt2D.getScale()));

                  in.close();
              } catch (Exception e) {
                  e.printStackTrace();
                  return false;
              }
          }else{
              //no es img, reescalem igualment (mai m�s intensitat que la original)
              patt2D.recalcScale();
          }
          
          return true; // correcte
          
    }
    
    public static File writeEXZ(File exfile, Pattern2D patt2D) {
        //forcem extensio EXZ
        exfile = new File(FileUtils.getFNameNoExt(exfile).concat(".EXZ"));
        
        if(exfile.exists()){
            //avisem que es sobreescriur�
            Object[] options = {"Yes","No"};
            int n = JOptionPane.showOptionDialog(null,
                    "EXZ file will be overwritten. Continue?",
                    "File exists",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    options, //the titles of buttons
                    options[1]); //default button title
            if (n!=0){return null;} // si s'ha cancelat o tancat la finestra no es segueix salvant 
        }
        // creem un printwriter amb el fitxer file (el que estem escribint)
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(exfile)));
            // ESCRIBIM AL FITXER:
            output.println("! Excluded zones file for: " + patt2D.getImgfileString());
            output.println("EXZmargin="+ patt2D.getMargin());
            output.println("EXZthreshold="+ patt2D.getY0toMask());
            int polcount = 1;
            Iterator<PolyExZone> it = patt2D.getExZones().iterator();
            while (it.hasNext()) {
                PolyExZone p = it.next();
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<p.npoints;i++){
                    sb.append(p.getXYvertex(i)).append(" ");
                }
                output.println("EXZpol"+polcount+"=" + sb.toString().trim());
                polcount = polcount +1;
            }
            
            //COMENTARIS INFO
            output.println("!");
            output.println("! EXZmargin     Margin of the image in pixels (if any)");
            output.println("! EXZthreshold  Pixels with Y<threshold will be excluded");
            output.println("! EXZpol#       Sequence of pixels (X1 Y1 X2 Y2 X3 Y3...) defining a polygonal shape to be");
            output.println("!               considered as excluded zone for the image");
            
            output.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return exfile;
    }
    
    //if exffile==null es mira el nomb del patt2d
    public static boolean readEXZ(Pattern2D patt2D, File exfile) {
    	File dataFile = patt2D.getImgfile();
    	if (exfile == null){
            exfile = new File(FileUtils.getFNameNoExt(dataFile).concat(".exz"));
            if (!exfile.exists()) {
                exfile = new File(FileUtils.getFNameNoExt(dataFile).concat(".EXZ"));
                if (!exfile.exists())
                    return false;
            }
    	}
        if (!exfile.exists())
            return false;
        
        // aqui hauriem de tenir exfile ben assignada, la llegim
        String line;
        try {
            Scanner scExFile = new Scanner(exfile);
            boolean llegint = true;
            
            while(llegint){
                if (scExFile.hasNextLine()) {
                    line = scExFile.nextLine();
                } else {
                    scExFile.close();
                    llegint=false;
                    continue;
                }
                
                if(line.startsWith("!")){
                    continue;
                }
                
                int iigual=line.indexOf("=")+1;
                
                if(line.trim().startsWith("EXZmarg")){
                    patt2D.setMargin(Integer.parseInt(line.substring(iigual, line.trim().length()).trim()));
                    continue;
                }
                if(line.trim().startsWith("EXZthres")){
                    int yth = Integer.parseInt(line.substring(iigual, line.trim().length()).trim());
                    patt2D.setY0toMask(yth);
                    continue;
                }
                if(line.trim().startsWith("EXZpol")){
                    String linia = line.substring(iigual, line.trim().length()).trim();
                    String[] values = linia.split("\\s+");
                    PolyExZone z = new PolyExZone(false);
                    for (int i = 0; i < values.length; i=i+2) {
                        //parelles x1 y1 x2 y2 .... que son els vertexs
                        z.addPoint(Integer.parseInt(values[i]), Integer.parseInt(values[i+1]));
                    }
                    patt2D.addExZone(z);
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    public static File openXDS(File xdsFile) {
        // list of: x,y,z,Intensity,(iseg),h,k,l
        // (z is the image number where the centroid of the reflection is... it is FLOAT, does not correspond to exact an image)
        try {
            OrientSolucio.setNumSolucions(1); // num de solucions
            OrientSolucio.setHasFc(0); // 0 sense Fc, 1 amb Fc
            OrientSolucio.setGrainIdent(0);

            MainFrame.getPatt2D().getSolucions().add(new OrientSolucio(0)); // afegim una solucio
            MainFrame.getPatt2D().getSolucions().get(0).setGrainNr(0);
            MainFrame.getPatt2D().getSolucions().get(0).setValorFrot(0.0f); // valor funcio rotacio

            int npunts = 0;
            
            Scanner scSolfile = new Scanner(xdsFile);
            while(scSolfile.hasNextLine()){
                String line = scSolfile.nextLine();
                if (line.isEmpty()) continue;
                npunts = npunts + 1;
                VavaLogger.LOG.info("xdsfileline= "+line);
                String lineS[] = line.trim().split("\\s+");
                MainFrame.getPatt2D()
                        .getSolucions()
                        .get(0)
                        .addSolPoint(Float.parseFloat(lineS[0]), Float.parseFloat(lineS[1]),
                                Integer.parseInt(lineS[4]), Integer.parseInt(lineS[5]),
                                Integer.parseInt(lineS[6]), 1.0f,
                                Float.parseFloat(lineS[2]));
                

            }
            scSolfile.close();
            MainFrame.getPatt2D().getSolucions().get(0).setNumReflexions(npunts);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return xdsFile;
    }
    
    public static File openSol(File solFile) {

        String line;
        boolean endSol = false;

        try {
            Scanner scSolfile = new Scanner(solFile);
            scSolfile.nextLine(); // number of solutions
            OrientSolucio.setNumSolucions(scSolfile.nextInt()); // num de solucions
            scSolfile.nextLine();
            scSolfile.nextLine(); // structure factor calculation
            OrientSolucio.setHasFc(scSolfile.nextInt()); // 0 sense Fc, 1 amb Fc
            scSolfile.nextLine();
            scSolfile.nextLine(); // grain identificator
            OrientSolucio.setGrainIdent(scSolfile.nextInt());
            scSolfile.nextLine();
            scSolfile.nextLine(); // grain nr (cap�alera) comen�a el primer gra

            /*
             * En el cas que grain identificator sigui 0, hi ha #NumSolucions mostrant nom�s la solucio amb major Frot
             * (CENTRE). En cas que sigui 1, hi ha X solucions properes a la del gra seleccionat (indicat per grain
             * identificator). Les solucions estan etiquetades per la cap�alera ORIENT excepte la de major valor de Frot
             * que �s CENTRE.
             */

            if (OrientSolucio.getGrainIdent() == 0) {
                for (int i = 0; i < OrientSolucio.getNumSolucions(); i++) {
                    MainFrame.getPatt2D().getSolucions().add(new OrientSolucio(i)); // afegim una solucio
                    int npunts = 0;
                    endSol = false;
                    MainFrame.getPatt2D().getSolucions().get(i).setGrainNr(scSolfile.nextInt());
                    line = scSolfile.nextLine();
                    VavaLogger.LOG.info(scSolfile.nextLine());// CENTRE
//                    scSolfile.nextLine();// CENTRE
                    MainFrame.getPatt2D().getSolucions().get(i).setNumReflexions(scSolfile.nextInt());
                    line = scSolfile.nextLine();
                    MainFrame.getPatt2D().getSolucions().get(i).setValorFrot(Float.parseFloat(line)); // valor funcio rotacio
//                    VavaLogger.LOG.info(scSolfile.nextLine());
                    VavaLogger.LOG.info(MainFrame.getPatt2D().getSolucions().get(i).getNumReflexions()+" "+MainFrame.getPatt2D().getSolucions().get(i).getValorFrot());
                    VavaLogger.LOG.info(scSolfile.nextLine());// matriu Rot
                    VavaLogger.LOG.info(scSolfile.nextLine());// matriu Rot
                    VavaLogger.LOG.info(scSolfile.nextLine());// matriu Rot
//                    scSolfile.nextLine();// matriu Rot
//                    scSolfile.nextLine();// matriu Rot
//                    scSolfile.nextLine();// matriu Rot
                    // ara comencen les reflexions
                    while (!endSol) {
                        if (!scSolfile.hasNextLine()) {
                            endSol = true;
                            continue;
                        }
                        line = scSolfile.nextLine();
                        VavaLogger.LOG.info("bona "+line);
                        if (line.trim().isEmpty())
                            continue;
                        if (line.trim().startsWith("GRAIN")) {
                            endSol = true;
                            continue;
                        }
                        npunts = npunts + 1;
                        String[] lineS = line.trim().split("\\s+");
                        MainFrame.getPatt2D()
                                .getSolucions()
                                .get(i)
                                .addSolPoint(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]),
                                        Integer.parseInt(lineS[3]), Integer.parseInt(lineS[4]),
                                        Integer.parseInt(lineS[5]), Float.parseFloat(lineS[6]),
                                        Float.parseFloat(lineS[7]));
                    }
                    MainFrame.getPatt2D().getSolucions().get(i).setNumReflexions(npunts);
                }

            } else { // cas d'un sol gra

                int grainNr;
                for (int j = 0; j < OrientSolucio.getNumSolucions(); j++) {
                    grainNr = scSolfile.nextInt();
                    line = scSolfile.nextLine();
                    if (grainNr != OrientSolucio.getGrainIdent()) {
                        // no es el gra del que trobem orientacions properes, llegim seguent cap�alera i el saltem
                        if (scSolfile.hasNextLine()) {
                            scSolfile.nextLine();
                        } // cap�alera GRAIN NR.
                        continue;
                    }
                    // es el gra correcte
                    scSolfile.nextLine();// ORIENT o CENTRE (cap�alera)
                    int i = 0;
                    boolean endGrain = false;
                    while (!endGrain) {
                        MainFrame.getPatt2D().getSolucions().add(new OrientSolucio(i)); // afegim una solucio
                        MainFrame.getPatt2D().getSolucions().get(i).setGrainNr(grainNr);
                        line = scSolfile.nextLine();
                        String[] lineS = line.trim().split("\\s+");
                        // valor funcio rotacio. S'haura de canviar si es passa de int a float en futurs fitxers sol
                        MainFrame.getPatt2D().getSolucions().get(i).setValorFrot(Integer.parseInt(lineS[0]));
                        scSolfile.nextLine();// matriu Rot
                        scSolfile.nextLine();// matriu Rot
                        scSolfile.nextLine();// matriu Rot
                        // ara comencen les reflexions
                        endSol = false;
                        int npunts = 0;
                        while (!endSol) {
                            if (!scSolfile.hasNextLine()) {
                                endGrain = true;
                                endSol = true;
                                continue;
                            }
                            line = scSolfile.nextLine();
                            if (line.trim().isEmpty())
                                continue;
                            if (line.trim().startsWith("ORIENT") || line.trim().startsWith("CENTRE")) {
                                endSol = true;
                                continue;
                            }
                            if (line.trim().startsWith("GRAIN")) {
                                endGrain = true;
                                endSol = true;
                                continue;
                            }
                            npunts = npunts + 1;
                            lineS = line.trim().split("\\s+");
                            MainFrame.getPatt2D()
                                    .getSolucions()
                                    .get(i)
                                    .addSolPoint(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]),
                                            Integer.parseInt(lineS[3]), Integer.parseInt(lineS[4]),
                                            Integer.parseInt(lineS[5]), Float.parseFloat(lineS[6]),
                                            Float.parseFloat(lineS[7]));
                        }
                        MainFrame.getPatt2D().getSolucions().get(i).setNumReflexions(npunts);
                        i++;
                    }
                }
            }
            scSolfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return solFile;
    }    
    
    
//    public void reopenIMG(File d2dfile, Pattern2D patt2D){
//        //TODO
        
//        //fem que si es zona exclosa no el tingui en compte
//        if(patt2D.isInExZone(j,i))continue;
//        //fem que si es zona exclosa valdra -1
//        if(patt2D.isInExZone(j,i)){
//            patt2D.setIntenB2(j, i, (short)-1);
//            continue;
//        }
//    }
}
