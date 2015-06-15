package com.corning.helloworld;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mexens.android.navizon.NavizonIndoorsSettings;
import com.mexens.android.navizon.NavizonLocationManager;


public class DisplayMap extends ActionBarActivity implements LocationListener {

    private MapSurfaceView mMap;
    private NavizonLocationManager nlm = null;
    private static final String TAG = "NavizonSDKTest";
    private int updateCount = 0;

    double originX = -77.12428;//-77.124275;//-77.12426; - Lower --> Right, Higher --> Left

    double originY = 42.15836;//42.158349;//42.15829; Lower --> Down, Higher --> Up

    private TextView txtText = null;

    private static final double NORTH_UP_ADJ = 8.0;

    private static final double HEADING_OFFSET = 90.0;


    /**
     * Your Navizon Indoors username
     */
    private final String username = "xtrinsik@gmail.com";				// aka Navizon I.T.S. username
    /**
     * Your Navizon Indoors password
     */
    private final String password = "Jordan99";				// aka Navizon I.T.S. password
    /**
     * Your Navizon Indoors siteID
     */
    private final String siteID = "1448";
    /**
     * Your Navizon Indoors levelIDs. Note: Only required for Navigation in order to determine the levels,
     * which should be prepared for navigation. In Tracking mode the list may be used to limit the
     * set of available levels in order to make it easier for the server to find the correct level.
     * A list of levelIDs separated by spaces as string.
     */
    private final String levelID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        mMap = (MapSurfaceView)findViewById(R.id.MapView);

        NavizonIndoorsSettings s = new NavizonIndoorsSettings();
        // Mandatory username, no default
        s.indoorsUsername = username;

        // Optional password, no default
        s.indoorsPassword = password;

        // Mandatory siteID, no default
        s.indoorsSiteID = siteID;

        // levelIDs, Default "0"
        s.indoorsLevelID = levelID;

        // Optionally disassociate wifi while tracking or navigating (be
        // careful). Default false
        s.indoorsDisassociateWifi = false;

        // Tracking: Optional period of updates in secs. Default 5.0s
        s.indoorsLocationUpdatePeriod = 1.0;

        // Tracking: Optionally enhance accuracy by local movement detection.
        // Default false
        s.indoorsMovementDetectionEnabled = true;

        // Optionally enable some debug output. Default false
        s.indoorsDebugLogEnabled = true;

        // Choose the usage of iBeacons. Check the javadoc and readme for the
        // additions
        // necessary to support iBeacons. Default is
        // NavizonIndoorsIBeaconMode.IBEACONS_IGNORE
        s.indoorsIBeaconMode = NavizonIndoorsSettings.NavizonIndoorsIBeaconMode.IBEACONS_IGNORE;

        // In case you have a custom installation of Navizon I.T.S. you may
        // provide the base URL of your server here.
        // Otherwise dont't touch this property.
        // Default is null, which is replaced by "https://its.navizon.com"
        // internally. Omit trailing slashes please.
        s.indoorsServerURL = null;

        switch (NavizonLocationManager.getIBeaconAvailablility(this)) {
            case NavizonLocationManager.IBEACON_OK:
                Toast.makeText(this, "IBeacons can be used", Toast.LENGTH_LONG).show();
                s.indoorsIBeaconMode = NavizonIndoorsSettings.NavizonIndoorsIBeaconMode.IBEACONS_AS_ADDON;
                break;
            case NavizonLocationManager.IBEACON_BT_DISABLED:
                Toast.makeText(this, "Bluetooth disabled. IBeacons cannot be used", Toast.LENGTH_LONG).show();
                break;
            case NavizonLocationManager.IBEACON_BTLE_NOT_AVAILABLE:
                Toast.makeText(this, "Bluetooth LE not available. IBeacons cannot be used", Toast.LENGTH_LONG).show();
                break;
            case NavizonLocationManager.IBEACON_UNDEFINED_PROBLEM:
                Toast.makeText(this, "Undefined problem with iBeacons. IBeacons cannot be used", Toast.LENGTH_LONG).show();
                break;
            case NavizonLocationManager.IBEACON_BTLE_WIFI_DISRUPTION_POSSIBLE:
                Toast.makeText(this, "Wifi and BT LE may disrupt each other. IBeacons should not be used together with Wifi", Toast.LENGTH_LONG).show();
                break;
        }

        txtText = (TextView) findViewById(R.id.txtText);

        // Get access to the singleton class and initialize
        nlm = NavizonLocationManager.getInstance().init(this, s);

        Log.d(TAG, "Version " + nlm.getVersion());

        nlm.requestLocationUpdates(
                NavizonLocationManager.INDOORS_PROVIDER_NAVIGATION,			// or INDOORS_PROVIDER_TRACKING
                0, 															// dummy for compatibility to Core Location
                0,															// dummy for compatibility to Core Location
                this);

        updateCount = 0;

        mMap.RenderSurface(1.0, 1.0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_map, menu);
        return true;
    }

    public static final double Radius = 6372.8 * 1000.0; // In meters


    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }
/*    public static void main(String[] args) {
        System.out.println(haversine(36.12, -86.67, 33.94, -118.40));
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mMap.RenderSurface(1.0, 1.0);
            return true;
        }
        else if (id == R.id.cell_data) {
            Intent i = new Intent(this, CellDataActivity.class);
            startActivity(i);
            /*double distance = 0.0;
            double distance1 = 0.0;
            double lat, lon;
            double angle, angle1 =0.0;
            double x, y;

            lat = 42.15855;//42.158367;
            lon = -77.12421;//-77.124139;

            //distance1 = distFrom(originY, originX, lat, lon);
            distance = haversine(originY, originX, lat, lon);
            angle = angleFromCoordinate(originY, originX, lat, lon);
            //angle = Math.toDegrees(distance);
            //angle = 45.0;
            //distance = 20.0;

            angle = HEADING_OFFSET - (angle + NORTH_UP_ADJ);
            x = 0.0 + distance * Math.cos(Math.toRadians(angle));
            y = 0.0 + distance * Math.sin(Math.toRadians(angle));
            //mMap.RenderSurface(x, y, 32.50, 26.8);
            mMap.RenderSurface(x, y);

            txtText.setText(String.format("#%d: X: %.2f, Y: %.2f, D: %.2f, A: %.2f",
                    ++updateCount,
                    x,
                    y, distance, angle));*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat, lon, distance, angle, x, y = 0.0;

        Log.d(TAG, "onLocationChanged " + location.toString());
        Bundle b = location.getExtras();

        // Check the javadoc for additional statistics information provided in the extras

        lat = location.getLatitude();
        lon = location.getLongitude();

//        txtText.setText(String.format("#%d: Lat: %.6f, Lng: %.6f, Acc: %.2f\nSrc: %s, Site: %s, Level: %d",
//                ++updateCount,
//                lat,
//                lon,
//                (float)location.getAccuracy(),
//                b.getString(NavizonLocationManager.BUNDLE_INFO_SOURCE), 				// Tracking: either "n3" or "its", Navigation: always "v2"
//                b.getString(NavizonLocationManager.BUNDLE_INFO_SITE_ID),
//                b.getInt(NavizonLocationManager.BUNDLE_INFO_LEVEL_ID)));

        /*distance = distFrom(originY, originX, lat, lon);
        angle = angleFromCoordinate(originY, originX, lat, lon) + 90.0;

        x = 0.0 + distance * Math.cos(Math.toRadians(angle));
        y = 0.0 + distance * Math.sin(Math.toRadians(angle));
        mMap.RenderSurface(x, y, 28.4, 39.7);

        txtText.setText(String.format("#%d: X: %.6f, Y: %.6f",
                ++updateCount,
                x,
                y));*/

        distance = haversine(originY, originX, lat, lon);
        angle = angleFromCoordinate(originY, originX, lat, lon);
        //angle = Math.toDegrees(distance);
        //angle = 45.0;
        //distance = 20.0;

        angle = HEADING_OFFSET - (angle + NORTH_UP_ADJ);
        x = 0.0 + distance * Math.cos(Math.toRadians(angle));
        y = 0.0 + distance * Math.sin(Math.toRadians(angle));
        //mMap.RenderSurface(x, y, 32.50, 26.8);
        mMap.RenderSurface(x, y);

        txtText.setText(String.format("#%d: X: %.2f, Y: %.2f, D: %.2f, A: %.2f",
                ++updateCount,
                x,
                y, distance, angle));

        //txtDistance.setText(String.format("Distance: %.2f, Angle: %.1f", distance, angle));

    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {

        double dLon = (long2 - long1);

        double x = Math.sin(dLon) * Math.cos(lat2);
        double y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(x, y);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + status + " Extras: " + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (nlm != null) {
            // Optional call
            nlm.pauseLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nlm != null) {
            // Optional call
            nlm.resumeLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nlm != null) {
            nlm.removeUpdates(this);
            nlm = null;
        }
    }
}
