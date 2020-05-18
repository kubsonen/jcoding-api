package pl.jcoding.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.jcoding.entity.CarPair;
import pl.jcoding.repository.AdvertTempRepository;
import pl.jcoding.repository.CarAdvertRepository;

import java.util.List;

@Service
public class AdvertService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertService.class);

    @Value("${advert.url}")
    private String url;

    @Value("${advert.class.list.element}")
    private String classListElement;

    @Autowired
    private AdvertTempRepository advertTempRepository;

    @Autowired
    private CarAdvertRepository carAdvertRepository;

    @Autowired
    private CarPairService carPairService;

    public void getTempAdvertsForCarPair(CarPair cp) {

        try (final WebClient wc = new WebClient()) {
            wc.getOptions().setJavaScriptEnabled(false);
            wc.getOptions().setCssEnabled(false);

            // If fail on page request again

            for (int i = 1; i < 200; i++) {
                try {
                    String carUrl = url + cp.getBrand() + "/" + cp.getModel() + "/?page=" + i;
                    final HtmlPage hp = wc.getPage(carUrl);
                    List<HtmlDivision> advertPositions = hp.getPage().getByXPath("//div[@class='" + classListElement + "']");
                    log("Found " + advertPositions.size() + " positions");
                } catch (Throwable t) {
                    LOGGER.info("Fail");
                }

            }

        }
//        catch (IOException e) {
//            e.printStackTrace();
//            log("Error occurred in getTempAdvert, with message: " + e.getMessage() + " \n " + ExceptionUtils.getStackTrace(e));
//        }

    }

    public void log(String txt) {
        LOGGER.info(txt);
    }


}
