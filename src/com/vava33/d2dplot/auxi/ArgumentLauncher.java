package com.vava33.d2dplot.auxi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.vava33.d2dplot.Calibration;
import com.vava33.d2dplot.ConvertTo1DXRD;
import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.IncoPlot;
import com.vava33.d2dplot.MainFrame;
import com.vava33.jutils.ConsoleWritter;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public final class ArgumentLauncher {

    public final static String interactiveCode = "-macro";

    private static boolean launchGraphics = true; //dira si cal mostrar o no el graphical user interface o sortir del programa directament

    private static final String className = "ArgLauncher";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    /*
     * -macro com a primer argument implica interactive
     * el segon argument aleshores ha de ser la imatge de treball
     *
     *
     * possibilitat macrofile? -macrofile i llegir linia a linia les opcions?
     *
     * DE MOMENT FUNCIONA PERO CALDRIA ESCRIURE MISSATGES PER CONSOLA.
     *
     */

    public static void readArguments(MainFrame mf, String[] args) {

//        ConsoleWritter.afegirText(true, false, FileUtils.getCharLine('=', 78));
//        ConsoleWritter.afegirSaltLinia();
//        ConsoleWritter.afegirText(false, false, "               ");
//        ConsoleWritter.afegirText(true, false, D2Dplot_global.welcomeMSG);
//        ConsoleWritter.afegirText(true, false, FileUtils.getCharLine('=', 78));

        ConsoleWritter.afegirText(true, true, "");
        ConsoleWritter.afegirText(true, false, FileUtils.getCharLine('=', 80));
        ConsoleWritter.afegirSaltLinia();
        ConsoleWritter.afegirText(true, false, FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[0]+". Session on "+FileUtils.fDiaHora.format(new Date()),80));
        ConsoleWritter.afegirText(true, false, FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[1], 78));
        ConsoleWritter.afegirSaltLinia();
        ConsoleWritter.afegirText(true, false, FileUtils.getCharLine('=', 80));
//      ConsoleWritter.afegirText(true, false, D2Dplot_global.welcomeMSG);
//      ConsoleWritter.afegirText(true, false, FileUtils.getCharLine('=', 78));
        
        if (args.length == 0)
            return; //no hi ha res

        if (args[0].trim().equalsIgnoreCase("-macro")) {
            ConsoleWritter.stat("MACRO MODE ON");
            ArgumentLauncher.setLaunchGraphics(false); //macromode per defecte false launch grafics
            ArgumentLauncher.startInteractive(mf, args);
            return;
        }

        if (args[0].trim().equalsIgnoreCase("-help")) {
            ArgumentLauncher.setLaunchGraphics(false); //help per defecte false launch grafics
            ConsoleWritter.stat("TWO AVAILABLE OPTIONS FOR COMMAND LINE ARGUMENTS:");
            ConsoleWritter.stat("");
            ConsoleWritter.stat(" a) Entering an image filename as argument will open directly the image");
            ConsoleWritter.stat("");
            ConsoleWritter.stat(" b) Entering -macro as 1st argument to enable command line processing mode");
            ConsoleWritter.stat("");
            ConsoleWritter.stat(
                    "    In macro mode, right after the -macro option, the next argument MUST be the filename of");
            ConsoleWritter.stat("    the image to be processed. Then following options are available:");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -sol");
            ConsoleWritter.stat("          Displays directly a tts-inco SOL file (same filename as the input image)");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -rint [CALfile] [-outdat DATfile]");
            ConsoleWritter.stat("          Performs radial integration.");
            ConsoleWritter.stat(
                    "          · If no CALfile is specified, calibration parameters are taken from the image header.");
            ConsoleWritter
                    .stat("          · If no DATfile is specified, same name as the input image (but .dat) is used.");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -cal 0/1/2... [dist] [wave] [-outcal [CALfile]]");
            ConsoleWritter.stat("          Instrumental Parameter Calibration.");
            ConsoleWritter.stat(
                    "          · The first argument following -cal is an integer to select the calibrant (mandatory)");
            ConsoleWritter.stat(
                    "            [0= LaB6, 1= Si, 2= first calibrant in config file, 3= second calibrant in cfg file, etc...");
            ConsoleWritter.stat("          · If no dist or wave are specified they are taken from the image header");
            ConsoleWritter.stat(
                    "          · Add -outcal option to generate a CAL filename with the same name as the input image");
            ConsoleWritter.stat("            as long as no CALfile is specified.");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -show");
            ConsoleWritter.stat("          · To open graphical display and do not exit after processing");
            ConsoleWritter.stat("");
            return;
        }

        //sino tindrem el path
        final StringBuilder path = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            path.append(args[i]).append(" ");
            log.debug("args[" + i + "]=" + args[i]);
        }
        //prova FITXER + SOL
        final String pathS = path.toString().trim();
        log.debug("pathS=" + pathS);
        D2Dplot_global.setWorkdir(pathS);

        //obrim el fitxer si es imatge
        final File f = new File(pathS);
        if (f.exists()) {
            if (f.isFile()) {//l'obrim
                mf.updatePatt2D(f);
            }
        }
    }

    public static void startInteractive(MainFrame mf, String[] args) {

        //el seguent argument obligatori es la imatge de treball
        final StringBuilder path = new StringBuilder();
        int index = 1;
        for (int i = 1; i < args.length; i++) { //comencem a 1 perque el primer es -macro
            log.debug("args[" + i + "]=" + args[i]);
            if (args[i].startsWith("-"))
                break; //vol dir que es una altra opcio     ---- TODO: podria fer una subrutina que fes aixo no? perque ho fare servir mes
            path.append(args[i]).append(" ");
            index = index + 1;
        }
        final String pathS = path.toString().trim();
        log.debug("pathS=" + pathS);
        D2Dplot_global.setWorkdir(pathS);

        //obrim el fitxer si es imatge
        final File f = new File(pathS);
        ConsoleWritter.stat(String.format("Reading img file: %s", f.toString()));
        if (f.exists()) {
            if (f.isFile()) {//l'obrim
                mf.updatePatt2D(f);
            } else {
                ConsoleWritter.stat(String.format("File %s is not a valid file (may be a directory?)", f.toString()));
                return;
            }
        } else {
            ConsoleWritter.stat(String.format("File %s not found", f.toString()));
            return;
        }

        if (index == args.length) {//no hi ha mes opcions
            //com que nomes hi havia la imatge mostrem grafics
            ConsoleWritter.stat("No options found, opening image only");
            ArgumentLauncher.setLaunchGraphics(true);
            return;
        }

        //Hi ha mes opcions, fem arraylist

        final ArrayList<String> argL = new ArrayList<String>(Arrays.asList(args));
        int ifound = -1;

        ifound = getArgLindexOf(argL, "-show");
        if (ifound >= 0) {
            ArgumentLauncher.setLaunchGraphics(true);
            mf.showMainFrame();
        }

        ifound = getArgLindexOf(argL, "-sol");
        if (ifound >= 0) {
            final File fsol = ArgumentLauncher.findSOLfile(f);
            if (fsol != null) {
                ConsoleWritter.stat(String.format("SOL file found: %s", fsol.toString()));
                //OBRIM dialeg INCO directament amb un fitxer SOL
                if (mf.getDincoFrame() == null) {
                    mf.setDincoFrame(new IncoPlot(mf.getMainF(), mf.getPanelImatge()));
                }
                mf.getDincoFrame().setSOLMode();
                mf.getDincoFrame().loadSOLFileDirectly(fsol);
                mf.getDincoFrame().setVisible(true);
                mf.getPanelImatge().setDinco(mf.getDincoFrame());
            } else {
                ConsoleWritter.stat("SOL file not found");
            }
        }

        //radial integration
        ifound = getArgLindexOf(argL, "-rint");
        if (ifound >= 0) {
            ConsoleWritter.stat("RINT option found, performing Radial Integration");

            //followed by CAL file
            final StringBuilder calpath = new StringBuilder();
            for (int k = ifound + 1; k < argL.size(); k++) {
                if (argL.get(k).startsWith("-"))
                    break; //is starting another option
                calpath.append(argL.get(k)).append(" ");
            }
            final String calpathS = calpath.toString().trim();

            //now we start the radial integration dialog (it contains the integ options)
            if (mf.getIrWin() == null) {
                mf.setIrWin(new ConvertTo1DXRD(mf.getMainF(), mf.getPanelImatge()));
                if (mf.getMainF().isVisible()) {
                    mf.getIrWin().setVisible(true);
                }
            }

            if (!calpathS.isEmpty()) {
                File calFile = new File(calpathS);
                if (!calFile.exists()) {
                    //try to find to the same path as Imgfile
                    calFile = new File(
                            mf.getPatt2D().getImgfile().getParent() + D2Dplot_global.separator + calFile.getName());
                }
                if (calFile.exists()) {
                    //llegim les opcions i apliquem a irwin
                    ConsoleWritter
                            .stat(String.format("** Using integration parameters from CAL file: %s **", calpathS));
                    ImgFileUtils.readCALfile(mf.getPatt2D(), calFile, mf.getIrWin());
                } else {
                    ConsoleWritter.stat("** NO CAL file given or found, using values from header **");
                }

            }

            //integ parameters
            ConsoleWritter.stat("");
            ConsoleWritter.stat(String.format("   x-beam center: %.3f", mf.getPatt2D().getCentrX()));
            ConsoleWritter.stat(String.format("   y-beam center: %.3f", mf.getPatt2D().getCentrY()));
            ConsoleWritter.stat(String.format("   distance:      %.3f", mf.getPatt2D().getDistMD()));
            ConsoleWritter.stat(String.format("   wavelength:    %.4f", mf.getPatt2D().getWavel()));
            ConsoleWritter.stat(String.format("   tilt rotation: %.1f", mf.getPatt2D().getRotDeg()));
            ConsoleWritter.stat(String.format("   angle of tilt: %.2f", mf.getPatt2D().getTiltDeg()));
            ConsoleWritter.stat("");
            ConsoleWritter.stat(String.format("   t2ini:      %.3f", mf.getIrWin().getTxtT2i()));
            ConsoleWritter.stat(String.format("   t2fin:      %.3f", mf.getIrWin().getTxtT2f()));
            ConsoleWritter.stat(String.format("   stepsize:   %.4f", mf.getIrWin().getTxtStep()));
            ConsoleWritter.stat(String.format("   start azim: %.1f", mf.getIrWin().getTxtCakein()));
            ConsoleWritter.stat(String.format("   end azim:   %.1f", mf.getIrWin().getTxtCakefin()));
            ConsoleWritter.stat(String.format("   subadu:     %.1f", mf.getIrWin().getTxtZeroval()));
            ConsoleWritter.stat("");
            if (mf.getIrWin().getMaskfile() != null) {
                ConsoleWritter.stat(String.format("   maskfile:   %s", mf.getIrWin().getMaskfile().toString()));
            }

            mf.getIrWin().getTxtAzimBins();

            //INTEGRACIO
            mf.getIrWin().do_btnIntegrartilt_actionPerformed(null);

            //ara el outfile
            String outpathS = FileUtils.canviExtensio(mf.getPatt2D().getImgfile(), "dat").toString();
            final int ifound2 = getArgLindexOf(argL, "-outdat");
            if (ifound2 >= 0) {
                final StringBuilder outpath = new StringBuilder();
                for (int k = ifound2 + 1; k < argL.size(); k++) {
                    if (argL.get(k).startsWith("-"))
                        break; //is starting another option
                    outpath.append(argL.get(k)).append(" ");
                }
                outpathS = outpath.toString().trim();
                if (outpathS.isEmpty())
                    outpathS = FileUtils.canviExtensio(mf.getPatt2D().getImgfile(), "dat").toString();

            }
            log.debug("outpathdat=" + outpathS);

            //ESCRIBIM FITXER
            if (mf.getIrWin().getPatt1D().size() > 1) {
                ConsoleWritter.stat(String.format("Writting output DAT files with base name: %s",
                        FileUtils.getFNameNoExt(outpathS)));
            } else {
                ConsoleWritter.stat(String.format("Writting output DAT file: %s", outpathS));
            }
            try {
                mf.getIrWin().savePatterns(new File(outpathS));
            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                ConsoleWritter.stat("Error writting output DAT file(s)");
            }

        }

        ifound = getArgLindexOf(argL, "-cal");
        if (ifound >= 0) {
            ConsoleWritter.stat(
                    "CAL option found, performing instrumental parameters calibration with header info (distance, wavelength, pixsize)");

            //primer llegim calibrant
            int calib = 0;
            int arg_index_correction = 0;
            try {
                calib = Integer.parseInt(argL.get(ifound + 1));
                ConsoleWritter.stat(String.format("Using calibrant %s", CalibOps.getCalibrants().get(calib).getName()));
            } catch (final Exception e) {
                ConsoleWritter.stat(String.format("No calibrant provided, using  %s",
                        CalibOps.getCalibrants().get(calib).getName()));
                arg_index_correction = -1;
            }

            //TODO LLEGIR PARAMETRES SEGÜENTS? dist, wave
            try {
                final float dist = Float.parseFloat(argL.get(ifound + 2 + arg_index_correction));
                mf.getPatt2D().setDistMD(dist);
                ConsoleWritter.stat(String.format("Using entered distance %.3f", mf.getPatt2D().getDistMD()));
            } catch (final Exception e) {
                ConsoleWritter.stat(String.format("No distance provided, using value from header %.3f",
                        mf.getPatt2D().getDistMD()));
            }

            try {
                final float wave = Float.parseFloat(argL.get(ifound + 3 + arg_index_correction));
                mf.getPatt2D().setWavel(wave);
                ConsoleWritter.stat(String.format("Using entered wavelength %.4f", mf.getPatt2D().getWavel()));
            } catch (final Exception e) {
                ConsoleWritter.stat(String.format("No wavelength provided, using value from header %.4f",
                        mf.getPatt2D().getWavel()));
            }

            if (mf.getCalibration() == null) {
                mf.setCalibration(new Calibration(mf.getMainF(), mf.getPanelImatge()));
                mf.getPanelImatge().setCalibration(mf.getCalibration());
                if (mf.getMainF().isVisible()) {
                    mf.getCalibration().setVisible(true);
                }
            }
            if ((!mf.getPatt2D().checkIfWavel() || !mf.getPatt2D().checkIfPixSize()
                    || !mf.getPatt2D().checkIfDistMD())) {
                ConsoleWritter
                        .stat("Wavelength, PixelSize or Sample-Detector distance missing in the header. ABORTING.");
            } else {
                mf.getCalibration().selectCalibrant(calib);
                mf.getCalibration().do_btnAuto_actionPerformed(null);
                final float cx = mf.getCalibration().getRefCX();
                final float cy = mf.getCalibration().getRefCY();
                final float distMD = mf.getCalibration().getRefMD();
                final float rot = mf.getCalibration().getRefRotDeg();
                final float tilt = mf.getCalibration().getRefTiltDeg();
                ConsoleWritter.afegirLinia('-');
                ConsoleWritter.stat("REFINEMENT RESULTS:");
                ConsoleWritter.afegirLinia('-');
                ConsoleWritter.writeNameNumPairs(false, "CenterX,CenterY,S-D_distance,ROT,TILT", cx, cy, distMD, rot,
                        tilt);
                ConsoleWritter.afegirLinia('-');
            }

            final int ifound2 = getArgLindexOf(argL, "-outcal");
            if (ifound2 >= 0) {
                final StringBuilder outpath = new StringBuilder();
                for (int k = ifound2 + 1; k < argL.size(); k++) {
                    if (argL.get(k).startsWith("-"))
                        break; //is starting another option
                    outpath.append(argL.get(k)).append(" ");
                }
                String outpathS = outpath.toString().trim();
                if (outpathS.isEmpty())
                    outpathS = FileUtils.canviExtensio(mf.getPatt2D().getImgfile(), "inp").toString();
                log.debug("outpathcal=" + outpathS);
                //ESCRIBIM FITXER
                ConsoleWritter.stat(String.format("Writting output CAL file: %s", outpathS));
                try {
                    ImgFileUtils.writeCALfile(new File(outpathS), mf.getPatt2D(), mf.getCalibration(), false); //aqui exporto un fitxer INP (per Spock) TODO: mirar si es pot canviar a CAL
                    // GIVE THE COMMAND TO INTEGRATE:
                    ConsoleWritter.stat("To perform 1D-integration with this cal file type:");
                    ConsoleWritter.stat(String.format("d2Dplot -macro %s -rint %s",
                            mf.getPatt2D().getImgfile().toString(), outpathS));
                } catch (final Exception e) {
                    if (D2Dplot_global.isDebug())
                        e.printStackTrace();
                    ConsoleWritter.stat(String.format("Error writting output CAL file: %s", outpathS));
                }
            }
        }

    }

    //returns the index of the opt in the array. INGORES CASE. if not found returns -1
    private static int getArgLindexOf(ArrayList<String> argL, String opt) {
        if (argL == null)
            return -1;
        if (argL.size() <= 0)
            return -1;

        //primer provem tal qual
        if (argL.contains(opt)) {
            return argL.indexOf(opt);
        }

        if (argL.contains(opt.toLowerCase())) {
            return argL.indexOf(opt.toLowerCase());
        }
        if (argL.contains(opt.toUpperCase())) {
            return argL.indexOf(opt.toUpperCase());
        }

        return -1;
    }

    public static boolean isLaunchGraphics() {
        return launchGraphics;
    }

    public static void setLaunchGraphics(boolean launchGraphics) {
        ArgumentLauncher.launchGraphics = launchGraphics;
    }

    //f is the opened image file
    public static File findSOLfile(File f) {
        //opcio SOL mostrem grafics
        ConsoleWritter.stat("SOL option found, trying to find SOL file");
        ArgumentLauncher.setLaunchGraphics(true); //mandatory for this option
        //        argL.indexOf per si mes endavant volem especificar fitxer
        boolean trobat = false;
        //provarem 2 coses, un sol tal qual amb el mateix nom que f i un sense numeros
        File fsol = FileUtils.canviExtensio(f, "SOL");
        log.debug("fsol=" + fsol.toString());
        ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
        if (fsol.exists()) {
            ConsoleWritter.afegirText(false, false, "YES!");
            trobat = true;
        }
        ConsoleWritter.afegirSaltLinia();
        if (!trobat) {
            fsol = FileUtils.canviExtensio(f, "sol");
            log.debug("fsol=" + fsol.toString());
            ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
            if (fsol.exists()) {
                ConsoleWritter.afegirText(false, false, "YES!");
                trobat = true;
            }
        }
        ConsoleWritter.afegirSaltLinia();
        if (!trobat) {
            final String fn1 = FileUtils.getFNameNoExt(f);
            final String fs = fn1.substring(0, fn1.toString().length() - 5) + ".SOL";
            fsol = new File(fs);
            log.debug("fsol=" + fsol.toString());
            ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
            if (fsol.exists()) {
                ConsoleWritter.afegirText(false, false, "YES!");
                trobat = true;
            }
        }
        ConsoleWritter.afegirSaltLinia();
        if (!trobat) {
            fsol = FileUtils.canviExtensio(fsol, "sol");
            log.debug("fsol=" + fsol.toString());
            ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
            if (fsol.exists()) {
                ConsoleWritter.afegirText(false, false, "YES!");
                trobat = true;
            }
        }
        ConsoleWritter.afegirSaltLinia();
        if (trobat)
            return fsol;
        return null;
    }

}
