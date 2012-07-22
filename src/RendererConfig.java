import java.util.HashMap;

public class RendererConfig extends Config {

    protected void addDefaultValues(HashMap<String, Object> map) {
        map.put("numVolumeNodes", new Integer(50));
        map.put("numWallNodes", new Integer(50));
        map.put("numWallNodesRandom", new Integer(50));
        map.put("stretchFactor", new Double(1.0));

        map.put("sampleRate", 44100f);
        map.put("samplesToProcess", 100000);
    }

    public RendererConfig(String fileName) {
        super(fileName);
    }

    public RendererConfig() {
        super();
    }

    public int getNumVolNodes() {
        return (Integer) this.getValue("numVolumeNodes");
    }

    public int getNumWallNodes() {
        return (Integer) this.getValue("numWallNodes");
    }

    public int getNumWallNodesRandom() {
        return (Integer) this.getValue("numWallNodesRandom");
    }

    public Double getStretchFactor() {
        return (Double) this.getValue("stretchFactor");
    }

    public Float getSampleRate() {
        return (Float) this.getValue("sampleRate");
    }

    public Integer getSamplesToProcess() {
        return (Integer) this.getValue("samplesToProcess");
    }

}
