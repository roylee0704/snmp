
/*
 * frmAddDevice.java
 *
 * Created on Feb 11, 2011, 1:49:12 PM
 */

package DriverMaintainance;
import testSNMP.SNMPAgent_V1;
import DriverPack.Driver_Bag;
import MIB_Helper.*;
import OIDPack.OIDBag;
import OIDPack.OIDProperties;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author roylee
 */
public class frmAddDevice extends javax.swing.JDialog implements TableModelListener {
    protected static JDialog m;
    protected MIB_Walker MIBWALKER = new MIB_Walker();
    protected Vector bagPacks;
    protected String[] columnNames = {"Attribute Name", "Syntax", "Access Right", "Description", "Value"};
    protected Object[][] rowData={};
    protected DefaultTableModel tableModel;
    private Object[] inputAddAttr = new Object[5];
    protected int selectedRow;
    protected static final int ADDDEVICE = 1;
    protected static final int ADDDEVICEITEM = 2;
    protected static final int ADDATTRIBUTE = 3;
    protected static final int AttributeName = 0;
    protected static final int Syntax = 1;
    protected static final int AccessRight = 2;
    protected static final int Description = 3;
    protected static final int Value = 4;

    protected static String OIDDeviceType = "";
    protected OIDBag oidBag;
    /** Creates new form frmAddDevice */
    public frmAddDevice() {
        tableModel = new DefaultTableModel(rowData,columnNames);
        tableModel.addTableModelListener(this);
        oidBag = new OIDBag();
        initComponents();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        cboDeviceType = new javax.swing.JComboBox(MIBWALKER.getAllAvailableDeviceName());
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txtAttrName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cboSyntax = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboAccess = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaDescription = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        txtValue = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAttributes = new javax.swing.JTable(tableModel){
            public Class getColumnClass(int column){
                return getValueAt(0, column).getClass();
            }
            //  The Cost is not editable
            public boolean isCellEditable(int row, int column){
                int modelColumn = convertColumnIndexToModel( column );
                return (modelColumn == 0 || modelColumn == 1 ||  modelColumn == 3 || modelColumn == 4  ) ? false : true;
            }
        }
        ;
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnConfirmAddDevice = new javax.swing.JButton();
        btnCancelAddDriver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("frmAddDevice");

        cboDeviceType.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboDeviceType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDeviceTypeActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel1.setText("Select Device Type :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboDeviceType, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(707, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboDeviceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cboDeviceType.addItem("New...");

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Add Attribute", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        txtAttrName.setFont(new java.awt.Font("Segoe UI", 0, 12));
        txtAttrName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAttrNameActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel3.setText("Syntax :");

        cboSyntax.setFont(new java.awt.Font("Segoe UI", 0, 11));
        cboSyntax.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SNMPInteger", "SNMPOctetString" }));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel4.setText("Access Right :");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel2.setText("Attribute Name :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel5.setText("Description :");

        cboAccess.setFont(new java.awt.Font("Segoe UI", 0, 11));
        cboAccess.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Read-Only", "Read-Write" }));

        jtaDescription.setColumns(20);
        jtaDescription.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jtaDescription.setRows(5);
        jScrollPane1.setViewportView(jtaDescription);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel6.setText("Initial Value :");

        txtValue.setFont(new java.awt.Font("Segoe UI", 0, 12));

        btnAdd.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnAdd.setText("Add Attribute");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboAccess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboSyntax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtValue, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(txtAttrName, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtAttrName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboSyntax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboAccess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAdd))
        );

        tblAttributes.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jScrollPane2.setViewportView(tblAttributes);

        btnEdit.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnEdit.setText("Edit Attribute");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnDelete.setText("Delete Attribute");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnEdit)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete)))
                .addContainerGap())
        );

        btnConfirmAddDevice.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnConfirmAddDevice.setText("Comfirm");
        btnConfirmAddDevice.setEnabled(false);
        btnConfirmAddDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmAddDeviceActionPerformed(evt);
            }
        });

        btnCancelAddDriver.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnCancelAddDriver.setText("Cancel");
        btnCancelAddDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelAddDriverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnConfirmAddDevice, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelAddDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmAddDevice)
                    .addComponent(btnCancelAddDriver))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboDeviceTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDeviceTypeActionPerformed
        // TODO add your handling code here:

        if (cboDeviceType.getSelectedItem().equals("New...")) {
            String newDeviceType = JOptionPane.showInputDialog(null, "Please enter new Device Type");
            cboDeviceType.insertItemAt(newDeviceType, cboDeviceType.getSelectedIndex());
            cboDeviceType.setEditable(true);
            cboDeviceType.setSelectedItem(newDeviceType);
            //come bck later. get whole array out and search
        }
}//GEN-LAST:event_cboDeviceTypeActionPerformed

    private void txtAttrNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAttrNameActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txtAttrNameActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        boolean exist = false;
        //update data to table.

        //check is SNMPInteger ==> integer




        inputAddAttr[AttributeName] = txtAttrName.getText();
        inputAddAttr[Syntax] = cboSyntax.getSelectedItem();
        inputAddAttr[AccessRight] = cboAccess.getSelectedItem();
        inputAddAttr[Description] = jtaDescription.getText();
        inputAddAttr[Value] = txtValue.getText();

        if(isEmptyInput(inputAddAttr)){
            JOptionPane.showMessageDialog(null,"Please enter all the fields!","ERROR",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(cboSyntax.getSelectedItem().toString().equals("SNMPInteger"))
            if(!isIntNumber(txtValue.getText())){
                JOptionPane.showMessageDialog(null, "Please check your data type and value!","ERROR",JOptionPane.ERROR_MESSAGE);
                txtValue.setText("");
                txtValue.requestFocus();
                txtValue.setCaretPosition(txtValue.getDocument().getLength());
                return;
            }
        btnConfirmAddDevice.setEnabled(true);

        if(!btnEdit.getText().equals("Cancel")){
            //check duplicated attribute name.
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (tableModel.getValueAt(row, 0).equals(inputAddAttr[0])) {
                    exist = true;
                    JOptionPane.showMessageDialog(null,"Duplicated attribute name found, please make correction!","ERROR",JOptionPane.ERROR_MESSAGE);
                }
            }
            if (!exist) {
                if((tblAttributes.getSelectedRow() >= 0))
                    tableModel.insertRow(tblAttributes.getSelectedRow(), inputAddAttr);
                else
                    tableModel.addRow(inputAddAttr);

                resetInput();
            }
        } else{

            tableModel.setValueAt(inputAddAttr[0], selectedRow, 0);
            tableModel.setValueAt(inputAddAttr[1], selectedRow, 2);
            tableModel.setValueAt(inputAddAttr[2], selectedRow, 3);
            tableModel.setValueAt(inputAddAttr[3], selectedRow, 3);
            tableModel.setValueAt(inputAddAttr[4], selectedRow, 4);
            resetInput();
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:

        if(btnEdit.getText().equals("Edit Attribute")){
            if (tblAttributes.getSelectedRow() >= 0) {
                selectedRow =  tblAttributes.getSelectedRow();
                txtAttrName.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtAttrName.requestFocus();
                cboSyntax.setSelectedItem(tableModel.getValueAt(selectedRow, 1).toString());
                cboAccess.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                jtaDescription.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtValue.setText(tableModel.getValueAt(selectedRow, 4).toString());
                btnEdit.setText("Cancel");
                btnAdd.setText("Save Changes");
                btnDelete.setEnabled(false);
            } else
                JOptionPane.showMessageDialog(null,"Please select an attribute to edit!","ERROR",JOptionPane.ERROR_MESSAGE);
        } else{
            resetInput();//come back;
        }

    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:


        if (tblAttributes.getSelectedColumn() >= 0) {
            tableModel.removeRow(tblAttributes.getSelectedRow());
            tblAttributes.clearSelection();
        } else
            JOptionPane.showMessageDialog(null,"Please select an attribute to remove!","ERROR",JOptionPane.ERROR_MESSAGE);

        if(tableModel.getRowCount() > 0)
             btnConfirmAddDevice.setEnabled(true);
        else
             btnConfirmAddDevice.setEnabled(false);


    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnConfirmAddDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmAddDeviceActionPerformed
        // TODO add your handling code here:
        Driver_Bag drvBag = new Driver_Bag();

        Vector<String> existing = MIBWALKER.getAllAvailableDeviceName();
        //it will add 2 folders, new device folder + device item folder.
        if(!existing.contains(cboDeviceType.getSelectedItem().toString()) && OIDDeviceType.equals("")){
            OIDDeviceType = MIBWALKER.getNewDeviceOID(cboDeviceType.getSelectedItem().toString());
            SNMPAgent_V1.addChildNodeToParent("Devices 1", cboDeviceType.getSelectedItem().toString() + " " + MIBWALKER.extractLast2ID(OIDDeviceType));
            SNMPAgent_V1.addChildNodeToParent(cboDeviceType.getSelectedItem().toString() + " " + MIBWALKER.extractLast2ID(OIDDeviceType), cboDeviceType.getSelectedItem().toString() + "A" + " "+ oidBag.extractAttributeID(OIDDeviceType));
            
        }
        else if(existing.contains(cboDeviceType.getSelectedItem().toString()) && OIDDeviceType.equals("")){
            OIDDeviceType = MIBWALKER.getNewDeviceOID(cboDeviceType.getSelectedItem().toString());
            SNMPAgent_V1.addChildNodeToParent(
                    oidBag.getOIDProperties(oidBag.extractDriverName(OIDDeviceType)).getOIDProperty(OIDProperties.NAME) + " " + MIBWALKER.extractLast2ID(OIDDeviceType) ,
                    oidBag.getOIDProperties(OIDDeviceType).getOIDProperty(OIDProperties.NAME)+ " " + oidBag.extractAttributeID(OIDDeviceType));
        }

        int dID  = oidBag.extractAttributeID(OIDDeviceType);
        int attrCID = 0;
        String attrNID = "";
        String OID = ""; //OID = qualify A.B.C.D
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            attrCID = MIBWALKER.getNumOfChild(Integer.parseInt(oidBag.getOIDProperties(OIDDeviceType).getOIDProperty(OIDProperties.NODEINDEX))) + 1;
            attrNID = Integer.toString(MIBWALKER.getMax() + 1);
            OID = OIDDeviceType + "." + attrCID;

            oidBag.snycAddOID(OID, new OIDProperties(attrNID,  getRealAccessValue(tableModel.getValueAt(row, AccessRight).toString()), getRealSyntaxValue(tableModel.getValueAt(row, Syntax).toString()), tableModel.getValueAt(row, Description).toString(), tableModel.getValueAt(row, AttributeName).toString(), oidBag.getOIDProperties(OIDDeviceType).getOIDProperty(OIDProperties.NODEINDEX)));
            SNMPAgent_V1.addChildNodeToParent(oidBag.getOIDProperties(OIDDeviceType).getOIDProperty(OIDProperties.NAME) + " " + dID, tableModel.getValueAt(row, AttributeName).toString() + " " + attrCID);


            drvBag.addDriverItem(OID, attrNID, tableModel.getValueAt(row, Value).toString());
        }
       
        resetInput();

        m.setVisible(false);
        
         
    }//GEN-LAST:event_btnConfirmAddDeviceActionPerformed

    private void btnCancelAddDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelAddDriverActionPerformed
        // TODO add your handling code here:
        m.setVisible(false);
}//GEN-LAST:event_btnCancelAddDriverActionPerformed

    protected void resetInput(){
        txtAttrName.setText("");
        cboSyntax.setSelectedIndex(0);
        cboAccess.setSelectedIndex(0);
        jtaDescription.setText("");
        txtValue.setText("");
        btnEdit.setText("Edit Attribute");
        btnAdd.setText("Add Attribute");
        btnDelete.setEnabled(true);
        tblAttributes.clearSelection();
    }

    protected boolean isEmptyInput(Object[] inputAddAttr){

        return (inputAddAttr[0].equals("") || inputAddAttr[1].equals("") || inputAddAttr[2].equals("") || inputAddAttr[3].equals("") || inputAddAttr[4].equals(""));
    }

    public static void AddDevice(){
        m = new frmAddDevice();
        m.setVisible(true);
     
        cboDeviceType.setEnabled(true);
       
        OIDDeviceType = "";
    }

    public static void AddDeviceItem(String deviceName){
        m = new frmAddDevice();
        m.setVisible(true);
      
        cboDeviceType.setSelectedItem(deviceName);
        cboDeviceType.setEnabled(false);
 
        OIDDeviceType = "";
    }

    public static void AddAttribute(String deviceName, String OIDFrmDeviceType){
        m = new frmAddDevice();
        m.setVisible(true);
       
        cboDeviceType.setSelectedItem(deviceName);
        cboDeviceType.setEnabled(false);
      
        OIDDeviceType = OIDFrmDeviceType;
    }

    public String getRealSyntaxValue(String value){
       
        if(value.equals("SNMPOctetString"))
            return "3";
        else
            return "1";
    }

    public String getRealAccessValue(String value){

        if(value.equals("Read-Only"))
            return "1";
        else
            return "2";

    }

    public void tableChanged(TableModelEvent e) {
    }
    /**
    * @param args the command line arguments
    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                frmAddDevice dialog = new frmAddDevice(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    public boolean isIntNumber(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancelAddDriver;
    private javax.swing.JButton btnConfirmAddDevice;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JComboBox cboAccess;
    private static javax.swing.JComboBox cboDeviceType;
    private javax.swing.JComboBox cboSyntax;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JTextArea jtaDescription;
    private javax.swing.JTable tblAttributes;
    private javax.swing.JTextField txtAttrName;
    private javax.swing.JTextField txtValue;
    // End of variables declaration//GEN-END:variables

}
