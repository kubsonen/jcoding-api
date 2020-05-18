package pl.jcoding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.jcoding.entity.Car;
import pl.jcoding.entity.CarPair;
import pl.jcoding.service.AdvertService;
import pl.jcoding.service.CarPairService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:app.properties")
public class JCodingAppTests {

    @Autowired
    private CarPairService carPairService;

    @Autowired
    private AdvertService advertService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCarPairImport() {
        carPairService.importCarPairs();
    }

    @Test
    public void testTempAdvert() {
        CarPair cp = new CarPair();
        cp.setBrand("volkswagen");
        cp.setModel("passat");

        advertService.getTempAdvertsForCarPair(cp);

    }

}
