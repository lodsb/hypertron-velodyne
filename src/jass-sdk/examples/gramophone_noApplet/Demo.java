import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.contact.*;
import java.awt.event.*;

/** 
 */
public class Demo extends Controller {

    static int nsliders = 1;
    static int nbuttons = 0;

    public Demo(java.awt.Frame parent,boolean modal) {
        super (parent, modal,nsliders,nbuttons);
        parent.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("Close handler called");
                sp1.stopPlaying();
                
                System.exit(0);
            }
	    });
        
    }

    static SourcePlayer sp1;
    static GramophoneGroove gramophoneGroove;
    static Turntable turnTable;
    static Demo app;
    static double speed=1; // turntable speed 1 = normal
    static double scratchp = 0.; // set to 0.0001 or so to have scratches
        
    static String[] names = {"speed "};
    static double[] val =   {speed};
    static double[] min =   {-3};
    static double[] max =   {3};

    
    public static void main (String args[]) throws SinkIsFullException, UnsupportedAudioFileFormatException {
        float srate = 44100.f;
        int bufferSize = 1024; // JASS buffersize, determines control rate
        int bufferSizeJavaSound = 8*1024; // internal JavaSound one
        
        if(args.length != 1) {
            System.out.println("Usage: java Demo ../data/hello.wav\n");
            return;
        }
        gramophoneGroove = new GramophoneGroove(srate,bufferSize,args[0]);
        turnTable = new Turntable(srate,bufferSize);
        gramophoneGroove.setTurntable(turnTable);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        sp1.setUseNativeSound(false); // false if use JavaSound
        sp1.addSource(gramophoneGroove);
        sp1.start();

        app = new Demo(new java.awt.Frame("Groove"), true);
        app.setSliders(val,min,max,names);
        for(int k=0;k<nsliders;k++) {
            app.onSlider(k);
        }
        //app.setButtonNames (new String[] {"Bang","Mute"});
        app.setVisible(true);

    }
    
    public void onSlider(int k) {
        switch(k) {
        case 0:
            speed = (float)super.val[0];
            turnTable.v = speed;
            break;
        }
    }

}



