
/*
 *
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
 *
 */

'use strict';

var argscheck = require('cordova/argscheck'),
    exec      = require('cordova/exec');

function Analytics() {
}

Analytics.prototype = {

  Fields: {
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
  },

  setTrackingId: function (trackingId, success, error) {
    argscheck.checkArgs('sFF', 'Analytics.setTrackingId', arguments);
    exec(success, error, 'GoogleAnalytics', 'setTrackingId', [trackingId]);
  },

  get: function (key, success, error) {
    argscheck.checkArgs('sFF', 'Analytics.get', arguments);
    exec(success, error, 'GoogleAnalytics', 'get', [key]);
  },

  set: function (key, value, success, error) {
    argscheck.checkArgs('s*FF', 'Analytics.set', arguments);
    exec(success, error, 'GoogleAnalytics', 'set', [key, value]);
  },

  send: function (map, success, error) {
    argscheck.checkArgs('oFF', 'Analytics.send', arguments);
    exec(success, error, 'GoogleAnalytics', 'send', [map]);
  },

  close: function (success, error) {
    argscheck.checkArgs('FF', 'Analytics.close', arguments);
    exec(success, error, 'GoogleAnalytics', 'close', []);
  }

};

module.exports = new Analytics();

