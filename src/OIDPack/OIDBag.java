package OIDPack;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

/**
 *
 * @author roylee
 */
public class OIDBag {

    private static Map OIDHashMap = new HashMap<String, OIDProperties>();//everyoid de name.
    private List listKeys;

    public OIDBag() {
       
    }

    public void addOID(String newOID, OIDProperties attrs) {
        OIDHashMap.put(newOID, attrs);
    }

    private void delOID(String OID) {
        OIDHashMap.remove(OID);
    }


    public void snycAddOID(String newOID, OIDProperties attrs){ //hold first. can enter more than 2 params,.
        addOID(newOID, attrs);
        OID_DBManager ctrlOID = new OID_DBManager();
        String pNodeIndex = ((OIDProperties)OIDHashMap.get(newOID)).getOIDProperty(attrs.PNODEINDEX);//((OIDProperties)OIDHashMap.get(extractDriverName(newOID))).getOIDProperty(attrs.NODEINDEX);
        
        ctrlOID.addOIDToMIB(attrs.getOIDProperty(attrs.NODEINDEX), pNodeIndex, extractAttributeID(newOID) + "", attrs.getOIDProperty(attrs.NAME), attrs.getOIDProperty(attrs.ACCESS), attrs.getOIDProperty(attrs.TYPE), attrs.getOIDProperty(attrs.DESCRIPTION));
    }

    public void snycDelOID(String OID){ //hold first. can enter more than 2 params,.
        OID_DBManager ctrlOID = new OID_DBManager();
        ctrlOID.removeOIDFromMIB(this.getOIDProperties(OID).getOIDProperty(OIDProperties.NODEINDEX));

        delOID(OID);
    }
    public HashMap<String, OIDProperties> getOIDBag() {
        return (HashMap<String, OIDProperties>)OIDHashMap;
    }

    public OIDProperties getOIDProperties(String OID){
        if(OID != null)
            return (OIDProperties)OIDHashMap.get(OID);
        else{
            return null;
        }
    }

    private void sortOIDHashMap(){
        OIDHashMap = new TreeMap(OIDHashMap);//autoSort.
    }

    public String getNextOID(String OID) {
        sortOIDHashMap(); //sort first
        listKeys = new ArrayList(OIDHashMap.keySet());
        try {
            String oid = listKeys.get(listKeys.indexOf(OID) + 1).toString(); //equals to 1 means failed!
            if (!oid.equals("1"))
                return oid;
            else 
                return null; // means not found
            
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

//    public int getCount(){
//        return OIDHashMap.size();
//    }

    public void clear() {
        OIDHashMap.clear();
    }

    public String extractDriverName(String OID){
        long[] oid = convertDigitString(OID);
        int lengthOfLastNum = (oid[oid.length - 1] + "").length();
        return OID.substring(0, OID.length() - (lengthOfLastNum + 1));
    }

    public int extractAttributeID(String OID){
        long[] oid = convertDigitString(OID);
        return Integer.parseInt(oid[oid.length - 1] + "");
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
