package Model;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Explanation {
    String expression;
    HashMap<String, String> description;
    private static String indent;
    private String[] special = {"\\","p", ")","(","[","]","{","}","^","$","?","&"};
    public boolean isInsideClass = false;
    int up;

    public Explanation(){
        description = loadElements();
        indent = "  ";
    }

    public String explain(String regex){
        expression = regex;
        String result = "";
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < regex.length(); i++){
            up = 0;                                     // if matching character is not special, there is no need to
            String fragment = regex.substring(i,i+1);   // change int i value, and skip indices of regular expression

            if(isInsideClass){
                matchCharacter(fragment);
            }else if(isSpecialCase(fragment)){
                result = explainSpecialCase(i);
            }else{
                result = explainSimpleChar(fragment,i);
            }

            i += up;                                   // to skip character already described by specialized methods
            System.out.println(result.split(" ")[0]);
            builder.append(indent);
            builder.append(result);
            builder.append("\n");
        }

        return builder.toString();
    }

    public String explainSimpleChar(String character, int i){
        if(isSimpleMetacharacter(character)){
            return character + "  -  " + description.get(character);
        }else{
            if(isCharacterClass(i)){
                up = 2;
                return expression.substring(i,i+3) + "  -  " + "range from \"" + character + "\" to \""
                        + expression.substring(i+2,i+3) + "\"";
            }else{
                return matchCharacter(character);
            }
        }
    }

    public String explainSpecialCase(int i){
        String character = expression.substring(i,i+1);
        switch(Special.getSpecial(character)){
            case ESCAPE_MARK:
                return matchBackslash(i);

            case BEGINNING_OF_LINE:
                return matchBeginningOrEnd(i);

            case OPEN_PARANTHESIS:
            case OPEN_SQUARE_BRACKET:
                indent += "        ";
                return matchSimpleMetacharacter(i);

            case CLOSE_PARANTHESIS:
            case CLOSE_SQUARE_BRACKET:
            case CLOSE_CURLY_BRACKET:
                return closingBracket(character);

            case OPEN_CURLY_BRACKET:
                indent += "        ";
                String range = expression.substring(i + 1, expression.indexOf("}", i));
                if(!range.contains(",")){
                    up = range.length();
                    return matchSimpleMetacharacter(i) + "\n" + range + "  -  " + "exactly " + range + " times";
                }else{
                    String[] temp = range.split(",");
                    if(temp.length == 1){
                        up = range.length();
                        return matchSimpleMetacharacter(i) + "\n"+ range + "  -  " + "at least " + temp[0] + " times";
                    }else{
                        up = range.length();
                        return matchSimpleMetacharacter(i) + "\n"+ range + "  -  " + "at least " + temp[0] + " but no more than " + temp[1] + " times";
                    }
                }

            case QUESTION_MARK:
                return "??";
            case END_OF_LINE:
                return matchBeginningOrEnd(i);
            case AND:
                return matchAnd(i);
            default:
                return "!!!!!!!!!!!!!!!!!!!!!";

        }
    }

    public String matchBackslash(int i){
        String character = expression.substring(i,i+2);
        if(description.containsKey(character)){
            up = 1;
            return expression.substring(i,i+2) + "  -  " + description.get(character);
        }else if(expression.charAt(i+1) == 'p' || expression.charAt(i+1) == 'P'){                //todo
            up = expression.substring(i,expression.indexOf("}",i)+1).length();
            return expression.substring(i,expression.indexOf("}",i)+1) + "  -  "
                    + description.get(expression.substring(i,expression.indexOf("}",i)+1));
        }else{
            up = 1;
            return character + "  -  " + description.get(character)  + expression.substring(i+1,i+2) + " (" + description.get(expression.substring(i+1,i+2))+")";
        }
    }

    public String matchBeginningOrEnd(int i){
        String character = expression.substring(i,i+1);
        int beginningOrEnd = (character.equals("^")) ? 0 : expression.length()-1;  // match for beginning or end of regex
        if(i==beginningOrEnd){
            return character + "  -  " + description.get(character).split("\\.")[0];
        }else{
            return character + "  -  " + description.get(character).split("\\.")[1];
        }
    }

    public String matchCharacter(String character){
        return character + "  -  " + "matching for character: "+ character;
    }

    public String matchSimpleMetacharacter(int i){
        String character = expression.substring(i,i+1);
        return character + "  -  " + description.get(character);
    }

    public String matchAnd(int index){
        if(isInBounds(index,2) && expression.charAt(index+1)=='&'){
            up = 1;
            return  "&&" + "  -  " + description.get("&&");
        }else{
            return matchCharacter("&");
        }
    }

    public String closingBracket(String character){
        indent = indent.substring(0, indent.length()-8);
        return character + "  -  " + description.get(character);
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

    private HashMap<String, String> loadElements(){
        HashMap<String, String> elements = new HashMap<String,String>();
        File file = new File("src\\Model\\regex.txt");
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

    //todo zostaje tylko podział na greedy, reluctant and possesive quantifires no i refactoring całej metody
}
