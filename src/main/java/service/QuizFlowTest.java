package service;

import dao.QuestionDAO;
import dao.QuizDAO;
import model.Question;
import model.Quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFlowTest {

    public static void main(String[] args) {
        int quizId = 1;
        int userId = 1;

        QuizService quizService = new QuizService();
        QuizDAO quizDAO = new QuizDAO();
        QuestionDAO questionDAO = new QuestionDAO();

        // Step 0: Seed test questions if none exist
        System.out.println("--- Step 0: Check/Create test questions ---");
        try {
            List<Question> existing = questionDAO.getQuestionsByQuizId(quizId);
            if (existing.isEmpty()) {
                System.out.println("No questions found. Creating 3 test questions...");
                questionDAO.addQuestion(new Question(0, quizId, "What is the capital of France?",
                    "London", "Paris", "Berlin", "Madrid", "B"));
                questionDAO.addQuestion(new Question(0, quizId, "What is 2 + 2?",
                    "3", "4", "5", "6", "B"));
                questionDAO.addQuestion(new Question(0, quizId, "Which language runs on the JVM?",
                    "Java", "C", "Python", "HTML", "A"));
                System.out.println("3 test questions created.");
            } else {
                System.out.println("Questions already exist (" + existing.size() + " found). Skipping creation.");
            }
        } catch (Exception e) {
            System.out.println("Error creating questions: " + e.getMessage());
            return;
        }

        // Step 1: Load the quiz
        System.out.println("\n--- Step 1: Load quiz ID " + quizId + " ---");
        try {
            Quiz quiz = quizDAO.getQuizById(quizId);
            if (quiz != null) {
                System.out.println("Quiz: " + quiz.getTitle());
                System.out.println("Description: " + quiz.getDescription());
            } else {
                System.out.println("Quiz not found.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error loading quiz: " + e.getMessage());
            return;
        }

        // Step 2: Load all questions for the quiz
        System.out.println("\n--- Step 2: Load questions ---");
        List<Question> questions;
        try {
            questions = questionDAO.getQuestionsByQuizId(quizId);
            System.out.println("Total questions: " + questions.size());
            for (Question q : questions) {
                System.out.println("  [" + q.getId() + "] " + q.getQuestionText());
            }
        } catch (Exception e) {
            System.out.println("Error loading questions: " + e.getMessage());
            return;
        }

        // Step 3: Create answers dynamically using actual question IDs
        System.out.println("\n--- Step 3: Submit answers ---");
        Map<Integer, String> userAnswers = new HashMap<>();
        for (Question q : questions) {
            userAnswers.put(q.getId(), q.getCorrectAnswer());
        }
        System.out.println("Answers submitted: " + userAnswers.size());

        // Step 4: Submit the quiz and get the score
        System.out.println("\n--- Step 4: Calculate and save result ---");
        try {
            int score = quizService.submitQuiz(quizId, userId, userAnswers);
            System.out.println("Final score: " + score + "/" + questions.size());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid quiz: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Quiz has no questions: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error submitting quiz: " + e.getMessage());
        }
    }
}
