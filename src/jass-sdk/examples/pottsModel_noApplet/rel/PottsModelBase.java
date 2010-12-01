import java.util.*;

abstract class PottsModelBase {
    protected int N; // grid is NXN
    protected double q=5;     // q-factor for Potts model: s= (-q,-(q-1)...,q-1,q)/q
    protected double[][]s;    // NXN state vector
    protected double T=.001;       // temperature
    protected double bias=0; // term bias*s in energy
    protected double M = 0; // running average magnetization
    
    public PottsModelBase(int N,double q) {
        this.N=N;
        this.q=q;
        s = new double[N][N];
    }

    public void setT(double T) {
        this.T=T;
        resetRunningAverage();
    }

    public void setBias(double b) {
        bias=b;
        resetRunningAverage();
    }

    public double getT() {
        return T;
    }
    
    public double getQ() {
        return q;
    }
    
    public double[][] getState() {
        return s;
    }
    
    public int getN() {
        return N;
    }

    /* set all states to -1. You may also want to compute the energy and cache it here
     */
    public abstract void init();

    /* Maintain running average of M = "sum of all s-values / N^2",
       updated at each Metropolis step. Return current value.
    */
    public double getMagnetization() {
        return M;
    }

    /*
      Reset the calculation of the running average of M, i.e.,
      start again from scratch by setting M to the value computed
      from the current state.
    */
    public abstract void resetRunningAverage();

    /*
      Perform a Metropolis update for every s on the grid.
      After each individual update recompute the running average of M
    */
    public abstract void sweep();


}
