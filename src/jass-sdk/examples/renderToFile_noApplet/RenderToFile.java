import java.io.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   Render to raw 16 bit PCM file (or ascii) tmp.raw as long as not interrupted.
*/
public class RenderToFile extends Thread {
    public static void main(String[] args) 
        throws InterruptedException, SinkIsFullException, FileNotFoundException {
        int bufferSize = 128;
        float srate = 44100;
        int twait = 50;
        boolean ascii = false;
        SourcePlayer sp1 = new SourcePlayer(bufferSize,srate,"tmp.raw");
        LoopBuffer af2 = new LoopBuffer(srate,bufferSize,"../data/grid.wav");
        LoopBuffer af3 = new LoopBuffer(srate,bufferSize,"../data/neytone2.wav");
        ModalObject mo1 = new ModalObject(new ModalModel("../data/ketle.sy"),srate,bufferSize);
        ModalObject.Contact c1 = (ModalObject.Contact)mo1.addSource(af2);
        ModalObject.Contact c2 = (ModalObject.Contact)mo1.addSource(af3);
        c1.start();
        c2.start();
        sp1.addSource(mo1);
        long lt = 0;
        float v=1;
        //sp1.start(); // render to file instead

        // render loop
        try {
            while(true) {
                lt += twait;
                double realTime = lt/1000.;
                if(ascii) {
                    sp1.advanceTime(realTime,ascii);
                } else {
                    sp1.advanceTime(realTime);
                }
                sleep(twait);
                v = v + .1f;
                af2.setSpeed(v);
            }
        } catch(Exception e) {
            System.out.println("RenderToFile"+e);
        }
        
    }
}



