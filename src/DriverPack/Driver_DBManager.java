package DriverPack;

import DBCentre.DB_HOST;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author roylee
 */
public class Driver_DBManager {
    //db connection variable


    private PreparedStatement pstmtInsert;
    private Connection con;
    

    public Driver_DBManager() {
        
        pstmtInsert = null;
        con = null;
        con2MIB();
    }
    
    private void con2MIB() {
        try {
            con = DB_HOST.getConnection();
            con.setAutoCommit(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean batchSaving(HashMap<String, GenericDriver> driverItems) throws SQLException, Exception {

        String strUpdateState = "UPDATE nodedata SET value = (?) WHERE nodeIndex = (?)";
        String[] drValue = null;
        Iterator itrAttributes = null;
        GenericDriver newDriver = null;
        HashMap<Integer, DriverValue> attrHashMap = null;
        pstmtInsert = con.prepareStatement(strUpdateState);

        Iterator itrDrivers = driverItems.keySet().iterator(); //search thru all driver, each driver has many attributes.

        while(itrDrivers.hasNext()){
            newDriver = driverItems.get(itrDrivers.next());
            attrHashMap = newDriver.getAttributeHashMap();
            itrAttributes = attrHashMap.keySet().iterator();

            while(itrAttributes.hasNext()){
                Object nodeindex = itrAttributes.next();
                drValue = attrHashMap.get(nodeindex).getDriverValue();
                pstmtInsert.setObject(1, drValue[1]);
                pstmtInsert.setObject(2, drValue[0]);
                pstmtInsert.addBatch();
            }
        }
        return this.COMMIT();
    }
    
    private boolean COMMIT(){
        int[] updateCounts = null;
        boolean pushed = false;
        try{
            updateCounts = pstmtInsert.executeBatch();
            con.commit();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        if(updateCounts.length > 0)
            pushed = true;

        return pushed;
    }

}
