import java.util.ArrayList;

import jm.util.Write;

public class WavFileWriter {
	private int channels = 0;
	private ArrayList<Float>[] channelData;
	private String filename;
	
	private double[] azArray;
	private double[] elArray;
	
	public WavFileWriter(String filename, int channels) {
		this.channels = channels;
		this.filename = filename;
		
		this.channelData = new ArrayList[channels];
		
		for(int i = 0; i < channels; i++) {
			this.channelData[i] = new ArrayList<Float>();
		}
		
		this.azArray = new double[channels];
		this.elArray = new double[channels];
	}
	
	
	public void setListenerAngleForDelayLine(int channel, double az, double el) {
		this.azArray[channel] = az;
		this.elArray[channel] = el;
	}
	
	public void appendData(int channel, float sample) {
		this.channelData[channel].add(sample); 
	}
	
	public void writeData() {
		/*float[] samples = new float[this.channels*this.channelData[0].size()];
		
		for(int j = 0; j < this.channelData[0].size(); j++) {
			for(int i = 0; i < channels; i++) {
				samples[(j*this.channels)+i] = this.channelData[i].get(j); 
			}
		}
		
		Write.audio(samples, filename, this.channels, 44100, 32);*/
		
		
		
		
		
		float[] samples = new float[this.channelData[0].size()];
		
		float max = -1.0f;
		int i = 0;
		for(float sample : this.channelData[0]) {
			samples[i] = sample;
			if(Math.abs(sample) > max) {
				max = Math.abs(sample);
			}
			i++;        	        
		}
		i = 0;
		for(float s: samples) {
				samples[i] = 1.0f/max*s;
				i++;
		}
		
		Write.audio(samples, filename, 1, 44100, 32);
	}
	
}
