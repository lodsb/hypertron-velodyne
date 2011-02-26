import java.io.*;


// NOTE:
// the python script for the CIPIC conversion calculates also the FFT and pads the original data
// to 256 bins;

public class HRIRLoader {
	// reimplemented from hrir_data_documentation.pdf
	
	private final int fftLen = 256;

	private final double[] azimuthDeg ={-80, -65, -55, -45, -40, -35, 
			-30,-25,-20,-15,-10,-5,0,5,10,
			15,20,25,30,35,40,45,55,65,80};

	private static double elevForm(int i) {
		return -45.0+(5.625*i);
	}

	public double getAzimuthError() {
		return 0.0;
	}

	public double getElevationError() {
		return 0.0;
	}


	private String path;
//	private double az;
//	private double el;
	public HRIRLoader(String path) {
		this.path = path;
	}
	
	// 3d -> left/right each real, imag arrays
	public double[][][] getImpulseResponses(double azimuth, double elevation) {
		System.err.println("AZ ELEV "+azimuth+" "+elevation);
		
		double[][] polarCoords = this.getNearestTwoPolarCoords(azimuth, elevation);

		double sL1[][] = this.loadIRFile(polarCoords[0][0], polarCoords[0][1], true);
		double sR1[][] = this.loadIRFile(polarCoords[0][0], polarCoords[0][1], false);

		double sL2[][] = this.loadIRFile(polarCoords[1][0], polarCoords[1][1], true);
		double sR2[][] = this.loadIRFile(polarCoords[1][0], polarCoords[1][1], false);
		
		double azDiff = Math.abs(polarCoords[1][0]-polarCoords[0][0]); 
		double azDiffRatio = Math.abs(polarCoords[1][0])/azDiff;
		
		double l[][] = null;
		double r[][] = null;
		
		// Fixme: recheck!
		if(sL1 != null && sL2 != null && sR1 != null && sR2 != null) {
			l = interpolate(sL1, sL2, azDiffRatio);
			r = interpolate(sR1, sR2, azDiffRatio);
		}
		
		return new double[][][]{l,r};
	}

	private double[][] interpolate(double[][] a, double[][] b, double ratio) {
		double[][] ret = new double[a.length][2];
		double inratio = 1.0-ratio;
		
		for(int i = 0; i < ret.length; i++) {
			ret[i][0] = (a[i][0]*ratio)+(b[i][0]*inratio);
			ret[i][1] = (a[i][1]*ratio)+(b[i][1]*inratio);
		}
		
		return ret;
	}
	
	private double[][] loadIRFile(double azi, double ele, boolean left) {
		double[][] ret = new double[fftLen][2];

		String filename;

		if(left) {
			filename = "L_";
		} else {
			filename = "R_";
		}

        System.out.println("Open file for azimuth: "+azi+" elev: "+ele);
		filename = filename+((int)(azi*100.0))/100+"_"+ele+".txt";

		System.out.print("Loading "+filename);

		// is this really the shortest way to do this? its fucking ugly but ought to be 
		// x-platform
		File file = new File(this.path + filename);
        if(file.exists()) System.out.println("...");

		try{
            BufferedReader br = new BufferedReader(new FileReader(file));

			for(int i = 0; i < ret.length; i++) {
				String strLine;

				if((strLine = br.readLine()) != null)   {
					
					String[] strings = strLine.split(" ");
					
					// real
					ret[i][0] = new Double(strings[0]);
					// imag
					ret[i][1] = new Double(strings[1]);
					
				} else {
					System.out.println("loaded "+i+" samples");
					break;
				}
			}

			br.close();

		}catch (Exception e){
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}

	private double[][] getNearestTwoPolarCoords(double az, double elev) {
		double azimuth  = this.pValDeg(az);
		double elevation= this.pValDeg(elev);

		if(azimuth < -90.0 || azimuth > 90.0) {
			// FIXME change channels? do some mapping stuff??

			azimuth = 90.0*Math.signum(azimuth);
		}


		double currDiff = Double.MAX_VALUE;
		double minAzOld = Double.MAX_VALUE;
        double minAz = Double.MAX_VALUE;

		for(double azDeg: azimuthDeg) {
            //System.out.println(azimuth+" "+minAz);
			if(Math.signum(azDeg) == Math.signum(azimuth) || azDeg == 0 ) {
				double diff = Math.abs(azDeg)-Math.abs(azimuth);
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

		double minElev = Double.MAX_VALUE;
		double minElevOld = Double.MAX_VALUE;
        currDiff = Double.MAX_VALUE;

		for(int i=0; i < 50; i++) {
            //System.out.println(elevation+" + "+minElev);

			double elevDeg = elevForm(i);

			if(Math.signum(elevDeg) == Math.signum(elevation)) {
				double diff = Math.abs(elevDeg)-Math.abs(elevation);
				if(diff <= currDiff) {
					minElevOld = minElev;
					minElev = elevDeg;
                    currDiff = diff;
				}
			}
		}

        if(minElevOld == Double.MAX_VALUE) {
            minElevOld = minElev;
        }

		return new double[][]{new double[]{minAzOld, minElevOld}, new double[]{minElev, minElevOld}};
	}



	// ret angle
	private double pValDeg(double angle) {
		double dtr = Math.PI/180.0;

		double angleRet = Math.atan2(Math.sin(angle*dtr), Math.cos(angle*dtr))/dtr;

		if(angleRet < -90.0) {
			angleRet = angleRet+360;
		}

		return angleRet;
	} 

}
