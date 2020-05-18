package pl.jcoding.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.jcoding.entity.CarPair;
import pl.jcoding.repository.CarPairRepository;

import javax.annotation.Resource;
import java.io.*;

@Service("carParService")
public class CarPairService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarPairService.class);

    @Value("${car.pair.import.directory}")
    private String importDirectory;

    @Autowired
    private CarPairRepository carPairRepository;

    @Resource(name = "carParService")
    private CarPairService carPairService;

    public void importCarPairs() {
        File file = new File(importDirectory);
        if (!file.exists()) return;
        File[] fd = file.listFiles();
        if (fd != null && fd.length > 0)
            importCarPairFile(fd[0]);
    }

    public void importCarPairFile(File file) {
        StringBuilder sb = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {

                    line = line.replace("\t", " ");
                    while (line.contains("  "))
                        line = line.replace("  ", " ");

                    if (sb == null)
                        sb = new StringBuilder();
                    sb.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.info("Error occurred");
        }
        if (sb != null)
            importCarPairText(sb.toString());
    }

    public void importCarPairText(String txt) {
        LOGGER.info("Import started");
        for (String line: txt.split("\n")) {
            String[] lineParts = line.split(" ");
            if (lineParts.length == 2) {
                String brand = lineParts[0];
                String model = lineParts[1];
                try {
                    CarPair cp = new CarPair();
                    cp.setBrand(brand);
                    cp.setModel(model);
                    carPairService.saveCarPair(cp);
                } catch (RuntimeException re) {
                    re.printStackTrace();
                    LOGGER.info("Cannot save car pair for values: \n-brand: " + brand + "\n-model: " + model);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCarPair(CarPair carPair) {
        carPairRepository.save(carPair);
    }

}
