import java.util.ArrayList;

import jm.util.Write;


public class HRTFRenderer {
	private String filename;
	private Node node; 
	
	//private static int windowLength = 256;
	//private String hrtf_files = "db/cipic/CIPIC_hrtf_database/standard_hrir_database/subject_009/";

	//private IHRIRLoader hrirLoaderCIPIC = new HRIRLoaderCIPIC(hrtf_files);

	private String hrtf_listen =  "/home/lodsb/Downloads/listen/COMPENSATED/WAV/IRC_1002_C/";
	private IHRIRLoader hrirLoaderListen = new HRIRLoaderListen(hrtf_listen, "IRC_1002_C_R0195");

	private static IHRIRLoader hrirLoader;
	
	public boolean normalizeSum = false;
	
	public HRTFRenderer(Node listenerNode, String filename, boolean normalizeSum) {
		this.node = listenerNode;
		this.filename = filename;
		this.normalizeSum = normalizeSum;

		hrirLoader = hrirLoaderListen;
	}


	public static IHRIRLoader getHRIRRenderer() {
		return hrirLoader;
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

			//azAndElev[0] = azAndElev[0] * 180.0f;
			//azAndElev[1] = azAndElev[1] * 180.0f;

			double[][][] ir = this.hrirLoader.getImpulseResponses(azAndElev[0],azAndElev[1]);
			
			HRTFConv hrtf = new HRTFConv(ir);
			
			float[] tmpRight = new float[samples.length];
			float[] tmpLeft  = new float[samples.length];
			
			hrtf.process(samples, tmpLeft, tmpRight);

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
