package io.waazaki.ibmassignment.ui.home.map;

import java.util.List;

import io.waazaki.ibmassignment.network.YelpService;
import io.waazaki.ibmassignment.objects.Business;
import io.waazaki.ibmassignment.objects.BusinessesResponse;
import io.waazaki.ibmassignment.objects.Coordinates;
import io.waazaki.ibmassignment.utils.CustomMarker;
import io.waazaki.ibmassignment.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.waazaki.ibmassignment.utils.AppConstants.MAX_LOCATION_RETRIES;

public class MapsPresenter implements MapsContract.IMapsPresenter {

    private MapsContract.IMapsView view;
    private List<CustomMarker> customMarkerList;
    private Coordinates currentDestination;
    private int countLocationRetries = 0;

    MapsPresenter(MapsContract.IMapsView view) {
        this.view = view;
    }

    @Override
    public void loadBusinesses(double lat , double lon) {
        view.defaultViewsState();
        //Call the WS that returns the list of businesses
        Utils.getRetrofit().create(YelpService.class).getBusinessesByPoint(lat , lon).enqueue(new Callback<BusinessesResponse>() {
            @Override
            public void onResponse(Call<BusinessesResponse> call, final Response<BusinessesResponse> response) {
                if(response.body() != null){
                    //Getting the list from the response body
                    List<Business> businessList = response.body().getBusinesses();

                    //Updating the recycler view
                    view.updateBusinesses(businessList);

                    //Add markers to the map
                    customMarkerList = Utils.getMarkerOptionsFromBusinesses(businessList);
                    view.updateMap(customMarkerList);
                }else{
                    view.showEmptyMessage();
                }
            }

            @Override
            public void onFailure(Call<BusinessesResponse> call, Throwable t) {
                view.showFailureMessage();
            }
        });
    }

    @Override
    public void setCurrentDestination(double lat , double lon) {
        currentDestination = new Coordinates(lat , lon);
        //currentDestination = new Coordinates(-33.875110, 151.211253);
    }

    /**
     * @return whether if the retries counter was exceeded or not
     */
    @Override
    public boolean hasExceededLocationRetries() {
        return countLocationRetries >= MAX_LOCATION_RETRIES;
    }

    /**
     * Reset te retries counter to 0
     */
    @Override
    public void resetLocationRetries() {
        this.countLocationRetries = 0;
    }

    /**
     * Increment the location retries counter
     */
    @Override
    public void incrementLocationRetries() {
        this.countLocationRetries++;
    }

    Coordinates getCurrentCoordinate() {
        return currentDestination;
    }

    List<CustomMarker> getCustomMarkerList() {
        return customMarkerList;
    }
}
