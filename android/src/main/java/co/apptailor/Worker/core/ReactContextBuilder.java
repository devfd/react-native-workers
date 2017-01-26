package co.apptailor.Worker.core;

import android.content.Context;

import com.facebook.react.EagerModuleProvider;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.cxxbridge.CatalystInstanceImpl;
import com.facebook.react.cxxbridge.JSBundleLoader;
import com.facebook.react.cxxbridge.JSCJavaScriptExecutor;
import com.facebook.react.cxxbridge.JavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.JavaScriptModuleRegistry;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.bridge.ModuleSpec;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.cxxbridge.NativeModuleRegistry;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.queue.ReactQueueConfigurationSpec;
import com.facebook.react.devsupport.DevSupportManager;
import com.facebook.soloader.SoLoader;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ReactContextBuilder {

    private Context parentContext;
    private JSBundleLoader jsBundleLoader;
    private DevSupportManager devSupportManager;
    private ArrayList<ReactPackage> reactPackages;

    public ReactContextBuilder(Context context) {
        this.parentContext = context;
        SoLoader.init(context, /* native exopackage */ false);
    }

    public ReactContextBuilder setJSBundleLoader(JSBundleLoader jsBundleLoader) {
        this.jsBundleLoader = jsBundleLoader;
        return this;
    }

    public ReactContextBuilder setDevSupportManager(DevSupportManager devSupportManager) {
        this.devSupportManager = devSupportManager;
        return this;
    }

    public ReactContextBuilder setReactPackages(ArrayList<ReactPackage> reactPackages) {
        this.reactPackages = reactPackages;
        return this;
    }

    public ReactApplicationContext build() throws Exception {
        //JavaScriptExecutor jsExecutor = new JSCJavaScriptExecutor.Factory().create(new WritableNativeMap());
        JavaScriptExecutor jsExecutor = new JSCJavaScriptExecutor.Factory(new WritableNativeMap()).create();

        // fresh new react context
        final ReactApplicationContext reactContext = new ReactApplicationContext(parentContext);
        if (devSupportManager != null) {
            reactContext.setNativeModuleCallExceptionHandler(devSupportManager);
        }

        // load native modules
        final List<ModuleSpec> mModuleSpecList = new ArrayList<>();
        //NativeModuleRegistry.Builder nativeRegistryBuilder = new NativeModuleRegistry.Builder();
        addNativeModules(reactContext,mModuleSpecList); //, nativeRegistryBuilder);

        // load js modules
        JavaScriptModuleRegistry.Builder jsModulesBuilder = new JavaScriptModuleRegistry.Builder();
        addJSModules(jsModulesBuilder);

        CatalystInstanceImpl.Builder catalystInstanceBuilder = new CatalystInstanceImpl.Builder()
                .setReactQueueConfigurationSpec(ReactQueueConfigurationSpec.createDefault())
                .setJSExecutor(jsExecutor)
                //.setRegistry(nativeRegistryBuilder.build())
                .setRegistry(new NativeModuleRegistry(
                  mModuleSpecList,
                  Collections.<Class, ReactModuleInfo>emptyMap()))
                .setJSModuleRegistry(jsModulesBuilder.build())
                .setJSBundleLoader(jsBundleLoader)
                .setNativeModuleCallExceptionHandler(devSupportManager != null
                        ? devSupportManager
                        : createNativeModuleExceptionHandler()
                );


        final CatalystInstance catalystInstance;
        catalystInstance = catalystInstanceBuilder.build();

        catalystInstance.getReactQueueConfiguration().getJSQueueThread().callOnQueue(
                new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        try {
                            reactContext.initializeWithInstance(catalystInstance);
                            catalystInstance.runJSBundle();
                        } catch (Exception e) {
                            e.printStackTrace();
                            devSupportManager.handleException(e);
                        }

                        return null;
                    }
                }
        ).get();

        catalystInstance.getReactQueueConfiguration().getUIQueueThread().callOnQueue(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    catalystInstance.initialize();
                    reactContext.onHostResume(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    devSupportManager.handleException(e);
                }

                return null;
            }
        }).get();

        return reactContext;
    }

    private NativeModuleCallExceptionHandler createNativeModuleExceptionHandler() {
        return new NativeModuleCallExceptionHandler() {
            @Override
            public void handleException(Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void addJSModules(JavaScriptModuleRegistry.Builder jsModulesBuilder) {
        for (int i = 0; i < reactPackages.size(); i++) {
            ReactPackage reactPackage = reactPackages.get(i);
            for (Class<? extends JavaScriptModule> jsModuleClass : reactPackage.createJSModules()) {
                jsModulesBuilder.add(jsModuleClass);
            }
        }
    }

    private void addNativeModules(ReactApplicationContext reactContext,List<ModuleSpec> mModuleSpecList) { //, NativeModuleRegistry.Builder nativeRegistryBuilder) {
        for (int i = 0; i < reactPackages.size(); i++) {
            ReactPackage reactPackage = reactPackages.get(i);
            for (NativeModule nativeModule : reactPackage.createNativeModules(reactContext)) {
                //nativeRegistryBuilder.add(nativeModule);
                ModuleSpec ms = new ModuleSpec(nativeModule.getClass(), new EagerModuleProvider(nativeModule));
                mModuleSpecList.add(ms);
            }
        }
    }
}
