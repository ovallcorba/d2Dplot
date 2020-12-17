package com.vava33.d2dplot;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.tts.AboutTTS_dialog;
import com.vava33.d2dplot.tts.BackgroundPanel;
import com.vava33.d2dplot.tts.Settings_dialog;
import com.vava33.d2dplot.tts.TSDfile;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class TTS {

    private static int window_width = 960;
    private static int window_height = 600;

    private static final String userGuideFile = "Write_up_tts_software.pdf";
    private static final String ttsVersion = "1609 [160930]";
    private static final String welcomeMSG = " --> tts software UI - version " + ttsVersion + " by OV\n"
            + " |    tts software main author: J.Rius (ICMAB-CSIC)\n"
            + " |    Main collaborators: O.Vallcorba (ALBA-CELLS), C.Frontera (ICMAB-CSIC)\n"
            + " |    Report of errors/suggestions/comments to the authors are appreciated\n"
            + " |    Click on the [?] button for user's guide and conditions of use.\n";
    private static final int max_logo_width = 1200;
    private static final int min_logo_width = 600;
    private static final String code = "110606";

    private static final String incoExecPath = FileUtils.fileSeparator + "bin" + FileUtils.fileSeparator + "tts_inco";
    private static final String mergeExecPath = FileUtils.fileSeparator + "bin" + FileUtils.fileSeparator
            + "tts_merge";
    private static final String celrefExecPath = FileUtils.fileSeparator + "bin" + FileUtils.fileSeparator
            + "tts_celref";
    private static String incoExec = "";
    private static String mergeExec = "";
    private static String celrefExec = "";
    private static String maskFileName = "MASK.BIN";

    private final JFrame ttsFrame;
    private final JPanel contentPane;
    private Settings_dialog prefs;
    private final LogJTextArea txtOut;
    private final JLabel lblWorkingFile;
    private final JProgressBar progressBar;
    private final JButton btnRunInco;
    private final JButton btnRunMerge;
    private final JButton btnRunCelRef;
    private final JButton btnStop;

    private File currentTSD;
    private TSDfile tsdfile;
    private static Process TTS_process;
    protected InputStreamReader sortidaProg;
    private static boolean TTSInExecution = false;
    private static boolean TTSRunStopped = false;
    private static final String className = "TTS";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private final MainFrame d2DplotMain;

    /**
     * Create the frame.
     */
    public TTS(MainFrame mf) {
        this.d2DplotMain = mf;
        this.ttsFrame = new JFrame("tts software");
        this.ttsFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //        setBounds(100, 100, 780, 580); //780x600 inicial per provar
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.ttsFrame.setContentPane(this.contentPane);
        this.contentPane.setLayout(new MigLayout("insets 0", "[grow]", "[grow][]"));

        final JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.2);
        splitPane.setContinuousLayout(true);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.contentPane.add(splitPane, "cell 0 0,grow");

        final JPanel panel_top = new JPanel();
        splitPane.setLeftComponent(panel_top);
        panel_top.setLayout(new MigLayout("insets 0", "[grow][grow][grow]", "[100px,grow][][][]"));

        final JPanel panel_current = new JPanel();
        panel_top.add(panel_current, "flowx,cell 0 1 3 1,grow");
        panel_current.setLayout(new MigLayout("insets 2", "[][][][][grow]", "[][]"));

        final JButton btnOpenTsd = new JButton("Open TSD");
        btnOpenTsd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                TTS.this.do_btnOpenTsd_actionPerformed(arg0);
            }
        });
        panel_current.add(btnOpenTsd, "cell 0 0");

        final JButton btnEdit = new JButton("Edit TSD");
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnEdit_actionPerformed(e);
            }
        });
        panel_current.add(btnEdit, "cell 1 0,aligny center");

        final JButton btnGenerateTsd = new JButton("Generate template TSD");
        btnGenerateTsd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnGenerateTsd_actionPerformed(e);
            }
        });

        final JButton btnCopyTsd = new JButton("Copy TSD");
        btnCopyTsd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnCopyTsd_actionPerformed(e);
            }
        });
        panel_current.add(btnCopyTsd, "cell 2 0");
        panel_current.add(btnGenerateTsd, "cell 3 0");

        final JLabel lblCurrentFile = new JLabel("WorkFile:");
        panel_current.add(lblCurrentFile, "cell 0 1,alignx right,aligny baseline");

        this.lblWorkingFile = new JLabel("(none)");
        this.lblWorkingFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                TTS.this.do_lblWorkingFile_mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                TTS.this.do_lblWorkingFile_mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                TTS.this.do_lblWorkingFile_mouseExited(e);
            }
        });
        panel_current.add(this.lblWorkingFile, "cell 1 1 4 1,growx,aligny baseline");

        final BackgroundPanel panel_logo = new BackgroundPanel(
                new ImageIcon(this.getClass().getResource("/img/tts_splash_760x120.png")).getImage(),
                TTS.getMaxLogoWidth(), TTS.getMinLogoWidth());
        panel_top.add(panel_logo, "cell 0 0 3 1,grow");
        panel_logo.setLayout(new MigLayout("insets 5", "[grow][]", "[][][grow]"));

        final JButton btnSettings = new JButton("");
        btnSettings.setPreferredSize(new Dimension(30, 30));
        btnSettings.setMinimumSize(new Dimension(30, 30));
        btnSettings.setMargin(new Insets(2, 2, 2, 2));
        btnSettings.setIcon(new ImageIcon(this.getClass().getResource("/img/config_small.png")));

        btnSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnSettings_actionPerformed(e);
            }
        });
        panel_logo.add(btnSettings, "cell 0 0,alignx right,aligny bottom");

        final JButton btnManual = new JButton("");
        btnManual.setPreferredSize(new Dimension(30, 30));
        btnManual.setMinimumSize(new Dimension(30, 30));
        btnManual.setMargin(new Insets(2, 2, 2, 2));
        btnManual.setIcon(new ImageIcon(this.getClass().getResource("/img/interrogant.png")));
        btnManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnManual_actionPerformed(e);
            }
        });
        panel_logo.add(btnManual, "cell 0 1,alignx right,aligny center");

        final JPanel panel_inco = new JPanel();
        panel_inco.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "tts_Inco",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_top.add(panel_inco, "cell 0 2,grow");
        panel_inco.setLayout(new MigLayout("insets 2", "[grow][grow]", "[][]"));

        this.btnRunInco = new JButton("RUN");
        this.btnRunInco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnRunInco_actionPerformed(e);
            }
        });
        panel_inco.add(this.btnRunInco, "cell 0 0,growx");

        final JButton btnCheckOut = new JButton("View OUT");
        btnCheckOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnCheckOut_actionPerformed(e);
            }
        });
        panel_inco.add(btnCheckOut, "cell 1 0,growx");

        final JButton btnChecksol = new JButton("Check SOL");
        btnChecksol.setToolTipText("with D2Dplot");
        btnChecksol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                TTS.this.do_btnChecksol_actionPerformed(arg0);
            }
        });
        panel_inco.add(btnChecksol, "cell 0 1,growx");

        final JButton btnCreateTsdIref = new JButton("Create TSD IREF=1");
        btnCreateTsdIref.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnCreateTsdIref_actionPerformed(e);
            }
        });
        panel_inco.add(btnCreateTsdIref, "cell 1 1,growx");

        final JPanel panel_merge = new JPanel();
        panel_merge.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "tts_Merge",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_top.add(panel_merge, "cell 1 2,grow");
        panel_merge.setLayout(new MigLayout("insets 2", "[grow][]", "[][]"));

        this.btnRunMerge = new JButton("RUN");
        this.btnRunMerge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnRunMerge_actionPerformed(e);
            }
        });
        panel_merge.add(this.btnRunMerge, "cell 0 0,growx");

        final JButton btnCheckMrg = new JButton("View MRG");
        btnCheckMrg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnCheckMrg_actionPerformed(e);
            }
        });
        panel_merge.add(btnCheckMrg, "cell 1 0,growx");

        final JButton btnMultdom = new JButton("Create TSD MULTDOM=1");
        btnMultdom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnMultdom_actionPerformed(e);
            }
        });
        panel_merge.add(btnMultdom, "cell 0 1 2 1,growx");

        final JPanel panelCelref = new JPanel();
        panelCelref.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "tts_Celref",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_top.add(panelCelref, "cell 2 2,grow");
        panelCelref.setLayout(new MigLayout("insets 2", "[grow][]", "[][]"));

        this.btnRunCelRef = new JButton("RUN");
        this.btnRunCelRef.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnRunCelRef_actionPerformed(e);
            }
        });
        panelCelref.add(this.btnRunCelRef, "cell 0 0,growx");

        final JButton btnCheckCel = new JButton("View CEL");
        btnCheckCel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnCheckCel_actionPerformed(e);
            }
        });
        panelCelref.add(btnCheckCel, "cell 1 0,growx");

        final JButton btnCreateTsdFor = new JButton("Create TSD for CelRef");
        btnCreateTsdFor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnCreateTsdFor_actionPerformed(e);
            }
        });
        panelCelref.add(btnCreateTsdFor, "cell 0 1 2 1,growx");

        final JPanel panelExec = new JPanel();
        panel_top.add(panelExec, "cell 0 3 3 1,grow");
        panelExec.setLayout(new MigLayout("insets 2", "[grow][grow][]", "[]"));

        this.progressBar = new JProgressBar();
        panelExec.add(this.progressBar, "cell 0 0 2 1,grow");

        this.btnStop = new JButton("Stop");
        this.btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnStop_actionPerformed(e);
            }
        });
        panelExec.add(this.btnStop, "cell 2 0,grow");

        final JPanel panel_bottom = new JPanel();
        splitPane.setRightComponent(panel_bottom);
        panel_bottom.setLayout(new MigLayout("insets 1", "[grow]", "[grow]"));

        final JScrollPane scrollPane = new JScrollPane();
        panel_bottom.add(scrollPane, "cell 0 0,grow");

        this.txtOut = new LogJTextArea();
        scrollPane.setViewportView(this.txtOut);

        final JTextPane txtpnAaa = new JTextPane();
        txtpnAaa.setContentType("text/html");

        txtpnAaa.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        txtpnAaa.setEditable(false);

        txtpnAaa.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (final Exception e1) {
                            if (D2Dplot_global.isDebug())
                                e1.printStackTrace();
                        }
                    }
                }
            }
        });
        txtpnAaa.setOpaque(false);

        txtpnAaa.setText(
                "<html>Download TTS_software from: <a href=\"http://www.icmab.es/crystallography/software\">http://www.icmab.es/crystallography/software</a></html>");
        this.contentPane.add(txtpnAaa, "flowx,cell 0 1,growx");

        final JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TTS.this.do_btnClose_actionPerformed(e);
            }
        });
        this.contentPane.add(btnClose, "cell 0 1");

        this.iniciaFinestra();
        this.iniciPrefs();

    }

    private void iniciaFinestra() {
        this.ttsFrame.setTitle("tts software");
        //icono petit de la finestra
        final List<Image> icons = new ArrayList<Image>();
        icons.add(new ImageIcon(this.getClass().getResource("/img/tts_icon120x120.png")).getImage());
        this.ttsFrame.setIconImages(icons);

        //Farem que es posicioni correctament i a mida bona en pantalla
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        this.ttsFrame.setPreferredSize(new Dimension(TTS.window_width, TTS.window_height));
        //        this.setMinimumSize(new Dimension(MainFrame_tts.window_width/2, MainFrame_tts.window_height/2)); //posem mida minima
        this.ttsFrame.setMinimumSize(new Dimension(TTS.getMinLogoWidth(), TTS.window_height / 2)); //posem mida minima

        if (screen.width < TTS.window_width)
            TTS.window_width = screen.width;
        if (screen.height < TTS.window_height)
            TTS.window_height = screen.height;

        //finalment redimensionem i centrem a la pantalla
        final int x = (screen.width - TTS.window_width) / 2;
        final int y = (screen.height - TTS.window_height) / 4; //fem entre 4 perque hi ha la barra d'inici
        this.ttsFrame.setBounds(x, y, TTS.window_width, TTS.window_height);

        this.ttsFrame.pack();// TODO: PROVAR SI CAL
        //        resizeImage(MainFrame_tts.window_width);
        //        txtOut.saltL();
        this.txtOut.ln(TTS.getWelcomemsg());
        this.txtOut.saltL();
    }

    private void iniciPrefs() {
        //LLEGIM PREFERENCIES
        int stat = 0;
        try {
            stat = this.llegirPreferences();
        } catch (final Exception e) {
            if (isDebug())
                e.printStackTrace();
            this.print_logdebug_tAOut("error reading tts_software executables, check in preferences");
        }
        if (stat != 0) {//hi ha hagut error al localitzar fitxers}
            this.print_logdebug_tAOut("tts_software_folder not found, set it in preferences");
        }
    }

    //Aquest metode crea la finestra preferencies, les llegeix del fitxer .\bin\prefs.cfg i assigna les opcions.
    //TAMBE SERVEIX DE COMPROVACIO QUE EXISTEIXI EL DIRECTORI BIN I ELS FITXERS NECESSARIS.
    private int llegirPreferences() {
        final boolean tts_deps = this.checkTTSDependencies();
        //es creen les preferencies
        if (this.prefs == null) {
            this.prefs = new Settings_dialog(this.ttsFrame, this);
        }
        //mirem que existeixi tot
        if (!tts_deps) {
            return 1;
        }
        return 0; //tot correcte
    }

    public boolean checkTTSDependencies() {
        final String tts_folder = D2Dplot_global.getTTSsoftwareFolder().trim();
        boolean somethingWrong = false;
        if (tts_folder.isEmpty()) {
            this.print_logdebug_tAOut("no tts_software folder has been set");
            return false;
        } else {
            //ara buscarem els executables un a un
            this.print_logdebug_tAOut("tts folder supplied: " + tts_folder);

            File temp = new File(tts_folder + TTS.incoExecPath);
            if (!temp.exists()) {
                this.print_logdebug_tAOut(String.format("warning: %s not found", temp));
                TTS.setIncoExec("");
                somethingWrong = true;
            } else {
                TTS.setIncoExec(temp.getAbsolutePath());
                this.print_logdebug_tAOut("tts_inco found: " + TTS.getIncoExec());
            }

            temp = new File(tts_folder + TTS.mergeExecPath);
            if (!temp.exists()) {
                this.print_logdebug_tAOut(String.format("warning: %s not found", temp));
                TTS.setMergeExec("");
                somethingWrong = true;
            } else {
                TTS.setMergeExec(temp.getAbsolutePath());
                this.print_logdebug_tAOut("tts_merge found: " + TTS.getMergeExec());
            }

            temp = new File(tts_folder + TTS.celrefExecPath);
            if (!temp.exists()) {
                this.print_logdebug_tAOut(String.format("warning: %s not found", temp));
                TTS.setCelrefExec("");
                somethingWrong = true;
            } else {
                TTS.setCelrefExec(temp.getAbsolutePath());
                this.print_logdebug_tAOut("tts_celref found: " + TTS.getCelrefExec());
            }
        }
        if (somethingWrong) {
            this.print_logdebug_tAOut(
                    "Files missing, please check that /bin/ folder inside tts_software_folder exists");
        }
        return true;
    }

    protected void do_btnSettings_actionPerformed(ActionEvent e) {
        if (this.prefs == null) {
            this.prefs = new Settings_dialog(this.ttsFrame, this);
        }
        this.prefs.setVisible(true);
    }

    protected void do_btnManual_actionPerformed(ActionEvent e) {
        final AboutTTS_dialog about = new AboutTTS_dialog(this.ttsFrame);
        about.setVisible(true);
    }

    private static boolean isDebug() {
        return D2Dplot_global.isDebug();
    }

    protected void do_btnOpenTsd_actionPerformed(ActionEvent arg0) {
        final FileNameExtensionFilter[] filt = { new FileNameExtensionFilter("TSD file", "tsd", "TSD") };
        final File f = FileUtils.fchooserOpen(this.ttsFrame, new File(D2Dplot_global.getWorkdir()), filt, 0);
        if (f != null) {
            this.updateWorkDir(f);
            this.setCurrentTSD(f);
        }
    }

    public File getCurrentTSD() {
        return this.currentTSD;
    }

    //SET OR UPDATE CURRENT TSD (with reading)
    public void setCurrentTSD(File currentTSD) {
        this.currentTSD = currentTSD;
        this.lblWorkingFile.setText(currentTSD.toString());
        this.print_logdebug_tAOut("File selected: " + currentTSD.toString());
        //el llegim
        try {
            this.tsdfile = new TSDfile(currentTSD.toString());
        } catch (final Exception e) {
            if (isDebug())
                e.printStackTrace();
            this.print_logdebug_tAOut("Parsing TSD failed, open files directly in D2Dplot will not work");
        }
    }

    protected void do_btnEdit_actionPerformed(ActionEvent e) {
        if (this.getCurrentTSD() == null) {
            this.print_logdebug_tAOut("No working TSD file found");
            return;
        }
        this.openTEXTfile(this.getCurrentTSD());
    }

    protected void do_lblWorkingFile_mouseReleased(MouseEvent e) {
        this.openWorkDir();
    }

    protected void do_lblWorkingFile_mouseEntered(MouseEvent e) {
        this.ttsFrame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.lblWorkingFile.setForeground(Color.blue);
    }

    protected void do_lblWorkingFile_mouseExited(MouseEvent e) {
        this.ttsFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.lblWorkingFile.setForeground(Color.black);
    }

    private void updateWorkDir(File newDir) {
        D2Dplot_global.setWorkdir(newDir.getParent());
    }

    private void openWorkDir() {
        if (this.getCurrentTSD() != null) {
            // vol dir que hi ha fitxer obert amb el que estem treballant
            log.debug(this.getCurrentTSD().getParentFile().toString());
            boolean opened = true;
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(this.getCurrentTSD().getParentFile());
                } else {
                    if (FileUtils.getOS().equalsIgnoreCase("win")) {
                        new ProcessBuilder("explorer.exe", "/select,", this.getCurrentTSD().getParent()).start();
                    }
                    if (FileUtils.getOS().equalsIgnoreCase("lin")) {
                        new ProcessBuilder("nautilus", this.getCurrentTSD().getParent()).start();
                    }
                }
            } catch (final Exception e) {
                if (isDebug())
                    e.printStackTrace();
                opened = false;
            }
            if (opened)
                return;
            if (FileUtils.getOS().equalsIgnoreCase("lin")) {
                opened = true;
                //gnome nautilus
                try {
                    new ProcessBuilder("nautilus", this.getCurrentTSD().getParent()).start();
                } catch (final Exception e) {
                    if (isDebug())
                        e.printStackTrace();
                    opened = false;
                }
                if (opened)
                    return;
                opened = true;
                //kde dolphin
                try {
                    new ProcessBuilder("dolphin", this.getCurrentTSD().getParent()).start();
                } catch (final Exception e) {
                    if (isDebug())
                        e.printStackTrace();
                    opened = false;
                }
                if (!opened)
                    this.print_logdebug_tAOut("Unable to open folder");
            }
        }
    }

    private void stopExecution() {
        TTS_process.destroy();
        TTSInExecution = false;
        this.btnStop.setEnabled(false);
        TTSRunStopped = true;

    }

    protected void do_btnStop_actionPerformed(ActionEvent e) {
        if (TTSInExecution) {
            this.stopExecution();
            this.print_logdebug_tAOut("*************************");
            this.print_logdebug_tAOut("** Run stopped by user **");
            this.print_logdebug_tAOut("*************************");
        }

    }

    private class TTSRunnable implements Runnable {

        private static final int INCO = 0;
        private static final int MERGE = 1;
        private static final int CELREF = 2;

        private JButton activeButt = null;
        private String exec = "";
        private String progName = "";
        //        private boolean generate = false;

        public TTSRunnable(int proc) {
            switch (proc) {
            case INCO:
                this.activeButt = TTS.this.btnRunInco;
                this.exec = TTS.getIncoExec();
                this.progName = "INCO";
                break;
            case MERGE:
                this.activeButt = TTS.this.btnRunMerge;
                this.exec = TTS.getMergeExec();
                this.progName = "MERGE";
                break;
            case CELREF:
                this.activeButt = TTS.this.btnRunCelRef;
                this.exec = TTS.getCelrefExec();
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
                final String filenameNoExt = FileUtils.getFNameNoExt(TTS.this.getCurrentTSD().getName());
                final File workDirF = TTS.this.getCurrentTSD().getParentFile();

                log.debug("filenameNoExt=" + filenameNoExt);
                log.debug("workDirF=" + workDirF.toString());

                final String[] cmd = { this.exec, code, filenameNoExt };
                final ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.directory(workDirF);
                pb.redirectErrorStream(true);
                TTS_process = pb.start();

                TTS.this.sortidaProg = new InputStreamReader(TTS_process.getInputStream());
                TTSInExecution = true;
                TTS.this.btnStop.setEnabled(true);
                TTSRunStopped = false;
                TTS.this.progressBar.setIndeterminate(true);
                TTS.this.progressBar.setString(String.format("Running %s...", this.progName));
                TTS.this.progressBar.setStringPainted(true);
                final BufferedReader reader = new BufferedReader(TTS.this.sortidaProg);
                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(TTS_process.getOutputStream()));

                writer.newLine();
                writer.flush();
                String line = reader.readLine();
                int nblank = 0;
                while (line != null) {
                    if (TTSRunStopped) {
                        break;
                    } //si s'ha parat no escribim mes resultats

                    boolean print = true;

                    //limitem nombre de linies buides... realment s'hauria d'arreglar al fortran INCO
                    if (line.trim().isEmpty()) {
                        nblank = nblank + 1;
                    } else {
                        nblank = 0;
                    }

                    //no mostrem la linia de premer <CR> per finir...
                    if (line.contains("<CR>"))
                        print = false;
                    if (nblank > 2)
                        print = false;

                    if (print)
                        TTS.this.print_logdebug_tAOut(line);
                    line = reader.readLine();
                }
                reader.close();
                writer.close();
                this.activeButt.setEnabled(true);
                try {
                    if (TTS_process.exitValue() == 0) {
                        TTS.this.print_logdebug_tAOut(String.format("%s finished correctly.", this.progName));
                    } else {
                        TTS.this.print_logdebug_tAOut(String.format("%s finished.", this.progName));
                    }
                } catch (final Exception e) {
                    if (isDebug())
                        e.printStackTrace();
                    TTS.this.print_logdebug_tAOut(String.format("%s has NOT finished correctly.", this.progName));
                    this.activeButt.setEnabled(true);
                    TTSInExecution = false;
                    TTS.this.btnStop.setEnabled(false);
                    TTS.this.progressBar.setString(null);
                    TTS.this.progressBar.setStringPainted(false);
                    TTS.this.progressBar.setIndeterminate(false);
                }
            } catch (final Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
                this.activeButt.setEnabled(true);
                TTSInExecution = false;
                TTS.this.btnStop.setEnabled(false);
                TTS.this.progressBar.setString(null);
                TTS.this.progressBar.setStringPainted(false);
                TTS.this.progressBar.setIndeterminate(false);
            }
            TTSInExecution = false;
            TTS.this.btnStop.setEnabled(false);
            TTSRunStopped = false;
            TTS.this.progressBar.setString(null);
            TTS.this.progressBar.setStringPainted(false);
            TTS.this.progressBar.setIndeterminate(false);
        }

    }

    protected void do_btnRunInco_actionPerformed(ActionEvent e) {
        //comprovacions previes (missatges a les subrutines) TODO
        if (!this.checkTSDrequirements())
            return;
        if (!this.checkForExistingMaskBIN())
            return;
        if (!this.checkForPCSfiles())
            return;

        //Fem correr el programa
        this.txtOut.saltL();
        if (this.tsdfile.getIoff() != 1) { //coarse
            this.print_logdebug_tAOut(" RUNNING INCO (coarse scan for domains in central frame):");
        } else { //=1 oriented
            this.print_logdebug_tAOut(" RUNNING INCO (full scan of a single oriented domain):");
        }
        this.print_logdebug_tAOut("  Input file: " + this.getCurrentTSD());
        this.txtOut.saltL();
        //creem i executem el thread del ribbols
        final Thread inco = new Thread(new TTSRunnable(TTSRunnable.INCO));
        inco.start();
    }

    //retorna la imatge de la qual donem el numero de suffix (cas ioff=1 o per generar pcs)
    // -1 per tal de tornar primera de la llista del tsd
    private File findImageFileFromTSD(int imgnumber) {
        File imgfile = this.findRelatedFile(this.getCurrentTSD(), "bin", imgnumber, true);
        if (imgfile != null)
            return imgfile;
        imgfile = this.findRelatedFile(this.getCurrentTSD(), "edf", imgnumber, true);
        if (imgfile != null)
            return imgfile;
        return null;
    }
    //    //retorna la imatge de mateix nom que el TSD o la primera de la llista (cas coarse scan)  --- ara es pot fer servir el de sobre amb -1
    //    private File findImageFileFromTSD() {
    //        //ara hem de mirar que tinguem llegit el TSD
    //        if (tsdfile!=null){
    //            if (tsdfile.isSuccessfulRead()){
    //                //PODEM BUSCAR IMATGE I SOL
    //                //LA SOL FILE LA BUSCA D2DPLOT
    ////                File solfile = FileUtils.canviExtensio(this.getCurrentTSD(), "SOL");
    ////                log.debug("solfile="+solfile.toString());
    //                boolean trobat = false;
    //                
    //                File imgfile = FileUtils.canviExtensio(this.getCurrentTSD(), "bin");
    //                log.debug("imgfile="+imgfile.toString());
    //                if (imgfile.exists())trobat = true;
    //                
    //                if (!trobat){
    //                    imgfile = FileUtils.canviExtensio(this.getCurrentTSD(), "BIN");
    //                    log.debug("imgfile="+imgfile.toString());
    //                    if (imgfile.exists())trobat = true;
    //                }
    //                
    //                if (!trobat){
    //                    imgfile = FileUtils.canviExtensio(this.getCurrentTSD(), "edf");
    //                    log.debug("imgfile="+imgfile.toString());
    //                    if (imgfile.exists())trobat = true;
    //                }
    //                
    //                if (!trobat){
    //                    imgfile = FileUtils.canviExtensio(this.getCurrentTSD(), "EDF");
    //                    log.debug("imgfile="+imgfile.toString());
    //                    if (imgfile.exists())trobat = true;
    //                }
    //                
    //                //ara provem la imatge inicial de la llista tsdfile que segur que es la bona
    //                int codifile = -1;
    //                if(tsdfile.getNfiles()>0)codifile = tsdfile.getFnum()[0];
    //                log.debug("codifile="+codifile);
    //                
    //                if (codifile<0){
    //                    txtOut.stat("Sorry, image file could not be found. Open it manually with d2Dplot");
    //                    return null;
    //                }
    //                imgfile = findImageFileFromTSD(codifile);
    //                return imgfile;
    //            }
    //            return null;
    //        }
    //        return null;
    //    }

    protected void do_btnChecksol_actionPerformed(ActionEvent arg0) {
        //el sol el gestiona d2dplot amb l'opcio -sol
        final File imgfile = this.findImageFileFromTSD(-1);
        //SI HEM TROBAT OBRIM
        if (imgfile != null) {
            this.print_logdebug_tAOut("Opening d2Dplot (imgfile=" + imgfile.toString() + ")");
            this.openInD2Dplot(imgfile.getAbsolutePath(), true);
            return;
        } else {
            this.print_logdebug_tAOut("Sorry, image file could not be found. Open it manually with d2Dplot");
        }
    }

    protected void do_btnCheckOut_actionPerformed(ActionEvent e) {
        if (!this.checkTSDrequirements())
            return;
        final File outFile = this.findRelatedFile(this.getCurrentTSD(), "out", -1, true);
        if (outFile != null) {
            this.openTEXTfile(outFile);
            return;
        }

        //        File outFile = FileUtils.canviExtensio(this.getCurrentTSD(), "OUT");
        //        if (outFile.exists()){
        //            this.openTEXTfile(outFile);
        //            return;
        //        }
        //        outFile = FileUtils.canviExtensio(this.getCurrentTSD(), "out");
        //        if (outFile.exists()){
        //            this.openTEXTfile(outFile);
        //            return;
        //        }
        //si s'ha arribat aqui no s'ha trobat fitxer out
        this.print_logdebug_tAOut("No INCO results file found (.OUT), run INCO first");
        return;
    }

    private void openTEXTfile(File txtf) {
        //we have to OPEN a text file (TSD, OUT, ...) with preferred editor
        if (txtf == null) {
            this.print_logdebug_tAOut("File not found");
            return;
        }
        final File txtEditorExec = new File(D2Dplot_global.getTxtEditPath());
        if (!txtEditorExec.exists()) {
            try {
                //use default
                if (Desktop.isDesktopSupported()) { // s'obre amb el programa per defecte
                    Desktop.getDesktop().open(txtf);
                } else {
                    new ProcessBuilder("cmd", "/c", txtf.toString()).start();
                }
            } catch (final Exception e1) {
                if (isDebug())
                    e1.printStackTrace();
            }
        } else {
            final String[] cmd = { D2Dplot_global.getTxtEditPath(), txtf.toString() };
            final ProcessBuilder pb = new ProcessBuilder(cmd);
            try {
                pb.start();
            } catch (final IOException e1) {
                if (isDebug())
                    e1.printStackTrace();
            }
        }
    }

    private void openInD2Dplot(String imgPath, boolean openSOL) {
        final File imgFull = new File(imgPath);
        final String fname = imgFull.getName();
        final String fdir = imgFull.getParent();
        log.debug("fname=" + fname);
        log.debug("fdir=" + fdir);
        ;
        //        String[] args = {"",imgFull.getAbsolutePath(),""};
        //        if(openSOL) args[2]="-sol";
        //        ArgumentLauncher.startInteractive(d2DplotMain, args);

        //provem obrir directe
        this.d2DplotMain.updateFromTTS(imgFull);

        if (openSOL) {//busquem el sol i l'obrim
            //            File fsol = ArgumentLauncher.findSOLfile(imgFull);
            File fsol = this.findRelatedFile(this.getCurrentTSD(), "sol", -1, false); //false per donar prioritat al del domini
            if (fsol == null) {
                fsol = this.findRelatedFile(this.getCurrentTSD(), "sol", -1, true); //ara mirem el SOL general (coarse)
            }
            if (fsol != null) {
                this.print_logdebug_tAOut(String.format("SOL file found: %s", fsol.toString()));
                //OBRIM dialeg INCO directament amb un fitxer SOL
                if (this.d2DplotMain.getDincoFrame() == null) {
                    this.d2DplotMain.setDincoFrame(new IncoPlot(this.ttsFrame, this.d2DplotMain.getPanelImatge()));
                }
                this.d2DplotMain.getDincoFrame().setSOLMode();
                this.d2DplotMain.getDincoFrame().loadSOLFileDirectly(fsol);
                this.d2DplotMain.getDincoFrame().setVisible(true);
                this.d2DplotMain.getPanelImatge().setDinco(this.d2DplotMain.getDincoFrame());
            } else {
                this.print_logdebug_tAOut("Could not find SOL file, try to open it manually");
            }
        }
    }

    protected void do_btnCheckCel_actionPerformed(ActionEvent e) {
        if (!this.checkTSDrequirements())
            return;
        final File celFile = this.findRelatedFile(this.getCurrentTSD(), "cel", -1, true);
        if (celFile != null) {
            this.openTEXTfile(celFile);
            return;
        }

        //        File celFile = FileUtils.canviExtensio(this.getCurrentTSD(), "CEL");
        //        if (celFile.exists()){
        //            this.openTEXTfile(celFile);
        //            return;
        //        }
        //        celFile = FileUtils.canviExtensio(this.getCurrentTSD(), "cel");
        //        if (celFile.exists()){
        //            this.openTEXTfile(celFile);
        //            return;
        //        }
        //si s'ha arribat aqui no s'ha trobat fitxer out
        this.print_logdebug_tAOut("No CELREF results file found (.CEL), run CELREF first");
        return;
    }

    protected void do_btnCheckMrg_actionPerformed(ActionEvent e) {
        if (!this.checkTSDrequirements())
            return;
        final File mrgFile = this.findRelatedFile(this.getCurrentTSD(), "mrg", -1, true);
        if (mrgFile != null) {
            this.openTEXTfile(mrgFile);
            return;
        }

        //        File mrgFile = FileUtils.canviExtensio(this.getCurrentTSD(), "MRG");
        //        if (mrgFile.exists()){
        //            this.openTEXTfile(mrgFile);
        //            return;
        //        }
        //        mrgFile = FileUtils.canviExtensio(this.getCurrentTSD(), "mrg");
        //        if (mrgFile.exists()){
        //            this.openTEXTfile(mrgFile);
        //            return;
        //        }
        //si s'ha arribat aqui no s'ha trobat fitxer out
        this.print_logdebug_tAOut("No MERGE results file found (.MRG), run MERGE first");
        return;
    }

    protected void do_btnRunMerge_actionPerformed(ActionEvent e) {
        //comprovacions previes (missatges a les subrutines) TODO
        if (!this.checkTSDrequirements())
            return;

        if (this.tsdfile.getMultdom() != 1) {
            if (!this.checkForExistingMaskBIN())
                return;
            if (!this.checkForPCSfiles())
                return;
        }

        //Fem correr el programa
        this.txtOut.saltL();
        if (this.tsdfile.getMultdom() != 1) {
            this.print_logdebug_tAOut(" RUNNING MERGE (partial datasets of 1 domain):");
        } else { //==1 multidomain
            this.print_logdebug_tAOut(" RUNNING MERGE (multiple oriented domains):");
        }

        this.print_logdebug_tAOut("  Input file: " + this.getCurrentTSD());
        this.txtOut.saltL();
        //creem i executem el thread del merge
        final Thread merge = new Thread(new TTSRunnable(TTSRunnable.MERGE));
        merge.start();
    }

    private boolean checkTSDrequirements() {
        //rellegim el TSD per si hi ha hagut canvis
        this.setCurrentTSD(this.getCurrentTSD());
        if (this.getCurrentTSD() == null) {
            this.print_logdebug_tAOut("Select first a valid TSD input file");
            return false;
        }
        if (!this.getCurrentTSD().exists()) {
            this.print_logdebug_tAOut("Selected TSD file does not exist");
            return false;
        }
        if (!FileUtils.getExtension(this.getCurrentTSD()).equalsIgnoreCase("TSD")) {
            this.print_logdebug_tAOut("Working file must have the .TSD extension");
            return false;
        }
        return true;
    }

    private boolean checkForExistingMaskBIN() {
        //        File msk = FileUtils.canviNomFitxer(this.getCurrentTSD(), "MASK");
        //        msk = FileUtils.canviExtensio(msk, "BIN");
        File msk = new File(this.getCurrentTSD().getParentFile() + FileUtils.fileSeparator + TTS.maskFileName);
        log.debug("msk=" + msk.toString());
        if (msk.exists())
            return true;
        //test uppercase
        String upp = this.getCurrentTSD().getParentFile() + FileUtils.fileSeparator + TTS.maskFileName;
        upp = upp.toUpperCase();
        log.debug("msk=" + upp);
        msk = new File(upp);
        if (msk.exists())
            return true;
        //otherwise ask for creating one
        final boolean open = FileUtils.YesNoDialog(this.ttsFrame,
                "MASK.BIN not found, open Excluded Zones module to generate one?");
        if (open) {
            //first open image
            final File imgfile = this.findImageFileFromTSD(-1);
            //first open image
            this.openInD2Dplot(imgfile.getAbsolutePath(), false);
            //then open module
            this.d2DplotMain.do_mntmExcludedZones_actionPerformed(null);
        }

        return false;
    }

    private boolean checkForPCSfiles() {
        //TODO
        //comprovem de la llista si hi ha tots els pcs
        boolean generalTrobat = true;
        if (this.tsdfile.getNfiles() > 0) {
            for (int i = 0; i < this.tsdfile.getNfiles(); i++) {
                final int codifile = this.tsdfile.getFnum()[i];
                final File pcsfile = this.findRelatedFile(this.getCurrentTSD(), "pcs", codifile, false);
                if (pcsfile == null) {
                    generalTrobat = false; //n'hi ha una al menys de no trobada
                    continue;
                }
            }
            if (generalTrobat == true) { //s'han trobat totes
                return true;
            } else {
                final boolean open = FileUtils.YesNoDialog(this.ttsFrame,
                        "Generate missing PCS files in the peaksearch module?");
                if (open) {//we will open only the first one missing
                    for (int i = 0; i < this.tsdfile.getNfiles(); i++) {
                        final int codifile = this.tsdfile.getFnum()[i];
                        final File pcsfile = this.findRelatedFile(this.getCurrentTSD(), "pcs", codifile, false);
                        if (pcsfile == null) {
                            final File imgfile = this.findImageFileFromTSD(codifile);
                            this.openInD2Dplot(imgfile.getAbsolutePath(), false);
                            this.d2DplotMain.do_mntmFindPeaks_actionPerformed(null);
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*
     * Busca un fitxer amb el mateix nom però amb l'extensió donada (majuscula o minuscula, indiferent) a un fitxer
     * original (usualment currentTSD) i
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
            log.debug("extfile=" + extfile.toString());
            if (extfile.exists())
                trobat = true;

            if (!trobat) {
                extfile = FileUtils.canviExtensio(original, ext.toUpperCase());
                log.debug("imgfile=" + extfile.toString());
                if (extfile.exists())
                    trobat = true;
            }
            if (trobat)
                return extfile;
        }

        //Segon busquem pel codi (o llista tsd) però en cada cas primer sense reduïr el nom per mantenir el "dX" i donar-li prioritat
        if (codiFile >= 0) { //busquem imatge concreta
            final String suffix = String.format("_%04d", codiFile);
            String imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().concat(suffix);
            log.debug("suffix=" + codiFile);
            log.debug("imgfileNoExt=" + imgfileNoExt);
            extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
            if (extfile != null)
                return extfile;
            //ara mirem reduint 2 caracters "dX"
            imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim()
                    .substring(0, FileUtils.getFNameNoExt(this.getCurrentTSD()).length() - 2).concat(suffix); //check also removing d1 from: z1p1d1
            log.debug("imgfileNoExt=" + imgfileNoExt);
            extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
            if (extfile != null)
                return extfile;
        } else {//aqui mirem la llsta del tsd (retorna el primer trobat) pero igual que abans mirar primer per domini
            for (int i = 0; i < this.tsdfile.getNfiles(); i++) {
                codiFile = this.tsdfile.getFnum()[i];
                final String suffix = String.format("_%04d", codiFile);
                String imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().concat(suffix);
                log.debug("suffix=" + codiFile);
                log.debug("imgfileNoExt=" + imgfileNoExt);
                extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
                if (extfile != null)
                    return extfile;
                //ara mirem reduint 2 caracters "dX"
                imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim()
                        .substring(0, FileUtils.getFNameNoExt(this.getCurrentTSD()).length() - 2).concat(suffix); //check also removing d1 from: z1p1d1
                log.debug("imgfileNoExt=" + imgfileNoExt);
                extfile = this.findFileExtCaseInsensitive(imgfileNoExt, ext);
                if (extfile != null)
                    return extfile;
            }
        }
        return null;
    }

    //    //metode a cridar des de l'altre amb mes paràmetres, per tal de reduïr repetició de codi
    //    private File findRelatedFile(String ext, int seqNumber) {
    //        boolean trobat = false;
    //        int codifile = tsdfile.getFnum()[seqNumber];
    //        log.debug("codifile="+codifile);
    //        String suffix = String.format("_%04d", codifile);
    //        String imgfileNoExt = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().concat(suffix);
    //        String imgfileNoExtNoDomain = FileUtils.getFNameNoExt(this.getCurrentTSD()).trim().substring(0, FileUtils.getFNameNoExt(this.getCurrentTSD()).length()-2).concat(suffix); //check also removing d1 from: z1p1d1
    //        log.debug("suffix="+codifile);
    //        log.debug("imgfileNoExt="+imgfileNoExt);
    //        log.debug("imgfileNoExtNoDomain="+imgfileNoExtNoDomain);
    //        
    //        //primer mirem domini
    //        
    //        
    //        
    //        File extfile = new File(imgfileNoExt+"."+ext.toLowerCase());
    //        if (extfile.exists()) trobat = true;
    //        if (!trobat) {
    //            extfile = new File(imgfileNoExt+"."+ext.toUpperCase());
    //            if (extfile.exists())trobat = true;
    //        }
    //        if (!trobat) {
    //            extfile = new File(imgfileNoExtNoDomain+"."+ext.toLowerCase());
    //            if (extfile.exists())trobat = true;
    //        }
    //        if (!trobat) {
    //            extfile = new File(imgfileNoExtNoDomain+"."+ext.toUpperCase());
    //            if (extfile.exists())trobat = true;
    //        }
    //        if (!trobat) {
    //            this.print_loginfo_tAOut(imgfileNoExt+"."+ext.toLowerCase()+" or ."+ext.toUpperCase()+" not found");
    //            this.print_loginfo_tAOut(imgfileNoExtNoDomain+"."+ext.toLowerCase()+" or ."+ext.toUpperCase()+" not found");
    //        }
    //        if (trobat) {
    //            this.print_loginfo_tAOut("file found: "+extfile.getAbsolutePath());
    //            return extfile;
    //        }
    //        return null;
    //    }

    private File findFileExtCaseInsensitive(String filenameNoExt, String ext) {
        boolean trobat = false;
        File extfile = new File(filenameNoExt + "." + ext.toLowerCase());
        if (extfile.exists())
            trobat = true;
        if (!trobat) {
            extfile = new File(filenameNoExt + "." + ext.toUpperCase());
            if (extfile.exists())
                trobat = true;
        }
        if (!trobat) {
            this.print_logdebug_tAOut(
                    filenameNoExt + "." + ext.toLowerCase() + " or ." + ext.toUpperCase() + " not found");
            return null;
        }
        this.print_logdebug_tAOut("file found: " + extfile.getAbsolutePath());
        return extfile;
    }

    protected void do_btnRunCelRef_actionPerformed(ActionEvent e) {
        //comprovacions previes (missatges a les subrutines) TODO
        if (!this.checkTSDrequirements())
            return;
        //        if (!checkForExistingMaskBIN())return; 
        //        if (!checkForPCSfiles())return;

        //Fem correr el programa
        this.txtOut.saltL();
        this.print_logdebug_tAOut(" RUNNING CELREF:             ");
        this.print_logdebug_tAOut("  Input file: " + this.getCurrentTSD());
        this.txtOut.saltL();
        //creem i executem el thread del celref
        final Thread celref = new Thread(new TTSRunnable(TTSRunnable.CELREF));
        celref.start();
    }

    protected void do_btnGenerateTsd_actionPerformed(ActionEvent e) {
        this.print_logdebug_tAOut("This will generate a template TSD file");
        final FileNameExtensionFilter[] filt = { new FileNameExtensionFilter("TSD file", "tsd", "TSD") };
        File f = FileUtils.fchooserSaveAsk(this.ttsFrame, D2Dplot_global.getWorkdirFile(), filt, "TSD");
        if (f == null)
            return;
        this.updateWorkDir(f);
        //        f = FileUtils.canviExtensio(f, "TSD");
        //        if (f.exists()){
        //            boolean over = FileUtils.YesNoDialog(this, "Overwrite existing "+f.getName()+" file?");
        //            if(!over)return;
        //        }
        f = ImgFileUtils.generateTSD(f, false);//ext forçada al fchoser
        if (f == null) {
            this.print_logdebug_tAOut("Error generating TSD file");
            return;
        } else {
            this.print_logdebug_tAOut("TSD file saved: " + f.getAbsolutePath());
            this.print_logdebug_tAOut(
                    "Now you can edit TSD file according to your sample, check the manual for all the details.");
            this.print_logdebug_tAOut("Do not forget to follow the file naming convention suggested on the manual.");
            //set as working TSD
            final boolean open = FileUtils.YesNoDialog(this.ttsFrame,
                    "Load " + f.getName() + " as working file and edit it?");
            if (open) {
                this.setCurrentTSD(f);
                this.openTEXTfile(f);
            }
        }
    }

    protected void do_btnCopyTsd_actionPerformed(ActionEvent e) {
        if (this.getCurrentTSD() == null) {
            this.print_logdebug_tAOut("Select first a valid TSD input file");
            return;
        }
        if (!this.getCurrentTSD().exists()) {
            this.print_logdebug_tAOut("Selected TSD file does not exist");
            return;
        }
        final FileNameExtensionFilter[] filt = { new FileNameExtensionFilter("TSD file", "tsd", "TSD") };
        final File dest = FileUtils.fchooserSaveAsk(this.ttsFrame, new File(D2Dplot_global.getWorkdir()), filt, "TSD");
        if (dest == null)
            return;
        this.updateWorkDir(dest);
        FileUtils.copyFile(this.getCurrentTSD(), dest, false, this.txtOut);
        final boolean over = FileUtils.YesNoDialog(this.ttsFrame,
                "Load " + dest.getName() + " as working file and edit it?");
        if (over) {
            this.setCurrentTSD(dest);
            this.openTEXTfile(dest);
        }
    }

    //String suffixdX,
    //alon, alat, aspin

    //destfile ha de tenir el suffix dX, cal preguntar el num domini i el num d'imatges abans
    private File copyTSDtoIREF1(File out) {
        //copia linia a linia
        //canvi linia iref
        //posar/canviar alon alat aspin
        //nimages/swing trobar central i escriure-ho be al final

        if (this.tsdfile != null) {
            if (this.tsdfile.isSuccessfulRead()) {
                if (!this.checkTSDrequirements())
                    return null;
                if (this.d2DplotMain.getDincoFrame() == null) {
                    this.print_logdebug_tAOut("no opened solutions found, try doing it by editing manually TSD files");
                    return null;
                }
                final float lon = this.d2DplotMain.getDincoFrame().getActiveOrientSol().getAngR_lon();
                final float lat = this.d2DplotMain.getDincoFrame().getActiveOrientSol().getAngS_lat();
                final float spin = this.d2DplotMain.getDincoFrame().getActiveOrientSol().getAngT_spin();

                final float swing = this.tsdfile.getSwing(); //es la deltaphi, es a dir mitja oscil·lació

                int centralImage = -1;
                for (int i = 0; i < this.tsdfile.getNfiles(); i++) {
                    final float off = this.tsdfile.getFnumAngOff()[i];
                    if (FastMath.round(off) == 0) {
                        centralImage = this.tsdfile.getFnum()[i];
                        break;
                    }
                }
                if (centralImage < 0) {
                    this.print_logdebug_tAOut(
                            "Could not find image number with offset 0, try doing it by editing manually TSD files");
                    return null;
                }
                //tindrem les imatges 0 1 2 3... centralImage ...3 2 1 0  (es a dir "simètric" segons la central)

                //ara llegim i escribim
                //                File out = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), null, "TSD");
                //                boolean firstDash = false;
                try {
                    final Scanner inTSDfile = new Scanner(new BufferedReader(new FileReader(this.getCurrentTSD())));
                    final PrintWriter outTSDfile = new PrintWriter(new BufferedWriter(new FileWriter(out)));

                    while (inTSDfile.hasNextLine()) {
                        String line = inTSDfile.nextLine();

                        if (line.startsWith("&CONTROL")) {
                            //comença el bloc control
                            outTSDfile.println(line);
                            boolean end = false;
                            while (!end) {
                                line = inTSDfile.nextLine();
                                if (line.contains("/")) {
                                    end = true;
                                    outTSDfile.println(String.format("ALON=%.3f,", lon));
                                    outTSDfile.println(String.format("ALAT=%.3f,", lat));
                                    outTSDfile.println(String.format("SPIN=%.3f,", spin));
                                    outTSDfile.println("/");
                                    continue;
                                }
                                if (line.startsWith("ALON"))
                                    continue;
                                if (line.startsWith("ALAT"))
                                    continue;
                                if (line.startsWith("SPIN"))
                                    continue;
                                if (line.startsWith("IOFF")) {
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
                            outTSDfile.println(Integer.toString(centralImage * 2 + 1));
                            for (int i = 0; i <= centralImage * 2; i++) {
                                outTSDfile.println(String.format("%d,%.2f", i, swing * (i - centralImage)));
                            }
                            break;
                        }
                        //si no ha passat res escribim linia
                        outTSDfile.println(line);
                    }
                    inTSDfile.close();
                    outTSDfile.close();
                    this.print_logdebug_tAOut("file TSD written");
                    return out;

                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            this.print_logdebug_tAOut("Current TSD file has not been read successfully, check for syntax errors");
            return null;
        }
        this.print_logdebug_tAOut("no TSD file opened");
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
        TTS.incoExec = incoExec;
    }

    public static String getMergeExec() {
        return mergeExec;
    }

    public static void setMergeExec(String mergeExec) {
        TTS.mergeExec = mergeExec;
    }

    public static String getCelrefExec() {
        return celrefExec;
    }

    public static void setCelrefExec(String celrefExec) {
        TTS.celrefExec = celrefExec;
    }

    private void print_logdebug_tAOut(String toPrint) {
        this.txtOut.stat(toPrint);
        log.debug(toPrint);
    }

    protected void do_btnCreateTsdIref_actionPerformed(ActionEvent e) {
        //1rCOMPROVAR QUE HI HA UNA SOLUCIO OBERTA
        if (this.tsdfile != null) {
            if (this.tsdfile.isSuccessfulRead()) {
                if (!this.checkTSDrequirements())
                    return;
                if (this.d2DplotMain.getDincoFrame() == null) {
                    this.print_logdebug_tAOut(
                            "no opened solutions found, you must open a SOL file to generate TSD(IREF=1)");
                    return;
                }
                final FileNameExtensionFilter[] filt = { new FileNameExtensionFilter("TSD file", "tsd", "TSD") };
                final File out = FileUtils.fchooserSaveAsk(this.ttsFrame, D2Dplot_global.getWorkdirFile(), filt, "TSD");
                if (out == null)
                    return;
                final File f = this.copyTSDtoIREF1(out);
                if (f != null) {
                    //open new TSD
                    final boolean over = FileUtils.YesNoDialog(this.ttsFrame,
                            "Load " + f.getName() + " as working file and edit it?");
                    if (over) {
                        this.setCurrentTSD(f);
                        this.openTEXTfile(f);
                    }
                }
                this.print_logdebug_tAOut(
                        "TSD (IREF=1) file created, check that everything is ok (especially PCS/HKL block) and click RUN");
            }
        }
    }

    protected void do_btnMultdom_actionPerformed(ActionEvent e) {
        //1r Demanem fitxer TSD a guardar
        if (this.tsdfile != null) {
            if (this.tsdfile.isSuccessfulRead()) {
                final FileNameExtensionFilter[] filt = { new FileNameExtensionFilter("TSD file", "tsd", "TSD") };
                final File f = FileUtils.fchooserSaveAsk(this.ttsFrame, D2Dplot_global.getWorkdirFile(), filt, "TSD",
                        "New TSD file to create");
                if (f == null)
                    return;
                final FileNameExtensionFilter[] filt2 = { new FileNameExtensionFilter("HKL file", "hkl", "HKL") };
                final File[] hkls = FileUtils.fchooserMultiple(this.ttsFrame, D2Dplot_global.getWorkdirFile(), filt2, 0,
                        "Select HKL files to merge (FULL oriented domains only)");
                final String[] hklfilenames = new String[hkls.length];
                for (int i = 0; i < hkls.length; i++) {
                    hklfilenames[i] = hkls[i].getName();
                }
                final File newTSD = this.copyTSDtoMULTDOM(this.getCurrentTSD(), f, hklfilenames);
                if (newTSD != null) {
                    //ask to open and run merge
                    final boolean over = FileUtils.YesNoDialog(this.ttsFrame,
                            "Load " + newTSD.getName() + " as working file and run merge?");
                    if (over) {
                        this.setCurrentTSD(f);
                        this.btnRunMerge.doClick();
                        return;
                    }
                }
            }
            this.print_logdebug_tAOut("You should have a valid single-domain TSD as working file");
        }

    }

    private File copyTSDtoMULTDOM(File tsdin, File tsdout, String[] hklfilenames) {
        //copia linia a linia
        //canvi linia MULTDOM=1,
        //PCS hkl escriure llista de fitxers HKL
        try {
            final Scanner inTSDfile = new Scanner(new BufferedReader(new FileReader(tsdin)));
            final PrintWriter outTSDfile = new PrintWriter(new BufferedWriter(new FileWriter(tsdout)));

            while (inTSDfile.hasNextLine()) {
                String line = inTSDfile.nextLine();
                boolean multdomFound = false;
                if (line.startsWith("&CONTROL")) {
                    //comença el bloc control
                    outTSDfile.println(line);
                    boolean end = false;
                    while (!end) {
                        line = inTSDfile.nextLine();

                        if (line.startsWith("MULTDOM")) {
                            outTSDfile.println("MULTDOM=1,");
                            multdomFound = true;
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
            for (final String hklfilename : hklfilenames) {
                outTSDfile.println(hklfilename);
            }
            outTSDfile.close();
            this.print_logdebug_tAOut("file TSD written");
            return tsdout;

        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void do_btnCreateTsdFor_actionPerformed(ActionEvent e) { //molt igual a MULTDOM=1
        //1r Demanem fitxer TSD a guardar
        if (this.tsdfile != null) {
            if (this.tsdfile.isSuccessfulRead()) {
                final FileNameExtensionFilter[] filt = { new FileNameExtensionFilter("TSD file", "tsd", "TSD") };
                final File f = FileUtils.fchooserSaveAsk(this.ttsFrame, D2Dplot_global.getWorkdirFile(), filt, "TSD",
                        "New TSD file to create");
                if (f == null)
                    return;
                final FileNameExtensionFilter[] filt2 = { new FileNameExtensionFilter("HKL file", "hkl", "HKL") };
                final File[] hkls = FileUtils.fchooserMultiple(this.ttsFrame, D2Dplot_global.getWorkdirFile(), filt2, 0,
                        "Select HKL files to use in CelRef (PARTIAL oriented domains only)");
                final String[] hklfilenames = new String[hkls.length];
                for (int i = 0; i < hkls.length; i++) {
                    hklfilenames[i] = hkls[i].getName();
                }
                final File newTSD = this.copyTSDtoCELREF(this.getCurrentTSD(), f, hklfilenames);
                if (newTSD != null) {
                    //ask to open and run merge
                    final boolean over = FileUtils.YesNoDialog(this.ttsFrame,
                            "Load " + newTSD.getName() + " as working file and run celref?");
                    if (over) {
                        this.setCurrentTSD(f);
                        this.btnRunCelRef.doClick();
                        return;
                    }
                }
            }
            this.print_logdebug_tAOut("You should have a valid single-domain TSD as working file");
        }
    }

    private File copyTSDtoCELREF(File tsdin, File tsdout, String[] hklfilenames) {
        //copia linia a linia
        //PCS hkl escriure llista de fitxers HKL
        try {
            final Scanner inTSDfile = new Scanner(new BufferedReader(new FileReader(tsdin)));
            final PrintWriter outTSDfile = new PrintWriter(new BufferedWriter(new FileWriter(tsdout)));

            while (inTSDfile.hasNextLine()) {
                final String line = inTSDfile.nextLine();
                if (line.startsWith("PCS")) {//ja estem al final
                    outTSDfile.println(line);
                    outTSDfile.println(Integer.toString(hklfilenames.length));
                    break;
                }
                outTSDfile.println(line);
            }
            inTSDfile.close();
            //ara cal escriure els noms
            for (final String hklfilename : hklfilenames) {
                outTSDfile.println(hklfilename);
            }
            outTSDfile.close();
            this.print_logdebug_tAOut("file TSD written");
            return tsdout;

        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setVisible(boolean vis) {
        this.ttsFrame.setVisible(vis);
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public void dispose() {
        if (TTSInExecution) {
            this.stopExecution();
        }
        this.ttsFrame.dispose();
    }
}