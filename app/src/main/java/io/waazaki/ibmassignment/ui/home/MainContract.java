package io.waazaki.ibmassignment.ui.home;

public class MainContract {
    interface IMainView{
        void bindViews();
        void setupViews();
        void killProcessGPSNotEnabled();
        void killProcessGPSPermissionDenied();
        void showDefaultFragment();
        void showMessage(int stringId);
        void checkGpsThenShowFragment();
    }

    interface IMainPresenter{
    }
}
