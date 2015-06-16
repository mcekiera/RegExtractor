package Model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Grouper {
    int escape;
    String regex;

    public TreeMap<Integer,String> getPatternsGroups(String pattern){
        this.regex = pattern;
        TreeMap<Integer,StringBuilder> temp = new TreeMap<Integer,StringBuilder>();
        int level = 0;              // to determine level of nested parenthesis, and which strings update
        int place = 1;              // to determine 'key' for group, group 0 is always whole regular expression
        escape = 0;             // to count backslash occurrence, to determine if it is escape for parenthesis or for
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
                if(isGrouping(index)) {
                    temp.put(place++, builder);
                }
            }

            for(int k = 0; k<=level-1; k++){
                builders.get(k).append(current);
            }

            if(isClosingParenthesis(index)){
                level --;
            }
        }
        /*
        for(StringBuilder srt : builders){
            System.out.println(srt.toString() +"   "+ srt.indexOf(srt.toString()));
        }
        System.out.println();
        for(int key : temp.keySet()){
            System.out.println(key + "     "  + temp.get(key).toString());
        }
        */

        TreeMap<Integer,String> groups = new TreeMap<Integer, String>();
        groups.put(0, regex);
        for(int key : temp.keySet()){
            groups.put(key, temp.get(key).toString());
        }

        return groups;
    }

    public TreeMap<Integer,String> getExampleGroups(String regex,String example){
        TreeMap<Integer,String> groups = new TreeMap<Integer, String>();
        try{
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(example);
            matcher.find();
            groups.put(0,regex);
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

    public boolean isMetacharacter(int i){
        return (i>0 && (!regex.substring(i-1,i).equals("\\")));// && (escape%2==0));
    }

    public boolean isOpeningParenthesis(int i){
        return regex.substring(i,i+1).equals("(") && (i==0 || (i>0 && isMetacharacter(i)));
    }

    public boolean isClosingParenthesis(int i){
        return regex.substring(i,i+1).equals(")")&& (!regex.substring(i-1,i).equals("\\"));
    }

    public boolean isGrouping(int i){
        return !regex.substring(i,i+3).equals("(?:");
    }
}
