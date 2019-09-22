package pl.jcoding;

import org.junit.Test;
import pl.jcoding.util.JcodingCommons;

public class JCodingUtilsTests {

    private static final Integer length = 10;
    private static final String a = null;
    private static final String b = "";
    private static final String c = "a";
    private static final String d = "aaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Test
    public void testFillSpacesStringUtils(){

        System.out.println(">" + JcodingCommons.fillSpaces(a, length) + "<");
        System.out.println(">" + JcodingCommons.fillSpaces(b, length) + "<");
        System.out.println(">" + JcodingCommons.fillSpaces(c, length) + "<");
        System.out.println(">" + JcodingCommons.fillSpaces(d, length) + "<");

    }

}
