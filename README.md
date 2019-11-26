# AKPlacePicker
Custom place picker using google map

## Download

How to

To get a Git project into your build:

Step 1. Add it in your root build.gradle at the end of repositories:
```Java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Step 2. Add the dependency
```Java
dependencies {
       implementation 'com.github.AkashG58:AKPlacePicker:1.0.0'
}
```

## Hands on
Check the [sample](https://github.com/AkashG58/AKPlacePicker) project for a full working example.

### -Java

```Java
Intent intent = new PlacePicker.IntentBuilder()
                .setGoogleMapApiKey("Your API Key")
                .setLatLong(18.520430, 73.856743)
                .setMapZoom(19.0f)
                .setAddressRequired(true)
                .setPrimaryTextColor(R.color.black)
                .build(this);
startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);


@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == Constants.PLACE_PICKER_REQUEST) {
           if (resultCode == Activity.RESULT_OK && data != null) {
               AddressData addressData =  data.getParcelableExtra(Constants.ADDRESS_INTENT);
              // your code
           }
       }
}
