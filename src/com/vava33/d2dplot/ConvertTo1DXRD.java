package com.vava33.d2dplot;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.Pattern1D;
import com.vava33.d2dplot.auxi.Pattern1D.PointPatt1D;
import com.vava33.d2dplot.d1dplot.DataPoint;
import com.vava33.d2dplot.d1dplot.DataSerie;
import com.vava33.d2dplot.d1dplot.PlotPanel;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Insets;

public class ConvertTo1DXRD {

	private JDialog D1dialog;
	private JPanel contentPane;
	private JTextField txt_2ti;
	private JTextField txt_2tf;
	private JTextField txt_step;
	private static String theta = "\u03B8";
	private ArrayList<Pattern1D> patt1D;
    private static final String className = "1DPXRD";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    
//	private Pattern2D patt2D;
	private PlotPanel plotpanel;
	private JButton btn_save;
	private JLabel lblCakeIni;
	private JLabel lblCakeEnd;
	private JTextField txtCakein;
	private JTextField txtCakefin;
	private JButton btnIntegrar;
	private JLabel lblAzimBins;
	private JTextField txtAzimBins;
	private JButton btnSetMin;
	private JButton btnSetMax;
	private JLabel lblSubtractI;
	private JTextField txtZeroval;
	
	private ImagePanel ip;
	private File maskfile;
	private JButton btnClose;
	
	com.vava33.d2dplot.d1dplot.Pattern1D p1;
	
	/**
	 * Create the frame.
	 */
	public ConvertTo1DXRD(JFrame parent, ImagePanel ipanel) {
	    this.ip=ipanel;
	    D1dialog = new JDialog(parent,"Integration of 2DXRD to 1DXRD",false);
//	    this.inicia();
	    D1dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    D1dialog.setBounds(100, 100, 940, 540);
	    D1dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(Help.class.getResource("/img/Icona.png")));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		D1dialog.setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow][][][grow][][grow][grow]", "[][][][grow][]"));
		
		JLabel lbltini = new JLabel("2"+theta+" ini");
		contentPane.add(lbltini, "cell 0 0,alignx right");
		
		txt_2ti = new JTextField();
		txt_2ti.setText("1.00");
		contentPane.add(txt_2ti, "cell 1 0 2 1,growx");
		
		lblCakeIni = new JLabel("Cake ini");
		contentPane.add(lblCakeIni, "cell 3 0,alignx trailing");
		
		txtCakein = new JTextField();
		txtCakein.setText("0");
		contentPane.add(txtCakein, "cell 4 0,growx");
		
		btnIntegrar = new JButton("Integrate");
		btnIntegrar.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		        do_btnIntegrartilt_actionPerformed(arg0);
		    }
		});
		
		lblSubtractI = new JLabel("Add I");
		lblSubtractI.setToolTipText("Subtract intensity to all pixels (useful if detector is adding a value to avoid negative intensities)");
		contentPane.add(lblSubtractI, "cell 5 0,alignx trailing");
		
		txtZeroval = new JTextField();
		txtZeroval.setText("-9.5");
		contentPane.add(txtZeroval, "cell 6 0,growx");
		contentPane.add(btnIntegrar, "cell 7 0,growx");
		
		JLabel lblStep = new JLabel("Step");
		contentPane.add(lblStep, "cell 0 1,alignx right");
		
		txt_step = new JTextField();
		txt_step.setText("0.01");
		contentPane.add(txt_step, "cell 1 1,growx");
		
		btnSetMin = new JButton("set Min");
		btnSetMin.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		        do_btnSetMin_actionPerformed(arg0);
		    }
		});
		btnSetMin.setMargin(new Insets(2, 2, 2, 2));
		contentPane.add(btnSetMin, "cell 2 1");
		
		lblCakeEnd = new JLabel("Cake end");
		contentPane.add(lblCakeEnd, "cell 3 1,alignx trailing");
		
		txtCakefin = new JTextField();
		txtCakefin.setText("360");
		contentPane.add(txtCakefin, "cell 4 1,growx");
		
		btn_save = new JButton("Save");
		btn_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_btn_save_actionPerformed(arg0);
			}
		});
		contentPane.add(btn_save, "cell 7 1,growx");
		
		JLabel lbltfin = new JLabel("2"+theta+" end");
		contentPane.add(lbltfin, "cell 0 2,alignx right");
		
		txt_2tf = new JTextField();
		txt_2tf.setText("40.0");
		contentPane.add(txt_2tf, "cell 1 2,growx");
		
		btnSetMax = new JButton("set Max");
		btnSetMax.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        do_btnSetMax_actionPerformed(e);
		    }
		});
		btnSetMax.setMargin(new Insets(2, 2, 2, 2));
		contentPane.add(btnSetMax, "cell 2 2");
		
		lblAzimBins = new JLabel("Azim Bins");
		contentPane.add(lblAzimBins, "cell 3 2,alignx trailing");
		
		txtAzimBins = new JTextField();
		txtAzimBins.setText("1");
		contentPane.add(txtAzimBins, "cell 4 2,growx");
		
		plotpanel = new PlotPanel();
		contentPane.add(plotpanel.getPlotPanel(), "cell 0 3 8 1,grow");
		
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_btnClose_actionPerformed(e);
			}
		});
		contentPane.add(btnClose, "cell 7 4,alignx right,aligny center");
		
		btnSetMin.doClick();
		btnSetMax.doClick();
		patt1D = new ArrayList<Pattern1D>();
		p1 = new com.vava33.d2dplot.d1dplot.Pattern1D();
	}
	
//	public void inicia(){
//	    this.patt2D=ip.getPatt2D();
//	}
    
	protected void do_btn_save_actionPerformed(ActionEvent arg0) {
		if(patt1D.size()>0){
		    File fileout;
            FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("Data file (2T I ESD)","dat","xy")};
	        fileout = FileUtils.fchooserSaveAsk(null,new File(MainFrame.getWorkdir()), filter, "dat"); 
//	        if(!FileUtils.getExtension(fileout).equalsIgnoreCase("dat")||!FileUtils.getExtension(fileout).equalsIgnoreCase("xy")){
//	            fileout = FileUtils.canviExtensio(fileout, "dat");  
//	        }
		    if (fileout!=null){
		        this.savePatterns(fileout);
		    }
		}
	}
	
	
	public void savePatterns(File fileout){
        if (patt1D.size()==1){
            patt1D.get(0).writeDAT(fileout,ip.getPatt2D().getImgfile().toString());
            log.info(String.format("file %s written", fileout.toString()));
        }else{
            for (int i=0;i<patt1D.size();i++){
                File fout = FileUtils.canviNomFitxer(fileout, FileUtils.getFNameNoExt(fileout.getName())+String.format("_azbin%02d", i));
                patt1D.get(i).writeDAT(fout, ip.getPatt2D().getImgfile().toString());
                log.info(String.format("file %s written", fout.toString()));
            }
        }
	}
	
	public void setTxtT2i(float t2i){
	    txt_2ti.setText(Float.toString(t2i));
	}
	public void setTxtT2f(float t2f){
        txt_2tf.setText(Float.toString(t2f));
    }
	public void setTxtStep(float step){
        txt_step.setText(Float.toString(step));
    }
    public void setTxtCakeIn(float cakeinDeg){
        txtCakein.setText(Float.toString(cakeinDeg));
    }
    public void setTxtCakeFin(float cakefinDeg){
        txtCakefin.setText(Float.toString(cakefinDeg));
    }
    public void setTxtSubadu(float subadu){
        txtZeroval.setText(Float.toString(subadu));
    }
    public void setTxtAzimbins(int abins){
        txtAzimBins.setText(Integer.toString(abins));
    }
	
    public float getTxtT2i(){
        try{
            return Float.parseFloat(txt_2ti.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error parsing initial 2theta");
        }
        return -1f;
    }
    
    public float getTxtT2f(){
        try{
            return Float.parseFloat(txt_2tf.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error parsing final 2theta");
        }
        return -1f;
    }
    
    public float getTxtStep(){
        try{
            return Float.parseFloat(txt_step.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error parsing stepsize");
        }
        return -1f;
    }
    
    public float getTxtCakein(){
        try{
            return Float.parseFloat(txtCakein.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error parsing start azimuth");
        }
        return -1f;
    }
    
    public float getTxtCakefin(){
        try{
            return Float.parseFloat(txtCakefin.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error parsing end azimuth");
        }
        return -1f;
    }
    
    public float getTxtZeroval(){
        try{
            return Float.parseFloat(txtZeroval.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error parsing zero intensity level");
        }
        return -1f;
    }
    
    public int getTxtAzimBins(){
        try{
            return Integer.parseInt(txtAzimBins.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error parsing Azimuthal bins");
        }
        return -1;
    }
    
    private void plotPattern(Pattern1D p, boolean norm, boolean appendPatt, String seriesName){
        
      DataSerie ds = new DataSerie();
      
      ds.setWavelength(ip.getPatt2D().getWavel());
      
      Iterator<PointPatt1D> itp = p.getPoints().iterator();
      while(itp.hasNext()){
          PointPatt1D point = itp.next();
          log.fine(point.getT2()+","+point.getCounts());
          if(norm){
              ds.addPoint(new DataPoint(point.getT2(),(float)point.getCounts()/(float)point.getNpix(),0.f));
          }else{
              ds.addPoint(new DataPoint(point.getT2(),(float)point.getCounts(),0.f));
          }
      }
      
      ds.setSerieName(seriesName);
      p1.AddDataSerie(ds);
      plotpanel.fitGraph();
  }
    
    public void do_btnIntegrartilt_actionPerformed(ActionEvent arg0) {
        this.patt1D.clear();
        if(ip.getPatt2D()==null)return;
        if((!ip.getPatt2D().checkIfDistMD())||(!ip.getPatt2D().checkIfPixSize())) {
        	log.warning("Instrumental parameters missing");
        	return;
        }
        float t2ini=1.0f;
        float t2fin=40.0f;
        float stepsize=-0.01f;
        try{
            t2ini=Float.parseFloat(txt_2ti.getText());
            t2fin=Float.parseFloat(txt_2tf.getText());
            stepsize=Float.parseFloat(txt_step.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            JOptionPane.showMessageDialog(D1dialog,
                    "Check input angles and step",
                    "Incorrect values",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        //comprovacio que el minim stepsize no sigui inferior al entrepixels TODO: NOV18 desactivat
//        float minstep = this.patt2D.calcMinStepsizeBy2Theta4Directions();
//        if (stepsize < minstep){
//            stepsize = minstep;
//        }
        
        //t2fin dins dels limits: --ho trec a veure que passa (util quan el centre no es al centre)
//        t2fin = FastMath.min(t2fin, patt2D.getMax2TdegCircle() - 2*stepsize);
        
        //cake
        float cakein = -1f;
        float cakeout = -1f;
        try{
            cakein=Float.parseFloat(txtCakein.getText());
            cakeout=Float.parseFloat(txtCakefin.getText());
        }catch(Exception e){
            log.info("Taking default cake value, full pattern (0-360)");
        }       
        
        //subadu
        float subadu = 0.f;
        try{
            subadu=Float.parseFloat(txtZeroval.getText());
        }catch(Exception e){
            log.info("Taking default zeroIVal value, 0");
        } 
        
        //AZIM BINS        
        int azimBins = 1;
        try{
            azimBins=Integer.parseInt(txtAzimBins.getText());
        }catch(Exception e){
            log.info("Taking default azimuthal bins value, 1");
        }  
        float azimRange = cakeout - cakein;
        if (cakeout<cakein) azimRange = 360+azimRange;
        float azimInc = azimRange/(float)azimBins;

        boolean corrLP = true;
        boolean corrInAng = false;         
        
        
        //NOW WE INTEGRATE ALL THE BINS
        p1.removeAllSeries();
		plotpanel.getPatterns().clear();
		plotpanel.getPatterns().add(p1);
        for (int k = 0; k<azimBins; k++){
            
            float azIni = cakein + azimInc * k;
            if (azIni > 360) azIni = azIni - 360f;
            float azFin = azIni + azimInc;
            if (azFin > 360) azFin = azFin - 360f;
            
            try{
                Pattern1D p1D = ImgOps.radialIntegration(ip.getPatt2D(), t2ini, t2fin,stepsize, azIni, azFin,corrLP,corrInAng,subadu);
                String comment = String.format("I vs. 2Theta [deg] t2i step t2f: %.4f %.4f %.4f Wave: %.4f Azim: %.2f %.2f addI: %.1f", t2ini,stepsize,t2fin,ip.getPatt2D().getWavel(),stepsize,azIni,azFin,subadu);
                p1D.setComment(comment);
                this.patt1D.add(p1D);
            }catch(Exception e){
                log.warning("Error during radial integration");
            }
            
            if (k==0) {//new plot
                this.plotPattern(patt1D.get(patt1D.size()-1),true,false,String.format("azRange= %.1f to %.1f", azIni,azFin));
            }else{//append
                this.plotPattern(patt1D.get(patt1D.size()-1),true,true,String.format("azRange= %.1f to %.1f", azIni,azFin));   
            }
        }

    }
    protected void do_btnSetMin_actionPerformed(ActionEvent arg0) {
        txt_step.setText(FileUtils.dfX_4.format(ip.getPatt2D().calcMinStepsizeBy2Theta4Directions()));
    }
    protected void do_btnSetMax_actionPerformed(ActionEvent e) {
        txt_2tf.setText(FileUtils.dfX_2.format(ip.getPatt2D().getMax2TdegCircle() - 2*ip.getPatt2D().calcMinStepsizeBy2Theta4Directions()));
    }

    public ArrayList<Pattern1D> getPatt1D() {
        return patt1D;
    }

    public void setPatt1D(ArrayList<Pattern1D> patt1d) {
        patt1D = patt1d;
    }

    public File getMaskfile() {
        return maskfile;
    }

    public void setMaskfile(File maskfile) {
        this.maskfile = maskfile;
    }
    
    public void dispose() {
    	D1dialog.dispose();
    }
    
    public void setVisible(boolean vis) {
    	D1dialog.setVisible(vis);
    }
	protected void do_btnClose_actionPerformed(ActionEvent e) {
		this.dispose();
	}
}
