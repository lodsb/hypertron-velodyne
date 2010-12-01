import jass.render.*;
import jass.engine.*;
import jass.patches.*;
import jass.generators.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class TestColorSonificator extends Thread {
    
    public static void main (String args[]) throws Exception {
        new TestColorSonificator(args);
    }

    public TestColorSonificator(String args[]) throws Exception {
        boolean nativeSound = false;
        float srate = 44100.f;
        int bufferSize = 64;
        int bufferSizeJavaSound = 1024*10;
        int numRtAudioBuffersNative = 512/bufferSize;
        final ColorDisplay[] frameArray = new ColorDisplay[1];

        if(args!=null && (args.length != 0)) {
            System.out.println("Usage: java TestColorSonificator");
            return;
        }
        final SourcePlayer player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        if(nativeSound) {
            player.setUseNativeSound(true);
            player.setNumRtAudioBuffersNative(numRtAudioBuffersNative);
        }
        final LowpassColorSonificator lowpassColorSonificator = new LowpassColorSonificator(srate,bufferSize);
        final RandOut input = new RandOut(bufferSize);
        final Mixer mixer = new Mixer(bufferSize,1); // 1 source
        lowpassColorSonificator.addSource(input);
        mixer.addSource(lowpassColorSonificator);
        player.addSource(mixer);
        mixer.setGain(0,1);

        // Add control panel
        // experimentally sounds best: min =3 (otherwise instabiliteis when rapidly changing f)  maxd = 25 fudgepow = .35
        String[] names =        {"Hue ","Saturation ","Brightness ","mind ","maxd ","fudgePow ", "vel "};
        final double[] val2 =   {.5,     .5,           .5,           3,     25,      .35,       1};
        double[] min =          {0,      0,             0,           .5,     10,     .1,        0};
        double[] max =          {1,      1,             1,            5,     50,      1,        2.5};
        
        int nbuttons = 4;
        final boolean[] doLoop = {false};
        
        Controller a_controlPanel = new Controller(new java.awt.Frame ("TestColorSonificator"),false,val2.length,nbuttons) {

            public void onButton(int k) {
                switch(k) {
                    case 0:
                    player.resetAGC();
                    break;
                    case 1: {
                        FileDialog fd = new FileDialog(new Frame(),"Save");
                        fd.setMode(FileDialog.SAVE);
                        fd.setVisible(true);
                        saveToFile(fd.getFile());
                    }
                    break;
                    case 2: {
                        FileDialog fd = new FileDialog(new Frame(),"Load");
                        fd.setMode(FileDialog.LOAD);
                        fd.setVisible(true);
                        loadFromFile(fd.getFile());
                        break;
                    }
                    case 3:
                    doLoop[0] = !doLoop[0];
                    if(doLoop[0]) {
                        setButtonName("Loop off (is on)",k);
                    } else {
                        setButtonName("Loop on (is off)",k);
                    }
                    break;
                } 
            }

            float rgb[] = new float[3];
            float lch[] = new float[3];
            
            public void onSlider(int k) {
                Color c=null;
                switch(k) {
                    case 0:
                    case 1:
                    case 2:
                    val2[k]=(float)this.val[k];
                    float pitch = (float)jass.utils.PitchMap.hue2pitch(val2[0]);
                    lowpassColorSonificator.setHSB_V(pitch,(float)val2[1],(float)val2[2],(float)val2[6]);
                    /*
                    int rgbint = java.awt.Color.HSBtoRGB((float)val2[0],(float)val2[1],(float)val2[2]);
                    int red   = (rgbint >> 16) & 0xff;
                    int green = (rgbint >>  8) & 0xff;
                    int blue  = (rgbint      ) & 0xff;
                    rgb[0] = (float)(red/255.);
                    rgb[1] = (float)(green/255.);
                    rgb[2] = (float)(blue/255.);
                    lch = jass.utils.Color.Rgb2lch(rgb);
                    lowpassColorSonificator.setHSB_V(lch[2],lch[1],lch[0],(float)val2[6]);
                    */
                    c = Color.getHSBColor((float)val2[0],(float)val2[1],(float)val2[2]);
                    if(frameArray[0] != null) {
                        frameArray[0].setColor(c);
                        frameArray[0].repaint();
                    }
                    break;
                    case 3:
                    val2[k]=(float)this.val[k];
                    lowpassColorSonificator.setSaturationLimits((float)val2[3],(float)val2[4]);
                    break;
                    case 4:
                    val2[k]=(float)this.val[k];
                    lowpassColorSonificator.setSaturationLimits((float)val2[3],(float)val2[4]);
                    break;

                    case 5:
                    val2[k]=(float)this.val[k];
                    lowpassColorSonificator.setFudgePower((float)val2[k]);
                    break;
                    
                    case 6:
                    val2[k]=(float)this.val[k];
                    lowpassColorSonificator.setHSB_V((float)val2[0],(float)val2[1],(float)val2[2],(float)val2[k]);
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
	
        a_controlPanel.setSliders(val2,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Reset","Save","Load","Loop on (is off)"});

        //UIManager.LookAndFeelInfo[] lfinfo = UIManager.getInstalledLookAndFeels();
        //for(int i=0;i<lfinfo.length;i++) {
        //    System.out.println(lfinfo[i].toString());
        //}
        //UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
        //UIManager.setLookAndFeel(new  com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
        //UIManager.setLookAndFeel(new  com.sun.java.swing.plaf.motif.MotifLookAndFeel());

        a_controlPanel.setVisible(true);
        for(int i=0;i<names.length;i++) {
            a_controlPanel.onSlider(i);
        }

        // display the color
        frameArray[0] = new ColorDisplay();
        frameArray[0].setSize(new Dimension(640,400));
        frameArray[0].setResizable(false);
        frameArray[0].setVisible(true);
        //frameArray[0].setLocationRelativeTo(a_controlPanel);
        
        player.start();

        // loop hue...
        float hue =0,dhue = .01f;
        while(true) {
            if(doLoop[0]) {
                hue += dhue;
                if(hue>1) {
                    hue -= 1;
                }
                val2[0] = hue;
                a_controlPanel.setSliders(val2,min,max,names);
                a_controlPanel.onSlider(0); // sethue
            }
            try {
                sleep(100);
            } catch(Exception e) {
            }
        }    
    }

}
    

class ColorDisplay extends JFrame {
    private Color color;

    public void setColor(Color c) {
        color = c;
    }
    
    public void paint(Graphics g) {
        //System.out.println("Paint: "+color);
        g.setColor(color);
        Dimension dim = this.getSize();
        g.fillRect(0,0,dim.width,dim.height);
    }
}

