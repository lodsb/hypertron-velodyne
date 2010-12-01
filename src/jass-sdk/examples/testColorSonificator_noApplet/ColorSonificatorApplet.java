import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.applet.*;

public class ColorSonificatorApplet extends Applet {

    public void init() {
        //syfile = getParameter("syfile");
    }
    
    public void start() {
        try {
            new TestColorSonificator(null);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}

