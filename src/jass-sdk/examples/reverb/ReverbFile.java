import java.io.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.patches.*;

/**
   Render to raw 16 bit PCM file tmp.raw as long as not interrupted.
*/
public class ReverbFile extends Thread {
    public static void main(String[] args) {
        int bufferSize = 128;
        float srate = 44100;
        int twait = 50;
        int nReflections = 6;
        float dryToWet = .1f; // 1 is dry only
        SourcePlayer player=null;
        CombReverb reverb=null;
        ConstantLoopBuffer input=null;
            
        if(args.length != 1) {
            System.out.println("Usage: java ReverbFile foo.wav");
            return;
        }
        try {
            player = new SourcePlayer(bufferSize,srate,"tmp.raw");
            reverb = new CombReverb(bufferSize,srate,nReflections);
            input = new ConstantLoopBuffer(srate,bufferSize,args[0]);
            player.addSource(reverb);
            reverb.addSource(input);
        } catch(Exception e) {
            System.out.println(e);
        }
        reverb.setDryToWet(dryToWet);
        // render
        try {
            double realTime = 45;
            player.advanceTime(realTime);
        } catch(Exception e) {
            System.out.println("RenderToFile"+e);
        }
        
    }
}



