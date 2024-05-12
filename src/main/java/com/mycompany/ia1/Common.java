/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ia1;



import java.awt.image.BufferedImage;

import java.util.*;
import java.sql.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Common {
    public static String[] tiedTitles={"",""};
    public static String contestID;
    public static String currentUser = "undefined";
    
    public static void updateStatus(){
        //extract names of contest in the database
        String query = "SELECT * FROM Contests";
        String[] columnResults = {"name"};
        String[] empty = {};
        String[] names = Common.SQLquery(query, empty, columnResults, -1,null);
        for(int i = 0; i<names.length;i++){//cycle through all contests
            //extract the dates for the contest the loop is currently on
            query = "SELECT * FROM Contests where name = ?";
            String[] parameters = {names[i]};
            String[] columnResults2 = {"status", "startForum", "startVoting","endVoting","id"};
            String[] data = Common.SQLquery(query, parameters, columnResults2,  -1,null);
            String status = data[0];
            LocalDate startForum = turnLocalDate(data[1]);
            LocalDate startVoting = turnLocalDate(data[2]);
            LocalDate endVoting = turnLocalDate(data[3]);
            LocalDate currentDate = LocalDate.now();
            contestID = (data[4]);
            String updatedStatus = "";
            switch (status) {
                case "submission" -> {
                    if(!currentDate.isBefore(startForum)){//not is after because I want true when they're equal
                        updatedStatus = "forum"; //possible bug here if the date passes and then it does not update
                    }
                }
                case "forum" -> {
                    if(!currentDate.isBefore(startVoting)){//not is after because I want true when they're equal
                        //here the titles of the reports (titles of reported submissions) that have been voted out are extracted
                        query = "SELECT * FROM ForumReports WHERE pass = 'no'AND contestID = ?";
                        String[] columnToDelete = {"titleReport"};
                        String[] parameter = {contestID};
                        String[] namesToDelete = Common.SQLquery(query, parameter, columnToDelete, -1, null);
                        updatedStatus = "voting"; //possible bug here if the date passes and then it does not update
                        //loop through the titles of submissions to be deleted
                        for (String nameToDelete : namesToDelete) {
                            //retrieve the username of users who submitted a submission to be deleted
                            query = "SELECT * FROM Submissions WHERE title = ? AND contestID = ?";
                            String[] parameterss = {nameToDelete, contestID};
                            String[] columnResultss = {"username"};
                            String[] usernameToDelete = Common.SQLquery(query, parameterss, columnResultss, -1,null);
                            //use the username to find the email
                            query = "SELECT * FROM TolkienSociety WHERE username = ?";
                            String[] parameterss2 = {usernameToDelete[0]};
                            String[] columnResultss2 = {"email"};
                            //send an email notifying the user that their submission was deleted
                            String[] sendTo = Common.SQLquery(query, parameterss2, columnResultss2, -1,null);
                            String to = sendTo[0]; 
                            String text = "Your submission "+ nameToDelete + "has been deleted from the competition for breaching the rules of the contest."
                                    + " This was decided in a democratic process that occured during the forum period of the contest you submitted to";
                            String subject = "Information about your submission to "+contestID;
                            Common.sendEmail(to, text, subject);
                            //delete the submission from the table
                            query = "DELETE FROM Submissions WHERE title=? AND contestID=?";
                            String[] parameters3 = {nameToDelete, contestID};
                            System.out.println("deleted all this"+ nameToDelete);
                            Common.SQLquery(query, parameters3 ,-1, null);
                        }
                        //retrieve all submissions from the contest
                        query = "SELECT * FROM Submissions WHERE contestID = ?";
                        String[] parameters4 = {contestID};
                        String[] columns = {"contestID","title","username"};
                        String[] retrieved = Common.SQLquery(query, parameters4, columns,-1,null);
                        //insert the submission names and users who created them into a new table that will store the votes each submission receives
                        for(int j = 0; j<retrieved.length;j+=3){
                            query = "INSERT INTO VotesperSubmission (contestID,titleSubmission,userSubmitted,votes) VALUES (?,?,?,?)";
                            String[] parameters5 = {retrieved[j],retrieved[j+1],retrieved[j],"0"};
                            Common.SQLquery(query, parameters5 ,-1, null);
                        }
                    }
                }
                case "voting" -> {
                    if(!currentDate.isBefore(endVoting)){//not is after because I want true when they're equal
                        //retrieve the submissions ordered by the number of votes received
                        query = "SELECT * FROM VotesperSubmission WHERE contestID = ?  ORDER BY CONVERT(votes, UNSIGNED)";
                        String[] columnResult = {"votes", "titleSubmission"};
                        String[] parameters1 = {contestID};
                        String[] winners = Common.SQLquery(query, parameters1, columnResult,  -1, null);
                        //if the first and second are tied
                        if(winners[0].equals(winners[2])){
                            updatedStatus = "emergency";//possible bug here if the date passes and then it does not update
                            tiedTitles[0] = winners[1];//store the titles of the submissions
                            tiedTitles[1] = winners[3];
                            //send an email to the president
                            String from = "tolkiensocietyvoting@gmail.com";
                            String password = "kbzmnzeygouygmcj";
                            //find president's email
                            query = "SELECT * FROM TolkienSociety WHERE username = ?";
                            String[] parameterss2 = {"president"};
                            String[] columnResultss2 = {"email"};
                            String[] sendTo = Common.SQLquery(query, parameterss2, columnResultss2, -1,null);
                            String to = sendTo[0];
                            String text = "Please open the Tolkien Society app immediately, there is a contest with a tie that you need to break";
                            String subject = "Tie break for first place!";
                            Common.sendEmail(to, text, subject);
                        }
                        //if the second and third are tied
                        else if(winners[2].equals(winners[4])){
                            updatedStatus = "emergency";//possible bug here if the date passes and then it does not updated
                            tiedTitles[0] = winners[3];//store the titles of the submissions
                            tiedTitles[1] = winners[5];
                            //send an email to the president and get his email address
                            query = "SELECT * FROM TolkienSociety WHERE username = ?";
                            String[] parameterss2 = {"president"};
                            String[] columnResultss2 = {"email"};
                            String[] sendTo = Common.SQLquery(query, parameterss2, columnResultss2, -1,null);
                            String to = sendTo[0];
                            String text = "Please open the Tolkien Society app immediately, there is a contest with a tie that you need to break";
                            String subject = "Tie break for second place!";
                            Common.sendEmail(to, text, subject);
                        
                        }
                        else{//if there is no ties the contest is finished
                            updatedStatus = "finished"; //possible bug here if the date passes and then it does not update
                        }

                    }
                }
                default -> {
                }
            }
            if(!updatedStatus.equals("")){//if the updatedStatus is different from its original value of ""
                //update the contest's status in the database
                query = "UPDATE Contests SET status = ? WHERE name = ? ";
                String[] parameters2 = {updatedStatus, names[i]};
                Common.SQLquery(query, parameters2, -1, null);
            }
            
        }
    }
    
    public static boolean isPresident(){
        if(currentUser.equals("president")){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean sendEmail(String to, String text, String subject){
        String from = "tolkiensocietyvoting@gmail.com";
        String password = "kbzmnzeygouygmcj";
        //not the actual password for the gmail this is a App/device specific password
        
        //setting the properties of the port and password authentication
        //it is assummed that the the host will have connection to the internet
        Session session;
        MimeMessage message;
        Properties props;
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.setProperty("mail.smtp.starttls.enable", "true"); 
        props.setProperty("mail.smtp.port", "587"); 
        props.setProperty("mail.smtp.user",from); 
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
            //sending email
            Transport transport;
            transport = session.getTransport("smtp");
            transport.connect(to, password);//connect to server
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));//send
            transport.close();//close connection
        } catch (MessagingException ex) {
            Logger.getLogger(Common.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;//to indicate the sending has been succesful
    }

    public static LocalDate turnLocalDate(String date){
        String[] subDate = date.split("/");
        int day = Integer.parseInt(subDate[0]);
        int month = Integer.parseInt(subDate[1]);
        int year = Integer.parseInt(subDate[2]);
        return(LocalDate.of(year, month, day));
    }

    public static String[] SQLquery(String query, String[] parameters, String[] columnResults, int bytesPosition, byte[] Byte){
        PreparedStatement ps;
        ArrayList<String>  extracted = new ArrayList<>(10);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //open connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql","root", "Secure1$");
            ps = conn.prepareStatement(query);//prepare statement
            for (int i = 1; i<=parameters.length;i++){
                if(i == bytesPosition){//the byte position is neeeded as a parameter because the ps.setDataType is different for each type
                    ps.setBytes(i,Byte);//also it is necesssary to pass Byte separately because the parameters array stores strings
                }
                else{
                    ps.setString(i,parameters[i-1]);//set the rest as String
                } 
            }
                ResultSet rs = ps.executeQuery();//execute the query
                while (rs.next()) {//while not all results have been extracted
                    for (String columnResult : columnResults) {
                        //these are the only two columns that store bytes so it is essentially checking if the rs is a byte
                        if("salt".equals(columnResult)|| "document".equals(columnResult)){
                            byte[] Bytes = rs.getBytes(columnResult);//extract byte[]
                            extracted.add(Base64.getEncoder().encodeToString(Bytes));//encode it into a String
                        }
                        else{//if it is not a byte it has to be a string because there is no other type in the database, except for id in Contests
                            extracted.add(rs.getString(columnResult));
                        }
                    }
                }//returning as a String[] is better than abstract data types because it allows the code to access the extracted data with indexes
                String[] extractedArray = extracted.toArray(String[]::new);
                return(extractedArray);//returns String[]
            }catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
            }
        return null;//in the case of no results that matched the query null is returned
    }
    
    public static void SQLquery(String query, String[] parameters, int bytesPosition, byte[] Byte){
        PreparedStatement ps;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //open connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql","root", "Secure1$");
            ps = conn.prepareStatement(query);//prepare statement
            for (int i = 1; i<=parameters.length;i++){
                if(i == bytesPosition){//the byte position is neeeded as a parameter because the ps.setDataType is different for each type
                    ps.setBytes(i,Byte);//also it is necesssary to pass Byte separately because the parameters array stores strings
                }
                else{
                    ps.setString(i,parameters[i-1]);//set the rest as String
                }
            }
            ps.executeUpdate();//here it is executeUpdate and not query as there is no result set
            }catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
            }//no return as it a void class
    }
    
    public static String hashPassword(String password, byte[] salt){
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);//apply generated salt to SHA-256
            byte[] bytes = md.digest(password.getBytes());//hash the bytes
            //Instead of storing it as a byte the hash is made into a string so that later on it is easier to compare hashes
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return hash;//return the hashed password as a String
    }
    
}
