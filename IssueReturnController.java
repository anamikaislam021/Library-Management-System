/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package library;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;

public class IssueReturnController {

    @FXML private TextField bookIdField, memberIdField;
    @FXML private TableView<Issue> issueTable;
    @FXML private TableColumn<Issue, Integer> idCol, bookIdCol, memberIdCol, fineCol;
    @FXML private TableColumn<Issue, String> issueDateCol, returnDateCol;

    private ObservableList<Issue> issueList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        memberIdCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        issueDateCol.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));

        loadIssues();
    }

    public void loadIssues() {
        issueList.clear();
        try (Connection conn = DBUtil.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM issue_log")) {

            while (rs.next()) {
                issueList.add(new Issue(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("member_id"),
                    rs.getString("issue_date"),
                    rs.getString("return_date"),
                    rs.getInt("fine")
                ));
            }
            issueTable.setItems(issueList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void issueBook() {
        try (Connection conn = DBUtil.connect()) {
            int bookId = Integer.parseInt(bookIdField.getText());
            int memberId = Integer.parseInt(memberIdField.getText());

            // Check if the book is available
            PreparedStatement checkStmt = conn.prepareStatement("SELECT available FROM books WHERE id=?");
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && !rs.getBoolean("available")) {
                showAlert("This book is already issued!");
                return;
            }

            // Insert issue record
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO issue_log (book_id, member_id, issue_date) VALUES (?, ?, ?)");
            stmt.setInt(1, bookId);
            stmt.setInt(2, memberId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();

            // Update book availability
            PreparedStatement updateStmt = conn.prepareStatement("UPDATE books SET available=FALSE WHERE id=?");
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();

            loadIssues();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnBook() {
        try (Connection conn = DBUtil.connect()) {
            int bookId = Integer.parseInt(bookIdField.getText());
            int memberId = Integer.parseInt(memberIdField.getText());

            // Find the unreturned record
            PreparedStatement findStmt = conn.prepareStatement(
                "SELECT * FROM issue_log WHERE book_id=? AND member_id=? AND return_date IS NULL");
            findStmt.setInt(1, bookId);
            findStmt.setInt(2, memberId);
            ResultSet rs = findStmt.executeQuery();

            if (rs.next()) {
                int issueId = rs.getInt("id");
                Date issueDate = rs.getDate("issue_date");
                LocalDate returnDate = LocalDate.now();

                // Calculate fine (e.g., 5 per day after 7 days)
                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(issueDate.toLocalDate(), returnDate);
                int fine = daysBetween > 7 ? (int)(daysBetween - 7) * 5 : 0;

                // Update return date and fine
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE issue_log SET return_date=?, fine=? WHERE id=?");
                updateStmt.setDate(1, Date.valueOf(returnDate));
                updateStmt.setInt(2, fine);
                updateStmt.setInt(3, issueId);
                updateStmt.executeUpdate();

                // Mark book available again
                PreparedStatement bookUpdate = conn.prepareStatement("UPDATE books SET available=TRUE WHERE id=?");
                bookUpdate.setInt(1, bookId);
                bookUpdate.executeUpdate();

                loadIssues();
                clearFields();
            } else {
                showAlert("No active issue found for this Book and Member.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        bookIdField.clear();
        memberIdField.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

