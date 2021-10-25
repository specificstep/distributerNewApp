package specificstep.com.GlobalClasses;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener {

  /*  // Get Class Name
    private static String TAG = GPSTracker.class.getName();

    private Context mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = false;

    Location location;
    double latitude;
    double longitude;

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;

    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public GPSTracker() {
    }

    *//**
     * Try to get my current location by GPS or Network Provider
     *//*
    public void getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true;

                Log.d(TAG, "Application use GPS Service");

                *//*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 *//*

                provider_info = LocationManager.GPS_PROVIDER;

            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                this.isGPSTrackingEnabled = true;

                Log.d(TAG, "Application use Network State to get GPS coordinates");

                *//*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 *//*
                provider_info = LocationManager.NETWORK_PROVIDER;

            }

            // Application can use GPS or Network Provider
            if (!provider_info.isEmpty()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(
                        provider_info,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider_info);
                    updateGPSCoordinates();
                }
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        }
    }

    *//**
     * Update GPSTracker latitude and longitude
     *//*
    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    *//**
     * GPSTracker latitude getter and setter
     * @return latitude
     *//*
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    *//**
     * GPSTracker longitude getter and setter
     * @return
     *//*
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    *//**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     *//*
    public boolean getIsGPSTrackingEnabled() {

        return this.isGPSTrackingEnabled;
    }

    *//**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     *//*
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    *//**
     * Function to show settings alert dialog
     *//*
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.gpsAlertTitle);

        //Setting Dialog Message
        alertDialog.setMessage(R.string.gpsAlertMessage);

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.gpsAlertSettings, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    *//**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     *//*
    public List<Address> getGeocoderAddress(Context context) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                *//**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 *//*
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);

                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    *//**
     * Try to get AddressLine
     * @return null or addressLine
     *//*
    public String getAddressLine(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    *//**
     * Try to get Locality
     * @return null or locality
     *//*
    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        }
        else {
            return null;
        }
    }

    *//**
     * Try to get Postal Code
     * @return null or postalCode
     *//*
    public String getPostalCode(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }

    *//**
     * Try to get CountryName
     * @return null or postalCode
     *//*
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
*/



    private final Context mContext;
    private String provider;
    // flag for GPS Status
    boolean isGPSEnabled = false;

    //flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location;// location
    double latitude;//latitude
    double longitude;//longitude

    //The minimum distance to change updates in meters.
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;//10 meters

    //The minimum time to change updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1; // 1 minute

    //Declaring LocationManager
    protected LocationManager locationManager;

    public GPSTracker(Context context){
        this.mContext = context;
        getLocation();
        //onLocationChanged(location);

    }


    @SuppressLint("MissingPermission")
    public Location getLocation(){
        try{
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            //Criteria criteria = new Criteria();
            //provider = locationManager.getBestProvider(criteria, true);
            //Location location = locationManager.getLastKnownLocation(provider);


            // if (location == null) {
            // request for a single update, and try again.
            // Later will request for updates every 10 mins
            //locationManager.requestSingleUpdate(criteria, this, null);
            // location = locationManager
            //    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // }

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            ////getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                //no network provider is enabled
            }else{
                this.canGetLocation = true;
                isGPSEnabled =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                //isGPSEnabled = true;
                //First get location from Network Provider
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    Log.d("Network","Network");
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location !=null){
                            System.out.println("Provider " + provider + " has been selected.");
                            onLocationChanged(location);
                            //latitude = location.getLatitude();
                            //longitude = location.getLongitude();
                        }
                    }

                }
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location){


        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider){
        Toast.makeText(getApplicationContext(), "Disabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getApplicationContext(), "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    /**
     * Function to get latitude
     *
     */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude, Toast.LENGTH_LONG).show();
        }

        //return latitude;
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude(){
        if(location !=null){
            longitude = location.getLongitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLng: " + longitude, Toast.LENGTH_LONG).show();
        }

        //return longitude;
        return longitude;

    }

    /**
     * Function to check if best network provider
     * @return boolean
     */
    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        //Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled.Do you want to go to settings menu?");

        //On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which){
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);

            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }

        });

        //Showing Alert Message
        alertDialog.show();

    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in app
     *
     */

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }


}
