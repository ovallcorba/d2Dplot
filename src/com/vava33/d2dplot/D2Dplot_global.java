package com.vava33.d2dplot;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import com.vava33.d2dplot.auxi.PDDatabase;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.d2dplot.auxi.PuntClick;
import com.vava33.d2dplot.auxi.PuntSolucio;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public final class D2Dplot_global {

    public static final int satur32 = Short.MAX_VALUE;
    public static final int satur65 = (Short.MAX_VALUE * 2) + 1;
    public static final String welcomeMSG = "d2Dplot v1703 (170616) by OV"; //nomes canviare la versio global quan faci un per distribuir
    public static final String separator = System.getProperty("file.separator");
    public static final String binDir = System.getProperty("user.dir") + separator + "bin" + separator;
    public static final String userDir = System.getProperty("user.dir");
    public static final String configFilePath = System.getProperty("user.dir") + separator + "d2dconfig.cfg";
    public static final String usersGuidePath = System.getProperty("user.dir") + separator + "d2Dplot_userguide.pdf";
    public static Boolean configFileReaded = null; //true=readed false=errorReading null=notFound
    
    //symbols and characters
    public static final String theta = "\u03B8";
    public static final String angstrom= "\u212B";
    public static final String fDiaHora1 = "[yyMMddHHmm]";
    public static final String fDiaHora2 = "[yyyy-MM-dd 'at' HH:mm]";
    
    public static String[] lightColors = {"black","blue","red","green","magenta","cyan","pink","yellow"}; //8 colors
    public static String[] DarkColors = {"yellow","white","cyan","green","magenta","blue","red","pink"}; //8 colors
    
    public static VavaLogger log;

    public static boolean keepCalibration = false;
    public static float distMD, centX, centY, tilt, rot;
    
//*** parametres que es poden canviar a les opcions *****
    //global 
    public static boolean logging = true;
    public static String loglevel = "info"; //info, config, etc...
    private static final boolean overrideLogLevelConfigFile = false;
    private static String workdir = System.getProperty("user.dir");
    public static boolean sideControls = true;
    private static Integer def_Width=768;
    private static Integer def_Height=1024;
    
    //DB
    public static String DBfile;
    public static String QLfile;
    private static Float minDspacingToSearch;

    //Calib

    //ImagePanel and pattern2D
    private static Float t2tolDegClickPoints; //in patt2d
    private static Float incZoom;
    private static Float maxScaleFit;
    private static Float minScaleFit;
    private static Integer hklfontSize;
    private static Float factorAutoContrast;
    private static Color colorCallibEllipses;
    private static Color colorGuessPointsEllipses;
    private static Color colorFittedEllipses;
    private static Color colorBoundariesEllipses;
    private static Color colorExcludedZones;
    private static Color colorPeakSearch;
    private static Color colorPeakSearchSelected;
    private static Color colorQLcomp;
    private static Color colorDBcomp;
    private static Color colorEXZ = new Color(255, 0, 255);

    //puntClick
    private static Color colorClickPointsCircle;
    private static Color colorClickPoints;
    private static Integer clickPointSize;
    
    //dinco
    private static Integer dincoSolPointSize;
    private static Float dincoSolPointStrokeSize; 
    private static Boolean dincoSolPointSizeByFc;
    private static Boolean dincoSolPointFill;
    
//***    
    
    
    public static void initEarlyPars(){
        //init logger during reading pars in config mode
        initLogger(D2Dplot_global.class.getName()); //during the par reading
        
        //global
        workdir = System.getProperty("user.dir");
        sideControls = true;
        def_Width=null;
        def_Height=null;
        
        //now initilize the others with the NONE value
        //DB (dels DBfiles ja s'encarrega checkDBs
        DBfile=null;
        QLfile=null;
        minDspacingToSearch=null;

        //Calib
        
        //ImagePanel and pattern2D
        t2tolDegClickPoints = null; //in patt2d
        incZoom = null;
        maxScaleFit=null;
        minScaleFit=null;
        hklfontSize=null;
        factorAutoContrast=null;
        colorCallibEllipses = null;
        colorGuessPointsEllipses = null;
        colorFittedEllipses = null;
        colorBoundariesEllipses = null;
        colorExcludedZones = null;
        colorPeakSearch = null;
        colorPeakSearchSelected = null;
        colorQLcomp = null;
        colorDBcomp = null;
        
        //puntClick
        colorClickPointsCircle = null;
        colorClickPoints = null;
        clickPointSize = null;
        
        //dinco
        dincoSolPointSize=null; 
        dincoSolPointSizeByFc=null;
        dincoSolPointStrokeSize=null;
        dincoSolPointFill=null;
    }
    
    public static void initPars(){
        
        //DB (dels DBfiles ja s'encarrega checkDBs
        if (minDspacingToSearch == null){
            minDspacingToSearch = DB_dialog.getMinDspacingToSearch();    
        }else{
            DB_dialog.setMinDspacingToSearch(minDspacingToSearch.floatValue());
        }
        
        if (def_Width == null){
            def_Width = MainFrame.getDef_Width();
        }else{
            MainFrame.setDef_Width(def_Width.intValue());
        }
        if (def_Height == null){
            def_Height = MainFrame.getDef_Height();
        }else{
            MainFrame.setDef_Height(def_Height.intValue());
        }
        
        //Calib
        
        //ImagePanel and pattern2D
        if (t2tolDegClickPoints == null){
            t2tolDegClickPoints = Pattern2D.getT2tolDegSelectedPoints();
        }else{
            Pattern2D.setT2tolDegSelectedPoints(t2tolDegClickPoints.floatValue());
        }
        if (incZoom == null){
            incZoom = ImagePanel.getIncZoom();
        }else{
            ImagePanel.setIncZoom(incZoom.floatValue());
        }  
        if (maxScaleFit == null){
            maxScaleFit = ImagePanel.getMaxScaleFit();
        }else{
            ImagePanel.setMaxScaleFit(maxScaleFit.floatValue());
        }        
        if (minScaleFit == null){
            minScaleFit = ImagePanel.getMinScaleFit();
        }else{
            ImagePanel.setMinScaleFit(minScaleFit.floatValue());
        }  
        if (hklfontSize == null){
            hklfontSize = ImagePanel.getHklfontSize();
        }else{
            ImagePanel.setHklfontSize(hklfontSize.intValue());
        }          
        if (factorAutoContrast == null){
            factorAutoContrast = ImagePanel.getFactorAutoContrast();
        }else{
            ImagePanel.setFactorAutoContrast(factorAutoContrast.floatValue());
        }  
        
        //puntClick
        if (colorClickPointsCircle == null){
            colorClickPointsCircle = PuntClick.getColorCercle();
        }else{
            PuntClick.setColorCercle(colorClickPointsCircle);
        }
        if (colorClickPoints == null){
            colorClickPoints = PuntClick.getColorPunt();
        }else{
            PuntClick.setColorPunt(colorClickPoints);
        }
        if (clickPointSize  == null){
            clickPointSize = PuntClick.getMidaPunt();
        }else{
            PuntClick.setMidaPunt(clickPointSize.intValue());
        }
        
        //punt solucio
        if (dincoSolPointSize  == null){
            dincoSolPointSize = PuntSolucio.getDincoSolPointSize();
        }else{
            PuntSolucio.setDincoSolPointSize(dincoSolPointSize.intValue());
        }
        if (dincoSolPointSizeByFc == null){
            dincoSolPointSizeByFc = PuntSolucio.isDincoSolPointSizeByFc();
        }else{
            PuntSolucio.setDincoSolPointSizeByFc(dincoSolPointSizeByFc.booleanValue());
        }
        if (dincoSolPointStrokeSize == null){
            dincoSolPointStrokeSize = PuntSolucio.getDincoSolPointStrokeSize();
        }else{
            PuntSolucio.setDincoSolPointStrokeSize(dincoSolPointStrokeSize.floatValue());
        }
        if (dincoSolPointFill == null){
            dincoSolPointFill = PuntSolucio.isDincoSolPointFill();
        }else{
            PuntSolucio.setDincoSolPointFill(dincoSolPointFill.booleanValue());
        }
        
    }
    
    public static void checkDBs(){
        if (DBfile==null){
            DBfile = PDDatabase.getLocalDB();    
        }else{
            PDDatabase.setLocalDB(DBfile);
        }
        if (QLfile==null){
            QLfile = PDDatabase.getLocalQL();    
        }else{
            PDDatabase.setLocalQL(QLfile);
        }
    }    
    
    public static void init_ApplyColorsToIPanel(ImagePanel ip){
        
        if (colorCallibEllipses == null){
            colorCallibEllipses = ip.getPanelImatge().getColorCallibEllipses();
        }else{
            ip.getPanelImatge().setColorCallibEllipses(colorCallibEllipses);
        }
        if (colorGuessPointsEllipses == null){
            colorGuessPointsEllipses = ip.getPanelImatge().getColorGuessPointsEllipses();
        }else{
            ip.getPanelImatge().setColorGuessPointsEllipses(colorGuessPointsEllipses);
        }
        if (colorFittedEllipses == null){
            colorFittedEllipses = ip.getPanelImatge().getColorFittedEllipses();
        }else{
            ip.getPanelImatge().setColorFittedEllipses(colorFittedEllipses);
        }
        if (colorBoundariesEllipses == null){
            colorBoundariesEllipses = ip.getPanelImatge().getColorBoundariesEllipses();
        }else{
            ip.getPanelImatge().setColorBoundariesEllipses(colorBoundariesEllipses);
        }
        if (colorExcludedZones == null){
            colorExcludedZones = ip.getPanelImatge().getColorExcludedZones();
        }else{
            ip.getPanelImatge().setColorExcludedZones(colorExcludedZones);
        }
        if (colorPeakSearch == null){
            colorPeakSearch = ip.getPanelImatge().getColorPeakSearch();
        }else{
            ip.getPanelImatge().setColorPeakSearch(colorPeakSearch);
        }
        if (colorPeakSearchSelected == null){
            colorPeakSearchSelected = ip.getPanelImatge().getColorPeakSearchSelected();
        }else{
            ip.getPanelImatge().setColorPeakSearchSelected(colorPeakSearchSelected);
        }
        if (colorQLcomp == null){
            colorQLcomp = ip.getPanelImatge().getColorQLcomp();
        }else{
            ip.getPanelImatge().setColorQLcomp(colorQLcomp);
        }
        if (colorDBcomp == null){
            colorDBcomp = ip.getPanelImatge().getColorDBcomp();
        }else{
            ip.getPanelImatge().setColorDBcomp(colorDBcomp);
        }
    }
    
    //POSEM SEMPRE LA COMPROVACIO DE SI EL VALOR PARSEJAT ES NULL PERQUÈ EN AQUEST CAS FEM SERVIR EL QUE VE DE INITPARS
    // QUE PODRIA NO SER NULL!!!
    public static boolean readParFile(){
        initEarlyPars();
        File confFile = new File(configFilePath);
        if (!confFile.exists()){
            return false;
        }
        try {
            Scanner scParFile = new Scanner(confFile);
            
            while (scParFile.hasNextLine()){
                String line = scParFile.nextLine();
                if (line.trim().startsWith("#"))continue;
                int iigual=line.indexOf("=")+1;
                if (iigual<0)continue;
                
                if (FileUtils.containsIgnoreCase(line, "workdir")){
                    String sworkdir = (line.substring(iigual, line.trim().length()).trim());
                    if (new File(sworkdir).exists())workdir = sworkdir;
                }
                
                if (FileUtils.containsIgnoreCase(line, "QuickListDB")){
                    String sQLfile = (line.substring(iigual, line.trim().length()).trim());
                    if (new File(sQLfile).exists())QLfile = sQLfile;
                }
                if (FileUtils.containsIgnoreCase(line, "CompoundDB")){
                    String sDBfile = (line.substring(iigual, line.trim().length()).trim());
                    if (new File(sDBfile).exists())DBfile = sDBfile;
                }
                
                if (FileUtils.containsIgnoreCase(line, "IniWidth")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Integer ivalue = parseInteger(value);
                    if(ivalue!=null)def_Width = ivalue.intValue();
                }
                if (FileUtils.containsIgnoreCase(line, "IniHeight")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Integer ivalue = parseInteger(value);
                    if(ivalue!=null)def_Height = ivalue.intValue();
                }

                if(!overrideLogLevelConfigFile){
                    if (FileUtils.containsIgnoreCase(line, "logging")){
                        String logstr = (line.substring(iigual, line.trim().length()).trim());
                        Boolean bvalue = parseBoolean(logstr);
                        if(bvalue!=null)logging = bvalue.booleanValue();
                    }
                    if (FileUtils.containsIgnoreCase(line, "loglevel")){
                        String loglvl = (line.substring(iigual, line.trim().length()).trim());
                        if (FileUtils.containsIgnoreCase(loglvl, "debug")||FileUtils.containsIgnoreCase(loglvl, "config"))loglevel = "config";
                        if (FileUtils.containsIgnoreCase(loglvl, "fine"))loglevel = "fine";
                        if (FileUtils.containsIgnoreCase(loglvl, "warning"))loglevel = "warning";
                        if (FileUtils.containsIgnoreCase(loglvl, "info"))loglevel = "info";
                    }
                }
                
                if (FileUtils.containsIgnoreCase(line, "sideControls")){
                    String sidestr = (line.substring(iigual, line.trim().length()).trim());
                    if (FileUtils.containsIgnoreCase(sidestr, "false")||FileUtils.containsIgnoreCase(sidestr, "no")||FileUtils.containsIgnoreCase(sidestr, "f")){
                        sideControls = false;
                    }
                    if (FileUtils.containsIgnoreCase(sidestr, "true")||FileUtils.containsIgnoreCase(sidestr, "yes")||FileUtils.containsIgnoreCase(sidestr, "t")){
                        sideControls = true;
                    }
                }
                
                //added parameters (they already have the default value)
                if (FileUtils.containsIgnoreCase(line, "minDspacingToSearch")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)minDspacingToSearch = fvalue.floatValue();
                }
                
                if (FileUtils.containsIgnoreCase(line, "colorClickPoints")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorClickPoints = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorClickPointsCircle")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorClickPointsCircle = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorQLcomp")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorQLcomp = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorDBcomp")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorDBcomp = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorPeakSearch")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorPeakSearch = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorPeakSearchSelected")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorPeakSearchSelected = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorCallibEllipses")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorCallibEllipses = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorGuessPointsEllipses")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorGuessPointsEllipses = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorFittedEllipses")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorFittedEllipses = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorBoundariesEllipses")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorBoundariesEllipses = cvalue;
                }
                if (FileUtils.containsIgnoreCase(line, "colorExcludedZones")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Color cvalue = parseColorName(value);
                    if (cvalue!=null)colorExcludedZones = cvalue;
                }
                
                if (FileUtils.containsIgnoreCase(line, "clickPointSize")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Integer ivalue = parseInteger(value);
                    if(ivalue!=null)clickPointSize = ivalue.intValue();
                }
                if (FileUtils.containsIgnoreCase(line, "t2tolDegClickPoints")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)t2tolDegClickPoints = fvalue.floatValue();
                }
                if (FileUtils.containsIgnoreCase(line, "incZoom")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)incZoom = fvalue.floatValue();
                }
                if (FileUtils.containsIgnoreCase(line, "maxScaleFit")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)maxScaleFit = fvalue.floatValue();
                }
                if (FileUtils.containsIgnoreCase(line, "minScaleFit")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)minScaleFit = fvalue.floatValue();
                }
                if (FileUtils.containsIgnoreCase(line, "hklfontSize")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Integer ivalue = parseInteger(value);
                    if(ivalue!=null)hklfontSize = ivalue.intValue();
                }
                if (FileUtils.containsIgnoreCase(line, "factorAutoContrast")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)factorAutoContrast = fvalue.floatValue();
                }
                
                if (FileUtils.containsIgnoreCase(line, "dincoSolPointSize")){
                    if (FileUtils.containsIgnoreCase(line, "ByFc")){
                        String value = (line.substring(iigual, line.trim().length()).trim());
                        Boolean bvalue = parseBoolean(value);
                        if (bvalue!=null)dincoSolPointSizeByFc=bvalue.booleanValue();
                    }else{
                        String value = (line.substring(iigual, line.trim().length()).trim());
                        Integer ivalue = parseInteger(value);
                        if(ivalue!=null)dincoSolPointSize = ivalue.intValue();
                        
                    }
                }
                if (FileUtils.containsIgnoreCase(line, "dincoSolPointStrokeSize")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)dincoSolPointStrokeSize = fvalue.floatValue();
                }
                if (FileUtils.containsIgnoreCase(line, "dincoSolPointFill")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Boolean bvalue = parseBoolean(value);
                    if (bvalue!=null)dincoSolPointFill=bvalue.booleanValue();
                }
            }
            //per si ha canviat el loglevel/logging
            initLogger(D2Dplot_global.class.getName()); //during the par reading
            scParFile.close();
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("error reading config file");
            setConfigFileReaded(false);
            return false;
        }
        setConfigFileReaded(true);
        return true;
        
    }
    
    public static boolean writeParFile(){
        
        try {
            File f = new File(configFilePath);
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(f)));

            // ESCRIBIM AL FITXER:
            output.println("** D2Dplot configuration file **");
            output.println("# Global");
            output.println("workdir = "+workdir);
            output.println("logging = "+Boolean.toString(logging));
            output.println("loglevel = "+loglevel);
            output.println("sideControls = "+Boolean.toString(sideControls));
            output.println(String.format("%s = %d", "IniWidth",def_Width));
            output.println(String.format("%s = %d", "IniHeight",def_Height));
            
            output.println("# General plotting");
            output.println("colorClickPoints = "+getColorName(colorClickPoints));
            output.println("colorClickPointsCircle = "+getColorName(colorClickPointsCircle));
            output.println(String.format("%s = %d", "clickPointSize",clickPointSize));
            output.println(String.format("%s = %.4f", "t2tolDegClickPoints",t2tolDegClickPoints));
            output.println(String.format("%s = %.4f", "incZoom",incZoom));
            output.println(String.format("%s = %.3f", "maxScaleFit",maxScaleFit));
            output.println(String.format("%s = %.3f", "minScaleFit",minScaleFit));
            output.println(String.format("%s = %.3f", "factorAutoContrast",factorAutoContrast));
            
            output.println("# Compound DB");
            output.println("defQuickListDB = "+QLfile);
            output.println("defCompoundDB = "+DBfile);
            output.println(String.format("%s = %.4f", "minDspacingToSearch",minDspacingToSearch));
            output.println("colorQLcomp = "+getColorName(colorQLcomp));
            output.println("colorDBcomp = "+getColorName(colorDBcomp));
            
            output.println("# DINCO");
            output.println(String.format("%s = %d", "hklfontSize",hklfontSize));
            output.println("dincoSolPointSizeByFc = "+Boolean.toString(dincoSolPointSizeByFc));
            output.println(String.format("%s = %d", "dincoSolPointSize",dincoSolPointSize));
            output.println(String.format("%s = %.1f", "dincoSolPointStrokeSize",dincoSolPointStrokeSize));
            output.println("dincoSolPointFill = "+Boolean.toString(dincoSolPointFill));

            output.println("# Calib");
            output.println("colorCallibEllipses = "+getColorName(colorCallibEllipses));
            output.println("colorGuessPointsEllipses = "+getColorName(colorGuessPointsEllipses));
            output.println("colorFittedEllipses = "+getColorName(colorFittedEllipses));
            output.println("colorBoundariesEllipses = "+getColorName(colorPeakSearchSelected));
            
            output.println("# PeakSearch and Excluded Zones");
            output.println("colorPeakSearch = "+getColorName(colorPeakSearch));
            output.println("colorPeakSearchSelected = "+getColorName(colorPeakSearchSelected));
            output.println("colorExcludedZones = "+getColorName(colorCallibEllipses));            

            output.close();

        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("error writing confing file");
            return false;
        }
        return true;
    }
    
    
    public static void initLogger(String name){
        log = new VavaLogger(name);
        log.setLogLevel(loglevel);
        log.enableLogger(logging);
    }
    
    public static VavaLogger getVavaLogger(String name){
        VavaLogger l = new VavaLogger(name);
        l.setLogLevel(loglevel);
        l.enableLogger(logging);
        return l;
    }
    
    public static String getWorkdir() {
        return workdir;
    }
    
    public static File getWorkdirFile() {
        return new File(workdir);
    }

    public static void setWorkdir(String workdir) {
//        D2Dplot_global.workdir = workdir;
        setWorkdir(new File(workdir));
    }
    
    public static void setWorkdir(File workDirOrFile) {
//        D2Dplot_global.workdir = workDirOrFile.getParent();
        D2Dplot_global.workdir = new File(workDirOrFile.getAbsolutePath()).getParent();
        if (D2Dplot_global.workdir==null)D2Dplot_global.workdir=".";
    }
    
    public static Boolean isConfigFileReaded() {
        return configFileReaded;
    }

    public static void setConfigFileReaded(boolean configFileReaded) {
        D2Dplot_global.configFileReaded = configFileReaded;
    }

    public static Color parseColorName(String name){
        if (FileUtils.containsIgnoreCase(name, "black")) return Color.black;
        if (FileUtils.containsIgnoreCase(name, "green")) return Color.green;
        if (FileUtils.containsIgnoreCase(name, "red")) return Color.red;
        if (FileUtils.containsIgnoreCase(name, "cyan")) return Color.cyan;
        if (FileUtils.containsIgnoreCase(name, "yellow")) return Color.yellow;
        if (FileUtils.containsIgnoreCase(name, "magenta")) return Color.magenta;
        if (FileUtils.containsIgnoreCase(name, "orange")) return Color.orange;
        if (FileUtils.containsIgnoreCase(name, "pink")) return Color.pink;
        if (FileUtils.containsIgnoreCase(name, "blue")) return Color.blue;
        if (FileUtils.containsIgnoreCase(name, "white")) return Color.white;
        return null;
    }
    
    private static String getColorName(Color c){
        if (c==Color.black) return "black";
        if (c==Color.green) return "green";
        if (c==Color.red) return "red";
        if (c==Color.cyan) return "cyan";
        if (c==Color.yellow) return "yellow";
        if (c==Color.magenta) return "magenta";
        if (c==Color.orange) return "orange";
        if (c==Color.pink) return "pink";
        if (c==Color.blue) return "blue";
        if (c==Color.white) return "white";
        return "";
    }
    
    private static Float parseFloat(String value){
        Float f=null;
        try{
            f = Float.parseFloat(value);
        }catch(Exception e){
            log.config("error parsing float "+value);
        }
        return f;
    }
    
    private static Boolean parseBoolean(String value){
        Boolean b=null;
        if (FileUtils.containsIgnoreCase(value, "false")||FileUtils.containsIgnoreCase(value, "no")||FileUtils.containsIgnoreCase(value, "f")){
            b = false;
        }
        if (FileUtils.containsIgnoreCase(value, "true")||FileUtils.containsIgnoreCase(value, "yes")||FileUtils.containsIgnoreCase(value, "t")){
            b = true;
        }
        return b;
    }
    
    private static Integer parseInteger(String value){
        Integer i=null;
        try{
            i = Integer.parseInt(value);
        }catch(Exception e){
            log.config("error parsing integer "+value);
        }
        return i;
    }
    
    //returns true if logging is enabled and level is <= config
    public static boolean isDebug(){
        if (logging){
            if (loglevel.equalsIgnoreCase("config")||loglevel.equalsIgnoreCase("debug")||loglevel.equalsIgnoreCase("fine")||loglevel.equalsIgnoreCase("finest")){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the complimentary (opposite) color.
     * @param color int RGB color to return the compliment of
     * @return int RGB of compliment color
     */
    public static Color getComplimentColor(Color color) {
      // get existing colors
      int alpha = color.getAlpha();
      int red = color.getRed();
      int blue = color.getBlue();
      int green = color.getGreen();
      
      // find compliments
      red = (~red) & 0xff;
      blue = (~blue) & 0xff;
      green = (~green) & 0xff;
      
      return new Color(red, green, blue,alpha);
    }

    public static boolean isKeepCalibration() {
        return D2Dplot_global.keepCalibration;
    }

    public static void setKeepCalibration(boolean keepCalib) {
        D2Dplot_global.keepCalibration = keepCalib;
    }

    public static float getDistMD() {
        return distMD;
    }

    public static void setDistMD(float distMD) {
        D2Dplot_global.distMD = distMD;
    }

    public static float getCentX() {
        return centX;
    }

    public static void setCentX(float centX) {
        D2Dplot_global.centX = centX;
    }

    public static float getCentY() {
        return centY;
    }

    public static void setCentY(float centY) {
        D2Dplot_global.centY = centY;
    }

    public static float getTilt() {
        return tilt;
    }

    public static void setTilt(float tilt) {
        D2Dplot_global.tilt = tilt;
    }

    public static float getRot() {
        return rot;
    }

    public static void setRot(float rot) {
        D2Dplot_global.rot = rot;
    }
    
    public static Color getColorEXZ() {
        return colorEXZ;
    }

    public static void setColorEXZ(Color colorEXZ) {
        D2Dplot_global.colorEXZ = colorEXZ;
    }

    public static void setCalib(float dist, float cX, float cY, float tilt, float rot){
        setRot(rot);
        setTilt(tilt);
        setCentX(cX);
        setCentY(cY);
        setDistMD(dist);
    }

    
    public static void printAllOptions(String loglevel){
        log.printmsg(loglevel,"*************************** CURRENT CONFIGURATION ***************************");
        log.printmsg(loglevel,"# Global");
        log.printmsg(loglevel,"workdir = "+workdir);
        log.printmsg(loglevel,"logging = "+Boolean.toString(logging));
        log.printmsg(loglevel,"loglevel = "+D2Dplot_global.loglevel);
        log.printmsg(loglevel,"sideControls = "+Boolean.toString(sideControls));
        log.printmsg(loglevel,String.format("%s = %d", "IniWidth",def_Width));
        log.printmsg(loglevel,String.format("%s = %d", "IniHeight",def_Height));
        
        log.printmsg(loglevel,"# General plotting");
        log.printmsg(loglevel,"colorClickPoints = "+getColorName(colorClickPoints));
        log.printmsg(loglevel,"colorClickPointsCircle = "+getColorName(colorClickPointsCircle));
        log.printmsg(loglevel,String.format("%s = %d", "clickPointSize",clickPointSize));
        log.printmsg(loglevel,String.format("%s = %.4f", "t2tolDegClickPoints",t2tolDegClickPoints));
        log.printmsg(loglevel,String.format("%s = %.4f", "incZoom",incZoom));
        log.printmsg(loglevel,String.format("%s = %.3f", "maxScaleFit",maxScaleFit));
        log.printmsg(loglevel,String.format("%s = %.3f", "minScaleFit",minScaleFit));
        log.printmsg(loglevel,String.format("%s = %.3f", "factorAutoContrast",factorAutoContrast));

        log.printmsg(loglevel,"# Compound DB");
        log.printmsg(loglevel,"defQuickListDB = "+QLfile);
        log.printmsg(loglevel,"defCompoundDB = "+DBfile);
        log.printmsg(loglevel,String.format("%s = %.4f", "minDspacingToSearch",minDspacingToSearch));
        log.printmsg(loglevel,"colorQLcomp = "+getColorName(colorQLcomp));
        log.printmsg(loglevel,"colorDBcomp = "+getColorName(colorDBcomp));

        log.printmsg(loglevel,"# DINCO");
        log.printmsg(loglevel,String.format("%s = %d", "hklfontSize",hklfontSize));
        log.printmsg(loglevel,"dincoSolPointSizeByFc = "+Boolean.toString(dincoSolPointSizeByFc));
        log.printmsg(loglevel,String.format("%s = %d", "dincoSolPointSize",dincoSolPointSize));
        log.printmsg(loglevel,String.format("%s = %.1f", "dincoSolPointStrokeSize",dincoSolPointStrokeSize));
        log.printmsg(loglevel,"dincoSolPointFill = "+Boolean.toString(dincoSolPointFill));

        log.printmsg(loglevel,"# Calib");
        log.printmsg(loglevel,"colorCallibEllipses = "+getColorName(colorCallibEllipses));
        log.printmsg(loglevel,"colorGuessPointsEllipses = "+getColorName(colorGuessPointsEllipses));
        log.printmsg(loglevel,"colorFittedEllipses = "+getColorName(colorFittedEllipses));
        log.printmsg(loglevel,"colorBoundariesEllipses = "+getColorName(colorPeakSearchSelected));

        log.printmsg(loglevel,"# PeakSearch and Excluded Zones");
        log.printmsg(loglevel,"colorPeakSearch = "+getColorName(colorPeakSearch));
        log.printmsg(loglevel,"colorPeakSearchSelected = "+getColorName(colorPeakSearchSelected));
        log.printmsg(loglevel,"colorExcludedZones = "+getColorName(colorCallibEllipses));         
        log.printmsg(loglevel,"*****************************************************************************");
    }
    
    public static String getStringTimeStamp(String simpleDateFormatStr){
        SimpleDateFormat fHora = new SimpleDateFormat(simpleDateFormatStr);
        return fHora.format(new Date());
    }
}

//EXAMPLE (may be incomplete...)
//** D2Dplot parameter file **
//# Global
//workdir = /home/ovallcorba/ovallcorba/Progs_dev/2DXRD_UI/test_calib_integ/
//logging = true
//loglevel = config
//sideControls = true;
//# DB related
//defQuickListDB = /home/ovallcorba/ovallcorba/eclipse_ws/D2Dplot_current/quicklist.db
//defCompoundDB = /home/ovallcorba/ovallcorba/eclipse_ws/D2Dplot_current/default.db
//minDspacingToSearch = 1.15
//#plot
//colorClickPoints = Color.green;
//colorClickPointsCircle = Color.red;
//colorQLcomp = Color.green;
//colorDBcomp = Color.cyan;
//colorPeakSearch = Color.green;
//colorPeakSearchSelected = Color.red;
//colorCallibEllipses = Color.orange;
//colorGuessPointsEllipses = Color.red;
//colorFittedEllipses = Color.green;
//colorBoundariesEllipses = Color.magenta;
//colorExcludedZones = Color.CYAN;
//clickPointSize = 6;
//t2tolDegClickPoints = 0.05;
//incZoom = 0.05;
//maxScaleFit = 25.0;
//minScaleFit = 0.10;
//hklfontSize=13;
//factSliderMax=3.0;
//# Calib
//outliersFactSD = 1.5f;
//showAltCenter = false;
//considerGlobalRot = true;
//forceGlobalRot = false;