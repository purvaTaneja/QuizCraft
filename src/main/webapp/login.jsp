<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuizCraft - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="centered">
    <div class="container">
        <div class="hero">
            <div class="brand">
                <div class="brand-icon">QC</div>
                <div class="brand-name">QuizCraft</div>
            </div>
            <h1>Level up your <span>Java skills.</span></h1>
            <p>Learn. Practice. Challenge yourself. Master programming concepts through interactive quizzes designed for developers.</p>
            <div class="tagline">Learn. Practice. Level Up.</div>
            <div class="decorative">
                <span></span>
                <span></span>
                <span></span>
            </div>
        </div>
        <div class="form-section">
            <div class="form-card">
                <h2>Welcome Back!</h2>
                <p class="subtitle">Sign in to continue your learning journey</p>

                <% if ("1".equals(request.getParameter("error"))) { %>
                    <div class="alert alert-error">Invalid email or password. Please try again.</div>
                <% } %>

                <% if ("1".equals(request.getParameter("registered"))) { %>
                    <div class="alert alert-success">Account created successfully. Welcome to QuizCraft!</div>
                <% } %>

                <form action="${pageContext.request.contextPath}/login" method="POST">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" placeholder="Enter your email" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" placeholder="Enter your password" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Sign In</button>
                </form>

                <div class="form-link">
                    Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
