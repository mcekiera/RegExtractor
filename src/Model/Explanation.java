package Model;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Explanation {
    Pattern pattern;
    Matcher matcher;
    HashMap<String, String> description;
    private static String intend;

    public Explanation(){
        description = loadElements();
        intend = "";
    }

    public String explain(String regex){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < regex.length(); i++){
            String part = regex.substring(i,i+1);
            //escepe
            if(part.equals("\\")){
                if(description.containsKey(regex.substring(i,i+2))){
                    builder.append(intend + regex.substring(i,i+2) + "  -  " + description.get(regex.substring(i,i+2))+ "\n");
                }else{
                    builder.append(intend + regex.substring(i,i+2) + "  -  " + description.get(part) +
                            "  " + regex.substring(i+1,i+2) + " (" + description.get(regex.substring(i+1,i+2)) + ")\n");
                }
                i++;
                //parantesies
            }else{
                if(part.equals("]") || part.equals(")") || part.equals("}")){
                    intend = intend.substring(0,intend.length()-8);
                }
                builder.append(intend + part + "  -  " + description.get(part)+ "\n");
                if(part.equals("[") || part.equals("(") || part.equals("{")){
                    intend += "        ";
                }
            }
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
                System.out.println(i++);
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
