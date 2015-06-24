package Model;

import Control.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer{
    private String regex;
    private String example;
    private Pattern pattern;
    private Matcher matcher;
    private static TreeMap<Integer,String> groups;

    public Analyzer(String regex, String analyzed){
        this.regex = regex;
        this.example = analyzed;
        this.regex = trimLookAround(regex);
        Grouper grouper = new Grouper();
        groups = grouper.getExampleGroups(regex, example);
    }

    /**
     *Divides used pattern and text into parts, depending on which part was responsible for matching of particular
     *text fragment.
     *
     *Takes two Strings: Pattern (regular expression for matching purposes), and Example (text to match), and use
     *multiple sub-strings of the Pattern (from a range: [0 - pattern.length()]) as separate regular expression
     *patterns, for matching in given Example. If a particular substring matches to fragment of Example, it writes:
     *end index of match in Example, and start index of substring in Patter; into TreeMap, which holds record for
     *all matches for particular pattern-example set.
     *Method throws PatternSyntax and IllegalState Exceptions, as a part of regular method flow, to distinguish invalid
     *patterns, from valid pattern which are then recorded.
     * @return TreeMap\<Integer,Integer\> with two sets of indices - for Pattern and Example Strings.
     */
    /**
     * analyze składa się teraz z dwóch elementów, analizy od przodu i od tyłu, później wyniki są łączone
     * @return a
     */
    public TreeMap<Integer,Integer> analyze(){             // merge two versions
        TreeMap<Integer,Integer> merged = new TreeMap<Integer, Integer>();
        TreeMap<Integer,Integer> forward = analyzeForward();
        TreeMap<Integer,Integer> backward = analyzeBackward();
        merged.putAll(forward);
        for(int key : backward.keySet()){
            if((!(backward.get(key)==0))
                    && merged.containsKey(key) && forward.get(key)==example.length()){
                merged.put(key,backward.get(key));
            }
        }
        merged = validate(merged);
        merged = checkForEndOfALina(merged);
        merged = checkForBeginningOfALina(merged);

        return merged;
    }

    private TreeMap<Integer, Integer> analyzeForward(){
        TreeMap<Integer, Integer> divided = new TreeMap<Integer, Integer>();

        for(int i = 0; i <= regex.length() ; i++){    // int i decide about length of substring
            try{
                pattern = Pattern.compile(regex.substring(0,i));
                matcher = pattern.matcher(example);
                matcher.find();
                divided.put(i, matcher.end());
            }catch (Exception ex){
                Main.exceptionMessage(ex);
            }
        }

        return divided;
    }

    private TreeMap<Integer, Integer> analyzeBackward(){
        TreeMap<Integer, Integer> divided = new TreeMap<Integer, Integer>();
        for(int i = regex.length(); i >= 0 ; i--){    // int i decide about length of substring
            try{
                String temp = getProperSample(regex.substring(i));
                pattern = Pattern.compile(temp+"$");
                matcher = pattern.matcher(example);
                matcher.find();
                if(i == regex.length() && matcher.start()==0){      //is it necessary with merge?
                    divided.put(i, example.length());
                    continue;
                }
                divided.put(i, matcher.start());
            }catch (Exception ex){
                Main.exceptionMessage(ex);
            }
        }
        divided = checkForEndOfALina(divided);
        return divided;
    }

    /**
     * zmienia fragmenty zwiazane z powtarzeniem schwytanego wzoru: /1/2/3 na schwytane fragmenty, tak by dopasowało
     * dokładnie to samo
     * @param str  a
     * @return a
     */
    private String getProperSample(String str){

        String sample = str;
        if(str.length()>1 && str.substring(0,2).matches("\\\\\\d")){
            for(int key : groups.keySet()){
                if(groups.get(key)!=null){
                    String temp = "(" + groups.get(key).replace("\\","\\\\\\\\") + ")";         //this is a crucial part to replece single "\"!!!
                    sample = sample.replaceAll("\\\\"+key, temp);
                }
            }
        }
        return sample;
    }

    /**
     * If "$"( - end of a line) mark is present in pattern, it ensure that a last matched text part is properly
     * distinguished and highlighted.
     * @param toCheck TreeMap with indexes of matched pattern-example set,
     * @return TreeMap toCheck, modified if pattern ends with "$".
     */
    private TreeMap<Integer,Integer> checkForEndOfALina(TreeMap<Integer,Integer> toCheck){
        if(regex.endsWith("$")){
            int lastMatch = getLastMatch();
            for(int i : toCheck.keySet()){                                    //loop ensure that only one index will
                if(toCheck.get(i)==example.length() && i!=regex.length()){    //be the last index of last character
                    toCheck.put(i,lastMatch);                                 //in pattern
                }
            }
            toCheck.remove(regex.length()-1);     //this line remove one before last index, because without it, the
        }                                         //last character of pattern("$") would not be highlighted.
        return toCheck;
    }

    /**
     * Creates sub-patterns from last characters of original regular expression pattern. First sub-pattern which will
     * match is therefore directly connected with "$"( - the end of line) mark, and match last required by pattern
     * fragment of text.
     * @return index of first character in text, which match with "end of line" sub-pattern.
     */
    private int getLastMatch(){

        for(int i = regex.length(); i >= 0 ; i--){    // int i decide about length of substring
            try{

                pattern = Extractor.getPattern(regex.substring(i,regex.length()),Extractor.getOption());
                matcher = pattern.matcher(example);
                matcher.find();
                if(matcher.start() != 0 && matcher.start() != example.length()){
                    return matcher.start();
                }

            }catch (Exception ex){
                Main.exceptionMessage(ex);
            }
        }
        return example.length();
    }

    /**
     * If "^" ( - beginning of a line) mark is present in pattern, it ensures that first character of pattern is
     * correctly highlighted with first distinguished part of matched text.
     * @param toCheck TreeMap with indexes of matched pattern-example set
     * @return TreeMap to Check, modified if pattern ends with "^".
     */
    private TreeMap<Integer,Integer> checkForBeginningOfALina(TreeMap<Integer,Integer> toCheck){
        if(regex.startsWith("^")){
            toCheck.remove(1,0);
        }
        return toCheck;
    }

    public static String trimLookAround(String regex){
        Grouper grouper = new Grouper();
        TreeMap<Integer,String> groups = grouper.getPatternsGroups(regex);
        for(int key : groups.keySet()){
            if(groups.get(key).matches("(\\(\\?([=]|[=!<][=!])[^\\)]+\\))")){
                regex = regex.replaceAll("(\\(\\?([=]|[=!<][=!])[^\\)]+\\))", "");
            }
        }
        return regex;
    }

    private TreeMap<Integer,Integer> validate(TreeMap<Integer,Integer> sample){
        ArrayList<Integer> keys = new ArrayList<Integer>(sample.keySet());
        Collections.reverse(keys);

        int lastValue = example.length();
        int lastKey = regex.length();

        for(int key : keys){
            if(sample.get(key) > lastValue){
                sample.put(key,lastValue);
            }
            lastValue = sample.get(key);
            lastKey = key;
        }

        return sample;
    }
}
