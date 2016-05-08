package co.apptailor.Worker;

import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Random;

import co.apptailor.Worker.core.CatalystBuilder;
import co.apptailor.Worker.core.BaseModuleList;
import co.apptailor.Worker.core.WorkerSelfModule;

public class JSWorker {
    private int id;
    private String bundleName;
    private String sourceURL;
    private String filePath;
    private CatalystInstance catalystInstance;

    public JSWorker(String bundleName, String sourceURL, String filePath) {
        this.id = Math.abs(new Random().nextInt());
        this.bundleName = bundleName;
        this.filePath = filePath;
        this.sourceURL = sourceURL;
    }

    public int getWorkerId() {
        return this.id;
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void runFromContext(ReactApplicationContext context) throws Exception {
        terminate();

        catalystInstance = new CatalystBuilder(context)
                .setSourceURL(sourceURL)
                .setBundleFilePath(filePath)
                .setModuleList(new BaseModuleList())
                .build();

        WorkerSelfModule workerSelfModule = catalystInstance.getNativeModule(WorkerSelfModule.class);

        if (workerSelfModule == null) {
            throw new RuntimeException("Missing required WorkerSelfModule");
        }

        workerSelfModule.initialize(id, context);
    }

    public void postMessage(String message) {
        catalystInstance.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("WorkerMessage", message);
    }

    public void terminate() {
        if (catalystInstance == null) {
            return;
        }

        catalystInstance.destroy();
        catalystInstance = null;
    }
}
