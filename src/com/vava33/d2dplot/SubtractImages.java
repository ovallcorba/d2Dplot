package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class SubtractImages {

    private final JDialog subDialog;
    private final JPanel contentPanel;
    private JTextField txtImage;
    private JTextField txtFactor;
    private JTextField txtImage_1;
    private boolean cancelpressed;
    private static final String className = "Subtract";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

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
    public SubtractImages(JFrame parent) {
        this.contentPanel = new JPanel();
        this.subDialog = new JDialog(parent, "Subtract Images", true);
        this.subDialog.setBounds(100, 100, 450, 220);
        this.subDialog.getContentPane().setLayout(new BorderLayout());
        this.subDialog.setLocationRelativeTo(parent);
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.subDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[][grow][]", "[grow][][][]"));
        {
            final JLabel lblImageFactorimage = new JLabel("Image1 - factor*Image2");
            lblImageFactorimage.setFont(new Font("Dialog", Font.BOLD, 14));
            this.contentPanel.add(lblImageFactorimage, "cell 0 0 3 1,alignx center");
        }
        {
            final JLabel lblImage = new JLabel("Image1:");
            this.contentPanel.add(lblImage, "cell 0 1,alignx trailing");
        }
        {
            this.txtImage = new JTextField();
            this.contentPanel.add(this.txtImage, "cell 1 1,growx");
            this.txtImage.setColumns(10);
        }
        {
            final JButton btnSelect = new JButton("...");
            btnSelect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SubtractImages.this.do_btnSelect_actionPerformed(e);
                }
            });
            btnSelect.setMargin(new Insets(2, 2, 2, 2));
            this.contentPanel.add(btnSelect, "cell 2 1");
        }
        {
            final JLabel lblFactor = new JLabel("factor:");
            this.contentPanel.add(lblFactor, "cell 0 2,alignx trailing");
        }
        {
            this.txtFactor = new JTextField();
            this.txtFactor.setText("1.00");
            this.contentPanel.add(this.txtFactor, "cell 1 2,growx");
            this.txtFactor.setColumns(10);
        }
        {
            final JLabel lblImage_1 = new JLabel("Image2:");
            this.contentPanel.add(lblImage_1, "cell 0 3,alignx trailing");
        }
        {
            this.txtImage_1 = new JTextField();
            this.contentPanel.add(this.txtImage_1, "cell 1 3,growx");
            this.txtImage_1.setColumns(10);
        }
        {
            final JButton btnSelect_1 = new JButton("...");
            btnSelect_1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SubtractImages.this.do_btnSelect_1_actionPerformed(e);
                }
            });
            btnSelect_1.setMargin(new Insets(2, 2, 2, 2));
            this.contentPanel.add(btnSelect_1, "cell 2 3");
        }
        {
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            this.subDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                final JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SubtractImages.this.do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                this.subDialog.getRootPane().setDefaultButton(okButton);
            }
            {
                final JButton btnNewButton = new JButton("Cancel");
                btnNewButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SubtractImages.this.do_btnNewButton_actionPerformed(e);
                    }
                });
                buttonPane.add(btnNewButton);
            }
        }
        this.cancelpressed = true;
    }

    public File getImage() {
        File f = null;
        try {
            f = new File(this.txtImage.getText().trim());
            if (!f.exists()) {
                log.warning("Error locating image1 file (check path)");
                return null;
            }
        } catch (final Exception e) {
            log.warning("Error reading file path");
        }
        return f;
    }

    public File getBkgImage() {
        File f = null;
        try {
            f = new File(this.txtImage_1.getText().trim());
            if (!f.exists()) {
                log.warning("Error locating image2 file (check path)");
                return null;
            }
        } catch (final Exception e) {
            log.warning("Error reading file path");
        }
        return f;
    }

    public float getFactor() {
        float fac = 1.0f;
        try {
            fac = Float.parseFloat(this.txtFactor.getText().trim());
        } catch (final Exception e) {
            log.warning("Error reading factor (using 1)");
        }
        return fac;
    }

    protected void do_btnSelect_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File d2File = FileUtils.fchooserOpen(this.subDialog, new File(D2Dplot_global.getWorkdir()), filt, 0);
        if (d2File != null) {
            this.txtImage.setText(d2File.toString());
            D2Dplot_global.setWorkdir(d2File);
        }
    }

    protected void do_btnSelect_1_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File d2File = FileUtils.fchooserOpen(this.subDialog, new File(D2Dplot_global.getWorkdir()), filt, 0);
        if (d2File != null) {
            this.txtImage_1.setText(d2File.toString());
            D2Dplot_global.setWorkdir(d2File);
        }
    }

    protected void do_okButton_actionPerformed(ActionEvent e) {
        this.cancelpressed = false;
        this.subDialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.subDialog.setVisible(vis);
    }

    protected void do_btnNewButton_actionPerformed(ActionEvent e) {
        this.cancelpressed = true;
        this.subDialog.dispose();
    }

    protected boolean isCancel() {
        return this.cancelpressed;
    }
}
