google-analytics-plugin
=======================

Cordova Google Analytics v3 Plugin for Android &amp; iOS

Provides Apache Cordova support for Google Analytics v3 (Universal Analytics) using the native sdks for Android &ampl iOS.

Rather than implementing specific methods for tracking screen views and events, this plugin provides the more generic send/set methods.

## Installation
```
cordova plugin add https://github.com/cmackay/google-analytics-plugin.git
```

## Example Usage

```js

// basic example using send function

var Fields    = analytics.Fields,
    HitTypes  = analytics.HitTypes;

analytics.setTrackingId('UA-XXXXX-X', successCallback, errorCallback);

var params = {};
params[Fields.HIT_TYPE] = HitTypes.APP_VIEW;
params[Fields.SCREEN_NAME] = 'home';

analytics.send(params, successCallback, errorCallback);

```

## API

```js

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

// provides object containing field name mappings
analytics.Fields

// provides object containing hit type mappings
analytics.HitTypes

```

Useful Links

[Measurement Protocol Developer Guide](https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide)
