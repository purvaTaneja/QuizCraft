package model;

/**
 * Topic-level performance for a single quiz attempt.
 * One row exists per topic that was represented in the attempt.
 */
public class TopicPerformance {

    private int id;
    private int resultId;
    private String topic;
    private int correctAnswers;
    private int totalQuestions;
    private double percentage;

    public TopicPerformance() {
    }

    public TopicPerformance(int id, int resultId, String topic,
                            int correctAnswers, int totalQuestions, double percentage) {
        this.id = id;
        this.resultId = resultId;
        this.topic = topic;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
    }

    public TopicPerformance(String topic, int correctAnswers, int totalQuestions) {
        this.topic = topic;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.percentage = (totalQuestions > 0)
                ? ((double) correctAnswers / totalQuestions * 100)
                : 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
