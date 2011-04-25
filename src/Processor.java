import davaguine.jeq.core.IIR;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class Processor<T extends Double> implements Runnable {

	private Model<T> model;
	private boolean initialized = false;
	public static float SR = 44100f;
	private int samplesToProcess = (int)SR*5 ;
	public Processor(Model<T> model) {
		this.model = model;

		for(Node n: this.model.getGraph().getVertices()) {
			if(n.type == Node.NodeType.wall) {
				n.eq = createEqualizer();
			}
		}

	}

	private IIR createEqualizer() {
		return new IIR(10, SR , 1);
	}
	
	// parallelize?! geht evtl weil ja intern nur Sum auf delay lines
	
	@Override
	public void run() {
	
		long startTime = System.currentTimeMillis();
		
		if(!initialized) {
			for(Edge<T> edge: this.model.getModelData().graph.getEdges()) {
				edge.createDelayLine();
			}
			
			int i = 0;
            String usrDir = System.getProperty("user.dir");

			for(Node node: this.model.getModelData().sourceNodes) {
				switch(i) {
				case 0:
					node.wavFileReader = new WavFileReader("/usr/lib/pd-extended/extra/ekext/examples/beauty.wav");
					break;
				case 1:
					node.wavFileReader = new WavFileReader("/usr/lib/pd-extended/extra/ekext/examples/drummach.wav");
					break;
				default:
					node.wavFileReader = new WavFileReader("/usr/lib/pd-extended/extra/ekext/examples/stink.wav");
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
		
		for(Node node: (LinkedList<Node>) md.sourceNodes) {
			float inputSample =  node.wavFileReader.getNextSample();
	/*		
			if(t < 10) {
				inputSample = 1.0f;
			} else {
				inputSample = 0.0f;
			}
	*/
			distributeSample(md.graph, node, (double)inputSample);
			//traverseGraph(md.graph, node, inputSample);
		}
		
		for(Node node: (Collection<Node>)  md.graph.getVertices()) {
			//if(node.type == Node.NodeType.source)
			//	continue;
			
			double sample = 0;
			for(Edge edge: (Collection<Edge>) md.graph.getInEdges(node)) {
				sample =  sample + edge.getDelayLine().getCurrentSample();
				edge.visited = true;
			}
			
			distributeSample(md.graph, node, sample);
		}
		
		double outSample = 0;
		for(Node node: (LinkedList<Node>) md.listenerNodes) {
			int channel = 0;
			
			for(Edge edge: (Collection<Edge>) md.graph.getInEdges(node)) {
				double c =  1.0;
				if(((Node)md.graph.getSource(edge)).type == Node.NodeType.source)
					c =  0.1;
				
				outSample = outSample + c*edge.getDelayLine().getCurrentSample();
				//if(outSample != 0) System.out.println(outSample+ " chan "+channel+" node "
				//		+ md.graph.getPredecessorCount(node) );

				node.appendSample(channel, (float)outSample);
				channel++;
			}
		}


		for(Edge edge: (Collection<Edge>) md.graph.getEdges()) {
			edge.visited = false;
			edge.getDelayLine().incrementIndex();
		}
		
	}
	
	private void distributeSample(DirectedSparseGraph<Node, Edge> graph, Node node, double sample) {
		// distribute energy equally
		int numOutputs = graph.getOutEdges(node).size();
		sample = sample/numOutputs;
		
		for(Edge edge: graph.getOutEdges(node)) {
			edge.visited = true;
			edge.getDelayLine().addSample(sample);
		}
	}
}
