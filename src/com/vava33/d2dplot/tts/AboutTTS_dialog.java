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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.TTS;
import com.vava33.jutils.FileUtils;

import net.miginfocom.swing.MigLayout;

public class AboutTTS_dialog {

    private final JDialog aboutTTSdialog;
    private JButton btnUsersGuide;
    private final JPanel contentPanel;
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
    public AboutTTS_dialog(JFrame parent) {
        this.contentPanel = new JPanel();
        this.aboutTTSdialog = new JDialog(parent, "About TTS software", true);
        this.aboutTTSdialog
                .setIconImage(new ImageIcon(this.getClass().getResource("/img/tts_icon120x120.png")).getImage());
        this.aboutTTSdialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = 680;
        final int height = 780;
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.aboutTTSdialog.setBounds(x, y, width, height);
        this.aboutTTSdialog.setResizable(false);
        this.aboutTTSdialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.aboutTTSdialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[grow]", "[64px][grow]"));
        {
            this.lblTalplogo = new JLabel("** LOGO **");
            this.contentPanel.add(this.lblTalplogo, "flowx,cell 0 0,alignx center,aligny center");
        }
        {
            final JPanel buttonPane = new JPanel();
            this.aboutTTSdialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                {
                    this.btnUsersGuide = new JButton("User's Guide");
                    this.btnUsersGuide.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            AboutTTS_dialog.this.do_btnUsersGuide_actionPerformed(arg0);
                        }
                    });
                    buttonPane.setLayout(new MigLayout("", "[][grow]", "[]"));
                    buttonPane.add(this.btnUsersGuide, "cell 0 0,alignx left,aligny top");
                }
            }
            final JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    AboutTTS_dialog.this.do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 1 0,alignx right,aligny top");
            this.aboutTTSdialog.getRootPane().setDefaultButton(okButton);
        }

        // posem el logo escalat
        final Image img = Toolkit.getDefaultToolkit()
                .getImage(AboutTTS_dialog.class.getResource("/img/tts_splash_760x120.png"));
        final Image newimg = img.getScaledInstance(650, -100, java.awt.Image.SCALE_SMOOTH);
        final ImageIcon logo = new ImageIcon(newimg);
        this.lblTalplogo.setText("");
        this.lblTalplogo.setIcon(logo);
        {
            this.scrollPane = new JScrollPane();
            this.scrollPane.setViewportBorder(null);
            this.scrollPane.setOpaque(false);
            this.contentPanel.add(this.scrollPane, "cell 0 1,grow");
            {
                this.textPane = new JEditorPane();
                this.textPane.setOpaque(false);
                this.scrollPane.setViewportView(this.textPane);
            }
        }

        // this.setIconImage(new
        // ImageIcon(getClass().getResource("/img/icona.png")).getImage());

        //llegim el fitxer html per poblar la label:

        this.textPane.setContentType("text/html");
        this.textPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        this.textPane.setEditable(false);

        this.textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (final Exception e1) {
                            if (D2Dplot_global.isDebug())
                                e1.printStackTrace();
                        }
                    }
                }
            }
        });
        final java.net.URL aboutURL = AboutTTS_dialog.class.getResource("/img/tts_about.html");
        if (aboutURL != null) {
            try {
                this.textPane.setPage(aboutURL);
            } catch (final IOException e) {
                System.err.println("Attempted to read a bad URL: " + aboutURL);
            }
        } else {
            System.err.println("Couldn't find file");
        }

        this.scrollPane.getViewport().setOpaque(false);

    }

    protected void do_btnUsersGuide_actionPerformed(ActionEvent arg0) {
        if (D2Dplot_global.getTTSsoftwareFolder().trim().isEmpty())
            return;
        final File userGuideFile = new File(
                D2Dplot_global.getTTSsoftwareFolder() + FileUtils.getSeparator() + TTS.getUserguidefile());
        try {
            if (Desktop.isDesktopSupported()) { // s'obre amb el programa per defecte
                Desktop.getDesktop().open(userGuideFile);
                return;
            } else {
                new ProcessBuilder("cmd", "/c", userGuideFile.getAbsolutePath()).start();
                return;
            }
        } catch (final Exception ex) {
            if (D2Dplot_global.isDebug())
                ex.printStackTrace();
            JOptionPane.showMessageDialog(this.aboutTTSdialog,
                    "Sorry, unable to open user's guide with default pdf viewer. \n"
                            + "Please open it manually from the program folder",
                    "TTS software User's Guide", JOptionPane.PLAIN_MESSAGE);
        }

    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.aboutTTSdialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.aboutTTSdialog.setVisible(vis);
    }
}
