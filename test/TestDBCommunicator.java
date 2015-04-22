/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ahkjava.DBCommunicator;
import org.testng.Assert;
import static org.testng.Assert.*;
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
