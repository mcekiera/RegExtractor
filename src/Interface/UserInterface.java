package Interface;

import Control.IO;
import Control.Main;
import Model.Analyzer;
import Model.Grouper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
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
    private JTextArea splittedText;
    private JTabbedPane tab;
    private JTextArea explain;
    private JTextArea descriptionArea;
    private JList builder;
    private JTextArea groupsArea;
    private JList<String> groupList;

    public UserInterface(Main main){
        frame = new JFrame();
        this.main = main;
        font = new Font("Arial",Font.BOLD,16);

        inputRegex = new JTextField();
        inputRegex.setFont(font);
        inputRegex.setText("");
        inputRegex.getDocument().addDocumentListener(new TextListener());
        JButton reset = new JButton("RESET");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetView();
                inputRegex.setText("");
                explain.setText("");
                regexView.setText("");
                exampleView.setText("");
                matcherView.setText("");
            }
        });
        reset.setBorder(new LineBorder(Color.BLACK));
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north,BoxLayout.LINE_AXIS));
        north.add(inputRegex);
        north.add(reset);

        frame.add(north, BorderLayout.NORTH);


        frame.add(buildStatusBar(),BorderLayout.PAGE_END);
        frame.add(buildMatcherDisplay(),BorderLayout.CENTER);
        frame.add(buildSidePanel(),BorderLayout.EAST);
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
        tab.addTab("Groups", buildGroupPanel());
        tab.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tab.getSelectedIndex() == 0) {
                    main.setTabs(Tabs.MATCH);
                } else if (tab.getSelectedIndex() == 1) {
                    main.setTabs(Tabs.SPLIT);
                } else if (tab.getSelectedIndex() == 2) {
                    main.setTabs(Tabs.DESCRIBE);
                } else if (tab.getSelectedIndex() == 3) {
                    main.setTabs(Tabs.GROUPS);
                }
                main.updateMatchView();
            }
        });

        return tab;
    }

    public JScrollPane buildSplitPanel(){
        splittedText = new JTextArea();
        splittedText.setWrapStyleWord(true);
        splittedText.setLineWrap(true);
        splittedText.setEditable(false);
        return new JScrollPane(splittedText);

    }

    public JScrollPane buildExplainPanel(){
        explain = new JTextArea();
        explain.setFont(new Font("Arial", Font.BOLD, 16));
        return new JScrollPane(explain);
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
        JScrollPane analysis = new JScrollPane(panel);
        analysis.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane exampleScroll = new JScrollPane(examplesList);
        exampleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        exampleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        all.add(exampleScroll);
        all.add(analysis);
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
        statusBar.setEditable(false);
        return statusBar;
    }

    public JPanel buildSidePanel(){
        JPanel panel = new JPanel();
        ArrayList<String> elements = new ArrayList<String>(IO.load().keySet());
        builder = new JList<Object>(elements.toArray());
        builder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
                if(e.getClickCount()==2){
                    int caret = inputRegex.getCaretPosition();
                    String content = inputRegex.getText();
                    inputRegex.setText(content.substring(0,caret) + builder.getSelectedValue() + content.substring(caret));
                }
            }
        });
        builder.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                descriptionArea.setText(IO.load().get(builder.getSelectedValue().toString()));
            }
        });
        JScrollPane pane = new JScrollPane(builder);

        descriptionArea = new JTextArea(1,1);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);

        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(pane);
        panel.add(scrollPane);

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
        highlighter.removeAllHighlights();
        for (int index : toHighlight.keySet()) {
            try {
                highlighter.addHighlight(index, toHighlight.get(index), getPainter());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void displayExplanation(String description){
         explain.setText(description);
    }

    public void updateSplitTab(String[] parts){
        splittedText.setText(Arrays.toString(parts));
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
                System.out.println(ex.toString() + "BadLocationException is expected");
                //ex.printStackTrace();
            }
            r = i;
        }

    }

    public void updateAnalyzer(String regex,String example){
        regexView.setText(Analyzer.trimLookaround(regex));
        exampleView.setText(example);
        isNotSupported(regex);
    }

    public void updateExamples(){
        examples.clear();
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
    public void isNotSupported(String regex){
        String warning = "";
        if(regex.contains("??") || regex.contains("*?") || regex.contains("+?") || regex.contains("?+")
                || regex.contains("*+") || regex.contains("++") || regex.contains("}+") || regex.contains("?+")){
            warning += "Reluctant and possessive quantifiers. ";
        }
        if(regex.contains("(?<")){

            warning += "Named groups.";
        }
        if(regex.contains("(?=") || regex.contains("(?!") || regex.contains("(?<=") || regex.contains("(?<!")){
           warning += "Look ahead and look behind matches.";
        }

        if(warning.length()>0){
        updateStatus("Analyzer does not support: " + warning + " Visualization could show wrong results!");
        }
    }

    public JPanel buildGroupPanel(){
        JPanel all = new JPanel(new GridLayout(2,1));
        groupsArea = new JTextArea();
        groupsArea.setEditable(false);
        groupList = new JList<String>(examples);

        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.setLayoutOrientation(JList.VERTICAL);
        groupList.setVisibleRowCount(-1);
        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateGroups();
            }
        });
        JScrollPane scrollPane = new JScrollPane(groupsArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollPane listPane = new JScrollPane(groupList);
        listPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        all.add(listPane);
        all.add(scrollPane);


        return all;
    }

    public void updateGroups(){
        Grouper grouper = new Grouper();
        ArrayList<String> patternGroups = new ArrayList<String>(grouper.getPatternsGroups(inputRegex.getText()).values());
        ArrayList<String> exampleGroups = new ArrayList<String>(grouper.getExampleGroups(inputRegex.getText(),groupList.getSelectedValue()).values());
        String result = "";
        for(int i = 0; i <= patternGroups.size()-1; i++){
            result += "  "+ i + ":     " + patternGroups.get(i) + "   -   " + patternGroups.get(i) + "\n";
            System.out.println(patternGroups.size());
            System.out.println(patternGroups.get(i));
        }
        groupsArea.setText(result);
    }

}
