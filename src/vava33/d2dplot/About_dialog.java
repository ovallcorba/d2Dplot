package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.io.File;
import java.io.IOException;

import net.miginfocom.swing.MigLayout;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import com.vava33.jutils.FileUtils;

public class About_dialog extends JDialog {

    private static final long serialVersionUID = 5573831371448966498L;

    private JButton btnUsersGuide;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblTalplogo;
    private JEditorPane textPane;
    private JScrollPane scrollPane;
    private JLabel lblLogoalba;

    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        try {
//            About_dialog dialog = new About_dialog();
//            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//            dialog.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Create the dialog.
     */
    public About_dialog() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(About_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About d2Dplot");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 680;
        int height = 780;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 680, 780);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow]", "[64px][grow][]"));
        {
            lblTalplogo = new JLabel("** LOGO **");
            contentPanel.add(lblTalplogo, "flowx,cell 0 0,alignx center,aligny center");
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                {
                    this.btnUsersGuide = new JButton("User's Guide");
                    this.btnUsersGuide.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnUsersGuide_actionPerformed(arg0);
                        }
                    });
                    buttonPane.setLayout(new MigLayout("", "[][grow]", "[]"));
                    buttonPane.add(this.btnUsersGuide, "cell 0 0,alignx left,aligny top");
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
            buttonPane.add(okButton, "cell 1 0,alignx right,aligny top");
            getRootPane().setDefaultButton(okButton);
        }

        // posem el logo escalat
        Image img = Toolkit.getDefaultToolkit().getImage(About_dialog.class.getResource("/img/Icona.png"));
        Image newimg = img.getScaledInstance(-100, 64, java.awt.Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(newimg);
        lblTalplogo.setText("");
        lblTalplogo.setIcon(logo);
        {
            scrollPane = new JScrollPane();
            scrollPane.setViewportBorder(null);
            scrollPane.setOpaque(false);
            contentPanel.add(scrollPane, "cell 0 1,grow");
            {
                textPane = new JEditorPane();
                textPane.setOpaque(false);
                scrollPane.setViewportView(textPane);
            }
        }

        // this.setIconImage(new
        // ImageIcon(getClass().getResource("/img/icona.png")).getImage());
        
        //llegim el fitxer html per poblar la label:
        
        textPane.setContentType("text/html");
        textPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        textPane.setEditable(false);
        
        textPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if(Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception e1) {
                            if(D2Dplot_global.isDebug())e1.printStackTrace();
                        }
                    }
                }
            }
        });
        {
            lblLogoalba = new JLabel("");
            lblLogoalba.setIcon(new ImageIcon(About_dialog.class.getResource("/img/ALBALogo.png")));
            contentPanel.add(lblLogoalba, "cell 0 2,alignx center");
        }
        java.net.URL aboutURL = About_dialog.class.getResource("/img/about.html");
        if (aboutURL != null) {
            try {
                textPane.setPage(aboutURL);
            } catch (IOException e) {
                System.err.println("Attempted to read a bad URL: " + aboutURL);
            }
        } else {
            System.err.println("Couldn't find file: " + aboutURL);
        }
        
        scrollPane.getViewport().setOpaque(false);
       
    }

    protected void do_btnUsersGuide_actionPerformed(ActionEvent arg0) {
        try{
            if(Desktop.isDesktopSupported()){ // s'obre amb el programa per defecte
                Desktop.getDesktop().open(new File(D2Dplot_global.usersGuidePath));
                return;
            }else{
                if(FileUtils.confirmDialog(FileUtils.getOS(),"win")){
                    new ProcessBuilder("cmd","/c",D2Dplot_global.usersGuidePath).start();  
                }else{
                    throw new Exception(); 
                }
                return;
            }
        } catch (Exception e) {
            if(D2Dplot_global.isDebug())e.printStackTrace();
        }
        JOptionPane.showMessageDialog(this,
                "Sorry, unable to open user's guide with default pdf viewer. \n"
                + "Please open it manually from the program folder",
                "D2Dplot User's Guide",
                JOptionPane.PLAIN_MESSAGE);
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
}
