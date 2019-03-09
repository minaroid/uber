package com.minageorge.uber;

import android.app.Application;
import android.content.Context;

import com.minageorge.uber.di.application.AppComponent;
import com.minageorge.uber.di.application.AppModule;
import com.minageorge.uber.di.application.DaggerAppComponent;

public class UberApplication extends Application {

    private static Context context;
    private final AppComponent appComponent = createAppComponent();

    public static AppComponent getComponent(Context context) {
        return getApp(context).appComponent;
    }

    //This is a hack to get a non-static field from a static method (i.e. appComponent)
    private static UberApplication getApp(Context context) {
        return (UberApplication) context.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    private AppComponent createAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

}
