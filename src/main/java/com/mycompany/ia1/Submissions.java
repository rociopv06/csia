/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ia1;

import java.awt.Image;
import java.awt.image.BufferedImage;
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
    private byte[] document;
    public Submissions(String title){
        String query = "SELECT * FROM Submissions WHERE title = ?"; 
        String[] parameters ={title};
        String[] columnResults = {"document"};
        String[] extracted = Common.SQLquery(query, parameters, columnResults, -1, null);
        this.document = Base64.getDecoder().decode(extracted[0]);
    }
    public ImageIcon displaySubmission(int width, int height){
        int[] dimensions = {width,height};
        ByteArrayInputStream bis = new ByteArrayInputStream(document);
        try {
            dimensions = resizeProportionally( ImageIO.read(bis), 800, 1000) ;
        } catch (IOException ex) {
            Logger.getLogger(Submissions.class.getName()).log(Level.SEVERE, null, ex);
        }
        ImageIcon image = new ImageIcon(document);
        Image img = image.getImage();
        Image scaledImg = img.getScaledInstance(dimensions[0], dimensions[1],Image.SCALE_SMOOTH);
        ImageIcon newImage = new ImageIcon(scaledImg);
        return newImage;
    }
       private int[] resizeProportionally(BufferedImage img, int maxWidth, int maxHeight){
       int originalWidth = img.getWidth();
       int originalHeight = img.getHeight();
       int newWidth = originalWidth;
       int newHeight = originalHeight;
       double ratio = (double) originalWidth / originalHeight;
       if (originalWidth > maxWidth || originalHeight > maxHeight) {// Check if the image needs to be resized
           if (ratio > 1.0) { // this indicates a image wider than desired
               newWidth = maxWidth; //width is set to max
               newHeight = (int) (newWidth / ratio); // the height is set in relation to the width
           } else { //the image is taller than desired
               newHeight = maxHeight;// the height is set to the max
               newWidth = (int) (newHeight * ratio);// the width is set in relation to the height
           }
        }
        return new int[]{newWidth, newHeight};
    }
}
