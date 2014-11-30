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

#import "GoogleAnalyticsPlugin.h"
#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"
#import "TAGContainer.h"
#import "TAGContainerOpener.h"
#import "TAGManager.h"

@interface GoogleAnalyticsPlugin ()<TAGContainerOpenerNotifier>
@end

@implementation GoogleAnalyticsPlugin

@synthesize tagManager = _tagManager;
@synthesize container = _container;

- (void) openContainer: (CDVInvokedUrlCommand*)command
{
    NSString* containerId = [command.arguments objectAtIndex:0];
    self.tagManager = [TAGManager instance];
    self.containerOpenedCallbackId = command.callbackId;
    
    // Optional: Change the LogLevel to Verbose to enable logging at VERBOSE and higher levels.
    [self.tagManager.logger setLogLevel:kTAGLoggerLogLevelVerbose];
    
    /*
     * Opens a container.
     *
     * @param containerId The ID of the container to load.
     * @param tagManager The TAGManager instance for getting the container.
     * @param openType The choice of how to open the container.
     * @param timeout The timeout period (default is 2.0 seconds).
     * @param notifier The notifier to inform on container load events.
     */
    [TAGContainerOpener openContainerWithId:containerId   // Update with your Container ID.
                                 tagManager:self.tagManager
                                   openType:kTAGOpenTypePreferFresh
                                    timeout:nil
                                   notifier:self];
 
}

/**
 *
 * Refresh an already opened container
 *
 **/
- (void) refreshContainer: (CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = nil;
    
    if (!self.container) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"container not opened"];
    } else {
        [self.container refresh];
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Refreshed"];
    }
    
    [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

/**
 *
 * TAGContainerOpenerNotifier callback. This will be called then openContainerWithId
 * returns with the container
 *
 */
- (void)containerAvailable:(TAGContainer *)container {
    //
    // Note that containerAvailable may be called on any thread, so you may need to dispatch back to
    // your main thread.
    //
    dispatch_async(dispatch_get_main_queue(), ^{
        self.container = container;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:self.containerOpenedCallbackId];
    });
}

/**
 *
 * Get GTM config value for the passed in key
 *
 */
- (void) getConfigStringValue: (CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = nil;
    NSString* key = [command.arguments objectAtIndex:0];
    
    if (!self.container) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"container not opened"];
    } else {
        //
        // Get the configuration value by key.
        //
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[self.container stringForKey:key]];
    }
    [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) getConfigBoolValue: (CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = nil;
    NSString* key = [command.arguments objectAtIndex:0];
    
    if (!self.container) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"container not opened"];
    } else {
        //
        // Get the configuration value by key.
        //
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:[self.container booleanForKey:key]];
    }
    [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) getConfigIntValue: (CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = nil;
    NSString* key = [command.arguments objectAtIndex:0];
    
    if (!self.container) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"container not opened"];
    } else {
        //
        // Get the configuration value by key.
        //
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[self.container int64ForKey:key]];
    }
    [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) getConfigFloatValue: (CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = nil;
    NSString* key = [command.arguments objectAtIndex:0];
    
    if (!self.container) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"container not opened"];
    } else {
        //
        // Get the configuration value by key.
        //
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDouble:[self.container doubleForKey:key]];
    }
    [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}
/**
 *
 * Initialise the tracker by using the passed in tracking ID
 *
 */
- (void) setTrackingId: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSString* trackingId = [command.arguments objectAtIndex:0];

  [GAI sharedInstance].dispatchInterval = 10;
  [GAI sharedInstance].trackUncaughtExceptions = YES;

  if (tracker) {
    [[GAI sharedInstance] removeTrackerByName:[tracker name]];
  }

  tracker = [[GAI sharedInstance] trackerWithTrackingId:trackingId];
  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) setLogLevel: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  GAILogLevel logLevel = (GAILogLevel)[command.arguments objectAtIndex:0];

  [[[GAI sharedInstance] logger] setLogLevel:logLevel];

  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) setIDFAEnabled: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  if (!tracker) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker not initialized"];
  } else {
    // Enable IDFA collection.
    tracker.allowIDFACollection = YES;
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) get: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSString* key = [command.arguments objectAtIndex:0];

  if (!tracker) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker not initialized"];
  } else {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[tracker get:key]];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) set: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSString* key = [command.arguments objectAtIndex:0];
  NSString* value = [command.arguments objectAtIndex:1];

  if (!tracker) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker not initialized"];
  } else {
    [tracker set:key value:value];
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) send: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSDictionary* params = [command.arguments objectAtIndex:0];

  if (!tracker) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker not initialized"];
  } else {
    [tracker send:params];
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) close: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  if (!tracker) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker not initialized"];
  } else {
    [[GAI sharedInstance] removeTrackerByName:[tracker name]];
    tracker = nil;
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

@end
