import java.io.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   Testing that a source can be attached to multiple sinks.
   Kinda superfluous as examples/delay also has the Mixer attached to
   itself and the SourcePlayer.
*/
public class ShareSourceTest extends Thread {
    public static void main(String[] args) throws InterruptedException{
        int bufferSize = 128;
        float srate = 44100/8;
        SourcePlayer sp1 = new SourcePlayer(bufferSize,8*1024,srate);
        LoopBuffer af = new LoopBuffer(srate,bufferSize,"../../data/neytone2.wav");
        ModalModel mm1 = null, mm2 = null;
        try {
            mm1 = new ModalModel("../../data/stick.sy");
            mm2 = new ModalModel("../../data/sword1.sy");
        } catch(FileNotFoundException e) {
            System.out.println(e);
            System.exit(0);
        }
        ModalObjectWithOneContact mo1 = new ModalObjectWithOneContact(mm1,srate,bufferSize);
        ModalObjectWithOneContact mo2 = new ModalObjectWithOneContact(mm2,srate,bufferSize);
        
        try {
            mo1.addSource(af);
            mo2.addSource(af);
            sp1.addSource(mo1);
            sp1.addSource(mo2);
        } catch(SinkIsFullException e) {
            System.out.println(e);
            System.exit(0);
        }
        sp1.start();

    }        
}


