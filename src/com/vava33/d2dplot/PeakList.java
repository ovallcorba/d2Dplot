package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.PuntClick;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Pklist_dialog extends JDialog {

    private static final long serialVersionUID = -5876034353317165127L;
    private JButton btnUpdate;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblCheckValues;
    private JLabel lblPeakList;
    private JList<String> list_pk;
    private JButton btnRemovePoint;
    private JButton btnRemoveAll;
    private JScrollPane scrollPane;
    private JCheckBox cbox_onTop;
    private JButton btnSaveappend;
    
    private static VavaLogger log = D2Dplot_global.getVavaLogger(Pklist_dialog.class.getName());

    private ImagePanel ipanel;

    /**
     * Create the dialog.
     */
    public Pklist_dialog(ImagePanel ip) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Pklist_dialog.class.getResource("/img/Icona.png")));
        ipanel = ip;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Selected peak List");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 400, 500);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
        	lblPeakList = new JLabel("Selected Peak list");
        	GridBagConstraints gbc_lblPeakList = new GridBagConstraints();
        	gbc_lblPeakList.gridwidth = 2;
        	gbc_lblPeakList.fill = GridBagConstraints.HORIZONTAL;
        	gbc_lblPeakList.insets = new Insets(0, 5, 5, 5);
        	gbc_lblPeakList.gridx = 0;
        	gbc_lblPeakList.gridy = 0;
        	contentPanel.add(lblPeakList, gbc_lblPeakList);
        }
        {
        	cbox_onTop = new JCheckBox("on top");
        	cbox_onTop.addItemListener(new ItemListener() {
        		public void itemStateChanged(ItemEvent arg0) {
        			do_chckbxOnTop_itemStateChanged(arg0);
        		}
        	});
        	cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
        	cbox_onTop.setSelected(true);
        	GridBagConstraints gbc_cbox_onTop = new GridBagConstraints();
        	gbc_cbox_onTop.insets = new Insets(0, 0, 5, 0);
        	gbc_cbox_onTop.gridx = 2;
        	gbc_cbox_onTop.gridy = 0;
        	contentPanel.add(cbox_onTop, gbc_cbox_onTop);
        }
        {
        	scrollPane = new JScrollPane();
        	GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        	gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        	gbc_scrollPane.gridwidth = 3;
        	gbc_scrollPane.fill = GridBagConstraints.BOTH;
        	gbc_scrollPane.gridx = 0;
        	gbc_scrollPane.gridy = 1;
        	contentPanel.add(scrollPane, gbc_scrollPane);
        	{
        		list_pk = new JList<String>();
        		scrollPane.setViewportView(list_pk);
        	}
        }
        {
            this.btnUpdate = new JButton("Update");
            GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
            gbc_btnUpdate.anchor = GridBagConstraints.EAST;
            gbc_btnUpdate.insets = new Insets(0, 0, 0, 5);
            gbc_btnUpdate.gridx = 0;
            gbc_btnUpdate.gridy = 2;
            contentPanel.add(btnUpdate, gbc_btnUpdate);
            this.btnUpdate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_btnUpdate_actionPerformed(arg0);
                }
            });
        }
        btnRemovePoint = new JButton("Remove Point");
        GridBagConstraints gbc_btnRemovePoint = new GridBagConstraints();
        gbc_btnRemovePoint.anchor = GridBagConstraints.EAST;
        gbc_btnRemovePoint.insets = new Insets(0, 0, 0, 5);
        gbc_btnRemovePoint.gridx = 1;
        gbc_btnRemovePoint.gridy = 2;
        contentPanel.add(btnRemovePoint, gbc_btnRemovePoint);
        btnRemovePoint.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		do_btnRemovePoint_actionPerformed(e);
        	}
        });
        {
        	btnRemoveAll = new JButton("Remove All");
        	GridBagConstraints gbc_btnRemoveAll = new GridBagConstraints();
        	gbc_btnRemoveAll.gridx = 2;
        	gbc_btnRemoveAll.gridy = 2;
        	contentPanel.add(btnRemoveAll, gbc_btnRemoveAll);
        	btnRemoveAll.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent arg0) {
        			do_btnRemoveAll_actionPerformed(arg0);
        		}
        	});
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 25, 0 };
                gbl_buttonPane.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
            JButton okButton = new JButton("ok");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            {
            }
            {
            	btnSaveappend = new JButton("Save");
            	btnSaveappend.addActionListener(new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            			do_btnSaveappend_actionPerformed(e);
            		}
            	});
            	GridBagConstraints gbc_btnSaveappend = new GridBagConstraints();
            	gbc_btnSaveappend.anchor = GridBagConstraints.BASELINE;
            	gbc_btnSaveappend.insets = new Insets(5, 5, 5, 5);
            	gbc_btnSaveappend.gridx = 0;
            	gbc_btnSaveappend.gridy = 0;
            	buttonPane.add(btnSaveappend, gbc_btnSaveappend);
            }

            {
                lblCheckValues = new JLabel("");
                lblCheckValues.setForeground(Color.RED);
                GridBagConstraints gbc_lblCheckValues = new GridBagConstraints();
                gbc_lblCheckValues.insets = new Insets(5, 5, 0, 5);
                gbc_lblCheckValues.gridx = 2;
                gbc_lblCheckValues.gridy = 0;
                buttonPane.add(lblCheckValues, gbc_lblCheckValues);
            }
            okButton.setActionCommand("OK");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 3;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
        }
        
        list_pk.setModel(new DefaultListModel<String>());
        this.loadPeakList();
    }

    protected void do_btnUpdate_actionPerformed(ActionEvent arg0) {
        this.loadPeakList();
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
    
    public void loadPeakList() {

        DefaultListModel<String> lm = (DefaultListModel<String>)list_pk.getModel();
        lm.clear();
        //mostrem la llista puntsCercles
        Iterator<PuntClick> itrP = ipanel.getPatt2D().getPuntsCercles().iterator();
        int i = 1;
        lblPeakList.setText(" Num     pX       pY       2T       I");
        while (itrP.hasNext()) {
            PuntClick pa = itrP.next();
            String entry = String.format(Locale.ENGLISH, "%4d  %s", i,pa.toString());
            lm.addElement(entry);
            i++;
        }
        list_pk.setModel(lm);
        this.ipanel.actualitzarVista();
    }

    protected void tanca() {
        this.dispose();
    }
	protected void do_btnRemoveAll_actionPerformed(ActionEvent arg0) {
		if(list_pk.getModel().getSize()<=0)return;
        ipanel.getPatt2D().getPuntsCercles().clear();
        this.loadPeakList();
	}
	protected void do_btnRemovePoint_actionPerformed(ActionEvent e) {
		if(list_pk.getSelectedIndex()<0)return;
		//busquem quin element est� seleccionat i el borrem
		//    String[] sel = list_pk.getSelectedValue().split("\\s+");
		String[] sel = ((String) list_pk.getModel().getElementAt(list_pk.getSelectedIndex())).trim().split("\\s+");
		float selx = Float.parseFloat(sel[1]);
		float sely = Float.parseFloat(sel[2]);
		float tol = 0.1f;
		Iterator<PuntClick> itrP = ipanel.getPatt2D().getPuntsCercles().iterator();
		while (itrP.hasNext()) {
		    PuntClick pa = itrP.next();
		    if ((FastMath.abs(pa.getX()-selx)<=tol)&&(FastMath.abs(pa.getY()-sely)<=tol)){
		        //es el mateix punt, el borrem
		        ipanel.getPatt2D().getPuntsCercles().remove(pa);
		    }
		}
        this.loadPeakList(); //recarreguem la llista
		
	}
	protected void do_chckbxOnTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
	}
	
	protected void do_btnSaveappend_actionPerformed(ActionEvent e) {
		
	    btnSaveappend.setText("Save");
	    //simplement guardem normal
	    File outfileIndex = FileUtils.fchooserSaveAsk(this,new File(D2Dplot_global.getWorkdir()), null, null);
	    try{
	        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfileIndex, false)));
	        out.println("# "+this.ipanel.getPatt2D().getImgfile().toString());
	        //escribim els elements de la llista
	        //primer ordenem la llista !220415
	        ArrayList<PuntClick> ord = new ArrayList<PuntClick>();
	        Iterator<PuntClick> itrP = ipanel.getPatt2D().getPuntsCercles().iterator();
	        while (itrP.hasNext()) {
	            ord.add(itrP.next());
	        }
	        Collections.sort(ord);

	        itrP = ord.iterator();
	        int i = 1;
	        while (itrP.hasNext()) {
	            PuntClick pa = itrP.next();
	            String entry = String.format(Locale.ENGLISH, "%4d  %s", i,pa.toString());
	            out.println(entry);
	            i++;
	        }
	        out.close();
	    }catch(Exception ex){
	        if(D2Dplot_global.isDebug())ex.printStackTrace();
	        log.warning("error saving file");
	    }
	    D2Dplot_global.setWorkdir(outfileIndex);
	    return;
	}
}

