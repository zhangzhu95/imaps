package io.waazaki.ibmassignment.ui.home.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import io.waazaki.ibmassignment.R;
import io.waazaki.ibmassignment.objects.Business;
import io.waazaki.ibmassignment.ui.adapters.VerticalAdapter;
import io.waazaki.ibmassignment.ui.base.BaseFragment;
import io.waazaki.ibmassignment.utils.CustomMarker;
import io.waazaki.ibmassignment.utils.Utils;

import static io.waazaki.ibmassignment.utils.AppConstants.TIME_BETWEEN_LOCATION_RETRIES;

public class MapsFragment extends BaseFragment implements MapsContract.IMapsView, OnMapReadyCallback{

    private View view;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private ProgressBar mProgressBar;
    private RecyclerView recyclerViewVertical;
    private VerticalAdapter verticalAdapter;
    private BottomSheetBehavior mBehavior;
    private NestedScrollView nestedScrollView;
    private FusedLocationProviderClient fusedLocationClient;
    private MapsPresenter presenter;

    //Error message
    LinearLayout mLinearLayoutError;
    ImageView mImageViewIcon;
    TextView mTextViewError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_maps, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        presenter = new MapsPresenter(this);

        bindViews();
        setupViews();
        retrieveCurrentPosition();

        return view;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /*
    Linking with views
     */
    @Override
    public void bindViews() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mProgressBar = view.findViewById(R.id.progress_bar);
        recyclerViewVertical = view.findViewById(R.id.recycler_view_vertical);
        mLinearLayoutError = view.findViewById(R.id.linear_layout_error);
        mImageViewIcon = view.findViewById(R.id.image_view_icon);
        mTextViewError = view.findViewById(R.id.text_view_error);

        //Bottom Sheet behavior
        nestedScrollView = view.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(nestedScrollView);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                //On Change state
                if(i == BottomSheetBehavior.STATE_COLLAPSED){
                    scrollOnTop();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                //On Slide event
            }
        });
    }

    /**
     * Setup views
     */
    @Override
    public void setupViews() {
        //Map listener
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        //Preparing the recycler view
        verticalAdapter = new VerticalAdapter(new ArrayList<Business>());
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewVertical.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewVertical.setAdapter(verticalAdapter);
        verticalAdapter.setOnItemClickListener(new VerticalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Business obj) {
                if (mMap == null) {
                    return;
                }

                //Move the map camera to the position of the selected business
                CustomMarker customMarkerToSelect = Utils.findCustomMarkerFromCoordinates(presenter.getCustomMarkerList(), obj.getCoordinates());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(customMarkerToSelect.getMarkerOptions().getPosition()));

                if (customMarkerToSelect.getMarker() != null) {
                    customMarkerToSelect.getMarker().showInfoWindow();
                }

                //Scroll to the top of the list
                scrollOnTop();

                //Collapse the bottom sheet
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    /*
    If not able to retieve the current position, retry after a certain duration
     */
    @Override
    public void retryRetrieveCurrentPosition(){
        LogInfo("Retry retrieving current position Again");
        presenter.incrementLocationRetries();
        new CountDownTimer(TIME_BETWEEN_LOCATION_RETRIES , TIME_BETWEEN_LOCATION_RETRIES){
            @Override
            public void onTick(long l) {
                //Interval
            }

            @Override
            public void onFinish() {
                if(presenter.hasExceededLocationRetries()){
                    LogError("Exceeded the number of retries");
                    showPositionMessage();
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
                            presenter.resetLocationRetries();
                        }

                        //Set the location as a current destination
                        presenter.setCurrentDestination(location.getLatitude() , location.getLongitude());

                        //Navigate to the current destination
                        navigateDefaultDestination();

                        //Load businesses of the current destination
                        presenter.loadBusinesses(presenter.getCurrentCoordinate().getLatitude() , presenter.getCurrentCoordinate().getLongitude());
                    }
                });
    }

    /**
     * Hide the progress bar
     */
    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Show the progress bar
     */
    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the adapter
     * @param businessList : List of businesses
     */
    @Override
    public void updateBusinesses(List<Business> businessList) {
        hideProgressBar();
        if(businessList.isEmpty()) {
            //Show empty message if the list is empty
            showEmptyMessage();
        }else {
            //Hide error message
            mLinearLayoutError.setVisibility(View.GONE);
            //Update recycler view data
            verticalAdapter.setData(businessList);
        }
    }

    /**
     * @param customMarkersList : List containing the map options
     */
    @Override
    public void updateMap(List<CustomMarker> customMarkersList) {

        if(mMap == null)
            return;

        if(customMarkersList.isEmpty())
            return;

        //Clear previous marks from the map
        mMap.clear();

        //Create a LatLngBounds to hold all markerOptions
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (CustomMarker customMarker: customMarkersList) {
            //Add the marker to the map
            customMarker.setMarker(mMap.addMarker(customMarker.getMarkerOptions()));

            //Include the position of the marker in the bounds builder
            builder.include(customMarker.getMarkerOptions().getPosition());
        }

        //Move the camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
    }

    /**
     *
     */
    @Override
    public void navigateDefaultDestination() {
        if(mMap == null)
            return;

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(presenter.getCurrentCoordinate().getLatitude(), presenter.getCurrentCoordinate().getLongitude());
        mMap.addMarker(new MarkerOptions().position(position));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
    }

    @Override
    public void scrollOnTop() {
        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });

    }

    @Override
    public void defaultViewsState() {
        verticalAdapter.setData(new ArrayList<Business>());
        showProgressBar();
    }

    /**
     * Show a message when the web service returned an unexpected result
     */
    @Override
    public void showEmptyMessage() {
        hideProgressBar();
        mLinearLayoutError.setVisibility(View.VISIBLE);
        mImageViewIcon.setImageResource(R.drawable.ic_location_on_black_24dp);
        mTextViewError.setText(R.string.no_places_available);
    }

    /**
     * Show a message when not reaching the web service
     */
    @Override
    public void showFailureMessage() {
        hideProgressBar();
        mLinearLayoutError.setVisibility(View.VISIBLE);
        mImageViewIcon.setImageResource(R.drawable.ic_network_check_black_24dp);
        mTextViewError.setText(R.string.couldnt_retrive_data);
    }

    /**
     * Shpw a message when not getting the users position
     */
    @Override
    public void showPositionMessage() {
        hideProgressBar();
        mLinearLayoutError.setVisibility(View.VISIBLE);
        mImageViewIcon.setImageResource(R.drawable.ic_location_on_black_24dp);
        mTextViewError.setText(R.string.could_not_get_position);
    }
}
