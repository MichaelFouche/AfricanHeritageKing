/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjava;
//import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Driver;
import javax.sql.DataSource;
import java.sql.*;
/**
 *
 * @author mifouche
 */
public class AHKJava {
    public AHKJava() throws SQLException{
        makeConnection();
    } 
    
    private Connection conn;  

     public  Connection makeConnection() throws SQLException {
        if (conn == null) {
             new Driver();
            // buat koneksi
             conn = DriverManager.getConnection(
                       "jdbc:mysql://10.0.0.100/ahk",
                       "root",
                       "panda");
         }
         return conn;
     } 
     
     public void getRecords()
     {
         try
         {
            conn = makeConnection();
            Statement s = conn.createStatement();
            s.executeQuery("Select userID from users"); // select the data from the table
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs != null) // if rs == null, then there is no ResultSet to view  
            {
                while (rs.next())
                {
                    System.out.println( rs.getString(1));
                }
            }
            
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
         }
         catch(Exception e)
         {
             
         }
         
     }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
             AHKJava c = new AHKJava();
             c.getRecords();
             System.out.println("Connectionblished");
         }
         catch (SQLException e) {
             e.printStackTrace();
             System.err.println("Connectionure");
         }  
    }
    
}
