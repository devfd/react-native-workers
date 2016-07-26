//package co.apptailor.Worker;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.common.ApplicationHolder;
//import com.facebook.react.devsupport.DevInternalSettings;
//import com.facebook.react.devsupport.DevServerHelper;
//import com.facebook.soloader.SoLoader;
//
//import java.io.File;
//
//public class JSService extends Service {
//    private static final String TAG = "JSService";
//    private JSWorker worker;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        SoLoader.init(this, /* native exopackage */ false);
////        try {
////            ApplicationHolder.getApplication();
////        }
////        catch (AssertionError err) {
////            ApplicationHolder.setApplication(getApplication());
////        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        clean();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "Starting background JS Service");
//
//        DevInternalSettings devInternalSettings = new DevInternalSettings(this, new StubDevSupportManager());
//        devInternalSettings.setHotModuleReplacementEnabled(false);
//        devInternalSettings.setElementInspectorEnabled(false);
//        devInternalSettings.setReloadOnJSChangeEnabled(false);
//
//        DevServerHelper devServerHelper = new DevServerHelper(devInternalSettings);
//
//        String bundleName = "src/service.bundle";
//        String bundleSlug = bundleName.replaceAll("/", "_");
//
//        final File bundleFile = new File(this.getFilesDir(), bundleSlug);
//        worker = new JSWorker(bundleName, devServerHelper.getSourceUrl(bundleName), bundleFile.getAbsolutePath());
//
//        final Handler mainHandler = new Handler(Looper.getMainLooper());
//        final ReactApplicationContext context = new ReactApplicationContext(getApplicationContext());
//
//        devServerHelper.downloadBundleFromURL(new DevServerHelper.BundleDownloadCallback() {
//            @Override
//            public void onSuccess() {
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            worker.runFromContext(context);
//                        } catch (Exception e) {
//                            Log.d(TAG, "Error while running service bundle");
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Exception cause) {
//                Log.d(TAG, "Error while downloading service bundle");
//                cause.printStackTrace();
//            }
//        }, bundleName, bundleFile);
//
//        return Service.START_STICKY;
//    }
//
//    private void clean() {
//        if (worker != null) {
//            worker.terminate();
//            worker = null;
//        }
//    }
//}
