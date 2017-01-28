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

@interface GoogleAnalyticsPlugin()

- (void) _releaseTrackers;
- (void) _setTrackingIds: (CDVInvokedUrlCommand*)command;

@end

@implementation GoogleAnalyticsPlugin

- (void) _releaseTrackers
{
  if (trackers) {
    for (NSUInteger i = 0; i < [trackers count]; i++) {
      [[GAI sharedInstance] removeTrackerByName:[[trackers objectAtIndex:i] name]];
    }
    [trackers removeAllObjects];
  }
}

- (void) _setTrackingIds: (CDVInvokedUrlCommand*)command
{
  [GAI sharedInstance].trackUncaughtExceptions = YES;

  [self _releaseTrackers];

  if (!trackers) {
    trackers = [[NSMutableArray alloc] initWithCapacity:[command.arguments count]];
  }

  for (NSUInteger i = 0; i < [command.arguments count]; i++) {
    id<GAITracker> tracker = [[GAI sharedInstance] trackerWithTrackingId:[command.arguments objectAtIndex:i]];
    [trackers addObject:tracker];
  }
}

- (void) setTrackingId: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  [self _setTrackingIds:command];

  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) setMultipleTrackingIds: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  [self _setTrackingIds:command];

  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) dispatchHits: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  [[GAI sharedInstance] dispatch];
  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) setDispatchInterval: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSTimeInterval dispatchInterval = [[command.arguments objectAtIndex:0] unsignedLongValue];

  [GAI sharedInstance].dispatchInterval = dispatchInterval;
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

  @try {
    NSString* key = [command.arguments objectAtIndex:0];

    if (!trackers || ![trackers count]) {
      result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker(s) not initialized"];
    } else if ([trackers count] == 1) {
      id<GAITracker> tracker = [trackers objectAtIndex:0];
      result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[tracker get:key]];
    } else {
        /*
         * Returns an array of tracker id and value combinations, e.g.,
         * [{ "tid1" : "param_value1" }, { "tid2" : "param_value2" }]
         */
        NSMutableArray *array = [NSMutableArray arrayWithCapacity:[trackers count]];
        for (NSUInteger i = 0; i < [trackers count]; i++) {
          id<GAITracker> tracker = [trackers objectAtIndex:i];
          NSString *tid = [tracker get:@"&tid"];
          NSString *value = [tracker get:key];
          NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
          [dict setValue:value == nil ? [NSNull null] : value forKey:tid];
          [array addObject:dict];
        }

        NSError *err = nil;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:array options:0 error:&err];
        if (!jsonData) {
          // something went wrong with dict - json serialization
          result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[err localizedDescription]];
        } else {
          NSString *serialized = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
          result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:serialized];
        }
    }
  } @catch (NSException *e) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[e reason]];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) set: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSString* key = [command.arguments objectAtIndex:0];
  NSString* value = [command.arguments objectAtIndex:1];

  if (!trackers || ![trackers count]) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker(s) not initialized"];
  } else {
    for (NSUInteger i = 0; i < [trackers count]; i++) {
      [[trackers objectAtIndex:i] set:key value:value];
    }
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) send: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  NSDictionary* params = [command.arguments objectAtIndex:0];

  if (!trackers || ![trackers count]) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker(s) not initialized"];
  } else {
    for (NSUInteger i = 0; i < [trackers count]; i++) {
      [[trackers objectAtIndex:i] send:params];
    }
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) close: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  if (!trackers || ![trackers count]) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"tracker(s) not initialized"];
  } else {
    [self _releaseTrackers];
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) getAppOptOut: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;

  if ([GAI sharedInstance].optOut) {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:(true)];
  } else {
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:(false)];
  }

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

- (void) setAppOptOut: (CDVInvokedUrlCommand*)command
{
  CDVPluginResult* result = nil;
  BOOL enabled = [[command.arguments objectAtIndex:0] boolValue];

  if (enabled) {
    [[GAI sharedInstance] setOptOut:YES];
  } else {
    [[GAI sharedInstance] setOptOut:NO];
  }
  result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

  [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
}

@end
