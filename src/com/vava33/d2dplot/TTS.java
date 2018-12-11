package com.vava33.d2dplot;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.tts.AboutTTS_dialog;
import com.vava33.d2dplot.tts.BackgroundPanel;
import com.vava33.d2dplot.tts.Settings_dialog;
import com.vava33.d2dplot.tts.TSDfile;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

import javax.swing.JSplitPane;

import com.vava33.jutils.LogJTextArea;

import javax.swing.JButton;
import javax.swing.JEditorPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math3.util.FastMath;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

public class TTS_frame extends JFrame {

    private static final long serialVersionUID = -3907664630337090114L;
    private static int window_width = 960;
    private static int window_height = 600;
    
    private static final String userGuideFile="Write_up_tts_software.pdf";
//    private static final String cfgFile = "prefs";
    private static final String ttsVersion="1609 [160930]";
    private static final String welcomeMSG = " --> tts software UI - version "+ttsVersion+" by OV\n"
                                           + " |    tts software main author: J.Rius (ICMAB-CSIC)\n"
                                           + " |    Main collaborators: O.Vallcorba (ALBA-CELLS), C.Frontera (ICMAB-CSIC)\n"
                                           + " |    Report of errors/suggestions/comments to the authors are appreciated\n"
                                           + " |    Click on the [?] button for user's guide and conditions of use.\n";
    private static final int max_logo_width = 1200;
    private static final int min_logo_width = 600; 
    private static final String code = "110606";
//    private static String d2DplotPath = "(not set)";
    
    private static final String incoExecPath = FileUtils.getSeparator()+"bin"+FileUtils.getSeparator()+"tts_inco";
    private static final String mergeExecPath = FileUtils.getSeparator()+"bin"+FileUtils.getSeparator()+"tts_merge";
    private static final String celrefExecPath = FileUtils.getSeparator()+"bin"+FileUtils.getSeparator()+"tts_celref";
    private static String incoExec = "";
    private static String mergeExec = "";
    private static String celrefExec = "";
    private static String maskFileName = "MASK.BIN";
    
    private JPanel contentPane;
    private Settings_dialog prefs;
    private LogJTextArea txtOut;
    private JLabel lblWorkingFile;
    private JProgressBar progressBar;
    private JButton btnRunInco;
    private JButton btnRunMerge;
    private JButton btnRunCelRef;
    private JButton btnStop;
        
    private File currentTSD;
    private TSDfile tsdfile;
    private static Process TTS_process;
    protected InputStreamReader sortidaProg;
    private static boolean TTSInExecution=false;
    private static boolean TTSRunStopped=false;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(TTS_frame.class.getName());
    
    private MainFrame d2DplotMain;

    /**
     * Create the frame.
     */
    public TTS_frame(MainFrame mf) {
        d2DplotMain = mf;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setBounds(100, 100, 780, 580); //780x600 inicial per provar
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("insets 0", "[grow]", "[grow][]"));
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.2);
        splitPane.setContinuousLayout(true);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane, "cell 0 0,grow");
        
        JPanel panel_top = new JPanel();
        splitPane.setLeftComponent(panel_top);
        panel_top.setLayout(new MigLayout("insets 0", "[grow][grow][grow]", "[100px,grow][][][]"));
        
        JPanel panel_current = new JPanel();
        panel_top.add(panel_current, "flowx,cell 0 1 3 1,grow");
        panel_current.setLayout(new MigLayout("insets 2", "[][][][][grow]", "[][]"));
        
        JButton btnOpenTsd = new JButton("Open TSD");
        btnOpenTsd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnOpenTsd_actionPerformed(arg0);
            }
        });
        panel_current.add(btnOpenTsd, "cell 0 0");
        
        JButton btnEdit = new JButton("Edit TSD");
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnEdit_actionPerformed(e);
            }
        });
        panel_current.add(btnEdit, "cell 1 0,aligny center");
        
        JButton btnGenerateTsd = new JButton("Generate template TSD");
        btnGenerateTsd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnGenerateTsd_actionPerformed(e);
            }
        });
        
        JButton btnCopyTsd = new JButton("Copy TSD");
        btnCopyTsd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCopyTsd_actionPerformed(e);
            }
        });
        panel_current.add(btnCopyTsd, "cell 2 0");
        panel_current.add(btnGenerateTsd, "cell 3 0");
        
        JLabel lblCurrentFile = new JLabel("WorkFile:");
        panel_current.add(lblCurrentFile, "cell 0 1,alignx right,aligny baseline");
        
        lblWorkingFile = new JLabel("(none)");
        lblWorkingFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                do_lblWorkingFile_mouseReleased(e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                do_lblWorkingFile_mouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                do_lblWorkingFile_mouseExited(e);
            }
        });
        panel_current.add(lblWorkingFile, "cell 1 1 4 1,growx,aligny baseline");
        
        BackgroundPanel panel_logo = new BackgroundPanel(new ImageIcon(getClass().getResource("/img/tts_splash_760x120.png")).getImage(),TTS_frame.getMaxLogoWidth(),TTS_frame.getMinLogoWidth());
        panel_top.add(panel_logo, "cell 0 0 3 1,grow");
        panel_logo.setLayout(new MigLayout("insets 5", "[grow][]", "[][][grow]"));
        
        JButton btnSettings = new JButton("");
        btnSettings.setPreferredSize(new Dimension(30, 30));
        btnSettings.setMinimumSize(new Dimension(30, 30));
        btnSettings.setMargin(new Insets(2, 2, 2, 2));
        btnSettings.setIcon(new ImageIcon(getClass().getResource("/img/config_small.png")));
        
        btnSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnSettings_actionPerformed(e);
            }
        });
        panel_logo.add(btnSettings, "cell 0 0,alignx right,aligny bottom");
        
        JButton btnManual = new JButton("");
        btnManual.setPreferredSize(new Dimension(30, 30));
        btnManual.setMinimumSize(new Dimension(30, 30));
        btnManual.setMargin(new Insets(2, 2, 2, 2));
        btnManual.setIcon(new ImageIcon(getClass().getResource("/img/interrogant.png")));
        btnManual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnManual_actionPerformed(e);
            }
        });
        panel_logo.add(btnManual, "cell 0 1,alignx right,aligny center");
        
        JPanel panel_inco = new JPanel();
        panel_inco.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "tts_Inco", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_top.add(panel_inco, "cell 0 2,grow");
        panel_inco.setLayout(new MigLayout("insets 2", "[grow][grow]", "[][]"));
        
        btnRunInco = new JButton("RUN");
        btnRunInco.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRunInco_actionPerformed(e);
            }
        });
        panel_inco.add(btnRunInco, "cell 0 0,growx");
        
        JButton btnCheckOut = new JButton("View OUT");
        btnCheckOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCheckOut_actionPerformed(e);
            }
        });
        panel_inco.add(btnCheckOut, "cell 1 0,growx");
        
        JButton btnChecksol = new JButton("Check SOL");
        btnChecksol.setToolTipText("with D2Dplot");
        btnChecksol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnChecksol_actionPerformed(arg0);
            }
        });
        panel_inco.add(btnChecksol, "cell 0 1,growx");
        
        JButton btnCreateTsdIref = new JButton("Create TSD IREF=1");
        btnCreateTsdIref.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCreateTsdIref_actionPerformed(e);
            }
        });
        panel_inco.add(btnCreateTsdIref, "cell 1 1,growx");
        
        JPanel panel_merge = new JPanel();
        panel_merge.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "tts_Merge", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_top.add(panel_merge, "cell 1 2,grow");
        panel_merge.setLayout(new MigLayout("insets 2", "[grow][]", "[][]"));
        
        btnRunMerge = new JButton("RUN");
        btnRunMerge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRunMerge_actionPerformed(e);
            }
        });
        panel_merge.add(btnRunMerge, "cell 0 0,growx");
        
        JButton btnCheckMrg = new JButton("View MRG");
        btnCheckMrg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCheckMrg_actionPerformed(e);
            }
        });
        panel_merge.add(btnCheckMrg, "cell 1 0,growx");
        
        JButton btnMultdom = new JButton("Create TSD MULTDOM=1");
        btnMultdom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnMultdom_actionPerformed(e);
            }
        });
        panel_merge.add(btnMultdom, "cell 0 1 2 1,growx");
        
        JPanel panelCelref = new JPanel();
        panelCelref.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "tts_Celref", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_top.add(panelCelref, "cell 2 2,grow");
        panelCelref.setLayout(new MigLayout("insets 2", "[grow][]", "[][]"));
        
        btnRunCelRef = new JButton("RUN");
        btnRunCelRef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRunCelRef_actionPerformed(e);
            }
        });
        panelCelref.add(btnRunCelRef, "cell 0 0,growx");
        
        JButton btnCheckCel = new JButton("View CEL");
        btnCheckCel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCheckCel_actionPerformed(e);
            }
        });
        panelCelref.add(btnCheckCel, "cell 1 0,growx");
        
        JButton btnCreateTsdFor = new JButton("Create TSD for CelRef");
        btnCreateTsdFor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCreateTsdFor_actionPerformed(e);
            }
        });
        panelCelref.add(btnCreateTsdFor, "cell 0 1 2 1,growx");
        
        JPanel panelExec = new JPanel();
        panel_top.add(panelExec, "cell 0 3 3 1,grow");
        panelExec.setLayout(new MigLayout("insets 2", "[grow][grow][]", "[]"));
        
        progressBar = new JProgressBar();
        panelExec.add(progressBar, "cell 0 0 2 1,grow");
        
        btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnStop_actionPerformed(e);
            }
        });
        panelExec.add(btnStop, "cell 2 0,grow");

        JPanel panel_bottom = new JPanel();
        splitPane.setRightComponent(panel_bottom);
        panel_bottom.setLayout(new MigLayout("insets 1", "[grow]", "[grow]"));
        
        JScrollPane scrollPane = new JScrollPane();
        panel_bottom.add(scrollPane, "cell 0 0,grow");
        
        txtOut = new LogJTextArea();
        scrollPane.setViewportView(txtOut);
        
        JTextPane txtpnAaa = new JTextPane();
        txtpnAaa.setContentType("text/html");
        
        
        txtpnAaa.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        txtpnAaa.setEditable(false);
        
        txtpnAaa.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if(Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception e1) {
                            if(D2Dplot_global.isDebug())e1.printStackTrace();
                        }
                    }
                }
            }
        });
        txtpnAaa.setOpaque(false);
        
        txtpnAaa.setText("<html>Download TTS_software from: <a href=\"http://www.icmab.es/crystallography/software\">http://www.icmab.es/crystallography/software</a></html>");
        contentPane.add(txtpnAaa, "cell 0 1,growx");
        
        this.iniciaFinestra();
        this.iniciPrefs();
    
    }
    
    private void iniciaFinestra(){
        setTitle("tts software");
        //icono petit de la finestra
        final List<Image> icons = new ArrayList<Image>();
        icons.add(new ImageIcon(getClass().getResource("/img/tts_icon120x120.png")).getImage());
        this.setIconImages(icons);
        
        //Farem que es posicioni correctament i a mida bona en pantalla
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        
        this.setPreferredSize(new Dimension(TTS_frame.window_width, TTS_frame.window_height));
//        this.setMinimumSize(new Dimension(MainFrame_tts.window_width/2, MainFrame_tts.window_height/2)); //posem mida minima
        this.setMinimumSize(new Dimension(TTS_frame.getMinLogoWidth(), TTS_frame.window_height/2)); //posem mida minima

        if(screen.width<TTS_frame.window_width)TTS_frame.window_width=screen.width;
        if(screen.height<TTS_frame.window_height)TTS_frame.window_height=screen.height;
        
        //finalment redimensionem i centrem a la pantalla
        int x = (screen.width-TTS_frame.window_width)/2;
        int y = (screen.height-TTS_frame.window_height)/4; //fem entre 4 perque hi ha la barra d'inici
        this.setBounds(x,y,TTS_frame.window_width,TTS_frame.window_height);

        this.pack();// TODO: PROVAR SI CAL
//        resizeImage(MainFrame_tts.window_width);
//        txtOut.saltL();
        txtOut.ln(TTS_frame.getWelcomemsg());
        txtOut.saltL();
    }
    
    private void iniciPrefs(){
        //LLEGIM PREFERENCIES
        int stat = 0;
        try{
            stat = llegirPreferences();
        }catch(Exception e){
            if(isDebug())e.printStackTrace();
            this.print_loginfo_tAOut("error reading tts_software executables, check in preferences");
        }
        if (stat!=0){//hi ha hagut error al localitzar fitxers}
            this.print_loginfo_tAOut("tts_software_folder not found, set it in preferences");
        }
    }
    
    //Aquest metode crea la finestra preferencies, les llegeix del fitxer .\bin\prefs.cfg i assigna les opcions.
    //TAMBE SERVEIX DE COMPROVACIO QUE EXISTEIXI EL DIRECTORI BIN I ELS FITXERS NECESSARIS.
    private int llegirPreferences(){
        boolean tts_deps = this.checkTTSDependencies();
        //es creen les preferencies
        if (prefs==null){
            prefs = new Settings_dialog(this);
        }
        //mirem que existeixi tot
        if(!tts_deps){return 1;}
        return 0; //tot correcte
    }
    
    public boolean checkTTSDependencies(){
        String tts_folder = D2Dplot_global.getTTSsoftwareFolder().trim();
        boolean somethingWrong = false;
        if (tts_folder.isEmpty()) {
            this.print_loginfo_tAOut("no tts_software folder has been set");
            return false;
        }else {
            //ara buscarem els executables un a un
            this.print_loginfo_tAOut("tts folder found: "+tts_folder);
            
            File temp = new File(tts_folder+TTS_frame.incoExecPath);
            if (!temp.exists()){
                this.print_loginfo_tAOut(String.format("warning: %s not found",temp));
                somethingWrong = true;
            }else {
                TTS_frame.setIncoExec(temp.getAbsolutePath());
                this.print_loginfo_tAOut("tts_inco found: "+TTS_frame.getIncoExec());
            }
            
            temp = new File(tts_folder+TTS_frame.mergeExecPath);
            if (!temp.exists()){
                this.print_loginfo_tAOut(String.format("warning: %s not found",temp));
                somethingWrong = true;
            }else {
                TTS_frame.setMergeExec(temp.getAbsolutePath());
                this.print_loginfo_tAOut("tts_merge found: "+TTS_frame.getMergeExec());
            }
            
            temp = new File(tts_folder+TTS_frame.celrefExecPath);
            if (!temp.exists()){
                this.print_loginfo_tAOut(String.format("warning: %s not found",temp));
                somethingWrong = true;
            }else {
                TTS_frame.setCelrefExec(temp.getAbsolutePath());
                this.print_loginfo_tAOut("tts_celref found: "+TTS_frame.getCelrefExec());
            }
        }
        if (somethingWrong) {
            this.print_loginfo_tAOut("Files missing, please check that /bin/ folder inside tts_software_folder exists");
        }
        return true;
    }
    
    protected void do_btnSettings_actionPerformed(ActionEvent e) {
        if (prefs==null){
            prefs = new Settings_dialog(this);
        }
        prefs.setVisible(true);
    }
    
    protected void do_btnManual_actionPerformed(ActionEvent e) {
        AboutTTS_dialog about = new AboutTTS_dialog();
        about.setVisible(true);
    }
    
    private static boolean isDebug(){
        return D2Dplot_global.isDebug();
    }
    
    protected void do_btnOpenTsd_actionPerformed(ActionEvent arg0) {
        FileNameExtensionFilter[] filt = {new FileNameExtensionFilter("TSD file", "tsd","TSD")};
        File f = FileUtils.fchooserOpen(this, new File(D2Dplot_global.getWorkdir()), filt, 0);
        if (f!=null){
            this.updateWorkDir(f);
            this.setCurrentTSD(f);
        }
    }

    public File getCurrentTSD() {
        return currentTSD;
    }

    //SET OR UPDATE CURRENT TSD (with reading)
    public void setCurrentTSD(File currentTSD) {
        this.currentTSD = currentTSD;
        lblWorkingFile.setText(currentTSD.toString());
        txtOut.stat("File selected: "+currentTSD.toString());
        //el llegim
        try{
            this.tsdfile=new TSDfile(currentTSD.toString());    
        }catch(Exception e){
            if(isDebug())e.printStackTrace();
            log.debug("Parsing TSD failed, open files directly in D2Dplot will not work");
        }
    }
    
    protected void do_btnEdit_actionPerformed(ActionEvent e) {
        if (this.getCurrentTSD()==null){
            txtOut.stat("No working TSD file found");
            return;
        }
        this.openTEXTfile(this.getCurrentTSD());
    }
    protected void do_lblWorkingFile_mouseReleased(MouseEvent e) {
        this.openWorkDir();
    }
    protected void do_lblWorkingFile_mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.lblWorkingFile.setForeground(Color.blue);
    }
    protected void do_lblWorkingFile_mouseExited(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.lblWorkingFile.setForeground(Color.black);
    }

    private void updateWorkDir(File newDir){
        D2Dplot_global.setWorkdir(newDir.getParent());
    }
    
    private void openWorkDir(){
        if (this.getCurrentTSD()!=null){
            // vol dir que hi ha fitxer obert amb el que estem treballant
            log.debug(this.getCurrentTSD().getParentFile().toString());
            boolean opened=true;
            try {
                if(Desktop.isDesktopSupported()){
                    Desktop.getDesktop().open(this.getCurrentTSD().getParentFile());
                }else{
                    if(FileUtils.getOS().equalsIgnoreCase("win")){
                       new ProcessBuilder("explorer.exe","/select,",this.getCurrentTSD().getParent()).start();
                    }
                    if(FileUtils.getOS().equalsIgnoreCase("lin")){
                       new ProcessBuilder("nautilus",this.getCurrentTSD().getParent()).start();
                    }
                }
            } catch (Exception e) {
                if(isDebug())e.printStackTrace();
                opened=false;
            }
            if(opened)return;
            if(FileUtils.getOS().equalsIgnoreCase("lin")){
                opened=true;
                //gnome nautilus
                try{
                    new ProcessBuilder("nautilus",this.getCurrentTSD().getParent()).start();    
                }catch(Exception e){
                    if(isDebug())e.printStackTrace();
                    opened=false;
                }
                if(opened)return;
                opened=true;
                //kde dolphin
                try{
                    new ProcessBuilder("dolphin",this.getCurrentTSD().getParent()).start();    
                }catch(Exception e){
                    if(isDebug())e.printStackTrace();
                    opened=false;
                }
                if(!opened)txtOut.stat("Unable to open folder");
            }
        }
    }
    
    protected void do_btnStop_actionPerformed(ActionEvent e) {
        if(TTSInExecution){
            TTS_process.destroy();
            TTSInExecution=false;
            btnStop.setEnabled(false);
            TTSRunStopped=true;
            txtOut.stat("*************************");
            txtOut.stat("** Run stopped by user **");
            txtOut.stat("*************************");
        }
    }
    
    private class TTSRunnable implements Runnable{
        
        private static final int INCO = 0;
        private static final int MERGE = 1;
        private static final int CELREF = 2;
        
        private JButton activeButt = null;
        private String exec = "";
        private String progName= "";
//        private boolean generate = false;
        
        public TTSRunnable(int proc){
            switch(proc){
                case INCO:
                    this.activeButt = btnRunInco;
                    this.exec = TTS_frame.getIncoExec();
                    this.progName = "INCO";
                    break;
                case MERGE:
                    activeButt = btnRunMerge;
                    this.exec = TTS_frame.getMergeExec();
                    this.progName = "MERGE";
                    break;
                case CELREF:
                    activeButt = btnRunCelRef;
                    this.exec = TTS_frame.getCelrefExec();
                    this.progName = "CELREF";
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void run() {
            
            this.activeButt.setEnabled(false);
            try {
                //PROVA DE NO FER SERVIR TOT EL PATH del TSD
                String filenameNoExt = FileUtils.getFNameNoExt(getCurrentTSD().getName());
                File workDirF = getCurrentTSD().getParentFile();
                
                log.debug("filenameNoExt="+filenameNoExt);
                log.debug("workDirF="+workDirF.toString());
                
                String[] cmd = {this.exec, code, filenameNoExt};
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.directory(workDirF);
                pb.redirectErrorStream(true);
                TTS_process=pb.start();
                
                sortidaProg = new InputStreamReader(TTS_process.getInputStream());
                TTSInExecution=true;
                btnStop.setEnabled(true);
                TTSRunStopped=false;
                progressBar.setIndeterminate(true);
                progressBar.setString(String.format("Running %s...",this.progName));
                progressBar.setStringPainted(true);
                BufferedReader reader = new BufferedReader(sortidaProg);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(TTS_process.getOutputStream()));

                writer.newLine();
                writer.flush();
                String line = reader.readLine();
                int nblank=0;
                while(line!=null){
                    if (TTSRunStopped){break;} //si s'ha parat no escribim mes resultats

                    boolean print = true;
                    
                    //limitem nombre de linies buides... realment s'hauria d'arreglar al fortran INCO
                    if (line.trim().isEmpty()){
                        nblank = nblank+1;
                    }else{
                        nblank = 0;
                    }
                    
                    //no mostrem la linia de premer <CR> per finir...
                    if (line.contains("<CR>"))print = false;
                    if (nblank>2) print=false;
                        
                    if(print)txtOut.stat(line);
                    line=reader.readLine();
                }
                reader.close();
                writer.close();
                this.activeButt.setEnabled(true);
                try{
                    if(TTS_process.exitValue()==0){
                        txtOut.stat(String.format("%s finished correctly.",this.progName));
                    }else{
                        txtOut.stat(String.format("%s finished.",this.progName));
                    }
                }catch(Exception e){
                    if(isDebug())e.printStackTrace();
                    txtOut.stat(String.format("%s has NOT finished correctly.",this.progName));
                    this.activeButt.setEnabled(true);
                    TTSInExecution=false;
                    btnStop.setEnabled(false);
                    progressBar.setString(null);
                    progressBar.setStringPainted(false);
                    progressBar.setIndeterminate(false);
                }
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
                this.activeButt.setEnabled(true);
                TTSInExecution=false;
                btnStop.setEnabled(false);
                progressBar.setString(null);
                progressBar.setStringPainted(false);
                progressBar.setIndeterminate(false);
            }
            TTSInExecution=false;
            btnStop.setEnabled(false);
            TTSRunStopped=false;
            progressBar.setString(null);
            progressBar.setStringPainted(false);
            progressBar.setIndeterminate(false);
        }
        
    }

    protected void do_btnRunInco_actionPerformed(ActionEvent e) {
        //comprovacions previes (missatges a les subrutines) TODO
        if (!checkTSDrequirements())return;
        if (!checkForExistingMaskBIN())return; 
        if (!checkForPCSfiles())return;
        
        //Fem correr el programa
        txtOut.saltL();
        if(tsdfile.getIoff()!=1) { //coarse
            txtOut.stat(" RUNNING INCO (coarse scan for domains in central frame):");
        }else { //=1 oriented
            txtOut.stat(" RUNNING INCO (full scan of a single oriented domain):");
        }
        txtOut.stat("  Input file: "+this.getCurrentTSD());
        txtOut.saltL();
        //creem i executem el thread del ribbols
        Thread inco = new Thread(new TTSRunnable(TTSRunnable.INCO));
        inco.start();
    }
    
    //retorna la imatge de la qual donem el numero de suffix (cas ioff=1 o per generar pcs)
    // -1 per tal de tornar primera de la llista del tsd
    private File findImageFileFromTSD(int imgnumber) {
        File imgfile = this.findRelatedFile(this.getCurrentTSD(), "bin", imgnumber, true);
        if (imgfile!=null)return imgfile;
        imgfile = this.findRelatedFile(this.getCurrentTSD(), "edf", imgnumber, true);
        if (imgfile!=null)return imgfile;
        return null;
    }
 
    protected void do_btnChecksol_actionPerformed(ActionEvent arg0) {
        //el sol el gestiona d2dplot amb l'opcio -sol
        File imgfile = findImageFileFromTSD(-1);
        //SI HEM TROBAT OBRIM
        if (imgfile!=null){
            txtOut.stat("Opening d2Dplot (imgfile="+imgfile.toString()+")");
            this.openInD2Dplot(imgfile.getAbsolutePath(),true);
            return;
        }else{
            txtOut.stat("Sorry, image file could not be found. Open it manually with d2Dplot");
        }
    }

    protected void do_btnCheckOut_actionPerformed(ActionEvent e) {
        if (!checkTSDrequirements())return;
        File outFile = this.findRelatedFile(this.getCurrentTSD(), "out", -1, true);
        if (outFile!=null) {
            this.openTEXTfile(outFile);
            return;
        }
        
        
        
        //si s'ha arribat aqui no s'ha trobat fitxer out
        txtOut.stat("No INCO results file found (.OUT), run INCO first");
        return;
    }
    
    private void openTEXTfile(File txtf){
        //we have to OPEN a text file (TSD, OUT, ...) with preferred editor
        if (txtf==null){
            txtOut.stat("File not found");
            return;
        }
        File txtEditorExec = new File(D2Dplot_global.getTxtEditPath());
        if (!txtEditorExec.exists()){
            try{
                //use default
                if(Desktop.isDesktopSupported()){ // s'obre amb el programa per defecte
                    Desktop.getDesktop().open(txtf);
                }else{
                    new ProcessBuilder("cmd","/c",txtf.toString()).start();
                }
            }catch(Exception e1){
                if(isDebug())e1.printStackTrace();
            }
        }else{
            String[] cmd = {D2Dplot_global.getTxtEditPath(), txtf.toString()};
            ProcessBuilder pb = new ProcessBuilder(cmd);
            try {
                pb.start();
            } catch (IOException e1) {
                if(isDebug())e1.printStackTrace();
            }
        }
    }
    
    private void openInD2Dplot(String imgPath,boolean openSOL) {
        File imgFull = new File(imgPath);
        String fname = imgFull.getName();
        String fdir = imgFull.getParent();
        log.info("fname="+fname);
        log.info("fdir="+fdir);;
//        String[] args = {"",imgFull.getAbsolutePath(),""};
//        if(openSOL) args[2]="-sol";
//        ArgumentLauncher.startInteractive(d2DplotMain, args);
        
        //provem obrir directe
        d2DplotMain.updateFromTTS(imgFull);
        
        if(openSOL) {//busquem el sol i l'obrim
//            File fsol = ArgumentLauncher.findSOLfile(imgFull);
            File fsol = this.findRelatedFile(this.getCurrentTSD(), "sol", -1, false); //false per donar prioritat al del domini
            if (fsol==null) {
                fsol = this.findRelatedFile(this.getCurrentTSD(), "sol", -1, true); //ara mirem el SOL general (coarse)
            }
            if (fsol!=null) {
                this.print_loginfo_tAOut(String.format("SOL file found: %s", fsol.toString()));
                //OBRIM dialeg INCO directament amb un fitxer SOL
                if (d2DplotMain.getDincoFrame() == null) {
                    d2DplotMain.setDincoFrame(new Dinco_frame(d2DplotMain.getPanelImatge()));
                }
                d2DplotMain.getDincoFrame().setSOLMode();
                d2DplotMain.getDincoFrame().loadSOLFileDirectly(fsol);
                d2DplotMain.getDincoFrame().setVisible(true);
                d2DplotMain.getPanelImatge().setDinco(d2DplotMain.getDincoFrame());
            }else {
                this.print_loginfo_tAOut("Could not find SOL file, try to open it manually");
            }
        }
    }
    
    protected void do_btnCheckCel_actionPerformed(ActionEvent e) {
        if (!checkTSDrequirements())return;
        File celFile = this.findRelatedFile(this.getCurrentTSD(), "cel", -1, true);
        if (celFile!=null) {
            this.openTEXTfile(celFile);
            return;
        }
        
        //si s'ha arribat aqui no s'ha trobat fitxer out
        txtOut.stat("No CELREF results file found (.CEL), run CELREF first");
        return;
    }
    
    protected void do_btnCheckMrg_actionPerformed(ActionEvent e) {
        if (!checkTSDrequirements())return;
        File mrgFile = this.findRelatedFile(this.getCurrentTSD(), "mrg", -1, true);
        if (mrgFile!=null) {
            this.openTEXTfile(mrgFile);
            return;
        }

        //si s'ha arribat aqui no s'ha trobat fitxer out
        txtOut.stat("No MERGE results file found (.MRG), run MERGE first");
        return;
    }
    
    
    protected void do_btnRunMerge_actionPerformed(ActionEvent e) {
        //comprovacions previes (missatges a les subrutines) TODO
        if (!checkTSDrequirements())return;
        
        if (tsdfile.getMultdom()!=1) {
            if (!checkForExistingMaskBIN())return; 
            if (!checkForPCSfiles())return;
        }
        
        
        //Fem correr el programa
        txtOut.saltL();
        if (tsdfile.getMultdom()!=1) {
            txtOut.stat(" RUNNING MERGE (partial datasets of 1 domain):");    
        }else { //==1 multidomain
            txtOut.stat(" RUNNING MERGE (multiple oriented domains):");
        }
        
        txtOut.stat("  Input file: "+this.getCurrentTSD());
        txtOut.saltL();
        //creem i executem el thread del merge
        Thread merge = new Thread(new TTSRunnable(TTSRunnable.MERGE));
        merge.start();
    }
    
    private boolean checkTSDrequirements() {
        //rellegim el TSD per si hi ha hagut canvis
        this.setCurrentTSD(this.getCurrentTSD());
        if(this.getCurrentTSD()==null){
            txtOut.stat("Select first a valid TSD input file");
            return false;
        }
        if(!this.getCurrentTSD().exists()){
            txtOut.stat("Selected TSD file does not exist");
            return false;            
        }
        if(!FileUtils.getExtension(this.getCurrentTSD()).equalsIgnoreCase("TSD")){
            txtOut.stat("Working file must have the .TSD extension");
            return false;            
        }
        return true;
    }
    
    private boolean checkForExistingMaskBIN(){
//        File msk = FileUtils.canviNomFitxer(this.getCurrentTSD(), "MASK");
//        msk = FileUtils.canviExtensio(msk, "BIN");
        File msk = new File(this.getCurrentTSD().getParentFile()+FileUtils.getSeparator()+TTS_frame.maskFileName); 
        log.debug("msk="+msk.toString());
        if (msk.exists())return true;
        //test uppercase
        String upp = this.getCurrentTSD().getParentFile()+FileUtils.getSeparator()+TTS_frame.maskFileName;
        upp = upp.toUpperCase();
        log.debug("msk="+upp);
        msk = new File(upp);
        if (msk.exists())return true;
        //otherwise ask for creating one
        boolean open = FileUtils.YesNoDialog(this,"MASK.BIN not found, open Excluded Zones module to generate one?");
        if (open) {
            //first open image
            File imgfile = this.findImageFileFromTSD(-1);
            //first open image
            this.openInD2Dplot(imgfile.getAbsolutePath(), false);
            //then open module
            d2DplotMain.do_mntmExcludedZones_actionPerformed(null);
        }
        
        return false;
    }
    
    private boolean checkForPCSfiles(){
        //TODO
        //comprovem de la llista si hi ha tots els pcs
        boolean generalTrobat = true;
        if(tsdfile.getNfiles()>0) {
            for (int i=0; i<tsdfile.getNfiles();i++) {
                int codifile = tsdfile.getFnum()[i];
                File pcsfile = this.findRelatedFile(this.getCurrentTSD(), "pcs", codifile, false);
                if (pcsfile==null) {
                    generalTrobat = false; //n'hi ha una al menys de no trobada
                    continue;
                }
            }
            if (generalTrobat==true) { //s'han trobat totes
                return true;
            }else {
                boolean open = FileUtils.YesNoDialog(this,"Generate missing PCS files in the peaksearch module?");
                if (open) {//we will open only the first one missing
                    for (int i=0; i<tsdfile.getNfiles();i++) {
                        int codifile = tsdfile.getFnum()[i];
                        File pcsfile = this.findRelatedFile(this.getCurrentTSD(), "pcs", codifile, false);
                        if (pcsfile==null) {
                            File imgfile = findImageFileFromTSD(codifile);
                            this.openInD2Dplot(imgfile.getAbsolutePath(), false);
                            d2DplotMain.do_mntmFindPeaks_actionPerformed(null);
                            break;
                        }
                    }
                }
            }
        }
        return false;
  }
    
    /*
     * Busca un fitxer amb el mateix nom però amb l'extensió donada (majuscula o minuscula, indiferent) a un fitxer original (usualment currentTSD) i 
     * també mira si existeix sense el domini (removing d1 from: z1p1d1)
     * Es pot donar directament el num de sequència (o -1 per mirar tota la llista del tsd)
     * Es pot posar self true per mirar exactament el mateix nom de fitxer (sense afegir _0000) o treure d1, etc...
     * 
     * 
     * SELF té preferència sobre SELF+SEQ+DOMINI i domini sobre el SELF+SEQ
     *  
     */
    private File findRelatedFile(File original, String ext, int codiFile, boolean self) {//, boolean domini) {
        File extfile = null;
        
        //primer mirar si cal mirarse amb el mateix nom i si es troba retornem
        if (self) { 
            boolean trobat = false;
            extfile = FileUtils.canviExtensio(original, ext.toLowerCase());
            log.debug("extfile="+extfile.toString());
            if (extfile.exists())trobat = true;
            
            if (!trobat){
                extfile = FileUtils.canviExtensio(original, ext.toUpperCase());
                log.debug("imgfile="+extfile.toString());
                if (extfile.exists())trobat = true;
            }
            if (trobat) return extfile;
        }
        
        //Segon busquem pel codi (o llista tsd) però en cada cas primer sense reduïr el nom per mantenir el "dX" i donar-li prioritat
        if (codiFile >= 0) { //busquem imatge concreta
            String suffix = String.format("_%04d", codiFile);
            String imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().concat(suffix);
            log.debug("suffix="+codiFile);
            log.debug("imgfileNoExt="+imgfileNoExt);
            extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
            if (extfile!=null) return extfile;
            //ara mirem reduint 2 caracters "dX"
            imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().substring(0, FileUtils.getFNameNoExt(this.getCurrentTSD()).length()-2).concat(suffix); //check also removing d1 from: z1p1d1
            log.debug("imgfileNoExt="+imgfileNoExt);
            extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
            if (extfile!=null) return extfile;
        }else {//aqui mirem la llsta del tsd (retorna el primer trobat) pero igual que abans mirar primer per domini
            for (int i=0; i<tsdfile.getNfiles();i++) {
                codiFile = tsdfile.getFnum()[i];
                String suffix = String.format("_%04d", codiFile);
                String imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().concat(suffix);
                log.debug("suffix="+codiFile);
                log.debug("imgfileNoExt="+imgfileNoExt);
                extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
                if (extfile!=null) return extfile;
                //ara mirem reduint 2 caracters "dX"
                imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().substring(0, FileUtils.getFNameNoExt(this.getCurrentTSD()).length()-2).concat(suffix); //check also removing d1 from: z1p1d1
                log.debug("imgfileNoExt="+imgfileNoExt);
                extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
                if (extfile!=null) return extfile;
            }
        }
        return null;
    }
    
    
    private File findFileExtCaseInsensitive(String filenameNoExt, String ext) {
        boolean trobat = false;
        File extfile = new File(filenameNoExt+"."+ext.toLowerCase());
        if (extfile.exists()) trobat = true;
        if (!trobat) {
            extfile = new File(filenameNoExt+"."+ext.toUpperCase());
            if (extfile.exists())trobat = true;
        }
        if (!trobat) {
            this.print_loginfo_tAOut(filenameNoExt+"."+ext.toLowerCase()+" or ."+ext.toUpperCase()+" not found");
            return null;
        }
        this.print_loginfo_tAOut("file found: "+extfile.getAbsolutePath());
        return extfile;
    }
    
    protected void do_btnRunCelRef_actionPerformed(ActionEvent e) {
        //comprovacions previes (missatges a les subrutines) TODO
        if (!checkTSDrequirements())return;
//        if (!checkForExistingMaskBIN())return; 
//        if (!checkForPCSfiles())return;
        
        //Fem correr el programa
        txtOut.saltL();
        txtOut.stat(" RUNNING CELREF:             ");
        txtOut.stat("  Input file: "+this.getCurrentTSD());
        txtOut.saltL();
        //creem i executem el thread del celref
        Thread celref = new Thread(new TTSRunnable(TTSRunnable.CELREF));
        celref.start();
    }
    protected void do_btnGenerateTsd_actionPerformed(ActionEvent e) {
        txtOut.stat("This will generate a template TSD file");
        FileNameExtensionFilter[] filt = {new FileNameExtensionFilter("TSD file", "tsd","TSD")};
        File f = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), filt, "TSD");
        if (f==null)return;
        this.updateWorkDir(f);
//        f = FileUtils.canviExtensio(f, "TSD");
//        if (f.exists()){
//            boolean over = FileUtils.YesNoDialog(this, "Overwrite existing "+f.getName()+" file?");
//            if(!over)return;
//        }
        f = ImgFileUtils.generateTSD(f);
        if (f==null) {
            txtOut.stat("Error generating TSD file");
            return;
        }else {
            txtOut.stat("TSD file saved: "+f.getAbsolutePath());
            txtOut.stat("Now you can edit TSD file according to your sample, check the manual for all the details.");
            txtOut.stat("Do not forget to follow the file naming convention suggested on the manual.");
            //set as working TSD
            boolean open = FileUtils.YesNoDialog(this, "Load "+f.getName()+" as working file and edit it?");
            if (open) {
                this.setCurrentTSD(f);
                this.openTEXTfile(f);
            }
        }
    }
        
    protected void do_btnCopyTsd_actionPerformed(ActionEvent e) {
        if(this.getCurrentTSD()==null){
            txtOut.stat("Select first a valid TSD input file");
            return;
        }
        if(!this.getCurrentTSD().exists()){
            txtOut.stat("Selected TSD file does not exist");
            return;            
        }
        FileNameExtensionFilter[] filt = {new FileNameExtensionFilter("TSD file", "tsd","TSD")};
        File dest = FileUtils.fchooserSaveAsk(this, new File(D2Dplot_global.getWorkdir()), filt,"TSD"); 
        if (dest == null) return;
        this.updateWorkDir(dest);
        FileUtils.copyFile(this.getCurrentTSD(), dest, false, txtOut);
        boolean over = FileUtils.YesNoDialog(this, "Load "+dest.getName()+" as working file and edit it?");
        if (over){
            setCurrentTSD(dest);
            this.openTEXTfile(dest);
        }
    }
    
    //String suffixdX,
    //alon, alat, aspin
    
    //destfile ha de tenir el suffix dX, cal preguntar el num domini i el num d'imatges abans
    private File copyTSDtoIREF1(File out){
        //copia linia a linia
        //canvi linia iref
        //posar/canviar alon alat aspin
        //nimages/swing trobar central i escriure-ho be al final
        
        if (tsdfile!=null){
            if (tsdfile.isSuccessfulRead()){
                if (!checkTSDrequirements())return null;
                if (d2DplotMain.getDincoFrame() == null) {
                    txtOut.stat("no opened solutions found, try doing it by editing manually TSD files");
                    return null;
                }
                float lon = d2DplotMain.getDincoFrame().getActiveOrientSol().getAngR_lon();
                float lat = d2DplotMain.getDincoFrame().getActiveOrientSol().getAngS_lat();
                float spin = d2DplotMain.getDincoFrame().getActiveOrientSol().getAngT_spin();   
                
                float swing = tsdfile.getSwing(); //es la deltaphi, es a dir mitja oscil·lació
                
                int centralImage = -1;
                for (int i=0;i<tsdfile.getNfiles();i++) {
                    float off = tsdfile.getFnumAngOff()[i];
                    if (FastMath.round(off)==0) {
                        centralImage = tsdfile.getFnum()[i];
                        break;
                    }
                }
                if (centralImage<0) {
                    txtOut.stat("Could not find image number with offset 0, try doing it by editing manually TSD files");
                    return null;
                }
                //tindrem les imatges 0 1 2 3... centralImage ...3 2 1 0  (es a dir "simètric" segons la central)
                
                //ara llegim i escribim
//                File out = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), null, "TSD");
//                boolean firstDash = false;
                try {
                    Scanner inTSDfile = new Scanner(new BufferedReader(new FileReader(this.getCurrentTSD())));
                    PrintWriter outTSDfile = new PrintWriter(new BufferedWriter(new FileWriter(out)));
                    
                    while (inTSDfile.hasNextLine()){
                        String line = inTSDfile.nextLine();
                        
                        if(line.startsWith("&CONTROL")) {
                            //comença el bloc control
                            outTSDfile.println(line);
                            boolean end = false;
                            while (!end) {
                                line = inTSDfile.nextLine();
                                if (line.contains("/")) {
                                    end = true;
                                    outTSDfile.println(String.format("ALON=%.3f,",lon));
                                    outTSDfile.println(String.format("ALAT=%.3f,",lat));
                                    outTSDfile.println(String.format("SPIN=%.3f,",spin));
                                    outTSDfile.println("/");
                                    continue;
                                }
                                if (line.startsWith("ALON"))continue;
                                if (line.startsWith("ALAT"))continue;
                                if (line.startsWith("SPIN"))continue;
                                if (line.startsWith("IOFF")){
                                    outTSDfile.println("IOFF=1,");
                                    continue;
                                }
                                outTSDfile.println(line);
                            }
                            continue;
                        }
                        
//                        if(line.contains("/")) {
//                            if (!firstDash) {
//                                //compte que vol dir que s'han acabat els codewords, revisem
//                                outTSDfile.println(String.format("ALON=%.3f,",lon));
//                                outTSDfile.println(String.format("ALAT=%.3f,",lat));
//                                outTSDfile.println(String.format("SPIN=%.3f,",spin));
//                                firstDash=true;
//                                outTSDfile.println("/");
//                                continue;
//                            }
//                        }
                        
                        //ignorem
//                        if (line.startsWith("ALON"))continue;
//                        if (line.startsWith("ALAT"))continue;
//                        if (line.startsWith("SPIN"))continue;
                        
                        //el final ja
                        if (line.startsWith("PCS")) {
                            outTSDfile.println(line);
                            outTSDfile.println(Integer.toString(centralImage*2+1));
                            for (int i=0;i<=centralImage*2;i++) {
                                outTSDfile.println(String.format("%d,%.2f", i,swing*(i-centralImage)));    
                            }
                            break;
                        }
                        //si no ha passat res escribim linia
                        outTSDfile.println(line);
                    }
                    inTSDfile.close();
                    outTSDfile.close();
                    txtOut.stat("file TSD written");
                    return out;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            txtOut.stat("Current TSD file has not been read successfully, check for syntax errors");
            return null;
        }
        txtOut.stat("no TSD file opened");
        return null;
    }
    
    public static int getMaxLogoWidth() {
        return max_logo_width;
    }

    public static int getMinLogoWidth() {
        return min_logo_width;
    }
    
    public static String getWelcomemsg() {
        return welcomeMSG;
    }

    /**
     * @return the userguidefile
     */
    public static String getUserguidefile() {
        return userGuideFile;
    }
    
    public static String getIncoExec() {
        return incoExec;
    }

    public static void setIncoExec(String incoExec) {
        TTS_frame.incoExec = incoExec;
    }
    
    public static String getMergeExec() {
        return mergeExec;
    }

    public static void setMergeExec(String mergeExec) {
        TTS_frame.mergeExec = mergeExec;
    }

    public static String getCelrefExec() {
        return celrefExec;
    }

    public static void setCelrefExec(String celrefExec) {
        TTS_frame.celrefExec = celrefExec;
    }
    private void print_loginfo_tAOut(String toPrint) {
        txtOut.stat(toPrint);
        log.info(toPrint);
    }
    protected void do_btnCreateTsdIref_actionPerformed(ActionEvent e) {
        //1rCOMPROVAR QUE HI HA UNA SOLUCIO OBERTA
        if (tsdfile!=null){
            if (tsdfile.isSuccessfulRead()){
                if (!checkTSDrequirements())return;
                if (d2DplotMain.getDincoFrame() == null) {
                    txtOut.stat("no opened solutions found, you must open a SOL file to generate TSD(IREF=1)");
                    return;
                }
                FileNameExtensionFilter[] filt = {new FileNameExtensionFilter("TSD file", "tsd","TSD")};
                File out = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), filt, "TSD");
                if (out==null)return;
                File f = this.copyTSDtoIREF1(out);
                if (f!=null) {
                    //open new TSD
                    boolean over = FileUtils.YesNoDialog(this, "Load "+f.getName()+" as working file and edit it?");
                    if (over){
                        setCurrentTSD(f);
                        this.openTEXTfile(f);
                    }
                }
                txtOut.stat("TSD (IREF=1) file created, check that everything is ok (especially PCS/HKL block) and click RUN");
            }
        }
    }
    
    protected void do_btnMultdom_actionPerformed(ActionEvent e) {
        //1r Demanem fitxer TSD a guardar
        if (tsdfile!=null){
            if (tsdfile.isSuccessfulRead()){
                FileNameExtensionFilter[] filt = {new FileNameExtensionFilter("TSD file", "tsd","TSD")};
                File f = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), filt, "TSD", "New TSD file to create");
                if (f==null)return;
                FileNameExtensionFilter[] filt2 = {new FileNameExtensionFilter("HKL file", "hkl","HKL")};
                File[] hkls = FileUtils.fchooserMultiple(this, D2Dplot_global.getWorkdirFile(), filt2, 0,"Select HKL files to merge (FULL oriented domains only)");
                String[] hklfilenames = new String[hkls.length];
                for (int i=0; i<hkls.length;i++) {
                    hklfilenames[i]=hkls[i].getName();
                }
                File newTSD = copyTSDtoMULTDOM(this.getCurrentTSD(),f,hklfilenames);
                if (newTSD!=null) {
                    //ask to open and run merge
                    boolean over = FileUtils.YesNoDialog(this, "Load "+newTSD.getName()+" as working file and run merge?");
                    if (over){
                        setCurrentTSD(f);
                        this.btnRunMerge.doClick();
                        return;
                    }
                }
            }
            txtOut.stat("You should have a valid single-domain TSD as working file");
        }
        
        
    }
    
    private File copyTSDtoMULTDOM(File tsdin, File tsdout, String[] hklfilenames){
        //copia linia a linia
        //canvi linia MULTDOM=1,
        //PCS hkl escriure llista de fitxers HKL
        try {
            Scanner inTSDfile = new Scanner(new BufferedReader(new FileReader(tsdin)));
            PrintWriter outTSDfile = new PrintWriter(new BufferedWriter(new FileWriter(tsdout)));

            while (inTSDfile.hasNextLine()){
                String line = inTSDfile.nextLine();
                boolean multdomFound = false;
                if(line.startsWith("&CONTROL")) {
                    //comença el bloc control
                    outTSDfile.println(line);
                    boolean end = false;
                    while (!end) {
                        line = inTSDfile.nextLine();
                        
                        if(line.startsWith("MULTDOM")) {
                            outTSDfile.println("MULTDOM=1,");
                            multdomFound=true;
                            continue;
                        }
                        
                        if (line.contains("/")) {
                            end = true; //hem arribat al final
                            //hem escrit MULTDOM??
                            if (!multdomFound) {
                                outTSDfile.println("MULTDOM=1,");
                            }
                            outTSDfile.println("/");
                            continue;
                        }
                        outTSDfile.println(line);
                    }
                    continue;
                }
                
                if (line.startsWith("PCS")) {//ja estem al final
                    outTSDfile.println(line);
                    outTSDfile.println(Integer.toString(hklfilenames.length));
                    break;
                }
                outTSDfile.println(line);
            }
            inTSDfile.close();
            //ara cal escriure els noms
            for(int i=0;i<hklfilenames.length;i++) {
                outTSDfile.println(hklfilenames[i]);
            }
            outTSDfile.close();
            txtOut.stat("file TSD written");
            return tsdout;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    protected void do_btnCreateTsdFor_actionPerformed(ActionEvent e) { //molt igual a MULTDOM=1
        //1r Demanem fitxer TSD a guardar
        if (tsdfile!=null){
            if (tsdfile.isSuccessfulRead()){
                FileNameExtensionFilter[] filt = {new FileNameExtensionFilter("TSD file", "tsd","TSD")};
                File f = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), filt, "TSD", "New TSD file to create");
                if (f==null)return;
                FileNameExtensionFilter[] filt2 = {new FileNameExtensionFilter("HKL file", "hkl","HKL")};
                File[] hkls = FileUtils.fchooserMultiple(this, D2Dplot_global.getWorkdirFile(), filt2, 0,"Select HKL files to use in CelRef (PARTIAL oriented domains only)");
                String[] hklfilenames = new String[hkls.length];
                for (int i=0; i<hkls.length;i++) {
                    hklfilenames[i]=hkls[i].getName();
                }
                File newTSD = copyTSDtoCELREF(this.getCurrentTSD(),f,hklfilenames);
                if (newTSD!=null) {
                    //ask to open and run merge
                    boolean over = FileUtils.YesNoDialog(this, "Load "+newTSD.getName()+" as working file and run celref?");
                    if (over){
                        setCurrentTSD(f);
                        this.btnRunCelRef.doClick();
                        return;
                    }
                }
            }
            txtOut.stat("You should have a valid single-domain TSD as working file");
        }
    }
    private File copyTSDtoCELREF(File tsdin, File tsdout, String[] hklfilenames){
        //copia linia a linia
        //PCS hkl escriure llista de fitxers HKL
        try {
            Scanner inTSDfile = new Scanner(new BufferedReader(new FileReader(tsdin)));
            PrintWriter outTSDfile = new PrintWriter(new BufferedWriter(new FileWriter(tsdout)));

            while (inTSDfile.hasNextLine()){
                String line = inTSDfile.nextLine();
                if (line.startsWith("PCS")) {//ja estem al final
                    outTSDfile.println(line);
                    outTSDfile.println(Integer.toString(hklfilenames.length));
                    break;
                }
                outTSDfile.println(line);
            }
            inTSDfile.close();
            //ara cal escriure els noms
            for(int i=0;i<hklfilenames.length;i++) {
                outTSDfile.println(hklfilenames[i]);
            }
            outTSDfile.close();
            txtOut.stat("file TSD written");
            return tsdout;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}