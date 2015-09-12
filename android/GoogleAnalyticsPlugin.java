/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package com.cmackay.plugins.googleanalytics;

import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class GoogleAnalyticsPlugin extends CordovaPlugin {

  private static GoogleAnalytics ga;
  private static Tracker tracker;

  /**
   * Initializes the plugin
   *
   * @param cordova The context of the main Activity.
   * @param webView The associated CordovaWebView.
   */
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    ga = GoogleAnalytics.getInstance(cordova.getActivity());
  }

  /**
   * Executes the request.
   *
   * @param action          The action to execute.
   * @param args            The exec() arguments.
   * @param callback        The callback context used when calling back into JavaScript.
   * @return                Whether the action was valid.
   */
  @Override
  public boolean execute(String action, String rawArgs, CallbackContext callback)
    throws JSONException {

    if ("setTrackingId".equals(action)) {
      setTrackingId(rawArgs, callback);
      return true;

    } else if ("setDispatchInterval".equals(action)) {
      setDispatchInterval(rawArgs, callback);
      return true;

    } else if ("setLogLevel".equals(action)) {
      setLogLevel(rawArgs, callback);
      return true;

    } else if ("setIDFAEnabled".equals(action)) {
      setIDFAEnabled(rawArgs, callback);
      return true;

    } else if ("get".equals(action)) {
      get(rawArgs, callback);
      return true;

    } else if ("set".equals(action)) {
      set(rawArgs, callback);
      return true;

    } else if ("send".equals(action)) {
      send(rawArgs, callback);
      return true;

    } else if ("close".equals(action)) {
      close(rawArgs, callback);
      return true;

    } else if ("setAppOptOut".equals(action)) {
      setAppOptOut(rawArgs, callback);
      return true;

    } else if ("getAppOptOut".equals(action)) {
      getAppOptOut(callback);
      return true;
    }

    return false;
  }

  private void setTrackingId(String rawArgs, CallbackContext callback) {
    try {
      tracker = ga.newTracker(new JSONArray(rawArgs).getString(0));
      // setup uncaught exception handler
      tracker.enableExceptionReporting(true);
      callback.success();
    } catch (JSONException e) {
      callback.error(e.toString());
    }
  }

  private void setDispatchInterval(String rawArgs, CallbackContext callback) {
    try {
      ga.setLocalDispatchPeriod(new JSONArray(rawArgs).getInt(0));
      callback.success();
    } catch (JSONException e) {
      callback.error(e.toString());
    }
  }

  private void setLogLevel(String rawArgs, CallbackContext callbackContext) {
    if (hasTracker(callbackContext)) {
      try {
        int level = new JSONArray(rawArgs).getInt(0);
        int logLevel = LogLevel.WARNING;
        switch (level) {
          case 0:
          logLevel = LogLevel.VERBOSE;
          break;
          case 1:
          logLevel = LogLevel.INFO;
          break;
          case 2:
          logLevel = LogLevel.WARNING;
          break;
          case 3:
          logLevel = LogLevel.ERROR;
          break;
        }
        ga.getLogger().setLogLevel(logLevel);
        callbackContext.success();
      } catch (JSONException e) {
        callbackContext.error(e.toString());
      }
    }
  }

  private void setIDFAEnabled(String rawArgs, CallbackContext callbackContext) {
    if (hasTracker(callbackContext)) {
      tracker.enableAdvertisingIdCollection(true);
      callbackContext.success();
    }
  }

  private void get(String rawArgs, CallbackContext callbackContext) {
    if (hasTracker(callbackContext)) {
      try {
        String value = tracker.get(new JSONArray(rawArgs).getString(0));
        callbackContext.success(value);
      } catch (JSONException e) {
        callbackContext.error(e.toString());
      }
    }
  }

  private void set(String rawArgs, CallbackContext callbackContext) {
    if (hasTracker(callbackContext)) {
      try {
        JSONArray args = new JSONArray(rawArgs);
        String key = args.getString(0);
        String value = args.isNull(1) ? null : args.getString(1);
        tracker.set(key, value);
        callbackContext.success();
      } catch (JSONException e) {
        callbackContext.error(e.toString());
      }
    }
  }

  private void send(final String rawArgs, final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      public void run()  {
        GoogleAnalyticsPlugin plugin = GoogleAnalyticsPlugin.this;
        if (plugin.hasTracker(callbackContext)) {
          try {
            JSONArray args = new JSONArray(rawArgs);
            plugin.tracker.send(objectToMap(args.getJSONObject(0)));
            callbackContext.success();
          } catch (JSONException e) {
            callbackContext.error(e.toString());
          }
        }
      }
    });
  }

  private void close(final String rawArgs, final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        GoogleAnalyticsPlugin plugin = GoogleAnalyticsPlugin.this;
        if (plugin.hasTracker(callbackContext)) {
          plugin.ga.dispatchLocalHits();
          plugin.tracker = null;
          callbackContext.success();
        }
      }
    });
  }

  private boolean hasTracker(CallbackContext callbackContext) {
    if (tracker == null) {
      callbackContext.error("Tracker not initialized. Call setTrackingId prior to using tracker.");
      return false;
    }
    return true;
  }

  private static Map<String, String> objectToMap(JSONObject o) throws JSONException {
    if (o.length() == 0) {
      return Collections.<String, String>emptyMap();
    }
    Map<String, String> map = new HashMap<String, String>(o.length());
    Iterator it = o.keys();
    String key, value;
    while (it.hasNext()) {
      key = it.next().toString();
      value = o.has(key) ? o.get(key).toString(): null;
      map.put(key, value);
    }
    return map;
  }

  private void setAppOptOut(final String rawArgs, CallbackContext callbackContext) {
    try {
      ga.setAppOptOut(new JSONArray(rawArgs).getBoolean(0));
      callbackContext.success();
    } catch (JSONException e) {
      callbackContext.error(e.toString());
    }
  }

  private void getAppOptOut(CallbackContext callbackContext) {
    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, ga.getAppOptOut()));
  }

}
