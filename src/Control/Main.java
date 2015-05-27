package Control;


import Interface.UserInterface;
import Model.Extractor;

import java.util.ArrayList;

public class Main {
    Extractor extractor;
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

        String[] analyzed = extractor.analyze(regex,example);

        if(analyzed[0]==null){
            userInterface.updateStatus("Example not found");
            return;
        }
        userInterface.highlightAnalyzedElements(analyzed);
    }

}
