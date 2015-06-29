package DriverPack;

import java.util.*;

public class Driver_Bag {
//oid  + generic driver = oid + sum of(attrIndex + values) = oid + sum of(attrIndex + (nodeindex + value))
    private static HashMap<String, GenericDriver> driverItems = new HashMap<String, GenericDriver>(); //string will be the edited oid.. until .printerA.
    protected Driver_DBManager d_dbm = new Driver_DBManager();

    public Driver_Bag() {
        
    }

    public void addDriverItem(String OID, String nodeIndex, String value) { //useful during load. and add.
        //OID = A.B.C.D
        String OID_DvrName = extractDriverName(OID);
        int attrID = extractAttributeID(OID);
        GenericDriver driver = driverItems.get(OID_DvrName);
        
        if(driver != null){
            driver.addAttributes(attrID, nodeIndex, value);
        }
        else{
            GenericDriver newDriver = new GenericDriver(attrID, nodeIndex, value);
            driverItems.put(OID_DvrName, newDriver);
        }
    }
    
    public void setDrvValue(String OID, String value) { //OID = ful qualify oid., for communication purpose.
        String OID_DvrName = extractDriverName(OID);
        GenericDriver newDriver = driverItems.get(OID_DvrName);
 
        newDriver.setValue(extractAttributeID(OID), value);
    }

    public String getDrvValue(String OID) {
        String OID_DvrName = extractDriverName(OID);
        GenericDriver newDriver = driverItems.get(OID_DvrName);
        if (newDriver != null)
            return newDriver.getValue(extractAttributeID(OID));
        else
            return null;
    }

    public String getDrvValueNodeIndex(String OID) {
        String OID_DvrName = extractDriverName(OID);
        GenericDriver newDriver = driverItems.get(OID_DvrName);

        return newDriver.getValueNodeIndex(extractAttributeID(OID));
    }

    public String extractDriverName(String OID){
        long[] oid = convertDigitString(OID);
        int lengthOfLastNum = (oid[oid.length -1] + "").length();
        return OID.substring(0, OID.length() - (lengthOfLastNum + 1));
    }

    public int extractAttributeID(String OID){
        long[] oid = convertDigitString(OID);
        return Integer.parseInt(oid[oid.length - 1] + "");
    }

    private HashMap<String, GenericDriver> getDriverBag(){
        return driverItems;
    }

    public GenericDriver getDriverItem(String OID){
        return driverItems.get(OID);
    }
  
    public int getCount(){
        return driverItems.size();
    }

    public boolean SaveAllDriversToDB() {
        try {
            return d_dbm.batchSaving(driverItems);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void delDriverItem(String OID){
        driverItems.remove(OID);
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
}








//1.3.6.1.4.1.999999.1.1.1.1.0


