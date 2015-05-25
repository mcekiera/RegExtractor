package Model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExtractorTest {
    String pattern;
    String input;
    ArrayList<int[]> test;

    @Test
    public void testSearchForMatchForIndividual() throws Exception {
        pattern = "^[_A-Za-z0-9-\\\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        input = "address@domain.com";

        test = new ArrayList<int[]>();
        test.add(new int[] {0,input.length()});

        Assert.assertArrayEquals(test.get(0),Extractor.search(pattern, input).get(0));
    }
    @Test
    public void testSearchForMatchForMultiple() throws Exception {
        pattern = "[A-Z]+";
        input = "AA66BB77";

        test = new ArrayList<int[]>();
        test.add(new int[] {0,2});
        test.add(new int[] {4,6});

        Assert.assertArrayEquals(test.get(1),Extractor.search(pattern, input).get(1));
    }
    @Test
    public void testSearchForMatchForNull() throws Exception {
        pattern = "[A-Z]+";
        input = "6677";

        Assert.assertTrue(Extractor.search(pattern, input).isEmpty());

    }

    @Test
    public void testSearchForMatchForEmptyRegex() throws Exception {
        pattern = "";
        input = "6677";

        Assert.assertTrue(Extractor.search(pattern, input).isEmpty());

    }

    @Test
    public void testSplit() throws Exception {
        pattern = ",";
        input = "a,b,c";

        String[] temp = {"a","b","c"};

        Assert.assertArrayEquals(temp,Extractor.split(pattern,input));
    }
    @Test
    public void testSplitWithNull() throws Exception {
        pattern = ",";
        input = "abc";

        String[] temp = {"abc"};

        Assert.assertArrayEquals(temp,Extractor.split(pattern,input));
    }
    @Test
    public void testAnalyzeSimple() throws Exception {
        pattern = "abc";
        input = "abc";


        List<Integer> patternParts = new ArrayList<Integer>();
        patternParts.add(3);
        patternParts.add(2);
        patternParts.add(1);
        patternParts.add(0);

        Assert.assertArrayEquals(patternParts.toArray(), Extractor.analyze(pattern,input).get(0).toArray());
        Assert.assertArrayEquals(patternParts.toArray(), Extractor.analyze(pattern,input).get(1).toArray());
    }
    @Test
    public void testAnalyzeAdvenced() throws Exception {
        pattern = "[a-z]+\\&";
        input = "abc&";

        List<Integer> inputParts = new ArrayList<Integer>();
        inputParts.add(4);
        inputParts.add(3);
        inputParts.add(1);
        inputParts.add(0);

        List<Integer> patternParts = new ArrayList<Integer>();
        patternParts.add(pattern.length());
        patternParts.add(6);
        patternParts.add(5);
        patternParts.add(0);

        Assert.assertArrayEquals(patternParts.toArray(), Extractor.analyze(pattern,input).get(0).toArray());
        Assert.assertArrayEquals(inputParts.toArray(), Extractor.analyze(pattern,input).get(1).toArray());
    }
}
