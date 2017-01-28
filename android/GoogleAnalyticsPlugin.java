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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class GoogleAnalyticsPlugin extends CordovaPlugin {

  private static GoogleAnalytics ga;
  private static List<Tracker> trackers = new ArrayList<Tracker>();

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

    } else if ("dispatchHits".equals(action)) {
      dispatchHits(rawArgs, callback);
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
      trackers.clear();
      JSONArray jsonArray = new JSONArray(rawArgs);
      for (int i = 0; i < jsonArray.length(); i++) {
        trackers.add(ga.newTracker(new JSONArray(rawArgs).getString(i)));
      }
      // setup uncaught exception handler
      // Currently we always use the first tracker id. Only one tracker may be
      // used to report uncaught exceptions - developers.google.com/analytics/devguides/collection/android/v4/advanced#multiple-trackers
      trackers.get(0).enableExceptionReporting(true);
      callback.success();
    } catch (JSONException e) {
      callback.error(e.toString());
    }
  }

  private void dispatchHits(String rawArgs, CallbackContext callback) {
    ga.dispatchLocalHits();
    callback.success();
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
      for (Tracker tracker : trackers) {
        tracker.enableAdvertisingIdCollection(true);
      }
      callbackContext.success();
    }
  }

  private void get(String rawArgs, CallbackContext callbackContext) {
    if (hasTracker(callbackContext)) {
      try {
        String param = new JSONArray(rawArgs).getString(0);
        if (trackers.size() == 1) {
          callbackContext.success(trackers.iterator().next().get(param));
        } else if (trackers.size() > 1) {
          /*
           * Returns an array of tracker id and value combinations, e.g.,
           * [{ "tid1" : "param_value1" }, { "tid2" : "param_value2" }]
           */
          JSONArray jsonArray = new JSONArray();
          for (Tracker tracker : trackers) {
            String id = tracker.get("&tid");
            if (id != null) {
              Object value = tracker.get(param);
              JSONObject obj = new JSONObject();
              obj.putOpt(id, value != null ? value : JSONObject.NULL);
              jsonArray.put(obj);
            }
          }
          callbackContext.success(jsonArray.toString());
        } else {
          callbackContext.success();
        }
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
        for (Tracker tracker : trackers) {
          tracker.set(key, value);
        }
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
            for (Tracker tracker : plugin.trackers) {
              tracker.send(objectToMap(args.getJSONObject(0)));
            }
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
          plugin.trackers.clear();
          callbackContext.success();
        }
      }
    });
  }

  private boolean hasTracker(CallbackContext callbackContext) {
    if (trackers.isEmpty()) {
      callbackContext.error("Tracker(s) not initialized. Call setTrackingId or setMultipleTrackingIds prior to using tracker.");
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
