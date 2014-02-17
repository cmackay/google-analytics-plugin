google-analytics-plugin
=======================

Cordova Google Analytics v3 Plugin for Android &amp; iOS

Provides Apache Cordova support for Google Analytics v3 (Universal Analytics) using the native sdks for Android &ampl iOS.

Rather than implementing specific methods for tracking screen views and events, this plugin provides the more generic send/set methods.

## Installation
```
cordova plugin add https://github.com/cmackay/google-analytics-plugin.git
```

## Usage

```js

// API
// =====================

// sets the tracking id (must be called first)
analytics.setTrackingId(trackingId, successCallback, errorCallback);

// sets a name and a value, to unset a value pass a null value
analytics.set(name, value, successCallback, errorCallback);

// gets the current value for the specified name
analytics.get(name, successCallback, errorCallback);

// sends the params object (key names should be from Fields)
analytics.send(params, successCallback, errorCallback);

// closes the current tracker
analytics.close(successCallback, errorCallback);


// EXAMPLE USAGE
// =====================

// basic example using send function
var Fields = analytics.Fields;
analytics.setTrackingId('UA-XXXXX-X', successCallback, errorCallback);

var params = {
  Fields.HIT_TYPE: 'appview',
  Fields.SCREEN_NAME: 'home'
};

analytics.send(params, successCallback, errorCallback);
```
