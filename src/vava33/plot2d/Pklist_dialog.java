package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import vava33.plot2d.auxi.OrientSolucio;
import vava33.plot2d.auxi.PuntCercle;
import vava33.plot2d.auxi.PuntSolucio;

import javax.swing.JList;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Pklist_dialog extends JDialog {

    private static final long serialVersionUID = -5876034353317165127L;
    private JButton btnUpdate;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblCheckValues;
    private ImagePanel panelImatge;
    private JLabel lblPeakList;
    private JList list_pk;
    private JButton btnRemovePoint;
    private JButton btnRemoveAll;
    private JScrollPane scrollPane;
    private JCheckBox cbox_onTop;
    private JButton btnSaveappend;


    /**
     * Create the dialog.
     */
    public Pklist_dialog(ImagePanel ip) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Pklist_dialog.class.getResource("/img/Icona.png")));
        panelImatge = ip;
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
        		list_pk = new JList();
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
            	btnSaveAs = new JButton("Save as");
            	btnSaveAs.addActionListener(new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            			do_btnSaveAs_actionPerformed(e);
            		}
            	});
            	GridBagConstraints gbc_btnSaveAs = new GridBagConstraints();
            	gbc_btnSaveAs.insets = new Insets(0, 0, 0, 5);
            	gbc_btnSaveAs.gridx = 1;
            	gbc_btnSaveAs.gridy = 0;
            	buttonPane.add(btnSaveAs, gbc_btnSaveAs);
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
        
        list_pk.setModel(new DefaultListModel());
        this.loadPeakList();
    }

//    public void addLS() {
//        this.txt_pklist.append(System.getProperty("line.separator"));
//        txt_pklist.setCaretPosition(txt_pklist.getDocument().getLength());
//    }
//
//    protected void clearList() {
//        txt_pklist.setText("");
//    }
    
//    protected void clearList2(){
//    	DefaultListModel<String> lm = (DefaultListModel<String>) list_pk.getModel();
//    	lm.clear();
//    	list_pk.setModel(lm);
//    }

    protected void do_btnUpdate_actionPerformed(ActionEvent arg0) {
        this.loadPeakList();
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

    public void loadPeakList() {
//        DefaultListModel<String> lm = new DefaultListModel<String>();
//        lm.clear();
        DefaultListModel lm = (DefaultListModel)list_pk.getModel();
        if (panelImatge.isShowIndexing()){
        	lm.clear();
        	//mostrem la llista puntsCercles
            Iterator<PuntCercle> itrP = panelImatge.getPuntsCercles().iterator();
            int i = 1;
            lblPeakList.setText(" Num     pX       pY       2T       I");
            while (itrP.hasNext()) {
                PuntCercle pa = itrP.next();
                String entry = String.format(Locale.ENGLISH, "%4d  %s", i,pa.toString());
                lm.addElement(entry);
                i++;
            }
        }
        if (panelImatge.isShowHKLIndexing()){
        	//aqui no borrem sino que afegim
        	//mostrem a la llista els punts HKL que s'han modificat les coordenades clic
    	    Iterator<OrientSolucio> itrOS = panelImatge.getSolucions().iterator();
    	    OrientSolucio os = null;
    	    while (itrOS.hasNext()) {
    	        os = itrOS.next();
    	        if (os.isShowSol()) {
    	        	break;
    	        }
    	    }
    	    if(os==null)return;
        	Iterator<PuntSolucio> itrPS = os.getSol().iterator();
            while (itrPS.hasNext()) {
                PuntSolucio s = itrPS.next();
                if(s.getCoordXclic()>0){
                	//mostrem a la llista
                	String entry = String.format(Locale.ENGLISH, " %s  %8.2f %8.2f  %5d", 
                			s.getHKLspaces(),s.getCoordXclic(),s.getCoordYclic(),(int)s.getIntenClic());
                	if(!lm.contains(entry)){
                		lm.addElement(entry);	
                	}
                }
            }
        }
        list_pk.setModel(lm);
    }

    protected void tanca() {
        this.dispose();
    }
	protected void do_btnRemoveAll_actionPerformed(ActionEvent arg0) {
		if(list_pk.getModel().getSize()<=0)return;
		if (panelImatge.isShowIndexing()){
			//borrem la llista de PuntsCercles i actualiztem llista
			panelImatge.getPuntsCercles().clear();
		}
		if (panelImatge.isShowHKLIndexing()){
			JOptionPane.showMessageDialog(this, "Peaks will not be deleted from the file, click on \"Save as\" to save the new list");
			//posem tots els "clic" a -1
    	    Iterator<OrientSolucio> itrOS = panelImatge.getSolucions().iterator();
    	    OrientSolucio os = null;
    	    while (itrOS.hasNext()) {
    	        os = itrOS.next();
    	        if (os.isShowSol()) {
    	        	break;
    	        }
    	    }
    	    if(os==null)return;
        	Iterator<PuntSolucio> itrPS = os.getSol().iterator();
            while (itrPS.hasNext()) {
                PuntSolucio s = itrPS.next();
                s.setCoordXclic(-1);
                s.setCoordYclic(-1);
                s.setIntenClic(-1);
            }
            nextToWrite=0; //corregim laswritten
            ((DefaultListModel)list_pk.getModel()).clear();
		}
		this.loadPeakList();
	}
	protected void do_btnRemovePoint_actionPerformed(ActionEvent e) {
		if(list_pk.getSelectedIndex()<0)return;
		if (panelImatge.isShowIndexing()){
			//busquem quin element est� seleccionat i el borrem
//			String[] sel = list_pk.getSelectedValue().split("\\s+");
			String[] sel = ((String) list_pk.getModel().getElementAt(list_pk.getSelectedIndex())).trim().split("\\s+");
			float selx = Float.parseFloat(sel[1]);
			float sely = Float.parseFloat(sel[2]);
			float tol = 0.1f;
            Iterator<PuntCercle> itrP = panelImatge.getPuntsCercles().iterator();
            while (itrP.hasNext()) {
                PuntCercle pa = itrP.next();
                if ((FastMath.abs(pa.getX()-selx)<=tol)&&(FastMath.abs(pa.getY()-sely)<=tol)){
                	//es el mateix punt, el borrem
                	panelImatge.getPuntsCercles().remove(pa);
                }
            }
		}
		if (panelImatge.isShowHKLIndexing()){
			JOptionPane.showMessageDialog(this, "Peaks will not be deleted from the file, click on \"Save as\" to save the new list");
			//posem a l'element seleccionat els "clic" a -1
    	    Iterator<OrientSolucio> itrOS = panelImatge.getSolucions().iterator();
    	    OrientSolucio os = null;
    	    while (itrOS.hasNext()) {
    	        os = itrOS.next();
    	        if (os.isShowSol()) {
    	        	break;
    	        }
    	    }
    	    if(os==null)return;
//			String[] sel = list_pk.getSelectedValue().split("\\s+");
			String[] sel = ((String) list_pk.getModel().getElementAt(list_pk.getSelectedIndex())).trim().split("\\s+");
			float selx = Float.parseFloat(sel[3]);
			float sely = Float.parseFloat(sel[4]);
			float tol = 0.1f;
        	Iterator<PuntSolucio> itrPS = os.getSol().iterator();
            while (itrPS.hasNext()) {
                PuntSolucio s = itrPS.next();
                if ((FastMath.abs(s.getCoordXclic()-selx)<=tol)&&(FastMath.abs(s.getCoordYclic()-sely)<=tol)){
                	//es el mateix punt, el borrem
                    s.setCoordXclic(-1);
                    s.setCoordYclic(-1);
                    s.setIntenClic(-1);
                }
            }
            //corregim lastwritten si hem borrat un dels interns
            if(list_pk.getSelectedIndex()<nextToWrite)nextToWrite=nextToWrite-1;
            VavaLogger.LOG.info("selIndex="+list_pk.getSelectedIndex());
            VavaLogger.LOG.info("nextToWrite="+nextToWrite);
            ((DefaultListModel)list_pk.getModel()).remove(list_pk.getSelectedIndex());
		}
		this.loadPeakList(); //recarreguem la llista
	}
	protected void do_chckbxOnTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
	}
	
	File outfile = null;
	boolean firstWrite = true;
	int nextToWrite=0;
	private JButton btnSaveAs;
	
	protected void do_btnSaveappend_actionPerformed(ActionEvent e) {
		
		if (panelImatge.isShowIndexing()){
			btnSaveappend.setText("Save");
			//simplement guardem normal
			File outfileIndex = FileUtils.fchooser(new File(MainFrame.getWorkdir()), null, true);
			try{
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfileIndex, false)));
				out.println("# "+this.panelImatge.getPatt2D().getImgfile().toString());
			    //escribim els elements de la llista
			    for(int i=0; i<list_pk.getModel().getSize(); i++){
			    	out.println(list_pk.getModel().getElementAt(i));
			    }
			    out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return;
		}
		
		//AIXO NOMES ES PER HKLINDEXING:
		//Aqui s'ha de guardar la llista a un fitxer nou o afegir-lo a algun ja existent
		//1ra linia amb nom del fitxer imatge
		boolean append=true;
		
		if(outfile==null){
			//s'ha d'obrir un filechooser, si s'escull un nou es crea i sino es pregunta si
			//es vol sobreesciure o fer append
			outfile = FileUtils.fchooserNoAskOverwrite(new File(MainFrame.getWorkdir()), null, true);
			if (outfile==null)return;
			if(outfile.exists()){
				//preguntar si sobreescriure o fer append
				//Custom button text
				Object[] options = {"Append",
				                    "Overwrite"};
				int n = JOptionPane.showOptionDialog(this,
				    "File exists. Append to file o overwrite it? ",
				    "Append or overwrite",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[0]);
				if (n==JOptionPane.CLOSED_OPTION)return; //si s'ha tancat
				if (n==JOptionPane.YES_OPTION)append=true;
				if (n==JOptionPane.NO_OPTION)append=false;
			}else{
				//el fitxer no existeix s'ha de crear
				append=false;
			}
			
		}
		//ja tenim un fitxer obert, fem append
//		if(!outfile.exists()){return;}
		
		//arribats aqu� tenim un fitxer SELECCIONAT en el qu� caldrar fer append o escriure de zero
		try {
			PrintWriter out;
		    if(append) {
		    	out = new PrintWriter(new BufferedWriter(new FileWriter(outfile, true)));
		    }else{
		    	out = new PrintWriter(new BufferedWriter(new FileWriter(outfile, false)));
		    }
		    if (firstWrite){
		    	if(!append){
		    		//s'esta creant fitxer nou, posem cap�alera
		    		out.println("# TITOL");
		    		float distMD=this.panelImatge.getPatt2D().getDistMD();
		    		float dimX=this.panelImatge.getPatt2D().getDimX();
		    		float dimY=this.panelImatge.getPatt2D().getDimY();
		    		float minI=this.panelImatge.getPatt2D().getMinI();
		    		float maxI=this.panelImatge.getPatt2D().getMaxI();
		    		float nSatur=this.panelImatge.getPatt2D().getnSatur();
		    		float scale=this.panelImatge.getPatt2D().getScale();
		    		float centrX=this.panelImatge.getPatt2D().getCentrX();
		    		float centrY=this.panelImatge.getPatt2D().getCentrY();
		    		float pixSx=this.panelImatge.getPatt2D().getPixSx();
		    		float pixSy=this.panelImatge.getPatt2D().getPixSy();
		    		float wavel=this.panelImatge.getPatt2D().getWavel();
		            String info = "#  NX(cols)= " + dimX + " NY(rows)= " + dimY + "\n" + 
		                    "#  minI=" + minI + " maxI=" + maxI + "  (Scale factor= " + FileUtils.dfX_3.format(1/scale) +"; Saturated= "+nSatur+")\n" + 
		                    "#  Xcent=" + FileUtils.dfX_3.format(centrX) + " Ycent=" + FileUtils.dfX_3.format(centrY) + "\n" + 
		                    "#  Distance Sample-Detector (mm)=" + FileUtils.dfX_3.format(distMD) + "\n" +
		                    "#  Wavelength (A)=" + FileUtils.dfX_5.format(wavel) + "\n" + 
		                    "#  Pixel Size X Y (mm)= " + FileUtils.dfX_4.format(pixSx) + " " + FileUtils.dfX_4.format(pixSy);
		    		out.println(info);
		    		out.println("#");
		    	}
		    	out.println("# "+this.panelImatge.getPatt2D().getImgfile().toString());	
		    	firstWrite=false;
		    }
		    //escribim els elements de la llista
		    for(int i=nextToWrite; i<list_pk.getModel().getSize(); i++){
		    	out.println(list_pk.getModel().getElementAt(i));
		    }
		    nextToWrite=list_pk.getModel().getSize();
		    VavaLogger.LOG.info("nextToWrite="+nextToWrite);
		    out.close();
		    btnSaveappend.setText("Append");
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	}
	protected void do_btnSaveAs_actionPerformed(ActionEvent e) {
		this.firstWrite=true;
		this.nextToWrite=0;
		this.outfile=null;
		this.btnSaveappend.setText("Save");
		this.btnSaveappend.doClick();
	}
}
