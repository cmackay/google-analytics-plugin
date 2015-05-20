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
#import "TAGDataLayer.h"



@interface GoogleAnalyticsPlugin ()<TAGContainerCallback>
@end

@implementation GoogleAnalyticsPlugin

@synthesize tagManager = _tagManager;
@synthesize container = _container;

/**
 *
 * Initialise the tracker by using the passed in tracking ID
 *
 */
- (void) setTrackingId: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSString* trackingId = [command.arguments objectAtIndex:0];

  [GAI sharedInstance].dispatchInterval = 1;
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


/******************************************************************************************
 *
 * Google Tag Manager related functions
 *
 ****************************************************************************************/

- (void) containerOpen: (CDVInvokedUrlCommand*)command
{
    NSString* containerId = [command.arguments objectAtIndex:0];
    self.tagManager = [TAGManager instance];
    self.containerOpenedCallbackId = command.callbackId;

    // Optional: Change the LogLevel to Verbose to enable logging at VERBOSE and higher levels.
    [self.tagManager.logger setLogLevel:kTAGLoggerLogLevelVerbose];

    /**
     * we could open the container by the TAGContainerOpener however the callback handling 
     * for that is not so sophisticated as the openContainerById:callback call so we rather 
     * use that here
     *
    [TAGContainerOpener openContainerWithId:containerId   // Update with your Container ID.
    tagManager:self.tagManager
    openType:kTAGOpenTypePreferNonDefault
    timeout:&timeout
    notifier:self];
    */
    
    [self.tagManager openContainerById:containerId callback:self];
}

/**
*
* Refresh an already opened container
*
**/
- (void) containerRefresh: (CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = nil;
    self.containerOpenedCallbackId = command.callbackId;

    if (!self.container) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"container not opened"];
        [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
    } else {
        [self.container refresh];
    }

}



//>-----

/**
 * Called before the refresh is about to begin.
 *
 * @param container The container being refreshed.
 * @param refreshType The type of refresh which is starting.
 */
- (void)containerRefreshBegin:(TAGContainer *)container
                  refreshType:(TAGContainerCallbackRefreshType)refreshType {
    // Notify UI that container refresh is beginning.
    NSLog(@"GTM: refresh begin");
}

/**
 * Called when a refresh has successfully completed for the given refresh type.
 *
 * @param container The container being refreshed.
 * @param refreshType The type of refresh which completed successfully.
 */
- (void)containerRefreshSuccess:(TAGContainer *)container
                    refreshType:(TAGContainerCallbackRefreshType)refreshType {
    // Notify UI that container is available.
    NSLog(@"GTM: refresh done");
    
    dispatch_async(dispatch_get_main_queue(), ^{
        self.container = container;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:self.containerOpenedCallbackId];
    });
}

/**
 * Called when a refresh has failed to complete for the given refresh type.
 *
 * @param container The container being refreshed.
 * @param failure The reason for the refresh failure.
 * @param refreshType The type of refresh which failed.
 */
- (void)containerRefreshFailure:(TAGContainer *)container
                        failure:(TAGContainerCallbackRefreshFailure)failure
                    refreshType:(TAGContainerCallbackRefreshType)refreshType {
    // Notify UI that container request has failed.
    
    NSLog(@"GTM: refresh failed %u", refreshType);
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if (failure != kTAGContainerCallbackRefreshFailureNoSavedContainer) {
            self.container = container;
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR] callbackId:self.containerOpenedCallbackId];
        }
    });
}

// >------

/**
*
* Get GTM config value for the passed in key
*
*/
- (void) getContainerString: (CDVInvokedUrlCommand*)command
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

- (void) getContainerBoolean: (CDVInvokedUrlCommand*)command
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

- (void) getContainerLong: (CDVInvokedUrlCommand*)command
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

- (void) getContainerDouble: (CDVInvokedUrlCommand*)command
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

@end


