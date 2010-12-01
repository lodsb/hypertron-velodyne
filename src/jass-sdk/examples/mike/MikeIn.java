import java.io.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

// Take mike input and filter through modal model. Can use ASIO DirectX or JavaSound
// Be sure to close the window instead of ^C or ASIO will hang!

public class MikeIn {
    public static void main(String[] args) throws InterruptedException{
        int bufferSize;
        int numBuffersRtAudio = 4;
        int buffersizeJavaSound = 0;
        float srate = 44100;
        int inputChannel = 0; //for ASIO
        if(args.length != 6) {
            System.out.println("Usage: java MikeIn javasound|rtaudio|asio jassBufferSize rtaudioNumBuffers buffersizeJavaSound outmixer inmixer");
            return;
        }
	
        String aAPI = args[0];
        bufferSize = Integer.parseInt(args[1]);
        numBuffersRtAudio = Integer.parseInt(args[2]);
        buffersizeJavaSound = Integer.parseInt(args[3]);
        String outmixer = args[4];
        String inmixer = args[5];
	
        AudioIn af = null;	//will be initialized in a moment
        final SourcePlayer player;	//will be initialized in a moment
	
        if(aAPI.equalsIgnoreCase("ASIO")) { //ASIO is selected by the user
            af = new AudioIn(srate,bufferSize,buffersizeJavaSound,inmixer, "ASIO",numBuffersRtAudio);
            player = new SourcePlayer(bufferSize,srate);
            player.setASIOInput(af);
            player.setInputChannelNum(inputChannel);
            player.setUseNativeSound(true, "ASIO");
	} else if(aAPI.equalsIgnoreCase("rtaudioFullDuplex")) {
	    player = new SourcePlayer(bufferSize,srate);
            player.setUseNativeSoundFullDuplex(true);
            player.setNumRtAudioBuffersNative(numBuffersRtAudio);
            af = new AudioIn(srate,bufferSize,numBuffersRtAudio,player);
        } else if(aAPI.equalsIgnoreCase("rtaudio")) {
            // figures out which one to use by trying to load native libraries
            player = new SourcePlayer(bufferSize,srate);
            player.setUseNativeSound(true);
            player.setNumRtAudioBuffersNative(numBuffersRtAudio);
            af = new AudioIn(srate,bufferSize,buffersizeJavaSound,inmixer, "RtAudio",numBuffersRtAudio);
        } else {		//JavaSound is required
            af = new AudioIn(srate,bufferSize,buffersizeJavaSound,inmixer, "javasound",numBuffersRtAudio);
            player = new SourcePlayer(bufferSize,numBuffersRtAudio,srate,outmixer);
        }
	
        ModalModel mm = null;
        try {
            mm = new ModalModel("../data/glass.sy");
        } catch(FileNotFoundException e) {
            System.out.println(e);
            System.exit(0);
        }
        ModalObjectWithOneContact mo = new ModalObjectWithOneContact(mm,srate,bufferSize);
        
        try {
            mo.addSource(af);
            player.addSource(mo);
        } catch(SinkIsFullException e) {
            System.out.println(e);
            System.exit(0);
        }
	
        Controller a_controlPanel = new Controller(new java.awt.Frame ("Close"), false,0,1) {
            public void onButton(int k) {
                switch(k) {
                    case 0:
                    System.exit(0);
                    break;
                }
            }
	    };
	
        a_controlPanel.setButtonNames(new String[] {"Exit"});
        a_controlPanel.setSize(200,200);
        a_controlPanel.setVisible(true);
	
        player.start();
	
    }

    
}
