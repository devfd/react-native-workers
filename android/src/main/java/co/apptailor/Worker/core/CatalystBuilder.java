package co.apptailor.Worker.core;

import android.content.Context;

import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.CatalystInstanceImpl;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.JSCJavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.JavaScriptModulesConfig;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.NativeModuleRegistry;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.queue.ReactQueueConfigurationSpec;
import com.facebook.soloader.SoLoader;

public class CatalystBuilder {

    private String bundleName;
    private Context parentContext;
    private BaseModuleList moduleList;
    private String sourceURL;
    private String filePath;

    public CatalystBuilder(Context context) {
        this.parentContext = context;
        SoLoader.init(context, /* native exopackage */ false);
    }

    public CatalystBuilder setBundleName(String bundleName) {
        this.bundleName = bundleName;
        return this;
    }

    public CatalystBuilder setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
        return this;
    }

    public CatalystBuilder setBundleFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public CatalystBuilder setModuleList(BaseModuleList moduleList) {
        this.moduleList = moduleList;
        return this;
    }

    public CatalystInstance build() throws Exception {
        JSCJavaScriptExecutor.Factory factory = new JSCJavaScriptExecutor.Factory();

//        JSBundleLoader bundleLoader = JSBundleLoader.createFileLoader(parentContext, bundleName);
        JSBundleLoader bundleLoader = JSBundleLoader.createCachedBundleFromNetworkLoader(sourceURL, filePath);

        JavaScriptExecutor jsExecutor = factory.create(new WritableNativeMap());

        ReactApplicationContext reactContext = new ReactApplicationContext(parentContext);

        NativeModuleRegistry.Builder nativeRegistryBuilder = new NativeModuleRegistry.Builder();
        addNativeModules(reactContext, nativeRegistryBuilder);

        JavaScriptModulesConfig.Builder jsModulesBuilder = new JavaScriptModulesConfig.Builder();
        addJSModules(jsModulesBuilder);

        NativeModuleRegistry nativeModuleRegistry = nativeRegistryBuilder.build();
        JavaScriptModulesConfig javaScriptModulesConfig = jsModulesBuilder.build();

        CatalystInstanceImpl.Builder catalystInstanceBuilder = new CatalystInstanceImpl.Builder()
                .setReactQueueConfigurationSpec(ReactQueueConfigurationSpec.createDefault())
                .setJSExecutor(jsExecutor)
                .setRegistry(nativeModuleRegistry)
                .setJSModulesConfig(javaScriptModulesConfig)
                .setJSBundleLoader(bundleLoader)
                .setNativeModuleCallExceptionHandler(new NativeModuleCallExceptionHandler() {
                    @Override
                    public void handleException(Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        CatalystInstance catalystInstance = catalystInstanceBuilder.build();
        reactContext.initializeWithInstance(catalystInstance);
        catalystInstance.runJSBundle();
        catalystInstance.initialize();
        reactContext.onHostResume(null);

        return catalystInstance;
    }

    private void addJSModules(JavaScriptModulesConfig.Builder jsModulesBuilder) {
        for (Class<? extends JavaScriptModule> jsModuleClass : moduleList.jsModules()) {
            jsModulesBuilder.add(jsModuleClass);
        }
    }

    private void addNativeModules(ReactApplicationContext reactContext, NativeModuleRegistry.Builder nativeRegistryBuilder) {
        for (NativeModule nativeModule : moduleList.nativeModules(reactContext)) {
            nativeRegistryBuilder.add(nativeModule);
        }
    }
}
