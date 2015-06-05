package Model;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Explanation {
    Pattern pattern;
    Matcher matcher;
    HashMap<String, String> description;

    public Explanation(){
        description = loadElements();

    }

    public String explain(String regex){
        StringBuilder builder = new StringBuilder();
        for(char at : regex.toCharArray()){
            if(at=='\\'){
                builder.append(at + "   -   " + description.get(Character.toString(at)) + "\n");
            }
            System.out.println(at);
            builder.append(at + "   -   " + description.get(Character.toString(at)) + "\n");
        }

        return builder.toString();
    }

    private HashMap<String, String> loadElements(){
        HashMap<String, String> elements = new HashMap<String,String>();
        File file = new File("C:\\Users\\Pacin\\IdeaProjects\\RegExtractor\\src\\Model\\regex.txt");
        try{
            int i = 0;
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
