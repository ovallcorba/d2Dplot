package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import net.miginfocom.swing.MigLayout;

public class SimpleImageDialog {

	private JDialog simpleImgDialog;
    private JPanel contentPanel;

	Pattern2D patt2D;
	private ImagePanel panel;
	/**
	 * Create the dialog.
	 */
	public SimpleImageDialog(JFrame parent, Pattern2D patt, String title) {
		this.contentPanel=new JPanel();
		simpleImgDialog = new JDialog(parent,title,false);
		simpleImgDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/img/Icona.png")));
		simpleImgDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		simpleImgDialog.setBounds(100, 100, 530, 559);
		simpleImgDialog.getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		simpleImgDialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		{
			panel = new ImagePanel();
			contentPanel.add(panel.getIpanelMain(), "cell 0 0,grow");
		}
		{
			JPanel buttonPane = new JPanel();
			simpleImgDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnSaveBin = new JButton("Save Image");
				btnSaveBin.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						do_btnSaveBin_actionPerformed(e);
					}
				});
				{
					JButton btnResetView = new JButton("Reset View");
					btnResetView.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							do_btnResetView_actionPerformed(e);
						}
					});
					buttonPane.setLayout(new MigLayout("", "[][][grow][]", "[25px]"));
					buttonPane.add(btnResetView, "cell 0 0,alignx left,aligny center");
				}
				{
					JButton btnTrueSize = new JButton("True Size");
					btnTrueSize.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							do_btnTrueSize_actionPerformed(e);
						}
					});
					buttonPane.add(btnTrueSize, "cell 1 0,alignx left,aligny center");
				}
				buttonPane.add(btnSaveBin, "cell 2 0,alignx right,aligny top");
			}
			JButton cancelButton = new JButton("Close");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					do_cancelButton_actionPerformed(e);
				}
			});
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton, "cell 3 0,alignx left,aligny top");
		}
		
		
		this.patt2D=patt;
		panel.setImagePatt2D(this.patt2D);
	}
	protected void do_cancelButton_actionPerformed(ActionEvent e) {
		simpleImgDialog.dispose();
	}
	protected void do_btnSaveBin_actionPerformed(ActionEvent e) {
	    FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterWrite();
        File fsave = FileUtils.fchooserSaveAsk(simpleImgDialog,new File(D2Dplot_global.getWorkdir()), filt, null);
    	if (fsave == null)return;
    	fsave = ImgFileUtils.writePatternFile(fsave, this.patt2D, true);
    	this.patt2D.setImgfile(fsave);
    }
	protected void do_btnResetView_actionPerformed(ActionEvent e) {
		this.panel.resetView();
	}
	protected void do_btnTrueSize_actionPerformed(ActionEvent e) {
		this.panel.setScalefit(1.0f);
	}
    public void setVisible(boolean vis) {
    	simpleImgDialog.setVisible(vis);
    }
}
