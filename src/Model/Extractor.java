package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Extractor{
    private static Pattern pattern;
    private static Matcher matcher;

    public static ArrayList<int[]> search(String regex, String input){
        ArrayList<int[]> startAndEndIndices = new ArrayList<int[]>();
        if(regex.equals("")){
            return startAndEndIndices;
        }

        try{
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(input);

            while(true){
                if(matcher.find()){
                    int[] indices = {matcher.start(),matcher.end()};
                    startAndEndIndices.add(indices);
                }else{
                    return startAndEndIndices;
                }
            }

        }catch (PatternSyntaxException ex){
            ex.printStackTrace();
        }
        return startAndEndIndices;
    }

    public static String[] split(String regex, String input){
        pattern = Pattern.compile(regex);
        return pattern.split(input);
    }

    public static List<List<Integer>> analyze(String regex, String analyzed){
        List<List<Integer>> patternAndInputInParts = new ArrayList<List<Integer>>();
        List<Integer> patternParts = new ArrayList<Integer>();
        List<Integer> inputParts = new ArrayList<Integer>();

        for(int i = regex.length(); i >= 0 ; i--){
            try{
                pattern = Pattern.compile(regex.substring(0,i));
                matcher = pattern.matcher(analyzed);
                matcher.find();

                inputParts.add(matcher.end());
                patternParts.add(i);

            }catch (PatternSyntaxException ex){
                ex.printStackTrace();
            }catch (IllegalStateException ex){
                ex.printStackTrace();
            }
        }

        patternAndInputInParts.add(patternParts);
        patternAndInputInParts.add(inputParts);
        return patternAndInputInParts;
    }
}
