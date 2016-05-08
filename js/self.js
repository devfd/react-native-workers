
module.exports = (WorkerSelf, EventEmitter) => {

  const self = {
    onmessage: null,
    postMessage: (message) => {
      if (!message) { return; }
      WorkerSelf.postMessage(message);
    }
  };

  EventEmitter.addListener("WorkerMessage", (message) => {
    !!message && self.onmessage && self.onmessage(message);
  });

  return self;
};
