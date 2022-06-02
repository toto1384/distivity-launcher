package com.distivity.productivitylauncher;


import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AppExecutors {



    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    private static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public static void runOnBackgroundThread(Runnable runnable){
        AppExecutors.getInstance().diskIO.execute(runnable);
    }

    public static void runOnUiThread(Runnable runnable){
        AppExecutors.getInstance().mainThread.execute(runnable);
    }

    private Executor diskIO() {
        return diskIO;
    }

    private Executor mainThread() {
        return mainThread;
    }

    private Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

