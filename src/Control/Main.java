package Control;


import Interface.UserInterface;
import Model.Analyzer;
import Model.Extractor;

import java.util.ArrayList;
import java.util.TreeMap;

public class Main {
    Extractor extractor;
    Analyzer analyzer;
    UserInterface userInterface;

    Main(){

        extractor = new Extractor();
        userInterface = new UserInterface(this);
    }

    public void updateView(){
        String regex = userInterface.getRegEx();
        String text = userInterface.getTextForMatching();

        ArrayList<String> matched = (ArrayList<String>)extractor.search(regex,text);

        if (matched.isEmpty()){
            userInterface.updateStatus("Match not found");
            return;
        }

        userInterface.highlightMatchedText(matched);
        userInterface.updateExamples();
        userInterface.updateStatus("");
    }

    public void analyze(String example){
        String regex = userInterface.getRegEx();
        userInterface.updateAnalyzer(regex,example);

        if(example == null){
            userInterface.updateStatus("Example not found");
            return;
        }

        analyzer = new Analyzer(regex,example);
        TreeMap analyzed = analyzer.analyze();


        userInterface.highlightAnalyzedElements(analyzed);
    }

}
