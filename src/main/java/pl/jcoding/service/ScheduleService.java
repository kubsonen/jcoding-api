package pl.jcoding.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final AuctionService as;
    private final CarPairService cps;

    public ScheduleService(AuctionService auctionService, CarPairService cps) {
        this.as = auctionService;
        this.cps = cps;
    }

//    @Scheduled(fixedDelay = 3000000) //5 minutes
//    @Scheduled(fixedDelay = 1000) //1 second
    public void refreshAuctions() {
        as.addAuctionsLocally(as.auctionsToAdd(as.getActionPositions()));
    }

//    @Scheduled(fixedRate = 5000)
    public void importCarPairs() {
        cps.importCarPairs();
    }

}
