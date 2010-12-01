/*************************************************************************
 *  Compilation:  javac Draw.java
 *
 *  Simple graphics library.
 *
 *
 *  Remarks
 *  -------
 *    -  lines are much faster than spots?
 *    -  careful using setFont in inner loop within an animation -
 *       it can cause flicker
 *
 *************************************************************************/


import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.net.*;
import java.applet.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.ArrayList;


public class Draw implements MouseListener, MouseMotionListener, KeyListener, ActionListener {

    // listeners
    private ArrayList<DrawListener> listeners = new ArrayList<DrawListener>();

    // default colors
    public final Color DEFAULT_PEN_COLOR   = Color.BLACK;
    public final Color DEFAULT_CLEAR_COLOR = Color.WHITE;

    // current pen color
    private Color penColor;

    // default canvas size is DEFAULT_SIZE-by-DEFAULT_SIZE
    private static final int DEFAULT_SIZE = 512;
    private int width  = DEFAULT_SIZE;
    private int height = DEFAULT_SIZE;

    // default pen radius
    private static final double DEFAULT_PEN_RADIUS = 0.002;

    // current pen radius
    private double penRadius;

    // show we draw immediately or wait until next show?
    private boolean defer = false;

    // boundary of drawing canvas, 5% border
    private final double BORDER = 0.05;
    private final double DEFAULT_XMIN = 0.0;
    private final double DEFAULT_XMAX = 1.0;
    private final double DEFAULT_YMIN = 0.0;
    private final double DEFAULT_YMAX = 1.0;
    private double xmin, ymin, xmax, ymax;

    // default font
    private static final Font DEFAULT_FONT = new Font("Serif", Font.PLAIN, 16);

    // current font
    private Font font;

    // double buffered graphics
    private final BufferedImage offscreenImage, onscreenImage;
    private final Graphics2D offscreen, onscreen;

    // the frame for drawing to the screen
    private JFrame frame = new JFrame();

    // the label
    private JLabel draw;


    // create a new drawing region of given dimensions
    public Draw() { this(DEFAULT_SIZE, DEFAULT_SIZE); }
    public Draw(int width, int height) {
        this.width  = width;
        this.height = height;
        if (width <= 0 || height <= 0) throw new RuntimeException("Illegal dimension");
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        setXscale();
        setYscale();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        setPenColor();
        setPenRadius();
        setFont();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        // the drawing panel
        ImageIcon icon = new ImageIcon(onscreenImage);
        draw = new JLabel(icon);
        draw.addMouseListener(this);
        draw.addMouseMotionListener(this);


        frame.setContentPane(draw);

        // label cannot get keyboard focus
        frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // closes only current window
        frame.setTitle("Standard Draw");
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.setVisible(true);

        clear();
    }


    // create the menu bar
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem(" Save...   ");
        menuItem1.addActionListener(this);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menuItem1);
        return menuBar;
    }

    // create the menu bar
    public JLabel getJLabel() { return draw; }


    // change the user coordinate system
    public void setXscale() { setXscale(DEFAULT_XMIN, DEFAULT_XMAX); }
    public void setYscale() { setYscale(DEFAULT_YMIN, DEFAULT_YMAX); }
    public void setXscale(double min, double max) {
        double size = max - min;
        xmin = min - BORDER * size;
        xmax = max + BORDER * size;
    }
    public void setYscale(double min, double max) {
        double size = max - min;
        ymin = min - BORDER * size;
        ymax = max + BORDER * size;
    }

    // helper functions that scale from user coordinates to screen coordinates and back
    private double scaleX (double x) { return width  * (x - xmin) / (xmax - xmin); }
    private double scaleY (double y) { return height * (ymax - y) / (ymax - ymin); }
    private double factorX(double w) { return w * width  / Math.abs(xmax - xmin);  }
    private double factorY(double h) { return h * height / Math.abs(ymax - ymin);  }
    private double userX  (double x) { return xmin + x * (xmax - xmin) / width;    }
    private double userY  (double y) { return ymax - y * (ymax - ymin) / height;   }


    // clear the screen with given color
    public void clear() { clear(DEFAULT_CLEAR_COLOR); }
    public void clear(Color color) {
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        show();
    }

    // set the pen size
    public void setPenRadius() { setPenRadius(DEFAULT_PEN_RADIUS); }
    public void setPenRadius(double r) {
        penRadius = r * DEFAULT_SIZE;
        BasicStroke stroke = new BasicStroke((float) penRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        offscreen.setStroke(stroke);
    }

    // set the pen color
    public void setPenColor() { setPenColor(DEFAULT_PEN_COLOR); }
    public void setPenColor(Color color) {
        penColor = color;
        offscreen.setColor(penColor);
    }

    // write the given string in the current font
    public void setFont() { setFont(DEFAULT_FONT); }
    public void setFont(Font f) { font = f; }

    // draw a line from (x0, y0) to (x1, y1)
    public void line(double x0, double y0, double x1, double y1) {
        offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
        show();
    }

    // draw one pixel at (x, y)
    private void pixel(double x, double y) {
        offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }

    // draw point at (x, y)
    public void point(double x, double y) {
        double xs = scaleX(x);
        double ys = scaleY(y);
        double r = penRadius;
        // double ws = factorX(2*r);
        // double hs = factorY(2*r);
        // if (ws <= 1 && hs <= 1) pixel(x, y);
        if (r <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(xs - r/2, ys - r/2, r, r));
        show();
    }

    // draw circle of radius r, centered on (x, y); degenerate to pixel if small
    public void circle(double x, double y, double r) {
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        show();
    }

    // draw arc of radius r, centered on (x, y), from angle1 to angle2 (in degrees)
    public void arc(double x, double y, double r, double angle1, double angle2) {
        while (angle2 < angle1) angle2 += 360;
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Arc2D.Double(xs - ws/2, ys - hs/2, ws, hs, angle1, angle2 - angle1, Arc2D.OPEN));
        show();
    }


    // draw filled circle of radius r, centered on (x, y); degenerate to pixel if small
    public void filledCircle(double x, double y, double r) {
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        show();
    }


    // draw squared of side length 2r, centered on (x, y); degenerate to pixel if small
    public void square(double x, double y, double r) {
        // screen coordinates
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        show();
    }

    // draw squared of side length 2r, centered on (x, y); degenerate to pixel if small
    public void filledSquare(double x, double y, double r) {
        // screen coordinates
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        show();
    }

    // draw a polygon with the given (x[i], y[i]) coordinates
    public void polygon(double[] x, double[] y) {
        int N = x.length;
        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < N; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.draw(path);
        show();
    }

    // draw a filled polygon with the given (x[i], y[i]) coordinates
    public void filledPolygon(double[] x, double[] y) {
        int N = x.length;
        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < N; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.fill(path);
        show();
    }


    // get an image from the given filename
    private Image getImage(String filename) {

        // to read from file
        ImageIcon icon = new ImageIcon(filename);

        // try to read from URL
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            try {
                URL url = new URL(filename);
                icon = new ImageIcon(url);
            } catch (Exception e) { /* not a url */ }
        }

        // in case file is inside a .jar
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            URL url = Draw.class.getResource(filename);
            if (url == null) throw new RuntimeException("image " + filename + " not found");
            icon = new ImageIcon(url);
        }

        return icon.getImage();
    }

    // draw picture (gif, jpg, or png) centered on (x, y)
    public void picture(double x, double y, String s) {
        Image image = getImage(s);
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = image.getWidth(null);
        int hs = image.getHeight(null);
        if (ws < 0 || hs < 0) throw new RuntimeException("image " + s + " is corrupt");
        offscreen.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);
        show();
    }

    // draw picture (gif, jpg, or png) centered on (x, y), rescaled to w-by-h
    public void picture(double x, double y, String s, double w, double h) {
        Image image = getImage(s);
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(w);
        double hs = factorY(h);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else {
            offscreen.drawImage(image, (int) Math.round(xs - ws/2.0),
                                       (int) Math.round(ys - hs/2.0),
                                       (int) Math.round(ws),
                                       (int) Math.round(hs), null);
        }
        show();
    }



    // write the given text string in the current font, center on (x, y)
    public void text(double x, double y, String s) {
        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(s);
        int hs = metrics.getDescent();
        offscreen.drawString(s, (float) (xs - ws/2.0), (float) (ys + hs));
        show();
    }

    // display on screen and pause for t miliseconds
    public void show(int t) {
        defer = true;
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
        try { Thread.currentThread().sleep(t); }
        catch (InterruptedException e) { System.out.println("Error sleeping"); }
    }


    // view on-screen, creating new frame if necessary
    public void show() {
        if (!defer) onscreen.drawImage(offscreenImage, 0, 0, null);
        if (!defer) frame.repaint();
    }


    // save to file - suffix must be png, jpg, or gif
    public void save(String filename) {
        File file = new File(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // png files
        if (suffix.toLowerCase().equals("png")) {
            try { ImageIO.write(offscreenImage, suffix, file); }
            catch (IOException e) { e.printStackTrace(); }
        }

        // need to change from ARGB to RGB for jpeg
        // reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
        else if (suffix.toLowerCase().equals("jpg")) {
            WritableRaster raster = offscreenImage.getRaster();
            WritableRaster newRaster;
            newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[] {0, 1, 2});
            DirectColorModel cm = (DirectColorModel) offscreenImage.getColorModel();
            DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(),
                                                          cm.getRedMask(),
                                                          cm.getGreenMask(),
                                                          cm.getBlueMask());
            BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false,  null);
            try { ImageIO.write(rgbBuffer, suffix, file); }
            catch (IOException e) { e.printStackTrace(); }
        }

        else {
            System.out.println("Invalid image file type: " + suffix);
        }
    }


    // open a save dialog when the user selects "Save As" from the menu
    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(frame, "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        String filename = chooser.getFile();
        if (filename != null) {
            save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }


    public void addListener(DrawListener listener) {
        listeners.add(listener);
    }

    // user types a key
    public void keyTyped(KeyEvent e) { 
        for (DrawListener listener : listeners)
            listener.keyTyped(e.getKeyChar());
    }

    // user presses mouse button #1
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
           for (DrawListener listener : listeners)
               listener.mousePressed(userX(e.getX()), userY(e.getY()));
        }
    }

    // user releases mouse button #1
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
           for (DrawListener listener : listeners)
               listener.mouseReleased(userX(e.getX()), userY(e.getY()));
        }
    }

    // user drags the mouse
    public void mouseDragged(MouseEvent e) {
        // seems to not recognize mouse dragging events if we specify a button???
        // if (e.getButton() == MouseEvent.BUTTON1)
        for (DrawListener listener : listeners)
            listener.mouseDragged(userX(e.getX()), userY(e.getY()));
    }

    // we don't support these methods
    public void keyPressed   (KeyEvent e)   { }
    public void keyReleased  (KeyEvent e)   { }
    public void mouseMoved   (MouseEvent e) { }
    public void mouseEntered (MouseEvent e) { }
    public void mouseExited  (MouseEvent e) { }
    public void mouseClicked (MouseEvent e) { }



    // test client
    public static void main(String[] args) {
        Draw draw = new Draw();
        draw.square(.2, .8, .1);
        draw.filledSquare(.8, .8, .2);
        draw.circle(.8, .2, .2);

        draw.setPenColor(Color.MAGENTA);
        draw.setPenRadius(.02);
        draw.arc(.8, .2, .1, 200, 45);

        // draw a blue diamond
        draw.setPenRadius();
        draw.setPenColor(Color.BLUE);
        double[] x = { .1, .2, .3, .2 };
        double[] y = { .2, .3, .2, .1 };
        draw.filledPolygon(x, y);

        // text
        draw.text(0.8, 0.2, "centered");
    }


}
