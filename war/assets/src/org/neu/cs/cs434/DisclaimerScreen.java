package org.neu.cs.cs434;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.noobs2d.tweenengine.utils.DynamicScreen;
import com.noobs2d.tweenengine.utils.DynamicText;

/**
 * First screen shown to the user saying something about the disclaimers and all that.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class DisclaimerScreen extends DynamicScreen implements TweenCallback {

    DynamicText disclaimer;

    /**
     * Default constructor.
     * 
     * @param game Reference to the Game object; needed mostly for screen transitions.
     */
    public DisclaimerScreen(Game game) {
	super(game, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
	disclaimer = new DynamicText(new BitmapFont(Fonts.computerFont24, new TextureRegion(Assets.computerFont24), false), "");
	disclaimer.position.set(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	disclaimer.text = "DISCLAIMER\n\n\n\n\nALL RESOURCES USED IN THIS GAME BELONG\n TO THEIR RESPECTIVE OWNERS.\n\nDEVELOPED AS A REQUIREMENT TO THE COURSE\nCS434: SOFTWARE ENGINEERING\nUNDER ENGR. JEREMIAS ESPERANZA, A.Y. 2012-2013.\n\n\n\n\n\nYEP, IT'S ALPHA VERSION AND CRAPPY. WHATEVER. SEND YOUR COMPLAINTS AT TEAM MINDBLAST - teammindblast@gmail.com\n\nNO RIGHTS RESERVED 2012";
	disclaimer.color.a = 0f;
	disclaimer.interpolateAlpha(1f, Linear.INOUT, 500, true);
	disclaimer.interpolateAlpha(0f, Linear.INOUT, 150, true).delay(15000);
	disclaimer.tween.setCallback(this);
	disclaimer.tween.setCallbackTriggers(COMPLETE);
    }

    /*
     * (non-Javadoc)
     * @see aurelienribon.tweenengine.TweenCallback#onEvent(int,
     * aurelienribon.tweenengine.BaseTween)
     */
    @Override
    public void onEvent(int type, BaseTween<?> source) {
	game.setScreen(new TitleScreen(game));
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchDown(float, float, int, int)
     */
    @Override
    public void onTouchDown(float x, float y, int pointer, int button) {
	super.onTouchDown(x, y, pointer, button);
	// the user tapped the screen; skip the disclaimer screen
	disclaimer.tweenManager.killAll();
	disclaimer.interpolateAlpha(0f, Linear.INOUT, 500, true);
	disclaimer.tween.setCallback(this);
	disclaimer.tween.setCallbackTriggers(COMPLETE);
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#render(float)
     */
    @Override
    public void render(float deltaTime) {
	spriteBatch.begin();
	disclaimer.render(spriteBatch);
	disclaimer.update(deltaTime);
	spriteBatch.end();
    }

}
