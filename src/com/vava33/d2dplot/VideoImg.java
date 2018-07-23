package com.vava33.d2dplot;

import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ChangeEvent;

public class VideoImg extends JDialog {

    private static final long serialVersionUID = 1L;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(VideoImg.class.getName());
    private JPanel contentPane;
//    ArrayList<Pattern2D> patts = new ArrayList<Pattern2D>();
    ArrayList<File> files = new ArrayList<File>();
    private JSlider slider;
    private ImagePanel imgpanel;
    private int currentLoadedImage=-1;
    private JButton button;
    private Timer timer;
    private JButton btnStop;

    /**
     * Create the frame.
     */
    public VideoImg() {
        setTitle("Fast Viewer");
        setIconImage(Toolkit.getDefaultToolkit().getImage(VideoImg.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 842, 646);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("", "[][][][grow]", "[grow][]"));
        
        imgpanel = new ImagePanel(true);
        contentPane.add(imgpanel, "cell 0 0 4 1,grow");
        
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
    }
    
    @Override
    public void dispose() {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        super.dispose();
    }
    
    public void showOpenImgsDialog() {
        this.do_btnOpenImgs_actionPerformed(null);
    }

    protected void do_btnOpenImgs_actionPerformed(ActionEvent arg0) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] fs = FileUtils.fchooserMultiple(this, new File(D2Dplot_global.getWorkdir()), filt, 0,"Select 2DXRD data files to open");
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
        imgpanel.setImagePatt2D(this.fastEdfRead(files.get(slider.getValue())));
    }
    
    private Pattern2D fastEdfRead(File d2File){
        Pattern2D patt2D = null;
        long start = System.nanoTime(); // control temps
        int headerSize = 0;
        int binSize = 0;
        float pixSizeX = 0, pixSizeY = 0;
        float distOD = 0;
        float beamCX = 0, beamCY = 0, wl = 0;
        int dimX = 0, dimY = 0, maxI = 0, minI = 9999999;
        float omeIni = 0, omeFin = 0, acqTime = -1;
        float tilt = 0, rot = 0;

        
        // primer treiem la info de les linies de text
        try {
            Scanner scD2file = new Scanner(new BufferedReader(new FileReader(d2File)));
//            log.debug(scD2file.toString());
            for (int i = 0; i < 50; i++) {
                if (scD2file.hasNextLine()) {
                    String line = scD2file.nextLine();
                    log.fine("edf line="+line);
                    int iigual = line.indexOf("=") + 1;
                    if (FileUtils.containsIgnoreCase(line, "Size =")) {
                        binSize = Integer.parseInt(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "Dim_1")) {
                        dimX = Integer.parseInt(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "Dim_2")) {
                        dimY = Integer.parseInt(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "beam_center_x")) {
                        beamCX = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "beam_center_y")) {
                        beamCY = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "pixel_size_x")
                            || FileUtils
                                    .containsIgnoreCase(line, "pixelsize_x")) {
                        pixSizeX = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                        pixSizeX = pixSizeX / 1000.f;
                    }
                    if (FileUtils.containsIgnoreCase(line, "pixel_size_y")
                            || FileUtils
                                    .containsIgnoreCase(line, "pixelsize_y")) {
                        pixSizeY = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                        pixSizeY = pixSizeY / 1000.f;
                    }
                    if (FileUtils.containsIgnoreCase(line, "ref_distance")) {
                        distOD = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "ref_wave")) {
                        wl = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "ref_tilt")) {
                        tilt = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
                    if (FileUtils.containsIgnoreCase(line, "ref_rot")) {
                        rot = Float.parseFloat(line.substring(iigual,
                                line.trim().length() - 1).trim());
                    }
//                    if (FileUtils.containsIgnoreCase(line, "ref_calfile")) {
//                        String line2 = line.substring(iigual,line.trim().length() - 1).trim();
//                        if (FileUtils.containsIgnoreCase(line2, "fit2d")){
//                            fit2d = true;
//                        } //else d2dplot convention
//                    }

                    try {
                        // scan_type = mar_scan ('hp_som', -5.0, 5.0, 2.0) ;
                        // scan_type = mar_ct (1.0,) ;
                        if (FileUtils.containsIgnoreCase(line, "scan_type")) {
                            String line2 = line.substring(iigual,
                                    line.trim().length() - 1).trim();
                            if (FileUtils.containsIgnoreCase(line2, "mar_scan")) {
                                String[] values = line2.split(",");
                                omeIni = Float.parseFloat(values[1]);
                                omeFin = Float.parseFloat(values[2]);
                                acqTime = Float.parseFloat(values[3]
                                        .split("\\)")[0]);
                            }
                            if (FileUtils.containsIgnoreCase(line2, "mar_ct")) {
                                omeIni = 0;
                                omeFin = 0;
                                String[] values = line2.split(",");
                                acqTime = Float.parseFloat(values[0]
                                        .split("\\(")[1]);
                            }
                        }

                    } catch (Exception ex) {
                        log.warning("Could not read the scan type from image header");
                    }

                }
            }
            headerSize = (int) (d2File.length() - binSize);

            log.fine("EDF header size (bytes)=" + headerSize);
            log.writeNameNumPairs("fine", true,
                    "dimX,dimY,beamCX,beamCY,pixSizeX,distOD,wl", dimX, dimY,
                    beamCX, beamCY, pixSizeX, distOD, wl);
            log.writeNameNumPairs("fine", true, "binsize,d2fileLength",
                    binSize, d2File.length());
            // calculem el pixel central
            // beamCX = beamCX / pixSize;
            // beamCY = beamCY / pixSize;

            scD2file.close();

            // ARA LLEGIREM ELS BYTES
            InputStream in = new BufferedInputStream(
                    new FileInputStream(d2File));
            byte[] buff = new byte[2];
            byte[] header = new byte[headerSize];
            patt2D = new Pattern2D(dimX, dimY, beamCX, beamCY, maxI, minI,
                    -1.0f, false); //I2
            int count = 0;
            in.read(header);
            patt2D.setScale(2);
            
            // ara aplico factor escala 2 per encabir a 2 bytes
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna
                                                             // (X)
                    in.read(buff);
                    int inten = (int) (FileUtils.B2toInt(buff) / patt2D.getScale());
                    patt2D.setInten(j, i,inten);
                
                    if (inten >= 0) { // fem >= o > directament sense
                        // considerar els zeros??!
                        if (inten > patt2D.getMaxI()) {
                            patt2D.setMaxI(inten);
                        }
                        if (inten < patt2D.getMinI()) {
                            patt2D.setMinI(inten);
                        }
                    }
                }
            }

            in.close();
            long end = System.nanoTime();
            patt2D.setMillis((float) ((end - start) / 1000000d));
            patt2D.setPixCount(count);
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("error reading EDF");
            return null;
        }

        // parametres instrumentals
        patt2D.setExpParam(pixSizeX, pixSizeY, distOD, wl);
        patt2D.setTiltDeg(tilt);
        patt2D.setRotDeg(rot);

        // parametres adquisicio
        patt2D.setScanParameters(omeIni, omeFin, acqTime);

        return patt2D; // correcte
    }
    
    protected void do_slider_stateChanged(ChangeEvent arg0) {
        this.loadPattern();
        
//
//        if (!slider.getValueIsAdjusting()) {
//            this.loadPattern();
//        }
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
}
