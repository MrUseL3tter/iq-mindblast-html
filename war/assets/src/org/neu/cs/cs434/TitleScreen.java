package org.neu.cs.cs434;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.noobs2d.tweenengine.utils.DynamicScreen;
import com.noobs2d.tweenengine.utils.DynamicSprite;
import com.noobs2d.tweenengine.utils.DynamicText;

/**
 * The ugly screen that says "CLICK ANYWHERE TO PLAY", with the game logo and all that.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class TitleScreen extends DynamicScreen {

    DynamicSprite title;

    DynamicText prompt;

    /**
     * Default constructor.
     * 
     * @param game Reference to the Game object; needed mostly for screen transitions.
     */
    public TitleScreen(Game game) {
	super(game, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
	title = new DynamicSprite(new TextureRegion(Assets.title, 6, 680, 446, 330), Settings.SCREEN_WIDTH / 2, 450);
	title.scale.set(1.5f, 1.5f);

	prompt = new DynamicText(new BitmapFont(Fonts.computerFont24, new TextureRegion(Assets.computerFont24), false), "CLICK ANYWHERE ON SCREEN TO PLAY");
	prompt.position.set(Settings.SCREEN_WIDTH / 2, 150);
	prompt.interpolateAlpha(0.12f, Linear.INOUT, 500, false).repeat(Tween.INFINITY, 500);
	prompt.tween.start(prompt.tweenManager);
	prompt.interpolateAlpha(1f, Linear.INOUT, 500, false).repeat(Tween.INFINITY, 500).delay(500); // repeat this tween FOREVER
	prompt.tween.start(prompt.tweenManager);

	Assets.titleMusic.setLooping(true);
	Assets.titleMusic.play();
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchUp(float, float, int, int)
     */
    @Override
    public void onTouchUp(float x, float y, int pointer, int button) {
	super.onTouchUp(x, y, pointer, button);
	// the player clicked anywhere on the screen; start the game!
	game.setScreen(new CategorySelectionScreen(game));
	Assets.titleMusic.stop();
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#render(float)
     */
    @Override
    public void render(float deltaTime) {
	spriteBatch.begin();
	title.render(spriteBatch);
	prompt.render(spriteBatch);
	prompt.update(deltaTime);
	spriteBatch.end();
    }

}
