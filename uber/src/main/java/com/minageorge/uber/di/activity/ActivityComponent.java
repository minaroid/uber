package com.minageorge.uber.di.activity;

import com.minageorge.uber.di.scope.ActivityScope;
import com.minageorge.uber.ui.hostactivity.HostActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(HostActivity hostActivity);
}