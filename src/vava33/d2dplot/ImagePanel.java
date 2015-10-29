package vava33.d2dplot;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.auxi.EllipsePars;
import vava33.d2dplot.auxi.ImgOps;
import vava33.d2dplot.auxi.OrientSolucio;
import vava33.d2dplot.auxi.PDCompound;
import vava33.d2dplot.auxi.PDReflection;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.PolyExZone;
import vava33.d2dplot.auxi.PuntCercle;
import vava33.d2dplot.auxi.PuntSolucio;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.SwingConstants;

public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // DEFINICIO BUTONS DEL MOUSE
    private static int MOURE = MouseEvent.BUTTON2;
    private static int CLICAR = MouseEvent.BUTTON1;
    private static int ZOOM_BORRAR = MouseEvent.BUTTON3;
    
    //parametres interaccio/contrast
    private static float incZoom = 0.05f;
    private static int contrast_fun=0;
    private static float swinglim=4.5f;
    private static int hklfontSize=13;
    private static float factSliderMax=3.f;
    private boolean color = false;
    private boolean invertY = false;
    private boolean mouseBox = false;
    private boolean mouseDrag = false;
    private boolean mouseZoom = false;
    private float scalefit;
    boolean fit = true;
    private int originX, originY;
    private float factorContrast;
    private Point2D.Float zoomPoint, dragPoint;
    
    //parametres per Forçar no pintar alguna cosa
    private boolean allowCalibration=true;
    private boolean allowEXZ=true;
    private boolean allowDINCO=true;
    private boolean allowSelPoints=true;
    private boolean showQuickListCompoundRings = false; //aquest es l'unic de control en aquest cas
    private boolean showDBCompoundRings = false; //aquest es l'unic de control en aquest cas
    
    //el diagrama i el frame
    private Pattern2D patt2D;
    private dades2d panelImatge;
    private BufferedImage image;
    private BufferedImage subimage;

    //Interaccio amb altres parts del programa
    private MainFrame mainf;
    private static VavaLogger log = D2Dplot_global.log;
    private Calib_dialog calibration;
    private ExZones_dialog exZones;
    private Dinco_frame dincoFrame;
    private Rectangle2D.Float currentRect;
    private PolyExZone currentPol;
    private PDCompound quickListCompound = null;
    private PDCompound dbCompound = null;
    
    //UI elements
    private static final boolean sideControls = true;
    private JLabel lbl2t;
    private JLabel lblCoordX;
    private JLabel lblIntensity;
    private JPanel panel;
    private JSlider slider_contrast;
    private JCheckBox chckbxAuto;
    private JTextField txtConminval;
    private JTextField txtConmaxval;
    private JLabel lblContrast;
    private JComboBox cbox_fun;
    private JLabel lbldsp;

    private JLabel lblCoordY;

    /**
     * Create the panel.
     */
    public ImagePanel() {
        super();
        
        if (sideControls){
            setLayout(new MigLayout("fill, insets 5", "[:90px:100px][grow]", "[grow]"));

            this.setPanelImatge(new dades2d());
            this.getPanelImatge().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            this.getPanelImatge().addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent arg0) {
                    do_panelImatge_mouseWheelMoved(arg0);
                }
            });
            this.getPanelImatge().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    do_panelImatge_mousePressed(arg0);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    do_panelImatge_mouseReleased(e);
                }
            });
            this.getPanelImatge().addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent arg0) {
                    do_panelImatge_mouseDragged(arg0);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    do_panelImatge_mouseMoved(e);
                }
            });
            add(this.getPanelImatge(), "cell 1 0 1 1,grow");

            this.panel = new JPanel();
            this.panel.setBorder(null);
            add(this.panel, "cell 0 0,grow");
            panel.setLayout(new MigLayout("", "[]", "[][][][][][][][grow][][][]"));
            this.lblCoordX = new JLabel("pixel X");
            lblCoordX.setFont(new Font("Dialog", Font.BOLD, 12));
            lblCoordX.setHorizontalAlignment(SwingConstants.CENTER);
            this.panel.add(this.lblCoordX, "cell 0 0,growx,aligny center");

            lblCoordY = new JLabel("pixel Y");
            lblCoordY.setHorizontalAlignment(SwingConstants.CENTER);
            lblCoordY.setFont(new Font("Dialog", Font.BOLD, 12));
            panel.add(lblCoordY, "cell 0 1,growx");
            this.lbl2t = new JLabel("2"+D2Dplot_global.theta);
            lbl2t.setFont(new Font("Dialog", Font.BOLD, 12));
            lbl2t.setHorizontalAlignment(SwingConstants.CENTER);
            this.panel.add(this.lbl2t, "cell 0 2,growx,aligny center");

            lbldsp = new JLabel("dsp");
            lbldsp.setFont(new Font("Dialog", Font.BOLD, 12));
            lbldsp.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(lbldsp, "cell 0 3,growx,aligny center");
            this.lblIntensity = new JLabel("Intensity");
            lblIntensity.setFont(new Font("Dialog", Font.BOLD, 12));
            lblIntensity.setHorizontalAlignment(SwingConstants.CENTER);
            this.panel.add(this.lblIntensity, "cell 0 4,growx,aligny center");

            lblContrast = new JLabel("Contrast:");
            lblContrast.setFont(new Font("Dialog", Font.PLAIN, 12));
            panel.add(lblContrast, "cell 0 5,alignx center");

            txtConmaxval = new JTextField();
            txtConmaxval.setFont(new Font("Dialog", Font.PLAIN, 10));
            txtConmaxval.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(txtConmaxval, "cell 0 6,alignx center,aligny center");
            txtConmaxval.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    do_txtConmaxval_actionPerformed(arg0);
                }
            });
            txtConmaxval.setColumns(5);

            this.slider_contrast = new JSlider();
            slider_contrast.setOrientation(SwingConstants.VERTICAL);
            panel.add(slider_contrast, "cell 0 7,grow");
            slider_contrast.setFont(new Font("Dialog", Font.PLAIN, 10));
            this.slider_contrast.setInverted(false);
            this.slider_contrast.setMinorTickSpacing(1);
            this.slider_contrast.setSnapToTicks(false);
            //        this.slider_contrast.setValue(3);
            this.slider_contrast.setMaximum(0);
            this.slider_contrast.setValue(this.slider_contrast.getMaximum()/2);

            txtConminval = new JTextField();
            txtConminval.setFont(new Font("Dialog", Font.PLAIN, 10));
            txtConminval.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(txtConminval, "cell 0 8,alignx center,aligny center");
            txtConminval.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_txtConminval_actionPerformed(e);
                }
            });
            txtConminval.setColumns(5);

            cbox_fun = new JComboBox();
            cbox_fun.setFont(new Font("Dialog", Font.PLAIN, 12));
            panel.add(cbox_fun, "cell 0 9,growx");
            cbox_fun.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    do_cbox_fun_itemStateChanged(e);
                }
            });
            cbox_fun.setModel(new DefaultComboBoxModel(new String[] {"linear", "quadr+", "quadr-"}));
            cbox_fun.setSelectedIndex(0);

            chckbxAuto = new JCheckBox("Auto");
            chckbxAuto.setFont(new Font("Dialog", Font.PLAIN, 12));
            panel.add(chckbxAuto, "cell 0 10,alignx center,aligny center");
            chckbxAuto.setToolTipText("Automatic contrast adjustment when opening consecutive images");
            this.slider_contrast.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent arg0) {
                    do_slider_contrast_stateChanged(arg0);
                }
            });

        }else{
            setLayout(new MigLayout("fill, insets 5", "[][][grow][][][]", "[grow][][]"));

            this.setPanelImatge(new dades2d());
            this.getPanelImatge().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            this.getPanelImatge().addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent arg0) {
                    do_panelImatge_mouseWheelMoved(arg0);
                }
            });
            this.getPanelImatge().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    do_panelImatge_mousePressed(arg0);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    do_panelImatge_mouseReleased(e);
                }
            });
            this.getPanelImatge().addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent arg0) {
                    do_panelImatge_mouseDragged(arg0);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    do_panelImatge_mouseMoved(e);
                }
            });
            add(this.getPanelImatge(), "cell 0 0 7 1,grow");

            this.slider_contrast = new JSlider();
            slider_contrast.setFont(new Font("Dialog", Font.PLAIN, 10));
            this.slider_contrast.setInverted(false);
            this.slider_contrast.setMinorTickSpacing(1);
            this.slider_contrast.setSnapToTicks(false);
            //          this.slider_contrast.setValue(3);
            this.slider_contrast.setMaximum(0);
            this.slider_contrast.setValue(this.slider_contrast.getMaximum()/2);
            this.slider_contrast.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent arg0) {
                    do_slider_contrast_stateChanged(arg0);
                }
            });


            this.panel = new JPanel();
            this.panel.setBorder(null);
            add(this.panel, "cell 0 1 6 1,alignx center,aligny center");
            panel.setLayout(new MigLayout("", "[][]", "[]"));
            this.lblCoordX = new JLabel("pixel X");
            this.panel.add(this.lblCoordX, "cell 0 0,alignx center,aligny center");
            this.lblCoordY = new JLabel("pixel X");
            this.panel.add(this.lblCoordY, "cell 1 0,alignx center,aligny center");
            this.lbl2t = new JLabel("2"+D2Dplot_global.theta);
            this.panel.add(this.lbl2t, "cell 2 0,alignx center,aligny center");
            this.lblIntensity = new JLabel("Intensity");
            this.panel.add(this.lblIntensity, "cell 4 0,alignx center,aligny center");
            this.lbldsp = new JLabel("dsp");
            this.lbldsp.setHorizontalAlignment(SwingConstants.CENTER);
            this.panel.add(this.lbldsp, "cell 3 0,growx,aligny center");

            lblContrast = new JLabel("Contrast");
            add(lblContrast, "cell 0 2,alignx trailing,aligny center");

            txtConminval = new JTextField();
            txtConminval.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_txtConminval_actionPerformed(e);
                }
            });
            add(txtConminval, "cell 1 2,growx,aligny center");
            txtConminval.setColumns(5);
            add(this.slider_contrast, "cell 2 2,grow");

            txtConmaxval = new JTextField();
            txtConmaxval.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    do_txtConmaxval_actionPerformed(arg0);
                }
            });
            add(txtConmaxval, "cell 3 2,growx,aligny center");
            txtConmaxval.setColumns(5);

            cbox_fun = new JComboBox();
            cbox_fun.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    do_cbox_fun_itemStateChanged(e);
                }
            });
            cbox_fun.setModel(new DefaultComboBoxModel(new String[] {"linear", "quadr+", "quadr-"}));
            cbox_fun.setSelectedIndex(0);
            add(cbox_fun, "cell 4 2,alignx center,aligny center");

            chckbxAuto = new JCheckBox("Auto");
            chckbxAuto.setToolTipText("Automatic contrast adjustment when opening consecutive images");
            add(chckbxAuto, "cell 5 2,alignx center,aligny center");   
        }
    
        //iniciem
        this.factorContrast=3;        
        this.resetView();
    }

    protected void do_panelImatge_mouseDragged(MouseEvent e) {
        if (this.mouseDrag == true && this.isPatt2D()) {
            Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
            float incX, incY;
            // agafem el dragpoint i l'actualitzem
            incX = p.x - dragPoint.x;
            incY = p.y - dragPoint.y;
            this.dragPoint = p;
            this.moveOrigin(incX, incY, true);
        }
        if (this.mouseZoom == true && this.isPatt2D()) {
            Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
            float incY;
            incY = p.y - dragPoint.y;
            this.dragPoint = p;
            boolean zoomIn = (incY < 0);
            this.zoom(zoomIn, zoomPoint);
        }
        if (this.isMouseBox() == true && this.isPatt2D()) {
            Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
            float incX, incY;
            // agafem el dragpoint i l'actualitzem (utilitzem scalefit per la sensibilitat)
            incX = (p.x - dragPoint.x) / this.getScalefit();
            incY = (p.y - dragPoint.y) / this.getScalefit();
            // hem d'enviar el punt clicat i l'increment a panel imatge perqu�
            // s'encarregui de moure el poligon
            if (estaDefinintEXZ()) {this.editPolygon(dragPoint, incX, incY, true);}            
            this.dragPoint = p;
        }
    }

    protected void do_panelImatge_mouseMoved(MouseEvent e) {
        // he de normalitzar les coordenades a la mida de la imatge en pixels
        if (this.isPatt2D()) {
          Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
          if (pix.x < 0 || pix.y < 0 || pix.x >= patt2D.getDimX() || pix.y >= patt2D.getDimY()) {
              return;
          }
          // El FastMath.round porta a 2048 i out of bound, millor no arrodonir i truncar aqu� igual que al if anterior
          int inten = (int) (patt2D.getInten((int) (pix.x), (int) (pix.y)));
          float dsp=-1;
          float tthRad=-1;
          if (patt2D.checkIfDistMD()){
              tthRad =  (float) patt2D.calc2T(pix,false);
              if (patt2D.checkIfWavel()){
                  dsp = (float) patt2D.calcDsp(tthRad);
              }  
          }
          this.setLabelValues(pix.x, pix.y, (float)FastMath.toDegrees(tthRad), dsp, inten);
            
//            Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
//            if (pix.x < 0 || pix.y < 0 || pix.x >= patt2D.getDimX() || pix.y >= patt2D.getDimY()) {
//                return;
//            }
//            lblCoord.setText("[" + FileUtils.dfX_1.format(pix.x) + ";" + FileUtils.dfX_1.format(pix.y) + "]");
//            int inten = 0;
//            // El FastMath.round porta a 2048 i out of bound, millor no arrodonir i truncar aqu� igual que al if anterior
//            inten = (int) (patt2D.getInten((int) (pix.x), (int) (pix.y)));
//            lblIntensity.setText("I= " + inten);
//            String t2 = "";
//            String dsp = "";
//            if (patt2D.checkIfDistMD()){
//                double twothetaRad =  patt2D.calc2T(pix,false);
//                t2 = "2" + D2Dplot_global.theta + "=" + FileUtils.dfX_3.format(FastMath.toDegrees(twothetaRad));
//                if (patt2D.checkIfWavel()){
//                    dsp = "; dsp=" + FileUtils.dfX_3.format(patt2D.calcDsp(twothetaRad));
//                }  
//                lbl2t.setText("(" + t2 + dsp + ")");
//            }
        }
    }

    
    // Identificar el bot� i segons quin sigui moure o fer zoom
    protected void do_panelImatge_mousePressed(MouseEvent arg0) {
        this.dragPoint = new Point2D.Float(arg0.getPoint().x, arg0.getPoint().y);

        if (arg0.getButton() == MOURE) {
            this.mouseDrag = true;
        }
        if (arg0.getButton() == ZOOM_BORRAR) {
            this.zoomPoint = new Point2D.Float(arg0.getPoint().x, arg0.getPoint().y);
            this.mouseZoom = true;
        }
        if (arg0.getButton() == CLICAR) {
            // si estem amb modo calibratge o definint EXZ
            if (estaCalibrant()||estaDefinintEXZ()){
                this.setMouseBox(true);
            }
        }

    }

    protected void do_panelImatge_mouseReleased(MouseEvent e) {
        if (e.getButton() == MOURE)
            this.mouseDrag = false;
        if (e.getButton() == ZOOM_BORRAR)
            this.mouseZoom = false;
        if (e.getButton() == CLICAR)
            this.setMouseBox(false);

        if (!this.isPatt2D())return;
        
        //LLAVORS PER ORDRE DE PREFERENCIA (si algun es compleix no es passarà als seguents)
        // 1) definicio exz, 2) calibracio 3)worksol ... ULTIM) select Points
        
        if(estaDefinintEXZ()){
            if(exZones.isDrawingExZone()){
                // afegim o treiem punts a la zona
                if (e.getButton() == CLICAR) {
                    Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                    exZones.getCurrentExZ().addPoint(Math.round(pix.x), Math.round(pix.y));
                }
            }    
            return;
        }
        
        if(estaCalibrant()){
            // afegim o treiem punts de la llista CALIB
            Point2D.Float pix = null;
            if (e.getButton() == CLICAR) {
                pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
            }
//            if (e.getButton() == ZOOM_BORRAR) {
//                TODO? no val la pena... millor tornar a començar
//            }
            
            if (pix == null) return;
            if (calibration.isSetting1stPeakCircle()){
                calibration.addPointToRing1Circle(pix);
            }
            return;
        }
        
        // Si tenim marcada l'opcio showHKLindexing afegim a la llista HKL els punts clicats m�s propers
        if (estaDincoWorkSol() && this.isPatt2D()){
            // assignem les coordenades "clic" als puntSolucio
            if (e.getButton() == CLICAR) {
                int inten;
                Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
//                inten = (int) (patt2D.getInten(FastMath.round(pix.x), FastMath.round(pix.y)) * patt2D.getScale());
                inten = (int) (patt2D.getInten(FastMath.round(pix.x), FastMath.round(pix.y)));
                //busquem quin �s l'HKL m�s proper i l'assignem
                PuntSolucio nearestPS = patt2D.getNearestPS(pix.x,pix.y);
                if (nearestPS!=null){
                    nearestPS.setCoordXclic(pix.x);
                    nearestPS.setCoordYclic(pix.y);
                    nearestPS.setIntenClic(inten);
                }
            }
            //faig que amb el dret es retorna la posicio original de la SOLUCIO
            if (e.getButton() == ZOOM_BORRAR) {
                Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                //busquem quin �s l'HKL m�s proper i l'assignem
                PuntSolucio nearestPS = patt2D.getNearestPS(pix.x,pix.y);
                if (nearestPS!=null){
                    nearestPS.setCoordXclic(-1);
                    nearestPS.setCoordYclic(-1);
                    nearestPS.setIntenClic(-1);
                }
            }
            //carregem a la llista
            this.dincoFrame.loadEditPeakList();
        }
        
        // NOMES afegim els punts i cercles si tenim marcada la indexacio (i no s'ha complert res d'anterior)
        if (estaSelectPoints()) {
            // afegim o treiem punts de la llista
            if (e.getButton() == CLICAR) {
                int inten;
                Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                inten = (int) (patt2D.getInten(FastMath.round(pix.x), FastMath.round(pix.y)));
                this.addPuntCercle(new Point2D.Float(e.getPoint().x, e.getPoint().y), inten);
            }
            if (e.getButton() == ZOOM_BORRAR) {
                this.removePuntCercle(new Point2D.Float(e.getPoint().x, e.getPoint().y));
            }
        }
    }
    

    protected void do_panelImatge_mouseWheelMoved(MouseWheelEvent e) {
        Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
        boolean zoomIn = (e.getWheelRotation() < 0);
        this.zoom(zoomIn, p);
    }

    protected void do_slider_contrast_stateChanged(ChangeEvent arg0) {
        this.pintaImatge();
        getPanelImatge().repaint();
    }
    
    protected Rectangle calcSubimatgeDinsFrame() {
        Point2D.Float startCoords = getPixel(new Point2D.Float(0, 0));
        Point2D.Float endCoords = getPixel(new Point2D.Float(getPanelImatge().getWidth() - 1, getPanelImatge()
                .getHeight() - 1));
        int panelX1 = FastMath.round(startCoords.x);
        int panelY1 = FastMath.round(startCoords.y);
        int panelX2 = FastMath.round(endCoords.x);
        int panelY2 = FastMath.round(endCoords.y);
        // No intersection? (que no movem la imatge fora del panell)
        if (panelX1 >= getImage().getWidth() || panelX2 < 0 || panelY1 >= getImage().getHeight() || panelY2 < 0) {
            return null;
        }
        int x1 = (panelX1 < 0) ? 0 : panelX1;
        int y1 = (panelY1 < 0) ? 0 : panelY1;
        int x2 = (panelX2 >= getImage().getWidth()) ? getImage().getWidth() - 1 : panelX2;
        int y2 = (panelY2 >= getImage().getHeight()) ? getImage().getHeight() - 1 : panelY2;
        return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    // afegim un punt i cercle a la llista de pics donat el punt en coordeandes
    // del panell (cal convertir-les abans)
    public void addPuntCercle(Point2D.Float p, int inten) {
        p = this.getPixel(p);
        patt2D.addPuntCercle(p, inten);
    }
    
    protected void setLabelValues(float pX, float pY, float tth, float dsp, int inten){
        lblCoordX.setText("x="+FileUtils.dfX_1.format(pX));
        lblCoordY.setText("y="+FileUtils.dfX_1.format(pY));
        lbl2t.setText("2" + D2Dplot_global.theta + "=" +FileUtils.dfX_2.format(tth)+"º");
        if (dsp<100){
            lbldsp.setText("d="+FileUtils.dfX_3.format(dsp)+D2Dplot_global.angstrom);    
        }else{
            lbldsp.setText("d="+FileUtils.dfX_2.format(dsp)+D2Dplot_global.angstrom);
        }
        lblIntensity.setText("I=" + inten);
    }
    
    /*
     * Mirar on es el dragpoint: - dins quadrat --> moure'l - a una aresta --> moure l'aresta - fora -> no fer res
     */
    public void editQuadrat(java.awt.geom.Point2D.Float dragPoint, float incX, float incY, boolean repaint) {

        Point2D.Float clic = getPixel(dragPoint);
        // els quatre vertexs del rectangle
        float ULx = currentRect.x;
        float ULy = currentRect.y;
        float URx = currentRect.x + currentRect.width;
        float URy = currentRect.y;
        float LLx = currentRect.x;
        float LLy = currentRect.y + currentRect.height;
        float LRx = currentRect.x + currentRect.width;
        float LRy = currentRect.y + currentRect.height;
        // tolerancia pot ser diferent segons el cas
        float tol = 20;
//        130923 aquesta subrutina ja no es crida per definir zones excloses (nomes calibracio)
//        if (estaDefinintEXZ())
//            tol = 1;
        // log.info(ULx+" "+ULy);
        // log.info(URx+" "+URy);
        // log.info(LLx+" "+LLy);
        // log.info(LRx+" "+LRy);

        // PRIMER mirem si est� a una arista
        boolean xMatch = false; // coicideix la x
        boolean yMatch = false;
        boolean xwidMatch = false; // coincideix la x+amplada
        boolean yheiMatch = false;
        // si estem a la mateixa X pot ser una arista vertical
        // si estem a la mateixa Y pot ser una arista vertical
        // pero s'ha de mirar si estem dins el width i el height
        xMatch = (clic.x > ULx - tol && clic.x < ULx + tol) && (clic.y > ULy && clic.y < LLy);
        yMatch = (clic.y > ULy - tol && clic.y < ULy + tol) && (clic.x > ULx && clic.x < URx);
        xwidMatch = (clic.x > URx - tol && clic.x < URx + tol) && (clic.y > URy && clic.y < LRy);
        yheiMatch = (clic.y > LLy - tol && clic.y < LLy + tol) && (clic.x > LLx && clic.x < LRx);
        if (xMatch) {
            // arista esquerra
            // cal moure la X del rectangle (x-inc) i sumar al width
            // +inc
            currentRect.x += incX;
            currentRect.width -= incX;
        }
        if (xwidMatch) {
            // NO cal moure la X del rectangle, nom�s sumar al width
            // +inc
            currentRect.width += incX;
        }
        if (yMatch) {
            // arista SUPERIOR
            // cal moure la Y del rectangle (y+inc) i restar al width
            // -inc
            currentRect.y += incY;
            currentRect.height -= incY;
        }
        if (yheiMatch) {
            // arista INFERIOR
            // NO cal moure la Y del rectangle nomes sumar al width +inc
            currentRect.height += incY;
        }

        // sino mirem si est� dins
        if (!xMatch && !xwidMatch && !yMatch && !yheiMatch) {
            if (currentRect.contains(clic)) {
            // if(currentRect.x>0&&currentRect.x<patt2D.getDimX()-1){
            // currentRect.x += incX;
            // }
            // if(currentRect.y>0&&currentRect.y<patt2D.getDimY()-1){
            // currentRect.y += incY;
            // }
            
            currentRect.x += incX;
            currentRect.y += incY;
            }
        }

        // COMPROVEM LIMITS
        if (currentRect.x < 0)
            currentRect.x = 0;
        if (currentRect.y < 0)
            currentRect.y = 0;
        // if(currentRect.x+currentRect.width>patt2D.getDimX())currentRect.width=patt2D.getDimX()-currentRect.x;
        // if(currentRect.y+currentRect.height>patt2D.getDimY())currentRect.height=patt2D.getDimY()-currentRect.y;
        if (currentRect.x + currentRect.width > patt2D.getDimX())
            currentRect.x = patt2D.getDimX() - currentRect.width;
        if (currentRect.y + currentRect.height > patt2D.getDimY())
            currentRect.y = patt2D.getDimY() - currentRect.height;

        if (repaint) {
            this.repaint();
        }
        ;

    }
    
    public void editPolygon(java.awt.geom.Point2D.Float dragPoint, float incX, float incY, boolean repaint) {
        Point2D.Float clic = getPixel(dragPoint);
//        float tolVertex = 10; //+-5 pixels
//        float tolArista = 5; //+-5 pixels
        
        //provem de fer la tolerancia en relacio a scalefit
        float tolVertex = FastMath.max(5/scalefit,5);
//        log.info("tolvertex="+tolVertex);
//        float tolArista = FastMath.max(5/scalefit,2);
        
        
        //primer mirem si hem clicat sobre algun vertex per moure'l
        int vertex=-1; //el vertex que cliquem
        for(int i=0;i<currentPol.npoints;i++){
            Rectangle2D.Float r = new Rectangle2D.Float(currentPol.getXVertex(i)-tolVertex,currentPol.getYVertex(i)-tolVertex,tolVertex*2,tolVertex*2);
            if (r.contains(clic)){
                vertex=i;
                break;
            }
        }
        //ara vertex assenyala el vertex que hem clicat o b� -1 si no s'ha clicat a cap
        if(vertex>=0){
            currentPol.incXVertex(vertex, incX);
            currentPol.incYVertex(vertex, incY);
            if(repaint)this.repaint();
            return;
        }
        
        //si no hem clicat a cap vertex mirem si hem clicat a dins i movem tota la zona
        if(currentPol.contains(clic)){
            currentPol.translate(FastMath.round(incX), FastMath.round(incY));
            if(repaint)this.repaint();
            return;
        }
        
        
    }

    protected Ellipse2D.Float ellipseToFrameCoords(Ellipse2D.Float c) {

        Point2D.Float vertex = new Point2D.Float(c.x, c.y);
        Point2D.Float newVertex = getFramePointFromPixel(vertex);
        float w = (float) (c.getWidth() * scalefit);
        float h = (float) (c.getHeight() * scalefit);
        // if(aresta<0.5)aresta=0.5f;
        Ellipse2D.Float newCercle = new Ellipse2D.Float(newVertex.x, newVertex.y, w, h);
        return newCercle;

    }

    private boolean estaCalibrant() {
        if (!allowCalibration) return false;
        if (calibration != null) {
            return calibration.isCalibrating();
        }
        return false;
    }

    private boolean estaDefinintEXZ() {
        if (!allowEXZ) return false;
        if (exZones != null) {
            return exZones.isSetExZones();
        }
        return false;
    }
    
    private boolean estaDincoWorkSol(){
        if (!allowDINCO) return false;
        if (dincoFrame != null){
            return dincoFrame.isWorkSOL();
        }
        return false;
    }
    
    private boolean estaDincoShowSpots(){
        if (!allowDINCO) return false;
        if (dincoFrame != null){
            return dincoFrame.isShowSpots();
        }
        return false;
    }
    
    private boolean estaDincoShowHKL(){
        if (!allowDINCO) return false;
        if (dincoFrame != null){
            return dincoFrame.isShowHKL();
        }
        return false;
    }
    
    private boolean estaSelectPoints(){
        if (!allowSelPoints) return false;
        if (mainf != null){
            return mainf.isSelectPoints();//TODO
        }
        return false;
    }
    
//    
//    private boolean estaPintantEXZ() {
//        if (exZones != null) {
//            if (exZones.isDrawingExZone()) {
//                return true;
//            }
//        }
//        return false;
//    }
    
//    private boolean estaPDdatabase() {
//        if (dbDialog != null){
//            if (dbDialog.isShowPDDataRings()){
//                return true;
//            }
//        }
//        return false;
//    }
    
    private boolean isShowDBCompoundRings(){
        return showDBCompoundRings;
    }
    
    private boolean isShowQuickListCompoundRings(){
        return showQuickListCompoundRings;
    }

    // ajusta la imatge al panell, mostrant-la tota sencera (calcula l'scalefit inicial)
    public void fitImage() {
        // l'ajustarem al frame mantenint la relacio, es a dir agafarem l'escala
        // m�s petita segons la mida del frame i la imatge
        double xScale = (double) getPanelImatge().getWidth() / getImage().getWidth(this);
        double yScale = (double) getPanelImatge().getHeight() / getImage().getHeight(this);
        scalefit = (float) FastMath.min(xScale, yScale);
        // CENTREM LA IMATGE AL PANELL
        if (scalefit == xScale) {
            // hem de centrar en y
            float gap = (getPanelImatge().getHeight() - (getImage().getHeight()) * scalefit) / 2.f;
            originY = originY + FastMath.round(gap);
        } else {
            // hem de centrar en x
            float gap = (getPanelImatge().getWidth() - (getImage().getWidth()) * scalefit) / 2.f;
            originX = originX + FastMath.round(gap);
        }
    }

    public Rectangle2D.Float getCurrentRect() {
        return currentRect;
    }

    // el pixel que entra est� al rang 0..n-1 donat un pixel px,py a quin punt x,y del JFrame est�
    public Point2D.Float getFramePointFromPixel(Point2D.Float px) {
        return new Point2D.Float(((px.x * scalefit) + originX), ((px.y * scalefit) + originY));
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public dades2d getPanelImatge() {
        return panelImatge;
    }

//    public MainFrame getParentFrame() {
//        return (MainFrame) this.getTopLevelAncestor();
//    }

    public Pattern2D getPatt2D() {
        return patt2D;
    }

    // segons la mida de la imatge actual, les coordenades d'un punt assenyalat amb el mouse correspondran a un pixel o
    // a un altre, aquesta subrutina ho corregeix: Donat el punt p on el mouse es troba te'l torna com a pixel de la imatge
    public Point2D.Float getPixel(Point2D.Float p) {
        return new Point2D.Float(((p.x - originX) / scalefit), ((p.y - originY) / scalefit));
    }

    public float getScalefit() {
        return scalefit;
    }

    public BufferedImage getSubimage() {
        return subimage;
    }

    //valor interpolat sobre una recta (fun=0) o una parabola (fun=1)
    protected Color intensityBW(int intensity, int maxInt, int minInt) {
    	
        if (intensity < 0) {// es mascara, el pintem magenta
            return new Color(255, 0, 255);
        }

    	float sliderMin = (float)slider_contrast.getMinimum();
    	float sliderMax = (float)slider_contrast.getMaximum();
    	float sliderVal = (float)slider_contrast.getValue();
        float ccomponent=-1.f;

        switch(contrast_fun){
        case 0:
        	// el valor s'interpolara sobre la recta (sliderMin,0) a (sliderMax,1)
            //interpolem
            float x1 = sliderMin+0.01f; // evitem diviso zero
            float y1 = 0.0f;
            float x2 = sliderVal;
            float y2 = 1.0f;
            ccomponent = ((y2 - y1) / (x2 - x1)) * intensity + y1 - ((y2 - y1) / (x2 - x1)) * x1;

        	break;
        case 1:
        	// el valor s'interpolara sobre una quadr�tica y=ax2 (centrat a 0,0)
        	//nomes varia el parametre a
        	float a=(1.f/(sliderVal*sliderVal));
        	ccomponent = a*(intensity*intensity);
        	break;
        case 2:
        	// el valor s'interpolara sobre una quadr�tica cap avall y=ax2 + 1 (centrat a 0,1)
        	//nomes varia el parametre a
        	a=(-1.f/(sliderVal*sliderVal));
        	ccomponent = a*(intensity*intensity)+1;
        	break;
        }

        if(ccomponent == -1.f){return new Color(255, 0, 255);}
        
        if (ccomponent < 0) {
            ccomponent = 0;
        }
        if (ccomponent > 1) {
            ccomponent = 1;
        }
        
        return new Color(ccomponent, ccomponent, ccomponent);
    }

    protected Color intensityBW_old(int intensity, int maxInt, int minInt, float val) {
        // canviarem la pendent mantenint el punt (0,0), es a dir, recta que passa pel punt (0,0) i (val,0.5)

        if (intensity < 0) {// es mascara, el pintem magenta
            return new Color(255, 0, 255);
        }

        // primer normalitzem la intensitat entre 0 i 1.
        float intNorm = (float) (intensity - minInt) / (float) (maxInt - minInt);
        float ccomponent;

        // PROVA si val<0.5 una recta sino una altra (incialment nomes tenia la primera recta per tots els valors de val)
        if (val <= 0.5) {
            // recta de (0,0) a (val,0.5)
            float x1 = -0.01f; // evitem diviso zero
            float y1 = 0.0f;
            float x2 = val;
            float y2 = 0.5f;
            ccomponent = ((y2 - y1) / (x2 - x1)) * intNorm + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        } else {
            // recta de (val,0.5) a (1,1)
            float x1 = val;
            float y1 = 0.5f;
            float x2 = 1.01f;// evitem diviso zero
            float y2 = 1.0f;
            ccomponent = ((y2 - y1) / (x2 - x1)) * intNorm + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        }
        if (ccomponent < 0) {
            ccomponent = 0;
        }
        if (ccomponent > 1) {
            ccomponent = 1;
        }

        return new Color(ccomponent, ccomponent, ccomponent);
    }
    
    // maxInt i minInt per normalitzar intensitats a 1, val es el valor per ajustar el contrast
    protected Color intensityColor(int intensity, int maxInt, int minInt, float val) {

        if (intensity < 0) {// es mascara, el pintem magenta
            return new Color(255, 0, 255);
        }

        // normalitzem la intensitat entre 0 i 1.
        float intNorm = (float) (intensity - minInt) / (float) (maxInt - minInt);
        float red, green, blue;

        // vermell
        if (intNorm <= val) {
            red = 0.f;
        } else {
            // interpolar en una recta de 0 a 1 comen�ant a (val,0). 2 punts: (val,0) (1,1)
            float x1 = val;
            float y1 = 0.f;
            float x2 = 1.01f;// evitem diviso zero
            float y2 = 1.f;
            red = ((y2 - y1) / (x2 - x1)) * intNorm + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        }

        // verd
        if (intNorm <= val) {
            // recta de (0,0) a (val,1)
            float x1 = -0.01f; // evitem diviso zero
            float y1 = 0.f;
            float x2 = val;
            float y2 = 1.f;
            green = ((y2 - y1) / (x2 - x1)) * intNorm + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        } else {
            // recta de (val,1) a (1,0)
            float x1 = val;
            float y1 = 1.f;
            float x2 = 1.01f; // evitem diviso zero
            float y2 = 0.f;
            green = ((y2 - y1) / (x2 - x1)) * intNorm + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        }

        // blau
        if (intNorm <= val) {
            blue = 1.f;
        } else {
            // recta de (val,1) a (1,0)
            float x1 = val;
            float y1 = 1.f;
            float x2 = 1.01f;// evitem diviso zero
            float y2 = 0.f;
            blue = ((y2 - y1) / (x2 - x1)) * intNorm + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        }

        if (intNorm == 0) {
            red = 0.f;
            green = 0.f;
            blue = 0.f;
        } // prova: poso el 0 absolut com a negre
        return new Color(red, green, blue);
    }

    public boolean isMouseBox() {
        return mouseBox;
    }

    public boolean isPatt2D() {
        if (this.patt2D != null) {
            return true;
        } else {
            return false;
        }
    }

//    public boolean isShowHKLsol() {
//        return showHKLsol;
//    }
//
//    public boolean isShowIndexing() {
//		return showIndexing;
//	}

	// es mou l'origen a traves d'un increment de les coordenades
    public void moveOrigin(float incX, float incY, boolean repaint) {
        // assignem un nou origen de la imatge amb un increment a les coordenades anteriors
        //  (util per moure'l fen drag del mouse)
        // hem de vigilar no sortir-nos de la imatge (igual que al fer zoom)
        originX = originX + FastMath.round(incX);
        originY = originY + FastMath.round(incY);

        // Els limits han de ser per:
        // -superior: image.height
        // -inferior: el "zero" de la imatge...
        // etc..
        // JA ESTA IMPLEMENTAT INDIRECTAMENT A CALCSUBIMATGEDINSFRAME

        if (repaint) {
            this.repaint();
        }
        ;
    }

    protected void pintaImatge() {

        if (patt2D == null) {
            return;
        }

        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage im = new BufferedImage(patt2D.getDimX(), patt2D.getDimY(), type);

        float maxValSlider = (this.slider_contrast.getMaximum() - this.slider_contrast.getMinimum());
//        float factorContrast = 3; // prova de multiplicar el maxVal per un factor (ja que sempre cal tirar "avall" el control)

        Color col;
        if (!this.isInvertY()) {
            // creem imatge normal
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
                    if (patt2D.isInExZone(j, i)) {// es mascara, el pintem magenta
                        col = new Color(255, 0, 255);
                    } else{
                        if (this.isColor()) {
                            // pintem en color
                            col = intensityColor(patt2D.getInten(j, i), patt2D.getMaxI(), patt2D.getMinI(),
                                    this.slider_contrast.getValue() / (maxValSlider * factorContrast));
                        } else {
                            // pintem en BW
                            col = intensityBW(patt2D.getInten(j, i), patt2D.getMaxI(), patt2D.getMinI());
                        }
                    }
                    im.setRGB(j, i, col.getRGB());
                }
            }
        } else {
            // creem imatge invertida
            int fila = 0;
            for (int i = patt2D.getDimY() - 1; i >= 0; i--) {
                for (int j = 0; j < patt2D.getDimX(); j++) {
                    if (patt2D.isInExZone(j, i)) {// es mascara, el pintem magenta
                        col = new Color(255, 0, 255);
                    }else{
                        if (this.isColor()) {
                            // pintem en color
                            col = intensityColor(patt2D.getInten(j, i), patt2D.getMaxI(), patt2D.getMinI(),
                                    this.slider_contrast.getValue() / (maxValSlider * factorContrast));
                        } else {
                            // pintem en BW
                            col = intensityBW(patt2D.getInten(j, i), patt2D.getMaxI(), patt2D.getMinI());
                        }    
                    }
                    im.setRGB(j, fila, col.getRGB());
                }
                fila = fila + 1;
            }
        }
        this.updateImage(im);
    }

    protected Rectangle2D.Float rectangleToFrameCoords(Rectangle2D.Float r) {
        Point2D.Float vertex = getFramePointFromPixel(new Point2D.Float(r.x, r.y));
        float width = (float) (r.getWidth() * scalefit);
        float height = (float) (r.getHeight() * scalefit);
        return new Rectangle2D.Float(vertex.x, vertex.y, width, height);
    }
    
    
    protected PolyExZone PolToFrameCoords(PolyExZone p) {
        if (p.npoints<1)return null; //CHECK
        PolyExZone exz = new PolyExZone(false);
        for (int i=0; i<p.npoints;i++){
            Point2D.Float v = getFramePointFromPixel(new Point2D.Float(p.getXVertex(i),p.getYVertex(i)));
            exz.addPoint(FastMath.round(v.x), FastMath.round(v.y));
        }
        return exz;
    }    

    // donat un punt clicat mirarem si hi ha cercle a aquest pixel i en cas que aixi sigui el borrarem
    public void removePuntCercle(Point2D.Float clic) {
        patt2D.removePuntCercle(this.getPixel(clic)); // el passem a pixels
    }

    public void resetView() {
        this.originX = 0;
        this.originY = 0;
        scalefit = 0.0f;
        this.repaint();
    }

    public void setShowQuickListCompoundRings(boolean show, PDCompound c) {
        if (getPatt2D() == null){return;}
        this.showQuickListCompoundRings = show;
        if (c == null){
            this.showQuickListCompoundRings = false;
            this.quickListCompound = null;
            return;
        }
        if (getPatt2D().getWavel() <= 0){
            log.info("wavelength missing");
            this.showQuickListCompoundRings = false;
            this.quickListCompound = null;
            log.debug("setShowRings: NO WAVELENGTH");
            return;
        }
        this.quickListCompound = c;
    }
    
    public void setShowDBCompoundRings(boolean show, PDCompound c) {
        if (getPatt2D() == null){return;}
        this.showDBCompoundRings = show;
        if (c == null){
            this.showDBCompoundRings = false;
            this.dbCompound = null;
            return;
        }
        if (getPatt2D().getWavel() <= 0){
            log.info("wavelength missing");
            this.showDBCompoundRings = false;
            this.dbCompound = null;
            log.debug("setShowRings: NO WAVELENGTH");
            return;
        }
        this.dbCompound = c;
    }
    
    public void setCalibration(Calib_dialog calibration) {
        this.calibration = calibration;
    }

    public void setExZones(ExZones_dialog exZones) {
        this.exZones = exZones;
    }
    
    public void setDinco(Dinco_frame df) {
        this.dincoFrame = df;
    }
    
    public void setDBdialog(DB_dialog dbDialog) {
    }

    public void setMainFrame(MainFrame mf) {
        this.mainf = mf;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setImagePatt2D(Pattern2D pattern) {
    	if(pattern == null)return;
        
        this.patt2D = pattern;

        log.debug("slider value before (max,min)= "+this.slider_contrast.getValue()+" ("+this.slider_contrast.getMaximum()+","+this.slider_contrast.getMinimum()+")");
//      this.slider_contrast.setValue(0); //per tal que es reinici al centre
        if(this.chckbxAuto.isSelected()){
            this.contrast_slider_properties(this.patt2D.getMinI(),this.calcOptMaxISlider());    
        }else{
            this.contrast_slider_properties(this.slider_contrast.getMinimum(),this.slider_contrast.getMaximum());
        }
        log.debug("slider value after (max,min)= "+this.slider_contrast.getValue()+" ("+this.slider_contrast.getMaximum()+","+this.slider_contrast.getMinimum()+")");
        this.loadContrastValues();
        this.pintaImatge();
        getPanelImatge().repaint();
        this.patt2D.clearPuntsCercles();
        this.patt2D.clearSolutions();

        this.resetView();// this.scalefit = 0.0f;        
    }

    public void setMouseBox(boolean mouseCalib) {
        this.mouseBox = mouseCalib;
    }

    public void setOriginX(int originX) {
        this.originX = originX;
    }

    public void setOriginY(int originY) {
        this.originY = originY;
    }

    public void setPanelImatge(dades2d panelImatge) {
        this.panelImatge = panelImatge;
    }

    public void setPatt2D(Pattern2D patt2d) {
        patt2D = patt2d;
    }

    public void setScalefit(float scalefit) {
        // centre del zoom:
        Point2D.Float centrePanel = new Point2D.Float(getPanelImatge().getWidth() / 2.f,
                getPanelImatge().getHeight() / 2.f);
        Point2D.Float centre = getPixel(centrePanel); // miro a quin pixel estem
                                                      // fent zoom

        // apliquem zoom
        this.scalefit = scalefit;

        // ara el pixel ja no est� al mateix lloc, mirem a quin punt del frame
        // est� (en aquest nou scalefit)
        centre = getFramePointFromPixel(centre);
        // ara tenim el punt del jframe que ha de quedar on tenim el mouse
        // apuntant, per tant hem de moure l'origen de
        // la imatge conforme aix� (vector nouCentre-mousePosition)
        originX = originX + FastMath.round(centrePanel.x - centre.x);
        originY = originY + FastMath.round(centrePanel.y - centre.y);

        this.repaint();
    }

//    public void setShowHKLsol(boolean showHKLsol) {
//        this.showHKLsol = showHKLsol;
//    }
//
//    public void setShowIndexing(boolean show) {
//        this.showIndexing = show;
//    }
//
//    public boolean isShowHKLIndexing() {
//		return showHKLIndexing;
//	}
//
//	public void setShowHKLIndexing(boolean showHKLIndexing) {
//		this.showHKLIndexing = showHKLIndexing;
//	}
//
//	public void setShowSolPoints(boolean show) {
//        this.showSolPoints = show;
//    }

    public void setSubimage(BufferedImage subimage) {
        this.subimage = subimage;
    }

    public void updateImage(BufferedImage i) {
        this.image = i;
        this.repaint();
    }

    // al fer zoom es canviara l'origen i l'escala de la imatge
    public void zoom(boolean zoomIn, Point2D.Float centre) {
        Point2D.Float mousePosition = new Point2D.Float(centre.x, centre.y);
        centre = getPixel(centre); // miro a quin pixel estem fent zoom

        // aplico el zoom
        if (zoomIn) {
            scalefit = scalefit + (incZoom * scalefit);
            // posem maxim?
            if (scalefit >= 25.f)
                scalefit = 25.f;
        } else {
            scalefit = scalefit - (incZoom * scalefit);
            if (scalefit <= 0.10)
                scalefit = 0.10f;
        }

        // ara el pixel ja no est� al mateix lloc, mirem a quin punt del frame
        // est� (en aquest nou scalefit)
        centre = getFramePointFromPixel(centre);

        // ara tenim el punt del jframe que ha de quedar on tenim el mouse
        // apuntant, per tant hem de moure l'origen de
        // la imatge conforme aix� (vector nouCentre-mousePosition)
        originX = originX + FastMath.round(mousePosition.x - centre.x);
        originY = originY + FastMath.round(mousePosition.y - centre.y);

        this.repaint();
    }

    
    
    public JSlider getSlider_contrast() {
        return slider_contrast;
    }

    public float getFactorContrast() {
        return factorContrast;
    }

    public void setFactorContrast(float factorContrast) {
        this.factorContrast = factorContrast;
    }

    public static int getContrast_fun() {
        return contrast_fun;
    }

    public static void setContrast_fun(int contrast_fun) {
        ImagePanel.contrast_fun = contrast_fun;
    }
    
    //hem fet varies proves
    //finalment farem:
    //  maxI-minI/fact (fact entre 2 i 10)
    //es el mes simple i general
    private int calcOptMaxISlider(){
        return (int)((float)(this.patt2D.getMaxI()-this.patt2D.getMinI())/factSliderMax);
    }
    
    //max=-1 for auto calc max intensity
    public void contrast_slider_properties(int min, int max){
        int old_val = slider_contrast.getValue();
        if(old_val==0){
            old_val=-1;
            min=this.patt2D.getMinI();
            max=this.calcOptMaxISlider();    
        }
        this.slider_contrast.setMaximum(max);
        this.slider_contrast.setMinimum(min);
        this.slider_contrast.setMinorTickSpacing((int)((float)(max-min)/50.f));
        //posicionem al valor que teniem si encara est� a l'escala i sino mantenim
        //la posicio en el nou valor (no cal, es posa a l'extrem m�s proper possible)
        if((old_val>0)&&(!this.chckbxAuto.isSelected())){
            this.slider_contrast.setValue(old_val); 
        }else{
            //posem al mig
//          this.slider_contrast.setValue((slider_contrast.getMaximum()-slider_contrast.getMinimum())/2);
            //provem de posar-lo al valor d'intensitat mitjana
            if (this.patt2D.getMeanI()>0){
                int meanI_sdev = this.patt2D.getMeanI() + (int)(factSliderMax*this.patt2D.getSdevI());
                if ((meanI_sdev>this.slider_contrast.getMinimum())&&(meanI_sdev<this.slider_contrast.getMaximum())){
                    this.slider_contrast.setValue(meanI_sdev);
                }else{//al mig
                    this.slider_contrast.setValue((slider_contrast.getMaximum()-slider_contrast.getMinimum())/2);    
                }
            }else{//al mig
                this.slider_contrast.setValue((slider_contrast.getMaximum()-slider_contrast.getMinimum())/2);
            }
        }

        
    }

    public boolean isColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public boolean isInvertY() {
        return invertY;
    }

    public void setInvertY(boolean invertY) {
        this.invertY = invertY;
    }
    
    private void loadContrastValues(){
        txtConminval.setText(String.valueOf(this.getSlider_contrast().getMinimum()));
        txtConmaxval.setText(String.valueOf(this.getSlider_contrast().getMaximum()));
        cbox_fun.setSelectedIndex(ImagePanel.getContrast_fun());
    }
    
    protected void applyNewContrastValues() {  
        int minI=0;
        int maxI=D2Dplot_global.satur65;
        int fun=getContrast_fun();
        try{
            minI=Integer.parseInt(txtConminval.getText());
        }catch(Exception ex){
            ex.printStackTrace();
            minI=0;
        }
        try{
            maxI=Integer.parseInt(txtConmaxval.getText());
        }catch(Exception ex){
            ex.printStackTrace();
            maxI=D2Dplot_global.satur65;
        }
        fun=cbox_fun.getSelectedIndex();
        ImagePanel.setContrast_fun(fun);
        this.contrast_slider_properties(minI, maxI);
        this.pintaImatge();
        this.getPanelImatge().repaint();
    }
    
    protected void do_cbox_fun_itemStateChanged(ItemEvent e) {
        applyNewContrastValues();
        log.debug("do_cbox_fun_itemStateChanged called");
    }
    protected void do_txtConmaxval_actionPerformed(ActionEvent arg0) {
        applyNewContrastValues();
        log.debug("do_txtConmaxval_actionPerformed called");
    }
    protected void do_txtConminval_actionPerformed(ActionEvent e) {
        applyNewContrastValues();
        log.debug("do_txtConminval_actionPerformed called");
    }
    
    
//    ------------------------------------ PANELL DE DIBUIX
    
    public class dades2d extends JPanel {

        private static final long serialVersionUID = 1L;
        
        public dades2d(){
            super();
        }
        
        //Dibuix de les Ellipses de calibració
        private void drawCalibrationC(Graphics2D g1){
          ArrayList<Point2D.Float> points = calibration.getPointsRing1circle();
          int radiPunt = Calib_dialog.getRadipunt();
          BasicStroke stroke = new BasicStroke(1.5f);
          g1.setStroke(stroke);
          if (points != null){
              g1.setColor(Color.orange);
              Iterator<Point2D.Float> itrP = points.iterator();
              while (itrP.hasNext()){
                  Point2D.Float p = itrP.next();
                  p = getFramePointFromPixel(p);
                  g1.drawOval((int)p.x-radiPunt, (int)p.y-radiPunt, radiPunt*2, radiPunt*2);
              }
          }
          
          if (calibration.getSolutions()!=null){

              //dibuixem els punts estimats dels anells
              if(calibration.isShowGuessPoints()){
                  g1.setColor(Color.red);
                  Iterator<EllipsePars> itre = calibration.getSolutions().iterator();
                  while (itre.hasNext()){
                      EllipsePars e = itre.next();
                      points =  e.getEstimPoints();
                      Iterator<Point2D.Float> itrP = points.iterator();
                      while (itrP.hasNext()){
                          Point2D.Float p = itrP.next();
                          p = getFramePointFromPixel(p);
                          g1.drawOval((int)p.x-radiPunt, (int)p.y-radiPunt, radiPunt*2, radiPunt*2);
                      }
                  }
              }
              
              //ara dibuixem les ellipses fitejades
              if(calibration.isShowFittedEllipses()){
                  g1.setColor(Color.green);
                  Iterator<EllipsePars> itre = calibration.getSolutions().iterator();
                  while (itre.hasNext()){
                      EllipsePars e = itre.next();
                      Point2D.Float cen = getFramePointFromPixel(new Point2D.Float((float)e.getXcen(),(float)e.getYcen()));
                      g1.drawOval((int)cen.x-radiPunt, (int)cen.y-radiPunt, radiPunt*2, radiPunt*2);
                      points =  e.getEllipsePoints(0, 360, 5);
                      if (points==null)continue;
                      //escribim a quin anell de LaB6 pertany (sobre el 1r punt)
                      Point2D.Float pLabel = getFramePointFromPixel(points.get(0));
                      g1.drawString(Integer.toString(e.getLab6ring()), pLabel.x, pLabel.y-2);
                      for (int i = 0; i < points.size(); i++){
                          Point2D.Float p1 = getFramePointFromPixel(points.get(i));
                          Point2D.Float p2 = null;
                          if (i==(points.size()-1)){
                              p2 = getFramePointFromPixel(points.get(0));
                          }else{
                              p2 = getFramePointFromPixel(points.get(i+1));
                          }
                          g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                      }
                  }
              }

              if(calibration.isShowSearchEllipsesBoundaries()){
                  //DEBUG ELLIPSES + and - BOUNDARIES
                  g1.setColor(Color.magenta);
                  if (calibration.getElliCerques()==null) return;
                  Iterator<EllipsePars> itre = calibration.getElliCerques().iterator();
                  while (itre.hasNext()){
                      EllipsePars e = itre.next();
                      Point2D.Float cen = getFramePointFromPixel(new Point2D.Float((float)e.getXcen(),(float)e.getYcen()));
                      g1.drawOval((int)cen.x-radiPunt, (int)cen.y-radiPunt, radiPunt*2, radiPunt*2);
                      points =  e.getEllipsePoints(0, 360, 5);
                      if (points==null)continue;
                      for (int i = 0; i < points.size(); i++){
                          Point2D.Float p1 = getFramePointFromPixel(points.get(i));
                          Point2D.Float p2 = null;
                          if (i==(points.size()-1)){
                              p2 = getFramePointFromPixel(points.get(0));
                          }else{
                              p2 = getFramePointFromPixel(points.get(i+1));
                          }
                          g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                      }
                  }   
              }

          }
          
        }

        //Dibuix de les Zones Excloses
        private void drawExZones(Graphics2D g1) {
            currentPol = exZones.getCurrentExZ();
            g1.setPaint(Color.CYAN);
            BasicStroke stroke = new BasicStroke(1.0f);
            g1.setStroke(stroke);
            if (currentPol != null) {
//                g1.draw(PolToFrameCoords(currentPol));
//                exZones.updateSelectedElement();
                if (currentPol.npoints>0) {
                    g1.draw(PolToFrameCoords(currentPol));
                    exZones.updateSelectedElement();
                }
            }
            // dibuixem el marge
            int marge = patt2D.getMargin();
            if (marge <= 0)
                return;
            Rectangle2D.Float r = new Rectangle2D.Float(marge, marge, patt2D.getDimX() - 2 * marge, patt2D.getDimY()
                    - 2 * marge);
            g1.draw(rectangleToFrameCoords(r));
        }

        //Dibuix dels punts seleccionats
        private void drawPuntsEllipses(Graphics2D g1) {
            Iterator<PuntCercle> itrPC = patt2D.getPuntsCercles().iterator();
            while (itrPC.hasNext()) {
                PuntCercle pc = itrPC.next();
                EllipsePars e = pc.getEllipse();
                Ellipse2D.Float p = pc.getPunt();
                
                //PRIMER DIBUIXEM L'ELIPSE I DESPRES EL PUNT (el centre de l'elipse no el dibuixem en aquest cas, nomes a la calibracio)
                // hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
                BasicStroke stroke = new BasicStroke(1.0f);
                g1.setStroke(stroke);
                g1.setPaint(PuntCercle.getColorCercle());
                ArrayList<Point2D.Float>points =  e.getEllipsePoints(0, 360, 5);
                if (points==null)continue;
                for (int i = 0; i < points.size(); i++){
                    Point2D.Float p1 = getFramePointFromPixel(points.get(i));
                    Point2D.Float p2 = null;
                    if (i==(points.size()-1)){
                        p2 = getFramePointFromPixel(points.get(0));
                    }else{
                        p2 = getFramePointFromPixel(points.get(i+1));
                    }
                    g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                }
                //el punt
                g1.setPaint(PuntCercle.getColorPunt());
                stroke = new BasicStroke(2.0f);
                g1.setStroke(stroke);
                g1.draw(ellipseToFrameCoords(p));
                g1.fill(ellipseToFrameCoords(p));
            }
        }
        
        //DIBUIX DB/QUICKLIST rings
        private void drawPDCompoundRings(Graphics2D g1, PDCompound pdc, Color c) {
            
            Iterator<PDReflection> itpks = pdc.getPeaks().iterator();
            while (itpks.hasNext()) {
                PDReflection ref = itpks.next();
                float dsp = ref.getDsp();
                float t2rad = (float) patt2D.dspToT2(dsp,false);
                //2theta to pixels
                EllipsePars e = ImgOps.getElliPars(patt2D, t2rad);
                
                //PRIMER DIBUIXEM L'ELIPSE I DESPRES EL PUNT (el centre de l'elipse no el dibuixem en aquest cas, nomes a la calibracio)
                // hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
                BasicStroke stroke = new BasicStroke(1.0f);
                g1.setStroke(stroke);
                g1.setPaint(c);
                ArrayList<Point2D.Float>points =  e.getEllipsePoints(0, 360, 5);
                if (points==null)continue;
                for (int i = 0; i < points.size(); i++){
                    Point2D.Float p1 = getFramePointFromPixel(points.get(i));
                    Point2D.Float p2 = null;
                    if (i==(points.size()-1)){
                        p2 = getFramePointFromPixel(points.get(0));
                    }else{
                        p2 = getFramePointFromPixel(points.get(i+1));
                    }
                    g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                }
            }
        }
                
//        private void drawHKLRings(Graphics2D g1) {
//            Iterator<HKL_reflection> it = compoundRings.iterator();
//            while (it.hasNext()) {
//                HKL_reflection r = it.next();
//                //2theta to pixels
//                float radi = (float) FastMath.tan(FastMath.toRadians(r.getT2())) * patt2D.getDistMD();
//                float cX = patt2D.getCentrX();
//                float cY = patt2D.getCentrY();
//                float px = radi/patt2D.getPixSx();
//                float py = radi/patt2D.getPixSy();
//                float vx = cX -px;
//                float vy = cY -py;
//                Ellipse2D.Float e = new Ellipse2D.Float(vx, vy, px*2, py*2);
//                g1.setPaint(Color.green);
//                BasicStroke stroke = new BasicStroke(1.f);
////                BasicStroke stroke = new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {20,15}, 0);
//                g1.setStroke(stroke);
//                // hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
//                g1.draw(ellipseToFrameCoords(e));
//            }
//        }
//        
//        private void drawDSPRings(Graphics2D g1) {
//            
//            Iterator<PDReflection> itpks = dbCompound.getPeaks().iterator();
//            while (itpks.hasNext()) {
//                PDReflection ref = itpks.next();
//                float dsp = ref.getDsp();
//                float t2rad = (float) patt2D.dspToT2(dsp,false);
//                //2theta to pixels
//                EllipsePars e = ImgOps.getElliPars(patt2D, t2rad);
//                
//                //PRIMER DIBUIXEM L'ELIPSE I DESPRES EL PUNT (el centre de l'elipse no el dibuixem en aquest cas, nomes a la calibracio)
//                // hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
//                BasicStroke stroke = new BasicStroke(1.0f);
//                g1.setStroke(stroke);
//                g1.setPaint(Color.cyan);
//                ArrayList<Point2D.Float>points =  e.getEllipsePoints(0, 360, 5);
//                if (points==null)continue;
//                for (int i = 0; i < points.size(); i++){
//                    Point2D.Float p1 = getFramePointFromPixel(points.get(i));
//                    Point2D.Float p2 = null;
//                    if (i==(points.size()-1)){
//                        p2 = getFramePointFromPixel(points.get(0));
//                    }else{
//                        p2 = getFramePointFromPixel(points.get(i+1));
//                    }
//                    g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
//                }
//            }
//        }
        
        //DIBUIX DINCO
        private void drawSolPoints(Graphics2D g1) {
            Iterator<OrientSolucio> itrOS = patt2D.getSolucions().iterator();
            while (itrOS.hasNext()) {
                OrientSolucio os = itrOS.next();
                if (os.isShowSol()) {
                    // es mostra la solucio
                    Iterator<PuntSolucio> itrS = os.getSol().iterator();
                    while (itrS.hasNext()) {
                        PuntSolucio s = itrS.next();
                        g1.setPaint(s.getColorPunt());
                        BasicStroke stroke = new BasicStroke(3.0f);
                        g1.setStroke(stroke);
                        Ellipse2D.Float e = ellipseToFrameCoords(s);
                        //si estem a HKLindexing NO mostrem els punts que s'han assignat
                        //provem de mostrar els actualitzats (els "clic")
                        if(estaDincoWorkSol()){
                        	if(s.getCoordXclic()>0){
                        		float xe = s.getCoordXclic() - s.getMidaPunt()/2;
                        		float ye = s.getCoordYclic() - s.getMidaPunt()/2;
                        		Ellipse2D.Float el = new Ellipse2D.Float(xe,ye,s.getMidaPunt(),s.getMidaPunt());
                        		e = ellipseToFrameCoords(el);
                        		g1.setPaint(s.getColorPunt().darker().darker());
                        	}
                        }
                        g1.draw(e);
                        g1.fill(e);
                        if (estaDincoShowHKL()){
                            Font font = new Font("Dialog", Font.PLAIN,hklfontSize+1);
//                            if(s.getOscil()>swinglim){
//                                font = new Font("Dialog", Font.ITALIC,hklfontSize-1);
//                            }
                            g1.setFont(font);
                            g1.setRenderingHint(
                                    RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                            g1.drawString(s.getHKL(), e.x + (float) s.getWidth() * scalefit, e.y);
                        }
                    }
                }
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            
            if (getImage() != null) {

                if (scalefit <= 0) {
                    fitImage();
                }

                Rectangle rect = calcSubimatgeDinsFrame();
                if (rect == null || rect.width == 0 || rect.height == 0) {
                    // no part of image is displayed in the panel
                    return;
                }
                try {
                    subimage = getImage().getSubimage(rect.x, rect.y, rect.width, rect.height);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Rectangle r = g2.getClipBounds();
                g2.drawImage(getSubimage(), FastMath.max(0, originX), FastMath.max(0, originY),
                        FastMath.round(getSubimage().getWidth() * scalefit),
                        FastMath.round(getSubimage().getHeight() * scalefit), null);

                final Graphics2D g1 = (Graphics2D) g2.create();

                // dibuixem els cercles
                g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON)); // perque es vegin mes suaus...

                if (estaDincoShowSpots()) {drawSolPoints(g1);}

                // nomes hauria d'haver-hi la possibilitat d'1 opcio, calibracio o zones excloses?
                if (estaCalibrant()) {drawCalibrationC(g1);}
                if (estaDefinintEXZ()) {drawExZones(g1);}
                
                // nomes hauria d'haver-hi la possibilitat d'1 opcio, drawIndexing o drawHKLindexing
                if (estaSelectPoints()&&!estaDincoWorkSol()) {drawPuntsEllipses(g1);}
                
                if (isShowQuickListCompoundRings() ){
                    if (quickListCompound != null) drawPDCompoundRings(g1,quickListCompound,Color.green);
                }
                
                if (isShowDBCompoundRings()){
                    if (dbCompound != null)drawPDCompoundRings(g1,dbCompound,Color.cyan);    
                }

                g1.dispose();
                g2.dispose();
                
                this.repaint();
            }
        }
    }
}
