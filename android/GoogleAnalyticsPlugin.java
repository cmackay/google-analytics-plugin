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

  private static final int GA_DISPATCH_PERIOD = 10;

  private void initializeGa() {
    ga = GoogleAnalytics.getInstance(cordova.getActivity());
    ga.setLocalDispatchPeriod(GA_DISPATCH_PERIOD);
  }

  private void initializeTm() {
    tm = TagManager.getInstance(cordova.getActivity());
  }

  /**
   * Initializes the plugin
   *
   * @param cordova The context of the main Activity.
   * @param webView The associated CordovaWebView.
   */
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    initializeGa();
    initializeTm();
  }

  /**
   * Executes the request.
   *
   * @param action          The action to execute.
   * @param args            The exec() arguments.
   * @param callbackContext The callback context used when calling back into JavaScript.
   * @return                Whether the action was valid.
   */
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException {
    try {
      if ("setTrackingId".equals(action)) {
        setTrackingId(args.getString(0));
        callback.success();
        return true;

      } else if ("setLogLevel".equals(action)) {
        setLogLevel(args.getInt(0));
        callback.success();
        return true;

      } else if ("setIDFAEnabled".equals(action)) {
        setIDFAEnabled();
        callback.success();
        return true;

      } else if ("get".equals(action)) {
        callback.success(get(args.getString(0)));
        return true;

      } else if ("set".equals(action)) {
        set(args.getString(0), args.isNull(1) ? null : args.getString(1));
        callback.success();
        return true;

      } else if ("send".equals(action)) {
        send(objectToMap(args.getJSONObject(0)));
        callback.success();
        return true;

      } else if ("close".equals(action)) {
        close();
        callback.success();
        return true;

      } else if ("openContainer".equals(action)) {
        openContainer(args.getString(0), callback);
        return true;

      } else if ("getConfigStringValue".equals(action)) {
        callback.success(getConfigStringValue(args.getString(0)));
        return true;

      } else if ("getConfigBoolValue".equals(action)) {
        callback.sendPluginResult(new PluginResult(
          PluginResult.Status.OK,
          getConfigBoolValue(args.getString(0))));
        return true;

      } else if ("getConfigIntValue".equals(action)) {
        callback.sendPluginResult(new PluginResult(
          PluginResult.Status.OK,
          getConfigIntValue(args.getString(0))));
        return true;

      } else if ("getConfigFloatValue".equals(action)) {
        callback.sendPluginResult(new PluginResult(
          PluginResult.Status.OK,
          (float) getConfigFloatValue(args.getString(0))));
        return true;
      }
    } catch (Exception e) {
      ga.getLogger().error(e);
      callback.error(e.getMessage());
    }
    return false;
  }

  private void openContainer(String containerId, final CallbackContext callback) {
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
  }

  private String getConfigStringValue(String key) {
    assertContainer();
    return containerHolder.getContainer().getString(key);
  }

  private boolean getConfigBoolValue(String key) {
    assertContainer();
    return containerHolder.getContainer().getBoolean(key);
  }

  private long getConfigIntValue(String key) {
    assertContainer();
    return containerHolder.getContainer().getLong(key);
  }

  private double getConfigFloatValue(String key) {
    assertContainer();
    return containerHolder.getContainer().getDouble(key);
  }

  private void setTrackingId(String trackingId) {
    if (tracker != null) {
      close();
    }
    tracker = ga.newTracker(trackingId);
    // setup uncaught exception handler
    tracker.enableExceptionReporting(true);
  }

  private void setLogLevel(int level) {
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
  }

  private void setIDFAEnabled() {
    assertTracker();
    tracker.enableAdvertisingIdCollection(true);
  }

  private String get(String key) {
    assertTracker();
    return tracker.get(key);
  }

  private void set(String key, String value) {
    assertTracker();
    tracker.set(key, value);
  }

  private void send(Map<String,String> map) {
    assertTracker();
    tracker.send(map);
  }

  private void close() {
    assertTracker();
    ga.dispatchLocalHits();
    tracker = null;
  }

  private void assertTracker() {
    if (tracker == null) {
      throw new IllegalStateException("Tracker not initialized. Call setTrackingId prior to using tracker.");
    }
  }

  private void assertContainer() {
    if (containerHolder == null ) {
      throw new IllegalStateException("Container not initialized. Call openContainer prior to using container.");
    }
  }

  private Map<String, String> objectToMap(JSONObject o) throws JSONException {
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

}
