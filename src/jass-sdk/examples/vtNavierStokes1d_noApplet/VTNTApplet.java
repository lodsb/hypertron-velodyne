import jass.render.*;
import jass.generators.*;
import jass.utils.*;
import java.awt.*;
import javax.swing.*;
import java.applet.*;

public class VTNTApplet extends Applet {

    String[] args =  {".17", "44100", ".10",  "8", "40", "0.5"};
    public void init() {
    }
    
    public void start() {
        new VTNTDemo(args);
    }
    
}

