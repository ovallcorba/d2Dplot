package com.vava33.d2dplot;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.event.ListSelectionEvent;

import org.apache.commons.math3.util.FastMath;

import com.vava33.cellsymm.PDCompound;
import com.vava33.cellsymm.PDDatabase;
import com.vava33.cellsymm.PDDatabase_dialog;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.PuntClick;

public class Database extends PDDatabase_dialog {

    private ImagePanel ipanel;
    
    //@Override
    protected String helpMSG = "\n" + "** General help **\n"
            + " - Click on a compound to see the rings on the image (if ShowRings is selected)\n"
            + " - Check apply name filter and type to find the desired compound\n"
            + " - Add/Edit compounds by clicking the respective buttons and filling the info. Alternatively you can edit manually the DB file\n"
            + "(which is a simple self-explanatory text file)\n"
            + " - Add to QuickList (QL) to access the rings from the main window directly. Compounds in the QL are saved in a separate file\n"
            + "with the same format as the DB file and can also be edited the same way\n"
            + "** Search by peaks **\n"
            + " - On the main window click on the desired rings so that they are selected in the point list (Sel.points should be active)\n"
            + " - Click the button -search by peaks-\n"
            + " - List will be updated by the best matching compounds (with respective residuals)\n"
            + " - Click on the compounds to see the rings on top of your image and check if they really match\n"
            + "\n" + "Note:\n"
            + "The default DB is a small selection of compounds taken from different sources, mostly publications. Each entry contains the reference from\n"
            + "where it has been taken (with the respective authors) which can be retrieved by clicking -compound info- or by editing the compound.\n"
            + "For any doubts/comments/complaints/suggestions, please contact the author\n" + "\n";
    
    /**
     * Create the dialog.
     */
    public Database(JFrame parent, ImagePanel ip) {
    	super(parent);
        this.setIpanel(ip);
        DBdialog.setIconImage(Toolkit.getDefaultToolkit().getImage(Database.class.getResource("/img/Icona.png")));
        this.initForD2D();
    }

    public void initForD2D() {
    	chckbxIntensity.setVisible(false);
    	btnAddAsSerie.setVisible(false);
    	chckbxPDdata.setText("Show Rings");
        chckbxPDdata.setSelected(true);
//        btnAddAsSerie.setText("Add to Quicklist");
    }

    public ImagePanel getIpanel() {
        return this.ipanel;
    }

    public void setIpanel(ImagePanel ipanel) {
        this.ipanel = ipanel;
    }

	@Override
	public void searchPeaks() {

		if (this.getIpanel().getPatt2D().getPuntsCercles().isEmpty()) {
            log.info("Please select some peaks clicking in the image");
            return;
        }
		
        this.pm = new ProgressMonitor(this.DBdialog, "Searching for peak matching...", "", 0, 100);
        this.pm.setProgress(0);

        pBarDB.setString("Searching DB");
        pBarDB.setStringPainted(true);

        
        List<Double> dspList = new ArrayList<Double>();
        List<Double> intList = new ArrayList<Double>();
        
        final float[] t2deglist = new float[this.getIpanel().getPatt2D().getPuntsCercles().size()];
        final Iterator<PuntClick> itrPks = this.getIpanel().getPatt2D().getPuntsCercles().iterator();
        int n = 0;
        while (itrPks.hasNext()) {
            final PuntClick pc = itrPks.next();
            final double dsp = this.getIpanel().getPatt2D().calcDsp(pc.getT2rad());
            if (dsp > minDspacingToSearch) {
                dspList.add(dsp);
                t2deglist[n] = (float) FastMath.toDegrees(pc.getT2rad());
                n = n + 1;
            }
        }
        
        //TODO aqui podriem posar un flag boolean de fer o no servir intensitats... si es que va molt lent
        final double[] circleIntensities = ImgOps.radialIntegrationVarious2th(this.getIpanel().getPatt2D(), t2deglist, -1, false,
                false, true, this.searchDBwk);
        for (final double circleIntensitie : circleIntensities) {
            intList.add(circleIntensitie);
        }
        
        this.searchDBwk = new PDDatabase.searchDBWorker(dspList,intList);
        this.searchDBwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //log.debug("hello from propertyChange");
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    Database.this.pm.setProgress(progress);
                    pBarDB.setValue(progress);
                    Database.this.pm.setNote(String.format("%d%%\n", progress));
                    if (Database.this.pm.isCanceled() || Database.this.searchDBwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (Database.this.pm.isCanceled()) {
                            Database.this.searchDBwk.cancel(true);
                            Database.this.searchDBwk.setStop(true);
                            log.warning("search cancelled");
                        } else {
                            log.info("search finished!");
                            Database.this.loadSearchPeaksResults();
                        }
                        Database.this.pm.close();
                        pBarDB.setValue(100);
                        pBarDB.setStringPainted(false);
                        //startButton.setEnabled(true);
                    }
                }
            }
        });

        this.searchDBwk.execute();
		
	}

	@Override
	protected void actualitzaPlot() {
		this.getIpanel().actualitzarVista();
	}

	@Override
	protected void checkboxShowCanvia() {
		this.getIpanel().setShowDBCompoundRings(this.isShowDataPeaks(), this.getCurrentCompounds());
		this.getIpanel().actualitzarVista();
	}

	@Override
	protected String getWorkDir() {
		return D2Dplot_global.getWorkdir();
	}

	@Override
	protected void setWorkDir(File f) {
		D2Dplot_global.setWorkdir(f);
		
	}

	@Override
	protected String getDBFile() {
		return D2Dplot_global.DBfile;
	}

	@Override
	protected void setDBFile(String s) {
		D2Dplot_global.DBfile=s;
		
	}

	@Override
	protected void do_listCompounds_valueChanged(ListSelectionEvent arg0) {
        if (arg0.getValueIsAdjusting())
            return;
        final List<PDCompound>comp = this.getCurrentCompounds();
        this.getIpanel().setShowDBCompoundRings(this.isShowDataPeaks(), comp);
        if (comp != null) {
        	if (comp.size()>0) {
        		this.updateInfo(comp.get(0));	
        	}
    		if (comp.size()>4) {
            	log.info("Only the reflections of the first 4 selected compounds will be shown");
            }
        }
        this.getIpanel().actualitzarVista();
	}

	@Override
	protected void do_btnAddAsSerie_actionPerformed(ActionEvent e) { //ADD TO QUICKLIST
//        final PDCompound pdc = this.getCurrentCompound();
//        if (pdc != null) {
//            PDDatabase.addCompoundQL(this.getCurrentCompound());
//            MainFrame.updateQuickList();
//        }
		return; //no es fa res perque el boto esta amagat i no s'utilitza

	}

	@Override
	protected void do_chckbxIntensity_itemStateChanged(ItemEvent e) {
		return; //no es fa res perque el label esta amagat i no s'utilitza
	}
    
    
        
}