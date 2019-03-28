package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.ArcExZone;
import com.vava33.d2dplot.auxi.ExZ_ArcDialog;
import com.vava33.d2dplot.auxi.ExZ_BSdiag;
import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.d2dplot.auxi.PolyExZone;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class ExZones {

    private JDialog exzDialog;
    private JButton btnAddPoly;
    private JButton btnDelPoly;
    private JButton btnWriteExzFile;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxExZones;
    private JPanel contentPanel;
    private JLabel lblHelp;
    private JLabel lblMargin;
    private JList<String> listPolZones;
    private JPanel panel_left;
    private JScrollPane scrollPane;
    private JTextField txtMargin;

    private DefaultListModel<String> lmPoly;
    private DefaultListModel<String> lmArc;
    private JTextField txtThreshold;

    private boolean drawingPolExZone; //true while drawing
    private boolean drawingArcExZone; //true while drawing
    private boolean drawingBSExZone; //true while drawing
    private boolean drawingFreeExZone; //true while drawing
    private PolyExZone currentPolyExZ;
    private ArcExZone currentArcExZ;

    private JLabel lblPolygonalZones;
    private JLabel lblThreshold;
    private JPanel panel;
    private JButton btnApply;
    private JButton btnReadExzFile;

    private ImagePanel ip;
    private JFrame parent;
    private JButton btnWriteMaskbin;
    private JButton btnAddBs;
    private static final String className = "EXZ";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private JPanel panel_1;
    private JLabel lblArcZones;
    private JList<String> listArcZones;
    private JScrollPane scrollPane_2;
    private JButton btnAddArc;
    private JButton btnDelArc;
    private JLabel lblDetectorCircularRadius;
    private JTextField txtCircular;
    private JButton btnAddByValues;

    ExZ_ArcDialog arcd;
    ExZ_BSdiag bsd;
    private JPanel panel_2;
    private JToggleButton tglbtnFreedraw;
    private JPanel panel_3;
    private JLabel lblSize;
    private JTextField txtSizeMouse;

    Pattern2D backupImage;
    private JButton btnUndo;
    private JButton clearAll;
    private JButton btnReadMaskbin;

    /**
     * Create the dialog.
     */
    public ExZones(JFrame parent, ImagePanel ipanel) {
        this.setIpanel(ipanel);
        this.parent = parent;
        this.contentPanel = new JPanel();
        this.exzDialog = new JDialog(parent, "Excluded Zones", false);
        //        final Pattern2D patt2D = ipanel.getPatt2D();
        this.exzDialog.setAlwaysOnTop(true);
        this.exzDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(ExZones.class.getResource("/img/Icona.png")));
        this.exzDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // setBounds(100, 100, 660, 730);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = 580;
        final int height = 640;
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.exzDialog.setBounds(x, y, width, height);
        this.exzDialog.getContentPane().setLayout(new BorderLayout());
        this.exzDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
        {
            {
                this.panel_left = new JPanel();
                this.contentPanel.add(this.panel_left, "cell 0 0,grow");
                {
                    this.chckbxExZones = new JCheckBox("Show/Edit Excluded Zones");
                    this.panel_left.setLayout(new MigLayout("", "[][grow][grow]", "[][][][][][grow][grow][][]"));
                    this.chckbxExZones.setSelected(true);
                    this.panel_left.add(this.chckbxExZones, "cell 0 0 2 1,alignx left,aligny center");
                }
                {
                    this.cbox_onTop = new JCheckBox("on top");
                    this.cbox_onTop.setSelected(true);
                    this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                    this.cbox_onTop.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            ExZones.this.do_cbox_onTop_itemStateChanged(arg0);
                        }
                    });
                    this.cbox_onTop.setActionCommand("on top");
                    this.panel_left.add(this.cbox_onTop, "cell 2 0,alignx right,aligny center");
                }
                {
                    {
                        {
                            this.lblMargin = new JLabel("Margin=");
                            this.panel_left.add(this.lblMargin, "cell 0 2,alignx right,aligny center");
                        }
                    }
                    {
                        {
                            this.txtMargin = new JTextField();
                            this.txtMargin.setText("0");
                            this.panel_left.add(this.txtMargin, "cell 1 2,growx,aligny center");
                            this.txtMargin.setColumns(4);
                        }

                        // Listen for changes in the textbox margin
                        this.txtMargin.getDocument().addDocumentListener(new DocumentListener() {
                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                this.updateMargin();
                            }

                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                this.updateMargin();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                this.updateMargin();
                            }

                            public void updateMargin() {
                                if (ExZones.this.txtMargin.getText().isEmpty()) {
                                    ExZones.this.getPatt2D().setExz_margin(0);
                                    return;
                                }
                                try {
                                    ExZones.this.getPatt2D()
                                            .setExz_margin(Integer.parseInt(ExZones.this.txtMargin.getText()));
                                } catch (final Exception e) {
                                    e.printStackTrace();
                                    log.warning("Invalid margin entered");
                                    ExZones.this.getPatt2D().setExz_margin(0);
                                }
                            }
                        });
                    }
                    this.lblHelp = new JLabel("?");
                    this.lblHelp.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            ExZones.this.do_lbllist_mouseEntered(e);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            ExZones.this.do_lbllist_mouseExited(e);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            ExZones.this.do_lbllist_mouseReleased(e);
                        }
                    });
                    this.lblHelp.setFont(new Font("Tahoma", Font.BOLD, 14));
                    this.panel_left.add(this.lblHelp, "cell 2 2,alignx right,aligny center");
                    {
                        this.lblThreshold = new JLabel("Threshold=");
                        this.panel_left.add(this.lblThreshold, "cell 0 3,alignx right");
                    }
                    {
                        this.txtThreshold = new JTextField();
                        this.txtThreshold.setText("0");
                        this.panel_left.add(this.txtThreshold, "cell 1 3,growx,aligny center");
                        this.txtThreshold.setColumns(10);
                    }
                    {
                        this.lblDetectorCircularRadius = new JLabel("Detector Circular Radius=");
                        this.panel_left.add(this.lblDetectorCircularRadius, "cell 0 4,alignx trailing");
                    }
                    {
                        this.txtCircular = new JTextField();
                        this.txtCircular.setToolTipText("0=no mask");
                        this.txtCircular.setText("0");
                        this.panel_left.add(this.txtCircular, "cell 1 4,growx");
                        this.txtCircular.setColumns(10);
                    }
                    {
                        this.panel = new JPanel();
                        this.panel_left.add(this.panel, "cell 0 5 3 1,grow");
                        this.panel.setLayout(new MigLayout("", "[][grow][][][]", "[][75px:n,grow]"));
                        {
                            this.lblPolygonalZones = new JLabel("Polygonal zones");
                            this.panel.add(this.lblPolygonalZones, "cell 0 0 2 1,alignx left,aligny center");
                        }
                        this.btnAddPoly = new JButton("Add by clicks");
                        this.panel.add(this.btnAddPoly, "cell 2 0,alignx center,aligny center");
                        this.btnAddPoly.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                ExZones.this.do_btnAddPoly_actionPerformed(arg0);
                            }
                        });
                        {
                            this.btnAddBs = new JButton("Add BeamStop");
                            this.btnAddBs.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    ExZones.this.do_btnAddBs_actionPerformed(arg0);
                                }
                            });
                            this.panel.add(this.btnAddBs, "cell 3 0");
                        }
                        this.btnDelPoly = new JButton("Delete");
                        this.panel.add(this.btnDelPoly, "cell 4 0,alignx center,aligny center");
                        this.btnDelPoly.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ExZones.this.do_btnDelPoly_actionPerformed(e);
                            }
                        });
                        {
                            this.scrollPane = new JScrollPane();
                            this.panel.add(this.scrollPane, "cell 0 1 5 1,grow");
                            {
                                this.listPolZones = new JList<String>();
                                this.listPolZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                this.listPolZones.addListSelectionListener(new ListSelectionListener() {
                                    @Override
                                    public void valueChanged(ListSelectionEvent arg0) {
                                        ExZones.this.do_listZones_valueChanged(arg0);
                                    }
                                });
                                this.scrollPane.setViewportView(this.listPolZones);
                            }
                        }
                    }
                    {
                        this.panel_1 = new JPanel();
                        this.panel_left.add(this.panel_1, "cell 0 6 3 1,grow");
                        this.panel_1.setLayout(new MigLayout("", "[grow][][][]", "[18px][75:n,grow]"));
                        {
                            this.lblArcZones = new JLabel("Arc zones");
                            this.panel_1.add(this.lblArcZones, "cell 0 0,alignx left,aligny center");
                        }
                        {
                            this.btnAddArc = new JButton("Add by clicks");
                            this.btnAddArc.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    ExZones.this.do_btnAddArc_actionPerformed(arg0);
                                }
                            });
                            this.panel_1.add(this.btnAddArc, "cell 1 0,alignx center");
                        }
                        {
                            this.btnAddByValues = new JButton("Add from values");
                            this.btnAddByValues.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    ExZones.this.do_btnAddByValues_actionPerformed(arg0);
                                }
                            });
                            this.panel_1.add(this.btnAddByValues, "cell 2 0");
                        }
                        {
                            this.btnDelArc = new JButton("Delete");
                            this.btnDelArc.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    ExZones.this.do_btnDelArc_actionPerformed(e);
                                }
                            });
                            this.panel_1.add(this.btnDelArc, "cell 3 0");
                        }
                        {
                            this.scrollPane_2 = new JScrollPane();
                            this.panel_1.add(this.scrollPane_2, "cell 0 1 4 1,grow");
                            {
                                this.listArcZones = new JList<String>();
                                this.listArcZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                this.listArcZones.addListSelectionListener(new ListSelectionListener() {
                                    @Override
                                    public void valueChanged(ListSelectionEvent arg0) {
                                        ExZones.this.do_listArcZones_valueChanged(arg0);
                                    }
                                });
                                this.scrollPane_2.setViewportView(this.listArcZones);
                            }
                        }
                    }
                    {
                        this.panel_3 = new JPanel();
                        this.panel_left.add(this.panel_3, "cell 0 7 3 1,grow");
                        this.panel_3.setLayout(new MigLayout("", "[][][][]", "[]"));
                        {
                            this.tglbtnFreedraw = new JToggleButton("Mouse Free Paint");
                            this.panel_3.add(this.tglbtnFreedraw, "cell 0 0");
                            {
                                this.lblSize = new JLabel("size=");
                                this.panel_3.add(this.lblSize, "cell 1 0,alignx trailing");
                            }
                            {
                                this.txtSizeMouse = new JTextField();
                                this.txtSizeMouse.setText("10");
                                this.panel_3.add(this.txtSizeMouse, "cell 2 0,growx");
                                this.txtSizeMouse.setColumns(10);
                            }
                            {
                                this.btnUndo = new JButton("Undo last");
                                this.btnUndo.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        ExZones.this.do_btnUndo_actionPerformed(e);
                                    }
                                });
                                this.panel_3.add(this.btnUndo, "cell 3 0");
                            }
                            {
                                this.clearAll = new JButton("Clear all");
                                this.clearAll.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        ExZones.this.do_btnclearAll_actionPerformed(e);
                                    }
                                });
                                this.panel_3.add(this.clearAll, "cell 4 0");
                            }
                            {
                                this.panel_2 = new JPanel();
                                this.panel_2.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
                                this.panel_left.add(this.panel_2, "cell 0 8 3 1,growx");
                                this.panel_2.setLayout(new MigLayout("insets 1", "[grow][][][grow]", "[]"));
                                {
                                    this.btnReadExzFile = new JButton("Read EXZ file");
                                    this.panel_2.add(this.btnReadExzFile, "cell 0 0,alignx right");
                                    {
                                        this.btnWriteExzFile = new JButton("Write EXZ file");
                                        this.panel_2.add(this.btnWriteExzFile, "cell 1 0,alignx center");
                                        {
                                            this.btnReadMaskbin = new JButton("Read MASK.BIN");
                                            this.btnReadMaskbin.addActionListener(new ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    ExZones.this.do_btnReadMaskbin_actionPerformed(e);
                                                }
                                            });
                                            this.panel_2.add(this.btnReadMaskbin, "cell 2 0");
                                        }
                                        {
                                            this.btnWriteMaskbin = new JButton("Write MASK.BIN");
                                            this.btnWriteMaskbin.setToolTipText("it also writes equivalent EXZ file");
                                            this.panel_2.add(this.btnWriteMaskbin, "cell 3 0");
                                            this.btnWriteMaskbin.addActionListener(new ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    ExZones.this.do_btnWriteMaskbin_actionPerformed(e);
                                                }
                                            });
                                        }
                                        this.btnWriteExzFile.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent arg0) {
                                                ExZones.this.do_btnWriteExzFile_actionPerformed(arg0);
                                            }
                                        });
                                    }
                                    this.btnReadExzFile.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            ExZones.this.do_btnReadExzFile_actionPerformed(e);
                                        }
                                    });
                                }
                            }
                            this.tglbtnFreedraw.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    ExZones.this.do_tglbtnFreedraw_actionPerformed(arg0);
                                }
                            });
                        }
                    }
                    {
                    }
                }
            }
        }
        {
            final JPanel buttonPane = new JPanel();
            this.exzDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            final JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    ExZones.this.do_okButton_actionPerformed(arg0);
                }
            });
            buttonPane.setLayout(new MigLayout("", "[grow][54px][54px]", "[28px]"));
            {
                this.btnApply = new JButton("Apply");
                this.btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        ExZones.this.do_btnApply_actionPerformed(arg0);
                    }
                });
                buttonPane.add(this.btnApply, "cell 1 0,alignx right,aligny top");
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 2 0,alignx right,aligny center");
            this.exzDialog.getRootPane().setDefaultButton(okButton);
        }

        log.info("** Excluded zones definition **");
        //        log.info("     (click on ? for help)");
        this.exzDialog.pack();
        this.inicia();
    }

    public void setIpanel(ImagePanel ipanel) {
        this.ip = ipanel;
    }

    public ImagePanel getIPanel() {
        return this.ip;
    }

    public Pattern2D getPatt2D() {
        return this.getIPanel().getPatt2D();
    }

    public void inicia() {
        //        patt2D = this.getIPanel().getPatt2D();
        this.lmPoly = new DefaultListModel<String>();
        this.listPolZones.setModel(this.lmPoly);
        this.lmArc = new DefaultListModel<String>();
        this.listArcZones.setModel(this.lmArc);
        this.getIPanel().getMainFrame().setViewExZ(true);
        if (ImgFileUtils.readEXZ(this.getPatt2D(), null, true)) {
            log.info("Excluded zones file (.EXZ) found & readed!");
        } else {
            log.info("Excluded zones file (.EXZ) not found/loaded. Set them if any.");
        }
        this.updateListPoly();
        this.updateListArc();
        this.updateFieldwithHeaderInfo();
    }

    protected void do_btnAddPoly_actionPerformed(ActionEvent arg0) {
        if (this.isDrawingArcExZone()) {
            log.info("finish drawing current Arc zone first");
            return;
        }
        //Dibuixem poligon
        if (this.btnAddPoly.getText() == "Add by clicks") {
            final PolyExZone p = new PolyExZone(false);
            this.btnAddPoly.setText("FINISH");
            this.currentPolyExZ = p;
            this.setDrawingPolExZone(true);
        } else {//s'ha acabat el dawing
            this.setDrawingPolExZone(false);
            if (this.currentPolyExZ.npoints > 0) {
                this.btnAddPoly.setText("Add by clicks");
                this.getPatt2D().getPolyExZones().add(this.currentPolyExZ);
                this.lmPoly.addElement(this.currentPolyExZ.printLnVertexs());
                this.updateListPoly();
                this.listPolZones.setSelectedIndex(this.lmPoly.size() - 1);
            }
        }
    }

    protected void do_btnDelPoly_actionPerformed(ActionEvent e) {
        this.getPatt2D().getPolyExZones().remove(this.listPolZones.getSelectedIndex());
        this.updateListPoly();
        if (!this.lmPoly.isEmpty()) {
            this.listPolZones.setSelectedIndex(this.lmPoly.size() - 1);
        }
    }

    protected void do_btnWriteExzFile_actionPerformed(ActionEvent arg0) {
        this.btnApply.doClick();
        final File exfile = FileUtils.fchooserSaveNoAsk(this.exzDialog, new File(D2Dplot_global.getWorkdir()), null,
                "exz");
        if (exfile != null) {
            ImgFileUtils.writeEXZ(exfile, this.getPatt2D(), false);//forcem ext al fchooser
            D2Dplot_global.setWorkdir(exfile);
        }
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.exzDialog.setAlwaysOnTop(this.cbox_onTop.isSelected());
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        this.lblHelp.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        this.lblHelp.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
        final String msg = "\n" + "** EXCLUDED ZONES HELP **\n"
                + " - To add a polygonal excluded zone click ADD and click several points to define the zone\n"
                + " - To add an arc shaped excluded zone click -Add by Clicks- and click 3 points to define\n"
                + "   the zone (center, half radial width, half azim aperture) **OR**\n"
                + "   you can introduce the values manually with -Add from values-\n"
                + " - To add a BeamStop shaped excluded zone click -Add Beamstop- and introduce requested parameters\n"
                + " - You can define a threshold such as if Y<Threshold the pixel will be excluded\n"
                + " - You can define a margin in case of image borders\n"
                + " - You can define a detector radius in case the detection area is circular\n"
                + " 2 options after defining excluded zones:\n"
                + " - Save the image in a format (D2D, BIN) that contain the information\n"
                + " - Save an Excluded Zones (ExZ) file to be loaded later, it is a text file\n"
                + " - Save a MASK.BIN file to be able to batch process images later (it also saves automatically an EXZ file just in case)\n"
                + "\n";
        FileUtils.InfoDialog(this.exzDialog, msg, "Excluded Zones Help");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        if (FileUtils.YesNoDialog(this.exzDialog, "Apply Changes?"))
            this.btnApply.doClick();
        this.dispose();
    }

    public PolyExZone getCurrentZone() {
        if (this.listPolZones.getSelectedIndex() >= 0) {
            return this.getPatt2D().getPolyExZones().get(this.listPolZones.getSelectedIndex());
        } else {
            return null;
        }
    }

    public boolean isSetExZones() {
        return this.chckbxExZones.isSelected();
    }

    public void updateListPoly() {
        this.lmPoly.clear();
        final Iterator<PolyExZone> it = this.getPatt2D().getPolyExZones().iterator();
        while (it.hasNext()) {
            final PolyExZone p = it.next();
            this.lmPoly.addElement(p.printLnVertexs().trim());
        }
        //        this.getPatt2D().recalcExcludedPixelsPolyExZone();
        this.getPatt2D().recalcExcludedPixels();
        this.getIPanel().pintaImatge();
    }

    public void updateListArc() {
        this.lmArc.clear();
        final Iterator<ArcExZone> it = this.getPatt2D().getArcExZones().iterator();
        while (it.hasNext()) {
            final ArcExZone p = it.next();
            this.lmArc.addElement(p.toString().trim());
        }
        //        this.getPatt2D().recalcExcludedPixelsArcExZone();
        this.getPatt2D().recalcExcludedPixels();
        this.getIPanel().pintaImatge();
    }

    private void updateFieldwithHeaderInfo() {
        this.txtThreshold.setText(Integer.toString(this.getPatt2D().getExz_threshold()));
        this.txtMargin.setText(Integer.toString(this.getPatt2D().getExz_margin()));
        this.txtCircular.setText(Integer.toString(this.getPatt2D().getExz_detcircle()));
    }

    public void updateSelectedElement() {
        if (this.listPolZones.getSelectedIndex() >= 0) {
            final PolyExZone p = this.getPatt2D().getPolyExZones().get(this.listPolZones.getSelectedIndex());
            this.lmPoly.set(this.listPolZones.getSelectedIndex(), p.printLnVertexs().trim());
        }
    }

    public boolean isDrawingPolExZone() {
        return this.drawingPolExZone;
    }

    public void setDrawingPolExZone(boolean drawingExZone) {
        this.drawingPolExZone = drawingExZone;
    }

    public boolean isDrawingArcExZone() {
        return this.drawingArcExZone;
    }

    public void setDrawingArcExZone(boolean drawingArcExZone) {
        this.drawingArcExZone = drawingArcExZone;
    }

    public boolean isDrawingBSExZone() {
        return this.drawingBSExZone;
    }

    public void setDrawingBSExZone(boolean drawingBSExZone) {
        this.drawingBSExZone = drawingBSExZone;
    }

    public PolyExZone getCurrentPolyExZ() {
        return this.currentPolyExZ;
    }

    public void setCurrentPolyExZ(PolyExZone currentExZ) {
        this.currentPolyExZ = currentExZ;
    }

    public ArcExZone getCurrentArcExZ() {
        return this.currentArcExZ;
    }

    public void setCurrentArcExZ(ArcExZone currentArcExZ) {
        this.currentArcExZ = currentArcExZ;
    }

    protected void do_btnApply_actionPerformed(ActionEvent arg0) {
        try {
            this.getPatt2D().setExz_margin(Integer.parseInt(this.txtMargin.getText()));
        } catch (final Exception e) {
            this.getPatt2D().setExz_margin(0);
            log.warning("Error reading margin, it should be an integer number");
        }
        try {
            this.getPatt2D().setExz_threshold(Integer.parseInt(this.txtThreshold.getText()));
        } catch (final Exception e) {
            this.getPatt2D().setExz_threshold(0);
            log.warning("Error reading threshold, it should be an integer number");
        }
        try {
            this.getPatt2D().setExz_detcircle(Integer.parseInt(this.txtCircular.getText()));
        } catch (final Exception e) {
            this.getPatt2D().setExz_detcircle(0);
            log.warning("Error reading detector circle, it should be an integer number");
        }
        //        this.getPatt2D().recalcExcludedPixelsThresholds();
        this.getPatt2D().recalcExcludedPixels();
        //        patt2D.recalcExcludedPixels();  //no caldria ja que s'ha anat fent per separat als tipus
        this.getIPanel().pintaImatge();
    }

    protected void do_btnReadExzFile_actionPerformed(ActionEvent e) {
        final File exfile = FileUtils.fchooserOpen(this.exzDialog, new File(D2Dplot_global.getWorkdir()), null, 0);
        if (exfile != null) {
            ImgFileUtils.readEXZ(this.getPatt2D(), exfile, false);
            this.updateListPoly();
            this.updateListArc();
            this.updateFieldwithHeaderInfo();
            D2Dplot_global.setWorkdir(exfile);
        }

    }

    protected void do_listZones_valueChanged(ListSelectionEvent arg0) {
        if (this.listPolZones.getSelectedIndex() >= 0) {
            this.setCurrentPolyExZ(this.getPatt2D().getPolyExZones().get(this.listPolZones.getSelectedIndex()));
        } else {
            this.setCurrentPolyExZ(null);
        }
        this.getIPanel().actualitzarVista();
    }

    protected void do_listArcZones_valueChanged(ListSelectionEvent arg0) {
        if (this.listArcZones.getSelectedIndex() >= 0) {
            this.setCurrentArcExZ(this.getPatt2D().getArcExZones().get(this.listArcZones.getSelectedIndex()));
        } else {
            this.setCurrentArcExZ(null);
        }
        this.getIPanel().actualitzarVista();
    }

    protected void do_btnWriteMaskbin_actionPerformed(ActionEvent e) {
        final File out = FileUtils.fchooserOpenDir(this.exzDialog, new File(D2Dplot_global.getWorkdir()),
                "Folder to save MASK.BIN");
        File outBin;
        File outexz;
        if (out != null) {
            outBin = new File(out.getAbsolutePath() + D2Dplot_global.separator + "MASK.BIN");
            outexz = FileUtils.canviExtensio(outBin, "exz");

            final Pattern2D mask = new Pattern2D(this.getPatt2D(), false); //ja copia les exz
            boolean overwrite = true;
            if (outBin.exists()) {
                overwrite = FileUtils.YesNoDialog(this.exzDialog,
                        String.format("Overwrite existing %s file?", outexz.getName()));
            }
            if (overwrite)
                outBin = ImgFileUtils.writePatternFile(outBin, mask, false); //extensio forçada abans
            overwrite = true;
            if (outexz.exists()) {
                overwrite = FileUtils.YesNoDialog(this.exzDialog,
                        String.format("Overwrite existing %s file?", outexz.getName()));
            }
            if (overwrite)
                outexz = ImgFileUtils.writeEXZ(outexz, this.getPatt2D(), false);

            if (outBin != null) {
                log.info(outBin.toString() + " written!");
            } else {
                log.warning("Error writting MASK.BIN file");
            }
            if (outexz != null) {
                log.info(outexz.toString() + " written!");
            } else {
                log.warning("Error writting MASK.exz file");
            }

        } else {
            log.warning("Error writting MASK.BIN file");
            return;
        }
    }

    protected void do_btnAddBs_actionPerformed(ActionEvent arg0) {
        //add a polygonal zone corresponding to the BeamStop
        this.bsd = new ExZ_BSdiag(this.parent, this);
        this.bsd.getExzBSdialog().setAlwaysOnTop(true);
        this.bsd.getExzBSdialog().setVisible(true);
    }

    public void applyBSparameters(int radi, int armw, int ipx, int ipy) {
        this.createBSmask(radi, armw, ipx, ipy);
    }

    protected void createBSmask(int radiPixels, int ampladaArm, int pXarm, int pYarm) {
        final Pattern2D patt2D = this.getPatt2D();
        //primer fem la rodona central
        //pixel a la "vertical" corresponent al radi
        int verX = patt2D.getCentrXI();
        int verY = patt2D.getCentrYI() - radiPixels;

        final PolyExZone pol = new PolyExZone(false);
        //        pol.addPoint(verX, verY);
        float t2deg = (float) patt2D.calc2T(verX, verY, true);
        //ara anirem girant i afegint punts
        for (int i = 0; i < 360; i = i + 20) {
            final Point2D.Float pixF = patt2D.getPixelFromAzimutAnd2T(i, t2deg);
            log.writeNameNumPairs("CONFIG", true, "azim,px,py", i, pixF.x, pixF.y);
            pol.addPoint((int) (pixF.x), (int) (pixF.y));
        }
        patt2D.addExZone(pol);

        //ara fem la resta del braç
        //pixel a la "vertical" corresponent a mitja amplada del braç
        verX = patt2D.getCentrXI();
        verY = patt2D.getCentrYI() - FastMath.round(ampladaArm / 2.f);
        t2deg = (float) patt2D.calc2T(verX, verY, true); //2theta de mitja amplada braç per aplicar azimuts
        final float azimArm = patt2D.getAzimAngle(pXarm, pYarm, true);

        //ara anem a buscar els 4 pixels
        float minus = azimArm - 90;
        if (minus < 0)
            minus = 360 + minus;
        float plus = azimArm + 90;
        if (plus > 360)
            plus = plus - 360;

        final float maxt2 = patt2D.getMax2TdegCircle();

        log.writeNameNumPairs("CONFIG", true, "minus,plus,maxt2", minus, plus, maxt2);

        final Point2D.Float pix1 = patt2D.getPixelFromAzimutAnd2T(minus, t2deg);
        final Point2D.Float pix2 = patt2D.getPixelFromAzimutAnd2T(plus, t2deg);

        //vectors centre pixel
        final float vCP1x = pix1.x - patt2D.getCentrX();
        final float vCP1y = patt2D.getCentrY() - pix1.y;
        final float vCP2x = pix2.x - patt2D.getCentrX();
        final float vCP2y = patt2D.getCentrY() - pix2.y;

        final Point2D.Float pix3 = patt2D.getPixelFromAzimutAnd2T(azimArm, maxt2);
        final Point2D.Float pix4 = patt2D.getPixelFromAzimutAnd2T(azimArm, maxt2);
        //ara hem de sumar el vCP1x al pix3.x, vCP1y al pix3.y, etc...
        pix3.x = pix3.x + vCP1x;
        pix3.y = pix3.y - vCP1y;
        pix4.x = pix4.x + vCP2x;
        pix4.y = pix4.y - vCP2y;

        final PolyExZone pol2 = new PolyExZone(false);
        pol2.addPoint((int) (pix1.x), (int) (pix1.y));
        pol2.addPoint((int) (pix2.x), (int) (pix2.y));
        pol2.addPoint((int) (pix4.x), (int) (pix4.y));
        pol2.addPoint((int) (pix3.x), (int) (pix3.y));

        patt2D.addExZone(pol2);

        this.lmPoly.addElement(pol.printLnVertexs());
        this.lmPoly.addElement(pol2.printLnVertexs());
        this.updateListPoly();
        this.listPolZones.setSelectedIndex(this.lmPoly.size() - 1);

    }

    public void finishedArcZone() {
        this.setDrawingArcExZone(false);
        this.getPatt2D().getArcExZones().add(this.currentArcExZ);
        this.lmArc.addElement(this.currentArcExZ.toString());
        this.updateListArc();
        this.listArcZones.setSelectedIndex(this.lmArc.size() - 1);
    }

    protected void do_btnAddArc_actionPerformed(ActionEvent arg0) {
        //Cliquem els 3 punts
        if (this.isDrawingArcExZone() || this.isDrawingPolExZone()) {
            log.info("finish drawing current zone first");
            return;
        }
        final ArcExZone p = new ArcExZone(this.getPatt2D());
        this.currentArcExZ = p;
        this.setDrawingArcExZone(true);
    }

    protected void do_btnAddByValues_actionPerformed(ActionEvent arg0) {
        this.arcd = new ExZ_ArcDialog(this.parent, this);
        this.arcd.getExzArcDialog().setAlwaysOnTop(true);
        this.arcd.getExzArcDialog().setVisible(true);
    }

    public void applyArcZoneParameters(int ipx, int ipy, int hradwpx, int hazimwdeg) {
        this.currentArcExZ = new ArcExZone(ipx, ipy, hradwpx, hazimwdeg, this.getPatt2D());
        this.finishedArcZone();
    }

    protected void do_btnDelArc_actionPerformed(ActionEvent e) {
        this.getPatt2D().getArcExZones().remove(this.listArcZones.getSelectedIndex());
        this.updateListArc();
        if (!this.lmArc.isEmpty()) {
            this.listArcZones.setSelectedIndex(this.lmArc.size() - 1);
        }
    }

    public boolean isDrawingFreeExZone() {
        return this.drawingFreeExZone;
    }

    public void setDrawingFreeExZone(boolean drawingFreeExZone) {
        this.drawingFreeExZone = drawingFreeExZone;
    }

    protected void do_tglbtnFreedraw_actionPerformed(ActionEvent arg0) {
        if (this.tglbtnFreedraw.isSelected()) {
            this.tglbtnFreedraw.setText("click to finish mouse free paint");
            int size = 10;
            try {
                size = Integer.parseInt(this.txtSizeMouse.getText());
            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
            }
            this.ip.setMouseFreeArestaQ(size);
            this.setDrawingFreeExZone(true);
            this.backupImage = new Pattern2D(this.getPatt2D(), true);
        } else {
            this.tglbtnFreedraw.setText("Mouse Free Paint");
            this.setDrawingFreeExZone(false);
        }
    }

    protected void do_btnUndo_actionPerformed(ActionEvent e) {
        if (this.backupImage != null) {
            this.ip.getMainFrame().updatePatt2D(this.backupImage, false, false);
            this.ip.pintaImatge();
        }
    }

    protected void do_btnclearAll_actionPerformed(ActionEvent e) {
        this.getPatt2D().clearPaintedEXZpixel();
        //        this.getPatt2D().recalcExcludedPixelsMouseFree();
        this.getPatt2D().recalcExcludedPixels();
        this.ip.pintaImatge();
        log.debug("clear pixels EXZ");
    }

    protected void do_btnReadMaskbin_actionPerformed(ActionEvent e) {
        final File f = FileUtils.fchooserOpen(this.exzDialog, new File(D2Dplot_global.getWorkdir()),
                ImgFileUtils.getExtensionFilterRead(), 0);
        if (f.exists()) {
            final boolean overwrite = FileUtils.YesNoDialog(this.exzDialog,
                    String.format("Apply excluded pixels from the selected image to the current image?"));
            if (!overwrite)
                return;
            final Pattern2D msk = ImgFileUtils.readPatternFile(f, false);
            this.getPatt2D().copyExZonesFromImage(msk);  //TODO comprovar que funcioni he canviat el metode (era copyMaskPixelsFromImage) , ja fa el recalcexcludedpixels
            this.updateListPoly();
            this.updateListArc();
            this.updateFieldwithHeaderInfo();
            this.getIPanel().pintaImatge();
        }
    }

    public void setVisible(boolean vis) {
        this.exzDialog.setVisible(vis);
        if (vis == true)
            this.chckbxExZones.setSelected(true);
    }

    public void dispose() {
        //ask for apply?
        this.chckbxExZones.setSelected(false);
        this.getIPanel().actualitzarVista();
        this.exzDialog.dispose();
    }
}