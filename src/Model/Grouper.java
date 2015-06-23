package Model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
        for(int index = 0; index < regex.length(); index++){
            String current = regex.substring(index,index+1);

            if(current.equals("\\")){
                escape++;
            }

            if(isOpeningParenthesis(index)){
                StringBuilder builder = new StringBuilder();
                builders.add(level++, builder);
                temp.put(place++, builder);
            }

            for(int k = 0; k<=level-1; k++){
                builders.get(k).append(current);
            }

            if(isClosingParenthesis(index)){
                level --;
            }
        }

        TreeMap<Integer,String> groups = new TreeMap<Integer, String>();
        groups.put(0, regex);
        for(int key : temp.keySet()){
            groups.put(key, temp.get(key).toString());
        }
        groups = validateGroups(groups);
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
            groups.put(0,example);
            for(int i = 1; i <= matcher.groupCount(); i++){
                groups.put(i,matcher.group(i));
                System.out.println(i + "   " + groups.get(i));
            }
        }catch (PatternSyntaxException ex){       //try-catch block is kept inside of method, to ensure
            //ex.printStackTrace();               //that it will continue to work even if exception is thrown
        }catch (IllegalStateException ex){
            //ex.printStackTrace();
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
     * It removes from the given TreeMap a Strings objects, that are matched by non-capturing regex elements, like
     * look behind, look ahead or non-capturing group.
     * @param groups is a TreeMap to validate,
     * @return validated TreeMap.
     */
    private TreeMap<Integer,String> validateGroups(TreeMap<Integer,String> groups){
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for(int key : groups.keySet()){
            if(groups.get(key).matches("(\\(\\?([=:]|[=!<][=!])[^\\)]+\\))")){
                toRemove.add(key);
            }
        }
        for(int key : toRemove){
            if(key!=0) groups.remove(key);
        }
        return groups;
    }
}
