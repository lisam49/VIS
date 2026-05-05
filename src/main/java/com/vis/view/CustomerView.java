package com.vis.view;

import com.vis.controller.CustomerController;
import com.vis.model.Customer;
import com.vis.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;

public class CustomerView {

    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private final CustomerController controller = new CustomerController();
    private final ObservableList<Customer> data = FXCollections.observableArrayList();
    private TableView<Customer> table;

    // Form fields
    private TextField idField;
    private TextField nameField;
    private TextField addrField;
    private TextField phoneField;
    private TextField emailField;

    // Buttons
    private Button addBtn;
    private Button updateBtn;
    private Button deleteBtn;
    private Button clearBtn;
    private Button reloadBtn;

    private boolean isEditMode = false;
    private int editingCustomerId = -1;

    public Node build() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        // Header
        Label title = new Label("👥 Customer Management");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(80);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        Label subtitle = new Label("Manage customer records - Add, Edit, Update, or Delete customers");
        subtitle.setFont(Font.font("Helvetica", 12));
        subtitle.setTextFill(Color.web(ROSE_GOLD));

        // TableView
        table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;"
        );
        table.setPrefHeight(350);

        TableColumn<Customer, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().idProperty());
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Customer, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());
        nameCol.setPrefWidth(180);

        TableColumn<Customer, String> addrCol = new TableColumn<>("Address");
        addrCol.setCellValueFactory(c -> c.getValue().addressProperty());
        addrCol.setPrefWidth(200);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> c.getValue().phoneProperty());
        phoneCol.setPrefWidth(120);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> c.getValue().emailProperty());
        emailCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, nameCol, addrCol, phoneCol, emailCol);

        // Style table rows
        table.setRowFactory(tv -> new TableRow<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: white; -fx-border-color: " + ROSE_TINT + ";");
                }
            }
        });

        // Selection listener - populate form when row is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
                isEditMode = true;
                editingCustomerId = newSelection.getId();
                addBtn.setDisable(true);
                updateBtn.setDisable(false);
                deleteBtn.setDisable(false);
            }
        });

        // Form Card
        VBox formCard = new VBox(12);
        formCard.setPadding(new Insets(20));
        formCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        Label formTitle = new Label("Customer Information");
        formTitle.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
        formTitle.setTextFill(Color.web(DARK_SLATE));

        // Form fields
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(10, 0, 15, 0));

        // ID Field (read-only)
        Label idLabel = new Label("Customer ID:");
        idLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
        idLabel.setTextFill(Color.web(DARK_SLATE));

        idField = new TextField();
        idField.setPromptText("Auto-generated");
        idField.setEditable(false);
        idField.setPrefHeight(38);
        idField.setStyle(
                "-fx-background-color: #F5F5F5;" +
                        "-fx-border-color: #DDDDDD;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;"
        );

        // Name Field
        Label nameLabel = new Label("Full Name:*");
        nameLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
        nameLabel.setTextFill(Color.web(DARK_SLATE));

        nameField = createStyledTextField("Enter customer full name");

        // Address Field
        Label addrLabel = new Label("Address:");
        addrLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
        addrLabel.setTextFill(Color.web(DARK_SLATE));

        addrField = createStyledTextField("Enter street address");

        // Phone Field
        Label phoneLabel = new Label("Phone:");
        phoneLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
        phoneLabel.setTextFill(Color.web(DARK_SLATE));

        phoneField = createStyledTextField("Enter phone number");

        // Email Field
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
        emailLabel.setTextFill(Color.web(DARK_SLATE));

        emailField = createStyledTextField("Enter email address");

        // Layout
        form.add(idLabel, 0, 0);
        form.add(idField, 1, 0);
        form.add(nameLabel, 2, 0);
        form.add(nameField, 3, 0);
        form.add(addrLabel, 0, 1);
        form.add(addrField, 1, 1);
        form.add(phoneLabel, 2, 1);
        form.add(phoneField, 3, 1);
        form.add(emailLabel, 0, 2);
        form.add(emailField, 1, 2, 3, 1);

        // Buttons
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(5, 0, 0, 0));

        addBtn = createStyledButton("➕ Add Customer", ROSE_GOLD);
        updateBtn = createStyledButton("✏️ Update Customer", "#3B82F6");
        deleteBtn = createStyledButton("🗑 Delete Customer", "#EF4444");
        clearBtn = createStyledButton("🔄 Clear Form", DARK_SLATE);
        reloadBtn = createStyledButton("⟳ Reload", SOFT_PINK);

        // Disable update/delete initially
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);

        actions.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn, reloadBtn);

        formCard.getChildren().addAll(formTitle, form, actions);

        // Info badge
        HBox infoBadge = new HBox(8);
        infoBadge.setAlignment(Pos.CENTER_LEFT);
        infoBadge.setPadding(new Insets(10, 0, 0, 0));

        Label infoLabel = new Label("ℹ️ Select a customer from the table to edit or delete. Name is required.");
        infoLabel.setFont(Font.font("Helvetica", 11));
        infoLabel.setTextFill(Color.web("#888888"));
        infoBadge.getChildren().add(infoLabel);

        root.getChildren().addAll(title, accent, subtitle, table, formCard, infoBadge);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Button Actions
        addBtn.setOnAction(e -> addCustomer());
        updateBtn.setOnAction(e -> updateCustomer());
        deleteBtn.setOnAction(e -> deleteCustomer());
        clearBtn.setOnAction(e -> clearForm());
        reloadBtn.setOnAction(e -> refresh());

        refresh();
        return root;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        field.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;" +
                        "-fx-font-size: 13px;"
        );
        return field;
    }

    private Button createStyledButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setPrefHeight(38);
        String textColor = (bgColor.equals(ROSE_GOLD) || bgColor.equals(SOFT_PINK)) ? BLACK_PEARL : "white";
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + SOFT_PINK + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;" +
                        "-fx-cursor: hand;"
        ));
        return btn;
    }

    private void populateForm(Customer customer) {
        idField.setText(String.valueOf(customer.getId()));
        nameField.setText(customer.getName());
        addrField.setText(customer.getAddress());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
    }

    private void clearForm() {
        idField.clear();
        nameField.clear();
        addrField.clear();
        phoneField.clear();
        emailField.clear();
        isEditMode = false;
        editingCustomerId = -1;
        addBtn.setDisable(false);
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
        table.getSelectionModel().clearSelection();
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        String address = addrField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty()) {
            AlertUtil.warn("Validation Error", "Customer name is required.");
            return;
        }

        try {
            Customer newCustomer = new Customer(0, name, address, phone, email);
            controller.create(newCustomer);
            clearForm();
            refresh();
            AlertUtil.info("Success", "Customer '" + name + "' has been added successfully!");
        } catch (IllegalArgumentException e) {
            AlertUtil.warn("Validation Error", e.getMessage());
        } catch (SQLException e) {
            AlertUtil.error("Database Error", "Could not add customer: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        if (editingCustomerId <= 0) {
            AlertUtil.warn("No Selection", "Please select a customer to update.");
            return;
        }

        String name = nameField.getText().trim();
        String address = addrField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty()) {
            AlertUtil.warn("Validation Error", "Customer name is required.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Update");
        confirm.setHeaderText("Update Customer");
        confirm.setContentText("Are you sure you want to update customer '" + name + "'?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Customer updatedCustomer = new Customer(editingCustomerId, name, address, phone, email);
                    controller.update(updatedCustomer);
                    clearForm();
                    refresh();
                    AlertUtil.info("Success", "Customer '" + name + "' has been updated successfully!");
                } catch (SQLException e) {
                    AlertUtil.error("Database Error", "Could not update customer: " + e.getMessage());
                }
            }
        });
    }

    private void deleteCustomer() {
        if (editingCustomerId <= 0) {
            AlertUtil.warn("No Selection", "Please select a customer to delete.");
            return;
        }

        String customerName = nameField.getText().trim();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Customer: " + customerName);
        confirm.setContentText("Are you sure you want to delete this customer?\n\n" +
                "Note: Vehicles owned by this customer will have owner set to NULL.\n" +
                "This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    controller.delete(editingCustomerId);
                    clearForm();
                    refresh();
                    AlertUtil.info("Success", "Customer '" + customerName + "' has been deleted.");
                } catch (SQLException e) {
                    AlertUtil.error("Database Error", "Could not delete customer: " + e.getMessage());
                }
            }
        });
    }

    private void refresh() {
        try {
            data.setAll(controller.getAll());
            table.refresh();
        } catch (SQLException ex) {
            AlertUtil.error("Database error", "Could not load customers: " + ex.getMessage());
        }
    }
}