package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.PuntClick;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public class PeakList {

    private final JDialog pointListDialog;
    private JButton btnUpdate;
    private final JPanel contentPanel;
    private JLabel lblCheckValues;
    private JLabel lblPeakList;
    private JList<String> list_pk;
    private final JButton btnRemovePoint;
    private JButton btnRemoveAll;
    private JScrollPane scrollPane;
    private JCheckBox cbox_onTop;
    private JButton btnSaveappend;

    private static final String className = "PKlist";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private final ImagePanel ipanel;

    /**
     * Create the dialog.
     */
    public PeakList(JFrame parent, ImagePanel ip) {
        this.contentPanel = new JPanel();
        this.pointListDialog = new JDialog(parent, "Selected peak List", false);
        this.pointListDialog
                .setIconImage(Toolkit.getDefaultToolkit().getImage(PeakList.class.getResource("/img/Icona.png")));
        this.ipanel = ip;
        this.pointListDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // setBounds(100, 100, 660, 730);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = 660;
        final int height = 730;
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.pointListDialog.setBounds(x, y, width, height);
        this.pointListDialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.pointListDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        final GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
        this.contentPanel.setLayout(gbl_contentPanel);
        {
            this.lblPeakList = new JLabel("Selected Peak list");
            final GridBagConstraints gbc_lblPeakList = new GridBagConstraints();
            gbc_lblPeakList.gridwidth = 2;
            gbc_lblPeakList.fill = GridBagConstraints.HORIZONTAL;
            gbc_lblPeakList.insets = new Insets(0, 5, 5, 5);
            gbc_lblPeakList.gridx = 0;
            gbc_lblPeakList.gridy = 0;
            this.contentPanel.add(this.lblPeakList, gbc_lblPeakList);
        }
        {
            this.cbox_onTop = new JCheckBox("on top");
            this.cbox_onTop.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent arg0) {
                    PeakList.this.do_chckbxOnTop_itemStateChanged(arg0);
                }
            });
            this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
            this.cbox_onTop.setSelected(true);
            final GridBagConstraints gbc_cbox_onTop = new GridBagConstraints();
            gbc_cbox_onTop.insets = new Insets(0, 0, 5, 0);
            gbc_cbox_onTop.gridx = 2;
            gbc_cbox_onTop.gridy = 0;
            this.contentPanel.add(this.cbox_onTop, gbc_cbox_onTop);
        }
        {
            this.scrollPane = new JScrollPane();
            final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
            gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
            gbc_scrollPane.gridwidth = 3;
            gbc_scrollPane.fill = GridBagConstraints.BOTH;
            gbc_scrollPane.gridx = 0;
            gbc_scrollPane.gridy = 1;
            this.contentPanel.add(this.scrollPane, gbc_scrollPane);
            {
                this.list_pk = new JList<String>();
                this.scrollPane.setViewportView(this.list_pk);
            }
        }
        {
            this.btnUpdate = new JButton("Update");
            final GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
            gbc_btnUpdate.anchor = GridBagConstraints.EAST;
            gbc_btnUpdate.insets = new Insets(0, 0, 0, 5);
            gbc_btnUpdate.gridx = 0;
            gbc_btnUpdate.gridy = 2;
            this.contentPanel.add(this.btnUpdate, gbc_btnUpdate);
            this.btnUpdate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    PeakList.this.do_btnUpdate_actionPerformed(arg0);
                }
            });
        }
        this.btnRemovePoint = new JButton("Remove Point");
        final GridBagConstraints gbc_btnRemovePoint = new GridBagConstraints();
        gbc_btnRemovePoint.anchor = GridBagConstraints.EAST;
        gbc_btnRemovePoint.insets = new Insets(0, 0, 0, 5);
        gbc_btnRemovePoint.gridx = 1;
        gbc_btnRemovePoint.gridy = 2;
        this.contentPanel.add(this.btnRemovePoint, gbc_btnRemovePoint);
        this.btnRemovePoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PeakList.this.do_btnRemovePoint_actionPerformed(e);
            }
        });
        {
            this.btnRemoveAll = new JButton("Remove All");
            final GridBagConstraints gbc_btnRemoveAll = new GridBagConstraints();
            gbc_btnRemoveAll.gridx = 2;
            gbc_btnRemoveAll.gridy = 2;
            this.contentPanel.add(this.btnRemoveAll, gbc_btnRemoveAll);
            this.btnRemoveAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    PeakList.this.do_btnRemoveAll_actionPerformed(arg0);
                }
            });
        }
        {
            final JPanel buttonPane = new JPanel();
            this.pointListDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                final GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 25, 0 };
                gbl_buttonPane.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
            final JButton okButton = new JButton("ok");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    PeakList.this.do_okButton_actionPerformed(arg0);
                }
            });
            {
            }
            {
                this.btnSaveappend = new JButton("Save");
                this.btnSaveappend.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        PeakList.this.do_btnSaveappend_actionPerformed(e);
                    }
                });
                final GridBagConstraints gbc_btnSaveappend = new GridBagConstraints();
                gbc_btnSaveappend.anchor = GridBagConstraints.BASELINE;
                gbc_btnSaveappend.insets = new Insets(5, 5, 5, 5);
                gbc_btnSaveappend.gridx = 0;
                gbc_btnSaveappend.gridy = 0;
                buttonPane.add(this.btnSaveappend, gbc_btnSaveappend);
            }

            {
                this.lblCheckValues = new JLabel("");
                this.lblCheckValues.setForeground(Color.RED);
                final GridBagConstraints gbc_lblCheckValues = new GridBagConstraints();
                gbc_lblCheckValues.insets = new Insets(5, 5, 0, 5);
                gbc_lblCheckValues.gridx = 2;
                gbc_lblCheckValues.gridy = 0;
                buttonPane.add(this.lblCheckValues, gbc_lblCheckValues);
            }
            okButton.setActionCommand("OK");
            final GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 3;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            this.pointListDialog.getRootPane().setDefaultButton(okButton);
        }

        this.list_pk.setModel(new DefaultListModel<String>());
        this.loadPeakList();
        this.pointListDialog.pack();
    }

    protected void do_btnUpdate_actionPerformed(ActionEvent arg0) {
        this.loadPeakList();
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.pointListDialog.dispose();
    }

    public void loadPeakList() {

        final DefaultListModel<String> lm = (DefaultListModel<String>) this.list_pk.getModel();
        lm.clear();
        //mostrem la llista puntsCercles
        final Iterator<PuntClick> itrP = this.ipanel.getPatt2D().getPuntsCercles().iterator();
        int i = 1;
        this.lblPeakList.setText(" Num     pX       pY       2T       I");
        while (itrP.hasNext()) {
            final PuntClick pa = itrP.next();
            final String entry = String.format(Locale.ENGLISH, "%4d  %s", i, pa.toString());
            lm.addElement(entry);
            i++;
        }
        this.list_pk.setModel(lm);
        this.ipanel.actualitzarVista();
    }

    protected void tanca() {
        this.pointListDialog.dispose();
    }

    protected void do_btnRemoveAll_actionPerformed(ActionEvent arg0) {
        if (this.list_pk.getModel().getSize() <= 0)
            return;
        this.ipanel.getPatt2D().getPuntsCercles().clear();
        this.loadPeakList();
    }

    protected void do_btnRemovePoint_actionPerformed(ActionEvent e) {
        if (this.list_pk.getSelectedIndex() < 0)
            return;
        //busquem quin element est� seleccionat i el borrem
        //    String[] sel = list_pk.getSelectedValue().split("\\s+");
        final String[] sel = this.list_pk.getModel().getElementAt(this.list_pk.getSelectedIndex()).trim().split("\\s+");
        final float selx = Float.parseFloat(sel[1]);
        final float sely = Float.parseFloat(sel[2]);
        final float tol = 0.1f;
        final Iterator<PuntClick> itrP = this.ipanel.getPatt2D().getPuntsCercles().iterator();
        while (itrP.hasNext()) {
            final PuntClick pa = itrP.next();
            if ((FastMath.abs(pa.getX() - selx) <= tol) && (FastMath.abs(pa.getY() - sely) <= tol)) {
                //es el mateix punt, el borrem
                this.ipanel.getPatt2D().getPuntsCercles().remove(pa);
            }
        }
        this.loadPeakList(); //recarreguem la llista

    }

    protected void do_chckbxOnTop_itemStateChanged(ItemEvent arg0) {
        this.pointListDialog.setAlwaysOnTop(this.cbox_onTop.isSelected());
    }

    protected void do_btnSaveappend_actionPerformed(ActionEvent e) {

        this.btnSaveappend.setText("Save");
        //simplement guardem normal
        final File outfileIndex = FileUtils.fchooserSaveAsk(this.pointListDialog, new File(D2Dplot_global.getWorkdir()),
                null, null);
        try {
            final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfileIndex, false)));
            out.println("# " + this.ipanel.getPatt2D().getImgfile().toString());
            //escribim els elements de la llista
            //primer ordenem la llista !220415
            final ArrayList<PuntClick> ord = new ArrayList<PuntClick>();
            Iterator<PuntClick> itrP = this.ipanel.getPatt2D().getPuntsCercles().iterator();
            while (itrP.hasNext()) {
                ord.add(itrP.next());
            }
            Collections.sort(ord);

            itrP = ord.iterator();
            int i = 1;
            while (itrP.hasNext()) {
                final PuntClick pa = itrP.next();
                final String entry = String.format(Locale.ENGLISH, "%4d  %s", i, pa.toString());
                out.println(entry);
                i++;
            }
            out.close();
        } catch (final Exception ex) {
            if (D2Dplot_global.isDebug())
                ex.printStackTrace();
            log.warning("Error saving peak list file");
        }
        D2Dplot_global.setWorkdir(outfileIndex);
        return;
    }

    public void dispose() {
        this.pointListDialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.pointListDialog.setVisible(vis);
    }

    public void toFront() {
        this.pointListDialog.toFront();
    }

}
