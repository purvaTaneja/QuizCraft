<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.QuizDAO" %>
<%@ page import="dao.QuestionDAO" %>
<%@ page import="model.Quiz" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String quizIdParam = request.getParameter("quizId");
    String scoreParam = request.getParameter("score");

    QuizDAO quizDAO = new QuizDAO();
    QuestionDAO questionDAO = new QuestionDAO();

    Quiz quiz = null;
    int totalQuestions = 0;
    int score = 0;

    try {
        if (quizIdParam != null && !quizIdParam.isEmpty()) {
            int quizId = Integer.parseInt(quizIdParam);
            quiz = quizDAO.getQuizById(quizId);
            List<model.Question> questions = questionDAO.getQuestionsByQuizId(quizId);
            totalQuestions = questions.size();
        }
    } catch (Exception e) {
        // Handle gracefully
    }

    if (scoreParam != null && !scoreParam.isEmpty()) {
        try {
            score = Integer.parseInt(scoreParam);
        } catch (NumberFormatException e) {
            score = 0;
        }
    }

    String quizTitle = (quiz != null) ? quiz.getTitle() : "Quiz";
    String message;
    String messageColor;
    if (totalQuestions > 0) {
        double percentage = (double) score / totalQuestions * 100;
        if (percentage >= 80) {
            message = "Outstanding! You're a Java pro!";
            messageColor = "#86efac";
        } else if (percentage >= 60) {
            message = "Good job! Keep practicing!";
            messageColor = "#a5b4fc";
        } else if (percentage >= 40) {
            message = "Not bad! Review and try again!";
            messageColor = "#fcd34d";
        } else {
            message = "Keep learning! You'll get there!";
            messageColor = "#fca5a5";
        }
    } else {
        message = "Quiz completed!";
        messageColor = "#a5b4fc";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuizCraft - Result</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="centered">
    <div class="result-container">
        <div class="result-card">
            <div class="result-icon">&#127942;</div>
            <h1 class="result-title">Quiz Completed!</h1>
            <p class="result-quiz"><%= quizTitle %></p>

            <div class="score-display">
                <div class="score-number"><%= score %></div>
                <% if (totalQuestions > 0) { %>
                    <div class="score-total">out of <%= totalQuestions %></div>
                <% } %>
            </div>

            <p class="result-message" style="color: <%= messageColor %>;"><%= message %></p>

            <div class="result-actions">
                <a href="${pageContext.request.contextPath}/quiz?quizId=<%= quizIdParam %>" class="btn btn-primary">Try Again</a>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Back to Dashboard</a>
            </div>
        </div>
    </div>
</body>
</html>
