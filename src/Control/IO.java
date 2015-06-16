package Control;

import java.io.*;
import java.util.HashMap;

public class IO {
    static private HashMap<String,String> description;

    public IO(){
        description = loadElements();
    }

    public static HashMap<String,String> load(){
        return description;
    }

    private static HashMap<String, String> loadElements(){
        HashMap<String, String> elements = new HashMap<String,String>();
        File file = new File("\\Control\\regex.txt");
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
