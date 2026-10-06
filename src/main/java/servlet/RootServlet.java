package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

/**
 * Entry point for the application root ("/").
 *
 * The quiz application has no page of its own at "/", so this servlet forwards
 * visitors to the login page. Users who already have a session are sent straight
 * to their dashboard instead.
 */
@WebServlet("")
public class RootServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user != null) {
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}