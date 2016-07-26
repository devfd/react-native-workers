package co.apptailor.Worker.core;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.devsupport.JSCHeapCapture;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.ExceptionsManagerModule;
import com.facebook.react.modules.core.JSTimersExecution;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.modules.core.Timing;
import com.facebook.react.modules.debug.SourceCodeModule;
import com.facebook.react.modules.intent.IntentModule;
import com.facebook.react.modules.location.LocationModule;
import com.facebook.react.modules.netinfo.NetInfoModule;
import com.facebook.react.modules.network.NetworkingModule;
import com.facebook.react.modules.storage.AsyncStorageModule;
import com.facebook.react.modules.systeminfo.AndroidInfoModule;
import com.facebook.react.modules.vibration.VibrationModule;
import com.facebook.react.modules.websocket.WebSocketModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseReactPackage implements ReactPackage {

    private final ReactInstanceManager reactInstanceManager;

    public BaseReactPackage(ReactInstanceManager reactInstanceManager) {
        this.reactInstanceManager = reactInstanceManager;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext catalystApplicationContext) {
        return Arrays.<NativeModule>asList(
                // Core list
                new AndroidInfoModule(),
                new ExceptionsManagerModule(reactInstanceManager.getDevSupportManager()),
                new Timing(catalystApplicationContext, reactInstanceManager.getDevSupportManager()),
                new UIManagerStubModule(catalystApplicationContext),
                new SourceCodeModule(reactInstanceManager.getSourceUrl()),
                new JSCHeapCapture(catalystApplicationContext),

                // Main list
                new AsyncStorageModule(catalystApplicationContext),
                new IntentModule(catalystApplicationContext),
                new LocationModule(catalystApplicationContext),
                new NetworkingModule(catalystApplicationContext),
                new NetInfoModule(catalystApplicationContext),
                new VibrationModule(catalystApplicationContext),
                new WebSocketModule(catalystApplicationContext),
                new WorkerSelfModule(catalystApplicationContext)
        );
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Arrays.asList(
                DeviceEventManagerModule.RCTDeviceEventEmitter.class,
                JSTimersExecution.class,
                RCTEventEmitter.class,
                RCTNativeAppEventEmitter.class,
                com.facebook.react.bridge.Systrace.class,
                JSCHeapCapture.HeapCapture.class
        );
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return new ArrayList<>(0);
    }
}
