package io.waazaki.ibmassignment.network;

import io.waazaki.ibmassignment.objects.BusinessesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YelpService {
    @GET("businesses/search")
    Call<BusinessesResponse> getBusinessesByPoint(@Query("latitude") double latitude , @Query("longitude") double longitude);
}