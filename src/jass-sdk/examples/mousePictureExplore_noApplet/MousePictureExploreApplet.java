import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.applet.*;

public class MousePictureExploreApplet extends Applet {

    String[] args = {"file:///D:/temp/gogh1.jpg","45","foobar"};

    public void init() {
        //syfile = getParameter("syfile");
    }
    
    public void start() {
        new VTNTDemo(args);
    }
    
}

