import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class HRIRLoader {
	// reimplemented from hrir_data_documentation.pdf

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
	private double az;
	private double el;
	public HRIRLoader(double az, double elev, String path) {
		this.path = path;
		this.az = az;
		this.el = elev;
	}

	private double[][] getImpulseResponses(double azimuth, double elevation) {
		double[][] polarCoords = this.getNearestTwoPolarCoords(azimuth, elevation);

		double sL1[] = this.loadIRFile(polarCoords[0][0], polarCoords[0][1], true);
		double sR1[] = this.loadIRFile(polarCoords[0][0], polarCoords[0][1], false);

		double sL2[] = this.loadIRFile(polarCoords[1][0], polarCoords[1][1], true);
		double sR2[] = this.loadIRFile(polarCoords[1][0], polarCoords[1][1], false);
		
		double azDiff = Math.abs(polarCoords[1][0]-polarCoords[0][0]); 
		double azDiffRatio = Math.abs(polarCoords[1][0])/azDiff;
		
		double l[] = null;
		double r[] = null;
		
		// Fixme: recheck!
		if(sL1 != null && sL2 != null && sR1 != null && sR2 != null) {
			l = interpolate(sL1, sL2, azDiffRatio);
			r = interpolate(sR1, sR2, azDiffRatio);
		}
		
		return new double[][]{l,r};
	}

	private double[] interpolate(double[] a, double[] b, double ratio) {
		double[] ret = new double[a.length];
		double inratio = 1.0-ratio;
		
		for(int i = 0; i < ret.length; i++) {
			ret[i] = (a[i]*ratio)+(b[i]*inratio);
		}
		
		return ret;
	}
	
	private double[] loadIRFile(double azi, double ele, boolean left) {
		double[] ret = new double[200];

		String filename;

		if(left) {
			filename = "L_";
		} else {
			filename = "R_";
		}

		filename = filename+Math.round(azi)+"_"+ele+".txt";

		System.out.print("Loading "+filename+" ... ");

		// is this really the shortest way to do this? its fucking ugly but ought to be 
		// x-platform
		File path = new File(this.path);
		File file = new File(path, filename);

		try{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));


			for(int i = 0; i < ret.length; i++) {
				String strLine;

				if((strLine = br.readLine()) != null)   {
					
					ret[i] = new Double(strLine);
					
				} else {
					System.out.println("loaded "+i+" samples");
					break;
				}
			}

			in.close();

		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
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


		double minAz = Double.MAX_VALUE;
		double minAzOld = Double.MAX_VALUE;

		for(double azDeg: azimuthDeg) {
			if(Math.signum(azDeg) == Math.signum(azimuth)) {
				double diff = Math.abs(azDeg-azimuth);
				if(diff < minAz) {
					minAzOld = minAz;
					minAz = diff;
				}
			}
		}

		double minElev = Double.MAX_VALUE;
		double minElevOld = Double.MAX_VALUE;

		for(int i=0; i < 50; i++) {
			double elevDeg = elevForm(i);

			if(Math.signum(elevDeg) == Math.signum(elevation)) {
				double diff = Math.abs(elevDeg-elevation);
				if(diff < minAz) {
					minElevOld = minElev;
					minElev = diff;
				}
			}
		}

		return new double[][]{new double[]{minAz, minElev}, new double[]{minAzOld, minElevOld}};
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
