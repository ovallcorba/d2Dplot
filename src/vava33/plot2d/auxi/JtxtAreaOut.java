package vava33.plot2d.auxi;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import java.awt.Font;

public class JtxtAreaOut extends JTextArea implements ActionListener {

    private static final long serialVersionUID = -9202696710189173284L;

    private static String BL = System.getProperty("line.separator");
    private static SimpleDateFormat fHora = new SimpleDateFormat("[HH:mm] ");
    private float fsize;
    private JPopupMenu popup;

    public JtxtAreaOut() {
        super();
        setFont(new Font("Monospaced", Font.BOLD, 14));
        setMargin(new Insets(5, 5, 5, 5));
        setTabSize(4);
        setLineWrap(true);
        setWrapStyleWord(true);
        this.setForeground(Color.YELLOW);
        this.setBackground(Color.BLACK);
        this.setBorder(null);
        this.fsize = this.getFont().getSize();
        this.createPopupMenu();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JMenuItem source = (JMenuItem) (e.getSource());
        if (source.getText().trim().startsWith("Copy"))
            this.copy();
        if (source.getText().trim().startsWith("Increase"))
            this.incFontSize();
        if (source.getText().trim().startsWith("Decrease"))
            this.decFontSize();
        if (source.getText().trim().startsWith("Clear"))
            this.cls();
    }

    // missatge amb timestamp i salt linia
    public void afegirText(boolean saltLinia, boolean timeStamp, String text) {

        if (timeStamp) {
            this.append(fHora.format(new Date()) + text);
        } else {
            this.append(text);
        }
        if (saltLinia) {
            this.append(BL);
        }
        this.setCaretPosition(this.getDocument().getLength());
    }

    // neteja text
    public void cls() {
        this.setText("");
    }

    private void createPopupMenu() {
        JMenuItem menuItem;

        // Create the popup menu.
        popup = new JPopupMenu();
        menuItem = new JMenuItem("Copy");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup = new JPopupMenu();
        menuItem = new JMenuItem("Clear");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();
        menuItem = new JMenuItem("Increase Font Size");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Decrease Font Size");
        menuItem.addActionListener(this);
        popup.add(menuItem);

        // Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        this.addMouseListener(popupListener);
    }

    public void decFontSize() {
        if (fsize > 6) {
            this.fsize -= 1.f;
            this.setFont(this.getFont().deriveFont(fsize));
        }
    }

    public float getFsize() {
        return fsize;
    }

    // MIDA LLETRA:
    public void incFontSize() {
        if (fsize < 22) {
            this.fsize += 1.f;
            this.setFont(this.getFont().deriveFont(fsize));
        }
    }

    // Simple missatge + BreakLine
    public void ln(String t) {
        this.append(t);
        this.append(BL);
        this.setCaretPosition(this.getDocument().getLength());
    }

    public void saltL() {
        this.append(BL);
        this.setCaretPosition(this.getDocument().getLength());
    }

    public void setFsize(float fsize) {
        this.fsize = fsize;
    }

    // Simple missatge + BreakLine
    public void stat(String t) {
        this.append(fHora.format(new Date()) + t);
        this.append(BL);
        this.setCaretPosition(this.getDocument().getLength());
    }

    // TORNA UN STRING AMB LA LINIA ESPCEIFICADA I SI AQUESTA NO EXISTEIX AMB LA
    // ÚLTIMA LINIA i si tampoc
    // EXISTEIX TORNA "0"
    public String txtArea_getLine(int lineNumber) {
        int startIndex;
        try {
            startIndex = this.getLineStartOffset(lineNumber);
        } catch (Exception e) {
            // Això passara si lineNumber no es linia
            try {
                startIndex = this.getLineStartOffset(this.getLineCount() - 1);
            } catch (Exception e2) {
                return "0";
            }
        }
        String line = this.getText().substring(startIndex);
        return line;
    }

    class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
    }
}
