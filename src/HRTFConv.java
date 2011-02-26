
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


import jass.generators.FFT;
import jass.generators.RectPolar;

import java.io.*;

/**
   class for using CIPIC-type HRTF files
 */

public class HRTFConv {

	boolean doSpatial = true;

	static final boolean DEBUG = false;

	// working buffers
	double [] hrtfL;
	double [] hrtfR;
	double [] hrtfL_img;
	double [] hrtfR_img;

	// buffers for the separated right/left channels of original sample
	// imaginary ones needed for FFT
	double [] channelL;
	double [] channelL_img;
	double [] channelR;
	double [] channelR_img;

	int windowLength = 256;   // FFT window length used

	FFT fft;

	float headRadius = 0.3f; // meters

	float gain = 1000;  // for some reason, these HRTF's make everything _very_ quiet.

	// conversion between rectangular/ polar
	RectPolar rp = new RectPolar(headRadius, windowLength);

	/* 
       takes the window size in samples
       and the filename of the HRTF data
	 */
	public HRTFConv (double[][][] irs) {

		// this is dependent on window size. 
		// figure out the formula later!
		fft = new FFT(8);

		// put HRTF data here

		// intermediate buffers for separated signal
		channelL =     new double[windowLength];
		channelR =     new double[windowLength];

		channelL_img = new double[windowLength];
		channelR_img = new double[windowLength];

		// intermediate buffers for HRTF
		hrtfL = 	irs[0][0];
		hrtfL_img = irs[0][1];

		hrtfR = 	irs[1][0];
		hrtfR_img = irs[1][1];

		// zero imaginary portion
		for(int i = 0; i < channelL_img.length; i++) {
			channelL_img[0] = 0.0;
			channelR_img[0] = 0.0;		    
		}	

        this.doSpatial = true;
	}

	/**
	 * spatialize a mono audio buffer
	 * at this point, the HRTF has already been prepared     
     @param buf mono audio buffer to process. 
     @param outL left channel output 
     @param outR right channel output
	 */    
	public void process(float [] buf, float [] outL, float [] outR) 
	{
		int numWindows = buf.length / windowLength;

		// convert to input buf to double and
		// separate them into left/right channels	    
		int index = 0;  //keeps track of window-index.
		for (int i = 0; i < numWindows; i++) {
			// copy source into left and right channels.
			separate(buf, index, index+windowLength, channelL, channelR);

				// zero imaginary part
				for (int k = 0; k< channelL_img.length; k++) {
					channelL_img[k] = 0;
				}

				// fft input signal
				fft.doFFT(channelL, channelL_img, false);
				fft.doFFT(channelR, channelR_img, false);
				if (DEBUG) {
					if (i == 0) {
						System.out.println("orig");
						for (int k = 0; k < windowLength; k++) {
							System.out.println(channelL[k] + " " + channelL_img[k]);
						}
					}
					if (i == 0) {
						System.out.println("hrtf_______________");
						for (int k = 0; k < windowLength; k++) {
							System.out.println(hrtfL[k] + " " + hrtfL_img[k]);
						}
					}
				}

				// multiply signals together
				rp.multiplyRect(hrtfL, hrtfL_img, channelL, channelL_img);
				rp.multiplyRect(hrtfR, hrtfR_img, channelR, channelR_img);

				if (DEBUG) {
					if (i == 0) {
						System.out.println("after");
						for (int k = 0; k < windowLength; k++) {
							System.out.println(channelL[k] + " "+ channelL_img[k]);
						}
					}
				}

				// inverse fft
				fft.doFFT(channelL, channelL_img, true);
				fft.doFFT(channelR, channelR_img, true);
				// copy window to output array 
				for (int j = 0; j < windowLength; j++ ) {
					outL[index + j] = gain*(float)channelL[j];
					outR[index + j] = gain*(float)channelR[j];
				}
			index +=  windowLength;
		}
	}

	/**
       right now, this just copies 
	 */
	public void separate(float [] inputBuf, int start, int end,
			double [] outLeft, 
			double [] outRight) {
		int j = 0;
		for (int i = start; i < end; i++) {
			outLeft[j] = inputBuf[i];
			outRight[j] = inputBuf[i];
			j++;
		}
	}

}
