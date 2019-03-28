package com.vava33.d2dplot.tts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.TTS;
import com.vava33.jutils.FileUtils;

import net.miginfocom.swing.MigLayout;

public class Settings_dialog {

    private final JDialog ttsSettingsdialog;
    private final JPanel contentPanel;
    private JTextField txtTxteditor;
    private JTextField txtTtsfolder;
    private JLabel lblInco;
    private JLabel lblTtsmerge;
    private JLabel lblTtscelref;
    private final TTS parent;

    /**
     * Create the dialog.
     */
    public Settings_dialog(JFrame parent, TTS pare) {
        this.parent = pare;
        this.contentPanel = new JPanel();
        this.ttsSettingsdialog = new JDialog(parent, "TTS software settings", true);
        this.ttsSettingsdialog
                .setIconImage(new ImageIcon(this.getClass().getResource("/img/tts_icon120x120.png")).getImage());
        this.ttsSettingsdialog.setBounds(100, 100, 450, 280);
        this.ttsSettingsdialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.ttsSettingsdialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("insets 0", "[][grow][]", "[][grow][][][][][grow]"));
        {
            final JLabel lblTextEditor = new JLabel("Text editor");
            this.contentPanel.add(lblTextEditor, "cell 0 0,alignx trailing,aligny center");
        }
        {
            this.txtTxteditor = new JTextField();
            this.txtTxteditor.setText("(system default)");
            this.contentPanel.add(this.txtTxteditor, "cell 1 0,growx,aligny center");
            this.txtTxteditor.setColumns(10);
        }
        {
            final JButton btnTxt = new JButton("...");
            btnTxt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Settings_dialog.this.do_btnTxt_actionPerformed(e);
                }
            });
            btnTxt.setMargin(new Insets(2, 2, 2, 2));
            this.contentPanel.add(btnTxt, "cell 2 0,aligny center");
        }
        {
            final JTextPane txtpnPleaseSelectTtssoftware = new JTextPane();
            txtpnPleaseSelectTtssoftware.setContentType("text/html");

            txtpnPleaseSelectTtssoftware.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
            txtpnPleaseSelectTtssoftware.setEditable(false);

            txtpnPleaseSelectTtssoftware.addHyperlinkListener(new HyperlinkListener() {
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
            txtpnPleaseSelectTtssoftware.setOpaque(false);

            txtpnPleaseSelectTtssoftware.setText(
                    "<html> Please select tts_software folder to use it inside d2Dplot.<br>\nIt can be downloaded from: <a href=\"http://www.icmab.es/crystallography/software\">http://www.icmab.es/crystallography/software</a> </html>");
            this.contentPanel.add(txtpnPleaseSelectTtssoftware, "cell 0 1 3 1,grow");
        }
        {
            final JLabel lblTtssoftwarefolder = new JLabel("tts_software_folder");
            this.contentPanel.add(lblTtssoftwarefolder, "cell 0 2,alignx trailing");
        }
        {
            this.txtTtsfolder = new JTextField();
            this.contentPanel.add(this.txtTtsfolder, "cell 1 2,growx");
            this.txtTtsfolder.setColumns(10);
        }
        {
            final JButton btnTTSfolder = new JButton("...");
            btnTTSfolder.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Settings_dialog.this.do_btnTTSfolder_actionPerformed(e);
                }
            });
            btnTTSfolder.setMargin(new Insets(2, 2, 2, 2));
            this.contentPanel.add(btnTTSfolder, "cell 2 2");
        }
        {
            this.lblInco = new JLabel("");
            this.contentPanel.add(this.lblInco, "cell 1 3 2 1,alignx center,aligny center");
        }
        {
            final JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Settings_dialog.this.do_okButton_actionPerformed(e);
                }
            });
            {
                this.lblTtsmerge = new JLabel("");
                this.contentPanel.add(this.lblTtsmerge, "cell 1 4 2 1,alignx center");
            }
            {
                this.lblTtscelref = new JLabel("");
                this.contentPanel.add(this.lblTtscelref, "cell 1 5 2 1,alignx center");
            }
            this.contentPanel.add(okButton, "cell 0 6 3 1,alignx center,aligny bottom");
            okButton.setActionCommand("OK");
            this.ttsSettingsdialog.getRootPane().setDefaultButton(okButton);
        }

        this.inicia();
    }

    //inicialitzacions extres
    private void inicia() {
        this.ttsSettingsdialog.setVisible(false); //per defecte no ho mostrarem al crear-lo
        this.updateTxtFields();
    }


    protected void do_btnTxt_actionPerformed(ActionEvent e) {
        final File f = FileUtils.fchooserOpen(this.ttsSettingsdialog, new File(D2Dplot_global.getWorkdir()), null, 0);
        if (f != null) {
            this.txtTxteditor.setText(f.getAbsolutePath());
            D2Dplot_global.setTxtEditPath(f.getAbsolutePath());
        }
    }

    protected void do_btnTTSfolder_actionPerformed(ActionEvent e) {
        final File dir = FileUtils.fchooserOpenDir(this.ttsSettingsdialog, new File(D2Dplot_global.getWorkdir()),
                "Select tts_software FOLDER");
        if (dir != null) {
            this.txtTtsfolder.setText(dir.getAbsolutePath());
            D2Dplot_global.setTTSsoftwareFolder(dir.getAbsolutePath());
            this.parent.checkTTSDependencies();
            this.updateTxtFields();
        }
    }

    private void updateTxtFields() {
        if (D2Dplot_global.getTxtEditPath().trim().isEmpty()) {
            this.txtTxteditor.setText("< system default >");
        } else {
            this.txtTxteditor.setText(D2Dplot_global.getTxtEditPath());
        }

        if (D2Dplot_global.getTTSsoftwareFolder().trim().isEmpty()) {
            this.txtTtsfolder.setText("< not set >");
        } else {
            this.txtTtsfolder.setText(D2Dplot_global.getTTSsoftwareFolder());
            //we try to detect the executables now...
            if (!TTS.getIncoExec().trim().isEmpty()) {
                this.lblInco.setForeground(Color.GREEN);
                this.lblInco.setText("tts_inco FOUND!");
            } else {
                this.lblInco.setForeground(Color.RED);
                this.lblInco.setText("tts_inco NOT FOUND!");
            }
            if (!TTS.getMergeExec().trim().isEmpty()) {
                this.lblTtsmerge.setForeground(Color.GREEN);
                this.lblTtsmerge.setText("tts_merge FOUND!");
            } else {
                this.lblTtsmerge.setForeground(Color.RED);
                this.lblTtsmerge.setText("tts_merge NOT FOUND!");
            }
            if (!TTS.getCelrefExec().trim().isEmpty()) {
                this.lblTtscelref.setForeground(Color.GREEN);
                this.lblTtscelref.setText("tts_celref FOUND!");
            } else {
                this.lblTtscelref.setForeground(Color.RED);
                this.lblTtscelref.setText("tts_celref NOT FOUND!");
            }
        }
    }

    protected void do_okButton_actionPerformed(ActionEvent e) {
        this.ttsSettingsdialog.setVisible(false);
        //        parent.checkTTSDependencies();
    }

    public void setVisible(boolean vis) {
        this.ttsSettingsdialog.setVisible(vis);
    }

}
