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

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>

@protocol GAITracker;
@class TAGManager;
@class TAGContainer;

@interface GoogleAnalyticsPlugin : CDVPlugin {
  id<GAITracker> tracker;
}

@property(nonatomic, retain) TAGManager *tagManager;
@property(nonatomic, retain) TAGContainer *container;
@property (nonatomic, strong) NSString* containerOpenedCallbackId;

- (void) containerOpen: (CDVInvokedUrlCommand*)command;
- (void) getContainerString: (CDVInvokedUrlCommand*)command;
- (void) getContainerBoolean: (CDVInvokedUrlCommand*)command;
- (void) getContainerLong: (CDVInvokedUrlCommand*)command;
- (void) getContainerDouble: (CDVInvokedUrlCommand*)command;
- (void) getDatalayer: (CDVInvokedUrlCommand*)command;
- (void) dataLayerPush: (CDVInvokedUrlCommand*)command;
- (void) setTrackingId: (CDVInvokedUrlCommand*)command;
- (void) setLogLevel: (CDVInvokedUrlCommand*)command;
- (void) get: (CDVInvokedUrlCommand*)command;
- (void) set: (CDVInvokedUrlCommand*)command;
- (void) send: (CDVInvokedUrlCommand*)command;
- (void) close: (CDVInvokedUrlCommand*)command;

@end
