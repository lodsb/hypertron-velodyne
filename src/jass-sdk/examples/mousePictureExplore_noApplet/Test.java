

public class Test {

    
    public static void main (String[] args) throws java.io.IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        process =  runtime.exec("java -classpath \".;../../\" DemoMousePictureExplore gfx/qt-colors.jpg 45.1 asd 512");
        Thread.sleep(10000);
        process.destroy();
        Thread.sleep(10000);
        process =  runtime.exec("java -classpath \".;../../\" DemoMousePictureExplore gfx/gogh1.jpg 45.1 sdf 512");
        Thread.sleep(10000);
        process.destroy();
        Thread.sleep(10000);            
    }
    
}
