package Model;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Explanation {
    String expression;
    HashMap<String, String> description;
    private static String indent;
    private String[] special = {"\\","p", ")","(","[","]","{","}","^","$","?","&"};

    public Explanation(){
        description = loadElements();
        indent = "  ";
    }

    public String explain(String regex){
        expression = regex;
        String result;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < regex.length(); i++){
            String fragment = regex.substring(i,i+1);
            System.out.println(fragment);
            //escepe
            if(isSpecialCase(fragment)){
                if(fragment.equals("\\")){
                    if(description.containsKey(regex.substring(i,i+2))){
                        builder.append(indent + regex.substring(i,i+2) + "  -  " + description.get(regex.substring(i,i+2))+ "\n");
                    }else if(regex.charAt(i+1) == 'p' || regex.charAt(i+1) == 'P'){
                        builder.append(indent + regex.substring(i,regex.indexOf("}",i)+1) + "  -  " + description.get(regex.substring(i,regex.indexOf("}",i)+1))+ "\n");
                        i+= regex.substring(i,regex.indexOf("}",i)).length()-1;
                    }else{
                        builder.append(indent + regex.substring(i,i+2) + "  -  " + description.get(fragment) +
                                "  " + regex.substring(i+1,i+2) + " (" + description.get(regex.substring(i+1,i+2)) + ")\n");
                    }
                    i++;
                    //parantesies
                }else if(fragment.equals("]") || fragment.equals(")") || fragment.equals("}")){
                    indent = indent.substring(0, indent.length()-8);
                    builder.append(indent + fragment + "  -  " + description.get(fragment)+ "\n");

                }else if(fragment.equals("[") || fragment.equals("(") || fragment.equals("{")){
                    builder.append(indent + fragment + "  -  " + description.get(fragment)+ "\n");
                    indent += "        ";
                    if(fragment.equals("{")){
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

                }else if(fragment.equals("^")){
                    if(i==0){
                        builder.append(indent + fragment + "  -  " + description.get(fragment).split("\\.")[0]+ "\n");
                    }else{
                        builder.append(indent + fragment + "  -  " + description.get(fragment).split("\\.")[1]+ "\n");
                    }
                }else if(fragment.equals("$")){
                    if(i==regex.length()-1){
                        builder.append(indent + fragment + "  -  " + description.get(fragment).split("\\.")[0]+ "\n");
                    }else{
                        builder.append(indent + fragment + "  -  " + description.get(fragment).split("\\.")[1]+ "\n");
                    }
                }else if(fragment.equals("&") && regex.charAt(i+1) == '&'){
                    builder.append(indent + "&&" + "  -  " + description.get("&&") + "\n");
                    i++;
                }
            }else{
                result = explainSimpleChar(fragment,i);
                i += result.split(" ")[0].length()-1;                  //to skip already described chars
                builder.append(indent + result + "\n");
            }
        }
        return builder.toString();
    }

    public String explainSimpleChar(String character, int i){
        if(isSimpleMetacharacter(character)){
            return character + "  -  " + description.get(character);
        }else{
            if(isCharacterClass(i)){
                return expression.substring(i,i+3) + "  -  " + "range from \"" + character + "\" to \""
                        + expression.substring(i+2,i+3) + "\"";
            }else{
                return character + "  -  " + "matching for character: "+ character;
            }
        }
    }

    public void separateCases(String character){
        switch(getMeta(character)){
            case ESCAPE_MARK:

            case BEGINNING_OF_LINE:
                break;
            case POSIX:
                break;
            case OPEN_PARANTHESIS:
                break;
            case CLOSE_PARANTHESIS:
                break;
            case OPEN_SQUARE_BRACKET:
                break;
            case CLOSE_SQUARE_BRACKET:
                break;
            case OPEN_CURLY_BRACKET:
                break;
            case CLOSE_CURLY_BRACKET:
                break;
            case QUESTION_MARK:
                break;
            case END_OF_LINE:
                break;
            case AND:
                break;
            default:
                break;

        }
    }



    public static Special getMeta(String ch){
        return Special.valueOf(ch);
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

    public boolean isSpecialCase(String character){
        return Arrays.asList(special).contains(character);
    }

    public boolean isSimpleMetacharacter(String character){
        return description.containsKey(character);
    }

    public boolean isCharacterClass(int index){
        return isInBounds(index,3) && isLetterOrDigit(expression.charAt(index)) && expression.charAt(index+1)=='-' &&
                isLetterOrDigit(expression.charAt(index+2));
    }

    public boolean isLetterOrDigit(char character){
        return Character.isDigit(character) || Character.isLetter(character);
    }

    public boolean isInBounds(int index, int expectedLength){
        return expression.length() >= index + expectedLength;
    }

    //todo zostaje tylko podział na greedy, reluctant and possesive quantifires no i refactoring całej metody
}
