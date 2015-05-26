package Model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Extractor{
    private static Pattern pattern;
    private static Matcher matcher;

    public static ArrayList<String> search(String regex, String input){
        ArrayList<String> startAndEndIndices = new ArrayList<String>();
        if(regex.equals("")) return startAndEndIndices;

        try{
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(input);

            while(true){
                if(matcher.find()){
                    String indices = matcher.start() + "," + matcher.end();
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

    public static String[] analyze(String regex, String analyzed){
        String[] patternAndInputInParts = new String[2];
        StringBuilder patternParts = new StringBuilder();
        StringBuilder inputParts = new StringBuilder();

        if(regex.equals("")) return patternAndInputInParts;

        for(int i = regex.length(); i >= 0 ; i--){
            try{
                pattern = Pattern.compile(regex.substring(0,i));
                matcher = pattern.matcher(analyzed);
                matcher.find();

                inputParts.append(matcher.end());
                inputParts.append(",");
                patternParts.append(i);
                patternParts.append(",");

            }catch (PatternSyntaxException ex){
                ex.printStackTrace();
            }catch (IllegalStateException ex){
                ex.printStackTrace();
            }
        }

        patternAndInputInParts[0]=(patternParts.toString());
        patternAndInputInParts[1]=(inputParts.toString());
        return patternAndInputInParts;
    }

    public static int[] arrayStringToInt(String[] toConvert){
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (String part : toConvert){
            Integer.parseInt(part);
        }
        int[] converted = new int[temp.size()];
        int index = 0;
        for (int element : temp){
            converted[index] = element;
            index++;
        }
        return converted;
    }
}
