package dao;

import model.TopicPerformance;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TopicPerformanceDAO {

    public void save(TopicPerformance performance) throws SQLException {
        String sql = "INSERT INTO result_topic_performance "
                + "(result_id, topic, correct_answers, total_questions, percentage) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, performance.getResultId());
            stmt.setString(2, performance.getTopic());
            stmt.setInt(3, performance.getCorrectAnswers());
            stmt.setInt(4, performance.getTotalQuestions());
            stmt.setDouble(5, performance.getPercentage());
            stmt.executeUpdate();
        }
    }

    public List<TopicPerformance> getByResultId(int resultId) throws SQLException {
        List<TopicPerformance> list = new ArrayList<>();
        String sql = "SELECT * FROM result_topic_performance WHERE result_id = ? "
                + "ORDER BY percentage DESC, topic ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resultId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new TopicPerformance(
                        rs.getInt("id"),
                        rs.getInt("result_id"),
                        rs.getString("topic"),
                        rs.getInt("correct_answers"),
                        rs.getInt("total_questions"),
                        rs.getDouble("percentage")
                ));
            }
        }
        return list;
    }
}
