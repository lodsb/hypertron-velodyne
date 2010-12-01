
public class DelayLine {
	private int SR;
	private double dist;
	private double time;
	
	private int currentSampleIndex = 0;
	
	private float[] delayLine; 
	private static int delayLineNr = 0;
	
	public DelayLine(double dist, int sampleRate) {
		this.dist = dist;
		this.time = dist/Model.speedOfSound;
		this.SR = sampleRate;
	
		int delayLineLength = (int)Math.round((this.time*((double) sampleRate)));
		delayLine = new float[delayLineLength];
		this.clearDelayLine();
		
		currentSampleIndex = 0;
		
		System.out.format("Delay Line #"+(delayLineNr++)+" size %20.2f KiB, length %20.2f s \n",(delayLine.length*2)/1024.0, dist);
	}
	
	private void clearDelayLine() {
		for(int i = 0; i < delayLine.length; i++) {
			delayLine[i] = 0.0f;
		}
	}
	
	public void incrementIndex() {
		currentSampleIndex = (currentSampleIndex+1) % (delayLine.length-1);
		delayLine[currentSampleIndex] = 0.0f;
	}
	
	public void addSample(float sample) {
		// add convolution with material filter here
		sample = Model.airAbsoption(this.dist, sample);
		delayLine[currentSampleIndex] = delayLine[currentSampleIndex]+sample; 
	} 
	
	public float getCurrentSample() {
		int index = (currentSampleIndex+1) % (delayLine.length-1);
		float outSample = delayLine[index];
		
		//if(outSample != 0) System.out.println(outSample);
		
		return outSample;
	}
}