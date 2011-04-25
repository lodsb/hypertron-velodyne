public interface IHRIRLoader {
	public double[][][] getImpulseResponses(double azimuth, double elevation);

	public int getFFTSize();
	public int getFFTPower(); // too lazy
}
