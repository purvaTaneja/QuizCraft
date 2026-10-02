# QuizCraft

## A Java-Based Online Quiz Platform

QuizCraft is a Java-based online quiz platform designed to provide a simple and interactive way for users to take quizzes and evaluate their knowledge.

The platform allows users to register, log in, attempt Java quizzes, receive automatic scores, and view their results.

## Features

- User Registration
- User Login and Logout
- Session-based Authentication
- Protected Quiz and Result Pages
- Java-based Multiple Choice Quizzes
- One Question at a Time Quiz Interface
- Quiz Progress Indicator
- Countdown Timer
- Automatic Score Calculation
- Result Display
- Responsive and Modern User Interface
- MariaDB Database Integration

## Quiz Flow

1. User creates an account.
2. User logs into QuizCraft.
3. Available quizzes are displayed.
4. User starts a quiz.
5. Questions are displayed one at a time.
6. User selects answers and navigates through the quiz.
7. The quiz is submitted.
8. The system automatically calculates the score.
9. The result is saved in the database.
10. The user can view the final result.

## Technologies Used

- Java
- JSP
- Jakarta Servlets
- JDBC
- Maven
- MariaDB
- HTML
- CSS
- JavaScript
- Apache Tomcat

## Project Structure

```text
QuizCraft/
│
├── src/
│   └── main/
│       ├── java/
│       │   ├── dao/
│       │   ├── model/
│       │   ├── service/
│       │   ├── servlet/
│       │   └── util/
│       │
│       ├── resources/
│       │   └── schema.sql
│       │
│       └── webapp/
│           ├── css/
│           │   └── style.css
│           ├── WEB-INF/
│           │   └── web.xml
│           ├── login.jsp
│           ├── register.jsp
│           ├── home.jsp
│           ├── quiz.jsp
│           └── result.jsp
│
├── pom.xml
├── .gitignore
└── README.md
