package DBCentre;

import java.sql.*;

/**
 *
 * @author roylee
 */
public class DB_HOST {
    
    private static String username="";
    private static String password="";
    private static String url= "";

    public DB_HOST(String newusername, String newpassword, String newurl){
        username = newusername;
        password = newpassword;
        url = newurl;
    }

    public static Connection getConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        }catch(Exception ex){
           
        }
        return null;
    }
  

}
