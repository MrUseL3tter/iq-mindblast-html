package org.neu.cs.cs434;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Store class for the font files variables.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class Fonts {

    public static FileHandle calibri32;
    public static FileHandle calibri41;
    public static FileHandle calibri52;
    public static FileHandle computerFont;
    public static FileHandle computerFont24;

    /**
     * Load all the variables. Invoke only once.
     */
    public static void load() {
	calibri32 = Gdx.files.internal("data/font/CALIBRI-32.fnt");
	calibri41 = Gdx.files.internal("data/font/CALIBRI-41.fnt");
	calibri52 = Gdx.files.internal("data/font/CALIBRI-52.fnt");
	computerFont = Gdx.files.internal("data/font/COMPUTERFONT.fnt");
	computerFont24 = Gdx.files.internal("data/font/COMPUTERFONT-24.fnt");
    }
}
