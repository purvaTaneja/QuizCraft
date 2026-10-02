package servlet;

import dao.QuizDAO;
import dao.QuestionDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Quiz;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    private QuizDAO quizDAO = new QuizDAO();
    private QuestionDAO questionDAO = new QuestionDAO();

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

        if (quizIdParam == null || quizIdParam.isEmpty() || scoreParam == null || scoreParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int quizId;
        int score;
        try {
            quizId = Integer.parseInt(quizIdParam);
            score = Integer.parseInt(scoreParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            Quiz quiz = quizDAO.getQuizById(quizId);
            String quizTitle = (quiz != null) ? quiz.getTitle() : "Unknown Quiz";

            List<model.Question> questions = questionDAO.getQuestionsByQuizId(quizId);
            int totalQuestions = questions.size();

            request.setAttribute("quizTitle", quizTitle);
            request.setAttribute("score", score);
            request.setAttribute("totalQuestions", totalQuestions);
            request.setAttribute("quizId", quizId);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/result.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
