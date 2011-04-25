// class file for HRTF data


// organized in terms of azimuth (naz) elevation (nel) time (nt)
// right now as a 25x50x200 data structure, as organized in CIPIC

/*
  % two arrays: hrtf_l and hrtf
  % indices naz, nel, nt  (azimuth, elevation, time)
  % (25x50x200) 3 dimensional array 
  % in matlab,  azimuth angle corresponding to nax  is the naz'th element
  % of the vector 
  % azimuths = [-80 -65 -55 -45:5:45 55 65 80]
  % elevations range from -45 to +230.625 in steps of 5.625
  % this angular  increment divides the full circle  into 64 equal parts,
  % but only 50 values are used
  %  elevation angle corresponding  to nel  is the  nel-th element  of the
  % vector elevations = -45 + 5.625*(0:49)
  % temporal

      @author Reynald Hoskinson (reynald@cs.ubc.ca), Niklas Klügel
 */


import ddf.minim.analysis.FFT;
import ddf.minim.analysis.HannWindow;
import jass.generators.RectPolar;

import java.io.*;

/**
 * class for using CIPIC-type HRTF files
 */

public class HRTFConv {

	boolean doSpatial = true;

	static final boolean DEBUG = false;

	// working buffers
	double[] hrtfL;
	double[] hrtfR;
	double[] hrtfL_img;
	double[] hrtfR_img;

	// buffers for the separated right/left channels of original sample
	// imaginary ones needed for FFT
	float[] channelL;
	float[] channelL_img;
	float[] channelR;
	float[] channelR_img;

	float[] channelLResult;
	float[] channelRResult;

	int windowLength = HRTFRenderer.getHRIRRenderer().getFFTSize();
	int fftPow = HRTFRenderer.getHRIRRenderer().getFFTPower();

	FFT fftL;
	FFT fftR;

	double gain = 1000000;  // for some reason, these HRTF's make everything _very_ quiet.

	/* 
       takes the window size in samples
       and the filename of the HRTF data
	 */

	double[] hrtfTest = {
		1.49812820e-08, 8.91756089e-08, -3.67706084e-08,
		2.50624447e-07, -1.21275185e-05, -4.86740394e-05,
		-8.21090000e-05, -3.40455368e-04, -6.88820770e-04,
		-1.49575507e-03, -6.54637859e-03, 1.37775482e-02,
		1.68365871e-02, -2.19209189e-02, 4.76669450e-03,
		-2.44267626e-03, -2.21061052e-02, -4.08494119e-03,
		8.53984804e-03, 5.33068988e-03, -1.38306311e-02,
		9.85981614e-03, -2.27866404e-02, 4.59322610e-02,
		-1.21726422e-02, 5.95650850e-01, 1.24407308e+00,
		-7.18469437e-02, -5.52192655e-01, -2.80034710e-01,
		-4.20492359e-01, -9.70572784e-02, -5.45128315e-01,
		-7.57956209e-01, 2.13109851e-01, 1.18744097e+00,
		2.75420803e-01, -4.55404022e-01, 4.67930840e-02,
		-4.00728109e-01, -1.46669583e-01, 1.77907532e-01,
		-1.71207849e-01, -1.13358730e-01, -3.01583293e-02,
		-2.46908189e-02, -4.35798611e-02, 6.04718618e-02,
		-8.86273443e-02, -3.63252325e-02, 5.07939359e-02,
		-9.03330620e-02, 3.26838771e-02, -5.12528462e-02,
		-1.21570469e-01, 2.42372328e-04, 4.09826386e-02,
		2.70953545e-02, 4.35317584e-02, 4.12253030e-02,
		3.77607747e-03, 3.89370943e-02, -5.11425710e-02,
		-1.09065397e-01, -3.36371794e-03, -1.26162030e-03,
		-5.82724189e-04, 3.64816494e-02, -3.62469337e-03,
		-3.89816215e-04, 5.51602181e-02, 1.48857607e-02,
		-2.51715599e-02, 1.72831710e-03, -5.60462986e-05,
		2.06131275e-02, 3.79451636e-02, 4.83434395e-04,
		-1.25518171e-02, -8.54836586e-03, -1.53476064e-02,
		-4.50772339e-03, 7.64135810e-03, -4.96364571e-03,
		1.23595637e-02, 1.92180581e-02, -1.05122585e-02,
		2.78070441e-03, 7.69650022e-03, -8.20642675e-03,
		9.67962563e-03, 5.25035153e-03, -9.44596029e-03,
		1.57279968e-02, 2.22953692e-02, 5.86879598e-03,
		8.39715732e-03, 7.92702112e-03, 4.73075667e-03,
		9.64872109e-03, -2.50495376e-03, -1.55963113e-02,
		8.04371328e-03, 1.33657921e-02, 4.58401799e-03,
		7.33871384e-03, -5.56337516e-03, -8.10712149e-03,
		-1.04435768e-02, -2.06834142e-02, -2.05423558e-02,
		-1.87628009e-03, 5.38305023e-03, -1.09254359e-02,
		8.89050827e-03, 1.66726445e-02, 4.78178313e-03,
		-5.33364998e-04, -1.12849522e-02, -7.61447229e-03,
		-1.07312131e-02, -1.45569377e-02, -1.08685529e-02,
		-1.03019816e-03, 4.16433479e-03, -3.32220968e-04,
		2.93243573e-03, 1.77460837e-03, -4.82020615e-04,
		-2.36072640e-03, -9.32430700e-03, -1.03840672e-02,
		-1.43629807e-02, -2.03298116e-02, -1.16214420e-02,
		1.70920262e-03, 8.00513300e-03, 8.54659819e-03,
		9.90191483e-03, 1.22147759e-02, 1.26768116e-02,
		1.88540023e-03, -1.02374257e-02, -9.98919150e-03,
		-3.87721009e-03, -6.33997515e-04, 1.26793709e-04,
		2.93543413e-03, 2.24285886e-03, 4.38243698e-03,
		4.52499993e-03, -2.87458635e-03, -5.11411318e-03,
		-1.50032626e-02, -3.29386417e-02, -1.81534596e-02,
		1.16293064e-02, 1.35472310e-02, 1.17717188e-02,
		4.00971449e-03, -9.49155853e-03, 7.44744813e-04,
		-4.85888625e-03, -1.93141157e-02, -1.25741179e-04,
		6.16236139e-03, -6.88484582e-03, -1.87656377e-03,
		4.74176267e-03, -4.40611466e-03, -7.96667348e-03,
		-4.99630698e-03, 2.03818096e-03, 1.56592471e-02,
		6.64670964e-03, -9.20124803e-03, -5.08381282e-03,
		-3.94072882e-03, -5.13097775e-03, -9.82466277e-05,
		-6.14851299e-04, -3.09357247e-03, 1.01123927e-04,
		1.37811387e-03, 8.60579007e-07, 5.62035143e-04,
		-1.09807473e-03, -1.88313009e-03, 3.01935442e-04,
		2.19736377e-05, -1.71165310e-03, -1.73212529e-03,
		-7.62456358e-04, 1.27693057e-06, 3.54994723e-04,
		-4.93005227e-05, -2.64212302e-04, 2.55524297e-07,
		2.52699778e-05, -2.20456016e-05,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0
	};

	double[] hrtfTest2 = {
		1.0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0
	};

	private void multiplyRect(double[] a, double[] aI,
							  float[] b, float[] bI) {
		// assume for now same length
		// puts result into b
		for (int i = 0; i < a.length; i++) {
			b[i] = (float) (a[i] * b[i] - aI[i] * bI[i]);
			bI[i] = (float) (aI[i] * b[i] + a[i] * bI[i]);
		}
	}


	private void multiplyRect(double[] a, double[] aI,
							  float[] b, float[] bI, float[] c, float[] cI) {
		// assume for now same length
		// puts result into b
		for (int i = 0; i < a.length; i++) {
			c[i] = (float) (a[i] * b[i] - aI[i] * bI[i]);
			cI[i] = (float) (aI[i] * b[i] + a[i] * bI[i]);
		}
	}

	double deltaTheta = (2 * Math.PI) / windowLength;

	private double hanningWindow(double sample, int index) {
		return (1.07672 - 0.92328 * Math.cos(index * deltaTheta)) * sample;
	}

	public HRTFConv(double[][][] irs) {

		// this is dependent on window size. 
		// figure out the formula later!
		fftL = new FFT(this.windowLength, Processor.SR);
		fftR = new FFT(this.windowLength, Processor.SR);

		fftL.window(new HannWindow());
		fftR.window(new HannWindow());

		// put HRTF data here

		// intermediate buffers for separated signal
		channelL = new float[windowLength];
		channelR = new float[windowLength];

		channelL_img = new float[windowLength];
		channelR_img = new float[windowLength];

		channelLResult = new float[windowLength];
		channelRResult = new float[windowLength];

		// intermediate buffers for HRTF
		hrtfL = irs[0][0];
		hrtfL_img = irs[0][1];

		hrtfR = irs[1][0];
		hrtfR_img = irs[1][1];


		/*
		hrtfL = new double[windowLength];
		hrtfR = new double[windowLength];
		hrtfL_img = new double[windowLength];
		hrtfR_img = new double[windowLength];

		for(int i = 0; i < hrtfL.length; i++) {
			hrtfL[i] = 1.0;
			hrtfR[i] = 1.0;
			hrtfL_img[i] = 0.0;
			hrtfR_img[i] = 0.0;
		}


		for(int i = 0; i < hrtfL.length; i++) {
			System.out.println(hrtfL[i]+" "+hrtfR[i]+" img "+hrtfL_img[i]+" "+hrtfR_img[i]);
		}*/


		// zero imaginary portion
		for (int i = 0; i < channelL_img.length; i++) {
			channelL_img[0] = 0.0f;
			channelR_img[0] = 0.0f;
		}

		this.doSpatial = true;
	}

	/**
	 * spatialize a mono audio buffer
	 * at this point, the HRTF has already been prepared
	 *
	 * @param buf  mono audio buffer to process.
	 * @param outL left channel output
	 * @param outR right channel output
	 */
	public void process(float[] buf, float[] outL, float[] outR) {
		System.err.println("hrtfs " + hrtfL.length);


		for (int i = 0; i < outL.length; i++) {
			for (int j = 0; j < hrtfL.length; j++) {
				if (i - j < 0 || i - j > buf.length) continue;
				if(i >= outL.length) break;
				outL[i] += hrtfL[j] * buf[i - j];
				outR[i] += hrtfR[j] * buf[i - j];
			}
		}

		//fuckit - fft+propper window => argh.
		/*int numWindows = (buf.length / windowLength); //overlap 50%

		int index = 0;
		int indexInc = windowLength; // 50% overlap

		for (int i = 0; i < numWindows; i++) {
			// copy source into left and right channels.
			separate(buf, index, index + indexInc, channelL, channelR);


			for (int k = 0; k < channelL_img.length; k++) {
			//	channelL[k] = hanningWindow(channelL[k], k);
			//	channelR[k] = hanningWindow(channelR[k], k);

				channelL_img[k] = 0;
				channelR_img[k] = 0;

				channelLResult[k] = 0;
				channelRResult[k] = 0;
			}

			// fft input signal
			fftL.forward(channelL);
			fftR.forward(channelR);
			this.multiplyRect(hrtfL, hrtfL_img,
							fftL.getSpectrumReal(),
							fftL.getSpectrumImaginary(),
							channelL, channelL_img);


			this.multiplyRect(hrtfR, hrtfR_img,
							fftR.getSpectrumReal(),
							fftR.getSpectrumImaginary(),
							channelR, channelR_img);

			// multiply signals together
			//multiplyRect(hrtfL, hrtfL_img, channelL, channelL_img);
			//multiplyRect(hrtfR, hrtfR_img, channelR, channelR_img);

			// inverse fft
			fftL.inverse(channelL);
			fftR.inverse(channelR);
			// copy window to output array
			for (int j = 0; j < indexInc; j++) {


				outL[index + j] += (float) (gain * channelL[j]);
				outR[index + j] += (float) (gain * channelR[j]);

				//System.out.println(outL[index + j]+" "+outR[index + j]);

			}
			index += indexInc;
		}   */
	}

	/**
	 * right now, this just copies
	 */
	public void separate(float[] inputBuf, int start, int end,
						 float[] outLeft,
						 float[] outRight) {
		int j = 0;
		for (int i = start; i < end; i++) {
			outLeft[j] = inputBuf[i];
			outRight[j] = inputBuf[i];
			j++;
		}
	}

}
