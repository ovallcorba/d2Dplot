package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;

import vava33.d2dplot.auxi.ImgFileUtils;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import java.awt.Insets;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Subtract_dialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1213037389106923612L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtImage;
    private JTextField txtFactor;
    private JTextField txtImage_1;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(Subtract_dialog.class.getName());

    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        try {
//            Subtract_dialog dialog = new Subtract_dialog();
//            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//            dialog.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Create the dialog.
     */
    public Subtract_dialog() {
        setModal(true);
        setTitle("Subtract Images");
        setBounds(100, 100, 450, 220);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow][]", "[grow][][][]"));
        {
            JLabel lblImageFactorimage = new JLabel("Image1 - factor*Image2");
            lblImageFactorimage.setFont(new Font("Dialog", Font.BOLD, 14));
            contentPanel.add(lblImageFactorimage, "cell 0 0 3 1,alignx center");
        }
        {
            JLabel lblImage = new JLabel("Image1:");
            contentPanel.add(lblImage, "cell 0 1,alignx trailing");
        }
        {
            txtImage = new JTextField();
            contentPanel.add(txtImage, "cell 1 1,growx");
            txtImage.setColumns(10);
        }
        {
            JButton btnSelect = new JButton("...");
            btnSelect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_btnSelect_actionPerformed(e);
                }
            });
            btnSelect.setMargin(new Insets(2, 2, 2, 2));
            contentPanel.add(btnSelect, "cell 2 1");
        }
        {
            JLabel lblFactor = new JLabel("factor:");
            contentPanel.add(lblFactor, "cell 0 2,alignx trailing");
        }
        {
            txtFactor = new JTextField();
            txtFactor.setText("1.00");
            contentPanel.add(txtFactor, "cell 1 2,growx");
            txtFactor.setColumns(10);
        }
        {
            JLabel lblImage_1 = new JLabel("Image2:");
            contentPanel.add(lblImage_1, "cell 0 3,alignx trailing");
        }
        {
            txtImage_1 = new JTextField();
            contentPanel.add(txtImage_1, "cell 1 3,growx");
            txtImage_1.setColumns(10);
        }
        {
            JButton btnSelect_1 = new JButton("...");
            btnSelect_1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_btnSelect_1_actionPerformed(e);
                }
            });
            btnSelect_1.setMargin(new Insets(2, 2, 2, 2));
            contentPanel.add(btnSelect_1, "cell 2 3");
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
        }
    }
    
    public File getImage(){
        File f = null;
        try{
            f = new File(txtImage.getText().trim());
        }catch(Exception e){
            log.warning("Error locating image1 file (check path)");
        }
        return f;
    }
    
    public File getBkgImage(){
        File f = null;
        try{
            f = new File(txtImage_1.getText().trim());
        }catch(Exception e){
            log.warning("Error locating image2 file (check path)");
        }
        return f;
    }
    
    public float getFactor(){
        float fac = 1.0f;
        try{
            fac = Float.parseFloat(txtFactor.getText().trim());
        }catch(Exception e){
            log.warning("Error reading factor (using 1)");
        }
        return fac;
    }    

    protected void do_btnSelect_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File d2File = FileUtils.fchooser(this,new File(D2Dplot_global.getWorkdir()), filt, filt.length-1, false, false);
        if (d2File != null){
            txtImage.setText(d2File.toString());
            D2Dplot_global.setWorkdir(d2File);
        }
    }
    protected void do_btnSelect_1_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File d2File = FileUtils.fchooser(this,new File(D2Dplot_global.getWorkdir()), filt, filt.length-1, false, false);
        if (d2File != null){
            txtImage_1.setText(d2File.toString());
            D2Dplot_global.setWorkdir(d2File);
        }
    }
    protected void do_okButton_actionPerformed(ActionEvent e) {
        this.dispose();
    }
}
