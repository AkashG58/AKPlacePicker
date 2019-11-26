package com.app.akplacepicker.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.app.akplacepicker.activities.PlacePickerActivity;

public class PlacePicker {

    public static class IntentBuilder {

        private Activity activity;
        private boolean showLatLong;
        private double latitude;
        private double longitude;
        private float zoom = 14.0F;
        private boolean addressRequired = true;
        private boolean hideMarkerShadow;
        private int markerDrawableRes = -1;
        private int markerImageColorRes = -1;
        private int fabBackgroundColorRes = -1;
        private int primaryTextColorRes = -1;
        private int secondaryTextColorRes = -1;
        private String googleMapApiKey;


        public final IntentBuilder setGoogleMapApiKey(String googleMapApiKey) {
            this.googleMapApiKey = googleMapApiKey;
            return this;
        }

        public final IntentBuilder showLatLong(boolean showLatLong) {
            this.showLatLong = showLatLong;
            return this;
        }


        public final IntentBuilder setLatLong(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            return this;
        }


        public final IntentBuilder setMapZoom(float zoom) {
            this.zoom = zoom;
            return this;
        }

        public final IntentBuilder setAddressRequired(boolean addressRequired) {
            this.addressRequired = addressRequired;
            return this;
        }


        public final IntentBuilder hideMarkerShadow(boolean hideMarkerShadow) {
            this.hideMarkerShadow = hideMarkerShadow;
            return this;
        }

        public final IntentBuilder setMarkerDrawable(@DrawableRes int markerDrawableRes) {
            this.markerDrawableRes = markerDrawableRes;
            return this;
        }

        public final IntentBuilder setMarkerImageImageColor(@ColorRes int markerImageColorRes) {
            this.markerImageColorRes = markerImageColorRes;
            return this;
        }

        public final IntentBuilder setFabColor(@ColorRes int fabBackgroundColor) {
            this.fabBackgroundColorRes = fabBackgroundColor;
            return this;
        }


        public final IntentBuilder setPrimaryTextColor(@ColorRes int primaryTextColor) {
            this.primaryTextColorRes = primaryTextColor;
            return this;
        }


        public final IntentBuilder setSecondaryTextColor(@ColorRes int secondaryTextColorRes) {
            this.secondaryTextColorRes = secondaryTextColorRes;
            return this;
        }

        public final Intent build(Activity activity) {
            this.activity = activity;
            Intent intent = new Intent((Context)activity, PlacePickerActivity.class);
            intent.putExtra(Constants.GOOGLE_MAP_API_KEY, this.googleMapApiKey);
            intent.putExtra(Constants.SHOW_LAT_LONG_INTENT , this.showLatLong);
            intent.putExtra(Constants.INITIAL_LATITUDE_INTENT, this.latitude);
            intent.putExtra(Constants.INITIAL_LONGITUDE_INTENT, this.longitude);
            intent.putExtra(Constants.INITIAL_ZOOM_INTENT, this.zoom);
            intent.putExtra(Constants.HIDE_MARKER_SHADOW_INTENT, this.hideMarkerShadow);
            intent.putExtra(Constants.MARKER_DRAWABLE_RES_INTENT, this.markerDrawableRes);
            intent.putExtra(Constants.MARKER_COLOR_RES_INTENT, this.markerImageColorRes);
            intent.putExtra(Constants.FAB_COLOR_RES_INTENT, this.fabBackgroundColorRes);
            intent.putExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, this.primaryTextColorRes);
            intent.putExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, this.secondaryTextColorRes);
            return intent;
        }
    }

}
