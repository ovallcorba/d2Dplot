package vava33.d2dplot.auxi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.math3.util.FastMath;

import vava33.d2dplot.D2Dplot_global;
import vava33.d2dplot.ExZones_dialog;

import com.vava33.jutils.VavaLogger;

public class ExZ_ArcDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 5338913641078713831L;

    private final JPanel contentPanel = new JPanel();

    private JTextField txtpx;
    private JTextField txtpy;
    private JTextField txthrw;
    private JTextField txthaw;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(ExZ_ArcDialog.class.getName());

    private boolean finishedOK;
    private int ipX=-1;
    private int ipY=-1;
    private int halfRadialWidthPx=-1;
    private int halfAzimAperDeg=-1; 
    ExZones_dialog pare;

    
    /**
     * Create the dialog.
     */
    public ExZ_ArcDialog(ExZones_dialog exzd) {
        this.pare=exzd;
        setTitle("Add arc-shaped mask zone");
        setBounds(100, 100, 344, 191);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
        {
            JLabel lblBSradi = new JLabel("Zone center px X");
            contentPanel.add(lblBSradi, "cell 0 0,alignx trailing");
        }
        {
            txtpx = new JTextField();
            contentPanel.add(txtpx, "cell 1 0,growx");
            txtpx.setColumns(10);
        }
        {
            JLabel lblBsArmPixel = new JLabel("Zone center px Y");
            contentPanel.add(lblBsArmPixel, "cell 0 1,alignx trailing");
        }
        {
            txtpy = new JTextField();
            contentPanel.add(txtpy, "cell 1 1,growx");
            txtpy.setColumns(10);
        }
        {
            JLabel lblBsArmPixel_1 = new JLabel("Half radial width (px)");
            contentPanel.add(lblBsArmPixel_1, "cell 0 2,alignx trailing");
        }
        {
            txthrw = new JTextField();
            contentPanel.add(txthrw, "cell 1 2,growx");
            txthrw.setColumns(10);
        }
        {
            JLabel lblBsArmWidth = new JLabel("Half azim aperture (deg)");
            contentPanel.add(lblBsArmWidth, "cell 0 3,alignx trailing");
        }
        {
            txthaw = new JTextField();
            contentPanel.add(txthaw, "cell 1 3,growx");
            txthaw.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_cancelButton_actionPerformed(e);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    
    private boolean getInfoFromFields(){
        boolean allReaded = true;
        try{
            setHalfRadialWidthPx(FastMath.round(Float.parseFloat(txthrw.getText())));    
        }catch(Exception e){
            log.info("Could not read half radial width (pixels)");
            allReaded = false;
        }
        try{
            setHalfAzimAperDeg(FastMath.round(Float.parseFloat(txthaw.getText())));  
        }catch(Exception e){
            log.info("Could not read half azimuthal aperture (degrees)");
            allReaded = false;
        }
        try{
            ipX = FastMath.round(Float.parseFloat(txtpx.getText()));  
        }catch(Exception e){
            log.info("Could not read pixel X");
            allReaded = false;
        }
        try{
            ipY = FastMath.round(Float.parseFloat(txtpy.getText()));  
        }catch(Exception e){
            log.info("Could not read pixel Y");
            allReaded = false;
        }
        return allReaded;
    }

    public int getIpX() {
        return ipX;
    }


    public void setIpX(int ipX) {
        this.ipX = ipX;
    }


    public int getIpY() {
        return ipY;
    }


    public void setIpY(int ipY) {
        this.ipY = ipY;
    }


    public int getHalfRadialWidthPx() {
        return halfRadialWidthPx;
    }


    public void setHalfRadialWidthPx(int halfRadialWidthPx) {
        this.halfRadialWidthPx = halfRadialWidthPx;
    }


    public int getHalfAzimAperDeg() {
        return halfAzimAperDeg;
    }


    public void setHalfAzimAperDeg(int halfAzimAperDeg) {
        this.halfAzimAperDeg = halfAzimAperDeg;
    }


    public boolean isFinishedOK() {
        return finishedOK;
    }


    public void setFinishedOK(boolean finishedOK) {
        this.finishedOK = finishedOK;
    }


    protected void do_okButton_actionPerformed(ActionEvent evt) {
        finishedOK = getInfoFromFields();
        if (finishedOK)pare.applyArcZoneParameters(ipX, ipY, halfRadialWidthPx, halfAzimAperDeg);
        this.dispose();
    }
    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        finishedOK = false;
        this.dispose();
    }

}
