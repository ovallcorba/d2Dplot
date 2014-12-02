package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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

import vava33.plot2d.auxi.Pattern1D;
import vava33.plot2d.auxi.Pattern1D.PointPatt1D;
import vava33.plot2d.auxi.Pattern2D;

import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;

public class IntegracioRadial extends JFrame {

	private JPanel contentPane;
	private JTextField txt_2ti;
	private JTextField txt_2tf;
	private JTextField txt_step;
	private static String theta = "\u03B8";
    private XYSeries pattplot; //diagrama xy 
    private XYSeriesCollection dataset;
    private JFreeChart chart;
	private Pattern1D patt1D;
	
	private Pattern2D patt2D;
	private ChartPanel chartPanel;
	private JButton btn_save;
	
	/**
	 * Create the frame.
	 */
	public IntegracioRadial(Pattern2D patt) {
		this.patt2D=patt;
		setTitle("Radial Integration (ON DEVELOPEMENT, USE WITH CAUTION)");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 726, 456);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Help_dialog.class.getResource("/img/Icona.png")));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][][][][][][grow]", "[][][][grow]"));
		
		JLabel lbltini = new JLabel("2"+theta+" ini");
		contentPane.add(lbltini, "cell 0 0,alignx right");
		
		txt_2ti = new JTextField();
		txt_2ti.setBackground(Color.WHITE);
		txt_2ti.setText("1.0");
		contentPane.add(txt_2ti, "cell 1 0,alignx left");
		txt_2ti.setColumns(10);
		
		JLabel lbltfin = new JLabel("2"+theta+" end");
		contentPane.add(lbltfin, "cell 2 0,alignx right");
		
		txt_2tf = new JTextField();
		txt_2tf.setBackground(Color.WHITE);
		txt_2tf.setText("40.0");
		contentPane.add(txt_2tf, "cell 3 0,alignx left");
		txt_2tf.setColumns(10);
		
		JLabel lblStep = new JLabel("Step");
		contentPane.add(lblStep, "cell 4 0,alignx right");
		
		txt_step = new JTextField();
		txt_step.setBackground(Color.WHITE);
		txt_step.setText("0.01");
		contentPane.add(txt_step, "cell 5 0,alignx left");
		txt_step.setColumns(10);
		
		JButton btnIntegrate = new JButton("Integrate");
		btnIntegrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_btnIntegrate_actionPerformed(arg0);
			}
		});
		contentPane.add(btnIntegrate, "cell 6 0,alignx left,aligny top");
		
		btn_save = new JButton("Save");
		btn_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_btn_save_actionPerformed(arg0);
			}
		});
		contentPane.add(btn_save, "cell 7 0,alignx left");
		
		chartPanel = new ChartPanel((JFreeChart) null);
		chartPanel.setVerticalAxisTrace(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setMaximumDrawWidth(2500);
		chartPanel.setMaximumDrawHeight(1800);
		chartPanel.setHorizontalAxisTrace(true);
		contentPane.add(chartPanel, "cell 0 1 8 3,grow");
		GridBagLayout gbl_chartPanel = new GridBagLayout();
		gbl_chartPanel.columnWidths = new int[]{0};
		gbl_chartPanel.rowHeights = new int[]{0};
		gbl_chartPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_chartPanel.rowWeights = new double[]{Double.MIN_VALUE};
		chartPanel.setLayout(gbl_chartPanel);
		
		double optstep = FastMath.atan(patt.getPixSx()/patt.getDistMD());
		VavaLogger.LOG.info("DistMD= "+patt.getDistMD()+" pixsx= "+patt.getPixSx());
		txt_step.setText(FileUtils.dfX_3.format(FastMath.toDegrees(optstep)));
		txt_2tf.setText(FileUtils.dfX_2.format(patt2D.getMax2TdegCircle()));

	}

	protected void do_btnIntegrate_actionPerformed(ActionEvent arg0) {
		if(patt2D==null)return;
		float t2ini=1.0f;
		float t2fin=40.0f;
		float stepsize=-0.01f;
		try{
			t2ini=Float.parseFloat(txt_2ti.getText());
			t2fin=Float.parseFloat(txt_2tf.getText());
			stepsize=Float.parseFloat(txt_step.getText());
		}catch(Exception e){
			e.printStackTrace();
	    	JOptionPane.showMessageDialog(this,
	    		    "Check input angles and step",
	    		    "Incorrect values",
	    		    JOptionPane.ERROR_MESSAGE);
	    	return;
		}
		t2fin = FastMath.min(t2fin, patt2D.getMax2Tdeg());
		//this.patt1D = patt2D.intRad(t2ini, t2fin, stepsize,false);
		//this.patt1D = patt2D.intRadPond(t2ini, t2fin, stepsize,false);
		this.patt1D = patt2D.intRadCircles(t2ini, t2fin,stepsize);
		this.plotPattern(patt1D,true);
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
//        	VavaLogger.LOG.info(point.getT2()+","+point.getCounts());
        	if(norm){
        	    pattplot.add(point.getT2(),(float)point.getCounts()/(float)point.getNpix());
        	}else{
        	    pattplot.add(point.getT2(),point.getCounts());    
        	}
        }
        
//        pattplot.add(0,0);
//        pattplot.add(1,1);
//        pattplot.add(2,2);
//        pattplot.add(3,3);
//        pattplot.add(4,4);
//        pattplot.add(5,5);
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
			patt1D.writeXYnorm(null,patt2D.getImgfile().toString());
		}
	}
}
