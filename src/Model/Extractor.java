package Model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Extractor{
    private Pattern pattern;
    private Matcher matcher;
    private static Options options = Options.NULL;

    public TreeMap<Integer,Integer> search(String regex, String input){
        TreeMap<Integer,Integer> indices = new TreeMap<Integer,Integer>();

        if(regex.equals("")) return indices;

        try{
            pattern = getPattern(regex,options);
            matcher = pattern.matcher(input);

            while(matcher.find()){
                    indices.put(matcher.start(),matcher.end());
            }

        }catch (PatternSyntaxException ex){
            Analyzer.exceptionMessage(ex);
        }
        return indices;
    }

    public String[] split(String regex, String input){
        pattern = Pattern.compile(regex);
        return pattern.split(input);
    }


    public static int[] arrayStringToInt(String[] toConvert){
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (String part : toConvert){
            temp.add(Integer.parseInt(part));
        }
        int[] converted = new int[temp.size()];
        int index = 0;
        for (int element : temp){
            converted[index] = element;
            index++;
        }
        return converted;
    }

    public static Pattern getPattern(String regex, Options option){
        int[] list = {Pattern.COMMENTS};
        switch (option){
            case CASE_INSENSITIVE:
                return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            case MULTILINE:
                return Pattern.compile(regex, Pattern.MULTILINE);
            case COMMENTS:
                return Pattern.compile(regex, Pattern.COMMENTS);
            case UNICODE_CASE:
                return Pattern.compile(regex, Pattern.UNICODE_CASE);
            case UNICODE_CHARACTER_CLASS:
                return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
            case UNIX_LINES:
                return Pattern.compile(regex, Pattern.UNIX_LINES);
            case CANON_EQ:
                return Pattern.compile(regex, Pattern.CANON_EQ);
            case DOTALL:
                return Pattern.compile(regex, Pattern.DOTALL);
            case LITERAL:
                return Pattern.compile(regex, Pattern.LITERAL);
            default:
                return Pattern.compile(regex);
        }
    }

    public static Options getOption(){
        return options;
    }

    public void setOptions(Options options){
        this.options = options;
    }
}
