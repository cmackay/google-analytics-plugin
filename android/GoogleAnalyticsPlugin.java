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

import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;

import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;
import com.google.android.gms.tagmanager.DataLayer;

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

  private static TagManager tm;
  private static ContainerHolder containerHolder;
  private static DataLayer dataLayer;

  private static final int GA_DISPATCH_PERIOD = 1;

  /**
   * Initializes the plugin
   *
   * @param cordova The context of the main Activity.
   * @param webView The associated CordovaWebView.
   */
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    // initialize google analytics
    ga = GoogleAnalytics.getInstance(cordova.getActivity());
    ga.setLocalDispatchPeriod(GA_DISPATCH_PERIOD);

    // initialize tag tagmanager and data layer
    tm = TagManager.getInstance(cordova.getActivity());
    dataLayer = tm.getDataLayer();
  }

  /**
   * Executes the request.
   *
   * @param action          The action to execute.
   * @param args            The exec() arguments.
   * @param callback The callback context used when calling back into JavaScript.
   * @return                Whether the action was valid.
   */
  @Override
  public boolean execute(String action, String rawArgs, CallbackContext callback)
    throws JSONException {

    if ("setTrackingId".equals(action)) {
      setTrackingId(rawArgs, callback);
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

    } else if ("containerOpen".equals(action)) {
      containerOpen(rawArgs, callback);
      return true;

    } else if ("getContainerString".equals(action)) {
      getContainerString(rawArgs, callback);
      return true;

    } else if ("getContainerBoolean".equals(action)) {
      getContainerBoolean(rawArgs, callback);
      return true;

    } else if ("getContainerLong".equals(action)) {
      getContainerLong(rawArgs, callback);
      return true;

    } else if ("getContainerDouble".equals(action)) {
      getContainerDouble(rawArgs, callback);
      return true;

    } else if ("dataLayerValue".equals(action)) {
      dataLayerValue(rawArgs, callback);
      return true;

    } else if ("dataLayerPush".equals(action)) {
      dataLayerPush(rawArgs, callback);
      return true;

    } else if ("dataLayerPushEvent".equals(action)) {
      dataLayerPushEvent(rawArgs, callback);
      return true;
    }
    return false;
  }

  private void setTrackingId(String rawArgs, CallbackContext callback) {
    try {
      tracker = ga.newTracker(new JSONArray(rawArgs).getString(0));
      // enabled exception reporting
      tracker.enableExceptionReporting(true);
      callback.success();
    } catch (JSONException e) {
      callback.error(e.toString());
    }
  }

  private void setLogLevel(String rawArgs, CallbackContext callback) {
    if (hasTracker(callback)) {
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
        callback.success();
      } catch (JSONException e) {
        callback.error(e.toString());
      }
    }
  }

  private void setIDFAEnabled(String rawArgs, CallbackContext callback) {
    if (hasTracker(callback)) {
      tracker.enableAdvertisingIdCollection(true);
      callback.success();
    }
  }

  private void get(String rawArgs, CallbackContext callback) {
    if (hasTracker(callback)) {
      try {
        String value = tracker.get(new JSONArray(rawArgs).getString(0));
        callback.success(value);
      } catch (JSONException e) {
        callback.error(e.toString());
      }
    }
  }

  private void set(String rawArgs, CallbackContext callback) {
    if (hasTracker(callback)) {
      try {
        JSONArray args = new JSONArray(rawArgs);
        String key = args.getString(0);
        String value = args.isNull(1) ? null : args.getString(1);
        tracker.set(key, value);
        callback.success();
      } catch (JSONException e) {
        callback.error(e.toString());
      }
    }
  }

  private void send(final String rawArgs, final CallbackContext callback) {
    cordova.getThreadPool().execute(new Runnable() {
      public void run()  {
        GoogleAnalyticsPlugin plugin = GoogleAnalyticsPlugin.this;
        if (plugin.hasTracker(callback)) {
          try {
            JSONArray args = new JSONArray(rawArgs);
            plugin.tracker.send(toStringMap(args.getJSONObject(0)));
            callback.success();
          } catch (JSONException e) {
            callback.error(e.toString());
          }
        }
      }
    });
  }

  private void close(final String rawArgs, final CallbackContext callback) {
    cordova.getThreadPool().execute(new Runnable() {

      public void run() {
        GoogleAnalyticsPlugin plugin = GoogleAnalyticsPlugin.this;
        if (plugin.hasTracker(callback)) {
          plugin.ga.dispatchLocalHits();
          plugin.tracker = null;
          callback.success();
        }
      }

    });
  }

  private boolean hasTracker(CallbackContext callback) {
    if (tracker == null) {
      callback.error("Tracker not initialized. Call setTrackingId prior to using tracker.");
      return false;
    }
    return true;
  }

  private void containerOpen(final String rawArgs, final CallbackContext callback) {
    cordova.getThreadPool().execute(new Runnable() {

      public void run() {
        try {
          String containerId = new JSONArray(rawArgs).getString(0);

          tm.loadContainerPreferFresh(containerId, -1)
            .setResultCallback(new ResultCallback<ContainerHolder>() {

              public void onResult(ContainerHolder holder) {
                if (holder.getStatus().isSuccess()) {
                  containerHolder = holder;
                  containerHolder.refresh();
                  callback.success();
                } else {
                  callback.error(holder.getStatus().toString());
                }
              }

            });
          } catch (JSONException e) {
            callback.error(e.toString());
          }
        }
      });
  }

  private void getContainerString(String rawArgs, CallbackContext callback) {
    if (hasContainer(callback)) {
      try {
        String key = new JSONArray(rawArgs).getString(0);
        String value = containerHolder.getContainer().getString(key);
        callback.success(value);

      } catch (JSONException e) {
        callback.error(e.toString());
      }
    }
  }

  private void getContainerBoolean(String rawArgs, CallbackContext callback) {
    if (hasContainer(callback)) {
      try {
        String key = new JSONArray(rawArgs).getString(0);
        boolean value = containerHolder.getContainer().getBoolean(key);
        callback.sendPluginResult(new PluginResult(
          PluginResult.Status.OK, value));

      } catch (JSONException e) {
        callback.error(e.toString());
      }
    }
  }

  private void getContainerLong(String rawArgs, CallbackContext callback) {
    if (hasContainer(callback)) {
      try {
        String key = new JSONArray(rawArgs).getString(0);
        long value = containerHolder.getContainer().getLong(key);
        callback.sendPluginResult(new PluginResult(
          PluginResult.Status.OK, value));

      } catch (JSONException e) {
        callback.error(e.toString());
      }
    }
  }

  private void getContainerDouble(String rawArgs, CallbackContext callback) {
    if (hasContainer(callback)) {
      try {
        String key = new JSONArray(rawArgs).getString(0);
        double value = containerHolder.getContainer().getDouble(key);
        callback.sendPluginResult(new PluginResult(
          PluginResult.Status.OK, (float) value));

      } catch (JSONException e) {
        callback.error(e.toString());
      }
    }
  }

  private void dataLayerValue(final String rawArgs, final CallbackContext callback) {
    cordova.getThreadPool().execute(new Runnable() {

      public void run() {
        try {
          String key = new JSONArray(rawArgs).getString(0);
          Object value = dataLayer.get(key);
          PluginResult result;
          if (value == null) {
            result = new PluginResult(PluginResult.Status.OK, (String) null);

          } else if (value instanceof Map) {
            result = new PluginResult(
              PluginResult.Status.OK, new JSONObject((Map)value));

          } else if (value instanceof Double || value instanceof Float) {
            result = new PluginResult(
              PluginResult.Status.OK, ((Number) value).floatValue());

          } else if (value instanceof Long || value instanceof Integer) {
            result = new PluginResult(
              PluginResult.Status.OK, ((Number) value).intValue());

          } else if (value instanceof Boolean) {
            result = new PluginResult(
              PluginResult.Status.OK, ((Boolean) value).booleanValue());

          } else {
            result = new PluginResult(PluginResult.Status.OK, value.toString());

          }
          callback.sendPluginResult(result);
        } catch (JSONException e) {
          callback.error(e.toString());
        }
      }

    });
  }

  private void dataLayerPush(final String rawArgs, final CallbackContext callback) {
    cordova.getThreadPool().execute(new Runnable() {

      public void run() {
        try {
          JSONArray args = new JSONArray(rawArgs);

          if (args.optJSONObject(0) != null) {
            dataLayer.push(toMap(args.getJSONObject(0)));

          } else {
            String key = args.getString(0);
            Object value = args.opt(1);
            dataLayer.push(key, value);
          }

          callback.success();

        } catch (JSONException e) {
          callback.error(e.toString());
        }
      }

    });
  }

  private void dataLayerPushEvent(final String rawArgs, final CallbackContext callback) {
    cordova.getThreadPool().execute(new Runnable() {

      public void run() {
        try {
          JSONArray args = new JSONArray(rawArgs);

          String eventName = args.getString(0);
          Map<String, Object> updates = toMap(args.optJSONObject(1));

          dataLayer.pushEvent(eventName, updates);
          callback.success();
        } catch (JSONException e) {
          callback.error(e.toString());
        }
      }

    });
  }

  private boolean hasContainer(CallbackContext callback) {
    if (containerHolder == null) {
      callback.error("Container not initialized. Call containerOpen prior to using container.");
      return false;
    }
    if (!containerHolder.getStatus().isSuccess()) {
      callback.error("Container status is not successful: " +
        containerHolder.getStatus().toString());
      return false;
    }
    return true;
  }

  private static Map<String, String> toStringMap(JSONObject o) throws JSONException {
    if (o == null || o.length() == 0) {
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

  private static Map<String, Object> toMap(JSONObject o) throws JSONException {
    if (o == null || o.length() == 0) {
      return Collections.<String, Object>emptyMap();
    }
    Map<String, Object> map = new HashMap<String, Object>(o.length());
    Iterator it = o.keys();
    String key;
    Object value;
    while (it.hasNext()) {
      key = it.next().toString();
      value = o.has(key) ? o.get(key): null;
      map.put(key, value);
    }
    return map;
  }

}
