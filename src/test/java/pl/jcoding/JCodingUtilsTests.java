package pl.jcoding;

import org.junit.Test;
import pl.jcoding.util.Commons;

public class JCodingUtilsTests {

    private static final Integer length = 10;
    private static final String a = null;
    private static final String b = "";
    private static final String c = "a";
    private static final String d = "aaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Test
    public void testFillSpacesStringUtils(){

        System.out.println(">" + Commons.fillSpaces(a, length) + "<");
        System.out.println(">" + Commons.fillSpaces(b, length) + "<");
        System.out.println(">" + Commons.fillSpaces(c, length) + "<");
        System.out.println(">" + Commons.fillSpaces(d, length) + "<");

    }

}
