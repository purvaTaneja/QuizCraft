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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    /** Session key holding the question IDs served for the current attempt. */
    public static final String ATTEMPT_QUESTION_IDS = "attemptQuestionIds";
    /** Session key holding the course the current attempt belongs to. */
    public static final String ATTEMPT_QUIZ_ID = "attemptQuizId";

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

            int availableQuestions = questionDAO.getQuestionCountByQuizId(quizId);
            if (availableQuestions == 0) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            String countParam = request.getParameter("count");

            if (countParam == null || countParam.isEmpty()) {
                // No question count chosen yet: show the selection screen.
                request.setAttribute("quiz", quiz);
                request.setAttribute("availableQuestions", availableQuestions);

                RequestDispatcher dispatcher = request.getRequestDispatcher("/selectQuestions.jsp");
                dispatcher.forward(request, response);
                return;
            }

            int count;
            try {
                count = Integer.parseInt(countParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/quiz?quizId=" + quizId);
                return;
            }

            if (count <= 0) {
                response.sendRedirect(request.getContextPath() + "/quiz?quizId=" + quizId);
                return;
            }

            // Never serve more questions than the course actually has.
            if (count > availableQuestions) {
                count = availableQuestions;
            }

            List<Question> questions = questionDAO.getQuestionsByQuizId(quizId, count);
            if (questions.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            // Record exactly which questions were served for this attempt.
            List<Integer> questionIds = new ArrayList<>();
            for (Question question : questions) {
                questionIds.add(question.getId());
            }
            session.setAttribute(ATTEMPT_QUESTION_IDS, questionIds);
            session.setAttribute(ATTEMPT_QUIZ_ID, quizId);

            request.setAttribute("quiz", quiz);
            request.setAttribute("questions", questions);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
