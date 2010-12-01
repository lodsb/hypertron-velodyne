import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;

// Test latency by clicking a button. Try this with JavaSound, DirectX, and ASIO
// Make sure to terminate by clicking  on the close button
// instead of ^C when using ASIO or system will hang. Occasionally DirexX will also hang. 
// Note that using ASIO the buffersize of ASIO = JASS buffersize. SOme restictions depending on audio
// card driver apply: for SB Audigy bufferSIze should be 2400 +- n*4

public class LatencyOutTest extends Thread {
    
    public static void main (String args[]) throws Exception {
        float srate = 44100.f; // SBAudigy driver only supports 48Khz
        if(args.length != 3) {
            System.out.println("Usage: java  LatencyOutTest javasound|directx|rtaudio|asio  bufferSizeJASS bufferSizeRender ");
            return;
        }
        String aAPI = args[0];
        int bufferSize = Integer.parseInt(args[1]); // JASS buffersize
        int bufferSizeRender = Integer.parseInt(args[2]); // Renderer buffersize
        double latencyInSeconds = (bufferSize*bufferSizeRender)/srate;


        final SourcePlayer player = new SourcePlayer(bufferSize,bufferSizeRender,srate,"SB Audigy Audio [DF80");
        if(aAPI.equals("asio")) {
            player.setUseNativeSound(true,"ASIO"); // ASIO buffer == synthesisBuffersize 
        } else if(aAPI.equals("directx")) {
            player.setUseNativeSound(true, "DirectX");
        } else if(aAPI.equals("rtaudio")) {
            player.setUseNativeSound(true);
            player.setNumRtAudioBuffersNative(bufferSizeRender);
        } else {
            player.setUseNativeSound(false);
            latencyInSeconds = (bufferSizeRender)/srate;
        }
        System.out.println("Native Latency=  " + latencyInSeconds);
	
        float[] excitation = new float[bufferSize];
        for(int i=0;i<bufferSize;i++) {
            excitation[i] = 0;
        }
        excitation[0] = 1;
        
        final ModalObjectWithOneContact bell =
            new ModalObjectWithOneContact(new ModalModel("../data/bell4.sy"),srate,bufferSize);
	
        final ConstantOneShotBuffer force = new ConstantOneShotBuffer(srate,bufferSize,excitation);
        
        bell.addSource(force);
        player.addSource(force);
        player.addSource(bell);

        Controller a_controlPanel = new Controller(new java.awt.Frame ("Click"),false,0,1) {
            public void onButton(int k) {
                switch(k) {
                    case 0:
                    force.hit();
                    System.out.println("Click");
                    break;
                }
            }
	    };

        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Close handler called");
                player.stopPlaying();
                try{
                    sleep(500);
                } catch(Exception e3) {
                }
                System.exit(0);
            }
	    });
	
        a_controlPanel.setButtonNames(new String[] {"Bang"});
        a_controlPanel.setSize(200,200);
        a_controlPanel.setVisible(true);
	
        if(player.getUseNativeSound() && player.getAudioAPI() == "ASIO") {
            player.initASIO();
        } else{
            player.start();
        }
	
    }
    
    
}


