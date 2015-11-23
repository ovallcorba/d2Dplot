package vava33.d2dplot.auxi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ImageTiltRot_diag extends JDialog {

    private static final long serialVersionUID = -4616295467870001148L;
    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public ImageTiltRot_diag() {
        setTitle("Tilt/Rot Convention");
        setModal(true);
        setBounds(100, 100, 548, 510);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        {
            JLabel lblImatge = new JLabel("");
            lblImatge.setIcon(new ImageIcon(ImageTiltRot_diag.class.getResource("/img/tilt_rot_drawing2.png")));
            contentPanel.add(lblImatge);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

    protected void do_okButton_actionPerformed(ActionEvent e) {
        this.dispose();
    }
}
