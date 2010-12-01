import jass.engine.*;
import jass.generators.*;
import java.net.*;

/**
   Position based playback of wav file. Wavfile (mono) is indexed
   with position of needle on record.
   Every call to getBuffer
   this UG polls for position of needle in seconds, and uses this with
   the previous saved value to index a wav file and compute an audio
   buffer for the corresponding segment.
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/
public class MousedGroove extends StreamingAudioGroove {
    //    public class MousedGroove extends AudioGroove {
    private double duration=0; // in seconds
    private int xsize=0; // in pixels
    private MousePositionServer mousePositionServer;

    /**
       For derived classes
       @param bufferSize buffer size
    */
    public MousedGroove(int bufferSize) {
        super(bufferSize); // this is the internal buffer size
    }

    /** Construct Groove from named file.
        @param srate sampling rate in Hertz.
        @param bufferSize bufferSize of this Out
        @param fn Audio file name.
    */
    public MousedGroove(float srate,int bufferSize, String fn) throws UnsupportedAudioFileFormatException {
        super(srate,bufferSize,fn);
        this.duration = grooveBufferLength/srate;
    }

    /** Construct Groove from named URL.
        @param srate sampling rate in Hertz.
        @param bufferSize bufferSize of this Out
        @param url Audio file url name.
    */
    public MousedGroove(float srate,int bufferSize, URL url) throws UnsupportedAudioFileFormatException {
        super(srate,bufferSize,url);
        this.duration = grooveBufferLength/srate;
    }

    // width in pixels
    public void setXSize(int xsize) {
        this.xsize = xsize;
    }

    // in pixels
    public void setMousePositionServer(MousePositionServer mousePositionServer ) {
        this.mousePositionServer = mousePositionServer;
    }

    boolean resetIt = true;
    int counter = 0;
    double dt = bufferSize/srate;
    double t0=0;
    double tol = 512/srate;
    double targetTime=0;

    /**
       Abstract method of parent class
       @return the position (in seconds) of the needle into the audio data
    */
    public double getPositionOfNeedle() {
        double realTime = jass.render.MicroTime.getTime();

        if(resetIt) {
            resetIt = false;
            counter = 0;
            t0 = realTime;
        } else {
            targetTime = counter*dt;
            double errt = Math.abs(targetTime-(realTime-t0));
            if(errt>tol) {
                //reset as has been audio dropout
                System.out.println("reset: "+errt);
                counter = 0;
                t0 = realTime;
                targetTime = counter*dt;
            }
        }
        System.out.println(targetTime-(realTime-t0));
        counter++;
        return (duration*mousePositionServer.getMouseXPosition())/xsize;
    }

}


