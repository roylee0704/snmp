package OIDPack;
import DBCentre.DB_HOST;
import java.sql.*;
/**
 *
 * @author roylee
 */
public class OID_DBManager {

    private PreparedStatement pstmt;
    private Connection con;

    public OID_DBManager() {
        pstmt = null;
        con = null;
        con2MIB();
    }
    
    private void con2MIB() {
        try {
            con = DB_HOST.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean addOIDToMIB(String nodeIndex,String pNodeIndex, String cNodeID, String name, String ACCESS, String TYPE, String DESCRIPTION){

        String sqlInsertNodeLink = "INSERT INTO nodelink(nodeIndex, pNodeIndex, cNodeID, name) VALUES (?,?,?,?)";
        String sqlInsertNodeData = "INSERT INTO nodedata(nodeIndex, ACCESS, TYPE, DESCRIPTION) VALUES (?,?,?,?)";
        try{
            pstmt = con.prepareStatement(sqlInsertNodeLink);
            pstmt.setInt(1, Integer.parseInt(nodeIndex));
            pstmt.setInt(2, Integer.parseInt(pNodeIndex));
            pstmt.setInt(3, Integer.parseInt(cNodeID));
            pstmt.setString(4, name);

            pstmt.executeUpdate();
            if(!ACCESS.equals("NULL")){
                pstmt = con.prepareStatement(sqlInsertNodeData);
                pstmt.setInt(1, Integer.parseInt(nodeIndex));
                pstmt.setInt(2, Integer.parseInt(ACCESS));
                pstmt.setInt(3, Integer.parseInt(TYPE));
                pstmt.setString(4, DESCRIPTION);

                pstmt.executeUpdate();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return true;
    }
    public boolean removeOIDFromMIB(String nodeIndex){

        String sqlDeleteFromNodeLink = "DELETE FROM nodelink WHERE nodeIndex = (?)";
        try{
            pstmt = con.prepareStatement(sqlDeleteFromNodeLink);
            pstmt.setInt(1, Integer.parseInt(nodeIndex));
            pstmt.executeUpdate();

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return true;
    }

}