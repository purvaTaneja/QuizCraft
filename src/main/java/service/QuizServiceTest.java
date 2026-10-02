package service;

import model.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizServiceTest {

    public static void main(String[] args) {
        QuizService quizService = new QuizService();

        // Create 5 test questions
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(1, 1, "Q1", "A", "B", "C", "D", "A"));
        questions.add(new Question(2, 1, "Q2", "A", "B", "C", "D", "B"));
        questions.add(new Question(3, 1, "Q3", "A", "B", "C", "D", "C"));
        questions.add(new Question(4, 1, "Q4", "A", "B", "C", "D", "D"));
        questions.add(new Question(5, 1, "Q5", "A", "B", "C", "D", "A"));

        // Test 1: 3 correct out of 5
        System.out.println("--- Test 1: 3 correct out of 5 ---");
        Map<Integer, String> answers1 = new HashMap<>();
        answers1.put(1, "A"); // correct
        answers1.put(2, "B"); // correct
        answers1.put(3, "C"); // correct
        answers1.put(4, "A"); // wrong
        answers1.put(5, "B"); // wrong
        int score1 = quizService.calculateScore(questions, answers1);
        System.out.println("Score: " + score1 + "/5 (expected: 3)");

        // Test 2: All correct
        System.out.println("\n--- Test 2: All correct ---");
        Map<Integer, String> answers2 = new HashMap<>();
        answers2.put(1, "A");
        answers2.put(2, "B");
        answers2.put(3, "C");
        answers2.put(4, "D");
        answers2.put(5, "A");
        int score2 = quizService.calculateScore(questions, answers2);
        System.out.println("Score: " + score2 + "/5 (expected: 5)");

        // Test 3: All wrong
        System.out.println("\n--- Test 3: All wrong ---");
        Map<Integer, String> answers3 = new HashMap<>();
        answers3.put(1, "B");
        answers3.put(2, "C");
        answers3.put(3, "D");
        answers3.put(4, "A");
        answers3.put(5, "B");
        int score3 = quizService.calculateScore(questions, answers3);
        System.out.println("Score: " + score3 + "/5 (expected: 0)");

        // Test 4: Some unanswered
        System.out.println("\n--- Test 4: Some unanswered ---");
        Map<Integer, String> answers4 = new HashMap<>();
        answers4.put(1, "A"); // correct
        answers4.put(3, "C"); // correct
        // questions 2, 4, 5 are unanswered
        int score4 = quizService.calculateScore(questions, answers4);
        System.out.println("Score: " + score4 + "/5 (expected: 2)");
    }
}
