package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Extractor{
    private static Pattern pattern;
    private static Matcher matcher;
    private Options options = Options.NULL;

    public List<String> search(String regex, String input){
        List<String> startAndEndIndices = new ArrayList<String>();
        String indices;
        if(regex.equals("")) return startAndEndIndices;

        try{
            pattern = getPattern(regex,options);
            matcher = pattern.matcher(input);

            while(true){
                if(matcher.find()){
                    indices = matcher.start() + "," + matcher.end();
                    startAndEndIndices.add(indices);
                }else{
                    return startAndEndIndices;
                }
            }

        }catch (PatternSyntaxException ex){
            //ex.printStackTrace();
        }
        return startAndEndIndices;
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

    public Pattern getPattern(String regex, Options option){
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

    public void setOptions(Options options){
        this.options = options;
    }
}
