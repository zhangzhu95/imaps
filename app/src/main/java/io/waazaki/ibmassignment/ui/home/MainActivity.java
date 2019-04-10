package io.waazaki.ibmassignment.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.widget.Toast;

import io.waazaki.ibmassignment.R;
import io.waazaki.ibmassignment.ui.base.BaseActivity;
import io.waazaki.ibmassignment.ui.home.map.MapsFragment;
import io.waazaki.ibmassignment.utils.AppConstants;
import io.waazaki.ibmassignment.utils.GpsUtils;

import static io.waazaki.ibmassignment.utils.AppConstants.LOCATION_REQUEST_CODE;
import static io.waazaki.ibmassignment.utils.AppConstants.TIME_TO_WAIT_GPS_MILLIS;

public class MainActivity extends BaseActivity implements MainContract.IMainView {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    Toast.makeText(MainActivity.this, R.string.title_map, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_dashboard:
                    Toast.makeText(MainActivity.this, R.string.title_dashboard, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_notifications:
                    Toast.makeText(MainActivity.this, R.string.title_notifications, Toast.LENGTH_SHORT).show();
                    return true;
            default:
                //Nothing to do
                break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupViews();

        //Checking location permission
        //Both ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION permissions are required to get the latest user position
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION} , LOCATION_REQUEST_CODE);
            return;
        }

        checkGpsThenShowFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE  && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Permission Accepted
            //Show the default fragment
            checkGpsThenShowFragment();
        } else {
            killProcessGPSPermissionDenied();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == AppConstants.GPS_REQUEST) {
            //
            Toast.makeText(this, R.string.preparing_gps, Toast.LENGTH_LONG).show();
            Toast.makeText(this, R.string.almost_there, Toast.LENGTH_SHORT).show();
            new CountDownTimer(TIME_TO_WAIT_GPS_MILLIS, TIME_TO_WAIT_GPS_MILLIS){
                @Override
                public void onTick(long l) {
                    //On Tick
                }
                @Override
                public void onFinish() {
                    //Show the default fragment
                    showDefaultFragment();
                }
            }.start();
        }else{
            killProcessGPSNotEnabled();
        }
    }

    /*
    Linking with views
     */
    @Override
    public void bindViews() {
        //Setup the bottom navigation
        BottomNavigationView mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void setupViews() {
        //Setup views
    }

    @Override
    public void killProcessGPSNotEnabled() {
        showMessage(R.string.could_not_start_gps_disabled);
        finish();
    }

    @Override
    public void killProcessGPSPermissionDenied() {
        //Permission Rejected
        //Show the error message
        showMessage(R.string.could_not_start);
        //Exit the activity
        finish();
    }


    /**
     * Checking weither the GPS is enabled or not
     * If the GPS is enabled then redirect to the next Fragment
     */
    @Override
    public void checkGpsThenShowFragment(){
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
            if(!isGPSEnable) {
                killProcessGPSNotEnabled();
            }else{
                showDefaultFragment();
            }
            }
        });
    }

    /**
     * Sow the default fragment
     */
    @Override
    public void showDefaultFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content , new MapsFragment())
                .commit();
    }

    /**
     * @param stringId : The resource id of the string to show
     */
    @Override
    public void showMessage(int stringId) {
        Toast.makeText(this , stringId, Toast.LENGTH_LONG).show();
    }
}