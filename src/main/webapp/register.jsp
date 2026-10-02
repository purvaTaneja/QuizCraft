<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuizCraft - Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="centered">
    <div class="container">
        <div class="hero">
            <div class="brand">
                <div class="brand-icon">QC</div>
                <div class="brand-name">QuizCraft</div>
            </div>
            <h1>Start your <span>Java journey.</span></h1>
            <p>Join QuizCraft and master programming concepts through interactive quizzes. Track your progress and level up your skills.</p>
            <div class="tagline">Learn. Practice. Level Up.</div>
            <div class="decorative">
                <span></span>
                <span></span>
                <span></span>
            </div>
        </div>
        <div class="form-section">
            <div class="form-card">
                <h2>Create Your Account</h2>
                <p class="subtitle">Join QuizCraft and start learning today</p>

                <% if ("exists".equals(request.getParameter("error"))) { %>
                    <div class="alert alert-error">An account with this email already exists.</div>
                <% } %>

                <% if ("1".equals(request.getParameter("error"))) { %>
                    <div class="alert alert-error">Something went wrong. Please try again.</div>
                <% } %>

                <form action="${pageContext.request.contextPath}/register" method="POST">
                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" placeholder="Choose a username" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" placeholder="Enter your email" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" placeholder="Create a password" required>
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm your password" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Create Account</button>
                </form>

                <div class="form-link">
                    Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
