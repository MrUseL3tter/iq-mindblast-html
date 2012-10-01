package org.neu.cs.cs434;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

/**
 * Store class for the asset files variables.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class Assets {

    public static FileHandle science;
    public static FileHandle health;
    public static FileHandle geography;
    public static FileHandle history;
    public static FileHandle math;

    public static Texture background;
    public static Texture categorySelection;
    public static Texture result;
    public static Texture stage;
    public static Texture title;

    public static Texture calibri;
    public static Texture calibri32;
    public static Texture calibri41;
    public static Texture calibri52;
    public static Texture computerFont;
    public static Texture computerFont24;

    public static Sound click;
    public static Sound hover;
    public static Sound correct;
    public static Sound wrong;
    public static Music stageMusic;
    public static Music titleMusic;
    public static Music categorySelectionMusic;
    public static Music resultMusic;

    /**
     * Load all the variables. Invoke only once.
     */
    public static void load() {
	loadFileHandles();
	loadSounds();

	background = new Texture(Gdx.files.internal("data/gfx/BACKGROUND.png"));
	background.setFilter(TextureFilter.Linear, TextureFilter.Linear);

	categorySelection = new Texture(Gdx.files.internal("data/gfx/CATEGORY-SELECTION.png"));
	categorySelection.setFilter(TextureFilter.Linear, TextureFilter.Linear);

	result = new Texture(Gdx.files.internal("data/gfx/RESULTS.png"));
	result.setFilter(TextureFilter.Linear, TextureFilter.Linear);

	stage = new Texture(Gdx.files.internal("data/gfx/STAGE.png"));
	stage.setFilter(TextureFilter.Linear, TextureFilter.Linear);

	title = new Texture(Gdx.files.internal("data/gfx/TITLE.png"));
	title.setFilter(TextureFilter.Linear, TextureFilter.Linear);

	calibri = new Texture(Gdx.files.internal("data/font/CALIBRI.png"));
	calibri.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	calibri32 = new Texture(Gdx.files.internal("data/font/CALIBRI-32.png"));
	calibri32.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	calibri41 = new Texture(Gdx.files.internal("data/font/CALIBRI-41.png"));
	calibri41.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	calibri52 = new Texture(Gdx.files.internal("data/font/CALIBRI-52.png"));
	calibri52.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	computerFont = new Texture(Gdx.files.internal("data/font/COMPUTERFONT.png"));
	computerFont.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	computerFont24 = new Texture(Gdx.files.internal("data/font/COMPUTERFONT-24.png"));
	computerFont24.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    private static void loadFileHandles() {
	science = Gdx.files.internal("data/questions/SCIENCE");
	health = Gdx.files.internal("data/questions/HEALTH");
	geography = Gdx.files.internal("data/questions/GEOGRAPHY");
	history = Gdx.files.internal("data/questions/HISTORY");
	math = Gdx.files.internal("data/questions/MATH");
    }

    private static void loadSounds() {
	click = Gdx.audio.newSound(Gdx.files.internal("data/sfx/CLICK.ogg"));
	hover = Gdx.audio.newSound(Gdx.files.internal("data/sfx/HOVER.ogg"));
	correct = Gdx.audio.newSound(Gdx.files.internal("data/sfx/CORRECT.ogg"));
	wrong = Gdx.audio.newSound(Gdx.files.internal("data/sfx/WRONG.ogg"));
	stageMusic = Gdx.audio.newMusic(Gdx.files.internal("data/sfx/STAGE.ogg"));
	categorySelectionMusic = Gdx.audio.newMusic(Gdx.files.internal("data/sfx/CATEGORY-SELECTION.ogg"));
	titleMusic = Gdx.audio.newMusic(Gdx.files.internal("data/sfx/TITLE.ogg"));
	resultMusic = Gdx.audio.newMusic(Gdx.files.internal("data/sfx/RESULT.ogg"));
    }
}
