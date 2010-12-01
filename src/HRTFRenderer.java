import java.util.ArrayList;

import jass.generators.HRTF;
import jass.generators.PositionData;
import jm.util.Write;


public class HRTFRenderer {
	private String filename;
	private Node node; 
	
	private HRTF hrtf;
	private static int windowLength = 256;
	private String hrtf_file = "db/CIPIC_hrtf_database/standard_hrir_database/subject_003/hrir_final.mat";
	
	public boolean normalizeSum = false;
	
	public HRTFRenderer(Node listenerNode, String filename, boolean normalizeSum) {
		this.node = listenerNode;
		this.filename = filename;
		
		this.hrtf = new HRTF(windowLength, hrtf_file);
		this.hrtf.startSpatial();
		this.normalizeSum = normalizeSum;
	}
	
	public void render() {
		ArrayList<Float>[] channels = node.getChannels();
		
		System.out.println("Rendering "+channels.length+" channels of audio HRTF'd...");
		
		float[] out = null;
		
		int channelNr = 0;
		
		for(ArrayList<Float> channel : channels) {
			// copy from arraylist
			float[] samples = new float[channel.size()];
			
			int idx = 0;
			for(float sample: channel) {
				samples[idx] = sample;
				idx++;
			}
			// init sum buffer (left+right samples)
			if(out == null) {
				out = new float[samples.length*2];
			}
			
			// setup loc+buf
			float[] azAndElev = node.azimuthAndElevation.get(channelNr);
			this.hrtf.setLocation(new PositionData(azAndElev[0], azAndElev[1]));
			float[] tmpRight = new float[samples.length];
			float[] tmpLeft  = new float[samples.length];
			
			this.hrtf.process(samples, tmpLeft, tmpRight);
			
			for(int i = 0; i < tmpLeft.length; i++) {
				out[2*i]   += tmpLeft[i];
				out[2*i+1] += tmpRight[i];
			}
			
			channelNr++;
			
		}
		
		if(normalizeSum == true) {
			// Normalize
			float max = Float.MIN_VALUE;
			for(int i= 0; i < out.length; i++) {
				if(out[i] > max) {
					max = out[i];
				}
			}

			for(int i= 0; i < out.length; i++) {
				out[i] = out[i]/max;
			}
		}
		
		Write.audio(out, this.filename, 2, 44100, 32);
	}
	
	
}
