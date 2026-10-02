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
import java.util.List;
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

        // Collect the user's answers: answer_<questionId> = A|B|C|D
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

        // Use the exact set of questions that were served for this attempt.
        @SuppressWarnings("unchecked")
        List<Integer> questionIds =
                (List<Integer>) session.getAttribute(QuizServlet.ATTEMPT_QUESTION_IDS);

        Integer attemptQuizId = (Integer) session.getAttribute(QuizServlet.ATTEMPT_QUIZ_ID);

        // Fall back to scoring the submitted answers when the attempt was not recorded.
        if (questionIds == null || questionIds.isEmpty()
                || attemptQuizId == null || attemptQuizId != quizId) {

            session.removeAttribute(QuizServlet.ATTEMPT_QUESTION_IDS);
            session.removeAttribute(QuizServlet.ATTEMPT_QUIZ_ID);

            response.sendRedirect(request.getContextPath() + "/quiz?quizId=" + quizId);
            return;
        }

        try {
            int resultId = quizService.submitQuizByQuestionIds(quizId, user.getId(), userAnswers, questionIds);

            // Clear the attempt so a refresh cannot re-submit the same attempt.
            session.removeAttribute(QuizServlet.ATTEMPT_QUESTION_IDS);
            session.removeAttribute(QuizServlet.ATTEMPT_QUIZ_ID);

            int score = 0;
            try {
                model.Result saved = new dao.ResultDAO().getResultById(resultId);
                if (saved != null) {
                    score = saved.getScore();
                }
            } catch (Exception ignored) {
                // Score display falls back to 0 if the lookup fails.
            }

            response.sendRedirect(request.getContextPath()
                    + "/result?resultId=" + resultId + "&quizId=" + quizId + "&score=" + score);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/quiz?quizId=" + quizId);
        }
    }
}
