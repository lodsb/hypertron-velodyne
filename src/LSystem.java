import processing.core.*;
import processing.dxf.*;      
import processing.opengl.*;

import java.awt.Button;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;                                                                
import java.awt.Panel;                                                                    
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.swing.JFrame;
import timeline.*;


public class LSystem extends PApplet {

	HashMap<String, LinkedList<VectorTuple>> faceMap = new HashMap<String, LinkedList<VectorTuple>>();

	int tr_x = 0;
	int tr_y = 0;
	int tr_z = 693;
	
	Random rand = new Random();
	
	float[] randoms = new float[10];
	
	int oldIt = 0;
	
	// Camera
	float cx,cy,cz,ax,ay,az;
	float ex = 0;           
	float ey = 0;           


	boolean gatherData = true;

	boolean dump = false;
	int boxSize = 2;     
	
	int dumpPass = 0;
	
	LinkedList<PVector> vectors = new LinkedList<PVector>();

	// UI
	TextField variables = new TextField("F",5);
	TextField rules = new TextField("F=[[F]-A+Bf_C_E^F]\\+F^");//"F=[AAAa+\\Fa_B]_[AA_AFa^B]^TTT+e+w^^__\\\\FA-v_V^Q",20);
	TextField initialState = new TextField("F",5);                                           
	TextField angleX = new TextField("90",3);                                                
	TextField angleY = new TextField("90",3);                                                
	TextField angleZ = new TextField("90",3);                                                
	TextField lineLength = new TextField("40",3);                                            
	TextField lineWidth = new TextField("1",3);                                              
	TextField startColor = new TextField("000000",4);                                        
	TextField endColor = new TextField("FF0000",4);                                          
	TextField ni = new TextField("2",2);                                                     
	TextField bgColor = new TextField("FFFFFF",4); 
	Button recalcFaces = new Button("Recalc faces!");
	Panel UI = new Panel();                                                                  
	Panel UI1 = new Panel();                                                                 
	Panel UI2 = new Panel();    

	//initial                                                                                
	int ccc = color(255);                                                              
	String r;                                                                                
	String drawForward = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";                                       
	String noDraw = "abcdefghijklmnopqrstuvwxyz";                                            
	public void setup() {   
		
		setUpRandom();
		
		size(800,800,P3D);                                                                     
		//UI.setLayout(new FlowLayout(FlowLayout.CENTER));                                     
		UI.add(new Label("Variables:"));                                                       
		UI.add(variables);                                                                     
		UI.add(new Label("   Rules:"));                                                        
		UI.add(rules);                                                                         
		UI.add(new Label("   Initial state:"));                                                
		UI.add(initialState);                                                                  
		UI1.add(new Label("Angle X, Y & Z:"));                                                 
		UI1.add(angleX);                                                                       
		UI1.add(angleY);                                                                       
		UI1.add(angleZ);                                                                       
		UI1.add(new Label("   Line length:"));                                                 
		UI1.add(lineLength);                                                                   
		UI1.add(new Label("   Iterations:"));                                                  
		UI1.add(ni);                                                                           
		UI2.add(new Label("Line width:"));                                                     
		UI2.add(lineWidth);                                                                    
		UI2.add(new Label("Start color:"));                                                    
		UI2.add(startColor);                                                                   
		UI2.add(new Label("End color:"));                                                      
		UI2.add(endColor);                                                                     
		UI2.add(new Label("Background color:"));                                               
		UI2.add(bgColor);     
		
		recalcFaces.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(faceMap) {
					faceMap.clear();
					vectors.clear();
					setUpRandom();
				}
				gatherData = true;
			}
		});

		JFrame frame = new JFrame("lsys");
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(UI);                                                                      
		frame.getContentPane().add(UI1);                                                                         
		frame.getContentPane().add(UI2);
		frame.getContentPane().add(recalcFaces);
		frame.setSize(550, 200);
		frame.setVisible(true);

		ortho();       
		
		Timeline timeline = new Timeline(this);
	}                                                                                        


	void addToFaceList(float x, float len, String Char) {

		if(!gatherData) {
			return;
		}

		synchronized(faceMap) {
			LinkedList<VectorTuple> list = null;                 

			list = faceMap.get(Char);                          

			if(list == null) {                                 
				list = new LinkedList<VectorTuple>();              
				faceMap.put(Char, list);                         
			}                                                  


			PMatrix3D m = getMatrix((PMatrix3D)null);          

			float x2 = x+len;                                  
			float y = 0;                                       
			float z = 0;                                       

			PVector v1 = new PVector(x, y, z);                         
			PVector v2 = new PVector(x2, y, z);

			v1 = m.mult(v1, null);                             
			v2 = m.mult(v2, null);                             

			VectorTuple t = new VectorTuple(v1, v2);
			list.add(t);
		}
	}

	void addVertex(float x, float y, float z) {

		if(!gatherData) {
			return;
		}
		
		PMatrix3D m = getMatrix((PMatrix3D)null);   
		PVector v1 = new PVector(x, y, z);                         

		v1 = m.mult(v1, null);
		
		this.vectors.push(v1);

	}
	int rIdx = 0;
	
	public void resetRnd() {
		rIdx = 0;
	}
	
	public float getNextRandom() {
		float ret;
		if(rIdx == randoms.length) {
			rIdx = 0;
		}
		ret = randoms[rIdx];
		rIdx++;
		return ret;
	}
	
	public void setUpRandom() {
		for(int i = 0; i < randoms.length; i++) {
			randoms[i] = rand.nextFloat();
		}
	}
	
	public boolean flipCoin() {
		float c = getNextRandom();
		return c >= 0.5;
	}
	
	public void draw() {  
		
		resetRnd();

		// Prepare scene
		try {           
			ccc = color(unhex(bgColor.getText()));
		} catch(NumberFormatException eee) {    
			ccc = color(255);                 
		}                                       
		background(ccc);                        
		strokeWeight(new Float(lineWidth.getText()));

		
		/// begin...
		
		
		// Center camera                         
		translate(width/2,height/2,0);           
		// Camera transformation                 
		translate(cx,cy,cz);                     
		rotateX(radians(ax));                    
		rotateY(radians(ay));                    
		rotateZ(radians(az));
		

		if (dump && dumpPass < 2) {             
			dumpPass++;
			//beginRaw(DXF, "/home/lodsb/lsysDump2.dxf");
			beginRecord("OBJExport", "/home/lodsb/lSystem"+dumpPass+".obj"); 
		} else if(dump) {
			dumpPass = 0;
			dump = false;
		}                                    

		pushMatrix();

		if(dumpPass != 2) {
			evaluateString();                            
			// Interpretate String                       
			float ll = (float) (new Float(lineLength.getText()));      
			float lrx = radians( new Float(angleX.getText()));
			float lry = radians(new Float(angleY.getText()));
			float lrz = radians(new Float(angleZ.getText()));
			for(int i=0;i<r.length();i++) {              
				String evv = str(r.charAt(i));             
				if (drawForward.contains(evv)) {           
					try {                                  
						ccc = lerpColor(color(unhex(startColor.getText())),color(unhex(endColor.getText())),new Float(i)/r.length());
					} catch(NumberFormatException eee) {                                                                       
						ccc = color(0);                                                                                    
					}                                                                                                          
					stroke(red(ccc),green(ccc),blue(ccc));                                                                     
					//line(0,0,0,ll,0,0);                                                                                    
					//translate(ll/2,0,0);                                                                                     
					fill(red(ccc),green(ccc),blue(ccc));     

					//box(ll+boxSize,boxSize,boxSize);
					beginShape(QUADS);
					vertex(0,0,0);
					vertex(ll,0,0);
					vertex(ll,boxSize,0);
					vertex(0,boxSize,0);

					vertex(0,0,boxSize);
					vertex(ll,0,boxSize);
					vertex(ll,boxSize,boxSize);
					vertex(0,boxSize,boxSize);

					float b = boxSize;

					vertex(0,b,0);
					vertex(ll,b,0);
					vertex(ll,b,b);
					vertex(0,b,b);

					vertex(0,0,0);
					vertex(ll,0,0);
					vertex(ll,0,b);
					vertex(0,0,b);

					vertex(0,0,0);
					vertex(0,0,b);
					vertex(0,b,b);
					vertex(0,b,0);

					vertex(ll,0,0);
					vertex(ll,0,b);
					vertex(ll,b,b);
					vertex(ll,b,0);


					endShape();    

					// for objdump

					addVertex(0,0,0);
					addVertex(ll,0,0);
					addVertex(ll,boxSize,0);
					addVertex(0,boxSize,0);

					addVertex(0,0,boxSize);
					addVertex(ll,0,boxSize);
					addVertex(ll,boxSize,boxSize);
					addVertex(0,boxSize,boxSize);

					b = boxSize;

					addVertex(0,b,0);
					addVertex(ll,b,0);
					addVertex(ll,b,b);
					addVertex(0,b,b);

					addVertex(0,0,0);
					addVertex(ll,0,0);
					addVertex(ll,0,b);
					addVertex(0,0,b);

					addVertex(0,0,0);
					addVertex(0,0,b);
					addVertex(0,b,b);
					addVertex(0,b,0);

					addVertex(ll,0,0);
					addVertex(ll,0,b);
					addVertex(ll,b,b);
					addVertex(ll,b,0);

					//System.out.println(m);

					//translate(-ll/2,0,0);   
					this.addToFaceList(0, ll, evv);
					fill(255);          
					translate(ll,0,0);      

				}                             
				if (evv.equals("f")) {    
					if(flipCoin())
						translate(ll,0,0);      
				}                         
				if (evv.equals("+")) {
					if(flipCoin())
						rotateZ(lrz);           
				}                         
				if (evv.equals("-")) {
					if(flipCoin())					
						rotateZ(-lrz);          
				}                         
				if (evv.equals("^")) {
					if(flipCoin())					
						rotateY(lry);           
				}                         
				if (evv.equals("_")) {
					if(flipCoin())					
						rotateY(-lry);          
				}                         
				if (evv.equals("\\")) {
					if(flipCoin())					
						rotateX(lrx);           
				}                         
				if (evv.equals("/")) {
					if(flipCoin())					
						rotateX(-lrx);          
				}                         
				if (evv.equals("[")) {    
					try {                   
						pushMatrix();           
					} catch (RuntimeException eee) {

					}                               
				}                                 
				if (evv.equals("]")) {            
					try {                           
						popMatrix();                  
					} catch (RuntimeException eee) {

					}                               
				}                                 
			}      
		}
		
		gatherData = false;
		
		popMatrix();
		
		translate(tr_x,tr_y,tr_z);    
		
		if(dumpPass != 1) {
			synchronized(faceMap) {
				for(Entry<String, LinkedList<VectorTuple>> entry: faceMap.entrySet()) {
					VectorTuple lastTuple = null;
					int runs = 1;
					for(VectorTuple le: entry.getValue()) {
						if(lastTuple != null) {
							if(runs %2 == 0) {
								beginShape(QUADS);
								fill(0, 255,0,50);
								noStroke();
								vertex(lastTuple.v1.x, lastTuple.v1.y, lastTuple.v1.z);
								vertex(lastTuple.v2.x, lastTuple.v2.y, lastTuple.v2.z);
								vertex(le.v1.x, le.v1.y, le.v1.z);
								vertex(le.v2.x, le.v2.y, le.v2.z);
								endShape();
								//return;
							}	
						}

						lastTuple = le;
						runs++;
					}
				}
			}
		}
		
/*		if(dumpPass != 1) {
			int i = 0;
			//beginShape(QUADS);
			for(PVector vector: vectors) {
				if(i % 512 == 0) {
					beginShape(QUADS);
				}
				fill(0);
				//fill(0, 255,0,50);
				vertex(vector.x, vector.y, vector.z);
				//System.out.print(vector);

				if(i % 512 == 0) {
					endShape();
				}
				i++;
			}
			endShape();
		}
*/
		if (dump) {                            
			//endRaw();
			endRecord();                         
			System.out.println("Done dump");      
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}    
	}                                         



	void evaluateString() {
		noLoop();            
		// L-System          
		String[] arrVariables = split(variables.getText(),",");
		String[] arrRules = split(rules.getText(),",");        
		// Evaluate String                                     
		r = initialState.getText();                            
		String c;                                              
		String l;                                              
		int sl;                                                
		String rule;                                           
		String[] arrRule;

		int depth = 0;
		try {
			
			depth = new Integer(ni.getText());
			
			if(oldIt != depth) {
				faceMap.clear();
				gatherData = true;
				oldIt = depth;
			}
			
			oldIt = depth;
		} catch (Exception e) {
			
		}

		for(int i=0;i< depth;i++) {                 
			c="";                                                
			sl = r.length();                                     
			for(int ii=0;ii<sl;ii++) {                           
				l = str(r.charAt(ii));                             
				for(int ir=0;ir<arrRules.length;ir++) {            
					rule = arrRules[ir];                             
					arrRule=split(rule,"=");                         
					if(l.equals(arrRule[0]) && arrRule.length>1) {   
						l = arrRule[1];                                
					}                                                
				}                                                  
				c=c+l;                                             
			}                                                    
			r=c;                                                 
		}                                                      
		//println(r);                                          
		loop();                                                
	}                                                        
	public // Camera handling                                       
	void mousePressed() {                                    
		ex = mouseX;                                           
		ey = mouseY;                                           
		if (mouseButton == LEFT) {                             

		} 
		else if (mouseButton == RIGHT) {

		} 
		else {

		}
	}  
	public void mouseDragged() 
	{                   
		if (mouseButton == LEFT) {
			noLoop();               
			az += (ex-mouseX)*0.25; 
			ax += (ey-mouseY)*0.25; 

			ex = mouseX;
			ey = mouseY;
			loop();
		}
		else if (mouseButton == RIGHT) {
			cz += (ex-mouseX);
			cz += (ey-mouseY);
			ex = mouseX;
			ey = mouseY;
			loop();
		}
		else {
			noLoop();
			cx -= ex-mouseX;
			cy -= ey-mouseY;
			ex = mouseX;
			ey = mouseY;
			loop();
		}
	}
	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == UP) {
				cy += 1;
			}
			else if (keyCode == DOWN) {
				cy -= 1;
			}
			else if (keyCode == LEFT) {
				cx += 1;
			}
			else if (keyCode == RIGHT) {
				cx -= 1;
			}
		}
		if(key == 'i') {
			dump = true;
		}
		
		if(key == 'w') {
			tr_x++;
		}
		
		if(key == 's') {
			tr_x--;
		}

		if(key == 'a') {
			tr_y++;
		}
		
		if(key == 'd') {
			tr_y--;
		}

		if(key == 'r') {
			tr_z++;
		}
		
		if(key == 'f') {
			tr_z--;
		}
		
		System.out.println("x "+tr_x+" y "+tr_y+" z "+tr_z);

	}



	public static void main(String args[]) {
		PApplet.main(new String[] {"LSystem" });
	}

}
