package RegExtractor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

    public void analyze(String regex, String analyzed){
        String expresion = regex;
        this.analyzed = analyzed;
        ArrayList<Integer> analyzedparts = new ArrayList<Integer>();
        ArrayList<Integer> regexparts = new ArrayList<Integer>();
        area1.setText(analyzed);
        area2.setText(regex);
        Pattern pattern;
        Matcher matcher;
        int paint = 0;

        for(int i = regex.length(); i >= 0 ; i--){
            try{
                pattern = Pattern.compile(regex.substring(0,i));
                matcher = pattern.matcher(analyzed);
                matcher.find();
                //.addHighlight(matcher.start(), matcher.end(), getPainter(paint));
                analyzedparts.add(matcher.end());
                regexparts.add(i);
                //highlighter2.addHighlight(0,i,getPainter(paint));
                if(paint ==9){
                    paint = 0;
                }

            }catch (PatternSyntaxException ex){
                //ex.printStackTrace();

            //}catch (BadLocationException ex){
                //ex.printStackTrace();
            }catch (IllegalStateException ex){
                //ex.printStackTrace();
            }

        }

        for(int i = analyzedparts.size()-1; i >=0 ; i--){
            try{
                highlighter.addHighlight(0,analyzedparts.get(i), getPainter(paint));
                highlighter2.addHighlight(0,regexparts.get(i),getPainter(paint));
                paint++;
            }catch (BadLocationException ex){
                ex.printStackTrace();
            }

        }
        System.out.println("end");
    }

}
