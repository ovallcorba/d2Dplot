package vava33.d2dplot.auxi;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTextField;

import vava33.d2dplot.Param_dialog;

import java.awt.Font;

public class ImageTiltRot_diag extends JDialog {

    private static final long serialVersionUID = -4616295467870001148L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtRot;
    private JTextField txtTilt;
    private JLabel lblRot_1;
    private JLabel lblTilt;
    Param_dialog par;
    float rot = 0;
    float tilt = 0;
    
    /**
     * Create the dialog.
     */
    public ImageTiltRot_diag(Param_dialog p) {
        this.par=p;
        setTitle("Tilt/Rot Convention");
        setModal(true);
        setBounds(100, 100, 488, 546);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][][][grow]", "[grow][][][][]"));
        {
            JLabel lblImatge = new JLabel("");
            lblImatge.setIcon(new ImageIcon(ImageTiltRot_diag.class.getResource("/img/tilt_rot_drawing2.png")));
            contentPanel.add(lblImatge, "cell 0 0 4 1,alignx left,aligny top");
        }
        {
            JLabel lblFitdToDdplot = new JLabel("fit2D to d2Dplot conversion:");
            contentPanel.add(lblFitdToDdplot, "cell 0 1 2 1");
        }
        {
            JLabel lblRot = new JLabel("tilt rotation=");
            contentPanel.add(lblRot, "cell 0 2,alignx trailing");
        }
        {
            txtRot = new JTextField();
            contentPanel.add(txtRot, "cell 1 2,growx");
            txtRot.setColumns(10);
        }
        {
            JButton btnConvert = new JButton("convert");
            btnConvert.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    do_btnConvert_actionPerformed(arg0);
                }
            });
            contentPanel.add(btnConvert, "cell 2 2 1 2");
        }
        {
            lblRot_1 = new JLabel("rot=");
            contentPanel.add(lblRot_1, "cell 3 2");
        }
        {
            JLabel lblAngleTilt = new JLabel("angle tilt=");
            contentPanel.add(lblAngleTilt, "cell 0 3,alignx trailing");
        }
        {
            txtTilt = new JTextField();
            contentPanel.add(txtTilt, "cell 1 3,growx");
            txtTilt.setColumns(10);
        }
        {
            lblTilt = new JLabel("tilt=");
            contentPanel.add(lblTilt, "cell 3 3");
        }
        {
            JLabel lblUseWithCaution = new JLabel("Use with caution, maybe you will need to invert tilt sign");
            lblUseWithCaution.setFont(new Font("Dialog", Font.PLAIN, 10));
            contentPanel.add(lblUseWithCaution, "cell 0 4 3 1,alignx center");
        }
        {
            JButton btnSet = new JButton("set as values");
            btnSet.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    do_btnSet_actionPerformed(arg0);
                }
            });
            contentPanel.add(btnSet, "cell 3 4,growx");
        }
    }
    protected void do_btnConvert_actionPerformed(ActionEvent arg0) {
        try{
            this.rot = Float.parseFloat(txtRot.getText());
            this.tilt = Float.parseFloat(txtTilt.getText());
            
            this.rot = (-1)*this.rot -90;
            this.rot = -this.rot;
            this.tilt = -this.tilt;
            
            lblRot_1.setText(String.format("rot= %.3f",this.rot));
            lblTilt.setText(String.format("tilt= %.3f",this.tilt));
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    protected void do_btnSet_actionPerformed(ActionEvent arg0) {
        this.par.setTiltRotFields(this.tilt, this.rot);
    }
}
