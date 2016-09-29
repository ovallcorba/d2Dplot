package vava33.d2dplot;

import java.awt.Color;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.auxi.ImgOps;
import vava33.d2dplot.auxi.Pattern1D;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.Pattern1D.PointPatt1D;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.JCheckBox;

public class IntegracioRadial extends JFrame {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
	private JTextField txt_2ti;
	private JTextField txt_2tf;
	private JTextField txt_step;
	private static String theta = "\u03B8";
    private XYSeries pattplot; //diagrama xy 
    private XYSeriesCollection dataset;
    private JFreeChart chart;
	private Pattern1D patt1D;
    private static VavaLogger log = D2Dplot_global.log;

	private Pattern2D patt2D;
	private ChartPanel chartPanel;
	private JButton btn_save;
	private JLabel lblCakeIni;
	private JLabel lblCakeEnd;
	private JTextField txtCakein;
	private JTextField txtCakefin;
	private JButton btnIntegrar;
	private JCheckBox chckbxCorrlp;
	private JCheckBox chckbxCorriang;
	private JCheckBox chckbxUsetilt;
	
	/**
	 * Create the frame.
	 */
	public IntegracioRadial(Pattern2D patt) {
		this.patt2D=patt;
		setTitle("Radial Integration");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 726, 456);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Help_dialog.class.getResource("/img/Icona.png")));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow][][][grow][grow]", "[74.00][][][grow]"));
		
		JLabel lbltini = new JLabel("2"+theta+" ini");
		contentPane.add(lbltini, "cell 0 0,alignx right");
		
		txt_2ti = new JTextField();
		txt_2ti.setText("1.0");
		contentPane.add(txt_2ti, "cell 1 0,alignx left");
		txt_2ti.setColumns(10);
		
		lblCakeIni = new JLabel("Cake ini");
		contentPane.add(lblCakeIni, "cell 2 0,alignx trailing");
		
		txtCakein = new JTextField();
		txtCakein.setText("0");
		contentPane.add(txtCakein, "cell 3 0,growx");
		txtCakein.setColumns(10);
		
		chckbxCorriang = new JCheckBox("Incident angle correction");
		chckbxCorriang.setSelected(true);
		contentPane.add(chckbxCorriang, "cell 4 0");
		
		btnIntegrar = new JButton("Integrate");
		btnIntegrar.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		        do_btnIntegrartilt_actionPerformed(arg0);
		    }
		});
		contentPane.add(btnIntegrar, "cell 5 0,growx");
		
		JLabel lblStep = new JLabel("Step");
		contentPane.add(lblStep, "cell 0 1,alignx right");
		
		txt_step = new JTextField();
		txt_step.setText("0.01");
		contentPane.add(txt_step, "cell 1 1,alignx left");
		txt_step.setColumns(10);
		
		lblCakeEnd = new JLabel("Cake end");
		contentPane.add(lblCakeEnd, "cell 2 1,alignx trailing");
		
		txtCakefin = new JTextField();
		txtCakefin.setText("360");
		contentPane.add(txtCakefin, "cell 3 1,growx");
		txtCakefin.setColumns(10);
		
		chckbxUsetilt = new JCheckBox("Use tilt info");
		chckbxUsetilt.setSelected(true);
		contentPane.add(chckbxUsetilt, "cell 4 1");
		
		btn_save = new JButton("Save");
		btn_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_btn_save_actionPerformed(arg0);
			}
		});
		contentPane.add(btn_save, "cell 5 1,growx");
		
		JLabel lbltfin = new JLabel("2"+theta+" end");
		contentPane.add(lbltfin, "cell 0 2,alignx right");
		
		txt_2tf = new JTextField();
		txt_2tf.setText("40.0");
		contentPane.add(txt_2tf, "cell 1 2,alignx left");
		txt_2tf.setColumns(10);
		txt_2tf.setText(FileUtils.dfX_2.format(patt2D.getMax2TdegCircle()));
		
		chckbxCorrlp = new JCheckBox("LP correction");
		contentPane.add(chckbxCorrlp, "cell 4 2");
		
		chartPanel = new ChartPanel((JFreeChart) null);
		chartPanel.setVerticalAxisTrace(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setMaximumDrawWidth(2500);
		chartPanel.setMaximumDrawHeight(1800);
		chartPanel.setHorizontalAxisTrace(true);
		contentPane.add(chartPanel, "cell 0 3 6 1,grow");
		
		double optstep = FastMath.atan(patt.getPixSx()/patt.getDistMD());
        txt_step.setText(FileUtils.dfX_3.format(FastMath.toDegrees(optstep)));
		log.debug("DistMD= "+patt.getDistMD()+" pixsx= "+patt.getPixSx());

	}
	
    private void plotPattern(Pattern1D p, boolean norm){
        
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setHorizontalAxisTrace(true);
        chartPanel.setVerticalAxisTrace(true);
        chartPanel.setPopupMenu(null);

        pattplot = new XYSeries("1D Pattern");
        dataset = new XYSeriesCollection();
        
        Iterator<PointPatt1D> itp = p.getPoints().iterator();
        while(itp.hasNext()){
        	PointPatt1D point = itp.next();
            log.fine(point.getT2()+","+point.getCounts());
        	if(norm){
        	    pattplot.add(point.getT2(),(float)point.getCounts()/(float)point.getNpix());
        	}else{
        	    pattplot.add(point.getT2(),point.getCounts());    
        	}
        }
        
        dataset.addSeries(pattplot);
        
        // Generate the graph
        chart = ChartFactory.createXYLineChart(
        "Radial integration",                  // Title
        "2"+theta,                      // x-axis Label
        "Counts",                  // y-axis Label
        dataset,                   // Dataset
        PlotOrientation.VERTICAL,  // Plot Orientation
        true,                      // Show Legend
        false,                     // Use tooltips
        false                      // Configure chart to generate URLs?
        );
        
        chart.getXYPlot().setBackgroundPaint(new Color(255,255,255));
        chart.setBackgroundPaint(new Color(240,240,240));
        chart.getXYPlot().setRangeGridlinePaint(Color.DARK_GRAY);
        chart.getXYPlot().setDomainGridlinePaint(Color.DARK_GRAY);
        chartPanel.setChart(chart);  //el pintem (es fa reset de zoom)
    }
    
	protected void do_btn_save_actionPerformed(ActionEvent arg0) {
		if(patt1D!=null){
		    File fileout;
            FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("Data file (2T I ESD)","dat","xy")};
	        fileout = FileUtils.fchooser(null,new File(MainFrame.getWorkdir()), filter, true);
	        if(!FileUtils.getExtension(fileout).equalsIgnoreCase("dat")||!FileUtils.getExtension(fileout).equalsIgnoreCase("xy")){
	            fileout = FileUtils.canviExtensio(fileout, "dat");  
	        }
		    if (fileout!=null){
	            patt1D.writeXYnorm(fileout,patt2D.getImgfile().toString());		        
		    }
		}
	}
	
    protected void do_btnIntegrartilt_actionPerformed(ActionEvent arg0) {
        
        if(patt2D==null)return;
        float t2ini=1.0f;
        float t2fin=40.0f;
        float stepsize=-0.01f;
        try{
            t2ini=Float.parseFloat(txt_2ti.getText());
            t2fin=Float.parseFloat(txt_2tf.getText());
            stepsize=Float.parseFloat(txt_step.getText());
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Check input angles and step",
                    "Incorrect values",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        //comprovacio que el minim stepsize no sigui inferior al entrepixels
        float minstep = this.patt2D.calcMinStepsizeBy2Theta4Directions();
        if (stepsize < minstep){
            stepsize = minstep;
        }
        
        //t2fin dins dels limits:
        t2fin = FastMath.min(t2fin, patt2D.getMax2TdegCircle() - 2*stepsize);
        
        //cake
        float cakein = -1f;
        float cakeout = -1f;
        try{
            cakein=Float.parseFloat(txtCakein.getText());
            cakeout=Float.parseFloat(txtCakefin.getText());
        }catch(Exception e){
            log.info("Taking default cake value, full pattern (0-360)");
        }       
        
        try{
            boolean usetilt = chckbxUsetilt.isSelected();
            boolean corrLP = chckbxCorrlp.isSelected();
            boolean corrInAng = chckbxCorriang.isSelected();
            this.patt1D = ImgOps.radialIntegration(patt2D, t2ini, t2fin,stepsize, cakein, cakeout,usetilt,corrLP,corrInAng);    
        }catch(Exception e){
            log.warning("Error during radial integration");
        }
        
        this.plotPattern(patt1D,true);
        
    }
}
