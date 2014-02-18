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
var Fields = analytics.Fields;
analytics.setTrackingId('UA-XXXXX-X', successCallback, errorCallback);

var params = {};
params[Fields.HIT_TYPE] = 'appview';
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

```
## Fields

```js

ANDROID_APP_UID: 'AppUID',
ANONYMIZE_IP: '&aip',
APP_ID: '&aid',
APP_INSTALLER_ID: '&aiid',
APP_NAME: '&an',
APP_VERSION: '&av',
CAMPAIGN_CONTENT: '&cc',
CAMPAIGN_ID: '&ci',
CAMPAIGN_KEYWORD: '&ck',
CAMPAIGN_MEDIUM: '&cm',
CAMPAIGN_NAME: '&cn',
CAMPAIGN_SOURCE: '&cs',
CLIENT_ID: '&cid',
CURRENCY_CODE: '&cu',
DESCRIPTION: '&cd',
ENCODING: '&de',
EVENT_ACTION: '&ea',
EVENT_CATEGORY: '&ec',
EVENT_LABEL: '&el',
EVENT_VALUE: '&ev',
EX_DESCRIPTION: '&exd',
EX_FATAL: '&exf',
FLASH_VERSION: '&fl',
HIT_TYPE: '&t',
HOSTNAME: '&dh',
ITEM_CATEGORY: '&iv',
ITEM_NAME: '&in',
ITEM_PRICE: '&ip',
ITEM_QUANTITY: '&iq',
ITEM_SKU: '&ic',
JAVA_ENABLED: '&je',
LANGUAGE: '&ul',
LOCATION: '&dl',
NON_INTERACTION: '&ni',
PAGE: '&dp',
REFERRER: '&dr',
SAMPLE_RATE: '&sf',
SCREEN_COLORS: '&sd',
SCREEN_NAME: '&cd',
SCREEN_RESOLUTION: '&sr',
SESSION_CONTROL: '&sc',
SOCIAL_ACTION: '&sa',
SOCIAL_NETWORK: '&sn',
SOCIAL_TARGET: '&st',
TIMING_CATEGORY: '&utc',
TIMING_LABEL: '&utl',
TIMING_VALUE: '&utt',
TIMING_VAR: '&utv',
TITLE: '&dt',
TRACKING_ID: '&tid',
TRANSACTION_AFFILIATION: '&ta',
TRANSACTION_ID: '&ti',
TRANSACTION_REVENUE: '&tr',
TRANSACTION_SHIPPING: '&ts',
TRANSACTION_TAX: '&tt',
USE_SECURE: 'useSecure',
VIEWPORT_SIZE: '&vp'

```
