import java.net.*;

import jass.generators.*;
import jass.render.*;
import jass.engine.*;

public class GrooveDemo {

    int buffersizeJASS = 32;          // buffers computed for rendering. Determines latency jitter
    boolean useNative = true;         // use native libs or JavaSound
    int bufferSizeJavaSound = 8*1024; // if use JavaSound 

    // Number of internal buffers of size buffersizeJASS. So latency is buffersizeJASS*bufferSizeJavaSound/srate.
    // On LINUX buffersizeJASS < 32 is not possible on my system, on windows can go as low as 2.
    // buffersizeJASS*bufferSizeJavaSound is renderBuffersize; I find 1024 is very solid, smaller up to 128 or lower is
    // possible but I can't check since my second HD is funny (interrupts CPU when accessing)
    int numRtAudioBuffersNative=0;    

    float srate = 44100;
    SourcePlayer sourcePlayer; // render object
    Groove theGroove;          // encapsulates hardware

    public static void main (String[] args) {
        if(args.length != 3) {
            System.out.println("Usage: java GrooveDemo jassBuffersize(2^k) renderBuffersize(2^m) mono16bit44KHzwavfile.wav");
            return;
        }
        new GrooveDemo(args);
    }

    public GrooveDemo(String[] args) {
        initAudio(args);
    }
    
    private void initAudio(String[] args) {
        buffersizeJASS = Integer.parseInt(args[0]);
        int renderBuffersize = Integer.parseInt(args[1]);
        if(renderBuffersize == 0) {
            useNative = false;
        } else {
            numRtAudioBuffersNative = renderBuffersize/buffersizeJASS;
        }
        
        sourcePlayer = new SourcePlayer(buffersizeJASS,bufferSizeJavaSound,srate);
        sourcePlayer.setUseNativeSound(useNative);
        sourcePlayer.setNumRtAudioBuffersNative(numRtAudioBuffersNative);

        try {
            theGroove = new Groove(srate,buffersizeJASS,args[2]);
        } catch(Exception e) {
            System.out.println("error Groove create:"+e);
        }
	// configure patch:
        try {
            sourcePlayer.addSource(theGroove);
        } catch(Exception e) {}

	//run it
        sourcePlayer.start();
        
    }

}
