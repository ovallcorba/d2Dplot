package vava33.plot2d;

import java.awt.BasicStroke;
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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.vava33.jutils.FileUtils;

import vava33.plot2d.auxi.ExZone;
import vava33.plot2d.auxi.OrientSolucio;
import vava33.plot2d.auxi.Pattern2D;
import vava33.plot2d.auxi.PuntCercle;
import vava33.plot2d.auxi.PuntSolucio;

public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    // DEFINICIO BUTONS DEL MOUSE
    private static int MOURE = MouseEvent.BUTTON2;
    private static int CLICAR = MouseEvent.BUTTON1;
    private static int ZOOM_BORRAR = MouseEvent.BUTTON3;
    private static float incZoom = 0.05f;
    private static String theta = "\u03B8";

    private JCheckBox chckbxColor;
    private JCheckBox chckbxInvertY;
    private JLabel lbl2t;
    private JLabel lblContrast;
    private JLabel lblCoord;
    private JLabel lblIntensity;
    private JPanel panel;
    private JSlider slider_contrast;

    private Calib_dialog calibration;
    private Rectangle2D.Float currentRect;
    private ExZone currentPol4;
    private ExZones_dialog exZones;
    boolean fit = true;
    private BufferedImage image;
    private boolean mouseBox = false;
    private boolean mouseDrag = false;
    private boolean mouseZoom = false;
    private int originX, originY;
    private dades2d panelImatge;
    private Pattern2D patt2D;
    private ArrayList<PuntCercle> puntsCercles;
    private float scalefit;
    private boolean showHKLsol = false;
    private boolean showIndexing = false; // indica si s'està en mode de seleccio de punts i si es visualitzen
    private boolean showSolPoints = true;
    private ArrayList<OrientSolucio> solucions; // contindra les solucions
    private BufferedImage subimage;
    private Point2D.Float zoomPoint, dragPoint;
    private float factorContrast;
    private static int contrast_fun=0;

    private static float swinglim=4.5f;
    private static int hklfontSize=14;
    private static float factSliderMax=3.f;
    
    /**
     * Create the panel.
     */
    public ImagePanel() {
        super();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 35, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);
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
        GridBagConstraints gbc_panelImatge = new GridBagConstraints();
        gbc_panelImatge.gridwidth = 4;
        gbc_panelImatge.insets = new Insets(0, 0, 5, 0);
        gbc_panelImatge.fill = GridBagConstraints.BOTH;
        gbc_panelImatge.gridx = 0;
        gbc_panelImatge.gridy = 0;
        add(this.getPanelImatge(), gbc_panelImatge);
        this.panel = new JPanel();
        this.panel.setBorder(null);
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 1, 0);
        gbc_panel.gridwidth = 4;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 1;
        add(this.panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        this.panel.setLayout(gbl_panel);
        this.lblCoord = new JLabel("[0,0]");
        GridBagConstraints gbc_lblCoord = new GridBagConstraints();
        gbc_lblCoord.insets = new Insets(0, 0, 0, 5);
        gbc_lblCoord.gridx = 0;
        gbc_lblCoord.gridy = 0;
        this.panel.add(this.lblCoord, gbc_lblCoord);
        this.lbl2t = new JLabel("(-\u00BA)");
        GridBagConstraints gbc_lbl2t = new GridBagConstraints();
        gbc_lbl2t.insets = new Insets(0, 0, 0, 5);
        gbc_lbl2t.gridx = 1;
        gbc_lbl2t.gridy = 0;
        this.panel.add(this.lbl2t, gbc_lbl2t);
        this.lblIntensity = new JLabel("I= 0");
        GridBagConstraints gbc_lblIntensity = new GridBagConstraints();
        gbc_lblIntensity.gridx = 2;
        gbc_lblIntensity.gridy = 0;
        this.panel.add(this.lblIntensity, gbc_lblIntensity);
        this.lbl2t.setText("");
        this.lblContrast = new JLabel("Contrast");
        this.lblContrast.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
                do_lblContrast_mouseReleased(arg0);
            }
        });
        GridBagConstraints gbc_lblContrast = new GridBagConstraints();
        gbc_lblContrast.anchor = GridBagConstraints.EAST;
        gbc_lblContrast.insets = new Insets(0, 5, 0, 5);
        gbc_lblContrast.gridx = 0;
        gbc_lblContrast.gridy = 2;
        add(this.lblContrast, gbc_lblContrast);
        this.slider_contrast = new JSlider();
        this.slider_contrast.setPaintTicks(true);
        this.slider_contrast.setInverted(false);
        this.slider_contrast.setMinorTickSpacing(1);
        this.slider_contrast.setSnapToTicks(false);
        this.slider_contrast.setMinimumSize(new Dimension(30, 23));
        this.slider_contrast.setMaximumSize(new Dimension(1000, 23));
        this.slider_contrast.setPreferredSize(new Dimension(150, 35));
//        this.slider_contrast.setValue(3);
        this.slider_contrast.setMaximum(0);
        this.slider_contrast.setValue(this.slider_contrast.getMaximum()/2);
        this.slider_contrast.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                do_slider_contrast_stateChanged(arg0);
            }
        });
        GridBagConstraints gbc_slider = new GridBagConstraints();
        gbc_slider.fill = GridBagConstraints.HORIZONTAL;
        gbc_slider.insets = new Insets(0, 0, 0, 5);
        gbc_slider.gridx = 1;
        gbc_slider.gridy = 2;
        add(this.slider_contrast, gbc_slider);
        this.chckbxColor = new JCheckBox("color");
        this.chckbxColor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxColor_itemStateChanged(arg0);
            }
        });
        GridBagConstraints gbc_chckbxColor = new GridBagConstraints();
        gbc_chckbxColor.insets = new Insets(0, 5, 0, 5);
        gbc_chckbxColor.gridx = 2;
        gbc_chckbxColor.gridy = 2;
        add(this.chckbxColor, gbc_chckbxColor);
        this.chckbxInvertY = new JCheckBox("Invert Y");
        this.chckbxInvertY.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                do_chckbxInvertY_itemStateChanged(e);
            }
        });
        GridBagConstraints gbc_chckbxInvertY = new GridBagConstraints();
        gbc_chckbxInvertY.insets = new Insets(0, 5, 0, 0);
        gbc_chckbxInvertY.gridx = 3;
        gbc_chckbxInvertY.gridy = 2;
        add(this.chckbxInvertY, gbc_chckbxInvertY);

        //iniciem
        this.factorContrast=3;
        this.resetView();
    }

    // afegim un punt i cercle a la llista de pics donat el punt en coordeandes
    // del panell (cal convertir-les abans)
    public void addPuntCercle(Point2D.Float p, int inten) {
        if (!patt2D.checkExpParam()) {
            this.getParentFrame().do_btnSetParams_actionPerformed(null);
            if (this.patt2D.getDistMD() <= 0 || this.patt2D.getPixSx() <= 0 || this.patt2D.getPixSy() <= 0)
                return;
        }
        p = this.getPixel(p);
        double t2 = this.calcT2(p);
        puntsCercles.add(new PuntCercle(p, patt2D.getCentrX(), patt2D.getCentrY(), t2, inten));
    }

    protected Rectangle calcSubimatgeDinsFrame() {
        Point2D.Float startCoords = getPixel(new Point2D.Float(0, 0));
        Point2D.Float endCoords = getPixel(new Point2D.Float(getPanelImatge().getWidth() - 1, getPanelImatge()
                .getHeight() - 1));
        int panelX1 = Math.round(startCoords.x);
        int panelY1 = Math.round(startCoords.y);
        int panelX2 = Math.round(endCoords.x);
        int panelY2 = Math.round(endCoords.y);
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

    public double calcT2(Point2D.Float pixel) {
        // calculem l'angle, dist MD esta en mm, el radi tambe ha d'estar en mm (utilitzem mida pixel)
        float psy = this.patt2D.getPixSy();
        float psx = this.patt2D.getPixSx();
        float cX = patt2D.getCentrX();
        float cy = patt2D.getCentrY();
        double x2 = (pixel.getX() * psx - cX * psx) * (pixel.getX() * psx - cX * psx);
        double y2 = (pixel.getY() * psy - cy * psy) * (pixel.getY() * psy - cy * psy);
        double radi = Math.sqrt(x2 + y2);
        double t2 = Math.atan(radi / patt2D.getDistMD());
        return t2;
    }

    public void clearSolutions() {
        solucions.clear();
    }

    protected void do_chckbxColor_itemStateChanged(ItemEvent arg0) {
        this.pintaImatge();
    }

    protected void do_chckbxInvertY_itemStateChanged(ItemEvent e) {
        this.pintaImatge();
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
            // hem d'enviar el punt clicat i l'increment a panel imatge perquè
            // s'encarregui de moure el quadrat o redimensionar-lo
            if (estaCalibrant()) {this.editQuadrat(dragPoint, incX, incY, true);}
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
            lblCoord.setText("[" + FileUtils.dfX_1.format(pix.x) + ";" + FileUtils.dfX_1.format(pix.y) + "]");
            int inten = 0;
            // El math.round porta a 2048 i out of bound, millor no arrodonir i truncar aquí igual que al if anterior
            // inten=(int)(patt2D.getIntenB2(Math.round(pix.x),Math.round(pix.y))*patt2D.getScale());
//            inten = (int) (patt2D.getIntenB2((int) (pix.x), (int) (pix.y)) * patt2D.getScale());
            inten = (int) (patt2D.getIntenB2((int) (pix.x), (int) (pix.y)));
            lblIntensity.setText("I= " + inten);
            if (patt2D.checkExpParam()) {
                lbl2t.setText("(2" + theta + "=" + FileUtils.dfX_3.format(Math.toDegrees(this.calcT2(pix))) + ")");
            }
        }
    }

    // Identificar el botó i segons quin sigui moure o fer zoom
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
            // si estem amb modo calibratge
            if (calibration != null) {
                if (calibration.isCalibrating()) {
                    this.setMouseBox(true);
                }
            }
            // si estem amb modo ex zones
            if (exZones != null) {
                if (exZones.isSetExZones()) {
                    this.setMouseBox(true);
                }
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

        // NOMES afegim els punts i cercles si tenim marcada la indexacio
        if (showIndexing && this.isPatt2D()) {
            // afegim o treiem punts de la llista DICVOL
            if (e.getButton() == CLICAR) {
                int inten;
                Point2D.Float pix = this.getPixel(new Point2D.Float(e.getPoint().x, e.getPoint().y));
//                inten = (int) (patt2D.getIntenB2(Math.round(pix.x), Math.round(pix.y)) * patt2D.getScale());
                inten = (int) (patt2D.getIntenB2(Math.round(pix.x), Math.round(pix.y)));
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
//        System.out.println("max= "+slider_contrast.getMaximum());
//        System.out.println("min= "+slider_contrast.getMinimum());
//        System.out.println("val= "+slider_contrast.getValue());
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
        // System.out.println(ULx+" "+ULy);
        // System.out.println(URx+" "+URy);
        // System.out.println(LLx+" "+LLy);
        // System.out.println(LRx+" "+LRy);

        // PRIMER mirem si està a una arista
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
            // NO cal moure la X del rectangle, només sumar al width
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

        // sino mirem si està dins
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
        float tolVertex = Math.max(5/scalefit,2);
        float tolArista = Math.max(5/scalefit,2);
        
        
        //primer mirem si hem clicat sobre algun vertex per moure'l
        int vertex=-1; //el vertex que cliquem
        for(int i=0;i<currentPol4.npoints;i++){
            Rectangle2D.Float r = new Rectangle2D.Float(currentPol4.getXVertex(i)-tolVertex,currentPol4.getYVertex(i)-tolVertex,tolVertex*2,tolVertex*2);
            if (r.contains(clic)){
                vertex=i;
                break;
            }
        }
        //ara vertex assenyala el vertex que hem clicat o bé -1 si no s'ha clicat a cap
        if(vertex>=0){
            currentPol4.incXVertex(vertex, incX);
            currentPol4.incYVertex(vertex, incY);
            if(repaint)this.repaint();
            return;
        }
        
        //mirem si cliquem a una arista i movem els 2 vertexs que la formen
        //hi ha 4 arestes (0-1,1-2,2-3,3-0), mirarem una a una si el punt clicat s'apropa a una tolerancia
        Line2D.Float li = new Line2D.Float(currentPol4.getXVertex(0), currentPol4.getYVertex(0),
                currentPol4.getXVertex(1), currentPol4.getYVertex(1));
        if(li.ptLineDist(clic)<tolArista){
            //TODO: COMPROVAR QUE ESTIGUI AL SEGMENT i NO FORA (LINIA INFINITA)
            //movem els dos vertexs que formen la linia
            currentPol4.incXVertex(0, incX);
            currentPol4.incYVertex(0, incY);
            currentPol4.incXVertex(1, incX);
            currentPol4.incYVertex(1, incY);
            if(repaint)this.repaint();
            return;
        }
        li = new Line2D.Float(currentPol4.getXVertex(1), currentPol4.getYVertex(1),
                currentPol4.getXVertex(2), currentPol4.getYVertex(2));
        if(li.ptLineDist(clic)<tolArista){
            //TODO: COMPROVAR QUE ESTIGUI AL SEGMENT i NO FORA (LINIA INFINITA)
            //movem els dos vertexs que formen la linia
            currentPol4.incXVertex(1, incX);
            currentPol4.incYVertex(1, incY);
            currentPol4.incXVertex(2, incX);
            currentPol4.incYVertex(2, incY);
            if(repaint)this.repaint();
            return;
        }
        li = new Line2D.Float(currentPol4.getXVertex(2), currentPol4.getYVertex(2),
                currentPol4.getXVertex(3), currentPol4.getYVertex(3));
        if(li.ptLineDist(clic)<tolArista){
            //TODO: COMPROVAR QUE ESTIGUI AL SEGMENT i NO FORA (LINIA INFINITA)
            //movem els dos vertexs que formen la linia
            currentPol4.incXVertex(2, incX);
            currentPol4.incYVertex(2, incY);
            currentPol4.incXVertex(3, incX);
            currentPol4.incYVertex(3, incY);
            if(repaint)this.repaint();
            return;
        }
        li = new Line2D.Float(currentPol4.getXVertex(3), currentPol4.getYVertex(3),
                currentPol4.getXVertex(0), currentPol4.getYVertex(0));
        if(li.ptLineDist(clic)<tolArista){
            //TODO: COMPROVAR QUE ESTIGUI AL SEGMENT i NO FORA (LINIA INFINITA)
            //movem els dos vertexs que formen la linia
            currentPol4.incXVertex(3, incX);
            currentPol4.incYVertex(3, incY);
            currentPol4.incXVertex(0, incX);
            currentPol4.incYVertex(0, incY);
            if(repaint)this.repaint();
            return;
        }
        
        //si no hem clicat a cap vertex mirem si hem clicat a dins i movem tota la zona
        if(currentPol4.contains(clic)){
            currentPol4.translate(Math.round(incX), Math.round(incY));
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
        if (calibration != null) {
            if (calibration.isCalibrating()) {
                return true;
            }
        }
        return false;
    }

    private boolean estaDefinintEXZ() {
        if (exZones != null) {
            if (exZones.isSetExZones()) {
                return true;
            }
        }
        return false;
    }

    // ajusta la imatge al panell, mostrant-la tota sencera (calcula l'scalefit inicial)
    public void fitImage() {
        // l'ajustarem al frame mantenint la relacio, es a dir agafarem l'escala
        // més petita segons la mida del frame i la imatge
        double xScale = (double) getPanelImatge().getWidth() / getImage().getWidth(this);
        double yScale = (double) getPanelImatge().getHeight() / getImage().getHeight(this);
        scalefit = (float) Math.min(xScale, yScale);
        // CENTREM LA IMATGE AL PANELL
        if (scalefit == xScale) {
            // hem de centrar en y
            float gap = (getPanelImatge().getHeight() - (getImage().getHeight()) * scalefit) / 2.f;
            originY = originY + Math.round(gap);
        } else {
            // hem de centrar en x
            float gap = (getPanelImatge().getWidth() - (getImage().getWidth()) * scalefit) / 2.f;
            originX = originX + Math.round(gap);
        }
    }

    public Rectangle2D.Float getCurrentRect() {
        return currentRect;
    }

    // el pixel que entra està al rang 0..n-1 donat un pixel px,py a quin punt x,y del JFrame està
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

    public MainFrame getParentFrame() {
        return (MainFrame) this.getTopLevelAncestor();
    }

    public Pattern2D getPatt2D() {
        return patt2D;
    }

    // segons la mida de la imatge actual, les coordenades d'un punt assenyalat amb el mouse correspondran a un pixel o
    // a un altre, aquesta subrutina ho corregeix: Donat el punt p on el mouse es troba te'l torna com a pixel de la imatge
    public Point2D.Float getPixel(Point2D.Float p) {
        return new Point2D.Float(((p.x - originX) / scalefit), ((p.y - originY) / scalefit));
    }

    public ArrayList<PuntCercle> getPuntsCercles() {
        return puntsCercles;
    }

    public float getScalefit() {
        return scalefit;
    }

    public ArrayList<OrientSolucio> getSolucions() {
        return solucions;
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
        	// el valor s'interpolara sobre una quadràtica y=ax2 (centrat a 0,0)
        	//nomes varia el parametre a
        	float a=(1.f/(sliderVal*sliderVal));
        	ccomponent = a*(intensity*intensity);
        	break;
        case 2:
        	// el valor s'interpolara sobre una quadràtica cap avall y=ax2 + 1 (centrat a 0,1)
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
            // interpolar en una recta de 0 a 1 començant a (val,0). 2 punts: (val,0) (1,1)
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

    public boolean isShowHKLsol() {
        return showHKLsol;
    }

    // es mou l'origen a traves d'un increment de les coordenades
    public void moveOrigin(float incX, float incY, boolean repaint) {
        // assignem un nou origen de la imatge amb un increment a les coordenades anteriors
        //  (util per moure'l fen drag del mouse)
        // hem de vigilar no sortir-nos de la imatge (igual que al fer zoom)
        originX = originX + Math.round(incX);
        originY = originY + Math.round(incY);

        // TODO:els limits han de ser per:
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
        if (!this.chckbxInvertY.isSelected()) {
            // creem imatge normal
            for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
                    if (this.chckbxColor.isSelected()) {
                        // pintem en color
                        col = intensityColor(patt2D.getIntenB2(j, i), patt2D.getMaxI(), patt2D.getMinI(),
                                this.slider_contrast.getValue() / (maxValSlider * factorContrast));
                    } else {
                        // pintem en BW
                        col = intensityBW(patt2D.getIntenB2(j, i), patt2D.getMaxI(), patt2D.getMinI());
                    }
                    im.setRGB(j, i, col.getRGB());
                }
            }
        } else {
            // creem imatge invertida
            int fila = 0;
            for (int i = patt2D.getDimY() - 1; i >= 0; i--) {
                for (int j = 0; j < patt2D.getDimX(); j++) {
                    if (this.chckbxColor.isSelected()) {
                        // pintem en color
                        col = intensityColor(patt2D.getIntenB2(j, i), patt2D.getMaxI(), patt2D.getMinI(),
                                this.slider_contrast.getValue() / (maxValSlider * factorContrast));
                    } else {
                        // pintem en BW
                        col = intensityBW(patt2D.getIntenB2(j, i), patt2D.getMaxI(), patt2D.getMinI());
                    }
                    im.setRGB(j, fila, col.getRGB());
                }
                fila = fila + 1;
            }
        }
        this.updateImage(im);
    }

    public void recalcularCercles() {
        Iterator<PuntCercle> itrPC = puntsCercles.iterator();
        while (itrPC.hasNext()) {
            PuntCercle pc = itrPC.next();
            pc.recalcular(patt2D.getCentrX(), patt2D.getCentrY(), calcT2(new Point2D.Float(pc.getX(), pc.getY())));
        }
    }

    protected Rectangle2D.Float rectangleToFrameCoords(Rectangle2D.Float r) {
        Point2D.Float vertex = getFramePointFromPixel(new Point2D.Float(r.x, r.y));
        float width = (float) (r.getWidth() * scalefit);
        float height = (float) (r.getHeight() * scalefit);
        return new Rectangle2D.Float(vertex.x, vertex.y, width, height);
    }
    
    protected ExZone Pol4ToFrameCoords(ExZone p) {
        Point2D.Float v1 = getFramePointFromPixel(new Point2D.Float(p.getXVertex(0),p.getYVertex(0)));
        Point2D.Float v2 = getFramePointFromPixel(new Point2D.Float(p.getXVertex(1),p.getYVertex(1)));
        Point2D.Float v3 = getFramePointFromPixel(new Point2D.Float(p.getXVertex(2),p.getYVertex(2)));
        Point2D.Float v4 = getFramePointFromPixel(new Point2D.Float(p.getXVertex(3),p.getYVertex(3)));
        return new ExZone(Math.round(v1.x),Math.round(v1.y), Math.round(v2.x),Math.round(v2.y),
                Math.round(v3.x),Math.round(v3.y),Math.round(v4.x),Math.round(v4.y));
    }

    // donat un punt clicat mirarem si hi ha cercle a aquest pixel i en cas que aixi sigui el borrarem
    public void removePuntCercle(Point2D.Float clic) {
        clic = this.getPixel(clic); // el passem a pixels primer
        Iterator<PuntCercle> itrPC = puntsCercles.iterator();
        int i = 0;
        int indexTrobat = -1;
        while (itrPC.hasNext()) {
            Ellipse2D.Float e = itrPC.next().getCercle();
            // determinarem l'equacio del cercle i mirarem si el punt satisfà
            // cercle centre (xc,yc) i radi r : (x-xc)2+(y-yc)2=r2
            double sumx2y2 = (clic.getX() - patt2D.getCentrX()) * (clic.getX() - patt2D.getCentrX())
                    + (clic.getY() - patt2D.getCentrY()) * (clic.getY() - patt2D.getCentrY());
            double r2 = (e.width / 2.f) * (e.width / 2.f);
            double tolerancia = r2 / 30; // tolarancia del radi +- x pixels
            if (sumx2y2 > (r2 - tolerancia) && sumx2y2 < (r2 + tolerancia)) {
                indexTrobat = i;
            }
            i++;
        }
        if (indexTrobat >= 0) {
            puntsCercles.remove(indexTrobat);
        }
    }

    public void resetView() {
        this.originX = 0;
        this.originY = 0;
        scalefit = 0.0f;
        this.repaint();
    }

    public void setCalibration(Calib_dialog calibration) {
        this.calibration = calibration;
    }

    public void setExZones(ExZones_dialog exZones) {
        this.exZones = exZones;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setImagePatt2D(Pattern2D pattern) {
    	this.patt2D = pattern;
    	
    	//slider contrast reinici
    	if(pattern!=null){
//    		this.slider_contrast.setValue(0); //per tal que es reinici al centre
        	this.contrast_slider_properties(this.patt2D.getMinI(),this.calcOptMaxISlider());	
        }
        
        this.pintaImatge();
        getPanelImatge().repaint();
        puntsCercles = new ArrayList<PuntCercle>();
        solucions = new ArrayList<OrientSolucio>();
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

        // ara el pixel ja no està al mateix lloc, mirem a quin punt del frame
        // està (en aquest nou scalefit)
        centre = getFramePointFromPixel(centre);
        // ara tenim el punt del jframe que ha de quedar on tenim el mouse
        // apuntant, per tant hem de moure l'origen de
        // la imatge conforme això (vector nouCentre-mousePosition)
        originX = originX + Math.round(centrePanel.x - centre.x);
        originY = originY + Math.round(centrePanel.y - centre.y);

        this.repaint();
    }

    public void setShowHKLsol(boolean showHKLsol) {
        this.showHKLsol = showHKLsol;
    }

    public void setShowIndexing(boolean show) {
        this.showIndexing = show;
    }

    public void setShowSolPoints(boolean show) {
        this.showSolPoints = show;
    }

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

        // ara el pixel ja no està al mateix lloc, mirem a quin punt del frame
        // està (en aquest nou scalefit)
        centre = getFramePointFromPixel(centre);

        // ara tenim el punt del jframe que ha de quedar on tenim el mouse
        // apuntant, per tant hem de moure l'origen de
        // la imatge conforme això (vector nouCentre-mousePosition)
        originX = originX + Math.round(mousePosition.x - centre.x);
        originY = originY + Math.round(mousePosition.y - centre.y);

        this.repaint();
    }

    protected class dades2d extends JPanel {

        private static final long serialVersionUID = 1L;

        private void drawCalibration(Graphics2D g1) {
            currentRect = calibration.getSelectedRectangle();
            g1.setPaint(Color.CYAN);
            BasicStroke stroke = new BasicStroke(1.5f);
            g1.setStroke(stroke);
            if (currentRect != null) {
                // dibuixem l'emmagatzemat
                // hem de convertir les coordenades de pixels
                // d'imatge a coordenades de pantalla (panell)
                g1.draw(rectangleToFrameCoords(currentRect));
                // dibuixarem també una el·lipse (prova)
                stroke = new BasicStroke(4.0f);
                g1.setStroke(stroke);
                Ellipse2D.Float e = new Ellipse2D.Float(currentRect.x, currentRect.y, currentRect.width,
                        currentRect.height);
                g1.draw(ellipseToFrameCoords(e));
            } else {
                if (calibration.getSelected() >= 0) {
                    // dibuixem un standard per començar
                    currentRect = new Rectangle2D.Float(patt2D.getDimX() / 4, patt2D.getDimY() / 4,
                            patt2D.getDimX() / 2, patt2D.getDimX() / 2);
                    calibration.setSelectedRectangle(currentRect);
                    g1.draw(rectangleToFrameCoords(currentRect));
                    stroke = new BasicStroke(4.0f);
                    g1.setStroke(stroke);
                    Ellipse2D.Float e = new Ellipse2D.Float(currentRect.x, currentRect.y, currentRect.width,
                            currentRect.height);
                    g1.draw(ellipseToFrameCoords(e));
                }
            }
        }

        private void drawExZones(Graphics2D g1) {
            currentPol4 = exZones.getCurrentZone();
            g1.setPaint(Color.CYAN);
            BasicStroke stroke = new BasicStroke(1.0f);
            g1.setStroke(stroke);
            if (currentPol4 != null) {
                g1.draw(Pol4ToFrameCoords(currentPol4));
                exZones.updateSelectedElement();
            }
            // dibuixem el marge
            int marge = patt2D.getMargin();
            if (marge <= 0)
                return;
            Rectangle2D.Float r = new Rectangle2D.Float(marge, marge, patt2D.getDimX() - 2 * marge, patt2D.getDimY()
                    - 2 * marge);
            g1.draw(rectangleToFrameCoords(r));
        }

        private void drawIndexing(Graphics2D g1) {
            Iterator<PuntCercle> itrPC = puntsCercles.iterator();
            while (itrPC.hasNext()) {
                PuntCercle pc = itrPC.next();
                Ellipse2D.Float e = pc.getCercle();
                Ellipse2D.Float p = pc.getPunt();
                g1.setPaint(PuntCercle.getColorCercle());
                BasicStroke stroke = new BasicStroke(1.5f);
                g1.setStroke(stroke);
                // hem de convertir les coordenades de pixels d'imatge de e a coordenades de pantalla (panell)
                g1.draw(ellipseToFrameCoords(e));
                g1.setPaint(PuntCercle.getColorPunt());
                stroke = new BasicStroke(3.0f);
                g1.setStroke(stroke);
                g1.draw(ellipseToFrameCoords(p));
                g1.fill(ellipseToFrameCoords(p));
            }
        }

        private void drawSolPoints(Graphics2D g1) {
            Iterator<OrientSolucio> itrOS = solucions.iterator();
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
                        g1.draw(e);
                        g1.fill(e);
                        if (showHKLsol){
                            Font font = new Font("Dialog", Font.PLAIN,hklfontSize+1);
                            if(s.getOscil()>swinglim){
                                font = new Font("Dialog", Font.ITALIC,hklfontSize-1);
                            }
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
                g2.drawImage(getSubimage(), Math.max(0, originX), Math.max(0, originY),
                        Math.round(getSubimage().getWidth() * scalefit),
                        Math.round(getSubimage().getHeight() * scalefit), null);

                final Graphics2D g1 = (Graphics2D) g2.create();

                // dibuixem els cercles
                g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON)); // perque es vegin mes suaus...

                if (showSolPoints) {drawSolPoints(g1);}

                if (showIndexing) {drawIndexing(g1);}

                // nomes hauria d'haver-hi la possibilitat d'1 opcio, calibracio o zones excloses
                if (estaCalibrant()) {drawCalibration(g1);}

                if (estaDefinintEXZ()) {drawExZones(g1);}

                g1.dispose();
                g2.dispose();
            }
            this.repaint();

        }
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

    //Farem que surti un dialog que pregunti els valors MIN MAX I FACTOR de la barra de contrast
    protected void do_lblContrast_mouseReleased(MouseEvent arg0) {
        Contrast_dialog cd = new Contrast_dialog(this);
        cd.setVisible(true);
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
//		return this.patt2D.calcMeanI((int)(this.patt2D.getMeanI()+factSliderMax*this.patt2D.getSdevI()));
//		return (int)((float)(this.patt2D.getMaxI()-this.patt2D.getMinI())/2.f);
		return (int)((float)(this.patt2D.getMaxI()-this.patt2D.getMinI())/factSliderMax);
	}
	
	//max=-1 for auto calc max intensity
	public void contrast_slider_properties(int min, int max){
		int old_val = slider_contrast.getValue();
		if(old_val==0){old_val=-1;}
//		int old_max = slider_contrast.getMaximum();
//		int old_min = slider_contrast.getMinimum();
		this.slider_contrast.setMaximum(max);
		this.slider_contrast.setMinimum(min);
        this.slider_contrast.setMinorTickSpacing((int)((float)(max-min)/50.f));
        //posicionem al valor que teniem si encara està a l'escala i sino mantenim
        //la posicio en el nou valor (no cal, es posa a l'extrem més proper possible)
        if(old_val>0){
        	this.slider_contrast.setValue(old_val);	
        }else{
        	//posem al mig
        	this.slider_contrast.setValue((slider_contrast.getMaximum()-slider_contrast.getMinimum())/2);
        }
        
//        if(old_val>=this.slider_contrast.getMinimum()&&old_val<=this.slider_contrast.getMaximum()){
//        	this.slider_contrast.setValue(old_val);
//        }else{
//            float newVal=(float)(max-min)*((float)old_val/(float)(old_max-old_min));
//            this.slider_contrast.setValue((int)newVal);
//        }

        
	}
}
