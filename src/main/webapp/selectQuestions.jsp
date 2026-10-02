<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Quiz" %>
<%@ page import="model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Integer availableQuestions = (Integer) request.getAttribute("availableQuestions");

    if (quiz == null || availableQuestions == null) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }

    boolean can5 = availableQuestions >= 5;
    boolean can10 = availableQuestions >= 10;
    boolean can15 = availableQuestions >= 15;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuizCraft - Select Questions</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .selection-container {
            max-width: 500px;
            margin: 0 auto;
            padding: 40px 20px;
            text-align: center;
        }

        .selection-card {
            background: var(--bg-card);
            border: 1px solid var(--border);
            border-radius: 20px;
            padding: 48px 36px;
            box-shadow: var(--shadow-lg);
        }

        .selection-icon {
            width: 84px;
            height: 84px;
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 40px;
            margin: 0 auto 24px;
        }

        .selection-title {
            font-size: 28px;
            font-weight: 800;
            color: var(--text-primary);
            margin-bottom: 6px;
        }

        .selection-quiz {
            font-size: 15px;
            color: var(--text-muted);
            margin-bottom: 32px;
        }

        .selection-options {
            display: flex;
            flex-direction: column;
            gap: 12px;
            margin-bottom: 24px;
        }

        .selection-option {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            padding: 16px 24px;
            background: var(--bg-secondary);
            border: 2px solid var(--border);
            border-radius: 12px;
            font-size: 16px;
            font-weight: 700;
            color: var(--text-primary);
            cursor: pointer;
            transition: var(--transition);
            text-decoration: none;
        }

        .selection-option:hover:not(.disabled) {
            border-color: var(--primary);
            background: #eff6ff;
            color: var(--primary);
        }

        .selection-option.disabled {
            opacity: 0.4;
            cursor: not-allowed;
        }

        .selection-back {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 24px;
            background: var(--bg-secondary);
            border: 1px solid var(--border);
            border-radius: var(--radius-sm);
            font-size: 14px;
            font-weight: 600;
            color: var(--text-secondary);
            text-decoration: none;
            transition: var(--transition);
        }

        .selection-back:hover {
            background: var(--bg-hover);
            color: var(--text-primary);
        }

        @media (max-width: 768px) {
            .selection-card {
                padding: 36px 24px;
            }
        }
    </style>
</head>
<body class="centered">
    <div class="selection-container">
        <div class="selection-card">
            <div class="selection-icon">&#128218;</div>
            <h1 class="selection-title">How many questions?</h1>
            <p class="selection-quiz"><%= quiz.getTitle() %> &mdash; <%= availableQuestions %> questions available</p>

            <div class="selection-options">
                <% if (can5) { %>
                    <a href="${pageContext.request.contextPath}/quiz?quizId=<%= quiz.getId() %>&count=5" class="selection-option">5 Questions</a>
                <% } else { %>
                    <span class="selection-option disabled">5 Questions (not enough questions)</span>
                <% } %>

                <% if (can10) { %>
                    <a href="${pageContext.request.contextPath}/quiz?quizId=<%= quiz.getId() %>&count=10" class="selection-option">10 Questions</a>
                <% } else { %>
                    <span class="selection-option disabled">10 Questions (not enough questions)</span>
                <% } %>

                <% if (can15) { %>
                    <a href="${pageContext.request.contextPath}/quiz?quizId=<%= quiz.getId() %>&count=15" class="selection-option">15 Questions</a>
                <% } else { %>
                    <span class="selection-option disabled">15 Questions (not enough questions)</span>
                <% } %>
            </div>

            <a href="${pageContext.request.contextPath}/home" class="selection-back">&larr; Back to Dashboard</a>
        </div>
    </div>
</body>
</html>
