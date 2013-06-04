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

public class About_dialog extends JDialog {

    private static final long serialVersionUID = 5573831371448966498L;

    private JButton btnUsersGuide;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblTalplogo;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            About_dialog dialog = new About_dialog();
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public About_dialog() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(About_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About Plot2D-XRD");
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
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            lblTalplogo = new JLabel("** LOGO **");
            GridBagConstraints gbc_lblTalplogo = new GridBagConstraints();
            gbc_lblTalplogo.insets = new Insets(2, 5, 5, 5);
            gbc_lblTalplogo.gridx = 0;
            gbc_lblTalplogo.gridy = 0;
            contentPanel.add(lblTalplogo, gbc_lblTalplogo);
        }
        {
            JLabel lblAbout = new JLabel(
                    "<html> \r\n<div style=\"text-align:center\"> \r\n<b><font size=+0>Oriol Vallcorba, Jordi Rius, Carlos Frontera & Carles Miravitlles</font></b>\r\n<br>\r\nInstitut de Ci\u00E8ncia de Materials de Barcelona<br>\r\nConsejo Superior de Investigaciones Cient\u00EDficas<br>\r\n08193 Bellaterra, Catalunya, Spain<br>\r\n<br>\r\nFor comments/errors/suggestions, please contact to: <strong>ovallcorba@icmab.es</strong><br>\r\n</div>\r\n<br>\r\n<div style=\"text-align:justify\"> \r\n<b><font size=+0>Conditions of use</font></b><br>\r\nThis software can be used free of charge for non-commercial academic purposes only. For any other purpose, please contact directly with the authors. Further distribution of this software is not allowed. The permission for using this software expires by end of 2015 and then it must be deleted.<br>\r\nCitation of the reference <u>XXXX</u> would be greatly appreciated when publishing works done with it.\r\n<br><br>\r\n<b><font size=+0>Disclaimer</font></b><br>\r\nThis software is distributed WITHOUT ANY WARRANTY. The authors (or their institutions) have no liabilities in respect of errors in the software, in the documentation and in any consequence of erroneous results or damages arising out of the use or inability to use this software. Use it at your own risk.\r\n<br><font size=-2>&nbsp;<br></font>\r\n<font size=-1>This 2D XRD visualization program is Java&trade; powered </font>\r\n<br><br>\r\n<b><font size=+0>Acknowledgements</font></b><br>\r\nThanks are due the Spanish \u2018Ministerio de Ciencia e Innovaci\u00F3n\u2019 and to the \u2018Generalitat the Catalunya\u2019 for continued financial support.\r\n<br><br>\r\n<font size=-1>\r\n&copy; O.Vallcorba & J.Rius 2013 <br>\r\nInstitut de Ci\u00E8ncia de Materials de Barcelona (CSIC)\r\n</font>\r\n<br>\r\n</div>\r\n</html> ");
            GridBagConstraints gbc_lblAbout = new GridBagConstraints();
            gbc_lblAbout.insets = new Insets(0, 5, 0, 5);
            gbc_lblAbout.fill = GridBagConstraints.HORIZONTAL;
            gbc_lblAbout.gridx = 0;
            gbc_lblAbout.gridy = 1;
            contentPanel.add(lblAbout, gbc_lblAbout);
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 468, 103, 0 };
                gbl_buttonPane.rowHeights = new int[] { 25, 0 };
                gbl_buttonPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
                {
                    this.btnUsersGuide = new JButton("User's Guide");
                    this.btnUsersGuide.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnUsersGuide_actionPerformed(arg0);
                        }
                    });
                    GridBagConstraints gbc_btnUsersGuide = new GridBagConstraints();
                    gbc_btnUsersGuide.anchor = GridBagConstraints.NORTHWEST;
                    gbc_btnUsersGuide.insets = new Insets(5, 5, 5, 5);
                    gbc_btnUsersGuide.gridx = 0;
                    gbc_btnUsersGuide.gridy = 0;
                    buttonPane.add(this.btnUsersGuide, gbc_btnUsersGuide);
                }
            }
            JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.anchor = GridBagConstraints.NORTHEAST;
            gbc_okButton.gridx = 1;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
        }

        // posem el logo escalat
        Image img = Toolkit.getDefaultToolkit().getImage(About_dialog.class.getResource("/img/Icona.png"));
        Image newimg = img.getScaledInstance(-100, 64, java.awt.Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(newimg);
        lblTalplogo.setText("");
        lblTalplogo.setIcon(logo);

        // this.setIconImage(new
        // ImageIcon(getClass().getResource("/img/icona.png")).getImage());
    }

    protected void do_btnUsersGuide_actionPerformed(ActionEvent arg0) {
        // try{
        // if(Desktop.isDesktopSupported()){ // s'obre amb el programa per
        // defecte
        // Desktop.getDesktop().open(new
        // File(System.getProperty("user.dir")+"\\"+userGuideFile));
        // }else{
        // Process p = new
        // ProcessBuilder("cmd","/c",System.getProperty("user.dir")+"\\"+userGuideFile).start();
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
}
