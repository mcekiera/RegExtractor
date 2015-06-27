package Interface;

import Control.IO;
import Control.Main;
import Model.Analyzer;
import Model.Extractor;
import Model.Grouper;
import Model.Options;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

/**
 * Creates GUI
 */
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
    private JList<String> groupList;
    private DefaultTableModel model;
    private JCheckBox multiline;
    private JCheckBox caseSensitive;

    /**
     * Basic constructor, creates JFrame and implements all necessary components.
     * @param main reference to Main class object, which controls GUI behaviour and pass data form GUI to model classes
     */
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
            /**
             * Reset content of all text components
             * @param e ActionEvent
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                resetView();
                inputRegex.setText("");
                explain.setText("");
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

    /**
     * Creates tabbed display
     * @return JTabbedPane with all panels
     */
    public JTabbedPane buildTabPanel(){
        tab = new JTabbedPane();

        tab.addTab("Analyze", buildAnalyzerDisplay());
        tab.addTab("Split", buildSplitPanel());
        tab.addTab("Explain", buildExplainPanel());
        tab.addTab("Groups", buildGroupPanel());
        tab.addChangeListener(new ChangeListener() {
            @Override
            /**
             * Controls selection of tab and therefore choose set of currently working GUI methods
             */
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

    /**
     * Creates split tab in which splitting of text by given regular expression is displayed
     * @return JScrollPane
     */
    public JScrollPane buildSplitPanel(){
        splittedText = new JTextArea();
        splittedText.setWrapStyleWord(true);
        splittedText.setLineWrap(true);
        splittedText.setEditable(false);
        return new JScrollPane(splittedText);

    }

    /**
     * Creates explaining tab in which explaining of regular expression is displayed
     * @return JScrollPane
     */
    public JScrollPane buildExplainPanel(){
        explain = new JTextArea();
        explain.setFont(new Font("Arial", Font.BOLD, 16));
        return new JScrollPane(explain);
    }

    /**
     * Creates analyzing display with example list, and two text fields displaying analyzed example
     * @return JPanel
     */
    public JPanel buildAnalyzerDisplay(){
        JPanel all = new JPanel(new GridLayout(2,1));

        examples = new DefaultListModel<String>();
        examplesList = new JList<String>(examples);
        examplesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examplesList.setLayoutOrientation(JList.VERTICAL);
        examplesList.setVisibleRowCount(-1);
        examplesList.addMouseListener(new MouseAdapter() {
            /**
             * Controls selection of example to analyze, from list of matched fragments
             * @param e MouseEvent
             */
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

    /**
     * Creates text area in which regular expression matching is displayed
     * @return JSplitPane
     */
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
        pane.setResizeWeight(0.5);
        return pane;
    }

    /**
     * Creates status bar for warning display
     * @return JTextField
     */
    public JTextField buildStatusBar(){
        statusBar = new JTextField();
        statusBar.setEditable(false);
        return statusBar;
    }

    /**
     * Creates side panel with options selection and builder panel
     * @return JPanel
     */
    public JPanel buildSidePanel(){
        JPanel panel = new JPanel();
        DefaultListModel<String> model = new DefaultListModel<String>();
        builder = new JList<String>(model);
        for(String key : IO.load().keySet()){
            model.addElement(key);
        }
        builder.addMouseListener(new MouseAdapter() {

            /**
             * Controls selection from building panel list
             * @param e MouseEvent
             */
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
            /**
             * Controls contents displayed on building text area
             * @param e ListSelectionEvent
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                descriptionArea.setText(IO.load().get(builder.getSelectedValue().toString()));
            }
        });
        JScrollPane pane = new JScrollPane(builder);

        descriptionArea = new JTextArea(1,1);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);

        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(buildOptions());
        panel.add(pane);
        panel.add(scrollPane);

        return panel;
    }

    /**
     * Creates side panel part responsible for displaying modes options
     * @return JPanel
     */
    public JPanel buildOptions(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        multiline = new JCheckBox("Multiline",false);
        caseSensitive = new JCheckBox("Case sensitive",false);
        panel.add(multiline);
        panel.add(caseSensitive);
        multiline.addItemListener(new OptionsUpdater());
        caseSensitive.addItemListener(new OptionsUpdater());
        return panel;
    }

    /**
     * Creates grouping tab, when captured by regex groups, and grouping parts of regex are displayed
     * @return JPanel
     */
    public JPanel buildGroupPanel(){
        JPanel all = new JPanel(new GridLayout(2,1));
        String[] columnNames = {"No","Pattern part","Example part"};
        String[][] data = new String[0][];
        model = new DefaultTableModel(data,columnNames);
        JTable grouping = new JTable(model);
        grouping.setEnabled(false);
        JTextArea groupsArea = new JTextArea();
        groupsArea.setEditable(false);
        groupList = new JList<String>(examples);

        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.setLayoutOrientation(JList.VERTICAL);
        groupList.setVisibleRowCount(-1);
        groupList.addMouseListener(new MouseAdapter() {
            /**
             * Controls selection of example to divide into groups
             * @param e MouseEvent
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateGroups();
            }
        });
        JScrollPane scrollPane = new JScrollPane(grouping);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollPane listPane = new JScrollPane(groupList);
        listPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        all.add(listPane);
        all.add(scrollPane);
        return all;
    }

    /**
     * @return currently used regular expression
     */
    public String getRegEx(){
        return inputRegex.getText();
    }

    /**
     * @return given text example for matching purposes
     */
    public String getTextForMatching(){
        return matcherView.getText();
    }

    /**
     * Highlights text on matching area, matched by currently used regex
     * @param toHighlight Map with start and end indices of fragments to highlight
     */
    public void highlightMatchedText(TreeMap<Integer,Integer> toHighlight){
        highlighter.removeAllHighlights();
        for (int index : toHighlight.keySet()) {
            try {
                highlighter.addHighlight(index, toHighlight.get(index), getPainter());
            } catch (BadLocationException ex) {
                Main.exceptionMessage(ex);
            }
        }
    }

    /**
     * Highlights parts of selected example and used regex, which are related. The colors of highlight is same for
     * matched fragment, and part of regex responsible for match
     * @param elements Map with start and end indices for highlight
     */
    public void highlightAnalyzedElements(TreeMap<Integer, Integer> elements){
        int r = 0;
        Highlighter.HighlightPainter pointer;
        for(int i : elements.keySet()){
            pointer = getPainter();
            try{
                forExample.addHighlight(elements.get(r),elements.get(i),pointer);
                forPattern.addHighlight(r,i,pointer);
            }catch (BadLocationException ex){
                Main.exceptionMessage(ex);
            }
            r = i;
        }

    }

    /**
     * Display description of regex in explains tab
     * @param description to display
     */
    public void displayExplanation(String description){
         explain.setText(description);
    }

    /**
     * Display result of split on given example text by currently used regular expression
     * @param parts array containing result of split
     */
    public void updateSplitTab(String[] parts){
        splittedText.setText(Arrays.toString(parts));
    }

    /**
     * Display given message on status bar
     * @param message to display
     */
    public void updateStatus(String message){
        statusBar.setText(message);
    }

    /**
     * Display in analysis area a chosen text example and currently used regex
     * @param regex
     * @param example
     */
    public void updateAnalyzer(String regex,String example){
        regexView.setText(Analyzer.trimLookAround(regex));
        exampleView.setText(example);
        ifNotSupported(regex);
    }

    /**
     * Displays fragments of example text, matched by given regex, in a JList
     */
    public void updateExamples(){
        examples.clear();
        for(Highlighter.Highlight light : highlighter.getHighlights()){
           examples.addElement(matcherView.getText().substring(light.getStartOffset(), light.getEndOffset()));
        }
    }

    /**
     * Provide a colors for highlighting text elements
     * @return Highlighter.HighlightPainter object witch random color
     */
    public static Highlighter.HighlightPainter getPainter(){
        Random random = new Random();
        int mod = 76;
        int red = random.nextInt(256-mod)+mod;
        int green = random.nextInt(256-mod)+mod;
        int blue = random.nextInt(256-mod)+mod;

        return new DefaultHighlighter.DefaultHighlightPainter(new Color(red,green,blue));
    }

    /**
     * Delete contents of all text components
     */
    public void resetView(){
        examples.removeAllElements();
        splittedText.setText("");
        highlighter.removeAllHighlights();
        regexView.setText("");
        exampleView.setText("");
    }

    /**
     * Checks and display appropriate message on status bar, if in given regular expression are elements which are
     * not supported, because of various reasons, by analyzing process
     * @param regex to control
     */
    public void ifNotSupported(String regex){
        String warning = "";
        if(regex.contains("??") || regex.contains("*?") || regex.contains("+?") || regex.contains("?+")
                || regex.contains("*+") || regex.contains("++") || regex.contains("}+") || regex.contains("?+")){
            warning += "reluctant and possessive quantifiers, ";
        }

        if(regex.contains("(?=") || regex.contains("(?!") || regex.contains("(?<=") || regex.contains("(?<!")){
            warning += "look ahead and look behind matches,";
        }

        if(regex.contains("?>")){
            warning += "atomic grouping";
        }

        if(warning.length()>0){
            updateStatus("Analyzer does not support: " + warning.substring(0,warning.length()-1) + " ...Visualization could show wrong results!");
        }
    }

    /**
     * Provide matching and matched groups of particular regex and text and display it on JList
     */
    public void updateGroups(){

        Grouper grouper = new Grouper();

        ArrayList<String> patternGroups = new ArrayList<String>(grouper.getPatternsGroups(Analyzer.trimLookAround(inputRegex.getText())).values());
        ArrayList<String> exampleGroups = new ArrayList<String>(grouper.getExampleGroups(Analyzer.trimLookAround(inputRegex.getText()),groupList.getSelectedValue()).values());
        String[] row = new String[3];
        for (int i = model.getRowCount(); i > 0; i--) {
            model.removeRow(0);
        }
        for(int i = 0; i < exampleGroups.size(); i++){

            row[0] = String.valueOf(i);
            row[1] = patternGroups.get(i);
            row[2] = exampleGroups.get(i);
            model.insertRow(i,row);
        }
        frame.revalidate();
    }

    /**
     * Controls reaction on user text input and change
     */
    private class TextListener implements DocumentListener {
        /**
         * Reacts on text insertion
         */
        @Override
        public void insertUpdate(DocumentEvent e) {
            resetView();
            main.updateMatchView();
        }

        /**
         * Reacts on text removal
         */
        @Override
        public void removeUpdate(DocumentEvent e) {
            resetView();
            main.updateMatchView();
        }

        /**
         * Reacts on changes in text
         */
        @Override
        public void changedUpdate(DocumentEvent e) {
            resetView();
            main.updateMatchView();
        }
    }

    /**
     * Controls selection of options(modes of regex)
     */
    private class OptionsUpdater implements ItemListener {
        /**
         * Reacts on user selection of modes
         * @param e ItemEvent
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if(multiline.isSelected() && caseSensitive.isSelected()){
                Extractor.setOptions(Options.BOTH);
            }else if(multiline.isSelected()){
                Extractor.setOptions(Options.MULTILINE);
            }else{
                Extractor.setOptions(Options.NULL);
            }
            main.updateMatchView();
        }
    }
}
