package pl.jcoding.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.jcoding.entity.Car;
import pl.jcoding.entity.CarAuction;
import pl.jcoding.repository.CarAuctionRepository;
import pl.jcoding.util.DomUtil;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    @Value("${auction.url}")
    private String url;

    @Value("${auction.auctions}")
    private String auctions;

    @Value("${auction.class.list.element}")
    private String listElement;

    @Value("${auction.class.tittle.element}")
    private String tittleElement;

    @Value("${auction.class.end.auction.element}")
    private String endAuctionElement;

    @Value("${auction.class.details.element}")
    private String detailsElement;

    private static final Logger LOGGER = LoggerFactory.getLogger("auctions");
    private static final String PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String TEXT_PATTERN = "MMM dd, yyyy";

    private final CarAuctionRepository carAuctionRepository;
    private final DomUtil domUtil;

    @Resource
    private AuctionService auctionService;

    public AuctionService(CarAuctionRepository carAuctionRepository, DomUtil domUtil) {
        this.carAuctionRepository = carAuctionRepository;
        this.domUtil = domUtil;
    }

    public Map<String, String> getActionPositions() {

        //Identity | Link
        Map<String, String> auctionPositionMap = null;

        try (final WebClient wc = new WebClient()) {
            wc.getOptions().setJavaScriptEnabled(false);
            wc.getOptions().setCssEnabled(false);

            final HtmlPage page = wc.getPage(url + auctions);
            List<HtmlDivision> pp = page.getBody().getByXPath("//div[@class='" + listElement + "']");
            log("Found " + pp.size() + " auctions.");

            for (HtmlDivision div : pp) {

                HtmlAnchor ha = domUtil.getByIndex(div, 0);
                HtmlDivision hd = domUtil.getByIndex(div, 1);
                HtmlParagraph infoParagraph = domUtil.getByIndex(hd, 1);
                HtmlBold identityBold = domUtil.getByIndex(infoParagraph, 6);

                String link = ha.getHrefAttribute();
                if (link.startsWith("/"))
                    link = link.substring(1);

                String identity = identityBold.getTextContent();

                if (auctionPositionMap == null)
                    auctionPositionMap = new HashMap<>();

                auctionPositionMap.put(identity, link);

            }

        } catch (Throwable t) {
            t.printStackTrace();
            log("Error occurred in refresh auctions, with message: " + t.getMessage() + " \n " + ExceptionUtils.getStackTrace(t));
        }

        return auctionPositionMap;

    }

    public Collection<String> auctionsToAdd(Map<String, String> auctionPositionMap) {
        Collection<String> actualPullAuctionIdentities = auctionPositionMap.keySet();
        Collection<String> identitiesExistsInDatabase = carAuctionRepository.getAuctionsForIdentities(actualPullAuctionIdentities);

        Collection<String> positionToAdd = actualPullAuctionIdentities
                .stream()
                .filter(s -> !identitiesExistsInDatabase.contains(s))
                .collect(Collectors.toList());

        Collection<String> auctionsToAddLinks = auctionPositionMap.entrySet().stream()
                .filter(sse -> positionToAdd.contains(sse.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        log("Positions " + positionToAdd.size() + " to add with " + auctionsToAddLinks.size() + " links");
        return auctionsToAddLinks;
    }

    public void addAuctionsLocally(Collection<String> links) {
        try (final WebClient wc = new WebClient()) {
            wc.getOptions().setJavaScriptEnabled(false);
            wc.getOptions().setCssEnabled(false);

            for (String link : links) {

                String fullLink = url + link;
                log("Get precise info in link: " + fullLink);
                final HtmlPage page = wc.getPage(fullLink);

                //Get tittle
                HtmlDivision tittleDiv = (HtmlDivision) page.getBody().getByXPath("//div[@class='" + tittleElement + "']").get(0);
                HtmlHeading3 htmlHeading3 = domUtil.getByIndex(tittleDiv, 0);

                String tittle = htmlHeading3.getTextContent();
                if (tittle != null) tittle = tittle.trim();

                HtmlDivision endAuctionDiv = (HtmlDivision) page.getBody().getByXPath("//div[@class='" + endAuctionElement + "']").get(0);
                String endAuction = domUtil.getContentByIndexes(endAuctionDiv, 1);

                HtmlDivision detailsDiv = (HtmlDivision) page.getBody().getByXPath("//div[@class='" + detailsElement + "']").get(0);

                String brand = domUtil.getContentByIndexes(detailsDiv, 1, 1, 0, 1);
                String mileage = domUtil.getContentByIndexes(detailsDiv, 1, 1, 1, 1);
                String registerDate = domUtil.getContentByIndexes(detailsDiv, 1, 1, 2, 1);


                String identityFullNumber = domUtil.getContentByIndexes(detailsDiv, 1, 1, 4, 1);
                String identity;
                String[] identities = identityFullNumber.split(" ");
                int identitiesLen = identities.length;

                if (identitiesLen == 1 || identitiesLen == 2) {
                    identity = identities[0];
                } else {
                    throw new IllegalStateException("Fail obtain identity for: " + identityFullNumber);
                }

                log("Found auction: " +
                        "Brand: " + brand +
                        ", mileage: " + mileage +
                        ", register date: " + registerDate +
                        ", end auction: " + endAuction +
                        ", identity number: " + identity);

                String secondSegment = registerDate.substring(registerDate.indexOf(" ") + 1);
                if (secondSegment.indexOf(",") == 1)
                    secondSegment = "0" + secondSegment;

                registerDate = registerDate.substring(0, 3) + " " + secondSegment;
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TEXT_PATTERN, Locale.forLanguageTag("en-EN"));
                LocalDate registerLocalDate = LocalDate.parse(registerDate, dateTimeFormatter);

                dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN);
                LocalDateTime endAuctionLocalDateTime = LocalDateTime.parse(endAuction, dateTimeFormatter);

                CarAuction ca = new CarAuction();
                ca.setIdentity(identity);
                ca.setIdentityFull(identityFullNumber);
                ca.setTittle(tittle);
                ca.setLink(fullLink);
                ca.setEndAuction(endAuctionLocalDateTime);

                Car c = new Car();
                c.setBrand(brand);
                c.setMileage(Long.valueOf(mileage));
                c.setRegisterDate(registerLocalDate);

                auctionService.saveAuction(ca);
                log("Auction with identity " + ca.getIdentity() + " added.");

            }

        } catch (Throwable t) {
            t.printStackTrace();
            log("Error occurred in add auctions locally, with message: " + t.getMessage() + " \n " + ExceptionUtils.getStackTrace(t));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuction(CarAuction carAuction) {
        carAuctionRepository.save(carAuction);
    }

    private void log(String info) {
        String pattern = "At: %s =======> %s";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        LOGGER.info(String.format(pattern, time, info));
    }

}
