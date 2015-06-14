package Model;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Explanator{
    String expression;
    HashMap<String, String> description;
    private static String indent;
    private String[] special = {"\\","p", ")","(","[","]","{","}","^","$","?","&"};
    private String[] metaInCharClass = {"\\","^","]","["};
    int isInsideClass;
    int up;
    String result;
    String character;
    StringBuilder builder;

    public Explanator(){
        description = loadElements();
        indent = "  ";
        builder = new StringBuilder();

    }

    public String explain(String regex){
        expression = regex;
        isInsideClass = 0;
        builder = new StringBuilder();

        for(int i = 0; i < regex.length(); i++){
            up = 0;                                     // if matching character is not special, there is no need to
            String fragment = regex.substring(i,i+1);   // change int i value, and skip indices of regular expression

            if(isInsideCharClass()){
                result = explainCharacterClass(i);
            }else{
                if(isSpecialCase(fragment)){
                    result = explainSpecialCase(i);
                }else{
                    result = explainSimpleCase(i);
                }
            }

            i += up;                                   // to skip character already described by specialized methods
            builder.append(indent);
            builder.append(result);
            builder.append("\n");
        }

        return builder.toString();
    }


    private String explainCharacterClass(int i){
        if(isMetacharacterInCharacterClass(i)){
            if(expression.substring(i,i+1).equals("^") && !(expression.substring(i-1,i).equals("["))){
                return matchCharacter("^");
            }
            return explainSpecialCase(i);
        }else if(isRange(i)){
            up = 2;
            return expression.substring(i,i+3) + "  -  " + "range from \"" + expression.substring(i,i+1) + "\" to \""
                    + expression.substring(i+2,i+3) + "\"";
        }else{
            return matchCharacter(expression.substring(i,i+1));
        }
    }

    private String explainSimpleCase(int i){
        String character = expression.substring(i,i+1);
        if(isSimpleMetacharacter(character) && !(isInsideCharClass())){
            return character + "  -  " + description.get(character);
        }else{
            return matchCharacter(character);
        }
    }

    private String explainSpecialCase(int i){
        String character = expression.substring(i,i+1);
        switch(Special.getSpecial(character)){
            case ESCAPE_MARK:
                return matchBackslash(i);

            case BEGINNING_OF_LINE:
                return matchBeginningOrEnd(i);

            case OPEN_SQUARE_BRACKET:
                isInsideClass++;
            case OPEN_PARANTHESIS:
                indent += "        ";
                return matchSimpleMetacharacter(i);

            case OPEN_CURLY_BRACKET:
                indent += "        ";
                return matchInterval(i);

            case CLOSE_SQUARE_BRACKET:
                isInsideClass--;
            case CLOSE_PARANTHESIS:
            case CLOSE_CURLY_BRACKET:
                return closingBracket(character);

            case QUESTION_MARK:
                return "??";
            case END_OF_LINE:
                return matchBeginningOrEnd(i);
            case AND:
                return matchAnd(i);
            default:
                return "Unexpected sequence";

        }
    }

    private String matchBackslash(int i){
        String escapeSequence = expression.substring(i,i+2);
        if(description.containsKey(escapeSequence)){           // is declared meta-sequence
            up = 1;
            return expression.substring(i,i+2) + "  -  " + description.get(escapeSequence);
        }else if(isPOSIX(expression.charAt(i+1))){
            up = expression.substring(i,expression.indexOf("}",i)+1).length();
            return expression.substring(i,expression.indexOf("}",i)+1) + "  -  "
                    + description.get(expression.substring(i,expression.indexOf("}",i)+1));
        }else{                                                // is a escape sequence
            up = 1;
            return escapeSequence + "  -  " + description.get("\\") + " for: " + expression.substring(i+1,i+2) + " (" + description.get(expression.substring(i+1,i+2))+")";
        }
    }

    private String matchInterval(int i){
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
    }
    private String matchBeginningOrEnd(int i){
        String character = expression.substring(i,i+1);
        int beginningOrEnd = (character.equals("^")) ? 0 : expression.length()-1;  // match for beginning or end of regex
        if(i==beginningOrEnd){
            return character + "  -  " + description.get(character).split("\\.")[0];
        }else{
            return character + "  -  " + description.get(character).split("\\.")[1];
        }
    }

    private String matchCharacter(String character){
        return character + "  -  " + "matching for character: "+ character;
    }

    private String matchSimpleMetacharacter(int i){
        String character = expression.substring(i,i+1);
        return character + "  -  " + description.get(character);
    }

    private String matchAnd(int index){
        if(isInBounds(index,2) && expression.charAt(index+1)=='&'){
            up = 1;
            return  "&&" + "  -  " + description.get("&&");
        }else{
            return matchCharacter("&");
        }
    }

    private String closingBracket(String character){
        indent = indent.substring(0, indent.length()-8);
        return character + "  -  " + description.get(character);
    }

    public void resetIndentation(){
        indent = "  ";
    }

    private boolean isSpecialCase(String character){
        return Arrays.asList(special).contains(character);
    }

    private boolean isSimpleMetacharacter(String character){
        return description.containsKey(character);
    }

    private boolean isRange(int index){
        return isInBounds(index,3) && isLetterOrDigit(expression.charAt(index)) && expression.charAt(index+1)=='-' &&
                isLetterOrDigit(expression.charAt(index+2));
    }

    private boolean isLetterOrDigit(char character){
        return Character.isDigit(character) || Character.isLetter(character);
    }

    private boolean isInBounds(int index, int expectedLength){
        return expression.length() >= index + expectedLength;
    }

    private boolean isInsideCharClass(){
        return isInsideClass > 0;
    }

    private boolean isPOSIX(char ch){
        return ch == 'p' || ch == 'P';
    }

    private boolean isMetacharacterInCharacterClass(int i){
        return Arrays.asList(metaInCharClass).contains(expression.substring(i, i+1));
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
