package Interface;

import Control.Main;
import Model.Extractor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class UserInterface {
    private JFrame frame;
    private JTextField statusBar;
    private JTextField inputRegex;
    private JTextField regexView;
    private JTextField exampleView;
    private JTextArea matcherView;
    private DefaultListModel<String> examples;
    private JList<String> examplesList;
    private Highlighter highlighter;
    private Highlighter forPattern;
    private Highlighter forExample;
    private Main main;
    private Font font;

    public UserInterface(Main main){

        frame = new JFrame();
        this.main = main;
        font = new Font("Arial",Font.BOLD,16);


        frame.add(buildStatusBar(),BorderLayout.PAGE_END);
        frame.add(buildCentralPanel(),BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900,500);
        frame.setTitle("Basic Java Regular Expression Visualizer");
        frame.setVisible(true);
    }

    public JPanel buildCentralPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        inputRegex = new JTextField();
        inputRegex.setFont(font);
        inputRegex.getDocument().addDocumentListener(new TextListener());

        panel.add(inputRegex, BorderLayout.NORTH);
        panel.add(buildMatcherDisplay(),BorderLayout.CENTER);
        panel.add(buildAnalyzerDisplay(), BorderLayout.SOUTH);

        return panel;
    }

    public JScrollPane buildAnalyzerDisplay(){
        JPanel panel = new JPanel(new GridLayout(2,1));
        Font font = new Font("Arial",Font.BOLD,34);
        regexView = new JTextField();
        forPattern = regexView.getHighlighter();
        regexView.setFont(font);
        regexView.setForeground(Color.BLACK);
        regexView.setEditable(false);
        exampleView = new JTextField();
        forExample = exampleView.getHighlighter();
        exampleView.setFont(font);
        exampleView.setForeground(Color.BLACK);
        exampleView.setEditable(false);

        panel.add(regexView);
        panel.add(exampleView);
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    public JSplitPane buildMatcherDisplay(){
        matcherView = new JTextArea();
        matcherView.getDocument().addDocumentListener(new TextListener());
        matcherView.setFont(font);
        highlighter = matcherView.getHighlighter();
        matcherView.setWrapStyleWord(true);
        matcherView.setLineWrap(true);
        examples = new DefaultListModel<String>();
        examplesList = new JList<String>(examples);
        examplesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examplesList.setLayoutOrientation(JList.VERTICAL);
        examplesList.setVisibleRowCount(-1);
        examplesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                main.analyze(examplesList.getSelectedValue());
                frame.revalidate();
                frame.repaint();
            }
        });
        JScrollPane matchScroll = new JScrollPane(matcherView);
        matchScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        matchScroll.setPreferredSize(new Dimension(250,150));
        JScrollPane splitScroll = new JScrollPane(examplesList);
        splitScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        splitScroll.setPreferredSize(new Dimension(150,70));

        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,matchScroll,splitScroll);
        pane.setResizeWeight(1);
        return pane;
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
        for (String aToHighlight : toHighlight) {
            int[] temp = Extractor.arrayStringToInt(aToHighlight.split(","));
            try {
                highlighter.addHighlight(temp[0], temp[1], getPainter());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void highlightAnalyzedElements(TreeMap<Integer, Integer> elements){
        int r = 0;
        Highlighter.HighlightPainter pointer;
        for(int i : elements.keySet()){
            pointer = getPainter();
            try{
                forExample.addHighlight(elements.get(r),elements.get(i),pointer);
                forPattern.addHighlight(r,i,pointer);
            }catch (BadLocationException ex){
                System.out.println(ex.toString() + "BadLocationException is expected" + ex.getCause());
                ex.printStackTrace();
            }
            r = i;
        }

    }

    public void updateAnalyzer(String regex,String example){
        regexView.setText(regex);
        exampleView.setText(example);
    }

    public void updateExamples(){
        for(Highlighter.Highlight light : highlighter.getHighlights()){
            examples.addElement(matcherView.getText().substring(light.getStartOffset(), light.getEndOffset()));
        }
    }

    public static Highlighter.HighlightPainter getPainter(){
        Random random = new Random();
        int mod = 76;
        int red = random.nextInt(256-mod)+mod;
        int green = random.nextInt(256-mod)+mod;
        int blue = random.nextInt(256-mod)+mod;

        return new DefaultHighlighter.DefaultHighlightPainter(new Color(red,green,blue));
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
