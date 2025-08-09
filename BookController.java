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

public class BookController {

    @FXML private TextField titleField, authorField, isbnField, categoryField;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Integer> idCol;
    @FXML private TableColumn<Book, String> titleCol, authorCol, isbnCol, categoryCol;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        loadBooks();

        bookTable.setOnMouseClicked(e -> {
            Book b = bookTable.getSelectionModel().getSelectedItem();
            if (b != null) {
                titleField.setText(b.getTitle());
                authorField.setText(b.getAuthor());
                isbnField.setText(b.getIsbn());
                categoryField.setText(b.getCategory());
            }
        });
    }

    public void loadBooks() {
        bookList.clear();
        try (Connection conn = DBUtil.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                bookList.add(new Book(rs.getInt("id"),
                                      rs.getString("title"),
                                      rs.getString("author"),
                                      rs.getString("isbn"),
                                      rs.getString("category")));
            }
            bookTable.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook() {
        try (Connection conn = DBUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO books (title, author, isbn, category) VALUES (?, ?, ?, ?)");
            stmt.setString(1, titleField.getText());
            stmt.setString(2, authorField.getText());
            stmt.setString(3, isbnField.getText());
            stmt.setString(4, categoryField.getText());
            stmt.executeUpdate();
            loadBooks();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = DBUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE books SET title=?, author=?, isbn=?, category=? WHERE id=?");
            stmt.setString(1, titleField.getText());
            stmt.setString(2, authorField.getText());
            stmt.setString(3, isbnField.getText());
            stmt.setString(4, categoryField.getText());
            stmt.setInt(5, selected.getId());
            stmt.executeUpdate();
            loadBooks();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = DBUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id=?");
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadBooks();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        categoryField.clear();
    }
}

