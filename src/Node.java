import java.util.ArrayList;
import java.util.LinkedList;

import davaguine.jeq.core.IIR;
import processing.core.PVector;


public class Node {
	public enum NodeType {
		wall,
		volume,
		listener,
		source,
	}
	
	public WavFileReader wavFileReader;

	public IIR eq = null;
	
	public NodeType type;
	public PVector pos;
	
	public LinkedList<float[]> azimuthAndElevation;
	
	private ArrayList<Float>[] channelData;
		
	public void createInputChannels(int channels) {
		this.channelData = new ArrayList[channels];
		
		for(int i = 0; i < channels; i++) {
			this.channelData[i] = new ArrayList<Float>();
		}
	}
	
	public void appendSample(int channel, float sample) {
		this.channelData[channel].add(sample); 
	}
	
	public ArrayList<Float>[] getChannels() {
		return this.channelData;
	}
	
	public String name; 
	
	public Node(PVector pos, NodeType type) {
		this.pos = pos;
		this.type = type;
	}
	
}
