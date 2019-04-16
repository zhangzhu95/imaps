package io.waazaki.ibmassignment.ui.home.map;

import java.util.List;

import io.waazaki.ibmassignment.objects.Business;
import io.waazaki.ibmassignment.objects.BusinessesResponse;
import io.waazaki.ibmassignment.utils.CustomMarker;
import retrofit2.Call;

class MapsContract {
    interface IMapsView{
        void bindViews();
        void setupViews();
        void defaultViewsState();
        void showProgressBar();
        void hideProgressBar();
        void updateBusinesses(List<Business> businessList);
        void updateMap(List<CustomMarker> customMarkersList);
        void navigateDefaultDestination();
        void scrollOnTop();
        void showEmptyMessage();
        void showFailureMessage();
        void showPositionMessage();
    }

    interface IMapsPresenter{
        void loadBusinesses(double lat , double lon);
        void setCurrentDestination(double lat , double lon);
        boolean hasExceededLocationRetries();
        void resetLocationRetries();
        void incrementLocationRetries();

        void retryRetrieveCurrentPosition();
        void retrieveCurrentPosition();
    }

    interface IMapsInteractor{
        Call<BusinessesResponse> loadBusinesses(double lat , double lon);
    }
}
