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
import javax.sql.DataSource;
import java.sql.*;
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
    
    private JButton btnSignIn,btnRegister;
    JLabel lblLogin, lblPW;
    JTextField txtLogin, txtPW;
    
    JFrame jf;
    JPanel panelMain, panelHeading, panelLogo, panelLogin;
    
    JPanel panelPool,panelPoolN,panelPoolS;
    
    public AHKJava() throws SQLException
    {
        makeConnection();
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
         txtPW = new JTextField(10);
         btnSignIn = new JButton("Sign in");
         btnSignIn.addActionListener(this);
         btnRegister = new JButton("Register");
         btnRegister.addActionListener(this);
         
         //panelLogin.add(lblTitle);
         panelLogin.add(lblLogin);
         panelLogin.add(lblPW);
         panelLogin.add(txtLogin);
         panelLogin.add(txtPW);
         panelLogin.add(btnSignIn);
         panelLogin.add(btnRegister);
         panelHeading.add(panelLogo,BorderLayout.WEST);
         panelHeading.add(panelLogin,BorderLayout.EAST);
         
         //--PANEL POOL
         
         
         panelPool = new JPanel(new BorderLayout(2,2));
         panelPool.setBorder(new TitledBorder("Game Pool"));
         panelPoolN = new JPanel();
         panelPoolS = new JPanel();
         
         
         
         JLabel lblUser[], lblScore[];
         JButton btnJoin[];
         
         lblUser = new JLabel[30];
         lblScore = new JLabel[30];
         btnJoin = new JButton[30];
         
         for(int i=0;i<lblUser.length;i++)
         {
             lblUser[i] = new JLabel("opponent");
             lblScore[i] = new JLabel("user");
             btnJoin[i] = new JButton("Join");
             btnJoin[i].addActionListener(this);
         }
         //get the amount of users in pool, then print those, and print empty labels for the rest (10rows) to display nicely.
         JPanel panel = new JPanel(new GridLayout(lblUser.length,3) );
         for (int i = 0; i < lblUser.length; i++) 
         {
            panel.add(lblUser[i]);
            panel.add(lblScore[i]);
            panel.add(btnJoin[i]);
         }
         JScrollPane scrollPane = new JScrollPane(panel);
         scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         scrollPane.setBounds(10, 10, 400, 200);
         JPanel contentPane = new JPanel(null);
         contentPane.setPreferredSize(new Dimension(450, 300));
         contentPane.add(scrollPane);
         panelPoolN.add(contentPane);
         
         panelPool.add(panelPoolN,BorderLayout.NORTH);
         //END OF PANEL POOL
         
         //GAME PANEL
         JPanel panelGame, panelGameW, panelGameE,panelGameS,panelGameN;
         JRadioButton rbc1, rbc2, rbc3, rbc4;
         ButtonGroup rbGroup;
         
         panelGame = new JPanel(new BorderLayout(2,2));
         panelGame.setBorder(new TitledBorder("Game Time"));
         panelGameW = new JPanel();
         panelGameE = new JPanel(new GridLayout(4,1));
         panelGameS = new JPanel();
         panelGameN = new JPanel(new BorderLayout(1,1));
         
         DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(100, 50, 0, 250);
         JProgressBar pbGame = new JProgressBar(0,100);
         pbGame.setStringPainted(true);
         pbGame.setValue(50);
         pbGame.setString("30 Seconds remaining");    
         panelGameN.add(pbGame);
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("tablemountain.jpg"));
            ImageIcon image = new ImageIcon(bi); 
            JLabel l1 = new JLabel(image);
            panelGameW.add(l1 );
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
         
         JLabel lblQuest1, lblQuest2, lblCorrect1, lblCorrect2;
         lblQuest1 = new JLabel("Question ");
         lblQuest2 = new JLabel("1");
         lblCorrect1 = new JLabel("Correct");
         lblCorrect2 = new JLabel("0");
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
     public void actionPerformed(ActionEvent e)
    {
        //Execute when button is pressed
        if(e.getSource()==btnSignIn)
        {
            System.out.println("You clicked the button btnSignIn");
        }
        if(e.getSource()==btnRegister)
        {
            System.out.println("You clicked the button btnRegister");
        }
    }
 
    public void AmortizationLayout() 
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
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
             AHKJava c = new AHKJava();
             c.getRecords();
             c.AmortizationLayout();
             c.createAHKGui();
             System.out.println("Connection Established");
         }
         catch (SQLException e) {
             e.printStackTrace();
             System.err.println("Connection Failure");
         }  
       
    }

    
}
