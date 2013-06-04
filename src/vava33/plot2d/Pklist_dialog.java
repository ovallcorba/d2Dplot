package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import vava33.plot2d.auxi.PuntCercle;

public class Pklist_dialog extends JDialog {

    private static final long serialVersionUID = -5876034353317165127L;
    private JButton btnUpdate;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblCheckValues;
    private JScrollPane scrollPane;
    private JTextArea txt_pklist;
    private ImagePanel panelImatge;


    /**
     * Create the dialog.
     */
    public Pklist_dialog(ImagePanel ip) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Pklist_dialog.class.getResource("/img/Icona.png")));
        panelImatge = ip;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Selected peak List");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 400, 500);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            this.scrollPane = new JScrollPane();
            GridBagConstraints gbc_scrollPane = new GridBagConstraints();
            gbc_scrollPane.fill = GridBagConstraints.BOTH;
            gbc_scrollPane.gridx = 0;
            gbc_scrollPane.gridy = 0;
            contentPanel.add(this.scrollPane, gbc_scrollPane);
            {
                txt_pklist = new JTextArea();
                this.scrollPane.setViewportView(this.txt_pklist);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 25, 0 };
                gbl_buttonPane.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
            JButton okButton = new JButton("ok");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            {
                lblCheckValues = new JLabel("");
                lblCheckValues.setForeground(Color.RED);
                GridBagConstraints gbc_lblCheckValues = new GridBagConstraints();
                gbc_lblCheckValues.insets = new Insets(5, 5, 0, 5);
                gbc_lblCheckValues.gridx = 0;
                gbc_lblCheckValues.gridy = 0;
                buttonPane.add(lblCheckValues, gbc_lblCheckValues);
            }
            {
                this.btnUpdate = new JButton("update");
                this.btnUpdate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnUpdate_actionPerformed(arg0);
                    }
                });
                GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
                gbc_btnUpdate.gridx = 1;
                gbc_btnUpdate.gridy = 0;
                buttonPane.add(this.btnUpdate, gbc_btnUpdate);
            }
            okButton.setActionCommand("OK");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 2;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
        }

        this.loadPeakList();
    }

    public void addLS() {
        this.txt_pklist.append(System.getProperty("line.separator"));
        txt_pklist.setCaretPosition(txt_pklist.getDocument().getLength());
    }

    protected void clearList() {
        txt_pklist.setText("");
    }

    protected void do_btnUpdate_actionPerformed(ActionEvent arg0) {
        this.loadPeakList();
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

    public void loadPeakList() {
        this.clearList();
        Iterator<PuntCercle> itrP = panelImatge.getPuntsCercles().iterator();
        int i = 1;
        txt_pklist.append(" Num     pX       pY       2T       I");
        this.addLS();
        while (itrP.hasNext()) {
            PuntCercle pa = itrP.next();
            String num = String.format(Locale.ENGLISH, "%4d", i);
            txt_pklist.append(num + "  " + pa.toString());
            this.addLS();
            i++;
        }
    }

    protected void tanca() {
        this.dispose();
    }
}
