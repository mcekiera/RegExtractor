package Model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Analyzer{
    String regex;
    String example;
    Pattern pattern;
    Matcher matcher;

    public Analyzer(String regex, String analyzed){
        this.regex = regex;
        this.example = analyzed;

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

    public TreeMap<Integer, Integer> analyze(){
        TreeMap<Integer, Integer> divided = new TreeMap<Integer, Integer>();
        ArrayList<Integer> matching = new ArrayList<Integer>();
        String sample;
        for(int i = 0; i <= regex.length() ; i++){    // int i decide about length of substring
            try{
                matching.add(0,i);
                sample = regex.substring(0,i);                                                   //try-catch block is kept inside of method, to ensure
                pattern = Extractor.getPattern(sample,Extractor.getOption());       //that it will continue to work even if exception is thrown
                matcher = pattern.matcher(example);
                matcher.find();
                divided.put(i, matcher.end());



               //if(matching.size()>=2) divided = checkForGrouping(divided,sample,matching.get(0),matching.get(1));

            }catch (PatternSyntaxException ex){
                exceptionMessage(ex);
            }catch (IllegalStateException ex){
                exceptionMessage(ex);
            }
        }
        System.out.println(divided.keySet().toString());
        System.out.println(divided.values().toString());

        divided = checkForEndOfALina(divided);
        divided = checkForBeginningOfALina(divided);
        divided = checkForGrouping(divided);

        System.out.println(divided.keySet().toString());
        System.out.println(divided.values().toString());

        return divided;
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
            System.out.println(lastMatch);
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

            }catch (PatternSyntaxException ex){
                exceptionMessage(ex);
            }catch (IllegalStateException ex){
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

    public boolean isEndingWithGroup(String expression){

        String end = expression.substring(expression.length()-2,expression.length());
        return end.startsWith("\\") && Character.isDigit(end.charAt(1));
    }

    public boolean isProceededByGroup(String expression){
        return expression.length() > 3 && isEndingWithGroup(expression) && isEndingWithGroup(expression.substring(0, expression.length() - 2));
    }

    public int getGroup(String regex){
        return Integer.parseInt(regex.substring(regex.length()-1));
    }

    public TreeMap<Integer,Integer> checkForGrouping(TreeMap<Integer,Integer> map){
        ArrayList<Integer> vals = new ArrayList<Integer>(map.values());
        ArrayList<Integer> keys = new ArrayList<Integer>(map.keySet());
        int p = 0;
        for(int i = 1; i < map.size(); i++){
            if(vals.get(i) < vals.get(i-1)){

                int range = vals.get(i) - vals.get(i-2);
                String patterns = regex.substring(0,keys.get(i-2))+regex.substring(keys.get(i-1));
                Pattern pattern = Pattern.compile(patterns);

                System.out.println(patterns);
                System.out.println(range);
                System.out.println(p++);

                for(int k = 0; k < range; k++){
                    try{

                        String sample = example.substring(0,vals.get(i-2)) + example.substring(vals.get(i-2)+k);
                        System.out.println(sample);
                        Matcher matcher = pattern.matcher(sample);
                        matcher.find();                                       //todo reverse plus this? kiedy dochodzi do grupowania, zaczyna od tyłu, az dojdzie do odpowiedniego indeksu i go zasąpi, póxniej sprawdzać wartości
                        map.put(keys.get(i-1),vals.get(i-2)+k);
                        System.out.println("yatta!" + k);
                        System.out.println(keys.get(i-1) + "," + (vals.get(i-2)+k));

                    }catch (PatternSyntaxException ex){
                        exceptionMessage(ex);
                    }catch (IllegalStateException ex){
                        exceptionMessage(ex);
                    }
                }
            }
        }
        return map;
    }



}
