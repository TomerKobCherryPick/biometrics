//
//  BioAuth.swift
//  bioAuth
//
//  Created by Tomer Kobrinsky on 26/09/2019.
//  Copyright © 2019 Facebook. All rights reserved.
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
  
  @objc(Authenticate)
  func Authenticate() {
     print("authticate")
    let authenticator = LAContext()
    // User authentication with either biometry, Apple watch or the device passcode.
    if (authenticator.canEvaluatePolicy(LAPolicy.deviceOwnerAuthentication, error: nil)) {
      authenticator.evaluatePolicy(LAPolicy.deviceOwnerAuthentication, localizedReason: "check") { (didSucceed, error) in
        if (didSucceed) {
           print("succeeded")
        } else {
           print("not succeeded")
        }
      }
    } else {
      print("not allowed")
    }
  }
}
