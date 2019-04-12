package io.waazaki.ibmassignment.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.waazaki.ibmassignment.objects.Business;
import io.waazaki.ibmassignment.objects.Coordinates;
import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    private final double latitude = 10f;
    private final double longitude = 20f;

    @Test
    public void getRetrofit() {
        Retrofit retrofit = Utils.getRetrofit();
        assertEquals("http://api.yelp.com/v3/" , retrofit.baseUrl().toString());
    }

    @Test
    public void getLatLngByCoordinates() {
        Coordinates coordinates = new Coordinates(latitude , longitude);
        LatLng position = Utils.getLatLngByCoordinates(coordinates);
        assertEquals(latitude , position.latitude , 0);
        assertEquals(longitude , position.longitude , 0);
    }

    @Test
    public void findCustomMarkerFromCoordinates() {
        List<CustomMarker> list = new ArrayList<>();

        CustomMarker customMarker = new CustomMarker();
        customMarker.setMarkerOptions(new MarkerOptions().position(new LatLng(latitude ,longitude)));
        list.add(customMarker);

        assertEquals(customMarker , Utils.findCustomMarkerFromCoordinates(list , new Coordinates(latitude , longitude)));
    }

    @Test
    public void getMarkerOptionsFromBusinesses() {
        List<Business> businessList = new ArrayList<>();
        Business business = new Business();
        business.setCoordinates(new Coordinates(latitude , longitude));
        businessList.add(business);

        List<CustomMarker> customMarkerList = Utils.getMarkerOptionsFromBusinesses(businessList);

        assertEquals(1 , customMarkerList.size());
        assertEquals(latitude , customMarkerList.get(0).getMarkerOptions().getPosition().latitude , 0);
        assertEquals(longitude , customMarkerList.get(0).getMarkerOptions().getPosition().longitude , 0);
    }
}