package com.app.akplacepickerexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.akplacepicker.models.AddressData;
import com.app.akplacepicker.utilities.Constants;
import com.app.akplacepicker.utilities.PlacePicker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnPlacePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initListener() {
        mBtnPlacePicker.setOnClickListener(this);
    }

    private void initView() {
        mBtnPlacePicker = findViewById(R.id.btnPlacePicker);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnPlacePicker:
                setMapIntent();
                break;

        }
    }

    private void setMapIntent() {
        Intent intent = new PlacePicker.IntentBuilder()
                .setGoogleMapApiKey("Your API Key")
                .setLatLong(18.520430, 73.856743)
                .setMapZoom(19.0f)
                .setAddressRequired(true)
                .setFabColor(R.color.colorPrimary)
                .setPrimaryTextColor(R.color.black)
                .build(this);
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData =  data.getParcelableExtra(Constants.ADDRESS_INTENT);
                addressData.getAddressList();
                Location mLocation = new Location("");
                mLocation.setLatitude(addressData.getLatitude());
                mLocation.setLongitude(addressData.getLongitude());
                //   getCurrentAddress(mLocation);
            }
        }
    }
}
