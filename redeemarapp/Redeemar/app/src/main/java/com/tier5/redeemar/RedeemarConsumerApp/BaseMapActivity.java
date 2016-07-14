/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tier5.redeemar.RedeemarConsumerApp;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;

import java.util.ArrayList;

public abstract class BaseMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    BitmapDescriptor icon ;
    private String LOGTAG = "BaseMapActivity";
    private RecyclerView mRecyclerView;


    private ArrayList<User> mDataSet = null;

    protected int getLayoutId() {
        return R.layout.map;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setUpMap();



    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (mMap != null) {
            return;
        }
        mMap = map;

        /*icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);



        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Hello world")
        .icon(icon));*/




        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        mDataSet = new ArrayList<>();



        // Layout Managers:
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Check if the Android version code is greater than or equal to Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //return resources.getDrawable(id, context.getTheme());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider, getApplicationContext().getTheme())));
        } else {
            //return resources.getDrawable(id);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        }

        /*mDataSet.clear();

        User user1 = new User();
        user1.setCompanyName("Dell");
        user1.setAddress("123 test Street, NY, USA");
        user1.setLogoName("http://www.logospike.com/wp-content/uploads/2015/11/Company_Logos_01.jpg");
        mDataSet.add(user1);

        User user2 = new User();
        user2.setCompanyName("IBM");
        user2.setAddress("456 test Street, NY, USA");
        user2.setLogoName("http://www.indeedjobsguru.com/wp-content/uploads/2016/02/IBM-Company-Logo.jpg");
        mDataSet.add(user2);*/


        // Creating Adapter object
        // Pass to ViewAdapter what activity which is calling this.
        BrandViewAdapter mAdapter = new BrandViewAdapter(getApplicationContext(), mDataSet, "BrandList");


        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        //((BrandViewAdapter) mAdapter).setMode(Attributes.Mode.Single);

        mRecyclerView.setAdapter(mAdapter);


        startDemo();
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


    }

    /**
     * Run the demo-specific code.
     */
    protected abstract void startDemo();

    protected GoogleMap getMap() {
        return mMap;
    }


}
