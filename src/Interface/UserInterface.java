package Interface;

import Control.*;
import Control.Action;
import Model.Extractor;

import javax.swing.*;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.ArrayList;

public class UserInterface {

    private JTextField statusBar;
    private JTextField regexInput;
    private JTextField regexDisplay;
    private JTextField exampleDisplay;
    private JTextArea matcherDisplay;
    private DefaultListModel<String> splitList;
    private Highlighter inputHigh;
    private Highlighter patternHigh;
    private Highlighter elementHigh;
    private Main main;

    public UserInterface(Main main){
        JFrame frame = new JFrame();
        this.main = main;

        frame.add(buildStatusBar(),BorderLayout.PAGE_END);
        frame.add(buildCentralPanel(),BorderLayout.CENTER);
        frame.add(buildSidePanel(),BorderLayout.EAST);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600,300);
        frame.setVisible(true);
    }

    public JPanel buildCentralPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        regexInput = new JTextField();
        regexInput.addActionListener(main.getListener(Action.INPUTCHANGE));

        panel.add(regexInput, BorderLayout.NORTH);
        panel.add(buildExtractorDisplay(),BorderLayout.CENTER);
        panel.add(buildAnalyzerDisplay(), BorderLayout.SOUTH);

        return panel;
    }

    public JPanel buildAnalyzerDisplay(){
        JPanel panel = new JPanel(new GridLayout(2,1));

        regexDisplay = new JTextField();
        exampleDisplay = new JTextField();

        panel.add(regexDisplay);
        panel.add(exampleDisplay);

        return panel;
    }

    public JSplitPane buildExtractorDisplay(){
        matcherDisplay = new JTextArea();
        inputHigh = matcherDisplay.getHighlighter();
        splitList = new DefaultListModel<String>();
        JList<String> splitterDisplay = new JList<String>(splitList);
        JScrollPane matchScroll = new JScrollPane(matcherDisplay);
        matchScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JScrollPane splitScroll = new JScrollPane(splitterDisplay);
        splitScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,matchScroll,splitScroll);
        pane.setResizeWeight(0.5);
        return pane;
    }

    public JPanel buildSidePanel(){
        JPanel panel = new JPanel();
        JTextArea area = new JTextArea(15,15);
        panel.add(area);
        return panel;
    }

    public JTextField buildStatusBar(){
        statusBar = new JTextField();
        statusBar.setEnabled(false);
        return statusBar;
    }

    public void updateStatus(String message){
        statusBar.setText(message);
    }

    public void updateAnalysisDisplay(String pattern, String example){
        regexDisplay.setText(pattern);
        patternHigh = regexDisplay.getHighlighter();
        exampleDisplay.setText(example);
        elementHigh = exampleDisplay.getHighlighter();
    }

    public void addToSplitList(String element){
        splitList.addElement(element);
    }

    public String getRegEx(){
        return regexInput.getText();
    }

    public String getTextForRetrieval(){
        return matcherDisplay.getText();
    }

    public void highlightContent(ArrayList<String> indices){

        for(String index : indices){
            int[] temp = Extractor.arrayStringToInt(index.split(","));
            inputHigh.addHighlight(temp[0],temp[1],);
            }
        }
    }





}
