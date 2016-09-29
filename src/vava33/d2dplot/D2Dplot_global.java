package vava33.d2dplot;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import vava33.d2dplot.auxi.PDDatabase;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.PuntClick;
import vava33.d2dplot.auxi.PuntSolucio;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public final class D2Dplot_global {

    public static final int satur32 = Short.MAX_VALUE;
    public static final int satur65 = (Short.MAX_VALUE * 2) + 1;
    public static final String welcomeMSG = "d2Dplot v1609 (160929) by OV";
//    public static final String welcomeMSG = "d2Dplot v1609 (160928) by OV == DEVELOPMENT VERSION, use at your own risk ==";
    public static final String separator = System.getProperty("file.separator");
    public static final String binDir = System.getProperty("user.dir") + separator + "bin" + separator;
    public static final String userDir = System.getProperty("user.dir");
    public static final String configFilePath = System.getProperty("user.dir") + separator + "d2dconfig.cfg";
    public static final String usersGuidePath = System.getProperty("user.dir") + separator + "d2Dplot_userguide.pdf";
    public static Boolean configFileReaded = null; //true=readed false=errorReading null=notFound
//    public static ArrayList<PDCompound> quicklist;

    //symbols and characters
    public static final String theta = "\u03B8";
    public static final String angstrom= "\u212B";

    public static VavaLogger log;

//*** parametres que es poden canviar a les opcions *****
    //global 
    public static boolean logging = true;
    public static String loglevel = "config"; //info, config, etc...
    public static String workdir = System.getProperty("user.dir");
    public static boolean sideControls = true;

    //DB
    public static String DBfile;
    public static String QLfile;
    private static Float minDspacingToSearch;

    //Calib
    private static Float outliersFactSD;
    private static Boolean showAltCenter;
    private static Boolean considerGlobalRot;
    private static Boolean forceGlobalRot;
    private static Boolean rejectOutliers;
    private static Float searchElliLimitFactor;

    //ImagePanel and pattern2D
    private static Float t2tolDegClickPoints; //in patt2d
    private static Float incZoom;
    private static Float maxScaleFit;
    private static Float minScaleFit;
    private static Integer hklfontSize;
    private static Float factSliderMax;
    private static Color colorCallibEllipses;
    private static Color colorGuessPointsEllipses;
    private static Color colorFittedEllipses;
    private static Color colorBoundariesEllipses;
    private static Color colorExcludedZones;
    private static Color colorPeakSearch;
    private static Color colorPeakSearchSelected;
    private static Color colorQLcomp;
    private static Color colorDBcomp;

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
        initLogger("d2dplot"); //during the par reading
        
        //global
        logging = true;
        loglevel = "info"; //info, config, etc...
        workdir = System.getProperty("user.dir");
        sideControls = true;
        
        //now initilize the others with the NONE value
        //DB (dels DBfiles ja s'encarrega checkDBs
        DBfile=null;
        QLfile=null;
        minDspacingToSearch=null;

        //Calib
        outliersFactSD = null;
        showAltCenter = null;
        considerGlobalRot = null;
        forceGlobalRot = null;
        rejectOutliers = null;
        searchElliLimitFactor = null;
        
        //ImagePanel and pattern2D
        t2tolDegClickPoints = null; //in patt2d
        incZoom = null;
        maxScaleFit=null;
        minScaleFit=null;
        hklfontSize=null;
        factSliderMax=null;
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

        //Calib
        if (outliersFactSD == null){
            outliersFactSD = Calib_dialog.getOutliersFactSD();
        }else{
            Calib_dialog.setOutliersFactSD(outliersFactSD.floatValue());
        }
        if (showAltCenter == null){
            showAltCenter = Calib_dialog.isShowAltCenter();
        }else{
            Calib_dialog.setShowAltCenter(showAltCenter.booleanValue());
        }
        if (considerGlobalRot == null){
            considerGlobalRot = Calib_dialog.isConsiderGlobalRot();
        }else{
            Calib_dialog.setConsiderGlobalRot(considerGlobalRot.booleanValue());
        }        
        if (forceGlobalRot == null){
            forceGlobalRot = Calib_dialog.isForceGlobalRot();
        }else{
            Calib_dialog.setForceGlobalRot(forceGlobalRot.booleanValue());
        }
        if (rejectOutliers == null){
            rejectOutliers = Calib_dialog.isRejectOutliers();
        }else{
            Calib_dialog.setRejectOutliers(rejectOutliers.booleanValue());
        }
        if (searchElliLimitFactor == null){
            searchElliLimitFactor = Calib_dialog.getSearchElliLimitFactor();
        }else{
            Calib_dialog.setSearchElliLimitFactor(searchElliLimitFactor.floatValue());
        }
        
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
        if (factSliderMax == null){
            factSliderMax = ImagePanel.getFactSliderMax();
        }else{
            ImagePanel.setFactSliderMax(factSliderMax.floatValue());
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
    
    public static void initDefaultDBs(){
        DBfile = PDDatabase.getLocalDB();
        QLfile = PDDatabase.getLocalQL();
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
                if (FileUtils.containsIgnoreCase(line, "factSliderMax")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)factSliderMax = fvalue.floatValue();
                }
                
                if (FileUtils.containsIgnoreCase(line, "outliersFactSD")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)outliersFactSD = fvalue.floatValue();
                }
                if (FileUtils.containsIgnoreCase(line, "searchElliLimitFactor")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Float fvalue = parseFloat(value);
                    if(fvalue!=null)searchElliLimitFactor = fvalue.floatValue();
                }
                
                if (FileUtils.containsIgnoreCase(line, "showAltCenter")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Boolean bvalue = parseBoolean(value);
                    if (bvalue!=null)showAltCenter=bvalue.booleanValue();
                }
                if (FileUtils.containsIgnoreCase(line, "considerGlobalRot")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Boolean bvalue = parseBoolean(value);
                    if (bvalue!=null)considerGlobalRot=bvalue.booleanValue();
                }
                if (FileUtils.containsIgnoreCase(line, "forceGlobalRot")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Boolean bvalue = parseBoolean(value);
                    if (bvalue!=null)forceGlobalRot=bvalue.booleanValue();
                }
                if (FileUtils.containsIgnoreCase(line, "rejectOutliers")){
                    String value = (line.substring(iigual, line.trim().length()).trim());
                    Boolean bvalue = parseBoolean(value);
                    if (bvalue!=null)rejectOutliers=bvalue.booleanValue();
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
            
            output.println("# General plotting");
            output.println("colorClickPoints = "+getColorName(colorClickPoints));
            output.println("colorClickPointsCircle = "+getColorName(colorClickPointsCircle));
            output.println(String.format("%s = %d", "clickPointSize",clickPointSize));
            output.println(String.format("%s = %.4f", "t2tolDegClickPoints",t2tolDegClickPoints));
            output.println(String.format("%s = %.4f", "incZoom",incZoom));
            output.println(String.format("%s = %.3f", "maxScaleFit",maxScaleFit));
            output.println(String.format("%s = %.3f", "minScaleFit",minScaleFit));
            output.println(String.format("%s = %.3f", "factSliderMax",factSliderMax));
            
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
            output.println(String.format("%s = %.3f", "outliersFactSD",outliersFactSD));
            output.println(String.format("%s = %.2f", "searchElliLimitFactor",searchElliLimitFactor));
            output.println("showAltCenter = "+Boolean.toString(showAltCenter));
            output.println("considerGlobalRot = "+Boolean.toString(considerGlobalRot));
            output.println("forceGlobalRot = "+Boolean.toString(forceGlobalRot));
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
        log.enableLogger(logging);
        log.setLogLevel(loglevel);
    }
    
    public static String getWorkdir() {
        return workdir;
    }

    public static void setWorkdir(String workdir) {
//        D2Dplot_global.workdir = workdir;
        setWorkdir(new File(workdir));
    }
    
    public static void setWorkdir(File workDirOrFile) {
        D2Dplot_global.workdir = workDirOrFile.getParent();
    }
    
    public static Boolean isConfigFileReaded() {
        return configFileReaded;
    }

    public static void setConfigFileReaded(boolean configFileReaded) {
        D2Dplot_global.configFileReaded = configFileReaded;
    }

    private static Color parseColorName(String name){
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