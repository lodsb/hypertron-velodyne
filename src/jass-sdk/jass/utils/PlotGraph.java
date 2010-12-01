/*
*   Class PlotGraph
*
*   A class that creates a window and displays within that window
*   a graph of one or more x-y data sets
*
*   This class extends Plot (also from Michael Thomas Flanagan's Library)
*
*   For use if you are incorporating a plot into your own Java program
*   See Plotter for a free standing graph plotting application
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:	 February 2002
*   UPDATED:  22 April 2004 and 14 August 2004
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web page:
*   PlotGraph.html
*
*   Copyright (c) April 2004, Auguswt 2004
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/


package jass.utils;

// Include the windowing libraries
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;

// Declare a class that creates a window capable of being drawn to
public class PlotGraph extends Plot{
    protected int graphWidth = 800;     	// width of the window for the graph in pixels
    protected int graphHeight = 600;    	// height of the window for the graph in pixels
    protected int closeChoice = 1;    	    // =1 clicking on close icon causes window to close
    //    and the the program is exited.
    // =2 clicking on close icon causes window to close
    //    leaving the program running.

    // location of window
    protected int topleft_x=600;
    protected int topleft_y=0;
    // Create the window object
    protected JFrame window = new JFrame("PlotGraph");
    protected boolean windowExists=false;

    // Constructor
    // One 2-dimensional data arrays
    public PlotGraph(double[][] data) {
        super(data);
    }

    // Constructor
    //Two 1-dimensional data arrays
    public PlotGraph(double[] xData, double[] yData) {
        super(xData, yData);
    }

    // Rescale the y dimension of the graph window and graph
    public void rescaleY(double yScaleFactor)
    {
        this.graphHeight=(int)Math.round((double)graphHeight*yScaleFactor);
        super.yLen=(int)Math.round((double)super.yLen*yScaleFactor);
        super.yTop=(int)Math.round((double)super.yTop*yScaleFactor);
        super.yBot=super.yTop + super.yLen;
    }

    // Rescale the x dimension of the graph window and graph
    public void rescaleX(double xScaleFactor)
    {
        this.graphWidth=(int)Math.round((double)graphWidth*xScaleFactor);
        super.xLen=(int)Math.round((double)super.xLen*xScaleFactor);
        super.xBot=(int)Math.round((double)super.xBot*xScaleFactor);
        super.xTop=super.xBot + super.xLen;
    }

    public void setLocation(int topleft_x,int topleft_y) {
        this.topleft_x = topleft_x;
        this.topleft_y = topleft_y;
    }
    
    // Get pixel width of the PlotGraph window
    public int getGraphWidth(){
        return this.graphWidth;
    }

    // Get pixel height of the PlotGraph window
    public int getGraphHeight(){
        return this.graphHeight;
    }

    // Reset height of graph window (pixels)
    public void setGraphHeight(int graphHeight){
        this.graphHeight=graphHeight;
    }

    // Reset width of graph window (pixels)
    public void setGraphWidth(int graphWidth){
        this.graphWidth=graphWidth;
    }

    // Get close choice
    public int getCloseChoice(){
        return this.closeChoice;
    }

    // Reset close choice
    public void setCloseChoice(int choice){
        this.closeChoice = choice;
    }

    // The paint method to draw the graph.
    public void paint(Graphics g){

        // Rescale - needed for redrawing if graph window is resized by dragging
        double newGraphWidth = this.getSize().width;
        double newGraphHeight = this.getSize().height;
        double xScale = (double)newGraphWidth/(double)this.graphWidth;
        double yScale = (double)newGraphHeight/(double)this.graphHeight;
        rescaleX(xScale);
        rescaleY(yScale);

        // Call graphing method
        graph(g);
    }

    public void close() {
        window.dispose();
        windowExists = false;
    }

    // Set up the window and show graph
    public void plot(){
        // Set the initial size of the graph window
        setSize(this.graphWidth, this.graphHeight);

        // Set background colour
        window.getContentPane().setBackground(java.awt.Color.white);

        // Choose close box
        if(this.closeChoice==1){
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        else{
            window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }

        // Add graph canvas
        window.getContentPane().add("Center", this);

        // Set the window up
        window.pack();
        window.setResizable(true);
        window.toFront();

        if(!windowExists) {
            window.setLocation(topleft_x,topleft_y);
        }

        // Show the window
        try {
            Thread.sleep(100);
        } catch(Exception e) {}
        window.show();
        windowExists = true;
    }

    // Displays dialogue box asking if you wish to exit program
    // Answering yes end program - will simultaneously close the graph windows
    public void endProgram(){

        int ans = JOptionPane.showConfirmDialog(null, "Do you wish to end the program\n"+"This will also close the graph window or windows", "End Program", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(ans==0){
            System.exit(0);
        }
        else{
            String message = "Now you must press the appropriate escape key/s, e.g. Ctrl C, to exit this program\n";
            if(this.closeChoice==1)message += "or close a graph window";
            JOptionPane.showMessageDialog(null, message);
        }
    }

}

