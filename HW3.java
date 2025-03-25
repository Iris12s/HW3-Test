package application;

/*******
 * <p><strong>Title:</strong> HW3.java</p>
 * 
 * <p><strong>Description:</strong> 
 * This Java application is a demonstration of automated testing using 
 * standalone mainline tests. It validates 5 core functionalities of the Q&A system: 
 * posting a question, deleting a question, updating a question, posting an answer, 
 * and retrieving answers to a specific question. Each test corresponds to a student-based 
 * user story described in the project requirements.</p>
 * 
 * <p><strong>Author:</strong> Phuong Nguyen</p>
 * 
 * @version 1.00, 2022-02-23
 */

import databasePart1.DatabaseHelper;
import application.Question;
import application.Answer;

import java.sql.SQLException;
import java.util.List;

/**
 * The HW3 class contains five automated tests simulating core student actions within the application:
 * <ul>
 *   <li>Posting a question</li>
 *   <li>Deleting a question</li>
 *   <li>Updating a question</li>
 *   <li>Posting an answer</li>
 *   <li>Get answers for a question</li>
 * </ul>
 * Each test prints a success or failure result and contributes to a summary report.
 *@author Phuong Nguyen
 *@version 1.0
 */
public class HW3 {

    static int numPassed = 0;
    static int numFailed = 0;

    /**
     * Main entry point for the automated testing.
     * Connects to the database, performs five tests, and prints a test summary.
     * 
     * @param args Not used.
     */
    public static void main(String[] args) {
        System.out.println("===== HW3 Automated Testing Cases =====");

        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            dbHelper.connectToDatabase();

            performTestPostQuestion(dbHelper);
            performTestDeleteQuestion(dbHelper);
            performTestUpdateQuestion(dbHelper);
            performTestPostAnswer(dbHelper);
            performTestGetAnswersForQuestion(dbHelper);

            dbHelper.closeConnection();

        } catch (SQLException e) {
            System.out.println("✘ Database connection failed: " + e.getMessage());
        }

        System.out.println("\n===== SUMMARY =====");
        System.out.println(" Passed: " + numPassed);
        System.out.println(" Failed: " + numFailed);
    }

    /**
     * Tests whether a question can be successfully posted to the system.
     * Simulates a student asking "How can I login?".
     * 
     * @param dbHelper The database helper instance.
     */
    private static void performTestPostQuestion(DatabaseHelper dbHelper) {
        System.out.println("\n🔹 Test: postQuestion()");
        try {
            Question q = new Question("hw3user1", "How can I login?", "student");
            dbHelper.post(q);

            List<Question> all = dbHelper.getAllQuestions();
            boolean found = all.stream().anyMatch(
                x -> x.getUserName().equals("hw3user1") && x.getQuestion().equals("How can I login?"));

            if (found) {
                System.out.println("✔ PASS: Question was posted and found.");
                numPassed++;
            } else {
                System.out.println("✘ FAIL: Question was not found.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("✘ ERROR: " + e.getMessage());
            numFailed++;
        }
    }

    /**
     * Tests whether a question can be deleted successfully.
     * Simulates a student deleting a question they no longer need.
     * 
     * @param dbHelper The database helper instance.
     */
    private static void performTestDeleteQuestion(DatabaseHelper dbHelper) {
        System.out.println("\n🔹 Test: deleteQuestion()");
        try {
            Question q = new Question("hw3user2", "Can I change role?", "student");
            dbHelper.post(q);
            boolean deleted = dbHelper.deleteQuestion("hw3user2", "Can I change role?");

            List<Question> questions = dbHelper.getAllQuestions();
            boolean stillExists = questions.stream().anyMatch(
                x -> x.getUserName().equals("hw3user2") && x.getQuestion().equals("Can I change role?"));

            if (deleted && !stillExists) {
                System.out.println("✔ PASS: Question deleted successfully.");
                numPassed++;
            } else {
                System.out.println("✘ FAIL: Deletion unsuccessful.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("✘ ERROR: " + e.getMessage());
            numFailed++;
        }
    }

    /**
     * Tests updating a question's content.
     * Simulates a student refining their original question.
     * 
     * @param dbHelper The database helper instance.
     */
    private static void performTestUpdateQuestion(DatabaseHelper dbHelper) {
        System.out.println("\n🔹 Test: updateQuestion()");
        try {
            dbHelper.post(new Question("hw3user3", "How can I change role?", "student"));
            boolean updated = dbHelper.updateQuestion("How can I change role?", "Ask Admin");

            List<Question> questions = dbHelper.getAllQuestions();
            boolean found = questions.stream().anyMatch(
                x -> x.getQuestion().equals("Ask Admin"));

            if (updated && found) {
                System.out.println("✔ PASS: Question updated successfully.");
                numPassed++;
            } else {
                System.out.println("✘ FAIL: Updated question not found.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("✘ ERROR: " + e.getMessage());
            numFailed++;
        }
    }

    /**
     * Tests posting an answer to an existing question.
     * Simulates a student suggesting a solution.
     * 
     * @param dbHelper The database helper instance.
     */
    private static void performTestPostAnswer(DatabaseHelper dbHelper) {
        System.out.println("\n🔹 Test: postAnswer()");
        try {
            Answer a = new Answer("hw3user4", "Why should assign role?", "Because easiler to use feature which fit which that role.", "student", "");
            dbHelper.answer(a);

            List<Answer> answers = dbHelper.getAnswers("Why should assign role?");
            boolean found = answers.stream().anyMatch(
                x -> x.getUserName().equals("hw3user4") && x.getAnswer().equals("Because easiler to use feature which fit which that role."));

            if (found) {
                System.out.println("✔ PASS: Answer was posted and found.");
                numPassed++;
            } else {
                System.out.println("✘ FAIL: Answer not found.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("✘ ERROR: " + e.getMessage());
            numFailed++;
        }
    }

    /**
     * Tests retrieving multiple answers to a single question.
     * Simulates a student viewing all current answers to their post.
     * 
     * @param dbHelper The database helper instance.
     */
    private static void performTestGetAnswersForQuestion(DatabaseHelper dbHelper) {
        System.out.println("\n🔹 Test: getAnswersForQuestion()");
        try {
            String question = "How can I post question?";
            dbHelper.answer(new Answer("hw3user5", question, "Question.", "student", ""));
            dbHelper.answer(new Answer("hw3user6", question, "Question, then add question and answer there.", "student", ""));

            List<Answer> answers = dbHelper.getAnswers(question);

            boolean found1 = answers.stream()
                .anyMatch(a -> a.getUserName().equals("hw3user5") && a.getAnswer().equals("Question."));
            boolean found2 = answers.stream()
                .anyMatch(a -> a.getUserName().equals("hw3user6") && a.getAnswer().equals("Question, then add question and answer there."));

            if (found1 && found2) {
                System.out.println("✔ PASS: All expected answers found.");
                numPassed++;
            } else {
                System.out.println("✘ FAIL: Missing expected answers.");
                numFailed++;
            }
        } catch (SQLException e) {
            System.out.println("✘ ERROR: " + e.getMessage());
            numFailed++;
        }
    }
}