
import java.util.LinkedList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;


public class Processor implements Runnable {

	private Model model;
	private boolean initialized = false;
	private int samplesToProcess = 1*44100;
	
	public Processor(Model model) {
		this.model = model;
		
	}
	
	// parallelize?! geht evtl weil ja intern nur Sum auf delay lines
	
	@Override
	public void run() {
	
		long startTime = System.currentTimeMillis();
		
		if(!initialized) {
			for(Edge edge: this.model.getModelData().graph.getEdges()) {
				edge.createDelayLine();
			}
			
			int i = 0;
			for(Node node: this.model.getModelData().sourceNodes) {
				switch(i) {
				case 0:
					node.wavFileReader = new WavFileReader("/home/lodsb/OB8Birds.wav");
					break;
				case 1:
					node.wavFileReader = new WavFileReader("/home/lodsb/Obifant.wav");
					break;
				default:
					node.wavFileReader = new WavFileReader("/home/lodsb/test_sound_obj.wav");
				}
				i++;
			}
			
			/*
			for(Node node: this.model.getModelData().listenerNodes) {
				// get headpositions here...
				int numInEdges = this.model.getModelData().graph.getInEdges(node).size();
				node.createInputChannels(numInEdges);
			}
			*/
			
			initialized = true;
		
		}
		
		for(int sampleNr = 0; sampleNr< samplesToProcess; sampleNr++) {
			if(sampleNr%100 == 0) {System.out.print("Processing sample #"+sampleNr+" ...");}
			
			processGraph(this.model.getModelData());
			t++;
			if(sampleNr%100 == 0) {System.out.println("done!");}
		}
		
		System.out.println("Processed "+samplesToProcess+" in "+(System.currentTimeMillis()-startTime)+"ms");
		
		int i = 0;
		for(Node node: this.model.getModelData().listenerNodes) {
			new HRTFRenderer(node, i+""+node.name+".wav", true).render();
			i++;
		}
	}

	int t = 0;
	// process SINGLE SAMPLE from sources
	private void processGraph(Model.GraphModelData md) {
		
		for(Node node: md.sourceNodes) {
			float inputSample = node.wavFileReader.getNextSample();
	/*		
			if(t < 10) {
				inputSample = 1.0f;
			} else {
				inputSample = 0.0f;
			}
	*/
			distributeSample(md.graph, node, inputSample);
			//traverseGraph(md.graph, node, inputSample);
		}
		
		for(Node node: md.graph.getVertices()) {
			//if(node.type == Node.NodeType.source)
			//	continue;
			
			float sample = 0.0f;
			for(Edge edge: md.graph.getInEdges(node)) {
				sample =  sample + edge.getDelayLine().getCurrentSample();
				edge.visited = true;
			}
			
			distributeSample(md.graph, node, sample);
		}
		
		float outSample = 0;
		for(Node node: md.listenerNodes) {
			int channel = 0;
			
			for(Edge edge: md.graph.getInEdges(node)) {
				float c = 1.0f;
				if(md.graph.getSource(edge).type == Node.NodeType.source)
					c = 0.1f;
				
				outSample = outSample + c*edge.getDelayLine().getCurrentSample();
				//if(outSample != 0) System.out.println(outSample+ " chan "+channel+" node "
				//		+ md.graph.getPredecessorCount(node) );
				
				node.appendSample(channel, outSample);
				channel++;
			}
		}


		for(Edge edge: md.graph.getEdges()) {
			edge.visited = false;
			edge.getDelayLine().incrementIndex();
		}
		
	}
	
	private void distributeSample(DirectedSparseGraph<Node, Edge> graph, Node node, float sample) {
		// distribute energy equally
		int numOutputs = graph.getOutEdges(node).size();
		sample = sample/numOutputs;
		
		for(Edge edge: graph.getOutEdges(node)) {
			edge.visited = true;
			edge.getDelayLine().addSample(sample);
		}
	}
}
