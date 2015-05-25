package RegExtractor;

import Model.Analyzer;
import Model.Extractor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    JTextArea splitDisplay;
    JTextField regex;
    JTextArea matchDisplay;
    JTextField status;
    Extractor extractor;
    Highlighter highlighter;
    Highlighter.HighlightPainter painter = new DefaultHighlighter.
            DefaultHighlightPainter(Color.GREEN);
    Analyzer analyzer;

    public static void main (String[] args){
        Main main = new Main();
        main.start();
    }

    public void start(){
        JFrame frame = new JFrame();
        extractor = new Extractor();

        regex = new JTextField(20);
        regex.getDocument().addDocumentListener(new TextListener());
        analyzer = new Analyzer();
        status = new JTextField(20);
        status.setEnabled(false);
        JButton analyze = new JButton("Analyze");
        analyze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Extractor.analyze(regex.getText(), matchDisplay.getText());
            }
        });
        JPanel panel = new JPanel(new BorderLayout());
        JPanel side = new JPanel();
        side.setSize(34,43);

        panel.add(regex, BorderLayout.NORTH);
        panel.add(buildSplitPane(), BorderLayout.CENTER);
        //frame.add(status, BorderLayout.SOUTH);
        frame.add(analyze, BorderLayout.NORTH);
        frame.add(side,BorderLayout.EAST);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(analyzer.getAnalyzer(), BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocation(50,50);
        frame.setSize(500,500);
        frame.setVisible(true);
    }

    public void displayResults(int start, int end){
        try{
            highlighter.addHighlight(start, end, painter);
        }catch (BadLocationException ex){
            ex.printStackTrace();
        }
    }

    public void displaySplit(String fragment){
        splitDisplay.append(fragment);
    }

    public void displayStatus(String message){
        status.setText(message);
    }

    public JSplitPane buildSplitPane(){
        matchDisplay = new JTextArea();
        matchDisplay.getDocument().addDocumentListener(new TextListener());
        matchDisplay.setLineWrap(true);
        matchDisplay.setWrapStyleWord(true);
        JScrollPane matchScroll = new JScrollPane(matchDisplay);
        matchScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        highlighter = matchDisplay.getHighlighter();

        splitDisplay = new JTextArea();
        matchDisplay.setLineWrap(true);
        matchDisplay.setWrapStyleWord(true);
        JScrollPane splitScroll = new JScrollPane(splitDisplay);
        splitScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,matchScroll,splitScroll);
        splitPane.setResizeWeight(0.5);
        return splitPane;

    }


    private class TextListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            highlighter.removeAllHighlights();
            splitDisplay.setText("");
            Extractor.search(regex.getText(), matchDisplay.getText());
            Extractor.split(regex.getText(), matchDisplay.getText());

        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            highlighter.removeAllHighlights();
            splitDisplay.setText("");
            Extractor.search(regex.getText(), matchDisplay.getText());
            Extractor.split(regex.getText(), matchDisplay.getText());

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            highlighter.removeAllHighlights();
            splitDisplay.setText("");
            Extractor.search(regex.getText(), matchDisplay.getText());
            Extractor.split(regex.getText(), matchDisplay.getText());


        }
    }
}
