package vava33.plot2d;

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

import vava33.plot2d.auxi.ExZone;
import vava33.plot2d.auxi.Pattern2D;

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
    private File dataFile;
    private Pattern2D patt2D;
    private boolean setExZones;
    private JCheckBox chckbxYToMask;

    /**
     * Create the dialog.
     */
    public ExZones_dialog(MainFrame mf) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(ExZones_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Excluded Zones");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 312, 450);
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
                GridBagLayout gbl_panel_left = new GridBagLayout();
                gbl_panel_left.columnWidths = new int[] { 0, 0, 0, 0, 0 };
                gbl_panel_left.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
                gbl_panel_left.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0 };
                gbl_panel_left.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
                this.panel_left.setLayout(gbl_panel_left);
                {
                    this.chckbxExZones = new JCheckBox("Show Excluded Zones");
                    this.chckbxExZones.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            do_chckbxCalibrate_itemStateChanged(arg0);
                        }
                    });
                    this.chckbxExZones.setSelected(true);
                    GridBagConstraints gbc_chckbxExZones = new GridBagConstraints();
                    gbc_chckbxExZones.gridwidth = 3;
                    gbc_chckbxExZones.anchor = GridBagConstraints.WEST;
                    gbc_chckbxExZones.insets = new Insets(5, 5, 5, 5);
                    gbc_chckbxExZones.gridx = 0;
                    gbc_chckbxExZones.gridy = 0;
                    this.panel_left.add(this.chckbxExZones, gbc_chckbxExZones);
                }
                {
                    this.cbox_onTop = new JCheckBox("on top");
                    this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                    this.cbox_onTop.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            do_cbox_onTop_itemStateChanged(arg0);
                        }
                    });
                    this.cbox_onTop.setActionCommand("on top");
                    GridBagConstraints gbc_cbox_onTop = new GridBagConstraints();
                    gbc_cbox_onTop.gridwidth = 2;
                    gbc_cbox_onTop.insets = new Insets(5, 0, 5, 0);
                    gbc_cbox_onTop.anchor = GridBagConstraints.EAST;
                    gbc_cbox_onTop.gridx = 3;
                    gbc_cbox_onTop.gridy = 0;
                    this.panel_left.add(this.cbox_onTop, gbc_cbox_onTop);
                }
                {
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
                    {
                        this.btnAdd = new JButton("Add");
                        this.btnAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                do_btnAdd_actionPerformed(arg0);
                            }
                        });
                        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
                        gbc_btnAdd.insets = new Insets(0, 5, 5, 5);
                        gbc_btnAdd.gridx = 0;
                        gbc_btnAdd.gridy = 1;
                        this.panel_left.add(this.btnAdd, gbc_btnAdd);
                    }
                    {
                        this.btnDel = new JButton("Del");
                        this.btnDel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                do_btnDel_actionPerformed(e);
                            }
                        });
                        GridBagConstraints gbc_btnDel = new GridBagConstraints();
                        gbc_btnDel.insets = new Insets(0, 0, 5, 5);
                        gbc_btnDel.gridx = 1;
                        gbc_btnDel.gridy = 1;
                        this.panel_left.add(this.btnDel, gbc_btnDel);
                    }
                    {
                        this.scrollPane = new JScrollPane();
                        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
                        gbc_scrollPane.gridwidth = 5;
                        gbc_scrollPane.fill = GridBagConstraints.BOTH;
                        gbc_scrollPane.insets = new Insets(0, 5, 5, 0);
                        gbc_scrollPane.gridx = 0;
                        gbc_scrollPane.gridy = 2;
                        this.panel_left.add(this.scrollPane, gbc_scrollPane);
                        {
                            this.listZones = new JList();
                            this.listZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            this.scrollPane.setViewportView(this.listZones);
                        }
                    }
                    {
                        this.lblMargin = new JLabel("Margin=");
                        GridBagConstraints gbc_lblMargin = new GridBagConstraints();
                        gbc_lblMargin.anchor = GridBagConstraints.EAST;
                        gbc_lblMargin.insets = new Insets(0, 5, 5, 5);
                        gbc_lblMargin.gridx = 0;
                        gbc_lblMargin.gridy = 3;
                        this.panel_left.add(this.lblMargin, gbc_lblMargin);
                    }
                    {
                        this.txtMargin = new JTextField();
                        this.txtMargin.setText("0");
                        GridBagConstraints gbc_txtMargin = new GridBagConstraints();
                        gbc_txtMargin.insets = new Insets(0, 0, 5, 5);
                        gbc_txtMargin.fill = GridBagConstraints.HORIZONTAL;
                        gbc_txtMargin.gridx = 1;
                        gbc_txtMargin.gridy = 3;
                        this.panel_left.add(this.txtMargin, gbc_txtMargin);
                        this.txtMargin.setColumns(4);
                    }
                    {
                        this.btnWriteExzFile = new JButton("write EXZ file");
                        this.btnWriteExzFile.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                do_btnWriteExzFile_actionPerformed(arg0);
                            }
                        });
                        GridBagConstraints gbc_btnWriteExzFile = new GridBagConstraints();
                        gbc_btnWriteExzFile.insets = new Insets(0, 0, 5, 0);
                        gbc_btnWriteExzFile.anchor = GridBagConstraints.EAST;
                        gbc_btnWriteExzFile.gridwidth = 3;
                        gbc_btnWriteExzFile.gridx = 2;
                        gbc_btnWriteExzFile.gridy = 3;
                        this.panel_left.add(this.btnWriteExzFile, gbc_btnWriteExzFile);
                    }
                    this.lblHelp.setFont(new Font("Tahoma", Font.BOLD, 14));
                    GridBagConstraints gbc_lbllist = new GridBagConstraints();
                    gbc_lbllist.insets = new Insets(0, 0, 5, 0);
                    gbc_lbllist.gridx = 4;
                    gbc_lbllist.gridy = 1;
                    this.panel_left.add(this.lblHelp, gbc_lbllist);
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
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 0, 0 };
                gbl_buttonPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
            JButton okButton = new JButton("close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.anchor = GridBagConstraints.EAST;
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 1;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
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

        tAOut.ln("** Excluded zones definition **");

        dataFile = mf.getOpenedFile();
        patt2D = mf.getPatt2D();
        lm = new DefaultListModel();
        listZones.setModel(lm);
        {
            this.chckbxYToMask = new JCheckBox("Y0 to Mask");
            this.chckbxYToMask.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent arg0) {
                    do_chckbxYToMask_itemStateChanged(arg0);
                }
            });
            GridBagConstraints gbc_chckbxYToMask = new GridBagConstraints();
            gbc_chckbxYToMask.gridwidth = 2;
            gbc_chckbxYToMask.insets = new Insets(0, 0, 0, 5);
            gbc_chckbxYToMask.gridx = 0;
            gbc_chckbxYToMask.gridy = 4;
            this.panel_left.add(this.chckbxYToMask, gbc_chckbxYToMask);
        }

        if (this.readEXZfile()) {
            tAOut.ln("Excluded zones file (.EXZ) found!");
        } else {
            tAOut.ln("Excluded zones file (.EXZ) not found. Set them if any.");
        }

    }

    @Override
    public void dispose() {
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
        //afegeix poligon per defecte
        ExZone p = new ExZone();
        patt2D.getExZones().add(p);
        lm.addElement(p.printLnVertexs());
    }

    protected void do_btnDel_actionPerformed(ActionEvent e) {
        patt2D.getExZones().remove(listZones.getSelectedIndex());
        this.updateList();
    }

    protected void do_btnWriteExzFile_actionPerformed(ActionEvent arg0) {
        File exfile = new File(FileUtils.getFNameNoExt(dataFile).concat(".EXZ"));
        if(exfile.exists()){
            //avisem que es sobreescriurà
            Object[] options = {"Yes","No"};
            int n = JOptionPane.showOptionDialog(this,
                    "EXZ file will be overwritten. Continue?",
                    "File exists",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    options, //the titles of buttons
                    options[1]); //default button title
            if (n!=0){return;} // si s'ha cancelat o tancat la finestra no es segueix salvant 
        }
        // creem un printwriter amb el fitxer file (el que estem escribint)
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(exfile)));
            // ESCRIBIM AL FITXER:
            output.println("! Excluded zones file for: " + dataFile.getName());
            output.println("MARGIN="+ patt2D.getMargin());
            int y0msk=0;
            if(chckbxYToMask.isSelected()) y0msk=1;
            output.println("Y0TOMASK="+ y0msk);
            output.println("NEXZONES="+ patt2D.getExZones().size());

            Iterator<ExZone> it = patt2D.getExZones().iterator();
            while (it.hasNext()) {
                ExZone p = it.next();
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<p.npoints;i++){
                    sb.append(p.getXYvertex(i)).append(" ");
                }
//                int ULx = p.getXVertex(0);
//                int ULy = Math.round(r.y);
//                int URx = Math.round(r.x + r.width - 1);
//                int URy = Math.round(r.y);
//                int BRx = Math.round(r.x + r.width - 1); // -1 perquè al representar fem +1
//                int BRy = Math.round(r.y + r.height - 1);
//                int BLx = Math.round(r.x);
//                int BLy = Math.round(r.y + r.height - 1);
//                String element = ULx + " " + ULy + " " + URx + " " + URy + " " + BRx + " " + BRy + " " + BLx + " " + BLy;
                output.println(" " + sb.toString().trim());
            }
            
            //COMENTARIS INFO
            output.println("!");
            output.println("! MARGIN    Margin of the image in pixels");
            output.println("! Y0TOMASK  Set pixels with intensity=0 to intensity=-1 (masouk) [values 1=Yes, 0=No]");
            output.println("! NEXZONES  Number of Excluded Zones. ");
            output.println("!           This line is followed by the N excluded zones defined as four vertex");
            output.println("!           polygons in clockwise order. For example for a rectangle-like shape:");
            output.println("!           ULx ULy URx URy BRx BRy BLx BLy");
            output.println("!           (space separated and in pixel units. UL: upper-left corner of a rectangle,");
            output.println("!           UR: upper-right corner of a rectangle, BR: bottom-right corner of a rectangle,");
            output.println("!           BL: bottom-left corner of a rectangle)");
            
            output.close();
            tAOut.ln("EXZ file saved successfully");
            
        } catch (IOException e) {
            e.printStackTrace();
            tAOut.ln("Error writing EXZ file");
        }
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.setExZones = chckbxExZones.isSelected();
    }

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
        this.dispose();
    }

    public ExZone getCurrentZone() {
        if (listZones.getSelectedIndex() >= 0) {
            return patt2D.getExZones().get(listZones.getSelectedIndex());
        } else {
            return null;
        }
    }

    public boolean isSetExZones() {
        return setExZones;
    }

    private boolean readEXZfile() {
        File exfile = new File(FileUtils.getFNameNoExt(dataFile).concat(".exz"));
        if (!exfile.exists()) {
            exfile = new File(FileUtils.getFNameNoExt(dataFile).concat(".EXZ"));
            if (!exfile.exists())
                return false;
        }
        // aqui hauriem de tenir exfile ben assignada, la llegim
        String line;
        try {
            Scanner scExFile = new Scanner(exfile);
            boolean llegint = true;
            
            while(llegint){
                if (scExFile.hasNextLine()) {
                    line = scExFile.nextLine();
                } else {
                    scExFile.close();
                    llegint=false;
                    continue;
                }
                
                if(line.startsWith("!")){
                    continue;
                }
                
                int iigual=line.indexOf("=")+1;
                
                if(line.trim().startsWith("MARGIN")){
                    patt2D.setMargin(Integer.parseInt(line.substring(iigual, line.trim().length()).trim()));
                    txtMargin.setText(Integer.toString(patt2D.getMargin()));
                    continue;
                }
                if(line.trim().startsWith("Y0TOMASK")){
                    int ytm = Integer.parseInt(line.substring(iigual, line.trim().length()).trim());
                    if(ytm==1){
                        chckbxYToMask.setSelected(true);    
                        patt2D.setY0toMask(1);
                    }else{
                        chckbxYToMask.setSelected(false);
                        patt2D.setY0toMask(0);
                    }
                    continue;
                }
                //reiniciem zones excloses de la imatge
                patt2D.getExZones().clear();
                if(line.trim().startsWith("NEXZONES")){
                    int nexz = Integer.parseInt(line.substring(iigual, line.trim().length()).trim());
                    //ara llegim les zones
                    for (int i = 0; i < nexz; i++) {
                        line = scExFile.nextLine();
                        //tolerem una linia de comentari
                        if(line.trim().startsWith("!"))line = scExFile.nextLine();
                        String[] zona = line.trim().split(" ");
                        patt2D.addExZone(new ExZone(Integer.parseInt(zona[0]),
                                Integer.parseInt(zona[1]),Integer.parseInt(zona[2]),Integer.parseInt(zona[3]),
                                Integer.parseInt(zona[4]),Integer.parseInt(zona[5]),Integer.parseInt(zona[6]),
                                Integer.parseInt(zona[7])));
                    }
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        updateList();
        return true;
    }

    public void updateList() {
        lm.clear();
        Iterator<ExZone> it = patt2D.getExZones().iterator();
        while (it.hasNext()) {
            ExZone p = it.next();
            lm.addElement(p.printLnVertexs().trim());
        }
    }

    public void updateSelectedElement() {
        if (listZones.getSelectedIndex() >= 0) {
            ExZone p =  patt2D.getExZones().get(listZones.getSelectedIndex());
            lm.set(listZones.getSelectedIndex(), p.printLnVertexs().trim());
        }
    }

    protected void do_chckbxYToMask_itemStateChanged(ItemEvent arg0) {
        if(arg0.getStateChange()==ItemEvent.SELECTED){
            patt2D.setY0toMask(1);
        }else{
            patt2D.setY0toMask(0);
        }
           
    }
}