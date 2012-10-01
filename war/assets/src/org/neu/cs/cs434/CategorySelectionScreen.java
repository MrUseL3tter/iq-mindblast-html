package org.neu.cs.cs434;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Sine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.noobs2d.tweenengine.utils.DynamicButton;
import com.noobs2d.tweenengine.utils.DynamicButton.DynamicButtonCallback;
import com.noobs2d.tweenengine.utils.DynamicButton.State;
import com.noobs2d.tweenengine.utils.DynamicScreen;
import com.noobs2d.tweenengine.utils.DynamicSprite;
import com.noobs2d.tweenengine.utils.DynamicText;

/**
 * Screen where players must pick a category; Science, Health, Geography, History, Math, or a random
 * of all of it.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class CategorySelectionScreen extends DynamicScreen implements TweenCallback, DynamicButtonCallback {

    // Instruction prompt in the middle of the screen
    DynamicText promptText;
    DynamicSprite promptFill;

    // Category buttons vars
    ArrayList<DynamicButton> selectables;
    DynamicButton science;
    DynamicButton health;
    DynamicButton geography;
    DynamicButton history;
    DynamicButton math;
    DynamicButton random;

    // Category backgrounds
    DynamicSprite scienceBG;
    DynamicSprite healthBG;
    DynamicSprite geographyBG;
    DynamicSprite historyBG;
    DynamicSprite mathBG;
    DynamicSprite randomBG;

    // Marquee vars
    ArrayList<DynamicSprite> marqueeTextsTop;
    ArrayList<DynamicSprite> marqueeTextsBottom;
    DynamicSprite marqueeFillTop;
    DynamicSprite marqueeFillBottom;

    // transition vars
    DynamicSprite left;
    DynamicSprite right;

    // The selected category. Will be set once the player picks one.
    int category;

    /**
     * Default constructor.
     * 
     * @param game Reference to the Game object; needed mostly for screen transitions.
     */
    public CategorySelectionScreen(Game game) {
	super(game, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
	initCategories();
	initMarquee();
	initPrompt();
	initTransition();
	interpolateBegin();

	Assets.categorySelectionMusic.setLooping(true);
	Assets.categorySelectionMusic.play();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.noobs2d.tweenengine.utils.DynamicButton.DynamicButtonCallback#onButtonEvent(com.noobs2d
     * .tweenengine.utils.DynamicButton, int)
     */
    @Override
    public void onButtonEvent(DynamicButton button, int eventType) {
	if (eventType == UP) {
	    for (int i = 0; i < selectables.size(); i++) {
		DynamicButton clicked = selectables.get(i);
		clicked.state = State.DOWN;
		if (!clicked.equals(button)) {
		    clicked.state = State.UP;

		    // for the screen transition, we should scale-down interpolate the category buttons except for the selected one. since it's
		    // tidy to identify which among the collection is the one selected, we just interpolate them right here instead on interpolateEnd().
		    clicked.interpolateScaleXY(new Vector2(0f, 0f), Linear.INOUT, 150, true).delay(500);
		} else
		    category = i; // the index of the categories is based on the values of categories in {@link Question}.
	    }

	    // create a new DynamicButton that looks like the clicked category button's downstate
	    // for the fade-out-up-scale effect
	    DynamicButton fade = new DynamicButton(button.upstate, button.hoverstate, button.downstate, new Vector2(button.position.x, button.position.y));
	    fade.interpolateAlpha(0f, Sine.OUT, 500, true);
	    fade.interpolateScaleXY(new Vector2(1.5f, 1.5f), Sine.OUT, 500, true);
	    fade.tween.setCallback(this);
	    fade.tween.setCallbackTriggers(COMPLETE);
	    selectables.add(fade);

	    // one of the category buttons was clicked; we may now disable them all so they may not be clicked again.
	    for (int i = 0; i < selectables.size(); i++)
		selectables.get(i).enabled = false;

	    Assets.click.play();
	} else if (eventType == HOVER && button.state == State.UP) {
	    button.state = State.HOVER;
	    Assets.hover.play();
	}
    }

    /*
     * (non-Javadoc)
     * @see aurelienribon.tweenengine.TweenCallback#onEvent(int,
     * aurelienribon.tweenengine.BaseTween)
     */
    @Override
    public void onEvent(int type, BaseTween<?> source) {
	// prompt animation is finished; enable the category buttons
	if (type == COMPLETE && source.equals(promptFill.tween))
	    for (int i = 0; i < selectables.size(); i++)
		selectables.get(i).enabled = true;
	else if (type == COMPLETE && source.equals(selectables.get(selectables.size() - 1).tween))
	    // the last index in the category buttons collection is the fade-out buff; once it ends, we invoke the method
	    // for the closing interpolations.
	    interpolateEnd();
	else if (type == COMPLETE && source.equals(right.tween)) {
	    // the transition-animation has ended; switch to the next screen.
	    game.setScreen(new StageScreen(game, category));
	    Assets.categorySelectionMusic.stop();
	}
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchDown(float, float, int, int)
     */
    @Override
    public void onTouchDown(float x, float y, int pointer, int button) {
	super.onTouchDown(x, y, pointer, button);
	for (int i = 0; i < selectables.size(); i++)
	    selectables.get(i).inputDown(x, Settings.SCREEN_HEIGHT - y);
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchMove(float, float)
     */
    @Override
    public void onTouchMove(float x, float y) {
	super.onTouchMove(x, y);
	for (int i = 0; i < selectables.size(); i++)
	    selectables.get(i).inputMove(x, Settings.SCREEN_HEIGHT - y);
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#onTouchUp(float, float, int, int)
     */
    @Override
    public void onTouchUp(float x, float y, int pointer, int button) {
	super.onTouchUp(x, y, pointer, button);
	for (int i = 0; i < selectables.size(); i++)
	    selectables.get(i).inputUp(x, Settings.SCREEN_HEIGHT - y);
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#render(float)
     */
    @Override
    public void render(float deltaTime) {
	super.render(deltaTime);
	spriteBatch.begin();
	renderCategories(spriteBatch, deltaTime);
	renderMarquee(spriteBatch, deltaTime);
	renderPromptBar(spriteBatch, deltaTime);
	renderTransition(spriteBatch, deltaTime);
	spriteBatch.end();
    }

    /**
     * Instantiate all displays related to the categories.
     */
    private void initCategories() {
	selectables = new ArrayList<DynamicButton>();

	TextureRegion upstate = new TextureRegion(Assets.categorySelection, 199, 0, 199, 160);
	TextureRegion downstate = new TextureRegion(Assets.categorySelection, 0, 0, 199, 160);
	TextureRegion hoverstate = new TextureRegion(Assets.categorySelection, 0, 0, 199, 160);
	science = new DynamicButton(upstate, hoverstate, downstate, new Vector2(149, 513));
	science.callback = this;
	selectables.add(science);

	upstate = new TextureRegion(Assets.categorySelection, 597, 0, 199, 160);
	downstate = new TextureRegion(Assets.categorySelection, 398, 0, 199, 160);
	hoverstate = new TextureRegion(Assets.categorySelection, 398, 0, 199, 160);
	health = new DynamicButton(upstate, hoverstate, downstate, new Vector2(363, 513));
	health.callback = this;
	selectables.add(health);

	upstate = new TextureRegion(Assets.categorySelection, 0, 160, 199, 160);
	downstate = new TextureRegion(Assets.categorySelection, 796, 0, 199, 160);
	hoverstate = new TextureRegion(Assets.categorySelection, 796, 0, 199, 160);
	geography = new DynamicButton(upstate, hoverstate, downstate, new Vector2(273, 340));
	geography.callback = this;
	selectables.add(geography);

	upstate = new TextureRegion(Assets.categorySelection, 398, 160, 199, 160);
	downstate = new TextureRegion(Assets.categorySelection, 199, 160, 199, 160);
	hoverstate = new TextureRegion(Assets.categorySelection, 199, 160, 199, 160);
	history = new DynamicButton(upstate, hoverstate, downstate, new Vector2(487, 340));
	history.callback = this;
	selectables.add(history);

	upstate = new TextureRegion(Assets.categorySelection, 796, 160, 199, 160);
	downstate = new TextureRegion(Assets.categorySelection, 597, 160, 199, 160);
	hoverstate = new TextureRegion(Assets.categorySelection, 597, 160, 199, 160);
	math = new DynamicButton(upstate, hoverstate, downstate, new Vector2(396, 167));
	math.callback = this;
	selectables.add(math);

	upstate = new TextureRegion(Assets.categorySelection, 199, 320, 199, 160);
	downstate = new TextureRegion(Assets.categorySelection, 0, 320, 199, 160);
	hoverstate = new TextureRegion(Assets.categorySelection, 0, 320, 199, 160);
	random = new DynamicButton(upstate, hoverstate, downstate, new Vector2(610, 167));
	random.callback = this;
	selectables.add(random);

	// all category buttons are disabled for the meantime until the prompt animation at the beginning ends.
	// the lines enabling them can be seen at onEvent(int, BaseTween);
	for (int i = 0; i < selectables.size(); i++)
	    selectables.get(i).enabled = false;
    }

    /**
     * Instantiate all displays related to the marquee info bar.
     */
    private void initMarquee() {
	marqueeTextsBottom = new ArrayList<DynamicSprite>();
	marqueeTextsTop = new ArrayList<DynamicSprite>();
	marqueeFillBottom = new DynamicSprite(new TextureRegion(Assets.categorySelection, 398, 320, 36, 36), 381, 58);
	marqueeFillBottom.scale.set(25f, 1f);
	marqueeFillTop = new DynamicSprite(new TextureRegion(Assets.categorySelection, 398, 320, 36, 36), 381, 622);
	marqueeFillTop.scale.set(25f, 1f);

	// Create 7 DynamicSprites for the bottom marquee
	final float width = 215f; // Width of a DynamicSprite with a text of "SELECT CATEGORY • ".
	for (int i = 0; i < 7; i++) {
	    DynamicSprite text = new DynamicSprite(new TextureRegion(Assets.categorySelection, 435, 321, 203, 15), 320, 58);
	    text.position.x = Settings.SCREEN_WIDTH + width * (i + 1); // Set the X outside the screen
	    text.interpolateX(text.position.x - 25000, Linear.INOUT, 100000, true); // left-wards interpolation
	    marqueeTextsBottom.add(text);
	}

	// Create 7 DynamicSprites for the top marquee
	for (int i = 0; i < 7; i++) {
	    DynamicSprite text = new DynamicSprite(new TextureRegion(Assets.categorySelection, 435, 321, 203, 15), 0, 622);
	    text.position.x = 0 - width * (i + 1); // Set the X outside the screen
	    text.interpolateX(text.position.x + 25000, Linear.INOUT, 100000, true); // right-wards interpolation
	    marqueeTextsTop.add(text);
	}
    }

    /**
     * Instantiate all displays related to the prompt that appears in the beginning.
     */
    private void initPrompt() {
	promptText = new DynamicText(new BitmapFont(Fonts.computerFont24, new TextureRegion(Assets.computerFont24), false), "SELECT A\nCATEGORY!");
	promptText.position.set(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	promptText.scale.set(3.5f, 3.5f);

	promptFill = new DynamicSprite(new TextureRegion(Assets.categorySelection, 398, 320, 36, 36), Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	promptFill.scale.set(25f, 7.5f);
    }

    /**
     * Instantiate all displays related to the transition animation.
     */
    private void initTransition() {
	left = new DynamicSprite(new TextureRegion(Assets.title, 0, 0, 380, 680), -(Settings.SCREEN_WIDTH / 2), Settings.SCREEN_HEIGHT / 2);
	right = new DynamicSprite(new TextureRegion(Assets.title, 381, 0, 379, 680), Settings.SCREEN_WIDTH + 340, Settings.SCREEN_HEIGHT / 2);
    }

    /**
     * Performs the interpolations in the beginning of the screen.
     */
    private void interpolateBegin() {
	// interpolate category buttons
	science.interpolateX(science.position.x, Expo.OUT, 1000, true).delay(250);
	science.position.x -= 1000;
	health.interpolateX(health.position.x, Expo.OUT, 1000, true).delay(150);
	health.position.x -= 1000;
	geography.interpolateX(geography.position.x, Expo.OUT, 1000, true);
	geography.position.x -= 1000;
	history.interpolateX(history.position.x, Expo.OUT, 1000, true);
	history.position.x += 1000;
	math.interpolateX(math.position.x, Expo.OUT, 1000, true).delay(150);
	math.position.x += 1000;
	random.interpolateX(random.position.x, Expo.OUT, 1000, true).delay(250);
	random.position.x += 1000;

	// interpolate prompt bar, appear
	promptFill.interpolateScaleY(promptFill.scale.y, Expo.IN, 500, true);
	promptFill.scale.y = 0f;
	promptText.interpolateX(promptText.position.x, Expo.IN, 500, true).delay(200);
	promptText.position.x += 1000;

	// interpolate prompt bar, disappear
	promptFill.interpolateScaleY(0f, Linear.INOUT, 150, true).delay(2500);
	promptFill.tween.setCallback(this);
	promptFill.tween.setCallbackTriggers(COMPLETE);
	promptText.interpolateX(promptText.position.x - 2000, Linear.INOUT, 150, true).delay(2200);
    }

    /**
     * Performs the interpolations for the closing of the screen.
     */
    private void interpolateEnd() {
	// We hide all the marquee texts and tween scale-y-down the marquee bars
	for (int i = 0; i < marqueeTextsBottom.size(); i++)
	    marqueeTextsBottom.get(i).visible = false;
	for (int i = 0; i < marqueeTextsTop.size(); i++)
	    marqueeTextsTop.get(i).visible = false;
	marqueeFillBottom.interpolateScaleY(0f, Expo.IN, 250, true);
	marqueeFillTop.interpolateScaleY(0f, Expo.IN, 250, true);
	left.interpolateX(190, Linear.INOUT, 250, true).delay(250);
	right.interpolateX(570, Linear.INOUT, 250, true).delay(250);
	right.tween.setCallback(this);
	right.tween.setCallbackTriggers(COMPLETE);
    }

    /**
     * Render and update all displays related to the categories.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderCategories(SpriteBatch spriteBatch, float deltaTime) {
	for (int i = 0; i < selectables.size(); i++) {
	    selectables.get(i).render(spriteBatch);
	    selectables.get(i).update(deltaTime);
	}
    }

    /**
     * Render and update all displays related to the marquee info bar.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderMarquee(SpriteBatch spriteBatch, float deltaTime) {
	marqueeFillBottom.render(spriteBatch);
	marqueeFillBottom.update(deltaTime);
	marqueeFillTop.render(spriteBatch);
	marqueeFillTop.update(deltaTime);

	for (int i = 0; i < marqueeTextsBottom.size(); i++) {
	    marqueeTextsBottom.get(i).render(spriteBatch);
	    marqueeTextsBottom.get(i).update(deltaTime);
	    // If the current DynamicSprite is outside the screen, move it to the other side and 
	    // repeat the tween; interpolate it left-wards
	    if (marqueeTextsBottom.get(i).position.x <= -530f) {
		marqueeTextsBottom.get(i).tweenManager.killAll();
		DynamicSprite sprite = marqueeTextsBottom.remove(i);
		sprite.position.set(marqueeTextsBottom.get(marqueeTextsBottom.size() - 1).position.x + 215f, 58);
		sprite.interpolateX(sprite.position.x - 25000, Linear.INOUT, 100000, true);
		marqueeTextsBottom.add(sprite);
	    }
	}

	for (int i = 0; i < marqueeTextsTop.size(); i++) {
	    marqueeTextsTop.get(i).render(spriteBatch);
	    marqueeTextsTop.get(i).update(deltaTime);
	    // If the current DynamicSprite is outside the screen, move it to the other side and 
	    // repeat the tween; interpolate it right-wards
	    if (marqueeTextsTop.get(i).position.x >= Settings.SCREEN_WIDTH + 530f) {
		marqueeTextsTop.get(i).tweenManager.killAll();
		DynamicSprite sprite = marqueeTextsTop.remove(i);
		sprite.position.set(marqueeTextsTop.get(marqueeTextsTop.size() - 1).position.x - 215f, 622);
		sprite.interpolateX(sprite.position.x + 25000, Linear.INOUT, 100000, true);
		marqueeTextsTop.add(sprite);
	    }
	}
    }

    /**
     * Render and update all displays related to the prompt bar
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderPromptBar(SpriteBatch spriteBatch, float deltaTime) {
	promptFill.render(spriteBatch);
	promptFill.update(deltaTime);
	promptText.render(spriteBatch);
	promptText.update(deltaTime);
    }

    /**
     * Render and update all displays related to the transition animation.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderTransition(SpriteBatch spriteBatch, float deltaTime) {
	left.render(spriteBatch);
	left.update(deltaTime);
	right.render(spriteBatch);
	right.update(deltaTime);
    }

}
