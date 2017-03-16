package net.pi.locator;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String PREFS = "examplePrefs";

    GoogleMap mGoogleMap;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    /*private ToggleButton toggleButton;*/
    private TextView something;
    private FloatingActionButton float1;
    private FloatingActionButton float2;

   // private Button btn_start, btn_stop;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;


    NavigationView navigationView;

    //register broadcast on resume
    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    SharedPreferences example1 = getSharedPreferences(PREFS, 0);
                    String mobilefNo = example1.getString("MobileNo", "Mobile no seems to be wrong");

                    textView.append("\n" +intent.getExtras().get("coordinates") + "Mobile no: " + mobilefNo);

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }
    //destrory broadcast
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //google services availability.
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Google services are ok!!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        } else {
            // No Google Maps Layout
        }

        //cordinates view and button by ID
        something = (TextView) findViewById(R.id.textView1);
        float1 = (FloatingActionButton) findViewById(R.id.float1);
        float2 = (FloatingActionButton) findViewById(R.id.float2);
        textView = (TextView) findViewById(R.id.textView);

        if(!runtime_permissions())
            enable_buttons();

        //Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Drawer end

        //Drawer Navigations
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch (item.getItemId()){
                    case R.id.nav_account:

                        //dialog box for mobile change

                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_l, null);
                        final EditText mMobile = (EditText) mView.findViewById(R.id.etEmail);

                        mBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String mfMobile = mMobile.getText().toString();
                                if(!mMobile.getText().toString().isEmpty()){

                                    SharedPreferences examplePrefs = getSharedPreferences(PREFS, 0);
                                    SharedPreferences.Editor editor = examplePrefs.edit();
                                    editor.putString("MobileNo", mfMobile);
                                    editor.commit();

                                    Toast.makeText(MainActivity.this, "Mobile No. Saved successfully.", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(MainActivity.this, "Please provide a number.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        //dialog box end

                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_settings:

                        //dialog box for mobile change

                        final AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(MainActivity.this);
                        View mView1 = getLayoutInflater().inflate(R.layout.dialog_device, null);
                        final EditText mDevice = (EditText) mView1.findViewById(R.id.etDevice);

                        mBuilder2.setPositiveButton("Save", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        mBuilder2.setNegativeButton("Dismiss", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        mBuilder2.setView(mView1);
                        final AlertDialog dialog2 = mBuilder2.create();
                        dialog2.show();
                        dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!mDevice.getText().toString().isEmpty()){
                                    Toast.makeText(MainActivity.this, "Mobile No. Saved successfully.", Toast.LENGTH_LONG).show();
                                    dialog2.dismiss();
                                }else{
                                    Toast.makeText(MainActivity.this, "Please provide a number.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        //dialog box end

                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        Toast.makeText(MainActivity.this, "Heheee Silly you...", Toast.LENGTH_LONG).show();
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;
                }

                    return true;
            }
        });//Drawer Navigations END

  } //end of on create activity_main

    //implement map fragment
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }

    //check if google services available
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    //Drawer selection option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //drawer menu selection enable
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //menu_type seletor
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //implement map async
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //   goToLocationZoom(4.1776805, 73.5031899, 15); //commented to enable my location

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }

    }

    //camera zoom in on map
    private void goToLocation(double lat, double lng){
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    private void goToLocationZoom(double lat, double lng, float zoom){
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

    //inflate map type menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_type, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //enable buttons for listning.
    private void enable_buttons() {

        float1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                something.setVisibility(View.VISIBLE);
                float1.setVisibility(View.INVISIBLE);
                float2.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);

                Intent i =new Intent(getApplicationContext(),GPS_service.class);
                startService(i);
            }
        });

        float2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                something.setVisibility(View.INVISIBLE);
                float2.setVisibility(View.INVISIBLE);
                float1.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);

                Intent i = new Intent(getApplicationContext(),GPS_service.class);
                stopService(i);

            }
        });

    }


    //runtime permission checks
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    //passing request persmision result set
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }

}
