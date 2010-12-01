import jass.render.*;
import jass.engine.*;
import jass.generators.*;

public class Creaking extends Controller {

    public Creaking(java.awt.Frame parent,boolean modal,int nsliders) {
        super (parent, modal,nsliders,1);
    }

    public void onSlider(int k) {
        switch(k) {
        case 0:
            v = (float)super.val[0];
            af.setContactProperties(v,fnorm);
            break;
        case 1:
            fnorm = (float)super.val[1];
            float ddamp = (float)(fnorm*10);
            af.setContactProperties(v,fnorm);
            mob.setDamping(1 + ddamp);
            break;
        }
    }

    public void onButton(int k) {
        switch(k) {
        case 0:
            sp1.resetAGC();
            break;
        } 
    }

        {
            setButtonNames (new String[] {"Reset"});
        }

    static ModalObject mob;
    static StickSlipSimple af;
    static ModalObject.Contact c1;
    static SourcePlayer sp1;
    static float k_mu = 5.f;
    static float vc = 1000.f;
    static float fmin = .1f;
    static float fnorm = .1f;
    static float v = .1f;
    static int nsliders = 2;

       
    static String[] names = {"speed ","Fnorm "};
    static double[] val =   {v,        fnorm};
    static double[] min =   {.1,       .001};
    static double[] max =   {1,         1};


    public static void main (String args[]) throws Exception {
        float srate = 44100.f;
        int bufferSize = 128;
        int bufferSizeJavaSound = 8*1024;
        if(args.length != 1) {
            System.out.println("Usage: java Creaking ../data/stick.sy");
            return;
        }
        try {
            mob = new ModalObject(new ModalModel(args[0]),srate,bufferSize);
        } catch (java.io.FileNotFoundException ee) {
            System.out.println("Modes file not found\n");
        }
        af = new StickSlipSimple(srate,bufferSize);
        c1 = (ModalObject.Contact)(mob.addSource(af));
        c1.start();
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        sp1.addSource(mob);
        sp1.start();
        af.setModelParameters(k_mu,fmin,vc);
        af.setContactProperties(v,fnorm);
        Creaking app=new Creaking (new java.awt.Frame (), true,nsliders);
        app.setValues(val,min,max,names);
        app.setVisible(true);
    }

}


