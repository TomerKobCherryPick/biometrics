const authenticator = NativeModules.BioAuth
import {NativeModules, Platform} from 'react-native'

const errors = {
  failure: "Failure",
  noAuthenticationOnDevice: "noAuthenticationOnDevice"
}

const authenticate =  (onSuccess, onFailure, title, description) => {
    const authenticationTitle = title ?? "myAuthentication"
    const authenticationDescription = description ?? "Please authenticate to continue"
    if (Platform.OS === 'android') {
      authenticator.Authenticate(authenticationTitle, authenticationDescription, onSuccess, onFailure)
    } else {
      // in ios there is no option to change the title of authentication alert
      authenticator.Authenticate(authenticationDescription, onSuccess, onFailure)
    }

}



export default { authenticate, errors }
