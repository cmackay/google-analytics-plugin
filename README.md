google-analytics-plugin
=======================

Provides Apache Cordova/Phonegap support for Google Analytics v3 (Universal Analytics) using the native sdks for Android &amp; iOS.

This plugin provides support for some of the more specific analytics functions (screen, event & exception tracking, custom metrics & dimensions) and also the more generic set and send functions which can be used to implement all of the Google Analytics collection features.

As an example tracking a screen could be implemented using either the sendAppView function or the send function:

```js

var analytics = navigator.analytics;

// set the tracking id
analytics.setTrackingId('UA-XXXXX-X');

analytics.sendAppView('home', successCallback, errorCallback);

// or the same could be done using the send function

var Fields    = analytics.Fields,
    HitTypes  = analytics.HitTypes,
    LogLevel  = analytics.LogLevel,
    params    = {};

params[Fields.HIT_TYPE]     = HitTypes.APP_VIEW;
params[Fields.SCREEN_NAME]  = 'home';

analytics.setLogLevel(LogLevel.INFO);

analytics.send(params, successCallback, errorCallback);

```

The send & set functions provide maximum flexibility and allow you to utilize all of the Google Analytics collection calls. Some helper function are also provided to support some of the more common analytic functions.

For more information about measurement protocol refer to the following page:

[Measurement Protocol Developer Guide](https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide)

## Installation
```
cordova plugin add com.cmackay.plugins.googleanalytics
```

## API

```js

// object containing field mapping
analytics.Fields

// object containing hit type mappings
analytics.HitTypes

// object containing log level constants
analytics.LogLevel  (VERBOSE, INFO, WARNING, ERROR)

// all of the function support success and error callback functions

// Sets the tracking id (must be called first)
//
//  trackingId  - String    (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.setTrackingId(trackingId, success, error);

// Sets the log level
//
//  logLevel    - Number    (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.setLogLevel(logLevel, success, error);

// Sets the log level
//
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.setIDFAEnabled(success, error);

// Turns on IDFA collection to get demographic data in GA
//
//  screenName  - String    (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.sendAppView(screenName, success, error);

// also supports the ability to send additional paramaters in the request
// The params argument is an object which can contain additional key and value
// parameters which will be sent as part of the analytics request
analytics.sendAppViewWithParams(screenName, params, success, error);

// Sends an event hit
//
//  category  - String    (required)
//  action    - String    (required)
//  label     - String    (optional, defaults to '')
//  value     - Number    (optional, defaults to 0)
//  success   - Function  (optional)
//  error     - Function  (optional)
analytics.sendEvent(category, action, label, value, success, error);

// also supports the ability to send additional paramaters in the request
// The params argument is an object which can contain additional key and value
// parameters which will be sent as part of the analytics request
analytics.sendEventWithParams(category, action, label, value, params, success, error);

// Sends an exception hit
//
//  description - String    (required)
//  fatal       - boolean   (required)
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.sendException(description, fatal, success, error);

// Tracks unhandled scripts errors (window.onerror) and then calls sendException.
// This function optionally can be passed an object containing a formmatter function
// which takes in all the args to window.onError and should return a String with
// the formatted error description to be sent to Google Analytics. Also the object
// can provide a fatal property which will be passed to sendException (defaults
// to true).
//
//  opts        - Object    (optional) {formatter: Function, fatal: Boolean}
//  success     - Function  (optional)
//  error       - Function  (optional)
analytics.trackUnhandledScriptErrors(opts, success, error);

// Sets a custom dimension
//
//  id        - Number    (required)
//  value     - String    (optional)
//  success   - Function  (optional)
//  error     - Function  (optional)
analytics.customDimension(id, value, success, error);

// Sets a custom metric
//
//  id        - Number    (required)
//  value     - Number    (optional)
//  success   - Function  (optional)
//  error     - Function  (optional)
analytics.customMetric(id, value, success, error);

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
