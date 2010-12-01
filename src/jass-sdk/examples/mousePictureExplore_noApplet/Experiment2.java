import	java.io.File;
import	java.io.IOException;
import java.io.*;
import java.util.Date;
import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioInputStream;
import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.DataLine;
import	javax.sound.sampled.LineUnavailableException;
import	javax.sound.sampled.SourceDataLine;


public class Experiment2 {
    public static AudioInputStream	audioInputStream1 = null;
    public static AudioInputStream	audioInputStream2 = null;
    public static AudioInputStream	audioInputStreamFile = null;
    public static AudioFormat af1 = null;
    public static AudioFormat af2 = null;
    public static AudioFormat afFile = null;
    private static final int EXTERNAL_BUFFER_SIZE = 128000;
    private static final int LARGE_NUMBER = 999999999;
    private static final double durationOfImageSound = 1; // the vOICe sweep
    static BufferedWriter bufferedWriter;
    static final String LOGFILENAME = "log2.txt";
    static boolean randomize=false;
    
    public static void play(AudioInputStream ais, AudioFormat af,int nRepeats) {
        int	nBytesRead=0;
        int	nBytesWritten=0;
        ais.mark(LARGE_NUMBER);
        SourceDataLine	line = null;
        DataLine.Info	info = new DataLine.Info(SourceDataLine.class, af);
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(af);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
        line.start();
        for(int i=0;i<nRepeats;i++) {
            nBytesRead = 0;

            while (nBytesRead != -1) {
                try {
                    nBytesRead = ais.read(abData, 0, abData.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (nBytesRead >= 0) {
                    nBytesWritten = line.write(abData, 0, nBytesRead);
                }
            }
            try {
                ais.reset();
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        line.drain();
        line.close();
    }
    
    public static void initAudio() {
		try {
			audioInputStream1 = AudioSystem.getAudioInputStream(new File("nextFile.wav"));
            audioInputStream2 = AudioSystem.getAudioInputStream(new File("inBetween.wav"));
            af1 = audioInputStream1.getFormat();
            af2 = audioInputStream2.getFormat();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
    }

    public static void initAudioFile(String wavFileName) {
		try {
			audioInputStreamFile = AudioSystem.getAudioInputStream(new File(wavFileName));
            afFile = audioInputStreamFile.getFormat();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
    }

    public static void usage() {
        System.out.println("Usage: java Experiment2 times_per_image time_between_images random(0|1) n_images image_1.wav ... image_n.wav dummy");
    }
    
    public static void main (String[] args) throws java.io.IOException, InterruptedException {
        double timePerImage=120;
        int nTimesPerImage=0;
        double timeBetweenImages=5;
        int nImages=0;

        String [] imageFileNames = null;
        int five = 5;
        if(args.length < five) {
            usage();
            return;
        } else {
            timePerImage = Double.parseDouble(args[0]);
            timeBetweenImages = Double.parseDouble(args[1]);
            int tmp = Integer.parseInt(args[2]);
            if(tmp == 0) {
                randomize = false;
            } else {
                randomize = true;
            }
            nImages = Integer.parseInt(args[3]);
            if(args.length != five + nImages) {
                usage();
                return;
            } else {
               imageFileNames = new String[nImages];
               for(int i=0;i<nImages;i++) {
                   imageFileNames[i] = args[five+i-1];
               }
            }
        }
        boolean append = true;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(LOGFILENAME,append));
        } catch(Exception e) {
            e.printStackTrace();
			System.exit(1);
        }
        bufferedWriter.write("=============");
        bufferedWriter.newLine();
        Date currentTime = new Date(System.currentTimeMillis());
        bufferedWriter.write(currentTime.toString());
        bufferedWriter.newLine();
        Shuffler shuffler = new Shuffler(nImages);
        if(randomize) {
            shuffler.shuffle();
        }
        int[] intlist = shuffler.getList();
        nTimesPerImage = (int)(timePerImage/durationOfImageSound);
        for(int i=0;i<nImages;i++) {
            int imageIndex = intlist[i];
            initAudio();
            play(audioInputStream1,af1,1);
            initAudioFile(imageFileNames[imageIndex]);
            System.out.println(imageFileNames[imageIndex]);
            play(audioInputStreamFile,afFile,nTimesPerImage);
            bufferedWriter.write(imageFileNames[imageIndex]);
            bufferedWriter.newLine();
            play(audioInputStream2,af2,1);
            int tWait_ms = (int)(1000 * timeBetweenImages);
            Thread.sleep(tWait_ms);
        }
        System.out.println("done");
        currentTime = new Date(System.currentTimeMillis());
        bufferedWriter.write(currentTime.toString());
        bufferedWriter.newLine();
        bufferedWriter.write("=============");
        bufferedWriter.newLine();
        bufferedWriter.close();
        System.exit(0);        
    }
    
}

