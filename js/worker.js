
module.exports = (WorkerModule, EventEmitter) => {

  class Worker {
    constructor(jsPath) {
      if (!jsPath || !jsPath.endsWith('.js')) {
        throw new Error("Invalid worker path. Only js files are supported");
      }

      this.id = WorkerModule.startWorker(jsPath.replace(".js", ""))
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
