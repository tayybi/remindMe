package com.remindme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationActivitys extends FragmentActivity implements OnMapReadyCallback {

    DataBaseHelper dataBaseHelper;
    private GoogleMap mMap;
    EditText l_title ,l_allcontact,contactsearch;
    Context context=this;
    ArrayAdapter<String> contactAdapter;
    ListView contactListView;
    private Circle mCircle;
    public  double lat;
    public  double lon;
    public int radiousset=300;
    public String dist;
    public String temp="";
    public String temp2="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_activitys);
        dataBaseHelper =new DataBaseHelper(this);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_NETWORK_STATE},10);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        l_allcontact= (EditText) findViewById(R.id.l_allcontact);
        l_title=(EditText)findViewById(R.id.l_title);


        findViewById(R.id.l_contactlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dialogcntc = new AlertDialog.Builder(context).create();
                dialogcntc.setCancelable(true);
                final ArrayList<String> cntctList = new ArrayList<>();

                View cntctview = getLayoutInflater().inflate(R.layout.allitems, null);
                contactListView= (ListView) cntctview.findViewById(R.id.allitems);
                contactsearch= (EditText) cntctview.findViewById(R.id.contact_serch);
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                while (phones.moveToNext())
                {
                    String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    cntctList.add(name+"\n"+phoneNumber);

                }
                phones.close();
                contactAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cntctList);
                contactListView.setAdapter(contactAdapter);
                contactsearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        LocationActivitys.this.contactAdapter.getFilter().filter(cs);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }
                });
                contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        temp = parent.getItemAtPosition(position).toString();
                        temp= temp.replaceAll("[^+0-9]", "");
                        temp2 +=temp+";";
                        l_allcontact.setText(temp2);
                        cntctList.remove(position);
                        contactAdapter.notifyDataSetChanged();
                    }
                });

                dialogcntc.setView(cntctview);
                dialogcntc.show();
            }
        });

        findViewById(R.id.l_searchbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText location_search = (EditText) findViewById(R.id.l_serch);
                boolean flag = true;
                if (location_search.getText().toString().equals("")) {
                    location_search.setError("");
                    location_search.requestFocus();
                    flag = false;
                }
                if (flag == true) {

                    if(isOnline()==true) {
                        try {
                            String location = location_search.getText().toString();
                            List<Address> addressList = null;

                            if (location != null || !location.equals("")) {
                                Geocoder geocoder = new Geocoder(context);
                                try {
                                    addressList = geocoder.getFromLocationName(location, 1);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mMap.clear();
                                Address address = addressList.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLng).title(""));
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Too Slow Internet", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(context, "Internet required for Search", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


            findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (l_title.getText().toString().equals("")) {
                    l_title.setError("Title");
                    l_title.requestFocus();
                    flag = false;
                }
                if (flag == true) {

                dataBaseHelper.insertIntoLocation(""+lat,""+lon,""+dist,l_allcontact.getText().toString(),l_title.getText().toString());
                    Intent gotoService=new Intent(LocationActivitys.this, LoactionServices.class);
                    startService(gotoService);
                    Intent gomain2=new Intent(LocationActivitys.this, MainActivity.class);
                    startActivity(gomain2);
                    LocationActivitys.this.finish();

                }
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gomain1=new Intent(LocationActivitys.this, MainActivity.class);
                startActivity(gomain1);
                LocationActivitys.this.finish();
            }
        });

        setRadious();

//        l_search_gps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String serchuserloc = l_serch.getText().toString();
//                List<Address> addressList = null;
//                if (serchuserloc != null || !serchuserloc.equals("")) {
//                    Geocoder geocoder = new Geocoder(context);
//                    try {
//                        addressList = geocoder.getFromLocationName(serchuserloc, 1);
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mMap.clear();
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//
//                }
//            }
//        });


    }/////oncreat


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("map","onmap");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LatLng lahore = new LatLng(33.518589, -86.810356);
        lat=33.518589;
        lon=-86.810356;
        CircleOptions circleOptions = new CircleOptions().center(lahore).radius(radiousset).fillColor(0x44ff0000).strokeColor(0xffff0000).strokeWidth(5);
        mCircle =mMap.addCircle(circleOptions);
        mMap.addMarker(new MarkerOptions().position(lahore).title("Marker in Lahore"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lahore,15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("map","mapclicked");
                mMap.clear();
                lat=latLng.latitude;
                lon=latLng.longitude;
                Log.i("map",""+lat+"==="+lon);
                CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiousset).fillColor(0x44ff0000).strokeColor(0xffff0000).strokeWidth(5);
                mCircle =mMap.addCircle(circleOptions);
                mMap.addMarker(new MarkerOptions().position(latLng).title(""));
            }
        });


    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void setRadious(){
        temp="";
        Spinner spinner= (Spinner) findViewById(R.id.radious);
        ArrayList<String> rad=new ArrayList<String>();
        rad.add("300 Meter");rad.add("400 Meter");rad.add("500 Meter");rad.add("600 Meter");rad.add("700 Meter");rad.add("800 Meter");rad.add("900 Meter");rad.add("1000 Meter");

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,rad);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp = parent.getItemAtPosition(position).toString();
                radiousset=Integer.parseInt(temp.replaceAll("[^0-9]", ""));
                if(radiousset==300){
                    dist="0.186411";
                }if(radiousset==400){
                    dist="0.248548";
                }if(radiousset==500){
                    dist="0.310686";
                }if(radiousset==600){
                    dist="0.372823";
                }if(radiousset==700){
                    dist="0.43496";
                }if(radiousset==800){
                    dist="0.497097";
                }if(radiousset==900){
                    dist="0.559234";
                }if(radiousset==1000){
                    dist="0.621371";
                }
             }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent gomain1=new Intent(LocationActivitys.this, MainActivity.class);
        startActivity(gomain1);
        LocationActivitys.this.finish();

    }
}    ///////main

