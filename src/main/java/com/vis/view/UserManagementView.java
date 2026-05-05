package com.vis.view;

import com.vis.model.AppUser;
import com.vis.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserManagementView {

    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String ROSE_TINT = "#FFF0F3";
    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String SOFT_PINK = "#FF9EB5";

    private final ObservableList<UserRecord> userData = FXCollections.observableArrayList();
    private TableView<UserRecord> userTable;

    public Node build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        // Header
        Label title = new Label("👑 User Management");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(80);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        Label subtitle = new Label("Create, modify, or disable system user accounts");
        subtitle.setFont(Font.font("Helvetica", 12));
        subtitle.setTextFill(Color.web(ROSE_GOLD));

        // User Table
        userTable = new TableView<>();
        userTable.setItems(userData);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        userTable.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        // Define columns
        TableColumn<UserRecord, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(120);

        TableColumn<UserRecord, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fullNameCol.setPrefWidth(180);

        TableColumn<UserRecord, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(120);

        TableColumn<UserRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<UserRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Active".equals(item)) {
                        setTextFill(Color.web("#10B981"));
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.web("#EF4444"));
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<UserRecord, String> createdCol = new TableColumn<>("Created At");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdCol.setPrefWidth(150);

        userTable.getColumns().addAll(userIdCol, fullNameCol, roleCol, statusCol, createdCol);

        // Form to add new user
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        // Form fields
        TextField userIdField = createStyledField("User ID (e.g., admin2)");
        TextField fullNameField = createStyledField("Full Name");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8; -fx-padding: 8;");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("ADMIN", "WORKSHOP", "CUSTOMER", "POLICE", "INSURANCE");
        roleCombo.setPromptText("Select Role");
        roleCombo.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8; -fx-padding: 8;");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive");
        statusCombo.setValue("Active");
        statusCombo.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8; -fx-padding: 8;");

        // Buttons
        Button addBtn = createButton("➕ Add User", ROSE_GOLD);
        Button disableBtn = createButton("🔒 Disable User", DARK_SLATE);
        Button enableBtn = createButton("🔓 Enable User", "#10B981");
        Button deleteBtn = createButton("🗑 Delete User", "#EF4444");
        Button refreshBtn = createButton("⟳ Refresh", SOFT_PINK);

        // Form layout
        form.add(new Label("User ID:"), 0, 0);
        form.add(userIdField, 1, 0);
        form.add(new Label("Full Name:"), 2, 0);
        form.add(fullNameField, 3, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);
        form.add(new Label("Role:"), 2, 1);
        form.add(roleCombo, 3, 1);
        form.add(new Label("Status:"), 0, 2);
        form.add(statusCombo, 1, 2);

        HBox buttonBar = new HBox(10, addBtn, disableBtn, enableBtn, deleteBtn, refreshBtn);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        VBox formBox = new VBox(form, buttonBar);

        // Load users
        loadUsers();

        // Event handlers
        addBtn.setOnAction(e -> addUser(userIdField, fullNameField, passwordField, roleCombo, statusCombo));
        disableBtn.setOnAction(e -> updateUserStatus("Inactive"));
        enableBtn.setOnAction(e -> updateUserStatus("Active"));
        deleteBtn.setOnAction(e -> deleteUser());
        refreshBtn.setOnAction(e -> loadUsers());

        root.getChildren().addAll(title, accent, subtitle, userTable, formBox);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        return root;
    }

    private TextField createStyledField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;"
        );
        return field;
    }

    private Button createButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setPrefHeight(38);
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: " + (bgColor.equals(ROSE_GOLD) ? BLACK_PEARL : "white") + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }

    private void loadUsers() {
        userData.clear();
        String sql = "SELECT user_id, full_name, role, active, created_at FROM AppUser ORDER BY created_at DESC";

        try (Connection conn = com.vis.dao.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UserRecord user = new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getBoolean("active") ? "Active" : "Inactive",
                        rs.getTimestamp("created_at") != null ?
                                rs.getTimestamp("created_at").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A"
                );
                userData.add(user);
            }

        } catch (SQLException e) {
            AlertUtil.error("Database Error", "Could not load users: " + e.getMessage());
        }
    }

    private void addUser(TextField userIdField, TextField fullNameField, PasswordField passwordField,
                         ComboBox<String> roleCombo, ComboBox<String> statusCombo) {

        String userId = userIdField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleCombo.getValue();
        boolean active = "Active".equals(statusCombo.getValue());

        if (userId.isEmpty() || fullName.isEmpty() || password.isEmpty() || role == null) {
            AlertUtil.warn("Validation Error", "Please fill in all fields.");
            return;
        }

        String sql = "INSERT INTO AppUser(user_id, full_name, password, role, active, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = com.vis.dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            pstmt.setBoolean(5, active);
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            pstmt.executeUpdate();

            // Clear form
            userIdField.clear();
            fullNameField.clear();
            passwordField.clear();
            roleCombo.setValue(null);
            statusCombo.setValue("Active");

            loadUsers();
            AlertUtil.info("Success", "User '" + userId + "' has been created successfully!");

        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key")) {
                AlertUtil.warn("Duplicate User", "User ID '" + userId + "' already exists. Please choose a different ID.");
            } else {
                AlertUtil.error("Database Error", "Could not create user: " + e.getMessage());
            }
        }
    }

    private void updateUserStatus(String newStatus) {
        UserRecord selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warn("No Selection", "Please select a user to " + (newStatus.equals("Active") ? "enable" : "disable") + ".");
            return;
        }

        if (selected.getUserId().equals("admin")) {
            AlertUtil.warn("Cannot Modify", "The default admin user cannot be disabled.");
            return;
        }

        boolean active = newStatus.equals("Active");
        String sql = "UPDATE AppUser SET active = ? WHERE user_id = ?";

        try (Connection conn = com.vis.dao.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, active);
            pstmt.setString(2, selected.getUserId());
            pstmt.executeUpdate();

            loadUsers();
            AlertUtil.info("Success", "User '" + selected.getUserId() + "' has been " + (active ? "enabled" : "disabled") + ".");

        } catch (SQLException e) {
            AlertUtil.error("Database Error", "Could not update user status: " + e.getMessage());
        }
    }

    private void deleteUser() {
        UserRecord selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warn("No Selection", "Please select a user to delete.");
            return;
        }

        if (selected.getUserId().equals("admin")) {
            AlertUtil.warn("Cannot Delete", "The default admin user cannot be deleted.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User: " + selected.getUserId());
        confirm.setContentText("Are you sure you want to delete this user? This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String sql = "DELETE FROM AppUser WHERE user_id = ?";

                try (Connection conn = com.vis.dao.DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, selected.getUserId());
                    pstmt.executeUpdate();

                    loadUsers();
                    AlertUtil.info("Success", "User '" + selected.getUserId() + "' has been deleted.");

                } catch (SQLException e) {
                    AlertUtil.error("Database Error", "Could not delete user: " + e.getMessage());
                }
            }
        });
    }

    // Inner class for table data
    public static class UserRecord {
        private final String userId;
        private final String fullName;
        private final String role;
        private final String status;
        private final String createdAt;

        public UserRecord(String userId, String fullName, String role, String status, String createdAt) {
            this.userId = userId;
            this.fullName = fullName;
            this.role = role;
            this.status = status;
            this.createdAt = createdAt;
        }

        public String getUserId() { return userId; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
    }
}