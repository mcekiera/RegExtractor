package Control;


import Interface.Tabs;
import Interface.UserInterface;
import Model.Analyzer;
import Model.Explanation;
import Model.Extractor;
import Model.Options;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

public class Main {
    Extractor extractor;
    Analyzer analyzer;
    UserInterface userInterface;
    Explanation explanation;
    Tabs tabs;

    Main(){
        extractor = new Extractor();
        userInterface = new UserInterface(this);
        explanation = new Explanation();
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
                userInterface.diplayExplanation(explanation.explain(regex));
                break;
        }

        userInterface.updateStatus("");
        explanation.resetIndentation();
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

    public PatternOptions getAction(){
        return new PatternOptions();
    }

    class PatternOptions implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<Options> temp = (JComboBox<Options>)(e.getSource());
            extractor.setOptions((Options)temp.getSelectedItem());
            updateMatchView();

        }
    }

    public void setTabs(Tabs tab){
        tabs = tab;
    }

}
