package pl.jcoding.util;

import pl.jcoding.entity.AdvertTemp;
import pl.jcoding.entity.CarAdvert;

import java.util.List;

public interface AdvertPosition {

    //Get link for page
    String link(Long page);

    //Get count of pages
    Long pages();

    //Positions in list
    List<AdvertTemp> listPosition();

    //Details
    CarAdvert details();

}
