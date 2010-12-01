import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import jm.util.Read;


public class WavFileReader {
	private final static int bufTime = 4;
	private double st;
	private  int sr;
	private long length;
	private double audioFileLenght;
	private double[] samples;
	private double[] wsamples;
	
	// all ugly
	private ArrayList<Float> fileSamples = new ArrayList<Float>();
	
	private AudioSampleReader reader;
	public WavFileReader(String filename) {
		try {
			reader = new AudioSampleReader(new File(filename));
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AudioFormat f = reader.getFormat();
		sr = (int) f.getSampleRate();
		
		st = 1.0/((double) sr);
		length = reader.getSampleCount();
		audioFileLenght = ((double)length)*st;
		samples = new double[sr*bufTime];
		wsamples = new double[sr*bufTime*f.getChannels()];
		

		System.out.println("Using input-file "+filename+" ...");	
		this.readNextBlock();
		
		audio = Read.audio(filename);
	}
	
	float[] audio;
	
	int currentBlkStart = 0;
	private void readNextBlock() {
		if(currentBlkStart <= length) {
			try {
				reader.getInterleavedSamples(currentBlkStart, currentBlkStart+sr*bufTime, wsamples);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reader.getChannelSamples(0, wsamples, samples);
			
			currentBlkStart += sr*bufTime;
		}
		
	}
	
	private int index = 0;
	private int sIndex = 0;
	
	public float getNextSample() {
		float ret;
		if(index < audio.length) {
			ret = audio[index];
			index++;
			index++;
			
		} else {
			ret = 0.0f;
		}
		
		return ret;
	}
	/*float getNextSample() {
		float ret;
		
		if(index < length) {
			if(sIndex >= samples.length) {
				this.readNextBlock();
				sIndex = 0;
			}
			
			ret = (float) samples[sIndex];
			sIndex++;
			index++;
		} else {
			ret = 0.0f;
		} 
		return ret;
	}*/
	
	
}
