package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class Help_dialog extends JDialog {

    private static final long serialVersionUID = 5573831371448966498L;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblTalplogo;
    private JLabel lbloriolVallcorbaJordi;

    /**
     * Create the dialog.
     * @wbp.parser.constructor
     */
    public Help_dialog(String title,String text) {
        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Help_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 570, 630);
        getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton okButton = new JButton("Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            buttonPane.add(okButton);
            getRootPane().setDefaultButton(okButton);
        }

        // posem el logo escalat
        Image img = Toolkit.getDefaultToolkit().getImage(Help_dialog.class.getResource("/img/Icona.png"));
        Image newimg = img.getScaledInstance(-100, 64, java.awt.Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(newimg);
        contentPanel.setLayout(new MigLayout("", "[][grow]", "[grow][]"));
        {
            JLabel lbltext = new JLabel(text);
            contentPanel.add(lbltext, "cell 0 0 2 1,growx,aligny top");
        }
        {
            lblTalplogo = new JLabel("** LOGO **");
            contentPanel.add(lblTalplogo, "cell 0 1,alignx right,aligny center");
        }
        lblTalplogo.setText("");
        lblTalplogo.setIcon(logo);
        {
            lbloriolVallcorbaJordi = new JLabel("<html>\r\n<div style=\"text-align:left\"> \r\n<b>Oriol Vallcorba</b><br>\r\nALBA Synchrotron Light Source - CELLS (www.cells.es)<br>\r\nFor comments/errors/suggestions, please contact to: <strong>ovallcorba@cells.es</strong><br>\r\n</div>\r\n</html>");
            contentPanel.add(lbloriolVallcorbaJordi, "cell 1 1,growx,aligny center");
        }

    }

    public Help_dialog(String title,String text,boolean noSignature) {
        this(title,text);
        if (noSignature){
            lbloriolVallcorbaJordi.setVisible(false);
            lblTalplogo.setVisible(false);
        }
    }
    
    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
}
