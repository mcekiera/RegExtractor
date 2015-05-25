package RegExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Extractor {
    Pattern pattern;
    Matcher matcher;
    Main main;

    public Extractor(Main main){
        this.main = main;

    }

    public void search(String regex, String input){
        if(!regex.equals("")){
            try{
                pattern = Pattern.compile(regex,Pattern.MULTILINE);
                matcher = pattern.matcher(input);
                boolean match = true;
                do{
                    if(matcher.find()){
                        main.displayResults(matcher.start(),matcher.end());
                        main.displayStatus("");
                    }else{
                        main.displayStatus("Match not found");
                        match = false;
                    }
                }while (match);
            }catch (PatternSyntaxException ex){
                //ex.printStackTrace();
                //main.displayStatus("Incorrect pattern");
            }

        }
    }

    public void split(String regex, String input){
        if(!regex.equals("")){
            try{
                pattern = Pattern.compile(regex);
                for(String part : pattern.split(input)){
                    main.displaySplit("\'" + part + "\'" + "\n");
                }
            }catch (PatternSyntaxException ex){
                //ex.printStackTrace();
                //main.displayStatus("Incorrect pattern");
            }
        }
    }
}
