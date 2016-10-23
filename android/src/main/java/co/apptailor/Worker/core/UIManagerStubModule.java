package co.apptailor.Worker.core;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class UIManagerStubModule extends ReactContextBaseJavaModule {

    public UIManagerStubModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    @Override
    public String getName() {
        return "UIManager";
    }
}
