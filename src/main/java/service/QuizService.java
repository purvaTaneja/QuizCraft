package service;

import dao.QuestionDAO;
import dao.QuizDAO;
import dao.ResultDAO;
import dao.TopicPerformanceDAO;
import model.Question;
import model.Quiz;
import model.Result;
import model.TopicPerformance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizService {

    private QuizDAO quizDAO = new QuizDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private ResultDAO resultDAO = new ResultDAO();
    private TopicPerformanceDAO topicPerformanceDAO = new TopicPerformanceDAO();
    private FeedbackService feedbackService = new FeedbackService();

    public int calculateScore(List<Question> questions, Map<Integer, String> userAnswers) {
        int score = 0;
        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectAnswer())) {
                score++;
            }
        }
        return score;
    }

    /**
     * Starts an attempt by randomly picking {@code questionCount} unique questions
     * from the course. Selection is done in SQL with ORDER BY RAND() LIMIT so a
     * question can never appear twice in the same attempt.
     */
    public List<Question> startAttempt(int quizId, int questionCount) throws SQLException {
        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        int available = questionDAO.getQuestionCountByQuizId(quizId);
        if (available == 0) {
            throw new IllegalStateException("No questions found for quiz ID: " + quizId);
        }

        int count = Math.min(questionCount, available);
        return questionDAO.getQuestionsByQuizId(quizId, count);
    }

    public int submitQuiz(int quizId, int userId, Map<Integer, String> userAnswers) throws SQLException {
        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        List<Question> questions = questionDAO.getQuestionsByQuizId(quizId);
        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions found for quiz ID: " + quizId);
        }

        return persistAttempt(quizId, userId, questions, userAnswers);
    }

    /**
     * Scores an attempt against exactly the questions that were served to the user,
     * stores the result, and stores per-topic performance for that attempt.
     *
     * @return the generated result ID
     */
    public int submitQuizByQuestionIds(int quizId, int userId, Map<Integer, String> userAnswers,
                                       List<Integer> questionIds) throws SQLException {

        if (questionIds == null || questionIds.isEmpty()) {
            throw new IllegalStateException("No questions were recorded for this attempt.");
        }

        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        List<Question> attemptedQuestions = new ArrayList<>();
        for (Integer questionId : questionIds) {
            Question question = questionDAO.getQuestionById(questionId);
            if (question != null) {
                attemptedQuestions.add(question);
            }
        }

        if (attemptedQuestions.isEmpty()) {
            throw new IllegalStateException("None of the attempted questions could be loaded.");
        }

        return persistAttempt(quizId, userId, attemptedQuestions, userAnswers);
    }

    /**
     * Calculates the score, saves the result row, then saves one topic-performance
     * row per topic represented in the attempt.
     *
     * @return the generated result ID
     */
    private int persistAttempt(int quizId, int userId, List<Question> attemptedQuestions,
                               Map<Integer, String> userAnswers) throws SQLException {

        int score = calculateScore(attemptedQuestions, userAnswers);

        Result result = new Result(0, userId, quizId, score, attemptedQuestions.size());
        int resultId = resultDAO.saveResultAndGetId(result);

        // Topic-level breakdown for this specific attempt.
        Map<String, TopicPerformance> analysis =
                feedbackService.analyzeTopics(attemptedQuestions, userAnswers);

        for (TopicPerformance performance : analysis.values()) {
            performance.setResultId(resultId);
            topicPerformanceDAO.save(performance);
        }

        return resultId;
    }

    public FeedbackService getFeedbackService() {
        return feedbackService;
    }
}
