/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ia1;

import static com.mycompany.ia1.SignUp.sendTo;
import java.awt.Image;
import java.awt.image.BufferedImage;
import static java.lang.Integer.min;
import java.util.*;
import java.sql.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 *
 * @author rociopv
 */
public class common {
    public static String[] tiedTitles={"",""};
    public static String contestID;
    public static void updateStatus(){
        String query = "SELECT * FROM Contests";
        String[] columnResults = {"name"};
        String[] empty = {};
        String[] names = common.SQLquery(query, empty, columnResults, false, -1,null);
       
        for(int i = 0; i<names.length;i++){
            query = "SELECT * FROM Contests where name = ?";
            String[] parameters = {names[i]};
            String[] columnResults2 = {"status", "startForum", "startVoting","endVoting","id"};
            String[] data = common.SQLquery(query, parameters, columnResults2, false, -1,null);
            String status = data[0];
            LocalDate startForum = turnLocalDate(data[1]);
            LocalDate startVoting = turnLocalDate(data[2]);
            LocalDate endVoting = turnLocalDate(data[3]);
            LocalDate currentDate = LocalDate.now();
            contestID = (data[4]);
            String updatedStatus = "";
            if(status.equals("submission")){
                if(!currentDate.isBefore(startForum)){//not is after because I want true when they're equal
                    updatedStatus = "forum"; //possible bug here if the date passes and then it does not update
                }
            }
            else if(status.equals("forum")){
                if(!currentDate.isBefore(startVoting)){//not is after because I want true when they're equal
                    query = "SELECT * FROM ForumReports WHERE pass = 'no'AND contestID = ?";
                    String[] columnToDelete = {"titleReport"};
                    String[] parameter = {common.contestID};
                    
                    String[] namesToDelete = common.SQLquery(query, parameter, columnToDelete, false, -1, null);
                    System.out.println("names to delete"+ namesToDelete[0]);
                    updatedStatus = "voting"; //possible bug here if the date passes and then it does not update
                    for (String nameToDelete : namesToDelete) {
                       
                        query = "SELECT * FROM Submissions WHERE title = ? AND contestID = ?";
                        String[] answers = null;
                        String[] parameterss = {nameToDelete, contestID};
                        String[] columnResultss = {"username"};
                        String[] usernameToDelete = common.SQLquery(query, parameterss, columnResultss, false, -1,null);
                        System.out.println(nameToDelete);
                        System.out.println(contestID);
                        String from = "tolkiensocietyvoting@gmail.com";
                        String password = "kbzmnzeygouygmcj";
                        query = "SELECT * FROM TolkienSociety WHERE username = ?";
                        String[] parameterss2 = {usernameToDelete[0]};
                        String[] columnResultss2 = {"email"};
                        String[] sendTo = common.SQLquery(query, parameterss2, columnResultss2, false, -1,null);
                        String to = sendTo[0];

   
                        String text = "Your submission "+ nameToDelete + "has been deleted from the competition for breaching the rules of the contest."
                                + " This was decided in ademocratic process that occured during the forum period of the contest you submitted to";
                        String subject = "Information about your submission to "+contestID;
                        common.sendEmail(from, password, to, text, subject);
                        query = "DELETE FROM Submissions WHERE title=? AND contestID=?";
                        String[] parameters3 = {nameToDelete, contestID};
                        System.out.println("deleted all this"+ nameToDelete);
                        common.SQLquery(query, parameters3, null, true, -1, null);
                        query = "SELECT * FROM Submissions WHERE contestID = ?";
                        String[] parameters4 = {contestID};
                        String[] columns = {"contestID","title","username"};
                        String[] retrieved = common.SQLquery(query, parameters4, columns, false,-1,null);
                        query = "INSERT INTO VotesperSubmission (contestID,titleSubmission,userSubmitted,votes) VALUES (?,?,?,?)";
                        String[] parameters5 = {retrieved[0],retrieved[1],retrieved[2],"0"};
                        common.SQLquery(query, parameters5, null, true, -1, null);
                    }
                }
                
            }
            else if(status.equals("voting")){
                if(!currentDate.isBefore(endVoting)){//not is after because I want true when they're equal
                    query = "SELECT * FROM VotesperSubmission WHERE contestID = ?  ORDER BY CONVERT(votes, UNSIGNED)";
                    String[] columnResult = {"votes"};
                    String[] parameters1 = {contestID};
                    String[] winners = common.SQLquery(query, parameters1, columnResult, false, -1, null);
                    if(winners[0].equals(winners[2])){
                        updatedStatus = "emergency";//possible bug here if the date passes and then it does not update
                        tiedTitles[0] = winners[1];
                        tiedTitles[1] = winners[3];
                        String from = "tolkiensocietyvoting@gmail.com";
                        String password = "kbzmnzeygouygmcj";
                        query = "SELECT * FROM TolkienSociety WHERE username = ?";
                        String[] parameterss2 = {"president"};
                        String[] columnResultss2 = {"email"};
                        String[] sendTo = common.SQLquery(query, parameterss2, columnResultss2, false, -1,null);
                        String to = sendTo[0];

   
                        String text = "Please open the Tolkien Society app immeadiately, there is a contest with a tie that you need to break";
                        String subject = "Tie break for first place!";
                        common.sendEmail(from, password, to, text, subject);
                    }
                    else if(winners[2].equals(winners[4])){
                        updatedStatus = "emergency";//possible bug here if the date passes and then it does not update
                        String from = "tolkiensocietyvoting@gmail.com";
                        String password = "kbzmnzeygouygmcj";
                        query = "SELECT * FROM TolkienSociety WHERE username = ?";
                        String[] parameterss2 = {"president"};
                        String[] columnResultss2 = {"email"};
                        String[] sendTo = common.SQLquery(query, parameterss2, columnResultss2, false, -1,null);
                        String to = sendTo[0];

   
                        String text = "Please open the Tolkien Society app immeadiately, there is a contest with a tie that you need to break";
                        String subject = "Tie break for second place!";
                        common.sendEmail(from, password, to, text, subject);
                        tiedTitles[0] = winners[3];
                        tiedTitles[1] = winners[5];
                    }
                    else{
                        updatedStatus = "finished"; //possible bug here if the date passes and then it does not update
                    }
                    /*
                    query = "SELECT * FROM VotesperSubmission WHERE contestID = "+contestID;
                    String[] column = {"votes"};
                    String[] stringVotes = common.SQLquery(query, null, column, false, -1, null);
                    int[] votes = new int[stringVotes.length];
                    for(int j = 0; j<stringVotes.length; j++){
                        votes[j] = Integer.parseInt(stringVotes[j]);
                    }
                    Arrays.sort(votes);
                    String secondVotes= Integer.toString(votes[votes.length-2]);
                    String thirdVotes= Integer.toString(votes[votes.length-3]);
                    query = "SELECT * FROM VotesperSubmission WHERE votes =" + Integer.toString(votes[votes.length-1]);
                    String[] column2 = {"userSubmitted"};
                    String[] firstUser = */
                    
                }
                
            }
            if(!updatedStatus.equals("")){
                query = "UPDATE Contests SET status = ? WHERE name = ? ";
                String[] parameters2 = {updatedStatus, names[i]};
                common.SQLquery(query, parameters2, null, true, -1, null);
            }
            
        }
    }
    public static String currentUser = "undefined";
    public static boolean isPresident(){
        if(currentUser.equals("president")){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean sendEmail(String from, String password, String to, String text, String subject){
        Session session;
        MimeMessage message;
        Properties props;
        props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.setProperty("mail.smtp.starttls.enable", "true"); 
        props.setProperty("mail.smtp.port", "587"); 
        props.setProperty("mall.smtp.user",from); 
        props.setProperty("mail.smtp.ssl.protocols","TLSv1.2"); 
        props.setProperty("mail.smtp.auth", "true");

        session = Session.getDefaultInstance(props,new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(from, password);
            }
        });   

        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setReplyTo( InternetAddress.parse( from ) );
            message.setRecipient(Message.RecipientType.TO, new InternetAddress (to));
            message.setSubject(subject);
            message.setText(text);
            Transport transport;
            transport = session.getTransport("smtp");
            transport.connect(to, password);
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
                
        } catch (AddressException ex ) {
            //Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (MessagingException ex) {
           // Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    public static int[] reziseProportionally(BufferedImage img, int maxWidth, int maxHeight){
       int originalWidth = img.getWidth();
       int originalHeight = img.getHeight();
       int newWidth = originalWidth;
       int newHeight = originalHeight;

    
       double ratio = (double) originalWidth / originalHeight;

       // Check if the image needs to be resized
       if (originalWidth > maxWidth || originalHeight > maxHeight) {
           if (ratio > 1.0) {
               
               newWidth = maxWidth;
               newHeight = (int) (newWidth / ratio);
           } else {
               newHeight = maxHeight;
               newWidth = (int) (newHeight * ratio);
           }
        }

        return new int[]{newWidth, newHeight};

    }
    public static LocalDate turnLocalDate(String date){
        String[] subDate = date.split("/");

        int day = Integer.parseInt(subDate[0]);
        int month = Integer.parseInt(subDate[1]);
        int year = Integer.parseInt(subDate[2]);

        return(LocalDate.of(year, month, day));
    }
    public static boolean validDates(String first,String second,String third, String fourth){
        LocalDate currentDate = LocalDate.now();
        LocalDate First = turnLocalDate(first);
        LocalDate Second = turnLocalDate(second);
        LocalDate Third = turnLocalDate(third);
        LocalDate Fourth = turnLocalDate(fourth);
        return (currentDate.isBefore(First) || currentDate.isEqual(First))&& First.isBefore(Second) && Second.isBefore(Third)
                && Third.isBefore(Fourth);
        
        
    }
    public static String[] SQLquery(String query, String[] parameters, String[] columnResults, boolean Update, int bytesPosition, byte[] Byte){
        System.out.println("begin sql");
        PreparedStatement ps;
        ArrayList<String>  extracted = new ArrayList<>(10);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql","root", "Secure1$");
            ps = conn.prepareStatement(query);
            for (int i = 1; i<=parameters.length;i++){
                if(i == bytesPosition){
                    System.out.println("is it salt?");
                    ps.setBytes(i,Byte);
                }
                
                else{
                    System.out.println("is it the other characters?");
                    ps.setString(i,parameters[i-1]);
                }
                
                
            }
            if(!Update){
                System.out.println("is it rs?");
                ResultSet rs = ps.executeQuery();
                //int j = 0;
                while (rs.next()) {
                    for (String columnResult : columnResults) {
                        
                        if("salt".equals(columnResult)|| "document".equals(columnResult)){
                            byte[] Bytes = rs.getBytes(columnResult);
                            //extracted.set(j,Base64.getEncoder().encodeToString(Bytes));
                            extracted.add(Base64.getEncoder().encodeToString(Bytes));
                            System.out.println("salt was extracted");
                            System.out.println("original salt: "+ Arrays.toString(rs.getBytes(columnResult)));
                        }
                        else{
                            //extracted.set(j,rs.getString(columnResult)) ;
                            extracted.add(rs.getString(columnResult));
                            System.out.println("other column was extracted");
                        }
                        //j++;
                    }

                }
                System.out.println("return");
                String[] extractedArray = extracted.toArray(String[]::new);
                return(extractedArray);
            }
            else{
                ps.executeUpdate();
                System.out.print("updated");
            }
            
            
            
            }catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        return null;
    }
    
    
    public static String hashPassword(String password, byte[] salt){
        
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));//???
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return hash;
    }
    
}
