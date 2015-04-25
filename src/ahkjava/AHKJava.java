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
    
    JButton btnSubmitAnswer;
    //variables
    
    String loggedInUsername;
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
    ScheduledExecutorService ses5 = Executors.newScheduledThreadPool(10);
    public int progressSize;
    boolean gameTimeLeft;
    boolean flagInGame;
    
    ArrayList<ArrayList<String>> poolList;
    ArrayList<String> allImagesForGame;
    ArrayList<String> questionForGameImage;
    String btnJoinPoolText ;
    String opponentUsername;
    int currentImageViewing;
    
    
    public AHKJava() throws SQLException
    {
        makeConnection();
        loggedInUsername = "";
        poolSize = 0;        
        flagInGame = false;
        btnJoinPoolText = "Join Pool";
        opponentUsername = "";
        currentImageViewing = 0;
        allImagesForGame = new ArrayList<>();
        
        
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
                    }    
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
                opponentUsername = dbc.matchFoundInPool(loggedInUsername);
                if(!opponentUsername.equals(""))
                {
                    dbc.removeUserFromPool(loggedInUsername);
                    gameTimeEnable(true);
                    gamePoolEnable(false);
                    progressSize = 60;
                    gameTimeLeft = true;
                    flagInGame = true;
                    //start game.
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
         pbGame = new JProgressBar(0,60);
         pbGame.setStringPainted(true);
         pbGame.setValue(60);
         pbGame.setString("60 Seconds remaining");    
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
         rbc2 = new JRadioButton("Choice 2");
         rbc3 = new JRadioButton("Choice 3");
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
         jf.pack();
         jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
         /*
         jfQ.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //DISPOSE_ON_CLOSE,  DISPOSE_ON_CLOSE 
        jfQ.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                int result = JOptionPane.showConfirmDialog(jfQ, "Are you sure you would like to exit?");
                if( result==JOptionPane.OK_OPTION)
                {
                    // NOW we change it to dispose on close..
                    jfQ.setDefaultCloseOperation(jfT.DISPOSE_ON_CLOSE);
                    jfQ.setVisible(false);
                    jfQ.dispose();
                    guiSelectQuantityBool = false;
                }
            }
        });
         */
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
            // System.out.println("");

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
        //System.out.println("refresh");
        //System.out.println("loggedInUsername: "+loggedInUsername + "\tflagInGame: "+flagInGame);
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
        jfR.setLocationRelativeTo(null);
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
                        System.out.println(fileItem);
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
         System.out.println("size: "+allImagesForGame.size());
         String imageID = allImagesForGame.get(currentImageViewing);
         questionForGameImage = dbc.requestQuestionForImage(imageID);
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("ahkLogo.JPG"));
            ImageIcon image = new ImageIcon(bi); 
            lblGamePic.setIcon(image);
            //panelGameW.add(lblGamePic );
         }
         catch(Exception e)
         {
             System.out.println("createAHKGui(load image): \n"+e);
         }
         
         //update label with image
         
         //update all the questions
         //set the question number
         //set the amount of correct answers
         
         //if at end of questions, set the currentImageViewing to zero
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
            String uname ="";
            if(txtRUser.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid username","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(txtRPass1.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid password","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(txtPass2.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid confirmation password","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(txtEmail.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid email","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                /*RegisterUser();String errors[]      (this is also used in login)
                -usernameExists(username);boolean
                -emailExists(email);boolean
                -passwordMatch(password1, password2);boolean
                -passwordValid(password);boolean
                -addUser(username, email, password);boolean(for successful adding)
                */
                //dbc.usernameExists()
            }
            jfR.dispose();
        }
        /*btnJoin*/
        if(poolSize>0)
        {
            
            for(int a =0;a<poolSize;a++)
            {
                System.out.println("poolSize: "+poolSize);
                if(e.getSource()==btnJoin[a])
                {
                    String userToJoin = lblUser[a].getText();
                    if(dbc.userAvailable(userToJoin))
                    {                        
                        dbc.joinUserInPool(loggedInUsername,userToJoin);
                        gameTimeEnable(true);
                        gamePoolEnable(false);
                        progressSize = 60;
                        gameTimeLeft = true;
                        flagInGame = true;
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
            System.out.println("Check question and get next");
            getNextQuestion();
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
