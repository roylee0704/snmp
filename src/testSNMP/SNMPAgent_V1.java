// note: 1.3.6.1.4.1.999999.1.1.2.1
/*       1.3.6.1.4.1.999999.1.1.1
 * TreeTraverseApp.java
 *
 * Created on Apr 27, 2010, 3:16:36 PM
 */
package testSNMP;

import ChatPack.frmInitChating;
import DBCentre.DB_HOST;
import DriverMaintainance.frmAddDevice;
import DriverMaintainance.frmConnectToMIB;
import DriverPack.Driver_Bag;

import MIB_Helper.MIB_Walker;
import OIDPack.OIDBag;
import OIDPack.OIDProperties;

import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.swing.JMenuItem;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import snmp.*;
import javax.swing.tree.TreePath;
import snmp.SNMPGetException;
import snmp.SNMPPDU;
import snmp.SNMPRequestListener;
import snmp.SNMPSequence;
import snmp.SNMPSetException;

/**
 *
 * @author roylee
 */
public class SNMPAgent_V1 extends javax.swing.JFrame implements SNMPRequestListener, Runnable{
    // MY OTHER INIT - r/w access
    private static final int READ_ONLY = 1;
    private static final int READ_WRITE = 2;
    private static final int NO_ACCESS = 3;
    //
    //type- access
    public static final byte TYPE_INTEGER = 1;//1 in db
    public static final byte TYPE_BITSTRING = 2;//and so on.,
    public static final byte TYPE_OCTETSTRING = 3;
    public static final byte TYPE_NULL = 4;

    public static final int OIDBAG = 0;
    public static final int DRIVERBAG = 1;

    public static final int NODEINDEX = 0;
    public static final int ACCESS = 1;
    public static final int TYPE = 2;
    public static final int DESCRIPTION = 3;
    public static final int NAME = 4;
    public static final int PNODEINDEX = 5;

    //** INIT MIB WALKER

    //**
    //** INIT DRIVER BAG
    Vector bagPack;
    //**

    JMenuItem jmiAddDevice = new JMenuItem("Add New Device...");
    JMenuItem jmiAddDeviceItem = new JMenuItem("Add New xxxx...");
    JMenuItem jmiAddAttribute = new JMenuItem("Add New Attribute...");
    JMenuItem jmiRemoveDeviceFolder = new JMenuItem("Remove xxxx Folder...");
    JMenuItem jmiRemoveDeviceItem = new JMenuItem("Remove xxxx...");
    static DefaultMutableTreeNode parent;
    static MIB_Walker mibWalker;
    static JTree jTree1;
    OIDBag oidBag = null;
    Driver_Bag drvBag = null;
    public String communityName = "public";


    SNMPv1AgentInterface agentInterface;
    SNMPOctetString storedSNMPValue;
    PipedReader errorReader;
    PipedWriter errorWriter;
    Thread readerThread;
  //  boolean haveReportFile = false;
    FileWriter reportFileWriter;

    //chat init//
    private JMenuItem jmiChat;


    /** Creates new form TreeTraverseApp */
    public SNMPAgent_V1() {
        WindowUtilities.setNimbusLookAndFeel();
        jmiChat = new JMenuItem("Chat");
        initComponents();
        royChatInit();


        addWindowListener(new WindowCloseAdapter());

        SNMPAgentInit();
        royInit();



    }
    private void royChatInit(){
        jmiChat.setIcon(frmInitChating.imageResize(new ImageIcon(getClass().getResource("/images/Chat.png")), 16 , 16));
        jmiAddDevice.setIcon(frmInitChating.imageResize(new ImageIcon(getClass().getResource("/images/DBAdd.png")), 16 , 16));
        jmiAddDeviceItem.setIcon(frmInitChating.imageResize(new ImageIcon(getClass().getResource("/images/1291898450_add_sheet.png")), 16 , 16));
        jmiAddAttribute.setIcon(frmInitChating.imageResize(new ImageIcon(getClass().getResource("/images/Add2.png")), 16 , 16));
        jmiRemoveDeviceFolder.setIcon(frmInitChating.imageResize(new ImageIcon(getClass().getResource("/images/DBRemove.png")), 16 , 16));
        jmiRemoveDeviceItem.setIcon(frmInitChating.imageResize(new ImageIcon(getClass().getResource("/images/Delete.png")), 16 , 16));
        jmFile.add(jmiChat);
        myMenuActionListener(); //action for jmiChat
    }

    private void SNMPAgentInit(){
        try
        {
            errorReader = new PipedReader();
            errorWriter = new PipedWriter(errorReader);
            readerThread = new Thread(this);
            readerThread.start();
            int version = 0;    // SNMPv1

            agentInterface = new SNMPv1AgentInterface(version, new PrintWriter(errorWriter));
            agentInterface.addRequestListener(this);
            agentInterface.setReceiveBufferSize(5120);
            agentInterface.startReceiving();

        }
        catch(Exception e)
        {
            messagesArea.append("Problem starting Agent Test: " + e.toString() + "\n");
        }
    }

    private void royInit(){

        frmConnectToMIB.showDialog();
        if (DB_HOST.getConnection() != null) {
            mibWalker = new MIB_Walker();
            bagPack = mibWalker.Load();
            oidBag = new OIDBag();
            drvBag = new Driver_Bag();
        }
        //insert since from root~!
        mibDisplay.setViewportView(jTree1 = new JTree(mibWalker.initTreeDisplay(1)));

        jpopA.add(jmiAddDevice);
        jpopB.add(jmiAddDeviceItem);
        jpopB.add(jmiRemoveDeviceFolder);
        jpopC.add(jmiAddAttribute);
        jpopC.add(jmiRemoveDeviceItem);

        jTree1.addMouseListener(clickable);

        jmiAddDevice.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                 frmAddDevice.AddDevice();
            }
        });

        jmiAddDeviceItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                 frmAddDevice.AddDeviceItem(oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(OIDProperties.NAME));
            }
        });

        jmiAddAttribute.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                 frmAddDevice.AddAttribute(oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(OIDProperties.NAME), getTreePathOID());
            }
        });
        jmiRemoveDeviceFolder.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                removingDevices(getTreePathOID());
            }
        });
        jmiRemoveDeviceItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
              removingDeviceItem(getTreePathOID());
            }
        });
    }
    private void myMenuActionListener() {
        jmiChat.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Runtime run = Runtime.getRuntime();
                try {
                    run.exec("cmd start /c C:/initChatClient.bat");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpopA = new javax.swing.JPopupMenu();
        jpopB = new javax.swing.JPopupMenu();
        jpopC = new javax.swing.JPopupMenu();
        jPanel2 = new javax.swing.JPanel();
        txtOID = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtType = new javax.swing.JTextField();
        txtAccess = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaDescription = new javax.swing.JTextArea();
        txtValue = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        mibDisplay = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        messagesArea = new javax.swing.JTextArea();
        btnClear = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jmFile = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SNMPAgent");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "View OID", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12))); // NOI18N

        txtOID.setFont(new java.awt.Font("Courier New", 0, 12));
        txtOID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOIDActionPerformed(evt);
            }
        });
        txtOID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOIDKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel1.setText("OID : ");

        txtFullName.setEditable(false);
        txtFullName.setFont(new java.awt.Font("Courier New", 0, 12));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOID, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE))
                    .addComponent(txtFullName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtOID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OID Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel2.setText("NAME:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel3.setText("ACCESS:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel4.setText("DESCRIPTION:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel5.setText("TYPE:");

        txtName.setFont(new java.awt.Font("Courier New", 0, 12));
        txtName.setForeground(new java.awt.Color(0, 51, 204));
        txtName.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        txtType.setFont(new java.awt.Font("Courier New", 0, 12));
        txtType.setForeground(new java.awt.Color(0, 51, 204));
        txtType.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        txtAccess.setFont(new java.awt.Font("Courier New", 0, 12));
        txtAccess.setForeground(new java.awt.Color(0, 51, 204));
        txtAccess.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jtaDescription.setColumns(20);
        jtaDescription.setFont(new java.awt.Font("Courier New", 0, 12));
        jtaDescription.setForeground(new java.awt.Color(0, 51, 204));
        jtaDescription.setRows(5);
        jtaDescription.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jScrollPane1.setViewportView(jtaDescription);

        txtValue.setFont(new java.awt.Font("Courier New", 0, 12));
        txtValue.setForeground(new java.awt.Color(0, 51, 204));
        txtValue.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 12));
        jLabel10.setText("VALUE:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAccess)
                            .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .addGap(71, 71, 71)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(10, 10, 10)
                                .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel4))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtAccess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mibDisplay.setFont(new java.awt.Font("Segoe UI", 0, 13));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Received Requests", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12))); // NOI18N

        messagesArea.setColumns(20);
        messagesArea.setEditable(false);
        messagesArea.setFont(new java.awt.Font("Courier New", 0, 12));
        messagesArea.setRows(5);
        jScrollPane2.setViewportView(messagesArea);

        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(btnClear, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addContainerGap())
        );

        jmFile.setText("File");
        jMenuBar1.add(jmFile);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mibDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mibDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtOIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOIDKeyReleased
        // TODO add your handling code here:
        if (oidBag.getOIDProperties(txtOID.getText()) != null) { //if oid typed existed in oid bag.
            ((DefaultTreeModel) (jTree1.getModel())).reload();
            setTreePath(txtOID.getText(), oidBag.getOIDProperties(txtOID.getText()).getOIDProperty(OIDProperties.NAME));
            txtFullName.setText(getTreePathOIDName());
        }
    }//GEN-LAST:event_txtOIDKeyReleased

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        messagesArea.setText("");
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtOIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOIDActionPerformed


    @Override
    protected void processWindowEvent(WindowEvent e) {

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {

            int exit = JOptionPane.showConfirmDialog(this, "Are you sure to exit?", "Confirmation Dialog", JOptionPane.YES_NO_OPTION);
            if (exit == JOptionPane.YES_OPTION) {
                drvBag.SaveAllDriversToDB();
                System.exit(0);
            }

        } else {
            super.processWindowEvent(e);
        }
    }


    private static TreePath find(DefaultMutableTreeNode root, String target) {
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
   
            while (e.hasMoreElements()) {
                Object o = e.nextElement();
                if (o.toString().equals(target))
                    return (new TreePath(((DefaultMutableTreeNode) o).getPath()));//get nodearray, den convert to tree path
            }
        
        return null;
    }

    MouseListener clickable = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            try {
                int selRow = jTree1.getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 1)
                        displayOutput();
                }
            } catch (Exception ex) {
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            try {
                int selRow = jTree1.getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (oidBag.getOIDProperties(mibWalker.tryExtractDriverOID(getTreePathOID())).getOIDProperty(new OIDProperties().NODEINDEX).equals("20")){
                        jmiRemoveDeviceItem.setText("Remove " + oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(new OIDProperties().NAME));

                        showPopupC(e);
                    }
                    else if (oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(new OIDProperties().PNODEINDEX).equals("20")){
                        jmiAddDeviceItem.setText("Add New " + oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(new OIDProperties().NAME) + "...");
                        jmiRemoveDeviceFolder.setText("Remove " + oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(new OIDProperties().NAME));

                        showPopupB(e);
                    }
                    else if(oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(new OIDProperties().NODEINDEX).equals("20"))
                        showPopupA(e);
                    else
                        return;
                }
            } catch (Exception ex) {
                
            }
        }
    };

    private void showPopupA(MouseEvent e){
        if(e.isPopupTrigger())
            jpopA.show(e.getComponent(), e.getX(), e.getY());

    }

    private void showPopupB(MouseEvent e){
        if(e.isPopupTrigger())
            jpopB.show(e.getComponent(), e.getX(), e.getY());

    }

    private void showPopupC(MouseEvent e){
        if(e.isPopupTrigger())
            jpopC.show(e.getComponent(), e.getX(), e.getY());
    }

    public static void addChildNodeToParent(String parentNodeName, String nodeName){
        setTreePath(parentNodeName);
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)jTree1.getLastSelectedPathComponent();
        parentNode.add(new DefaultMutableTreeNode(nodeName));
        ((DefaultTreeModel)(jTree1.getModel())).reload();
        setTreePath(parentNodeName);
        
    }

    private Vector<String> getAllChilds() {
        Vector<String> childOIDs = new Vector<String>();

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();

        Enumeration e = parentNode.children();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            setTreePath(o.toString());
            childOIDs.add(getTreePathOID());    
        }

        return childOIDs;
    }

    private Vector<String> getAllChilds(String strParentOID) {
        Vector<String> childOIDs = new Vector<String>();
        setTreePath(strParentOID, oidBag.getOIDProperties(strParentOID).getOIDProperty(OIDProperties.NAME));
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();

        Enumeration e = parentNode.children();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            setTreePath(o.toString());
            childOIDs.add(getTreePathOID());
        }
        if(!parentNode.toString().equals("Devices 1"))
            RemoveNodeFromTree(parentNode);
        return childOIDs;
    }

    private void RemoveNodeFromTree(DefaultMutableTreeNode parentNode){
        parentNode.removeAllChildren();
        parentNode.removeFromParent();
        updateJTree("Devices 1");
    }

    public void updateJTree(String pathToThisParent){
        ((DefaultTreeModel)(jTree1.getModel())).reload();
        setTreePath(pathToThisParent);
       
    }

    public void removingDevices(String DeviceFolderOID_Delete_Last) {
        Vector<String> ids = getAllChilds();

        String Pid = "";
        for (int i = 0; i < ids.size(); i++) {
            Pid = ids.get(i);
            removingDeviceItem(Pid);
        }

        removingNodesFromTree(DeviceFolderOID_Delete_Last);
        oidBag.snycDelOID(DeviceFolderOID_Delete_Last);

    }

    public void removingNodesFromTree(String strParentOID){
        setTreePath(strParentOID, oidBag.getOIDProperties(strParentOID).getOIDProperty(OIDProperties.NAME));
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        if(!parentNode.toString().equals("Devices 1"))
            RemoveNodeFromTree(parentNode);
    }
    public void removingDeviceItem(String parentNodeOID) {
        
        Vector<String> childsID = getAllChilds(parentNodeOID);
        
        oidBag.snycDelOID(parentNodeOID);
        drvBag.delDriverItem(parentNodeOID);

        for(int i = 0; i < childsID.size(); i++){
            oidBag.snycDelOID(childsID.get(i));
            drvBag.delDriverItem(childsID.get(i));
        }

    }
    public void displayOutput() {
        txtFullName.setText(getTreePathOIDName());
        txtOID.setText(getTreePathOID());
        txtName.setText(oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(OIDProperties.NAME));
        txtAccess.setText(getRealAccessValue(oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(OIDProperties.ACCESS)));
        txtType.setText(getRealSyntaxValue(oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(OIDProperties.TYPE)));
        
        jtaDescription.setText(oidBag.getOIDProperties(getTreePathOID()).getOIDProperty(OIDProperties.DESCRIPTION));
        if(drvBag.getDrvValue(getTreePathOID()) != null)
            txtValue.setText(drvBag.getDrvValue(getTreePathOID()));
        else
            txtValue.setText("NULL");
    }
    
    private void setTreePath(String OID, String lastNodeName) {
  
        StringTokenizer st = new StringTokenizer(OID, ".");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree1.getModel().getRoot();

        TreePath path = find(root, lastNodeName + " " + OID.split("\\.")[st.countTokens() - 1]);
        jTree1.setExpandsSelectedPaths(true);
        jTree1.setSelectionPath(path);
        jTree1.scrollPathToVisible(path);
    }

    private static void setTreePath(String pInTree){
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree1.getModel().getRoot();

        TreePath path = find(root, pInTree);
        jTree1.setExpandsSelectedPaths(true);
        jTree1.setSelectionPath(path);
        jTree1.scrollPathToVisible(path);
    }

    private String getTreePathOIDName() {
        return jTree1.getSelectionPaths()[0].toString().replaceAll("\\d|\\[|\\]", "").replaceAll(" , ", ".");
    }

    private String getTreePathOID() {
        return jTree1.getSelectionPaths()[0].toString().replaceAll("[a-z]|[A-Z]|\\_|\\[|\\]| ", "").replaceAll(",",".");
    }

    public String getRealSyntaxValue(String value){
        if(value.equals("3"))
            return "SNMPOctetString";
        else if(value.equals("1"))
            return "SNMPInteger";
        else
            return value;
    }

    public String getRealAccessValue(String value){
        if(value.equals("1"))
            return "Read-Only";
        else if(value.equals("2"))
            return "Read-Write";
        else
            return value;
    }

    public static void main(String args[])
    {
        try
        {
            SNMPAgent_V1 theApp = new SNMPAgent_V1();

            theApp.setVisible(true);
        }
        catch (Exception e)
        {}
    }
//
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                new SNMPAgent_V1().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenu jmFile;
    private javax.swing.JPopupMenu jpopA;
    private javax.swing.JPopupMenu jpopB;
    private javax.swing.JPopupMenu jpopC;
    private javax.swing.JTextArea jtaDescription;
    private javax.swing.JTextArea messagesArea;
    private javax.swing.JScrollPane mibDisplay;
    private javax.swing.JTextField txtAccess;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOID;
    private javax.swing.JTextField txtType;
    private javax.swing.JTextField txtValue;
    // End of variables declaration//GEN-END:variables
    private void writeMessage(String message)
    {
        messagesArea.append(message);

    }

    public SNMPSequence processRequest(SNMPPDU pdu, String communityName, DatagramPacket inpacket)
        throws SNMPGetException, SNMPSetException
    {
        //get number of items in list...
        boolean canenter = true;


        writeMessage("Got pdu:   from " + inpacket.getSocketAddress()+ "\n\n");

        writeMessage("  community name:     " + communityName + "\n");
        writeMessage("  request ID    :     " + pdu.getRequestID() + "\n");
        writeMessage("  pdu type      :     ");
        byte pduType = pdu.getPDUType();
        switch (pduType)
        {
            case SNMPBERCodec.SNMPGETREQUEST:
            {
                writeMessage("SNMPGETREQUEST\n");
                break;
            }

            case SNMPBERCodec.SNMPGETNEXTREQUEST:
            {
                writeMessage("SNMPGETNEXTREQUEST\n");
                break;
            }

            case SNMPBERCodec.SNMPSETREQUEST:
            {
                writeMessage("SNMPSETREQUEST\n");
                break;
            }

            default:
            {
                writeMessage("unknown\n");
                break;
            }
        }

        SNMPSequence varBindList = pdu.getVarBindList();
        SNMPSequence responseList = new SNMPSequence();

        for (int i = 0; i < varBindList.size(); i++)
        {
            SNMPSequence variablePair = (SNMPSequence)varBindList.getSNMPObjectAt(i);
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(0);
            SNMPObject snmpValue = (SNMPObject)variablePair.getSNMPObjectAt(1);

            writeMessage("       OID      :     " + snmpOID + "\n");
            writeMessage("     value      :     " + snmpValue + "\n");


            // check to see if supplied community name is ours; if not, we'll just silently
            // ignore the request by not returning anything
            if (!communityName.equals(this.communityName))
            {
                continue;
            }

            try {
                //snmpOID.toString()
                //outer if..controlling d snmpOID existed in db or nt,.
                OIDProperties oidProps = ((OIDBag)bagPack.get(OIDBAG)).getOIDProperties(snmpOID.toString());

                if (!oidProps.getOIDProperty(ACCESS).equals("NULL")) {
                    if (pduType == SNMPBERCodec.SNMPSETREQUEST) {
                        if (Integer.parseInt(oidProps.getOIDProperty(ACCESS)) != READ_ONLY) {//1 is read onli
                           if (snmpValue instanceof SNMPOctetString && oidProps.getOIDProperty(TYPE).equals("3")) {

                                ((Driver_Bag)bagPack.get(DRIVERBAG)).setDrvValue(snmpOID.toString(), snmpValue.toString());
                                try {
                                    SNMPVariablePair newPair = new SNMPVariablePair(snmpOID, (SNMPOctetString) snmpValue);
                                    responseList.addSNMPObject(newPair);
                                } catch (SNMPBadValueException e) {
                                    // won't happen...
                                }
                                canenter = false;
                            }
 
                            ///////////////////////////////////////////////////another
                           else if(snmpValue instanceof SNMPInteger && oidProps.getOIDProperty(TYPE).equals("1")) {
                               ((Driver_Bag)bagPack.get(DRIVERBAG)).setDrvValue(snmpOID.toString(), snmpValue.toString());
                                try {
                                    SNMPVariablePair newPair = new SNMPVariablePair(snmpOID, (SNMPInteger) snmpValue);
                                    responseList.addSNMPObject(newPair);
                                } catch (SNMPBadValueException e) {
                                    // won't happen...
                                }
                            }

                            else {

                               if(canenter){
                                int errorIndex = i + 1;
                                int errorStatus = SNMPRequestException.BAD_VALUE;
                                //since u hav already supplied the message into the exception , jst wait the moment it being catchg,
                                //we can anytime use ex.toString() to retrieve back our mesage.!
                                writeMessage("\nSNMPSetException: Supplied value must be SNMPInteger. \n");
                                writeMessage("Error Index     : " + errorIndex + ".\n");
                                writeMessage("Error Status    : " + errorStatus + ".\n");
                                throw new SNMPSetException("Supplied value must be in" + getRealSyntaxValue(oidProps.getOIDProperty(TYPE)) , errorIndex, errorStatus);
                                }
                            }
                           


                        } else {
                            //if its readonly value.
                            int errorIndex = i + 1;
                            int errorStatus = SNMPRequestException.VALUE_READ_ONLY;
                            writeMessage("\nSNMPSetException: Trying to set a read-only variable! \n");
                            writeMessage("Error Index     : " + errorIndex + ".\n");
                            writeMessage("Error Status    : " + errorStatus + ".\n");
                            throw new SNMPSetException("Trying to set a read-only variable!", errorIndex, errorStatus);
                        }

                    } else if (pduType == SNMPBERCodec.SNMPGETREQUEST){
                        try
                        {
                            SNMPVariablePair newPair;
                            if(Integer.parseInt(oidProps.getOIDProperty(TYPE)) == TYPE_INTEGER)
                                newPair = new SNMPVariablePair(new SNMPObjectIdentifier(snmpOID.toString()), new SNMPInteger(((Driver_Bag)bagPack.get(DRIVERBAG)).getDrvValue(snmpOID.toString())));
                            else
                                newPair = new SNMPVariablePair(new SNMPObjectIdentifier(snmpOID.toString()), new SNMPOctetString(((Driver_Bag)bagPack.get(DRIVERBAG)).getDrvValue(snmpOID.toString())));
                            responseList.addSNMPObject(newPair);
                        }
                        catch (SNMPBadValueException e)
                        {
                            // won't happen...
                        }
                    }
                }
                else
                {//if entered, oid value not found, not a d
                    writeMessage("\nSNMPException: Requested OID can't be get or set. \n");
                }

            } catch (Exception e) {
               
            }


        }

        writeMessage("******************************************************************************\n");
        // return the created list of variable pairs
        return responseList;
    }

    
    public SNMPSequence processGetNextRequest(SNMPPDU pdu, String communityName, DatagramPacket inpacket)
        throws SNMPGetException
    {

        writeMessage("Got pdu:\n");

        writeMessage("  community name:     " + communityName + "\n");
        writeMessage("  request ID:         " + pdu.getRequestID() + "\n");
        writeMessage("  pdu type:           ");
        byte pduType = pdu.getPDUType();

        switch (pduType)
        {
            case SNMPBERCodec.SNMPGETREQUEST:
            {
                writeMessage("SNMPGETREQUEST\n");
                break;
            }

            case SNMPBERCodec.SNMPGETNEXTREQUEST:
            {
                writeMessage("SNMPGETNEXTREQUEST\n");
                break;
            }

            case SNMPBERCodec.SNMPSETREQUEST:
            {
                writeMessage("SNMPSETREQUEST\n");
                break;
            }

            default:
            {
                writeMessage("unknown\n");
                break;
            }


        }



        SNMPSequence varBindList = pdu.getVarBindList();
        SNMPSequence responseList = new SNMPSequence();

        for (int i = 0; i < varBindList.size(); i++)
        {
            SNMPSequence variablePair = (SNMPSequence)varBindList.getSNMPObjectAt(i);
            SNMPObjectIdentifier suppliedOID = (SNMPObjectIdentifier)variablePair.getSNMPObjectAt(0);
            SNMPObject suppliedObject = (SNMPObject)variablePair.getSNMPObjectAt(1);

            writeMessage("       OID:           " + suppliedOID + "\n");
            writeMessage("       value:         " + suppliedObject + "\n");


            // check to see if supplied community name is ours; if not, we'll just silently
            // ignore the request by not returning anything
            if (!communityName.equals(this.communityName))
            {
                continue;
            }


            String getNextOID = ((OIDBag)bagPack.get(OIDBAG)).getNextOID(suppliedOID.toString());
            if (getNextOID != null)
            {
                if (pduType == SNMPBERCodec.SNMPGETNEXTREQUEST)
                {
                    // got a get-next-request for our variable; send back a value for OID 1.3.6.1.2.1.100.0
                    try
                    {
                        OIDProperties oidProps = ((OIDBag)bagPack.get(OIDBAG)).getOIDProperties(getNextOID);
                        // create SNMPVariablePair for the next OID and its value
                        SNMPObjectIdentifier nextSNMPOID = new SNMPObjectIdentifier(getNextOID);
                        
                       
                        SNMPVariablePair innerPair;
                        if (Integer.parseInt(oidProps.getOIDProperty(TYPE)) == TYPE_INTEGER)
                            innerPair = new SNMPVariablePair(nextSNMPOID, new SNMPInteger(((Driver_Bag) bagPack.get(DRIVERBAG)).getDrvValue(getNextOID)));
                        else
                            innerPair = new SNMPVariablePair(nextSNMPOID, new SNMPOctetString(((Driver_Bag) bagPack.get(DRIVERBAG)).getDrvValue(getNextOID)));


                        // now create a pair containing the supplied OID and the variable pair containing the following
                        // OID and its value; this allows the ANMPv1AgentInterface to know which of the supplied OIDs
                        // the new OID corresponds to (follows).
                        SNMPVariablePair outerPair = new SNMPVariablePair(suppliedOID, innerPair);

                        // add the "compound" SNMPVariablePair to the response list
                        responseList.addSNMPObject(outerPair);
                    }
                    catch (SNMPBadValueException e)
                    {
                        // won't happen...
                    }
                }

            }

        }

        writeMessage("\n");


        // return the created list of variable pairs
        return responseList;

    }

    public void run() {
        int numChars;
        char[] charArray = new char[256];

        try
        {
            while (!readerThread.isInterrupted() && ((numChars = errorReader.read(charArray, 0, charArray.length)) != -1))
            {
                StringBuffer errorMessage = new StringBuffer();
                errorMessage.append("Problem receiving request:\n");
                errorMessage.append(new String(charArray, 0, numChars));
                errorMessage.append("\n");
                writeMessage(errorMessage.toString());
            }
        }
        catch(IOException e)
        {
            messagesArea.append("Problem receiving errors; error reporter exiting!");
        }
    }
    private class WindowCloseAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            readerThread.interrupt();
            System.exit(0);
        }
    }
}