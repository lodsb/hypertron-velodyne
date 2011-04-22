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
	
	public static double epsilon = 1.0/0.0037;// should be frequency dependent
	public static double airAbsoption(double distance, double sample) {
		//System.out.println("sdfsdf "+(float)(Math.exp(-distance/epsilon))+" s "+sample);
		double ret = (((Double)sample)*Math.exp((-1.0)*distance/epsilon));
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
        if(abs_a < minDB) {
            y = minDB;
        } else {
            y = abs_a;
        }
        return 20*Math.log(abs_a)/Math.log(10);
    }
    
    private static float[] azimuthAndElevation(PVector p1, PVector p2) {
    	PVector diff = PVector.sub(p2, p1);
    	//PVector ret = cartesianToPolar(diff);
		float elev = PApplet.atan2(diff.y,diff.x);
		float azimuth = PApplet.atan2(diff.z,diff.x);
    	
    	return new float[]{azimuth,elev};
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
		
		public GraphModelData(DirectedSparseGraph<Node,Edge<X>> g, LinkedList<Node> s, LinkedList<Node> l) {
			this.listenerNodes = l;
			this.sourceNodes = s;
			this.graph = g;
			
		}
	}

	private LinkedList<Face> faceList = new LinkedList<Face>();
	
	private PVector minBounds;
	private PVector maxBounds;
	private double  size;
	
	//private DirectedSparseGraph<Node, Edge<T>> graph = new DirectedSparseGraph<Node, Edge<T>>();
	private DirectedSparseGraph<Node, Edge<T>> graph = new DirectedSparseGraph<Node, Edge<T>>();

	private int numVolNodes = 1;
	private int numWallNodes = 5;
	
	private int numSourceNodes = 1;
	private int numListenerNodes = 1;
	
	private double stretchFactor = 2.0;
	
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
	        
	        for(Face f: faces) {
	        	faceList.add(f);
	        	
		        // bounds	
	        	PVector center = f.getCenter();
	        	
	        	if(center.x < xmin) {
	        		xmin = center.x;
	        	}
	        	
	        	if(center.y < ymin) {
	        		ymin = center.y;
	        	}

	        	if(center.z < zmin) {
	        		zmin = center.z;
	        	}
	        	
	        	if(center.x >= xmax) {
	        		xmax = center.x;
	        	}
	        	
	        	if(center.y >= ymax) {
	        		ymax = center.y;
	        	}

	        	if(center.z >= zmax) {
	        		zmax = center.z;
	        	}
	        }
	        
	        minBounds = new PVector(xmin,ymin,zmin);
	        maxBounds = new PVector(xmax,ymax,zmax);
	        
	        size = Math.abs(minBounds.dist(maxBounds));
	    }
		System.out.println("Bounds: min "+minBounds+" "+maxBounds);
		
		this.createGraph();
		
	}
	
	public DirectedSparseGraph<Node, Edge<T>> getGraph() {
		return this.graph;
	}
	
	public GraphModelData<T> getModelData() {
		return new GraphModelData<T>(this.graph, this.sourceNodes, this.listenerNodes);
	}
	
	private void createGraph() {
		System.out.println("Creating mesh with "+numWallNodes+" wall Nodes and " + numVolNodes +" volume Nodes");
		createListenerAndSources();
		createVolumeMesh();
		createWallToVolumeScoreMap();
		createWallMesh(numWallNodes);
		
		
		// set pos & buffers for rendering
		for(Node n: this.listenerNodes) {
			LinkedList<float[]> azAndEl = new LinkedList<float[]>();
			Collection<Edge<T>> inEdges = this.graph.getInEdges(n);
			
			for(Edge e: inEdges) {
				Node dst = n;
				Node src = this.graph.getSource(e);
				azAndEl.add(azimuthAndElevation(dst.pos,src.pos));
			}
			
			n.createInputChannels(inEdges.size());
			n.azimuthAndElevation = azAndEl;
		}
		
		double delayLineLength = 0.0;
		for(Edge e: graph.getEdges()) {
			delayLineLength = delayLineLength + e.dist;
		}
		
		System.out.println("Overall delay line length: "+delayLineLength + " = "+ (delayLineLength/this.speedOfSound) + " seconds\nNum. of Lines: "+graph.getEdgeCount());
		//System.out.println(graph);
	}
	
	private void createListenerAndSources() {
		for(int i = 0; i < numListenerNodes; i++) {
			Node listener = new Node(this.getRandomPointInVolume(), Node.NodeType.listener);
			graph.addVertex(listener);
			listenerNodes.add(listener);
		}
		
		for(int i = 0; i < numSourceNodes; i++) {
			Node source = new Node(this.getRandomPointInVolume(), Node.NodeType.source);
			graph.addVertex(source);
			sourceNodes.add(source);
		}		
	}
		
	private void createVolumeMesh() {
		for(int i = 0; i < numVolNodes; i++) {
			graph.addVertex(new Node(this.getRandomPointInVolume(), Node.NodeType.volume));
		}
		
		double dist;
		// fully connected graph for the volume mesh, snd-source is feedforward, listner is feedback
		for(Node src: graph.getVertices()) {
			for(Node dst: graph.getVertices()) {
				if(src != dst) {
					dist = (double)Math.abs(PVector.dist(src.pos, dst.pos))*stretchFactor;

					if(src.type==Node.NodeType.source) {
						graph.addEdge(new Edge(dist), src, dst);						
					} else if(src.type == Node.NodeType.listener) {
						//graph.addEdge(new Edge(dist), dst, src);
					} else {
						// feedforward
						Edge e = new Edge(dist);
						e.feedforward = true;
						graph.addEdge(e, src, dst);
						// feedback
						e = new Edge(dist);
						e.feedforward = false;
						graph.addEdge(e, dst, src);
						
					}
				}
			}			
		}
		
	}
	
	// Use something serious here...
	// and take care of similar scores...
	TreeMap<Double,Pair<Node>> wallToVolumeMap = new TreeMap<Double, Pair<Node>>();
	private void createWallToVolumeScoreMap() {
		for(Node volNode: graph.getVertices()) {
			if(volNode.type != Node.NodeType.wall) {
				for(Face face: faceList) {
					wallToVolumeMap.put(directionalDistanceScore(face, volNode.pos),
							new Pair(new Node(face.getCenter(),Node.NodeType.wall),volNode));
				}
			}
		}
	}
	
	private void createWallMesh(int numFaces) {
		// use k-best faces
		int cntrAddedFaces = 0;
		double distance = 0;
		
		for(Entry<Double,Pair<Node>> e: wallToVolumeMap.entrySet()) {
			if(numFaces == cntrAddedFaces) { 
				break;
			}
			
			Pair<Node> nodes = e.getValue();
			Node wall = nodes.getFirst();
			Node vol  = nodes.getSecond();
			distance = wall.pos.dist(vol.pos);
			
			graph.addEdge(new Edge(distance), wall, vol);
			graph.addEdge(new Edge(distance), vol, wall);
			cntrAddedFaces++;
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
		
		double invCosA = 1.0- fNorm.dot(direction);
		
		return distance+invCosA;
	}
	
	public LinkedList<Face> getFaceList() {
		return this.faceList;
	}
	
	private Random rndm = new Random();
	
	private float random(float lo, float hi) {
		float dist = Math.abs(hi - lo);
		return (dist*rndm.nextFloat())+lo;
	}
	
	public PVector getRandomPointInsideBounds() {
		float xrnd = random(minBounds.x, maxBounds.x);
		float yrnd = random(minBounds.y, maxBounds.y);
		float zrnd = random(minBounds.z, maxBounds.z);
		
		return new PVector(xrnd,yrnd,zrnd);
	}
	
	public PVector getRandomPointInVolume() {
		
		boolean insideVolume = false;
		
		PVector pos = null;
		
		while(!insideVolume) {
			
			insideVolume = true;
			pos = this.getRandomPointInsideBounds();
			//System.out.println(pos);
			
			for(Face f: faceList) {
				if(!f.isFacingPosition(pos)) {
					insideVolume = false;
				}
			}
		}
	    
		return pos;
	}

}
