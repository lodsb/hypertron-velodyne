import java.io.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

public class MikeApplet extends java.applet.Applet {
    public void start() {
        int bufferSize = 64; // this doesn't do much here
        float srate = 44100;
        int bufferSizeJavaSound = (int)srate; // 1 sec delay
        SourcePlayer sp = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        AudioIn af = new AudioIn(srate,bufferSize,0);
        try {
            sp.addSource(af);
        } catch(SinkIsFullException e) {
        }
        sp.start();
    }        
}
