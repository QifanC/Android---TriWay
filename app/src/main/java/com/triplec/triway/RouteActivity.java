package com.triplec.triway;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.triplec.triway.common.TriPlace;
import com.triplec.triway.common.TriPlan;
import com.triplec.triway.mvp.MvpFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v4.app.Fragment;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class RouteActivity extends AppCompatActivity  {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    static RouteContract.Presenter routePresenter = new RoutePresenter();
    int AUTOCOMPLETE_REQUEST_CODE = 1;

    private boolean isSaved = false;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        Bundle bundle = getIntent().getExtras();//TODO
        TriPlan mPlan = (TriPlan) bundle.getSerializable("plan");
        if (mPlan != null)
            actionbar.setTitle(mPlan.getName());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_route, menu);
//        // Associate searchable configuration with the SearchView
//        /*SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.Tabs_menu_add).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), SearchResultActivity.class)));
//        searchView.setQueryHint("Search for another place");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                menu.findItem(R.id.Tabs_menu_add).collapseActionView();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.Tabs_menu_save:
                getDialog();
                return true;
            case R.id.Tabs_menu_add:
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields =
                        Arrays.asList(Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.LAT_LNG,
                                Place.Field.ADDRESS,
                                Place.Field.PHOTO_METADATAS);


                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                Toast.makeText(getApplicationContext(),
                        "Add a new place",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Tabs_menu_edit:
                Toast.makeText(getApplicationContext(),
                        "Editing plan",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Tabs_menu_delete:
                Toast.makeText(getApplicationContext(),
                        "Plan deleted",
                        Toast.LENGTH_SHORT).show();
                actionbar.setTitle("Try your way");
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                addPlace(place);
                Log.i("----- autocomplete", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("----- autocomplete", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {}
        }
    }

    private void addPlace(Place place){
        LatLng latLng = place.getLatLng();
        if (latLng == null) {
            Toast.makeText(RouteActivity.this,
                    place.getName() + " can't be found.", Toast.LENGTH_SHORT).show();
            return;
        }
        TriPlace newPlace = new TriPlace();
        newPlace.setLatitude(latLng.latitude);
        newPlace.setLongitude(latLng.longitude);
        newPlace.setName(place.getName());
        newPlace.setStreet(place.getAddress());
        newPlace.setCity("");
        newPlace.setId(place.getId());
        ListFragment lf = (ListFragment) findFragmentByPosition(1);;
        MapFragment mf = (MapFragment) findFragmentByPosition(0);
//        mf.addPlace(newPlace);//TODO
//        lf.addPlace(newPlace);//TODO
        routePresenter.addPlace(newPlace);


    }

    /**
     * go to save plan and name your plan
     */
    private void getDialog() {
//        MapFragment test = (MapFragment) this.getSupportFragmentManager().findFragmentById(R.id.test_fragment);

        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.route_change_name_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Rename Plan");
        alert.setView(inflator);
        final TextInputEditText plan_rename = inflator.findViewById(R.id.plan_rename_text);
        final TextInputLayout plan_rename_layout = inflator.findViewById(R.id.plan_rename_layout);
        if (actionbar.getTitle().equals("Try your way"))
            plan_rename.setText("My Plan");
        else
            plan_rename.setText(actionbar.getTitle());
        plan_rename.requestFocusFromTouch();
        plan_rename.setSelection(0, plan_rename.getText().length());
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String plan_name=plan_rename.getText().toString();
                //do operations using s1 and s2 here...
                MapFragment mf = (MapFragment)findFragmentByPosition(0);
                ListFragment lf = (ListFragment)findFragmentByPosition(1);
                if (getViewPager().getCurrentItem() == 0) {
                    String planId = mf.savePlans(plan_name);// TODO MVP
                    if (planId.length() !=0 ) {
                        lf.setTriPlanId(planId);
                        actionbar.setTitle(plan_name);
                    }//TODO MVP

                }
                else {
                    String planId = lf.savePlans(plan_name);
                    if (planId.length() !=0 ) {
                        mf.setTriPlanId(planId);
                        actionbar.setTitle(plan_name);
                    }
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }
    private Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = getFragmentPagerAdapter();
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + getViewPager().getId() + ":"
                        + fragmentPagerAdapter.getItemId(position));
    }

    private ViewPager getViewPager() {
        return mViewPager;
    }

    private FragmentPagerAdapter getFragmentPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //Bundle bundle = intent.getBundleExtra("strlist");
            Bundle bundle = getIntent().getExtras();
            MapFragment mf = new MapFragment().newInstance();
            mf.setArguments(bundle);
            ListFragment lf = new ListFragment().newInstance();
            lf.setArguments(bundle);
            switch (position) {
                case 0:
                    return mf;
                case 1:
                    return lf;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return pages;
        }

        final int pages = 2;
    }

    public static class MapFragment
            extends MvpFragment<RouteContract.Presenter>
            implements RouteContract.View{
        private MapView mMapView;
        private GoogleMap mMap;
        List<LatLng> markerPoints;
        MapListAdapter mapListAdapter;
        RecyclerView.LayoutManager layoutManager;
        RecyclerView recyclerView;
        TriPlan.TriPlanBuilder builder ;
        public static MapFragment newInstance() {

            Bundle args = new Bundle();

            MapFragment fragment = new MapFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            View view =
                    inflater.inflate(
                            R.layout.fragment_map,
                            container,
                            false);
            // parsing plan(list)

            mMapView = (MapView) view.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // needed to get the map to display immediately
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //initialize
                    mMap = googleMap;
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                }
            });

            recyclerView = view.findViewById(R.id.map_recycler);
            mapListAdapter = new MapListAdapter(getActivity(),null);
            recyclerView.setAdapter(mapListAdapter);
            layoutManager = new LinearLayoutManager(getActivity());
            ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == SCROLL_STATE_IDLE) {
                        // TODO: This is the item that is focused, update marker
                        int itemSelected = ((LinearLayoutManager) layoutManager)
                                .findFirstVisibleItemPosition();
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLng(markerPoints.get(itemSelected)));
                    }
                }
            });
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
            return view;
        }
        //MAP MAP MAP MAP MAP
        @Override
        public void showRoutes(TriPlan placePlan) {
            markerPoints= new ArrayList<LatLng>();
            List<TriPlace> resultPlaces = placePlan.getPlaceList();
            if (resultPlaces == null || resultPlaces.size() == 0)
                return;
            for (int i=0; i<resultPlaces.size(); i++) {
                markerPoints.add(new LatLng(resultPlaces.get(i).getLatitude(),
                        resultPlaces.get(i).getLongitude()));
            }
            Marker markers[] = new Marker[resultPlaces.size()];

            Log.d("SHOWING PLAN::", String.valueOf(placePlan.getPlaceList().size()));
            builder = new TriPlan.TriPlanBuilder();
            builder.addPlaceList(placePlan.getPlaceList());
            TriPlan plan = builder.buildPlan();
            mapListAdapter = new MapListAdapter(getActivity(), plan.getPlaceList());
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap.clear();
                    // pin all the places to the map
                    for(int i=0; i< markerPoints.size(); i++){
                        MarkerOptions options = new MarkerOptions();
                        options.position(markerPoints.get(i));
                        Marker marker = mMap.addMarker(options);
                        marker.setTag(i);
                        markers[i] = marker;
                    }

                    recyclerView.setAdapter(mapListAdapter);
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            recyclerView.setVisibility(recyclerView.INVISIBLE);
                            mMap.setPadding(0,0,0,0);
                        }
                    });

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            int markerPosition = 0;
                            try {
                                markerPosition = (Integer) marker.getTag();
                            } catch (NullPointerException e) {
                                Toast.makeText(getContext(), "Marker has no Tag", Toast.LENGTH_SHORT).show();
                            }
                            //                Toast.makeText(getContext(), "Marker " + markerPosition + " is selected", Toast.LENGTH_SHORT).show();
                            recyclerView.setVisibility(recyclerView.VISIBLE);
                            mMap.setPadding(0,0,0,(int) (200 * Resources.getSystem().getDisplayMetrics().density));
                            recyclerView.scrollToPosition(markerPosition);
                            return false;
                        }
                    });

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPoints.get(0)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


                    routePresenter.fetchRoutes(placePlan);//TODO

                    if (markerPoints.size() > 0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPoints.get(0)));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                    }
                }
            });
        }

        @Override
        public void onError(String message) {
            Toast.makeText(getActivity(), "Error: "
                    + message, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onSavedSuccess(String planName) {
            Toast.makeText(getActivity(), "Plan saved as "
                    + planName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public String getMainPlace() {
            if (getArguments() != null)
                return getArguments().getString("place");
            else
                return "";
        }

        @Override
        public String savePlans(String plan_name) {
            return routePresenter.savePlans(plan_name);
        }

        @Override
        public boolean addPlace(TriPlace newPlace) {
            Toast.makeText(getContext(), "Add a new place: " + newPlace.getName(), Toast.LENGTH_SHORT).show();
            return routePresenter.addPlace(newPlace);
        }

        @Override
        public TriPlan getPassedPlan() {
            TriPlan mPlan = (TriPlan)getArguments().getSerializable("plan");
            Log.d("received: ", mPlan.getName());
            if (getArguments() != null)
                return (TriPlan) getArguments().getSerializable("plan");
            else
                return null;
        }

        @Override
        public Context getContext() {
            return this.getActivity();
        }

        @Override
        public void addPolyline(PolylineOptions lineOptions) {
            mMap.addPolyline(lineOptions);
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            mapListAdapter.notifyDataSetChanged();
        }

        @Override
        public RouteContract.Presenter getPresenter() {
            return routePresenter;
        }
        public void setTriPlanId(String id) {
            routePresenter.setPlanId(id);
        }
    }



    //////////////////LIST
    public static class ListFragment
            extends MvpFragment<RouteContract.Presenter>
            implements RouteContract.View{
        PlaceListAdapter adapter;
        ListView list;
        List<LatLng> markerPoints;
        TriPlan tp;
        TriPlan.TriPlanBuilder builder ;

        public ListFragment newInstance() {

            Bundle args = new Bundle();

            ListFragment fragment = new ListFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // handle deleting in list fragment //TODO
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // TODO Add your menu entries here
            super.onCreateOptionsMenu(menu, inflater);
        }

        /**
         * For Edit the list of places
         *
         * @param item
         * @return
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                // remove selected place and update adapter
                case R.id.Tabs_menu_delete:
                    //  delete all selected places
                    SparseBooleanArray selected = adapter.getSelectedIds();
                    for(int i = selected.size() - 1; i >= 0; i--){
                        if(selected.valueAt(i)){
                            TriPlace p = adapter.getItem(selected.keyAt(i));
                            adapter.remove(p);
                            builder.removePlace(p);

                        }
                    }
                    //TODO need to update the lastest places here notifyDataSetChanged

                    adapter.removeSelection();
                    routePresenter.showRoutes(builder.buildPlan());
                    notifyDataSetChanged();
                    //TODO
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.fragment_list,
                    container,
                    false);
            // set up list with adapter
            list = (ListView)view.findViewById(R.id.route_list);
            return view;
        }

        @Override
        public void showRoutes(TriPlan placePlan) {

            markerPoints= new ArrayList<LatLng>();
            List<TriPlace> resultPlaces = placePlan.getPlaceList();
            if (resultPlaces == null || resultPlaces.size() == 0)
                return;
            for (int i=0; i<resultPlaces.size(); i++) {
                markerPoints.add(new LatLng(resultPlaces.get(i).getLatitude(),
                        resultPlaces.get(i).getLongitude()));
            }
            routePresenter.fetchRoutes(placePlan); ////TODO MVP where

            builder = new TriPlan.TriPlanBuilder();
            builder.addPlaceList(placePlan.getPlaceList());
            //TriPlan plan = builder.buildPlan();
            adapter = new PlaceListAdapter
                    (getActivity(), R.layout.fragment_list, placePlan.getPlaceList());
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // toggle selection status
                    adapter.toggleSelection(position);
                }
            });
        }

        @Override
        public void onError(String message) {
            Toast.makeText(getActivity(),
                    "Error: " + message,
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onSavedSuccess(String planName) {
            Toast.makeText(getActivity(),
                    "Plan saved as " + planName,
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public String getMainPlace() {
            if (getArguments() != null)
                return getArguments().getString("place");
            else
                return "";
        }

        @Override
        public String savePlans(String plan_name) {
            return routePresenter.savePlans(plan_name);
        }

        @Override
        public boolean addPlace(TriPlace newPlace) {
            Toast.makeText(
                    getContext(),
                    "Add a new place: " + newPlace.getName(),
                    Toast.LENGTH_SHORT).show();
            return routePresenter.addPlace(newPlace);
        }

        @Override
        public TriPlan getPassedPlan() {
            if (getArguments() != null)
                return (TriPlan) getArguments().getSerializable("plan");
            else
                return null;
        }

        @Override
        public Context getContext() {
            return this.getActivity();
        }

        @Override
        public void addPolyline(PolylineOptions lineOptions) {
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public RouteContract.Presenter getPresenter() {
            return routePresenter;
        }

        public void setTriPlanId(String id) {
            routePresenter.setPlanId(id);
        }
    }
}
