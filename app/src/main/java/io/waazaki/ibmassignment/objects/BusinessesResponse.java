package io.waazaki.ibmassignment.objects;

import java.util.ArrayList;

public class BusinessesResponse {
    int total;
    ArrayList<Business> businesses;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(ArrayList<Business> businesses) {
        this.businesses = businesses;
    }
}
