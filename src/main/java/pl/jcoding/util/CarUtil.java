package pl.jcoding.util;

import pl.jj.util.generator.RandomString;

public class CarUtil {

    public static final String TEMP_VIN_PREFIX = "$";

    public static String tempVin() {
        return TEMP_VIN_PREFIX + RandomString.getRandomString().substring(0, 16);
    }

}
