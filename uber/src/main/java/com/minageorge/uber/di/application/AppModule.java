package com.minageorge.uber.di.application;

import android.app.Application;
import android.content.Context;

import com.minageorge.uber.UberApplication;
import com.minageorge.uber.di.qualifier.ForApplication;
import com.minageorge.uber.di.scope.ApplicationScope;
import com.minageorge.uber.store.room.UberRoomStore;
import com.minageorge.uber.utils.ResourcesUtil;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * This class is responsible for providing the requested objects to {@link ApplicationScope} annotated classes
 */

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @ApplicationScope
    @Provides
    Application providesApplication() {
        return application;
    }

    @ApplicationScope
    @Provides
    @ForApplication
    Context providesApplicationContext() {
        return application;
    }

    @ApplicationScope
    @Provides
    @ForApplication
    UberRoomStore providesRoomStoreContext() {
        return UberRoomStore.getAppDatabase(application);
    }

    @ApplicationScope
    @Provides
    ResourcesUtil providesResourcesUtil() {
        return new ResourcesUtil(UberApplication.getContext());
    }
}