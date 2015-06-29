package MIB_Helper;
import DBCentre.DB_HOST;
import java.sql.*;
/**
 *
 * @author roylee, for stack implementation~~
 */
public class StackTree<T> implements Stack <T>{
    private Connection con;
    private PreparedStatement pstmt = null;
    private int topIndex;
    public StackTree(){
        //init of DB connection;
        String sqlPush = "INSERT INTO STACK VALUES(?,?)";
        try{
            con = con = DB_HOST.getConnection();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(sqlPush);//we put here coz if put down we cannot addbatch,it will overwrite
            topIndex = -1;
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void push(T nodeID){
        //we might need to consider a batch update, by creating one more method --> COMMIT;
        try{
            topIndex++;
            pstmt.setObject(1, topIndex);
            pstmt.setObject(2, nodeID);
            pstmt.addBatch();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public T pop(){
        PreparedStatement pstmtPop = null;
        PreparedStatement pstmtRemove = null;
        String sqlPop = "SELECT NODEID FROM STACK WHERE IDX = (?)";
        String sqlRemoveTop = "DELETE FROM STACK WHERE IDX = (?)";
        T top = null;
        try{
            pstmtPop = con.prepareStatement(sqlPop);
            pstmtPop.setObject(1, topIndex);
            ResultSet rs = pstmtPop.executeQuery();

            if(rs.next()){
                top = (T)rs.getObject(1);
                pstmtRemove = con.prepareStatement(sqlRemoveTop);
                pstmtRemove.setObject(1, topIndex);
                pstmtRemove.executeUpdate();
                con.commit();
                topIndex--;
           }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return top;
    }
    public boolean isEmpty(){
        return topIndex < 0;
    }

    public boolean COMMIT(){
        int[] updateCounts = null;
        boolean pushed = false;
        try{
            updateCounts = pstmt.executeBatch();
            con.commit();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        if(updateCounts.length > 0)
            pushed = true;
        
        return pushed;
    }
    
    public boolean clear(){
        String sqlClear  = "DELETE FROM STACK";
        Statement stmt1 = null;
        try{
            stmt1 = con.createStatement();
            stmt1.executeUpdate(sqlClear);
            con.commit();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
}

       //isEmpty (outdated)
      /*  Statement stmt = null;
        String sqlCount = "SELECT COUNT(*) FROM STACK";
        boolean isEmpty = true;
        try{
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlCount);
            if(rs.next())
                if(rs.getInt(1) > 0)
                    isEmpty = false;
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return isEmpty;*/
