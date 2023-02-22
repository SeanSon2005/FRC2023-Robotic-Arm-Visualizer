package robot;

import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import robot.Constants.ArmConstants;

import java.awt.event.*;

public class Simulate extends JFrame implements MouseListener, MouseMotionListener{
    private JPanel panel;
    private double x_coordinate;
    private double y_coordinate;
    private double x_click;
    private double y_click;
    private boolean flipped = false;
    private final int CENTER_X = 500;
    private final int CENTER_Y = 700;
    private final int HEIGHT = (int)(CENTER_Y-convertInchestoPixel(ArmConstants.ORIGIN_HEIGHT));
    int x_2 = 0;
    int y_2 = 0;
    int last_X = 0;
    int last_Y = 0;

    public Simulate(){
        panel = new javax.swing.JPanel();
        setBackground(Color.black);
        setSize(1000,1000);
        addMouseListener(this);
        addMouseMotionListener(this);
        getContentPane().add(panel);
        addKeyListener(new Keychecker());
        
    }
    class Keychecker extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {
    
            char ch = event.getKeyChar();
            if (ch == 'u'){
                flipped = !flipped;
                updateCoordinates(last_X, last_Y);
            }
    
        }
    
    }
    public void updateCoordinates(int x, int y){
        x_click = x;
        y_click = y;
        x_coordinate = convertPixeltoInches(CENTER_X - x);
        y_coordinate = convertPixeltoInches(CENTER_Y - y);
        double[] angles = InverseKinematicsUtil.getAnglesFromCoordinates(x_coordinate, y_coordinate, 0, flipped);
        if (!Double.isNaN(angles[0])){
            last_X = (int)x_click;
            last_Y = (int)y_click;
            y_2 = (int)(HEIGHT + convertInchestoPixel(ArmConstants.LIMB1_LENGTH * Math.cos(Math.toRadians(angles[0]))));
            x_2 = (int)(CENTER_X - convertInchestoPixel(ArmConstants.LIMB1_LENGTH * Math.sin(Math.toRadians(angles[0]))));
            //g.drawString((Double.toString(angles[0]) + " " + Double.toString(angles[1]) + " " + Double.toString(angles[2])), 100, 150); 
        }
        repaint();
    }
    public void paint(Graphics g){
        super.paint(g); 
        g.drawLine(CENTER_X, HEIGHT, CENTER_X, CENTER_Y);
        g.drawLine(CENTER_X, HEIGHT, x_2, y_2);
        g.drawLine(x_2, y_2, last_X, last_Y);

        g.setFont(new Font("Bold", 1, 20)); 
        g.drawString((Double.toString(x_coordinate) + " " + Double.toString(y_coordinate)), 100, 100); 

        //double[] coords = ForwardKinematicsUtil.getCoordinatesFromAngles(angles[0], angles[1], angles[2]);
        //g.drawString((Double.toString(coords[0]) + " " + Double.toString(coords[1])) + " " + Double.toString(coords[2]), 100, 200); 
        
    }
    public void mouseClicked(MouseEvent e)
    {
        updateCoordinates(e.getX(), e.getY());
       
    }
    public void mouseMoved(MouseEvent e)
    {
    }
 
    public void mouseDragged(MouseEvent e)
    {
        updateCoordinates(e.getX(), e.getY());
    }
    public void mouseExited(MouseEvent e)
    {
    }
 
    public void mouseEntered(MouseEvent e)
    {
    }
 
    public void mouseReleased(MouseEvent e)
    {
    }
 
    public void mousePressed(MouseEvent e)
    {
    }
    public double convertPixeltoInches(int pixel){
        return ((double)pixel / 6);
    }
    public int convertInchestoPixel(double inch){
        return (int)(inch * 6);
    }

    public static void main(String[]args){
        Simulate sim = new Simulate();
        sim.setVisible(true);
    }
}
