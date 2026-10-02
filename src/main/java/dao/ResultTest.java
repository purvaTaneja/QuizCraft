package dao;

import model.Result;

import java.util.List;

public class ResultTest {

    public static void main(String[] args) {
        ResultDAO resultDAO = new ResultDAO();

        // Test 1: Save a result for user ID 1 and quiz ID 1
        System.out.println("--- Test 1: Save a result ---");
        Result newResult = new Result(0, 1, 1, 8, 10);
        try {
            resultDAO.saveResult(newResult);
            System.out.println("Result saved successfully!");
        } catch (Exception e) {
            System.out.println("Save result error: " + e.getMessage());
        }

        // Test 2: Retrieve the result by ID
        System.out.println("\n--- Test 2: Get result by ID ---");
        try {
            Result result = resultDAO.getResultById(1);
            if (result != null) {
                System.out.println("Result found:");
                System.out.println("  User ID: " + result.getUserId());
                System.out.println("  Quiz ID: " + result.getQuizId());
                System.out.println("  Score: " + result.getScore() + "/" + result.getTotalQuestions());
            } else {
                System.out.println("Result not found.");
            }
        } catch (Exception e) {
            System.out.println("Get result error: " + e.getMessage());
        }

        // Test 3: Retrieve all results for user ID 1
        System.out.println("\n--- Test 3: Get all results for user ID 1 ---");
        try {
            List<Result> results = resultDAO.getResultsByUserId(1);
            if (results.isEmpty()) {
                System.out.println("No results found for user ID 1.");
            } else {
                System.out.println("Total results: " + results.size());
                for (Result r : results) {
                    System.out.println("  [" + r.getId() + "] Quiz " + r.getQuizId() + ": " + r.getScore() + "/" + r.getTotalQuestions());
                }
            }
        } catch (Exception e) {
            System.out.println("Get results error: " + e.getMessage());
        }
    }
}
