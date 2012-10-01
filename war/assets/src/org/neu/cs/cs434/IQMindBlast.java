package org.neu.cs.cs434;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.noobs2d.tweenengine.utils.DynamicDisplay;

/**
 * The class that must be created an instance for a platform version (desktop, HTML, or android).
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class IQMindBlast extends Game {

    SpriteBatch spriteBatch;
    TextureRegion background;

    /**
     * Invoked FIRST.
     */
    @Override
    public void create() {
	Assets.load();
	Fonts.load();
	DynamicDisplay.register();
	setScreen(new DisclaimerScreen(this));
	spriteBatch = new SpriteBatch();
	background = new TextureRegion(Assets.background, 0, 0, 760, 680);
    }

    /**
     * Repeatedly invoked. Renders our current screen.
     */
    @Override
    public void render() {
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // clear the screen

	spriteBatch.begin();
	if (!(getScreen() instanceof DisclaimerScreen)) // if the current screen is DisclaimerScreen, don't show the background
	    spriteBatch.draw(background, 0, 0);
	spriteBatch.end();
	super.render(); // renders our screen
    }

}
