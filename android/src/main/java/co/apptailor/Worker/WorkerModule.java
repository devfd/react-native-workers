package co.apptailor.Worker;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.devsupport.DevInternalSettings;
import com.facebook.react.devsupport.DevServerHelper;

import java.io.File;
import java.util.HashMap;

import co.apptailor.Worker.core.StubDevSupportManager;

public class WorkerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private String TAG = "WorkerModule";

    private ReactApplicationContext context;

    private HashMap<Integer, JSWorker> workers;
    private DevServerHelper devServerHelper;

    public WorkerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        this.workers = new HashMap<>();
        context.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "RNWorker";
    }

    @ReactMethod
    public void startWorker(String bundleName, final Promise promise) {
        if (devServerHelper == null) {
            DevInternalSettings devInternalSettings = new DevInternalSettings(context, new StubDevSupportManager());
            devInternalSettings.setHotModuleReplacementEnabled(false);
            devInternalSettings.setElementInspectorEnabled(false);
            devInternalSettings.setReloadOnJSChangeEnabled(false);

            devServerHelper = new DevServerHelper(devInternalSettings);
        }

        String bundleSlug = bundleName;
        if (bundleName.contains("/")) {
            bundleSlug = bundleName.replaceAll("/", "_");
        }

        final File bundleFile = new File(context.getFilesDir(), bundleSlug);

        final JSWorker worker = new JSWorker(bundleName, devServerHelper.getSourceUrl(bundleName),bundleFile.getAbsolutePath());

        devServerHelper.downloadBundleFromURL(new DevServerHelper.BundleDownloadCallback() {
            @Override
            public void onSuccess() {
                Activity activity = getCurrentActivity();
                if (activity == null) {
                    Log.d(TAG, "Worker startWorker - activity is null. aborting.");
                    return;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            worker.runFromContext(context);
                            workers.put(worker.getWorkerId(), worker);
                            promise.resolve(worker.getWorkerId());
                        } catch (Exception e) {
                            e.printStackTrace();
                            promise.reject(e);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception cause) {
                promise.reject(cause);
            }
        }, bundleName, bundleFile);
    }

    @ReactMethod
    public void stopWorker(final int workerId) {
        final JSWorker worker = workers.get(workerId);
        if (worker == null) {
            Log.d(TAG, "Cannot stop worker - worker is null for id " + workerId);
            return;
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                worker.terminate();
                workers.remove(workerId);
            }
        });
    }

    @ReactMethod
    public void postWorkerMessage(int workerId, String message) {
        JSWorker worker = workers.get(workerId);
        if (worker == null) {
            Log.d(TAG, "Cannot post message to worker - worker is null for id " + workerId);
            return;
        }

        worker.postMessage(message);
    }

    @ReactMethod
    public void startService() {
        Activity activity = getCurrentActivity();
        if (activity == null) { return; }
        Intent intent = new Intent(activity, JSService.class);
        activity.startService(intent);
    }

    @Override
    public void onHostResume() {}

    @Override
    public void onHostPause() {}

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "Clean JS Workers");

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for (int workerId : workers.keySet()) {
                    workers.get(workerId).terminate();
                }
            }
        });
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        onHostDestroy();
    }
}
