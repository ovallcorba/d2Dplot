package vava33.plot2d;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import vava33.plot2d.auxi.PDCompound;
import vava33.plot2d.auxi.PDDatabase;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DB_editor extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNumber;
    private JTextField txtName;
    private JTextField txtNamealt;
    private JTextField txtFormula;
    private JTextField txtCell;
    private JTextField txtSg;
    private JTextField txtRef;
    private JTextField txtComm;
    private JTextArea textAreaDsp;
    private JTextArea textAreaInten;
    
    private boolean editingExisting = false;

    /**
     * Create the frame.
     */
    public DB_editor(PDCompound comp) {
        
        setBounds(100, 100, 699, 529);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("", "[grow]", "[grow][][]"));
        
        JPanel panel_1 = new JPanel();
        contentPane.add(panel_1, "cell 0 0,grow");
        panel_1.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][][][grow][grow]"));
        
        JLabel lblNumber = new JLabel("Number:");
        panel_1.add(lblNumber, "cell 0 0,alignx trailing");
        
        txtNumber = new JTextField();
        txtNumber.setEditable(false);
        txtNumber.setText("number");
        panel_1.add(txtNumber, "cell 1 0,growx");
        txtNumber.setColumns(10);
        
        JLabel lblName = new JLabel("Name:");
        panel_1.add(lblName, "cell 0 1,alignx trailing");
        
        txtName = new JTextField();
        txtName.setText("name");
        panel_1.add(txtName, "cell 1 1,growx");
        txtName.setColumns(10);
        
        JLabel lblNamealt = new JLabel("Name (alt):");
        panel_1.add(lblNamealt, "cell 0 2,alignx trailing");
        
        txtNamealt = new JTextField();
        txtNamealt.setText("namealt");
        panel_1.add(txtNamealt, "cell 1 2,growx");
        txtNamealt.setColumns(10);
        
        JLabel lblFormula = new JLabel("Formula:");
        panel_1.add(lblFormula, "cell 0 3,alignx trailing");
        
        txtFormula = new JTextField();
        txtFormula.setText("formula");
        panel_1.add(txtFormula, "cell 1 3,growx");
        txtFormula.setColumns(10);
        
        JLabel lblCellParameters = new JLabel("Cell parameters:");
        panel_1.add(lblCellParameters, "cell 0 4,alignx trailing");
        
        txtCell = new JTextField();
        txtCell.setText("cell");
        panel_1.add(txtCell, "cell 1 4,growx");
        txtCell.setColumns(10);
        
        JLabel lblSpaceGroup = new JLabel("Space group:");
        panel_1.add(lblSpaceGroup, "cell 0 5,alignx trailing");
        
        txtSg = new JTextField();
        txtSg.setText("sg");
        panel_1.add(txtSg, "cell 1 5,growx");
        txtSg.setColumns(10);
        
        JLabel lblReference = new JLabel("Reference:");
        panel_1.add(lblReference, "cell 0 6,alignx trailing");
        
        txtRef = new JTextField();
        txtRef.setText("ref");
        panel_1.add(txtRef, "cell 1 6,growx");
        txtRef.setColumns(10);
        
        JLabel lblComment = new JLabel("Comment:");
        panel_1.add(lblComment, "cell 0 7,alignx trailing");
        
        txtComm = new JTextField();
        txtComm.setText("comm");
        panel_1.add(txtComm, "cell 1 7,growx");
        txtComm.setColumns(10);
        
        JLabel lblDspacings = new JLabel("d-spacings:");
        panel_1.add(lblDspacings, "cell 0 8,alignx trailing");
        
        JScrollPane scrollPane = new JScrollPane();
        panel_1.add(scrollPane, "cell 1 8,grow");
        
        textAreaDsp = new JTextArea();
        scrollPane.setViewportView(textAreaDsp);
        textAreaDsp.setRows(3);
        
        JLabel lblIntensities = new JLabel("Intensities:");
        panel_1.add(lblIntensities, "cell 0 9,alignx trailing");
        
        JScrollPane scrollPane_1 = new JScrollPane();
        panel_1.add(scrollPane_1, "cell 1 9,grow");
        
        textAreaInten = new JTextArea();
        scrollPane_1.setViewportView(textAreaInten);
        textAreaInten.setRows(3);
        
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
        panel_2.add(btnCancel, "cell 2 0");
        
        if (comp==null){
            setTitle("Add new compound");
            prepareFields();
            btnRemove.setVisible(false);
        }else{
            setTitle("Edit compound");
            updateInfo(comp);
            editingExisting = true;
            btnRemove.setVisible(true);
        }
    }
    

    public void updateInfo(PDCompound c){
        txtNumber.setText(Integer.toString(c.getCnumber()));
        txtName.setText(c.getCompName().get(0));
        txtNamealt.setText(c.getAltNames());
        txtFormula.setText(c.getFormula());
        txtCell.setText(c.getCellParameters());
        txtSg.setText(c.getSpaceGroup());
        txtRef.setText(c.getReference());
        txtComm.setText(c.getAllComments());
        textAreaDsp.setText(c.getDspacingsString());
        textAreaInten.setText(c.getIntensitiesString());
    }
    
    public void prepareFields(){
        //one options = next,but maybe there is a hole inside and does not correspond...
        //txtNumber.setText(Integer.toString(PDDatabase.getCompList().size()+1));
        //second option find the first non used:
        txtNumber.setText(Integer.toString(PDDatabase.getFirstEmptyNum()));
        txtNumber.setText(Integer.toString(PDDatabase.getCompList().size()+1));
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
        
        if (editingExisting){
            
        }else{
            
        }
        
    }
    protected void do_btnRemove_actionPerformed(ActionEvent e) {
        //TODO renumera
        //PDDatabase.getCompList().get(index)
    }
}
