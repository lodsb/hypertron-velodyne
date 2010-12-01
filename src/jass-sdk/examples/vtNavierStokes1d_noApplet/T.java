public class T {

    public static void main(String[] args) {
        double x = 1;
        double y=0;
        double z=x/y;
        boolean is = (z==Double.NaN);
        boolean isbig = (z>1000  || z<-1000);
        System.out.println(is+":"+isbig+":"+z);
    }

}



