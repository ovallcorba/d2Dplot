package com.vava33.d2dplot;

//per editar posar true sidecontrols

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

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.ArcExZone;
import com.vava33.d2dplot.auxi.EllipsePars;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.OrientSolucio;
import com.vava33.d2dplot.auxi.PDCompound;
import com.vava33.d2dplot.auxi.PDReflection;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.d2dplot.auxi.Peak;
import com.vava33.d2dplot.auxi.Pixel;
import com.vava33.d2dplot.auxi.PolyExZone;
import com.vava33.d2dplot.auxi.PuntClick;
import com.vava33.d2dplot.auxi.PuntSolucio;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ImagePanel {

	// DEFINICIO BUTONS DEL MOUSE
	private static int MOURE = MouseEvent.BUTTON2;
	private static int CLICAR = MouseEvent.BUTTON1;
	private static int ZOOM_BORRAR = MouseEvent.BUTTON3;

	//parametres interaccio/contrast
	private static float incZoom = 0.05f;
	private static float maxScaleFit=40.f;
	private static float minScaleFit=0.10f;
	private static int hklfontSize=13;
	//    private static float factSliderMax=3.f;
	private static int contrast_fun=0;
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
	boolean fit = true;
	private int originX, originY;
	private Point2D.Float zoomPoint, dragPoint;
	private Point2D.Float currentMousePoint;
	private int minValSlider,valSlider,maxI,minI;

	//parametres per Forçar no pintar alguna cosa
	private boolean allowCalibration=true;
	private boolean allowEXZ=true;
	private boolean allowDINCO=true;
	private boolean allowSelPoints=true;
	private boolean allowPKsearch=true;
	private boolean showQuickListCompoundRings = false; //aquest es l'unic de control en aquest cas
	private boolean showDBCompoundRings = false; //aquest es l'unic de control en aquest cas

	//el diagrama i el frame
	private Pattern2D patt2D;
	private dades2d data2D;
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
	private Rectangle2D.Float currentRect;
	private PolyExZone currentPol;
	private PDCompound quickListCompound = null;
	private PDCompound dbCompound = null;

	//UI elements
	private JPanel imagePanel;
	private JLabel lbl2t;
	private JLabel lblCoordX;
	private JLabel lblIntensity;
	private JPanel panel;
	private JSlider slider_contrast;
	private JCheckBox chckbxAuto;
	private JTextField txtConminval;
	private JTextField txtConmaxval;
	private JLabel lblContrast;
	private JComboBox<String> cbox_fun;
	private JLabel lbldsp;
	private JLabel lblCoordY;
	private JLabel lblAzim;
	Border etchedborder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	/**
	 * Create the panel.
	 */
	public ImagePanel() {
		imagePanel = new JPanel();

		imagePanel.setLayout(new MigLayout("fill, insets 5", "[:90px:100px][grow]", "[grow]"));
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
		//            this.getPanelImatge().setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		imagePanel.add(this.getPanelImatge(), "cell 1 0 1 1,grow");
		//            add(new JPanel(), "cell 1 0,grow");

		this.panel = new JPanel();
		this.panel.setBorder(null);
		imagePanel.add(this.panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[]", "[][][][][][][][][grow][][][]"));
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

		lblAzim = new JLabel("azim");
		lblAzim.setHorizontalAlignment(SwingConstants.CENTER);
		lblAzim.setFont(new Font("Dialog", Font.BOLD, 12));
		panel.add(lblAzim, "cell 0 3,growx,aligny center");

		lbldsp = new JLabel("dsp");
		lbldsp.setFont(new Font("Dialog", Font.BOLD, 12));
		lbldsp.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbldsp, "cell 0 4,growx,aligny center");
		this.lblIntensity = new JLabel("Intensity");
		lblIntensity.setFont(new Font("Dialog", Font.BOLD, 12));
		lblIntensity.setHorizontalAlignment(SwingConstants.CENTER);
		this.panel.add(this.lblIntensity, "cell 0 5,growx,aligny center");

		lblContrast = new JLabel("Contrast:");
		lblContrast.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(lblContrast, "cell 0 6,alignx center");

		txtConmaxval = new JTextField();
		txtConmaxval.setFont(new Font("Dialog", Font.PLAIN, 10));
		txtConmaxval.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txtConmaxval, "cell 0 7,alignx center,aligny center");
		txtConmaxval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_txtConmaxval_actionPerformed(arg0);
			}
		});
		txtConmaxval.setColumns(5);

		this.slider_contrast = new JSlider();
		slider_contrast.setOrientation(SwingConstants.VERTICAL);
		panel.add(slider_contrast, "cell 0 8,grow");
		slider_contrast.setFont(new Font("Dialog", Font.PLAIN, 10));
		this.slider_contrast.setInverted(false);
		this.slider_contrast.setMinorTickSpacing(1);
		this.slider_contrast.setSnapToTicks(false);
		this.slider_contrast.setMaximum(0);
		this.slider_contrast.setValue(this.slider_contrast.getMaximum()/2);
		this.slider_contrast.setMinimumSize(new Dimension(50,50));
		this.slider_contrast.setPreferredSize(new Dimension(50,100));

		txtConminval = new JTextField();
		txtConminval.setFont(new Font("Dialog", Font.PLAIN, 10));
		txtConminval.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txtConminval, "cell 0 9,alignx center,aligny center");
		txtConminval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_txtConminval_actionPerformed(e);
			}
		});
		txtConminval.setColumns(5);

		cbox_fun = new JComboBox<String>();
		cbox_fun.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(cbox_fun, "cell 0 10,growx");
		cbox_fun.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				do_cbox_fun_itemStateChanged(e);
			}
		});
		cbox_fun.setModel(new DefaultComboBoxModel<String>(new String[] {"linear", "quadr+", "quadr-"}));
		cbox_fun.setSelectedIndex(0);

		chckbxAuto = new JCheckBox("Auto");
		chckbxAuto.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				do_chckbxAuto_itemStateChanged(e);
			}
		});
		chckbxAuto.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel.add(chckbxAuto, "cell 0 11,alignx center,aligny center");
		chckbxAuto.setToolTipText("Automatic contrast adjustment when opening consecutive images");
		this.slider_contrast.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				do_slider_contrast_stateChanged(arg0);
			}
		});

		//iniciem
		this.resetView();
	}

	public void resetView() {
		this.originX = 0;
		this.originY = 0;
		scalefit = 0.0f;
		this.actualitzarVista();
	}

	public void actualitzarVista(){
		imagePanel.repaint();
	}

	public void setImagePatt2D(Pattern2D pattern) {
		if(pattern == null)return;

		if ((pattern.getMaxI()==0)&&(pattern.getMinI()==0)) {
			pattern.recalcMaxMinI();
		}
		
		this.patt2D = pattern;
		log.writeNameNumPairs("config", true, "maxI,minI", pattern.getMaxI(),pattern.getMinI());
		log.debug("slider value before (max,min)= "+this.slider_contrast.getValue()+" ("+this.slider_contrast.getMaximum()+","+this.slider_contrast.getMinimum()+")");
		if(this.chckbxAuto.isSelected()){
			this.setSliderContrastValuesAuto();
		}else{
			if (slider_contrast.getValue()==0) {
				this.setSliderContrastValuesAuto();
			}else {
				this.setSliderContrastValues(this.patt2D.getMinI(), this.patt2D.getMaxI());	
			}
			
		}
		log.debug("slider value after (max,min)= "+this.slider_contrast.getValue()+" ("+this.slider_contrast.getMaximum()+","+this.slider_contrast.getMinimum()+")");
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
		float x = (px.x * scalefit) + originX; //0.5 per posar-ho al centre del pixel
		float y = (px.y * scalefit) + originY;
		return new Point2D.Float(x,y);
	}
	//    
	//    // segons la mida de la imatge actual, les coordenades d'un punt assenyalat amb el mouse correspondran a un pixel o
	//    // a un altre, aquesta subrutina ho corregeix: Donat el punt p on el mouse es troba te'l torna com a pixel de la imatge
	public Point2D.Float getPixel(Point2D.Float p) {
		float x = (p.x - originX) / scalefit;
		float y = (p.y - originY) / scalefit;
		return new Point2D.Float(x,y);
	}


	// afegim un punt i cercle a la llista de pics donat el punt en coordeandes
	// del panell (cal convertir-les abans)
	public void addPuntCercle(Point2D.Float p, int inten) {
		p = this.getPixel(p);
		patt2D.addPuntCercle(p, inten);
	}

	// donat un punt clicat mirarem si hi ha cercle a aquest pixel i en cas que aixi sigui el borrarem
	public void removePuntCercle(Point2D.Float clic) {
		patt2D.removePuntCercle(this.getPixel(clic)); // el passem a pixels
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
				currentRect.x += incX;
				currentRect.y += incY;
			}
		}

		// COMPROVEM LIMITS
		if (currentRect.x < 0)
			currentRect.x = 0;
		if (currentRect.y < 0)
			currentRect.y = 0;
		if (currentRect.x + currentRect.width > patt2D.getDimX())
			currentRect.x = patt2D.getDimX() - currentRect.width;
		if (currentRect.y + currentRect.height > patt2D.getDimY())
			currentRect.y = patt2D.getDimY() - currentRect.height;

		if (repaint) {
			this.actualitzarVista();
		}

	}

	public void editPolygon(java.awt.geom.Point2D.Float dragPoint, float incX, float incY, boolean repaint) {

		if (currentPol == null)return;

		Point2D.Float clic = getPixel(dragPoint);

		//provem de fer la tolerancia en relacio a scalefit
		float tolVertex = FastMath.max(5/scalefit,5);

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
			if(repaint)this.actualitzarVista();
			return;
		}

		//si no hem clicat a cap vertex mirem si hem clicat a dins i movem tota la zona
		if(currentPol.contains(clic)){
			currentPol.translate(FastMath.round(incX), FastMath.round(incY));
			if(repaint)this.actualitzarVista();
			return;
		}
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
			log.warning("Wavelength missing");
			this.showQuickListCompoundRings = false;
			this.quickListCompound = null;
			log.debug("setShowRings: NO WAVELENGTH");
			return;
		}
		this.quickListCompound = c;
		this.actualitzarVista();
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
			log.warning("Wavelength missing");
			this.showDBCompoundRings = false;
			this.dbCompound = null;
			log.debug("setShowRings: NO WAVELENGTH");
			return;
		}
		this.dbCompound = c;
		this.actualitzarVista();
	}

	// al fer zoom es canviara l'origen i l'escala de la imatge
	public void zoom(boolean zoomIn, Point2D.Float centre) {
		Point2D.Float mousePosition = new Point2D.Float(centre.x, centre.y);
		centre = getPixel(centre); // miro a quin pixel estem fent zoom

		// aplico el zoom
		if (zoomIn) {
			scalefit = scalefit + (incZoom * scalefit);
			// posem maxim?
			if (scalefit >= maxScaleFit)
				scalefit = maxScaleFit;
		} else {
			scalefit = scalefit - (incZoom * scalefit);
			if (scalefit <= minScaleFit)
				scalefit = minScaleFit;
		}

		// ara el pixel ja no est� al mateix lloc, mirem a quin punt del frame
		// est� (en aquest nou scalefit)
		centre = getFramePointFromPixel(centre);

		// ara tenim el punt del jframe que ha de quedar on tenim el mouse
		// apuntant, per tant hem de moure l'origen de
		// la imatge conforme aix� (vector nouCentre-mousePosition)
		originX = originX + FastMath.round(mousePosition.x - centre.x);
		originY = originY + FastMath.round(mousePosition.y - centre.y);
		this.actualitzarVista();
	}


	// es mou l'origen a traves d'un increment de les coordenades
	public void moveOrigin(float incX, float incY, boolean repaint) {
		// assignem un nou origen de la imatge amb un increment a les coordenades anteriors
		//  (util per moure'l fen drag del mouse)
		if (D2Dplot_global.isDebug())log.writeNameNums("fine", true, "incX,incY", incX,incY);
		originX = originX + FastMath.round(incX);
		originY = originY + FastMath.round(incY);
		if (repaint) {
			this.actualitzarVista();
		}
	}


	// ajusta la imatge al panell, mostranoriginXt-la tota sencera (calcula l'scalefit inicial)
	public void fitImage() {
		// l'ajustarem al frame mantenint la relacio, es a dir agafarem l'escala
		// m�s petita segons la mida del frame i la imatge
		if (D2Dplot_global.isDebug()) {
			log.writeNameNums("CONFIG", true, "panelWidth, ImageWidth", getPanelImatge().getWidth(),getImage().getWidth());
			log.writeNameNums("CONFIG", true, "panelHeight, Imageheigh", getPanelImatge().getHeight(),getImage().getHeight());
		}

		double xScale = (double) getPanelImatge().getWidth() / getImage().getWidth();
		double yScale = (double) getPanelImatge().getHeight() / getImage().getHeight(imagePanel);
		scalefit = (float) FastMath.min(xScale, yScale);
		if (D2Dplot_global.isDebug())log.writeNameNums("CONFIG", true, "scalefit", scalefit);
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

	//161103 afegit el -0.5f
	public void setScalefit(float scalefit) {
		// centre del zoom:
		Point2D.Float centrePanel = new Point2D.Float(getPanelImatge().getWidth() / 2.f - 0.5f,
				getPanelImatge().getHeight() / 2.f -0.5f);
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

		this.actualitzarVista();

	}

	//prova utilitzant scalefit
	protected Rectangle calcSubimatgeDinsFrame() {
		Point2D.Float startCoords = getPixel(new Point2D.Float(0, 0)); //nov2018 change 1,1 per 0,0 TODO revisar
		int InPixX = (int)startCoords.x; //faig int per agafar el pixel en questio des del començament
		int InPixY = (int)startCoords.y;
		int OutPixX = InPixX + (int)(getPanelImatge().getWidth()/scalefit) + 1; //nov2018 tret el +1 final
		int OutPixY = InPixY + (int)(getPanelImatge().getHeight()/scalefit) + 1;

		// Que no movem la imatge fora del panell
		if (InPixX >= getImage().getWidth() || OutPixX < 0 || InPixY >= getImage().getHeight() || OutPixY < 0) {
			return null;
		}
		if (InPixX < 0)InPixX = 0;
		if (InPixY < 0)InPixY = 0;
		if (OutPixX >= patt2D.getDimX())OutPixX = patt2D.getDimX()-1;
		if (OutPixY >= patt2D.getDimY())OutPixY = patt2D.getDimY()-1;

		return new Rectangle(InPixX, InPixY, OutPixX-InPixX+1, OutPixY-InPixY+1);
		//        return new Rectangle(InPixX, InPixY, OutPixX-InPixX, OutPixY-InPixY); //nov 2018 tret els -1
	}

    protected void pintaImatge() {
		if (D2Dplot_global.isDebug())log.debug("ImagePanel pintaImatge called");

		if (patt2D == null) {
			return;
		}

		int type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage im = new BufferedImage(patt2D.getDimX(), patt2D.getDimY(), type);


		minValSlider = this.slider_contrast.getMinimum();
		valSlider = this.slider_contrast.getValue();
		maxI = patt2D.getMaxI();
		minI = patt2D.getMinI();

		if (!this.isInvertY()) {
			// creem imatge normal
			for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
				for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
					im.setRGB(i, j, this.getColorOfAPixel(i, j).getRGB());
				}
			}
		} else {
			// creem imatge invertida
			int fila = 0;
			for (int j = patt2D.getDimY() - 1; j >= 0; j--) {
				for (int i = 0; i < patt2D.getDimX(); i++) {
					im.setRGB(i, fila, this.getColorOfAPixel(i, j).getRGB());
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

	private PolyExZone PolToFrameCoords(PolyExZone p) {
		if (p.npoints<1)return null; //CHECK
		PolyExZone exz = new PolyExZone(false);
		for (int i=0; i<p.npoints;i++){
			Point2D.Float v = getFramePointFromPixel(new Point2D.Float(p.getXVertex(i),p.getYVertex(i)));
			exz.addPoint(FastMath.round(v.x), FastMath.round(v.y));
		}
		return exz;
	}

	private void setLabelValues(float pX, float pY, float tth, float dsp, float azim, int inten, boolean ex){
		lblCoordX.setText("x="+FileUtils.dfX_1.format(pX));
		lblCoordY.setText("y="+FileUtils.dfX_1.format(pY));
		lbl2t.setText("2" + D2Dplot_global.theta + "=" +FileUtils.dfX_2.format(tth)+"º");
		if (dsp<100){
			lbldsp.setText("d="+FileUtils.dfX_3.format(dsp)+D2Dplot_global.angstrom);    
		}else{
			lbldsp.setText("d="+FileUtils.dfX_2.format(dsp)+D2Dplot_global.angstrom);
		}
		if (!ex) {
			lblIntensity.setText("I=" + inten);    
		}else {
			lblIntensity.setText("I=" + inten +" (ex)");
		}
		lblAzim.setText("az="+FileUtils.dfX_1.format(azim)+"º");
	}

	private Ellipse2D.Float ellipseToFrameCoords(Ellipse2D.Float c) {

		Point2D.Float vertex = new Point2D.Float(c.x, c.y);
		Point2D.Float newVertex = getFramePointFromPixel(vertex);
		float w = (float) (c.getWidth() * scalefit);
		float h = (float) (c.getHeight() * scalefit);
		Ellipse2D.Float newCercle = new Ellipse2D.Float(newVertex.x, newVertex.y, w, h);
		return newCercle;

	}

	//valor interpolat sobre una recta (fun=0) o una parabola (fun=1)
	private Color intensityBW(int intensity, int maxInt, int minInt,int minVal, int maxVal) {

		float ccomponent=-1.f;

		switch(contrast_fun){
		case 0:
			// el valor s'interpolara sobre la recta (sliderMin,0) a (sliderMax,1)
			//interpolem
			float x1 = minVal; // evitem diviso zero
			float y1 = 0.0f;
			float x2 = maxVal;
			float y2 = 1.0f;
			ccomponent = ((y2 - y1) / (x2 - x1)) * intensity + y1 - ((y2 - y1) / (x2 - x1)) * x1;
			break;
		case 1:
			// el valor s'interpolara sobre una quadr�tica y=ax2 (centrat a 0,0)
			//nomes varia el parametre a
			float a=(1.f/(maxVal*maxVal));
			ccomponent = a*(intensity*intensity);
			break;
		case 2:
			// el valor s'interpolara sobre una quadr�tica cap avall y=ax2 + 1 (centrat a 0,1)
			//nomes varia el parametre a
			a=(-1.f/(maxVal*maxVal));
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

	/*
	 * maxInt,minInt son maxim i minim d'intensitat de la imatge
	 * minVal,maxVal corresponen a l'slide. MAX es el valor actual assenyalat per l'slide.
	 * grafiques RGB entre minval i maxval amb punt inflexio a (maxval-minval)/2
	 * intensitat normalitzada entre minval i maxval
	 */
	private Color intensityColor(int intensity, int maxInt, int minInt, int minVal, int maxVal) {

		if (intensity <= 0) {
			return new Color(0,0,0);
		} // poso el 0 (o menor) absolut com a negre

		//LIMITS
		if(intensity>=maxVal){
			return new Color(255,0,0);
		}         
		if(intensity<=minVal){
			return new Color(0,0,255);
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
		green = blue*red*4.f;

		Color c = new Color(255, 0, 255);
		try{
			c = new Color(red, green, blue);
		}catch(Exception e){
			if (D2Dplot_global.isDebug()) {
				e.printStackTrace();
				log.debug("invalid color");
			}

		}
		return c;
	}
	
	//aplica al modificar valors funcio o max/min textbox
	private void applyNewContrastValues() {  
		int minI=0;
		int maxI=getPatt2D().getSaturValue();
		int fun=getContrast_fun();
		try{
			minI=Integer.parseInt(txtConminval.getText());
		}catch(Exception ex){
			if (D2Dplot_global.isDebug()) {
				ex.printStackTrace();
				log.debug("Error parsing contrast minimum value");
			}
			minI=0;
		}
		try{
			maxI=Integer.parseInt(txtConmaxval.getText());
		}catch(Exception ex){
			if (D2Dplot_global.isDebug()) {
				ex.printStackTrace();
				log.debug("Error parsing contrast max value");
			}

			maxI=getPatt2D().getSaturValue();
		}
		fun=cbox_fun.getSelectedIndex();
		ImagePanel.setContrast_fun(fun);
		this.setSliderContrastValues(minI, maxI); //ha de mantenir el valor
		this.pintaImatge(); //ja conte actualitzar vista
	}

	//this one keeps the value of the slider
	private void setSliderContrastValues(int min, int max){
		this.setSliderContrastValues(min, max,this.slider_contrast.getValue());
	}

	private void setSliderContrastValues(int min, int max, int val){
		this.slider_contrast.setMaximum(max);
		this.slider_contrast.setMinimum(min);
		this.slider_contrast.setMinorTickSpacing((int)((float)(max-min)/50.f));
		this.slider_contrast.setValue(val);
		
		//tots acaben cridant aquest metode, per tant cal actualitzar els valors dels txt per si de cas ve de l'auto:
		this.loadContrastValues();
	}

	private void setSliderContrastValuesAuto(){

		int max = patt2D.getMaxI();
		int min = patt2D.getMinI();
		int cinque = (int)((float)(max-min)/5.f);

		//        if (max<=0){//it is a mask image
		//            this.setSliderContrastValues(0, 5, 1);
		//            return;
		//        }
		float extrafactor = 1.0f;
		if (this.patt2D.getSaturValue()==D2Dplot_global.saturInt)extrafactor = 0.2f;
		if (this.patt2D.getMeanI()>0){
			int val = (int) (this.patt2D.getMeanI()+factorAutoContrast*extrafactor*this.patt2D.getSdevI());
			this.setSliderContrastValues(min, max, val);
		}else{
			this.setSliderContrastValues(min, max, min+cinque);
		}
	}

	private void loadContrastValues(){
		txtConminval.setText(String.valueOf(this.getSlider_contrast().getMinimum()));
		txtConmaxval.setText(String.valueOf(this.getSlider_contrast().getMaximum()));
		cbox_fun.setSelectedIndex(ImagePanel.getContrast_fun());
	}
	
	private void pintaExZclick(Point2D.Float clickpoint, int arestaQuadrat){
		Point2D.Float pix = this.getPixel(clickpoint);
		int ix = (int)pix.x;
		int iy = (int)pix.y;
		for (int i = ix-(int)(arestaQuadrat/2); i<=ix+(int)(arestaQuadrat/2); i++){
			for (int j = iy-(int)(arestaQuadrat/2); j<=iy+(int)(arestaQuadrat/2); j++){
				if (this.getPatt2D().isInside(i,j)){
					this.getPatt2D().getPixel(i, j).setExcluded(true);
					this.getPatt2D().addPaintedEXZpixel(new Point(i, j));
				}
			}
		}
		this.pintaImatge();
	}

	private boolean estaCalibrant() {
		if (!allowCalibration) return false;
		if (calibration != null) {
			return calibration.isCalibrating();
		}
		return false;
	}

	private boolean estaPeakSearch() {
		if (!allowPKsearch) return false;
		if (PKsearch != null) {
			return PKsearch.isShowPoints();
		}
		return false;
	}

	private boolean estaPeakSearchAddPeaks(){
		if (!allowPKsearch) return false;
		if (estaPeakSearch()){
			return PKsearch.isEditPoints();
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

	private boolean estaDincoShowSpots(){
		if (!allowDINCO) return false;
		if (dincoFrame != null){
			return dincoFrame.isShowSpots();
		}
		return false;
	}

	private boolean estaDincoAddPeaks(){
		if (!allowDINCO) return false;
		if (estaDincoShowSpots()){
			return dincoFrame.isAddPeaks();
		}
		return false;
	}

	private boolean estaDincoShowHKL(){
		if (!allowDINCO) return false;
		if (estaDincoShowSpots()){
			return dincoFrame.isShowHKL();
		}
		return false;
	}

	private boolean estaSelectPoints(){
		if (!allowSelPoints) return false;
		if (d2dplot_mainFrame != null){
			return d2dplot_mainFrame.isSelectPoints();
		}
		return false;
	}

	private Color getColorOfAPixel(int x_col, int y_row){
		Color col;
		int inten = patt2D.getInten(x_col, y_row);
		if (this.getPaintExZ()){
			if (patt2D.isExcluded(x_col, y_row)) return D2Dplot_global.getColorEXZ();
			if (inten>=patt2D.getSaturValue()) return D2Dplot_global.getColorSATUR();
		}

		if (this.isColor()) {
			// pintem en color
			col = intensityColor(inten, maxI,minI, minValSlider,valSlider);
		} else {
			// pintem en BW
			col = intensityBW(inten, maxI,minI,minValSlider,valSlider);
		}

		return col;
	}



	private void do_panelImatge_mouseDragged(MouseEvent e) {
		if (this.mouseDrag == true && this.isPatt2D()) {
			Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
			float incX, incY;
			// agafem el dragpoint i l'actualitzem
			incX = (p.x - dragPoint.x);
			incY = (p.y - dragPoint.y);
			this.dragPoint = p;
			this.moveOrigin(incX, incY, true);    
			if (D2Dplot_global.isDebug())log.writeNameNumPairs("finer", true, "fX,fY,imX,imY,scfit,orX,orY,panw,panh", e.getPoint().x,e.getPoint().y,p.x,p.y,scalefit,originX,originY,getPanelImatge().getWidth(),getPanelImatge().getHeight());
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
		if (this.mouseFree && exZones.isDrawingFreeExZone()){
			pintaExZclick(new Point2D.Float(e.getPoint().x, e.getPoint().y),mouseFreeArestaQ);
		}
		actualitzarVista();

	}

	private void do_panelImatge_mouseMoved(MouseEvent e) {
		// he de normalitzar les coordenades a la mida de la imatge en pixels
		this.currentMousePoint = new Point2D.Float(e.getPoint().x, e.getPoint().y);
		if (this.isPatt2D()) {
			Point2D.Float pix = this.getPixel(currentMousePoint);
			if (pix.x < 0 || pix.y < 0 || pix.x >= patt2D.getDimX() || pix.y >= patt2D.getDimY()) {
				return;
			}
			// El FastMath.round porta a 2048 i out of bound, millor no arrodonir i truncar aqu� igual que al if anterior
			Pixel imgPix = patt2D.getPixel((int) (pix.x), (int) (pix.y));
			float dsp=-1;
			float tthRad=-1;
			if (patt2D.checkIfDistMD()){
				tthRad =  (float) patt2D.calc2T(pix,false);
				if (patt2D.checkIfWavel()){
					dsp = (float) patt2D.calcDsp(tthRad);
				}  
			}
			float azim = patt2D.getAzimAngle((int)(pix.x), (int)(pix.y), true);
			this.setLabelValues(pix.x, pix.y, (float)FastMath.toDegrees(tthRad), dsp, azim, imgPix.getIntensity(),imgPix.isExcluded());              
			if (D2Dplot_global.isDebug())log.writeNameNumPairs("finer", true, "fX,fY,imX,imY,scfit,orX,orY,panw,panh", e.getPoint().x,e.getPoint().y,pix.x,pix.y,scalefit,originX,originY,getPanelImatge().getWidth(),getPanelImatge().getHeight());
		}
		if (exZones!=null){
			if (exZones.isDrawingFreeExZone()) this.actualitzarVista();    
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
			if (estaDefinintEXZ()){
				if (!exZones.isDrawingFreeExZone()){  //TODO: AQUI PETA PERQUE NO EXISTEIX exZones (es null) quan ho fem des de la calibració)
					this.setMouseBox(true);    
				}else{
					//posar alguna variable? de moment ho faig a mousedrag
					this.mouseFree=true;
					pintaExZclick(new Point2D.Float(arg0.getPoint().x, arg0.getPoint().y),mouseFreeArestaQ);
				}
			}
		}
		actualitzarVista();

	}

	private void do_panelImatge_mouseReleased(MouseEvent e) {
		if (e.getButton() == MOURE)
			this.mouseDrag = false;
		if (e.getButton() == ZOOM_BORRAR)
			this.mouseZoom = false;
		if (e.getButton() == CLICAR)
			this.setMouseBox(false);
		this.mouseFree=false;

		if (!this.isPatt2D())return;

		//LLAVORS PER ORDRE DE PREFERENCIA (si algun es compleix no es passarà als seguents)
		// 1) definicio exz, 2) calibracio 3)worksol ... ULTIM) select Points

		if(estaDefinintEXZ()){
			if(exZones.isDrawingPolExZone()){
				// afegim o treiem punts a la zona
				if (e.getButton() == CLICAR) {
					Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
					exZones.getCurrentPolyExZ().addPoint(Math.round(pix.x), Math.round(pix.y));
				}
			}
			if(exZones.isDrawingArcExZone()){
				// clickem 3 punts!
				if (e.getButton() == CLICAR) {
					Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
					boolean finished = exZones.getCurrentArcExZ().addClickPoint(pix);
					if (finished){
						exZones.finishedArcZone();
					}
				}
			}    
			actualitzarVista();
			return;
		}

		if(estaCalibrant()){
			// afegim o treiem punts de la llista CALIB
			Point2D.Float pix = null;
			if (e.getButton() == CLICAR) {
				pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
			}

			if (pix == null) return;
			if (calibration.isSetting1stPeakCircle()){
				calibration.addPointToRing1Circle(pix);
			}
			actualitzarVista();
			return;
		}

		// Si tenim marcada l'opcio showHKLindexing afegim a la llista HKL els punts clicats m�s propers
		if (estaDincoAddPeaks() && this.isPatt2D()){

			//new: afegim el punt a la llista de puntsSol, incrementant el numero (o deixant -1 en cas que sigui en un SOL)
			if (e.getButton() == CLICAR) {
				Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
				OrientSolucio os = this.dincoFrame.getActiveOrientSol();
				if (os==null)return;
				//2 casos, si s'afegeix en un pxy es posa el següent index. Si es a un fitxer SOL es més complicat... ho gestiona OrientSol
				os.addSolPoint(pix.x, pix.y, true);
			}
			//faig que amb el dret es poden borrar punts afegits
			if (e.getButton() == ZOOM_BORRAR) {
				Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
				//busquem quin �s l'HKL m�s proper i l'assignem
				PuntSolucio nearestPS = this.dincoFrame.getActiveOrientSol().getNearestPS(pix.x,pix.y,-1);
				if (nearestPS!=null){
					this.dincoFrame.getActiveOrientSol().removeSolPoint(nearestPS);
				}
			}

			//carregem a la llista
			this.dincoFrame.loadPeakList();
			actualitzarVista();
			return;
		}

		// Pksearch
		if (estaPeakSearchAddPeaks()) {
			// afegim o treiem punts a la llista pksearch
			if (e.getButton() == CLICAR) {
				Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
				Peak pic = ImgOps.addPeakFromCoordinates(patt2D, pix, PeakSearch.zoneR);
				PKsearch.integratePk(pic);
			}
			if (e.getButton() == ZOOM_BORRAR) {
				Peak pktodel = patt2D.findNearestPeak(this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y)),10);
				if (pktodel != null) {
					PKsearch.removePeak(pktodel);
					log.debug(pktodel.toString());
				}else{
					log.debug("pktodel is null");
				}
			}
			actualitzarVista();
			return;
		}

		// NOMES afegim els punts i cercles si tenim marcada la indexacio (i no s'ha complert res d'anterior)
		if (estaSelectPoints()) {
			// afegim o treiem punts de la llista
			if (e.getButton() == CLICAR) {
				int inten;
				Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
				inten = (int) (patt2D.getInten((int)(pix.x), (int)(pix.y)));
				this.addPuntCercle(new Point2D.Float(e.getPoint().x, e.getPoint().y), inten);
			}
			if (e.getButton() == ZOOM_BORRAR) {
				this.removePuntCercle(new Point2D.Float(e.getPoint().x, e.getPoint().y));
			}
		}
		//i fem lo del zoom si no s'ha mogut de lloc

		if (e.getButton() == MOURE) {
			float tol = 1.f; //si no ens movem del pixel...
			if (D2Dplot_global.isDebug())log.writeNameNumPairs("config", true, "e.getPoint().x,zoomPoint.x,e.getPoint().y,zoomPoint.y", e.getPoint().x,zoomPoint.x,e.getPoint().y,zoomPoint.y);
			if ((FastMath.abs(e.getPoint().x-zoomPoint.x)<tol)&&(FastMath.abs(e.getPoint().y-zoomPoint.y)<tol)){
				this.resetView();
			}
		}
		actualitzarVista();
	}

	private void do_panelImatge_mouseWheelMoved(MouseWheelEvent e) {
		Point2D.Float p = new Point2D.Float(e.getPoint().x, e.getPoint().y);
		boolean zoomIn = (e.getWheelRotation() < 0);
		this.zoom(zoomIn, p); //ja fa actualitzar
	}

	private void do_slider_contrast_stateChanged(ChangeEvent arg0) {
		this.pintaImatge(); //ja conte actualitzar vista
	}

	private void do_cbox_fun_itemStateChanged(ItemEvent e) {
		applyNewContrastValues();
		//        log.debug("do_cbox_fun_itemStateChanged called");
	}

	private void do_txtConmaxval_actionPerformed(ActionEvent arg0) {
		applyNewContrastValues();
		//        log.debug("do_txtConmaxval_actionPerformed called");
	}

	private void do_txtConminval_actionPerformed(ActionEvent e) {
		applyNewContrastValues();
		//        log.debug("do_txtConminval_actionPerformed called");
	}
	private void do_chckbxAuto_itemStateChanged(ItemEvent e) {
		log.debug("chckbxAuto itemStateChanged");
		if (!this.isPatt2D())return;
		if (chckbxAuto.isSelected()){
			setSliderContrastValuesAuto();
		}
	}
	/**
	 * @return the ipanelMain
	 */
	public JPanel getIpanelMain() {
		return imagePanel;
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

	private boolean isShowDBCompoundRings(){
		return showDBCompoundRings;
	}

	private boolean isShowQuickListCompoundRings(){
		return showQuickListCompoundRings;
	}

	public void setSubimage(BufferedImage subimage) {
		this.subimage = subimage;
	}

	public Rectangle2D.Float getCurrentRect() {
		return currentRect;
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
		return data2D;
	}

	public Pattern2D getPatt2D() {
		return patt2D;
	}

	public float getScalefit() {
		return scalefit;
	}

	public BufferedImage getSubimage() {
		return subimage;
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

	public void setPaintExZ(boolean paintexz){
		this.paintExZ = paintexz;
	}

	public boolean getPaintExZ(){
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
		PKsearch = pKsearch;
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

	public void setPanelImatge(dades2d panelImatge) {
		this.data2D = panelImatge;
	}

	public void setPatt2D(Pattern2D patt2d) {
		patt2D = patt2d;
	}

	public JSlider getSlider_contrast() {
		return slider_contrast;
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

	public int getMouseFreeArestaQ() {
		return mouseFreeArestaQ;
	}

	public void setMouseFreeArestaQ(int mouseFreeArestaQ) {
		this.mouseFreeArestaQ = mouseFreeArestaQ;
	}

	public Point2D.Float getCurrentMousePoint() {
		return currentMousePoint;
	}

	public void setCurrentMousePoint(Point2D.Float currentMousePoint) {
		this.currentMousePoint = currentMousePoint;
	}

	//    ------------------------------------ PANELL DE DIBUIX

	public class dades2d extends JPanel {

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

		public dades2d(){
			super();
		}

		//Dibuix de les Ellipses de calibració
		private void drawCalibrationC(Graphics2D g1){
			ArrayList<Point2D.Float> points = calibration.getPointsRing1circle();
			int radiPunt = Calibration.getRadipunt();
			BasicStroke stroke = new BasicStroke(1.5f);
			g1.setStroke(stroke);
			if (points != null){
				g1.setColor(colorCallibEllipses);
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
					g1.setColor(colorGuessPointsEllipses);
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
					g1.setColor(colorFittedEllipses);
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
					g1.setColor(colorBoundariesEllipses);
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

			//DIBUIXEM POLIGONS
			currentPol = exZones.getCurrentPolyExZ();
			g1.setPaint(colorExcludedZonesLines);
			BasicStroke stroke = new BasicStroke(1.0f);
			g1.setStroke(stroke);
			if (currentPol != null) {
				if (currentPol.npoints>0) {
					g1.draw(PolToFrameCoords(currentPol));
					exZones.updateSelectedElement();
				}
			}

			//DIBUIXEM ELS ARCS
			ArcExZone currentArc = exZones.getCurrentArcExZ();
			if (currentArc != null && currentArc.getNclicks()>=2) {

				//copio el mateix que per a peaksearch
				int px = currentArc.getPx();
				int py = currentArc.getPy();
				float angdeg = currentArc.getHalfAzimApertureDeg();
				float tol2t = currentArc.getRadialWth2t();

				if (tol2t>0 && angdeg>0){
					double t2rad = patt2D.calc2T(px, py, false);
					float azim = patt2D.getAzimAngle(px, py, true);


					EllipsePars eOut = ImgOps.getElliPars(patt2D, (t2rad+FastMath.toRadians(tol2t/2)));
					EllipsePars eIn= ImgOps.getElliPars(patt2D, (t2rad-FastMath.toRadians(tol2t/2)));
					ArrayList<Point2D.Float>pointsOut =  eOut.getEllipsePoints(azim-angdeg, azim+angdeg, 0.1f);
					ArrayList<Point2D.Float>pointsIn =  eIn.getEllipsePoints(azim-angdeg, azim+angdeg, 0.1f);

					//ara juntem els punts
					if (pointsIn!=null && pointsOut!=null){
						for (int j = 0; j < pointsIn.size(); j++){
							Point2D.Float p1 = getFramePointFromPixel(pointsIn.get(j));
							if (j==(pointsIn.size()-1))break;
							Point2D.Float p2 = getFramePointFromPixel(pointsIn.get(j+1));
							g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
						}

						for (int j = 0; j < pointsOut.size(); j++){
							Point2D.Float p1 = getFramePointFromPixel(pointsOut.get(j));
							if (j==(pointsOut.size()-1))break;
							Point2D.Float p2 = getFramePointFromPixel(pointsOut.get(j+1));
							g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
						}

						//ara juntem els dos arcs
						Point2D.Float p1 = getFramePointFromPixel(pointsIn.get(0));
						Point2D.Float p2 = getFramePointFromPixel(pointsOut.get(0));
						g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
						p1 = getFramePointFromPixel(pointsIn.get(pointsIn.size()-1));
						p2 = getFramePointFromPixel(pointsOut.get(pointsOut.size()-1));
						g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));                        
					}
				}
			}

			if (exZones.isDrawingFreeExZone()){
				//dibuixem quadrat al punt en questio
				if (getCurrentMousePoint()!=null){
					Point2D.Float pix = getPixel(getCurrentMousePoint());
					Rectangle2D.Float r = new Rectangle2D.Float(pix.x-getMouseFreeArestaQ()/2, pix.y-getMouseFreeArestaQ()/2,getMouseFreeArestaQ(),getMouseFreeArestaQ());
					g1.draw(rectangleToFrameCoords(r));
				}

			}

			// dibuixem el marge
			int marge = patt2D.getExz_margin();
			if (marge <= 0)
				return;
			Rectangle2D.Float r = new Rectangle2D.Float(marge, marge, patt2D.getDimX() - 2 * marge, patt2D.getDimY() - 2 * marge);
			g1.draw(rectangleToFrameCoords(r));
		}

		//Dibuix dels punts seleccionats
		private void drawPuntsEllipses(Graphics2D g1) {
			Iterator<PuntClick> itrPC = patt2D.getPuntsCercles().iterator();
			while (itrPC.hasNext()) {
				PuntClick pc = itrPC.next();
				EllipsePars e = pc.getEllipse();
				Ellipse2D.Float p = pc.getPunt();

				//PRIMER DIBUIXEM L'ELIPSE I DESPRES EL PUNT (el centre de l'elipse no el dibuixem en aquest cas, nomes a la calibracio)
				// hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
				BasicStroke stroke = new BasicStroke(1.0f);
				g1.setStroke(stroke);
				g1.setPaint(PuntClick.getColorCercle());
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
				g1.setPaint(PuntClick.getColorPunt());
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

		private void drawPksearch(Graphics2D g1) {
			if (getPatt2D().getPkSearchResult()!=null){
				Iterator<Peak> itrpk = getPatt2D().getPkSearchResult().iterator();
				while (itrpk.hasNext()){
					Point2D.Float pk = ((Peak)itrpk.next()).getPixelCentre();
					g1.setPaint(colorPeakSearch);
					BasicStroke stroke = new BasicStroke(1.0f);
					g1.setStroke(stroke);
					float radiPixelsCercle = ((float)PKsearch.getPlotSize())/2.f;
					Ellipse2D.Float e = new Ellipse2D.Float(pk.x-radiPixelsCercle,pk.y-radiPixelsCercle,radiPixelsCercle*2,radiPixelsCercle*2);
					e = ellipseToFrameCoords(e);
					g1.draw(e);
					g1.fill(e);
				}

				//ara pintarem el seleccionat si és el cas
				g1.setPaint(colorPeakSearchSelected);
				BasicStroke stroke = new BasicStroke(1.0f);
				g1.setStroke(stroke);

				Peak[] selpeaks = PKsearch.getSelectedPeaks();
				if (selpeaks==null)return;


				for (int i=0;i<selpeaks.length;i++){
					if (selpeaks[i]==null)continue;
					float px = selpeaks[i].getPixelCentre().x;
					float py = selpeaks[i].getPixelCentre().y;
					float angdeg = selpeaks[i].getZona().getAzimAngle();
					float tol2t = ImgOps.getTol2TFromIntRad(patt2D, px, py, selpeaks[i].getZona().getIntradPix());

					if (tol2t<=0)continue;
					if (angdeg<=0)continue;

					double t2rad = patt2D.calc2T(selpeaks[i].getPixelCentre(), false);
					float azim = patt2D.getAzimAngle((int)(px), (int)(py), true);


					EllipsePars eOut = ImgOps.getElliPars(patt2D, (t2rad+FastMath.toRadians(tol2t/2)));
					EllipsePars eIn= ImgOps.getElliPars(patt2D, (t2rad-FastMath.toRadians(tol2t/2)));
					ArrayList<Point2D.Float>pointsOut =  eOut.getEllipsePoints(azim-angdeg, azim+angdeg, 0.5f);
					ArrayList<Point2D.Float>pointsIn =  eIn.getEllipsePoints(azim-angdeg, azim+angdeg, 0.5f);

					//TODO:revisar hecanviat i < pointsIn.size per j <... estava malament no?
							//ara juntem els punts
							if (pointsIn==null)continue;
					if (pointsOut==null)continue;
					for (int j = 0; j < pointsIn.size(); j++){
						Point2D.Float p1 = getFramePointFromPixel(pointsIn.get(j));
						if (j==(pointsIn.size()-1))break;
						Point2D.Float p2 = getFramePointFromPixel(pointsIn.get(j+1));
						g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
					}

					for (int j = 0; j < pointsOut.size(); j++){
						Point2D.Float p1 = getFramePointFromPixel(pointsOut.get(j));
						if (j==(pointsOut.size()-1))break;
						Point2D.Float p2 = getFramePointFromPixel(pointsOut.get(j+1));
						g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
					}

					//ara juntem els dos arcs
					Point2D.Float p1 = getFramePointFromPixel(pointsIn.get(0));
					Point2D.Float p2 = getFramePointFromPixel(pointsOut.get(0));
					g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
					p1 = getFramePointFromPixel(pointsIn.get(pointsIn.size()-1));
					p2 = getFramePointFromPixel(pointsOut.get(pointsOut.size()-1));
					g1.drawLine(FastMath.round(p1.x), FastMath.round(p1.y), FastMath.round(p2.x), FastMath.round(p2.y));
				}
			}
		}

		//DIBUIX DINCO
		private void drawSolPoints(Graphics2D g1) {
			//mosrem ELS SELCCIONATS
			Object[] oos = dincoFrame.getActiveOrientSols();
			if (oos == null) return;
			for (int i=0;i<oos.length;i++){
				OrientSolucio os = (OrientSolucio)oos[i];
				Iterator<PuntSolucio> itrS = os.getSol().iterator();
				while (itrS.hasNext()) {
					PuntSolucio s = itrS.next();
					Ellipse2D.Float e = null;
					g1.setPaint(s.getColorPunt());
					BasicStroke stroke = new BasicStroke(PuntSolucio.getDincoSolPointStrokeSize());
					g1.setStroke(stroke);
					if (dincoFrame.getSelectedPuntSolucio()!=null){
						if (dincoFrame.getSelectedPuntSolucio().equals(s)){
							g1.setPaint(D2Dplot_global.getComplimentColor(s.getColorPunt()));
							stroke = new BasicStroke(PuntSolucio.getDincoSolPointStrokeSize()+1);
							g1.setStroke(stroke);
						}
					}
					e = ellipseToFrameCoords(s.getEllipseAsDrawingPoint());
					g1.draw(e);

					//g1.fill(e);
					if (estaDincoShowHKL()){
						Font font = new Font("Dialog", Font.PLAIN,hklfontSize+1);
						g1.setFont(font);
						g1.setRenderingHint(
								RenderingHints.KEY_TEXT_ANTIALIASING,
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

			if (getImage() != null) {
				Graphics2D g2 = (Graphics2D) g;

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
					//log.writeNameNumPairs("fine", true, "rect.x, rect.y,", rect.x, rect.y);
				} catch (Exception e) {
					if (D2Dplot_global.isDebug())e.printStackTrace();
					log.debug("error getting the subImage");
				}
				// Rectangle r = g2.getClipBounds();
				AffineTransform t = new AffineTransform();
				float offsetX = originX % scalefit;
				if (originX>0)offsetX = originX;
				float offsetY = originY % scalefit;
				if (originY>0)offsetY = originY;
				t.translate(offsetX, offsetY);
				t.scale(scalefit, scalefit);

				BufferedImage off_Image =
						new BufferedImage(this.getWidth(), this.getHeight(),
								BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g1 = off_Image.createGraphics();

				g1.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON)); // perque es vegin mes suaus...

				g2.drawImage(getSubimage(), t, null); //dibuixem imatge

				// dibuixem els cercles
				g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON)); // perque es vegin mes suaus...

				if (estaDincoShowSpots()) {drawSolPoints(g1);}

				// nomes hauria d'haver-hi la possibilitat d'1 opcio, calibracio o zones excloses?
				if (estaCalibrant()) {drawCalibrationC(g1);}
				if (estaDefinintEXZ()) {drawExZones(g1);}
				if (estaPeakSearch()) {drawPksearch(g1);}

				// nomes hauria d'haver-hi la possibilitat d'1 opcio, drawIndexing o drawHKLindexing
				if (estaSelectPoints()&&!estaDincoAddPeaks()) {drawPuntsEllipses(g1);}

				if (isShowQuickListCompoundRings() ){
					if (quickListCompound != null) drawPDCompoundRings(g1,quickListCompound,colorQLcomp);
				}

				if (isShowDBCompoundRings()){
					if (dbCompound != null)drawPDCompoundRings(g1,dbCompound,colorDBcomp);
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
			return colorCallibEllipses;
		}

		public void setColorCallibEllipses(Color colorCallibEllipses) {
			this.colorCallibEllipses = colorCallibEllipses;
		}

		public Color getColorGuessPointsEllipses() {
			return colorGuessPointsEllipses;
		}

		public void setColorGuessPointsEllipses(Color colorGuessPointsEllipses) {
			this.colorGuessPointsEllipses = colorGuessPointsEllipses;
		}

		public Color getColorFittedEllipses() {
			return colorFittedEllipses;
		}

		public void setColorFittedEllipses(Color colorFittedEllipses) {
			this.colorFittedEllipses = colorFittedEllipses;
		}

		public Color getColorBoundariesEllipses() {
			return colorBoundariesEllipses;
		}

		public void setColorBoundariesEllipses(Color colorBoundariesEllipses) {
			this.colorBoundariesEllipses = colorBoundariesEllipses;
		}

		public Color getColorExcludedZonesLines() {
			return colorExcludedZonesLines;
		}

		public void setColorExcludedZonesLines(Color colorExcludedZonesL) {
			this.colorExcludedZonesLines = colorExcludedZonesL;
		}

		public Color getColorPeakSearch() {
			return colorPeakSearch;
		}

		public void setColorPeakSearch(Color colorPeakSearch) {
			this.colorPeakSearch = colorPeakSearch;
		}

		public Color getColorPeakSearchSelected() {
			return colorPeakSearchSelected;
		}

		public void setColorPeakSearchSelected(Color colorPeakSearchSelected) {
			this.colorPeakSearchSelected = colorPeakSearchSelected;
		}

		public Color getColorQLcomp() {
			return colorQLcomp;
		}

		public void setColorQLcomp(Color colorQLcomp) {
			this.colorQLcomp = colorQLcomp;
		}

		public Color getColorDBcomp() {
			return colorDBcomp;
		}

		public void setColorDBcomp(Color colorDBcomp) {
			this.colorDBcomp = colorDBcomp;
		}

		/**
		 * @return the colorEXZ
		 */
		public Color getColorEXZ() {
			return colorEXZ;
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
			return colorSATUR;
		}

		/**
		 * @param colorSATUR the colorSATUR to set
		 */
		public void setColorSATUR(Color colorSATUR) {
			this.colorSATUR = colorSATUR;
		}
	}

}