package com.remindme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.AppBarLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context context=this;
    ListView listitemForTextnote,listitemForLocation;
    FloatingActionMenu fab_main;
    FloatingActionButton fab_location,fab_text;
    ArrayAdapter<String> adapterText,adapterAlaram,adapterLocation;
    DataBaseHelper dataBaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
          setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
           setSupportActionBar(toolbar);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},10);

        dataBaseHelper= new DataBaseHelper(this);
        listitemForLocation=(ListView)findViewById(R.id.listitem);
        fab_text= (FloatingActionButton) findViewById(R.id.fab_text);
        fab_main = (FloatingActionMenu) findViewById(R.id.fab_main);
        fab_location= (FloatingActionButton) findViewById(R.id.fab_location);




        fab_location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent goLocation = new Intent(MainActivity.this, LocationActivitys.class);
                            startActivity(goLocation);
                            MainActivity.this.finish();

                        }
                    });

        fab_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            fabText();
                        }
                    });


        updateall();
        deletefromall();
//        updateLocationData();
//        updateAlaramData();
//        updateTextnote();
//        deletefromLocation();
//        deletefromText();
//        deletefromAlaram();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }//////oncreat

    //importing database
    private void importDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "/data/com.example.tayyabali.remindmefinel1/databases/RemindMe.db";
                String backupDBPath  = "/remindBackup/RemindMe.db";
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), "Data Imported",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            //Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();

        }
    }
    //exporting database
    private void exportDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/com.example.tayyabali.remindmefinel1/databases/RemindMe.db";
                String backupDBPath = "/remindBackup/RemindMe.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), "Data Exported",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            //Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();

        }

    }
        //////
    public void updateall(){

        ArrayList<String> arrayListl=new ArrayList<>();
        Cursor cursor = dataBaseHelper.showLocation();
        String msg = "";
        while (cursor.moveToNext()) {
            String titlee = cursor.getString(cursor.getColumnIndex("L_TITLE"));
            String lat = cursor.getString(cursor.getColumnIndex("LAT"));
            String lon = cursor.getString(cursor.getColumnIndex("LON"));
           // msg =titlee+"\n\n"+lat+" , "+lon;
            msg =titlee+"\n\n";
            arrayListl.add(msg);
        }
        cursor = dataBaseHelper.showText();
        while (cursor.moveToNext()) {
            String titlee = cursor.getString(cursor.getColumnIndex("TTITLE"));
            String descr = cursor.getString(cursor.getColumnIndex("TDESC"));
            msg = titlee + "\n\n"+descr;
            arrayListl.add(msg);
        }
        adapterLocation=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, arrayListl);
        listitemForLocation.setAdapter(adapterLocation);
        deletefromall();


    }

    public  void deletefromall(){


        listitemForLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder bb=  new  AlertDialog.Builder(context);

                final String wName=parent.getItemAtPosition(position).toString();
                final String[] b=wName.split("\n");
                bb.setMessage("Parmanent Delete");
                bb.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            /////////location
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String n1 = "";
                                boolean check=true;
                                Cursor cursor1;
                                if(check==true) {
                                     n1 = "";
                                     cursor1 = dataBaseHelper.showLocation();
                                    while (cursor1.moveToNext()) {
                                        if (b[0].equals(cursor1.getString(cursor1.getColumnIndex("L_TITLE")))) {
                                            n1 = cursor1.getString(cursor1.getColumnIndex("ID"));
                                            check=false;
                                        }
                                    }
                                    dataBaseHelper.deleteDataFromLocation(n1);

                                }
                                //////////text

                                if (check==true) {
                                    n1 = "";
                                    cursor1 = dataBaseHelper.showText();
                                    while (cursor1.moveToNext()) {
                                        if (b[0].equals(cursor1.getString(cursor1.getColumnIndex("TTITLE")))) {
                                            n1 = cursor1.getString(cursor1.getColumnIndex("ID"));
                                            check=false;
                                        }
                                    }
                                    dataBaseHelper.deleteDataFromText(n1);

                                }
                                if (check==false){
                                    updateall();
                                }
                            }

                        }
                );
                bb.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );
                bb.show();
            }
        });

    }

    public  void fabText(){
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);
        final View view = getLayoutInflater().inflate(R.layout.textnote,null);
        final EditText title= (EditText) view.findViewById(R.id.t_title);
        final EditText des= (EditText) view.findViewById(R.id.t_description);
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=true;
                if(des.getText().toString().equals("")){
                    des.setError("Descreption");
                    des.requestFocus();
                    flag=false;
                }
                if(title.getText().toString().equals("")){
                    title.setError("Title");
                    title.requestFocus();
                    flag=false;
                }
                if(flag==true){
                    dataBaseHelper.insertIntoText(title.getText().toString(),des.getText().toString());
                    dialog.cancel();
                    updateall();
                }
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setView(view);
        dialog.show();
    }





    public void feedBackOfUser(){
        final AlertDialog dialog11=new AlertDialog.Builder(context).create();
        final View v1=getLayoutInflater().inflate(R.layout.feedback,null);
        v1.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText subject= (EditText) v1.findViewById(R.id.subject);
                EditText message= (EditText) v1.findViewById(R.id.message);
                Intent emailIntent=new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"tayyabali.tayybi@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject.getText().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT,message.getText().toString());
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent,"Choose an Email Client!"));


            }
        });
        v1.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog11.cancel();
            }
        });
        dialog11.setView(v1);
        dialog11.show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("RemindMe Alert!").setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    MainActivity.super.onBackPressed();
                                    exportDB();
                                    Toast.makeText(context,"Your BackUp seved",Toast.LENGTH_LONG).show();
                                }
                            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).setCancelable(false).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            importDB();
            updateall();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        if (id == R.id.nav_location) {

                    Intent goLocation= new Intent(MainActivity.this, LocationActivitys.class);
                    startActivity(goLocation);
                    MainActivity.this.finish();
        }

        else if (id == R.id.nav_textnote) {
            fabText();
        }

        else if (id == R.id.nav_feedback) {

            feedBackOfUser();
        }

        else if (id == R.id.nav_syncbackup) {


            File direct = new File(Environment.getExternalStorageDirectory(),"remindBackup");

            if(!direct.exists()) {
                if (direct.mkdir()) {
                    Toast.makeText(context, "BackUp Created", Toast.LENGTH_SHORT).show();
                }

            }
            exportDB();
            updateall();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}////////main


