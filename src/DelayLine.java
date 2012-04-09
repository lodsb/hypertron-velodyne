public class DelayLine<T extends Double> {
	static private int SR;
	private double dist;
	private double time;

	private int currentSampleIndex = 0;

	private double[] delayLine;
	private static int delayLineNr = 0;

	public DelayLine(double dist, int sampleRate) {
		this.dist = dist;
		this.time = dist / Model.speedOfSound;
		this.SR = sampleRate;

		int delayLineLength = (int) Math.round((this.time * ((double) sampleRate)));
		delayLine = new double[delayLineLength];
		this.clearDelayLine();

		currentSampleIndex = 0;

		System.out.format("Delay Line #" + (delayLineNr++) + " size %8.2f KiB, length %8.2f s \n", (delayLine.length * 4) / 1024.0, (delayLine.length) / ((double)SR));
	}

	private void clearDelayLine() {
		for (int i = 0; i < delayLine.length; i++) {
			delayLine[i] = 0.0;
		}
	}

	public void incrementIndex() {
		currentSampleIndex = (currentSampleIndex + 1) % (delayLine.length - 1);
		delayLine[currentSampleIndex] = 0.0;
	}

	public void addSample(double sample) {
		// add convolution with material filter here
		delayLine[currentSampleIndex] = delayLine[currentSampleIndex] + sample;
	}

	public double getCurrentSample() {
		double ret = 0.0;
		//interpolate?
		if (currentSampleIndex + 1 == delayLine.length - 1) {
			ret =  (delayLine[delayLine.length - 1])*0.5;
			int index = (currentSampleIndex + 1) % (delayLine.length - 1);
			ret = ret+(delayLine[index]*0.5);
		} else {
			int index = (currentSampleIndex + 1) % (delayLine.length - 1);
			ret = delayLine[index];
		}

		//if(outSample != 0) System.out.println(outSample);
		ret = Model.airAbsoption(this.dist, ret);

		return ret;
	}
}
