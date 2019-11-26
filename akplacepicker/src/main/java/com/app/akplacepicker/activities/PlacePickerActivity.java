package com.app.akplacepicker.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.app.akplacepicker.R;
import com.app.akplacepicker.models.AddressData;
import com.app.akplacepicker.utilities.Constants;
import com.app.akplacepicker.utilities.CurrentPlaceSelectionBottomSheet;
import com.app.akplacepicker.utilities.PermissionUtility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ImageView markerImage;
    private ImageView mImgBack;
    private ImageView markerShadowImage;
    private CurrentPlaceSelectionBottomSheet bottomSheet;
    private FloatingActionButton fab;
    private double latitude;
    private double longitude;
    private boolean showLatLong = true;
    private float zoom = 12.0F;
    private boolean addressRequired = true;
    private String shortAddress = "";
    private String fullAddress = "";
    private boolean hideMarkerShadow;
    private int markerDrawableRes = -1;
    private int markerColorRes = -1;
    private int fabColorRes = -1;
    private int primaryTextColorRes = -1;
    private int secondaryTextColorRes = -1;
    private List<Address> addresses = new ArrayList<>();
    private ImageView mBtnSearchLocation;
    private CardView mBtnSelectAddress;
    private CardView mBtnSearchBar;
    private String mCountryCode = "";
    private FusedLocationProviderClient fusedLocationClient;
    private ImageView mBtnCurrentLocation;
    private boolean isSearchFromDropdown = false;
    private String googleMapApiKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ak_activity_place_picker);
        getIntentData();
        initViews();
        updateCurrentLocation();
    }

    private void updateCurrentLocation(){
        if (latitude == 0 && longitude == 0) {
            if (PermissionUtility.checkLocationFineAndCoarsePermission(this))
              startLocationUpdates();
        }
    }

    private void initViews() {

        initPlaces();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheet.showCoordinatesTextView(showLatLong);

        TextView mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.search_address);
        markerImage = findViewById(R.id.marker_image_view);
        mImgBack=findViewById(R.id.img_back);
        markerShadowImage = findViewById(R.id.marker_shadow_image_view);
        fab = findViewById(R.id.place_chosen_button);
        mBtnSelectAddress = findViewById(R.id.btnSelectAddress);

        mBtnSearchBar = findViewById(R.id.cvSearchAddress);
        mBtnSearchLocation = findViewById(R.id.img_search);
        mBtnCurrentLocation = findViewById(R.id.ivCurrentLocation);

        mBtnSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addresses != null) {
                    AddressData addressData =new AddressData(latitude, longitude,shortAddress, addresses);
                    Intent returnIntent =new Intent();
                    returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    if (!addressRequired) {
                        AddressData addressData =new AddressData(latitude, longitude,shortAddress, null);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                         Toast.makeText(getApplicationContext(), getString(R.string.no_address), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addresses != null) {
                    AddressData addressData =new AddressData(latitude, longitude, addresses);
                    Intent returnIntent =new Intent();
                    returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    if (!addressRequired) {
                        AddressData addressData =new AddressData(latitude, longitude, null);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_address), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });*/

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompletePicker();
              //  openAutocompletePicker(new LatLng(latitude,longitude),50000);
            }
        });

        mBtnSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompletePicker();
//                openAutocompletePicker(new LatLng(latitude,longitude),50000);
            }
        });

//        mBtnCurrentLocation.setVisibility(View.GONE);
        mBtnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtility.checkLocationFineAndCoarsePermission(PlacePickerActivity.this))
                    startLocationUpdates();
            }
        });
        setIntentCustomization();
    }

    private void initPlaces() {
        try {
            // Initialize Places.
//            Places.initialize(this, getResources().getString(R.string.google_maps_api_key));
            Places.initialize(this, googleMapApiKey);
            // Create a new Places client instance.
            PlacesClient placesClient = Places.createClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getIntentData() {
        if (getIntent() != null) {

            this.googleMapApiKey = this.getIntent().getStringExtra(Constants.GOOGLE_MAP_API_KEY);
            this.latitude = this.getIntent().getDoubleExtra(Constants.INITIAL_LATITUDE_INTENT, 0.0D);
            this.longitude = this.getIntent().getDoubleExtra(Constants.INITIAL_LONGITUDE_INTENT, 0.0D);
            this.showLatLong = this.getIntent().getBooleanExtra(Constants.SHOW_LAT_LONG_INTENT, false);
            this.addressRequired = this.getIntent().getBooleanExtra(Constants.ADDRESS_REQUIRED_INTENT, true);
            this.hideMarkerShadow = this.getIntent().getBooleanExtra(Constants.HIDE_MARKER_SHADOW_INTENT, false);
            this.zoom = this.getIntent().getFloatExtra(Constants.INITIAL_ZOOM_INTENT, 12.0F);
            this.markerDrawableRes = this.getIntent().getIntExtra(Constants.MARKER_DRAWABLE_RES_INTENT, -1);
            this.markerColorRes = this.getIntent().getIntExtra(Constants.MARKER_COLOR_RES_INTENT, -1);
            this.fabColorRes = this.getIntent().getIntExtra(Constants.FAB_COLOR_RES_INTENT, -1);
            this.primaryTextColorRes = this.getIntent().getIntExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, -1);
            this.secondaryTextColorRes = this.getIntent().getIntExtra(Constants.SECONDARY_TEXT_COLOR_RES_INTENT, -1);
        }
    }

    private void setIntentCustomization() {
        if (hideMarkerShadow)
            markerShadowImage.setVisibility(View.GONE);
        else
            markerShadowImage.setVisibility(View.VISIBLE);

        if (markerColorRes != -1) {
            markerImage.setColorFilter(ContextCompat.getColor(this, markerColorRes));
        }
        if (markerDrawableRes != -1) {
            markerImage.setImageDrawable(ContextCompat.getDrawable(this, markerDrawableRes));
        }
        if (fabColorRes != -1) {
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, fabColorRes)));
        }
        if(primaryTextColorRes!=-1) {
            bottomSheet.setPrimaryTextColor(ContextCompat.getColor(this, primaryTextColorRes));
        }
        if(secondaryTextColorRes!=-1) {
            bottomSheet.setSecondaryTextColor(ContextCompat.getColor(this, secondaryTextColorRes));
        }

    }

    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        /*if (PermissionUtility.checkLocationFineAndCoarsePermission(this)) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }*/

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (markerImage.getTranslationY() == 0f) {
                    markerImage.animate()
                            .translationY(-75f)
                            .setInterpolator(new OvershootInterpolator())
                            .setDuration(250)
                            .start();
                    if (bottomSheet.isShowing()) {
                        bottomSheet.dismissPlaceDetails();
                    }
                }
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                markerImage.animate()
                        .translationY(0f)
                        .setInterpolator(new OvershootInterpolator())
                        .setDuration(250)
                        .start();

                if (isSearchFromDropdown) {
                    if (bottomSheet != null)
                        bottomSheet.setPlaceDetails(latitude, longitude, shortAddress, fullAddress);
                    isSearchFromDropdown = false;
                }else {
                    bottomSheet.showLoadingBottomDetails();
                    LatLng latLng = map.getCameraPosition().target;
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    AsyncTask.execute(new Runnable() {
                        public final void run() {
                            getAddressForLocation();
                            runOnUiThread(new Runnable() {
                                public final void run() {
                                    bottomSheet.setPlaceDetails(latitude, longitude, shortAddress, fullAddress);
                                }
                            });
                        }
                    });
                }

            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));

    }

    private void getAddressForLocation() {
        setAddress(latitude, longitude);
    }

    private void setAddress(double latitude, double longitude) {

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
            this.addresses = addresses;
            if (addresses != null && addresses.size() != 0) {
                fullAddress = addresses.get(0).getAddressLine(0);
                shortAddress = generateFinalAddress(fullAddress).trim();
                mCountryCode = addresses.get(0).getCountryCode();
            } else {
                shortAddress = "";
                fullAddress = "";
            }
        } catch (Exception e) {
            shortAddress = "";
            fullAddress = "";
            addresses = null;
            e.printStackTrace();
        }
    }

    private String generateFinalAddress(String address) {
        String strRtr;
        String[] s = address.split(",");
        if (s.length >= 3)
            strRtr = s[1] + "," + s[2];
        else if (s.length == 2)
            strRtr =  s[1];
        else
            strRtr =  s[0];
        return strRtr;

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.AUTOCOMPLETE_PLACE_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data != null) {
                            Place place = Autocomplete.getPlaceFromIntent(data);
                            if (place.getLatLng() != null) {
                             //   bottomSheet.showLoadingBottomDetails();
                                isSearchFromDropdown = true;
                                latitude = place.getLatLng().latitude;
                                longitude = place.getLatLng().longitude;
                                shortAddress = place.getName();
                                fullAddress = place.getAddress();

                                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && addresses.size() != 0) {
                                    fullAddress = addresses.get(0).getAddressLine(0);
                                    shortAddress = getPlaceName(place.getName(),generateFinalAddress(fullAddress).trim());
//                                    shortAddress += " " +generateFinalAddress(fullAddress).trim();
                                    mCountryCode = addresses.get(0).getCountryCode();
                                }

                                if (map != null)
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void openAutocompletePicker() {
        try {
            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID
                    , Place.Field.NAME
                    , Place.Field.ADDRESS
                    , Place.Field.ADDRESS_COMPONENTS
                    , Place.Field.LAT_LNG, Place.Field.RATING
                    , Place.Field.PHOTO_METADATAS, Place.Field.WEBSITE_URI
                    , Place.Field.VIEWPORT, Place.Field.PRICE_LEVEL
                    , Place.Field.TYPES, Place.Field.OPENING_HOURS
                    , Place.Field.PLUS_CODE, Place.Field.USER_RATINGS_TOTAL);
            // Start the autocomplete intent.

            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);

            startActivityForResult(intent, Constants.AUTOCOMPLETE_PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAutocompletePicker(LatLng location, int mDistanceInMeters){
        double latRadian = Math.toRadians(location.latitude);

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.latitude - deltaLat;
        double minLong = location.longitude - deltaLong;
        double maxLat = location.latitude + deltaLat;
        double maxLong = location.longitude + deltaLong;

        Log.d("Min lat long","Min: "+Double.toString(minLat)+","+Double.toString(minLong));
        Log.d("Max lat long","Max: "+Double.toString(maxLat)+","+Double.toString(maxLong));

        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(minLat, minLong),
                new LatLng(maxLat, maxLong));

        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID
                , Place.Field.NAME
                , Place.Field.ADDRESS
                , Place.Field.ADDRESS_COMPONENTS
                , Place.Field.LAT_LNG, Place.Field.RATING
                , Place.Field.PHOTO_METADATAS, Place.Field.WEBSITE_URI
                , Place.Field.VIEWPORT, Place.Field.PRICE_LEVEL
                , Place.Field.TYPES, Place.Field.OPENING_HOURS
                , Place.Field.PLUS_CODE, Place.Field.USER_RATINGS_TOTAL);
        // Start the autocomplete intent.

        Intent intent;

        if (TextUtils.isEmpty(mCountryCode)){
            intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .setLocationRestriction(bounds)
                    .build(this);
        }else {
            intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .setCountry(mCountryCode)
                    .setLocationRestriction(bounds)

                    .build(this);
        }

        startActivityForResult(intent, Constants.AUTOCOMPLETE_PLACE_PICKER_REQUEST);

    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000)*/;
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback(),
                Looper.getMainLooper());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            if (map != null)
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constants.MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();

                } else {
//                    CustomDialogUtil.showAlert(mContext, getString(R.string.permission_popup_title), getString(R.string.location_permission_manually));
                }
                break;
        }
    }

    private String getPlaceName(String shortName,String fullName){

        String returnName = "";

        if (TextUtils.isEmpty(shortName)){
            if (!TextUtils.isEmpty(fullName))
                returnName = fullName;
        }else {
            if (TextUtils.isEmpty(fullName))
                returnName = shortName;
            else
                returnName = shortName + ", " + fullName;

        }
        return returnName;

    }
}
