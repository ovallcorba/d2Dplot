package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import com.vava33.jutils.FileUtils;

import vava33.plot2d.auxi.ImgFileUtils;
import vava33.plot2d.auxi.OrientSolucio;
import vava33.plot2d.auxi.Pattern2D;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class WorkSOL_dialog extends JDialog {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final JPanel contentPanel = new JPanel();

	Pattern2D patt2D;
	private ImagePanel panel;
	/**
	 * Create the dialog.
	 */
	public WorkSOL_dialog(Pattern2D patt, OrientSolucio os, String title) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(About_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);
		setBounds(100, 100, 530, 559);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			panel = new ImagePanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 0;
			contentPanel.add(panel, gbc_panel);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				{
					GridBagLayout gbl_buttonPane = new GridBagLayout();
					gbl_buttonPane.columnWidths = new int[] {0, 0, 0, 0};
					gbl_buttonPane.rowHeights = new int[]{25, 0};
					gbl_buttonPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};
					gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
					buttonPane.setLayout(gbl_buttonPane);
				}
				JButton btnSaveBin = new JButton("Save BIN");
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
					GridBagConstraints gbc_btnResetView = new GridBagConstraints();
					gbc_btnResetView.anchor = GridBagConstraints.WEST;
					gbc_btnResetView.insets = new Insets(5, 5, 5, 5);
					gbc_btnResetView.gridx = 0;
					gbc_btnResetView.gridy = 0;
					buttonPane.add(btnResetView, gbc_btnResetView);
				}
				{
					JButton btnTrueSize = new JButton("True Size");
					btnTrueSize.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							do_btnTrueSize_actionPerformed(e);
						}
					});
					GridBagConstraints gbc_btnTrueSize = new GridBagConstraints();
					gbc_btnTrueSize.anchor = GridBagConstraints.WEST;
					gbc_btnTrueSize.insets = new Insets(5, 0, 5, 5);
					gbc_btnTrueSize.gridx = 1;
					gbc_btnTrueSize.gridy = 0;
					buttonPane.add(btnTrueSize, gbc_btnTrueSize);
				}
				GridBagConstraints gbc_btnSaveBin = new GridBagConstraints();
				gbc_btnSaveBin.anchor = GridBagConstraints.NORTHWEST;
				gbc_btnSaveBin.insets = new Insets(5, 0, 5, 5);
				gbc_btnSaveBin.gridx = 2;
				gbc_btnSaveBin.gridy = 0;
				buttonPane.add(btnSaveBin, gbc_btnSaveBin);
			}
			JButton cancelButton = new JButton("Close");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					do_cancelButton_actionPerformed(e);
				}
			});
			cancelButton.setActionCommand("Cancel");
			GridBagConstraints gbc_cancelButton = new GridBagConstraints();
			gbc_cancelButton.insets = new Insets(5, 0, 5, 5);
			gbc_cancelButton.anchor = GridBagConstraints.NORTHWEST;
			gbc_cancelButton.gridx = 3;
			gbc_cancelButton.gridy = 0;
			buttonPane.add(cancelButton, gbc_cancelButton);
		}
		
		this.patt2D=patt;
		panel.setImagePatt2D(this.patt2D);

		//CAL MOSTRAR LA SOLUCIO:
		MainFrame.getPatt2D().getSolucions().add(os);
		panel.setShowHKLsol(true);
		panel.setShowSolPoints(true);
	
	}
	protected void do_cancelButton_actionPerformed(ActionEvent e) {
		this.dispose();
	}
	protected void do_btnSaveBin_actionPerformed(ActionEvent e) {
    	FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("2D Data file (bin)", "bin")};
    	File fsave = FileUtils.fchooser(new File(MainFrame.getWorkdir()), filter, true);
    	if (fsave == null)return;
    	fsave = ImgFileUtils.writeBIN(fsave, this.patt2D);
    	this.patt2D.setImgfile(fsave);
    }
	protected void do_btnResetView_actionPerformed(ActionEvent e) {
		this.panel.resetView();
	}
	protected void do_btnTrueSize_actionPerformed(ActionEvent e) {
		this.panel.setScalefit(1.0f);
	}
}
