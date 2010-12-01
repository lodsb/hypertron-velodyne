import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.net.*;
import java.io.*;

public class CreakingApplet extends AppletController {
    float srate = 44100.f;
    int bufferSize = 128;
    int bufferSizeJavaSound = 8*1024;
    ModalObject mob;
    StickSlipSimple af;
    ModalObject.Contact c1;
    SourcePlayer sp1;
    float k_mu = 5.f;
    float vc = 1000.f;
    float fmin = .1f;
    float fnorm = .1f;
    float v = .1f;
    String syfile = "../data/stick.sy";
    
    public void setNSliders() {
        nsliders = 2;
    }

    public void setNButtons() {
        nbuttons = 1;
    }

    protected void onSlider(int k) {
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

    protected void jButtonMousePressed (int k,java.awt.event.MouseEvent evt) {
        switch(k) {
        case 0:
            sp1.resetAGC();
            break;
        }
    }

    public void init() {
        super.init();
        syfile = getParameter("syfile");
    }
    
    public void start() {
        URL codebase = getCodeBase();
        URL syurl = null;
        try {
            syurl = new URL(codebase,syfile);
        } catch(MalformedURLException e) {
            System.out.println(e+" Malformed URL: " +codebase+" "+ syfile);
        }
        ModalModel mm=null;
        try {
            mm = new ModalModel(syurl);
        } catch(IOException e) {
            System.out.println(e);
        }
        mob = new ModalObject(mm,srate,bufferSize);
        af = new StickSlipSimple(srate,bufferSize);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        try {
            c1 = (ModalObject.Contact)(mob.addSource(af));
            sp1.addSource(mob);
        } catch(SinkIsFullException e) {
            System.out.println(e);
        }
        c1.start();
        af.setModelParameters(k_mu,fmin,vc);
        af.setContactProperties(v,fnorm);

        String[] names = {"speed ","Fnorm "};
        double[] val =   {v,        fnorm};
        double[] min =   {.1,       .001};
        double[] max =   {1,         1};

        setValues(val,min,max,names);
        jButton[0].setText ("Reset");
        
        sp1.start();
    }

}

