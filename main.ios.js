import {
  NativeModules,
  NativeAppEventEmitter,
} from 'react-native';

module.exports = {
  get self() {
    return require('./js/self')(NativeModules.WorkerSelfManager, NativeAppEventEmitter);
  },
  get Worker() {
    return require('./js/worker')(NativeModules.WorkerManager, NativeAppEventEmitter);
  },
}
