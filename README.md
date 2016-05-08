# react-native-workers

Spin worker threads and run CPU intensive tasks in the background. Bonus point on Android you can keep a worker alive even when a user quit the application :fireworks:

## Features
- JS workers for iOS and Android
- access to native modules (network, geolocation, storage ...)
- Android Services in JS :tada:

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

1. Open your project in XCode, right click on Libraries and click Add Files to "Your Project Name". Look under node_modules/react-native-workers/ios and add `Workers.xcodeproj`
2. Add `libWorkers.a` to `Build Phases -> Link Binary With Libraries`

### Android

in `android/settings.gradle`

```
 include ':app', ':react-native-workers'
 project(':react-native-workers').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-workers/android')
 ```

in `android/app/build.gradle` add:

```
dependencies {
   ...
   compile project(':react-native-workers')
}
```

and finally, in your `MainActivity.java` add:

```java

import co.apptailor.Worker.WorkerPackage; // <--- This!

public class MainActivity extends ReactActivity {

 @Override
 protected String getMainComponentName() {
     return "MyApp";
 }

 @Override
 protected boolean getUseDeveloperSupport() {
     return BuildConfig.DEBUG;
 }

 @Override
 protected List<ReactPackage> getPackages() {
   return Arrays.<ReactPackage>asList(
     new MainReactPackage(),
     new WorkerPackage() // <---- and This!
   );
 }
}
```

## JS API

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
