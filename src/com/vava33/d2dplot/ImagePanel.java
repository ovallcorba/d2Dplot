package com.vava33.d2dplot;

// per editar posar true sidecontrols

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.math3.util.FastMath;

import com.vava33.cellsymm.HKLrefl;
import com.vava33.cellsymm.PDCompound;
import com.vava33.d2dplot.auxi.ArcExZone;
import com.vava33.d2dplot.auxi.EllipsePars;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.OrientSolucio;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.d2dplot.auxi.Peak;
import com.vava33.d2dplot.auxi.Pixel;
import com.vava33.d2dplot.auxi.PolyExZone;
import com.vava33.d2dplot.auxi.PuntClick;
import com.vava33.d2dplot.auxi.PuntSolucio;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class ImagePanel {

    // DEFINICIO BUTONS DEL MOUSE
    private static int MOURE = MouseEvent.BUTTON2;
    private static int CLICAR = MouseEvent.BUTTON1;
    private static int ZOOM_BORRAR = MouseEvent.BUTTON3;

    //parametres interaccio/contrast
    private static float incZoom = 0.05f;
    private static float maxScaleFit = 40.f;
    private static float minScaleFit = 0.10f;
    private static int hklfontSize = 13;
    //    private static float factSliderMax=3.f;
    private static int contrast_fun = 0;
    private static float factorAutoContrast = 3.0f; //era 20
    private boolean color = false;
    private boolean invertY = false;
    private boolean paintExZ = false;
    private boolean mouseBox = false;
    private boolean mouseDrag = false;
    private boolean mouseFree = false;
    private int mouseFreeArestaQ = 10;
    private boolean mouseZoom = false;
    private float scalefit;
    //	boolean fit = true;
    private int originX, originY;
    private Point2D.Float zoomPoint, dragPoint;
    private Point2D.Float currentMousePoint;
    private int minValSlider, valSlider, maxI, minI;

    //parametres per Forçar no pintar alguna cosa
    private final boolean allowCalibration = true;
    private final boolean allowEXZ = true;
    private final boolean allowDINCO = true;
    private final boolean allowSelPoints = true;
    private final boolean allowPKsearch = true;
//    private boolean showQuickListCompoundRings = false; //aquest es l'unic de control en aquest cas
    private boolean showDBCompoundRings = false; //aquest es l'unic de control en aquest cas

    //el diagrama i el frame
    private Pattern2D patt2D;
    private Dades2d data2D;
    private BufferedImage image;
    private BufferedImage subimage;

    //Interaccio amb altres parts del programa
    private MainFrame d2dplot_mainFrame;
    private static final String className = "ImagePanel";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private Calibration calibration;
    private ExZones exZones;
    private IncoPlot dincoFrame;
    private PeakSearch PKsearch;
    //	private Rectangle2D.Float currentRect;
    private PolyExZone currentPol;
//    private PDCompound quickListCompound = null;
    private List<PDCompound> dbCompounds;

    //UI elements
    private JPanel imagePanel;
    private final JLabel lbl2t;
    private final JLabel lblCoordX;
    private final JLabel lblIntensity;
    private final JPanel panel;
    private final JSlider slider_contrast;
    private final JCheckBox chckbxAuto;
    private final JTextField txtConminval;
    private final JTextField txtConmaxval;
    private final JLabel lblContrast;
    private final JComboBox<String> cbox_fun;
    private final JLabel lbldsp;
    private final JLabel lblCoordY;
    private final JLabel lblAzim;
    //	Border etchedborder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    /**
     * Create the panel.
     */
    public ImagePanel() {
        this.imagePanel = new JPanel();

        this.imagePanel.setLayout(new MigLayout("fill, insets 5", "[:90px:100px][grow]", "[grow]"));
        this.setPanelImatge(new Dades2d());
        this.getPanelImatge().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.getPanelImatge().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent arg0) {
                ImagePanel.this.do_panelImatge_mouseWheelMoved(arg0);
            }
        });
        this.getPanelImatge().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                ImagePanel.this.do_panelImatge_mousePressed(arg0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ImagePanel.this.do_panelImatge_mouseReleased(e);
            }
        });
        this.getPanelImatge().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent arg0) {
                ImagePanel.this.do_panelImatge_mouseDragged(arg0);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                ImagePanel.this.do_panelImatge_mouseMoved(e);
            }
        });
        //            this.getPanelImatge().setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.imagePanel.add(this.getPanelImatge(), "cell 1 0 1 1,grow");
        //            add(new JPanel(), "cell 1 0,grow");

        this.panel = new JPanel();
        this.panel.setBorder(null);
        this.imagePanel.add(this.panel, "cell 0 0,grow");
        this.panel.setLayout(new MigLayout("", "[]", "[][][][][][][][][grow][][][]"));
        this.lblCoordX = new JLabel("pixel X");
        this.lblCoordX.setFont(new Font("Dialog", Font.BOLD, 12));
        this.lblCoordX.setHorizontalAlignment(SwingConstants.CENTER);
        this.panel.add(this.lblCoordX, "cell 0 0,growx,aligny center");

        this.lblCoordY = new JLabel("pixel Y");
        this.lblCoordY.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblCoordY.setFont(new Font("Dialog", Font.BOLD, 12));
        this.panel.add(this.lblCoordY, "cell 0 1,growx");
        this.lbl2t = new JLabel("2" + D2Dplot_global.theta);
        this.lbl2t.setFont(new Font("Dialog", Font.BOLD, 12));
        this.lbl2t.setHorizontalAlignment(SwingConstants.CENTER);
        this.panel.add(this.lbl2t, "cell 0 2,growx,aligny center");

        this.lblAzim = new JLabel("azim");
        this.lblAzim.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblAzim.setFont(new Font("Dialog", Font.BOLD, 12));
        this.panel.add(this.lblAzim, "cell 0 3,growx,aligny center");

        this.lbldsp = new JLabel("dsp");
        this.lbldsp.setFont(new Font("Dialog", Font.BOLD, 12));
        this.lbldsp.setHorizontalAlignment(SwingConstants.CENTER);
        this.panel.add(this.lbldsp, "cell 0 4,growx,aligny center");
        this.lblIntensity = new JLabel("Intensity");
        this.lblIntensity.setFont(new Font("Dialog", Font.BOLD, 12));
        this.lblIntensity.setHorizontalAlignment(SwingConstants.CENTER);
        this.panel.add(this.lblIntensity, "cell 0 5,growx,aligny center");

        this.lblContrast = new JLabel("Contrast:");
        this.lblContrast.setFont(new Font("Dialog", Font.PLAIN, 12));
        this.panel.add(this.lblContrast, "cell 0 6,alignx center");

        this.txtConmaxval = new JTextField();
        this.txtConmaxval.setFont(new Font("Dialog", Font.PLAIN, 10));
        this.txtConmaxval.setHorizontalAlignment(SwingConstants.CENTER);
        this.panel.add(this.txtConmaxval, "cell 0 7,alignx center,aligny center");
        this.txtConmaxval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ImagePanel.this.do_txtConmaxval_actionPerformed(arg0);
            }
        });
        this.txtConmaxval.setColumns(5);

        this.slider_contrast = new JSlider();
        this.slider_contrast.setOrientation(SwingConstants.VERTICAL);
        this.panel.add(this.slider_contrast, "cell 0 8,grow");
        this.slider_contrast.setFont(new Font("Dialog", Font.PLAIN, 10));
        this.slider_contrast.setInverted(false);
        this.slider_contrast.setMinorTickSpacing(1);
        this.slider_contrast.setSnapToTicks(false);
        this.slider_contrast.setMaximum(0);
        this.slider_contrast.setValue(this.slider_contrast.getMaximum() / 2);
        this.slider_contrast.setMinimumSize(new Dimension(50, 50));
        this.slider_contrast.setPreferredSize(new Dimension(50, 100));

        this.txtConminval = new JTextField();
        this.txtConminval.setFont(new Font("Dialog", Font.PLAIN, 10));
        this.txtConminval.setHorizontalAlignment(SwingConstants.CENTER);
        this.panel.add(this.txtConminval, "cell 0 9,alignx center,aligny center");
        this.txtConminval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImagePanel.this.do_txtConminval_actionPerformed(e);
            }
        });
        this.txtConminval.setColumns(5);

        this.cbox_fun = new JComboBox<String>();
        this.cbox_fun.setFont(new Font("Dialog", Font.PLAIN, 12));
        this.panel.add(this.cbox_fun, "cell 0 10,growx");
        this.cbox_fun.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ImagePanel.this.do_cbox_fun_itemStateChanged(e);
            }
        });
        this.cbox_fun.setModel(new DefaultComboBoxModel<String>(new String[] { "linear", "quadr+", "quadr-" }));
        this.cbox_fun.setSelectedIndex(0);

        this.chckbxAuto = new JCheckBox("Auto");
        this.chckbxAuto.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ImagePanel.this.do_chckbxAuto_itemStateChanged(e);
            }
        });
        this.chckbxAuto.setFont(new Font("Dialog", Font.PLAIN, 12));
        this.panel.add(this.chckbxAuto, "cell 0 11,alignx center,aligny center");
        this.chckbxAuto.setToolTipText("Automatic contrast adjustment when opening consecutive images");
        this.slider_contrast.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                ImagePanel.this.do_slider_contrast_stateChanged(arg0);
            }
        });

        //iniciem
        this.resetView();
    }

    public void resetView() {
        this.originX = 0;
        this.originY = 0;
        this.scalefit = 0.0f;
        this.actualitzarVista();
    }

    public void actualitzarVista() {
        this.imagePanel.repaint();
    }

    public void setImagePatt2D(Pattern2D pattern) {
        if (pattern == null)
            return;

        if ((pattern.getMaxI() == 0) && (pattern.getMinI() == 0)) {
            pattern.recalcMaxMinI();
        }

        this.patt2D = pattern;
        log.writeNameNumPairs("config", true, "maxI,minI", pattern.getMaxI(), pattern.getMinI());
        log.debug("slider value before (max,min)= " + this.slider_contrast.getValue() + " ("
                + this.slider_contrast.getMaximum() + "," + this.slider_contrast.getMinimum() + ")");
        if (this.chckbxAuto.isSelected()) {
            this.setSliderContrastValuesAuto();
        } else {
            if (this.slider_contrast.getValue() == 0) {
                this.setSliderContrastValuesAuto();
            } else {
                this.setSliderContrastValues(this.patt2D.getMinI(), this.patt2D.getMaxI());
            }

        }
        log.debug("slider value after (max,min)= " + this.slider_contrast.getValue() + " ("
                + this.slider_contrast.getMaximum() + "," + this.slider_contrast.getMinimum() + ")");
        //		this.loadContrastValues();
        this.pintaImatge();
        this.patt2D.clearPuntsCercles();
        this.patt2D.clearSolutions();
        this.actualitzarVista();
    }

    public void updateImage(BufferedImage i) {
        this.image = i;
        this.actualitzarVista();
    }

    // el pixel que entra est� al rang 0..n-1 donat un pixel px,py a quin punt x,y del JFrame est�
    public Point2D.Float getFramePointFromPixel(Point2D.Float px) {
        final float x = (px.x * this.scalefit) + this.originX; //0.5 per posar-ho al centre del pixel
        final float y = (px.y * this.scalefit) + this.originY;
        return new Point2D.Float(x, y);
    }

    //    
    //    // segons la mida de la imatge actual, les coordenades d'un punt assenyalat amb el mouse correspondran a un pixel o
    //    // a un altre, aquesta subrutina ho corregeix: Donat el punt p on el mouse es troba te'l torna com a pixel de la imatge
    public Point2D.Float getPixel(Point2D.Float p) {
        final float x = (p.x - this.originX) / this.scalefit;
        final float y = (p.y - this.originY) / this.scalefit;
        return new Point2D.Float(x, y);
    }

    // afegim un punt i cercle a la llista de pics donat el punt en coordeandes
    // del panell (cal convertir-les abans)
    public void addPuntCercle(Point2D.Float p, int inten) {
        p = this.getPixel(p);
        this.patt2D.addPuntCercle(p, inten);
    }

    // donat un punt clicat mirarem si hi ha cercle a aquest pixel i en cas que aixi sigui el borrarem
    public void removePuntCercle(Point2D.Float clic) {
        this.patt2D.removePuntCercle(this.getPixel(clic)); // el passem a pixels
    }

    /*
     * Mirar on es el dragpoint: - dins quadrat --> moure'l - a una aresta --> moure l'aresta - fora -> no fer res
     */
    //	public void editQuadrat(java.awt.geom.Point2D.Float dragPoint, float incX, float incY, boolean repaint) {
    //	    if (currentRect==null)return;
    //		Point2D.Float clic = getPixel(dragPoint);
    //		// els quatre vertexs del rectangle
    //		float ULx = currentRect.x;
    //		float ULy = currentRect.y;
    //		float URx = currentRect.x + currentRect.width;
    //		float URy = currentRect.y;
    //		float LLx = currentRect.x;
    //		float LLy = currentRect.y + currentRect.height;
    //		float LRx = currentRect.x + currentRect.width;
    //		float LRy = currentRect.y + currentRect.height;
    //		// tolerancia pot ser diferent segons el cas
    //		float tol = 20;
    //
    //		// PRIMER mirem si est� a una arista
    //		boolean xMatch = false; // coicideix la x
    //		boolean yMatch = false;
    //		boolean xwidMatch = false; // coincideix la x+amplada
    //		boolean yheiMatch = false;
    //		// si estem a la mateixa X pot ser una arista vertical
    //		// si estem a la mateixa Y pot ser una arista vertical
    //		// pero s'ha de mirar si estem dins el width i el height
    //		xMatch = (clic.x > ULx - tol && clic.x < ULx + tol) && (clic.y > ULy && clic.y < LLy);
    //		yMatch = (clic.y > ULy - tol && clic.y < ULy + tol) && (clic.x > ULx && clic.x < URx);
    //		xwidMatch = (clic.x > URx - tol && clic.x < URx + tol) && (clic.y > URy && clic.y < LRy);
    //		yheiMatch = (clic.y > LLy - tol && clic.y < LLy + tol) && (clic.x > LLx && clic.x < LRx);
    //		if (xMatch) {
    //			// arista esquerra
    //			// cal moure la X del rectangle (x-inc) i sumar al width
    //			// +inc
    //			currentRect.x += incX;
    //			currentRect.width -= incX;
    //		}
    //		if (xwidMatch) {
    //			// NO cal moure la X del rectangle, nom�s sumar al width
    //			// +inc
    //			currentRect.width += incX;
    //		}
    //		if (yMatch) {
    //			// arista SUPERIOR
    //			// cal moure la Y del rectangle (y+inc) i restar al width
    //			// -inc
    //			currentRect.y += incY;
    //			currentRect.height -= incY;
    //		}
    //		if (yheiMatch) {
    //			// arista INFERIOR
    //			// NO cal moure la Y del rectangle nomes sumar al width +inc
    //			currentRect.height += incY;
    //		}
    //
    //		// sino mirem si est� dins
    //		if (!xMatch && !xwidMatch && !yMatch && !yheiMatch) {
    //			if (currentRect.contains(clic)) {
    //				currentRect.x += incX;
    //				currentRect.y += incY;
    //			}
    //		}
    //
    //		// COMPROVEM LIMITS
    //		if (currentRect.x < 0)
    //			currentRect.x = 0;
    //		if (currentRect.y < 0)
    //			currentRect.y = 0;
    //		if (currentRect.x + currentRect.width > patt2D.getDimX())
    //			currentRect.x = patt2D.getDimX() - currentRect.width;
    //		if (currentRect.y + currentRect.height > patt2D.getDimY())
    //			currentRect.y = patt2D.getDimY() - currentRect.height;
    //
    //		if (repaint) {
    //			this.actualitzarVista();
    //		}
    //
    //	}

    public void editPolygon(java.awt.geom.Point2D.Float dragPoint, float incX, float incY, boolean repaint) {

        if (this.currentPol == null)
            return;

        final Point2D.Float clic = this.getPixel(dragPoint);

        //provem de fer la tolerancia en relacio a scalefit
        final float tolVertex = FastMath.max(5 / this.scalefit, 5);

        //primer mirem si hem clicat sobre algun vertex per moure'l
        int vertex = -1; //el vertex que cliquem
        for (int i = 0; i < this.currentPol.npoints; i++) {
            final Rectangle2D.Float r = new Rectangle2D.Float(this.currentPol.getXVertex(i) - tolVertex,
                    this.currentPol.getYVertex(i) - tolVertex, tolVertex * 2, tolVertex * 2);
            if (r.contains(clic)) {
                vertex = i;
                break;
            }
        }
        //ara vertex assenyala el vertex que hem clicat o b� -1 si no s'ha clicat a cap
        if (vertex >= 0) {
            this.currentPol.incXVertex(vertex, incX);
            this.currentPol.incYVertex(vertex, incY);
            if (repaint)
                this.actualitzarVista();
            return;
        }

        //si no hem clicat a cap vertex mirem si hem clicat a dins i movem tota la zona
        if (this.currentPol.contains(clic)) {
            this.currentPol.translate(FastMath.round(incX), FastMath.round(incY));
            if (repaint)
                this.actualitzarVista();
            return;
        }
    }


    public void setShowDBCompoundRings(boolean show, List<PDCompound> comps) {
        if (this.getPatt2D() == null) {
            return;
        }
        this.showDBCompoundRings = show;
        if (comps == null) {
            this.showDBCompoundRings = false;
            this.dbCompounds = null;
            return;
        }
        if (this.getPatt2D().getWavel() <= 0) {
            log.warning("Wavelength missing");
            this.showDBCompoundRings = false;
            this.dbCompounds = null;
            log.debug("setShowRings: NO WAVELENGTH");
            return;
        }
        this.dbCompounds = comps;
        this.actualitzarVista();
    }

    // al fer zoom es canviara l'origen i l'escala de la imatge
    public void zoom(boolean zoomIn, Point2D.Float centre) {
        final Point2D.Float mousePosition = new Point2D.Float(centre.x, centre.y);
        centre = this.getPixel(centre); // miro a quin pixel estem fent zoom

        // aplico el zoom
        if (zoomIn) {
            this.scalefit = this.scalefit + (incZoom * this.scalefit);
            // posem maxim?
            if (this.scalefit >= maxScaleFit)
                this.scalefit = maxScaleFit;
        } else {
            this.scalefit = this.scalefit - (incZoom * this.scalefit);
            if (this.scalefit <= minScaleFit)
                this.scalefit = minScaleFit;
        }

        // ara el pixel ja no est� al mateix lloc, mirem a quin punt del frame
        // est� (en aquest nou scalefit)
        centre = this.getFramePointFromPixel(centre);

        // ara tenim el punt del jframe que ha de quedar on tenim el mouse
        // apuntant, per tant hem de moure l'origen de
        // la imatge conforme aix� (vector nouCentre-mousePosition)
        this.originX = this.originX + FastMath.round(mousePosition.x - centre.x);
        this.originY = this.originY + FastMath.round(mousePosition.y - centre.y);
        this.actualitzarVista();
    }

    // es mou l'origen a traves d'un increment de les coordenades
    public void moveOrigin(float incX, float incY, boolean repaint) {
        // assignem un nou origen de la imatge amb un increment a les coordenades anteriors
        //  (util per moure'l fen drag del mouse)
        if (D2Dplot_global.isDebug())
            log.writeNameNums("fine", true, "incX,incY", incX, incY);
        this.originX = this.originX + FastMath.round(incX);
        this.originY = this.originY + FastMath.round(incY);
        if (repaint) {
            this.actualitzarVista();
        }
    }

    // ajusta la imatge al panell, mostranoriginXt-la tota sencera (calcula l'scalefit inicial)
    public void fitImage() {
        // l'ajustarem al frame mantenint la relacio, es a dir agafarem l'escala
        // m�s petita segons la mida del frame i la imatge
        if (D2Dplot_global.isDebug()) {
            log.writeNameNums("CONFIG", true, "panelWidth, ImageWidth", this.getPanelImatge().getWidth(),
                    this.getImage().getWidth());
            log.writeNameNums("CONFIG", true, "panelHeight, Imageheigh", this.getPanelImatge().getHeight(),
                    this.getImage().getHeight());
        }

        final double xScale = (double) this.getPanelImatge().getWidth() / this.getImage().getWidth();
        final double yScale = (double) this.getPanelImatge().getHeight() / this.getImage().getHeight(this.imagePanel);
        this.scalefit = (float) FastMath.min(xScale, yScale);
        if (D2Dplot_global.isDebug())
            log.writeNameNums("CONFIG", true, "scalefit", this.scalefit);
        // CENTREM LA IMATGE AL PANELL
        if (FastMath.abs(this.scalefit - xScale) < 0.0001) { //TODO corregit març 2019
            // hem de centrar en y
            final float gap = (this.getPanelImatge().getHeight() - (this.getImage().getHeight()) * this.scalefit) / 2.f;
            this.originY = this.originY + FastMath.round(gap);
        } else {
            // hem de centrar en x
            final float gap = (this.getPanelImatge().getWidth() - (this.getImage().getWidth()) * this.scalefit) / 2.f;
            this.originX = this.originX + FastMath.round(gap);
        }
    }

    //161103 afegit el -0.5f
    public void setScalefit(float scalefit) {
        // centre del zoom:
        final Point2D.Float centrePanel = new Point2D.Float(this.getPanelImatge().getWidth() / 2.f - 0.5f,
                this.getPanelImatge().getHeight() / 2.f - 0.5f);
        Point2D.Float centre = this.getPixel(centrePanel); // miro a quin pixel estem
        // fent zoom

        // apliquem zoom
        this.scalefit = scalefit;

        // ara el pixel ja no est� al mateix lloc, mirem a quin punt del frame
        // est� (en aquest nou scalefit)
        centre = this.getFramePointFromPixel(centre);
        // ara tenim el punt del jframe que ha de quedar on tenim el mouse
        // apuntant, per tant hem de moure l'origen de
        // la imatge conforme aix� (vector nouCentre-mousePosition)
        this.originX = this.originX + FastMath.round(centrePanel.x - centre.x);
        this.originY = this.originY + FastMath.round(centrePanel.y - centre.y);

        this.actualitzarVista();

    }

    //prova utilitzant scalefit
    protected Rectangle calcSubimatgeDinsFrame() {
        final Point2D.Float startCoords = this.getPixel(new Point2D.Float(0, 0)); //nov2018 change 1,1 per 0,0 TODO revisar
        int InPixX = (int) startCoords.x; //faig int per agafar el pixel en questio des del començament
        int InPixY = (int) startCoords.y;
        int OutPixX = InPixX + (int) (this.getPanelImatge().getWidth() / this.scalefit) + 1; //nov2018 tret el +1 final
        int OutPixY = InPixY + (int) (this.getPanelImatge().getHeight() / this.scalefit) + 1;

        // Que no movem la imatge fora del panell
        if (InPixX >= this.getImage().getWidth() || OutPixX < 0 || InPixY >= this.getImage().getHeight()
                || OutPixY < 0) {
            return null;
        }
        if (InPixX < 0)
            InPixX = 0;
        if (InPixY < 0)
            InPixY = 0;
        if (OutPixX >= this.patt2D.getDimX())
            OutPixX = this.patt2D.getDimX() - 1;
        if (OutPixY >= this.patt2D.getDimY())
            OutPixY = this.patt2D.getDimY() - 1;

        return new Rectangle(InPixX, InPixY, OutPixX - InPixX + 1, OutPixY - InPixY + 1);
        //        return new Rectangle(InPixX, InPixY, OutPixX-InPixX, OutPixY-InPixY); //nov 2018 tret els -1
    }

    protected void pintaImatge() {
        if (D2Dplot_global.isDebug())
            log.debug("ImagePanel pintaImatge called");

        if (this.patt2D == null) {
            return;
        }

        final int type = BufferedImage.TYPE_INT_ARGB;
        final BufferedImage im = new BufferedImage(this.patt2D.getDimX(), this.patt2D.getDimY(), type);

        this.minValSlider = this.slider_contrast.getMinimum();
        this.valSlider = this.slider_contrast.getValue();
        this.maxI = this.patt2D.getMaxI();
        this.minI = this.patt2D.getMinI();

        if (!this.isInvertY()) {
            // creem imatge normal
            for (int j = 0; j < this.patt2D.getDimY(); j++) { // per cada fila (Y)
                for (int i = 0; i < this.patt2D.getDimX(); i++) { // per cada columna (X)
                    im.setRGB(i, j, this.getColorOfAPixel(i, j).getRGB());
                }
            }
        } else {
            // creem imatge invertida
            int fila = 0;
            for (int j = this.patt2D.getDimY() - 1; j >= 0; j--) {
                for (int i = 0; i < this.patt2D.getDimX(); i++) {
                    im.setRGB(i, fila, this.getColorOfAPixel(i, j).getRGB());
                }
                fila = fila + 1;
            }
        }
        this.updateImage(im);
    }

    protected Rectangle2D.Float rectangleToFrameCoords(Rectangle2D.Float r) {
        final Point2D.Float vertex = this.getFramePointFromPixel(new Point2D.Float(r.x, r.y));
        final float width = (float) (r.getWidth() * this.scalefit);
        final float height = (float) (r.getHeight() * this.scalefit);
        return new Rectangle2D.Float(vertex.x, vertex.y, width, height);
    }

    private PolyExZone PolToFrameCoords(PolyExZone p) {
        if (p.npoints < 1)
            return null; //CHECK
        final PolyExZone exz = new PolyExZone(false);
        for (int i = 0; i < p.npoints; i++) {
            final Point2D.Float v = this.getFramePointFromPixel(new Point2D.Float(p.getXVertex(i), p.getYVertex(i)));
            exz.addPoint(FastMath.round(v.x), FastMath.round(v.y));
        }
        return exz;
    }

    private void setLabelValues(float pX, float pY, float tth, float dsp, float azim, int inten, boolean ex) {
        this.lblCoordX.setText("x=" + FileUtils.dfX_1.format(pX));
        this.lblCoordY.setText("y=" + FileUtils.dfX_1.format(pY));
        this.lbl2t.setText("2" + D2Dplot_global.theta + "=" + FileUtils.dfX_2.format(tth) + "º");
        if (dsp < 100) {
            this.lbldsp.setText("d=" + FileUtils.dfX_3.format(dsp) + D2Dplot_global.angstrom);
        } else {
            this.lbldsp.setText("d=" + FileUtils.dfX_2.format(dsp) + D2Dplot_global.angstrom);
        }
        if (!ex) {
            this.lblIntensity.setText("I=" + inten);
        } else {
            this.lblIntensity.setText("I=" + inten + " (ex)");
        }
        this.lblAzim.setText("az=" + FileUtils.dfX_1.format(azim) + "º");
    }

    private Ellipse2D.Float ellipseToFrameCoords(Ellipse2D.Float c) {

        final Point2D.Float vertex = new Point2D.Float(c.x, c.y);
        final Point2D.Float newVertex = this.getFramePointFromPixel(vertex);
        final float w = (float) (c.getWidth() * this.scalefit);
        final float h = (float) (c.getHeight() * this.scalefit);
        final Ellipse2D.Float newCercle = new Ellipse2D.Float(newVertex.x, newVertex.y, w, h);
        return newCercle;

    }

    //valor interpolat sobre una recta (fun=0) o una parabola (fun=1)
    private Color intensityBW(int intensity, int maxInt, int minInt, int minVal, int maxVal) {

        float ccomponent = -1.f;

        switch (contrast_fun) {
        case 0:
            // el valor s'interpolara sobre la recta (sliderMin,0) a (sliderMax,1)
            //interpolem
            final float x1 = minVal; // evitem diviso zero
            final float y1 = 0.0f;
            final float x2 = maxVal;
            final float y2 = 1.0f;
            ccomponent = ((y2 - y1) / (x2 - x1)) * intensity + y1 - ((y2 - y1) / (x2 - x1)) * x1;
            break;
        case 1:
            // el valor s'interpolara sobre una quadr�tica y=ax2 (centrat a 0,0)
            //nomes varia el parametre a
            float a = (1.f / (maxVal * maxVal));
            ccomponent = a * (intensity * intensity);
            break;
        case 2:
            // el valor s'interpolara sobre una quadr�tica cap avall y=ax2 + 1 (centrat a 0,1)
            //nomes varia el parametre a
            a = (-1.f / (maxVal * maxVal));
            ccomponent = a * (intensity * intensity) + 1;
            break;
        }

        if (ccomponent == -1.f) {
            return new Color(255, 0, 255);
        }

        if (ccomponent < 0) {
            ccomponent = 0;
        }
        if (ccomponent > 1) {
            ccomponent = 1;
        }

        return new Color(ccomponent, ccomponent, ccomponent);
    }

    /*
     * maxInt,minInt son maxim i minim d'intensitat de la imatge
     * minVal,maxVal corresponen a l'slide. MAX es el valor actual assenyalat per l'slide.
     * grafiques RGB entre minval i maxval amb punt inflexio a (maxval-minval)/2
     * intensitat normalitzada entre minval i maxval
     */
    private Color intensityColor(int intensity, int maxInt, int minInt, int minVal, int maxVal) {

        if (intensity <= 0) {
            return new Color(0, 0, 0);
        } // poso el 0 (o menor) absolut com a negre

        //LIMITS
        if (intensity >= maxVal) {
            return new Color(255, 0, 0);
        }
        if (intensity <= minVal) {
            return new Color(0, 0, 255);
        }

        float red = 0.0f;
        float green = 0.0f;
        float blue = 0.0f;

        //dins rang minVal - maxVal

        //vermell recta de 0 a 1
        float x1 = minVal;
        float y1 = 0.f;
        float x2 = maxVal;// evitem diviso zero
        float y2 = 1.f;
        red = ((y2 - y1) / (x2 - x1)) * intensity + y1 - ((y2 - y1) / (x2 - x1)) * x1;

        //BLUE recta de 1 a 0
        x1 = minVal;
        y1 = 1.f;
        x2 = maxVal;// evitem diviso zero
        y2 = 0.f;
        blue = ((y2 - y1) / (x2 - x1)) * intensity + y1 - ((y2 - y1) / (x2 - x1)) * x1;

        //green fa un pic a half (equvalent a aquesta operacio)
        green = blue * red * 4.f;

        Color c = new Color(255, 0, 255);
        try {
            c = new Color(red, green, blue);
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug()) {
                e.printStackTrace();
                log.debug("invalid color");
            }

        }
        return c;
    }

    //aplica al modificar valors funcio o max/min textbox
    private void applyNewContrastValues() {
        //		int minI=0;
        //		int maxI=getPatt2D().getSaturValue();
        this.minI = 0;
        this.maxI = this.getPatt2D().getSaturValue();
        int fun = getContrast_fun();
        try {
            this.minI = Integer.parseInt(this.txtConminval.getText());
        } catch (final Exception ex) {
            if (D2Dplot_global.isDebug()) {
                ex.printStackTrace();
                log.debug("Error parsing contrast minimum value");
            }
            this.minI = 0;
        }
        try {
            this.maxI = Integer.parseInt(this.txtConmaxval.getText());
        } catch (final Exception ex) {
            if (D2Dplot_global.isDebug()) {
                ex.printStackTrace();
                log.debug("Error parsing contrast max value");
            }

            this.maxI = this.getPatt2D().getSaturValue();
        }
        fun = this.cbox_fun.getSelectedIndex();
        ImagePanel.setContrast_fun(fun);
        this.setSliderContrastValues(this.minI, this.maxI); //ha de mantenir el valor
        this.pintaImatge(); //ja conte actualitzar vista
    }

    //this one keeps the value of the slider
    private void setSliderContrastValues(int min, int max) {
        this.setSliderContrastValues(min, max, this.slider_contrast.getValue());
    }

    private void setSliderContrastValues(int min, int max, int val) {
        this.slider_contrast.setMaximum(max);
        this.slider_contrast.setMinimum(min);
        this.slider_contrast.setMinorTickSpacing((int) ((max - min) / 50.f));
        this.slider_contrast.setValue(val);

        //tots acaben cridant aquest metode, per tant cal actualitzar els valors dels txt per si de cas ve de l'auto:
        this.loadContrastValues();
    }

    private void setSliderContrastValuesAuto() {

        final int max = this.patt2D.getMaxI();
        final int min = this.patt2D.getMinI();
        final int cinque = (int) ((max - min) / 5.f);

        //        if (max<=0){//it is a mask image
        //            this.setSliderContrastValues(0, 5, 1);
        //            return;
        //        }
        float extrafactor = 1.0f;
        if (this.patt2D.getSaturValue() == D2Dplot_global.saturInt)
            extrafactor = 0.2f;
        if (this.patt2D.getMeanI() > 0) {
            final int val = (int) (this.patt2D.getMeanI() + factorAutoContrast * extrafactor * this.patt2D.getSdevI());
            this.setSliderContrastValues(min, max, val);
        } else {
            this.setSliderContrastValues(min, max, min + cinque);
        }
    }

    private void loadContrastValues() {
        this.txtConminval.setText(String.valueOf(this.getSlider_contrast().getMinimum()));
        this.txtConmaxval.setText(String.valueOf(this.getSlider_contrast().getMaximum()));
        this.cbox_fun.setSelectedIndex(ImagePanel.getContrast_fun());
    }

    private void pintaExZclick(Point2D.Float clickpoint, int arestaQuadrat) {
        final Point2D.Float pix = this.getPixel(clickpoint);
        final int ix = (int) pix.x;
        final int iy = (int) pix.y;
        for (int i = ix - arestaQuadrat / 2; i <= ix + arestaQuadrat / 2; i++) {
            for (int j = iy - arestaQuadrat / 2; j <= iy + arestaQuadrat / 2; j++) {
                if (this.getPatt2D().isInside(i, j)) {
                    this.getPatt2D().getPixel(i, j).setExcluded(true);
                    this.getPatt2D().addPaintedEXZpixel(new Point(i, j));
                }
            }
        }
        this.pintaImatge();
    }

    private boolean estaCalibrant() {
        if (!this.allowCalibration)
            return false;
        if (this.calibration != null) {
            return this.calibration.isCalibrating();
        }
        return false;
    }

    private boolean estaPeakSearch() {
        if (!this.allowPKsearch)
            return false;
        if (this.PKsearch != null) {
            return this.PKsearch.isShowPoints();
        }
        return false;
    }

    private boolean estaPeakSearchAddPeaks() {
        if (!this.allowPKsearch)
            return false;
        if (this.estaPeakSearch()) {
            return this.PKsearch.isEditPoints();
        }
        return false;
    }

    private boolean estaDefinintEXZ() {
        if (!this.allowEXZ)
            return false;
        if (this.exZones != null) {
            return this.exZones.isSetExZones();
        }
        return false;
    }

    private boolean estaDincoShowSpots() {
        if (!this.allowDINCO)
            return false;
        if (this.dincoFrame != null) {
            return this.dincoFrame.isShowSpots();
        }
        return false;
    }

    private boolean estaDincoAddPeaks() {
        if (!this.allowDINCO)
            return false;
        if (this.estaDincoShowSpots()) {
            return this.dincoFrame.isAddPeaks();
        }
        return false;
    }

    private boolean estaDincoShowHKL() {
        if (!this.allowDINCO)
            return false;
        if (this.estaDincoShowSpots()) {
            return this.dincoFrame.isShowHKL();
        }
        return false;
    }

    private boolean estaSelectPoints() {
        if (!this.allowSelPoints)
            return false;
        if (this.d2dplot_mainFrame != null) {
            return this.d2dplot_mainFrame.isSelectPoints();
        }
        return false;
    }

    private Color getColorOfAPixel(int x_col, int y_row) {
        Color col;
        final int inten = this.patt2D.getInten(x_col, y_row);
        if (this.getPaintExZ()) {
            if (this.patt2D.isExcluded(x_col, y_row))
                return D2Dplot_global.getColorEXZ();
            if (inten >= this.patt2D.getSaturValue())
                return D2Dplot_global.getColorSATUR();
        }

        if (this.isColor()) {
            // pintem en color
            col = this.intensityColor(inten, this.maxI, this.minI, this.minValSlider, this.valSlider);
        } else {
            // pintem en BW
            col = this.intensityBW(inten, this.maxI, this.minI, this.minValSlider, this.valSlider);
        }

        return col;
    }

    private void do_panelImatge_mouseDragged(MouseEvent e) {
        if (this.mouseDrag == true && this.isPatt2D()) {
            final Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
            float incX, incY;
            // agafem el dragpoint i l'actualitzem
            incX = (p.x - this.dragPoint.x);
            incY = (p.y - this.dragPoint.y);
            this.dragPoint = p;
            this.moveOrigin(incX, incY, true);
            if (D2Dplot_global.isDebug())
                log.writeNameNumPairs("finer", true, "fX,fY,imX,imY,scfit,orX,orY,panw,panh", e.getPoint().x,
                        e.getPoint().y, p.x, p.y, this.scalefit, this.originX, this.originY,
                        this.getPanelImatge().getWidth(), this.getPanelImatge().getHeight());
        }
        if (this.mouseZoom == true && this.isPatt2D()) {
            final Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
            float incY;
            incY = p.y - this.dragPoint.y;
            this.dragPoint = p;
            final boolean zoomIn = (incY < 0);
            this.zoom(zoomIn, this.zoomPoint);
        }
        if (this.isMouseBox() == true && this.isPatt2D()) {
            final Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
            float incX, incY;
            // agafem el dragpoint i l'actualitzem (utilitzem scalefit per la sensibilitat)
            incX = (p.x - this.dragPoint.x) / this.getScalefit();
            incY = (p.y - this.dragPoint.y) / this.getScalefit();
            // hem d'enviar el punt clicat i l'increment a panel imatge perqu�
            // s'encarregui de moure el poligon
            if (this.estaDefinintEXZ()) {
                this.editPolygon(this.dragPoint, incX, incY, true);
            }
            this.dragPoint = p;
        }
        if (this.mouseFree && this.exZones.isDrawingFreeExZone()) {
            this.pintaExZclick(new Point2D.Float(e.getPoint().x, e.getPoint().y), this.mouseFreeArestaQ);
        }
        this.actualitzarVista();

    }

    private void do_panelImatge_mouseMoved(MouseEvent e) {
        // he de normalitzar les coordenades a la mida de la imatge en pixels
        this.currentMousePoint = new Point2D.Float(e.getPoint().x, e.getPoint().y);
        if (this.isPatt2D()) {
            final Point2D.Float pix = this.getPixel(this.currentMousePoint);
            if (pix.x < 0 || pix.y < 0 || pix.x >= this.patt2D.getDimX() || pix.y >= this.patt2D.getDimY()) {
                return;
            }
            // El FastMath.round porta a 2048 i out of bound, millor no arrodonir i truncar aqu� igual que al if anterior
            final Pixel imgPix = this.patt2D.getPixel((int) (pix.x), (int) (pix.y));
            float dsp = -1;
            float tthRad = -1;
            if (this.patt2D.checkIfDistMD()) {
                tthRad = (float) this.patt2D.calc2T(pix, false);
                if (this.patt2D.checkIfWavel()) {
                    dsp = (float) this.patt2D.calcDsp(tthRad);
                }
            }
            final float azim = this.patt2D.getAzimAngle((int) (pix.x), (int) (pix.y), true);
            this.setLabelValues(pix.x, pix.y, (float) FastMath.toDegrees(tthRad), dsp, azim, imgPix.getIntensity(),
                    imgPix.isExcluded());
            if (D2Dplot_global.isDebug())
                log.writeNameNumPairs("finer", true, "fX,fY,imX,imY,scfit,orX,orY,panw,panh", e.getPoint().x,
                        e.getPoint().y, pix.x, pix.y, this.scalefit, this.originX, this.originY,
                        this.getPanelImatge().getWidth(), this.getPanelImatge().getHeight());
        }
        if (this.exZones != null) {
            if (this.exZones.isDrawingFreeExZone())
                this.actualitzarVista();
        }

    }

    // Identificar el botó i segons quin sigui moure o fer zoom
    private void do_panelImatge_mousePressed(MouseEvent arg0) {
        this.dragPoint = new Point2D.Float(arg0.getPoint().x, arg0.getPoint().y);
        if (arg0.getButton() == MOURE) {
            this.mouseDrag = true;
            this.zoomPoint = new Point2D.Float(arg0.getPoint().x, arg0.getPoint().y); //faig servir zoomPoint per guardar el punt inicial
        }
        if (arg0.getButton() == ZOOM_BORRAR) {
            this.zoomPoint = new Point2D.Float(arg0.getPoint().x, arg0.getPoint().y);
            this.mouseZoom = true;
        }
        if (arg0.getButton() == CLICAR) {
            // si estem amb modo calibratge o definint EXZ
            if (this.estaDefinintEXZ()) {
                if (!this.exZones.isDrawingFreeExZone()) {  //TODO: AQUI PETA PERQUE NO EXISTEIX exZones (es null) quan ho fem des de la calibració)
                    this.setMouseBox(true);
                } else {
                    //posar alguna variable? de moment ho faig a mousedrag
                    this.mouseFree = true;
                    this.pintaExZclick(new Point2D.Float(arg0.getPoint().x, arg0.getPoint().y), this.mouseFreeArestaQ);
                }
            }
        }
        this.actualitzarVista();

    }

    private void do_panelImatge_mouseReleased(MouseEvent e) {
        if (e.getButton() == MOURE)
            this.mouseDrag = false;
        if (e.getButton() == ZOOM_BORRAR)
            this.mouseZoom = false;
        if (e.getButton() == CLICAR)
            this.setMouseBox(false);
        this.mouseFree = false;

        if (!this.isPatt2D())
            return;

        //LLAVORS PER ORDRE DE PREFERENCIA (si algun es compleix no es passarà als seguents)
        // 1) definicio exz, 2) calibracio 3)worksol ... ULTIM) select Points

        if (this.estaDefinintEXZ()) {
            if (this.exZones.isDrawingPolExZone()) {
                // afegim o treiem punts a la zona
                if (e.getButton() == CLICAR) {
                    final Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                    this.exZones.getCurrentPolyExZ().addPoint(Math.round(pix.x), Math.round(pix.y));
                }
            }
            if (this.exZones.isDrawingArcExZone()) {
                // clickem 3 punts!
                if (e.getButton() == CLICAR) {
                    final Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                    final boolean finished = this.exZones.getCurrentArcExZ().addClickPoint(pix);
                    if (finished) {
                        this.exZones.finishedArcZone();
                    }
                }
            }
            this.actualitzarVista();
            return;
        }

        if (this.estaCalibrant()) {
            // afegim o treiem punts de la llista CALIB
            Point2D.Float pix = null;
            if (e.getButton() == CLICAR) {
                pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
            }

            if (pix == null)
                return;
            if (this.calibration.isSetting1stPeakCircle()) {
                this.calibration.addPointToRing1Circle(pix);
            }
            this.actualitzarVista();
            return;
        }

        // Si tenim marcada l'opcio showHKLindexing afegim a la llista HKL els punts clicats m�s propers
        if (this.estaDincoAddPeaks() && this.isPatt2D()) {

            //new: afegim el punt a la llista de puntsSol, incrementant el numero (o deixant -1 en cas que sigui en un SOL)
            if (e.getButton() == CLICAR) {
                final Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                final OrientSolucio os = this.dincoFrame.getActiveOrientSol();
                if (os == null)
                    return;
                //2 casos, si s'afegeix en un pxy es posa el següent index. Si es a un fitxer SOL es més complicat... ho gestiona OrientSol
                os.addSolPoint(pix.x, pix.y, true);
            }
            //faig que amb el dret es poden borrar punts afegits
            if (e.getButton() == ZOOM_BORRAR) {
                final Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                //busquem quin �s l'HKL m�s proper i l'assignem
                final PuntSolucio nearestPS = this.dincoFrame.getActiveOrientSol().getNearestPS(pix.x, pix.y, -1);
                if (nearestPS != null) {
                    this.dincoFrame.getActiveOrientSol().removeSolPoint(nearestPS);
                }
            }

            //carregem a la llista
            this.dincoFrame.loadPeakList();
            this.actualitzarVista();
            return;
        }

        // Pksearch
        if (this.estaPeakSearchAddPeaks()) {
            // afegim o treiem punts a la llista pksearch
            if (e.getButton() == CLICAR) {
                final Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                final Peak pic = ImgOps.addPeakFromCoordinates(this.patt2D, pix, PeakSearch.zoneR);
                this.PKsearch.integratePk(pic);
            }
            if (e.getButton() == ZOOM_BORRAR) {
                final Peak pktodel = this.patt2D
                        .findNearestPeak(this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y)), 10);
                if (pktodel != null) {
                    this.PKsearch.removePeak(pktodel);
                    log.debug(pktodel.toString());
                } else {
                    log.debug("pktodel is null");
                }
            }
            this.actualitzarVista();
            return;
        }

        // NOMES afegim els punts i cercles si tenim marcada la indexacio (i no s'ha complert res d'anterior)
        if (this.estaSelectPoints()) {
            // afegim o treiem punts de la llista
            if (e.getButton() == CLICAR) {
                int inten;
                final Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
                inten = (this.patt2D.getInten((int) (pix.x), (int) (pix.y)));
                this.addPuntCercle(new Point2D.Float(e.getPoint().x, e.getPoint().y), inten);
            }
            if (e.getButton() == ZOOM_BORRAR) {
                this.removePuntCercle(new Point2D.Float(e.getPoint().x, e.getPoint().y));
            }
        }
        //i fem lo del zoom si no s'ha mogut de lloc

        if (e.getButton() == MOURE) {
            final float tol = 1.f; //si no ens movem del pixel...
            if (D2Dplot_global.isDebug())
                log.writeNameNumPairs("config", true, "e.getPoint().x,zoomPoint.x,e.getPoint().y,zoomPoint.y",
                        e.getPoint().x, this.zoomPoint.x, e.getPoint().y, this.zoomPoint.y);
            if ((FastMath.abs(e.getPoint().x - this.zoomPoint.x) < tol)
                    && (FastMath.abs(e.getPoint().y - this.zoomPoint.y) < tol)) {
                this.resetView();
            }
        }
        this.actualitzarVista();
    }

    private void do_panelImatge_mouseWheelMoved(MouseWheelEvent e) {
        final Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
        final boolean zoomIn = (e.getWheelRotation() < 0);
        this.zoom(zoomIn, p); //ja fa actualitzar
    }

    private void do_slider_contrast_stateChanged(ChangeEvent arg0) {
        this.pintaImatge(); //ja conte actualitzar vista
    }

    private void do_cbox_fun_itemStateChanged(ItemEvent e) {
        this.applyNewContrastValues();
        //        log.debug("do_cbox_fun_itemStateChanged called");
    }

    private void do_txtConmaxval_actionPerformed(ActionEvent arg0) {
        this.applyNewContrastValues();
        //        log.debug("do_txtConmaxval_actionPerformed called");
    }

    private void do_txtConminval_actionPerformed(ActionEvent e) {
        this.applyNewContrastValues();
        //        log.debug("do_txtConminval_actionPerformed called");
    }

    private void do_chckbxAuto_itemStateChanged(ItemEvent e) {
        log.debug("chckbxAuto itemStateChanged");
        if (!this.isPatt2D())
            return;
        if (this.chckbxAuto.isSelected()) {
            this.setSliderContrastValuesAuto();
        }
    }

    /**
     * @return the ipanelMain
     */
    public JPanel getIpanelMain() {
        return this.imagePanel;
    }

    /**
     * @param ipanelMain the ipanelMain to set
     */
    public void setIpanelMain(JPanel ipanelMain) {
        this.imagePanel = ipanelMain;
    }

    public static float getIncZoom() {
        return incZoom;
    }

    public static void setIncZoom(float incZoom) {
        ImagePanel.incZoom = incZoom;
    }

    public static float getMaxScaleFit() {
        return maxScaleFit;
    }

    public static void setMaxScaleFit(float maxScaleFit) {
        ImagePanel.maxScaleFit = maxScaleFit;
    }

    public static float getMinScaleFit() {
        return minScaleFit;
    }

    public static void setMinScaleFit(float minScaleFit) {
        ImagePanel.minScaleFit = minScaleFit;
    }

    public static int getHklfontSize() {
        return hklfontSize;
    }

    public static void setHklfontSize(int hklfontSize) {
        ImagePanel.hklfontSize = hklfontSize;
    }

    public static float getFactorAutoContrast() {
        return factorAutoContrast;
    }

    public static void setFactorAutoContrast(float factorAutoContrast) {
        ImagePanel.factorAutoContrast = factorAutoContrast;
    }

    public static int getContrast_fun() {
        return contrast_fun;
    }

    public static void setContrast_fun(int contrast_fun) {
        ImagePanel.contrast_fun = contrast_fun;
    }

    private boolean isShowDBCompoundRings() {
        return this.showDBCompoundRings;
    }

    public void setSubimage(BufferedImage subimage) {
        this.subimage = subimage;
    }

    //	public Rectangle2D.Float getCurrentRect() {
    //		return currentRect;
    //	}

    public BufferedImage getImage() {
        return this.image;
    }

    public int getOriginX() {
        return this.originX;
    }

    public int getOriginY() {
        return this.originY;
    }

    public Dades2d getPanelImatge() {
        return this.data2D;
    }

    public Pattern2D getPatt2D() {
        return this.patt2D;
    }

    public float getScalefit() {
        return this.scalefit;
    }

    public BufferedImage getSubimage() {
        return this.subimage;
    }

    public boolean isMouseBox() {
        return this.mouseBox;
    }

    public boolean isPatt2D() {
        if (this.patt2D != null) {
            return true;
        } else {
            return false;
        }
    }

    public void setPaintExZ(boolean paintexz) {
        this.paintExZ = paintexz;
    }

    public boolean getPaintExZ() {
        return this.paintExZ;
    }

    public void setCalibration(Calibration calibration) {
        this.calibration = calibration;
    }

    public void setExZones(ExZones exZones) {
        this.exZones = exZones;
    }

    public void setDinco(IncoPlot df) {
        this.dincoFrame = df;
    }

    public void setPKsearch(PeakSearch pKsearch) {
        this.PKsearch = pKsearch;
    }

    public void setDBdialog(Database dbDialog) {
    }

    public void setMainFrame(MainFrame mf) {
        this.d2dplot_mainFrame = mf;
    }

    public MainFrame getMainFrame() {
        return this.d2dplot_mainFrame;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
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

    public void setPanelImatge(Dades2d panelImatge) {
        this.data2D = panelImatge;
    }

    public void setPatt2D(Pattern2D patt2d) {
        this.patt2D = patt2d;
    }

    public JSlider getSlider_contrast() {
        return this.slider_contrast;
    }

    public boolean isColor() {
        return this.color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public boolean isInvertY() {
        return this.invertY;
    }

    public void setInvertY(boolean invertY) {
        this.invertY = invertY;
    }

    public int getMouseFreeArestaQ() {
        return this.mouseFreeArestaQ;
    }

    public void setMouseFreeArestaQ(int mouseFreeArestaQ) {
        this.mouseFreeArestaQ = mouseFreeArestaQ;
    }

    public Point2D.Float getCurrentMousePoint() {
        return this.currentMousePoint;
    }

    public void setCurrentMousePoint(Point2D.Float currentMousePoint) {
        this.currentMousePoint = currentMousePoint;
    }

    //    ------------------------------------ PANELL DE DIBUIX

    public class Dades2d extends JPanel {

        private Color colorCallibEllipses = Color.orange;
        private Color colorGuessPointsEllipses = Color.red;
        private Color colorFittedEllipses = Color.green;
        private Color colorBoundariesEllipses = Color.magenta;
        private Color colorExcludedZonesLines = Color.orange;
        private Color colorPeakSearch = Color.green;
        private Color colorPeakSearchSelected = Color.red;
        private Color colorQLcomp = Color.green;
        private Color colorDBcomp = Color.cyan;
        private Color colorEXZ = Color.magenta;
        private Color colorSATUR = Color.yellow;

        private static final long serialVersionUID = 1L;

        public Dades2d() {
            super();
        }

        //Dibuix de les Ellipses de calibració
        private void drawCalibrationC(Graphics2D g1) {
            ArrayList<Point2D.Float> points = ImagePanel.this.calibration.getPointsRing1circle();
            final int radiPunt = Calibration.getRadipunt();
            final BasicStroke stroke = new BasicStroke(1.5f);
            g1.setStroke(stroke);
            if (points != null) {
                g1.setColor(this.colorCallibEllipses);
                final Iterator<Point2D.Float> itrP = points.iterator();
                while (itrP.hasNext()) {
                    Point2D.Float p = itrP.next();
                    p = ImagePanel.this.getFramePointFromPixel(p);
                    g1.drawOval((int) p.x - radiPunt, (int) p.y - radiPunt, radiPunt * 2, radiPunt * 2);
                }
            }

            if (ImagePanel.this.calibration.getSolutions() != null) {

                //dibuixem els punts estimats dels anells
                if (ImagePanel.this.calibration.isShowGuessPoints()) {
                    g1.setColor(this.colorGuessPointsEllipses);
                    final Iterator<EllipsePars> itre = ImagePanel.this.calibration.getSolutions().iterator();
                    while (itre.hasNext()) {
                        final EllipsePars e = itre.next();
                        points = e.getEstimPoints();
                        final Iterator<Point2D.Float> itrP = points.iterator();
                        while (itrP.hasNext()) {
                            Point2D.Float p = itrP.next();
                            p = ImagePanel.this.getFramePointFromPixel(p);
                            g1.drawOval((int) p.x - radiPunt, (int) p.y - radiPunt, radiPunt * 2, radiPunt * 2);
                        }
                    }
                }

                //ara dibuixem les ellipses fitejades
                if (ImagePanel.this.calibration.isShowFittedEllipses()) {
                    g1.setColor(this.colorFittedEllipses);
                    final Iterator<EllipsePars> itre = ImagePanel.this.calibration.getSolutions().iterator();
                    while (itre.hasNext()) {
                        final EllipsePars e = itre.next();
                        final Point2D.Float cen = ImagePanel.this
                                .getFramePointFromPixel(new Point2D.Float((float) e.getXcen(), (float) e.getYcen()));
                        g1.drawOval((int) cen.x - radiPunt, (int) cen.y - radiPunt, radiPunt * 2, radiPunt * 2);
                        points = e.getEllipsePoints(0, 360, 5);
                        if (points == null)
                            continue;
                        //escribim a quin anell de LaB6 pertany (sobre el 1r punt)
                        final Point2D.Float pLabel = ImagePanel.this.getFramePointFromPixel(points.get(0));
                        g1.drawString(Integer.toString(e.getLab6ring()), pLabel.x, pLabel.y - 2);
                        for (int i = 0; i < points.size(); i++) {
                            final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(points.get(i));
                            Point2D.Float p2 = null;
                            if (i == (points.size() - 1)) {
                                p2 = ImagePanel.this.getFramePointFromPixel(points.get(0));
                            } else {
                                p2 = ImagePanel.this.getFramePointFromPixel(points.get(i + 1));
                            }
                            g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                    FastMath.round(p2.y));
                        }
                    }
                }

                if (ImagePanel.this.calibration.isShowSearchEllipsesBoundaries()) {
                    //DEBUG ELLIPSES + and - BOUNDARIES
                    g1.setColor(this.colorBoundariesEllipses);
                    if (ImagePanel.this.calibration.getElliCerques() == null)
                        return;
                    final Iterator<EllipsePars> itre = ImagePanel.this.calibration.getElliCerques().iterator();
                    while (itre.hasNext()) {
                        final EllipsePars e = itre.next();
                        final Point2D.Float cen = ImagePanel.this
                                .getFramePointFromPixel(new Point2D.Float((float) e.getXcen(), (float) e.getYcen()));
                        g1.drawOval((int) cen.x - radiPunt, (int) cen.y - radiPunt, radiPunt * 2, radiPunt * 2);
                        points = e.getEllipsePoints(0, 360, 5);
                        if (points == null)
                            continue;
                        for (int i = 0; i < points.size(); i++) {
                            final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(points.get(i));
                            Point2D.Float p2 = null;
                            if (i == (points.size() - 1)) {
                                p2 = ImagePanel.this.getFramePointFromPixel(points.get(0));
                            } else {
                                p2 = ImagePanel.this.getFramePointFromPixel(points.get(i + 1));
                            }
                            g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                    FastMath.round(p2.y));
                        }
                    }
                }

            }

        }

        //Dibuix de les Zones Excloses
        private void drawExZones(Graphics2D g1) {

            //DIBUIXEM POLIGONS
            ImagePanel.this.currentPol = ImagePanel.this.exZones.getCurrentPolyExZ();
            g1.setPaint(this.colorExcludedZonesLines);
            final BasicStroke stroke = new BasicStroke(1.0f);
            g1.setStroke(stroke);
            if (ImagePanel.this.currentPol != null) {
                if (ImagePanel.this.currentPol.npoints > 0) {
                    g1.draw(ImagePanel.this.PolToFrameCoords(ImagePanel.this.currentPol));
                    ImagePanel.this.exZones.updateSelectedElement();
                }
            }

            //DIBUIXEM ELS ARCS
            final ArcExZone currentArc = ImagePanel.this.exZones.getCurrentArcExZ();
            if (currentArc != null && currentArc.getNclicks() >= 2) {

                //copio el mateix que per a peaksearch
                final int px = currentArc.getPx();
                final int py = currentArc.getPy();
                final float angdeg = currentArc.getHalfAzimApertureDeg();
                final float tol2t = currentArc.getRadialWth2t();

                if (tol2t > 0 && angdeg > 0) {
                    final double t2rad = ImagePanel.this.patt2D.calc2T(px, py, false);
                    final float azim = ImagePanel.this.patt2D.getAzimAngle(px, py, true);

                    final EllipsePars eOut = ImgOps.getElliPars(ImagePanel.this.patt2D,
                            (t2rad + FastMath.toRadians(tol2t / 2)));
                    final EllipsePars eIn = ImgOps.getElliPars(ImagePanel.this.patt2D,
                            (t2rad - FastMath.toRadians(tol2t / 2)));
                    final ArrayList<Point2D.Float> pointsOut = eOut.getEllipsePoints(azim - angdeg, azim + angdeg,
                            0.1f);
                    final ArrayList<Point2D.Float> pointsIn = eIn.getEllipsePoints(azim - angdeg, azim + angdeg, 0.1f);

                    //ara juntem els punts
                    if (pointsIn != null && pointsOut != null) {
                        for (int j = 0; j < pointsIn.size(); j++) {
                            final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(j));
                            if (j == (pointsIn.size() - 1))
                                break;
                            final Point2D.Float p2 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(j + 1));
                            g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                    FastMath.round(p2.y));
                        }

                        for (int j = 0; j < pointsOut.size(); j++) {
                            final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(j));
                            if (j == (pointsOut.size() - 1))
                                break;
                            final Point2D.Float p2 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(j + 1));
                            g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                    FastMath.round(p2.y));
                        }

                        //ara juntem els dos arcs
                        Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(0));
                        Point2D.Float p2 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(0));
                        g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                FastMath.round(p2.y));
                        p1 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(pointsIn.size() - 1));
                        p2 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(pointsOut.size() - 1));
                        g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                FastMath.round(p2.y));
                    }
                }
            }

            if (ImagePanel.this.exZones.isDrawingFreeExZone()) {
                //dibuixem quadrat al punt en questio
                if (ImagePanel.this.getCurrentMousePoint() != null) {
                    final Point2D.Float pix = ImagePanel.this.getPixel(ImagePanel.this.getCurrentMousePoint());
                    final Rectangle2D.Float r = new Rectangle2D.Float(
                            pix.x - ImagePanel.this.getMouseFreeArestaQ() / 2.f,
                            pix.y - ImagePanel.this.getMouseFreeArestaQ() / 2.f, ImagePanel.this.getMouseFreeArestaQ(),
                            ImagePanel.this.getMouseFreeArestaQ());
                    g1.draw(ImagePanel.this.rectangleToFrameCoords(r));
                }

            }

            // dibuixem el marge
            final int marge = ImagePanel.this.patt2D.getExz_margin();
            if (marge <= 0)
                return;
            final Rectangle2D.Float r = new Rectangle2D.Float(marge, marge,
                    ImagePanel.this.patt2D.getDimX() - 2 * marge, ImagePanel.this.patt2D.getDimY() - 2 * marge);
            g1.draw(ImagePanel.this.rectangleToFrameCoords(r));
        }

        //Dibuix dels punts seleccionats
        private void drawPuntsEllipses(Graphics2D g1) {
            final Iterator<PuntClick> itrPC = ImagePanel.this.patt2D.getPuntsCercles().iterator();
            while (itrPC.hasNext()) {
                final PuntClick pc = itrPC.next();
                final EllipsePars e = pc.getEllipse();
                final Ellipse2D.Float p = pc.getPunt();

                //PRIMER DIBUIXEM L'ELIPSE I DESPRES EL PUNT (el centre de l'elipse no el dibuixem en aquest cas, nomes a la calibracio)
                // hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
                BasicStroke stroke = new BasicStroke(1.0f);
                g1.setStroke(stroke);
                g1.setPaint(PuntClick.getColorCercle());
                final ArrayList<Point2D.Float> points = e.getEllipsePoints(0, 360, 5);
                if (points == null)
                    continue;
                for (int i = 0; i < points.size(); i++) {
                    final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(points.get(i));
                    Point2D.Float p2 = null;
                    if (i == (points.size() - 1)) {
                        p2 = ImagePanel.this.getFramePointFromPixel(points.get(0));
                    } else {
                        p2 = ImagePanel.this.getFramePointFromPixel(points.get(i + 1));
                    }
                    g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                }
                //el punt
                g1.setPaint(PuntClick.getColorPunt());
                stroke = new BasicStroke(2.0f);
                g1.setStroke(stroke);
                g1.draw(ImagePanel.this.ellipseToFrameCoords(p));
                g1.fill(ImagePanel.this.ellipseToFrameCoords(p));
            }
        }

        //DIBUIX DB/QUICKLIST rings
        private void drawPDCompoundRings(Graphics2D g1, PDCompound pdc, Color c) {

            final Iterator<HKLrefl> itpks = pdc.getPeaks().iterator();
            while (itpks.hasNext()) {
                final HKLrefl ref = itpks.next();
                final float dsp = (float) ref.getDsp();
                final float t2rad = (float) ImagePanel.this.patt2D.dspToT2(dsp, false);
                //2theta to pixels
                final EllipsePars e = ImgOps.getElliPars(ImagePanel.this.patt2D, t2rad);

                //PRIMER DIBUIXEM L'ELIPSE I DESPRES EL PUNT (el centre de l'elipse no el dibuixem en aquest cas, nomes a la calibracio)
                // hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
                final BasicStroke stroke = new BasicStroke(1.0f);
                g1.setStroke(stroke);
                g1.setPaint(c);
                final ArrayList<Point2D.Float> points = e.getEllipsePoints(0, 360, 5);
                if (points == null)
                    continue;
                for (int i = 0; i < points.size(); i++) {
                    final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(points.get(i));
                    Point2D.Float p2 = null;
                    if (i == (points.size() - 1)) {
                        p2 = ImagePanel.this.getFramePointFromPixel(points.get(0));
                    } else {
                        p2 = ImagePanel.this.getFramePointFromPixel(points.get(i + 1));
                    }
                    g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                }
            }
        }

        private void drawPksearch(Graphics2D g1) {
            if (ImagePanel.this.getPatt2D().getPkSearchResult() != null) {
                final Iterator<Peak> itrpk = ImagePanel.this.getPatt2D().getPkSearchResult().iterator();
                while (itrpk.hasNext()) {
                    final Point2D.Float pk = itrpk.next().getPixelCentre();
                    g1.setPaint(this.colorPeakSearch);
                    final BasicStroke stroke = new BasicStroke(1.0f);
                    g1.setStroke(stroke);
                    final float radiPixelsCercle = (ImagePanel.this.PKsearch.getPlotSize()) / 2.f;
                    Ellipse2D.Float e = new Ellipse2D.Float(pk.x - radiPixelsCercle + 0.5f,
                            pk.y - radiPixelsCercle + 0.5f, radiPixelsCercle * 2, radiPixelsCercle * 2); //+0.5 per posar la rodoneta al centre del pixel en qüestió
                    e = ImagePanel.this.ellipseToFrameCoords(e);
                    g1.draw(e);
                    g1.fill(e);
                }

                //ara pintarem el seleccionat si és el cas
                g1.setPaint(this.colorPeakSearchSelected);
                final BasicStroke stroke = new BasicStroke(1.0f);
                g1.setStroke(stroke);

                final Peak[] selpeaks = ImagePanel.this.PKsearch.getSelectedPeaks();
                if (selpeaks == null)
                    return;

                for (final Peak selpeak : selpeaks) {
                    if (selpeak == null)
                        continue;
                    final float px = selpeak.getPixelCentre().x;
                    final float py = selpeak.getPixelCentre().y;
                    final float angdeg = selpeak.getZona().getAzimAngle();
                    final float tol2t = ImgOps.getTol2TFromIntRad(ImagePanel.this.patt2D, px, py,
                            selpeak.getZona().getIntradPix());

                    if (tol2t <= 0)
                        continue;
                    if (angdeg <= 0)
                        continue;

                    final double t2rad = ImagePanel.this.patt2D.calc2T(selpeak.getPixelCentre(), false);
                    final float azim = ImagePanel.this.patt2D.getAzimAngle((int) (px), (int) (py), true);

                    final EllipsePars eOut = ImgOps.getElliPars(ImagePanel.this.patt2D,
                            (t2rad + FastMath.toRadians(tol2t / 2)));
                    final EllipsePars eIn = ImgOps.getElliPars(ImagePanel.this.patt2D,
                            (t2rad - FastMath.toRadians(tol2t / 2)));
                    final ArrayList<Point2D.Float> pointsOut = eOut.getEllipsePoints(azim - angdeg, azim + angdeg,
                            0.5f);
                    final ArrayList<Point2D.Float> pointsIn = eIn.getEllipsePoints(azim - angdeg, azim + angdeg, 0.5f);

                    //TODO:revisar hecanviat i < pointsIn.size per j <... estava malament no?
                    //ara juntem els punts
                    if (pointsIn == null)
                        continue;
                    if (pointsOut == null)
                        continue;
                    for (int j = 0; j < pointsIn.size(); j++) {
                        final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(j));
                        if (j == (pointsIn.size() - 1))
                            break;
                        final Point2D.Float p2 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(j + 1));
                        g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                FastMath.round(p2.y));
                    }

                    for (int j = 0; j < pointsOut.size(); j++) {
                        final Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(j));
                        if (j == (pointsOut.size() - 1))
                            break;
                        final Point2D.Float p2 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(j + 1));
                        g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x),
                                FastMath.round(p2.y));
                    }

                    //ara juntem els dos arcs
                    Point2D.Float p1 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(0));
                    Point2D.Float p2 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(0));
                    g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                    p1 = ImagePanel.this.getFramePointFromPixel(pointsIn.get(pointsIn.size() - 1));
                    p2 = ImagePanel.this.getFramePointFromPixel(pointsOut.get(pointsOut.size() - 1));
                    g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
                }
            }
        }

        //DIBUIX DINCO
        private void drawSolPoints(Graphics2D g1) {
            //mosrem ELS SELCCIONATS
            final Object[] oos = ImagePanel.this.dincoFrame.getActiveOrientSols();
            if (oos == null)
                return;
            for (final Object oo : oos) {
                final OrientSolucio os = (OrientSolucio) oo;
                final Iterator<PuntSolucio> itrS = os.getSol().iterator();
                while (itrS.hasNext()) {
                    final PuntSolucio s = itrS.next();
                    Ellipse2D.Float e = null;
                    g1.setPaint(s.getColorPunt());
                    BasicStroke stroke = new BasicStroke(PuntSolucio.getDincoSolPointStrokeSize());
                    g1.setStroke(stroke);
                    if (ImagePanel.this.dincoFrame.getSelectedPuntSolucio() != null) {
                        if (ImagePanel.this.dincoFrame.getSelectedPuntSolucio().equals(s)) {
                            g1.setPaint(D2Dplot_global.getComplimentColor(s.getColorPunt()));
                            stroke = new BasicStroke(PuntSolucio.getDincoSolPointStrokeSize() + 1);
                            g1.setStroke(stroke);
                        }
                    }
                    e = ImagePanel.this.ellipseToFrameCoords(s.getEllipseAsDrawingPoint());
                    g1.draw(e);

                    //g1.fill(e);
                    if (ImagePanel.this.estaDincoShowHKL()) {
                        final Font font = new Font("Dialog", Font.PLAIN, hklfontSize + 1);
                        g1.setFont(font);
                        g1.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                        g1.drawString(s.getHKL(), e.x + e.width, e.y);
                    }
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //            log.fine("paint Component dades2d called");

            if (ImagePanel.this.getImage() != null) {
                final Graphics2D g2 = (Graphics2D) g;

                if (ImagePanel.this.scalefit <= 0) {
                    ImagePanel.this.fitImage();
                }

                final Rectangle rect = ImagePanel.this.calcSubimatgeDinsFrame();
                if (rect == null || rect.width == 0 || rect.height == 0) {
                    // no part of image is displayed in the panel
                    return;
                }
                try {
                    ImagePanel.this.subimage = ImagePanel.this.getImage().getSubimage(rect.x, rect.y, rect.width,
                            rect.height);
                    //log.writeNameNumPairs("fine", true, "rect.x, rect.y,", rect.x, rect.y);
                } catch (final Exception e) {
                    if (D2Dplot_global.isDebug())
                        e.printStackTrace();
                    log.debug("error getting the subImage");
                }
                // Rectangle r = g2.getClipBounds();
                final AffineTransform t = new AffineTransform();
                float offsetX = ImagePanel.this.originX % ImagePanel.this.scalefit;
                if (ImagePanel.this.originX > 0)
                    offsetX = ImagePanel.this.originX;
                float offsetY = ImagePanel.this.originY % ImagePanel.this.scalefit;
                if (ImagePanel.this.originY > 0)
                    offsetY = ImagePanel.this.originY;
                t.translate(offsetX, offsetY);
                t.scale(ImagePanel.this.scalefit, ImagePanel.this.scalefit);

                final BufferedImage off_Image = new BufferedImage(this.getWidth(), this.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                final Graphics2D g1 = off_Image.createGraphics();

                g1.addRenderingHints(
                        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)); // perque es vegin mes suaus...

                g2.drawImage(ImagePanel.this.getSubimage(), t, null); //dibuixem imatge

                // dibuixem els cercles
                g2.addRenderingHints(
                        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)); // perque es vegin mes suaus...

                if (ImagePanel.this.estaDincoShowSpots()) {
                    this.drawSolPoints(g1);
                }

                // nomes hauria d'haver-hi la possibilitat d'1 opcio, calibracio o zones excloses?
                if (ImagePanel.this.estaCalibrant()) {
                    this.drawCalibrationC(g1);
                }
                if (ImagePanel.this.estaDefinintEXZ()) {
                    this.drawExZones(g1);
                }
                if (ImagePanel.this.estaPeakSearch()) {
                    this.drawPksearch(g1);
                }

                // nomes hauria d'haver-hi la possibilitat d'1 opcio, drawIndexing o drawHKLindexing
                if (ImagePanel.this.estaSelectPoints() && !ImagePanel.this.estaDincoAddPeaks()) {
                    this.drawPuntsEllipses(g1);
                }


                if (ImagePanel.this.isShowDBCompoundRings()) {
                    if (ImagePanel.this.dbCompounds != null) {
                    	if (!ImagePanel.this.dbCompounds.isEmpty()) {
                    		//max dibuixem 4 --> missatge passat a Database al listvaluechanged
//                    		if (ImagePanel.this.dbCompounds.size()>4) {
//                            	log.info("Only the reflections of the first 4 selected compounds will be shown");
//                            }
                    		int nc = 0;
                    		for (PDCompound pdc:ImagePanel.this.dbCompounds) {
                    			Color col = this.colorDBcomp;
                    			switch(nc) {
                    			case 1:
                    				col = this.colorQLcomp;
                    				break;
                    			case 2:
                    				col = FileUtils.getComplementary(this.colorDBcomp);
                    				break;
                    			case 3:
                    				col = FileUtils.getComplementary(this.colorQLcomp);
                    			default:
                    				break;
                    			}
                    			this.drawPDCompoundRings(g1, pdc, col);
                    			nc++;
                    			if (nc>=4)break;
                    		}
                    		
//                    		this.drawPDCompoundRings(g1, ImagePanel.this.dbCompounds, this.colorDBcomp);
                    	}

                            
                    }
                        
                }

                //draw beam center
                //                g1.setPaint(Color.CYAN);
                //                Point2D.Float c = getFramePointFromPixel(new Point2D.Float(patt2D.getCentrX(),patt2D.getCentrY()));
                //                g1.drawOval((int)c.x-2, (int)c.y-2, 4, 4);

                //provo dibuixar
                g.drawImage(off_Image, 0, 0, null); //ara dibuixem a sobre la parafarnalia

                //ATENCIO: faig dispose nomes de g1 perquè es el que he creat jo, el g2 (g) no ho he de fer!!
                g1.dispose();
                //                g2.dispose();

            }
        }

        public Color getColorCallibEllipses() {
            return this.colorCallibEllipses;
        }

        public void setColorCallibEllipses(Color colorCallibEllipses) {
            this.colorCallibEllipses = colorCallibEllipses;
        }

        public Color getColorGuessPointsEllipses() {
            return this.colorGuessPointsEllipses;
        }

        public void setColorGuessPointsEllipses(Color colorGuessPointsEllipses) {
            this.colorGuessPointsEllipses = colorGuessPointsEllipses;
        }

        public Color getColorFittedEllipses() {
            return this.colorFittedEllipses;
        }

        public void setColorFittedEllipses(Color colorFittedEllipses) {
            this.colorFittedEllipses = colorFittedEllipses;
        }

        public Color getColorBoundariesEllipses() {
            return this.colorBoundariesEllipses;
        }

        public void setColorBoundariesEllipses(Color colorBoundariesEllipses) {
            this.colorBoundariesEllipses = colorBoundariesEllipses;
        }

        public Color getColorExcludedZonesLines() {
            return this.colorExcludedZonesLines;
        }

        public void setColorExcludedZonesLines(Color colorExcludedZonesL) {
            this.colorExcludedZonesLines = colorExcludedZonesL;
        }

        public Color getColorPeakSearch() {
            return this.colorPeakSearch;
        }

        public void setColorPeakSearch(Color colorPeakSearch) {
            this.colorPeakSearch = colorPeakSearch;
        }

        public Color getColorPeakSearchSelected() {
            return this.colorPeakSearchSelected;
        }

        public void setColorPeakSearchSelected(Color colorPeakSearchSelected) {
            this.colorPeakSearchSelected = colorPeakSearchSelected;
        }

        public Color getColorQLcomp() {
            return this.colorQLcomp;
        }

        public void setColorQLcomp(Color colorQLcomp) {
            this.colorQLcomp = colorQLcomp;
        }

        public Color getColorDBcomp() {
            return this.colorDBcomp;
        }

        public void setColorDBcomp(Color colorDBcomp) {
            this.colorDBcomp = colorDBcomp;
        }

        /**
         * @return the colorEXZ
         */
        public Color getColorEXZ() {
            return this.colorEXZ;
        }

        /**
         * @param colorEXZ the colorEXZ to set
         */
        public void setColorEXZ(Color colorEXZ) {
            this.colorEXZ = colorEXZ;
        }

        /**
         * @return the colorSATUR
         */
        public Color getColorSATUR() {
            return this.colorSATUR;
        }

        /**
         * @param colorSATUR the colorSATUR to set
         */
        public void setColorSATUR(Color colorSATUR) {
            this.colorSATUR = colorSATUR;
        }
    }

}