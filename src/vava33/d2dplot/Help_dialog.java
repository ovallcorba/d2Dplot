package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JSeparator;

public class Help_dialog extends JDialog {

    private static final long serialVersionUID = 5573831371448966498L;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblTalplogo;
    private JLabel lbloriolVallcorbaJordi;

    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        try {
//            Help_dialog dialog = new Help_dialog();
//            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//            dialog.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Create the dialog.
     */
    public Help_dialog(String title,String text) {
        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Help_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 570, 630);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            GridBagLayout gbl_buttonPane = new GridBagLayout();
            gbl_buttonPane.columnWidths = new int[] {0, 0, 0};
            gbl_buttonPane.rowHeights = new int[] {0, 0};
            gbl_buttonPane.columnWeights = new double[]{0.0, 1.0};
            gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
            buttonPane.setLayout(gbl_buttonPane);
            JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.insets = new Insets(0, 0, 5, 5);
            gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
            gbc_okButton.gridx = 1;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
        }

        // posem el logo escalat
        Image img = Toolkit.getDefaultToolkit().getImage(Help_dialog.class.getResource("/img/Icona.png"));
        Image newimg = img.getScaledInstance(-100, 64, java.awt.Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(newimg);
        {
            JLabel lbltext = new JLabel(text);
            GridBagConstraints gbc_lbltext = new GridBagConstraints();
            gbc_lbltext.anchor = GridBagConstraints.NORTH;
            gbc_lbltext.fill = GridBagConstraints.HORIZONTAL;
            gbc_lbltext.gridwidth = 2;
            gbc_lbltext.insets = new Insets(5, 5, 5, 5);
            gbc_lbltext.gridx = 0;
            gbc_lbltext.gridy = 0;
            contentPanel.add(lbltext, gbc_lbltext);
        }
        {
            JSeparator separator = new JSeparator();
            GridBagConstraints gbc_separator = new GridBagConstraints();
            gbc_separator.gridwidth = 2;
            gbc_separator.fill = GridBagConstraints.HORIZONTAL;
            gbc_separator.insets = new Insets(0, 0, 5, 0);
            gbc_separator.gridx = 0;
            gbc_separator.gridy = 1;
            contentPanel.add(separator, gbc_separator);
        }
        {
            lblTalplogo = new JLabel("** LOGO **");
            GridBagConstraints gbc_lblTalplogo = new GridBagConstraints();
            gbc_lblTalplogo.anchor = GridBagConstraints.EAST;
            gbc_lblTalplogo.insets = new Insets(0, 5, 5, 5);
            gbc_lblTalplogo.gridx = 0;
            gbc_lblTalplogo.gridy = 2;
            contentPanel.add(lblTalplogo, gbc_lblTalplogo);
        }
        lblTalplogo.setText("");
        lblTalplogo.setIcon(logo);
        {
            lbloriolVallcorbaJordi = new JLabel("<html>\r\n<div style=\"text-align:left\"> \r\n<b>Oriol Vallcorba</b><br>\r\nALBA Synchrotron Light Source - CELLS (www.cells.es)<br>\r\nFor comments/errors/suggestions, please contact to: <strong>ovallcorba@cells.es</strong><br>\r\n</div>\r\n</html>");
            GridBagConstraints gbc_lbloriolVallcorbaJordi = new GridBagConstraints();
            gbc_lbloriolVallcorbaJordi.fill = GridBagConstraints.HORIZONTAL;
            gbc_lbloriolVallcorbaJordi.insets = new Insets(0, 0, 5, 0);
            gbc_lbloriolVallcorbaJordi.gridx = 1;
            gbc_lbloriolVallcorbaJordi.gridy = 2;
            contentPanel.add(lbloriolVallcorbaJordi, gbc_lbloriolVallcorbaJordi);
        }
        {
            JSeparator separator = new JSeparator();
            GridBagConstraints gbc_separator = new GridBagConstraints();
            gbc_separator.fill = GridBagConstraints.HORIZONTAL;
            gbc_separator.gridwidth = 2;
            gbc_separator.gridx = 0;
            gbc_separator.gridy = 3;
            contentPanel.add(separator, gbc_separator);
        }

    }

    public Help_dialog(String title,String text,boolean noSignature) {
        this(title,text);
        if (noSignature){
            lbloriolVallcorbaJordi.setVisible(false);
            lblTalplogo.setVisible(false);
        }
    }
    
    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
}
