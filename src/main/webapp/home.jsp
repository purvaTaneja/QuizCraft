<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.QuizDAO" %>
<%@ page import="dao.ResultDAO" %>
<%@ page import="model.Quiz" %>
<%@ page import="model.Result" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    QuizDAO quizDAO = new QuizDAO();
    ResultDAO resultDAO = new ResultDAO();

    List<Quiz> quizzes = null;
    List<Result> results = null;
    try {
        quizzes = quizDAO.getAllQuizzes();
        results = resultDAO.getResultsByUserId(user.getId());
    } catch (Exception e) {
        quizzes = new java.util.ArrayList<>();
        results = new java.util.ArrayList<>();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuizCraft - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="navbar-brand">
            <div class="brand-icon">QC</div>
            <div class="brand-name">QuizCraft</div>
        </div>
        <div class="navbar-nav">
            <a href="${pageContext.request.contextPath}/home" class="active">Dashboard</a>
        </div>
        <div class="user-menu">
            <div class="user-avatar"><%= user.getUsername().substring(0, 1).toUpperCase() %></div>
            <span class="user-name"><%= user.getUsername() %></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <div class="dashboard">
        <div class="hero-section">
            <h1>Hey, <span><%= user.getUsername() %></span>!</h1>
            <p>Ready to level up your Java skills?</p>
            <div class="motivation-pill">Learn. Practice. Challenge yourself.</div>
        </div>

        <div class="section-header">
            <h2>Choose Your Challenge</h2>
        </div>
        <div class="quiz-grid">
            <% for (Quiz quiz : quizzes) { %>
                <div class="quiz-card">
                    <div class="quiz-card-icon">&#128187;</div>
                    <h3><%= quiz.getTitle() %></h3>
                    <p><%= quiz.getDescription() %></p>
                    <a href="${pageContext.request.contextPath}/quiz?quizId=<%= quiz.getId() %>" class="btn btn-primary">Start Quiz &rarr;</a>
                </div>
            <% } %>
            <% if (quizzes.isEmpty()) { %>
                <div class="empty-state">
                    <div class="empty-state-icon">&#127919;</div>
                    <p>No quizzes available yet. Check back soon!</p>
                </div>
            <% } %>
        </div>

        <div class="section-header">
            <h2>Recent Results</h2>
        </div>
        <% if (results.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">&#128640;</div>
                <p>Your quiz journey starts here. Take your first quiz!</p>
            </div>
        <% } else { %>
            <div class="results-table">
                <div class="results-table-header">
                    <span>Quiz ID</span>
                    <span>Score</span>
                    <span>Total Questions</span>
                </div>
                <% for (Result result : results) { %>
                    <div class="results-table-row">
                        <span>Quiz #<%= result.getQuizId() %></span>
                        <span><span class="score-badge"><%= result.getScore() %></span></span>
                        <span><%= result.getTotalQuestions() %></span>
                    </div>
                <% } %>
            </div>
        <% } %>

        <div class="progress-section">
            <h3>Keep learning. Keep improving.</h3>
            <p>Every quiz you take is a step forward in your Java journey.</p>
        </div>
    </div>
</body>
</html>
