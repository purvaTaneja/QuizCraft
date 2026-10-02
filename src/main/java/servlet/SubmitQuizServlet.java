package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.QuizService;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/submitQuiz")
public class SubmitQuizServlet extends HttpServlet {

    private QuizService quizService = new QuizService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String quizIdParam = request.getParameter("quizId");
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

        Map<Integer, String> userAnswers = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (paramName.startsWith("answer_")) {
                try {
                    int questionId = Integer.parseInt(paramName.substring(7));
                    String answer = request.getParameter(paramName);
                    userAnswers.put(questionId, answer);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        try {
            int score = quizService.submitQuiz(quizId, user.getId(), userAnswers);
            response.sendRedirect(request.getContextPath() + "/result?quizId=" + quizId + "&score=" + score);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/quiz?quizId=" + quizId);
        }
    }
}
