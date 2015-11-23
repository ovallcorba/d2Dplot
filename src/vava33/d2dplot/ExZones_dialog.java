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

import vava33.d2dplot.auxi.ImgFileUtils;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.PolyExZone;
import net.miginfocom.swing.MigLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

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
                                listZones.addListSelectionListener(new ListSelectionListener() {
                                    public void valueChanged(ListSelectionEvent arg0) {
                                        do_listZones_valueChanged(arg0);
                                    }
                                });
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
            tAOut.ln("Excluded zones file (.EXZ) found & readed!");
            updateList();
            updateFieldwithHeaderInfo();
        } else {
            tAOut.ln("Excluded zones file (.EXZ) not found/loaded. Set them if any.");
            updateList();
            updateFieldwithHeaderInfo();
        }

    }
    
    @Override
    public void dispose() {
        //ask for apply?
        this.chckbxExZones.setSelected(false);
        super.dispose();
    }

    protected void do_btnAdd_actionPerformed(ActionEvent arg0) {
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
        File exfile = FileUtils.fchooserSaveNoAsk(this,new File(D2Dplot_global.workdir), null);
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
        tAOut.ln(" - You can define a threshold such as if Y<Threshold the pixel will be excluded");
        tAOut.ln(" 2 options after defining excluded zones:");
        tAOut.ln(" - Save in a format (D2D, BIN) that contain the information");
        tAOut.ln(" - Save an Excluded Zones (ExZ) file to be loaded later");
        tAOut.ln("");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        if (FileUtils.YesNoDialog(this,"Apply Changes?"))btnApply.doClick();
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
    
    private void updateFieldwithHeaderInfo(){
        txtThreshold.setText(Integer.toString(patt2D.getExz_threshold()));
        txtMargin.setText(Integer.toString(patt2D.getExz_margin()));
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
        mf.getPanelImatge().pintaImatge();
    }
    protected void do_btnReadExzFile_actionPerformed(ActionEvent e) {
        File exfile = FileUtils.fchooser(this,new File(D2Dplot_global.workdir), null, false);
        if (exfile != null){
            ImgFileUtils.readEXZ(patt2D,exfile);
            updateList();
            txtThreshold.setText(Integer.toString(patt2D.getExz_threshold()));
            txtMargin.setText(Integer.toString(patt2D.getExz_margin()));
            D2Dplot_global.setWorkdir(exfile);
        }
        
    }
    protected void do_listZones_valueChanged(ListSelectionEvent arg0) {
        if (listZones.getSelectedIndex() >= 0) {
            this.setCurrentExZ(patt2D.getExZones().get(listZones.getSelectedIndex()));
        }
    }
    
    
}