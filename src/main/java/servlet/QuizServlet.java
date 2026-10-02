package servlet;

import dao.QuestionDAO;
import dao.QuizDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Question;
import model.Quiz;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

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

        try {
            Quiz quiz = quizDAO.getQuizById(quizId);
            if (quiz == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            List<Question> questions = questionDAO.getQuestionsByQuizId(quizId);
            if (questions.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            request.setAttribute("quiz", quiz);
            request.setAttribute("questions", questions);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
