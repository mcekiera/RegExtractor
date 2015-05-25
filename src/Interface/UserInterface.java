package Interface;

import javax.swing.*;
import java.awt.*;

public class UserInterface {

    private JTextField status;
    private JFrame frame;

    UserInterface(){
        frame = new JFrame();

        frame.add(buildStatusBar(),BorderLayout.PAGE_END);
        frame.add(buildCentralPanel(),BorderLayout.CENTER);
        frame.add(buildSidePanel(),BorderLayout.EAST);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600,300);
        frame.setVisible(true);
    }

    public JPanel buildCentralPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        JTextField regex = new JTextField();


        panel.add(regex, BorderLayout.NORTH);
        panel.add(buildExtractorDisplay(),BorderLayout.CENTER);
        panel.add(buildAnalyzerDisplay(), BorderLayout.SOUTH);

        return panel;
    }

    public JPanel buildAnalyzerDisplay(){
        JPanel panel = new JPanel(new GridLayout(2,1));

        JTextField regex = new JTextField();
        JTextField input = new JTextField();

        panel.add(regex);
        panel.add(input);

        return panel;
    }

    public JSplitPane buildExtractorDisplay(){
        JTextArea matcher = new JTextArea();
        JList splitter = new JList();
        JScrollPane matchScroll = new JScrollPane(matcher);
        matchScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JScrollPane splitScroll = new JScrollPane(splitter);
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
        status = new JTextField();
        status.setEnabled(false);
        return status ;
    }

    public void updateStatus(String message){
        status.setText(message);
    }

    public static void main(String[] args){
        UserInterface UI = new UserInterface();
    }
}
