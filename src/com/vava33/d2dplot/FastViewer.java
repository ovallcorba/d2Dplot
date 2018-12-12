package com.vava33.d2dplot;

import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ChangeEvent;

public class FastViewer {

    private static final String className = "FastView";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private JDialog fastViewerDialog;
    private JPanel contentPane;
    ArrayList<File> files = new ArrayList<File>();
    private JSlider slider;
    private ImagePanel imgpanel;
    private int currentLoadedImage=-1;
    private JButton button;
    private Timer timer;
    private JButton btnStop;
    private JButton btnClose;

    /**
     * Create the frame.
     */
    public FastViewer(JFrame parent) {
    	fastViewerDialog = new JDialog(parent,"Fast Viewer",false);
    	fastViewerDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(FastViewer.class.getResource("/img/Icona.png")));
    	fastViewerDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	fastViewerDialog.setBounds(100, 100, 842, 646);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        fastViewerDialog.setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("", "[][][][grow][]", "[grow][]"));
        
        imgpanel = new ImagePanel();
        contentPane.add(imgpanel.getIpanelMain(), "cell 0 0 5 1,grow");
        
        JButton btnOpenImgs = new JButton("Open Imgs");
        btnOpenImgs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnOpenImgs_actionPerformed(arg0);
            }
        });
        contentPane.add(btnOpenImgs, "cell 0 1");
        
        slider = new JSlider();
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                do_slider_stateChanged(arg0);
            }
        });
        
        button = new JButton(">");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_button_actionPerformed(e);
            }
        });
        
        btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnStop_actionPerformed(e);
            }
        });
        contentPane.add(btnStop, "cell 1 1");
        contentPane.add(button, "cell 2 1");
        contentPane.add(slider, "cell 3 1,growx");
        
        btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		do_btnClose_actionPerformed(e);
        	}
        });
        contentPane.add(btnClose, "cell 4 1");
    }
    
    public void dispose() {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        fastViewerDialog.dispose();
    }
    
    public void showOpenImgsDialog() {
        this.do_btnOpenImgs_actionPerformed(null);
    }

    protected void do_btnOpenImgs_actionPerformed(ActionEvent arg0) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] fs = FileUtils.fchooserMultiple(fastViewerDialog, new File(D2Dplot_global.getWorkdir()), filt, 0,"Select 2DXRD data files to open");
        files = new ArrayList<File>(Arrays.asList(fs));
        if (fs.length<=0)return;
        slider.setMinimum(0);
        slider.setMaximum(files.size()-1);
        slider.setValue(0);
        loadPattern();
    }
    
    
    private void loadPattern(){
        log.debug("sliderValue="+slider.getValue());
        if (slider.getValue()==currentLoadedImage)return;
//        imgpanel.setImagePatt2D(this.fastEdfRead(files.get(slider.getValue())));
        imgpanel.setImagePatt2D(ImgFileUtils.readPatternFile(files.get(slider.getValue()), false));
    }
    
    
    protected void do_slider_stateChanged(ChangeEvent arg0) {
        this.loadPattern();
    }
    
    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            log.debug("Timer task started at:"+new Date());
            
            int ini = slider.getValue();
            int fin = slider.getMaximum();
            
            if (ini<fin){
                slider.setValue(ini+1);
            }else{
                timer.cancel();
                timer=null;
            }
        }
        
    
    }
    protected void do_button_actionPerformed(ActionEvent e) {
        if (timer==null){
            timer = new Timer();
            timer.scheduleAtFixedRate(new MyTimerTask(), 0, 1000);
            log.debug("TimerTask started");
        }
    }
    protected void do_btnStop_actionPerformed(ActionEvent e) {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
    }
    public void setVisible(boolean vis) {
    	fastViewerDialog.setVisible(vis);
    }
	protected void do_btnClose_actionPerformed(ActionEvent e) {
		this.dispose();
	}
}
