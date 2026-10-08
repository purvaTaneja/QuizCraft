# QuizCraft

## A Java-Based Online Quiz Platform

QuizCraft is a Java-based online quiz platform that allows users to register, log in, attempt interactive quizzes, receive instant results, and track their quiz performance.

The project is built using Java, JSP, Jakarta Servlets, JDBC, MySQL/MariaDB, HTML, CSS, and JavaScript, with a responsive web interface and database-backed quiz management.

## 🌐 Live Demo

**QuizCraft:**  
https://quizcraft-f9jq.onrender.com/login

---

## ✨ Features

### 🔐 User Authentication

- User registration
- User login and logout
- Session-based authentication
- Protected quiz and result pages
- Duplicate email validation
- Login validation
- User-specific quiz results

### 📚 Quiz Dashboard

- Personalized dashboard after login
- Displays available quizzes
- Quiz title and description
- Easy navigation to available courses
- Clean and responsive card-based interface

### 📝 Interactive Quiz System

- Multiple-choice questions
- One question displayed at a time
- Question navigation
- Progress indicator
- Answer selection
- Countdown timer
- Automatic quiz submission
- Support for different quiz lengths
- Random question selection for quiz attempts
- Prevents duplicate questions within the same attempt

### ⏱️ Quiz Timer

- Countdown timer during an active quiz
- Helps simulate a real examination environment
- Automatically manages the quiz duration

### 📊 Automatic Evaluation

- Answers are evaluated automatically
- Score is calculated immediately after submission
- Supports unanswered questions
- Displays score based on attempted questions

### 🏆 Result System

- Displays final quiz score
- Shows score percentage
- Displays total questions
- Shows quiz completion status
- Stores quiz attempts in the database
- Users can view their previous quiz results

### 📈 Topic-Wise Performance

QuizCraft analyzes performance at the topic level.

The result page can provide:

- Topic-wise performance
- Correct and incorrect answers by topic
- Performance breakdown for the completed attempt
- Better understanding of strong and weak areas

### 📋 Recent Results

Users can view their previous quiz attempts through the Recent Results section.

It includes:

- Quiz number
- Score obtained
- Total questions
- Previous attempt records

### 🗄️ Database Integration

QuizCraft uses a relational database to store application data.

The database manages:

- Users
- Quizzes
- Questions
- Quiz results
- Topic-wise performance

Database access is implemented using JDBC and DAO classes.

### 🔒 Secure Configuration

Database credentials are not stored directly in the source code.

The application supports:

- Environment variables for production
- Local properties for development
- Separate production database configuration
- TLS/SSL database connections
- Local configuration files excluded through `.gitignore`

### 🐳 Docker Deployment

QuizCraft includes a production-ready Docker configuration.

The Docker setup:

- Builds the application using Maven
- Uses Java 17
- Packages the application as a WAR
- Runs the application using Apache Tomcat 10.1
- Supports dynamically assigned deployment ports
- Removes unused Tomcat applications from the production image

### ☁️ Cloud Deployment

The application is deployed online using:

- **Render** for application hosting
- **Aiven MySQL** for the production database

The deployed application uses environment-based database configuration so production credentials remain outside the GitHub repository.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Backend development |
| JSP | Dynamic web pages |
| Jakarta Servlets | Request handling and application flow |
| JDBC | Database connectivity |
| MySQL | Production database |
| MariaDB | Local database development |
| Maven | Build and dependency management |
| HTML5 | Page structure |
| CSS3 | Styling and responsive UI |
| JavaScript | Client-side interactions and quiz timer |
| Apache Tomcat 10.1 | Servlet container |
| Docker | Production deployment |
| Render | Cloud application hosting |
| Aiven | Cloud database hosting |
| Git & GitHub | Version control |

---

## 🏗️ Project Architecture

QuizCraft follows a layered architecture to keep different responsibilities separated.

```text
QuizCraft
│
├── Model
│   ├── User
│   ├── Quiz
│   ├── Question
│   ├── Result
│   └── TopicPerformance
│
├── DAO
│   ├── UserDAO
│   ├── QuizDAO
│   ├── QuestionDAO
│   ├── ResultDAO
│   └── TopicPerformanceDAO
│
├── Service
│   ├── QuizService
│   └── FeedbackService
│
├── Servlet
│   ├── Authentication
│   ├── Quiz
│   └── Result handling
│
├── JSP
│   ├── Login
│   ├── Registration
│   ├── Dashboard
│   ├── Quiz
│   └── Result
│
└── Database
    ├── Users
    ├── Quizzes
    ├── Questions
    ├── Results
    └── Topic Performance
