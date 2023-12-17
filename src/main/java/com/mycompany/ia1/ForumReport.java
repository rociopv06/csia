/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ia1;

/**
 *
 * @author rociopv
 */
public class ForumReport {
    private String title;
    private String body;
    private String userSubmitted;
    private String userVoted;
    private String votesKeep;
    private String votesDelete;
    private String pass;
    public ForumReport(){
    }
    public void setForumReport(String title){
        String query = "SELECT * FROM ForumReports WHERE titleReport = ?"; 
        String[] parameters = {title};
        String[] columnResults = {"bodyReport","userSubmitted","userVoted", "votesKeep", "votesDelete", "pass"};   
        String[] extracted = Common.SQLquery(query, parameters, columnResults, -1, null);
        this.body = extracted[0];
        this.userSubmitted = extracted[1];
        this.userVoted = extracted[2];
        this.votesKeep= extracted[3];
        this.votesDelete = extracted[4];
        this.pass = extracted[5]; 
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    public String getBody(){
        setForumReport(title);
        return body;
    }
    public String getUserSubmitted(){
        setForumReport(title);
        return userSubmitted;
    }
    public String getUserVoted(){
        setForumReport(title);
        return userVoted;
    }
    public String getVotesKeep(){
        setForumReport(title);
        return votesKeep;
    }
    public String getVotesDelete(){
        setForumReport(title);
        return votesDelete;
        
    }
    public String getPass(){
        setForumReport(title);
        return pass;
    }
    public boolean setVoteKeep(){
        if(userVoted.contains(Common.currentUser)){
            return false;
        }
        else {
            int votes = Integer.parseInt(getVotesKeep());
            votes= votes+1;
            votesKeep = Integer.toString(votes);
            String query = "UPDATE ForumReports SET votesKeep = ?, userVoted = CONCAT(COALESCE(userVoted, ''), ?) WHERE titleReport = ?";
            String[] parameters = {votesKeep,Common.currentUser, title};
            Common.SQLquery(query, parameters,  -1,null);
            return true;
        
        }
        
    }
    public boolean setVoteDelete(){
        if(userVoted.contains(Common.currentUser)){
            return false;
        }
        else {
            int votess = Integer.parseInt(getVotesDelete());
            votess= votess+1;
            votesDelete = Integer.toString(votess);
            String query = "UPDATE ForumReports SET votesDelete = ?, userVoted = CONCAT(COALESCE(userVoted, ''), ?) WHERE titleReport = ?";
            String[] parameters = {votesDelete,Common.currentUser, title};
            Common.SQLquery(query, parameters, -1,null);
            return true;
        
        }
       
    }
    public void setPass(){
        if (Integer.parseInt(votesKeep)<Integer.parseInt(votesDelete)){
            pass = "no";
        }
        else{
            pass = "yes";
        }
        String query = "UPDATE ForumReports SET  pass=?  WHERE titleReport = ?";
        String[] parameters = {pass,title};
        Common.SQLquery(query, parameters, -1,null);
    }
}
