package io.waazaki.ibmassignment.ui.base;

import android.support.v4.app.Fragment;
import android.util.Log;

public class BaseFragment extends Fragment {
    public void LogInfo(String message){
        Log.i(getClass().getName() , message);
    }

    public void LogError(String message){
        Log.e(getClass().getName() , message);
    }
}
