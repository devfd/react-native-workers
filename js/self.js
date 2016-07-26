import {
  NativeModules,
  DeviceEventEmitter,
} from 'react-native';

const { WorkerSelfManager } = NativeModules;

const self = {
  onmessage: null,

  postMessage: (message) => {
    if (!message) { return; }
    WorkerSelfManager.postMessage(message);
  }
};

DeviceEventEmitter.addListener("WorkerMessage", (message) => {
  !!message && self.onmessage && self.onmessage(message);
});

export default self;
