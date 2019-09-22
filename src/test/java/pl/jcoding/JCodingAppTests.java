package pl.jcoding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.jcoding.util.GalleryUtil;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JCodingAppTests {

    @Autowired
    private GalleryUtil galleryUtil;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testTempFileCreate() throws Exception{

        Long start = System.currentTimeMillis();
        for (int i=0; i<100; i++) {
            galleryUtil.getTempFile();
        }
        Long end = System.currentTimeMillis();
        System.out.println("Action done in: " + (((end - start) + 0.0) / 1000.0));
    }

    @Test
    public void testMoveTempFiles() throws Exception {
        File tempFolder = galleryUtil.getGalleryFolderTemp();
        Arrays.stream(tempFolder.listFiles()).forEach(file -> {
            try {
                galleryUtil.moveTempFile(UUID.fromString(file.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
