package pl.jcoding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jcoding.service.CarPairService;

@RestController
@RequestMapping("car-pair")
public class CarPairController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarPairController.class);

    private final CarPairService carPairService;

    public CarPairController(CarPairService carPairService) {
        this.carPairService = carPairService;
    }

    @GetMapping("import")
    public void importPairs() {
        log("Start");
        carPairService.importCarPairs();
        log("Done");
    }

    private void log(String log) {
        LOGGER.info(log);
    }

}
