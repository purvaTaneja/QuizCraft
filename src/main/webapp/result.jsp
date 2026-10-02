<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="model.TopicPerformance" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String quizTitle = (String) request.getAttribute("quizTitle");
    if (quizTitle == null) {
        quizTitle = "Quiz";
    }

    Integer scoreObj = (Integer) request.getAttribute("score");
    int score = (scoreObj != null) ? scoreObj : 0;

    Integer totalObj = (Integer) request.getAttribute("totalQuestions");
    int totalQuestions = (totalObj != null) ? totalObj : 0;

    Object quizIdAttr = request.getAttribute("quizId");
    int quizId = (quizIdAttr instanceof Integer) ? (Integer) quizIdAttr : 0;

    @SuppressWarnings("unchecked")
    Map<String, TopicPerformance> analysis =
            (Map<String, TopicPerformance>) request.getAttribute("topicAnalysis");

    TopicPerformance strongest = (TopicPerformance) request.getAttribute("strongestTopic");
    TopicPerformance weakest = (TopicPerformance) request.getAttribute("weakestTopic");
    Boolean tieObj = (Boolean) request.getAttribute("topicTie");
    boolean tie = (tieObj != null) && tieObj;

    String feedback = (String) request.getAttribute("feedbackMessage");
    String suggestion = (String) request.getAttribute("practiceSuggestion");

    if (analysis == null) {
        analysis = new java.util.LinkedHashMap<>();
    }
    if (feedback == null) {
        feedback = "Topic analysis was not available for this attempt.";
    }
    if (suggestion == null) {
        suggestion = "";
    }

    double percentage = (totalQuestions > 0) ? ((double) score / totalQuestions * 100) : 0;

    java.text.DecimalFormat df = new java.text.DecimalFormat("#.#");
    String percentageLabel = df.format(percentage) + "%";

    // Confetti celebration threshold: 70% or higher.
    boolean celebrate = (totalQuestions > 0) && (percentage >= 70);

    String message;
    String messageClass;
    if (percentage >= 80) {
        message = "Outstanding! You have a strong command of these concepts.";
        messageClass = "result-success";
    } else if (percentage >= 60) {
        message = "Good job! Keep practicing to reach mastery.";
        messageClass = "result-good";
    } else if (percentage >= 40) {
        message = "Fair attempt. Review the topics below and try again.";
        messageClass = "result-fair";
    } else {
        message = "Keep learning! Every attempt makes you stronger.";
        messageClass = "result-encourage";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuizCraft - Result</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .result-container {
            max-width: 640px;
            margin: 0 auto;
            padding: 40px 20px;
            text-align: center;
        }

        .result-card {
            background: var(--bg-card);
            border: 1px solid var(--border);
            border-radius: 20px;
            padding: 44px 36px;
            box-shadow: var(--shadow-lg);
        }

        .result-icon {
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

        .result-title {
            font-size: 28px;
            font-weight: 800;
            color: var(--text-primary);
            margin-bottom: 6px;
        }

        .result-quiz {
            font-size: 15px;
            color: var(--text-muted);
            margin-bottom: 28px;
        }

        .score-display {
            margin-bottom: 12px;
        }

        .score-number {
            font-size: 72px;
            font-weight: 900;
            color: var(--primary);
            line-height: 1;
        }

        .score-total {
            font-size: 20px;
            color: var(--text-muted);
            margin-top: 8px;
        }

        .score-percent {
            display: inline-block;
            margin-top: 12px;
            padding: 6px 18px;
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            border-radius: 50px;
            font-size: 15px;
            font-weight: 700;
            color: var(--primary);
        }

        .result-message {
            font-size: 17px;
            font-weight: 600;
            margin: 20px 0 32px;
            line-height: 1.6;
        }

        .result-success { color: var(--success); }
        .result-good { color: var(--primary); }
        .result-fair { color: var(--warning); }
        .result-encourage { color: var(--text-secondary); }

        /* ---------- Topic analysis ---------- */
        .analysis-section {
            text-align: left;
            margin-bottom: 28px;
        }

        .analysis-heading {
            font-size: 18px;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 16px;
            padding-bottom: 10px;
            border-bottom: 1px solid var(--border);
        }

        .topic-highlight {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
            margin-bottom: 20px;
        }

        .topic-box {
            padding: 16px;
            border-radius: 12px;
            border: 1px solid var(--border);
        }

        .topic-box-best {
            background: #f0fdf4;
            border-color: #bbf7d0;
        }

        .topic-box-weak {
            background: #fef2f2;
            border-color: #fecaca;
        }

        .topic-box-label {
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.6px;
            margin-bottom: 6px;
        }

        .topic-box-best .topic-box-label { color: var(--success); }
        .topic-box-weak .topic-box-label { color: var(--error); }

        .topic-box-name {
            font-size: 15px;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 4px;
        }

        .topic-box-score {
            font-size: 13px;
            color: var(--text-secondary);
        }

        .topic-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .topic-row {
            display: grid;
            grid-template-columns: 1fr auto;
            align-items: center;
            gap: 12px;
            padding: 12px 14px;
            background: var(--bg-secondary);
            border: 1px solid var(--border);
            border-radius: 10px;
        }

        .topic-row-name {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary);
        }

        .topic-row-detail {
            font-size: 12px;
            color: var(--text-muted);
            margin-top: 2px;
        }

        .topic-bar-wrap {
            width: 120px;
        }

        .topic-bar {
            height: 7px;
            background: var(--border);
            border-radius: 10px;
            overflow: hidden;
            margin-bottom: 4px;
        }

        .topic-bar-fill {
            height: 100%;
            background: var(--gradient-primary);
            border-radius: 10px;
        }

        .topic-bar-value {
            font-size: 12px;
            font-weight: 700;
            color: var(--primary);
            text-align: right;
        }

        .feedback-box {
            text-align: left;
            padding: 18px;
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            border-radius: 12px;
            margin-bottom: 24px;
        }

        .feedback-box p {
            font-size: 14px;
            color: var(--text-secondary);
            line-height: 1.7;
        }

        .tie-note {
            font-size: 13px;
            color: var(--text-muted);
            font-style: italic;
            margin-bottom: 16px;
        }

        .result-actions {
            display: flex;
            gap: 14px;
            justify-content: center;
            flex-wrap: wrap;
        }

        @media (max-width: 768px) {
            .result-card { padding: 32px 22px; }
            .score-number { font-size: 52px; }
            .topic-highlight { grid-template-columns: 1fr; }
            .topic-row { grid-template-columns: 1fr; }
            .topic-bar-wrap { width: 100%; }
            .result-actions { flex-direction: column; }
            .result-actions .btn { width: 100%; justify-content: center; }
        }
    </style>
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
                <% if (totalQuestions > 0) { %>
                    <div class="score-percent"><%= percentageLabel %></div>
                <% } %>
            </div>

            <p class="result-message <%= messageClass %>"><%= message %></p>

            <% if (!analysis.isEmpty()) { %>
                <div class="analysis-section">
                    <h2 class="analysis-heading">Your Performance</h2>

                    <% if (tie) { %>
                        <p class="tie-note">Every topic scored the same in this attempt, so there is no clear strongest or weakest area yet.</p>
                    <% } else { %>
                        <div class="topic-highlight">
                            <div class="topic-box topic-box-best">
                                <div class="topic-box-label">Strongest Area</div>
                                <div class="topic-box-name"><%= strongest.getTopic() %></div>
                                <div class="topic-box-score">
                                    <%= strongest.getCorrectAnswers() %>/<%= strongest.getTotalQuestions() %>
                                    &middot; <%= df.format(strongest.getPercentage()) %>%>
                                </div>
                            </div>
                            <div class="topic-box topic-box-weak">
                                <div class="topic-box-label">Needs More Practice</div>
                                <div class="topic-box-name"><%= weakest.getTopic() %></div>
                                <div class="topic-box-score">
                                    <%= weakest.getCorrectAnswers() %>/<%= weakest.getTotalQuestions() %>
                                    &middot; <%= df.format(weakest.getPercentage()) %>%>
                                </div>
                            </div>
                        </div>
                    <% } %>

                    <div class="topic-list">
                        <% for (TopicPerformance performance : analysis.values()) { %>
                            <div class="topic-row">
                                <div>
                                    <div class="topic-row-name"><%= performance.getTopic() %></div>
                                    <div class="topic-row-detail">
                                        <%= performance.getCorrectAnswers() %> correct out of <%= performance.getTotalQuestions() %>
                                    </div>
                                </div>
                                <div class="topic-bar-wrap">
                                    <div class="topic-bar">
                                        <div class="topic-bar-fill" style="width: <%= performance.getPercentage() %>%"></div>
                                    </div>
                                    <div class="topic-bar-value"><%= df.format(performance.getPercentage()) %>%</div>
                                </div>
                            </div>
                        <% } %>
                    </div>
                </div>

                <div class="feedback-box">
                    <p><%= feedback %></p>
                </div>
            <% } %>

            <div class="result-actions">
                <a href="${pageContext.request.contextPath}/quiz?quizId=<%= quizId %>" class="btn btn-primary">Try Again</a>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Back to Dashboard</a>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/confetti.js"></script>
    <script>
        (function () {
            var shouldCelebrate = <%= celebrate %>;

            if (!shouldCelebrate || !window.QuizCraftConfetti) {
                return;
            }

            // Fire after the result has actually painted, so the score is
            // visible before the celebration begins.
            function fire() {
                window.QuizCraftConfetti.celebrate();
            }

            if (document.readyState === "complete") {
                window.setTimeout(fire, 350);
            } else {
                window.addEventListener("load", function () {
                    window.setTimeout(fire, 350);
                });
            }
        })();
    </script>
</body>
</html>
