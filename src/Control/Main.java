package Control;


import Interface.Tabs;
import Interface.UserInterface;
import Model.Analyzer;
import Model.Explanator;
import Model.Extractor;
import Model.Grouper;

import java.util.TreeMap;

public class Main {
    Extractor extractor;
    Analyzer analyzer;
    UserInterface userInterface;
    Explanator explanator;
    Tabs tabs;
    Grouper grouper;

    Main(){
        extractor = new Extractor();
        userInterface = new UserInterface(this);
        explanator = new Explanator();
        tabs = Tabs.MATCH;
    }

    public void updateMatchView(){
        String regex = userInterface.getRegEx();
        String text = userInterface.getTextForMatching();
        TreeMap<Integer,Integer> matched = extractor.search(regex,text);
        if (matched.isEmpty()){
            userInterface.updateStatus("Match not found");
            return;
        }
        userInterface.highlightMatchedText(matched);
        switch (tabs){
            case MATCH:
                userInterface.updateExamples();
                break;
            case SPLIT:
                userInterface.updateSplitTab(extractor.split(regex,text));
                break;
            case DESCRIBE:
                userInterface.displayExplanation(explanator.explain(regex));
                grouper = new Grouper(regex);
                grouper.getGroups();

                break;
        }

        userInterface.updateStatus("");
        explanator.resetIndentation();
    }

    public void analyze(String example){
        String regex = userInterface.getRegEx();
        userInterface.updateAnalyzer(regex,example);

        if(example == null){
            userInterface.updateStatus("Example not found");
            return;
        }

        analyzer = new Analyzer(regex,example);
        TreeMap<Integer,Integer> analyzed = analyzer.analyze();


        userInterface.highlightAnalyzedElements(analyzed);
    }

    public void setTabs(Tabs tab){
        tabs = tab;
    }

}
