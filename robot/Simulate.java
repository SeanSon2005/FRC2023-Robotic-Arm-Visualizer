package robot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

import robot.Constants.ArmConstants;

import java.awt.event.KeyEvent;
import java.awt.event.*;

/**
 * Basic Double Buffer Swing Example .
 * 
 * @author bbrown
 *
 */
public class Simulate{
    
    public static class Canvas extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
        
        private static final long serialVersionUID = 1L;
        private Image offScreenImageDrawed = null;
        private Graphics offScreenGraphicsDrawed = null;         
        private double x_coordinate;
        private double y_coordinate;
        private double x_click;
        private double y_click;
        private boolean flipped = false;
        private final int CENTER_X = 500;
        private final int CENTER_Y = 700;
        private final int HEIGHT = (int)(CENTER_Y-convertInchestoPixel(ArmConstants.ORIGIN_HEIGHT));
        private int x_2 = 0;
        private int y_2 = 0;
        private int last_X = 0;
        private int last_Y = 0;
        private double angle[] = {0.0,0.0,0.0};   
        private double inv_x;
        private double inv_y;
        
        public Canvas() {
            this.setPreferredSize(new Dimension(1000, 1000));
            this.setBackground(Color.white);
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addKeyListener(this);
            this.setFocusable(true);
            this.setFocusTraversalKeysEnabled(false);
        }
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar() == 'u'){
                flipped = !flipped;
                updateCoordinates(last_X, last_Y);
            }
        
        }
        public void keyPressed(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {} 

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
        /** 
         * Use double buffering.
         * @see java.awt.Component#update(java.awt.Graphics)
         */
        @Override
        public void update(Graphics g) {                                
            paint(g);
        }
              
        /**
         * Draw this generation.
         * @see java.awt.Component#paint(java.awt.Graphics)
         */
        @Override
        public void paint(final Graphics g) {

            final Dimension d = getSize();
            if (offScreenImageDrawed == null) {   
                // Double-buffer: clear the offscreen image. 
                offScreenImageDrawed = createImage(d.width, d.height);   
            }          
            offScreenGraphicsDrawed = offScreenImageDrawed.getGraphics();      
            offScreenGraphicsDrawed.setColor(Color.white);
            offScreenGraphicsDrawed.fillRect(0, 0, d.width, d.height) ;
            renderOffScreen(offScreenImageDrawed.getGraphics());
            g.drawImage(offScreenImageDrawed, 0, 0, null);
        }
        
        public void renderOffScreen(final Graphics g) {
            g.setColor(Color.black);
            g.drawLine(CENTER_X, HEIGHT, CENTER_X, CENTER_Y);
            g.drawLine(CENTER_X, HEIGHT, x_2, y_2);
            g.drawLine(x_2, y_2, last_X, last_Y);

            g.setFont(new Font("Bold", 1, 20)); 
            g.drawString((Double.toString(x_coordinate) + " " + Double.toString(y_coordinate)), 100, 100);        
            g.drawString((Double.toString(angle[0]) + " " + Double.toString(angle[1]) + " " + Double.toString(angle[2])), 100, 150); 
            g.drawString((Double.toString(inv_x) + " " + Double.toString(inv_y)), 100 ,200);    
        }
        public void updateCoordinates(int x, int y){
            x_click = x;
            y_click = y;
            x_coordinate = convertPixeltoInches(CENTER_X - x);
            y_coordinate = convertPixeltoInches(CENTER_Y - y);
            double[] angles = InverseKinematicsUtil.getAnglesFromCoordinates(x_coordinate, y_coordinate, 0, flipped);
            if (!Double.isNaN(angles[0])){
                angle = angles;
                double coords[] = ForwardKinematicsUtil.getCoordinatesFromAngles(angles[0], angles[1], angles[2]);
                inv_x = coords[0];
                inv_y = coords[1];
                last_X = CENTER_X - convertInchestoPixel(coords[0]);
                last_Y = HEIGHT*2 - convertInchestoPixel(coords[1]);
                y_2 = (int)(HEIGHT + convertInchestoPixel(ArmConstants.LIMB1_LENGTH * Math.cos(Math.toRadians(angles[0]))));
                x_2 = (int)(CENTER_X - convertInchestoPixel(ArmConstants.LIMB1_LENGTH * Math.sin(Math.toRadians(angles[0]))));
            }
            repaint();
            
        }
    }

    
    public static void main(final String [] args) {
        
        System.out.println("Running");
        final JFrame frame = new JFrame("Simple Double Buffer") {
            private static final long serialVersionUID = 1L;
            public void processWindowEvent(java.awt.event.WindowEvent e) {
                super.processWindowEvent(e);
                if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING) {
                    System.exit(-1);
                }
              }
        };
        frame.setPreferredSize(new Dimension(1000, 1000));
        frame.setBackground(Color.white);
        frame.add(new Canvas());
        frame.pack();
        frame.setVisible(true);
        double[] initCoords = ForwardKinematicsUtil.getCoordinatesFromAngles(ArmConstants.ARM_1_INITIAL_ANGLE, ArmConstants.ARM_2_INITIAL_ANGLE, 0);
        System.out.println("x " + initCoords[0] + ", y " + initCoords[1]);
        double[] initAngles = InverseKinematicsUtil.getAnglesFromCoordinates(initCoords[0], initCoords[1], initCoords[2], false);
        System.out.println("ang1: " + initAngles[0] + ", ang2 " + initAngles[1]);
      
    }
    public static double convertPixeltoInches(int pixel){
        return ((double)pixel / 7);
    }
    public static int convertInchestoPixel(double inch){
        return (int)(inch * 7);
    }   
    
} 