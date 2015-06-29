
/*
 * SNMP_Message_Diagram.java
 *
 * Created on Feb 19, 2011, 4:36:04 PM
 */
package SNMPMessageStructure;

import javax.swing.JDialog;
import snmp.*;
import testSNMP.WindowUtilities;

/**
 *
 * @author roylee
 */
public class SNMP_Message_Diagram extends javax.swing.JDialog {
    public static JDialog m = new SNMP_Message_Diagram();
    /** Creates new form SNMP_Message_Diagram */
//    public SNMP_Message_Diagram(java.awt.Frame parent, boolean modal) {
//        super(parent, true);
//        WindowUtilities.setNimbusLookAndFeel();
//        initComponents();
//    }
    public SNMP_Message_Diagram() {

        WindowUtilities.setNimbusLookAndFeel();
        initComponents();
    }

    public static void showDialog(String cs, String requestType, String OID, String value){
        processSNMPMessage(cs, requestType, OID, value);
        m.setModal(true);
        m.setVisible(true);
        
        System.out.println(OID);
        
    }


    public int processSNMPVersion() {

        return 3; // 02 01 00.
    }

    public static int processSNMPCommunityString(String cs) {
        SNMPOctetString intCS = new SNMPOctetString(cs);
        String strHex = "04 ";
        SNMPTLV tlv = null;
        byte b[] = intCS.getBEREncoding();
        try {
            tlv = SNMPBERCodec.extractNextTLV(b, 0);
            strHex += completeHex(Integer.toHexString(tlv.value.length)) + " ";
            lblCommunityLength.setText("length = " + tlv.value.length);
            lblCommunityValue.setText("value = " + cs);

            for (int i = 0; i < tlv.value.length; i++) {
                strHex += completeHex(Integer.toHexString(tlv.value[i] & 0xFF)) + " "; //for otect string extraction.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        lblCommunityHex.setText(strHex.substring(0, strHex.length() - 1));
        return tlv.totalLength;
    }

    public static int processPDU_OID(String OID) {
        String strHex = "06 ";
        SNMPTLV tlv = null;
        try {
            SNMPObjectIdentifier in = new SNMPObjectIdentifier(OID);

            byte b[] = in.getBEREncoding();

            tlv = SNMPBERCodec.extractNextTLV(b, 0);
            strHex += completeHex(Integer.toHexString(tlv.value.length)) + " ";
            lblOIDLength.setText("length = " + tlv.value.length);
            lblOIDValue.setText("value = " + OID);

            for (int i = 0; i < tlv.value.length; i++) {
                strHex += completeHex(Integer.toHexString(tlv.value[i] & 0xFF)) + " "; //for otect string extraction.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        lblOIDHex.setText(strHex.substring(0, strHex.length() - 1));
        return tlv.totalLength;
    }

    public static int processPDU_Value(String value) {
        String strHex = "";
        lblValueType.setText("Type = Null");
        lblValueLength.setText("Length = 0");
        lblValueValue.setText("        ");
        lblValueHex.setText("05 00");

        SNMPTLV tlv = null;
        if (value != null) {
            if (isIntNumber(value)) {
                strHex = "02 ";
                lblValueType.setText("Type = Integer");
                SNMPInteger in = new SNMPInteger(value);

                try {
                    byte b[] = in.getBEREncoding();
                    tlv = SNMPBERCodec.extractNextTLV(b, 0);
                    strHex += completeHex(Integer.toHexString(tlv.value.length)) + " ";
                    for (int i = 0; i < tlv.value.length; i++) {
                        strHex += completeHex(Integer.toHexString(tlv.value[i] & 0xFF)) + " "; //for otect string extraction.
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                strHex = "04 ";
                lblValueType.setText("Type = OctetString");
                SNMPOctetString in = new SNMPOctetString(value);

                try {
                    byte b[] = in.getBEREncoding();
                    tlv = SNMPBERCodec.extractNextTLV(b, 0);
                    strHex += completeHex(Integer.toHexString(tlv.value.length)) + " ";
                    for (int i = 0; i < tlv.value.length; i++) {
                        strHex += completeHex(Integer.toHexString(tlv.value[i] & 0xFF)) + " "; //for otect string extraction.
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            lblValueHex.setText(strHex.substring(0, strHex.length() - 1));
            lblValueLength.setText("length = " + tlv.value.length);
            lblValueValue.setText("value = " + value);
            return tlv.totalLength;
        }
        return 2;
    }


    public static int processPDU_Varbind(String OID, String value){
        int totalLength = processPDU_OID(OID) + processPDU_Value(value);
        lblVarbindLength.setText("Type = Sequence, Length = " + totalLength);
        lblVarbindHex.setText("30 " + completeHex(Integer.toHexString(totalLength)));
        return totalLength + 2;//1 byte tag, 1 byte length
    }


    public static int processPDU_VarbindList(String OID, String value){
        int totalLength = processPDU_Varbind(OID, value);
        lblVarbindListLength.setText("Type = Sequence, Length = " + totalLength);
        lblVarbindListHex.setText("30 " + completeHex(Integer.toHexString(totalLength)));
        return totalLength + 2;
    }

    public static int processPDU(String Type, String OID, String value){ //getreq,getnextreq,setreq,
        int totalLength = processPDU_VarbindList(OID, value) + 9;//from index, error, errorindex,
        String hexType;
        lblPDULength.setText("Type = "+ Type +", Length = " + totalLength);

        if(Type.equals("GetRequest"))
            hexType = "A0 ";
        else if(Type.equals("GetNextRequest"))
            hexType = "A1 ";
        else
            hexType = "A3 ";

        lblPDUHex.setText(hexType + completeHex(Integer.toHexString(totalLength)));
        return totalLength + 2;
    }

    public static void processSNMPMessage(String cs, String Type, String OID, String value){
        int totalLength = 3 + processSNMPCommunityString(cs) + processPDU(Type, OID, value);
        lblSNMPMessageLength.setText("Type = Sequence, Length = " + totalLength);
        lblSNMPMessageHex.setText("30 " + completeHex(Integer.toHexString(totalLength)));
       
    }



    public static String completeHex(String hex) {
        if (hex.length() == 1) {
            return "0" + hex.toUpperCase();
        }
        return hex.toUpperCase();
    }

    public static boolean isIntNumber(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblCommunityHex = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        lblCommunityType = new javax.swing.JLabel();
        lblCommunityLength = new javax.swing.JLabel();
        lblCommunityValue = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        lblPDUHex = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        lblPDULength = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblVarbindHex = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        lblVarbindLength = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        lblOIDHex = new javax.swing.JLabel();
        lblOIDLength = new javax.swing.JLabel();
        lblOIDValue = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lblValueType = new javax.swing.JLabel();
        lblValueLength = new javax.swing.JLabel();
        lblValueValue = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        lblValueHex = new javax.swing.JLabel();
        lblVarbindListHex = new javax.swing.JLabel();
        lblVarbindListLength = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        lblSNMPMessageHex = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        lblSNMPMessageLength = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel8.setPreferredSize(new java.awt.Dimension(96, 458));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel17.setText("Type = Integer");

        jLabel18.setFont(new java.awt.Font("Courier New", 0, 12));
        jLabel18.setText("02 01 00");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel19.setText("Length = 1");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel20.setText("Value = 0");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel21.setText("SNMP Version");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18)))
                    .addComponent(jLabel17)
                    .addComponent(jLabel21))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jLabel18))
        );

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblCommunityHex.setFont(new java.awt.Font("Courier New", 0, 12));
        lblCommunityHex.setText("04 07 70 72 69 76 61 74 65");

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel44.setText("Community String");

        lblCommunityType.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblCommunityType.setText("Type = Octet String");

        lblCommunityLength.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblCommunityLength.setText("Length = 5");

        lblCommunityValue.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblCommunityValue.setText("Value = public");

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel48.setText("SNMP");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblCommunityHex))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(jLabel48))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCommunityType)
                            .addComponent(jLabel44)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(lblCommunityLength))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(lblCommunityValue)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCommunityType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCommunityLength)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCommunityValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(lblCommunityHex))
        );

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel5.setPreferredSize(new java.awt.Dimension(96, 146));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel1.setText("Type = Integer");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel8.setText("Length = 1");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel9.setText("Value = 1");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel14.setText("Request ID");

        jLabel36.setFont(new java.awt.Font("Courier New", 0, 12));
        jLabel36.setText("02 01 01");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel36))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addComponent(jLabel36))
        );

        lblPDUHex.setFont(new java.awt.Font("Courier New", 0, 12));
        lblPDUHex.setText("A0 1E");

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel6.setPreferredSize(new java.awt.Dimension(96, 146));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel4.setText("Type = Integer");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel10.setText("Length = 1");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel11.setText("Value = 1");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel15.setText("Error");

        jLabel38.setFont(new java.awt.Font("Courier New", 0, 12));
        jLabel38.setText("02 01 01");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel38)
                                    .addComponent(jLabel11)))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel15)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addComponent(jLabel38))
        );

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel7.setPreferredSize(new java.awt.Dimension(96, 146));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel6.setText("Type = Integer");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel12.setText("Length = 1");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel13.setText("Value = 1");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel16.setText("Error Index");

        jLabel39.setFont(new java.awt.Font("Courier New", 0, 12));
        jLabel39.setText("02 01 01");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel39)
                                    .addComponent(jLabel13)))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel16)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addComponent(jLabel39))
        );

        lblPDULength.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblPDULength.setText("Type = GetRequest, Length = 30");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel25.setText("SNMP PDU");

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblVarbindHex.setFont(new java.awt.Font("Courier New", 0, 12));
        lblVarbindHex.setText("30 11");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel29.setText("Varbind");

        lblVarbindLength.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblVarbindLength.setText("Type = Sequence, Length = 17");

        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 11));
        jLabel31.setText("Type = Object Identifier");

        lblOIDHex.setFont(new java.awt.Font("Courier New", 0, 12));
        lblOIDHex.setText("06 0D 2B 06 01 04 01 94 78 01 02 07 03 02 00");

        lblOIDLength.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblOIDLength.setText("Length = 13");

        lblOIDValue.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblOIDValue.setText("Value = 1.3.6.1.4.1.2680.1.2.7.3.2.0");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel35.setText("Object Identifier");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(jLabel35))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(lblOIDValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(136, 136, 136)
                        .addComponent(lblOIDLength, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblOIDHex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOIDLength)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOIDValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblOIDHex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel10.setPreferredSize(new java.awt.Dimension(96, 146));

        lblValueType.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblValueType.setText("Type = Null");

        lblValueLength.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblValueLength.setText("Length = 0");

        lblValueValue.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblValueValue.setText("  ");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel42.setText("Value");

        lblValueHex.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        lblValueHex.setText("05 00");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel42))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblValueLength)
                            .addComponent(lblValueType)
                            .addComponent(lblValueValue)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblValueHex)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblValueType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblValueLength)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblValueValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblValueHex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblVarbindHex)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblVarbindLength, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVarbindLength)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblVarbindHex, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        lblVarbindListHex.setFont(new java.awt.Font("Courier New", 0, 12));
        lblVarbindListHex.setText("30 13");

        lblVarbindListLength.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblVarbindListLength.setText("Type = Sequence, Length = 17");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel28.setText("Varbind List");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblVarbindListHex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVarbindListLength, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(293, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVarbindListLength)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblVarbindListHex)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPDUHex, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(186, 186, 186)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPDULength, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                .addGap(550, 550, 550))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(lblPDULength))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblPDUHex, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)))
        );

        lblSNMPMessageHex.setFont(new java.awt.Font("Courier New", 0, 12));
        lblSNMPMessageHex.setText("A0 1E");

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 11));
        jLabel49.setText("SNMP Message");

        lblSNMPMessageLength.setFont(new java.awt.Font("Segoe UI", 0, 11));
        lblSNMPMessageLength.setText("Type = Sequence, Length = 44");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblSNMPMessageHex, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(484, 484, 484)
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSNMPMessageLength, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                        .addGap(620, 620, 620)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(lblSNMPMessageLength))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblSNMPMessageHex, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                SNMP_Message_Diagram dialog = new SNMP_Message_Diagram(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private static javax.swing.JLabel lblCommunityHex;
    private static javax.swing.JLabel lblCommunityLength;
    private javax.swing.JLabel lblCommunityType;
    private static javax.swing.JLabel lblCommunityValue;
    private static javax.swing.JLabel lblOIDHex;
    private static javax.swing.JLabel lblOIDLength;
    private static javax.swing.JLabel lblOIDValue;
    private static javax.swing.JLabel lblPDUHex;
    private static javax.swing.JLabel lblPDULength;
    private static javax.swing.JLabel lblSNMPMessageHex;
    private static javax.swing.JLabel lblSNMPMessageLength;
    private static javax.swing.JLabel lblValueHex;
    private static javax.swing.JLabel lblValueLength;
    private static javax.swing.JLabel lblValueType;
    private static javax.swing.JLabel lblValueValue;
    private static javax.swing.JLabel lblVarbindHex;
    private static javax.swing.JLabel lblVarbindLength;
    private static javax.swing.JLabel lblVarbindListHex;
    private static javax.swing.JLabel lblVarbindListLength;
    // End of variables declaration//GEN-END:variables
}
