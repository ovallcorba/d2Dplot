package vava33.plot2d.auxi;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public final class FileUtils {
    
    private static Locale currentlocale = Locale.getDefault();
    private static DecimalFormatSymbols mySymbols = new DecimalFormatSymbols(currentlocale);
    public static DecimalFormat dfX_1 = new DecimalFormat("#0.0");
    public static DecimalFormat dfX_2 = new DecimalFormat("#0.00");
    public static DecimalFormat dfX_3 = new DecimalFormat("#0.000");
    public static DecimalFormat dfX_4 = new DecimalFormat("#0.0000");
    public static DecimalFormat dfX_5 = new DecimalFormat("#0.00000");

    public static void setLocale(){
        //PER TEST:
//        System.out.println(mySymbols.getDecimalSeparator());
//        mySymbols.setDecimalSeparator('.');
//        System.out.println(mySymbols.getDecimalSeparator());
//        System.out.println(mySymbols.getGroupingSeparator());
//        System.out.println(dfX_3.format(12345678.009921));
//        dfX_3.setDecimalFormatSymbols(mySymbols);
//        System.out.println(dfX_3.format(12345678.009921));
        
        mySymbols.setDecimalSeparator('.');
        dfX_1.setDecimalFormatSymbols(mySymbols);
        dfX_2.setDecimalFormatSymbols(mySymbols);
        dfX_3.setDecimalFormatSymbols(mySymbols);
        dfX_4.setDecimalFormatSymbols(mySymbols);
        dfX_5.setDecimalFormatSymbols(mySymbols);
        dfX_1.setGroupingUsed(false);
        dfX_2.setGroupingUsed(false);
        dfX_3.setGroupingUsed(false);
        dfX_4.setGroupingUsed(false);
        dfX_5.setGroupingUsed(false);

    }
    

    
    public static int B1UnsigtoInt(byte b) {
        int result = (0xFF & b);
        return result;
    }

    // little endian
    public static int B2toInt(byte[] b) {
        int result = (((0xFF & b[1]) << 8) | (0xFF & b[0]));
        return result;
    }

    public static float B4toFloat(byte[] b) {
        int asInt = (b[0] & 0xFF) | ((b[1] & 0xFF) << 8) | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);
        return Float.intBitsToFloat(asInt);
    }

    // Canvia l'extensio del file f, a newExt. Si no en te l'afegeix
    public static File canviExtensio(File f, String newExt) {
        String path = f.toString(); // cami complert al fitxer
        String fname = f.getName(); // nom del fitxer (amb extensio si en tÃ©)
        path = path.substring(0, path.length() - fname.length()); // directori
                                                                  // del fitxer

        int i = fname.lastIndexOf('.');
        if (i > 0) { // si tÃ© extensio
            int midaExt = fname.length() - i;
            fname = fname.substring(0, fname.length() - midaExt);
            if (newExt.equals("")) {// volem treure l'extensio, la nova es sense
                                    // ext
                f = new File(path + fname);
            } else {// afegim l'extensio normal
                f = new File(path + fname + "." + newExt);
            }
        } else { // no tÃ© extensio
            if (newExt.equals("")) {// volem treure l'extensio, la nova es sense
                                    // ext
                f = new File(path + fname);
            } else {// afegim l'extensio normal
                f = new File(path + fname + "." + newExt);
            }
        }
        return f;
    }

    // Canvia el nom del file f, a nouNom. Deixa l'extensio que tenia.
    public static File canviNomFitxer(File f, String nouNom) {
        String path = f.toString(); // cami complert al fitxer
        String fname = f.getName(); // nom del fitxer (amb extensio si en te)
        path = path.substring(0, path.length() - fname.length()); // directori
                                                                  // del fitxer

        int i = fname.lastIndexOf('.');
        if (i > 0) { // si te extensio
            String ext = fname.substring(i + 1);
            f = new File(path + nouNom + "." + ext);
        } else { // no tÃ© extensio
            f = new File(path + nouNom);
        }
        return f;
    }

    // metode que copia un fitxer, torna 0 si correcte o -1 si no ha anat be
    public static int copyFile(File srFile, File dtFile) {
        int ret = -1;
        try {
            InputStream in = new FileInputStream(srFile);

            // For Append the file.
            // OutputStream out = new FileOutputStream(dtFile,true);

            // For Overwrite the file.
            OutputStream out = new FileOutputStream(dtFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            // log.addtxt(true,true,srFile.toString()+" copied to: "+dtFile.toString());

            ret = 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    // Get the extension of a file.
    public static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    // igual pero a partir d'un string (path o filename)
    public static String getExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String getFNameNoExt(File fn) {
        String ext = getExtension(fn);
        if (ext.length() > 0) {
            return fn.getPath().substring(0, fn.getPath().length() - ext.length() - 1);
        } else {
            return fn.getPath();
        }
    }

    // retorna el nom del fitxer sense extensio
    public static String getFNameNoExt(String fn) {
        String ext = getExtension(fn);
        if (ext.length() > 0) {
            return fn.substring(0, fn.length() - ext.length() - 1);
        } else {
            return fn;
        }
    }

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
            scale = B4toFloat(buff4);

            in.read(buff4);
            centrX = B4toFloat(buff4);

            in.read(buff4);
            centrY = B4toFloat(buff4);

            in.read(buff4);
            pixlx = B4toFloat(buff4) / 1000.f; // passem a mm

            in.read(buff4);
            pixly = B4toFloat(buff4) / 1000.f;

            in.read(buff4);
            sepod = B4toFloat(buff4);

            in.read(buff4);
            wl = B4toFloat(buff4);

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
                        patt2D.setIntenB2(j, i, (short) B2toInt(buff));
                        count = count + 1;
                        // nomes considerem valors superiors a zero pel minI
                        if (patt2D.getIntenB2(j, i) >= 0) {
                            if (patt2D.getIntenB2(j, i) > patt2D.getMaxI()) {
                                patt2D.setMaxI(patt2D.getIntenB2(j, i));
                            }
                            if (patt2D.getIntenB2(j, i) < patt2D.getMinI()) {
                                patt2D.setMinI(patt2D.getIntenB2(j, i));
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
                        int valorLlegit = B2toInt(buff);
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
                patt2D.setScale(patt2D.getMaxI() / 32767.f);

                in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                           // buffer
                                                                           // lectura
                in.read(new byte[60]);

                // ara aplico factor escala i guardo on i com toca (inclòs els
                // valors -1 de mascara)
                for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila
                                                             // (Y)
                    for (int j = 0; j < patt2D.getDimX(); j++) { // per cada
                                                                 // columna (X)
                        in.read(buff);
                        patt2D.setIntenB2(j, i, (short) (B2toInt(buff) / patt2D.getScale()));
                    }
                }
                // corregim maxI i minI
                patt2D.setMaxI(Math.round(patt2D.getMaxI() / patt2D.getScale()));
                patt2D.setMinI(Math.round(patt2D.getMinI() / patt2D.getScale()));
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
            scale=B4toFloat(buff4);
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
                        patt2D.setIntenB2(j,i,(short)((float)B2toInt(buff)));
                        count = count+1;
                        //nomes considerem valors superiors a zero pel minI
                        if(patt2D.getIntenB2(j,i)>=0){
                            if(patt2D.getIntenB2(j,i)>patt2D.getMaxI()){patt2D.setMaxI(patt2D.getIntenB2(j,i));}
                            if(patt2D.getIntenB2(j,i)<patt2D.getMinI()){patt2D.setMinI(patt2D.getIntenB2(j,i));}
                        }
                    }
                }   
            }else{
                //Haurem de fer dues passades, una per determinar el maxI, minI i factor d'escala i l'altre per
                //llegir totes les intensitats i aplicar el factor d'escala per encabir-ho a short.
                for (int i=0;i<patt2D.getDimY();i++){ //per cada fila (Y)
                    for (int j=0;j<patt2D.getDimX();j++){  //per cada columna (X)
                        in.read(buff);
                        int valorLlegit=B2toInt(buff);
                        count = count+1;
                        //nomes considerem valors superiors a zero pel minI
                        if(valorLlegit>=0){
                            if(valorLlegit>patt2D.getMaxI()){patt2D.setMaxI(valorLlegit);}
                            if(valorLlegit<patt2D.getMinI()){patt2D.setMinI(valorLlegit);}    
                        }
                    }
                }     
                
                //calculem el factor d'escala
                patt2D.setScale((float)patt2D.getMaxI()/32767.f);    

                in = new BufferedInputStream(new FileInputStream(d2File)); //reiniciem buffer lectura
                in.read(buff); //dimx
                in.read(buff); //dimy
                in.read(buff4); //scale
                in.read(buff); //cenx
                in.read(buff); //ceny
                
                //ara aplico factor escala i guardo on i com toca (inclòs els valors -1 de mascara)
                for (int i=0;i<patt2D.getDimY();i++){ //per cada fila (Y)
                    for (int j=0;j<patt2D.getDimX();j++){  //per cada columna (X)
                        in.read(buff);
                        patt2D.setIntenB2(j,i,(short)((float)B2toInt(buff)/patt2D.getScale()));
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
            System.out.println(line);
            // treurem la informació d'aquesta linia.
            // 0 1 2 3 4 5 6 7 8
            String[] llista = { "HDRBLKS:", "NROWS  :", "NCOLS  :", "CENTER :", "DISTANC:", "NOVERFL:", "MINIMUM:",
                    "MAXIMUM:", "NPIXELB:" };
            for (int i = 0; i < llista.length; i++) {
                Scanner scLine = new Scanner(line);
                scLine.useDelimiter(llista[i]);
                if (scLine.hasNext()) {
                    // String temp = scLine.findInLine(llista[i]);
                    scLine.findInLine(llista[i]);
                    // ara hem de llegir els 71 caracters següents:
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
            patt2D = new Pattern2D(dimX, dimY, centrX, centrY, maxI, minI, -1, false);
            int count = 0;
            in.read(header);

            // si la intensitat màxima supera el short escalem
            patt2D.setScale(Math.max(maxI / 32767.f, 1.000f));

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
                            valorLlegit = B1UnsigtoInt(buff[0]);
                            break;
                        case 2:
                            valorLlegit = B2toInt(buff);
                            break;
                    // case 4:
                    // valorLlegit=buffer.getInt();
                    // break;
                    }
                    patt2D.setIntenB2(j, i, (short) (valorLlegit / patt2D.getScale()));
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

                    patt2D.setIntenB2(col, fila, (short) (intensity / patt2D.getScale()));
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
        int dimX = 0, dimY = 0, maxI = 0, minI = 0;

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
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI, -1.0f, false);
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
                    int valorLlegit = B2toInt(buff);
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
            patt2D.setScale(Math.max(patt2D.getMaxI() / 32767.f, 1.000f));

            in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                       // buffer
                                                                       // lectura
            in.read(header);

            // ara aplico factor escala i guardo on i com toca
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    patt2D.setIntenB2(j, i, (short) (B2toInt(buff) / patt2D.getScale()));
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
        int dimX = 0, dimY = 0, maxI = 0, minI = 0;

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
                    if (line.contains("beam_center_x")) {
                        beamCX = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("beam_center_y")) {
                        beamCY = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("pixel_size_x")) {
                        pixSize = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                        pixSize = pixSize/1000.f;
                    }
                    if (line.contains("exp_distance")) {
                        distOD = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                    if (line.contains("exp_wavelength")) {
                        wl = Float.parseFloat(line.substring(iigual, line.trim().length() - 1).trim());
                    }
                }
            }
            headerSize = (int) (d2File.length()-binSize);

            // calculem el pixel central
//            beamCX = beamCX / pixSize;
//            beamCY = beamCY / pixSize;

            scD2file.close();

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(new FileInputStream(d2File));
            byte[] buff = new byte[2];
            byte[] header = new byte[headerSize];
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI, -1.0f, false);
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
                    int valorLlegit = B2toInt(buff);
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
            patt2D.setScale(Math.max(patt2D.getMaxI() / 32767.f, 1.000f));

            in = new BufferedInputStream(new FileInputStream(d2File)); // reiniciem
                                                                       // buffer
                                                                       // lectura
            in.read(header);

            // ara aplico factor escala i guardo on i com toca
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    patt2D.setIntenB2(j, i, (short) (B2toInt(buff) / patt2D.getScale()));
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
        String ext = FileUtils.getExtension(d2File);
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
                patt2D = FileUtils.openBinaryFile(d2File);    
            }else{
                patt2D = FileUtils.openBinaryFileOLD(d2File);
                patt2D.oldBIN=true;
            }
        }
        if (ext.equalsIgnoreCase("IMG")) {
            patt2D = FileUtils.openIMGfile(d2File);
        }
        if (ext.equalsIgnoreCase("SPR")) {
            patt2D = FileUtils.openSPRfile(d2File);
        }
        if (ext.equalsIgnoreCase("GFRM")) {
            patt2D = FileUtils.openGFRMfile(d2File);
        }
        if (ext.equalsIgnoreCase("EDF")) {
            patt2D = FileUtils.openEDFfile(d2File);
        }

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

            patt2D = new Pattern2D(dimX, dimY, -1.0f, -1.0f, 0, 999999999, -1.0f, false);

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
                    int in = new BigDecimal(r).intValue();
                    if (in > patt2D.getMaxI()) {
                        patt2D.setMaxI(in);
                    }
                    if (in < patt2D.getMinI()) {
                        patt2D.setMinI(in);
                    }
                }
            }
            // calculem el factor d'escala
            patt2D.setScale(Math.max(patt2D.getMaxI() / 32767.f, 1.000f));

            scD2file = new Scanner(d2File);
            scD2file.next();// x
            scD2file.next();// y
            scD2file.nextLine(); // salta linia

            // ara aplico factor escala i guardo on i com toca (inclòs els
            // valors -1 de mascara)
            // EDIT130417: pinto amb la Y invertida directament
            for (int i = patt2D.getDimY() - 1; i >= 0; i--) { // per cada fila
                                                              // (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    String r = scD2file.next();
                    int in = new BigDecimal(r).intValue();
                    patt2D.setIntenB2(j, i, (short) (in / patt2D.getScale()));
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
     * - Com que a l'obrir un fitxer probablement s'haurà escalat, hauríem de tornar
     *   a llegir les intensitats del fitxer original considerant les zones excloses
     *   per tenir els maxI i minI correctes. Aleshores escalar i guardar el BIN.
     *   Potser millor que fem un mètode nou.
     *   -- hem fet que al llegir img es tinguin en compte zones excloses, rellegint-lo
     *      abans de guardar el bin ja n'hi ha prou.
     *      No estaria de mes però fer un mètode que recalculés l'escala. (a Pattern2D)
     */
    public static File saveBIN(File d2File, Pattern2D patt2D) {
        // Forcem extensio bin
        d2File = new File(getFNameNoExt(d2File).concat(".bin"));

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

            bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(patt2D.getScale());
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

            output.write(new byte[60 - dataHeaderBytes]);

            // creem imatge normal
            for (int i = 0; i < patt2D.getDimY(); i++) {
                for (int j = 0; j < patt2D.getDimX(); j++) {
                    bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    if (patt2D.isInExZone(j, i)) {
                        bb.putShort((short) -1);
                    } else {
                        if((patt2D.getY0toMask()==1)&&(patt2D.getIntenB2(j, i)==0)){
                            bb.putShort((short) -1);
                        }else{
                            bb.putShort(patt2D.getIntenB2(j, i));
                        }
                    }
                    output.write(bb.array());
                    bb.clear();
                }
            }

            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("(FileUtils)Error saving BIN file");
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

    // format little endian
    public static byte[] toBytes2(int i) {
        byte[] result = new byte[2];
        result[1] = (byte) (i >> 8);
        result[0] = (byte) (i >> 0);
        return result;
    }
    
    private static boolean isNewBIN(File d2File){
        //primer mirem la capçalera
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
            System.out.println(bytes+" bytes --> format NOU");
            return true; 
        }else{
            System.out.println(bytes+" bytes --> format ANTIC");
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
                          int valorLlegit = B2toInt(buff);
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
                  patt2D.setScale(Math.max(patt2D.getMaxI() / 32767.f, 1.000f));

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
                              patt2D.setIntenB2(j, i, (short)-1);
                              continue;
                          }
                          patt2D.setIntenB2(j, i, (short) (B2toInt(buff) / patt2D.getScale()));
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
              //no es img, reescalem igualment (mai més intensitat que la original)
              patt2D.recalcScale();
          }
          
          return true; // correcte
          
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
