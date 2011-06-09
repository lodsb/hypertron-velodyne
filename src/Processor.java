import davaguine.jeq.core.IIR;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class Processor<T extends Double> implements Runnable {

	private Model<T> model;
	private boolean initialized = false;
	public static float SR = 44100f;
	private int samplesToProcess = (int) SR * 4;

	private Object lock = new Object();

	int numThreads = 8;

	public Processor(Model<T> model) {
		this.model = model;

		for (Node n : this.model.getGraph().getVertices()) {
			if (n.type == Node.NodeType.wall) {
				n.eq = createEqualizer();
			}
		}


	}

	CyclicBarrier barrierEnd = new CyclicBarrier(numThreads + 1);
	CyclicBarrier barrierStart = new CyclicBarrier(numThreads + 1);
	LinkedList<Computation> comps = new LinkedList<Computation>();

	public class Computation implements Runnable {
		public Collection<Node> nodes;
		public Model.GraphModelData md;
		public boolean running = true;

		@Override
		public void run() {

			while (running) {
				try {
					barrierStart.await();
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (Node node : nodes) {
					calcSample(node, md);
				}
				try {
					barrierEnd.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	private IIR createEqualizer() {
		return new IIR(10, SR, 1);
	}

	// parallelize?! geht evtl weil ja intern nur Sum auf delay lines

	@Override
	public void run() {

		long startTime = System.currentTimeMillis();

		if (!initialized) {
			for (Edge<T> edge : this.model.getModelData().graph.getEdges()) {
				edge.createDelayLine();
			}

			int i = 0;
			String usrDir = System.getProperty("user.dir");

			for (Node node : this.model.getModelData().sourceNodes) {
				switch (i) {
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

			LinkedList<Node>[] a = new LinkedList[numThreads];
			for (int kk = 0; kk < a.length; kk++) {
				a[kk] = new LinkedList<Node>();
			}

			System.err.println("**** "+this.model.getGraph().getVertices().size());
			int mod = 0;
			for (Node node : this.model.getGraph().getVertices()) {
				a[mod].add(node);
				mod = (mod + 1) % (a.length - 1);
			}

			int sizeLists = 0;

			for (int k = 0; k < numThreads; k++) {
				Computation c = new Computation();
				c.md = this.model.getModelData();
				c.nodes = a[k];

				sizeLists = sizeLists + c.nodes.size();

				Thread t = new Thread(c);
				this.comps.add(c);
				threads.add(t);
				t.start();
			}

			System.err.println("????????? "+sizeLists);

		}

		for (int sampleNr = 0; sampleNr < samplesToProcess; sampleNr++) {
			if (sampleNr % 100 == 0) {
				System.out.print("Processing sample #" + sampleNr + " ...");
			}

			processGraph(this.model.getModelData());
			t++;
			if (sampleNr % 100 == 0) {
				System.out.println("done!");
			}
		}

		for(Computation c: comps) {
			c.running = false;
		}

		System.out.println("Processed " + samplesToProcess + " in " + (System.currentTimeMillis() - startTime) + "ms");

		int i = 0;
		for (Node node : this.model.getModelData().listenerNodes) {
			new HRTFRenderer(node, i + "" + node.name + ".wav", true).render();
			i++;
		}
	}


	private void calcSample(Node node, Model.GraphModelData md) {
		double sample = 0;
		for (Edge edge : (Collection<Edge>) md.graph.getInEdges(node)) {
			synchronized(edge) {
				sample = sample + edge.getDelayLine().getCurrentSample();
				edge.visited = true;
			}
		}

		distributeSample(md.graph, node, sample);
	}

	int t = 0;

	LinkedList<Thread> threads = new LinkedList<Thread>();
	AtomicBoolean startB = new AtomicBoolean();

	// process SINGLE SAMPLE from sources
	private void processGraph(Model.GraphModelData md) {

		for (Node node : (LinkedList<Node>) md.sourceNodes) {
			float inputSample = node.wavFileReader.getNextSample();
			/*
			  if(t < 10) {
				  inputSample = 1.0f;
			  } else {
				  inputSample = 0.0f;
			  }
	  */
			distributeSample(md.graph, node, (double) inputSample);
			//traverseGraph(md.graph, node, inputSample);
		}
	  /*
		     	for (Node node : this.model.getGraph().getVertices()) {
					calcSample(node, md);
				}
       */

		try {
			barrierStart.await();
			barrierStart.reset();
			//System.err.println("waiting");
			barrierEnd.await();
			barrierEnd.reset();

		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (BrokenBarrierException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		double outSample = 0;
		for (Node node : (LinkedList<Node>) md.listenerNodes) {
			int channel = 0;

			for (Edge edge : (Collection<Edge>) md.graph.getInEdges(node)) {
				double c = 1.0;

				/*if (((Node) md.graph.getSource(edge)).type == Node.NodeType.source)
					c = 0.1;

				*/
				outSample = outSample + c * edge.getDelayLine().getCurrentSample();
				//if(outSample != 0) System.out.println(outSample+ " chan "+channel+" node "
				//		+ md.graph.getPredecessorCount(node) );

				node.appendSample(channel, (float) outSample);
				channel++;
			}
		}


		for (Edge edge : (Collection<Edge>) md.graph.getEdges()) {
			edge.visited = false;
			edge.getDelayLine().incrementIndex();
		}

	}

	private void distributeSample(DirectedSparseGraph<Node, Edge> graph, Node node, double sample) {
		// distribute energy equally
		int numOutputs = graph.getOutEdges(node).size();
		sample = sample / numOutputs;

		/*double c = 1.0;

		if(node.type == Node.NodeType.wall) {
			c = -1.0;
		} */

		for (Edge edge : graph.getOutEdges(node)) {
			edge.visited = true;
			synchronized (edge) {
				edge.getDelayLine().addSample(sample);
			}
		}
	}
}
