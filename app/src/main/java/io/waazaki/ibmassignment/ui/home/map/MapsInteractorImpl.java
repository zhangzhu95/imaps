package io.waazaki.ibmassignment.ui.home.map;

import io.waazaki.ibmassignment.network.YelpService;
import io.waazaki.ibmassignment.objects.BusinessesResponse;
import io.waazaki.ibmassignment.utils.Utils;
import retrofit2.Call;

public class MapsInteractorImpl implements MapsContract.IMapsInteractor {

    private YelpService yelpService;

    public MapsInteractorImpl(){
        this.yelpService = Utils.getRetrofit().create(YelpService.class);
    }

    @Override
    public Call<BusinessesResponse> loadBusinesses(double lat, double lon) {
        return yelpService.getBusinessesByPoint(lat , lon);
    }
}
