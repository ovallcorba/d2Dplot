package vava33.d2dplot;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.auxi.Pattern2D;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.apache.commons.math3.util.FastMath;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;

public class HPtools_frame extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -3398509795601157936L;

    private VavaLogger log = D2Dplot_global.log;

    private JPanel contentPane;
    private JTextField txtCellpar;
    private Pattern2D patt2d;
    private JLabel lblresult;


    /**
     * Create the frame.
     */
    public HPtools_frame(Pattern2D patt) {
        setAlwaysOnTop(true);
        this.patt2d=patt;
        setTitle("HP tools (Cu pressure calc.) -- (IN DEVELOPMENT, USE WITH CAUTION))");
        setIconImage(Toolkit.getDefaultToolkit().getImage(Dinco_frame.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 480, 317);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("", "[][][grow]", "[grow][][][][]"));
        
        JLabel lblInstructins = new JLabel("<html> \nCopper Calibration:<br>\n1) Remove all selected peaks<br>\n2) Select (click) the first two copper reflections:<br>\n&nbsp&nbsp&nbsp 1 1 1 (dspacing ~ 2.08)<br>\n&nbsp&nbsp&nbsp 2 0 0 (dspacing ~ 1.81)<br>\n3) Once selected, click on calculate<br>\n(an alternative is to give directly the cell parameter)<br>\n</html> ");
        contentPane.add(lblInstructins, "cell 0 0 3 1");
        
        JButton btnCalculate = new JButton("Calculate");
        btnCalculate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCalculate_actionPerformed(e);
            }
        });
        contentPane.add(btnCalculate, "cell 0 1,growx");
        
        JLabel lblA = new JLabel("a=");
        contentPane.add(lblA, "cell 1 1,alignx trailing");
        
        txtCellpar = new JTextField();
        contentPane.add(txtCellpar, "cell 2 1,growx");
        txtCellpar.setColumns(10);
        
        JButton btnRemovePeaks = new JButton("Remove Peaks");
        btnRemovePeaks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnRemovePeaks_actionPerformed(arg0);
            }
        });
        contentPane.add(btnRemovePeaks, "cell 0 2,alignx left");
        
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        contentPane.add(panel, "cell 0 3 3 1,grow");
        panel.setLayout(new MigLayout("", "[][grow]", "[]"));
        
        JLabel lblP = new JLabel("P (GPa) =");
        lblP.setForeground(Color.YELLOW);
        panel.add(lblP, "cell 0 0,alignx right");
        lblP.setFont(new Font("Dialog", Font.BOLD, 16));
        
        lblresult = new JLabel("0");
        lblresult.setForeground(Color.YELLOW);
        panel.add(lblresult, "cell 1 0");
        lblresult.setFont(new Font("Dialog", Font.BOLD, 16));
        
        JLabel lblNewLabel = new JLabel("A. Dewaele, P. Loubeyre & M. Mezouar. Phys. Rev. B 70, 094112 (2004)");
        lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        contentPane.add(lblNewLabel, "cell 0 4 3 1");
    }

    protected void do_btnRemovePeaks_actionPerformed(ActionEvent arg0) {
        patt2d.getPuntsCercles().clear();
    }
    
    protected void do_btnCalculate_actionPerformed(ActionEvent e) {
        double a = -1;
        try{
            a = Double.parseDouble(txtCellpar.getText());
        }catch(Exception ex){
            log.info("no cell parameter given, calculating from the peak selection");
        }
        
        if(a>0){
            Object[] options = {"Use peaks","Use entered cell parameter"};
            int n = JOptionPane.showOptionDialog(null,
                    "Cell parameter entered. Use it or use the selection of peaks?",
                    "cell or peaks",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    options, //the titles of buttons
                    options[0]); //default button title
           if (n==JOptionPane.YES_OPTION){
               a=-1;
           }
        }
    
        if (a<0){ //take from the selection
            if (patt2d.getPuntsCercles().size()!=2) {
                log.info("Select the first two Cu peaks");
                return;
            }
            float dsp1 = (float) patt2d.calcDsp(patt2d.getPuntsCercles().get(0).getT2rad());
            float dsp2 = (float) patt2d.calcDsp(patt2d.getPuntsCercles().get(1).getT2rad());
            
            if (dsp1<dsp2){
                float temp = dsp1;
                dsp1 = dsp2;
                dsp2 = temp;
            }
            
            //now dsp1 should be the one at 2.0873 and dsp2 the one at 1.8077
            //1st we check that they are fine...+-0.5A otherwise report
            if ((dsp1<(2.08-0.5))||(dsp1>(2.08+0.5))){
                log.warning("dspacing for 1 1 1 is anomalously long or short");
            }
            if ((dsp2<(1.807-0.5))||(dsp1>(2.807+0.5))){
                log.warning("dspacing for 2 0 0 is anomalously long or short");
            }
            
            //calculate the parameter a (mean of the two clicks)
            double a1 = FastMath.sqrt(3)*dsp1;
            double a2 = FastMath.sqrt(4)*dsp2;
            a = (a1+a2)/2;
            txtCellpar.setText(Double.toString(a));
        }
        
        //calc the "atomic??" volume
        double V = (a*a*a)/4.;
        
        //calc the dpressure
        double V0 = 11.808;
        double K0 = 133;
        double K0p = 5.3;
        double x = FastMath.pow(V/V0, 1.d/3.d);
        
        double p1 = 3*K0*FastMath.pow(x, -2.d);
        double p2 = 1-x;
        double p3 = FastMath.exp((1.5*K0p-1.5)*p2);

        double p = p1*p2*p3;
        
        log.writeNameNums("CONFIG", true, "x,p1,p2,p3,p", x,p1,p2,p3,p);
        
        lblresult.setText(Double.toString(p));
    }
}
