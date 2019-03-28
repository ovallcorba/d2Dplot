package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class Help {

    private final JPanel contentPanel;
    private JDialog helpDialog;
    private JLabel lblTalplogo;
    private JLabel lbloriolVallcorbaJordi;

    /**
     * Create the dialog.
     * 
     * @wbp.parser.constructor
     */
    public Help(JFrame parent, String title, String text) {
        this.contentPanel = new JPanel();
        this.helpDialog = new JDialog(parent, title, true);
        this.helpDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        this.helpDialog.setAlwaysOnTop(true);
        this.helpDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(Help.class.getResource("/img/Icona.png")));
        this.helpDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = 660;
        final int height = 730;
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.helpDialog.setBounds(x, y, 570, 630);
        this.helpDialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.helpDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        {
            final JPanel buttonPane = new JPanel();
            this.helpDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            final JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Help.this.do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            buttonPane.add(okButton);
            this.helpDialog.getRootPane().setDefaultButton(okButton);
        }

        // posem el logo escalat
        final Image img = Toolkit.getDefaultToolkit().getImage(Help.class.getResource("/img/Icona.png"));
        final Image newimg = img.getScaledInstance(-100, 64, java.awt.Image.SCALE_SMOOTH);
        final ImageIcon logo = new ImageIcon(newimg);
        this.contentPanel.setLayout(new MigLayout("", "[][grow]", "[grow][]"));
        {
            final JLabel lbltext = new JLabel(text);
            this.contentPanel.add(lbltext, "cell 0 0 2 1,growx,aligny top");
        }
        {
            this.lblTalplogo = new JLabel("** LOGO **");
            this.contentPanel.add(this.lblTalplogo, "cell 0 1,alignx right,aligny center");
        }
        this.lblTalplogo.setText("");
        this.lblTalplogo.setIcon(logo);
        {
            this.lbloriolVallcorbaJordi = new JLabel(
                    "<html>\r\n<div style=\"text-align:left\"> \r\n<b>Oriol Vallcorba</b><br>\r\nALBA Synchrotron Light Source - CELLS (www.cells.es)<br>\r\nFor comments/errors/suggestions, please contact to: <strong>ovallcorba@cells.es</strong><br>\r\n</div>\r\n</html>");
            this.contentPanel.add(this.lbloriolVallcorbaJordi, "cell 1 1,growx,aligny center");
        }

    }

    public Help(JFrame parent, String title, String text, boolean noSignature) {
        this(parent, title, text);
        if (noSignature) {
            this.lbloriolVallcorbaJordi.setVisible(false);
            this.lblTalplogo.setVisible(false);
        }
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.helpDialog.dispose();
    }

    /**
     * @return the helpDialog
     */
    public JDialog getHelpDialog() {
        return this.helpDialog;
    }

    /**
     * @param helpDialog the helpDialog to set
     */
    public void setHelpDialog(JDialog helpDialog) {
        this.helpDialog = helpDialog;
    }
}
