package dao;

import model.Result;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public void saveResult(Result result) throws SQLException {
        String sql = "INSERT INTO results (user_id, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, result.getUserId());
            stmt.setInt(2, result.getQuizId());
            stmt.setInt(3, result.getScore());
            stmt.setInt(4, result.getTotalQuestions());
            stmt.executeUpdate();
        }
    }

    public List<Result> getResultsForUser(int userId) throws SQLException {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new Result(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions")
                ));
            }
        }
        return results;
    }

    public List<Result> getResultsByUserId(int userId) throws SQLException {
        return getResultsForUser(userId);
    }

    public Result getResultById(int id) throws SQLException {
        String sql = "SELECT * FROM results WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Result(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions")
                );
            }
        }
        return null;
    }
}
