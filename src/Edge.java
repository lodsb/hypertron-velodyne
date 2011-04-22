
public class Edge<T extends Double> {
	public double dist;
	
	public boolean visited = false;
	public boolean feedforward = true;
	
	private DelayLine<T> dl;
	
	public Edge(double dist) {
		this.dist = dist;
	}
	
	public void createDelayLine() {
		this.dl = new DelayLine<T>(this.dist, HypertronVelodyne.sampleRate);
	}
	
	public DelayLine getDelayLine() {
		return dl;
	}
}
