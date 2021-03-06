/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjava;
//import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Driver;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
/**
 *
 * @author mifouche
 * Update 25 April 8:17PM
 */

public class AHKJava implements ActionListener{
    DBCommunicator dbc = new DBCommunicator();
    private JButton btnSignIn,btnRegister;
    JLabel lblLogin, lblPW;
    JTextField txtLogin;
    JPasswordField txtPW;
    
    JFrame jf;
    JPanel panelMain, panelHeading, panelLogo, panelLogin;
    
    JPanel panelPool,panelPoolN,panelPoolS;
    
    //Register
    JFrame jfR;
    JPanel panelRN, panelRC, panelRS;
    JLabel lblRLogo, lblRUser, lblRPass1, lblPass2, lblEmail;
    JTextField  txtRUser,  txtEmail;
    JPasswordField txtRPass1, txtPass2;
    JButton btnRRegister;
    
    JLabel lblGamePic;
    JProgressBar pbGame;
    JPanel panelGame, panelGameW, panelGameE,panelGameS,panelGameN;
    JRadioButton rbc1, rbc2, rbc3, rbc4;
    ButtonGroup rbGroup;
    JLabel lblQuest1, lblQuest2, lblCorrect1, lblCorrect2;
    
    //pool
    JLabel lblUser[], lblScore[];
    JButton btnJoin[];
    JButton btnAddUserToPool;
    JScrollPane scrollPane;
    JPanel contentPane;
    int poolSize;
    String imageID;
    
    JButton btnSubmitAnswer;
    //game variables
    
    String loggedInUsername;
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
    ScheduledExecutorService ses5 = Executors.newScheduledThreadPool(10);
    public int progressSize;
    boolean gameTimeLeft;
    boolean flagInGame;
    String userToJoin;
    
    ArrayList<ArrayList<String>> poolList;
    ArrayList<String> allImagesForGame;
    ArrayList<String> questionForGameImage;
    String btnJoinPoolText ;
    String[] opponentUsername = {"",""};
    int currentImageViewing;
    int sessionID;
    boolean waitingInPool;
    boolean justFinishedGame;
    int matchID;
    
    //Scoreboard
    JFrame jfW;
    JPanel panelWN, panelWC, panelWS;
    JLabel lblWLogo, lblWplayer;
    JProgressBar pbWPlayer1, pbWPlayer2;
    JButton btnWReturn;
    Boolean scoreboardOpen;
    Boolean imTheJoiningUser;
    //Scoreboard
    
    
    public AHKJava() throws SQLException
    {
        makeConnection();
        loggedInUsername = "";
        poolSize = 0;        
        flagInGame = false;
        btnJoinPoolText = "Join Pool";
        currentImageViewing = 0;
        allImagesForGame = new ArrayList<>();
        waitingInPool = false;
        justFinishedGame = false;
        scoreboardOpen = false;
        imTheJoiningUser = false;
        gameTimeLeft = false;
        
        
        ses.scheduleAtFixedRate(new Runnable() 
        {
            @Override
            public void run() 
            {
                //System.out.println("execute the timer query");
                //Update Pool
                //check if user in pool, then whether the user was matched yet to another user.
                
                if(gameTimeLeft)
                {
                    pbGame.setValue(progressSize);
                    progressSize = progressSize-1;
                    pbGame.setString(progressSize + " Seconds Remaining");
                    if(progressSize<1)
                    {
                        gameTimeLeft = false;
                        flagInGame = false;
                        justFinishedGame = true;
                        scoreboardOpen = true;
                        createWinningGui();
                    } 
                }
                else if(justFinishedGame)
                {
                    justFinishedGame = false;
                   // JOptionPane.showMessageDialog(null, "Your time is up!\nYou answered "+dbc.getCurrentQuestionForUser(sessionID, loggedInUsername)+" questions, with "+dbc.getScoreForUser(sessionID, loggedInUsername)+" correct","AHK - Game",JOptionPane.ERROR_MESSAGE);
                    
                    //call the who won window
                    //that window should display the results of both players
                }
                
                
                
            }
        }, 5, 1, TimeUnit.SECONDS);  // execute every x seconds
        
        ses5.scheduleAtFixedRate(new Runnable() 
        {
            @Override
            public void run() 
            {
                //System.out.println("execute the timer query");
                //Update Pool
                //check if user in pool, then whether the user was matched yet to another user. 
                if(waitingInPool)
                {
                    //System.out.println("Waiting in pool");
                    opponentUsername = dbc.matchFoundInPool(loggedInUsername);
                    //System.out.println("Opponent try: "+opponentUsername);
                    //opponentUsername[1] is the matchID  <-- IMPORTANT
                    if(!opponentUsername[0].equals(""))
                    {
                        //System.out.println("Partner found");
                        dbc.removeUserFromPool(loggedInUsername);
                        gameTimeEnable(true);
                        gamePoolEnable(false);
                        progressSize = 30;
                        gameTimeLeft = true;
                        flagInGame = true;
                        waitingInPool = false;
                        imTheJoiningUser = true;
                        //--------
                        
                        try
                        {
                            //opponentUsername[1] is the matchID  <-- IMPORTANT
                            sessionID = dbc.createMatch(Integer.parseInt(opponentUsername[1]), loggedInUsername, opponentUsername[0], 1, 0);
                            //dbc.createMatch(matchID, loggedInUsername, userToJoin, 1, 0);
                            //System.out.println("Session: "+sessionID);
                        }
                        catch(Exception ee)
                        {
                            System.out.println("Could not create the game session: \n"+ee);
                        }
                        System.out.println("username:"+loggedInUsername+" opponent: "+opponentUsername[0]+ " session: "+sessionID+" match: "+opponentUsername[1]);
                        getNextQuestion();
                        //start game.
                    }       
                }
                if(scoreboardOpen)
                {
                    if(imTheJoiningUser)
                    {
                        //System.out.println("username:"+loggedInUsername+" opponent: "+opponentUsername[0]+ " session: "+sessionID+" match: "+opponentUsername[1]);
                        System.out.println("Results for joiner: ");
                        lblWplayer.setText(loggedInUsername+" VS "+opponentUsername[0]);
                        int score1 = dbc.getResults(sessionID,loggedInUsername, opponentUsername[0]);
                        int score2 = dbc.getOpponentScore(Integer.parseInt(opponentUsername[1]), opponentUsername[0]);
                        pbWPlayer1.setValue(score1);
                        pbWPlayer1.setString(loggedInUsername+" scored "+score1);
                        pbWPlayer2.setValue(score2);
                        pbWPlayer2.setString(opponentUsername[0]+" scored "+score2);
                        
                    }
                    else
                    {
                        //System.out.println("username:"+loggedInUsername+"opponent: "+userToJoin+ "session: "+sessionID+" match: "+matchID);
                        lblWplayer.setText(loggedInUsername+" VS "+userToJoin);
                        int score1 = dbc.getResults(sessionID,loggedInUsername, userToJoin);
                        int score2 = dbc.getOpponentScore(matchID, userToJoin);
                        pbWPlayer1.setValue(score1);
                        pbWPlayer1.setString(loggedInUsername+" scored "+score1);
                        pbWPlayer2.setValue(score2);
                        pbWPlayer2.setString(userToJoin+" scored "+score2);
                        
                    }
                    //update who won here
                    
                }
                
                updatePoolPanel();
                    
                
            }
        }, 5, 5, TimeUnit.SECONDS);  // execute every x seconds

    } 
    
    
    

    private Connection conn;  

        
     public  Connection makeConnection() throws SQLException {
        if (conn == null) {
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
     
     public void getRecords()
     {
         try
         {
            conn = makeConnection();
            Statement s = conn.createStatement();

            s.executeQuery("Select userID, email, password, score from users"); // select the data from the table

            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs != null) // if rs == null, then there is no ResultSet to view  
            {
                while (rs.next())
                {
                    System.out.println( rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getInt(4));
                }
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
         }
         catch(Exception e)
         {
             System.out.println(e);
         }
         
     }
     public void createWinningGui()
     {
         
                 
         
         jfW = new JFrame("AHK - Scoreboard");
         panelWN = new JPanel();
         panelWC = new JPanel(new GridLayout(3,1));
         panelWS = new JPanel();
         
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("ahkMiniLogo.JPG"));
            ImageIcon image = new ImageIcon(bi); 
            lblWLogo = new JLabel(image);
            panelWN.add(lblWLogo );
         }
         catch(Exception e)
         {
             System.out.println("createWinningGui(load image): \n"+e);             
         }
         
         lblWplayer = new JLabel("Player 1 vs Player 2");
         pbWPlayer1 = new JProgressBar(0,100);
         pbWPlayer1.setStringPainted(true);
         pbWPlayer1.setValue(0);
         pbWPlayer1.setString("Player 1 score loading");  
         
         pbWPlayer2 = new JProgressBar(0,100);
         pbWPlayer2.setStringPainted(true);
         pbWPlayer2.setValue(0);
         pbWPlayer2.setString("Player 1 score loading");
         
         panelWC.add(lblWplayer);
         panelWC.add(pbWPlayer1);
         panelWC.add(pbWPlayer2);
         
         btnWReturn = new JButton("Return to main window");
         btnWReturn.addActionListener(this);
         panelWS.add(btnWReturn);
         
         jfW.add(panelWN,BorderLayout.NORTH);
         jfW.add(panelWC,BorderLayout.CENTER);
         jfW.add(panelWS,BorderLayout.SOUTH);
         //jfW.setDefaultCloseOperation(jfW.DISPOSE_ON_CLOSE);
         jfW.pack();
         jfW.setResizable(false);
         jfW.setVisible(true);
         jfW.setLocationRelativeTo(jf);
         jfW.addWindowListener(new java.awt.event.WindowAdapter() 
         {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) 
            {
                {
                    jfW.dispose();
                    scoreboardOpen = false;
                }
            }
        });
     }
     
     public void createAHKGui()
     {
         jf = new JFrame();
         panelMain = new JPanel(new BorderLayout(2,2));
         panelHeading = new JPanel(new FlowLayout(FlowLayout.CENTER));
         panelHeading.setBorder(new TitledBorder("African Heritage King"));
         panelLogo = new JPanel();
         panelLogin = new JPanel(new GridLayout(3,2));
         
         
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("ahkLogo.JPG"));
            ImageIcon image = new ImageIcon(bi); 
            JLabel l1 = new JLabel(image);
            panelLogo.add(l1 );
         }
         catch(Exception e)
         {
             System.out.println("createAHKGui(load image): \n"+e);
         }         
            
         
         lblLogin = new JLabel("Username");
         lblPW = new JLabel("Password");
         txtLogin = new JTextField(10);
         txtPW = new JPasswordField(10);
         btnSignIn = new JButton("Sign in");
         btnSignIn.addActionListener(this);
         btnRegister = new JButton("Register");
         btnRegister.addActionListener(this);
         txtLogin.setText(null);
         txtPW.setText(null);
         
         //panelLogin.add(lblTitle);
         panelLogin.add(lblLogin);
         panelLogin.add(lblPW);
         panelLogin.add(txtLogin);
         panelLogin.add(txtPW);
         panelLogin.add(btnSignIn);
         panelLogin.add(btnRegister);
         panelHeading.add(panelLogo,BorderLayout.WEST);
         panelHeading.add(panelLogin,BorderLayout.EAST);
         
         //PANEL POOL
         panelPool = new JPanel(new BorderLayout(2,2));
         this.addGamePoolToGUI();
         //GAME PANEL
         
         
         panelGame = new JPanel(new BorderLayout(2,2));
         panelGame.setBorder(new TitledBorder("Game Time"));
         panelGameW = new JPanel();
         panelGameE = new JPanel(new GridLayout(4,1));
         panelGameS = new JPanel();
         panelGameN = new JPanel(new BorderLayout(1,1));
         
         DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(100, 50, 0, 250);
         pbGame = new JProgressBar(0,30);
         pbGame.setStringPainted(true);
         pbGame.setValue(30);
         pbGame.setString("30 Seconds remaining");    
         panelGameN.add(pbGame);
         
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("tablemountain.jpg"));
            ImageIcon image = new ImageIcon(bi); 
            lblGamePic = new JLabel(image);
            panelGameW.add(lblGamePic );
         }
         catch(Exception e)
         {
             System.out.println("createAHKGui(load image): \n"+e);
         }
         rbc1 = new JRadioButton("Choice 1");
         rbc1.addActionListener(this);
         rbc2 = new JRadioButton("Choice 2");
         rbc1.addActionListener(this);
         rbc3 = new JRadioButton("Choice 3");
         rbc1.addActionListener(this);
         rbc4 = new JRadioButton("Choice 4");
         
         rbGroup = new ButtonGroup();
         rbGroup.add(rbc1);
         rbGroup.add(rbc2);
         rbGroup.add(rbc3);
         rbGroup.add(rbc4);
         
         panelGameE.add(rbc1);
         panelGameE.add(rbc2);
         panelGameE.add(rbc3);
         panelGameE.add(rbc4);
         
         
         btnSubmitAnswer = new JButton("Submit Answer");
         btnSubmitAnswer.addActionListener(this);
         lblQuest1 = new JLabel("Question ");
         lblQuest2 = new JLabel("1");
         lblCorrect1 = new JLabel("Correct");
         lblCorrect2 = new JLabel("0");
         panelGameS.add(btnSubmitAnswer);
         panelGameS.add(lblQuest1);
         panelGameS.add(lblQuest2);
         panelGameS.add(new JLabel("~ ~"));
         panelGameS.add(lblCorrect1);
         panelGameS.add(lblCorrect2);         
         
         panelGame.add(panelGameN, BorderLayout.NORTH);
         panelGame.add(panelGameW,BorderLayout.WEST);
         panelGame.add(panelGameE,BorderLayout.EAST);
         panelGame.add(panelGameS,BorderLayout.SOUTH);
         
         
         //END OF GAME PANEL
         JPanel panelSouth;
         panelSouth = new JPanel();
         panelSouth.add(new JLabel(""));
         
         jf.add(panelHeading, BorderLayout.NORTH);
         jf.add(panelPool, BorderLayout.WEST);
         jf.add(panelGame, BorderLayout.EAST);
         jf.add(panelSouth, BorderLayout.SOUTH);
         jf.setSize(1350,700);
         jf.setLocationRelativeTo(null);
         jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
         
         
         jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //DISPOSE_ON_CLOSE,  DISPOSE_ON_CLOSE 
        jf.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                int result = JOptionPane.showConfirmDialog(jf, "Are you sure you would like to exit?");
                if( result==JOptionPane.OK_OPTION)
                {
                    // NOW we change it to dispose on close..
                    jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
                    jf.setVisible(false);
                    jf.dispose();
                    try
                    {
                        
                    }
                    catch(Exception ee)
                    {
                        System.out.println("ERROR in leaving pool when exiting program\n"+ee);
                    }
                    //check if in pool, if in pool, delete
                    if(dbc.checkIfUserInPool(loggedInUsername))
                    {
                        dbc.removeUserFromPool(loggedInUsername);
                    }
                }
            }
        });
         
         jf.setVisible(true);
     }
     public void addGamePoolToGUI()
     {
         //--PANEL POOL   
         
         
         panelPool.setBorder(new TitledBorder("Game Pool"));
         panelPoolN = new JPanel();
         panelPoolS = new JPanel();
         
         poolList = dbc.getPoolList();  
         poolSize = poolList.size(); 
         lblUser = new JLabel[poolSize];
         lblScore = new JLabel[poolSize];
         btnJoin = new JButton[poolSize];
         
           
         for(int i=0;i<poolSize;i++)
         {
             ArrayList<String> currentList = poolList.get(i);
             lblUser[i] = new JLabel(currentList.get(0));

             lblScore[i] = new JLabel(currentList.get(1));
             btnJoin[i] = new JButton("Join");
             btnJoin[i].addActionListener(this);
         }
         
         //get the amount of users in pool, then print those, and print empty labels for the rest (10rows) to display nicely.
         JPanel panel;
         if(poolSize<11)
         {
             panel = new JPanel(new GridLayout(10,3) );
         }
         else
         {
             panel = new JPanel(new GridLayout(poolSize,3) );             
         }
         
         for (int i = 0; i < poolSize; i++) 
         {             
            panel.add(lblUser[i]);
            panel.add(lblScore[i]);
            panel.add(btnJoin[i]);
         }
         if(poolSize<11)
         {
            for(int i=0;i<10-poolSize;i++)
            {
               panel.add(new JLabel(""));
               panel.add(new JLabel(""));
               panel.add(new JLabel(""));
            }    
         }
         
         scrollPane = new JScrollPane(panel);
         scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         scrollPane.setBounds(10, 10, 400, 300);
         contentPane = new JPanel(null);
         contentPane.setPreferredSize(new Dimension(450, 300));
         contentPane.add(scrollPane);
         panelPoolN.add(contentPane);
         
         if(dbc.checkUserInPool(loggedInUsername))
         {
            btnJoinPoolText = "Leave Pool";
         }
         btnAddUserToPool = new JButton(btnJoinPoolText);
         btnAddUserToPool.addActionListener(this);
         
         panelPoolS.add(btnAddUserToPool);
         
         panelPool.add(panelPoolN,BorderLayout.NORTH);
         panelPool.add(panelPoolS, BorderLayout.SOUTH);
         //END OF PANEL POOL
         
     }
     public void updatePoolPanel()
     {
        panelPool.removeAll();
        addGamePoolToGUI();
        panelPool.revalidate();
        panelPool.repaint();
        panelPool.updateUI();
        if(loggedInUsername.equals("")&&!flagInGame)
        {
            gameTimeEnable(false);
            gamePoolEnable(false);
        }
        else if(flagInGame)
        {
            gameTimeEnable(true);
            gamePoolEnable(false);
        }
        else if(!loggedInUsername.equals("")&&!flagInGame)
        {
            gameTimeEnable(false);
            gamePoolEnable(true);
        }
     }
     public void gamePoolEnable(boolean flag)
     {
         for(int a=0;a<poolList.size();a++)
         {
             lblUser[a].setEnabled(flag);
         }
         for(int a=0;a<poolList.size();a++)
         {
             lblScore[a].setEnabled(flag);
         }
         for(int a=0;a<poolList.size();a++)
         {
             btnJoin[a].setEnabled(flag);
         }    
         panelPool.setEnabled(flag);
         panelPoolN.setEnabled(flag);
         panelPoolS.setEnabled(flag);
         scrollPane.setEnabled(flag);
         contentPane.setEnabled(flag);
         btnAddUserToPool.setEnabled(flag);
     }
     public void gameTimeEnable(boolean flag)
     {
         btnSubmitAnswer.setEnabled(flag);
         pbGame.setEnabled(flag);
         lblGamePic.setEnabled(true);
         rbc1.setEnabled(flag);
         rbc2.setEnabled(flag);
         rbc3.setEnabled(flag);
         rbc4 .setEnabled(flag);
         lblQuest1.setEnabled(flag);
         lblQuest2.setEnabled(flag);
         lblCorrect1 .setEnabled(flag);
         lblCorrect2.setEnabled(flag);
         
         panelGame.setEnabled(flag);
        panelGameW.setEnabled(flag);
        panelGameE.setEnabled(flag);
        panelGameS.setEnabled(flag);
        panelGameN.setEnabled(flag);
     }
     
     public void registerGUI()
     {
        jfR = new JFrame("Registration - African Heritage King");
        
        panelRN = new JPanel();
        panelRC = new JPanel(new GridLayout(4,2));
        panelRS = new JPanel();
              
        lblRLogo = new JLabel("Logo");
        lblRUser = new JLabel("Username");
        lblRPass1 = new JLabel("Password");
        lblPass2 = new JLabel("Confirm Password");
        lblEmail = new JLabel("Email");
    
        
        txtRUser = new JTextField(10);
        txtRPass1 = new JPasswordField(10);
        txtPass2 = new JPasswordField(10);
        txtEmail = new JTextField(10);
        btnRRegister = new JButton ("Register user");
        btnRRegister.addActionListener(this);
        
        try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("ahkMiniLogo.JPG"));
            ImageIcon image = new ImageIcon(bi); 
            lblRLogo = new JLabel(image);
            panelRN.add(lblRLogo );
         }
         catch(Exception e)
         {
             System.out.println("createAHKGui(load image): \n"+e);
         }
        
        
        panelRC.add(lblRUser);
        panelRC.add(txtRUser);
        panelRC.add(lblRPass1);
        panelRC.add(txtRPass1);
        panelRC.add(lblPass2);
        panelRC.add(txtPass2);
        panelRC.add(lblEmail);
        panelRC.add(txtEmail);
        
        panelRS.add(btnRRegister);
        
        jfR.add(panelRN, BorderLayout.NORTH);
        jfR.add(panelRC, BorderLayout.CENTER);
        jfR.add(panelRS, BorderLayout.SOUTH);
        jfR.pack();
        jfR.setVisible(true);
        jfR.setLocationRelativeTo(jf);
        jfR.setDefaultCloseOperation(jfR.DISPOSE_ON_CLOSE);
        jfR.setResizable(false);
     }
     
     public void getAllImages() throws IOException
     {
         try
         {
            allImagesForGame.clear();
            String path = "./src/ahkjava/resources/images"; 

            String fileItem;
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles(); 

            for (int i = 0; i < listOfFiles.length; i++) 
            {

                if (listOfFiles[i].isFile()) 
                {
                    fileItem = listOfFiles[i].getName();
                    if (fileItem.endsWith(".jpg") || fileItem.endsWith(".JPG"))
                    {
                        allImagesForGame.add(fileItem);
                    }
                }
            }
         }
         catch(Exception e)
         {
             System.out.println("Get all images"+e);
         }
                 
        
        
     }
     public void getNextQuestion()
     {
         imageID = allImagesForGame.get(currentImageViewing);
        
         //System.out.println("Imageid: "+imageID.substring(0, imageID.length() - 4));
         questionForGameImage = dbc.requestQuestionForImage(imageID.substring(0, imageID.length() - 4));
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("resources/images/"+imageID));
            ImageIcon image = new ImageIcon(bi); 
            lblGamePic.setIcon(image);
            //panelGameW.add(lblGamePic );
         }
         catch(Exception e)
         {
             System.out.println("getNextQuestion(load image): \n"+e);
         }
         try
         {
            rbc1.setText(questionForGameImage.get(0));
            rbc2.setText(questionForGameImage.get(1));
            rbc3.setText(questionForGameImage.get(2));
            rbc4.setText(questionForGameImage.get(3));
         }
         catch(Exception e)
         {
             System.out.println("Error setting radiobuttons \n"+e);
         }
         currentImageViewing=currentImageViewing+1;
         if(currentImageViewing>allImagesForGame.size()-1)
         {
             currentImageViewing=0;
         }
         
         
         
         //System.out.println("currentImageViewing: "+currentImageViewing);
         //need to update that entire center panel to allow the length of each place to not f**k everything up
         //set the question number
         //set the amount of correct answers
         
     }
     
     public void actionPerformed(ActionEvent e)
    {
        //Execute when button is pressed
        if(e.getSource()==btnSignIn)
        {
            if(btnSignIn.getText().equals("Sign in"))
            {
                if(txtLogin.getText()==null||txtLogin.getText().equals("")||txtLogin.getText().equals(" "))
                {
                    JOptionPane.showMessageDialog(null, "Please enter a username to login","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                }
                else if(txtPW.getText()==null||txtPW.getText().equals("")||txtPW.getText().equals(" "))
                {
                    JOptionPane.showMessageDialog(null, "Please enter a password to login","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    String uname = txtLogin.getText();
                    String pw = txtPW.getText();
                    if(dbc.usernameExists(uname))
                    {
                        if(dbc.usernameMatchPassword(uname, pw))
                        {
                            if(dbc.checkUserInPool(loggedInUsername))
                            {
                                btnJoinPoolText = "Leave Pool";
                                btnAddUserToPool.setText(btnJoinPoolText);
                            }
                            //poolList = dbc.getPoolList();  
                            loggedInUsername = uname;
                            txtLogin.setEnabled(false);
                            txtPW.setEnabled(false);
                            txtPW.setText(null);
                            btnSignIn.setText("Sign Out");
                            gameTimeEnable(false);
                            gamePoolEnable(true);
                            updatePoolPanel();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Sorry, the password entered is incorrect!","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Please enter a valid username","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                    }
                        
                }            
            }
            else
            {
                loggedInUsername = "";
                txtLogin.setEnabled(true);
                txtPW.setEnabled(true);
                txtPW.setText(null);
                btnSignIn.setText("Sign in");
                gameTimeEnable(false);
                gamePoolEnable(false);                
            }
            
            
        }
        if(e.getSource()==btnRegister)
        {
            this.registerGUI();
            System.out.println("You clicked the button btnRegister");
        }
        if(e.getSource() == btnRRegister)
        {
            String uname =txtRUser.getText();
            String pass1 = txtRPass1.getText();
            String pass2 = txtPass2.getText();
            String email = txtEmail.getText();
            if(uname.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid username","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(pass1.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid password","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(pass2.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid confirmation password","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(email.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid email","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                boolean errors = false;
                ArrayList<String> listOfErrors;
                listOfErrors = dbc.registerUser(uname, email, pass1, pass2);
                for(int i=0;i<listOfErrors.size();i++)
                {
                    System.out.println(listOfErrors.get(i));
                    switch(listOfErrors.get(i))
                    {
                        case "userNotAdded":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "The user was not added","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        case "passwordMismatch":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "The passwords does not match","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        case "passwordInvalid":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "Please enter a valid password\nA valid password is at least 6 characters long\nand should consist of at least a number, and a letter","AHK - Register",JOptionPane.ERROR_MESSAGE);
                            //password moet 6 characters long wees en bestaan uit lowercase letters en numbers
                        }break;
                        case "emailInvalid":                            
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "Please enter a valid email address","AHK - Register",JOptionPane.ERROR_MESSAGE);
                           /* . die email moet begin met _, A-Z(upper of lower), 0-9, na dit optionally n "." en a-z, 0-9, moet @ he, 
                            dan weer tussen a-z0-9, optional "." en a-z,0-9 en dan ".", a-z,0-9 met min length of 2 */
                        }break;
                        case "emailExists":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "The email is already registered to a user","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        case "UserExists":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "This username is already in use","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        default: ;
                        break;
                    }
                }
                if(!errors)
                {
                    jfR.dispose(); 
                }
               
                /*RegisterUser();String errors[]      (this is also used in login)
                -usernameExists(username);boolean
                -emailExists(email);boolean
                -passwordMatch(password1, password2);boolean
                -passwordValid(password);boolean
                -addUser(username, email, password);boolean(for successful adding)
                */
                //dbc.usernameExists()
            }
            
        }
        /*btnJoin*/
        if(poolSize>0)
        {
            
            for(int a =0;a<poolSize;a++)
            {
                if(e.getSource()==btnJoin[a])
                {
                    userToJoin = lblUser[a].getText();
                    if(dbc.userAvailable(userToJoin))
                    {  
                        getNextQuestion();
                        
                        gameTimeEnable(true);
                        gamePoolEnable(false);
                        progressSize = 30;
                        gameTimeLeft = true;
                        flagInGame = true;
                        matchID = 0;
                        try
                        {
                            matchID = dbc.getNextMatchID();
                            dbc.joinUserInPool(loggedInUsername,userToJoin,matchID);
                            sessionID = dbc.createMatch(matchID, loggedInUsername, userToJoin, 1, 0);
                            //System.out.println("Session: "+sessionID);
                        }
                        catch(Exception ee)
                        {
                            System.out.println("Could not create the game session: \n"+ee);
                        }
                        System.out.println("username:"+loggedInUsername+"opponent: "+userToJoin+ "session: "+sessionID+" match: "+matchID);
                        lblQuest2.setText(dbc.getCurrentQuestionForUser(sessionID, loggedInUsername)+"");
                        lblCorrect2.setText(dbc.getScoreForUser(sessionID, loggedInUsername)+"");
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Sorry, the user is not available anymore","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    }
                    
                }
            }
            
        }
        if(e.getSource()==btnAddUserToPool)
        {
            if(btnAddUserToPool.getText().equals("Join Pool"))
            {
                if(dbc.checkUserInPool(loggedInUsername))
                {
                    JOptionPane.showMessageDialog(null, "You are already in the pool","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    btnJoinPoolText = "Leave Pool";
                    btnAddUserToPool.setText(btnJoinPoolText);
                }
                else if(dbc.addUserToPool(loggedInUsername))
                {                    
                    btnJoinPoolText = "Leave Pool";
                    btnAddUserToPool.setText(btnJoinPoolText);
                    updatePoolPanel();
                    waitingInPool = true;
                }
            }
            else
            {
                if(dbc.checkUserInPool(loggedInUsername))
                {
                    if(dbc.userAvailable(loggedInUsername))
                    {
                        dbc.removeUserFromPool(loggedInUsername);
                        btnJoinPoolText = "Join Pool";
                        btnAddUserToPool.setText(btnJoinPoolText);
                        updatePoolPanel();
                        waitingInPool = false;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "You are not in the pool","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You are not in the pool","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    btnJoinPoolText = "Join Pool";
                    btnAddUserToPool.setText(btnJoinPoolText);
                    updatePoolPanel();
                }
                btnAddUserToPool.setText("Join Pool");
            }
            
            
        }        
            
        if(e.getSource()==btnSubmitAnswer)
        {
            String answerText = "";
            if(rbc1.isSelected())
            {
                answerText = rbc1.getText();
            }
            else if(rbc2.isSelected())
            {
                answerText = rbc2.getText();
            }
            else if(rbc3.isSelected())
            {
                answerText = rbc3.getText();
            }
            else if(rbc4.isSelected())
            {
                answerText = rbc4.getText();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Oops, you didn't select an answer","AHK - Pool",JOptionPane.ERROR_MESSAGE);
            }
            
            boolean answerCorrect = dbc.submitAnswer(imageID.substring(0, imageID.length() - 4),answerText);
            dbc.updateAnswer(sessionID, loggedInUsername,answerCorrect);
            
            lblQuest2.setText(dbc.getCurrentQuestionForUser(sessionID, loggedInUsername)+"");
            lblCorrect2.setText(dbc.getScoreForUser(sessionID, loggedInUsername)+"");
            //get the radio button selected
            //match the rb text to the asnwer for the question
            //add the mark if correct
            getNextQuestion();
        }
        if(e.getSource()== btnWReturn)
        {
            jfW.dispose();
            scoreboardOpen = false;
            imTheJoiningUser = false;
            
            gamePoolEnable(true);
            gameTimeEnable(false);
        }
    }
 
    /*public void AmortizationLayout() 
    {
        JFrame jf = new JFrame();
        JPanel gui = new JPanel(new BorderLayout(2,2));
        JPanel labelFields = new JPanel(new BorderLayout(2,2));
        labelFields.setBorder(new TitledBorder("BorderLayout"));

        JPanel labels = new JPanel(new GridLayout(0,1,1,1));
        labels.setBorder(new TitledBorder("GridLayout"));
        JPanel fields = new JPanel(new GridLayout(0,1,1,1));
        fields.setBorder(new TitledBorder("GridLayout"));

        for (int ii=1; ii<4; ii++) {
            labels.add(new JLabel("Label " + ii));
            // if these were of different size, it would be necessary to
            // constrain them using another panel
            fields.add(new JTextField(10));
        }

        labelFields.add(labels, BorderLayout.CENTER);
        labelFields.add(fields, BorderLayout.EAST);

        JPanel guiCenter = new JPanel(new BorderLayout(2,2));
        guiCenter.setBorder(new TitledBorder("BorderLayout"));
        JPanel buttonConstrain = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonConstrain.setBorder(new TitledBorder("FlowLayout"));
        buttonConstrain.add( new JButton("Click Me") );
        guiCenter.add( buttonConstrain, BorderLayout.NORTH );

        guiCenter.add(new JScrollPane(new JTextArea(5,30)));

        gui.add(labelFields, BorderLayout.NORTH);
        gui.add(guiCenter, BorderLayout.CENTER);

        //JOptionPane.showMessageDialog(null, gui);
        jf.add(gui);
        jf.setVisible(true);
    }*/
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        //System.out.println("hello updater");
        try {
             AHKJava c = new AHKJava();
             c.getAllImages();
             c.getRecords();
             //c.AmortizationLayout();
             c.createAHKGui();
             System.out.println("Connection Established");
             c.gameTimeEnable(false);
             c.gamePoolEnable(false);
         }
         catch (SQLException e) {
             e.printStackTrace();
             System.err.println("Connection Failure");
         }  
       
        DBCommunicator dbc = new DBCommunicator();
        
        //System.out.println("User exists: "+dbc.usernameExists("foosh"));
        //System.out.println("User exists: "+dbc.usernameExists("ryno"));
        

        
                
    }

    
}
