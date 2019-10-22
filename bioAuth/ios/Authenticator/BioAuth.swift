//
//  BioAuth.swift
//  bioAuth
//
//  Created by Tomer Kobrinsky on 26/09/2019.
//  Copyright © 2019 Cherrypick Consulting LTD. All rights reserved.
//

import Foundation
import LocalAuthentication

@objc (BioAuth)
class BioAuth: NSObject {
  override init() {
    super.init()
      print("constructed")
  }
  
  @objc static func requiresMainQueueSetup() -> Bool {
    return false
  }
  
  @objc func Authenticate(_ authenticationDescription: NSString, onSuccess: @escaping RCTResponseSenderBlock, onFailure: @escaping RCTResponseSenderBlock) {
     print("authticate")
    let authenticator = LAContext()
    // User authentication with either biometry, Apple watch or the device passcode.
    if (authenticator.canEvaluatePolicy(LAPolicy.deviceOwnerAuthentication, error: nil)) {
      authenticator.evaluatePolicy(LAPolicy.deviceOwnerAuthentication, localizedReason: authenticationDescription as String) { (didSucceed, error) in
        if (didSucceed) {
           print("Authentication succeeded")
           onSuccess(["success"])
        } else {
           print("Authentication failed")
           onFailure(["Failure"])
        }
      }
    } else {
      print("no Authentication On this Device")
      onFailure(["noAuthenticationOnDevice"])
    }
  }
}
