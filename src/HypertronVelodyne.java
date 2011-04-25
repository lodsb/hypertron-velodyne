import edu.uci.ics.jung.graph.util.Pair;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import saito.objloader.BoundingBox;
import saito.objloader.Face;
import saito.objloader.Segment;

import java.util.Collection;
import java.util.LinkedList;


public class HypertronVelodyne extends PApplet {
	
	public static int sampleRate = 44100;
	
	Model model;
	BoundingBox bbox;

	float rotX;
	float rotY;

	float normLength = -25;

	PVector pos;
	
	int rndmPtCnt = 50;
	LinkedList<PVector> rndmPts = new LinkedList<PVector>();

	
	//pcam
	PeasyCam cam;
	
	public void setup() {
	    size(600, 600, P3D);

	    model = new Model(this, "cube_sphere.obj", "relative", QUADS);

	    model.scale(3);
	    model.translateToCenter();
	    pos = new PVector();
	    
	  /*  for(int i = 0; i < rndmPtCnt; i++) {
	    	rndmPts.add(model.getRandomPointInVolume());
	    }*/

	    cam = new PeasyCam(this, 400);
	    cam.setMinimumDistance(10);
	    cam.setMaximumDistance(1000);
	    
	    cam.lookAt(300, 300, 0);
	}

	public void draw() {
	    background(32);
	    lights();


	    pushMatrix();

	    translate(width / 2, height / 2, 0); 

	    rotateX(rotY);
	    rotateY(rotX);
	    scale(1.5f);


	   /* for(PVector p: rndmPts) {
	    	pushMatrix();
	    	scale(2.0f);
	    	drawPoint(p);
		    popMatrix();
	    }*/

	    //we have to get the faces out of each segment.
	    // a segment is all verts of the one material
	    for (int j = 0; j < model.getSegmentCount(); j++) {

	        Segment segment = model.getSegment(j);
	        Face[] faces = segment.getFaces();

	       drawFaces( faces );

	       // drawNormals( faces );
	        
	    }
	    scale(3.0f);
	    stroke(0,0,255);
		Collection<Edge> edges = model.getGraph().getEdges();

	    for(Edge<Double> e: edges) {
	    	//pushMatrix();
	    	beginShape(LINES);
	    	Pair<Node> nodes = model.getGraph().getEndpoints(e);
	    	
	    	Node src = nodes.getFirst();
	    	Node dst = nodes.getSecond();
	    	
	    	if(!e.visited) {
	    		if(src.type==Node.NodeType.wall){
	    			stroke(255,255,255);
	    		} else if(dst.type==Node.NodeType.wall) {
	    			stroke(0,255,255);
	    		} else {
	    			stroke(255,255,0);
	    		}
	    	} else {
	    		stroke(255,0,0);
	    	}
	    	
	    	vertex(src.pos.x,src.pos.y,src.pos.z);
	    	vertex(dst.pos.x,dst.pos.y,dst.pos.z);
	    	endShape();
	    	
	    	if(dst.type == Node.NodeType.listener) {
	    		fill(255,0,0);
	    		pushMatrix();
	    		drawPoint(dst.pos);
	    		popMatrix();
	    	} else if(src.type == Node.NodeType.source) {
	    		fill(0,255,0);
	    		pushMatrix();
	    		drawPoint(src.pos);
	    		popMatrix();	    		
	    	}
	    	//popMatrix();
	    }
	    
	    popMatrix();
	}


	PVector camPos = new PVector();
	PVector lookAt = new PVector();
	
	void drawFaces(Face[] fc) {
		
		camPos.set(cam.getPosition());
		lookAt.set(cam.getLookAt());
		
		camPos.sub(lookAt);

	    // draw faces
	    stroke(255,255,255,50);
	    fill(255,0,0,50);
	    beginShape(QUADS);

	    for (int i = 0; i < fc.length; i++)
	    {
	        PVector[] vs = fc[i].getVertices();
	        PVector[] ns = fc[i].getNormals();
	        
	        if(fc[i].isFacingPosition(camPos)) {

	            for (int k = 0; k < vs.length; k++) {
	                normal(ns[k].x, ns[k].y, ns[k].z);
	                vertex(vs[k].x, vs[k].y, vs[k].z);
	            }
	        }
	    }
	    endShape();
	}



	void drawNormals( Face[] fc ) {

	    beginShape(LINES);
	    // draw face normals
	    for (int i = 0; i < fc.length; i++) {
	        PVector v = fc[i].getCenter();
	        PVector n = fc[i].getNormal();

	        // scale the alpha of the stroke by the facing amount.
	        // 0.0 = directly facing away
	        // 1.0 = directly facing 
	        // in truth this is the dot product normalized
	        stroke(255* fc[i].getFacingAmount(pos),255,0,255);

	        vertex(v.x, v.y, v.z);
	        vertex(v.x + (n.x * normLength), v.y + (n.y * normLength), v.z + (n.z * normLength));
	    }
	    endShape();
	}


	void drawPoint(PVector p){
	// System.out.println(p);
	    translate(p.x, p.y, p.z);
	    
	    //fill(0,0,255,250);
	    
	    pushMatrix();
	    scale(0.5f);
	    noStroke();
	    ellipse(0,0,10,10);
	    rotateX(HALF_PI);
	    ellipse(0,0,10,10);
	    rotateY(HALF_PI);
	    ellipse(0,0,10,10);   
	    popMatrix();
	}


	/*public void mouseDragged() {
	    rotX += (mouseX - pmouseX) * 0.01;
	    rotY -= (mouseY - pmouseY) * 0.01;
	}*/

	public void keyPressed() {
		if(key == 's') {
			Processor proc = new Processor(this.model);
			Thread procThread = new Thread(proc);
			
			procThread.start();
		}
	}
	

	
	public static void main(String args[]) {
		PApplet.main(new String[] {"HypertronVelodyne" });
	}

}
