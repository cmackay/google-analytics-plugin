
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 *: 'License',); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 *: 'AS IS', BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var argscheck = require('cordova/argscheck'),
    utils     = require('cordova/utils'),
    exec      = require('cordova/exec'),
    platform  = require('cordova/platform');

var Fields = {
  ANDROID_APP_UID:          'AppUID',
  ANONYMIZE_IP:             '&aip',
  APP_ID:                   '&aid',
  APP_INSTALLER_ID:         '&aiid',
  APP_NAME:                 '&an',
  APP_VERSION:              '&av',
  CAMPAIGN_CONTENT:         '&cc',
  CAMPAIGN_ID:              '&ci',
  CAMPAIGN_KEYWORD:         '&ck',
  CAMPAIGN_MEDIUM:          '&cm',
  CAMPAIGN_NAME:            '&cn',
  CAMPAIGN_SOURCE:          '&cs',
  CLIENT_ID:                '&cid',
  CURRENCY_CODE:            '&cu',
  DESCRIPTION:              '&cd',
  ENCODING:                 '&de',
  EVENT_ACTION:             '&ea',
  EVENT_CATEGORY:           '&ec',
  EVENT_LABEL:              '&el',
  EVENT_VALUE:              '&ev',
  EX_DESCRIPTION:           '&exd',
  EX_FATAL:                 '&exf',
  FLASH_VERSION:            '&fl',
  HIT_TYPE:                 '&t',
  HOSTNAME:                 '&dh',
  ITEM_CATEGORY:            '&iv',
  ITEM_NAME:                '&in',
  ITEM_PRICE:               '&ip',
  ITEM_QUANTITY:            '&iq',
  ITEM_SKU:                 '&ic',
  JAVA_ENABLED:             '&je',
  LANGUAGE:                 '&ul',
  LOCATION:                 '&dl',
  NON_INTERACTION:          '&ni',
  PAGE:                     '&dp',
  REFERRER:                 '&dr',
  SAMPLE_RATE:              '&sf',
  SCREEN_COLORS:            '&sd',
  SCREEN_NAME:              '&cd',
  SCREEN_RESOLUTION:        '&sr',
  SESSION_CONTROL:          '&sc',
  SOCIAL_ACTION:            '&sa',
  SOCIAL_NETWORK:           '&sn',
  SOCIAL_TARGET:            '&st',
  TIMING_CATEGORY:          '&utc',
  TIMING_LABEL:             '&utl',
  TIMING_VALUE:             '&utt',
  TIMING_VAR:               '&utv',
  TITLE:                    '&dt',
  TRACKING_ID:              '&tid',
  TRANSACTION_AFFILIATION:  '&ta',
  TRANSACTION_ID:           '&ti',
  TRANSACTION_REVENUE:      '&tr',
  TRANSACTION_SHIPPING:     '&ts',
  TRANSACTION_TAX:          '&tt',
  USE_SECURE:               'useSecure',
  USER_ID:                  '&uid',
  VIEWPORT_SIZE:            '&vp'
};

var HitTypes = {
  APP_VIEW:     'appview',
  EVENT:        'event',
  EXCEPTION:    'exception',
  ITEM:         'item',
  SOCIAL:       'social',
  TIMING:       'timing',
  TRANSACTION:  'transaction'
};

var LogLevel = {
  VERBOSE: 0,
  INFO:    1,
  WARNING: 2,
  ERROR:   3
};

var logLevelCount = 0, key;
for (key in LogLevel) {
  if (LogLevel.hasOwnProperty(key)) {
    logLevelCount++;
  }
}

/**
 * @module
 */
module.exports = {

  /**
   * GA Field Types
   */
  Fields: Fields,

  /**
   * GA Hit Types
   */
  HitTypes: HitTypes,

  /**
   * Log Levels
   */
  LogLevel: LogLevel,

  /**
   * Sets the tracking id
   *
   * @param {string} trackingId - the trackingId
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  setTrackingId: function (trackingId, success, error) {
    argscheck.checkArgs('sFF', 'analytics.setTrackingId', arguments);
    exec(success, error, 'GoogleAnalytics', 'setTrackingId', [trackingId]);
  },

  /**
   * Sets multiple tracking ids.
   * This will override any tracking id previously set.
   *
   * @param {array} trackingIds - array of trackingId parameters
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  setMultipleTrackingIds: function (trackingIds, success, error) {
    argscheck.checkArgs('aFF', 'analytics.setTrackingId', arguments);
    exec(success, error, 'GoogleAnalytics', 'setTrackingId', trackingIds);
  },

  /**
   * Sets the dispatch Interval
   *
   * @param {number} seconds - the interval in seconds
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  setDispatchInterval: function (seconds, success, error) {
    argscheck.checkArgs('nFF', 'analytics.setDispatchInterval', arguments);
    exec(success, error, 'GoogleAnalytics', 'setDispatchInterval', [seconds]);
  },

  /**
   * Get app-level opt out flag that will disable Google Analytics
   *
   * @param {function} [success] - the success callback (value is passed to callback)
   */
  getAppOptOut: function (success) {
    argscheck.checkArgs('F', 'analytics.getAppOptOut', arguments);
    exec(success, null, 'GoogleAnalytics', 'getAppOptOut', []);
  },

  /**
   * Set app-level opt out flag that will disable Google Analytics
   *
   * @param {boolean} [enabled=true] - true for opt out or false to opt in
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  setAppOptOut: function (enabled, success, error) {
    argscheck.checkArgs('*FF', 'analytics.setAppOptOut', arguments);
    exec(success, error, 'GoogleAnalytics', 'setAppOptOut', [enabled]);
  },

  /**
   * Sets the log level
   *
   * @param {number} logLevel - the log level (refer to LogLevel for values)
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  setLogLevel: function (logLevel, success, error) {
    argscheck.checkArgs('nFF', 'analytics.setLogLevel', arguments);
    if (platform.id === 'ios') {
      // the log levels for android are 0,1,2,3 and for ios are 4,3,2,1
      logLevel = logLevelCount - logLevel;
    }
    exec(success, error, 'GoogleAnalytics', 'setLogLevel', [logLevel]);
  },

  enableAdvertisingIdCollection: function (success, error) {
    argscheck.checkArgs('FF', 'analytics.enableAdvertisingIdCollection', arguments);
    exec(success, error, 'GoogleAnalytics', 'setIDFAEnabled', []);
  },

  /**
   * Manually dispatches hits
   *
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  dispatchHits: function (success, error) {
    argscheck.checkArgs('FF', 'analytics.dispatchHits', arguments);
    exec(success, error, 'GoogleAnalytics', 'dispatchHits', []);
  },

  /**
   * Gets a field value. Returned as argument to success callback.
   * If multiple trackers are being used, this returns an array of trackerId and
   * field value pairs, e.g.,
   * [{ "UA-XXXXX-1" : "field_value1" }, { "UA-XXXXX-2" : "field_value2" }]
   *
   * @param {string} key - the key
   * @param {function} success - the success callback
   * @param {function} [error] - the error callback
   */
  get: function (key, success, error) {
    argscheck.checkArgs('sfF', 'analytics.get', arguments);
    exec(success, error, 'GoogleAnalytics', 'get', [key]);
  },

  /**
   * Sets a field value
   *
   * @param {string} key - the key
   * @param value - the value
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  set: function (key, value, success, error) {
    argscheck.checkArgs('s*FF', 'analytics.set', arguments);
    exec(success, error, 'GoogleAnalytics', 'set', [key, value]);
  },

  /**
   * Generates a hit to be sent with the specified params and current field values
   *
   * @param {object} params - the params
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  send: function (params, success, error) {
    argscheck.checkArgs('oFF', 'analytics.send', arguments);
    exec(success, error, 'GoogleAnalytics', 'send', [params]);
  },

  /**
   * Closes the tracker
   *
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  close: function (success, error) {
    argscheck.checkArgs('FF', 'analytics.close', arguments);
    exec(success, error, 'GoogleAnalytics', 'close', []);
  },

  /**
   * Sets a custom dimension
   *
   * @param {number} id - the id
   * @param {string} [value] - the value
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  customDimension: function (id, value, success, error) {
    argscheck.checkArgs('n*FF', 'analytics.customDimension', arguments);
    this.set('&cd' + id, value, success, error);
  },

  /**
   * Sets a custom metric
   *
   * @param {number} id - the id
   * @param {number} [value] - the value
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  customMetric: function (id, value, success, error) {
    argscheck.checkArgs('n*FF', 'analytics.customMetric', arguments);
    this.set('&cm' + id, value, success, error);
  },

  /**
   * Sends an event
   *
   * @param {string} category - the category
   * @param {string} action - the action
   * @param {string} [label=''] - the label
   * @param {number} [value=0] - the value
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  sendEvent: function (category, action, label, value, success, error) {
    this.sendEventWithParams(category, action, label, value, {}, success, error);
  },

  /**
   * Sends an event with additional params
   *
   * @param {string} category - the category
   * @param {string} action - the action
   * @param {string} [label=''] - the label
   * @param {number} [value=0] - the value
   * @param {object} params - the params
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  sendEventWithParams: function (category, action, label, value, params, success, error) {
    argscheck.checkArgs('ssSNoFF', 'analytics.sendEvent', arguments);
    if (params === undefined || params === null) {
      params = {};
    }
    params[Fields.HIT_TYPE]       = HitTypes.EVENT;
    params[Fields.EVENT_CATEGORY] = category;
    params[Fields.EVENT_ACTION]   = action;
    params[Fields.EVENT_LABEL]    = label || '';
    params[Fields.EVENT_VALUE]    = value || 0;
    this.send(params, success, error);
  },

  /**
   * Sends a screen view
   *
   * @param {string} screenName - the screenName
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  sendAppView: function (screenName, success, error) {
    this.sendAppViewWithParams(screenName, {}, success, error);
  },

/**
   * Sends a screen view with additional params
   *
   * @param {string} screenName - the screenName
   * @param {object} params - the params
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  sendAppViewWithParams: function (screenName, params, success, error) {
    argscheck.checkArgs('soFF', 'analytics.sendAppView', arguments);
    if (params === undefined || params === null) {
      params = {};
    }
    params[Fields.HIT_TYPE]       = HitTypes.APP_VIEW;
    params[Fields.SCREEN_NAME]    = screenName;
    this.send(params, success, error);
  },

  /**
   * Sends a user timing
   *
   * @param {string} category - the category
   * @param {string} variable - the variable
   * @param {string} label - the label
   * @param {number} time - the time
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  sendTiming: function (category, variable, label, time, success, error) {
    argscheck.checkArgs('sssnFF', 'analytics.sendException', arguments);
    var params = {};
    params[Fields.HIT_TYPE]        = HitTypes.TIMING;
    params[Fields.TIMING_CATEGORY] = category;
    params[Fields.TIMING_VAR]      = variable;
    params[Fields.TIMING_VALUE]    = time;
    params[Fields.TIMING_LABEL]    = label;
    this.send(params, success, error);
  },

  /**
   * Sends an exception
   *
   * @param {string} description - the exception description
   * @param {boolean} [fatal] - marks the exception as fatal
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  sendException: function (description, fatal, success, error) {
    argscheck.checkArgs('s*FF', 'analytics.sendException', arguments);
    var params = {};
    params[Fields.HIT_TYPE]        = HitTypes.EXCEPTION;
    params[Fields.EX_DESCRIPTION]  = description;
    params[Fields.EX_FATAL]        = fatal ? 1 : 0;
    this.send(params, success, error);
  },

  /**
   * Tracks unhandled scripts errors (window.onerror) and then calls sendException.
   * This function optionally can be passed an object containing a formmatter function
   * which takes in all the args to window.onError and should return a String with
   * the formatted error description to be sent to Google Analytics. Also the object
   * can provide a fatal property which will be passed to sendException (defaults
   * to true).
   *
   * @param {object} [opts] - the options { formatter: Function, fatal: Boolean }
   * @param {function} [success] - the success callback
   * @param {function} [error] - the error callback
   */
  trackUnhandledScriptErrors: function (opts, success, error) {
    argscheck.checkArgs('OFF', 'analytics.trackUnhandledScriptErrors', arguments);
    var self = this,
      fatal = true,
      formatter;
    if (opts && utils.typeName(opts.formatter) === 'Function') {
      formatter = opts.formatter;
    }
    if (opts && utils.typeName(opts.fatal) === 'Boolean') {
      fatal = opts.fatal;
    }
    window.onError = function (message, file, line, col, error) {
      var description;
      try {
        if (formatter) {
          description = formatter(message, file, line, col, error);
        }
      } catch (e) {
        utils.alert(
          'analytics.trackUnhandledScriptErrors invalid formatter. error:' + e);
      } finally {
        // if there is an error formatting or no formatter use default
        if (description === undefined) {
          description = (file || '');
          if (line || col) {
            description += ' (' + (line || '') + (col && ',' + col) + ')';
          }
          description += (description.length > 0 ? ':' : '') + message;
        }
      }
      self.sendException(description, fatal, success, error);
    };
  }

};
