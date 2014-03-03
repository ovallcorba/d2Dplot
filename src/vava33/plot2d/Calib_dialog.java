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
import java.awt.geom.Rectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;

import vava33.plot2d.auxi.Pattern2D;


public class Calib_dialog extends JDialog {

    private static final long serialVersionUID = -5947817749730984383L;

    private static float[] LaB6_d = { 0.0f, 4.156878635f, 2.939357609f, 2.399975432f, 2.078432243f, 1.859004281f,
            1.697043447f, 1.469674856f, 1.385628455f, 1.314520218f, 1.25335391f, 1.199991704f, 1.152911807f,
            1.110975349f, 1, .039218708f, 1.008191043f };
     
    private final ButtonGroup bGroupBoxes = new ButtonGroup();
    private JButton btnCalc;
    private JButton btnNewButton;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxCalibrate;
    private JCheckBox chckbxDrawBox1;
    private JCheckBox chckbxDrawBox2;
    private final JPanel contentPanel = new JPanel();
    private JLabel lbllist;
    private JLabel lblPixelSizemm;
    private JPanel panel_left;
    private JPanel panel_right;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private JTextField txt_pixSize;
    private JTextField txt_r1;
    private JTextField txt_r2;
    private JTextField txtDistOD;
    private LogJTextArea tAOut;
    private float WLmean;
    private float Xmean, Ymean;
    private float MDmean;
    private Rectangle2D.Float[] anells = new Rectangle2D.Float[2];
    private boolean calibrating;
    private Pattern2D patt2D;

    /**
     * Create the dialog.
     */
    public Calib_dialog(Pattern2D pattern, MainFrame mf) {
        this.patt2D = pattern;
        setIconImage(Toolkit.getDefaultToolkit().getImage(Calib_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LaB6 Calibration");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 380, 580);
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
                gbl_panel_left.columnWidths = new int[] { 0, 0, 0 };
                gbl_panel_left.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
                gbl_panel_left.columnWeights = new double[] { 1.0, 1.0, 0.0 };
                gbl_panel_left.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
                this.panel_left.setLayout(gbl_panel_left);
                {
                    this.chckbxCalibrate = new JCheckBox("Calibration Mode");
                    this.chckbxCalibrate.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            do_chckbxCalibrate_itemStateChanged(arg0);
                        }
                    });
                    this.chckbxCalibrate.setSelected(true);
                    GridBagConstraints gbc_chckbxCalibrate = new GridBagConstraints();
                    gbc_chckbxCalibrate.anchor = GridBagConstraints.WEST;
                    gbc_chckbxCalibrate.insets = new Insets(5, 5, 5, 5);
                    gbc_chckbxCalibrate.gridx = 0;
                    gbc_chckbxCalibrate.gridy = 0;
                    this.panel_left.add(this.chckbxCalibrate, gbc_chckbxCalibrate);
                }
                {
                    JLabel lblDistODguess = new JLabel("Aprox. Sample-Detector dist. (mm)=");
                    GridBagConstraints gbc_lblDistODguess = new GridBagConstraints();
                    gbc_lblDistODguess.anchor = GridBagConstraints.EAST;
                    gbc_lblDistODguess.insets = new Insets(0, 5, 5, 5);
                    gbc_lblDistODguess.gridx = 0;
                    gbc_lblDistODguess.gridy = 1;
                    this.panel_left.add(lblDistODguess, gbc_lblDistODguess);
                }
                {
                    this.txtDistOD = new JTextField();
                    GridBagConstraints gbc_txtDistOD = new GridBagConstraints();
                    gbc_txtDistOD.gridwidth = 2;
                    gbc_txtDistOD.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtDistOD.insets = new Insets(0, 0, 5, 5);
                    gbc_txtDistOD.gridx = 1;
                    gbc_txtDistOD.gridy = 1;
                    this.panel_left.add(this.txtDistOD, gbc_txtDistOD);
                    this.txtDistOD.setText("150.000");
                    this.txtDistOD.setColumns(10);
                }
                {
                    this.lblPixelSizemm = new JLabel("Pixel size (mm)=");
                    GridBagConstraints gbc_lblPixelSizemm = new GridBagConstraints();
                    gbc_lblPixelSizemm.anchor = GridBagConstraints.EAST;
                    gbc_lblPixelSizemm.insets = new Insets(0, 5, 5, 5);
                    gbc_lblPixelSizemm.gridx = 0;
                    gbc_lblPixelSizemm.gridy = 2;
                    this.panel_left.add(this.lblPixelSizemm, gbc_lblPixelSizemm);
                }
                {
                    this.txt_pixSize = new JTextField();
                    this.txt_pixSize.setText("0.1024");
                    this.txt_pixSize.setColumns(10);
                    GridBagConstraints gbc_txt_pixSize = new GridBagConstraints();
                    gbc_txt_pixSize.gridwidth = 2;
                    gbc_txt_pixSize.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txt_pixSize.insets = new Insets(0, 0, 5, 5);
                    gbc_txt_pixSize.gridx = 1;
                    gbc_txt_pixSize.gridy = 2;
                    this.panel_left.add(this.txt_pixSize, gbc_txt_pixSize);
                }
                {
                    this.chckbxDrawBox1 = new JCheckBox("Edit boundary-box for ring no.");
                    bGroupBoxes.add(this.chckbxDrawBox1);
                    GridBagConstraints gbc_chckbxDrawBox1 = new GridBagConstraints();
                    gbc_chckbxDrawBox1.anchor = GridBagConstraints.EAST;
                    gbc_chckbxDrawBox1.insets = new Insets(0, 5, 5, 5);
                    gbc_chckbxDrawBox1.gridx = 0;
                    gbc_chckbxDrawBox1.gridy = 3;
                    this.panel_left.add(this.chckbxDrawBox1, gbc_chckbxDrawBox1);
                }
                {
                    this.txt_r1 = new JTextField();
                    this.txt_r1.setText("2");
                    GridBagConstraints gbc_txt_r1 = new GridBagConstraints();
                    gbc_txt_r1.anchor = GridBagConstraints.WEST;
                    gbc_txt_r1.insets = new Insets(0, 0, 5, 5);
                    gbc_txt_r1.gridx = 1;
                    gbc_txt_r1.gridy = 3;
                    this.panel_left.add(this.txt_r1, gbc_txt_r1);
                    this.txt_r1.setColumns(2);
                }
                {
                    this.chckbxDrawBox2 = new JCheckBox("Edit boundary-box for ring no.");
                    bGroupBoxes.add(this.chckbxDrawBox2);
                    GridBagConstraints gbc_chckbxDrawBox2 = new GridBagConstraints();
                    gbc_chckbxDrawBox2.insets = new Insets(0, 5, 5, 5);
                    gbc_chckbxDrawBox2.anchor = GridBagConstraints.EAST;
                    gbc_chckbxDrawBox2.gridx = 0;
                    gbc_chckbxDrawBox2.gridy = 4;
                    this.panel_left.add(this.chckbxDrawBox2, gbc_chckbxDrawBox2);
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
                    gbc_cbox_onTop.insets = new Insets(0, 0, 5, 0);
                    gbc_cbox_onTop.anchor = GridBagConstraints.EAST;
                    gbc_cbox_onTop.gridx = 1;
                    gbc_cbox_onTop.gridy = 0;
                    this.panel_left.add(this.cbox_onTop, gbc_cbox_onTop);
                }
                {
                    this.txt_r2 = new JTextField();
                    this.txt_r2.setText("8");
                    GridBagConstraints gbc_txt_r2 = new GridBagConstraints();
                    gbc_txt_r2.anchor = GridBagConstraints.WEST;
                    gbc_txt_r2.insets = new Insets(0, 0, 5, 5);
                    gbc_txt_r2.gridx = 1;
                    gbc_txt_r2.gridy = 4;
                    this.panel_left.add(this.txt_r2, gbc_txt_r2);
                    this.txt_r2.setColumns(2);
                }
                {
                    this.lbllist = new JLabel("?");
                    this.lbllist.addMouseListener(new MouseAdapter() {
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
                    this.lbllist.setFont(new Font("Tahoma", Font.BOLD, 14));
                    GridBagConstraints gbc_lbllist = new GridBagConstraints();
                    gbc_lbllist.insets = new Insets(0, 0, 5, 5);
                    gbc_lbllist.gridx = 2;
                    gbc_lbllist.gridy = 5;
                    this.panel_left.add(this.lbllist, gbc_lbllist);
                }
                {
                    this.btnCalc = new JButton("Calculate");
                    GridBagConstraints gbc_btnCalc = new GridBagConstraints();
                    gbc_btnCalc.gridwidth = 2;
                    gbc_btnCalc.insets = new Insets(0, 5, 5, 5);
                    gbc_btnCalc.gridx = 0;
                    gbc_btnCalc.gridy = 5;
                    this.panel_left.add(this.btnCalc, gbc_btnCalc);
                    this.btnCalc.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnCalc_actionPerformed(arg0);
                        }
                    });
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
                        this.tAOut.setTabSize(4);
                        this.tAOut.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                        this.tAOut.setWrapStyleWord(true);
                        this.tAOut.setLineWrap(true);
                        this.tAOut.setEditable(false);
                        // this.textArea.setForeground(Color.GREEN);
                        // this.textArea.setBackground(Color.BLACK);
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
            {
                this.btnNewButton = new JButton("Use these values");
                this.btnNewButton.setEnabled(false);
                this.btnNewButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnNewButton_actionPerformed(arg0);
                    }
                });
                GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
                gbc_btnNewButton.insets = new Insets(5, 5, 5, 5);
                gbc_btnNewButton.gridx = 0;
                gbc_btnNewButton.gridy = 0;
                buttonPane.add(this.btnNewButton, gbc_btnNewButton);
            }
            okButton.setActionCommand("OK");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.anchor = GridBagConstraints.EAST;
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 1;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
        }

        // tAOut.ln("** The LaB6 calibration is an experimental \nfeature, use with caution **");
        tAOut.ln("** The LaB6 calibration is an experimental feature, use with caution **");
    }

    @Override
    public void dispose() {
        this.chckbxCalibrate.setSelected(false);
        super.dispose();
    }

    protected void do_btnCalc_actionPerformed(ActionEvent arg0) {
        float iniDist = Float.parseFloat(this.txtDistOD.getText());
        float pixSize = Float.parseFloat(this.txt_pixSize.getText());
        int d1 = Integer.parseInt(this.txt_r1.getText());
        int d2 = Integer.parseInt(this.txt_r2.getText());

        //calculem excentricitat elipses
        float majRr1=Math.max(anells[0].height, anells[0].width);
        float menRr1=Math.min(anells[0].height, anells[0].width);
        float majRr2=Math.max(anells[1].height, anells[1].width);
        float menRr2=Math.min(anells[1].height, anells[1].width);
        float ex1=(float)Math.sqrt(1-(menRr1/majRr1)*(menRr1/majRr1));
        float ex2=(float)Math.sqrt(1-(menRr2/majRr2)*(menRr2/majRr2));

        tAOut.ln("Eccentricity");
        tAOut.ln(" (ring"+d1+")= "+ex1);
        tAOut.ln(" (ring"+d2+")= "+ex2);        
//        tAOut.ln("Angular Eccentricity (º)");
//        tAOut.ln(" (ring"+d1+")= "+Math.toDegrees(Math.asin(ex1)));
//        tAOut.ln(" (ring"+d2+")= "+Math.toDegrees(Math.asin(ex2)));        

        // primer mirem els radis verticals
        float r1 = (anells[0].height / 2.f) * pixSize; 
        float r2 = (anells[1].height / 2.f) * pixSize;
        float vertMD = getMDdist(iniDist, r1, r2, d1, d2);

        // segon mirem els radis horitzontals
        r1 = (anells[0].width / 2.f) * pixSize;
        r2 = (anells[1].width / 2.f) * pixSize;
        float horMD = getMDdist(iniDist, r1, r2, d1, d2);

        MDmean = (horMD + vertMD) / 2.f;

        tAOut.ln("Sample-Detector dist (mm)");
        tAOut.ln(" (vert)= " + FileUtils.dfX_3.format(vertMD));
        tAOut.ln(" (hori)= " + FileUtils.dfX_3.format(horMD));
        tAOut.ln(" (mean)= " + FileUtils.dfX_3.format(MDmean));

        // estimacio del centre
        Xmean = (float) (anells[0].getCenterX() + anells[1].getCenterX()) / 2;
        Ymean = (float) (anells[0].getCenterY() + anells[1].getCenterY()) / 2;

        tAOut.ln("Beam center (pixel)");
        tAOut.ln(" (ring"+d1+")= [" + FileUtils.dfX_3.format(anells[0].getCenterX()) + ";" + FileUtils.dfX_3.format(anells[0].getCenterY())+"]");
        tAOut.ln(" (ring"+d2+")= [" + FileUtils.dfX_3.format(anells[1].getCenterX()) + ";" + FileUtils.dfX_3.format(anells[1].getCenterY())+"]");
        tAOut.ln("  (mean)= [" + FileUtils.dfX_3.format(Xmean) + ";" + FileUtils.dfX_3.format(Ymean) + "]");

        // estimacio de la lambda
        float lambdaR2 = (float) (2 * LaB6_d[d1] * (Math.sin(0.5 * Math.atan(r1 / MDmean))));
        float lambdaR8 = (float) (2 * LaB6_d[d2] * (Math.sin(0.5 * Math.atan(r2 / MDmean))));
        WLmean = (lambdaR2 + lambdaR8) / 2;
        tAOut.ln("Wavelength (A)");
        tAOut.ln(" (ring"+d1+")= " + FileUtils.dfX_5.format(lambdaR2));
        tAOut.ln(" (ring"+d2+")= " + FileUtils.dfX_5.format(lambdaR8));
        tAOut.ln(" (mean)= " + FileUtils.dfX_5.format(WLmean));

        btnNewButton.setEnabled(true);
    }

    protected void do_btnNewButton_actionPerformed(ActionEvent arg0) {
        if (getMDmean() > 0) {
            patt2D.setDistMD(MDmean);
            patt2D.setCentrX(Xmean);
            patt2D.setCentrY(Ymean);
            patt2D.setPixSx(Float.parseFloat(txt_pixSize.getText()));
            patt2D.setPixSy(Float.parseFloat(txt_pixSize.getText()));
            tAOut.ln("Values set as image calibrated parameters");
            return;
        }
        tAOut.ln("no calculated values");
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.calibrating = chckbxCalibrate.isSelected();
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        lbllist.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        lbllist.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
        tAOut.ln("");
        tAOut.ln("** CALIBRATION HELP **");
        tAOut.ln(" Fit manually (and carefully) the ellipses of two rings (of your choice) by "
                + "moving and resizing the drawn boxes in the image. Then click on calculate.");
        tAOut.ln("+----------------------+");
        tAOut.ln("| LaB6 reflection list |");
        tAOut.ln("+----------------------+");
        tAOut.ln("|  Ring  h k l   d(A)  |");
        tAOut.ln("|   1    1 0 0  4.156  |");
        tAOut.ln("|   2    1 1 0  2.939  |");
        tAOut.ln("|   3    1 1 1  2.399  |");
        tAOut.ln("|   4    2 0 0  2.078  |");
        tAOut.ln("|   5    2 1 0  1.859  |");
        tAOut.ln("|   6    2 1 1  1.697  |");
        tAOut.ln("|   7    2 2 0  1.469  |");
        tAOut.ln("|   8    3 0 0  1.385  |");
        tAOut.ln("|   9    3 1 0  1.314  |");
        tAOut.ln("|  10    3 1 1  1.253  |");
        tAOut.ln("|  11    2 2 2  1.199  |");
        tAOut.ln("|  12    3 2 0  1.152  |");
        tAOut.ln("|  13    3 2 1  1.110  |");
        tAOut.ln("|  14    4 0 0  1.039  |");
        tAOut.ln("|  15    4 1 0  1.008  |");
        tAOut.ln("+----------------------+");
        tAOut.ln("");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

    // Donat un valor inicial de distancia MD i dos "radis" dels quadrats
    // corresponents a dos anells (d1,d2)
    // del LaB6, et calcula la distancia MD optimitzada
    private float getMDdist(float inMD, float r1, float r2, int d1, int d2) {

        int range = 50; // increment +- al valor MD inicial
        float inc = 0.0005f; // increment a cada cicle

        // farem de la distancia entrada (mm)+-inc el valor que més apropi la
        // igualtat
        // amb increments de inc
        float val = inMD - range;
        float minVal = inMD; // emmagatzarem el valor que dona menor diferencia
                             // a la igualtat
        float minDif = 100000; // VALOR MINIM DE DIFERENCIA

        while (val < inMD + range) {
            // debug: calcul per separat
            float v1 = (float) (2 * LaB6_d[d1] * (Math.sin(0.5 * Math.atan(r1 / val))));
            float v2 = (float) (2 * LaB6_d[d2] * (Math.sin(0.5 * Math.atan(r2 / val))));
            float dif = Math.abs(v1 - v2);
            // if(val>149.5&&val<150.5){
            // System.out.println("val="+val+" atan="+atanV1+" sin="+sinAtan+" v1="+v1+" v2="+v2+"  dif="+dif);
            // }
            // System.out.println(dif);
            if (dif < minDif) {
                minDif = dif;
                minVal = val;
                // System.out.println(minDif);
                // System.out.println(minVal);
            }
            // incrementem
            val = val + inc;
        }
        // System.out.println("r2="+r2+" r8="+r8);

        return minVal;
    }

    public float getMDmean() {
        return MDmean;
    }

    public Rectangle2D.Float getRectangle(int index) {
        return anells[index];
    }

    public int getSelected() {
        // mirem quin esta seleccionat
        if (chckbxDrawBox1.isSelected())
            return 0;
        if (chckbxDrawBox2.isSelected())
            return 1;
        return -1;
    }

    public Rectangle2D.Float getSelectedRectangle() {
        if (this.getSelected() < 0) {
            return null;
        } else {
            return anells[this.getSelected()];
        }
    }

    public float getWLmean() {
        return WLmean;
    }

    public float getXmean() {
        return Xmean;
    }

    public float getYmean() {
        return Ymean;
    }

    public boolean isCalibrating() {
        return calibrating;
    }

    public void setMDmean(float mDmean) {
        MDmean = mDmean;
    }

    public void setSelectedRectangle(Rectangle2D.Float r) {
        if (this.getSelected() < 0) {
            return;
        } else {
            anells[this.getSelected()] = r;
        }
    }

    public void setWLmean(float wLmean) {
        WLmean = wLmean;
    }

    public void setXmean(float xmean) {
        Xmean = xmean;
    }

    public void setYmean(float ymean) {
        Ymean = ymean;
    }
}