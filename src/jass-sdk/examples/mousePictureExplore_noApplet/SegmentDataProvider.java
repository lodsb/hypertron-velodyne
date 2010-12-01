import java.awt.*;

/**
   Provide data to figure out how to fill buffer with audio from mouse position
 */
interface SegmentDataProvider {
    /**
       @return (x,y) position in pixels
    */
    public int[] getMousePosition();

    /**
       @return if mouse button is down
    */
    public boolean isMousePressed();

    /**
       @return int[] array of image pixels
    */
    public int[] getImagePixels();

    /**
       @return Dimension of image in pixels
    */
    public Dimension getImageDimension();
}
