google-analytics-plugin
=======================

Cordova Google Analytics Plugin v3 for Android &amp; iOS

Provides Apache Cordova support for Google Analytics v3 (Universal Analytics) using the native sdks for Android &ampl iOS.

Rather than implementing specific methods for tracking screen views and events, this plugin provides the more generic send/set methods.

```js

// API
analytics.setTrackingId(trackingId, successCallback, errorCallback);
analytics.set(name, value, successCallback, errorCallback);
analytics.get(name, successCallback, errorCallback); // value is passed to successCallback
analytics.send(object, successCallback, errorCallback);
analytics.close(successCallback, errorCallback);

// basic example using send function
var Fields = analytics.Fields;
analytics.setTrackingId('UA-XXXXX-X', successCallback, errorCallback);

var params = {
  Fields.HIT_TYPE: 'appview',
  Fields.SCREEN_NAME: 'home'
};

analytics.send(params, successCallback, errorCallback);
```
