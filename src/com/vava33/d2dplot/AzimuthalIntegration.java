package com.vava33.d2dplot;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.EllipsePars;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.Patt2Dzone;
import com.vava33.d2dplot.auxi.Pattern1D;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.d2dplot.auxi.Pattern1D.PointPatt1D;
import com.vava33.d2dplot.d1dplot.DataPoint;
import com.vava33.d2dplot.d1dplot.DataSerie;
import com.vava33.d2dplot.d1dplot.PlotPanel;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Insets;

public class AzimuthalIntegration extends JFrame {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
	private JTextField txt_2t;
	private static String theta = "\u03B8";
//    private XYSeriesCollection dataset;
//    private JFreeChart chart;
	private ArrayList<Pattern1D> patt1D;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(AzimuthalIntegration.class.getName());

	private Pattern2D patt2D;
//	private ChartPanel chartPanel;
	private PlotPanel plotpanel;
	private JButton btn_save;
	private JLabel lblCakeIni;
	private JLabel lblCakeEnd;
	private JTextField txtT2w;
	private JTextField txtAzStep;
	private JButton btnIntegrar;
	
	private ImagePanel ip;
	private File maskfile;
	
	/**
	 * Create the frame.
	 */
	public AzimuthalIntegration(ImagePanel ipanel) {
	    this.ip=ipanel;
	    this.inicia();
		setTitle("Azimuthal Integration");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 940, 540);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Help_dialog.class.getResource("/img/Icona.png")));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow][][][grow][][grow][grow][]", "[][grow]"));
		
		JLabel lbltini = new JLabel("2θ");
		contentPane.add(lbltini, "cell 0 0,alignx right");
		
		txt_2t = new JTextField();
		txt_2t.setText("1.00");
		contentPane.add(txt_2t, "cell 1 0 2 1,growx");
		
		lblCakeIni = new JLabel("2θ window");
		contentPane.add(lblCakeIni, "cell 3 0,alignx trailing");
		
		txtT2w = new JTextField();
		txtT2w.setText("0.1");
		contentPane.add(txtT2w, "cell 4 0,growx");
		
		btnIntegrar = new JButton("Integrate");
		btnIntegrar.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		        do_btnIntegrartilt_actionPerformed(arg0);
		    }
		});
		
		lblCakeEnd = new JLabel("Azim step (º)");
		contentPane.add(lblCakeEnd, "cell 5 0,alignx trailing");
		
		txtAzStep = new JTextField();
		txtAzStep.setText("5");
		contentPane.add(txtAzStep, "cell 6 0,growx");
		contentPane.add(btnIntegrar, "cell 7 0,growx");
		
		btn_save = new JButton("Save");
		btn_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_btn_save_actionPerformed(arg0);
			}
		});
		contentPane.add(btn_save, "cell 8 0,growx");
		
		plotpanel = new PlotPanel();
		contentPane.add(plotpanel, "cell 0 1 9 1,grow");
		patt1D = new ArrayList<Pattern1D>();
	}
	
	public void inicia(){
	    this.patt2D=ip.getPatt2D();
	}
	
    private void plotPattern(Pattern1D p, boolean norm, boolean appendPatt, String seriesName){
        
        com.vava33.d2dplot.d1dplot.Pattern1D p1 = new com.vava33.d2dplot.d1dplot.Pattern1D();
        
        DataSerie ds = new DataSerie();
        
        ds.setWavelength(this.patt2D.getWavel());
        
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
        
        if (appendPatt!=true){
            plotpanel.getPatterns().clear();
            plotpanel.getPatterns().add(p1);
        }else{
            plotpanel.getPatterns().add(p1);    
        }
        plotpanel.fitGraph();
    }
    
	protected void do_btn_save_actionPerformed(ActionEvent arg0) {
		if(patt1D.size()>0){
		    File fileout;
            FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("Data file (2T I ESD)","dat","xy")};
	        fileout = FileUtils.fchooser(null,new File(MainFrame.getWorkdir()), filter, true);
	        if(!FileUtils.getExtension(fileout).equalsIgnoreCase("dat")||!FileUtils.getExtension(fileout).equalsIgnoreCase("xy")){
	            fileout = FileUtils.canviExtensio(fileout, "dat");  
	        }
		    if (fileout!=null){
		        this.savePatterns(fileout);
		    }
		}
	}
	
	public void savePatterns(File fileout){
        if (patt1D.size()==1){
            patt1D.get(0).writeDAT(fileout,patt2D.getImgfile().toString());
            log.info(String.format("file %s written", fileout.toString()));
        }else{
            for (int i=0;i<patt1D.size();i++){
                File fout = FileUtils.canviNomFitxer(fileout, FileUtils.getFNameNoExt(fileout.getName())+String.format("_azbin%02d", i));
                patt1D.get(i).writeDAT(fout, patt2D.getImgfile().toString());
                log.info(String.format("file %s written", fout.toString()));
            }
        }
	}
	
    public float getTxtT2(){
        try{
            return Float.parseFloat(txt_2t.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.info("error parsing t2");
        }
        return -1f;
    }
    
    public float getTxtT2w(){
        try{
            return Float.parseFloat(txtT2w.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.info("error parsing T2w");
        }
        return -1f;
    }
    
    public float getTxtAzStep(){
        try{
            return Float.parseFloat(txtAzStep.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.info("error parsing Azimuth step");
        }
        return -1f;
    }
    
    public void do_btnIntegrartilt_actionPerformed(ActionEvent arg0) {
        this.patt1D.clear();
        
        if(patt2D==null)return;
        float t2=10.0f;
        float t2w=0.2f;
        float Azstep=1.0f;
        try{
            t2=Float.parseFloat(txt_2t.getText());
            t2w=Float.parseFloat(txtT2w.getText());
            Azstep=Float.parseFloat(txtAzStep.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Check input angles and step",
                    "Incorrect values",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //busquem el pixel en la vertical que correspon al 2t entrat
//        int j = patt2D.getCentrXI();
//        float minstep = patt2D.calcMinStepsizeBy2Theta4Directions();
//        int pX = -1;
//        int pY = -1;
//        for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
//            double t2i = patt2D.calc2T(j, i, true);
//            if (FastMath.abs(t2i-t2)<=minstep) {
//                pX = j;
//                pY = i;
//                break;
//            }
//        }
//        if ((pX < 0)||(pY<0)) {
//            log.info("2theta not found");
//            return;
//        }
        
        EllipsePars e = ImgOps.getElliPars(patt2D, FastMath.toRadians(t2));
        ArrayList<Point2D.Float> punts = e.getEllipsePoints(0, 360, Azstep);
        
        Iterator<Point2D.Float> itrp = punts.iterator();
        
        Pattern1D patt1Daz = new Pattern1D(0,360,Azstep); 
        int count = 0;
        while (itrp.hasNext()) {
            Point2D.Float p = itrp.next();
            Patt2Dzone z = ImgOps.YarcTilt(patt2D, FastMath.round(p.x), FastMath.round(p.y), t2w/2.f, Azstep/2.f, true, 0, false);
            z.getYsum();
            patt1Daz.getPoint(count).setIntensity(z.getYsum());
            patt1Daz.getPoint(count).setCounts(z.getYsum());
            patt1Daz.getPoint(count).setNpix(1);
            patt1Daz.getPoint(count).setDesv(z.getYmeandesv());
            count = count +1;
        }
        
        //ja tindrem el pattern1D
        String comment = String.format("Azimuthal integration 0-360º for t2=%.2f with t2win=%.2f and AzStep=.2f", t2,t2w,Azstep);
        patt1Daz.setComment(comment);
        this.patt1D.add(patt1Daz);
        this.plotPattern(patt1D.get(patt1D.size()-1),false,false,comment);

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
}
