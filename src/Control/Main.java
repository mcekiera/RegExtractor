package Control;


import Interface.Tabs;
import Interface.UserInterface;
import Model.Analyzer;
import Model.Explanator;
import Model.Extractor;

import java.util.TreeMap;

/**
 * Controls program flow. It connects a GUI with model's Classes, such as Analyzer, Extractor on Explanator.
 */
public class Main {
    Extractor extractor;
    Analyzer analyzer;
    UserInterface userInterface;
    Explanator explanator;
    Tabs tabs;
    IO io;

    Main(){
        io = new IO();
        extractor = new Extractor();
        userInterface = new UserInterface(this);
        explanator = new Explanator();
        tabs = Tabs.MATCH;

    }

    /**
     * Updates a main GUI with data during program flow, in relation to which tab is currently watched by user.
     */
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
                break;
            case GROUPS:
                userInterface.updateExamples();
                break;
        }

        userInterface.updateStatus("");
        explanator.resetIndentation();
    }

    /**
     * Controls flow of analysis process. It takes data form user interface, and pass it to Analyzer object.
     * Then it displays data retrieved during analysis.
     * @param example String chosen by user from among matched fragments of tested text.
     */
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

    /**
     * Set a tab currently chosen by a user.
     * @param tab chosen by user.
     */
    public void setTabs(Tabs tab){
        tabs = tab;
    }

    /**
     * Controls how exception which are part of normal program flow, are handled.
     * @param ex caught exception
     */
    public static void exceptionMessage(Exception ex){
        System.out.println(ex.getClass());
        //ex.printStackTrace();
    }

}
