import java.util.*;

public class Shuffler {
    
    int n=0;
    int [] nlist = null;
    final int NSHUFFLES = 1000;
    
    public Shuffler(int n) {
        this.n = n;
        nlist = new int[n];
        for(int i=0;i<n;i++) {
            nlist[i] = i;
        }
    }

    protected void shuffle() {
        Random random = new Random();
        for(int i=0;i<NSHUFFLES;i++) {
            int k1 = random.nextInt(n);
            int k2 = random.nextInt(n);
            int tmp = nlist[k1];
            nlist[k1] = nlist[k2];
            nlist[k2] = tmp;
        }
    }
    
    public int[] getList() {
        return nlist;
    }

    
    
}

