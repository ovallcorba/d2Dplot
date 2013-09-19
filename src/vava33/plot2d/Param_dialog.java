package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import vava33.plot2d.auxi.Pattern2D;

public class Param_dialog extends JDialog {

    private static final long serialVersionUID = -7972051523318564847L;
    private JButton btnDef1;
    private JButton btnDef2;
    private JButton btnDef3;
    private JButton btnDef4;
    private JButton btnDef5;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblCheckValues;
    private JTextField txtCentrX;
    private JTextField txtCentrY;
    private JTextField txtDistOD;
    private JTextField txtPicSizeX;
    private JTextField txtPicSizeY;
    private Color[] col = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
    private int counter = 0;
    private Pattern2D patt2D;
    private JLabel lblWavelengtha;
    private JTextField txtWave;

    /**
     * Create the dialog.
     */
    public Param_dialog(Pattern2D pattern) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Param_dialog.class.getResource("/img/Icona.png")));
        setModal(true);
        this.patt2D = pattern;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Instrumental Parameters");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 400, 331);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblSampledetectorDistancemm = new JLabel("Sample-Detector distance (mm)=");
            GridBagConstraints gbc_lblSampledetectorDistancemm = new GridBagConstraints();
            gbc_lblSampledetectorDistancemm.insets = new Insets(0, 0, 5, 5);
            gbc_lblSampledetectorDistancemm.anchor = GridBagConstraints.EAST;
            gbc_lblSampledetectorDistancemm.gridx = 0;
            gbc_lblSampledetectorDistancemm.gridy = 0;
            contentPanel.add(lblSampledetectorDistancemm, gbc_lblSampledetectorDistancemm);
        }
        {
            this.txtDistOD = new JTextField();
            this.txtDistOD.setText("150.000");
            GridBagConstraints gbc_txtDistOD = new GridBagConstraints();
            gbc_txtDistOD.insets = new Insets(0, 0, 5, 0);
            gbc_txtDistOD.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtDistOD.gridx = 1;
            gbc_txtDistOD.gridy = 0;
            contentPanel.add(this.txtDistOD, gbc_txtDistOD);
            this.txtDistOD.setColumns(10);
        }
        {
            JLabel lblPixelSizeX = new JLabel("Pixel size X (mm)=");
            GridBagConstraints gbc_lblPixelSizeX = new GridBagConstraints();
            gbc_lblPixelSizeX.anchor = GridBagConstraints.EAST;
            gbc_lblPixelSizeX.insets = new Insets(0, 0, 5, 5);
            gbc_lblPixelSizeX.gridx = 0;
            gbc_lblPixelSizeX.gridy = 1;
            contentPanel.add(lblPixelSizeX, gbc_lblPixelSizeX);
        }
        {
            this.txtPicSizeX = new JTextField();
            this.txtPicSizeX.setText("0.1024");
            GridBagConstraints gbc_textPicSizeX = new GridBagConstraints();
            gbc_textPicSizeX.insets = new Insets(0, 0, 5, 0);
            gbc_textPicSizeX.fill = GridBagConstraints.HORIZONTAL;
            gbc_textPicSizeX.gridx = 1;
            gbc_textPicSizeX.gridy = 1;
            contentPanel.add(this.txtPicSizeX, gbc_textPicSizeX);
            this.txtPicSizeX.setColumns(10);
        }
        {
            JLabel lblPixelSizeY = new JLabel("Pixel size Y (mm)=");
            GridBagConstraints gbc_lblPixelSizeY = new GridBagConstraints();
            gbc_lblPixelSizeY.anchor = GridBagConstraints.EAST;
            gbc_lblPixelSizeY.insets = new Insets(0, 0, 5, 5);
            gbc_lblPixelSizeY.gridx = 0;
            gbc_lblPixelSizeY.gridy = 2;
            contentPanel.add(lblPixelSizeY, gbc_lblPixelSizeY);
        }
        {
            this.txtPicSizeY = new JTextField();
            this.txtPicSizeY.setText("0.1024");
            GridBagConstraints gbc_textPicSizeY = new GridBagConstraints();
            gbc_textPicSizeY.insets = new Insets(0, 0, 5, 0);
            gbc_textPicSizeY.fill = GridBagConstraints.HORIZONTAL;
            gbc_textPicSizeY.gridx = 1;
            gbc_textPicSizeY.gridy = 2;
            contentPanel.add(this.txtPicSizeY, gbc_textPicSizeY);
            this.txtPicSizeY.setColumns(10);
        }
        {
            JLabel lblBeamCentreX = new JLabel("Beam centre X (pixel)=");
            GridBagConstraints gbc_lblBeamCentreX = new GridBagConstraints();
            gbc_lblBeamCentreX.anchor = GridBagConstraints.EAST;
            gbc_lblBeamCentreX.insets = new Insets(0, 0, 5, 5);
            gbc_lblBeamCentreX.gridx = 0;
            gbc_lblBeamCentreX.gridy = 3;
            contentPanel.add(lblBeamCentreX, gbc_lblBeamCentreX);
        }
        {
            this.txtCentrX = new JTextField();
            this.txtCentrX.setText("1024");
            GridBagConstraints gbc_txtCentrX = new GridBagConstraints();
            gbc_txtCentrX.insets = new Insets(0, 0, 5, 0);
            gbc_txtCentrX.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtCentrX.gridx = 1;
            gbc_txtCentrX.gridy = 3;
            contentPanel.add(this.txtCentrX, gbc_txtCentrX);
            this.txtCentrX.setColumns(10);
        }
        {
            JLabel lblBeamCentreY = new JLabel("Beam centre Y (pixel)=");
            GridBagConstraints gbc_lblBeamCentreY = new GridBagConstraints();
            gbc_lblBeamCentreY.anchor = GridBagConstraints.EAST;
            gbc_lblBeamCentreY.insets = new Insets(0, 0, 5, 5);
            gbc_lblBeamCentreY.gridx = 0;
            gbc_lblBeamCentreY.gridy = 4;
            contentPanel.add(lblBeamCentreY, gbc_lblBeamCentreY);
        }
        {
            this.txtCentrY = new JTextField();
            this.txtCentrY.setText("1024");
            GridBagConstraints gbc_txtCentrY = new GridBagConstraints();
            gbc_txtCentrY.insets = new Insets(0, 0, 5, 0);
            gbc_txtCentrY.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtCentrY.gridx = 1;
            gbc_txtCentrY.gridy = 4;
            contentPanel.add(this.txtCentrY, gbc_txtCentrY);
            this.txtCentrY.setColumns(10);
        }
        {
            this.lblWavelengtha = new JLabel("Wavelength (A)=");
            GridBagConstraints gbc_lblWavelengtha = new GridBagConstraints();
            gbc_lblWavelengtha.anchor = GridBagConstraints.EAST;
            gbc_lblWavelengtha.insets = new Insets(0, 0, 0, 5);
            gbc_lblWavelengtha.gridx = 0;
            gbc_lblWavelengtha.gridy = 5;
            contentPanel.add(this.lblWavelengtha, gbc_lblWavelengtha);
        }
        {
            this.txtWave = new JTextField();
            this.txtWave.setText("0.0");
            GridBagConstraints gbc_txtWave = new GridBagConstraints();
            gbc_txtWave.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtWave.gridx = 1;
            gbc_txtWave.gridy = 5;
            contentPanel.add(this.txtWave, gbc_txtWave);
            this.txtWave.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 25, 0, 0 };
                gbl_buttonPane.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
            {
                this.btnDef1 = new JButton("ESRF");
                this.btnDef1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        do_btnDef1_actionPerformed(e);
                    }
                });
                GridBagConstraints gbc_btnDef1 = new GridBagConstraints();
                gbc_btnDef1.insets = new Insets(5, 5, 0, 5);
                gbc_btnDef1.gridx = 0;
                gbc_btnDef1.gridy = 0;
                buttonPane.add(this.btnDef1, gbc_btnDef1);
            }
            {
                this.btnDef2 = new JButton("ALBA");
                this.btnDef2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnDef2_actionPerformed(arg0);
                    }
                });
                GridBagConstraints gbc_btnDef2 = new GridBagConstraints();
                gbc_btnDef2.insets = new Insets(5, 0, 0, 5);
                gbc_btnDef2.gridx = 1;
                gbc_btnDef2.gridy = 0;
                buttonPane.add(this.btnDef2, gbc_btnDef2);
            }
            {
                this.btnDef3 = new JButton("Def3");
                this.btnDef3.setEnabled(false);
                GridBagConstraints gbc_btnDef3 = new GridBagConstraints();
                gbc_btnDef3.insets = new Insets(5, 0, 0, 5);
                gbc_btnDef3.gridx = 2;
                gbc_btnDef3.gridy = 0;
                buttonPane.add(this.btnDef3, gbc_btnDef3);
            }
            {
                this.btnDef4 = new JButton("Def4");
                this.btnDef4.setEnabled(false);
                GridBagConstraints gbc_btnDef4 = new GridBagConstraints();
                gbc_btnDef4.insets = new Insets(5, 0, 0, 0);
                gbc_btnDef4.gridx = 3;
                gbc_btnDef4.gridy = 0;
                buttonPane.add(this.btnDef4, gbc_btnDef4);
            }
            {
                this.btnDef5 = new JButton("Def5");
                this.btnDef5.setEnabled(false);
                GridBagConstraints gbc_btnDef5 = new GridBagConstraints();
                gbc_btnDef5.insets = new Insets(5, 5, 0, 5);
                gbc_btnDef5.gridx = 4;
                gbc_btnDef5.gridy = 0;
                buttonPane.add(this.btnDef5, gbc_btnDef5);
            }
            {
                lblCheckValues = new JLabel("");
                lblCheckValues.setForeground(Color.RED);
                GridBagConstraints gbc_lblCheckValues = new GridBagConstraints();
                gbc_lblCheckValues.gridwidth = 4;
                gbc_lblCheckValues.insets = new Insets(5, 5, 0, 0);
                gbc_lblCheckValues.gridx = 0;
                gbc_lblCheckValues.gridy = 1;
                buttonPane.add(lblCheckValues, gbc_lblCheckValues);
            }
            JButton okButton = new JButton("ok");
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
            gbc_okButton.gridx = 4;
            gbc_okButton.gridy = 1;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
        }

        // this.setIconImage(new
        // ImageIcon(Param_dialog.class.getResource("/img/icona.png")).getImage());

        // Llegim els parametres de la imatge
        this.getParameters();

    }

    protected void do_btnDef1_actionPerformed(ActionEvent e) {
        this.txtDistOD.setText("150.324");
        this.txtPicSizeX.setText("0.1024");
        this.txtPicSizeY.setText("0.1024");
        this.txtCentrX.setText("995.81");
        this.txtCentrY.setText("1038.76");
        this.txtWave.setText("0.7378");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        boolean ok = this.setParameters();
        if (ok) {
            this.dispose();
        } else {
            lblCheckValues.setForeground(col[counter % 3]);
            lblCheckValues.setText("check values!");
            counter = counter + 1;
        }

    }

    private void getParameters() {
        this.patt2D.getDistMD();
        this.txtDistOD.setText(Float.toString(this.patt2D.getDistMD()));
        this.txtPicSizeX.setText(Float.toString(this.patt2D.getPixSx()));
        this.txtPicSizeY.setText(Float.toString(this.patt2D.getPixSy()));
        this.txtCentrX.setText(Float.toString(this.patt2D.getCentrX()));
        this.txtCentrY.setText(Float.toString(this.patt2D.getCentrY()));
        this.txtWave.setText(Float.toString(this.patt2D.getWavel()));
        this.counter = 0;
    }

    // true=tot correcte
    private boolean setParameters() {
        try {
            this.patt2D.setDistMD(Float.parseFloat(this.txtDistOD.getText()));
            this.patt2D.setPixSx(Float.parseFloat(this.txtPicSizeX.getText()));
            this.patt2D.setPixSy(Float.parseFloat(this.txtPicSizeY.getText()));
            this.patt2D.setCentrX(Float.parseFloat(this.txtCentrX.getText()));
            this.patt2D.setCentrY(Float.parseFloat(this.txtCentrY.getText()));
            this.patt2D.setWavel(Float.parseFloat(this.txtWave.getText()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    protected void do_btnDef2_actionPerformed(ActionEvent arg0) {
        this.txtDistOD.setText("225.389");
        this.txtPicSizeX.setText("0.079");
        this.txtPicSizeY.setText("0.079");
        this.txtCentrX.setText("1029.341");
        this.txtCentrY.setText("1031.164");
        this.txtWave.setText("0.4243");
    }
}
