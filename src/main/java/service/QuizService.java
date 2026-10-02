package service;

import dao.QuestionDAO;
import dao.QuizDAO;
import dao.ResultDAO;
import model.Question;
import model.Quiz;
import model.Result;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class QuizService {

    private QuizDAO quizDAO = new QuizDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private ResultDAO resultDAO = new ResultDAO();

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

    public int submitQuiz(int quizId, int userId, Map<Integer, String> userAnswers) throws SQLException {
        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        List<Question> questions = questionDAO.getQuestionsByQuizId(quizId);
        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions found for quiz ID: " + quizId);
        }

        int score = calculateScore(questions, userAnswers);

        Result result = new Result(0, userId, quizId, score, questions.size());
        resultDAO.saveResult(result);

        return score;
    }
}
