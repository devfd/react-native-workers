import {
  NativeModules,
  DeviceEventEmitter,
} from 'react-native';

module.exports = {
  get self() {
    return require('./js/self')(NativeModules.RNWorkerSelf, DeviceEventEmitter);
  },
  get Worker() {
    return require('./js/worker')(NativeModules.RNWorker, DeviceEventEmitter);
  },
}
