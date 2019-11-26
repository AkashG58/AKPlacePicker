package com.app.akplacepicker.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.akplacepicker.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CurrentPlaceSelectionBottomSheet extends CoordinatorLayout{

    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout rootView;
    private TextView placeNameTextView;
    private TextView placeAddressTextView;
    private TextView placeCoordinatesTextView;
    private ProgressBar placeProgressBar;
    private LinearLayout mBottomContainer;

    public CurrentPlaceSelectionBottomSheet(Context context) {
        super(context);
        initialize(context);
    }

    public CurrentPlaceSelectionBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CurrentPlaceSelectionBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }


    public boolean isShowing() {
        return this.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN;
    }

    private void initialize(Context context) {
        rootView = (CoordinatorLayout) CoordinatorLayout.inflate(context, R.layout.ak_bottom_sheet_view,this);
        bottomSheetBehavior = BottomSheetBehavior.from(rootView.findViewById(R.id.root_bottom_sheet));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bindViews();
    }

    private void bindViews() {
        placeNameTextView = rootView.findViewById(R.id.text_view_place_name);
        placeAddressTextView = rootView.findViewById(R.id.text_view_place_address);
        placeCoordinatesTextView = rootView.findViewById(R.id.text_view_place_coordinates);
        placeProgressBar = rootView.findViewById(R.id.progress_bar_place);
        mBottomContainer = findViewById(R.id.ll_bottom_container);

    }

    public final void showCoordinatesTextView(boolean show) {
        if (show)
          placeCoordinatesTextView.setVisibility(View.VISIBLE);
        else
            placeCoordinatesTextView.setVisibility(View.GONE);
    }

    public final void setPrimaryTextColor(int color) {
        placeNameTextView.setTextColor(color);
    }

    public final void setSecondaryTextColor(int color) {
        placeAddressTextView.setTextColor(color);
    }

    @SuppressLint({"SetTextI18n"})
    public final void setPlaceDetails(double latitude, double longitude,String shortAddress,String fullAddress) {
        if (latitude == -1.0 || longitude == -1.0) {
            placeNameTextView.setText("");
            placeAddressTextView.setText("");
            mBottomContainer.setVisibility(INVISIBLE);
            placeProgressBar.setVisibility(View.VISIBLE);
            return;
        }
        placeProgressBar.setVisibility(View.INVISIBLE);
        mBottomContainer.setVisibility(VISIBLE);

        if (shortAddress.isEmpty())
            placeNameTextView.setText("Dropped Pin");
        else
            placeNameTextView.setText(shortAddress);

        placeAddressTextView.setText(fullAddress);
        placeCoordinatesTextView.setText(Location.convert(latitude, Location.FORMAT_DEGREES) + ", " + Location.convert(longitude, Location.FORMAT_DEGREES));

        showBottomSheetView();
    }

    public final void showLoadingBottomDetails() {
        if (!isShowing()) {
            toggleBottomSheet();
        }
        placeNameTextView.setText("");
        placeAddressTextView.setText("");
        placeCoordinatesTextView.setText("");
        placeProgressBar.setVisibility(View.VISIBLE);
        mBottomContainer.setVisibility(INVISIBLE);

    }

    public final void dismissPlaceDetails() {
        this.toggleBottomSheet();
    }


    private void showBottomSheetView(){

        final RelativeLayout relativeLayout =  rootView.findViewById(R.id.bottom_sheet_header);

        ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = relativeLayout.getMeasuredHeight();
                bottomSheetBehavior.setPeekHeight(height);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetBehavior.setPeekHeight(rootView.findViewById(R.id.bottom_sheet_header).getHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private  void toggleBottomSheet() {
        bottomSheetBehavior.setPeekHeight(rootView.findViewById(R.id.bottom_sheet_header).getHeight());
        if (isShowing())
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


}
