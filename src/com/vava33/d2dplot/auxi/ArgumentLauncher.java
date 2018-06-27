package com.vava33.d2dplot.auxi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.vava33.d2dplot.Calib_dialog;
import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.Dinco_frame;
import com.vava33.d2dplot.IntegracioRadial;
import com.vava33.d2dplot.MainFrame;
import com.vava33.jutils.ConsoleWritter;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public final class ArgumentLauncher {
    
    public final static String interactiveCode = "-macro";

    private static boolean launchGraphics = true; //dira si cal mostrar o no el graphical user interface o sortir del programa directament
    
    private static VavaLogger log = D2Dplot_global.getVavaLogger(ArgumentLauncher.class.getName());

    public static void readArguments(MainFrame mf, String[] args){
        
        if (args.length==0)return; //no hi ha res
        
        if (args[0].trim().equalsIgnoreCase("-macro")){
            ConsoleWritter.stat("MACRO MODE ON");
            ArgumentLauncher.setLaunchGraphics(false); //macromode per defecte false launch grafics
            ArgumentLauncher.startInteractive(mf,args);
            return;
        }
        
        if (args[0].trim().equalsIgnoreCase("-help")){
            ArgumentLauncher.setLaunchGraphics(false); //help per defecte false launch grafics
            ConsoleWritter.stat("TWO AVAILABLE OPTIONS FOR COMMAND LINE ARGUMENTS:");
            ConsoleWritter.stat("");
            ConsoleWritter.stat(" a) Entering an image filename as argument will open directly the image");
            ConsoleWritter.stat("");
            ConsoleWritter.stat(" b) Entering -macro as 1st argument to enable command line processing mode");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("    In macro mode, right after the -macro option, the next argument MUST be the filename of");
            ConsoleWritter.stat("    the image to be processed. Then following options are available:");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -sol");
            ConsoleWritter.stat("            Displays directly a tts-inco SOL file (same filename as the input image)");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -rint [CALfile] [-outdat DATfile]");
            ConsoleWritter.stat("            Performs radial integration.");
            ConsoleWritter.stat("            If no CALfile is specified, calibration parameters are taken from the image header.");
            ConsoleWritter.stat("            If no DATfile is specified, same name as the input image (but .dat) is used.");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -cal [dist] [wave] [-outcal [CALfile]]");
            ConsoleWritter.stat("            LaB6 calibration.");
            ConsoleWritter.stat("            If no dist or wave are specified they are taken from the image header");
            ConsoleWritter.stat("            Add -outcal option to generate a CAL filename with the same name as the input image");
            ConsoleWritter.stat("            as long as no CALfile is specified.");
            ConsoleWritter.stat("");
            ConsoleWritter.stat("      -show");
            ConsoleWritter.stat("            To open graphical display and do not exit after processing");
            ConsoleWritter.stat("");
            return;
        }
        
        //sino tindrem el path
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            path.append(args[i]).append(" ");
            log.debug("args["+i+"]="+args[i]);
        }
        //prova FITXER + SOL
        String pathS = path.toString().trim();
        log.debug("pathS="+pathS);
        D2Dplot_global.setWorkdir(pathS);

        //obrim el fitxer si es imatge
        File f = new File(pathS);
        if(f.exists()){
            if (f.isFile()){//l'obrim
                mf.updatePatt2D(f);
            }
        }
    }
    
    
    public static void startInteractive(MainFrame mf, String[] args){
        
        //el seguent argument obligatori es la imatge de treball
        StringBuilder path = new StringBuilder();
        int index = 1;
        for (int i = 1; i < args.length; i++) { //comencem a 1 perque el primer es -macro
            log.debug("args["+i+"]="+args[i]);
            if (args[i].startsWith("-"))break; //vol dir que es una altra opcio     ---- TODO: podria fer una subrutina que fes aixo no? perque ho fare servir mes
            path.append(args[i]).append(" ");
            index = index + 1;
        }
        String pathS = path.toString().trim();
        log.debug("pathS="+pathS);
        D2Dplot_global.setWorkdir(pathS);
        
        //obrim el fitxer si es imatge
        File f = new File(pathS);
        ConsoleWritter.stat(String.format("Reading img file: %s",f.toString()));
        if(f.exists()){
            if (f.isFile()){//l'obrim
                mf.updatePatt2D(f);
            }else{
                ConsoleWritter.stat(String.format("File %s is not a valid file (may be a directory?)",f.toString()));
                return;
            }
        }else{
            ConsoleWritter.stat(String.format("File %s not found",f.toString()));
            return;
        }
        
        if (index==args.length){//no hi ha mes opcions
            //com que nomes hi havia la imatge mostrem grafics
            ConsoleWritter.stat("No options found, opening image only");
            ArgumentLauncher.setLaunchGraphics(true);
            return; 
        }
        
        //Hi ha mes opcions, fem arraylist

        ArrayList<String> argL = new ArrayList<String>(Arrays.asList(args)); 
        int ifound = -1;
        
        ifound = getArgLindexOf(argL,"-show");
        if (ifound>=0){
            ArgumentLauncher.setLaunchGraphics(true);
            mf.showMainFrame();
        }
            
        ifound = getArgLindexOf(argL,"-sol");
        if (ifound>=0){
            File fsol = ArgumentLauncher.findSOLfile(f);
            if (fsol!=null){
                ConsoleWritter.stat(String.format("SOL file found: %s", fsol.toString()));
                //OBRIM dialeg INCO directament amb un fitxer SOL
                if (mf.getDincoFrame() == null) {
                    mf.setDincoFrame(new Dinco_frame(mf.getPanelImatge()));
                }
                mf.getDincoFrame().setSOLMode();
                mf.getDincoFrame().loadSOLFileDirectly(fsol);
                mf.getDincoFrame().setVisible(true);
                mf.getPanelImatge().setDinco(mf.getDincoFrame());
            }else{
                ConsoleWritter.stat("SOL file not found");
            }
        }
        
        //radial integration
        ifound = getArgLindexOf(argL,"-rint");
        if (ifound>=0){
            ConsoleWritter.stat("RINT option found, performing Radial Integration");

            //followed by CAL file
            StringBuilder calpath = new StringBuilder();
            for (int k= ifound+1; k<argL.size();k++){
                if(argL.get(k).startsWith("-"))break; //is starting another option
                calpath.append(argL.get(k)).append(" ");
            }
            String calpathS = calpath.toString().trim();
            
            //now we start the radial integration dialog (it contains the integ options)
            if (mf.getIrWin() == null) {
                mf.setIrWin(new IntegracioRadial(mf.getPanelImatge()));
                if (mf.isVisible()){
                    mf.getIrWin().setVisible(true);
                }
            }
            
            if (!calpathS.isEmpty()){
                File calFile = new File(calpathS);
                if (!calFile.exists()){
                    //try to find to the same path as Imgfile
                    calFile = new File(mf.getPatt2D().getImgfile().getParent()+D2Dplot_global.separator+calFile.getName());
                }
                if (calFile.exists()){
                    //llegim les opcions i apliquem a irwin
                    ConsoleWritter.stat(String.format("** Using integration parameters from CAL file: %s **",calpathS));
                    ImgFileUtils.readCALfile(mf.getPatt2D(), calFile, mf.getIrWin());
                }else{
                    ConsoleWritter.stat("** NO CAL file given or found, using values from header **");
                }
                
            }
            
            //integ parameters
            ConsoleWritter.stat("");
            ConsoleWritter.stat(String.format("   x-beam center: %.3f",mf.getPatt2D().getCentrX()));
            ConsoleWritter.stat(String.format("   y-beam center: %.3f",mf.getPatt2D().getCentrY()));
            ConsoleWritter.stat(String.format("   distance:      %.3f",mf.getPatt2D().getDistMD()));
            ConsoleWritter.stat(String.format("   wavelength:    %.4f",mf.getPatt2D().getWavel()));
            ConsoleWritter.stat(String.format("   tilt rotation: %.1f",mf.getPatt2D().getRotDeg()));
            ConsoleWritter.stat(String.format("   angle of tilt: %.2f",mf.getPatt2D().getTiltDeg()));
            ConsoleWritter.stat("");
            ConsoleWritter.stat(String.format("   t2ini:      %.3f",mf.getIrWin().getTxtT2i()));
            ConsoleWritter.stat(String.format("   t2fin:      %.3f",mf.getIrWin().getTxtT2f()));
            ConsoleWritter.stat(String.format("   stepsize:   %.4f",mf.getIrWin().getTxtStep()));
            ConsoleWritter.stat(String.format("   start azim: %.1f",mf.getIrWin().getTxtCakein()));
            ConsoleWritter.stat(String.format("   end azim:   %.1f",mf.getIrWin().getTxtCakefin()));
            ConsoleWritter.stat(String.format("   subadu:     %.1f",mf.getIrWin().getTxtZeroval()));
            ConsoleWritter.stat("");
            if (mf.getIrWin().getMaskfile()!=null){
                ConsoleWritter.stat(String.format("   maskfile:   %s",mf.getIrWin().getMaskfile().toString()));    
            }
            
            mf.getIrWin().getTxtAzimBins();
            
            //INTEGRACIO
            mf.getIrWin().do_btnIntegrartilt_actionPerformed(null);

            //ara el outfile
            String outpathS = FileUtils.canviExtensio(mf.getPatt2D().getImgfile(),"dat").toString();
            int ifound2 = getArgLindexOf(argL,"-outdat");
            if (ifound2>=0){
                StringBuilder outpath = new StringBuilder();
                for (int k= ifound2+1; k<argL.size();k++){
                    if(argL.get(k).startsWith("-"))break; //is starting another option
                    outpath.append(argL.get(k)).append(" ");
                }
                outpathS = outpath.toString().trim();
                if (outpathS.isEmpty())outpathS = FileUtils.canviExtensio(mf.getPatt2D().getImgfile(),"dat").toString();
               
            }
            log.debug("outpathdat="+outpathS);
            
            //ESCRIBIM FITXER
            if (mf.getIrWin().getPatt1D().size()>1){
                ConsoleWritter.stat(String.format("Writting output DAT files with base name: %s",FileUtils.getFNameNoExt(outpathS)));
            }else{
                ConsoleWritter.stat(String.format("Writting output DAT file: %s",outpathS));
            }
            try {
                mf.getIrWin().savePatterns(new File(outpathS));
            }catch(Exception e){
                if (D2Dplot_global.isDebug())e.printStackTrace();
                ConsoleWritter.stat("Error writting output DAT file(s)");
            }
            
        }
        
        ifound = getArgLindexOf(argL,"-cal");
        if (ifound>=0){
            ConsoleWritter.stat("CAL option found, performing LaB6 calibration with header info (distance, wavelength, pixsize)");
            
            //TODO LLEGIR PARAMETRES SEGÜENTS? dist, wave
            try{
                float dist = Float.parseFloat(argL.get(ifound +1));
                mf.getPatt2D().setDistMD(dist);
                ConsoleWritter.stat(String.format("Using entered distance %.3f",mf.getPatt2D().getDistMD()));
            }catch(Exception e){
                ConsoleWritter.stat(String.format("No distance provided, using value from header %.3f",mf.getPatt2D().getDistMD()));
            }

            try{
                float wave = Float.parseFloat(argL.get(ifound +2));
                mf.getPatt2D().setWavel(wave);
                ConsoleWritter.stat(String.format("Using entered wavelength %.4f",mf.getPatt2D().getWavel()));
            }catch(Exception e){
                ConsoleWritter.stat(String.format("No wavelength provided, using value from header %.4f",mf.getPatt2D().getWavel()));
            }
            
            
            if (mf.getCalibration() == null) {
                mf.setCalibration(new Calib_dialog(mf.getPanelImatge()));
                mf.getPanelImatge().setCalibration(mf.getCalibration());
                if (mf.isVisible()){
                    mf.getCalibration().setVisible(true);
                }
            }
            if((!mf.getPatt2D().checkIfWavel() || !mf.getPatt2D().checkIfPixSize() || !mf.getPatt2D().checkIfDistMD())){
                ConsoleWritter.stat("Wavelength, PixelSize or Sample-Detector distance missing in the header. ABORTING.");
            }else{
                mf.getCalibration().do_btnAuto_actionPerformed(null);
                float cx = mf.getCalibration().getRefCX();
                float cy = mf.getCalibration().getRefCY();
                float distMD = mf.getCalibration().getRefMD();
                float rot = mf.getCalibration().getRefRotDeg();
                float tilt = mf.getCalibration().getRefTiltDeg();
                ConsoleWritter.afegirLinia('-');
                ConsoleWritter.stat("REFINEMENT RESULTS:");
                ConsoleWritter.afegirLinia('-');
                ConsoleWritter.writeNameNumPairs(false, "CenterX,CenterY,S-D_distance,ROT,TILT",cx,cy,distMD,rot,tilt);
                ConsoleWritter.afegirLinia('-');
            }
            
            int ifound2 = getArgLindexOf(argL,"-outcal");
            if (ifound2>=0){
                StringBuilder outpath = new StringBuilder();
                for (int k= ifound2+1; k<argL.size();k++){
                    if(argL.get(k).startsWith("-"))break; //is starting another option
                    outpath.append(argL.get(k)).append(" ");
                }
                String outpathS = outpath.toString().trim();
                if (outpathS.isEmpty())outpathS = FileUtils.canviExtensio(mf.getPatt2D().getImgfile(),"inp").toString();
                log.debug("outpathcal="+outpathS);
                //ESCRIBIM FITXER
                ConsoleWritter.stat(String.format("Writting output CAL file: %s",outpathS));
                try {
                    mf.getCalibration().writeCALfile(new File(outpathS));
                    // GIVE THE COMMAND TO INTEGRATE:
                    ConsoleWritter.stat("To perform 1D-integration with this cal file type:");
                    ConsoleWritter.stat(String.format("d2Dplot -macro %s -rint %s",mf.getPatt2D().getImgfile().toString(),outpathS));
                }catch(Exception e){
                    if (D2Dplot_global.isDebug())e.printStackTrace();
                    ConsoleWritter.stat(String.format("Error writting output CAL file: %s",outpathS));
                }
            }
        }
        
    }

    //returns the index of the opt in the array. INGORES CASE. if not found returns -1
    private static int getArgLindexOf(ArrayList<String> argL, String opt){
        if (argL==null)return -1;
        if (argL.size()<=0)return -1;
        
        //primer provem tal qual
        if (argL.contains(opt)){
            return argL.indexOf(opt);
        }
        
        if (argL.contains(opt.toLowerCase())){
            return argL.indexOf(opt.toLowerCase());
        }
        if (argL.contains(opt.toUpperCase())){
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
        log.debug("fsol="+fsol.toString());
        ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
        if (fsol.exists()){
            ConsoleWritter.afegirText(false, false, "YES!");
            trobat = true;
        }
        ConsoleWritter.afegirSaltLinia();
        if (!trobat){
            fsol = FileUtils.canviExtensio(f, "sol");
            log.debug("fsol="+fsol.toString());
            ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
            if (fsol.exists()){
                ConsoleWritter.afegirText(false, false, "YES!");
                trobat = true;
            }
        }
        ConsoleWritter.afegirSaltLinia();
        if (!trobat){
            String fn1 = FileUtils.getFNameNoExt(f);
            String fs = fn1.substring(0, fn1.toString().length()-5)+".SOL";
            fsol = new File(fs);
            log.debug("fsol="+fsol.toString());
            ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
            if (fsol.exists()){
                ConsoleWritter.afegirText(false, false, "YES!");
                trobat = true;
            }
        }
        ConsoleWritter.afegirSaltLinia();
        if (!trobat){
            fsol = FileUtils.canviExtensio(fsol, "sol");
            log.debug("fsol="+fsol.toString());
            ConsoleWritter.afegirText(false, true, String.format("is it %s?", fsol.toString()));
            if (fsol.exists()){
                ConsoleWritter.afegirText(false, false, "YES!");
                trobat = true;
            }                        
        }
        ConsoleWritter.afegirSaltLinia();
        if (trobat) return fsol;
        return null;
    }

}
