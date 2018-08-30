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
// ...or set multiple tracking ids
analytics.setMultipleTrackingIds(['UA-XXXXX-1', 'UA-XXXXX-2']);

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

To use a specific version of Google's `play-services-analytics` library just specify it with the `ANALYTICS_VERSION` variable.

```
cordova plugin add com.cmackay.plugins.googleanalytics --variable ANALYTICS_VERSION='11.0.1'
```


## API
<a name="module_analytics"></a>

## analytics

* [analytics](#module_analytics)
    * [.Fields](#module_analytics.Fields)
    * [.HitTypes](#module_analytics.HitTypes)
    * [.LogLevel](#module_analytics.LogLevel)
    * [.setTrackingId(trackingId, [success], [error])](#module_analytics.setTrackingId)
    * [.setMultipleTrackingIds(trackingIds, [success], [error])](#module_analytics.setMultipleTrackingIds)
    * [.setDispatchInterval(seconds, [success], [error])](#module_analytics.setDispatchInterval)
    * [.getAppOptOut([success])](#module_analytics.getAppOptOut)
    * [.setAppOptOut([enabled], [success], [error])](#module_analytics.setAppOptOut)
    * [.setLogLevel(logLevel, [success], [error])](#module_analytics.setLogLevel)
    * [.dispatchHits([success], [error])](#module_analytics.dispatchHits)
    * [.get(key, success, [error])](#module_analytics.get)
    * [.set(key, value, [success], [error])](#module_analytics.set)
    * [.send(params, [success], [error])](#module_analytics.send)
    * [.close([success], [error])](#module_analytics.close)
    * [.customDimension(id, [value], [success], [error])](#module_analytics.customDimension)
    * [.customMetric(id, [value], [success], [error])](#module_analytics.customMetric)
    * [.sendEvent(category, action, [label], [value], [success], [error])](#module_analytics.sendEvent)
    * [.sendEventWithParams(category, action, [label], [value], params, [success], [error])](#module_analytics.sendEventWithParams)
    * [.sendAppView(screenName, [success], [error])](#module_analytics.sendAppView)
    * [.sendAppViewWithParams(screenName, params, [success], [error])](#module_analytics.sendAppViewWithParams)
    * [.sendTiming(category, variable, label, time, [success], [error])](#module_analytics.sendTiming)
    * [.sendException(description, [fatal], [success], [error])](#module_analytics.sendException)
    * [.trackUnhandledScriptErrors([opts], [success], [error])](#module_analytics.trackUnhandledScriptErrors)

<a name="module_analytics.Fields"></a>

### analytics.Fields
GA Field Types

**Kind**: static property of <code>[analytics](#module_analytics)</code>
<a name="module_analytics.HitTypes"></a>

### analytics.HitTypes
GA Hit Types

**Kind**: static property of <code>[analytics](#module_analytics)</code>
<a name="module_analytics.LogLevel"></a>

### analytics.LogLevel
Log Levels

**Kind**: static property of <code>[analytics](#module_analytics)</code>
<a name="module_analytics.setTrackingId"></a>

### analytics.setTrackingId(trackingId, [success], [error])
Sets the tracking id

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| trackingId | <code>string</code> | the trackingId |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.setMultipleTrackingIds"></a>

### analytics.setMultipleTrackingIds(trackingIds, [success], [error])
Sets multiple tracking ids.
This will override any tracking id previously set.

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| trackingIds | <code>array</code> | array of trackingId parameters |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.setDispatchInterval"></a>

### analytics.setDispatchInterval(seconds, [success], [error])
Sets the dispatch Interval

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| seconds | <code>number</code> | the interval in seconds |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.getAppOptOut"></a>

### analytics.getAppOptOut([success])
Get app-level opt out flag that will disable Google Analytics

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| [success] | <code>function</code> | the success callback (value is passed to callback) |

<a name="module_analytics.setAppOptOut"></a>

### analytics.setAppOptOut([enabled], [success], [error])
Set app-level opt out flag that will disable Google Analytics

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Default | Description |
| --- | --- | --- | --- |
| [enabled] | <code>boolean</code> | <code>true</code> | true for opt out or false to opt in |
| [success] | <code>function</code> |  | the success callback |
| [error] | <code>function</code> |  | the error callback |

<a name="module_analytics.setLogLevel"></a>

### analytics.setLogLevel(logLevel, [success], [error])
Sets the log level

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| logLevel | <code>number</code> | the log level (refer to LogLevel for values) |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.dispatchHits"></a>

### analytics.dispatchHits([success], [error])
Manually dispatches hits

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.get"></a>

### analytics.get(key, success, [error])
Gets a field value. Returned as argument to success callback.
If multiple trackers are being used, this returns an array of trackerId and
field value pairs, e.g.,
[{ "UA-XXXXX-1" : "field_value1" }, { "UA-XXXXX-2" : "field_value2" }]

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| key | <code>string</code> | the key |
| success | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.set"></a>

### analytics.set(key, value, [success], [error])
Sets a field value

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| key | <code>string</code> | the key |
| value |  | the value |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.send"></a>

### analytics.send(params, [success], [error])
Generates a hit to be sent with the specified params and current field values

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| params | <code>object</code> | the params |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.close"></a>

### analytics.close([success], [error])
Closes the tracker

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.customDimension"></a>

### analytics.customDimension(id, [value], [success], [error])
Sets a custom dimension

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| id | <code>number</code> | the id |
| [value] | <code>string</code> | the value |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.customMetric"></a>

### analytics.customMetric(id, [value], [success], [error])
Sets a custom metric

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| id | <code>number</code> | the id |
| [value] | <code>number</code> | the value |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.sendEvent"></a>

### analytics.sendEvent(category, action, [label], [value], [success], [error])
Sends an event

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Default | Description |
| --- | --- | --- | --- |
| category | <code>string</code> |  | the category |
| action | <code>string</code> |  | the action |
| [label] | <code>string</code> | <code>&quot;&#x27;&#x27;&quot;</code> | the label |
| [value] | <code>number</code> | <code>0</code> | the value |
| [success] | <code>function</code> |  | the success callback |
| [error] | <code>function</code> |  | the error callback |

<a name="module_analytics.sendEventWithParams"></a>

### analytics.sendEventWithParams(category, action, [label], [value], params, [success], [error])
Sends an event with additional params

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Default | Description |
| --- | --- | --- | --- |
| category | <code>string</code> |  | the category |
| action | <code>string</code> |  | the action |
| [label] | <code>string</code> | <code>&quot;&#x27;&#x27;&quot;</code> | the label |
| [value] | <code>number</code> | <code>0</code> | the value |
| params | <code>object</code> |  | the params |
| [success] | <code>function</code> |  | the success callback |
| [error] | <code>function</code> |  | the error callback |

<a name="module_analytics.sendAppView"></a>

### analytics.sendAppView(screenName, [success], [error])
Sends a screen view

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| screenName | <code>string</code> | the screenName |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.sendAppViewWithParams"></a>

### analytics.sendAppViewWithParams(screenName, params, [success], [error])
Sends a screen view with additional params

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| screenName | <code>string</code> | the screenName |
| params | <code>object</code> | the params |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.sendTiming"></a>

### analytics.sendTiming(category, variable, label, time, [success], [error])
Sends a user timing

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| category | <code>string</code> | the category |
| variable | <code>string</code> | the variable |
| label | <code>string</code> | the label |
| time | <code>number</code> | the time |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.sendException"></a>

### analytics.sendException(description, [fatal], [success], [error])
Sends an exception

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| description | <code>string</code> | the exception description |
| [fatal] | <code>boolean</code> | marks the exception as fatal |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

<a name="module_analytics.trackUnhandledScriptErrors"></a>

### analytics.trackUnhandledScriptErrors([opts], [success], [error])
Tracks unhandled scripts errors (window.onerror) and then calls sendException.
This function optionally can be passed an object containing a formmatter function
which takes in all the args to window.onError and should return a String with
the formatted error description to be sent to Google Analytics. Also the object
can provide a fatal property which will be passed to sendException (defaults
to true).

**Kind**: static method of <code>[analytics](#module_analytics)</code>

| Param | Type | Description |
| --- | --- | --- |
| [opts] | <code>object</code> | the options { formatter: Function, fatal: Boolean } |
| [success] | <code>function</code> | the success callback |
| [error] | <code>function</code> | the error callback |

