package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class Azimuthal2Th {

    private final JDialog simpleImgDialog;
    private final JPanel contentPanel;

    Pattern2D patt2D,pattAzim;
    private ImagePanel panel;

    private static final String className = "Azimtth_2Dplot";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    /**
     * Create the dialog.
     */
    public Azimuthal2Th(JFrame parent, Pattern2D patt, String title) {
        this.contentPanel = new JPanel();
        this.simpleImgDialog = new JDialog(parent, title, false);
        this.simpleImgDialog
                .setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/img/Icona.png")));
        this.simpleImgDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.simpleImgDialog.setBounds(100, 100, 530, 559);
        this.simpleImgDialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.simpleImgDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
        {
            this.panel = new ImagePanel();
            this.contentPanel.add(this.panel.getIpanelMain(), "cell 0 0,grow");
        }
        {
            final JPanel buttonPane = new JPanel();
            this.simpleImgDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                final JButton btnSaveBin = new JButton("Save Image");
                btnSaveBin.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Azimuthal2Th.this.do_btnSaveBin_actionPerformed(e);
                    }
                });
                {
                    final JButton btnResetView = new JButton("Reset View");
                    btnResetView.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Azimuthal2Th.this.do_btnResetView_actionPerformed(e);
                        }
                    });
                    buttonPane.setLayout(new MigLayout("", "[][][][grow][]", "[25px]"));
                    buttonPane.add(btnResetView, "cell 0 0,alignx left,aligny center");
                }
                {
                    final JButton btnTrueSize = new JButton("True Size");
                    btnTrueSize.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Azimuthal2Th.this.do_btnTrueSize_actionPerformed(e);
                        }
                    });
                    buttonPane.add(btnTrueSize, "cell 1 0,alignx left,aligny center");
                }
                
                JButton btnInteg = new JButton("INTEG");
                btnInteg.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnInteg_actionPerformed(e);
                    }
                });
                buttonPane.add(btnInteg, "cell 2 0");
                buttonPane.add(btnSaveBin, "cell 3 0,alignx right,aligny top");
            }
            final JButton cancelButton = new JButton("Close");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Azimuthal2Th.this.do_cancelButton_actionPerformed(e);
                }
            });
            cancelButton.setActionCommand("Cancel");
            buttonPane.add(cancelButton, "cell 4 0,alignx left,aligny top");
        }

        this.patt2D = patt;
//        this.panel.setImagePatt2D(this.patt2D);
    }

    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        this.simpleImgDialog.dispose();
    }

    protected void do_btnSaveBin_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterWrite();
        File fsave = FileUtils.fchooserSaveAsk(this.simpleImgDialog, new File(D2Dplot_global.getWorkdir()), filt, null);
        if (fsave == null)
            return;
        fsave = ImgFileUtils.writePatternFile(fsave, this.pattAzim, true);
        this.pattAzim.setImgfile(fsave);
    }

    protected void do_btnResetView_actionPerformed(ActionEvent e) {
        this.panel.resetView();
    }

    protected void do_btnTrueSize_actionPerformed(ActionEvent e) {
        this.panel.setScalefit(1.0f);
    }

    public void setVisible(boolean vis) {
        this.simpleImgDialog.setVisible(vis);
    }
    
    //azim 2.5 i t2 0.1 van be, o be 0.5 i 0.05
    protected void do_btnInteg_actionPerformed(ActionEvent e) {
//        double azimStep = 0.4;
//        double step2t = patt2D.calcMinStepsizeBy2Theta4Directions();
//        double step2t = 0.05;
        double step2t = patt2D.calcMinStepsizeEstimateWithPixSizeAndDistMD();
        double max2t = patt2D.getMax2TdegCircle();
//        double max2t = patt2D.getMax2Tdeg();
        
        int bins2t = (int)(max2t/step2t)+1;
//        int binsAzim = (int)(360./azimStep)+1;
        //provare fer imatge "quadrada"
        float azimStep = 360.f/(bins2t-1);
//        float azimStep = 0.5f;
//        float azimRange = 21f; //360 for full
        float azimRange=360f;
        int binsAzim = (int)(azimRange/azimStep)+1;
        
        log.writeNameNumPairs("config", true, "bins2t,step2t,binsAzim,azimStep", bins2t,step2t,binsAzim,azimStep);
        
        pattAzim = new Pattern2D(bins2t, binsAzim);
        
        //opcio 1... recorrem pixel a pixel i anem omplint a on toca
        for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                if(patt2D.isExcluded(i, j))continue;
                double t2 = patt2D.calc2T(i, j, true);
                if (t2>max2t)continue;
                double az = patt2D.getAzimAngle(i, j, true);
//                if ((az > 290)||(az < 270))continue;
//                log.writeNameNumPairs("config", true, "i,j,t2,az", i,j,t2,az);
                //a quina posicio de la imatge final ha d'anar
                //t2
                int pX = (int) (t2 / step2t);
                //az
                int pY = (int) (az / azimStep);
//                log.writeNameNumPairs("config", true, "pX,pY,az,t2", pX,pY,az,t2);
                pattAzim.getPixel(pX, pY).addIntensity(patt2D.getPixel(i, j).getIntensity(), true);
            }
        }
        //ara divideixo pel nombre de contribuents
        for (int j = 0; j < pattAzim.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < pattAzim.getDimX(); i++) { // per cada columna (X)
                pattAzim.getPixel(i, j).normalizeIntensity();
            }
        }
        
        pattAzim.recalcMaxMinI();
        this.panel.setImagePatt2D(this.pattAzim);
        
    }
    
    
}
