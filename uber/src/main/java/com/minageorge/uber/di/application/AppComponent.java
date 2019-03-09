package com.minageorge.uber.di.application;

import com.minageorge.uber.UberApplication;
import com.minageorge.uber.di.activity.ActivityComponent;
import com.minageorge.uber.di.activity.ActivityModule;
import com.minageorge.uber.di.scope.ApplicationScope;

import dagger.Component;

/**
 * This interface is used by dagger to generate the code that defines the connection between the provider of objects
 * (i.e. {@link AppModule}), and the object which expresses a dependency.
 */

@ApplicationScope
@Component(modules = {AppModule.class})
public interface AppComponent {

    ActivityComponent plus(ActivityModule activityModule);

    void inject(UberApplication uberApplication);
}