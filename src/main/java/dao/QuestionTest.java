package dao;

import model.Question;

import java.util.List;

public class QuestionTest {

    public static void main(String[] args) {
        QuestionDAO questionDAO = new QuestionDAO();

        // Test 1: Add a question to quiz ID 1
        System.out.println("--- Test 1: Add a question ---");
        Question newQuestion = new Question(0, 1, "What is the capital of France?",
            "London", "Paris", "Berlin", "Madrid", "B");
        try {
            questionDAO.addQuestion(newQuestion);
            System.out.println("Question added successfully!");
        } catch (Exception e) {
            System.out.println("Add question error: " + e.getMessage());
        }

        // Test 2: Get all questions for quiz ID 1
        System.out.println("\n--- Test 2: Get all questions for quiz ID 1 ---");
        try {
            List<Question> questions = questionDAO.getQuestionsByQuizId(1);
            if (questions.isEmpty()) {
                System.out.println("No questions found for quiz ID 1.");
            } else {
                System.out.println("Total questions: " + questions.size());
                for (Question q : questions) {
                    System.out.println("  [" + q.getId() + "] " + q.getQuestionText());
                }
            }
        } catch (Exception e) {
            System.out.println("Get questions error: " + e.getMessage());
        }

        // Test 3: Get a question by its ID
        System.out.println("\n--- Test 3: Get question by ID ---");
        try {
            Question question = questionDAO.getQuestionById(1);
            if (question != null) {
                System.out.println("Question found: " + question.getQuestionText());
                System.out.println("  A: " + question.getOptionA());
                System.out.println("  B: " + question.getOptionB());
                System.out.println("  C: " + question.getOptionC());
                System.out.println("  D: " + question.getOptionD());
                System.out.println("  Correct: " + question.getCorrectAnswer());
            } else {
                System.out.println("Question not found.");
            }
        } catch (Exception e) {
            System.out.println("Get question error: " + e.getMessage());
        }

        // Test 4: Delete a question
        System.out.println("\n--- Test 4: Delete question ID 1 ---");
        try {
            boolean deleted = questionDAO.deleteQuestion(1);
            if (deleted) {
                System.out.println("Question deleted successfully!");
            } else {
                System.out.println("Question not found or already deleted.");
            }
        } catch (Exception e) {
            System.out.println("Delete question error: " + e.getMessage());
        }
    }
}
