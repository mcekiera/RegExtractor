package Model;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class Analyzer {
    String regex;
    String analyzed;
    Highlighter highlighter;
    Highlighter.HighlightPainter painter;
    Highlighter highlighter2;
    JTextArea area1;
    JTextArea area2;

    public Highlighter.HighlightPainter getPainter(int color){
        switch (color){
            case 1:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.GREEN);
                break;
            case 2:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.RED);
                break;
            case 3:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.BLUE);
                break;
            case 4:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.YELLOW);
                break;
            case 5:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.GRAY);
                break;
            case 6:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.PINK);
                break;
            case 7:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.CYAN);
                break;
            case 8:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.MAGENTA);
                break;
            case 9:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.ORANGE);
                break;
            case 0:
                painter = new DefaultHighlighter.
                        DefaultHighlightPainter(Color.LIGHT_GRAY);
                break;
        }
        return painter;

    }

    public JPanel getAnalyzer(){
        JPanel panel = new JPanel(new GridLayout(2,1));
        area1 = new JTextArea(2,80);
        area2 = new JTextArea(2,80);
        highlighter = area1.getHighlighter();
        highlighter2 = area2.getHighlighter();
        panel.add(area1);
        panel.add(area2);
        return panel;
    }



}
