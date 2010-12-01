import jass.render.*;
import jass.engine.*;
//import jass.generators.*;

public class SawApplet extends java.applet.Applet {
    
    public void start() {

        float srate = 44100;
        float freq = 415;
        int bufferSize = (int)(srate/freq);
        
        try {
            new SourcePlayer(bufferSize,0,srate, new Out(bufferSize) {
                public void computeBuffer() {
                    for(int i=0;i<getBufferSize();i++)
                        buf[i] = i;
                }
            }).start();
        } catch(Exception e) {
        }
    }
}



