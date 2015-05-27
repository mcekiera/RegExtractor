package Interface;

import Control.Main;
import Model.Extractor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class UserInterface {

    private JTextField statusBar;
    private JTextField inputRegex;
    private JTextField regexView;
    private JTextField exampleView;
    private JTextArea matcherView;
    private DefaultListModel<String> examples;
    private JList<String> examplesView;
    private Highlighter highlighter;
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

        inputRegex = new JTextField();
        inputRegex.getDocument().addDocumentListener(new TextListener());

        panel.add(inputRegex, BorderLayout.NORTH);
        panel.add(buildMatcherDisplay(),BorderLayout.CENTER);
        panel.add(buildAnalyzerDisplay(), BorderLayout.SOUTH);

        return panel;
    }

    public JPanel buildAnalyzerDisplay(){
        JPanel panel = new JPanel(new GridLayout(2,1));

        regexView = new JTextField();
        exampleView = new JTextField();

        panel.add(regexView);
        panel.add(exampleView);

        return panel;
    }

    public JSplitPane buildMatcherDisplay(){
        matcherView = new JTextArea();
        highlighter = matcherView.getHighlighter();
        matcherView.setWrapStyleWord(true);
        matcherView.setLineWrap(true);
        examples = new DefaultListModel<String>();
        examplesView = new JList<String>(examples);
        examplesView.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                main.analyze(examplesView.getSelectedValue());
            }
        });
        JScrollPane matchScroll = new JScrollPane(matcherView);
        matchScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JScrollPane splitScroll = new JScrollPane(examplesView);
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

    public String getRegEx(){
        return inputRegex.getText();
    }

    public String getTextForMatching(){
        return matcherView.getText();
    }

    public void highlightMatchedText(ArrayList<String> toHighlight){
        for(int i = 0; i < toHighlight.size(); i++){
        int[] temp = Extractor.arrayStringToInt(toHighlight.get(i).split(","));
            try{
                highlighter.addHighlight(temp[0], temp[1], getPainter());
            }catch (BadLocationException ex){
                ex.printStackTrace();
            }
        }
    }

    public void highlightAnalyzedElements(String[] indices){
        String[] splittedPattern = indices[0].split(",");
        Highlighter forPattern = regexView.getHighlighter();
        String[] splittedExample = indices[1].split(",");
        Highlighter forExample = exampleView.getHighlighter();
        Highlighter.HighlightPainter pointer;

        for(int i = splittedPattern.length-1; i >= 0; i--){
            pointer = getPainter();
            try{
            forPattern.addHighlight(0,Integer.parseInt(splittedPattern[i]),pointer);
            forExample.addHighlight(0,Integer.parseInt(splittedExample[i]),pointer);
            }catch (BadLocationException ex){
                ex.printStackTrace();
            }

        }

    }

    public void updateAnalyzer(String regex,String example){
        regexView.setText(regex);
        exampleView.setText(example);
    }

    public void addExamples(){
        for(Highlighter.Highlight light : highlighter.getHighlights()){
            examples.addElement(matcherView.getText().substring(light.getStartOffset(), light.getEndOffset()));
        }
    }

    public static Highlighter.HighlightPainter getPainter(){
            Random r = new Random();
            return new DefaultHighlighter.DefaultHighlightPainter(new Color(r.nextFloat(),r.nextFloat(),r.nextFloat()));
    }

    public void resetView(){
        examples.removeAllElements();
        highlighter.removeAllHighlights();
    }


    private class TextListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            resetView();
            main.updateView();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            resetView();
            main.updateView();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            resetView();
            main.updateView();
        }
    }
}
