package service;

import model.TopicPerformance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds topic-level performance analysis and personalized practice feedback.
 */
public class FeedbackService {

    /**
     * Groups an attempt's questions by topic and computes correct/total/percentage
     * for every topic that was actually represented in the attempt.
     *
     * @param questions    the questions that were served for this attempt
     * @param userAnswers  map of questionId -> selected answer
     */
    public Map<String, TopicPerformance> analyzeTopics(List<model.Question> questions,
                                                       Map<Integer, String> userAnswers) {

        Map<String, int[]> tally = new LinkedHashMap<>();
        Map<String, TopicPerformance> analysis = new LinkedHashMap<>();

        for (model.Question question : questions) {
            String topic = question.getTopic();
            if (topic == null || topic.trim().isEmpty()) {
                topic = "General";
            }

            int[] counts = tally.get(topic);
            if (counts == null) {
                counts = new int[2]; // [0] = correct, [1] = total
                tally.put(topic, counts);
            }

            counts[1]++;

            String selected = userAnswers.get(question.getId());
            if (selected != null && selected.equals(question.getCorrectAnswer())) {
                counts[0]++;
            }
        }

        for (Map.Entry<String, int[]> entry : tally.entrySet()) {
            int correct = entry.getValue()[0];
            int total = entry.getValue()[1];
            analysis.put(entry.getKey(), new TopicPerformance(entry.getKey(), correct, total));
        }

        return analysis;
    }

    /**
     * Strongest topic = highest percentage. Ties are resolved by preferring the
     * topic with more questions answered correctly, then alphabetically for
     * deterministic output.
     */
    public TopicPerformance getStrongestTopic(Map<String, TopicPerformance> analysis) {
        TopicPerformance best = null;
        for (TopicPerformance current : analysis.values()) {
            if (best == null || isStronger(current, best)) {
                best = current;
            }
        }
        return best;
    }

    /**
     * Weakest topic = lowest percentage. Ties are resolved by preferring the
     * topic with more questions attempted (a weaker result across more
     * questions is more meaningful), then alphabetically.
     */
    public TopicPerformance getWeakestTopic(Map<String, TopicPerformance> analysis) {
        TopicPerformance worst = null;
        for (TopicPerformance current : analysis.values()) {
            if (worst == null || isWeaker(current, worst)) {
                worst = current;
            }
        }
        return worst;
    }

    private boolean isStronger(TopicPerformance candidate, TopicPerformance currentBest) {
        if (candidate.getPercentage() != currentBest.getPercentage()) {
            return candidate.getPercentage() > currentBest.getPercentage();
        }
        if (candidate.getCorrectAnswers() != currentBest.getCorrectAnswers()) {
            return candidate.getCorrectAnswers() > currentBest.getCorrectAnswers();
        }
        return candidate.getTopic().compareToIgnoreCase(currentBest.getTopic()) < 0;
    }

    private boolean isWeaker(TopicPerformance candidate, TopicPerformance currentWorst) {
        if (candidate.getPercentage() != currentWorst.getPercentage()) {
            return candidate.getPercentage() < currentWorst.getPercentage();
        }
        if (candidate.getTotalQuestions() != currentWorst.getTotalQuestions()) {
            return candidate.getTotalQuestions() > currentWorst.getTotalQuestions();
        }
        return candidate.getTopic().compareToIgnoreCase(currentWorst.getTopic()) < 0;
    }

    /**
     * Returns true when every represented topic scored the same percentage,
     * meaning there is no meaningful strongest/weakest distinction.
     */
    public boolean isTie(Map<String, TopicPerformance> analysis) {
        if (analysis.size() < 2) {
            return false;
        }
        Double reference = null;
        for (TopicPerformance performance : analysis.values()) {
            if (reference == null) {
                reference = performance.getPercentage();
            } else if (performance.getPercentage() != reference) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a topic-specific practice suggestion for the weakest topic.
     */
    public String getPracticeSuggestion(TopicPerformance weakestTopic) {
        if (weakestTopic == null) {
            return "Keep practising regularly to build a strong foundation.";
        }

        String topic = weakestTopic.getTopic();
        if (topic == null) {
            topic = "";
        }

        switch (topic.trim()) {
            case "Variables & Data Types":
                return "Practice primitive data types, type casting, variable declaration, "
                        + "scope rules, and the difference between int, long, float and double.";

            case "Operators":
                return "Practice arithmetic, relational and logical operators, "
                        + "operator precedence, and the difference between ++ and -- "
                        + "in pre and post fix forms.";

            case "Conditions & Loops":
                return "Practice if/else and switch statements, for, while and do-while loops, "
                        + "loop control with break and continue, and nested loop iteration.";

            case "Arrays":
                return "Practice array declaration, initialisation, traversal, indexing, "
                        + "searching, sorting, and common array operations using System.arraycopy.";

            case "Strings":
                return "Practice String methods, String comparison using equals() versus ==, "
                        + "String immutability, and when to use StringBuilder over String.";

            case "Collections":
                return "Practice List, Set and Map operations, the differences between "
                        + "ArrayList and LinkedList, HashMap versus TreeMap, and iteration order.";

            case "Exception Handling":
                return "Practice try-catch-finally, throw versus throws, checked versus "
                        + "unchecked exceptions, multi-catch blocks, and creating custom exceptions.";

            case "Generics":
                return "Practice generic classes, generic methods, bounded type parameters, "
                        + "wildcards, and how generics provide compile-time type safety.";

            case "Multithreading":
                return "Practice creating and starting threads, the Runnable versus Thread choice, "
                        + "synchronised blocks, thread lifecycle, and common concurrency pitfalls.";

            case "File Handling":
                return "Practice FileReader and FileWriter, try-with-resources, buffered I/O, "
                        + "and correctly closing file streams to avoid resource leaks.";

            case "Classes & Objects":
                return "Practice class structure, object creation, instance versus static members, "
                        + "object references, and how objects are stored in memory.";

            case "Constructors":
                return "Practice default, parameterised and overloaded constructors, "
                        + "constructor chaining with this(), and the fact constructors "
                        + "do not have a return type.";

            case "Encapsulation":
                return "Practice making fields private, exposing behaviour through getters and setters, "
                        + "and validating input inside methods rather than allowing direct field access.";

            case "Inheritance":
                return "Practice single, multilevel and hierarchical inheritance, using super, "
                        + "method overriding, and the limitations constructors have with inheritance.";

            case "Polymorphism":
                return "Practice method overloading versus method overriding, runtime polymorphism "
                        + "through dynamic dispatch, and using a parent reference to point to a child object.";

            case "Abstraction":
                return "Practice abstract classes, interfaces, default and static interface methods, "
                        + "and choosing between an abstract class and an interface.";

            case "General":
                return "Revise the core concepts for this course and attempt the quiz again.";

            default:
                return "Revise " + topic + " and attempt the quiz again to improve your score.";
        }
    }

    /**
     * Builds the full personalised message shown on the result page.
     */
    public String buildFeedbackMessage(TopicPerformance strongest, TopicPerformance weakest,
                                       Map<String, TopicPerformance> analysis) {

        if (analysis == null || analysis.isEmpty()) {
            return "No topic information was available for this attempt.";
        }

        if (analysis.size() == 1) {
            TopicPerformance only = analysis.values().iterator().next();
            return "This attempt covered a single topic, " + only.getTopic() + ", so the performance "
                    + "shown is based on the " + only.getTotalQuestions() + " question(s) attempted. "
                    + getPracticeSuggestion(only);
        }

        if (isTie(analysis)) {
            return "You scored an equal percentage across all " + analysis.size()
                    + " topics in this attempt, so there is no clear strongest or weakest area yet. "
                    + "Focus on broad revision and retake the quiz to create a clearer picture.";
        }

        StringBuilder message = new StringBuilder();

        message.append("Great work! You performed strongly in ")
                .append(strongest.getTopic())
                .append(" (").append(formatPercentage(strongest.getPercentage())).append("). ");

        message.append("Your weakest area is ")
                .append(weakest.getTopic())
                .append(" (").append(formatPercentage(weakest.getPercentage())).append("). ")
                .append(getPracticeSuggestion(weakest));

        return message.toString();
    }

    /**
     * Formats a percentage without trailing ".0" noise, e.g. 80.0 -> "80%", 66.67 -> "66.7%".
     */
    public String formatPercentage(double percentage) {
        if (percentage == Math.floor(percentage)) {
            return String.valueOf((int) percentage) + "%";
        }
        return String.format(java.util.Locale.ROOT, "%.1f%%", percentage);
    }
}
