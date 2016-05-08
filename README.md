# react-native-workers

Spin worker threads and run CPU intensive tasks in the background. Bonus point on Android you can keep a worker alive even when a user quit the application :fireworks:

## Features
- JS workers run on both iOS and Android
- access all native modules from the workers (network, geolocation, async storage ...)
- write your Android Services in JS :tada:

## Warning
This plugin is still in beta and some features are missing. Current restrictions include:
- worker files are only loaded from http://localhost:8081.
- worker files are not yet packaged with your application.
- no HMR support. no hot-reload support.

## Installation

```bash
npm install react-native-workers --save
```

## Setup

### iOS

1. Open your project in XCode, right click on Libraries and click Add Files to "Your Project Name". Look under node_modules/react-native-workers/ios and add `Workers.xcodeproj`.
2. Add `libWorkers.a` to `Build Phases -> Link Binary With Libraries.

## API

From your application:
```js
import { Worker } from 'react-native-workers';

/* start worker */
const worker = new Worker("path/to/js/worker");

/* post message to worker. String only ! */
worker.postMessage("hello from application");

/* get message from worker. String only ! */
worker.onmessage = (message) => {

}

/* stop worker */
worker.terminate();

```

From your worker:
```js
import { self } from 'react-native-workers';

/* get message from application. String only ! */
self.onmessage = (message) => {
}

/* post message to application. String only ! */
self.postMessage("hello from worker");
```

##### Reload your application to restart your worker with the latest bundle version
