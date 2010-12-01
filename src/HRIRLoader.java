
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
	
	
	
	public HRIRLoader(double az, double elev, String path) {
		
	}
	
	private float[][] getNearestTwoPolarCoords(double az, double elev) {
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
		
		return null;
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
