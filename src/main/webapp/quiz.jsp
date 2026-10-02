<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Quiz" %>
<%@ page import="model.Question" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>

<%
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<Question> questions =
            (List<Question>) request.getAttribute("questions");

    if (quiz == null || questions == null || questions.isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }

    int totalQuestions = questions.size();
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>QuizCraft - <%= quiz.getTitle() %></title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

<div class="quiz-container">

    <!-- HEADER -->
    <div class="quiz-header">

        <div>
            <div class="quiz-title">
                <%= quiz.getTitle() %>
            </div>

            <div class="quiz-description">
                <%= quiz.getDescription() %>
            </div>
        </div>

        <div class="timer-badge" id="timer">
            <span>&#9201;</span>
            <span id="timerDisplay">10:00</span>
        </div>

    </div>


    <!-- PROGRESS -->
    <div class="progress-container">

        <div class="progress-info">

            <span class="progress-text" id="progressText">
                Question 1 of <%= totalQuestions %>
            </span>

            <span class="progress-text" id="progressPercent">
                0%
            </span>

        </div>

        <div class="progress-bar">
            <div class="progress-fill"
                 id="progressFill"
                 style="width: 0%;">
            </div>
        </div>

    </div>


    <!-- QUIZ FORM -->
    <form id="quizForm"
          action="${pageContext.request.contextPath}/submitQuiz"
          method="POST">

        <input type="hidden"
               name="quizId"
               value="<%= quiz.getId() %>">


        <!-- QUESTIONS -->

        <%
            for (int i = 0; i < questions.size(); i++) {

                Question q = questions.get(i);
        %>

        <div class="question-card"
             id="question-<%= i %>"
             style="display: <%= i == 0 ? "flex" : "none" %>;">

            <div class="question-number">
                Question <%= i + 1 %>
            </div>

            <div class="question-text">
                <%= q.getQuestionText() %>
            </div>


            <!-- OPTIONS -->

            <div class="options-grid">

                <label class="option-card"
                       id="option-<%= q.getId() %>-A">

                    <input type="radio"
                           name="answer_<%= q.getId() %>"
                           value="A">

                    <span class="option-letter">A</span>

                    <span class="option-text">
                        <%= q.getOptionA() %>
                    </span>

                </label>


                <label class="option-card"
                       id="option-<%= q.getId() %>-B">

                    <input type="radio"
                           name="answer_<%= q.getId() %>"
                           value="B">

                    <span class="option-letter">B</span>

                    <span class="option-text">
                        <%= q.getOptionB() %>
                    </span>

                </label>


                <label class="option-card"
                       id="option-<%= q.getId() %>-C">

                    <input type="radio"
                           name="answer_<%= q.getId() %>"
                           value="C">

                    <span class="option-letter">C</span>

                    <span class="option-text">
                        <%= q.getOptionC() %>
                    </span>

                </label>


                <label class="option-card"
                       id="option-<%= q.getId() %>-D">

                    <input type="radio"
                           name="answer_<%= q.getId() %>"
                           value="D">

                    <span class="option-letter">D</span>

                    <span class="option-text">
                        <%= q.getOptionD() %>
                    </span>

                </label>

            </div>

        </div>

        <%
            }
        %>


        <!-- QUESTION DOTS -->

        <div class="question-dots" id="questionDots">

            <%
                for (int i = 0; i < questions.size(); i++) {
            %>

            <span class="dot <%= i == 0 ? "active" : "" %>"
                  data-question="<%= i %>"
                  onclick="goToQuestion(<%= i %>)">
            </span>

            <%
                }
            %>

        </div>


        <!-- NAVIGATION -->

        <div class="quiz-nav" style="margin-top: 24px;">

            <button type="button"
                    class="btn-nav btn-prev"
                    id="prevBtn"
                    onclick="prevQuestion()"
                    disabled>

                &larr; Previous

            </button>


            <button type="button"
                    class="btn-nav btn-next"
                    id="nextBtn"
                    onclick="nextQuestion()">

                Next &rarr;

            </button>


            <button type="submit"
                    class="btn-nav btn-submit"
                    id="submitBtn"
                    style="display: none;">

                Submit Quiz

            </button>

        </div>

    </form>

</div>


<script>

    var currentQuestion = 0;

    var totalQuestions = <%= totalQuestions %>;

    var answeredQuestions = {};


    /* TIMER */

    var timerElement = document.getElementById("timer");

    var timerDisplay =
        document.getElementById("timerDisplay");

    var timeLeft = 10 * 60;


    function updateTimer() {

        var minutes = Math.floor(timeLeft / 60);

        var seconds = timeLeft % 60;


        timerDisplay.textContent =
            minutes + ":" +
            (seconds < 10 ? "0" : "") +
            seconds;


        if (timeLeft <= 60) {

            timerElement.classList.add("warning");

        }


        if (timeLeft <= 0) {

            document.getElementById("quizForm").submit();

            return;
        }


        timeLeft--;

        setTimeout(updateTimer, 1000);
    }


    updateTimer();


    /* PROGRESS */

    function updateProgress() {

        var answeredCount =
            Object.keys(answeredQuestions).length;


        var percent =
            Math.round(
                (answeredCount / totalQuestions) * 100
            );


        document.getElementById("progressText").textContent =
            "Question " +
            (currentQuestion + 1) +
            " of " +
            totalQuestions;


        document.getElementById("progressPercent").textContent =
            percent + "%";


        document.getElementById("progressFill").style.width =
            percent + "%";


        var dots =
            document.querySelectorAll(".dot");


        for (var i = 0; i < dots.length; i++) {

            dots[i].classList.remove("active");

        }


        if (dots[currentQuestion]) {

            dots[currentQuestion].classList.add("active");

        }


        for (var j = 0; j < dots.length; j++) {

            if (answeredQuestions[j]) {

                dots[j].classList.add("answered");

            }

        }


        document.getElementById("prevBtn").disabled =
            currentQuestion === 0;


        var nextBtn =
            document.getElementById("nextBtn");

        var submitBtn =
            document.getElementById("submitBtn");


        if (currentQuestion === totalQuestions - 1) {

            nextBtn.style.display = "none";

            submitBtn.style.display = "inline-flex";

        } else {

            nextBtn.style.display = "inline-flex";

            submitBtn.style.display = "none";

        }

    }


    /* GO TO QUESTION */

    function goToQuestion(index) {

        var cards =
            document.querySelectorAll(".question-card");


        for (var i = 0; i < cards.length; i++) {

            cards[i].style.display = "none";

        }


        if (cards[index]) {

            cards[index].style.display = "flex";

        }


        currentQuestion = index;

        updateProgress();

    }


    /* PREVIOUS */

    function prevQuestion() {

        if (currentQuestion > 0) {

            goToQuestion(currentQuestion - 1);

        }

    }


    /* NEXT */

    function nextQuestion() {

        if (currentQuestion < totalQuestions - 1) {

            goToQuestion(currentQuestion + 1);

        }

    }


    /* OPTION SELECTION */

    var optionCards =
        document.querySelectorAll(".option-card");


    for (var i = 0; i < optionCards.length; i++) {

        optionCards[i].addEventListener("click", function () {

            var radio =
                this.querySelector(
                    'input[type="radio"]'
                );


            var cards =
                document.querySelectorAll(".question-card");


            var questionIndex = -1;


            for (var j = 0; j < cards.length; j++) {

                if (cards[j].contains(this)) {

                    questionIndex = j;

                    break;

                }

            }


            var siblingOptions =
                this.parentElement.querySelectorAll(
                    ".option-card"
                );


            for (var k = 0; k < siblingOptions.length; k++) {

                siblingOptions[k]
                    .classList
                    .remove("selected");

            }


            this.classList.add("selected");


            radio.checked = true;


            if (questionIndex >= 0) {

                answeredQuestions[questionIndex] = true;

            }


            updateProgress();

        });

    }


    updateProgress();

</script>

</body>
</html>