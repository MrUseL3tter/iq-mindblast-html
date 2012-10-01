package org.neu.cs.cs434;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Sine;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.noobs2d.tweenengine.utils.DynamicButton;
import com.noobs2d.tweenengine.utils.DynamicButton.DynamicButtonCallback;
import com.noobs2d.tweenengine.utils.DynamicButton.State;
import com.noobs2d.tweenengine.utils.DynamicDisplay.DynamicRegistration;
import com.noobs2d.tweenengine.utils.DynamicScreen;
import com.noobs2d.tweenengine.utils.DynamicSprite;
import com.noobs2d.tweenengine.utils.DynamicText;
import com.noobs2d.tweenengine.utils.DynamicValue;

/**
 * The prompt that appears after finishing a game session, showing that session's statistics.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class ResultScreen extends DynamicScreen implements TweenCallback, DynamicButtonCallback {

    DynamicButton playAgain;
    DynamicButton postToTimeline;
    DynamicButton titleScreen;

    DynamicSprite panel;
    DynamicSprite black;

    DynamicText speedText;
    DynamicText speedMultiplier;
    DynamicValue speedValue;
    DynamicText accuracyText;
    DynamicText accuracyMultiplier;
    DynamicValue accuracyValue;
    DynamicText streakText;
    DynamicText streakMultiplier;
    DynamicValue streakValue;
    DynamicText totalScoreText;
    DynamicValue totalScoreValue;

    StageScreen previousScreen;

    /**
     * Default constructor.
     * 
     * @param game Reference to the Game object; needed mostly for screen transitions.
     * @param previousScreen Will be rendered in the background and the source for the game session
     *            to be evaluated.
     */
    public ResultScreen(Game game, StageScreen previousScreen) {
	super(game, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
	this.previousScreen = previousScreen;
	this.previousScreen.paused = true;
	initButtons();
	initPanel();
	initValues();
	interpolateBegin();
	Assets.resultMusic.setLooping(true);
	Assets.resultMusic.play();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.noobs2d.tweenengine.utils.DynamicButton.DynamicButtonCallback#onButtonEvent(com.noobs2d
     * .tweenengine.utils.DynamicButton, int)
     */
    @Override
    public void onButtonEvent(DynamicButton button, int eventType) {
	switch (eventType) {
	    case UP:
		if (button.equals(playAgain) && playAgain.state == State.DOWN) {
		    button.state = State.HOVER;
		    game.setScreen(new CategorySelectionScreen(game));
		    Assets.resultMusic.stop();
		} else if (button.equals(titleScreen) && titleScreen.state == State.DOWN) {
		    game.setScreen(new TitleScreen(game));
		    button.state = State.HOVER;
		    Assets.resultMusic.stop();
		} else if (button.equals(postToTimeline) && postToTimeline.state == State.DOWN) {
		    button.state = State.HOVER;
		    if (Gdx.app.getType() == ApplicationType.WebGL)
			Facebook.publishFeed((int) totalScoreValue.value);
		}
		break;
	    case HOVER:
		button.state = State.HOVER;
		Assets.hover.play();
		break;
	    case DOWN:
		button.state = State.DOWN;
		Assets.click.play();
		break;
	}

    }

    /*
     * (non-Javadoc)
     * @see aurelienribon.tweenengine.TweenCallback#onEvent(int,
     * aurelienribon.tweenengine.BaseTween)
     */
    @Override
    public void onEvent(int type, BaseTween<?> source) {
	if (type == COMPLETE && source.equals(totalScoreValue.tween)) {
	    // 'computation' of score is done, move the panel quite up after 500 ms.
	    speedText.interpolateY(speedText.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    speedMultiplier.interpolateY(speedMultiplier.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    accuracyText.interpolateY(accuracyText.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    accuracyMultiplier.interpolateY(accuracyMultiplier.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    streakText.interpolateY(streakText.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    streakMultiplier.interpolateY(streakMultiplier.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    totalScoreText.interpolateY(totalScoreText.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    panel.interpolateY(panel.position.y + 65, Linear.INOUT, 250, true).delay(500);
	    panel.tween.setCallback(this); // when this tween is complete, show the buttons
	    panel.tween.setCallbackTriggers(COMPLETE);
	    if (Gdx.app.getType() == ApplicationType.WebGL)
		Facebook.publishScore((int) totalScoreValue.value);
	} else if (type == COMPLETE && source.equals(panel.tween)) {
	    // the move-the-panel-quite-up tween is done; show the buttons
	    playAgain.interpolateScaleXY(new Vector2(1f, 1f), Back.OUT, 500, true);
	    postToTimeline.interpolateScaleXY(new Vector2(1f, 1f), Back.OUT, 500, true).delay(100);
	    titleScreen.interpolateScaleXY(new Vector2(1f, 1f), Back.OUT, 500, true).delay(150);
	}
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchDown(float, float, int, int)
     */
    @Override
    public void onTouchDown(float x, float y, int pointer, int button) {
	super.onTouchDown(x, y, pointer, button);
	playAgain.inputDown(x, Settings.SCREEN_HEIGHT - y);
	postToTimeline.inputDown(x, Settings.SCREEN_HEIGHT - y);
	titleScreen.inputDown(x, Settings.SCREEN_HEIGHT - y);
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchMove(float, float)
     */
    @Override
    public void onTouchMove(float x, float y) {
	super.onTouchMove(x, y);
	playAgain.inputMove(x, Settings.SCREEN_HEIGHT - y);
	postToTimeline.inputMove(x, Settings.SCREEN_HEIGHT - y);
	titleScreen.inputMove(x, Settings.SCREEN_HEIGHT - y);
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchUp(float, float, int, int)
     */
    @Override
    public void onTouchUp(float x, float y, int pointer, int button) {
	super.onTouchUp(x, y, pointer, button);
	playAgain.inputUp(x, Settings.SCREEN_HEIGHT - y);
	postToTimeline.inputUp(x, Settings.SCREEN_HEIGHT - y);
	titleScreen.inputUp(x, Settings.SCREEN_HEIGHT - y);
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#render(float)
     */
    @Override
    public void render(float deltaTime) {
	previousScreen.render(deltaTime);
	spriteBatch.begin();
	renderPanel(spriteBatch, deltaTime);
	renderValues(spriteBatch, deltaTime);
	renderButtons(spriteBatch, deltaTime);
	spriteBatch.end();
    }

    /**
     * Instantiate all buttons.
     */
    private void initButtons() {
	TextureRegion upstate = new TextureRegion(Assets.result, 710, 391, 221, 117);
	TextureRegion downstate = new TextureRegion(Assets.result, 710, 391, 221, 117);
	TextureRegion hoverstate = new TextureRegion(Assets.result, 710, 510, 243, 129);
	playAgain = new DynamicButton(upstate, hoverstate, downstate, new Vector2(380, 76));
	playAgain.scale.set(0, 0);
	playAgain.callback = this;

	upstate = new TextureRegion(Assets.result, 710, 144, 221, 117);
	downstate = new TextureRegion(Assets.result, 710, 144, 221, 117);
	hoverstate = new TextureRegion(Assets.result, 710, 262, 243, 129);
	postToTimeline = new DynamicButton(upstate, hoverstate, downstate, new Vector2(624, 76));
	postToTimeline.scale.set(0, 0);
	postToTimeline.callback = this;

	upstate = new TextureRegion(Assets.result, 0, 523, 221, 117);
	downstate = new TextureRegion(Assets.result, 0, 523, 221, 117);
	hoverstate = new TextureRegion(Assets.result, 221, 523, 243, 129);
	titleScreen = new DynamicButton(upstate, hoverstate, downstate, new Vector2(135, 76));
	titleScreen.scale.set(0, 0);
	titleScreen.callback = this;
    }

    /**
     * Instantiate the panel.
     */
    private void initPanel() {
	black = new DynamicSprite(new TextureRegion(Assets.result, 709, 0, 159, 142), Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	black.scale.set(35f, 35f);
	panel = new DynamicSprite(new TextureRegion(Assets.result, 0, 0, 709, 522), Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2 - Settings.SCREEN_HEIGHT);

    }

    /**
     * Instantiate the texts and the values variables.
     */
    private void initValues() {
	speedText = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "0", HAlignment.RIGHT);
	speedText.position.set(669, 447 - Settings.SCREEN_HEIGHT);
	speedText.scale.set(1.9f, 1.9f);
	speedText.registration = DynamicRegistration.RIGHT_CENTER;
	speedValue = new DynamicValue(0, previousScreen.session.questionsAnswered / 60.0f, 500, 500);
	speedMultiplier = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "x100");
	speedMultiplier.position.set(667, 419);
	speedMultiplier.scale.set(.75f, .75f);
	speedMultiplier.color.a = 0f;

	accuracyText = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "0", HAlignment.RIGHT);
	accuracyText.position.set(669, 378 - Settings.SCREEN_HEIGHT);
	accuracyText.scale.set(1.9f, 1.9f);
	accuracyText.registration = DynamicRegistration.RIGHT_CENTER;
	accuracyValue = new DynamicValue(0, previousScreen.session.correctAnswers / previousScreen.session.questionsTotal, 500, 1500);
	accuracyMultiplier = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "x100");
	accuracyMultiplier.position.set(667, 347);
	accuracyMultiplier.scale.set(.75f, .75f);
	accuracyMultiplier.color.a = 0f;

	streakText = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "0", HAlignment.RIGHT);
	streakText.position.set(669, 309 - Settings.SCREEN_HEIGHT);
	streakText.scale.set(1.9f, 1.9f);
	streakText.registration = DynamicRegistration.RIGHT_CENTER;
	streakValue = new DynamicValue(0, previousScreen.session.maxStreak, 500, 2500);
	streakMultiplier = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "x100");
	streakMultiplier.position.set(667, 277);
	streakMultiplier.scale.set(.75f, .75f);
	streakMultiplier.color.a = 0f;

	totalScoreText = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "" + previousScreen.session.score.value, HAlignment.RIGHT);
	totalScoreText.position.set(669, 166 - Settings.SCREEN_HEIGHT);
	totalScoreText.scale.set(1.9f, 1.9f);
	totalScoreText.registration = DynamicRegistration.RIGHT_CENTER;
	float targetValue = previousScreen.session.score.value + (previousScreen.session.questionsAnswered / 60.0f + previousScreen.session.correctAnswers / previousScreen.session.questionsTotal + previousScreen.session.maxStreak) * 100;
	totalScoreValue = new DynamicValue(previousScreen.session.score.value, targetValue, 500, 4500);
	totalScoreValue.tween.setCallback(this); // when this tween is complete, tween-y-up the panel and its contents
	totalScoreValue.tween.setCallbackTriggers(COMPLETE);
    }

    /**
     * Performs the interpolations in the beginning of the screen.
     */
    private void interpolateBegin() {
	panel.interpolateY(Settings.SCREEN_HEIGHT / 2, Sine.OUT, 500, true);

	// values
	speedText.interpolateY(speedText.position.y + Settings.SCREEN_HEIGHT, Sine.OUT, 500, true);
	speedMultiplier.interpolateX(speedMultiplier.position.x, Linear.INOUT, 150, true).delay(3500);
	speedMultiplier.interpolateAlpha(1f, Linear.INOUT, 150, true).delay(3500);
	speedMultiplier.position.x -= 25;
	accuracyText.interpolateY(accuracyText.position.y + Settings.SCREEN_HEIGHT, Sine.OUT, 500, true);
	accuracyMultiplier.interpolateX(accuracyMultiplier.position.x, Linear.INOUT, 150, true).delay(3500);
	accuracyMultiplier.interpolateAlpha(1f, Linear.INOUT, 150, true).delay(3500);
	accuracyMultiplier.position.x -= 25;
	streakText.interpolateY(streakText.position.y + Settings.SCREEN_HEIGHT, Sine.OUT, 500, true);
	streakMultiplier.interpolateX(streakMultiplier.position.x, Linear.INOUT, 150, true).delay(3500);
	streakMultiplier.interpolateAlpha(1f, Linear.INOUT, 150, true).delay(3500);
	streakMultiplier.position.x -= 25;
	totalScoreText.interpolateY(totalScoreText.position.y + Settings.SCREEN_HEIGHT, Sine.OUT, 500, true);
    }

    /**
     * Render and update all buttons.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderButtons(SpriteBatch spriteBatch, float deltaTime) {
	playAgain.render(spriteBatch);
	playAgain.update(deltaTime);
	postToTimeline.render(spriteBatch);
	postToTimeline.update(deltaTime);
	titleScreen.render(spriteBatch);
	titleScreen.update(deltaTime);

    }

    /**
     * Render and update the panel.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderPanel(SpriteBatch spriteBatch, float deltaTime) {
	black.render(spriteBatch);
	black.update(deltaTime);
	panel.render(spriteBatch);
	panel.update(deltaTime);

    }

    /**
     * Render and update all the texts and values.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderValues(SpriteBatch spriteBatch, float deltaTime) {
	speedText.render(spriteBatch);
	speedText.update(deltaTime);
	speedText.text = speedValue.value >= 10 ? Float.toString(speedValue.value).substring(0, 4) : Float.toString(speedValue.value).substring(0, 3);
	speedValue.update(deltaTime);
	speedMultiplier.render(spriteBatch);
	speedMultiplier.update(deltaTime);
	accuracyText.render(spriteBatch);
	accuracyText.update(deltaTime);
	accuracyText.text = accuracyValue.value >= 10 ? Float.toString(accuracyValue.value).substring(0, 4) : Float.toString(accuracyValue.value).substring(0, 3);
	accuracyValue.update(deltaTime);
	accuracyMultiplier.render(spriteBatch);
	accuracyMultiplier.update(deltaTime);
	streakText.render(spriteBatch);
	streakText.update(deltaTime);
	streakText.text = "" + (int) streakValue.value;
	streakValue.update(deltaTime);
	streakMultiplier.render(spriteBatch);
	streakMultiplier.update(deltaTime);
	totalScoreText.render(spriteBatch);
	totalScoreText.update(deltaTime);
	totalScoreText.text = "" + (int) totalScoreValue.value;
	totalScoreValue.update(deltaTime);
    }
}
