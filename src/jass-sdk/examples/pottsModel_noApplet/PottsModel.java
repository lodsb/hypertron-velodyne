import java.util.*;

class PottsModel extends PottsModelBase {
    private double currentEn;
    private int nMeasurements;
    private double currentM;
    MersenneTwisterFast mersenneTwisterFast = new MersenneTwisterFast();
    double[][] J;
    
    public PottsModel(int N,double q) {
        super(N,q);
        init();
    }

    public void init() {
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                s[i][j]=-1;
            }
        }
	J = (new Current(N)).J;
        calcEnergy();
        resetRunningAverage();
    }

    public void resetRunningAverage() {
        M=computeMagnetization();
        currentM = M;
        nMeasurements = 1;
    }
    
    private double computeMagnetization() {
        double m=0;
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                m+=s[i][j];
            }
        }
        return m/(N*N);
    }

    private double calcEnergy() {
        double en = 0;
        for(int i=0;i<N;i++) {
            int ir = (i+1)%N;
            for(int j=0;j<N;j++) {
                int jr = (j+1)%N;
                en += -s[i][j]*(s[ir][j]+s[i][jr]);
                en += bias*J[i][j]*s[i][j];
            }
        }
        currentEn = en;
        return en;
    }

    private double flipEnergy(int i,int j,boolean up) {
        double dE;
        int ir = (i+1)%N;
        int il = i-1;
        if(il<0) {
            il += N;
        }
        int jr = (j+1)%N;
        int jl = j-1;
        if(jl<0) {
            jl += N;
        }
        
        double nbSum = s[i][jr]+s[i][jl]+s[ir][j]+s[il][j];
        if(up) {
            dE = -nbSum/q + bias*J[i][j]/q;
        } else {
            dE = nbSum/q-bias*J[i][j]/q;
        }
        return dE;
    }

    private double flipEnergy2(int i,int j,double ds) {
        double dE;
        int ir = (i+1)%N;
        int il = i-1;
        if(il<0) {
            il += N;
        }
        int jr = (j+1)%N;
        int jl = j-1;
        if(jl<0) {
            jl += N;
        }
        
        double nbSum = s[i][jr]+s[i][jl]+s[ir][j]+s[il][j];
	dE = -nbSum*ds + bias*J[i][j]*ds;
        return dE;
    }

    private void update2(int i,int j) {
        double oldS = s[i][j];
        double dM;
	double newS = 2*Math.random()-1;
	double ds = newS-oldS;
	double dE = flipEnergy2(i,j,ds);
        double newEn = currentEn + dE;
        double pnewOverp0 = Math.exp((currentEn-newEn)/T);
        boolean accept = false;
        if(pnewOverp0>=1) {
            accept = true;
        } else {
            double r = Math.random();
            if(r<pnewOverp0) {
                accept = true;
            }
        }
        //System.out.println("acept="+accept);
        if(accept) {
	    s[i][j] = newS;
	    dM = ds/(N*N);
        } else {
            dM = 0;
        }
        double oldM = currentM;
        currentM += dM;
        M = (M*nMeasurements + currentM)/(nMeasurements + 1);
        nMeasurements++;
    }

    private void update(int i,int j) {
	double state = s[i][j];
        boolean up;
        double dM;
        if(state<-.99) {
            up = true;
        } else if(state>.99) {
            up = false;
        } else {
            double r = Math.random();
            //double r = mersenneTwisterFast.nextDouble();
            if(r>.5) {
                up = true;
            } else {
                up=false;
            }
        }
        double dE = flipEnergy(i,j,up);
        //System.out.println("dE="+dE);
        //System.out.println("dcurreEn="+currentEn);
        double newEn = currentEn + dE;
        double pnewOverp0 = Math.exp((currentEn-newEn)/T);
        boolean accept = false;
        if(pnewOverp0>=1) {
            accept = true;
        } else {
            double r = Math.random();
            //double r = mersenneTwisterFast.nextDouble();
            if(r<pnewOverp0) {
                accept = true;
            }
        }
        //System.out.println("acept="+accept);
        if(accept) {
            if(up) {
                s[i][j] += 1/q;
                dM = (1/q)/(N*N);
            } else {
                s[i][j] -= 1/q;
                dM = -(1/q)/(N*N);
            }
        } else {
            dM = 0;
        }
        double oldM = currentM;
        currentM += dM;
        M = (M*nMeasurements + currentM)/(nMeasurements + 1);
        nMeasurements++;
    }



    public void sweep() {
        //System.out.println("m="+getMagnetization());
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                update(i,j);
            }
        }


    }

}

class Current {
    public int N;
    public double[][] J;

    public Current(int N) {
	this.N=N;
	J = new double[N][N];
	setJ();
    }

    private void setJ() {
	double x,y;
	double rad=.3;
	double radEye = .07;
	double p=.7;
	double peye = 0.0;
	for(int i=0;i<N;i++) {
	    x = i/(N-1.);
	    for(int j=0;j<N;j++) {
		y = j/(N-1.);
		double r2 = (x-.5)*(x-.5)+(y-.5)*(y-.5);
		double wrongRad = rad + p*(2*Math.random()-1)*rad*rad;
		if(r2<wrongRad*wrongRad) {
		    J[i][j] = -1;
		} else {
		    J[i][j] = 1;
		}

		r2 = (x-.4)*(x-.4)+(y-.6)*(y-.6) + peye*(2*Math.random()-1)*radEye*radEye;
		if(r2<radEye*radEye) {
		    J[i][j] = 1;
		}
		r2 = (x-.6)*(x-.6)+(y-.6)*(y-.6)+ peye*(2*Math.random()-1)*radEye*radEye;
		if(r2<radEye*radEye) {
		    J[i][j] = 1;
		}
		double rr = Math.random();
		if(rr<p) {
		    rr = Math.random();
		    if(rr<.5) {
			J[i][j] = -1;
		    } else {
			J[i][j] = 1;
		    }
		}
	    }
	}
    }

}
