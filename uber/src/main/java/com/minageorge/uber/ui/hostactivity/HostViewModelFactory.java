package com.minageorge.uber.ui.hostactivity;

import com.minageorge.uber.di.qualifier.ForApplication;
import com.minageorge.uber.di.scope.ActivityScope;
import com.minageorge.uber.store.room.UberRoomStore;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

@ActivityScope
class HostViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final UberRoomStore uberRoomStore;

    @Inject
    HostViewModelFactory(@ForApplication UberRoomStore uberRoomStore) {
        this.uberRoomStore = uberRoomStore;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HostViewModel(uberRoomStore);
    }
}
