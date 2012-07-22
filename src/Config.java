import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public abstract class Config {
    private HashMap<String, Object> configMap;

    private boolean isDefConfig = true;

    public boolean isDefaultConfig() {
        return isDefConfig;
    }

    public Config(String filename) {

        File configFile = new File(filename);
        FileReader configFileReader;

        try {
            configFileReader = new FileReader(configFile);

            XStream xstream = new XStream();

            configMap = (HashMap<String, Object>) xstream.fromXML(configFileReader);

            configFileReader.close();

            System.out.println("Loaded config from "+filename);
            isDefConfig = false;

        } catch (Exception e) {
          System.err.println(e.toString());
          e.printStackTrace();
        }

    }

    public Object getValue(String key) {
        return configMap.get(key);
    }

    public Config() {
        configMap = new HashMap<String, Object>();

        addDefaultValues(configMap);
    }

    protected abstract void addDefaultValues(HashMap<String, Object> hash);


    public void saveConfig(String fileName) {

        try {
            File saveFile = new File(fileName);

            FileWriter saveWriter = new FileWriter(saveFile);

            XStream xstream = new XStream();
            xstream.toXML(configMap, saveWriter);

            System.out.println("Saved config to "+fileName);

            saveWriter.close();


        } catch(Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }

    }
}
