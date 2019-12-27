package server.data;

import java.util.List;

/**
 * Question class containing all relevant data
 */
public class Question {
    private int id;
    private int answerId;
    private String text;
    private List<Answer> answers;

    public Question() {
    }

    /**
     * Constructor with all parameters to set
     * @param id ID of the question
     * @param answerId ID of the correct answer
     * @param text the text of the question
     * @param answers list of the given answers
     */
    public Question(int id, int answerId, String text, List<Answer> answers) {
        this.id = id;
        this.answerId = answerId;
        this.text = text;
        this.answers = answers;
    }

    public String getFormattedQuestion() {
        StringBuilder question = new StringBuilder(id + ". " + text + "\n");
        for (Answer answer : answers) {
            question.append("\t").append(answer).append("\n");
        }
        return question.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", answerId=" + answerId +
                ", text='" + text + '\'' +
                ", answers=" + answers +
                '}';
    }
}
