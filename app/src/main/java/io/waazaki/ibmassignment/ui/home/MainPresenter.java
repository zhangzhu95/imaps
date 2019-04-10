package io.waazaki.ibmassignment.ui.home;

class MainPresenter {

    MainContract.IMainView view;

    MainPresenter(MainContract.IMainView view) {
        this.view = view;
    }
}
