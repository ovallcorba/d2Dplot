package com.vava33.d2dplot.tts;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.TTS_frame;
import com.vava33.jutils.FileUtils;

import net.miginfocom.swing.MigLayout;

public class AboutTTS_dialog extends JDialog {

    private static final long serialVersionUID = -1952038531703806964L;
    private JButton btnUsersGuide;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblTalplogo;
    private JEditorPane textPane;
    private JScrollPane scrollPane;

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
    public AboutTTS_dialog() {
        setModal(true);
        this.setIconImage(new ImageIcon(getClass().getResource("/img/tts_icon120x120.png")).getImage());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About TTS software");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 680;
        int height = 780;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 680, 780);
        this.setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow]", "[64px][grow]"));
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
        Image img = Toolkit.getDefaultToolkit().getImage(AboutTTS_dialog.class.getResource("/img/tts_splash_760x120.png"));
        Image newimg = img.getScaledInstance(650, -100, java.awt.Image.SCALE_SMOOTH);
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
        java.net.URL aboutURL = AboutTTS_dialog.class.getResource("/img/tts_about.html");
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
        if (D2Dplot_global.getTTSsoftwareFolder().trim().isEmpty())return;
        File userGuideFile = new File(D2Dplot_global.getTTSsoftwareFolder()+FileUtils.getSeparator()+TTS_frame.getUserguidefile());
        try{
            if(Desktop.isDesktopSupported()){ // s'obre amb el programa per defecte
                Desktop.getDesktop().open(userGuideFile);
                return;
            }else{
                new ProcessBuilder("cmd","/c",userGuideFile.getAbsolutePath()).start();
                return;
            }
        } catch (Exception ex) {
            if(D2Dplot_global.isDebug())ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Sorry, unable to open user's guide with default pdf viewer. \n"
                    + "Please open it manually from the program folder",
                    "TTS software User's Guide",
                    JOptionPane.PLAIN_MESSAGE);
        }

    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

}
