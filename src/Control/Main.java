package Control;


import Interface.UserInterface;
import Model.Analyzer;
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

    Main(){

        extractor = new Extractor();
        userInterface = new UserInterface(this);
    }

    public void updateMatchView(){
        String regex = userInterface.getRegEx();
        String text = userInterface.getTextForMatching();

        TreeMap<Integer,Integer> matched = extractor.search(regex,text);

        if (matched.isEmpty()){
            userInterface.updateStatus("Match not found");
            return;
        }

        userInterface.updateSplitTab(extractor.split(regex,text));
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

}
