package Model;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Explanator{
    private final String[] special = {"\\","p", ")","(","[","]","{","}","^","$","?","&","+","*"};
    private final String[] metaInCharClass = {"\\","^","]","["};
    private final HashMap<String, String> description;
    private static String indent;
    private String expression;
    private int isInsideClass;
    private int skipIndices;

    public Explanator(){
        description = loadElements();
        indent = "  ";
    }

    public String explain(String regex){
        expression = regex;
        isInsideClass = 0;
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < regex.length(); i++){
            skipIndices = 0;                            // skipIndices = 0 because if matching character is not special, there is no need to
            String fragment = regex.substring(i,i+1);   // change int i value, and skip indices of regular expression

            String result;
            if(isInsideCharClass()){
                result = explainCharacterClass(i);
            }else{
                if(isSpecialCase(fragment)){
                    result = explainSpecialCase(i);
                }else{
                    result = explainSimpleCase(i);
                }
            }

            i += skipIndices;                                   // to skip character already described by specialized methods

            if(isClosing(result)) indent = indent.substring(0,indent.length()-8);

            builder.append(indent);
            builder.append(result);
            builder.append("\n");

            if(isOpening(result)) indent += "        ";
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
            skipIndices = 2;
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
                return matchSimpleMetacharacter(i);

            case OPEN_CURLY_BRACKET:
                return matchInterval(i);

            case CLOSE_SQUARE_BRACKET:
                isInsideClass--;
            case CLOSE_PARANTHESIS:
            case CLOSE_CURLY_BRACKET:
                return closingBracket(character);
            case QUESTION_MARK:
                return matchQuestionMark(i);
            case END_OF_LINE:
                return matchBeginningOrEnd(i);
            case AND:
                return matchAnd(i);
            case PLUS:
                return matchPlus(i);
            case STAR:
                return matchStar(i);
            default:
                return "Unexpected sequence";

        }
    }

    private String matchPlus(int i){
        if(isInBounds(i,2) && expression.substring(i,i+2).matches("\\+[?+]")){
            skipIndices++;
            return  expression.substring(i,i+2) + "  -  " + description.get(expression.substring(i,i+2));
        }else{
            return expression.substring(i,i+1) + "  -  " + description.get(expression.substring(i,i+1));
        }
    }

    private String matchStar(int i){
        if(isInBounds(i,2) && expression.substring(i,i+2).matches("\\*[?+]")){
            skipIndices++;
            return  expression.substring(i,i+2) + "  -  " + description.get(expression.substring(i,i+2));
        }else{
            return expression.substring(i,i+1) + "  -  " + description.get(expression.substring(i,i+1));
        }
    }

    private String matchBackslash(int i){
        String escapeSequence = expression.substring(i,i+2);
        if(description.containsKey(escapeSequence)){           // is declared meta-sequence
            skipIndices = 1;
            return expression.substring(i,i+2) + "  -  " + description.get(escapeSequence);
        }else if(isPOSIX(expression.charAt(i+1))){
            skipIndices = expression.substring(i,expression.indexOf("}",i)+1).length();
            return expression.substring(i,expression.indexOf("}",i)+1) + "  -  "
                    + description.get(expression.substring(i,expression.indexOf("}",i)+1));
        }else if(Character.isDigit(expression.charAt(i+1))){
            skipIndices++;
            return expression.substring(i,i+2) + "  -  " + "backreference to captured group: " +
                    new Grouper().getPatternsGroups(expression).get(Integer.parseInt(expression.substring(i+1,i+2)));
        }else{                                                // is a escape sequence
            skipIndices = 1;
            return escapeSequence + "  -  " + description.get("\\") + " for: " + expression.substring(i+1,i+2)
                    + " (" + description.get(expression.substring(i+1,i+2))+")";
        }
    }

    private String matchInterval(int i){
        String range = expression.substring(i + 1, expression.indexOf("}", i));
        if(!range.contains(",")){
            skipIndices = range.length();
            return matchSimpleMetacharacter(i) + "\n" + indent+"        " + range
                    + "  -  " + "exactly " + range + " times";
        }else{
            String[] temp = range.split(",");
            if(temp.length == 1){
                skipIndices = range.length();
                return matchSimpleMetacharacter(i) + "\n"+  indent+"        " + range
                        + "  -  " + "at least " + temp[0] + " times";
            }else{
                skipIndices = range.length();
                return matchSimpleMetacharacter(i) + "\n"+  indent+"        " + range
                        + "  -  " + "at least " + temp[0] + " but no more than " + temp[1] + " times";
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
            skipIndices = 1;
            return  "&&" + "  -  " + description.get("&&");
        }else{
            return matchCharacter("&");
        }
    }

    private String closingBracket(String character){
        return character + "  -  " + description.get(character);
    }

    private String matchQuestionMark(int i){
        String character = expression.substring(i,i+1);
        if(isInBounds(i,3) && expression.substring(i,i+3).matches("\\?[=!<>][=!]")){
            System.out.println(expression.substring(i,i+3));
            skipIndices +=2;
            return expression.substring(i,i+3) + "  -  " + description.get(expression.substring(i,i+3));
        }else if(isInBounds(i,2) && expression.substring(i,i+2).matches("\\?[=!<>:\\+\\?]")){
            System.out.println(expression.substring(i,i+2));
            skipIndices +=1;
            return expression.substring(i,i+2) + "  -  " + description.get(expression.substring(i,i+2));
        }
        return character + "  -  " + description.get(character);
    }

    public void resetIndentation(){
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

    public boolean isOpening(String substring){
        return substring.contains("opening");
    }

    public boolean isClosing(String substring){
        return substring.contains("closing");
    }

}
