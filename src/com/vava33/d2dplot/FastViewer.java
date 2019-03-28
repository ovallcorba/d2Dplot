package com.vava33.d2dplot;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class FastViewer {

    private static final String className = "FastView";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private final JDialog fastViewerDialog;
    private final JPanel contentPane;
    ArrayList<File> files = new ArrayList<File>();
    private final JSlider slider;
    private final ImagePanel imgpanel;
    private final int currentLoadedImage = -1;
    private final JButton button;
    private Timer timer;
    private final JButton btnStop;
    private final JButton btnClose;

    /**
     * Create the frame.
     */
    public FastViewer(JFrame parent) {
        this.fastViewerDialog = new JDialog(parent, "Fast Viewer", false);
        this.fastViewerDialog
                .setIconImage(Toolkit.getDefaultToolkit().getImage(FastViewer.class.getResource("/img/Icona.png")));
        this.fastViewerDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.fastViewerDialog.setBounds(100, 100, 842, 646);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.fastViewerDialog.setContentPane(this.contentPane);
        this.contentPane.setLayout(new MigLayout("", "[][][][grow][]", "[grow][]"));

        this.imgpanel = new ImagePanel();
        this.contentPane.add(this.imgpanel.getIpanelMain(), "cell 0 0 5 1,grow");

        final JButton btnOpenImgs = new JButton("Open Imgs");
        btnOpenImgs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                FastViewer.this.do_btnOpenImgs_actionPerformed(arg0);
            }
        });
        this.contentPane.add(btnOpenImgs, "cell 0 1");

        this.slider = new JSlider();
        this.slider.setPaintLabels(true);
        this.slider.setSnapToTicks(true);
        this.slider.setPaintTicks(true);
        this.slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                FastViewer.this.do_slider_stateChanged(arg0);
            }
        });

        this.button = new JButton(">");
        this.button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FastViewer.this.do_button_actionPerformed(e);
            }
        });

        this.btnStop = new JButton("Stop");
        this.btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FastViewer.this.do_btnStop_actionPerformed(e);
            }
        });
        this.contentPane.add(this.btnStop, "cell 1 1");
        this.contentPane.add(this.button, "cell 2 1");
        this.contentPane.add(this.slider, "cell 3 1,growx");

        this.btnClose = new JButton("Close");
        this.btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FastViewer.this.do_btnClose_actionPerformed(e);
            }
        });
        this.contentPane.add(this.btnClose, "cell 4 1");
    }

    public void dispose() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        this.fastViewerDialog.dispose();
    }

    public void showOpenImgsDialog() {
        this.do_btnOpenImgs_actionPerformed(null);
    }

    protected void do_btnOpenImgs_actionPerformed(ActionEvent arg0) {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File[] fs = FileUtils.fchooserMultiple(this.fastViewerDialog, new File(D2Dplot_global.getWorkdir()), filt,
                0, "Select 2DXRD data files to open");
        this.files = new ArrayList<File>(Arrays.asList(fs));
        if (fs.length <= 0)
            return;
        this.slider.setMinimum(0);
        this.slider.setMaximum(this.files.size() - 1);
        this.slider.setValue(0);
        this.loadPattern();
    }

    private void loadPattern() {
        log.debug("sliderValue=" + this.slider.getValue());
        if (this.slider.getValue() == this.currentLoadedImage)
            return;
        //        imgpanel.setImagePatt2D(this.fastEdfRead(files.get(slider.getValue())));
        this.imgpanel.setImagePatt2D(ImgFileUtils.readPatternFile(this.files.get(this.slider.getValue()), false));
    }


    protected void do_slider_stateChanged(ChangeEvent arg0) {
        this.loadPattern();
    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            log.debug("Timer task started at:" + new Date());

            final int ini = FastViewer.this.slider.getValue();
            final int fin = FastViewer.this.slider.getMaximum();

            if (ini < fin) {
                FastViewer.this.slider.setValue(ini + 1);
            } else {
                FastViewer.this.timer.cancel();
                FastViewer.this.timer = null;
            }
        }

    }

    protected void do_button_actionPerformed(ActionEvent e) {
        if (this.timer == null) {
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(new MyTimerTask(), 0, 1000);
            log.debug("TimerTask started");
        }
    }

    protected void do_btnStop_actionPerformed(ActionEvent e) {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    public void setVisible(boolean vis) {
        this.fastViewerDialog.setVisible(vis);
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }
}
