package vava33.d2dplot;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import vava33.d2dplot.auxi.PDCompound;
import vava33.d2dplot.auxi.PDDatabase;
import vava33.d2dplot.auxi.PDReflection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class DB_editor extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtName;
    private JTextField txtNamealt;
    private JTextField txtFormula;
    private JTextField txtCell;
    private JTextField txtSg;
    private JTextField txtRef;
    private JTextField txtComm;
    private JTextArea textAreaDsp;
    
    private boolean editingExisting = false;
    private PDCompound comp;
    private DB_dialog DBdialog;

    /**
     * Create the frame.
     */
    public DB_editor(PDCompound c, DB_dialog parent) {
        this.setComp(c);
        this.setDBdialog(parent);
        
        setBounds(100, 100, 699, 529);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("", "[grow]", "[grow][][]"));
        
        JPanel panel_1 = new JPanel();
        contentPane.add(panel_1, "cell 0 0,grow");
        panel_1.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][grow]"));
        
        JLabel lblName = new JLabel("Name:");
        panel_1.add(lblName, "cell 0 0,alignx trailing");
        
        txtName = new JTextField();
        txtName.setText("name");
        panel_1.add(txtName, "cell 1 0,growx");
        txtName.setColumns(10);
        
        JLabel lblNamealt = new JLabel("Name (alt):");
        panel_1.add(lblNamealt, "cell 0 1,alignx trailing");
        
        txtNamealt = new JTextField();
        txtNamealt.setText("namealt");
        panel_1.add(txtNamealt, "cell 1 1,growx");
        txtNamealt.setColumns(10);
        
        JLabel lblFormula = new JLabel("Formula:");
        panel_1.add(lblFormula, "cell 0 2,alignx trailing");
        
        txtFormula = new JTextField();
        txtFormula.setText("formula");
        panel_1.add(txtFormula, "cell 1 2,growx");
        txtFormula.setColumns(10);
        
        JLabel lblCellParameters = new JLabel("Cell parameters:");
        panel_1.add(lblCellParameters, "cell 0 3,alignx trailing");
        
        txtCell = new JTextField();
        txtCell.setText("cell");
        panel_1.add(txtCell, "cell 1 3,growx");
        txtCell.setColumns(10);
        
        JLabel lblSpaceGroup = new JLabel("Space group:");
        panel_1.add(lblSpaceGroup, "cell 0 4,alignx trailing");
        
        txtSg = new JTextField();
        txtSg.setText("sg");
        panel_1.add(txtSg, "cell 1 4,growx");
        txtSg.setColumns(10);
        
        JLabel lblReference = new JLabel("Reference:");
        panel_1.add(lblReference, "cell 0 5,alignx trailing");
        
        txtRef = new JTextField();
        txtRef.setText("ref");
        panel_1.add(txtRef, "cell 1 5,growx");
        txtRef.setColumns(10);
        
        JLabel lblComment = new JLabel("Comment:");
        panel_1.add(lblComment, "cell 0 6,alignx trailing");
        
        txtComm = new JTextField();
        txtComm.setText("comm");
        panel_1.add(txtComm, "cell 1 6,growx");
        txtComm.setColumns(10);
        
        JLabel lblDspacings = new JLabel("list of (one per line): h k l d-spacing intensity");
        panel_1.add(lblDspacings, "cell 0 7 2 1,alignx left");
        
        JScrollPane scrollPane = new JScrollPane();
        panel_1.add(scrollPane, "cell 0 8 2 1,grow");
        
        textAreaDsp = new JTextArea();
        scrollPane.setViewportView(textAreaDsp);
        textAreaDsp.setRows(3);
        
        JPanel panel_3 = new JPanel();
        contentPane.add(panel_3, "cell 0 1,grow");
        panel_3.setLayout(new MigLayout("", "[][]", "[]"));
        
        JButton btnImportFile = new JButton("import File");
        panel_3.add(btnImportFile, "flowx,cell 0 0");
        
        JButton btnImportHkl = new JButton("import HKL");
        panel_3.add(btnImportHkl, "cell 1 0");
        
        JPanel panel_2 = new JPanel();
        contentPane.add(panel_2, "cell 0 2,grow");
        panel_2.setLayout(new MigLayout("", "[grow][][]", "[]"));
        
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnSave_actionPerformed(arg0);
            }
        });
        
        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRemove_actionPerformed(e);
            }
        });
        panel_2.add(btnRemove, "cell 0 0,alignx right");
        panel_2.add(btnSave, "cell 1 0,alignx right");
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnCancel_actionPerformed(e);
            }
        });
        panel_2.add(btnCancel, "cell 2 0");
        
        if (this.getComp()==null){
            setTitle("Add new compound");
            btnSave.setText("Add to DB");;
            prepareFields();
            btnRemove.setVisible(false);
        }else{
            setTitle("Edit compound");
            btnSave.setText("Save Changes");;
            updateInfo(this.getComp());
            editingExisting = true;
            btnRemove.setVisible(true);
        }
    }
    

    public void updateInfo(PDCompound c){
        txtName.setText(c.getCompName().get(0));
        txtNamealt.setText(c.getAltNames());
        txtFormula.setText(c.getFormula());
        txtCell.setText(c.getCellParameters());
        txtSg.setText(c.getSpaceGroup());
        txtRef.setText(c.getReference());
        txtComm.setText(c.getAllComments());
        textAreaDsp.setText(c.getHKLlines());
    }
    
    public void prepareFields(){
        txtName.setText("");
        txtNamealt.setText("");
        txtFormula.setText("");
        txtCell.setText("");
        txtSg.setText("");
        txtRef.setText("");
        txtComm.setText("");
    }
    
    //SAVE HEM D'APLICAR ELS CANVIS EN CAS QUE SIGUI UN COMPOST EXISTENT O AFEGIR EN CAS QUE SIGUI UN COMPOST NOU
    protected void do_btnSave_actionPerformed(ActionEvent arg0) {
        
        //TODO:first check fields?
        String cell = txtCell.getText().trim();
        String[] cellp = cell.split("\\s+");
        float a,b,c,alfa,beta,gamma;
        try{
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error parsing cell parameters, should be: a b c alpha beta gamma");
            return;
        }
        if (txtName.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please give the compound name");
            return;
        }
        
        String[] hkl_lines = textAreaDsp.getText().trim().split("\\n");
        System.out.println(Arrays.toString(hkl_lines));
        ArrayList<PDReflection> pdref = new ArrayList<PDReflection>();
        //CHECK CONSISTENCY HKL
        for (int i=0;i<hkl_lines.length;i++){
            String[] line = hkl_lines[i].trim().split("\\s+");
            if (line.length<5){
                JOptionPane.showMessageDialog(this, "Error in hkl lines, should be: h k l dspacing Intensity");
                return;
            }else{
                try{
                    System.out.println(Arrays.toString(line));
                    int h = Integer.parseInt(line[0]);
                    int k = Integer.parseInt(line[1]);
                    int l = Integer.parseInt(line[2]);
                    float dsp = Float.parseFloat(line[3]);
                    float inten = Float.parseFloat(line[4]);            
                    PDReflection refl = new PDReflection(h,k,l,dsp,inten);
                    pdref.add(refl);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(this, "Error in parsing hkl lines, e.g: 1 0 0 12.5 100.0");
                    return;
                }
            }
        }
        
        PDCompound co = null;
        
        if (editingExisting){
            Object[] options = {"Yes","No"};
            int n = JOptionPane.showOptionDialog(this,
            "Save the changes to the DB?",
            "Apply changes",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
            if (n == JOptionPane.NO_OPTION) {
                return;
            }
            
            co = this.getComp();
            
            //APPLY:
            
            co.getCompName().clear();
            co.addCompoundName(txtName.getText().trim());
            
        }else{ //new entry
            Object[] options = {"Yes","No"};
            int n = JOptionPane.showOptionDialog(this,
            "Add compound to the DB?",
            "Add compound",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
            if (n == JOptionPane.NO_OPTION) {
                return;
            }
            //ADD COMPOUND:
            co = new PDCompound(txtName.getText().trim());
            PDDatabase.addCompoundDB(co);
        }
        
        co.addCompoundName(txtNamealt.getText().trim());
        co.setFormula(txtFormula.getText().trim());
        co.setA(a);
        co.setB(b);
        co.setC(c);
        co.setAlfa(alfa);
        co.setBeta(beta);
        co.setGamma(gamma);
        co.setSpaceGroup(txtSg.getText().trim());
        co.setReference(txtRef.getText().trim());
        co.getComment().clear();
        co.addComent(txtComm.getText().trim());
        
        //dsp + intensities
        co.getPeaks().clear();
//        for (int i=0;i<hkl_lines.length;i++){
//            String[] line = hkl_lines[i].trim().split("\\s+");
//            System.out.println(Arrays.toString(line));
//            int h = Integer.parseInt(line[0]);
//            int k = Integer.parseInt(line[1]);
//            int l = Integer.parseInt(line[2]);
//            float dsp = Float.parseFloat(line[3]);
//            float inten = Float.parseFloat(line[4]);
//            PDReflection refl = new PDReflection(h,k,l,dsp,inten);
//            co.getPeaks().add(refl);
//        }
        co.setPeaks(pdref);
        
        //TODO:dialog: don't forget to save the DB file to keep changes for future openings.
        JOptionPane.showMessageDialog(this, "Do not forget to save the DB into a file \n(otherwise changes will be lost on close)");
        
        this.closeAndUpdateList();
        
    }
    protected void do_btnRemove_actionPerformed(ActionEvent e) {
        Object[] options = {"Yes",
        "No"};
        int n = JOptionPane.showOptionDialog(this,
        "Remove this compound from the DB?",
        "Remove Compound",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);
        if (n == JOptionPane.YES_OPTION) {
            PDDatabase.getDBCompList().remove(this.getComp());
            this.closeAndUpdateList();
        }
    }

    public void closeAndUpdateList(){
        this.getDBdialog().updateListAllCompounds();
        this.dispose();
    }
    
    public PDCompound getComp() {
        return comp;
    }


    public void setComp(PDCompound comp) {
        this.comp = comp;
    }
    protected void do_btnCancel_actionPerformed(ActionEvent e) {
        this.setComp(null);
        this.dispose();
    }


    public DB_dialog getDBdialog() {
        return DBdialog;
    }


    public void setDBdialog(DB_dialog dBdialog) {
        DBdialog = dBdialog;
    }

}
