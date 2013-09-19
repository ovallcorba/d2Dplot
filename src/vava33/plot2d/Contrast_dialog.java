package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Contrast_dialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField txtMinval;
    private JTextField txtMaxval;
    private JTextField txtContrastfact;
    private ImagePanel ip;
    
    private static final float factor_default = 3.0f;
    private static final int min_default = 0;
    private static final int max_default = 30;
    

    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        try {
//            Contrast_dialog dialog = new Contrast_dialog();
//            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//            dialog.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Create the dialog.
     */
    public Contrast_dialog(ImagePanel ip) {
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.ip=ip;
        setIconImage(Toolkit.getDefaultToolkit().getImage(Calib_dialog.class.getResource("/img/Icona.png")));
        setTitle("Contrast Slide Parameters");
        setBounds(100, 100, 320, 290);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblContrastparaminfo = new JLabel("<html>Here you can define the parameters for the slide bar that control the contrast of the image. Modify them only in the case you do not see the image properly, otherwise leave the default parameters.\r\n</html>");
            GridBagConstraints gbc_lblContrastparaminfo = new GridBagConstraints();
            gbc_lblContrastparaminfo.fill = GridBagConstraints.BOTH;
            gbc_lblContrastparaminfo.gridwidth = 4;
            gbc_lblContrastparaminfo.insets = new Insets(0, 0, 5, 0);
            gbc_lblContrastparaminfo.gridx = 0;
            gbc_lblContrastparaminfo.gridy = 0;
            contentPanel.add(lblContrastparaminfo, gbc_lblContrastparaminfo);
        }
        {
            JLabel lblMinValuedef = new JLabel("Min Value (def=0)");
            GridBagConstraints gbc_lblMinValuedef = new GridBagConstraints();
            gbc_lblMinValuedef.anchor = GridBagConstraints.EAST;
            gbc_lblMinValuedef.insets = new Insets(0, 0, 5, 5);
            gbc_lblMinValuedef.gridx = 1;
            gbc_lblMinValuedef.gridy = 1;
            contentPanel.add(lblMinValuedef, gbc_lblMinValuedef);
        }
        {
            this.txtMinval = new JTextField();
            this.txtMinval.setText("0");
            GridBagConstraints gbc_txtMinval = new GridBagConstraints();
            gbc_txtMinval.insets = new Insets(0, 0, 5, 5);
            gbc_txtMinval.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtMinval.gridx = 2;
            gbc_txtMinval.gridy = 1;
            contentPanel.add(this.txtMinval, gbc_txtMinval);
            this.txtMinval.setColumns(10);
        }
        {
            JLabel lblMaxValuedef = new JLabel("Max Value (def=30)");
            GridBagConstraints gbc_lblMaxValuedef = new GridBagConstraints();
            gbc_lblMaxValuedef.anchor = GridBagConstraints.EAST;
            gbc_lblMaxValuedef.insets = new Insets(0, 0, 5, 5);
            gbc_lblMaxValuedef.gridx = 1;
            gbc_lblMaxValuedef.gridy = 2;
            contentPanel.add(lblMaxValuedef, gbc_lblMaxValuedef);
        }
        {
            this.txtMaxval = new JTextField();
            this.txtMaxval.setText("30");
            GridBagConstraints gbc_txtMaxval = new GridBagConstraints();
            gbc_txtMaxval.insets = new Insets(0, 0, 5, 5);
            gbc_txtMaxval.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtMaxval.gridx = 2;
            gbc_txtMaxval.gridy = 2;
            contentPanel.add(this.txtMaxval, gbc_txtMaxval);
            this.txtMaxval.setColumns(10);
        }
        {
            JLabel lblFactordef = new JLabel("Factor (def=3)");
            GridBagConstraints gbc_lblFactordef = new GridBagConstraints();
            gbc_lblFactordef.anchor = GridBagConstraints.EAST;
            gbc_lblFactordef.insets = new Insets(0, 0, 0, 5);
            gbc_lblFactordef.gridx = 1;
            gbc_lblFactordef.gridy = 3;
            contentPanel.add(lblFactordef, gbc_lblFactordef);
        }
        {
            this.txtContrastfact = new JTextField();
            this.txtContrastfact.setText("3");
            GridBagConstraints gbc_txtContrastfact = new GridBagConstraints();
            gbc_txtContrastfact.insets = new Insets(0, 0, 0, 5);
            gbc_txtContrastfact.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtContrastfact.gridx = 2;
            gbc_txtContrastfact.gridy = 3;
            contentPanel.add(this.txtContrastfact, gbc_txtContrastfact);
            this.txtContrastfact.setColumns(10);
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
                    public void actionPerformed(ActionEvent arg0) {
                        do_cancelButton_actionPerformed(arg0);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        
        this.loadParam();
    }
    
    private void loadParam(){
        txtContrastfact.setText(String.valueOf(ip.getFactorContrast()));
        txtMinval.setText(String.valueOf(ip.getSlider_contrast().getMinimum()));
        txtMaxval.setText(String.valueOf(ip.getSlider_contrast().getMaximum()));
    }

    protected void do_cancelButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
    protected void do_okButton_actionPerformed(ActionEvent e) {  
        try{
            ip.setFactorContrast(Float.parseFloat(txtContrastfact.getText()));
        }catch(Exception ex){
            ex.printStackTrace();
            ip.setFactorContrast(factor_default);
        }
        try{
            ip.getSlider_contrast().setMinimum(Integer.parseInt(txtMinval.getText()));
        }catch(Exception ex){
            ex.printStackTrace();
            ip.getSlider_contrast().setMinimum(min_default);
        }
        try{
            ip.getSlider_contrast().setMaximum(Integer.parseInt(txtMaxval.getText()));
        }catch(Exception ex){
            ex.printStackTrace();
            ip.getSlider_contrast().setMaximum(max_default);
        }
        ip.pintaImatge();
        ip.getPanelImatge().repaint();
        this.dispose();
    }
}
