package Model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExtractorTest {
    String pattern;
    String input;
    Extractor extractor;

    @Before
    public void setUp(){
        extractor = new Extractor();
    }

    @Test
    public void testSearchForMatchForIndividual() throws Exception {
        pattern = "^[_A-Za-z0-9-\\\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        input = "address@domain.com";

        String test = 0 + "," + input.length();

        Assert.assertEquals(test,extractor.search(pattern, input).get(0));
    }

    @Test
    public void testSearchForMatchForMultiple() throws Exception {
        Assert.assertEquals("4,6", extractor.search("[A-Z]+", "AA66BB77").get(1));
    }

    @Test
    public void testSearchForMatchForNull() throws Exception {
        Assert.assertTrue(extractor.search("[A-Z]+", "6677").isEmpty());
        Assert.assertTrue(extractor.search("[1-9]+", "0000").isEmpty());
        Assert.assertTrue(extractor.search("[a-z]+", "ABCD").isEmpty());

    }

    @Test
    public void testSearchForMatchForEmptyRegex() throws Exception {
        Assert.assertTrue(extractor.search("","6677").isEmpty());
    }

    @Test
    public void testSplit() throws Exception {
        Assert.assertArrayEquals(new String[]{"a","b","c"},extractor.split("\\.","a.b.c"));
        Assert.assertArrayEquals(new String[]{"a","b","c"},extractor.split(",","a,b,c"));          Assert.assertArrayEquals(new String[]{"a","b","c"},extractor.split(",","a,b,c"));
    }

    @Test
    public void testSplitWithNull() throws Exception {
        Assert.assertArrayEquals(new String[]{"abc"},extractor.split(",","abc"));
    }

}
