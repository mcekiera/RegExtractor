package Model;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Explanation {
    Pattern pattern;
    Matcher matcher;
    HashMap<String, String> description;
    private static String indent;

    public Explanation(){
        description = loadElements();
        indent = "  ";
    }

    public String explain(String regex){
        StringBuilder builder = new StringBuilder();
        int len = regex.length();
        for(int i = 0; i < regex.length(); i++){
            String part = regex.substring(i,i+1);
            //escepe
            if(part.equals("\\")){
                if(description.containsKey(regex.substring(i,i+2))){
                    builder.append(indent + regex.substring(i,i+2) + "  -  " + description.get(regex.substring(i,i+2))+ "\n");
                }else if(regex.charAt(i+1) == 'p'){
                    builder.append(indent + regex.substring(i,regex.indexOf("}",i)+1) + "  -  " + description.get(regex.substring(i,regex.indexOf("}",i)+1))+ "\n");
                    i+= regex.substring(i,regex.indexOf("}",i)).length()-1;
                }else{
                    builder.append(indent + regex.substring(i,i+2) + "  -  " + description.get(part) +
                            "  " + regex.substring(i+1,i+2) + " (" + description.get(regex.substring(i+1,i+2)) + ")\n");
                }
                i++;
                //parantesies
            }else if(part.equals("]") || part.equals(")") || part.equals("}")){
                indent = indent.substring(0, indent.length()-8);
                builder.append(indent + part + "  -  " + description.get(part)+ "\n");

            }else if(part.equals("[") || part.equals("(") || part.equals("{")){
                builder.append(indent + part + "  -  " + description.get(part)+ "\n");
                indent += "        ";
                if(part.equals("{")){
                    String range = regex.substring(i + 1, regex.indexOf("}", i));
                    if(!range.contains(",")){
                        builder.append(indent + range + "  -  " + "exactly " + range + " times" + "\n");
                    }else{
                        String[] temp = range.split(",");
                        if(temp.length == 1){
                            builder.append(indent + range + "  -  " + "at least " + temp[0] + " times" + "\n");
                        }else{
                            builder.append(indent + range + "  -  " + "at least " + temp[0] + " but no more than " + temp[1] + " times" + "\n");
                        }

                    }
                    i += range.length();
                }

            }else if(part.equals("^")){
                if(i==0){
                    builder.append(indent + part + "  -  " + description.get(part).split("\\.")[0]+ "\n");
                }else{
                    builder.append(indent + part + "  -  " + description.get(part).split("\\.")[1]+ "\n");
                }
            }else if(part.equals("$")){
                if(i==regex.length()-1){
                    builder.append(indent + part + "  -  " + description.get(part).split("\\.")[0]+ "\n");
                }else{
                    builder.append(indent + part + "  -  " + description.get(part).split("\\.")[1]+ "\n");
                }
            }else{
                if(description.get(part)!=null){
                    builder.append(indent + part + "  -  " + description.get(part)+ "\n");
                }else if(part.equals("&") && regex.charAt(i+1) == '&'){
                    builder.append(indent + "&&" + "  -  " + description.get("&&") + "\n");
                    i++;
                }else{
                    if(len > i + 2 && (Character.isDigit(regex.charAt(i)) || Character.isLetter(regex.charAt(i))) && regex.substring(i+1,i+2).equals("-")){
                        builder.append(indent + regex.substring(i,i+3) + "  -  " + "range from \"" + part + "\" to \"" + regex.substring(i+2,i+3) + "\"\n");
                    i += 2;
                    }else{
                        builder.append(indent + part + "  -  " + "matching for character: "+ part + "\n");
                    }
                }
            }
        }

        return builder.toString();
    }

    private HashMap<String, String> loadElements(){
        HashMap<String, String> elements = new HashMap<String,String>();
        File file = new File("src\\Model\\regex.txt");
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

    public void resetIndentation(){
        indent = "  ";
    }
}
