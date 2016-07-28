import { self } from 'react-native-workers';

/*
 * Web Worker
 * you have access to all RN native modules (timeout, fetch, AsyncStorage, Vibration ...)
 */

// receive messages from main thread
self.onmessage = (message) => {
  console.log('worker received message', message);
}

function ping() {
  // send messages to main thread
  self.postMessage("Ping");
  setTimeout(ping, 5000);
}


setTimeout(ping, 5000);
