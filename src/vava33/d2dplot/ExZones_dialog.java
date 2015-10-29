package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
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

import vava33.d2dplot.auxi.ImgFileUtils;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.PolyExZone;
import net.miginfocom.swing.MigLayout;

public class ExZones_dialog extends JDialog {

    private static final long serialVersionUID = 1280342249587120104L;
    private JButton btnAdd;
    private JButton btnDel;
    private JButton btnWriteExzFile;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxExZones;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblHelp;
    private JLabel lblMargin;
    private JList listZones;
    private JPanel panel_left;
    private JPanel panel_right;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private LogJTextArea tAOut;
    private JTextField txtMargin;
    
    private DefaultListModel lm;
    private Pattern2D patt2D;
    private JTextField txtThreshold;

//    private boolean setExZones;
    
    private boolean drawingExZone; //true while drawing
    private boolean editingExZone; //true while editing
    
    private PolyExZone currentExZ;
    private JLabel lblPolygonalZones;
    private JLabel lblThreshold;
    private JPanel panel;
    private JButton btnApply;
    private JButton btnReadExzFile;
    
    private MainFrame mf;
    
    /**
     * Create the dialog.
     */
    public ExZones_dialog(MainFrame m) {
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
        setBounds(x, y, 409, 450);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            this.splitPane = new JSplitPane();
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            GridBagConstraints gbc_splitPane = new GridBagConstraints();
            gbc_splitPane.fill = GridBagConstraints.BOTH;
            gbc_splitPane.gridx = 0;
            gbc_splitPane.gridy = 0;
            contentPanel.add(this.splitPane, gbc_splitPane);
            {
                this.panel_left = new JPanel();
                this.splitPane.setLeftComponent(this.panel_left);
                {
                    this.chckbxExZones = new JCheckBox("Show/Edit Excluded Zones");
//                    this.chckbxExZones.addItemListener(new ItemListener() {
//                        @Override
//                        public void itemStateChanged(ItemEvent arg0) {
//                            do_chckbxCalibrate_itemStateChanged(arg0);
//                        }
//                    });
                    panel_left.setLayout(new MigLayout("", "[][][grow]", "[][][][][]"));
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
                    {
                        {
                            this.lblMargin = new JLabel("Margin=");
                            this.panel_left.add(this.lblMargin, "cell 0 1,alignx right,aligny center");
                        }
                    }
                    {
                        {
                            this.txtMargin = new JTextField();
                            this.txtMargin.setText("0");
                            this.panel_left.add(this.txtMargin, "cell 1 1,growx,aligny center");
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
                                            patt2D.setMargin(0);
                                            return;
                                        }
                                        try {
                                            patt2D.setMargin(Integer.parseInt(txtMargin.getText()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            tAOut.ln("Invalid margin entered");
                                            patt2D.setMargin(0);
                                        }
                                    }
                                });
                    }
                    {
                        lblThreshold = new JLabel("Threshold=");
                        panel_left.add(lblThreshold, "cell 0 2,alignx right");
                    }
                    {
                        txtThreshold = new JTextField();
                        txtThreshold.setText("0");
                        panel_left.add(txtThreshold, "cell 1 2,growx,aligny center");
                        txtThreshold.setColumns(10);
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
                        panel = new JPanel();
                        panel_left.add(panel, "cell 0 3 3 1,grow");
                        panel.setLayout(new MigLayout("", "[][grow][][][]", "[][75px:n,grow]"));
                        {
                            lblPolygonalZones = new JLabel("Polygonal zones");
                            panel.add(lblPolygonalZones, "cell 0 0 3 1,alignx left,aligny center");
                        }
                        this.btnAdd = new JButton("Add");
                        panel.add(btnAdd, "cell 3 0,alignx center,aligny center");
                        this.btnDel = new JButton("Del");
                        panel.add(btnDel, "cell 4 0,alignx center,aligny center");
                        {
                            this.scrollPane = new JScrollPane();
                            panel.add(scrollPane, "cell 0 1 5 1,grow");
                            {
                                this.listZones = new JList();
                                this.listZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                this.scrollPane.setViewportView(this.listZones);
                            }
                        }
                        this.btnDel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                do_btnDel_actionPerformed(e);
                            }
                        });
                        this.btnAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                do_btnAdd_actionPerformed(arg0);
                            }
                        });
                    }
                    {
                    }
                }
            }
            {
                this.panel_right = new JPanel();
                this.panel_right.setBackground(Color.BLACK);
                this.splitPane.setRightComponent(this.panel_right);
                GridBagLayout gbl_panel_right = new GridBagLayout();
                gbl_panel_right.columnWidths = new int[] { 0, 0 };
                gbl_panel_right.rowHeights = new int[] { 0, 0 };
                gbl_panel_right.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                gbl_panel_right.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                this.panel_right.setLayout(gbl_panel_right);
                {
                    this.scrollPane_1 = new JScrollPane();
                    this.scrollPane_1.setBorder(null);
                    GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
                    gbc_scrollPane_1.insets = new Insets(5, 5, 5, 5);
                    gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
                    gbc_scrollPane_1.gridx = 0;
                    gbc_scrollPane_1.gridy = 0;
                    this.panel_right.add(this.scrollPane_1, gbc_scrollPane_1);
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
            buttonPane.setLayout(new MigLayout("", "[grow][][54px][54px]", "[28px]"));
            {
                btnReadExzFile = new JButton("Read EXZ file");
                btnReadExzFile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnReadExzFile_actionPerformed(e);
                    }
                });
                buttonPane.add(btnReadExzFile, "cell 0 0,alignx right");
            }
            {
                this.btnWriteExzFile = new JButton("Write EXZ file");
                buttonPane.add(btnWriteExzFile, "cell 1 0,alignx right");
                this.btnWriteExzFile.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnWriteExzFile_actionPerformed(arg0);
                    }
                });
            }
            {
                btnApply = new JButton("Apply");
                btnApply.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnApply_actionPerformed(arg0);
                    }
                });
                buttonPane.add(btnApply, "cell 2 0,alignx right,aligny top");
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 3 0,alignx right,aligny center");
            getRootPane().setDefaultButton(okButton);
        }

        tAOut.ln("** Excluded zones definition **");
        this.mf=m;
        mf.getOpenedFile();
        patt2D = mf.getPatt2D();
        lm = new DefaultListModel();
        listZones.setModel(lm);

        if (ImgFileUtils.readEXZ(patt2D,null)) {
            tAOut.ln("Excluded zones file (.EXZ) found!");
            updateList();
        } else {
            tAOut.ln("Excluded zones file (.EXZ) not found. Set them if any.");
        }

    }
    
    @Override
    public void dispose() {
        //ask for apply?
        this.chckbxExZones.setSelected(false);
        super.dispose();
    }

    // public void updateSelected(Rectangle2D.Float r){
    // if (listZones.getSelectedIndex()>=0){
    // int ULx=Math.round(r.x);
    // int ULy=Math.round(r.y);
    // int BRx=Math.round(r.x+r.width);
    // int BRy=Math.round(r.y+r.height);
    // String element = ULx+" "+ULy+" "+BRx+" "+BRy;
    // lm.set(listZones.getSelectedIndex(), element);
    // }
    //
    // }
    protected void do_btnAdd_actionPerformed(ActionEvent arg0) {
//        //afegeix poligon per defecte
//        ExZone p = new ExZone(true);
//        patt2D.getExZones().add(p);
//        lm.addElement(p.printLnVertexs());
        //Dibuixem poligon
        if (btnAdd.getText()=="Add") {
            PolyExZone p = new PolyExZone(false);
            btnAdd.setText("FINISH");
            currentExZ = p;
            this.setDrawingExZone(true);
        }else{//s'ha acabat el dawing
            this.setDrawingExZone(false);
            if (currentExZ.npoints>0){
                btnAdd.setText("Add");
                patt2D.getExZones().add(currentExZ);
                lm.addElement(currentExZ.printLnVertexs());
                this.updateList();
                listZones.setSelectedIndex(lm.size()-1);
            }
        }
    }
    protected void do_btnDel_actionPerformed(ActionEvent e) {
        patt2D.getExZones().remove(listZones.getSelectedIndex());
        this.updateList();
    }

    protected void do_btnWriteExzFile_actionPerformed(ActionEvent arg0) {
        btnApply.doClick();
        //TODO ADD CURRENTDIRECTORY
        File exfile = FileUtils.fchooserSaveNoAsk(new File(D2Dplot_global.workdir), null);
        if (exfile != null){
            ImgFileUtils.writeEXZ(exfile, patt2D);            
        }
       
//        File exfile = new File(FileUtils.getFNameNoExt(dataFile).concat(".EXZ"));
//        if(exfile.exists()){
//            //avisem que es sobreescriur�
//            Object[] options = {"Yes","No"};
//            int n = JOptionPane.showOptionDialog(this,
//                    "EXZ file will be overwritten. Continue?",
//                    "File exists",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE,
//                    null, //do not use a custom Icon
//                    options, //the titles of buttons
//                    options[1]); //default button title
//            if (n!=0){return;} // si s'ha cancelat o tancat la finestra no es segueix salvant 
//        }

    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }

//    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
//        this.setExZones = chckbxExZones.isSelected();
//    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        lblHelp.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        lblHelp.setForeground(Color.black);
    }

    //TODO: A ACTUALIZAR (ara son 4 vertexs)
    protected void do_lbllist_mouseReleased(MouseEvent e) {
        tAOut.ln("");
        tAOut.ln("** EXCLUDED ZONES HELP **");
        tAOut.ln(" To add excluded zones click ADD and match the generated box into the "
                + "desired zone. Each zone is described by 4 numbers (x1 y1 x2 y2) corresponding "
                + "to the Upper-left vertex (x1,y1) and the lower-right vertex (x1,y1) of " + "the rectangular zone.");
        tAOut.ln(" After setting the excluded zones, it is highly recommended to "
                + "return to the main window and save the image as a BIN file. "
                + "In the BIN file, the pixels in the excluded zones have a mask value "
                + "of -1. Working with the BIN file, next operations (d2Dsub,etc...) will "
                + "automatically detect the mask zones.");
        tAOut.ln(" Otherwise, if you prefer not to work with a .BIN file you should write the EXZ "
                + "file in order to keep the excluded zones information when using d2Dsub. "
                + "Keep in mind that d2Dsub will write all the output files in the BIN format.");
        tAOut.ln("");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        if (FileUtils.YesNoDialog("Apply Changes?"))btnApply.doClick();
        this.dispose();
    }

    public PolyExZone getCurrentZone() {
        if (listZones.getSelectedIndex() >= 0) {
            return patt2D.getExZones().get(listZones.getSelectedIndex());
        } else {
            return null;
        }
    }

    public boolean isSetExZones() {
        return chckbxExZones.isSelected();
    }


    public void updateList() {
        lm.clear();
        Iterator<PolyExZone> it = patt2D.getExZones().iterator();
        while (it.hasNext()) {
            PolyExZone p = it.next();
            lm.addElement(p.printLnVertexs().trim());
        }
    }

    public void updateSelectedElement() {
        if (listZones.getSelectedIndex() >= 0) {
            PolyExZone p =  patt2D.getExZones().get(listZones.getSelectedIndex());
            lm.set(listZones.getSelectedIndex(), p.printLnVertexs().trim());
        }
    }

    public boolean isDrawingExZone() {
        return drawingExZone;
    }

    public void setDrawingExZone(boolean drawingExZone) {
        this.drawingExZone = drawingExZone;
    }

    public boolean isEditingExZone() {
        return editingExZone;
    }

    public void setEditingExZone(boolean editingExZone) {
        this.editingExZone = editingExZone;
    }

    public PolyExZone getCurrentExZ() {
        return currentExZ;
    }

    public void setCurrentExZ(PolyExZone currentExZ) {
        this.currentExZ = currentExZ;
    }
    protected void do_btnApply_actionPerformed(ActionEvent arg0) {
        try{
            patt2D.setMargin(Integer.parseInt(txtMargin.getText()));            
        }catch(Exception e){
            patt2D.setMargin(0);
            tAOut.ln("Error reading margin, it should be an integer number");
        }
        try{
            patt2D.setY0toMask(Integer.parseInt(txtThreshold.getText()));            
        }catch(Exception e){
            patt2D.setY0toMask(0);
            tAOut.ln("Error reading threshold, it should be an integer number");
        }
        mf.getPanelImatge().pintaImatge();
    }
    protected void do_btnReadExzFile_actionPerformed(ActionEvent e) {
        //TODO ADD CURRENTDIRECTORY
        File exfile = FileUtils.fchooser(null, null, false);
        if (exfile != null){
            ImgFileUtils.readEXZ(patt2D,exfile);
            updateList();
            txtThreshold.setText(Integer.toString(patt2D.getY0toMask()));
            txtMargin.setText(Integer.toString(patt2D.getMargin()));
        }
        
    }
}