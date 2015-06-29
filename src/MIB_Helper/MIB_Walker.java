package MIB_Helper;

import DBCentre.DB_HOST;
import DriverPack.Driver_Bag;
import OIDPack.*;
import java.sql.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

public class MIB_Walker {

    //db connection variable

    private PreparedStatement pstmt;
    private Connection con;
    private static final String DEVICE_FOLDER_NAME = "Devices";
    private static final int NODEINDEX = 0;
    private static String OID_TO_DEVICE_FOLDER;
 
    //stack initialization
    Stack<Integer> s;

    public MIB_Walker() {

        pstmt = null;
        con = null;
        s = new StackTree<Integer>();
        con2MIB();
    }

    private void con2MIB() {
        try {
            con = DB_HOST.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getName(int index) {
        String sqlFindName = "SELECT name FROM nodelink WHERE nodeIndex = (?)";

        try {
            pstmt = con.prepareStatement(sqlFindName);//it can be global bcoz will be init to new value b4 process
            pstmt.setInt(1, index);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public Vector Load() {//by using stack,we can eliminate the need of recursive! <-knowledge
        int root = 1;
        int top = 0;
        HashMap<String, String> pathStore = new HashMap<String, String>();
        OIDBag oids = new OIDBag();
        Driver_Bag drvrBag = new Driver_Bag();
        String parent_OID = "";
        String child_OID = "";
        ResultSet rsMIB, rsData = null;
        String sqlFindChild = "SELECT nodeIndex, cNodeID, pNodeIndex, name  FROM nodelink WHERE pNodeIndex = (?) ORDER BY cNodeID";
        String sqlFindNodeData = "SELECT nodeIndex, ACCESS, TYPE, DESCRIPTION, VALUE FROM nodedata WHERE nodeIndex = (?)";
        pathStore.put("iso 1", "1");

        oids.clear(); //clear all b4 load.
        oids.addOID(pathStore.get("iso 1"), new OIDProperties("1", "NULL", "NULL", "NULL", "iso", "0"));
        s.push(root);
        s.COMMIT();
        while (!s.isEmpty()) {
            top = s.pop();
            parent_OID = pathStore.get(getName(top) + " " + getNodeID(top));

            try {
                pstmt = con.prepareStatement(sqlFindChild);
                pstmt.setInt(1, top);
                rsMIB = pstmt.executeQuery();

                while (rsMIB.next()) { // once i found my child,
                    s.push(rsMIB.getInt("nodeIndex"));
                    child_OID =  parent_OID + "." + rsMIB.getInt("cNodeID");
                    pathStore.put(rsMIB.getString("name") + " " + rsMIB.getInt("cNodeID"), child_OID);
                    
                    try {
                        pstmt = con.prepareStatement(sqlFindNodeData);
                        pstmt.setInt(1, rsMIB.getInt(1));
                        rsData = pstmt.executeQuery();

                        if (rsData.next()){  // if ever found a node with a nodedata.
                            oids.addOID(child_OID, new OIDProperties(rsMIB.getString("nodeIndex"), rsData.getString("ACCESS"), rsData.getString("TYPE"), rsData.getString("DESCRIPTION"),  rsMIB.getString("name"), rsMIB.getString("pNodeIndex")));
                            drvrBag.addDriverItem(child_OID, rsMIB.getString("nodeIndex"), rsData.getString("VALUE"));
                        }
                        else{

                            if(rsMIB.getString("name").equals(DEVICE_FOLDER_NAME))
                                OID_TO_DEVICE_FOLDER = child_OID;
                            
                            oids.addOID(child_OID, new OIDProperties(rsMIB.getString("nodeIndex"), "NULL", "NULL", "NULL", rsMIB.getString("name"), rsMIB.getString("pNodeIndex")));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                s.COMMIT();// commit on batch mode.

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Vector bagPacks = new Vector();
        bagPacks.add(oids);
        bagPacks.add(drvrBag);
        return bagPacks;
    }

    public DefaultMutableTreeNode initTreeDisplay(int root) {//by using stack,we can eliminate the need of recursive! <-knowledge

        DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode("iso 1");
        DefaultMutableTreeNode parent;
        int top = 0;
        boolean hasChild = false;
        String sqlFindChild = "SELECT nodeIndex, cNodeID, name FROM nodelink WHERE pNodeIndex = (?) ORDER BY cNodeID";
       
        s.push(root);
     
        s.COMMIT();

        while (!s.isEmpty()) {
            top = s.pop();
         
            parent = getParentNode(ROOT, getName(top) + " " + getNodeID(top));
            
            try {
                pstmt = con.prepareStatement(sqlFindChild);
                pstmt.setInt(1, top);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    s.push(rs.getInt(1));
                    parent.add(new DefaultMutableTreeNode(rs.getString(3) + " " + rs.getInt(2)));
                    hasChild = true;
                }
                if (hasChild) {//to facilitate batch update!
                    hasChild = false;
                    s.COMMIT();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }//while

        return ROOT;
    }

    //im going to return the device instance's OID Property, we can know the cNode = pNode of attributes.
    public String getNewDeviceOID(String deviceType) {
        OIDBag oidBag = new OIDBag();
        int DEVICE_FOLDER_INDEX = getDeviceFolderNodeIndex();
        Vector values = isDeviceTypeExisted(deviceType, DEVICE_FOLDER_INDEX);

  
        if (values != null) { //existed
            String deviceTypeID = values.get(0).toString();
            int deviceNodeIndex = Integer.parseInt(values.get(1) + "");
            String deviceInstanceNo = Integer.toString(getNumOfChild(deviceNodeIndex) + 1);
            oidBag.snycAddOID(OID_TO_DEVICE_FOLDER + "." + deviceTypeID + "." + deviceInstanceNo, new OIDProperties(Integer.toString(getMax() + 1), "NULL", "NULL", "NULL", deviceType + convertInt2Char(deviceInstanceNo),oidBag.getOIDProperties(OID_TO_DEVICE_FOLDER + "." + deviceTypeID).getOIDProperty(NODEINDEX)));
            
            return OID_TO_DEVICE_FOLDER + "." + deviceTypeID + "." + deviceInstanceNo;
        }

        else{
            String deviceTypeID = Integer.toString(getNumOfChild(DEVICE_FOLDER_INDEX) + 1);
            oidBag.snycAddOID(OID_TO_DEVICE_FOLDER + "." + deviceTypeID , new OIDProperties(Integer.toString(getMax()+ 1), "NULL", "NULL", "NULL", deviceType, oidBag.getOIDProperties(OID_TO_DEVICE_FOLDER).getOIDProperty(NODEINDEX)));
            oidBag.snycAddOID(OID_TO_DEVICE_FOLDER + "." + deviceTypeID + ".1", new OIDProperties(Integer.toString(getMax()+ 1), "NULL", "NULL", "NULL", deviceType + "A", oidBag.getOIDProperties(OID_TO_DEVICE_FOLDER + "." + deviceTypeID).getOIDProperty(NODEINDEX)));

            return  OID_TO_DEVICE_FOLDER + "." + deviceTypeID + ".1";
        }

    }

    private int getDeviceFolderNodeIndex(){
        OIDBag oidBag = new OIDBag();
        return  Integer.parseInt(oidBag.getOIDProperties(OID_TO_DEVICE_FOLDER).getOIDProperty(NODEINDEX));
    }

    public int getMax(){
        String sqlFindNumChild = "SELECT MAX(nodeIndex) FROM nodelink";
        try{
             pstmt = con.prepareStatement(sqlFindNumChild);

             ResultSet rs = pstmt.executeQuery();
             if(rs.next())
                 return rs.getInt(1);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

    private Vector isDeviceTypeExisted(String newDeviceType, int DEVICE_FOLDER_INDEX){
        Vector values = new Vector();
        String sqlFindNumChild = "SELECT name, cNodeID, nodeIndex FROM nodelink WHERE pNodeIndex = (?)";
        try{

             pstmt = con.prepareStatement(sqlFindNumChild);
             pstmt.setInt(1, DEVICE_FOLDER_INDEX); //pre=set to 20..unchangeable.
             ResultSet rs = pstmt.executeQuery();
             while(rs.next()){
                 if(rs.getString("name").equals(newDeviceType)){
                      values.add(rs.getInt("cNodeID"));
                      values.add(rs.getInt("nodeIndex"));
                      
                      return values;
                 }
             }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public Vector<String> getAllAvailableDeviceName() {
        int DEVICE_FOLDER_INDEX = getDeviceFolderNodeIndex();
        Vector<String> names = new Vector<String>();
        String sqlFindNumChild = "SELECT name FROM nodelink WHERE pNodeIndex = (?)";
        try {

            pstmt = con.prepareStatement(sqlFindNumChild);
            pstmt.setInt(1, DEVICE_FOLDER_INDEX); //pre=set to 20..unchangeable.
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                names.add(rs.getString("name"));
            return names;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
       return null;
    }

    private String convertInt2Char(String value){
        int val = Integer.parseInt(value) + 64;

        return Character.toString((char)val);
    }

    public int getNumOfChild(int pNodeIndex){
       
        String sqlFindNumChild = "SELECT COUNT(*) FROM nodelink WHERE pNodeIndex = (?)";
        try{
             pstmt = con.prepareStatement(sqlFindNumChild);
             pstmt.setInt(1, pNodeIndex);
             ResultSet rs = pstmt.executeQuery();
             if(rs.next())
                 return rs.getInt(1);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

    public DefaultMutableTreeNode getParentNode(DefaultMutableTreeNode root, String target) {
        
        Enumeration e = root.depthFirstEnumeration();
       
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o.toString().equals(target)) {
               
                return (DefaultMutableTreeNode) o;
            }
        }
       

        return null;
    }

    private int getNodeID(int index) {
        String sqlFindName = "SELECT cNodeID FROM nodelink WHERE nodeIndex = (?)";

        try {
            pstmt = con.prepareStatement(sqlFindName);
            pstmt.setInt(1, index);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public String tryExtractDriverOID(String OID){
        long[] oid = convertDigitString(OID);
        int lengthOfLastNum = (oid[oid.length - 1] + "").length();
        int lengthOfLast2Num = (oid[oid.length - 2] + "").length();
     
        return OID.substring(0, OID.length() - (lengthOfLastNum + lengthOfLast2Num + 2));
    }

    public int extractAttributeID(String OID){
        long[] oid = convertDigitString(OID);
        return Integer.parseInt(oid[oid.length - 1] + "");
    }

    public int extractLast2ID(String OID){
        long[] oid = convertDigitString(OID);
        return Integer.parseInt(oid[oid.length - 2] + "");
    }

    private long[] convertDigitString(String digitString)
    {
        try
        {
            StringTokenizer st = new StringTokenizer(digitString, " .");
            int size = 0;

            while (st.hasMoreTokens())
            {
                // figure out how many values are in string
                size++;
                st.nextToken();
            }


            long[] returnDigits = new long[size];

            st = new StringTokenizer(digitString, " .");

            for (int i = 0; i < size; i++)
            {
                returnDigits[i] = Long.parseLong(st.nextToken());
                if (returnDigits[i] < 0)
                    System.out.println("bad OID");
            }

            return returnDigits;

        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return null;

    }

 //   public static void main(String[] args) {
//        MIB_Walker mw = new MIB_Walker();
//        Vector bagPack = mw.Load();
       
//        if (((OIDBag)bagPack.get(0)).getOIDProperties("1.3.6.1.4.1.999999.1.1.1.1").getOIDProperty(2) == null)
//            System.out.println("null");
//        else
//            System.out.println(((Driver_Bag)bagPack.get(1)).getDrvValue("1.3.6.1.4.1.999999.1.1.1.2"));
//
//
//        System.out.println(((OIDBag)bagPack.get(0)).getNextOID("1.3.6.1.4.1"));
//
//        System.out.println(((OIDBag)bagPack.get(0)).getCount());
//
//        OIDBag ne = new OIDBag();
//        System.out.println("asdas " +ne.getCount());
  
//
//      OIDBag ne = new OIDBag();
//
//      String deviceOID = mw.getNewDeviceOID("Pendrive");


//      ne.snycAddOID(deviceOID + ".1", new OIDProperties(Integer.toString(ne.getCount()+ 1), "1", "1", "this is my description", "prtStatus"));
      
      
//use new OIDProperties.

      //prtStatus ,  "type: int/string", "access:read/write ","description: abcasdasdasd"
      //System.out.println(ne.getOIDProperties(mw.getNewDeviceOID("sohai")).getOIDProperty(NAME));

//    }
}
