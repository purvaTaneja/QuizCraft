package servlet;

import dao.ResultDAO;
import dao.TopicPerformanceDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Result;
import model.TopicPerformance;
import model.User;
import service.FeedbackService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    private ResultDAO resultDAO = new ResultDAO();
    private TopicPerformanceDAO topicPerformanceDAO = new TopicPerformanceDAO();
    private FeedbackService feedbackService = new FeedbackService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String quizIdParam = request.getParameter("quizId");
        String scoreParam = request.getParameter("score");
        String resultIdParam = request.getParameter("resultId");

        if (quizIdParam == null || quizIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int quizId;
        try {
            quizId = Integer.parseInt(quizIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int resultId = -1;
        if (resultIdParam != null && !resultIdParam.isEmpty()) {
            try {
                resultId = Integer.parseInt(resultIdParam);
            } catch (NumberFormatException ignored) {
                resultId = -1;
            }
        }

        try {
            String quizTitle = "Quiz";
            try {
                model.Quiz quiz = new dao.QuizDAO().getQuizById(quizId);
                if (quiz != null) {
                    quizTitle = quiz.getTitle();
                }
            } catch (Exception ignored) {
                // Fall back to a generic title.
            }

            int score = 0;
            int totalQuestions = 0;

            // Prefer the stored attempt so totals and topics match exactly.
            if (resultId > 0) {
                try {
                    Result stored = resultDAO.getResultById(resultId);
                    if (stored != null) {
                        score = stored.getScore();
                        totalQuestions = stored.getTotalQuestions();
                    }
                } catch (Exception ignored) {
                    // Fall through to query-parameter values.
                }
            }

            if (score == 0 && scoreParam != null && !scoreParam.isEmpty()) {
                try {
                    score = Integer.parseInt(scoreParam);
                } catch (NumberFormatException ignored) {
                    score = 0;
                }
            }

            if (totalQuestions == 0) {
                try {
                    int latestId = resultDAO.getLatestResultId(user.getId(), quizId);
                    Result latest = resultDAO.getResultById(latestId);
                    if (latest != null) {
                        totalQuestions = latest.getTotalQuestions();
                    }
                } catch (Exception ignored) {
                    // Leave total at 0 if it cannot be resolved.
                }
            }

            // Topic-level performance for this attempt.
            Map<String, TopicPerformance> analysis = new LinkedHashMap<>();
            if (resultId > 0) {
                try {
                    List<TopicPerformance> performances = topicPerformanceDAO.getByResultId(resultId);
                    for (TopicPerformance performance : performances) {
                        analysis.put(performance.getTopic(), performance);
                    }
                } catch (Exception ignored) {
                    // Topic analysis stays empty if unavailable.
                }
            }

            TopicPerformance strongest = feedbackService.getStrongestTopic(analysis);
            TopicPerformance weakest = feedbackService.getWeakestTopic(analysis);
            boolean tie = feedbackService.isTie(analysis);
            String feedback = feedbackService.buildFeedbackMessage(strongest, weakest, analysis);
            String suggestion = (weakest != null)
                    ? feedbackService.getPracticeSuggestion(weakest)
                    : "Keep practising regularly to build a strong foundation.";

            request.setAttribute("quizTitle", quizTitle);
            request.setAttribute("score", score);
            request.setAttribute("totalQuestions", totalQuestions);
            request.setAttribute("quizId", quizId);
            request.setAttribute("resultId", resultId);
            request.setAttribute("topicAnalysis", analysis);
            request.setAttribute("strongestTopic", strongest);
            request.setAttribute("weakestTopic", weakest);
            request.setAttribute("topicTie", tie);
            request.setAttribute("feedbackMessage", feedback);
            request.setAttribute("practiceSuggestion", suggestion);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/result.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
