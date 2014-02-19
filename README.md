google-analytics-plugin
=======================

Provides Apache Cordova/Phonegap support for Google Analytics v3 (Universal Analytics) using the native sdks for Android &amp; iOS.

This plugin provides support for some of the more specific analytics functions (screen, event & exception tracking, custom metrics & dimensions) and also the more generic set and send functions which can be used to implement all of the Google Analytics functions.

As an example tracking a screen could be implement using either the sendAppView function or the send function:

```js

var analytics = navigator.analytics;

analytics.setTrackingId('UA-XXXXX-X');

analytics.sendAppView('home', successCallback, errorCallback);

// or

var Fields    = analytics.Fields,
    HitTypes  = analytics.HitTypes;

var params = {};
params[Fields.HIT_TYPE]     = HitTypes.APP_VIEW;
params[Fields.SCREEN_NAME]  = 'home';

analytics.send(params, successCallback, errorCallback);

```

The send & set functions provide maximum flexibility and allow you to utilize all of the Google Analytics collection calls. Some helper function are also provided to support some of the more common analytic functions.

For more information about measurement protocol refer to the following page:

[Measurement Protocol Developer Guide](https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide)

## Installation
```
cordova plugin add https://github.com/cmackay/google-analytics-plugin.git
```

## API

```js

// object containing field mapping
analytics.Fields

// object containing hit type mappings
analytics.HitTypes

// all of the function support success and error callback functions

// Sets the tracking id (must be called first)
//
//  trackingId  - String    (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.setTrackingId(trackingId, success, error);

// Sends an app view hit
//
//  screenName  - String    (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytic.sendAppView(screenName, success, error);

// Sends an event hit
//
//  category  - String    (required)
//  action    - String    (required)
//  label     - String    (optional)
//  value     - Number    (optional)
//  success   - Function  (optional)
//  error     - Function  (optional)
analytic.sendEvent(category, action, label, value, success, error);

// Sends an exception hit
//
//  description - String    (required)
//  fatal       - boolean   (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytic.sendException(description, fatal, success, error);

// Sets a custom dimension
//
//  id        - Number    (required)
//  value     - String    (optional)
//  success   - Function  (optional)
//  error     - Function  (optional)
analytic.customDimension(id, value, success, error);

// Sets a custom metric
//
//  id        - Number    (required)
//  value     - Number    (optional)
//  success   - Function  (optional)
//  error     - Function  (optional)
analytic.customMetric(id, value, success, error);

// Sets a field
//
//  name        - String    (required)
//  value       - String    (optional) use null to unset a field
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.set(name, value, success, error);

// Gets a field value. Returned as argument to success callback
//
//  name        - String    (required)
//  success     - Function  (required)
//  error       - Function  (optional)
analytics.get(name, success, error);

// Generates a hit to be sent with the specified params and current field values
//
//  params      - Object    (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.send(params, success, error);

// Closes the the tracker
//
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.close(success, error);

```



