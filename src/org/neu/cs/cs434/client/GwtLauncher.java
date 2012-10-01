package org.neu.cs.cs434.client;

import org.neu.cs.cs434.IQMindBlast;
import org.neu.cs.cs434.Settings;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {

    @Override
    public ApplicationListener getApplicationListener() {
	return new IQMindBlast();
    }

    @Override
    public GwtApplicationConfiguration getConfig() {
	GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
	return cfg;
    }
}