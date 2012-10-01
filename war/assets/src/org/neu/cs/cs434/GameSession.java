package org.neu.cs.cs434;

import java.util.ArrayList;

import aurelienribon.tweenengine.equations.Linear;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.noobs2d.tweenengine.utils.DynamicValue;

/**
 * A class holding values for a single game session.
 * 
 * @author Roshe Nikhka, MrUseL3tter
 */
public class GameSession {

    ArrayList<Question> questions = new ArrayList<Question>();

    DynamicValue score;
    int correctAnswers;
    int maxStreak;
    int questionsAnswered;
    int questionsTotal;
    int streak;

    public GameSession(int questionsCategory) {
	correctAnswers = 0;
	maxStreak = 0;
	score = new DynamicValue(0);
	questionsAnswered = 0;
	streak = 0;

	switch (questionsCategory) {
	    case Question.GEOGRAPHY:
		loadGeography();
		break;
	    case Question.HEALTH:
		loadHealth();
		break;
	    case Question.HISTORY:
		loadHistory();
		break;
	    case Question.MATH:
		loadMath();
		break;
	    case Question.SCIENCE:
		loadScience();
		break;
	    default: // random
		loadGeography();
		loadHealth();
		loadHistory();
		loadMath();
		loadScience();
		break;
	}
	shuffleQuestions();
	questionsTotal = questions.size();
    }

    /**
     * Answer the first question in the collection.
     * 
     * @param answer The string matching the selected choice.
     * @returns -1 if the answer is wrong, -2 if all questions are already answered, otherwise the
     *          score of the answer.
     */
    public int answerQuestion(String answer) {
	questionsAnswered++;
	if (questions.size() > 0) {
	    Question question = questions.remove(0);
	    if (question.answer.equals(answer)) {
		// answer is correct
		correctAnswers++;
		int answerValue = (int) (100 + streak / 26.0 * 100.0);
		score.interpolate(score.value + answerValue, Linear.INOUT, 500, true);
		streak++;
		maxStreak = streak > maxStreak ? streak : maxStreak;
		Assets.correct.play();
		return answerValue;
	    } else {
		// answer is incorrect 
		streak = 0;
		maxStreak = streak > maxStreak ? streak : maxStreak;
		Assets.wrong.play();
		return -1;
	    }
	} else
	    return -2;

    }

    /**
     * Load all geography questions and add them into the questions collection.
     */
    private void loadGeography() {
	JSONObject parentObject = (JSONObject) JSONValue.parse(Assets.geography.reader());
	for (Object object : parentObject.values()) {
	    JSONObject questionObject = (JSONObject) object;
	    String question = questionObject.get(new String("question")).toString();
	    String answer = questionObject.get(new String("answer")).toString();
	    JSONObject choicesObject = (JSONObject) questionObject.get(new String("choices"));
	    String[] choices = new String[4];
	    choices[0] = choicesObject.get("choice1").toString();
	    choices[1] = choicesObject.get("choice2").toString();
	    choices[2] = choicesObject.get("choice3").toString();
	    choices[3] = choicesObject.get("choice4").toString();
	    questions.add(new Question(question, choices, Question.GEOGRAPHY, answer));
	}
    }

    /**
     * Load all health questions and add them into the questions collection.
     */
    private void loadHealth() {
	JSONObject parentObject = (JSONObject) JSONValue.parse(Assets.health.reader());
	for (Object object : parentObject.values()) {
	    JSONObject questionObject = (JSONObject) object;
	    String question = questionObject.get(new String("question")).toString();
	    String answer = questionObject.get(new String("answer")).toString();
	    JSONObject choicesObject = (JSONObject) questionObject.get(new String("choices"));
	    String[] choices = new String[4];
	    choices[0] = choicesObject.get("choice1").toString();
	    choices[1] = choicesObject.get("choice2").toString();
	    choices[2] = choicesObject.get("choice3").toString();
	    choices[3] = choicesObject.get("choice4").toString();
	    questions.add(new Question(question, choices, Question.HEALTH, answer));
	}
    }

    /**
     * Load all history questions and add them into the questions collection.
     */
    private void loadHistory() {
	JSONObject parentObject = (JSONObject) JSONValue.parse(Assets.history.reader());
	for (Object object : parentObject.values()) {
	    JSONObject questionObject = (JSONObject) object;
	    String question = questionObject.get(new String("question")).toString();
	    String answer = questionObject.get(new String("answer")).toString();
	    JSONObject choicesObject = (JSONObject) questionObject.get(new String("choices"));
	    String[] choices = new String[4];
	    choices[0] = choicesObject.get("choice1").toString();
	    choices[1] = choicesObject.get("choice2").toString();
	    choices[2] = choicesObject.get("choice3").toString();
	    choices[3] = choicesObject.get("choice4").toString();
	    questions.add(new Question(question, choices, Question.HISTORY, answer));
	}
    }

    /**
     * Load all math questions and add them into the questions collection.
     */
    private void loadMath() {
	JSONObject parentObject = (JSONObject) JSONValue.parse(Assets.math.reader());
	for (Object object : parentObject.values()) {
	    JSONObject questionObject = (JSONObject) object;
	    String question = questionObject.get(new String("question")).toString();
	    String answer = questionObject.get(new String("answer")).toString();
	    JSONObject choicesObject = (JSONObject) questionObject.get(new String("choices"));
	    String[] choices = new String[4];
	    choices[0] = choicesObject.get("choice1").toString();
	    choices[1] = choicesObject.get("choice2").toString();
	    choices[2] = choicesObject.get("choice3").toString();
	    choices[3] = choicesObject.get("choice4").toString();
	    questions.add(new Question(question, choices, Question.MATH, answer));
	}
    }

    /**
     * Load all science questions and add them into the questions collection.
     */
    private void loadScience() {
	JSONObject parentObject = (JSONObject) JSONValue.parse(Assets.science.reader());
	for (Object object : parentObject.values()) {
	    JSONObject questionObject = (JSONObject) object;
	    String question = questionObject.get(new String("question")).toString();
	    String answer = questionObject.get(new String("answer")).toString();
	    JSONObject choicesObject = (JSONObject) questionObject.get(new String("choices"));
	    String[] choices = new String[4];
	    choices[0] = choicesObject.get("choice1").toString();
	    choices[1] = choicesObject.get("choice2").toString();
	    choices[2] = choicesObject.get("choice3").toString();
	    choices[3] = choicesObject.get("choice4").toString();
	    questions.add(new Question(question, choices, Question.SCIENCE, answer));
	}
    }

    /**
     * Shuffles the questions and their choices.
     */
    private void shuffleQuestions() {
	for (int i = 0; i < questions.size(); i++) {
	    Question q = questions.remove(i);
	    // shuffle the question's choices
	    for (int j = 0; j < q.choices.length; j++) {
		String choice = q.choices[j];
		int random = (int) (Math.random() * q.choices.length);
		q.choices[j] = q.choices[random];
		q.choices[random] = choice;
	    }
	    questions.add((int) (Math.random() * questions.size() - 1), q);
	}
    }
}
