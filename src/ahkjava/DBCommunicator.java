/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjava;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author mifouche
 */
public class DBCommunicator {
    private Connection conn = null;  
    
    public DBCommunicator()
    {
        
    }
    
    public  Connection makeConnection() throws SQLException 
    {
        if (conn == null) 
        {
             new Driver();
            // buat koneksi
             conn = DriverManager.getConnection(
            /*THE AMAZON SERVER
                       "jdbc:mysql://ec2-54-201-3-103.us-west-2.compute.amazonaws.com:3306/ahk",
                       "mike",
                       "pine88appl3");*/

            //Localhost                    
                       "jdbc:mysql://localhost/ahk",
                       "root",
                       "");
         }
         return conn;
     } 
        
     public Boolean usernameExists(String username)
     {
         boolean flag = false;
         try
         {
            conn = makeConnection();
            
            Statement s = conn.createStatement();

            s.executeQuery("Select userID from users where userID like '"+username+"';"); // select the data from the table

            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs != null) // if rs == null, then there is no ResultSet to view  
            {
                flag = true;
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
         }
         catch(Exception e)
         {
             System.out.println(e);
             
         }
         return flag;
     }
    
    
    /*  FUNCTIONS RELATED TO LOGIN
        LoginUser(username, password);String errors[]
        -usernameExists(username);boolean   (this is also used in register)
        -usernameMatchPassword(username, password);boolean
    
        FUNCTIONS RELATED TO REGISTER USER
        RegisterUser();String errors[]      (this is also used in login)
        -usernameExists(username);boolean
        -emailExists(email);boolean
        -passwordMatch(password1, password2);boolean
        -passwordValid(password);boolean
        -addUser(username, email, password);boolean(for successful adding)
        
        FUNCTIONS RELATED TO GAME-POOL
        getPoolList();String[] (array with all usernames and their userscores)
        addUserToPool(username);boolean
        -checkIfUserInPool(username);boolean        
        joinUserInPool(username, opponentUsername);boolean 
        -userStillAvailableInPool(opponentUsername);Boolean
        -connectToUserInPool(username, opponentUsername);boolean 
        
        createMatch(matchID,userID, opponentUserID,currentQuestion, currentMatchScore);String sessionID //there will be 2 entries in the table, 1 for each user of the match
        -getNextMatchID();matchID
    
        FUNCTIONS RELATED TO GAME-TIME          
        requestQuestionForImage(imageID);String[] questions
        submitAnswer(imageID, answer);boolean correct
        getResults(matchSessionID, userID, opponentUserID);currentMatchScore for userID. will have to run twice, for each user.
        getScoreForUser(matchID, userName);int score
        getCurrentQuestionForUser(matchID, userName);int currentQuestion
    
    
    */
}
