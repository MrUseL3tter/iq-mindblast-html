package org.neu.cs.cs434;

/**
 * Model class holding values for a single question.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class Question {

    public static final int SCIENCE = 0;
    public static final int HEALTH = 1;
    public static final int GEOGRAPHY = 2;
    public static final int HISTORY = 3;
    public static final int MATH = 4;

    public final String question;
    public final String[] choices;
    public final String answer;
    public final int category;

    /**
     * @param question Limited to 100 characters.
     * @param choices MUST be a size of exactly four. Limited to 25 characters.
     * @param answerIndex Starting from zero.
     */
    public Question(String question, String[] choices, int category, String answer) {
	if (choices.length != 4)
	    throw new IllegalArgumentException("Array size of argument 'choices' must be exactly four.");
	this.question = question;
	this.choices = choices;
	this.category = category;
	this.answer = answer;
    }
}
