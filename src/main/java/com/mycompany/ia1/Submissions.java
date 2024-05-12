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
    public Submissions(String title){//constructor
        //retrieve document byte[] from the database
        String query = "SELECT * FROM Submissions WHERE title = ?"; 
        String[] parameters ={title};
        String[] columnResults = {"document"};
        String[] extracted = Common.SQLquery(query, parameters, columnResults, -1, null);
        //now reverse the process from String[] to byte[]
        this.document = Base64.getDecoder().decode(extracted[0]);
    }
    public ImageIcon displaySubmission(int width, int height){
        int[] dimensions = {width,height};// make a int[] with the width and height
        ByteArrayInputStream bis = new ByteArrayInputStream(document);//turn document into a input stream
        try {
            //now the image can be read and dimensions found under the max {width, height} of {800,1000}
            dimensions = resizeProportionally( ImageIO.read(bis), 800, 1000) ;
        } catch (IOException ex) {
            Logger.getLogger(Submissions.class.getName()).log(Level.SEVERE, null, ex);
        }
        ImageIcon image = new ImageIcon(document);//create a new image from the document byte[]
        Image img = image.getImage();
        //resize image to the found dimensions
        Image scaledImg = img.getScaledInstance(dimensions[0], dimensions[1],Image.SCALE_SMOOTH);
        ImageIcon newImage = new ImageIcon(scaledImg);
        return newImage;//return resized image
    }
       private int[] resizeProportionally(BufferedImage img, int maxWidth, int maxHeight){
       int originalWidth = img.getWidth();
       int originalHeight = img.getHeight();
       int newWidth = originalWidth;//set new as the original in the case that no modifications are done
       int newHeight = originalHeight;//set new as the original in the case that no modifications are done
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
        return new int[]{newWidth, newHeight};//return new dimensions int[]
    }
}
