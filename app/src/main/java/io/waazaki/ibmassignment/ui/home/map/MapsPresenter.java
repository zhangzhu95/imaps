package io.waazaki.ibmassignment.ui.home.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.CountDownTimer;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import io.waazaki.ibmassignment.objects.Business;
import io.waazaki.ibmassignment.objects.BusinessesResponse;
import io.waazaki.ibmassignment.objects.Coordinates;
import io.waazaki.ibmassignment.utils.CustomMarker;
import io.waazaki.ibmassignment.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.waazaki.ibmassignment.utils.AppConstants.MAX_LOCATION_RETRIES;
import static io.waazaki.ibmassignment.utils.AppConstants.TIME_BETWEEN_LOCATION_RETRIES;
import static io.waazaki.ibmassignment.utils.Utils.LogError;
import static io.waazaki.ibmassignment.utils.Utils.LogInfo;

public class MapsPresenter implements MapsContract.IMapsPresenter {

    private MapsContract.IMapsView view;
    private MapsContract.IMapsInteractor interactor;
    private Context context;

    private List<CustomMarker> customMarkerList;
    private Coordinates currentDestination;
    private int countLocationRetries = 0;

    private FusedLocationProviderClient fusedLocationClient;

    MapsPresenter(Context context , MapsContract.IMapsView view , MapsContract.IMapsInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
        this.context = context;

        //Location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
    }

    @Override
    public void loadBusinesses(double lat , double lon) {
        view.defaultViewsState();
        //Call the WS that returns the list of businesses
        interactor.loadBusinesses(lat , lon).enqueue(new Callback<BusinessesResponse>() {
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
        //currentDestination = new Coordinates(lat , lon);
        currentDestination = new Coordinates(-33.875110, 151.211253);
    }

    /*
    If not able to retieve the current position, retry after a certain duration
     */
    @Override
    public void retryRetrieveCurrentPosition(){
        LogInfo("Retry retrieving current position Again");
        incrementLocationRetries();
        new CountDownTimer(TIME_BETWEEN_LOCATION_RETRIES , TIME_BETWEEN_LOCATION_RETRIES){
            @Override
            public void onTick(long l) {
                //Interval
            }

            @Override
            public void onFinish() {
                if(hasExceededLocationRetries()){
                    LogError("Exceeded the number of retries");
                    view.showPositionMessage();
                }else{
                    retrieveCurrentPosition();
                }
            }
        }.start();
    }

    /**
     * Getting the current position
     */
    @SuppressLint("MissingPermission")
    @Override
    public void retrieveCurrentPosition() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        if (location == null) {
                            LogInfo("Location is null");
                            retryRetrieveCurrentPosition();
                            return;
                        }else{
                            LogInfo("Location is available, reset location retries");
                            resetLocationRetries();
                        }

                        //Set the location as a current destination
                        setCurrentDestination(location.getLatitude() , location.getLongitude());

                        //Navigate to the current destination
                        view.navigateDefaultDestination();

                        //Load businesses of the current destination
                        loadBusinesses(getCurrentCoordinate().getLatitude() , getCurrentCoordinate().getLongitude());
                    }
                });
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
