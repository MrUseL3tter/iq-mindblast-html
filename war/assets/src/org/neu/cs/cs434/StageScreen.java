package org.neu.cs.cs434;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Sine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
 * The main gameplay screen.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class StageScreen extends DynamicScreen implements TweenCallback, DynamicButtonCallback {

    // category vars
    DynamicSprite science;
    DynamicSprite health;
    DynamicSprite geography;
    DynamicSprite history;
    DynamicSprite math;

    // choices vars
    ArrayList<DynamicButton> selectables;
    DynamicButton choice1Button;
    DynamicButton choice2Button;
    DynamicButton choice3Button;
    DynamicButton choice4Button;
    DynamicButton choice1Letter;
    DynamicButton choice2Letter;
    DynamicButton choice3Letter;
    DynamicButton choice4Letter;
    DynamicText choice1;
    DynamicText choice2;
    DynamicText choice3;
    DynamicText choice4;

    // instruction prompt in the middle of the screen
    DynamicText promptText1; // 60 seconds
    DynamicText promptText2; // answer as many as you can
    DynamicText promptText3; // ready
    DynamicText promptText4; // start
    DynamicText promptText5; // start fade
    DynamicSprite promptFill;

    // question #
    DynamicSprite questionNumCircle;
    DynamicText questionNumText;

    // question
    DynamicSprite patch;
    DynamicText question;

    // score vars
    DynamicText score;
    DynamicText flyout;
    DynamicText streak;

    // settings buttons
    DynamicButton music;
    DynamicButton sounds;

    // timer vars
    DynamicText timerText;
    DynamicValue timeLeft;

    // transition vars
    DynamicSprite left;
    DynamicSprite right;

    GameSession session;
    int questionsCategory;
    boolean paused = false;

    /**
     * Default constructor.
     * 
     * @param game Reference to the Game object; needed mostly for screen transitions.
     * @param questionsCategory category index as forwarded by CategorySelectionScreen.
     */
    public StageScreen(Game game, int questionsCategory) {
	super(game, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
	this.questionsCategory = questionsCategory;
	initCategories();
	initChoices();
	initPrompt();
	initQuestionNumber();
	initQuestionPatch();
	initScore();
	initSettings();
	initTimer();
	initTransition();
	interpolateBegin();

	Assets.stageMusic.setLooping(true);
	Assets.stageMusic.play();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.noobs2d.tweenengine.utils.DynamicButton.DynamicButtonCallback#onButtonEvent(com.noobs2d
     * .tweenengine.utils.DynamicButton, int)
     */
    @Override
    public void onButtonEvent(DynamicButton button, int eventType) {
	if (eventType == DOWN)
	    for (int i = 0; i < selectables.size(); i++) {
		DynamicButton clicked = selectables.get(i);
		if (clicked.equals(button) && clicked.state == State.DOWN) {
		    // The player answers
		    clicked.state = State.UP;
		    onPlayerAnswers(session.answerQuestion(session.questions.get(0).choices[i]));
		} else if (clicked.equals(button)) {
		    clicked.state = State.DOWN;
		    Assets.click.play();
		}

		if (!clicked.equals(button))
		    clicked.state = State.UP;

	    }
	else if (eventType == UP && button.state == State.HOVER)
	    button.state = State.DOWN;
	else if (eventType == HOVER && button.state == State.UP) {
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
	if (type == COMPLETE && source.equals(promptFill.tween)) {
	    timeLeft.tween.resume();
	    setChoicesEnabled(true);
	    questionNumText.visible = true;
	    showQuestion();
	}
	if (type == COMPLETE && source.equals(choice4.tween))
	    // fade in is complete; enable the choices buttons
	    setChoicesEnabled(true);
	if (type == COMPLETE && source.equals(timeLeft.tween)) {
	    // the timer has ended
	    setChoicesEnabled(false);
	    promptText4.text = "TIME'S UP!";
	    promptText4.position.x = -Settings.SCREEN_WIDTH;
	    promptText4.scale.set(2f, 2f);
	    promptText4.color.a = 1f;
	    promptText4.interpolateX(Settings.SCREEN_WIDTH / 2, Expo.OUT, 500, true);
	    promptText4.interpolateAlpha(0f, Linear.INOUT, 250, true).delay(2000);
	    promptText4.tween.setCallback(this); // when this tween ends, show the results screen
	    promptText4.tween.setCallbackTriggers(COMPLETE);
	}
	if (type == COMPLETE && source.equals(promptText4.tween))
	    showResultScreen();
    }

    /**
     * Invoked when the players answers the current question.
     * 
     * @param score
     */
    public void onPlayerAnswers(int score) {
	setQuesionFade(0f, 0, false); // fade out the choices
	setChoicesEnabled(false); // disable the choices until the next question appears
	if (score >= 0) { // the answer is correct
	    float x = Gdx.input.getX(0);
	    float y = Settings.SCREEN_HEIGHT - Gdx.input.getY(0);
	    flyout.text = "+" + score + "!";
	    flyout.position.set(x, y);
	    flyout.tweenManager.update(1000); // end the current tween
	    flyout.color.set(1f, 1f, 1f, 0f);
	    flyout.interpolateAlpha(1f, Linear.INOUT, 150, true);
	    flyout.interpolateAlpha(0f, Linear.INOUT, 150, true).delay(1250);
	    flyout.interpolateY(flyout.position.y + 50, Sine.OUT, 250, true);
	    flyout.interpolateY(flyout.position.y, Sine.IN, 250, true).delay(250);
	    showQuestion();
	} else if (score == -1) { // wrong answer
	    float x = Gdx.input.getX(0);
	    float y = Settings.SCREEN_HEIGHT - Gdx.input.getY(0);
	    flyout.text = "WRONG!";
	    flyout.position.set(x, y);
	    flyout.tweenManager.update(1000); // end the current tween
	    flyout.color.set(Color.RED);
	    // shaking "WRONG!" text
	    flyout.interpolateX(flyout.position.x + 15, Linear.INOUT, 40, true);
	    flyout.interpolateX(flyout.position.x - 15, Linear.INOUT, 40, true).delay(40);
	    flyout.interpolateX(flyout.position.x + 15, Linear.INOUT, 40, true).delay(80);
	    flyout.interpolateX(flyout.position.x - 15, Linear.INOUT, 40, true).delay(120);
	    flyout.interpolateX(flyout.position.x, Linear.INOUT, 40, true).delay(160);
	    flyout.interpolateAlpha(0f, Linear.INOUT, 150, true).delay(1250);//fade out
	    showQuestion();
	} else if (score == -2) // all questions are already answered
	    showResultScreen();
	setQuesionFade(1f, 150, true); // fade in the choices
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
	renderChoices(spriteBatch, deltaTime);
	renderQuestionPatch(spriteBatch, deltaTime);
	renderCategories(spriteBatch, deltaTime);
	renderQuestionNumber(spriteBatch, deltaTime);
	renderScore(spriteBatch, deltaTime);
	renderSettings(spriteBatch, deltaTime);
	renderTimer(spriteBatch, deltaTime);
	renderPrompt(spriteBatch, deltaTime);
	renderTransition(spriteBatch, deltaTime);
	spriteBatch.end();
    }

    /*
     * (non-Javadoc)
     * @see com.noobs2d.tweenengine.utils.DynamicScreen#show()
     */
    @Override
    public void show() {
	super.show();
	session = new GameSession(questionsCategory);
    }

    /**
     * Instantiate all displays related to categories.
     */
    private void initCategories() {
	science = new DynamicSprite(new TextureRegion(Assets.stage, 0, 405, 174, 60), 236, 570);
	science.visible = false;
	health = new DynamicSprite(new TextureRegion(Assets.stage, 0, 466, 174, 60), 236, 570);
	health.visible = false;
	geography = new DynamicSprite(new TextureRegion(Assets.stage, 175, 466, 174, 60), 236, 570);
	geography.visible = false;
	history = new DynamicSprite(new TextureRegion(Assets.stage, 175, 405, 174, 60), 236, 570);
	history.visible = false;
	math = new DynamicSprite(new TextureRegion(Assets.stage, 349, 405, 174, 60), 236, 570);
	math.visible = false;
    }

    /**
     * Instantiate all displays related to the choices bars.
     */
    private void initChoices() {
	TextureRegion downstate = new TextureRegion(Assets.stage, 1, 155, 592, 83);
	TextureRegion hoverstate = new TextureRegion(Assets.stage, 1, 238, 592, 83);
	TextureRegion upstate = new TextureRegion(Assets.stage, 1, 321, 592, 83);

	selectables = new ArrayList<DynamicButton>();
	choice1Button = new DynamicButton(upstate, hoverstate, downstate, new Vector2(333, 363));
	choice1Button.callback = this;
	selectables.add(choice1Button);
	choice2Button = new DynamicButton(upstate, hoverstate, downstate, new Vector2(433, 274));
	choice2Button.callback = this;
	selectables.add(choice2Button);
	choice3Button = new DynamicButton(upstate, hoverstate, downstate, new Vector2(333, 184));
	choice3Button.callback = this;
	selectables.add(choice3Button);
	choice4Button = new DynamicButton(upstate, hoverstate, downstate, new Vector2(433, 94));
	choice4Button.callback = this;
	selectables.add(choice4Button);

	choice1 = new DynamicText(new BitmapFont(Fonts.calibri32, new TextureRegion(Assets.calibri32), false), "", HAlignment.LEFT);
	choice1.position.set(132, 360);
	choice1.registration = DynamicRegistration.LEFT_CENTER;
	choice2 = new DynamicText(new BitmapFont(Fonts.calibri32, new TextureRegion(Assets.calibri32), false), "", HAlignment.LEFT);
	choice2.position.set(232, 271);
	choice2.registration = DynamicRegistration.LEFT_CENTER;
	choice3 = new DynamicText(new BitmapFont(Fonts.calibri32, new TextureRegion(Assets.calibri32), false), "", HAlignment.LEFT);
	choice3.position.set(132, 181);
	choice3.registration = DynamicRegistration.LEFT_CENTER;
	choice4 = new DynamicText(new BitmapFont(Fonts.calibri32, new TextureRegion(Assets.calibri32), false), "", HAlignment.LEFT);
	choice4.position.set(232, 91);
	choice4.registration = DynamicRegistration.LEFT_CENTER;
    }

    /**
     * Instantiate all displays related to the beginning prompt.
     */
    private void initPrompt() {
	promptText1 = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/COMPUTERFONT.fnt"), new TextureRegion(Assets.computerFont), false), "60 SECONDS");
	promptText1.position.set(190, Settings.SCREEN_HEIGHT / 2 + 50);
	promptText2 = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/COMPUTERFONT.fnt"), new TextureRegion(Assets.computerFont), false), "ANSWER AS MANY\nAS YOU CAN!");
	promptText2.position.set(570, Settings.SCREEN_HEIGHT / 2 - 35);
	promptText2.scale.set(.75f, .75f);
	promptText3 = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/COMPUTERFONT.fnt"), new TextureRegion(Assets.computerFont), false), "READY");
	promptText3.position.set(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	promptText3.scale.set(3f, 3f);
	promptText4 = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/COMPUTERFONT.fnt"), new TextureRegion(Assets.computerFont), false), "START!");
	promptText4.position.set(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	promptText4.scale.set(3.5f, 3.5f);
	promptText5 = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/COMPUTERFONT.fnt"), new TextureRegion(Assets.computerFont), false), "START!");
	promptText5.position.set(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	promptText5.scale.set(3.5f, 3.5f);
	promptFill = new DynamicSprite(new TextureRegion(Assets.categorySelection, 398, 320, 36, 36), Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
	promptFill.scale.set(25f, 7.5f);
    }

    /**
     * Instantiate all displays related to the question number circle.
     */
    private void initQuestionNumber() {
	questionNumCircle = new DynamicSprite(new TextureRegion(Assets.stage, 696, 0, 61, 60), 104, 570);
	questionNumText = new DynamicText(new BitmapFont(Fonts.calibri32, new TextureRegion(Assets.calibri32), false), "");
	questionNumText.position.set(questionNumCircle.position);
	questionNumText.scale.set(.75f, .75f);
	questionNumText.visible = false;
    }

    /**
     * Instantiate all variables related to the question patch; the question text, and the holder
     * patch or fill.
     */
    private void initQuestionPatch() {
	patch = new DynamicSprite(new TextureRegion(Assets.stage, 0, 0, 695, 155), 382, 498);
	question = new DynamicText(new BitmapFont(Fonts.calibri32, new TextureRegion(Assets.calibri32), false), "", HAlignment.LEFT);
	question.position.set(64, 520);
	question.registration = DynamicRegistration.TOP_LEFT;
	question.wrapWidth = 626;
    }

    /**
     * Instantiate all variables related to the scores.
     */
    private void initScore() {
	score = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "0", HAlignment.RIGHT);
	score.position.set(730, 670);
	score.registration = DynamicRegistration.TOP_RIGHT;
	score.wrapWidth = 250;

	flyout = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "0");
	flyout.color.a = 0f;

	streak = new DynamicText(new BitmapFont(Gdx.files.internal("data/font/CALIBRI-41.fnt"), new TextureRegion(Assets.calibri41), false), "STREAK: 0", HAlignment.RIGHT);
	streak.position.set(730, 639);
	streak.scale.set(.5f, .5f);
	streak.registration = DynamicRegistration.TOP_RIGHT;
    }

    /**
     * Instantiate all variables related to the settings buttons.
     */
    private void initSettings() {
	TextureRegion downstate = new TextureRegion(Assets.stage, 595, 203, 61, 48);
	TextureRegion hoverstate = new TextureRegion(Assets.stage, 595, 156, 61, 48);
	TextureRegion upstate = new TextureRegion(Assets.stage, 595, 156, 61, 48);
	sounds = new DynamicButton(upstate, hoverstate, downstate, new Vector2(118, 651));

	downstate = new TextureRegion(Assets.stage, 657, 203, 38, 44);
	hoverstate = new TextureRegion(Assets.stage, 657, 155, 38, 44);
	upstate = new TextureRegion(Assets.stage, 657, 155, 38, 44);
	music = new DynamicButton(upstate, hoverstate, downstate, new Vector2(54, 651));
    }

    /**
     * Instantiate all variables related to the timer.
     */
    private void initTimer() {
	timerText = new DynamicText(new BitmapFont(Fonts.calibri52, new TextureRegion(Assets.calibri52), false), "01:00");
	timerText.position.set(Settings.SCREEN_WIDTH / 2, 650);

	timeLeft = new DynamicValue(60, 0, 60000, 0);
	timeLeft.tween.setCallback(this); // when the timer is done, show "TIME UP!"
	timeLeft.tween.setCallbackTriggers(COMPLETE);
    }

    /**
     * Instantiate all displays related to the transition animation.
     */
    private void initTransition() {
	left = new DynamicSprite(new TextureRegion(Assets.title, 0, 0, 380, 680), 190, Settings.SCREEN_HEIGHT / 2);
	right = new DynamicSprite(new TextureRegion(Assets.title, 381, 0, 379, 680), 570, Settings.SCREEN_HEIGHT / 2);
    }

    /**
     * Performs the interpolations in the beginning of the screen.
     */
    private void interpolateBegin() {
	// remove the transition display
	left.interpolateX(-(Settings.SCREEN_WIDTH / 2), Linear.INOUT, 250, true).delay(1000);
	right.interpolateX(Settings.SCREEN_WIDTH + 340, Linear.INOUT, 250, true).delay(1000);

	// Settings buttons
	music.interpolateY(music.position.y, Back.OUT, 1000, true);
	music.position.y += 200;
	sounds.interpolateY(sounds.position.y, Back.OUT, 1000, true).delay(1450);
	sounds.position.y += 200;

	// timer
	timerText.interpolateY(timerText.position.y, Bounce.OUT, 1000, true).delay(1350);
	timerText.position.y += 200;
	timeLeft.tween.pause(); // pause the timer until the prompt ends

	// scores
	score.interpolateX(score.position.x, Back.OUT, 1000, true).delay(1450);
	score.position.x += 200;
	streak.interpolateX(streak.position.x, Back.OUT, 1000, true).delay(1000);
	streak.position.x += 200;

	questionNumCircle.interpolateScaleXY(new Vector2(1f, 1f), Bounce.OUT, 1000, true).delay(1350);
	questionNumCircle.scale.set(0f, 0f);

	patch.interpolateScaleXY(new Vector2(1f, 1f), Back.OUT, 1000, true);
	patch.scale.set(0f, 0f);

	choice1Button.interpolateX(choice1Button.position.x, Back.OUT, 1000, true).delay(1300);
	choice1Button.position.x -= 1000;
	choice2Button.interpolateX(choice2Button.position.x, Back.OUT, 1000, true).delay(1350);
	choice2Button.position.x += 1000;
	choice3Button.interpolateX(choice3Button.position.x, Back.OUT, 1000, true).delay(1400);
	choice3Button.position.x -= 1000;
	choice4Button.interpolateX(choice4Button.position.x, Back.OUT, 1000, true).delay(1450);
	choice4Button.position.x += 1000;

	// interpolate prompt bar, appear
	promptFill.interpolateScaleY(promptFill.scale.y, Expo.IN, 500, true).delay(1000);
	promptFill.scale.y = 0f;
	promptText1.interpolateX(promptText1.position.x, Linear.INOUT, 250, true).delay(1500);
	promptText1.interpolateX(promptText1.position.x + 50, Linear.INOUT, 2000, true).delay(1750);
	promptText1.interpolateX(promptText1.position.x + 150, Linear.INOUT, 250, true).delay(3750);
	promptText1.interpolateAlpha(0f, Linear.INOUT, 250, true).delay(3750);
	promptText1.position.x -= 1000;
	promptText2.interpolateX(promptText2.position.x, Linear.INOUT, 250, true).delay(1500);
	promptText2.interpolateX(promptText2.position.x - 50, Linear.INOUT, 2000, true).delay(1750);
	promptText2.interpolateX(promptText2.position.x - 150, Linear.INOUT, 250, true).delay(3750);
	promptText2.interpolateAlpha(0f, Linear.INOUT, 250, true).delay(3750);
	promptText2.position.x += 1000;
	promptText3.color.a = 0f;
	promptText3.interpolateAlpha(1f, Linear.INOUT, 250, true).delay(3750);
	promptText3.interpolateAlpha(0f, Linear.INOUT, 250, true).delay(5250);
	promptText4.scale.set(9f, 9f);
	promptText4.color.a = 0f;
	promptText4.interpolateAlpha(1f, Linear.INOUT, 1, true).delay(5250);
	promptText4.interpolateScaleXY(new Vector2(3.5f, 3.5f), Linear.INOUT, 250, true).delay(5250);
	promptText4.interpolateAlpha(0f, Linear.INOUT, 1, true).delay(6250);
	promptText5.color.a = 0f;
	promptText5.interpolateAlpha(.5f, Linear.INOUT, 1, true).delay(5250);
	promptText5.interpolateScaleXY(new Vector2(7f, 7f), Linear.INOUT, 250, true).delay(5250);
	promptText5.interpolateAlpha(0f, Linear.INOUT, 500, true).delay(5250);

	// interpolate prompt bar, disappear
	promptFill.interpolateScaleY(0f, Linear.INOUT, 150, true).delay(6250);
	promptFill.tween.setCallback(this);
	promptFill.tween.setCallbackTriggers(COMPLETE);

	// disable inputs for the choices until the animations are done
	choice1Button.enabled = false;
	choice2Button.enabled = false;
	choice3Button.enabled = false;
	choice4Button.enabled = false;
    }

    /**
     * Render and update all displays related to the categories.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderCategories(SpriteBatch spriteBatch, float deltaTime) {
	science.render(spriteBatch);
	science.update(deltaTime);
	health.render(spriteBatch);
	health.update(deltaTime);
	geography.render(spriteBatch);
	geography.update(deltaTime);
	history.render(spriteBatch);
	history.update(deltaTime);
	math.render(spriteBatch);
	math.update(deltaTime);
    }

    /**
     * Render and update all displays related to the choices bars.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderChoices(SpriteBatch spriteBatch, float deltaTime) {
	choice1Button.render(spriteBatch);
	choice1Button.update(deltaTime);
	choice2Button.render(spriteBatch);
	choice2Button.update(deltaTime);
	choice3Button.render(spriteBatch);
	choice3Button.update(deltaTime);
	choice4Button.render(spriteBatch);
	choice4Button.update(deltaTime);
	choice1.render(spriteBatch);
	choice1.update(deltaTime);
	choice2.render(spriteBatch);
	choice2.update(deltaTime);
	choice3.render(spriteBatch);
	choice3.update(deltaTime);
	choice4.render(spriteBatch);
	choice4.update(deltaTime);
    }

    /**
     * Render and update all displays related to the beginning prompt.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderPrompt(SpriteBatch spriteBatch, float deltaTime) {
	promptFill.render(spriteBatch);
	promptFill.update(deltaTime);
	promptText1.render(spriteBatch);
	promptText1.update(deltaTime);
	promptText2.render(spriteBatch);
	promptText2.update(deltaTime);
	promptText3.render(spriteBatch);
	promptText3.update(deltaTime);
	promptText4.render(spriteBatch);
	promptText4.update(deltaTime);
	promptText5.render(spriteBatch);
	promptText5.update(deltaTime);
    }

    /**
     * Render and update all displays related to the question number circle.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderQuestionNumber(SpriteBatch spriteBatch, float deltaTime) {
	questionNumCircle.render(spriteBatch);
	questionNumCircle.update(deltaTime);
	questionNumText.render(spriteBatch);
	questionNumText.update(deltaTime);
	questionNumText.text = "#" + (session.questionsAnswered + 1);
    }

    /**
     * Render and update all displays related to the question patch.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderQuestionPatch(SpriteBatch spriteBatch, float deltaTime) {
	patch.render(spriteBatch);
	patch.update(deltaTime);
	question.render(spriteBatch);
	question.update(deltaTime);
    }

    /**
     * Render and update all displays related to the score.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderScore(SpriteBatch spriteBatch, float deltaTime) {
	flyout.render(spriteBatch);
	flyout.update(deltaTime);
	score.render(spriteBatch);
	score.update(deltaTime);
	streak.render(spriteBatch);
	streak.update(deltaTime);

	score.text = "" + (int) session.score.value;
	streak.text = "STREAK: " + session.streak;
	session.score.update(deltaTime);
    }

    /**
     * Render and update all displays related to the settings buttons.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderSettings(SpriteBatch spriteBatch, float deltaTime) {
	music.render(spriteBatch);
	music.update(deltaTime);
	sounds.render(spriteBatch);
	sounds.update(deltaTime);
    }

    /**
     * Render and update all displays related to the timer.
     * 
     * @param spriteBatch Used to render the displays.
     * @param deltaTime Lapse since the last frame.
     */
    private void renderTimer(SpriteBatch spriteBatch, float deltaTime) {
	timerText.render(spriteBatch);
	if (!paused) {
	    timerText.update(deltaTime);
	    timeLeft.update(deltaTime);
	}

	timerText.text = timeLeft.value >= 60 ? "01:00" : "00:" + (timeLeft.value >= 10 ? (int) timeLeft.value : "0" + (int) timeLeft.value);
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

    private void setChoicesEnabled(boolean enabled) {
	choice1Button.enabled = enabled;
	choice2Button.enabled = enabled;
	choice3Button.enabled = enabled;
	choice4Button.enabled = enabled;
    }

    /**
     * Fade-interpolate the question and choices texts depending on the value passed.
     * 
     * @param alpha target alpha of the choices texts
     * @param delay delay before the tween starts
     * @param useCallbacks whether to invoke the callback that will enable the choice buttons
     */
    private void setQuesionFade(float alpha, int delay, boolean useCallbacks) {
	question.interpolateAlpha(alpha, Linear.INOUT, 150, true).delay(delay);
	choice1.interpolateAlpha(alpha, Linear.INOUT, 150, true).delay(delay);
	choice2.interpolateAlpha(alpha, Linear.INOUT, 150, true).delay(delay);
	choice3.interpolateAlpha(alpha, Linear.INOUT, 150, true).delay(delay);
	choice4.interpolateAlpha(alpha, Linear.INOUT, 150, true).delay(delay);
	if (useCallbacks) {
	    choice4.tween.setCallback(this);
	    choice4.tween.setCallbackTriggers(COMPLETE);
	}
    }

    /**
     * Sets which category bar to show depending on the category of the current question.
     */
    private void showCategory() {
	switch (session.questions.get(0).category) {
	    case Question.GEOGRAPHY:
		geography.visible = true;
		health.visible = false;
		history.visible = false;
		math.visible = false;
		science.visible = false;
		break;
	    case Question.HEALTH:
		geography.visible = false;
		health.visible = true;
		history.visible = false;
		math.visible = false;
		science.visible = false;
		break;
	    case Question.HISTORY:
		geography.visible = false;
		health.visible = false;
		history.visible = true;
		math.visible = false;
		science.visible = false;
		break;
	    case Question.MATH:
		geography.visible = false;
		health.visible = false;
		history.visible = false;
		math.visible = true;
		science.visible = false;
		break;
	    case Question.SCIENCE:
		geography.visible = false;
		health.visible = false;
		history.visible = false;
		math.visible = false;
		science.visible = true;
		break;
	    default:
		geography.visible = false;
		health.visible = false;
		history.visible = false;
		math.visible = false;
		science.visible = false;
		break;
	}
    }

    /**
     * Show the question or the next one.
     */
    private void showQuestion() {
	if (session.questions.size() > 0) {
	    question.text = session.questions.get(0).question;
	    choice1.text = session.questions.get(0).choices[0];
	    choice2.text = session.questions.get(0).choices[1];
	    choice3.text = session.questions.get(0).choices[2];
	    choice4.text = session.questions.get(0).choices[3];
	    showCategory();
	} else
	    showResultScreen();
    }

    /**
     * Transition into the next screen
     */
    private void showResultScreen() {
	session.score.tweenManager.killAll();
	game.setScreen(new ResultScreen(game, this));
	Assets.stageMusic.stop();
    }
}
