package Control;

import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Lead and provide a date from file with description of regex elements.
 */
public class IO {
    static private TreeMap<String,String> description;

    public IO(){
        description = new TreeMap<String, String>(loadElements());
    }

    /**
     * Provide only instance of TreeMap containing data.
     * @return TreeMap with data.
     */
    public static TreeMap<String,String> load(){
        return description;
    }

    /**
     * Read data from file.
     * @return HashMap with data from regex.txt file.
     */
    private HashMap<String, String> loadElements(){
        System.out.println("loaded!");
        HashMap<String, String> elements = new HashMap<String,String>();
        File file = new File("src\\Control\\regex.txt");
        try{
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while((line = reader.readLine()) != null){
                String[] temp = line.split("    ");
                elements.put(temp[0],temp[1]);
            }

        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return elements;
    }
}
