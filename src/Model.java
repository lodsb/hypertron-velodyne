import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import processing.core.PApplet;
import processing.core.PVector;
import saito.objloader.Face;
import saito.objloader.OBJModel;
import saito.objloader.Segment;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;


public class Model<T extends Double> extends OBJModel {

	// should be done somewhere else 
	public static double speedOfSound = 343.2; // could be altered?

	public static double epsilon = 1.0 / 0.0037;// should be frequency dependent

	public static double airAbsoption(double distance, double sample) {
		//System.out.println("sdfsdf "+(float)(Math.exp(-distance/epsilon))+" s "+sample);
		//double ret = (((Double) sample) * Math.exp((-1.0) * distance / epsilon));

		double ret = (((Double) sample) * Math.exp((-1.0) * distance / epsilon));

		//float ret  = 0;

		//ret = 0.3f* sample*(float)(1.0/distance);
		return ret;

		//return (float)(decibel(sample)-20.0*Math.log(distance));

		//return sample*(float)(1.0/distance);
	}

	private final static double minDB = 1.e-10;

	public static double decibel(double a) {
		double y;
		double abs_a = Math.abs(a);
		if (abs_a < minDB) {
			y = minDB;
		} else {
			y = abs_a;
		}
		return 20 * Math.log(abs_a) / Math.log(10);
	}

	private static float[] azimuthAndElevation(PVector p1, PVector p2) {
		PVector diff = PVector.sub(p2, p1);
		//PVector ret = cartesianToPolar(diff);
		float elev = PApplet.atan2(diff.y, diff.x);
		float azimuth = PApplet.atan2(diff.z, diff.x);

		return new float[]{azimuth, elev};
	}

	// x = len; y = angleY -> elevation, z = angleZ -> azimuth
	public static PVector cartesianToPolar(PVector theVector) {
		PVector res = new PVector();
		res.x = theVector.mag();
		if (res.x > 0) {
			res.y = -PApplet.atan2(theVector.z, theVector.x);
			res.z = PApplet.asin(theVector.y / res.x);
		} else {
			res.y = 0f;
			res.z = 0f;
		}
		return res;
	}

	/////

	public class GraphModelData<X extends Double> {
		public LinkedList<Node> listenerNodes;
		public LinkedList<Node> sourceNodes;
		public LinkedList<Double> azimuth;
		public LinkedList<Double> elevation;
		public DirectedSparseGraph<Node, Edge<X>> graph;

		public GraphModelData(DirectedSparseGraph<Node, Edge<X>> g, LinkedList<Node> s, LinkedList<Node> l) {
			this.listenerNodes = l;
			this.sourceNodes = s;
			this.graph = g;

		}
	}

	private LinkedList<Face> faceList = new LinkedList<Face>();

	private PVector minBounds;
	private PVector maxBounds;
	private double size;

	//private DirectedSparseGraph<Node, Edge<T>> graph = new DirectedSparseGraph<Node, Edge<T>>();
	private DirectedSparseGraph<Node, Edge<T>> graph = new DirectedSparseGraph<Node, Edge<T>>();


    /**
     *
     * Model variables set by the RendererConfig-File
     *
     *
     **/
	private int numVolNodes = HypertronVelodyne.getRendererConfig().getNumVolNodes();
	private int numWallNodes = HypertronVelodyne.getRendererConfig().getNumWallNodes();
    private int numWallNodesRandom = HypertronVelodyne.getRendererConfig().getNumWallNodesRandom();
    private double stretchFactor = HypertronVelodyne.getRendererConfig().getStretchFactor();

	private int numSourceNodes = HypertronVelodyne.getSourceListenerConfig().getNumSources();
	private int numListenerNodes = HypertronVelodyne.getSourceListenerConfig().getNumListeners();


	private LinkedList<Node> listenerNodes = new LinkedList<Node>();
	private LinkedList<Node> sourceNodes = new LinkedList<Node>();

	public Model(PApplet parent, String fileName, String texturePathMode, int shapeMode) {
		super(parent, fileName, texturePathMode, shapeMode);

		faceList = new LinkedList<Face>();

		float xmin = Float.MAX_VALUE;
		float ymin = Float.MAX_VALUE;
		float zmin = Float.MAX_VALUE;

		float xmax = Float.MIN_VALUE;
		float ymax = Float.MIN_VALUE;
		float zmax = Float.MIN_VALUE;

		for (int j = 0; j < this.getSegmentCount(); j++) {

			Segment segment = this.getSegment(j);
			Face[] faces = segment.getFaces();

			for (Face f : faces) {
				faceList.add(f);

				// bounds
				PVector center = f.getCenter();

				if (center.x < xmin) {
					xmin = center.x;
				}

				if (center.y < ymin) {
					ymin = center.y;
				}

				if (center.z < zmin) {
					zmin = center.z;
				}

				if (center.x >= xmax) {
					xmax = center.x;
				}

				if (center.y >= ymax) {
					ymax = center.y;
				}

				if (center.z >= zmax) {
					zmax = center.z;
				}
			}

			minBounds = new PVector(xmin, ymin, zmin);
			maxBounds = new PVector(xmax, ymax, zmax);

			size = Math.abs(minBounds.dist(maxBounds));
		}
		System.out.println("Bounds: min " + minBounds + " " + maxBounds);

		this.createGraph();

	}

	public DirectedSparseGraph<Node, Edge<T>> getGraph() {
		return this.graph;
	}

	public GraphModelData<T> getModelData() {
		return new GraphModelData<T>(this.graph, this.sourceNodes, this.listenerNodes);
	}

	private void createGraph() {
		System.out.println("Creating mesh with " + numWallNodes + " wall Nodes and " + numVolNodes + " volume Nodes");
		createListenerAndSources();
		createVolumeMesh();
		createWallToVolumeScoreMap();
		createWallMesh(numWallNodes);
        createWallMeshRandomized(2*numWallNodes);

		/*for (Node w1 : this.getGraph().getVertices()) {
			if (w1.type == Node.NodeType.wall) {
				for (Node w2 : this.getGraph().getVertices()) {
					if (w2.type == Node.NodeType.wall && w1 != w2) {
						double dist = w2.pos.dist(w2.pos);
						this.getGraph().addEdge(new Edge(dist), w1, w2);
						this.getGraph().addEdge(new Edge(dist), w2, w1);
					}

				}
			}
		} */


		// set pos & buffers for rendering
		for (Node n : this.listenerNodes) {
			LinkedList<float[]> azAndEl = new LinkedList<float[]>();
			Collection<Edge<T>> inEdges = this.graph.getInEdges(n);

			for (Edge e : inEdges) {
				Node dst = n;
				Node src = this.graph.getSource(e);
				azAndEl.add(azimuthAndElevation(dst.pos, src.pos));
			}

			n.createInputChannels(inEdges.size());
			n.azimuthAndElevation = azAndEl;
		}

		double delayLineLength = 0.0;
		for (Edge e : graph.getEdges()) {
			delayLineLength = delayLineLength + e.dist;
		}

		for (Node n: this.graph.getVertices()) {
			Collection<Edge<T>> ins = this.graph.getInEdges(n);
			Collection<Edge<T>> outs = this.graph.getOutEdges(n);

			if(ins.size() >= 1 && outs.size() >= 1) {

				for(Edge<T> eout: outs) {
					float[] gains = new float[ins.size()];
					float gsum = 0.0f;

					Node outSrcNode = n;
					Node outDstNode = this.graph.getDest(eout);

					PVector outDir = PVector.sub(outSrcNode.pos, outDstNode.pos);

					int inEdgeCntr = 0;
					for(Edge<T> ein: ins) {
						Node inSrcNode = this.graph.getSource(ein);
						Node inDstNode = this.graph.getDest(ein);

						PVector inDir = PVector.sub(inSrcNode.pos, inDstNode.pos);

						//cosine similarity
						float g = (PVector.dot(outDir, inDir))/ (outDir.mag()*inDir.mag());
						g = (g +1)/2;
						//System.err.println(g);

						gains[inEdgeCntr] = g;
						gsum = gsum + g;

						inEdgeCntr++;
					}

					for(int j=0; j < gains.length; j++) {
						gains[j] = 5*gains[j]/gsum;
					}

					n.edgeGainMap.put((Object) eout, gains);
				}


			}

		}

		System.out.println("Overall delay line length: " + delayLineLength + " = " + (delayLineLength / this.speedOfSound) + " seconds\nNum. of Lines: " + graph.getEdgeCount());
		//System.out.println(graph);
	}

	private void createListenerAndSources() {

        Listener[] listeners = HypertronVelodyne.getSourceListenerConfig().getListeners();
        Source[] sources = HypertronVelodyne.getSourceListenerConfig().getSources();

        for(Listener l: listeners) {
            PVector position = new PVector(l.x, l.y, l.z);
            Node listener = new Node(position, Node.NodeType.listener);
            listener.name = l.name;

            graph.addVertex(listener);
            listenerNodes.add(listener);
        }

        for(Source s: sources) {
            PVector position = new PVector(s.x, s.y, s.z);
            Node source = new Node(position, Node.NodeType.source);
            source.name = s.name;

            //source.wavFileReader = new WavFileReader(s.fileName);
            graph.addVertex(source);
            listenerNodes.add(source);
        }

	}

	private void createVolumeMesh() {
		for (int i = 0; i < numVolNodes; i++) {
			graph.addVertex(new Node(this.getRandomPointInVolume(), Node.NodeType.volume));
		}

		double dist;
		// fully connected graph for the volume mesh, snd-source is feedforward, listner is feedback
		// not correct since listener->source is also possible?
		for (Node src : graph.getVertices()) {
			for (Node dst : graph.getVertices()) {
				if (src != dst) {
					dist = distanceFromVectors(src.pos,dst.pos);

					if (src.type == Node.NodeType.source) {

						//if(dst.type == Node.NodeType.listener) continue;

						graph.addEdge(new Edge(dist), src, dst);
					} else if (src.type == Node.NodeType.listener) {
						graph.addEdge(new Edge(dist), dst, src);
					} else {
						// feedforward
						Edge e = new Edge(dist);
						e.feedforward = true;
						graph.addEdge(e, src, dst);
						//TODO fixme???
						// feedback
						/*e = new Edge(dist);
						e.feedforward = false;
						graph.addEdge(e, dst, src);*/

					}
				}
			}
		}

	}

	// Use something serious here...
	// and take care of similar scores...
	TreeMap<Double, Pair<Node>> wallToVolumeMap = new TreeMap<Double, Pair<Node>>();

	private void createWallToVolumeScoreMap() {
		for (Node volNode : graph.getVertices()) {
			if (volNode.type != Node.NodeType.wall) {
				for (Face face : faceList) {
					// not correct, should overwrite only if score for specific node is better
					wallToVolumeMap.put(directionalDistanceScore(face, volNode.pos),
						new Pair(new Node(face.getCenter(), Node.NodeType.wall), volNode));
				}
			}
		}
	}

	private double distanceFromVectors(PVector a, PVector b) {
		return (double) Math.abs(PVector.dist(a,b)) * stretchFactor;
	}

	private void createWallMesh(int numFaces) {
		// use k-best faces
		int cntrAddedFaces = 0;
		double distance = 0;

		for (Entry<Double, Pair<Node>> e : wallToVolumeMap.entrySet()) {
			if (numFaces == cntrAddedFaces) {
				break;
			}

			Pair<Node> nodes = e.getValue();
			Node wall = nodes.getFirst();
			graph.addVertex(wall);

			Node vol = nodes.getSecond();
			//graph.addVertex(vol);
			System.err.println("www "+graph.containsVertex(vol)+ " --- wall "+graph.containsVertex(wall));

			distance = distanceFromVectors(wall.pos, vol.pos);
			System.err.println("wall node"+distance+" ");

			//graph.addEdge(new Edge(distance), wall, vol);
			graph.addEdge(new Edge(distance), vol, wall);

			for(Node nv: graph.getVertices()) {
				if(nv != wall && nv != vol && nv.type != Node.NodeType.wall && nv.type != Node.NodeType.source) {
					double nvd = distanceFromVectors(wall.pos, nv.pos);
					graph.addEdge(new Edge(nvd), wall, nv);
				}
			}

			cntrAddedFaces++;
		}
	}

    private void createWallMeshRandomized(int numFaces) {
        int nodeCounter = 0;

        while(nodeCounter != numFaces) {
            int index = (int)Math.floor(random(0, numFaces));

            if(index < 0 || index >= faceList.size()) {
                continue;
            }

            Face face = faceList.get(index);
            //TODO: possible bug, lookup if wall node already exists
            PVector center = face.getCenter();


            Node wall = null;
            for (Node n: graph.getVertices()) {
                if(n.pos.x == center.x && n.pos.y == center.y && n.pos.z == center.z) {
                    wall = n;
                }
            }

            if(wall == null) {
                wall = new Node(face.getCenter(), Node.NodeType.wall);
                graph.addVertex(wall);
            }

            Collection<Node> c = graph.getVertices();

            for(Node nv: c) {
         			if(nv != wall && nv.type != Node.NodeType.wall && nv.type != Node.NodeType.source) {
                        double nvd = distanceFromVectors(wall.pos, nv.pos);
                        graph.addEdge(new Edge(nvd), wall, nv);
                    }
         	}

            nodeCounter++;

        }
    }


	private double directionalDistanceScore(Face f, PVector pos) {
		// score = normalized distance + inv cos alpha
		PVector fc = f.getCenter();

		double distance = Math.abs(fc.dist(pos)) / this.size;
		PVector direction = fc.sub(fc, pos);

		PVector fNorm = f.getNormal().get();
		fNorm.normalize();
		direction.normalize();

		double invCosA = 1.0 - fNorm.dot(direction);

		return distance + invCosA;
	}

	public LinkedList<Face> getFaceList() {
		return this.faceList;
	}

	private Random rndm = new Random();

	private float random(float lo, float hi) {
		float dist = Math.abs(hi - lo);
		return (dist * rndm.nextFloat()) + lo;
	}

	public PVector getRandomPointInsideBounds() {
		float xrnd = random(minBounds.x, maxBounds.x);
		float yrnd = random(minBounds.y, maxBounds.y);
		float zrnd = random(minBounds.z, maxBounds.z);

		return new PVector(xrnd, yrnd, zrnd);
	}

	public PVector getRandomPointInVolume() {

		boolean insideVolume = false;

		PVector pos = null;

		while (!insideVolume) {

			insideVolume = true;
			pos = this.getRandomPointInsideBounds();
			//System.out.println(pos);

			for (Face f : faceList) {
				if (!f.isFacingPosition(pos)) {
					insideVolume = false;
				}
			}
		}

		return pos;
	}

}
