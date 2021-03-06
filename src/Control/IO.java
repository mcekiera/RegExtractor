package Control;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * Lead and provide a date from file with description of regex elements.
 */
public class IO {
    static private LinkedHashMap<String,String> description;

    public IO(){
        description = new LinkedHashMap<String, String>(loadElements());
    }

    /**
     * Provide only instance of TreeMap containing data.
     * @return TreeMap with data.
     */
    public static LinkedHashMap<String,String> load(){
        return description;
    }

    /**
     * Read data from file.
     * @return HashMap with data from regex.txt file.
     */
    private LinkedHashMap<String, String> loadElements(){
        LinkedHashMap<String, String> elements = new LinkedHashMap<String,String>();
        try{
            String line;
            InputStreamReader stream = new InputStreamReader(getClass().getResourceAsStream("regex.txt"));
            BufferedReader reader = new BufferedReader(stream);
            while((line = reader.readLine()) != null){
                String[] temp = line.split("    ");
                elements.put(temp[0],temp[1]);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return elements;
    }
}
