package dao;

import model.Quiz;

import java.util.List;

public class QuizTest {

    public static void main(String[] args) {
        QuizDAO quizDAO = new QuizDAO();

        // Test 1: Create a quiz
        System.out.println("--- Test 1: Create a quiz ---");
        Quiz newQuiz = new Quiz(0, "Java Basics", "Test your Java knowledge", 1);
        try {
            int quizId = quizDAO.createQuiz(newQuiz);
            if (quizId != -1) {
                System.out.println("Quiz created successfully! Quiz ID: " + quizId);
            } else {
                System.out.println("Failed to create quiz.");
            }
        } catch (Exception e) {
            System.out.println("Create quiz error: " + e.getMessage());
        }

        // Test 2: Get quiz by ID
        System.out.println("\n--- Test 2: Get quiz by ID ---");
        try {
            Quiz quiz = quizDAO.getQuizById(1);
            if (quiz != null) {
                System.out.println("Quiz found: " + quiz.getTitle());
                System.out.println("Description: " + quiz.getDescription());
            } else {
                System.out.println("Quiz not found.");
            }
        } catch (Exception e) {
            System.out.println("Get quiz error: " + e.getMessage());
        }

        // Test 3: Get all quizzes
        System.out.println("\n--- Test 3: Get all quizzes ---");
        try {
            List<Quiz> quizzes = quizDAO.getAllQuizzes();
            if (quizzes.isEmpty()) {
                System.out.println("No quizzes found.");
            } else {
                System.out.println("Total quizzes: " + quizzes.size());
                for (Quiz q : quizzes) {
                    System.out.println("  [" + q.getId() + "] " + q.getTitle());
                }
            }
        } catch (Exception e) {
            System.out.println("Get all quizzes error: " + e.getMessage());
        }
    }
}
