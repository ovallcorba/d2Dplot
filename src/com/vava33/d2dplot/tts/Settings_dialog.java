package com.vava33.d2dplot.tts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.TTS;
import com.vava33.jutils.FileUtils;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JTextPane;

public class Settings_dialog {

    private JDialog ttsSettingsdialog;
    private JPanel contentPanel;
    private JTextField txtTxteditor;
    private JTextField txtTtsfolder;
    private JLabel lblInco;
    private JLabel lblTtsmerge;
    private JLabel lblTtscelref;
    private TTS parent;

    /**
     * Create the dialog.
     */
    public Settings_dialog(JFrame parent, TTS pare) {
        this.parent = pare;
        contentPanel = new JPanel();
        ttsSettingsdialog = new JDialog(parent, "TTS software settings",true);
        ttsSettingsdialog.setIconImage(new ImageIcon(getClass().getResource("/img/tts_icon120x120.png")).getImage());
        ttsSettingsdialog.setBounds(100, 100, 450, 280);
        ttsSettingsdialog.getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        ttsSettingsdialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("insets 0", "[][grow][]", "[][grow][][][][][grow]"));
        {
            JLabel lblTextEditor = new JLabel("Text editor");
            contentPanel.add(lblTextEditor, "cell 0 0,alignx trailing,aligny center");
        }
        {
            txtTxteditor = new JTextField();
            txtTxteditor.setText("(system default)");
            contentPanel.add(txtTxteditor, "cell 1 0,growx,aligny center");
            txtTxteditor.setColumns(10);
        }
        {
            JButton btnTxt = new JButton("...");
            btnTxt.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_btnTxt_actionPerformed(e);
                }
            });
            btnTxt.setMargin(new Insets(2, 2, 2, 2));
            contentPanel.add(btnTxt, "cell 2 0,aligny center");
        }
        {
            JTextPane txtpnPleaseSelectTtssoftware = new JTextPane();
            txtpnPleaseSelectTtssoftware.setContentType("text/html");
            
            txtpnPleaseSelectTtssoftware.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
            txtpnPleaseSelectTtssoftware.setEditable(false);
            
            txtpnPleaseSelectTtssoftware.addHyperlinkListener(new HyperlinkListener() {
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
            txtpnPleaseSelectTtssoftware.setOpaque(false);
            
            txtpnPleaseSelectTtssoftware.setText("<html> Please select tts_software folder to use it inside d2Dplot.<br>\nIt can be downloaded from: <a href=\"http://www.icmab.es/crystallography/software\">http://www.icmab.es/crystallography/software</a> </html>");
            contentPanel.add(txtpnPleaseSelectTtssoftware, "cell 0 1 3 1,grow");
        }
        {
            JLabel lblTtssoftwarefolder = new JLabel("tts_software_folder");
            contentPanel.add(lblTtssoftwarefolder, "cell 0 2,alignx trailing");
        }
        {
            txtTtsfolder = new JTextField();
            contentPanel.add(txtTtsfolder, "cell 1 2,growx");
            txtTtsfolder.setColumns(10);
        }
        {
            JButton btnTTSfolder = new JButton("...");
            btnTTSfolder.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_btnTTSfolder_actionPerformed(e);
                }
            });
            btnTTSfolder.setMargin(new Insets(2, 2, 2, 2));
            contentPanel.add(btnTTSfolder, "cell 2 2");
        }
        {
            lblInco = new JLabel("");
            contentPanel.add(lblInco, "cell 1 3 2 1,alignx center,aligny center");
        }
        {
            JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_okButton_actionPerformed(e);
                }
            });
            {
                lblTtsmerge = new JLabel("");
                contentPanel.add(lblTtsmerge, "cell 1 4 2 1,alignx center");
            }
            {
                lblTtscelref = new JLabel("");
                contentPanel.add(lblTtscelref, "cell 1 5 2 1,alignx center");
            }
            contentPanel.add(okButton, "cell 0 6 3 1,alignx center,aligny bottom");
            okButton.setActionCommand("OK");
            ttsSettingsdialog.getRootPane().setDefaultButton(okButton);
        }
        
        inicia();
    }

    //inicialitzacions extres
    private void inicia(){
    	ttsSettingsdialog.setVisible(false); //per defecte no ho mostrarem al crear-lo
        this.updateTxtFields();
    }
    
    protected void do_btnTxt_actionPerformed(ActionEvent e) {
        File f = FileUtils.fchooserOpen(ttsSettingsdialog, new File(D2Dplot_global.getWorkdir()), null, 0);
        if (f!=null){
            txtTxteditor.setText(f.getAbsolutePath());
            D2Dplot_global.setTxtEditPath(f.getAbsolutePath());
        }
    }
    
    protected void do_btnTTSfolder_actionPerformed(ActionEvent e) {
        File dir = FileUtils.fchooserOpenDir(ttsSettingsdialog, new File(D2Dplot_global.getWorkdir()), "Select tts_software FOLDER");
        if (dir!=null){
            txtTtsfolder.setText(dir.getAbsolutePath());
            D2Dplot_global.setTTSsoftwareFolder(dir.getAbsolutePath());
            parent.checkTTSDependencies();
            updateTxtFields();
        }
    }
    
    private void updateTxtFields(){
        if (D2Dplot_global.getTxtEditPath().trim().isEmpty()) {
            this.txtTxteditor.setText("< system default >");
        }else {
            this.txtTxteditor.setText(D2Dplot_global.getTxtEditPath());
        }
        
        if (D2Dplot_global.getTTSsoftwareFolder().trim().isEmpty()) {
            this.txtTtsfolder.setText("< not set >");
        }else {
            this.txtTtsfolder.setText(D2Dplot_global.getTTSsoftwareFolder());
            //we try to detect the executables now...
            if (!TTS.getIncoExec().trim().isEmpty()) {
            	lblInco.setForeground(Color.GREEN);
                lblInco.setText("tts_inco FOUND!");
            }else {
            	lblInco.setForeground(Color.RED);
                lblInco.setText("tts_inco NOT FOUND!");
            }
            if (!TTS.getMergeExec().trim().isEmpty()) {
            	lblTtsmerge.setForeground(Color.GREEN);
                lblTtsmerge.setText("tts_merge FOUND!");
            }else {
            	lblTtsmerge.setForeground(Color.RED);
                lblTtsmerge.setText("tts_merge NOT FOUND!");
            }
            if (!TTS.getCelrefExec().trim().isEmpty()) {
            	lblTtscelref.setForeground(Color.GREEN);
                lblTtscelref.setText("tts_celref FOUND!");
            }else {
            	lblTtscelref.setForeground(Color.RED);
                lblTtscelref.setText("tts_celref NOT FOUND!");
            }
        }
    }
    
    protected void do_okButton_actionPerformed(ActionEvent e) {
    	ttsSettingsdialog.setVisible(false);
//        parent.checkTTSDependencies();
    }
    public void setVisible(boolean vis) {
    	ttsSettingsdialog.setVisible(vis);
    }

}
