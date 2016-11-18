package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.auxi.ArcExZone;
import vava33.d2dplot.auxi.ExZ_ArcDialog;
import vava33.d2dplot.auxi.ExZ_BSdiag;
import vava33.d2dplot.auxi.ImgFileUtils;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.PolyExZone;
import net.miginfocom.swing.MigLayout;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math3.util.FastMath;
import javax.swing.JToggleButton;

public class ExZones_dialog extends JDialog {

    private static final long serialVersionUID = 1280342249587120104L;
    private JButton btnAddPoly;
    private JButton btnDelPoly;
    private JButton btnWriteExzFile;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxExZones;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblHelp;
    private JLabel lblMargin;
    private JList listPolZones;
    private JPanel panel_left;
    private JPanel panel_right;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private LogJTextArea tAOut;
    private JTextField txtMargin;
    
    private DefaultListModel lmPoly;
    private DefaultListModel lmArc;
    private Pattern2D patt2D;
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
    private JButton btnWriteMaskbin;
    private JButton btnAddBs;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(ExZones_dialog.class.getName());
    private JPanel panel_1;
    private JLabel lblArcZones;
    private JList listArcZones;
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
    
    /**
     * Create the dialog.
     */
    public ExZones_dialog(ImagePanel ipanel) {
        setAlwaysOnTop(true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(ExZones_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Excluded Zones");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 540, 608);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
        {
            this.splitPane = new JSplitPane();
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            contentPanel.add(this.splitPane, "cell 0 0,grow");
            {
                this.panel_left = new JPanel();
                this.splitPane.setLeftComponent(this.panel_left);
                {
                    this.chckbxExZones = new JCheckBox("Show/Edit Excluded Zones");
                    panel_left.setLayout(new MigLayout("", "[grow][grow][grow]", "[][][][][][grow][grow][]"));
                    this.chckbxExZones.setSelected(true);
                    this.panel_left.add(this.chckbxExZones, "cell 0 0 2 1,alignx left,aligny center");
                }
                {
                    this.cbox_onTop = new JCheckBox("on top");
                    cbox_onTop.setSelected(true);
                    this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                    this.cbox_onTop.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            do_cbox_onTop_itemStateChanged(arg0);
                        }
                    });
                    this.cbox_onTop.setActionCommand("on top");
                    this.panel_left.add(this.cbox_onTop, "cell 2 0,alignx right,aligny center");
                }
                {
                    panel_2 = new JPanel();
                    panel_left.add(panel_2, "cell 0 1 3 1,growx");
                    panel_2.setLayout(new MigLayout("insets 1", "[][grow]", "[]"));
                    {
                        btnReadExzFile = new JButton("Read EXZ file");
                        panel_2.add(btnReadExzFile, "cell 0 0,alignx left");
                        {
                            this.btnWriteExzFile = new JButton("Write EXZ file");
                            panel_2.add(btnWriteExzFile, "cell 1 0,alignx left");
                            this.btnWriteExzFile.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnWriteExzFile_actionPerformed(arg0);
                                }
                            });
                        }
                        btnReadExzFile.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                do_btnReadExzFile_actionPerformed(e);
                            }
                        });
                    }
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
                                txtMargin.getDocument().addDocumentListener(new DocumentListener() {
                                    @Override
                                    public void changedUpdate(DocumentEvent e) {
                                        updateMargin();
                                    }
                        
                                    @Override
                                    public void insertUpdate(DocumentEvent e) {
                                        updateMargin();
                                    }
                        
                                    @Override
                                    public void removeUpdate(DocumentEvent e) {
                                        updateMargin();
                                    }
                        
                                    public void updateMargin() {
                                        if (txtMargin.getText().isEmpty()) {
                                            patt2D.setExz_margin(0);
                                            return;
                                        }
                                        try {
                                            patt2D.setExz_margin(Integer.parseInt(txtMargin.getText()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            tAOut.ln("Invalid margin entered");
                                            patt2D.setExz_margin(0);
                                        }
                                    }
                                });
                    }
                    this.lblHelp = new JLabel("?");
                    this.lblHelp.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            do_lbllist_mouseEntered(e);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            do_lbllist_mouseExited(e);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            do_lbllist_mouseReleased(e);
                        }
                    });
                    this.lblHelp.setFont(new Font("Tahoma", Font.BOLD, 14));
                    this.panel_left.add(this.lblHelp, "cell 2 2,alignx right,aligny center");
                    {
                        lblThreshold = new JLabel("Threshold=");
                        panel_left.add(lblThreshold, "cell 0 3,alignx right");
                    }
                    {
                        txtThreshold = new JTextField();
                        txtThreshold.setText("0");
                        panel_left.add(txtThreshold, "cell 1 3,growx,aligny center");
                        txtThreshold.setColumns(10);
                    }
                    {
                        lblDetectorCircularRadius = new JLabel("Detector Circular Radius=");
                        panel_left.add(lblDetectorCircularRadius, "cell 0 4,alignx trailing");
                    }
                    {
                        txtCircular = new JTextField();
                        txtCircular.setToolTipText("0=no mask");
                        txtCircular.setText("0");
                        panel_left.add(txtCircular, "cell 1 4,growx");
                        txtCircular.setColumns(10);
                    }
                    {
                        panel = new JPanel();
                        panel_left.add(panel, "cell 0 5 3 1,grow");
                        panel.setLayout(new MigLayout("", "[][grow][][][]", "[][75px:n,grow]"));
                        {
                            lblPolygonalZones = new JLabel("Polygonal zones");
                            panel.add(lblPolygonalZones, "cell 0 0 2 1,alignx left,aligny center");
                        }
                        this.btnAddPoly = new JButton("Add by clicks");
                        panel.add(btnAddPoly, "cell 2 0,alignx center,aligny center");
                        this.btnAddPoly.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                do_btnAddPoly_actionPerformed(arg0);
                            }
                        });
                        {
                            btnAddBs = new JButton("Add BeamStop");
                            btnAddBs.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnAddBs_actionPerformed(arg0);
                                }
                            });
                            panel.add(btnAddBs, "cell 3 0");
                        }
                        this.btnDelPoly = new JButton("Delete");
                        panel.add(btnDelPoly, "cell 4 0,alignx center,aligny center");
                        this.btnDelPoly.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                do_btnDelPoly_actionPerformed(e);
                            }
                        });
                        {
                            this.scrollPane = new JScrollPane();
                            panel.add(scrollPane, "cell 0 1 5 1,grow");
                            {
                                this.listPolZones = new JList();
                                listPolZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                listPolZones.addListSelectionListener(new ListSelectionListener() {
                                    public void valueChanged(ListSelectionEvent arg0) {
                                        do_listZones_valueChanged(arg0);
                                    }
                                });
                                this.scrollPane.setViewportView(this.listPolZones);
                            }
                        }
                    }
                    {
                        panel_1 = new JPanel();
                        panel_left.add(panel_1, "cell 0 6 3 1,grow");
                        panel_1.setLayout(new MigLayout("", "[grow][][][]", "[18px][75:n,grow]"));
                        {
                            lblArcZones = new JLabel("Arc zones");
                            panel_1.add(lblArcZones, "cell 0 0,alignx left,aligny center");
                        }
                        {
                            btnAddArc = new JButton("Add by clicks");
                            btnAddArc.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnAddArc_actionPerformed(arg0);
                                }
                            });
                            panel_1.add(btnAddArc, "cell 1 0,alignx center");
                        }
                        {
                            btnAddByValues = new JButton("Add from values");
                            btnAddByValues.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnAddByValues_actionPerformed(arg0);
                                }
                            });
                            panel_1.add(btnAddByValues, "cell 2 0");
                        }
                        {
                            btnDelArc = new JButton("Delete");
                            btnDelArc.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnDelArc_actionPerformed(e);
                                }
                            });
                            panel_1.add(btnDelArc, "cell 3 0");
                        }
                        {
                            scrollPane_2 = new JScrollPane();
                            panel_1.add(scrollPane_2, "cell 0 1 4 1,grow");
                            {
                                listArcZones = new JList();
                                listArcZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                listArcZones.addListSelectionListener(new ListSelectionListener() {
                                    public void valueChanged(ListSelectionEvent arg0) {
                                        do_listArcZones_valueChanged(arg0);
                                    }
                                });
                                scrollPane_2.setViewportView(listArcZones);
                            }
                        }
                    }
                    {
                        panel_3 = new JPanel();
                        panel_left.add(panel_3, "cell 0 7 3 1,grow");
                        panel_3.setLayout(new MigLayout("", "[][][][]", "[]"));
                        {
                            tglbtnFreedraw = new JToggleButton("Mouse Free Paint");
                            panel_3.add(tglbtnFreedraw, "cell 0 0");
                            {
                                lblSize = new JLabel("size=");
                                panel_3.add(lblSize, "cell 1 0,alignx trailing");
                            }
                            {
                                txtSizeMouse = new JTextField();
                                txtSizeMouse.setText("10");
                                panel_3.add(txtSizeMouse, "cell 2 0,growx");
                                txtSizeMouse.setColumns(10);
                            }
                            {
                                btnUndo = new JButton("undo");
                                btnUndo.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        do_btnUndo_actionPerformed(e);
                                    }
                                });
                                panel_3.add(btnUndo, "cell 3 0");
                            }
                            tglbtnFreedraw.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_tglbtnFreedraw_actionPerformed(arg0);
                                }
                            });
                        }
                    }
                    {
                    }
                }
            }
            {
                this.panel_right = new JPanel();
                this.panel_right.setBackground(Color.BLACK);
                this.splitPane.setRightComponent(this.panel_right);
                panel_right.setLayout(new MigLayout("fill, insets 2", "[395px]", "[139px]"));
                {
                    this.scrollPane_1 = new JScrollPane();
                    this.scrollPane_1.setBorder(null);
                    this.panel_right.add(this.scrollPane_1, "cell 0 0,grow");
                    {
                        this.tAOut = new LogJTextArea();
                        this.tAOut.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                        this.tAOut.setWrapStyleWord(true);
                        this.tAOut.setLineWrap(true);
                        this.tAOut.setEditable(false);
                        this.scrollPane_1.setViewportView(this.tAOut);
                    }
                }
            }
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            buttonPane.setLayout(new MigLayout("", "[grow][54px][54px]", "[28px]"));
            {
                btnApply = new JButton("Apply");
                btnApply.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnApply_actionPerformed(arg0);
                    }
                });
                {
                    btnWriteMaskbin = new JButton("Write MASK.BIN");
                    btnWriteMaskbin.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            do_btnWriteMaskbin_actionPerformed(e);
                        }
                    });
                    buttonPane.add(btnWriteMaskbin, "flowx,cell 0 0,alignx center");
                }
                buttonPane.add(btnApply, "cell 1 0,alignx right,aligny top");
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 2 0,alignx right,aligny center");
            getRootPane().setDefaultButton(okButton);
        }

        tAOut.ln("** Excluded zones definition **");
        tAOut.ln("     (click on ? for help)");
        this.setIpanel(ipanel);
        this.inicia();
    }
    

    public void setIpanel(ImagePanel ipanel){
        this.ip = ipanel;
    }
    
    public ImagePanel getIPanel() {
        return ip;
    }
    
    
    public void inicia(){
        patt2D = this.getIPanel().getPatt2D();
        lmPoly = new DefaultListModel();
        listPolZones.setModel(lmPoly);
        lmArc = new DefaultListModel();
        listArcZones.setModel(lmArc);
        this.getIPanel().getMainFrame().setViewExZ(true);
        
        if (ImgFileUtils.readEXZ(patt2D,null,true)) {
            tAOut.ln("Excluded zones file (.EXZ) found & readed!");
            updateListPoly();
            updateListArc();
            updateFieldwithHeaderInfo();
        } else {
            tAOut.ln("Excluded zones file (.EXZ) not found/loaded. Set them if any.");
            updateListPoly();
            updateListArc();
            updateFieldwithHeaderInfo();
        }
    }
    
    @Override
    public void dispose() {
        //ask for apply?
        this.chckbxExZones.setSelected(false);
        getIPanel().actualitzarVista();
        super.dispose();
    }

    protected void do_btnAddPoly_actionPerformed(ActionEvent arg0) {
        if (this.isDrawingArcExZone()){
            tAOut.stat("finish drawing current Arc zone first");
            return;
        }
        //Dibuixem poligon
        if (btnAddPoly.getText()=="Add by clicks") {
            PolyExZone p = new PolyExZone(false);
            btnAddPoly.setText("FINISH");
            currentPolyExZ = p;
            this.setDrawingPolExZone(true);
        }else{//s'ha acabat el dawing
            this.setDrawingPolExZone(false);
            if (currentPolyExZ.npoints>0){
                btnAddPoly.setText("Add by clicks");
                patt2D.getPolyExZones().add(currentPolyExZ);
                lmPoly.addElement(currentPolyExZ.printLnVertexs());
                this.updateListPoly();
                listPolZones.setSelectedIndex(lmPoly.size()-1);
            }
        }
    }
    protected void do_btnDelPoly_actionPerformed(ActionEvent e) {
        patt2D.getPolyExZones().remove(listPolZones.getSelectedIndex());
        this.updateListPoly();
    }

    protected void do_btnWriteExzFile_actionPerformed(ActionEvent arg0) {
        btnApply.doClick();
        File exfile = FileUtils.fchooserSaveNoAsk(this,new File(D2Dplot_global.getWorkdir()), null);
        if (exfile != null){
            ImgFileUtils.writeEXZ(exfile, patt2D);
            D2Dplot_global.setWorkdir(exfile);
        }
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }


    protected void do_lbllist_mouseEntered(MouseEvent e) {
        lblHelp.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        lblHelp.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
        tAOut.ln("");
        tAOut.ln("** EXCLUDED ZONES HELP **");
        tAOut.ln(" - To add a polygonal excluded zone click ADD and click several points to define the zone");
        tAOut.ln(" - To add an arc shaped excluded zone click -Add by Clicks- and click 3 points to define ");
        tAOut.ln("   the zone (center, half radial width, half azim aperture) **OR**");
        tAOut.ln("   you can introduce the values manually with -Add from values-");
        tAOut.ln(" - To add a BeamStop shaped excluded zone click -Add Beamstop- and introduce requested parameters");
        tAOut.ln(" - You can define a threshold such as if Y<Threshold the pixel will be excluded");
        tAOut.ln(" - You can define a margin in case of image borders");
        tAOut.ln(" - You can define a detector radius in case the detection area is circular");
        tAOut.ln(" 2 options after defining excluded zones:");
        tAOut.ln(" - Save in a format (D2D, BIN) that contain the information");
        tAOut.ln(" - Save an Excluded Zones (ExZ) file to be loaded later, it is a text file");
        tAOut.ln("");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        if (FileUtils.YesNoDialog(this,"Apply Changes?"))btnApply.doClick();
        this.dispose();
    }

    public PolyExZone getCurrentZone() {
        if (listPolZones.getSelectedIndex() >= 0) {
            return patt2D.getPolyExZones().get(listPolZones.getSelectedIndex());
        } else {
            return null;
        }
    }

    public boolean isSetExZones() {
        return chckbxExZones.isSelected();
    }

    public void updateListPoly() {
        lmPoly.clear();
        Iterator<PolyExZone> it = patt2D.getPolyExZones().iterator();
        while (it.hasNext()) {
            PolyExZone p = it.next();
            lmPoly.addElement(p.printLnVertexs().trim());
        }
        getIPanel().pintaImatge();
    }
    
    public void updateListArc() {
        lmArc.clear();
        Iterator<ArcExZone> it = patt2D.getArcExZones().iterator();
        while (it.hasNext()) {
            ArcExZone p = it.next();
            lmArc.addElement(p.toString().trim());
        }
        getIPanel().pintaImatge();
    }
    
    private void updateFieldwithHeaderInfo(){
        txtThreshold.setText(Integer.toString(patt2D.getExz_threshold()));
        txtMargin.setText(Integer.toString(patt2D.getExz_margin()));
    }

    public void updateSelectedElement() {
        if (listPolZones.getSelectedIndex() >= 0) {
            PolyExZone p =  patt2D.getPolyExZones().get(listPolZones.getSelectedIndex());
            lmPoly.set(listPolZones.getSelectedIndex(), p.printLnVertexs().trim());
        }
    }

    public boolean isDrawingPolExZone() {
        return drawingPolExZone;
    }

    public void setDrawingPolExZone(boolean drawingExZone) {
        this.drawingPolExZone = drawingExZone;
    }

    public boolean isDrawingArcExZone() {
        return drawingArcExZone;
    }


    public void setDrawingArcExZone(boolean drawingArcExZone) {
        this.drawingArcExZone = drawingArcExZone;
    }


    public boolean isDrawingBSExZone() {
        return drawingBSExZone;
    }


    public void setDrawingBSExZone(boolean drawingBSExZone) {
        this.drawingBSExZone = drawingBSExZone;
    }


    public PolyExZone getCurrentPolyExZ() {
        return currentPolyExZ;
    }

    public void setCurrentPolyExZ(PolyExZone currentExZ) {
        this.currentPolyExZ = currentExZ;
    }
    
    public ArcExZone getCurrentArcExZ() {
        return currentArcExZ;
    }

    public void setCurrentArcExZ(ArcExZone currentArcExZ) {
        this.currentArcExZ = currentArcExZ;
    }


    protected void do_btnApply_actionPerformed(ActionEvent arg0) {
        try{
            patt2D.setExz_margin(Integer.parseInt(txtMargin.getText()));            
        }catch(Exception e){
            patt2D.setExz_margin(0);
            tAOut.ln("Error reading margin, it should be an integer number");
        }
        try{
            patt2D.setExz_threshold(Integer.parseInt(txtThreshold.getText()));            
        }catch(Exception e){
            patt2D.setExz_threshold(0);
            tAOut.ln("Error reading threshold, it should be an integer number");
        }
        try{
            patt2D.setExz_detcircle(Integer.parseInt(txtCircular.getText()));            
        }catch(Exception e){
            patt2D.setExz_detcircle(0);
            tAOut.ln("Error reading detector circle, it should be an integer number");
        }
        this.getIPanel().pintaImatge();
    }
    protected void do_btnReadExzFile_actionPerformed(ActionEvent e) {
        File exfile = FileUtils.fchooser(this,new File(D2Dplot_global.getWorkdir()), null, false);
        if (exfile != null){
            ImgFileUtils.readEXZ(patt2D,exfile,false);
            updateListPoly();
            txtThreshold.setText(Integer.toString(patt2D.getExz_threshold()));
            txtMargin.setText(Integer.toString(patt2D.getExz_margin()));
            D2Dplot_global.setWorkdir(exfile);
        }
        
    }
    protected void do_listZones_valueChanged(ListSelectionEvent arg0) {
        if (listPolZones.getSelectedIndex() >= 0) {
            this.setCurrentPolyExZ(patt2D.getPolyExZones().get(listPolZones.getSelectedIndex()));
        }
        getIPanel().actualitzarVista();
    }
    
    protected void do_listArcZones_valueChanged(ListSelectionEvent arg0) {
        if (listArcZones.getSelectedIndex() >= 0) {
            this.setCurrentArcExZ(patt2D.getArcExZones().get(listArcZones.getSelectedIndex()));
        }
        getIPanel().actualitzarVista();
    }
    
    protected void do_btnWriteMaskbin_actionPerformed(ActionEvent e) {
        //fem que es posi nom fitxer pero despres preguntem l'extensió
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[1];
        filter[0] = new FileNameExtensionFilter("d2Dplot BIN format", "bin","BIN");
        File out = FileUtils.fchooserSaveAsk(this, new File(D2Dplot_global.getWorkdir()), filter);
        if (out!=null){
            Pattern2D mask = new Pattern2D(this.patt2D,false,true);
            mask.setExz_threshold(0);
            out = ImgFileUtils.writeBIN(out, mask);
        }else{
            tAOut.ln("Error writting MASK.BIN file");
        }
        if (out != null){
            tAOut.ln(out.toString()+" written!");
        }else{
            tAOut.ln("Error writting MASK.BIN file");

        }
    }
    
    protected void do_btnAddBs_actionPerformed(ActionEvent arg0) {
        //add a polygonal zone corresponding to the BeamStop
        bsd = new ExZ_BSdiag(this);
        bsd.setAlwaysOnTop(true);
        bsd.setVisible(true);
    }
    
    public void applyBSparameters(int radi, int armw, int ipx, int ipy){
        this.createBSmask(radi, armw, ipx, ipy);    
    }
    
    protected void createBSmask(int radiPixels, int ampladaArm, int pXarm, int pYarm){
        
        //primer fem la rodona central
        //pixel a la "vertical" corresponent al radi
        int verX=patt2D.getCentrXI();
        int verY=patt2D.getCentrYI()-radiPixels;
        
        PolyExZone pol = new PolyExZone(false);
        pol.addPoint(verX, verY);
        float t2deg = (float) patt2D.calc2T(verX,verY, true);
        //ara anirem girant i afegint punts
        for (int i=20; i<360; i=i+20){
            Point2D.Float pixF = patt2D.getPixelFromAzimutAnd2T(i, t2deg);
            log.writeNameNumPairs("CONFIG", true, "azim,px,py", i,pixF.x,pixF.y);
            pol.addPoint((int)(pixF.x), (int)(pixF.y));
        }
        patt2D.addExZone(pol);
        
        //ara fem la resta del braç
        //pixel a la "vertical" corresponent a mitja amplada del braç
        verX=patt2D.getCentrXI();
        verY=patt2D.getCentrYI()-FastMath.round(ampladaArm/2);
        t2deg = (float) patt2D.calc2T(verX,verY, true); //2theta de mitja amplada braç per aplicar azimuts
        float azimArm = patt2D.getAzimAngle(pXarm, pYarm, true);
        
        //ara anem a buscar els 4 pixels
        float minus = azimArm - 90;
        if (minus<0)minus = 360 + minus;
        float plus = azimArm + 90;
        if (plus>360)plus = plus - 360;
        
        float maxt2 = patt2D.getMax2TdegCircle();

        log.writeNameNumPairs("CONFIG", true, "minus,plus,maxt2", minus,plus,maxt2);

        Point2D.Float pix1 = patt2D.getPixelFromAzimutAnd2T(minus, t2deg);
        Point2D.Float pix2 = patt2D.getPixelFromAzimutAnd2T(plus, t2deg);
        
        //vectors centre pixel
        float vCP1x = pix1.x - patt2D.getCentrX();
        float vCP1y = patt2D.getCentrY() - pix1.y;
        float vCP2x = pix2.x - patt2D.getCentrX();
        float vCP2y = patt2D.getCentrY() - pix2.y;
        
        Point2D.Float pix3 = patt2D.getPixelFromAzimutAnd2T(azimArm, maxt2);
        Point2D.Float pix4 = patt2D.getPixelFromAzimutAnd2T(azimArm, maxt2);
        //ara hem de sumar el vCP1x al pix3.x, vCP1y al pix3.y, etc...
        pix3.x=pix3.x+vCP1x;
        pix3.y=pix3.y-vCP1y;
        pix4.x=pix4.x+vCP2x;
        pix4.y=pix4.y-vCP2y;
        
        PolyExZone pol2 = new PolyExZone(false);
        pol2.addPoint((int)(pix1.x), (int)(pix1.y));
        pol2.addPoint((int)(pix2.x), (int)(pix2.y));
        pol2.addPoint((int)(pix4.x), (int)(pix4.y));
        pol2.addPoint((int)(pix3.x), (int)(pix3.y));
        
        patt2D.addExZone(pol2);

        lmPoly.addElement(pol.printLnVertexs());
        lmPoly.addElement(pol2.printLnVertexs());
        this.updateListPoly();
        listPolZones.setSelectedIndex(lmPoly.size()-1);
        
    }
    public void finishedArcZone(){
        this.setDrawingArcExZone(false);
        patt2D.getArcExZones().add(currentArcExZ);
        lmArc.addElement(currentArcExZ.toString());
        this.updateListPoly();
        listArcZones.setSelectedIndex(lmArc.size()-1);
    }
    protected void do_btnAddArc_actionPerformed(ActionEvent arg0) {
        //Cliquem els 3 punts
        if (this.isDrawingArcExZone()||this.isDrawingPolExZone()){
            tAOut.stat("finish drawing current zone first");
            return;
        }
        ArcExZone p = new ArcExZone(this.patt2D);
        currentArcExZ = p;
        this.setDrawingArcExZone(true);
    }
    
    protected void do_btnAddByValues_actionPerformed(ActionEvent arg0) {
        arcd = new ExZ_ArcDialog(this);
        arcd.setAlwaysOnTop(true);
        arcd.setVisible(true);
    }
    
    public void applyArcZoneParameters(int ipx, int ipy, int hradwpx, int hazimwdeg){
        currentArcExZ = new ArcExZone(ipx,ipy,hradwpx,hazimwdeg,patt2D);
        this.finishedArcZone();
    }
    protected void do_btnDelArc_actionPerformed(ActionEvent e) {
        patt2D.getArcExZones().remove(listArcZones.getSelectedIndex());
        this.updateListArc();
    }

    

    public boolean isDrawingFreeExZone() {
        return drawingFreeExZone;
    }


    public void setDrawingFreeExZone(boolean drawingFreeExZone) {
        this.drawingFreeExZone = drawingFreeExZone;
    }
    
    protected void do_tglbtnFreedraw_actionPerformed(ActionEvent arg0) {
        if (tglbtnFreedraw.isSelected()){
            tglbtnFreedraw.setText("click to finish mouse free paint");
            int size = 10;
            try{
                size = Integer.parseInt(txtSizeMouse.getText());
            }catch(Exception e){
                if (D2Dplot_global.isDebug())e.printStackTrace();
            }
            ip.setMouseFreeArestaQ(size);
            this.setDrawingFreeExZone(true);
            backupImage = new Pattern2D(this.patt2D,true);
        }else{
            tglbtnFreedraw.setText("Mouse Free Paint");
            this.setDrawingFreeExZone(false);
        }
    }
    protected void do_btnUndo_actionPerformed(ActionEvent e) {
        if (backupImage!=null){
            this.ip.getMainFrame().updatePatt2D(backupImage, false);
            this.ip.pintaImatge();
            patt2D = backupImage;
        }
    }
}