

module.exports = (WorkerModule, EventEmitter) => {

  class Worker {
    constructor(jsPath) {
      this.id = WorkerModule.startWorker(jsPath)
        .then(id => {
          EventEmitter.addListener(`Worker${id}`, (message) => {
            !!message && this.onmessage && this.onmessage(message);
          });
          return id;
        })
        .catch(err => { throw new Error(err) });
    }

    postMessage(message) {
      this.id.then(id => WorkerModule.postWorkerMessage(id, message));
    }

    terminate() {
      this.id.then(WorkerModule.stopWorker);
    }
  }

  return Worker;
};
