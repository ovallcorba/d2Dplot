package com.vava33.d2dplot;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.FindPksTableRenderer;
import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.ImgOps.PkIntegrateFileWorker;
import com.vava33.d2dplot.auxi.ImgOps.PkSCIntegrateFileWorker;
import com.vava33.d2dplot.auxi.OrientSolucio;
import com.vava33.d2dplot.auxi.Patt2Dzone;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.d2dplot.auxi.Peak;
import com.vava33.d2dplot.auxi.PuntSolucio;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class PeakSearch {

    private static final String className = "PKsearch";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private JDialog pksearchDialog;
    private JFrame parent;
    private JPanel contentPane;

    private boolean fromInco;
    private IncoPlot dincoFrame;
    private ImagePanel iPanel;

    private JCheckBox chckbxOnTop;
    private JPanel panel;
    private JLabel lblDelsig;
    private JLabel lblZoneradius;
    private JButton btnCalculate;
    private JCheckBox chckbxShowPoints;
    private JTextField txtDelsig;
    private JTextField txtZoneradius;
    private JButton btnClose;
    private JSpinner spinner;
    private JLabel lblPlotSize;
    private JCheckBox chckbxLpCorrection;
    private JCheckBox chckbxAddremovePeaks;
    private JPanel panel_2;
    private JLabel lblthWidth;
    private JTextField txtTtol;
    private JLabel lblAzimAperture;
    private JTextField txtAngdeg;
    private JLabel lblBkgPoints;
    private JTextField txtBkgPt;

    private JTable table;
    private JScrollPane scrollPane_1;
    private JSpinner spinerAxis;
    private JButton btnExportPeakList;

    //defaults
    public static int def_bkgpt = 20;
    public static int def_tol2tpix = 30;
    public static float def_angDeg = 4.0f;
    public static float def_delsig = 6.0f;
    public static int def_zoneR = FastMath.round(def_tol2tpix / 2.f);
    public static int def_minpix = 15;
    public static int nzonesFindPeaks = 24;
    public static float def_bkgPxAutoPercent = 0.005f;
    public static int def_minbkgPx = 10;

    //generals
    public static int bkgpt;
    //    public static float tol2t;
    public static int tol2tpix;
    public static float angDeg;
    public static float delsig;
    public static int zoneR;
    public static int minpix;
    public static int iosc = 0;
    public static boolean estimbkg = false;
    public static boolean pondmerging = true;

    private JPanel panel_3;
    private JPanel panel_4;
    private JLabel lblMinNrPixels;
    private JTextField txtMinpixels;
    private JCheckBox chckbxAutointrad;
    private JCheckBox chckbxAutoazim;
    private JCheckBox chckbxAutobkgpt;
    private JPanel panel_1;
    private JButton btnExportTable;
    private JButton btnMaskbin;

    //    Pattern2D maskInt;
    private JButton btnBatch;
    private JLabel lblNpks;

    ProgressMonitor pm;
    PkIntegrateFileWorker convwk;
    PkSCIntegrateFileWorker pkscwk;
    private JButton btnImport;
    private JButton btnRemoveDiamonds;
    private JButton btnRemoveSaturated;

    private File fileout;
    private JButton btnBatchout;
    private JCheckBox chckbxCalcBkgslow;
    private JCheckBox chckbxAvg;
    private JPanel panel_5;

    /**
     * Create the frame.
     */
    public PeakSearch(JFrame parent, ImagePanel ip, boolean fromInco, IncoPlot dframe) {
        this.pksearchDialog = new JDialog(parent, "Peak Search and Integrate", false);
        this.parent = parent;
        this.pksearchDialog.setAlwaysOnTop(true);
        this.fromInco = fromInco;
        this.dincoFrame = dframe;
        this.iPanel = ip;
        this.pksearchDialog
                .setIconImage(Toolkit.getDefaultToolkit().getImage(IncoPlot.class.getResource("/img/Icona.png")));
        this.pksearchDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pksearchDialog.setBounds(100, 100, 860, 600);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.pksearchDialog.setContentPane(this.contentPane);
        this.contentPane.setLayout(new MigLayout("fill, insets 3", "[grow]", "[grow][]"));

        final JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.contentPane.add(splitPane, "cell 0 0,grow");

        this.panel = new JPanel();
        splitPane.setLeftComponent(this.panel);
        this.panel.setLayout(new MigLayout("insets 5", "[grow][grow]", "[][][]"));

        this.panel_4 = new JPanel();
        this.panel.add(this.panel_4, "cell 0 0 2 1,grow");
        this.panel_4.setLayout(new MigLayout("", "[][][][grow]", "[]"));

        this.chckbxShowPoints = new JCheckBox("Show Points");
        this.chckbxShowPoints.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PeakSearch.this.do_chckbxShowPoints_itemStateChanged(e);
            }
        });
        this.panel_4.add(this.chckbxShowPoints, "cell 0 0");
        this.chckbxShowPoints.setSelected(true);

        this.lblPlotSize = new JLabel("Plot size");
        this.panel_4.add(this.lblPlotSize, "cell 1 0");

        this.spinner = new JSpinner();
        this.spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                PeakSearch.this.do_spinner_stateChanged(e);
            }
        });
        this.panel_4.add(this.spinner, "cell 2 0");
        this.spinner.setModel(new SpinnerNumberModel(5, 1, 10, 1));

        this.chckbxOnTop = new JCheckBox("on top");
        this.panel_4.add(this.chckbxOnTop, "cell 3 0,alignx right");
        this.chckbxOnTop.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                PeakSearch.this.do_chckbxOnTop_itemStateChanged(arg0);
            }
        });
        this.chckbxOnTop.setSelected(true);

        this.panel_3 = new JPanel();
        this.panel_3.setBorder(new TitledBorder(null, "Peak detection", TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(51, 51, 51)));
        this.panel.add(this.panel_3, "cell 0 1,grow");
        this.panel_3.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));

        this.lblDelsig = new JLabel("ESD factor=");
        this.panel_3.add(this.lblDelsig, "cell 0 0,alignx right");

        this.txtDelsig = new JTextField();
        this.panel_3.add(this.txtDelsig, "cell 1 0,growx");
        this.txtDelsig.setText("3.0");
        this.txtDelsig.setColumns(10);

        this.chckbxCalcBkgslow = new JCheckBox("Calc. bkg (slow)");
        this.panel_3.add(this.chckbxCalcBkgslow, "cell 2 0");

        this.lblZoneradius = new JLabel("Peak merge zone (px)=");
        this.panel_3.add(this.lblZoneradius, "cell 0 1,alignx right");

        this.txtZoneradius = new JTextField();
        this.panel_3.add(this.txtZoneradius, "cell 1 1,growx");
        this.txtZoneradius.setText(Integer.toString(def_zoneR));
        this.txtZoneradius.setColumns(10);

        this.chckbxAvg = new JCheckBox("Avg. position");
        this.chckbxAvg.setSelected(true);
        this.panel_3.add(this.chckbxAvg, "cell 2 1");

        this.lblMinNrPixels = new JLabel("Min. pixels for a peak=");
        this.panel_3.add(this.lblMinNrPixels, "cell 0 2,alignx right");

        this.txtMinpixels = new JTextField();
        this.txtMinpixels.setText("12");
        this.panel_3.add(this.txtMinpixels, "cell 1 2,growx");
        this.txtMinpixels.setColumns(10);

        this.chckbxAddremovePeaks = new JCheckBox("Add/Remove peaks");
        this.chckbxAddremovePeaks.setToolTipText("Once enabled, left click to Add, right click to remove");
        this.panel_3.add(this.chckbxAddremovePeaks, "cell 0 3,alignx center");

        this.btnRemoveDiamonds = new JButton("Rem. Diamonds");
        this.panel_3.add(this.btnRemoveDiamonds, "cell 1 3");
        this.btnRemoveDiamonds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PeakSearch.this.do_btnRemoveDiamonds_actionPerformed(e);
            }
        });

        this.btnRemoveSaturated = new JButton("Rem. Saturated");
        this.panel_3.add(this.btnRemoveSaturated, "cell 2 3");
        this.btnRemoveSaturated.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PeakSearch.this.do_btnRemoveSaturated_actionPerformed(arg0);
            }
        });

        this.btnCalculate = new JButton("Calculate");
        this.btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PeakSearch.this.do_btnCalculate_actionPerformed(arg0);
            }
        });

        this.panel_2 = new JPanel();
        this.panel_2.setBorder(new TitledBorder(null, "Integration parameters", TitledBorder.LEADING, TitledBorder.TOP,
                null, new Color(51, 51, 51)));
        this.panel.add(this.panel_2, "cell 1 1,grow");
        this.panel_2.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));

        this.lblthWidth = new JLabel("Radial width (px)");
        this.panel_2.add(this.lblthWidth, "cell 0 0,alignx trailing");

        this.txtTtol = new JTextField();
        this.txtTtol.setText(Integer.toString(def_tol2tpix));
        this.panel_2.add(this.txtTtol, "cell 1 0,growx");
        this.txtTtol.setColumns(10);

        this.chckbxAutointrad = new JCheckBox("auto");
        this.chckbxAutointrad.setSelected(true);
        this.panel_2.add(this.chckbxAutointrad, "cell 2 0");

        this.lblAzimAperture = new JLabel("Azim aperture (º)");
        this.panel_2.add(this.lblAzimAperture, "cell 0 1,alignx trailing");

        this.txtAngdeg = new JTextField();
        this.txtAngdeg.setText(Float.toString(def_angDeg));
        this.panel_2.add(this.txtAngdeg, "cell 1 1,growx");
        this.txtAngdeg.setColumns(10);

        this.chckbxAutoazim = new JCheckBox("auto");
        this.chckbxAutoazim.setSelected(true);
        this.panel_2.add(this.chckbxAutoazim, "cell 2 1");

        this.lblBkgPoints = new JLabel("Background px");
        this.panel_2.add(this.lblBkgPoints, "cell 0 2,alignx trailing");

        this.txtBkgPt = new JTextField();
        this.txtBkgPt.setText(Integer.toString(def_bkgpt));
        this.panel_2.add(this.txtBkgPt, "cell 1 2,growx");
        this.txtBkgPt.setColumns(10);

        this.chckbxAutobkgpt = new JCheckBox("auto");
        this.chckbxAutobkgpt.setSelected(true);
        this.panel_2.add(this.chckbxAutobkgpt, "cell 2 2");

        this.chckbxLpCorrection = new JCheckBox("LP correction");
        this.chckbxLpCorrection.setSelected(true);
        this.panel_2.add(this.chckbxLpCorrection, "cell 0 3,alignx center");

        this.spinerAxis = new JSpinner();
        this.spinerAxis.setModel(new SpinnerListModel(new String[] { "V oscil. axis", "H oscil. axis" }));
        this.panel_2.add(this.spinerAxis, "cell 1 3,growx");
        this.panel.add(this.btnCalculate, "flowx,cell 0 2 2 1,alignx center");

        this.lblNpks = new JLabel("");
        this.panel.add(this.lblNpks, "cell 1 2,alignx right");

        this.scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(this.scrollPane_1);
        this.table = new JTable() {
            private static final long serialVersionUID = 1403003908758846889L;

            @Override
            public boolean getScrollableTracksViewportWidth() {
                return this.getPreferredSize().width < this.getParent().getWidth();
            }
        };
        this.table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                PeakSearch.this.do_table_keyReleased(e);
            }
        });
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                PeakSearch.this.do_table_valueChanged(lse);
            }
        });

        this.table.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, },
                new String[] { "XPix", "YPix", "Radius", "Ymax", "Fh2", "s(Fh2)", "Ymean", "Npix", "Ybkg", "s(Ybkg)",
                        "Nbkg", "RadWth", "AzimDeg", "dsp", "p", "Swarm", "Satur", "nearMsk"
                //  0      1        2         3      4       5         6        7       8       9         10        11       12        13    14    15       16        17
                //                Float   Float     Float     Int   Float   Float     Float     Int    Float   Float      Int       Int      Float    Float  Float  Int      Int      Bool
                }) {
            /**
             *
             */
            private static final long serialVersionUID = -4167032016502576681L;
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] { Float.class, Float.class, Float.class, Integer.class, Float.class,
                    Float.class, Float.class, Integer.class, Float.class, Float.class, Integer.class, Integer.class,
                    Float.class, Float.class, Float.class, Integer.class, Integer.class, String.class };

            @Override
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Class getColumnClass(int columnIndex) {
                return this.columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;//This causes all cells to be not editable
            }

        });
        this.table.setFillsViewportHeight(true);
        this.table.setDefaultRenderer(Float.class, new FindPksTableRenderer());
        this.table.setDefaultRenderer(Integer.class, new FindPksTableRenderer());
        this.table.setDefaultRenderer(String.class, new FindPksTableRenderer());
        this.table.setAutoCreateRowSorter(true);
        this.table.getTableHeader().setReorderingAllowed(false);

        this.scrollPane_1.setViewportView(this.table);

        this.panel_1 = new JPanel();
        this.contentPane.add(this.panel_1, "flowx,cell 0 1,grow");
        this.panel_1.setLayout(new MigLayout("", "[][][grow]", "[grow][]"));

        this.btnExportTable = new JButton("Export Table");
        this.btnExportTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PeakSearch.this.do_btnExportTable_actionPerformed(arg0);
            }
        });
        this.panel_1.add(this.btnExportTable, "cell 0 0,grow");

        this.panel_5 = new JPanel();
        this.panel_5.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "tts_software (INCO)",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        this.panel_1.add(this.panel_5, "cell 1 0 1 2,grow");
        this.panel_5.setLayout(new MigLayout("", "[][][][]", "[]"));

        this.btnExportPeakList = new JButton("Write PCS");
        this.panel_5.add(this.btnExportPeakList, "cell 0 0");

        this.btnBatch = new JButton("Batch Proc. (PCS)");
        this.btnBatch.setToolTipText("Batch processing (one PCS file per image)");
        this.panel_5.add(this.btnBatch, "cell 1 0");
        this.btnBatch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PeakSearch.this.do_btnBatch_actionPerformed(arg0);
            }
        });

        this.btnBatchout = new JButton("Batch Proc. (OUT)");
        this.btnBatchout.setToolTipText("Batch processing (1 single file with all images output)");
        this.panel_5.add(this.btnBatchout, "cell 2 0");
        this.btnBatchout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PeakSearch.this.do_btnBatchout_actionPerformed(arg0);
            }
        });

        this.btnMaskbin = new JButton("MASK.BIN");
        this.panel_5.add(this.btnMaskbin, "cell 3 0,growx");
        this.btnMaskbin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PeakSearch.this.do_btnMaskbin_actionPerformed(e);
            }
        });
        this.btnExportPeakList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PeakSearch.this.do_btnExportPeakList_actionPerformed(arg0);
            }
        });

        this.btnImport = new JButton("Import Table");
        this.btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PeakSearch.this.do_btnImport_actionPerformed(arg0);
            }
        });
        this.panel_1.add(this.btnImport, "cell 0 1,grow");

        this.btnClose = new JButton("close");
        this.panel_1.add(this.btnClose, "cell 2 1,alignx right");
        this.btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PeakSearch.this.do_btnClose_actionPerformed(e);
            }
        });

        this.table.getColumnModel().getColumn(0).setPreferredWidth(70);
        this.table.getColumnModel().getColumn(1).setPreferredWidth(70);
        this.table.getColumnModel().getColumn(2).setPreferredWidth(70);
        this.table.getColumnModel().getColumn(3).setPreferredWidth(70);
        this.table.getColumnModel().getColumn(4).setPreferredWidth(65);
        this.table.getColumnModel().getColumn(5).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(6).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(7).setPreferredWidth(55);
        this.table.getColumnModel().getColumn(8).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(9).setPreferredWidth(55);
        this.table.getColumnModel().getColumn(10).setPreferredWidth(50);
        this.table.getColumnModel().getColumn(11).setPreferredWidth(50);
        this.table.getColumnModel().getColumn(12).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(13).setPreferredWidth(60);
        this.table.getColumnModel().getColumn(14).setPreferredWidth(55);
        this.table.getColumnModel().getColumn(15).setPreferredWidth(50);
        this.table.getColumnModel().getColumn(16).setPreferredWidth(50);
        this.table.getColumnModel().getColumn(17).setPreferredWidth(50);

        this.inicia(fromInco);
    }

    public void inicia(boolean fromINCO) {
        this.fromInco = fromINCO;
        //        this.patt2d=this.iPanel.getPatt2D();

        //resetejem si hi ha peaksearch previ (de l'inco o wherever)
        if (this.getPatt2D().getPkSearchResult() != null)
            this.getPatt2D().getPkSearchResult().clear();

        if (this.fromInco) {
            this.lblDelsig.setEnabled(false);
            this.lblZoneradius.setEnabled(false);
            this.txtDelsig.setEnabled(false);
            this.txtZoneradius.setEnabled(false);
            this.btnCalculate.doClick();
            this.lblMinNrPixels.setEnabled(false);
            this.txtMinpixels.setEnabled(false);
        }

        //        this.updateTable(); //aixo peta perque llista pics es null
        ((DefaultTableModel) this.table.getModel()).setRowCount(0);
        //podria fer això pero millor ho deixo tot tal com està
        //        txtDelsig.setText(Float.toString(def_delsig));
        //        txtZoneradius.setText(Integer.toString(def_zoneR));
        //        txtMinpixels.setText(Integer.toString(def_minpix));
        //        txtTtol.setText(Integer.toString(def_tol2tpix));
        //        txtAngdeg.setText(Float.toString(def_angDeg));
        //        txtBkgPt.setText(Integer.toString(def_bkgpt));
        this.iPanel.actualitzarVista();
    }

    public void setIpanel(ImagePanel ipanel) {
        this.iPanel = ipanel;
    }

    public ImagePanel getIPanel() {
        return this.iPanel;
    }

    public Pattern2D getPatt2D() {
        return this.getIPanel().getPatt2D();
    }

    protected void do_chckbxOnTop_itemStateChanged(ItemEvent arg0) {
        this.pksearchDialog.setAlwaysOnTop(this.chckbxOnTop.isSelected());
    }

    public boolean isShowPoints() {
        return this.chckbxShowPoints.isSelected();
    }

    public boolean isEditPoints() {
        return this.chckbxAddremovePeaks.isSelected();
    }

    private void searchPeaks() {
        //        boolean t2dep = chckbxAutodelsig.isSelected();
        this.readSearchOptions();
        //        ArrayList<Peak> pks = ImgOps.findPeaks(patt2d, delsig, zoneR, t2dep, nzonesFindPeaks, minpix, false);
        final ArrayList<Peak> pks = ImgOps.findPeaks(this.getPatt2D(), delsig, zoneR, minpix, false, estimbkg,
                pondmerging);

        //        if(chckbxAvoidDiamonds.isSelected()){
        //            
        //        }
        //if noSaturated //TODO

        this.getPatt2D().setPkSearchResult(pks);
        this.iPanel.actualitzarVista();
    }

    protected void do_btnRemoveSaturated_actionPerformed(ActionEvent arg0) {
        final int removed = ImgOps.removeSaturPeaks(this.getPatt2D().getPkSearchResult());
        log.info(String.format("Removed %d saturated peaks", removed));
        this.lblNpks.setText(String.format("%d Peaks!", this.getPatt2D().getPkSearchResult().size()));
        this.iPanel.actualitzarVista();
    }

    protected void do_btnRemoveDiamonds_actionPerformed(ActionEvent e) {
        final int removed = ImgOps.removeDiamondPeaks(this.getPatt2D().getPkSearchResult());
        log.info(String.format("Removed %d diamond (guessed) peaks", removed));
        this.lblNpks.setText(String.format("%d Peaks!", this.getPatt2D().getPkSearchResult().size()));
        this.iPanel.actualitzarVista();
    }

    public void readSearchOptions() {
        try {
            delsig = Float.parseFloat(this.txtDelsig.getText());
        } catch (final Exception e) {
            log.warning(String.format("Cannot read delsig, using default value %f", def_delsig));
        }
        try {
            zoneR = Integer.parseInt(this.txtZoneradius.getText());
        } catch (final Exception e) {
            log.warning(String.format("Cannot read zone radius, using default value %d", def_zoneR));
        }
        try {
            minpix = Integer.parseInt(this.txtMinpixels.getText());
        } catch (final Exception e) {
            log.warning(String.format("Cannot read min pixels, using default value %d", def_minpix));
        }
        estimbkg = this.chckbxCalcBkgslow.isSelected();
        pondmerging = this.chckbxAvg.isSelected();
    }

    private void integratePeaks() {

        final boolean debug = false;
        final boolean autoTol = this.chckbxAutointrad.isSelected();
        final boolean autoazim = this.chckbxAutoazim.isSelected();
        final boolean autobkgpt = this.chckbxAutobkgpt.isSelected();
        final Pattern2D patt2d = this.getPatt2D();

        this.readIntegrateOptions();

        if (patt2d.getPkSearchResult() == null) {
            log.info("No peaks found");
            return;
        }
        final Iterator<Peak> itrpks = patt2d.getPkSearchResult().iterator();
        while (itrpks.hasNext()) {
            final Peak pk = itrpks.next();
            final Point2D.Float pxc = pk.getPixelCentre();

            if (autobkgpt) {
                final int npix = ImgOps.arcNPix(patt2d, (int) (pxc.x), (int) (pxc.y), tol2tpix, angDeg);
                //agafem un 5% dels pixels com a fons
                bkgpt = FastMath.round(npix * def_bkgPxAutoPercent);
                if (bkgpt < def_minbkgPx)
                    bkgpt = def_minbkgPx;
                log.fine("bkgPT=" + bkgpt);
            }
            if (autoTol) {
                tol2tpix = ImgOps.intRadPixelsOfAPeak(patt2d, pxc.x, pxc.y);
                //TODO: posem limits?
            }
            if (autoazim) {
                angDeg = ImgOps.azimAngleOfAPeak(patt2d, pxc.x, pxc.y);
                log.writeNameNums("FINE", true, "x,y,angDeg", pxc.x, pxc.y, angDeg);
                //TODO
            }

            final Patt2Dzone pz = ImgOps.yArcTilt(patt2d, (int) (pxc.x), (int) (pxc.y), tol2tpix, angDeg, true, bkgpt,
                    debug);
            pk.setZona(pz);
            pk.calculate(this.chckbxLpCorrection.isSelected());
        }
        this.updateTable(); //ja actualitza IP
    }

    protected void do_btnCalculate_actionPerformed(ActionEvent arg0) {
        final Pattern2D patt2d = this.getPatt2D();
        if (this.fromInco)
            log.debug("CALCULATE CALLED FROM INCO");
        if (!this.fromInco) { //busquem els pics amb els parametres entrats
            this.searchPeaks();
        } else {//importem els pics de la solucio seleccionada de l'inco
            final OrientSolucio[] oos = this.dincoFrame.getActiveOrientSols();
            if (oos == null)
                return;
            if (OrientSolucio.isPCS) {
                //llegir array peaks nomes hi haura una solucio, la zero
                patt2d.setPkSearchResult(oos[0].getPeaksPCS());
            } else { //ve de SOL o PXY, pot haver-hi més d'una
                final ArrayList<Peak> dincoSolPunts = new ArrayList<Peak>();
                for (final OrientSolucio oo : oos) {
                    //llegir array puntSolucio
                    final Iterator<PuntSolucio> itrS = oo.getSol().iterator();
                    while (itrS.hasNext()) {
                        final PuntSolucio s = itrS.next();
                        final Peak pic = ImgOps.addPeakFromCoordinates(patt2d,
                                new Point2D.Float(s.getCoordX(), s.getCoordY()), PeakSearch.zoneR);
                        dincoSolPunts.add(pic);
                    }
                }
                patt2d.setPkSearchResult(dincoSolPunts);
            }
        }
        //now we integrate the peaks using Yarctilt
        this.integratePeaks();
        this.updateTable();
    }

    public void integratePk(Peak pic) {
        final Pattern2D patt2d = this.getPatt2D();
        this.readIntegrateOptions();
        //afegim comprovacio "autos"
        final boolean autoTol = this.chckbxAutointrad.isSelected();
        final boolean autoazim = this.chckbxAutoazim.isSelected();
        final boolean autobkgpt = this.chckbxAutobkgpt.isSelected();
        if (autobkgpt) {
            final int npix = ImgOps.arcNPix(patt2d, (int) (pic.getPixelCentre().x), (int) (pic.getPixelCentre().y),
                    tol2tpix, angDeg);
            //agafem un 5% dels pixels com a fons
            bkgpt = FastMath.round(npix * def_bkgPxAutoPercent);
            if (bkgpt < def_minbkgPx)
                bkgpt = def_minbkgPx;
            log.debug("bkgPT=" + bkgpt);
        }
        if (autoTol) {
            tol2tpix = ImgOps.intRadPixelsOfAPeak(patt2d, pic.getPixelCentre().x, pic.getPixelCentre().y);
            //TODO: posem limits?
        }
        if (autoazim) {
            angDeg = ImgOps.azimAngleOfAPeak(patt2d, pic.getPixelCentre().x, pic.getPixelCentre().y);
            log.writeNameNums("CONFIG", true, "x,y,angDeg", pic.getPixelCentre().x, pic.getPixelCentre().y, angDeg);
            //TODO
        }
        pic.setZona(ImgOps.yArcTilt(patt2d, (int) (pic.getPixelCentre().x), (int) (pic.getPixelCentre().y), tol2tpix,
                angDeg, true, bkgpt, false));
        pic.calculate(this.chckbxLpCorrection.isSelected());
        if (patt2d.getPkSearchResult() == null)
            patt2d.setPkSearchResult(new ArrayList<Peak>()); //in case it is not initialized
        patt2d.getPkSearchResult().add(pic);
        this.updateTable();
    }

    //hauria de ser privat
    public void readIntegrateOptions() {
        try {
            tol2tpix = Integer.parseInt(this.txtTtol.getText());
        } catch (final Exception e) {
            log.warning(String.format("Cannot read tol2t, using default value %d", tol2tpix));
            this.txtTtol.setText(String.valueOf(def_tol2tpix));
        }
        try {
            angDeg = Float.parseFloat(this.txtAngdeg.getText());
        } catch (final Exception e) {
            log.warning(String.format("Cannot read angDeg, using default value %f", angDeg));
            this.txtAngdeg.setText(String.valueOf(def_angDeg));
        }
        try {
            bkgpt = Integer.parseInt(this.txtBkgPt.getText());
        } catch (final Exception e) {
            log.warning(String.format("Cannot read bkgpt, using default value %d", bkgpt));
            this.txtBkgPt.setText(String.valueOf(def_bkgpt));
        }
        iosc = 0; //iosc es l'eix de tilt, 1=horitzontal, 2=vertical, 0=noOscil
        final String val = this.spinerAxis.getValue().toString();
        log.fine(val.toString()); //V oscil. axis
        log.fine(val.getClass().getName()); //java.lang.String
        if (val.contains("H")) {
            iosc = 1;
        }
        if (val.contains("V")) {
            iosc = 2;
        }
        this.getPatt2D().setIscan(iosc);
    }

    public void updateTable() {
        final Pattern2D patt2d = this.getPatt2D();
        ((DefaultTableModel) this.table.getModel()).setRowCount(0);

        //mostrarem els pics
        if (patt2d.getPkSearchResult() == null) {
            this.lblNpks.setText("No Peaks found");
            return;
        }
        for (int i = 0; i < patt2d.getPkSearchResult().size(); i++) {
            final Peak pk = patt2d.getPkSearchResult().get(i);
            final Object[] row = new Object[this.table.getColumnCount()];
            //          "XPix", "YPix", "Radius", "Ymax", "Fh2", "s(Fh2)", "Ymean", "Npix", "Ybkg", "s(Ybkg)", "Nbkg", "RadWth", "AzimDeg", "dsp", "p", "Swarm", "Satur", "nearMsk"
            //             0      1        2         3      4       5         6        7       8       9         10        11       12        13    14    15       16        17
            //          Float   Float     Float     Int   Float   Float     Float     Int    Float   Float      Int       Int      Float    Float  Float  Int      Int      Bool
            row[0] = pk.getPixelCentre().x;
            row[1] = pk.getPixelCentre().y;
            row[2] = pk.getRadi();
            row[3] = pk.getYmax();
            row[4] = pk.getFh2();
            row[5] = pk.getSfh2();
            row[6] = pk.getYmean();
            row[7] = pk.getNpix();
            row[8] = pk.getYbkg();
            row[9] = pk.getYbkgSD();
            row[10] = pk.getNbkgpix();
            row[11] = pk.getIntRadPx();
            row[12] = pk.getAzimAper();
            row[13] = pk.getDsp();
            row[14] = pk.getP();
            row[15] = pk.getnVeinsEixam();
            row[16] = pk.getnSatur();
            String nmsk = "No";
            if (pk.isNearMask())
                nmsk = "Yes";
            row[17] = nmsk;

            ((DefaultTableModel) this.table.getModel()).addRow(row);
        }
        //ordenem per Ymax
        this.table.getRowSorter().toggleSortOrder(3);
        this.table.getRowSorter().toggleSortOrder(3);
        this.lblNpks.setText(String.format("%d Peaks!", patt2d.getPkSearchResult().size()));

        this.iPanel.actualitzarVista();
    }

    public Peak[] getSelectedPeaks() {
        final int[] val = this.table.getSelectedRows();
        if (val.length <= 0)
            return null;
        final Peak[] selpeaks = new Peak[val.length];

        for (int i = 0; i < val.length; i++) {
            final int modelRow = this.table.convertRowIndexToModel(val[i]);
            Peak pk = null;
            try {
                final float px = (Float) this.table.getModel().getValueAt(modelRow, 0);
                final float py = (Float) this.table.getModel().getValueAt(modelRow, 1);
                pk = this.getPatt2D().getPeakFromCoordinates(new Point2D.Float(px, py));
            } catch (final Exception e) {
                log.debug("error getting selected point coordinates");
            }
            selpeaks[i] = pk;
        }
        return selpeaks;
    }

    public float getCurrentTol2T() {
        float angDeg = -1;
        try {
            angDeg = Float.parseFloat(this.txtTtol.getText());
        } catch (final Exception e) {
            log.debug(String.format("error reading angdeg"));
        }
        return angDeg;
    }

    public float getCurrentAngDeg() {
        float tol2t = -1;
        try {
            tol2t = Float.parseFloat(this.txtAngdeg.getText());
        } catch (final Exception e) {
            log.debug(String.format("error reading tol2t"));
        }
        return tol2t;
    }

    public int getPlotSize() {
        return (Integer) this.spinner.getValue();
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.iPanel.actualitzarVista();
        this.dispose();
    }

    protected void do_table_keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            final Peak[] selpeaks = this.getSelectedPeaks();
            if (selpeaks != null) {
                for (final Peak selpeak : selpeaks) {
                    log.writeNameNums("CONFIG", true, "remove peak", selpeak.getPixelCentre().x,
                            selpeak.getPixelCentre().y);
                    this.getPatt2D().removePeak(selpeak);
                }
            }
            this.updateTable();
        }
    }

    protected void removePeak(Peak pk) {
        this.getPatt2D().removePeak(pk);
        this.updateTable();
    }

    protected void do_btnExportPeakList_actionPerformed(ActionEvent arg0) {
        final File f = FileUtils.fchooserSaveAsk(this.pksearchDialog, new File(D2Dplot_global.getWorkdir()), null,
                "PCS");
        if (f == null)
            return;
        D2Dplot_global.setWorkdir(f);
        ImgFileUtils.writePCS(this.getPatt2D(), f, delsig, angDeg, this.chckbxAutointrad.isSelected(), zoneR, minpix,
                bkgpt, this.chckbxAutobkgpt.isSelected(), this.chckbxAutoazim.isSelected(), false);

    }

    protected void do_btnExportTable_actionPerformed(ActionEvent arg0) {
        final File f = FileUtils.fchooserSaveAsk(this.pksearchDialog, new File(D2Dplot_global.getWorkdir()), null,
                null);
        if (f == null)
            return;
        D2Dplot_global.setWorkdir(f);
        this.exportTable(f);
    }

    private void importTable(File txtFile) {
        try {
            final Scanner scPCSfile = new Scanner(txtFile);
            boolean firstLine = false;
            String line;
            while (!firstLine) {
                line = scPCSfile.nextLine();
                if (line.trim().startsWith("-----"))
                    firstLine = true;
            }
            line = scPCSfile.nextLine(); //capçalera
            final ArrayList<Peak> realPeaks = new ArrayList<Peak>();
            while (scPCSfile.hasNextLine()) {
                line = scPCSfile.nextLine();
                final String[] lineS = line.trim().split("\\s+");
                // Npeak     Xpix     Ypix  Radi(px)      Ymax      Fh2   s(Fh2)      Ymean     Npix     Ybkg  s(Ybkg) nBkgPx RadWthPx RadWth2t AzimDeg      dsp  Nbour Nsatur NearMsk      p
                //      1  1134.83  1606.13   592.37   38557.12   104.61     4.84     227.39     1150    19.88     1.82     26       32     0.74    3.48   1.7195      1      0      No  0.932

                final Peak pic = new Peak(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]));
                pic.setRadi(Float.parseFloat(lineS[3]));
                pic.setYmax(Float.parseFloat(lineS[4]));
                pic.setFh2(Float.parseFloat(lineS[5]));
                pic.setSfh2(Float.parseFloat(lineS[6]));
                pic.setYmean(Float.parseFloat(lineS[7]));
                pic.setNpix(Integer.parseInt(lineS[8]));
                pic.setYbkg(Float.parseFloat(lineS[9]));
                pic.setYbkgSD(Float.parseFloat(lineS[10]));
                pic.setNbkgpix(Integer.parseInt(lineS[11]));
                pic.setIntRadPx(Integer.parseInt(lineS[12]));
                pic.setIntRad2th(Float.parseFloat(lineS[13]));
                pic.setAzimAper(Float.parseFloat(lineS[14]));
                pic.setDsp(Float.parseFloat(lineS[15]));
                pic.setnVeinsEixam(Integer.parseInt(lineS[16]));
                pic.setnSatur(Integer.parseInt(lineS[17]));
                pic.setNearMask(false);
                if (lineS[18].trim().equalsIgnoreCase("Yes"))
                    pic.setNearMask(true);
                pic.setP(Float.parseFloat(lineS[19]));

                final Patt2Dzone pz = new Patt2Dzone(pic.getNpix(), -1, (int) pic.getYmax(), pic.getYmean(), -1,
                        pic.getYbkg(), pic.getYbkgSD());
                pz.setIntradPix(pic.getIntRadPx());
                pz.setAzimAngle(pic.getAzimAper());
                pz.setBkgpt(pic.getNbkgpix());
                pz.setCentralPoint(pic.getPixelCentre());
                pz.setPatt2d(this.getPatt2D());
                pic.setZona(pz);

                pic.setIntegrated(true);

                realPeaks.add(pic);
            }
            this.getPatt2D().setPkSearchResult(realPeaks);
            scPCSfile.close();
            this.updateTable();
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error reading SOL file");
        }
    }

    private File exportTable(File txtFile) {
        final String eqLine = "===========================================================================================================================================================================";
        final String minLine = "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
        final Pattern2D patt2d = this.getPatt2D();
        // creem un printwriter amb el fitxer file (el que estem escribint)
        try {
            //preparem les files (ho he mogut aqui perque em fan falta alguns calculs per la capçalera
            final Iterator<Peak> itrR = patt2d.getPkSearchResult().iterator();
            int maxIntRad = 0;
            float maxAzim = 0.f;
            float minAzim = 99999999.f;
            while (itrR.hasNext()) {
                final Peak r = itrR.next();
                if (r.getIntRadPx() > maxIntRad)
                    maxIntRad = r.getIntRadPx();
                if (r.getAzimAper() > maxAzim)
                    maxAzim = r.getAzimAper();
                if (r.getAzimAper() < minAzim)
                    minAzim = r.getAzimAper();
            }

            final PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
            // ESCRIBIM AL FITXER:
            output.println(eqLine);
            output.println("D2Dplot peak integration for INCO");
            output.println(eqLine);
            output.println("Image File= " + patt2d.getImgfileString());
            output.println(String.format("dimX= %d, dimY= %d, centX= %.2f, centY= %.2f", patt2d.getDimX(),
                    patt2d.getDimY(), patt2d.getCentrX(), patt2d.getCentrY()));
            output.println(String.format("pixSize(micron)= %.3f, dist(mm)= %.3f, wave(A)= %.5f",
                    patt2d.getPixSx() * 1000, patt2d.getDistMD(), patt2d.getWavel()));
            switch (patt2d.getIscan()) {
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
            output.println(
                    String.format("Saturated pixels= %d (sat. value= %d)", patt2d.getnSatur(), patt2d.getSaturValue()));
            output.println(String.format("Saturated peaks= %d", patt2d.getnPkSatur()));
            output.println(String.format("MeanI= %.1f Sigma(I)= %.3f", patt2d.getMeanI(), patt2d.getSdevI()));
            output.println(String.format("ESD factor= %.2f", angDeg));
            if (this.chckbxAutoazim.isSelected()) {
                output.println(String.format("Auto azim aperture of the integration (º) in the range %.2f to %.2f",
                        minAzim, maxAzim));
            } else {
                output.println(String.format("Azim aperture of the integration (º)= %.2f", angDeg));
            }
            output.println(String.format("Max radial integration width (pixels)= %d", maxIntRad));
            output.println(String.format("Peak merge zone radius (pixels)= %d", zoneR));
            output.println(String.format("Min pixels for a peak= %d", minpix));
            if (this.chckbxAutobkgpt.isSelected()) {
                output.println(String.format("Background pixels determined automatically"));
            } else {
                output.println(String.format("Background pixels= %d", bkgpt));
            }
            output.println(minLine);

            output.println(Peak.all_header);

            //ara ordenem la llista i l'escribim
            Collections.sort(patt2d.getPkSearchResult());
            final Iterator<Peak> itrpcs = patt2d.getPkSearchResult().iterator();
            int npk = 1;
            while (itrpcs.hasNext()) {
                final Peak pcsr = itrpcs.next();
                output.println(String.format("%6d %s", npk, pcsr.getFormmattedStringAll()));
                npk = npk + 1;
            }
            output.close();

        } catch (final IOException e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error writting PCS file");
            return null;
        }
        log.info("Full info table exported in text format!");
        return txtFile;
    }

    protected void do_btnMaskbin_actionPerformed(ActionEvent e) {
        if (this.getPatt2D() != null) {
            // tanquem calibracio en cas que estigui obert
            final ExZones exZones = new ExZones(this.parent, this.iPanel);
            exZones.setVisible(true);
            this.iPanel.setExZones(exZones);
        }
    }

    protected void do_btnBatch_actionPerformed(ActionEvent arg0) {
        //obrir un fchoser multiple i seleccionar imatges
        FileUtils.InfoDialog(this.pksearchDialog,
                "Selected images will be integrated with current options \nand PCS files with the same filename generated.",
                "batch pk search and integrate");

        //integrar amb les opcions actuals
        this.readSearchOptions();
        this.readIntegrateOptions();//aqui s'assigna iosc

        //carregar imatge, integrar, escriure PCS amb el mateix nom
        //i tal s'encarrega el swingworker de imgops
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File[] flist = FileUtils.fchooserMultiple(this.pksearchDialog, new File(D2Dplot_global.getWorkdir()),
                filt, 0, "Select 2DXRD data files to process");
        if (flist == null)
            return;
        D2Dplot_global.setWorkdir(flist[0]);

        this.pm = new ProgressMonitor(null, "Peak Search and Integrate on several images in progress...", "", 0, 100);
        this.pm.setProgress(0);
        this.convwk = new ImgOps.PkIntegrateFileWorker(flist, delsig, zoneR, minpix, bkgpt,
                this.chckbxAutobkgpt.isSelected(), tol2tpix, this.chckbxAutointrad.isSelected(), angDeg,
                this.chckbxAutoazim.isSelected(), this.chckbxLpCorrection.isSelected(), iosc, estimbkg, pondmerging,
                this.iPanel.getMainFrame());

        this.convwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    PeakSearch.this.pm.setProgress(progress);
                    PeakSearch.this.pm.setNote(String.format("%d%%\n", progress));
                    if (PeakSearch.this.pm.isCanceled() || PeakSearch.this.convwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (PeakSearch.this.pm.isCanceled()) {
                            PeakSearch.this.convwk.cancel(true);
                            log.info("Peak Search and Integrate stopped by user");
                        } else {
                            log.info("Peak Search and Integrate finished!!");
                        }
                        PeakSearch.this.pm.close();
                    }
                }
            }
        });
        this.convwk.execute();
    }

    protected void do_btnImport_actionPerformed(ActionEvent arg0) {
        final File f = FileUtils.fchooserOpen(this.pksearchDialog, new File(D2Dplot_global.getWorkdir()), null, 0);
        if (f == null)
            return;
        if (FileUtils.getExtension(f).equalsIgnoreCase("OUT")) {
            this.fileout = f;
            this.importOUT(f);
        } else {
            this.importTable(f);
        }
    }

    public File getFileOut() {
        return this.fileout;
    }

    public void setVisible(boolean vis) {
        this.pksearchDialog.setVisible(vis);
        if (vis == true)
            this.chckbxShowPoints.setSelected(true);
    }

    public void dispose() {
        this.chckbxShowPoints.setSelected(false);
        this.stopProcesses();
        this.pksearchDialog.dispose();
    }

    private void stopProcesses() {
        if (this.convwk != null)
            this.convwk.cancel(true);
        if (this.pkscwk != null)
            this.pkscwk.cancel(true);
    }

    public void importOUT(File outFile) {
        boolean singleImg = false;
        final int inum = this.getPatt2D().getFileNameNumber();
        log.debug("image Number=" + inum);
        if (inum >= 0)
            singleImg = true;

        try {
            final Scanner scOUTfile = new Scanner(outFile);
            boolean firstLine = false;
            String line;
            while (!firstLine) {
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK"))
                    firstLine = true;
            }
            line = scOUTfile.nextLine(); //linia numero d'imatge
            String[] lineS = line.trim().split("\\s+");
            //            int cnum = Integer.parseInt(lineS[0])-1; //-1 perque fortran comença a escriure a 1 i no a zero
            int cnum = Integer.parseInt(lineS[0]); // numero de la imatge segons el fitxer
            log.debug("current out reading Number=" + cnum);

            final ArrayList<Peak> realPeaks = new ArrayList<Peak>();
            while (scOUTfile.hasNextLine()) {
                //System.out.println(line);
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK")) {
                    line = scOUTfile.nextLine(); //linia numero d'imatge
                    lineS = line.trim().split("\\s+");
                    //                    cnum = Integer.parseInt(lineS[0])-1;
                    cnum = Integer.parseInt(lineS[0]);
                    log.debug("current out reading Number=" + cnum);
                    continue;//seguim
                }
                if (line.trim().startsWith("----"))
                    continue;
                if (line.trim().startsWith("NOMBRE"))
                    continue;
                if (line.trim().isEmpty())
                    continue;

                if (singleImg) {
                    if (cnum != inum) {
                        log.debug("cnum!=inum");
                        continue;
                    }
                }

                //en principi aqui tindrem pics a afegir

                log.debug("Reading Matching Image Peaks=" + inum + " (out file=" + cnum + ")");

                lineS = line.trim().split("\\s+");
                final Peak pic = new Peak(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]));
                try {
                    pic.setRadi(Float.parseFloat(lineS[3]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setRadi(0);
                }
                try {
                    pic.setYmax(Float.parseFloat(lineS[4]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setYmax(0);
                }
                try {
                    pic.setFh2(Float.parseFloat(lineS[5]) * Float.parseFloat(lineS[5]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setFh2(0);
                }
                try {
                    pic.setSfh2(Float.parseFloat(lineS[6]) * Float.parseFloat(lineS[6]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setSfh2(0);
                }
                try {
                    pic.setP(Float.parseFloat(lineS[7]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setP(0);
                }
                try {
                    pic.setIntRadPx(Integer.parseInt(lineS[9]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setIntRadPx(0);
                }
                try {
                    pic.setDsp(Float.parseFloat(lineS[10]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setDsp(0);
                }

                pic.setYmean(0);
                pic.setNpix(0);
                pic.setYbkg(0);
                pic.setYbkgSD(0);
                pic.setNbkgpix(0);
                pic.setIntRad2th(0);
                pic.setAzimAper(0);
                pic.setnVeinsEixam(0);
                pic.setnSatur(0);
                pic.setNearMask(false);

                final Patt2Dzone pz = new Patt2Dzone(pic.getNpix(), -1, (int) pic.getYmax(), pic.getYmean(), -1,
                        pic.getYbkg(), pic.getYbkgSD());
                pz.setIntradPix(pic.getIntRadPx());
                pz.setAzimAngle(pic.getAzimAper());
                pz.setBkgpt(pic.getNbkgpix());
                pz.setCentralPoint(pic.getPixelCentre());
                pz.setPatt2d(this.getPatt2D());
                pic.setZona(pz);

                pic.setIntegrated(true);

                realPeaks.add(pic);

                if (singleImg) {
                    if (cnum > inum) {
                        log.debug("cnum>inum");
                        break; //ja hem llegit la imatge bona
                    }
                }
            }
            this.getPatt2D().setPkSearchResult(realPeaks);
            scOUTfile.close();
            this.updateTable();
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error reading OUT file");
        }
    }

    protected void do_chckbxShowPoints_itemStateChanged(ItemEvent e) {
        this.iPanel.actualitzarVista();
    }

    protected void do_spinner_stateChanged(ChangeEvent e) {
        this.iPanel.actualitzarVista();
    }

    protected void do_table_valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            this.iPanel.actualitzarVista();
        }
    }

    //TODO
    public void writeOUT(File outFile) {
        boolean singleImg = false;
        final int inum = this.getPatt2D().getFileNameNumber();
        log.debug("image Number=" + inum);
        if (inum >= 0)
            singleImg = true;

        try {
            final Scanner scOUTfile = new Scanner(outFile);
            boolean firstLine = false;
            String line;
            while (!firstLine) {
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK"))
                    firstLine = true;
            }
            line = scOUTfile.nextLine(); //linia numero d'imatge
            String[] lineS = line.trim().split("\\s+");
            //            int cnum = Integer.parseInt(lineS[0])-1; //-1 perque fortran comença a escriure a 1 i no a zero
            int cnum = Integer.parseInt(lineS[0]); // numero de la imatge segons el fitxer
            log.debug("current out reading Number=" + cnum);

            final ArrayList<Peak> realPeaks = new ArrayList<Peak>();
            while (scOUTfile.hasNextLine()) {
                System.out.println(line);
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK")) {
                    line = scOUTfile.nextLine(); //linia numero d'imatge
                    lineS = line.trim().split("\\s+");
                    //                    cnum = Integer.parseInt(lineS[0])-1;
                    cnum = Integer.parseInt(lineS[0]);
                    log.debug("current out reading Number=" + cnum);
                    continue;//seguim
                }
                if (line.trim().startsWith("----"))
                    continue;
                if (line.trim().startsWith("NOMBRE"))
                    continue;
                if (line.trim().isEmpty())
                    continue;

                if (singleImg) {
                    if (cnum != inum) {
                        log.debug("cnum!=inum");
                        continue;
                    }
                }

                //en principi aqui tindrem pics a afegir

                lineS = line.trim().split("\\s+");
                final Peak pic = new Peak(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]));
                try {
                    pic.setRadi(Float.parseFloat(lineS[3]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setRadi(0);
                }
                try {
                    pic.setYmax(Float.parseFloat(lineS[4]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setYmax(0);
                }
                try {
                    pic.setFh2(Float.parseFloat(lineS[5]) * Float.parseFloat(lineS[5]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setFh2(0);
                }
                try {
                    pic.setSfh2(Float.parseFloat(lineS[6]) * Float.parseFloat(lineS[6]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setSfh2(0);
                }
                try {
                    pic.setP(Float.parseFloat(lineS[7]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setP(0);
                }
                try {
                    pic.setIntRadPx(Integer.parseInt(lineS[9]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setIntRadPx(0);
                }
                try {
                    pic.setDsp(Float.parseFloat(lineS[10]));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    pic.setDsp(0);
                }

                pic.setYmean(0);
                pic.setNpix(0);
                pic.setYbkg(0);
                pic.setYbkgSD(0);
                pic.setNbkgpix(0);
                pic.setIntRad2th(0);
                pic.setAzimAper(0);
                pic.setnVeinsEixam(0);
                pic.setnSatur(0);
                pic.setNearMask(false);

                final Patt2Dzone pz = new Patt2Dzone(pic.getNpix(), -1, (int) pic.getYmax(), pic.getYmean(), -1,
                        pic.getYbkg(), pic.getYbkgSD());
                pz.setIntradPix(pic.getIntRadPx());
                pz.setAzimAngle(pic.getAzimAper());
                pz.setBkgpt(pic.getNbkgpix());
                pz.setCentralPoint(pic.getPixelCentre());
                pz.setPatt2d(this.getPatt2D());
                pic.setZona(pz);

                pic.setIntegrated(true);

                realPeaks.add(pic);

                if (singleImg) {
                    if (cnum > inum) {
                        log.debug("cnum>inum");
                        break; //ja hem llegit la imatge bona
                    }
                }
            }
            this.getPatt2D().setPkSearchResult(realPeaks);
            scOUTfile.close();
            this.updateTable();
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error reading OUT file");
        }
    }

    protected void do_btnBatchout_actionPerformed(ActionEvent arg0) {
        //obrir un fchoser multiple i seleccionar imatges
        FileUtils.InfoDialog(this.pksearchDialog,
                "Selected images will be integrated with current options \nand a single OUT file will be generated.",
                "batch pk search and integrate");

        //integrar amb les opcions actuals
        this.readSearchOptions();
        this.readIntegrateOptions();//aqui s'assigna iosc

        //carregar imatge, integrar, escriure PCS amb el mateix nom
        //i tal s'encarrega el swingworker de imgops
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File[] flist = FileUtils.fchooserMultiple(this.pksearchDialog, new File(D2Dplot_global.getWorkdir()),
                filt, 0, "Select 2DXRD data files to process");
        if (flist == null)
            return;
        D2Dplot_global.setWorkdir(flist[0]);

        this.pm = new ProgressMonitor(null, "Peak Search and Integrate on several images in progress...", "", 0, 100);
        this.pm.setProgress(0);
        this.pkscwk = new ImgOps.PkSCIntegrateFileWorker(flist, delsig, zoneR, minpix, bkgpt,
                this.chckbxAutobkgpt.isSelected(), tol2tpix, this.chckbxAutointrad.isSelected(), angDeg,
                this.chckbxAutoazim.isSelected(), this.chckbxLpCorrection.isSelected(), iosc, estimbkg, pondmerging);

        this.pkscwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    PeakSearch.this.pm.setProgress(progress);
                    PeakSearch.this.pm.setNote(String.format("%d%%\n", progress));
                    if (PeakSearch.this.pm.isCanceled() || PeakSearch.this.pkscwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (PeakSearch.this.pm.isCanceled()) {
                            PeakSearch.this.pkscwk.cancel(true);
                            log.info("Peak Search and Integrate stopped by user");
                        } else {
                            log.info("Peak Search and Integrate finished!!");
                        }
                        PeakSearch.this.pm.close();
                    }
                }
            }
        });
        this.pkscwk.execute();
    }
}
