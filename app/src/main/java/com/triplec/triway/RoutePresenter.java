package com.triplec.triway;

import android.content.Context;

import com.google.android.gms.maps.model.PolylineOptions;
import com.triplec.triway.common.TriPlace;
import com.triplec.triway.common.TriPlan;

import java.util.Vector;

class RoutePresenter
        implements RouteContract.Presenter {
    private RouteModel model;
    private RouteContract.View view;
    private Vector<RouteContract.View> viewArray;

    public RoutePresenter() {
        this.model = new RouteModel();
        this.model.setPresenter(this);
    }

    @Override
    public void showRoutes(TriPlan placePlan) {
        for (RouteContract.View  v : viewArray) {
            //if (v != null) {
                v.showRoutes(placePlan);
            //}
        }
//        if (this.view != null) {
//            view.showRoutes(placePlan);
//        }
    }

    @Override
    public String savePlans(String planName) {
        return this.model.savePlans(planName);
    }

    @Override
    public void onError(String message) {
        if(viewArray.size() != 0) {
            for (RouteContract.View v : viewArray) {
                ///if (v != null) {
                v.onError(message);
                //}
            }
        }

//        if (this.view != null) {
//            view.onError(message);
//        }
    }

    @Override
    public void onSavedSuccess(String planName) {
        if (viewArray.size() != 0) {
            for (RouteContract.View v : viewArray) {
                //if (v != null) {
                v.onSavedSuccess(planName);
                //}
            }
        }
//        if (view != null) {
//            view.onSavedSuccess(planName);
//        }
    }

    @Override
    public boolean addPlace(TriPlace newPlace) {
        if (this.model.addPlace(newPlace)){
            showRoutes(this.model.getTriPlan());
            return true;
        }
        return false;
    }

    @Override
    public void setPlanId(String id) {
        this.model.setPlanId(id);
    }

    @Override
    public Context getContext() {
        if(viewArray.size()!=0) {
            for (RouteContract.View v : viewArray) {
                v.getContext();
            }
            return viewArray.elementAt(0).getContext();//TODO
        }
        return null;
        //return this.view.getContext();
    }

    @Override
    public void fetchRoutes(TriPlan placePlan) {
        this.model.fetchRoutes(placePlan);

    }


    @Override
    public void addPolyline(PolylineOptions lineOptions) {
        for (RouteContract.View  v : viewArray) {
            if (v != null) {
                v.addPolyline(lineOptions);
            }
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onViewAttached(RouteContract.View view) {
        if(viewArray == null) {
            viewArray = new Vector<>();
        }
        viewArray.add(view);
//        this.view = view;

        this.model.setGeocoder(view.getContext());

        if (view.getMainPlace() == null ||
                view.getMainPlace().length() == 0)
        {
            this.model.updatePlan(view.getPassedPlan());
            for (RouteContract.View  v : viewArray) {
                v.showRoutes(view.getPassedPlan());
            }
        } else {
            //for (RouteContract.View  v : viewArray)
            this.model.fetchData(view.getMainPlace());
        }
    }

    @Override
    public void onViewDetached() {
        this.viewArray = null;
//        this.view = null;
    }
}
