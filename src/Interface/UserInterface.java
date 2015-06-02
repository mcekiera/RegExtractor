package Interface;

import Control.Main;
import Model.Options;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    JTextArea textToSplit;
    JTextArea splittedText;
    JTabbedPane tab;

    public UserInterface(Main main){

        frame = new JFrame();
        this.main = main;
        font = new Font("Arial",Font.BOLD,16);

        inputRegex = new JTextField();
        inputRegex.setFont(font);
        inputRegex.getDocument().addDocumentListener(new TextListener());

        frame.add(inputRegex, BorderLayout.NORTH);


        frame.add(buildStatusBar(),BorderLayout.PAGE_END);
        frame.add(buildMatcherDisplay(),BorderLayout.CENTER);
        //frame.add(buildSidePanel(),BorderLayout.EAST);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900,500);
        frame.setTitle("Basic Java Regular Expression Visualizer");
        frame.setVisible(true);
    }

    public JTabbedPane buildTabPanel(){

        tab = new JTabbedPane();
        tab.addTab("Analyze", buildAnalyzerDisplay());
        tab.addTab("Split", buildSplitPanel());
        tab.addTab("Explain", buildExplainPanel());

        return tab;
    }

    public JScrollPane buildSplitPanel(){
        splittedText = new JTextArea();
        JScrollPane splitScroll = new JScrollPane(splittedText);

        return splitScroll;

    }

    public JScrollPane buildExplainPanel(){
        JTextArea area = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(area);
        return scrollPane;
    }

    public JPanel buildAnalyzerDisplay(){
        JPanel all = new JPanel(new GridLayout(2,1));

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

        JScrollPane splitScroll = new JScrollPane(examplesList);
        splitScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        splitScroll.setPreferredSize(new Dimension(150,70));

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
        JScrollPane exampleScroll = new JScrollPane(examplesList);
        exampleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        exampleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        all.add(exampleScroll);
        all.add(panel);
        return all;
    }

    public JSplitPane buildMatcherDisplay(){
        matcherView = new JTextArea();
        matcherView.getDocument().addDocumentListener(new TextListener());
        matcherView.setFont(font);
        highlighter = matcherView.getHighlighter();
        matcherView.setWrapStyleWord(true);
        matcherView.setLineWrap(true);

        JScrollPane matchScroll = new JScrollPane(matcherView);
        matchScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        matchScroll.setPreferredSize(new Dimension(250,150));

        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,matchScroll,buildTabPanel());
        pane.setResizeWeight(1);
        return pane;
    }

    public JTextField buildStatusBar(){
        statusBar = new JTextField();
        statusBar.setEnabled(false);
        return statusBar;
    }

    public JPanel buildSidePanel(){
        JPanel panel = new JPanel();

        JComboBox<Options> combo = new JComboBox<Options>(Options.values());

        //JRadioButton caseSensitive = new JRadioButton(Options.CASE_INSENSITIVE.name());
        //panel.add(caseSensitive);
        //JRadioButton multiline = new JRadioButton(Options.MULTILINE.name());
        //panel.add(multiline);

        panel.add(combo);
        return panel;
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

    public void highlightMatchedText(TreeMap<Integer,Integer> toHighlight){
        for (int index : toHighlight.keySet()) {
            try {
                highlighter.addHighlight(index, toHighlight.get(index), getPainter());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateSplitTab(String[] parts){
        for(String part : parts){
            splittedText.append(part + "\n");
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
        splittedText.setText("");
        highlighter.removeAllHighlights();
    }


    private class TextListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            resetView();
            main.updateMatchView();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            resetView();
            main.updateMatchView();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            resetView();
            main.updateMatchView();
        }
    }
}
