package co.apptailor.Worker.core;

import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.JSTimersExecution;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.modules.core.Timing;
import com.facebook.react.modules.intent.IntentModule;
import com.facebook.react.modules.location.LocationModule;
import com.facebook.react.modules.netinfo.NetInfoModule;
import com.facebook.react.modules.network.NetworkingModule;
import com.facebook.react.modules.storage.AsyncStorageModule;
import com.facebook.react.modules.systeminfo.AndroidInfoModule;
import com.facebook.react.modules.vibration.VibrationModule;
import com.facebook.react.modules.websocket.WebSocketModule;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Arrays;
import java.util.List;

public class BaseModuleList {

    public List<NativeModule> nativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(
                new AndroidInfoModule(),
                new Timing(reactContext),
                new UIManagerStubModule(reactContext),
                new AsyncStorageModule(reactContext),
                new IntentModule(reactContext),
                new LocationModule(reactContext),
                new NetworkingModule(reactContext),
                new NetInfoModule(reactContext),
                new VibrationModule(reactContext),
                new WebSocketModule(reactContext),
                new WorkerSelfModule(reactContext)
        );
    }

    public List<Class<? extends JavaScriptModule>> jsModules() {
        return Arrays.asList(
                DeviceEventManagerModule.RCTDeviceEventEmitter.class,
                JSTimersExecution.class,
                RCTEventEmitter.class,
                RCTNativeAppEventEmitter.class,
                com.facebook.react.bridge.Systrace.class
        );
    }
}
