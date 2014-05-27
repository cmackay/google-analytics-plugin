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

import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.Tracker;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class GoogleAnalyticsPlugin extends CordovaPlugin {

  private static GoogleAnalytics ga;
  private static GAServiceManager serviceManager;
  private static Tracker tracker;
  private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

  private static final int GA_DISPATCH_PERIOD = 10;
  private static final LogLevel GA_LOG_LEVEL = LogLevel.VERBOSE;

  private void initializeGa() {
    ga = GoogleAnalytics.getInstance(cordova.getActivity());
    ga.getLogger().setLogLevel(GA_LOG_LEVEL);

    serviceManager = GAServiceManager.getInstance();
    serviceManager.setLocalDispatchPeriod(GA_DISPATCH_PERIOD);

    uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
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
      }
    } catch (Exception e) {
      ga.getLogger().error(e);
      callback.error(e.getMessage());
    }
    return false;
  }

  private void setTrackingId(String trackingId) {
    if (tracker != null) {
      close();
    }
    tracker = ga.getTracker(trackingId);

    // setup uncaught exception handler
    Thread.setDefaultUncaughtExceptionHandler(new ExceptionReporter(
      tracker, serviceManager, uncaughtExceptionHandler, cordova.getActivity()));
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
    ga.closeTracker(tracker.getName());
    tracker = null;
  }

  private void assertTracker() {
    if (tracker == null) {
      throw new IllegalStateException("Tracker not initialized. Call setTrackingId prior to using tracker.");
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
