package co.apptailor.Worker;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.devsupport.DevSupportManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import co.apptailor.Worker.core.BaseReactPackage;
import co.apptailor.Worker.core.ReactContextBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Okio;
import okio.Sink;


public class WorkerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private String TAG = "WorkerManager";
    private HashMap<Integer, JSWorker> workers;

    private ReactPackage additionalWorkerPackages[];

    public WorkerModule(final ReactApplicationContext reactContext, ReactPackage additionalWorkerPackages[]) {
        super(reactContext);
        workers = new HashMap<>();
        this.additionalWorkerPackages = additionalWorkerPackages;
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "WorkerManager";
    }

    @ReactMethod
    public void startWorker(final String jsFileName, final Promise promise) {
        Log.d(TAG, "Starting web worker - " + jsFileName);

        String jsFileSlug = jsFileName.contains("/") ? jsFileName.replaceAll("/", "_") : jsFileName;

        JSBundleLoader bundleLoader = getDevSupportManager().getDevSupportEnabled()
                ? createDevBundleLoader(jsFileName, jsFileSlug)
                : createReleaseBundleLoader(jsFileName, jsFileSlug);

        try {
            ArrayList<ReactPackage> workerPackages = new ArrayList<ReactPackage>(Arrays.asList(additionalWorkerPackages));
            workerPackages.add(0, new BaseReactPackage(getReactInstanceManager()));

            ReactContextBuilder workerContextBuilder = new ReactContextBuilder(getReactApplicationContext())
                    .setJSBundleLoader(bundleLoader)
                    .setDevSupportManager(getDevSupportManager())
                    .setReactPackages(workerPackages);

            JSWorker worker = new JSWorker(jsFileSlug);
            worker.runFromContext(
                    getReactApplicationContext(),
                    workerContextBuilder
            );
            workers.put(worker.getWorkerId(), worker);
            promise.resolve(worker.getWorkerId());
        } catch (Exception e) {
            promise.reject(e);
            getDevSupportManager().handleException(e);
        }
    }

    @ReactMethod
    public void stopWorker(final int workerId) {
        final JSWorker worker = workers.get(workerId);
        if (worker == null) {
            Log.d(TAG, "Cannot stop worker - worker is null for id " + workerId);
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
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
//        Activity activity = getCurrentActivity();
//        if (activity == null) { return; }
//        Intent intent = new Intent(activity, JSService.class);
//        activity.startService(intent);
    }

    @Override
    public void onHostResume() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (int workerId : workers.keySet()) {
                    workers.get(workerId).onHostResume();
                }
            }
        });
    }

    @Override
    public void onHostPause() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (int workerId : workers.keySet()) {
                    workers.get(workerId).onHostPause();
                }
            }
        });
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "onHostDestroy - Clean JS Workers");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
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

    /*
     *  Helper methods
     */

    private JSBundleLoader createDevBundleLoader(String jsFileName, String jsFileSlug) {
        String bundleUrl = bundleUrlForFile(jsFileName);
        String bundleOut = getReactApplicationContext().getFilesDir().getAbsolutePath() + "/" + jsFileSlug;

        Log.d(TAG, "createDevBundleLoader - download web worker to - " + bundleOut);
        downloadScriptToFileSync(bundleUrl, bundleOut);

        return JSBundleLoader.createCachedBundleFromNetworkLoader(bundleUrl, bundleOut);
    }

    private JSBundleLoader createReleaseBundleLoader(String jsFileName, String jsFileSlug) {
        Log.d(TAG, "createReleaseBundleLoader - reading file from assets");
        return JSBundleLoader.createFileLoader(getReactApplicationContext(), "assets://workers/" + jsFileSlug + ".bundle");
    }

    private ReactInstanceManager getReactInstanceManager() {
        ReactApplication reactApplication = (ReactApplication)getCurrentActivity().getApplication();
        return reactApplication.getReactNativeHost().getReactInstanceManager();
    }

    private DevSupportManager getDevSupportManager() {
        return getReactInstanceManager().getDevSupportManager();
    }

    private String bundleUrlForFile(final String fileName) {
        // http://localhost:8081/index.android.bundle?platform=android&dev=true&hot=false&minify=false
        String sourceUrl = getDevSupportManager().getSourceUrl().replace("http://", "");
        return  "http://"
                + sourceUrl.split("/")[0]
                + "/"
                + fileName
                + ".bundle?platform=android&dev=true&hot=false&minify=false";
    }

    private void downloadScriptToFileSync(String bundleUrl, String bundleOut) {
        OkHttpClient client = new OkHttpClient();
        final File out = new File(bundleOut);

        Request request = new Request.Builder()
                .url(bundleUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error downloading web worker script - " + response.toString());
            }

            Sink output = Okio.sink(out);
            Okio.buffer(response.body().source()).readAll(output);
        } catch (IOException e) {
            throw new RuntimeException("Exception downloading web worker script to file", e);
        }
    }
}
