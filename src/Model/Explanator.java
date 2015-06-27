package Model;

import Control.IO;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Responsible to explaining given regex by describing its elements
 */
public class Explanator{
    private final String[] special = {"\\","p", ")","(","[","]","{","}","^","$","?","&","+","*"};
    private final String[] metaInCharClass = {"\\","^","]","["};
    private final LinkedHashMap<String, String> description;
    private static String indent;
    private String expression;
    private int isInsideClass;
    private int skipIndices;
    private String hyphen;

    public Explanator(){
        description = IO.load();
        indent = "  ";
        hyphen = "  -  ";
    }

    /**
     * Iterate through all regular expression character, and describe its usage int this particular case
     * @param regex described regular expression
     * @return description of regular expression
     */
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

            if(isClosing(result)){
                indent = indent.substring(0,indent.length()-8);
            }       // controls indention, with
                                                                                        // every opening bracket
            builder.append(indent);                                                     // it moves text to the right,
            builder.append(result);                                                     // and with every closing
            builder.append("\n");                                                       // bracket, to the left

            if(isOpening(result)){
                indent += "        ";
            }
        }

        return builder.toString();
    }

    /**
     * Describe characters inside character class
     * @param i beginning index
     * @return description of character class elements
     */
    private String explainCharacterClass(int i){
        if(isMetacharacterInCharacterClass(i)){
            if(isNegation(i)){
                return matchCharacter("^");
            }
            return explainSpecialCase(i);
        }else if(isRange(i)){
            skipIndices = 2;
            return expression.substring(i,i+3) + hyphen + "range from \"" + expression.substring(i,i+1) + "\" to \""
                    + expression.substring(i+2,i+3) + "\"";
        }else{
            return matchCharacter(expression.substring(i,i+1));
        }
    }

    /**
     * Recognize simple case character and pass it to proper method
     * @param i character index
     * @return description returned by method the case was passed
     */
    private String explainSimpleCase(int i){
        String character = expression.substring(i,i+1);
        if(isSimpleMetacharacter(character) && !(isInsideCharClass())){
            return character + hyphen + description.get(character);
        }else{
            return matchCharacter(character);
        }
    }

    /**
     * Recognizes special case characters and pass it to proper method
     * @param i character index
     * @return description returned by method the case was passed
     */
    private String explainSpecialCase(int i){
        String character = expression.substring(i,i+1);
        switch(Special.getSpecial(character)){
            case ESCAPE_MARK:
                return matchBackslash(i);

            case BEGINNING_OF_LINE:
                return matchBeginning(i);

            case OPEN_SQUARE_BRACKET:
                isInsideClass++;
            case OPEN_PARANTHESIS:
                if(isMode(i)){
                    return matchModes(i);
                }
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
                return matchEnd(i);
            case AND:
                return matchAnd(i);
            case PLUS:
            case STAR:
                return matchQuantifier(i);
            default:
                return "Unexpected sequence";

        }
    }

    /**
     * Recognize and describe in what meaning the quantifier (+ or *) char is used
     * @param i beginning sequence index
     * @return description of sequence
     */
    private String matchQuantifier(int i){
        if(isInBounds(i,2) && expression.substring(i,i+2).matches("[+*][?+]")){
            skipIndices++;
            return  expression.substring(i,i+2) + hyphen + description.get(expression.substring(i,i+2));
        }else{
            return expression.substring(i,i+1) + hyphen + description.get(expression.substring(i,i+1));
        }
    }

    /**
     * Recognize and describe meta-sequence which begin with backslash mark \ on given index
     * @param i character index
     * @return description of meta-sequence
     */
    private String matchBackslash(int i){
        String escapeSequence = expression.substring(i,i+2);
        if(description.containsKey(escapeSequence)){           // is declared meta-sequence
            skipIndices = 1;
            return expression.substring(i,i+2) + hyphen + description.get(escapeSequence);
        }else if(isPOSIX(expression.charAt(i+1))){
            skipIndices = expression.substring(i,expression.indexOf("}",i)+1).length();
            return expression.substring(i,expression.indexOf("}",i)+1) + hyphen
                    + description.get(expression.substring(i,expression.indexOf("}",i)+1));
        }else if(Character.isDigit(expression.charAt(i + 1))){
            skipIndices++;
            return expression.substring(i,i+2) + hyphen + "backreference to captured group: " +
                    new Grouper().getPatternsGroups(expression).get(Integer.parseInt(expression.substring(i+1,i+2)));
        }else if(isCallOfNamedGroup(escapeSequence)){
            int open = expression.indexOf("<",i);
            int close = expression.indexOf(">",i);
            skipIndices += close - i;
            return expression.substring(i,close+1) + hyphen + "call to named capturing group: " + expression.substring(open,close+1);

        }else{                                                // is a escape sequence
            skipIndices = 1;
            return escapeSequence + hyphen + description.get("\\") + " for: " + expression.substring(i+1,i+2)
                    + " (" + description.get(expression.substring(i+1,i+2))+")";
        }
    }

    /**
     * Recognize and describe internal part of interval sequence: {x},{x,},{x,x}.
     * @param i beginning sequence index
     * @return description of sequence
     */
    private String matchInterval(int i){
        String range = expression.substring(i + 1, expression.indexOf("}", i));
        if(!range.contains(",")){
            skipIndices = range.length();
            return matchSimpleMetacharacter(i) + "\n" + indent+"        " + range
                    + hyphen + "exactly " + range + " times";
        }else{
            String[] temp = range.split(",");
            if(temp.length == 1){
                skipIndices = range.length();
                return matchSimpleMetacharacter(i) + "\n"+  indent+"        " + range
                        + hyphen + "at least " + temp[0] + " times";
            }else{
                skipIndices = range.length();
                return matchSimpleMetacharacter(i) + "\n"+  indent+"        " + range
                        + hyphen + "at least " + temp[0] + " but no more than " + temp[1] + " times";
            }
        }
    }

    /**
     * Recognize and describe in what meaning the roof mark '^' is used
     * @param i character index
     * @return appropriate description of character
     */
    private String matchBeginning(int i){
        String character = expression.substring(i,i+1);
        if(expression.substring(0,i).matches("(\\(+(\\?[ixmsdu]+\\))*)+")){
            return character + hyphen + description.get(character).split("\\.")[0];
        }else{
            return character + hyphen + description.get(character).split("\\.")[1];
        }
    }

    /**
     * Recognize and describe in what meaning the dollar mark '$' is used
     * @param i character index
     * @return appropriate description of character
     */
    private String matchEnd(int i){
        String character = expression.substring(i,i+1);
        if(i >= expression.length()-2){
            return character + hyphen + description.get(character).split("\\.")[0];
        }else{
            return character + hyphen + description.get(character).split("\\.")[1];
        }
    }

    /**
     * Describe non-meta characters, or meta-character when they are treated as simple characters
     * @param character described
     * @return description of character
     */
    private String matchCharacter(String character){
        return character + hyphen + "matching for character: "+ character;
    }

    /**
     * Describe simple meta-character
     * @param i character index
     * @return description of character
     */
    private String matchSimpleMetacharacter(int i){
        String character = expression.substring(i,i+1);
        return character + hyphen + description.get(character);
    }

    /**
     * Recognize and describe meta-sequence which begin with and mark '&' on given index
     * @param index character index
     * @return description of meta-sequence
     */
    private String matchAnd(int index){
        if(isLogicalAnd(index)){
            skipIndices = 1;
            return  "&&" + hyphen + description.get("&&");
        }else{
            return matchCharacter("&");
        }
    }

    /**
     * Describe closing bracket
     * @param character described
     * @return description of character
     */
    private String closingBracket(String character){
        return character + hyphen + description.get(character);
    }

    /**
     * Recognize and describe meta-sequence which begin with question mark ? on given index
     * @param i character index
     * @return description of meta-sequence
     */
    private String matchQuestionMark(int i){
        String character = expression.substring(i,i+1);
        if(isInBounds(i,3) && expression.substring(i,i+3).matches("\\?[=!<>][=!]")){
            skipIndices +=2;
            return expression.substring(i,i+3) + hyphen + description.get(expression.substring(i,i+3));
        }else if(isInBounds(i,2) && expression.substring(i,i+2).matches("\\?[=!>:\\+\\?]")){
            skipIndices +=1;
            return expression.substring(i,i+2) + hyphen + description.get(expression.substring(i,i+2));
        }else if(isNamedGroup(i)){
            int open = expression.indexOf("<",i);
            int close = expression.indexOf(">",i);
            skipIndices += close - i;
            return expression.substring(i,close+1) + hyphen + "named capturing group: " + expression.substring(open,close+1);
        }
        return character + hyphen + description.get(character);
    }

    /**
     * Recognize and describe particular modes (?ixmsud) beginning from given index
     * @param i character index
     * @return description of mode
     */
    private String matchModes(int i){
        int end = expression.indexOf(")",i)+1;
        String mode = expression.substring(i,end);
        System.out.println(mode);
        String result = "";
        skipIndices += end - i - 1;
        for(int r = 2; r < mode.length()-1; r++){
             result += indent+"    " + description.get("(?" + mode.charAt(r) + ")") + ",";
            if(r != mode.length()-2) result += "\n";
        }
        return mode + hyphen + "modes: \n" + result;
    }

    /**
     * Verify if given roof symbol '^' is a beginning of negation statement in character class
     * @param i character index
     * @return true if it is
     */
    private boolean isNegation(int i){
        return expression.substring(i,i+1).equals("^") && !(expression.substring(i-1,i).equals("["));
    }

    /**
     * Verify if character on given index is a part of logical and metacharacter &&
     * @param i character index
     * @return true if it is
     */
    private boolean isLogicalAnd(int i){
        return isInBounds(i,2) && expression.charAt(i+1)=='&';
    }

    /**
     * Verify if character at given index is a beginning of named group
     * @param i character index
     * @return true if it equals "?<"
     */
    private boolean isNamedGroup(int i){
        return expression.substring(i,i+2).equals("?<");
    }

    /**
     * Verify if given string is a call to named capturing group
     * @param substring described
     * @return true if string equals "\k"
     */
    private boolean isCallOfNamedGroup(String substring){
        return substring.equals("\\k");
    }

    /**
     * Verify if given character is potentially more complicated text and therefore should be treated carefully
     * @param character described
     * @return true if it is on special case list {"\\","p", ")","(","[","]","{","}","^","$","?","&","+","*"}
     */
    private boolean isSpecialCase(String character){
        return Arrays.asList(special).contains(character);
    }

    /**
     * Verify if given character is simple and single metacharacter, or more complicated to recognize sequence
     * @param character described
     * @return true if it is on list of simple metacharacters
     */
    private boolean isSimpleMetacharacter(String character){
        return description.containsKey(character);
    }

    /**
     * Verify if range (ex. {2},{2,},{2,5}) occur on given index of regular expression
     * @param index index of regex
     * @return true if range starts at given index
     */
    private boolean isRange(int index){
        return isInBounds(index,3) && isLetterOrDigit(expression.charAt(index)) && expression.charAt(index+1)=='-' &&
                isLetterOrDigit(expression.charAt(index+2));
    }

    /**
     * Verify if character index of regular expression is letter or digit
     * @param character given character
     * @return true if given char is a letter or digit
     */
    private boolean isLetterOrDigit(char character){
        return Character.isDigit(character) || Character.isLetter(character);
    }

    /**
     * Verify if given indices fit inside of regular expression length bounds
     * @param index start index
     * @param expectedLength length of substring
     * @return true if it stay in bounds
     */
    private boolean isInBounds(int index, int expectedLength){
        return expression.length() >= index + expectedLength;
    }

    /**
     * Verify if currently described element is in character class and therefore should be treated differently
     * @return true if it is in character class
     */
    private boolean isInsideCharClass(){
        return isInsideClass > 0;
    }

    /**
     * Verify if given character, occurring after backslash('\'), is a symbol of POSIX
     * @param character described
     * @return true if character is 'p' or 'P'
     */
    private boolean isPOSIX(char character){
        return character == 'p' || character == 'P';
    }

    /**
     * Verify if character on given index is a metacharacter inside a character class and therefore should be treated
     * differently
     * @param i index of char
     * @return true if char is a metacharacter in character class
     */
    private boolean isMetacharacterInCharacterClass(int i){
        return Arrays.asList(metaInCharClass).contains(expression.substring(i, i+1));
    }

    /**
     * Verify if regular opening brackets occur inside given String
     * @param substring String of length: 1  containing one character
     * @return true if this character is a regular opening bracket
     */
    private boolean isOpening(String substring){
        return substring.contains("opening") && (!substring.contains("escape"));
    }

    /**
     * Verify if regular closing brackets occur inside given String
     * @param substring String of length: 1  containing one character
     * @return true if this character is a regular closing bracket
     */
    private boolean isClosing(String substring){
        return substring.contains("closing") && (!substring.contains("escape"));
    }

    /**
     * Verify if regex modes (?ximsud) occur on given index of regular expression
     * @param i index of regex
     * @return true if modes begins on that index
     */
    private boolean isMode(int i){
        int end = expression.indexOf(")",i)+1;
        return isInBounds(i,end-i) && expression.substring(i,end).matches("\\(\\?[ixmsud]+\\)");
    }

}
