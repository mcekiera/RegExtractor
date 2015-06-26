package Model;

import Control.Main;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Operates on regular expression and an example text to provide regex fragments responsible for capturing groups
 * and example fragments match by particular regex fragments.
 */
public class Grouper {
    int escape;
    String regex;

    /**
     * Provide a set of regex fragments, which are responsible for capturing groups.
     * @param pattern matching pattern.
     * @return TreeMap with capturing fragments of regular expression.
     */
    public TreeMap<Integer,String> getPatternsGroups(String pattern){
        this.regex = pattern;
        TreeMap<Integer,StringBuilder> temp = new TreeMap<Integer,StringBuilder>();
        int level = 0;              // to determine level of nested parenthesis, and which strings update
        int place = 1;              // to determine 'key' for group, group 0 is always whole regular expression
        escape = 0;                 // to count backslash occurrence, to determine if it is escape for parenthesis or for
                                    // other backslash

        ArrayList<StringBuilder> builders = new ArrayList<StringBuilder>();
        for(int i = 0; i < regex.length(); i++){
            String current = regex.substring(i,i+1);

            if(current.equals("\\")){
                escape++;
            }

            if(isOpeningParenthesis(i)){
                StringBuilder builder = new StringBuilder();
                builders.add(level++, builder);
                if(isGrouping(i)) {
                    temp.put(place++, builder);
                    System.out.println(builder.toString());
                }
            }

            for(int k = 0; k<=level-1; k++){
                builders.get(k).append(current);
            }

            if(isClosingParenthesis(i)){
                level --;
            }
        }

        return toProperFormat(temp);
    }

    /**
     * Convert a temporary Map from getPatternGroups() method into final return object.
     * @param toConvert Map to convert
     * @return converted Map object
     */
    public TreeMap<Integer,String> toProperFormat(TreeMap<Integer,StringBuilder> toConvert){
        TreeMap<Integer,String> groups = new TreeMap<Integer, String>();
        groups.put(0, regex);
        for(int key : toConvert.keySet()){
            groups.put(key, toConvert.get(key).toString());
        }
        return groups;
    }

    /**
     * Provide a set of captured regex groups from given pattern and String example pair.
     * @param regex pattern to match.
     * @param example String to match by pattern.
     * @return TreeMap with captured Strings.
     */
    public TreeMap<Integer,String> getExampleGroups(String regex,String example){
        TreeMap<Integer,String> groups = new TreeMap<Integer, String>();
        try{
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(example);
            matcher.find();
            for(int i = 0; i <= matcher.groupCount(); i++){
                groups.put(i,matcher.group(i));
            }
        }catch (Exception ex){
            Main.exceptionMessage(ex);
        }
        return groups;
    }

    /**
     * Determine if character on given position is a metacharacter in regex.
     * @param i position of character in String,
     * @return true if character is metacharacter.
     */
    private boolean isMetacharacter(int i){
        return (i>0 && (!regex.substring(i-1,i).equals("\\")));
    }

    /**
     * Determine if a character on a given index position in String i a simple opening parenthesis character.
     * @param i position of character in String,
     * @return true i character is opening parenthesis.
     */
    private boolean isOpeningParenthesis(int i){
        return regex.substring(i,i+1).equals("(") && (i==0 || (i>0 && isMetacharacter(i)));
    }

    /**
     * Determine if a character on a given index position in String i a simple closing parenthesis character.
     * @param i position of character in String,
     * @return true i character is closing parenthesis.
     */
    private boolean isClosingParenthesis(int i){
        return regex.substring(i,i+1).equals(")")&& (!regex.substring(i-1,i).equals("\\"));
    }

    /**
     * Determine if a String, which starts from given index, is a beginning of a grouping parentheses.
     * @param i index of character which is currently checking
     * @return true if group is capturing
     */
    public boolean isGrouping(int i){
        return !(regex.length() >= i +4 && regex.substring(i,i+3).equals("(?:") && regex.substring(i,i+4).matches("\\(\\?[ixmsud]\\)"));
    }
}
