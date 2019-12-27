package server.data;

import java.util.List;

/**
 * QuestionBank class that holds the number of players and a list of questions needed for a quiz
 */
public class QuestionBank {
    private int players;
    private List<Question> questions;

    public QuestionBank() {
    }

    public QuestionBank(int players, List<Question> questions) {
        this.players = players;
        this.questions = questions;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "QuestionBank{" +
                "players=" + players +
                ", questions=" + questions +
                '}';
    }
}
