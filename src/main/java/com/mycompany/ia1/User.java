/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ia1;

import java.util.Base64;

/**
 *
 * @author rociopv
 */
public class User  {

    String username = "";
    String email = "";
    String password = "";
    byte[] salt = null;
    public User (String username){
        this.username = username; 
        String query = "SELECT * FROM TolkienSociety WHERE username = ?";
        String[] parameters = {username};
        String[] columnTitles = {"email","password","salt"};
        String[] extracted = Common.SQLquery(query, parameters, columnTitles,-1,null);
        this.email = extracted[0];
        this.password = extracted[1];
        this.salt =  Base64.getDecoder().decode(extracted[2]);
        
    }
    public String getUsername(){
        return username;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
    public byte[] getSalt(){
        return salt;
    }
}
