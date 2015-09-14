google-analytics-plugin
=======================

Provides Apache Cordova/Phonegap support for Google Analytics using the native sdks for Android &amp; iOS.

 * Android Native SDK v4 (using Google Play Services SDK)
 * iOS Native SDK v3

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

The send & set functions provide maximum flexibility and allow you to utilize all of the Google Analytics collection calls. Some helper functions are also provided to support some of the more common analytic functions.

For more information about measurement protocol refer to the following page:

[Measurement Protocol Developer Guide](https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide)

## Installation
```
cordova plugin add com.cmackay.plugins.googleanalytics
```

## API
