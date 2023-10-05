/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ia1;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author rociopv
 */
public class Submissions {
    private String contestID;
    private String userSubmitted;
    private byte[] document;
    private String title;
    public Submissions(String title){
        this.title =  title;
        String query = "SELECT * FROM Submissions WHERE title = ?"; 
        String[] parameters ={title};
        String[] columnResults = {"userSubmitted", "document","title"};
        String[] extracted = Common.SQLquery(query, parameters, columnResults, -1, null);
        this.userSubmitted = extracted[0];
        this.document = Base64.getDecoder().decode(extracted[1]);
        this.title = extracted[2];
    }
    public ImageIcon displaySubmission(int width, int height){
        int[] dimensions = {width,height};
        ByteArrayInputStream bis = new ByteArrayInputStream(document);
        try {
            dimensions = Common.reziseProportionally( ImageIO.read(bis), 800, 1000) ;
        } catch (IOException ex) {
            Logger.getLogger(contestForum.class.getName()).log(Level.SEVERE, null, ex);
        }
        ImageIcon image = new ImageIcon(document);
        Image img = image.getImage();
        System.out.println("dimensions" + dimensions[0]+ dimensions[1]);
        Image scaledImg = img.getScaledInstance(dimensions[0], dimensions[1],Image.SCALE_SMOOTH);
        ImageIcon newImage = new ImageIcon(scaledImg);
        return newImage;
    }
}
