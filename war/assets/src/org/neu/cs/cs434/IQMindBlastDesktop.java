package org.neu.cs.cs434;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class IQMindBlastDesktop {

    public static void main(String[] args) {
	new LwjglApplication(new IQMindBlast(), "IQ Mind Blast!", Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, false);
    }

}
