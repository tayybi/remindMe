package com.remindme;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;

/**
 * Created by TAYYAB ALI on 4/23/2017.
 */


public class LoactionServices extends Service {


    DataBaseHelper mydb;
    Context context = this;
    private LocationListener listener;
    private LocationManager locationManager;
    double lat=0;
    String AllContact="";
    MediaPlayer ringtone;
    double lon=0;
    double dist=0;
    String title="",id="";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        mydb=new DataBaseHelper(this);

        listener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location)
                    {
                        boolean flag=false;
                        Cursor cursor=mydb.showLocation();
                        while (cursor.moveToNext()) {
                            title = cursor.getString(cursor.getColumnIndex("L_TITLE"));
                            id = cursor.getString(cursor.getColumnIndex("ID"));
                            lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LAT")));
                            lon = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LON")));
                            dist = Double.parseDouble(cursor.getString(cursor.getColumnIndex("DIST")));
                            AllContact = cursor.getString(cursor.getColumnIndex("L_ALLCONTACT"));

                            if(distance( location.getLatitude(), location.getLongitude(), lat, lon)<dist){
                                ringtone = MediaPlayer.create(context, R.raw.alaram);

                                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                Intent intent1 = new Intent(context, LocationActivitys.class);
                                PendingIntent pendingIntent =PendingIntent.getActivity(context,200,intent1,0);

                                Notification notification = new Notification.Builder(context).setContentTitle(title).setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).build();
                                manager.notify(Integer.parseInt(id),notification);
                                sendSms(AllContact,title);
                                ringtone.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        ringtone.start();
                                    }
                                });
                                ringtone.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        ringtone.stop();
                                    }
                                });

                             flag=true;
                            }
                            if(flag==true){
                                mydb.deleteDataFromLocation(id);
                            }

                        }

                    }


                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    public void onProviderEnabled(String s) {

                    }
                    public void onProviderDisabled(String s) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }

                };

                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                //noinspection MissingPermission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates((LocationListener) listener);
           // ringtone.stop();
        }
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    public void sendSms(String no,String text) {
        String anyno="";
        char check;
        for (int i=0;i<no.length(); i++) {
            check =no.charAt(i);
            if(check==';') {
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(anyno, "+9230", text, null, null);
                anyno="";
            }
            else {
                anyno +=no.charAt(i);
                if(no.length()<=11){
                    SmsManager manager = SmsManager.getDefault();
                    manager.sendTextMessage(no, "+9230", text, null, null);
                    break;
                }
            }

        }
    }

}
