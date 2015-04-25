/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjava;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
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
     public boolean usernameMatchPassword(String username, String password)
     {
         boolean flag = false;
         try
         {
            conn = makeConnection();
            
            Statement s = conn.createStatement();

            s.executeQuery("Select userID from users where userID like '"+username+"' and password like '"+password+"';"); // select the data from the table

            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
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
    
    
    /*FUNCTIONS RELATED TO REGISTER USER
        RegisterUser();String errors[]      (this is also used in login)
        -usernameExists(username);boolean
        -emailExists(email);boolean
        -passwordMatch(password1, password2);boolean
        -passwordValid(password);boolean
        -addUser(username, email, password);boolean(for successful adding)*/
        
    public ArrayList<String> registerUser(String uname, String email, String pword1, String pword2)
    {
        ArrayList<String> errors = new ArrayList<String>();
        boolean userExists = false;
        boolean emailExists = false;
        boolean emailValid = false;
        boolean pwordMatch = false;
        boolean pwordValid = false;
        boolean userAdded = false;
        
        try
        {
            userExists = usernameExists(uname);
            if(!userExists)
            {
                emailExists = emailExists(email);
                if(!emailExists)
                {   
                    emailValid = emailValid(email);
                    if(emailValid)
                    {
                        pwordValid = passwordValid(pword1);
                        if(pwordValid)
                        {
                            pwordMatch = passwordMatch(pword1,pword2);
                            if(pwordMatch)
                            {
                                userAdded = addUser(uname, email, pword1);
                                if(userAdded)
                                {
                                    System.out.println("user added");
                                }
                                else
                                    errors.add("userNotAdded");
                            }
                            else
                            errors.add("passwordMismatch");
                        }
                        else
                          errors.add("passwordInvalid");   
                    }
                    else
                        errors.add("emailInvalid");
                }
                else
                    errors.add("emailExists");
            }
            else
                errors.add("UserExists");
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return errors;
    }
    
    public boolean emailExists(String email)
    {
        boolean exists = false;
        
        try
        {
            conn = makeConnection();
            Statement s = conn.createStatement();
            s.executeQuery("Select Email from users where Email = '"+email+"';");
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                exists = true;
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {    
            System.out.println(e);
        }
        return exists;
    }
    
    public boolean emailValid(String email)
    {
        boolean valid = false;
        
        if(email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))
            valid = true;
        
        return valid;
    }
    public boolean passwordValid(String pword)
    {
        boolean valid = false;
        if(pword.length() > 6)
        {
            if(pword.matches("^(?=.*[a-z])(?=.*[0-9])[a-z0-9]+$"))
            {
                valid=true;
            }
        }
        return valid;
    }
    
    public boolean passwordMatch(String pword1, String pword2)
    {
        boolean match = false;
        
        if(pword1.matches(pword2))
            match = true;
        
        return match;
    }
    
    public boolean addUser(String uname, String email, String pword)
    {
        boolean inserted = false;
        
        try
         {
            conn = makeConnection();
            
            Statement insert = conn.createStatement();
            //Statement s = conn.createStatement();
            
            insert.executeUpdate("INSERT INTO users Values( '"+uname+"','"+email+"' ,'"+pword+"', 0) ;"); // insert the data to the table
            //s.execute("Select userID from users where userID  = '"+uname+"';"); //check data inserted
            
            //ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            //if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            //{
            inserted = true;
            //}
            insert.close();
            //s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
         }
         catch(Exception e)
         {
             System.out.println(e);
             
         }
        
        return inserted;
    }
    
        /*FUNCTIONS RELATED TO GAME-POOL
        getPoolList();String[] (array with all usernames and their userscores)
        addUserToPool(username);boolean
        -checkIfUserInPool(username);boolean        
        joinUserInPool(username, opponentUsername);boolean 
        -userStillAvailableInPool(opponentUsername);Boolean
        -connectToUserInPool(username, opponentUsername);boolean 
        createMatch(matchID,userID, opponentUserID,currentQuestion, currentMatchScore);String sessionID //there will be 2 entries in the table, 1 for each user of the match
        -getNextMatchID();matchID
    */
        
    public ArrayList<ArrayList<String>> getPoolList()
    {
        ArrayList<ArrayList<String>> poolList = new ArrayList<ArrayList<String>>();
        ArrayList<String> usersPool = new ArrayList<String>();
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("Select gamepool.userID,users.score from gamepool INNER JOIN users ON users.userID =  gamepool.userID;"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                usersPool = new ArrayList<>();
                usersPool.add( rs.getString("userID"));
                usersPool.add(rs.getString("score"));              
                
                poolList.add(usersPool);
            }
            //int poolSize = poolList.size(); 
           /* for(int i=0;i<poolSize;i++)
            {
                ArrayList<String> currentList = poolList.get(i);
                System.out.println("uname "+currentList.get(0));
                System.out.println("score"+currentList.get(1));
                
            }
           for (int i = 0; i < poolList.size(); i++) 
           {
                for (ArrayList<String> innerList : poolList) {
                    //You don't need this if all lists are the same length.
                    System.out.println("uname"+innerList.get(0));
                    System.out.println("score"+innerList.get(1));
                }
                System.out.println(); //new line for the next row
            }*/
           
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return poolList;
    }
    
    public boolean addUserToPool(String uname)
    {
        boolean added = false;
        if(!checkUserInPool(uname))
        {
            System.out.println("user not in pool");
            //add to pool
            try
            {
            conn = makeConnection();
            
            Statement insert = conn.createStatement();
            Statement s = conn.createStatement();
            
            insert.execute("INSERT INTO gamePool (userID, opponentUserID) Values( '"+uname+"', '' );"); // insert the data to the table
            s.execute("Select userID from gamePool where userID  = '"+uname+"';"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                added = true;
            }
            insert.close();
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
           
        }
            
        return added;
    }
    
    public boolean checkUserInPool(String uname)
    {
        boolean inPool = false;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("SELECT userID FROM GamePool where userID = '"+uname+"';"); //check user in pool            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            while (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
               inPool = true;
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return inPool;
    }
    
    public boolean joinUserInPool(String uname, String oppoName)
    {
        boolean joinedPool = false;
        
        if(userAvailable(oppoName))
        {
            joinedPool = connectToUser(uname, oppoName);
            if(joinedPool)
                System.out.println("User Joined in Pool");
        }
        else
            System.out.println("Joining user in pool not available anymore");
        return joinedPool;
    }
    
    public boolean userAvailable(String uname)
    {
        boolean available = false;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("SELECT UserID FROM GamePool where UserID = '"+uname+"' and opponentUserID = '';"); //check user in pool            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            while (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
               available = true;
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return available;
    }
    public String matchFoundInPool(String uname)
    {
        String opponentUsername = "";
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("SELECT opponentUserID FROM GamePool where UserID = '"+uname+"' and opponentUserID not like '';"); //check user in pool            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            while (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
               opponentUsername = rs.getString("opponentUserID");
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return opponentUsername;
    }
    
    public boolean connectToUser(String uname, String oppoName)
    {
        boolean connected = false;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            String query = ("UPDATE GamePool SET opponentUserID = ? where UserID = ? ");    
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, uname);
            preparedStmt.setString(2, oppoName);
 
      // execute the java preparedstatement
            preparedStmt.executeUpdate();
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
            connected = true;
            System.out.println("GamePool joining opponent:"+oppoName+"\tUser: "+uname);
        }
        catch(Exception e)
        {
            System.out.println("Update gamepool to add userID in the opponent slot "+e);
        }
        
        return connected;
    }
    
    public int createMatch(int matchID, String userID, String opponentUserID, int currentQuestion, float currentMatchScore)
    {
        int sessionID = 0;
        
        try
         {
            conn = makeConnection();
            
            Statement insert = conn.createStatement();
            Statement s = conn.createStatement();
            
            insert.execute("INSERT INTO matchSession (matchID, userID, opponentUserID, currentQuestion, currentMatchScore) Values( '"+matchID+"','"+userID+"' ,'"+opponentUserID+"','"+currentQuestion+"' ,'"+currentMatchScore+"');"); // insert the data to the table
            s.execute("Select matchSessionID from matchSession where matchID  = '"+matchID+"';"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
               sessionID = rs.getInt("matchSessionID");
            }
            insert.close();
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
         }
         catch(Exception e)
         {
             System.out.println("Error in CreateMatch: \n"+e);
             
         }
        
        return sessionID;
    }
    
    public int getNextMatchID()
    {
        int prevMatchID = 0;
        
        try
         {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("Select MAX(matchID) from matchsession;"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
               prevMatchID = rs.getInt("matchID");
            }
            
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
         }
         catch(Exception e)
         {
             System.out.println("Error in getNextMatchID: \n"+e);
             
         }
        prevMatchID = prevMatchID+1;
        return prevMatchID;
    }
    
        /*
        FUNCTIONS RELATED TO GAME-TIME          
        requestQuestionForImage(imageID);String[] questions
        submitAnswer(imageID, answer);boolean correct
        getResults(matchSessionID, userID, opponentUserID);currentMatchScore for userID. will have to run twice, for each user.
        getScoreForUser(matchID, userName);int score
        getCurrentQuestionForUser(matchID, userName);int currentQuestion
        checkIfPartneredYet
       -checkIfUserInPool(username);boolean
    */
    
    public ArrayList<String> requestQuestionForImage(String imageID)
    {
        ArrayList<String> question = new ArrayList<String>();
         try
        {
            conn = makeConnection();            
            Statement s = conn.createStatement();             
            s.execute("Select question1, question2, question3, question4 from imagedata where imageID = '"+imageID+"';"); //check data inserted
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            while (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                System.out.println("hopefully only one"+ rs.getString("question1"));
                question.add(rs.getString("question1"));
                question.add(rs.getString("question2"));
                question.add(rs.getString("question3"));
                question.add(rs.getString("question4"));
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println("Get image questions:\n"+e);
        }
        
        return question;
    }
    
    public boolean submitAnswer(String imageID, String answer)
    {
        boolean correct = false;
        
         try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("Select answer from questions where imageID = '"+imageID+"';"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                    if(answer.matches(rs.getString("answer")))
                        correct = true;
                
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return correct;
        
    }
    public int getResults(String matchID,String userID, String opponentUserID)
    {
        int currentMatchScore = 0;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("Select currentMatchScore from matchSession where matchID = '"+matchID+"' and userID = '"+userID+"' and opponentUserID = '"+opponentUserID+"' ;"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                currentMatchScore = rs.getInt("currentMatchScore");
                
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return currentMatchScore;
    }
    
    public int getScoreForUser(String matchID, String userName)
    {
        int score = 0;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("Select currentMatchScore from matchSession where matchID = '"+matchID+"' userID = '"+userName+"';"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                score = rs.getInt("score");
                
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return score;
    }
    
    public int getCurrentQuestionForUser(String matchID,String userName)
    {
        int currentQuestion = 0;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("Select currentQuestion from matchSession where userID = '"+userName+"' and matchID = '"+matchID+"';"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                currentQuestion = rs.getInt("currentQuestion");
                
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return currentQuestion;
    }
    
    public boolean checkIfUserInPool(String username)
    {
        boolean inPool = false;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            
            s.execute("Select userID from gamePool where userID = '"+username+"';"); //check data inserted
            
            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                inPool = true;
                
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return inPool;
    }
    
    /*delete from gamepool
        delete matchSession
    delete from users*/
    public boolean removeUserFromPool(String uname )
    {
        boolean success = false;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            Statement check = conn.createStatement();
            
            s.execute("Delete from gamePool where userID = '"+uname+"';"); //check data inserted
            check.execute("select userID from gamePool where userID = '"+uname+"';");
            
            ResultSet rs = check.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                success = false;
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return success;
    }
    public boolean deleteMatch(int matchSessionID)
    {
        boolean success = false;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            Statement check = conn.createStatement();
            
            s.execute("Delete from matchSession where matchSessionID = '"+matchSessionID+"';"); //check data inserted
            check.execute("select matchSessionID from matchSession where matchSessionID = '"+matchSessionID+"';");
            
            ResultSet rs = check.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                success = false;
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return success;
    }
    public boolean deleteUser(String uname)
    {
        boolean success = true;
        
        try
        {
            conn = makeConnection();
            
            Statement s = conn.createStatement();
            Statement check = conn.createStatement();
            
            s.execute("Delete from Users where UserID = '"+uname+"';"); //check data inserted
            check.execute("select userID from Users where UserID = '"+uname+"';");
            
            ResultSet rs = check.getResultSet(); // get any ResultSet that came from our query
            if (rs.next() ) // if rs == null, then there is no ResultSet to view  
            {
                success = false;
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
            conn = null;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        return success;
    }
}
