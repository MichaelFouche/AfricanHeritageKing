/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ahkjava.DBCommunicator;
import java.util.ArrayList;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author mifouche
 */
public class TestDBCommunicator {
    
    public TestDBCommunicator() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    DBCommunicator dbc = new DBCommunicator();
    @Test (enabled = true)
    public void login()
    {
        Assert.assertTrue(dbc.usernameExists("foosh"));
        Assert.assertFalse(dbc.usernameExists("Dirk"));
    }
    @Test (enabled = true)
    public void uMatchPassword()
    {
        Assert.assertTrue(dbc.usernameMatchPassword("foosh", "1@3"));
        Assert.assertFalse(dbc.usernameMatchPassword("foosh","123"));
    }

    @Test (enabled = true)
    public void registerUser()
    {
        Assert.assertTrue(dbc.emailExists("foosh@outlook.com"));
        Assert.assertFalse(dbc.emailExists("batman@yahoo.com"));
        Assert.assertTrue(dbc.emailValid("batman@yahoo.com"));
        Assert.assertFalse(dbc.emailValid("mkyong@.com.my"));
        Assert.assertTrue(dbc.passwordValid("mayer123"));
        Assert.assertFalse(dbc.passwordValid("may1"));
        Assert.assertTrue(dbc.passwordMatch("mayer123", "mayer123"));
        Assert.assertFalse(dbc.passwordMatch("mayer123", "may1"));
        Assert.assertTrue(dbc.addUser("rynom", "rmayer@outlook.com", "mayer123"));
        Assert.assertTrue(dbc.deleteUser("rynom"));
    }
    
    @Test (enabled = true)
    public void GamePool()
    {   Assert.assertTrue(dbc.addUser("rynom", "rmayer@outlook.com", "mayer123"));
        
        ArrayList<ArrayList<String>> poolList = dbc.getPoolList();
        Assert.assertNotNull(poolList);
        Assert.assertTrue(dbc.addUserToPool("rynom"));
        Assert.assertTrue(dbc.checkUserInPool("ryno"));
        Assert.assertFalse(dbc.checkUserInPool("batman"));
        Assert.assertTrue(dbc.joinUserInPool("foosh", "ryno"));
        Assert.assertTrue(dbc.connectToUser("foosh", "ryno"));
        Assert.assertFalse(dbc.connectToUser("foosh", "batman"));
        Assert.assertFalse(dbc.userAvailable("foosh"));
        Assert.assertEquals(dbc.getNextMatchID(),2);
        Assert.assertTrue(dbc.userAvailable("rynom"));
        //delete match
        Assert.assertTrue(dbc.deleteUser("rynom"));
        //Assert.assertTrue(dbc.deleteGame("rynom"));
    }
    
    @Test (enabled = false)
    public void InGame()
    {
        ArrayList<String> Q = dbc.requestQuestionForImage("imgID1");
        Assert.assertEquals(Q.size(),4 );
        Assert.assertTrue(dbc.submitAnswer("imgID1", ""));
        Assert.assertFalse(dbc.submitAnswer("imgID1", "Axum Northern Stelea Park"));
        Assert.assertEquals(dbc.getResults("1", "foosh", "ryno"),1);
        Assert.assertEquals(dbc.getScoreForUser("1","foosh"), 1);
        Assert.assertEquals(dbc.getCurrentQuestionForUser("1","foosh"),1);
        Assert.assertTrue(dbc.checkUserInPool("rynom"));
        
       Assert.assertEquals(dbc.createMatch("1", "foosh", "ryno", 1, 0), 1);

        
        Assert.assertTrue(dbc.deleteMatch(1));
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
