import React, { Component } from 'react'
import {View, Text, TouchableOpacity} from 'react-native'
import {NativeModules} from 'react-native'
const authenticator = NativeModules.BioAuth

class App extends Component {
  constructor(props) {
    super(props)
    this.state = {
      text: 'Authenticate',
      buttonColor: 'grey'
    }
  }

  onSuccess = () => {
    this.setState({text: 'Success :)', buttonColor: 'green'})
  }

  onFail = () => {
    this.setState({text: 'Fail :/ , try again', buttonColor: 'red'})
  }

  render () {
    return (
      <View style={{flex: 1, alignItems: 'center', justifyContent: 'center', }}>
        <TouchableOpacity
          style={{alignItems: 'center', justifyContent: 'center', width: 120, height: 50, borderRadius: 5, backgroundColor: this.state.buttonColor, }}
          onPress={() => authenticator.Authenticate(this.onSuccess)}
          >
          <Text style={{fontSize: 14}}>
            {this.state.text}
          </Text>
        </TouchableOpacity>
      </View>
    )
  }
}

export default App
