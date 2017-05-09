/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postilion_error_checker;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author kithinjid
 */
public class checker_guiController implements Initializable {
    
  static Logger log;
  static Handler file_handler;
  static Connection conn=null;
  static Statement st=null;
  static ResultSet rs=null;
  static int i = 0;
   int counter=0;
  static boolean checker=true;
   String number; 
   String Sink_node; 
   String Rsp_code;
   int Respose_code;
  
  
  
    @FXML
    private  TextArea TA;
    @FXML
    private Label label;
    @FXML
    private TextField get_NO;
    @FXML
    private TextArea preview;
    @FXML
    private Button run;
    @FXML
    private Button stop;
    @FXML
    private ComboBox item;
    @FXML
    private ComboBox item2;
            
    
    @FXML
   private void handleRUNAction(ActionEvent event) 
    {
         scheduled();
         
        System.out.println("execution started");
        String out="execution started";
        TA.appendText(out+ "\n");
    } 
    @FXML
    private void handleButtonAction(ActionEvent event) 
    {
         number=get_NO.getText();
         Sink_node=item.getSelectionModel().getSelectedItem().toString();
         Rsp_code=item2.getSelectionModel().getSelectedItem().toString();
         Respose_code=Integer.parseInt(Rsp_code.substring(0, 1));
         preview.appendText("RECEIVER:"+number + "\n");
         preview.appendText("SINK NODE:"+Sink_node + "\n");
         preview.appendText("RESPONSE CODE:"+Rsp_code + "\n");
         preview.appendText("=========================\n");
         get_NO.clear();
    }
    @FXML
    private void handlestopAction(ActionEvent event)
    {
        dbclose();
        System.exit(0);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
       
    } 
       private  void dbconnect()
    {
        String username="Postilion";
        String password="Password12";
       
        String hostname="jdbc:sqlserver://172.16.112.6;databaseName=realtime";
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn=DriverManager.getConnection(hostname,username,password);
            System.out.println("TIME:"+dateString("MM:dd-HH:mm:ss")+":connected to database");
            String out_data="TIME:"+dateString("MM:dd-HH:mm:ss")+":connected to database";
            TA.appendText(out_data+ "\n");
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
      }
       
       private  ResultSet check_00(String query1)
    {
        dbconnect();     
       
        try
        {
        st=conn.createStatement();
        rs=st.executeQuery(query1);
        }catch(Exception e)
        {
        e.printStackTrace();
        }
       
  
        return rs ;
    }
       public void dbclose()
       {
           
           counter=0;
      try {
          conn.close();
          st.close();
      } catch (SQLException ex) {
          Logger.getLogger(checker_guiController.class.getName()).log(Level.SEVERE, null, ex);
      }
   
       }
  public void scheduled()
    {
        
        
    Timer timer=new Timer();
    timer.schedule(new TimerTask() {

    @Override
   public void run() 
   {
    while(checker==true)
    {
//    log=logger.getLogger("my logger");
//    try {
//    file_handler=new FileHandler("C:\\Users\\kithinjid\\Google Drive\\interswitch\\NetBeansProjects\\POSTILION_ERROR CHECKER\\dist\\logg.txt");
//    SimpleFormatter formatter=new SimpleFormatter();
//    file_handler.setFormatter(formatter);
//    log.addHandler(file_handler);
//    } catch (IOException ex) {
//    log.info(dateString("HHmmss"));
//    Logger.getLogger(POSTILION_ERROR_CHECKER.class.getName()).log(Level.SEVERE, null, ex);
//    } catch (SecurityException ex)
//    {
//    Logger.getLogger(POSTILION_ERROR_CHECKER.class.getName()).log(Level.SEVERE, null, ex);
////    }
    String query="SELECT TOP 10 [tran_nr]\n" +
"      ,[gmt_date_time]\n" +
"      ,[time_local]\n" +
"      ,[date_local]\n" +
"      ,[sink_node]\n" +
"      ,[rsp_code_req_rsp]\n" +
"      ,[rsp_code_cmp]\n" +
"      ,[rsp_code_rev]\n" +
"  FROM [realtime].[dbo].[tm_trans_10]where sink_node ='"+Sink_node+"' ORDER BY tran_nr desc";
       dbconnect();
       
       
    try
    {
      ResultSet result=check_00(query);
      
       while(result.next()==true)
       {
          //loop for counter and displaying
      int response=Integer.parseInt(result.getString("rsp_code_req_rsp"));
     System.out.println(response);
     TA.appendText(response+ "\n");
     if (response==0)
     {
     counter++;
    if (counter==5)
    {
    System.out.println("TIME:"+dateString("MM:dd-HH:mm:ss")+"--error respose code:91 of count:"+ counter+" to sink node:"+Sink_node);
    String output="TIME:"+dateString("MM:dd-HH:mm:ss")+"--error respose code:91 of count:"+ counter+" to sink node:"+Sink_node;
    TA.appendText(output);
    send_sms(number,output);
    }
     }

       }
         dbclose();
         delay();
         counter=0;
       }catch(Exception e)
       {
           e.printStackTrace();
       }
                       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                    }
   },0,30000); 
      
    }
    public static void delay()
    {
        try
        {
            Thread.sleep(5 * 60 * 1000);
        }catch(Exception e)
        {
            
        }
    }
    private static String dateString(String formart)
    {
        DateFormat dateFormat = new SimpleDateFormat(formart);
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }
     public static void send_sms(String receiver, String text)
    {
        
        String app_name="Postilion_Error_checker";
        String sender_ID="VERVE";
        String user_Name="PostilionErrorChecker";
        String pass_word="Post4334dild";
       String output=sendSMS(receiver,text, app_name, sender_ID, null,user_Name, pass_word);
       System.out.println(output);
    }
      private static String sendSMS(java.lang.String recipient, java.lang.String text, java.lang.String appName, java.lang.String senderID, java.lang.String reference, java.lang.String userName, java.lang.String password) {
        org.tempuri.SMSGatewayWs service = new org.tempuri.SMSGatewayWs();
        org.tempuri.SMSGatewayWsSoap port = service.getSMSGatewayWsSoap();
        return port.sendSMS(recipient, text, appName, senderID, reference, userName, password);
    }
}
