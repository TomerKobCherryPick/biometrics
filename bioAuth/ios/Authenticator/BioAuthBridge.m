//
//  BioAuthBridge.m
//  bioAuth
//
//  Created by Tomer Kobrinsky on 26/09/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(BioAuth, NSObject)

  RCT_EXTERN_METHOD(Authenticate)

@end
