package ChatPack;


/*
 * simpleClient.java
 *
 * Created on May 9, 2010, 9:34:57 PM
 */

/**
 *
 * @author roylee
 */
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import testSNMP.WindowUtilities;
public class SimpleClient extends javax.swing.JFrame {
private Socket tunnel = null;
private static int PORT_NUM = 19000;
private static String HOST = ""; ///"roylee.mine.nu";//"roylee-PC";//"126.32.3.160";
private DataInputStream fromServer = null;
private DataOutputStream toServer = null;
private static String name = "";

    /** Creates new form simpleClient */
    public SimpleClient() {
        WindowUtilities.setNimbusLookAndFeel();
   

        frmInitChating.showDialog();
        HOST = frmInitChating.getIPAddress();
        name = frmInitChating.getUserName();
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
        initClient();
        
        
        
    }

  


    public void initClient(){
        try{
            tunnel = new Socket(HOST, PORT_NUM);
            toServer = new DataOutputStream(tunnel.getOutputStream());
            fromServer = new DataInputStream(tunnel.getInputStream());
            toServer.writeUTF(name);
            jtaTypeArea.requestFocus();
            
            while(true){
                jtaClient.append(fromServer.readUTF() + "\n");
                jtaClient.setCaretPosition(jtaClient.getDocument().getLength());
            }

        }catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null,"Unknown host: check hostname");
            System.exit(0);
         
        }catch  (IOException ioe) {//this is must ... Output and Input itself is a IO operation, so it waits until user input!
            JOptionPane.showMessageDialog(null,"No connection from Server.\n The Server is not up!");
            System.exit(0);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaClient = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaTypeArea = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ChatClient");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jtaClient.setColumns(20);
        jtaClient.setEditable(false);
        jtaClient.setFont(new java.awt.Font("Segoe UI", 0, 13));
        jtaClient.setLineWrap(true);
        jtaClient.setRows(5);
        jtaClient.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jtaClient);
        jtaClient.getAccessibleContext().setAccessibleParent(this);

        jtaTypeArea.setColumns(20);
        jtaTypeArea.setFont(new java.awt.Font("Segoe UI", 0, 13));
        jtaTypeArea.setLineWrap(true);
        jtaTypeArea.setRows(5);
        jtaTypeArea.setWrapStyleWord(true);
        jtaTypeArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtaTypeAreaKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jtaTypeArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtaTypeAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtaTypeAreaKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            evt.consume();
            Request(jtaTypeArea.getText());
            jtaTypeArea.setText("");
            jtaClient.setCaretPosition(jtaClient.getDocument().getLength());
        }
    }//GEN-LAST:event_jtaTypeAreaKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        try {

            toServer.writeUTF("quit");
            toServer.close();
            fromServer.close();
            tunnel.close();
            System.exit(0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }//GEN-LAST:event_formWindowClosing


    public void Request(String message){//instead of while(true) we control wit button
        try{
            toServer.writeUTF(message);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
//    public static void showDialog(){
//        name = frmInitChating.getUserName();
//        HOST = frmInitChating.getIPAddress();
//        SimpleClient = new simpleClient();
//    }
    //@Override
//    protected void processWindowEvent(WindowEvent e) {
//
//        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
//
//            int exit = JOptionPane.showConfirmDialog(this, "Are you sure to exit?", "Confirmation Dialog", JOptionPane.YES_NO_OPTION);
//            if (exit == JOptionPane.YES_OPTION) {
//
//
//                setVisible(false);
//
//            }
//
//        } else {
//            super.processWindowEvent(e);
//        }
//    }

    public static void main(String[] args){
        new SimpleClient();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jtaClient;
    private javax.swing.JTextArea jtaTypeArea;
    // End of variables declaration//GEN-END:variables

}
