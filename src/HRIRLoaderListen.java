import com.sun.media.sound.WaveFileReader;
import jm.util.Read;
import jass.generators.FFT;

import java.text.DecimalFormat;

public class HRIRLoaderListen implements IHRIRLoader {

	private String path;
	private String id;

	private int fftLen = 512;
	private int fftPow = 9;

	public int getFFTSize() {
		return this.fftLen;
	}

	public int getFFTPower() {
		return this.fftPow;
	}

	public HRIRLoaderListen(String path, String id) {
		this.path = path;
		this.id = id;
	}

	private int nearest15(int val) {
		int ret = val/15;

		return ret*15;
	}

	private final double[] elevDeg ={0,15,30,45/*,60*/,315,330,345};

	private int nearestElev(double elev) {
		double currDiff = Double.MAX_VALUE;
		double minAzOld = Double.MAX_VALUE;
        double minAz = Double.MAX_VALUE;

		for(double azDeg: elevDeg) {
			if(Math.signum(azDeg) == Math.signum(elev) || azDeg == 0 ) {
				double diff = Math.abs(azDeg-elev);
				if(diff <= currDiff) {
                    minAzOld = minAz;
                    minAz = azDeg;
					currDiff = diff;
				}
			}
		}

        if(minAzOld == Double.MAX_VALUE) {
            minAzOld = minAz;
        }

		return (int) minAzOld;
	}


	private DecimalFormat decFormat = new DecimalFormat("000");

	public double[][][] getImpulseResponses(double azimuth, double elevation) {
		azimuth = ((azimuth*180.0)+180) % 360.0;
		if(azimuth < 0) {azimuth += 360;}

		elevation = (elevation*180.0) % 360;
		if(elevation < 0) {elevation += 360;}

		System.err.println("az elev "+azimuth+" "+elevation+"  < "+nearest15((int)azimuth)+" "+nearestElev((int)elevation));
		String az   = String.format("%03d",nearest15(((int) (azimuth * 100.0)) / 100));
		String elev = String.format("%03d",nearestElev(elevation));

		String filePath = this.path+this.id+"_T"+az+"_P"+elev+".wav";

		float[] audio = new WavFileReader(filePath).getAudio();

		System.out.println(audio.length);

		double[] left 	= new double[audio.length/2];
		double[] right 	= new double[audio.length/2];

		for(int i = 0; i < audio.length/2; i++) {
			left[i] = audio[2*i];
			right[i]= audio[(2*i)+1];
		}

		/*double[] left_img  = new double[audio.length/2];
		double[] right_img = new double[audio.length/2];

		FFT fft = new FFT(this.fftPow);
		fft.doFFT(left, left_img, false);
		fft.doFFT(right, right_img, false);

          */
		/*for(int i = 0; i < left.length; i++) {
			left[i] = 1.0;
			right[i]= 1.0;

			left_img[i] = 0;
			right_img[i]= 0;

		} */


		return new double[][][]{new double[][]{left, left},new double[][]{right, right}};
	}
}
