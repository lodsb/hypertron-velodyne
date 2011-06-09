import java.util.Random;

public class Edge<T extends Double> {
	public double dist;
	
	public boolean visited = false;
	public boolean feedforward = true;
	
	private DelayLine<T> dl;
	
	public Edge(double dist) {
		this.dist = dist;
	}

	private static Random rnd = new Random();
	public void createDelayLine() {
		this.dl = new DelayLine<T>(this.dist, HypertronVelodyne.sampleRate);
	}
	
	public DelayLine getDelayLine() {
		return dl;
	}
}
