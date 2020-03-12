package pl.jcoding.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CarPairService {

    @Async
    public void importCarPairs() {
        try {
            Thread.sleep(2000);
            System.out.println("DONE");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
