/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjava;

/**
 *
 * @author mifouche
 */
public class DBCommunicator {
    
    /*  FUNCTIONS RELATED TO LOGIN
        LoginUser(username, password);String errors[]
        -usernameExists(username);boolean
        -usernameMatchPassword(username, password);boolean
    
        FUNCTIONS RELATED TO REGISTER USER
        RegisterUser();String errors[]
        -usernameExists(username);boolean
        -emailExists(email);boolean
        -passwordMatch(password1, password2);boolean
        -passwordValid(password);boolean
        -addUser(username, email, password);boolean(for successful adding)
        
        FUNCTIONS RELATED TO GAME-POOL
        getPoolList();String[] (array with all usernames and their userscores)
        addUserToPool(username);boolean
        joinUserInPool(username, opponentUsername);boolean 
        -userStillAvailableInPool(opponentUsername);Boolean
        -connectToUserInPool(username, opponentUsername);boolean 
        
        createMatch(matchID,userID, opponentUserID,currentQuestion, currentMatchScore);String sessionID //there will be 2 entries in the table, 1 for each user of the match
        -getNextMatchID();matchID
    
        FUNCTIONS RELATED TO GAME-TIME          
        requestQuestionForImage(imageID);String[] questions
        submitAnswer(imageID, answer);boolean correct
        getResults(matchSessionID, userID, opponentUserID);currentMatchScore for userID, currentMatchScore for opponentUserID
        getScoreForUser(matchID, userName);int score
        getCurrentQuestionForUser(matchID, userName);int currentQuestion
    
    
    */
}
