package library;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class MemberController {

    @FXML private TextField nameField, emailField, phoneField;
    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, Integer> idCol;
    @FXML private TableColumn<Member, String> nameCol, emailCol, phoneCol;

    private ObservableList<Member> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        loadMembers();

        memberTable.setOnMouseClicked(e -> {
            Member m = memberTable.getSelectionModel().getSelectedItem();
            if (m != null) {
                nameField.setText(m.getName());
                emailField.setText(m.getEmail());
                phoneField.setText(m.getPhone());
            }
        });
    }

    public void loadMembers() {
        memberList.clear();
        try (Connection conn = DBUtil.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM members")) {

            while (rs.next()) {
                memberList.add(new Member(rs.getInt("id"), rs.getString("name"),
                                          rs.getString("email"), rs.getString("phone")));
            }
            memberTable.setItems(memberList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMember() {
        try (Connection conn = DBUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)");
            stmt.setString(1, nameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setString(3, phoneField.getText());
            stmt.executeUpdate();
            loadMembers();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMember() {
        Member selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = DBUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE members SET name=?, email=?, phone=? WHERE id=?");
            stmt.setString(1, nameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setString(3, phoneField.getText());
            stmt.setInt(4, selected.getId());
            stmt.executeUpdate();
            loadMembers();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMember() {
        Member selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = DBUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM members WHERE id=?");
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadMembers();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
    }
}
