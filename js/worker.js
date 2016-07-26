import {
  NativeModules,
  DeviceEventEmitter,
} from 'react-native';

const { WorkerManager } = NativeModules;

export default class Worker {
  constructor(jsPath) {
    if (!jsPath || !jsPath.endsWith('.js')) {
      throw new Error("Invalid worker path. Only js files are supported");
    }

    this.id = WorkerManager.startWorker(jsPath.replace(".js", ""))
      .then(id => {
        DeviceEventEmitter.addListener(`Worker${id}`, (message) => {
          !!message && this.onmessage && this.onmessage(message);
        });
        return id;
      })
      .catch(err => { throw new Error(err) });
  }

  postMessage(message) {
    this.id.then(id => WorkerManager.postWorkerMessage(id, message));
  }

  terminate() {
    this.id.then(WorkerManager.stopWorker);
  }
}
