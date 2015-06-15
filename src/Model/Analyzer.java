package Model;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Analyzer{
    String regex;
    String example;
    Pattern pattern;
    Matcher matcher;
    TreeMap<Integer,String> groups;
    int count;
    Grouper grouper;

    public Analyzer(String regex, String analyzed){
        this.regex = regex;
        this.example = analyzed;
        groups = getGroups();
        count = 0;
        grouper = new Grouper(regex);
        this.regex = trimLookaround(regex);
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
     * @return
     */
    public TreeMap<Integer,Integer> analyze(){             // merge two versions
        int del = regex.length()/2;
        TreeMap<Integer,Integer> merged = new TreeMap<Integer, Integer>();
        TreeMap<Integer,Integer> forward = analyzeForward();
        TreeMap<Integer,Integer> backward = analyzeBackward();
        merged.putAll(forward);
        for(int key : backward.keySet()){
            if((!(backward.get(key)==0))
                    && merged.containsKey(key) && forward.get(key)==example.length()){
                merged.put(key,backward.get(key));
            };
        }
        System.out.println(forward.keySet().toString());
        System.out.println(forward.values().toString());
        System.out.println(backward.keySet().toString());
        System.out.println(backward.values().toString());
        System.out.println(merged.keySet().toString());
        System.out.println(merged.values().toString());

        return merged;
    }

    public TreeMap<Integer, Integer> analyzeForward(){
        TreeMap<Integer, Integer> divided = new TreeMap<Integer, Integer>();

        for(int i = 0; i <= regex.length() ; i++){    // int i decide about length of substring
            try{
                pattern = Pattern.compile(regex.substring(0,i));
                matcher = pattern.matcher(example);
                matcher.find();
                divided.put(i, matcher.end());
            }catch (PatternSyntaxException ex){       //try-catch block is kept inside of method, to ensure
                //ex.printStackTrace();               //that it will continue to work even if exception is thrown
            }catch (IllegalStateException ex){
                //ex.printStackTrace();
            }
        }
        divided = checkForEndOfALina(divided);
        divided = checkForBeginningOfALina(divided);

        System.out.println();

        return divided;
    }

    public TreeMap<Integer, Integer> analyzeBackward(){
        TreeMap<Integer, Integer> divided = new TreeMap<Integer, Integer>();
        String sample;
        for(int i = regex.length(); i >= 0 ; i--){    // int i decide about length of substring
            try{
                String temp = getProperSample(regex.substring(i));
                pattern = Pattern.compile(temp+"$");
                System.out.println(temp);
                matcher = pattern.matcher(example);
                matcher.find();
                if(i == regex.length() && matcher.start()==0){      //is it necessary with merge?
                    divided.put(i, example.length());
                    continue;
                }
                divided.put(i, matcher.start());
            }catch (PatternSyntaxException ex){       //try-catch block is kept inside of method, to ensure
                //ex.printStackTrace();               //that it will continue to work even if exception is thrown
            }catch (IllegalStateException ex){
                //ex.printStackTrace();
            }
        }
        divided = checkForEndOfALina(divided);
        return divided;
    }

    /**
     * zmienia fragmenty zwiazane z powtarzeniem schwytanego wzoru: /1/2/3 na schwytane fragmenty, tak by dopasowało
     * dokładnie to samo
     * @param str
     * @return
     */
    public String getProperSample(String str){
        String sample = str;
        for(int key : groups.keySet()){
            if(groups.get(key)!=null){
                String temp = "(" + groups.get(key).replace("\\","\\\\\\\\") + ")";         //this is a crucial part to replece single "\"!!!
                sample = sample.replaceAll("\\\\"+key, temp);
            }
        }
        System.out.println(sample);
        return sample;
    }

    /**
     * If "$"( - end of a line) mark is present in pattern, it ensure that a last matched text part is properly
     * distinguished and highlighted.
     * @param toCheck TreeMap with indexes of matched pattern-example set,
     * @return TreeMap toCheck, modified if pattern ends with "$".
     */
    public TreeMap<Integer,Integer> checkForEndOfALina(TreeMap<Integer,Integer> toCheck){
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
    public int getLastMatch(){

        for(int i = regex.length(); i >= 0 ; i--){    // int i decide about length of substring
            try{

                pattern = Extractor.getPattern(regex.substring(i,regex.length()),Extractor.getOption());
                matcher = pattern.matcher(example);
                matcher.find();
                if(matcher.start() != 0 && matcher.start() != example.length()){
                    return matcher.start();
                }

            }catch (Exception ex){
                exceptionMessage(ex);
            }
        }
        return regex.length();
    }

    /**
     * If "^" ( - beginning of a line) mark is present in pattern, it ensures that first character of pattern is
     * correctly highlighted with first distinguished part of matched text.
     * @param toCheck TreeMap with indexes of matched pattern-example set
     * @return TreeMap to Check, modified if pattern ends with "^".
     */
    public TreeMap<Integer,Integer> checkForBeginningOfALina(TreeMap<Integer,Integer> toCheck){
        if(regex.startsWith("^")){
            toCheck.remove(1,0);
        }
        return toCheck;
    }

    public static void exceptionMessage(Exception ex){
        //System.out.println(ex.getClass() + " is expected as part of program flow");
    }


    /**
     * chyba wypadało by przenieś tę metodę do groupera, ewentualnie przerobić go na static
     * @return
     */
    public TreeMap<Integer,String> getGroups(){
        TreeMap<Integer,String> groups = new TreeMap<Integer, String>();
        try{
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(example);
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

    public static String trimLookaround(String regex){
        Grouper trimmer = new Grouper(regex);
        for(int key : trimmer.getGroups().keySet()){
            if(trimmer.getGroups().get(key).matches("(\\(\\?[=!<][=!]*[^\\)]+\\))")){
                regex = regex.replaceAll("(\\(\\?[=!<][=!]*[^\\)]+\\))", "");
            }
            System.out.println(regex);
        }
        return regex;
    }

}
