package vava33.d2dplot.auxi;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.vava33.jutils.FileUtils;

public class findPksTableRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 8297343427722845804L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column );

            // First format the cell value as required
            
            if (value instanceof Float){
                
//                if (((Float)value*10.)<1){
                if (((Float)value)<10.0){
                    value = FileUtils.dfX_2.format((Number)value);
                }else{
                    value = FileUtils.dfX_1.format((Number)value);
                }
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            super.setValue(value);
            return this;
         }
    
}
