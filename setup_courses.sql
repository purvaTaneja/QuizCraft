-- QuizCraft seed data: three Java courses with 15 questions each.
--
-- This script contains NO credentials and is safe to re-run (it is idempotent).
-- It expects the quizcraft database to already exist (see schema.sql) and at
-- least one user row, because quizzes.created_by references users(id).
--
-- Run with:
--   mysql -u <user> -p quizcraft < setup_courses.sql

USE quizcraft;

-- Clear existing quiz data. Order respects foreign keys:
-- topic performance -> results -> questions -> quizzes.
-- Deleting results also cascades to result_topic_performance.
DELETE FROM result_topic_performance;
DELETE FROM results;
DELETE FROM questions;
DELETE FROM quizzes;

-- Reset auto-increment counters so the fixed IDs below apply cleanly.
ALTER TABLE result_topic_performance AUTO_INCREMENT = 1;
ALTER TABLE results AUTO_INCREMENT = 1;
ALTER TABLE questions AUTO_INCREMENT = 1;
ALTER TABLE quizzes AUTO_INCREMENT = 1;

-- Insert three courses
INSERT INTO quizzes (id, title, description, created_by) VALUES
(1, 'Java Basics', 'Learn core Java concepts including syntax, variables, data types, operators, conditions, loops, arrays and basic programming concepts.', 1),
(2, 'Java Advanced', 'Practice advanced Java concepts including collections, exceptions, generics, multithreading, interfaces and advanced language features.', 1),
(3, 'OOP Fundamentals in Java', 'Master object-oriented programming concepts including classes, objects, constructors, inheritance, polymorphism, abstraction and encapsulation.', 1);

-- Java Basics Questions (15 questions)
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer, topic) VALUES
(1, 'Which of the following is the correct way to declare a variable in Java?', 'int x = 10;', 'x int = 10;', 'int 10 = x;', 'x = 10 int;', 'A', 'Variables & Data Types'),
(1, 'What is the default value of a boolean variable in Java?', 'true', 'false', '0', 'null', 'B', 'Variables & Data Types'),
(1, 'What is the size of an int in Java?', '16 bits', '32 bits', '64 bits', '8 bits', 'B', 'Variables & Data Types'),
(1, 'Which of the following is NOT a primitive data type in Java?', 'int', 'float', 'String', 'boolean', 'C', 'Variables & Data Types'),
(1, 'What is the result of 10 / 3 in Java?', '3.33', '3', '3.0', '4', 'B', 'Operators'),
(1, 'What is the output of System.out.println(5 + 3 + "8")?', '538', '88', '16', 'Compilation error', 'A', 'Operators'),
(1, 'Which operator is used for logical AND in Java?', '&', '&&', 'AND', 'and', 'B', 'Operators'),
(1, 'What is the output of: int x = 5; System.out.println(x++);', '5', '6', '4', 'Compilation error', 'A', 'Operators'),
(1, 'Which loop executes at least once even if the condition is false?', 'for loop', 'while loop', 'do-while loop', 'enhanced for loop', 'C', 'Conditions & Loops'),
(1, 'What is the output of: for(int i=0; i<3; i++) System.out.print(i);', '012', '123', '0123', 'Compilation error', 'A', 'Conditions & Loops'),
(1, 'Which keyword is used to exit a loop in Java?', 'exit', 'break', 'stop', 'return', 'B', 'Conditions & Loops'),
(1, 'What is the output of: int[] arr = {1,2,3}; System.out.println(arr.length);', '2', '3', '4', 'Compilation error', 'B', 'Arrays'),
(1, 'How do you access the first element of an array in Java?', 'arr[1]', 'arr[0]', 'arr.first()', 'arr(0)', 'B', 'Arrays'),
(1, 'What is the output of: String s = "Hello"; System.out.println(s.length());', '4', '5', '6', 'Compilation error', 'B', 'Strings'),
(1, 'Which method converts a String to uppercase in Java?', 'toUpper()', 'toUpperCase()', 'upper()', 'convertUpper()', 'B', 'Strings');

-- Java Advanced Questions (15 questions)
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer, topic) VALUES
(2, 'Which collection allows duplicate elements?', 'HashSet', 'TreeSet', 'ArrayList', 'HashMap', 'C', 'Collections'),
(2, 'What is the difference between ArrayList and LinkedList?', 'No difference', 'ArrayList uses array, LinkedList uses linked nodes', 'LinkedList is faster for random access', 'ArrayList uses linked nodes', 'B', 'Collections'),
(2, 'Which interface does HashMap implement?', 'List', 'Set', 'Map', 'Collection', 'C', 'Collections'),
(2, 'What is the correct way to handle exceptions in Java?', 'if/else', 'try-catch', 'for-loop', 'switch-case', 'B', 'Exception Handling'),
(2, 'Which block is always executed in exception handling?', 'try', 'catch', 'finally', 'throw', 'C', 'Exception Handling'),
(2, 'What is the difference between throw and throws?', 'No difference', 'throw is used to throw an exception, throws is used to declare an exception', 'throws is used to throw an exception, throw is used to declare', 'Both are used to catch exceptions', 'B', 'Exception Handling'),
(2, 'Which of the following is a wrapper class for int?', 'Int', 'Integer', 'intWrapper', 'NumberInt', 'B', 'Generics'),
(2, 'What is autoboxing in Java?', 'Automatic conversion of primitive to wrapper class', 'Automatic conversion of wrapper to primitive', 'Automatic memory allocation', 'Automatic garbage collection', 'A', 'Generics'),
(2, 'What is the correct syntax for a generic class?', 'class Box<T> { }', 'class Box<T extends Object> { }', 'class Box<generic T> { }', 'class Box { <T> }', 'A', 'Generics'),
(2, 'Which keyword is used to create a thread in Java?', 'start', 'run', 'thread', 'execute', 'A', 'Multithreading'),
(2, 'What is the output of: Thread t = new Thread(); t.start();', 'Compilation error', 'Runtime error', 'Thread starts running', 'Nothing happens', 'C', 'Multithreading'),
(2, 'Which method is used to pause a thread for a specified time?', 'pause()', 'sleep()', 'wait()', 'stop()', 'B', 'Multithreading'),
(2, 'What is the purpose of the synchronized keyword in Java?', 'To make a method faster', 'To prevent multiple threads from accessing a method simultaneously', 'To create a new thread', 'To stop a thread', 'B', 'Multithreading'),
(2, 'Which class is used to read from a file in Java?', 'FileReader', 'FileWriter', 'FileInputStream', 'BufferedReader', 'A', 'File Handling'),
(2, 'What is the purpose of the finally block in file handling?', 'To open the file', 'To close the file regardless of exceptions', 'To read the file', 'To write to the file', 'B', 'File Handling');

-- OOP Fundamentals Questions (15 questions)
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer, topic) VALUES
(3, 'Which keyword is used to create an object in Java?', 'create', 'new', 'object', 'make', 'B', 'Classes & Objects'),
(3, 'What is a constructor in Java?', 'A method that destroys objects', 'A special method used to initialize objects', 'A method that returns a value', 'A method that creates classes', 'B', 'Constructors'),
(3, 'What is the default constructor?', 'A constructor with parameters', 'A constructor provided by Java if no constructor is defined', 'A constructor that returns a value', 'A constructor that takes no arguments but is user-defined', 'B', 'Constructors'),
(3, 'What is encapsulation in Java?', 'Hiding internal implementation details', 'Creating multiple classes', 'Inheriting properties', 'Overloading methods', 'A', 'Encapsulation'),
(3, 'Which access modifier provides the most visibility?', 'private', 'protected', 'public', 'default', 'C', 'Encapsulation'),
(3, 'What is inheritance in Java?', 'Creating a new class from an existing class', 'Creating a new object', 'Creating a new method', 'Creating a new variable', 'A', 'Inheritance'),
(3, 'Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'super', 'B', 'Inheritance'),
(3, 'What is method overriding?', 'Same method name with different parameters', 'Subclass provides specific implementation of a parent method', 'Multiple methods with same name', 'A method that overrides variables', 'B', 'Polymorphism'),
(3, 'What is method overloading?', 'Same method name with different parameters in the same class', 'Subclass provides specific implementation', 'Multiple methods with same name in different classes', 'A method that overrides variables', 'A', 'Polymorphism'),
(3, 'What is runtime polymorphism also known as?', 'Compile-time polymorphism', 'Method overloading', 'Method overriding', 'Static binding', 'C', 'Polymorphism'),
(3, 'What is an abstract class in Java?', 'A class that cannot be instantiated', 'A class that cannot be inherited', 'A class with only static methods', 'A class with no methods', 'A', 'Abstraction'),
(3, 'What is an interface in Java?', 'A class with only abstract methods', 'A blueprint of a class that defines a contract', 'A class with only private methods', 'A class with only static methods', 'B', 'Abstraction'),
(3, 'Which keyword is used to implement an interface in Java?', 'extends', 'implements', 'interface', 'abstract', 'B', 'Abstraction'),
(3, 'What is the output of: System.out.println(new Object().toString());', 'Object@hashcode', 'null', 'Compilation error', 'Runtime error', 'A', 'Classes & Objects'),
(3, 'Which keyword is used to refer to the current object in Java?', 'this', 'super', 'current', 'self', 'A', 'Classes & Objects');
