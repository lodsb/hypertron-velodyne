import java.util.HashMap;
import java.util.LinkedList;

public class SourceListenerConfig extends Config {

    public SourceListenerConfig(String fileName) {
        super(fileName);
    }

    public SourceListenerConfig() {
        super();
    }

    public Listener[] getListeners() {
        return (Listener[]) this.getValue("listeners");
    }

    public Source[] getSources() {
        return (Source[]) this.getValue("sources");
    }

    public int getNumListeners() {
        return this.getListeners().length;
    }

    public int getNumSources() {
        return this.getSources().length;
    }

    protected void addDefaultValues(HashMap<String, Object> map) {
        Source[] sources = new Source[]{new Source()};
        Listener[] listener = new Listener[]{new Listener()};

        map.put("listeners", listener);
        map.put("sources", sources);
    }
}
