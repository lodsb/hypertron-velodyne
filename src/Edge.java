
public class Edge {
	public double dist;
	
	public boolean visited = false;
	public boolean feedforward = true;
	
	private DelayLine dl;
	
	public Edge(double dist) {
		this.dist = dist;
	}
	
	public void createDelayLine() {
		this.dl = new DelayLine(this.dist, ObjViewer.sampleRate);
	}
	
	public DelayLine getDelayLine() {
		return dl;
	}
}
