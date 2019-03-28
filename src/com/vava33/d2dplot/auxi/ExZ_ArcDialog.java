package com.vava33.d2dplot.auxi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.ExZones;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class ExZ_ArcDialog {

    private JDialog exzArcDialog;
    private final JPanel contentPanel;
    private JTextField txtpx;
    private JTextField txtpy;
    private JTextField txthrw;
    private JTextField txthaw;
    private static final String className = "ExZ_ArcDialog";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private boolean finishedOK;
    private int ipX = -1;
    private int ipY = -1;
    private int halfRadialWidthPx = -1;
    private int halfAzimAperDeg = -1;
    ExZones pare;

    /**
     * Create the dialog.
     */
    public ExZ_ArcDialog(JFrame parent, ExZones exzd) {
        this.pare = exzd;
        this.contentPanel = new JPanel();
        this.exzArcDialog = new JDialog(parent, "Add arc-shaped mask zone", false);
        this.exzArcDialog.setBounds(100, 100, 344, 191);
        this.exzArcDialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.exzArcDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
        {
            final JLabel lblBSradi = new JLabel("Zone center px X");
            this.contentPanel.add(lblBSradi, "cell 0 0,alignx trailing");
        }
        {
            this.txtpx = new JTextField();
            this.contentPanel.add(this.txtpx, "cell 1 0,growx");
            this.txtpx.setColumns(10);
        }
        {
            final JLabel lblBsArmPixel = new JLabel("Zone center px Y");
            this.contentPanel.add(lblBsArmPixel, "cell 0 1,alignx trailing");
        }
        {
            this.txtpy = new JTextField();
            this.contentPanel.add(this.txtpy, "cell 1 1,growx");
            this.txtpy.setColumns(10);
        }
        {
            final JLabel lblBsArmPixel_1 = new JLabel("Half radial width (px)");
            this.contentPanel.add(lblBsArmPixel_1, "cell 0 2,alignx trailing");
        }
        {
            this.txthrw = new JTextField();
            this.contentPanel.add(this.txthrw, "cell 1 2,growx");
            this.txthrw.setColumns(10);
        }
        {
            final JLabel lblBsArmWidth = new JLabel("Half azim aperture (deg)");
            this.contentPanel.add(lblBsArmWidth, "cell 0 3,alignx trailing");
        }
        {
            this.txthaw = new JTextField();
            this.contentPanel.add(this.txthaw, "cell 1 3,growx");
            this.txthaw.setColumns(10);
        }
        {
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            this.exzArcDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                final JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ExZ_ArcDialog.this.do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                this.exzArcDialog.getRootPane().setDefaultButton(okButton);
            }
            {
                final JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ExZ_ArcDialog.this.do_cancelButton_actionPerformed(e);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    private boolean getInfoFromFields() {
        boolean allReaded = true;
        try {
            this.setHalfRadialWidthPx(FastMath.round(Float.parseFloat(this.txthrw.getText())));
        } catch (final Exception e) {
            log.info("Could not read half radial width (pixels)");
            allReaded = false;
        }
        try {
            this.setHalfAzimAperDeg(FastMath.round(Float.parseFloat(this.txthaw.getText())));
        } catch (final Exception e) {
            log.info("Could not read half azimuthal aperture (degrees)");
            allReaded = false;
        }
        try {
            this.ipX = FastMath.round(Float.parseFloat(this.txtpx.getText()));
        } catch (final Exception e) {
            log.info("Could not read pixel X");
            allReaded = false;
        }
        try {
            this.ipY = FastMath.round(Float.parseFloat(this.txtpy.getText()));
        } catch (final Exception e) {
            log.info("Could not read pixel Y");
            allReaded = false;
        }
        return allReaded;
    }

    public int getIpX() {
        return this.ipX;
    }

    public void setIpX(int ipX) {
        this.ipX = ipX;
    }

    public int getIpY() {
        return this.ipY;
    }

    public void setIpY(int ipY) {
        this.ipY = ipY;
    }

    public int getHalfRadialWidthPx() {
        return this.halfRadialWidthPx;
    }

    public void setHalfRadialWidthPx(int halfRadialWidthPx) {
        this.halfRadialWidthPx = halfRadialWidthPx;
    }

    public int getHalfAzimAperDeg() {
        return this.halfAzimAperDeg;
    }

    public void setHalfAzimAperDeg(int halfAzimAperDeg) {
        this.halfAzimAperDeg = halfAzimAperDeg;
    }

    public boolean isFinishedOK() {
        return this.finishedOK;
    }

    public void setFinishedOK(boolean finishedOK) {
        this.finishedOK = finishedOK;
    }

    protected void do_okButton_actionPerformed(ActionEvent evt) {
        this.finishedOK = this.getInfoFromFields();
        if (this.finishedOK)
            this.pare.applyArcZoneParameters(this.ipX, this.ipY, this.halfRadialWidthPx, this.halfAzimAperDeg);
        this.exzArcDialog.dispose();
    }

    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        this.finishedOK = false;
        this.exzArcDialog.dispose();
    }

    /**
     * @return the exzArcDialog
     */
    public JDialog getExzArcDialog() {
        return this.exzArcDialog;
    }

    /**
     * @param exzArcDialog the exzArcDialog to set
     */
    public void setExzArcDialog(JDialog exzArcDialog) {
        this.exzArcDialog = exzArcDialog;
    }

}
