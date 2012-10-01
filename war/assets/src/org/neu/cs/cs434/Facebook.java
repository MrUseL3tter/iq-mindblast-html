package org.neu.cs.cs434;

/**
 * A class for providing connectivity to facebook features.
 * 
 * @author MrUseL3tter
 */
public class Facebook {

    private static final String scoreURL = "http://iqmindblast.herokuapp.com/score.php";

    public static native void publishFeed(int score) /*-{  $wnd.publishFeed(score); }-*/;

    public static native void publishScore(int score) /*-{ $wnd.publishScore(score); }-*/;
}
