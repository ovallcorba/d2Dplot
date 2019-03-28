package com.vava33.d2dplot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.OrientSolucio;
import com.vava33.d2dplot.auxi.PuntSolucio;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class IncoPlot {

    private final JDialog dincoDialog;
    private final JPanel contentPane;
    private final JFrame parent;
    private final JCheckBox chckbxOnTop;
    private final JList<OrientSolucio> listSol;
    private final JList<PuntSolucio> listEdit;
    private final JCheckBox chckbxAddPeaks;
    private static final String className = "INCO";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private final JButton btnExtractIntensities;
    private final JCheckBox chckbxShowHkl;
    private final JCheckBox chckbxShowSpots;
    private final JSplitPane splitPane;
    private final JPanel panel;
    private final JPanel panel_1;

    //    private Pattern2D patt2D;
    private ImagePanel ip;
    private final JLabel lblFsol;

    /**
     * Create the frame.
     */
    public IncoPlot(JFrame parent, ImagePanel ip) {
        this.ip = ip;
        this.parent = parent;
        this.dincoDialog = new JDialog(parent, "tts_INCO", false);
        this.dincoDialog
                .setIconImage(Toolkit.getDefaultToolkit().getImage(IncoPlot.class.getResource("/img/Icona.png")));
        this.dincoDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.dincoDialog.setBounds(100, 100, 450, 440);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.dincoDialog.setContentPane(this.contentPane);
        this.contentPane.setLayout(new MigLayout("fill, insets 5", "[][][grow]", "[][][grow][]"));

        final JButton btnLoadSol = new JButton("Load PXY/SOL file");
        btnLoadSol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IncoPlot.this.do_btnLoadSol_actionPerformed(e);
            }
        });
        this.contentPane.add(btnLoadSol, "cell 0 0 2 1");

        this.chckbxOnTop = new JCheckBox("on top");
        this.chckbxOnTop.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                IncoPlot.this.do_chckbxOnTop_itemStateChanged(e);
            }
        });
        this.chckbxOnTop.setSelected(true);
        this.contentPane.add(this.chckbxOnTop, "cell 2 0,alignx right");

        this.lblFsol = new JLabel(" ");
        this.contentPane.add(this.lblFsol, "cell 1 1 2 1,alignx leading");

        this.splitPane = new JSplitPane();
        this.splitPane.setResizeWeight(0.5);
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.contentPane.add(this.splitPane, "cell 0 2 3 1,grow");

        this.panel = new JPanel();
        this.splitPane.setRightComponent(this.panel);
        this.panel.setLayout(new MigLayout("fill, insets 5", "[grow]", "[][grow]"));

        final JLabel lblPeakList = new JLabel("Peak List (Nr pX pY h k l Fc Swing):");
        this.panel.add(lblPeakList, "cell 0 0");
        lblPeakList.setToolTipText("");

        final JScrollPane scrollPane_1 = new JScrollPane();
        this.panel.add(scrollPane_1, "cell 0 1,grow");

        this.listEdit = new JList<PuntSolucio>();
        this.listEdit.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                IncoPlot.this.do_listEdit_valueChanged(e);
            }
        });
        this.listEdit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final ListCellRenderer<? super PuntSolucio> renderer = new PointSolutionRenderer();
        this.listEdit.setCellRenderer(renderer);

        scrollPane_1.setViewportView(this.listEdit);

        this.panel_1 = new JPanel();
        this.splitPane.setLeftComponent(this.panel_1);
        this.panel_1.setLayout(new MigLayout("fill, insets 5", "[grow][grow][grow]", "[][grow][]"));

        final JLabel lblSolutionList = new JLabel("Solution list (Nsol Nref_total Nref_matching):");
        this.panel_1.add(lblSolutionList, "cell 0 0 3 1");

        final JScrollPane scrollPane = new JScrollPane();
        this.panel_1.add(scrollPane, "cell 0 1 3 1,grow");

        this.listSol = new JList<OrientSolucio>();
        this.listSol.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                IncoPlot.this.do_listSol_keyReleased(e);
            }
        });
        this.listSol.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                IncoPlot.this.do_listSol_valueChanged(arg0);
            }
        });
        scrollPane.setViewportView(this.listSol);

        this.chckbxShowSpots = new JCheckBox("Show Spots");
        this.chckbxShowSpots.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                IncoPlot.this.do_chckbxShowSpots_itemStateChanged(e);
            }
        });
        this.panel_1.add(this.chckbxShowSpots, "cell 0 2");
        this.chckbxShowSpots.setSelected(true);

        this.chckbxShowHkl = new JCheckBox("Show HKL");
        this.chckbxShowHkl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                IncoPlot.this.do_chckbxShowHkl_itemStateChanged(e);
            }
        });
        this.chckbxShowHkl.setSelected(true);
        this.panel_1.add(this.chckbxShowHkl, "cell 1 2");

        this.chckbxAddPeaks = new JCheckBox("Add Peaks");
        this.panel_1.add(this.chckbxAddPeaks, "cell 2 2");
        this.chckbxAddPeaks.setToolTipText("Add missing peaks not found automatically (e.g. weak spots)");

        this.btnExtractIntensities = new JButton("Extract Intensities");
        this.btnExtractIntensities.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                IncoPlot.this.do_btnExtractIntensities_actionPerformed(arg0);
            }
        });
        this.contentPane.add(this.btnExtractIntensities, "cell 0 3 2 1");

        final JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IncoPlot.this.do_btnClose_actionPerformed(e);
            }
        });
        this.contentPane.add(btnClose, "cell 2 3,alignx right");

        //TEST DESELECT BY CLICK
        final MouseListener[] mls = this.listEdit.getMouseListeners();
        for (final MouseListener ml : mls)
            this.listEdit.removeMouseListener(ml);
        this.listEdit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                final java.awt.Point point = evt.getPoint();
                final int index = IncoPlot.this.listEdit.locationToIndex(point);
                if (IncoPlot.this.listEdit.isSelectedIndex(index))
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            IncoPlot.this.listEdit.removeSelectionInterval(index, index);
                            IncoPlot.this.getIPanel().actualitzarVista();
                        }
                    });
            }
        });
        for (final MouseListener ml : mls)
            this.listEdit.addMouseListener(ml);

        this.dincoDialog.setAlwaysOnTop(this.chckbxOnTop.isSelected());

        this.inicia();
    }

    public void setIpanel(ImagePanel ipanel) {
        this.ip = ipanel;
    }

    public ImagePanel getIPanel() {
        return this.ip;
    }

    public void inicia() {
        //        this.setPatt2D(ip.getPatt2D());
        this.addSolutionsToList();
        this.loadPeakList();
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.getIPanel().actualitzarVista();
        this.dispose();
    }

    protected void do_btnLoadSol_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter[] filter = new FileNameExtensionFilter[2];
        filter[0] = new FileNameExtensionFilter("DINCO SOL/PXY files (*.SOL *.PXY)", "SOL", "sol", "PXY", "pxy");
        filter[1] = new FileNameExtensionFilter("XDS files (SPOT.XDS)", "XDS", "xds");
        File fsol = FileUtils.fchooserOpen(this.dincoDialog, new File(D2Dplot_global.getWorkdir()), filter, 0);
        if (fsol != null) {
            this.getIPanel().getPatt2D().clearSolutions();
            if (FileUtils.getExtension(fsol).equalsIgnoreCase("XDS")) {
                fsol = ImgFileUtils.readXDS(fsol, this.getIPanel().getPatt2D());
            } else {
                fsol = ImgFileUtils.readSOL(fsol, this.getIPanel().getPatt2D());
            }
            if (fsol == null) {
                log.info("No SOL file opened");
                return;
            } else {
                this.addSolutionsToList();
                this.changeFSOLlabel(fsol);
            }
            D2Dplot_global.setWorkdir(fsol);
        }
    }

    public void loadSOLFileDirectly(File fsol) {
        if (fsol != null) {
            this.getIPanel().getPatt2D().clearSolutions();
            fsol = ImgFileUtils.readSOL(fsol, this.getIPanel().getPatt2D());
            this.addSolutionsToList();
            this.changeFSOLlabel(fsol);
            D2Dplot_global.setWorkdir(fsol);
        }

    }

    public void openSOL() {
        final FileNameExtensionFilter[] filter = new FileNameExtensionFilter[2];
        filter[0] = new FileNameExtensionFilter("INCO SOL/PXY files (*.SOL *.PXY)", "SOL", "sol", "PXY", "pxy");
        filter[1] = new FileNameExtensionFilter("INCO/D2DPlot PCS files (*.PCS *.PXY)", "PCS", "pcs");
        File fsol = FileUtils.fchooserOpen(this.dincoDialog, new File(D2Dplot_global.getWorkdir()), filter, 0);
        if (fsol != null) {
            this.getIPanel().getPatt2D().clearSolutions();
            if (FileUtils.getExtension(fsol).equalsIgnoreCase("PCS")) {
                fsol = ImgFileUtils.readPCSasSOL(fsol, this.getIPanel().getPatt2D());
            } else {
                fsol = ImgFileUtils.readSOL(fsol, this.getIPanel().getPatt2D());
            }
            if (fsol == null) {
                log.info("No SOL file opened");
                return;
            } else {
                this.addSolutionsToList();
                this.changeFSOLlabel(fsol);
            }
            D2Dplot_global.setWorkdir(fsol);
        }
    }

    public void openXDS() {
        final FileNameExtensionFilter[] filter = new FileNameExtensionFilter[1];
        filter[0] = new FileNameExtensionFilter("XDS files (SPOT.XDS)", "XDS", "xds");
        File fsol = FileUtils.fchooserOpen(this.dincoDialog, new File(D2Dplot_global.getWorkdir()), filter, 0);
        if (fsol != null) {
            this.getIPanel().getPatt2D().clearSolutions();
            fsol = ImgFileUtils.readXDS(fsol, this.getIPanel().getPatt2D());
            if (fsol == null) {
                log.info("No XDS file opened");
                return;
            } else {
                this.addSolutionsToList();
                this.changeFSOLlabel(fsol);
            }
            D2Dplot_global.setWorkdir(fsol);
        }
    }

    private void changeFSOLlabel(File f) {
        this.lblFsol.setText(f.getName());
    }

    private void addSolutionsToList() {
        // afegim les solucions a la llista
        final DefaultListModel<OrientSolucio> lm = new DefaultListModel<OrientSolucio>();
        // ordenem arraylist ---> EL SOL JA ESTA ORDENAT A LA DARRERA VERSIO DE INCO, NO CAL
        Collections.sort(this.getIPanel().getPatt2D().getSolucions(), Collections.reverseOrder());
        final Iterator<OrientSolucio> iteros = this.getIPanel().getPatt2D().getSolucions().iterator();
        while (iteros.hasNext()) {
            lm.addElement(iteros.next());
        }
        this.listSol.setModel(lm);
        this.listSol.setSelectedIndex(0);
        this.loadPeakList();
    }

    public void setXDSMode() {
        //desactivem components
        this.btnExtractIntensities.setVisible(false);
        this.btnExtractIntensities.setVisible(false);
    }

    public void setSOLMode() {
        //activem components
        this.btnExtractIntensities.setVisible(true);
        this.btnExtractIntensities.setEnabled(true);
    }

    public OrientSolucio getActiveOrientSol() {
        if (this.listSol.getSelectedValue() == null)
            return null;
        return this.listSol.getSelectedValue();
    }

    public OrientSolucio[] getActiveOrientSols() {
        if ((this.listSol.getSelectedValuesList() == null) || (this.listSol.getSelectedValuesList().size() == 0))
            return null;
        final Object[] oosobj = this.listSol.getSelectedValuesList().toArray();
        final OrientSolucio[] oos = new OrientSolucio[oosobj.length];
        for (int i = 0; i < oos.length; i++) {
            oos[i] = (OrientSolucio) oosobj[i];
        }
        return oos;
    }

    public void loadPeakList() {
        if (this.listSol.getSelectedValue() == null)
            return;

        final DefaultListModel<PuntSolucio> lm = new DefaultListModel<PuntSolucio>();

        //mostrarem tots els punts de la solucio seleccionada
        final OrientSolucio os = this.listSol.getSelectedValue();
        final Iterator<PuntSolucio> itrPS = os.getSol().iterator();
        while (itrPS.hasNext()) {
            final PuntSolucio s = itrPS.next();
            //MOSTREM TOTS:
            lm.addElement(s);
        }
        this.listEdit.setModel(lm);
        this.getIPanel().actualitzarVista();
    }

    public boolean isAddPeaks() {
        return this.chckbxAddPeaks.isSelected();
    }

    public boolean isShowSpots() {
        return this.chckbxShowSpots.isSelected();
    }

    public boolean isShowHKL() {
        return this.chckbxShowHkl.isSelected();
    }

    protected void do_listSol_valueChanged(ListSelectionEvent arg0) {
        this.loadPeakList();
    }

    public boolean hasSolutionsLoaded() {
        if (this.listSol.getModel().getSize() > 0) {
            return true;
        } else {
            return false;
        }

    }

    protected void do_listSol_keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            if (this.getActiveOrientSols() != null) {
                final Object[] oos = this.getActiveOrientSols();
                if (oos == null)
                    return;
                for (final Object oo : oos) {
                    final OrientSolucio os = (OrientSolucio) oo;
                    this.getIPanel().getPatt2D().removeSolucio(os);
                }
                this.addSolutionsToList();
            }
        }
    }

    public PuntSolucio getSelectedPuntSolucio() {
        if (this.listEdit.getSelectedIndex() < 0)
            return null;
        final PuntSolucio ps = this.listEdit.getModel().getElementAt(this.listEdit.getSelectedIndex());
        return ps;
    }

    /**
     * Custom renderer to display in red the new added points
     */
    public static class PointSolutionRenderer extends JLabel implements ListCellRenderer<Object> {

        /**
         *
         */
        private static final long serialVersionUID = 6262600607370618116L;
        protected DefaultListCellRenderer defRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            final JLabel renderer = (JLabel) this.defRenderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            if (((PuntSolucio) value).isManuallyAdded()) {
                renderer.setForeground(Color.red);
            }
            return renderer;
        }
    }

    protected void do_chckbxOnTop_itemStateChanged(ItemEvent e) {
        this.dincoDialog.setAlwaysOnTop(this.chckbxOnTop.isSelected());
    }

    protected void do_btnExtractIntensities_actionPerformed(ActionEvent arg0) {
        final PeakSearch pksframe = new PeakSearch(this.parent, this.ip, true, this);
        this.ip.setPKsearch(pksframe);
        pksframe.setVisible(true);
    }

    protected void do_chckbxShowSpots_itemStateChanged(ItemEvent e) {
        this.getIPanel().actualitzarVista();
    }

    protected void do_chckbxShowHkl_itemStateChanged(ItemEvent e) {
        this.getIPanel().actualitzarVista();
    }

    protected void do_listEdit_valueChanged(ListSelectionEvent e) {
        this.getIPanel().actualitzarVista();
    }

    public void setVisible(boolean vis) {
        this.dincoDialog.setVisible(vis);
        if (vis == true)
            this.chckbxShowSpots.setSelected(true);
    }

    public void dispose() {
        this.chckbxShowSpots.setSelected(false);
        this.chckbxShowHkl.setSelected(false);
        this.chckbxAddPeaks.setSelected(false);
        this.getIPanel().actualitzarVista();
        this.dincoDialog.dispose();
    }
}