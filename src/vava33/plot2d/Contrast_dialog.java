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
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;

public class Contrast_dialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField txtMinval;
    private JTextField txtMaxval;
    private ImagePanel ip;
    
    private static final int min_default = 0;
    private static final int max_default = 32000;
    private static final int fun_default = 0;
    private JComboBox cbox_fun;
    private JTextField txtCurrentvalue;


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
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
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
            JLabel lblMinValuedef = new JLabel("Min Value");
            GridBagConstraints gbc_lblMinValuedef = new GridBagConstraints();
            gbc_lblMinValuedef.anchor = GridBagConstraints.EAST;
            gbc_lblMinValuedef.insets = new Insets(0, 0, 5, 5);
            gbc_lblMinValuedef.gridx = 1;
            gbc_lblMinValuedef.gridy = 1;
            contentPanel.add(lblMinValuedef, gbc_lblMinValuedef);
        }
        {
            this.txtMinval = new JTextField();
            txtMinval.setBackground(Color.WHITE);
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
            JLabel lblMaxValuedef = new JLabel("Max Value");
            GridBagConstraints gbc_lblMaxValuedef = new GridBagConstraints();
            gbc_lblMaxValuedef.anchor = GridBagConstraints.EAST;
            gbc_lblMaxValuedef.insets = new Insets(0, 0, 5, 5);
            gbc_lblMaxValuedef.gridx = 1;
            gbc_lblMaxValuedef.gridy = 2;
            contentPanel.add(lblMaxValuedef, gbc_lblMaxValuedef);
        }
        {
            this.txtMaxval = new JTextField();
            txtMaxval.setBackground(Color.WHITE);
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
        	JLabel lblCurrentValue = new JLabel("Current Value");
        	GridBagConstraints gbc_lblCurrentValue = new GridBagConstraints();
        	gbc_lblCurrentValue.anchor = GridBagConstraints.EAST;
        	gbc_lblCurrentValue.insets = new Insets(0, 0, 5, 5);
        	gbc_lblCurrentValue.gridx = 1;
        	gbc_lblCurrentValue.gridy = 3;
        	contentPanel.add(lblCurrentValue, gbc_lblCurrentValue);
        }
        {
        	txtCurrentvalue = new JTextField();
        	txtCurrentvalue.setEditable(false);
        	GridBagConstraints gbc_txtCurrentvalue = new GridBagConstraints();
        	gbc_txtCurrentvalue.insets = new Insets(0, 0, 5, 5);
        	gbc_txtCurrentvalue.fill = GridBagConstraints.HORIZONTAL;
        	gbc_txtCurrentvalue.gridx = 2;
        	gbc_txtCurrentvalue.gridy = 3;
        	contentPanel.add(txtCurrentvalue, gbc_txtCurrentvalue);
        	txtCurrentvalue.setColumns(10);
        }
        {
        	JButton btnSetAsMax = new JButton("Set as Max");
        	btnSetAsMax.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			do_btnSetAsMax_actionPerformed(e);
        		}
        	});
        	GridBagConstraints gbc_btnSetAsMax = new GridBagConstraints();
        	gbc_btnSetAsMax.insets = new Insets(0, 0, 5, 0);
        	gbc_btnSetAsMax.gridx = 3;
        	gbc_btnSetAsMax.gridy = 3;
        	contentPanel.add(btnSetAsMax, gbc_btnSetAsMax);
        }
        {
        	JLabel lblFunction = new JLabel("Function");
        	GridBagConstraints gbc_lblFunction = new GridBagConstraints();
        	gbc_lblFunction.anchor = GridBagConstraints.EAST;
        	gbc_lblFunction.insets = new Insets(0, 0, 0, 5);
        	gbc_lblFunction.gridx = 1;
        	gbc_lblFunction.gridy = 4;
        	contentPanel.add(lblFunction, gbc_lblFunction);
        }
        {
        	cbox_fun = new JComboBox();
        	cbox_fun.setModel(new DefaultComboBoxModel(new String[] {"linear", "quadratic (+)", "quadratic (-)"}));
        	GridBagConstraints gbc_cbox_fun = new GridBagConstraints();
        	gbc_cbox_fun.insets = new Insets(0, 0, 0, 5);
        	gbc_cbox_fun.fill = GridBagConstraints.HORIZONTAL;
        	gbc_cbox_fun.gridx = 2;
        	gbc_cbox_fun.gridy = 4;
        	contentPanel.add(cbox_fun, gbc_cbox_fun);
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
        txtMinval.setText(String.valueOf(ip.getSlider_contrast().getMinimum()));
        txtMaxval.setText(String.valueOf(ip.getSlider_contrast().getMaximum()));
        cbox_fun.setSelectedIndex(ImagePanel.getContrast_fun());
        txtCurrentvalue.setText(Integer.toString(ip.getSlider_contrast().getValue()));
    }

    protected void do_cancelButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
    protected void do_okButton_actionPerformed(ActionEvent e) {  
        int minI=min_default;
        int maxI=max_default;
        int fun=fun_default;
    	try{
            minI=Integer.parseInt(txtMinval.getText());
        }catch(Exception ex){
            ex.printStackTrace();
            minI=min_default;
        }
        try{
            maxI=Integer.parseInt(txtMaxval.getText());
        }catch(Exception ex){
            ex.printStackTrace();
            maxI=max_default;
        }
        fun=cbox_fun.getSelectedIndex();
        ImagePanel.setContrast_fun(fun);
        ip.contrast_slider_properties(minI, maxI);
        ip.pintaImatge();
        ip.getPanelImatge().repaint();
        this.dispose();
    }
	protected void do_btnSetAsMax_actionPerformed(ActionEvent e) {
		this.txtMaxval.setText(this.txtCurrentvalue.getText());
	}
}
