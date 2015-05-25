package Model;

import org.junit.Assert;
import org.junit.Test;

public class ExtractorTest {
    String pattern;
    String input;

    @Test
    public void testSearchForMatchForIndividual() throws Exception {
        pattern = "^[_A-Za-z0-9-\\\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        input = "address@domain.com";

        String test = 0 + "," + input.length();

        Assert.assertEquals(test,Extractor.search(pattern, input).get(0));
    }

    @Test
    public void testSearchForMatchForMultiple() throws Exception {
        Assert.assertEquals("4,6", Extractor.search("[A-Z]+", "AA66BB77").get(1));
    }

    @Test
    public void testSearchForMatchForNull() throws Exception {
        Assert.assertTrue(Extractor.search("[A-Z]+", "6677").isEmpty());
        Assert.assertTrue(Extractor.search("[1-9]+", "0000").isEmpty());
        Assert.assertTrue(Extractor.search("[a-z]+", "ABCD").isEmpty());

    }

    @Test
    public void testSearchForMatchForEmptyRegex() throws Exception {
        Assert.assertTrue(Extractor.search("","6677").isEmpty());
    }

    @Test
    public void testSplit() throws Exception {
        Assert.assertArrayEquals(new String[]{"a","b","c"},Extractor.split("\\.","a.b.c"));
        Assert.assertArrayEquals(new String[]{"a","b","c"},Extractor.split(",","a,b,c"));          Assert.assertArrayEquals(new String[]{"a","b","c"},Extractor.split(",","a,b,c"));
    }

    @Test
    public void testSplitWithNull() throws Exception {
        Assert.assertArrayEquals(new String[]{"abc"},Extractor.split(",","abc"));
    }

    @Test
    public void testAnalyzeSimple() throws Exception {
        Assert.assertEquals("3,2,1,0,",Extractor.analyze("abc","abc")[0]);
        Assert.assertEquals("3,2,1,0,", Extractor.analyze("abc","abc")[1]);
    }
    @Test
    public void testAnalyzeAdvanced() throws Exception{
        Assert.assertEquals("8,6,5,0,", Extractor.analyze("[a-z]+\\&","abc&")[0]);
        Assert.assertEquals("4,3,1,0,", Extractor.analyze("[a-z]+\\&","abc&")[1]);
    }
}
