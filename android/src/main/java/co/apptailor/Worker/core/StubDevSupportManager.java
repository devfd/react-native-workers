package co.apptailor.Worker.core;

import android.util.Log;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.devsupport.DevOptionHandler;
import com.facebook.react.devsupport.DevServerHelper;
import com.facebook.react.devsupport.DevSupportManager;
import com.facebook.react.modules.debug.DeveloperSettings;

public class StubDevSupportManager implements DevSupportManager {
    private final String TAG = "WorkerDev";

    @Override
    public void showNewJavaError(String message, Throwable e) {
        Log.d(TAG, message);
        e.printStackTrace();
    }

    @Override
    public void addCustomDevOption(String optionName, DevOptionHandler optionHandler) {

    }

    @Override
    public void showNewJSError(String message, ReadableArray details, int errorCookie) {
        Log.d(TAG, message);
    }

    @Override
    public void updateJSError(String message, ReadableArray details, int errorCookie) {

    }

    @Override
    public void hideRedboxDialog() {

    }

    @Override
    public void showDevOptionsDialog() {

    }

    @Override
    public void setDevSupportEnabled(boolean isDevSupportEnabled) {

    }

    @Override
    public boolean getDevSupportEnabled() {
        return false;
    }

    @Override
    public DeveloperSettings getDevSettings() {
        return null;
    }

    @Override
    public void onNewReactContextCreated(ReactContext reactContext) {

    }

    @Override
    public void onReactInstanceDestroyed(ReactContext reactContext) {

    }

    @Override
    public String getSourceMapUrl() {
        return null;
    }

    @Override
    public String getSourceUrl() {
        return null;
    }

    @Override
    public String getJSBundleURLForRemoteDebugging() {
        return null;
    }

    @Override
    public String getDownloadedJSBundleFile() {
        return null;
    }

    @Override
    public boolean hasUpToDateJSBundleInCache() {
        return false;
    }

    @Override
    public void reloadSettings() {

    }

    @Override
    public void handleReloadJS() {

    }

    @Override
    public void isPackagerRunning(DevServerHelper.PackagerStatusCallback callback) {

    }

    @Override
    public void handleException(Exception e) {

    }
}
