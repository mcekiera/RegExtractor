package Model;

import Control.Main;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for regular expression matching
 */
public class Extractor{
    private Pattern pattern;
    private static Options options = Options.NULL;

    /**
     * Searches through example text for fragments which matches to given regular expression.
     * @param regex regular expression
     * @param input example text
     * @return Map of position indices of matched fragments
     */
    public TreeMap<Integer,Integer> search(String regex, String input){
        TreeMap<Integer,Integer> indices = new TreeMap<Integer,Integer>();

        if(regex.equals("")) return indices;

        try{
            pattern = getPattern(regex,options);
            Matcher matcher = pattern.matcher(input);

            while(matcher.find()){
                    indices.put(matcher.start(), matcher.end());
            }

        }catch (Exception ex){
            Main.exceptionMessage(ex);
        }
        return indices;
    }

    /**
     * Split example text with given regex.
     * @param regex regular expression
     * @param input example text
     * @return String[] array of splitted text
     */
    public String[] split(String regex, String input){
        try{
            pattern = Pattern.compile(regex);
        }catch (Exception ex){
            Main.exceptionMessage(ex);
        }
        return pattern.split(input);
    }

    /**
     * Controls selection of regular expressions modes.
     * @param regex regular expression for creating Pattern object
     * @param option Options enums implements modes
     * @return Pattern object of given regular expression and chosen modes
     */
    public static Pattern getPattern(String regex, Options option){
        switch (option){
            case CASE_INSENSITIVE:
                return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            case MULTILINE:
                return Pattern.compile(regex, Pattern.MULTILINE);
            case BOTH:
                return Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            default:
                return Pattern.compile(regex);
        }
    }

    /**
     * Return currently used modes, implemented as Option enums object.
     * @return used Options enum object
     */
    public static Options getOption(){
        return options;
    }

    /**
     * Set which regular expressions modes, implemented as Option enums objects, are used in pattern
     * @param op chosen Options object,
     */
    public static void setOptions(Options op){
        options = op;
    }

}
