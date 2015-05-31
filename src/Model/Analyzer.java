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

                pattern = Pattern.compile(regex.substring(i,regex.length()));
                matcher = pattern.matcher(example);
                matcher.find();
                if(matcher.start() != 0 && matcher.start() != example.length()){
                    return matcher.start();
                }

            }catch (PatternSyntaxException ex){
                //ex.printStackTrace();
            }catch (IllegalStateException ex){
                //ex.printStackTrace();
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
}
