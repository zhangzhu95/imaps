package io.waazaki.ibmassignment.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.waazaki.ibmassignment.BuildConfig;
import io.waazaki.ibmassignment.objects.Business;
import io.waazaki.ibmassignment.objects.Coordinates;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    private Utils() {
        //Constructer
    }

    public static Retrofit getRetrofit(){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer " + BuildConfig.YELP_KEY)
                        .build();
                return chain.proceed(request);
            }
        });

        return new Retrofit
                .Builder()
                .baseUrl(BuildConfig.YELP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public static LatLng getLatLngByCoordinates(Coordinates coordinates){
        LatLng result = null;

        if(coordinates != null) {
            result = new LatLng(coordinates.getLatitude() , coordinates.getLongitude());
        }

        return result;
    }

    public static CustomMarker findCustomMarkerFromCoordinates(List<CustomMarker> customMarkerList , Coordinates coordinates){
        CustomMarker result = null;

        if(coordinates != null) {
            for (CustomMarker customMarker : customMarkerList) {
                LatLng position = customMarker.getMarkerOptions().getPosition();
                if(position.latitude == coordinates.getLatitude() && position.longitude == coordinates.getLongitude()){
                    result = customMarker;
                    break;
                }
            }
        }

        return result;
    }

    public static List<CustomMarker> getMarkerOptionsFromBusinesses(List<Business> businessList){
        List<CustomMarker> customMarkerList = new ArrayList<>();
        for (Business business: businessList) {
            if(business.getCoordinates() != null){
                Coordinates coordinates = business.getCoordinates();
                LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
                CustomMarker customMarker = new CustomMarker();
                customMarker.setMarkerOptions(new MarkerOptions().position(latLng).title(business.getName()));
                customMarkerList.add(customMarker);
            }
        }
        return customMarkerList;
    }

    public static void LogInfo(String message){
        Log.i("Zacklog" , message);
    }

    public static void LogError(String message){
        Log.e("Zacklog" , message);
    }
}
