package pl.jcoding;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.jcoding.util.CarUtil;

public class UtilTest {

    private static final Logger L = LoggerFactory.getLogger(UtilTest.class);

    @Test
    public void tempVinTester() {
        String genVin = CarUtil.tempVin().toUpperCase();
        L.info("Vin: " + genVin);
        L.info("Vin size: " + genVin.length());
    }

}
