const authenticator = NativeModules.BioAuth
import {NativeModules} from 'react-native'

const errors = {
  failure: "Failure",
  noAuthenticationOnDevice: "noAuthenticationOnDevice"
}
const authenticate = async (onSuccess, onFailure) => {
    await authenticator.Authenticate(onSuccess, onFailure)
}

export default { authenticate, errors }
