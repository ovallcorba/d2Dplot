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

public class ExZ_BSdiag {

    private JDialog exzBSdialog;
    private final JPanel contentPanel;
    private JTextField txtRadi;
    private JTextField txtBsarmx;
    private JTextField txtBsarmy;
    private JTextField txtBsarmw;
    private static final String className = "ExZ_BSdialog";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private boolean finishedOK;
    private int radiBS = -1;
    private int ampladaArm = -1;
    private int ipX = -1;
    private int ipY = -1;
    ExZones pare;

    /**
     * Create the dialog.
     */
    public ExZ_BSdiag(JFrame parent, ExZones exzd) {
        this.pare = exzd;
        this.contentPanel = new JPanel();
        this.exzBSdialog = new JDialog(parent, "Add BeamStop Mask", false);
        this.exzBSdialog.setBounds(100, 100, 344, 191);
        this.exzBSdialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.exzBSdialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
        {
            final JLabel lblBSradi = new JLabel("Beamstop Radius (px)");
            this.contentPanel.add(lblBSradi, "cell 0 0,alignx trailing");
        }
        {
            this.txtRadi = new JTextField();
            this.contentPanel.add(this.txtRadi, "cell 1 0,growx");
            this.txtRadi.setColumns(10);
        }
        {
            final JLabel lblBsArmPixel = new JLabel("BS arm pixel X");
            this.contentPanel.add(lblBsArmPixel, "cell 0 1,alignx trailing");
        }
        {
            this.txtBsarmx = new JTextField();
            this.contentPanel.add(this.txtBsarmx, "cell 1 1,growx");
            this.txtBsarmx.setColumns(10);
        }
        {
            final JLabel lblBsArmPixel_1 = new JLabel("BS arm pixel Y");
            this.contentPanel.add(lblBsArmPixel_1, "cell 0 2,alignx trailing");
        }
        {
            this.txtBsarmy = new JTextField();
            this.contentPanel.add(this.txtBsarmy, "cell 1 2,growx");
            this.txtBsarmy.setColumns(10);
        }
        {
            final JLabel lblBsArmWidth = new JLabel("BS arm width (px)");
            this.contentPanel.add(lblBsArmWidth, "cell 0 3,alignx trailing");
        }
        {
            this.txtBsarmw = new JTextField();
            this.contentPanel.add(this.txtBsarmw, "cell 1 3,growx");
            this.txtBsarmw.setColumns(10);
        }
        {
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            this.exzBSdialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                final JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ExZ_BSdiag.this.do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                this.exzBSdialog.getRootPane().setDefaultButton(okButton);
            }
            {
                final JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ExZ_BSdiag.this.do_cancelButton_actionPerformed(e);
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
            this.radiBS = FastMath.round(Float.parseFloat(this.txtRadi.getText()));
        } catch (final Exception e) {
            log.info("Could not read beamstop central radius (pixels)");
            allReaded = false;
        }
        try {
            this.ampladaArm = FastMath.round(Float.parseFloat(this.txtBsarmw.getText()));
        } catch (final Exception e) {
            log.info("Could not read beamstop arm width (pixels)");
            allReaded = false;
        }
        try {
            this.ipX = FastMath.round(Float.parseFloat(this.txtBsarmx.getText()));
        } catch (final Exception e) {
            log.info("Could not read pixel X");
            allReaded = false;
        }
        try {
            this.ipY = FastMath.round(Float.parseFloat(this.txtBsarmy.getText()));
        } catch (final Exception e) {
            log.info("Could not read pixel Y");
            allReaded = false;
        }
        return allReaded;
    }

    public int getRadiBS() {
        return this.radiBS;
    }

    public void setRadiBS(int radiBS) {
        this.radiBS = radiBS;
    }

    public int getAmpladaArm() {
        return this.ampladaArm;
    }

    public void setAmpladaArm(int ampladaArm) {
        this.ampladaArm = ampladaArm;
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

    public boolean isFinishedOK() {
        return this.finishedOK;
    }

    public void setFinishedOK(boolean finishedOK) {
        this.finishedOK = finishedOK;
    }

    protected void do_okButton_actionPerformed(ActionEvent evt) {
        this.finishedOK = this.getInfoFromFields();
        if (this.finishedOK)
            this.pare.applyBSparameters(this.radiBS, this.ampladaArm, this.ipX, this.ipY);
        this.exzBSdialog.dispose();
    }

    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        this.finishedOK = false;
        this.exzBSdialog.dispose();
    }

    /**
     * @return the exzBSdialog
     */
    public JDialog getExzBSdialog() {
        return this.exzBSdialog;
    }

    /**
     * @param exzBSdialog the exzBSdialog to set
     */
    public void setExzBSdialog(JDialog exzBSdialog) {
        this.exzBSdialog = exzBSdialog;
    }
}
