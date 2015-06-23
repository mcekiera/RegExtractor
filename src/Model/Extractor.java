package Model;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 */
public class Extractor{
    private Pattern pattern;
    private static Options options = Options.NULL;

    public TreeMap<Integer,Integer> search(String regex, String input){
        TreeMap<Integer,Integer> indices = new TreeMap<Integer,Integer>();

        if(regex.equals("")) return indices;

        try{
            pattern = getPattern(regex,options);
            Matcher matcher = pattern.matcher(input);

            while(matcher.find()){
                    indices.put(matcher.start(), matcher.end());
            }

        }catch (PatternSyntaxException ex){
            Analyzer.exceptionMessage(ex);
        }
        return indices;
    }

    public String[] split(String regex, String input){
        try{
            pattern = Pattern.compile(regex);
        }catch (PatternSyntaxException ex){
            Analyzer.exceptionMessage(ex);
        }
        return pattern.split(input);
    }

    public static Pattern getPattern(String regex, Options option){
        switch (option){
            case CASE_INSENSITIVE:
                return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            case MULTILINE:
                return Pattern.compile(regex, Pattern.MULTILINE);
            default:
                return Pattern.compile(regex);
        }
    }

    public static Options getOption(){
        return options;
    }

}
