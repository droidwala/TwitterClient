package com.example.punit.twitterclient.dagger.components;

import com.example.punit.twitterclient.dagger.modules.AppModule;
import com.example.punit.twitterclient.ui.MainActivity;
import com.example.punit.twitterclient.ui.TimelineActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);
    void inject(TimelineActivity timelineActivity);
}
