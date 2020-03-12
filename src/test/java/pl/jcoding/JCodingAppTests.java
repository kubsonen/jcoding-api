package pl.jcoding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:app.properties")
public class JCodingAppTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testTempFileCreate() {
    }

    @Test
    public void testMoveTempFiles() {
    }


}
