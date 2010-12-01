import	java.io.File;
import java.io.*;
import java.util.Date;
import	java.io.IOException;

import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioInputStream;
import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.DataLine;
import	javax.sound.sampled.LineUnavailableException;
import	javax.sound.sampled.SourceDataLine;


public class Experiment {
    public static AudioInputStream	audioInputStream1 = null;
    public static AudioInputStream	audioInputStream2 = null;
    public static AudioFormat af1 = null;
    public static AudioFormat af2 = null;
    private static final int EXTERNAL_BUFFER_SIZE = 128000;
    static BufferedWriter bufferedWriter;
    static final String LOGFILENAME = "log.txt";
    static boolean randomize=false;
    
    public static void play(AudioInputStream ais, AudioFormat af) {
        SourceDataLine	line = null;
		DataLine.Info	info = new DataLine.Info(SourceDataLine.class, af);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);

			/*
			  The line is there, but it is not yet ready to
			  receive audio data. We have to open the line.
			*/
			line.open(af);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
        line.start();
        int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1) {
			try {
				nBytesRead = ais.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				int	nBytesWritten = line.write(abData, 0, nBytesRead);
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

    public static void usage() {
        System.out.println("Usage: java Experiment time_per_image time_between_images random(0|1) n_images image_1.jpg ... image_n.jpg dummyArg");
    }
    
    public static void main (String[] args) throws java.io.IOException, InterruptedException {
        double timePerImage=10;
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
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        Shuffler shuffler = new Shuffler(nImages);
        if(randomize) {
            shuffler.shuffle();
        }
        int[] intlist = shuffler.getList();
        
        for(int i=0;i<nImages;i++) {
            int imageIndex = intlist[i];
            String command = "java -classpath \".;jass.jar\" DemoMousePictureExplore " +
                imageFileNames[imageIndex] + " 45.1 asd 512";
            System.out.println(command);
            initAudio();
            play(audioInputStream1,af1);
            process =  runtime.exec(command);
            int tWait_ms = (int)(1000 * timePerImage);
            Thread.sleep(tWait_ms);
            process.destroy();
            bufferedWriter.write(imageFileNames[imageIndex]);
            bufferedWriter.newLine();
            play(audioInputStream2,af2);
            tWait_ms = (int)(1000 * timeBetweenImages);
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

