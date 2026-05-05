package com.vis.view;

import com.vis.controller.CustomerController;
import com.vis.controller.InsuranceController;
import com.vis.model.Customer;
import com.vis.model.Vehicle;
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
import java.util.List;

public class InsuranceView {

    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private final InsuranceController controller = new InsuranceController();
    private final CustomerController customerController = new CustomerController();
    private final ObservableList<Vehicle> data = FXCollections.observableArrayList();
    private TableView<Vehicle> table;

    public Node build() {
        Label title = new Label("📄 Insurance Management");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(80);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        // Table setup
        table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle("-fx-background-color: transparent;");

        // Table Columns
        TableColumn<Vehicle, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().vehicleIdProperty());
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Vehicle, String> regCol = new TableColumn<>("Registration");
        regCol.setCellValueFactory(c -> c.getValue().registrationNumberProperty());
        regCol.setPrefWidth(100);

        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(c -> c.getValue().makeProperty());
        makeCol.setPrefWidth(100);

        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(c -> c.getValue().modelProperty());
        modelCol.setPrefWidth(100);

        TableColumn<Vehicle, Number> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(c -> c.getValue().yearProperty());
        yearCol.setPrefWidth(60);
        yearCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Vehicle, String> ownerCol = new TableColumn<>("Insured Owner");
        ownerCol.setCellValueFactory(c -> c.getValue().ownerNameProperty());
        ownerCol.setPrefWidth(150);

        TableColumn<Vehicle, String> statusCol = new TableColumn<>("Policy Status");
        statusCol.setCellValueFactory(c -> c.getValue().insuranceStatusProperty());
        statusCol.setPrefWidth(100);
        statusCol.setStyle("-fx-alignment: CENTER;");

        // Color-code status column
        statusCol.setCellFactory(column -> new TableCell<Vehicle, String>() {
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
                    } else if ("Inactive".equals(item)) {
                        setTextFill(Color.web("#EF4444"));
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.web(DARK_SLATE));
                    }
                }
            }
        });

        table.getColumns().addAll(idCol, regCol, makeCol, modelCol, yearCol, ownerCol, statusCol);

        // Color-code rows based on status
        table.setRowFactory(tv -> new TableRow<Vehicle>() {
            @Override
            protected void updateItem(Vehicle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if ("Active".equals(item.getInsuranceStatus())) {
                    setStyle("-fx-background-color: #E8F5E9;");
                } else {
                    setStyle("-fx-background-color: " + ROSE_TINT + ";");
                }
            }
        });

        // Form fields
        TextField regField = createStyledTextField("Registration Number");
        TextField makeField = createStyledTextField("Make");
        TextField modelField = createStyledTextField("Model");
        TextField yearField = createStyledTextField("Year");

        ComboBox<Customer> ownerBox = new ComboBox<>();
        ownerBox.setPromptText("Select Policy Holder");
        ownerBox.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-font-size: 13px;"
        );
        ownerBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Customer c) {
                return c == null ? "" : c.getId() + " — " + c.getName();
            }
            @Override
            public Customer fromString(String s) { return null; }
        });

        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("Inactive", "Active"));
        statusBox.setValue("Inactive");
        statusBox.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-font-size: 13px;"
        );

        // Buttons
        Button registerBtn = createStyledButton("📄 Register Insurance", ROSE_GOLD);
        Button activateBtn = createStyledButton("✅ Activate Policy", "#10B981");
        Button renewBtn = createStyledButton("🔄 Renew Policy", "#3B82F6");
        Button reloadBtn = createStyledButton("⟳ Reload", DARK_SLATE);

        // Register Insurance Action
        registerBtn.setOnAction(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                Customer c = ownerBox.getValue();
                if (c == null) {
                    AlertUtil.warn("Validation", "Please select a policy holder.");
                    return;
                }
                controller.registerForInsurance(regField.getText(), makeField.getText(),
                        modelField.getText(), year, c.getId());
                clearFields(regField, makeField, modelField, yearField);
                ownerBox.setValue(null);
                statusBox.setValue("Inactive");
                refresh();
                AlertUtil.info("Success", "Vehicle registered for insurance successfully!");
            } catch (NumberFormatException nfe) {
                AlertUtil.warn("Validation", "Year must be numeric.");
            } catch (IllegalArgumentException iae) {
                AlertUtil.warn("Validation", iae.getMessage());
            } catch (SQLException ex) {
                AlertUtil.error("Database error", ex.getMessage());
            }
        });

        // Activate Policy Action
        activateBtn.setOnAction(e -> {
            Vehicle selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtil.warn("No Selection", "Please select a vehicle to activate policy.");
                return;
            }

            String currentStatus = selected.getInsuranceStatus();
            if ("Active".equals(currentStatus)) {
                AlertUtil.warn("Already Active", "This policy is already active.");
                return;
            }

            try {
                controller.updateInsuranceStatus(selected.getVehicleId(), "Active");
                refresh();
                AlertUtil.info("Policy Activated",
                        "Insurance policy for " + selected.getRegistrationNumber() + " has been ACTIVATED.");
            } catch (SQLException ex) {
                AlertUtil.error("Database error", "Could not activate policy: " + ex.getMessage());
            }
        });

        // Renew Policy Action
        renewBtn.setOnAction(e -> {
            Vehicle selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtil.warn("No Selection", "Please select a vehicle to renew policy.");
                return;
            }

            if (!"Active".equals(selected.getInsuranceStatus())) {
                AlertUtil.warn("Not Active", "Only active policies can be renewed. Please activate the policy first.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Renew Policy");
            confirm.setHeaderText("Renew Insurance Policy");
            confirm.setContentText("Are you sure you want to renew the policy for " +
                    selected.getRegistrationNumber() + "?\n\nNew expiry date will be set to 12 months from today.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        controller.renewInsurancePolicy(selected.getVehicleId());
                        refresh();
                        AlertUtil.info("Policy Renewed",
                                "Insurance policy for " + selected.getRegistrationNumber() + " has been RENEWED.");
                    } catch (SQLException ex) {
                        AlertUtil.error("Database error", "Could not renew policy: " + ex.getMessage());
                    }
                }
            });
        });

        reloadBtn.setOnAction(e -> refresh());

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        form.add(createIconLabel("🔢"), 0, 0);
        form.add(regField, 1, 0);
        form.add(createIconLabel("🏭"), 2, 0);
        form.add(makeField, 3, 0);
        form.add(createIconLabel("📐"), 0, 1);
        form.add(modelField, 1, 1);
        form.add(createIconLabel("📅"), 2, 1);
        form.add(yearField, 3, 1);
        form.add(createIconLabel("👤"), 0, 2);
        form.add(ownerBox, 1, 2);
        form.add(createIconLabel("📊"), 2, 2);
        form.add(statusBox, 3, 2);

        HBox actionButtons = new HBox(12, registerBtn, activateBtn, renewBtn, reloadBtn);
        actionButtons.setPadding(new Insets(0, 20, 20, 20));

        HBox infoBadge = new HBox(8);
        infoBadge.setAlignment(Pos.CENTER_LEFT);
        infoBadge.setPadding(new Insets(0, 20, 20, 20));

        Label infoLabel = new Label("ℹ️ Insurance covers: theft, accident damage, third-party liability.");
        infoLabel.setFont(Font.font("Helvetica", 11));
        infoLabel.setTextFill(Color.web(DARK_SLATE));
        infoBadge.getChildren().add(infoLabel);

        VBox card = new VBox(form, actionButtons, infoBadge);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        VBox root = new VBox(15, title, accent, card, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        try {
            ownerBox.getItems().setAll(customerController.getAll());
        } catch (SQLException ex) {
            AlertUtil.error("Database error", ex.getMessage());
        }
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
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: " + (bgColor.equals(ROSE_GOLD) ? BLACK_PEARL : "white") + ";" +
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
                        "-fx-text-fill: " + (bgColor.equals(ROSE_GOLD) ? BLACK_PEARL : "white") + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 20;" +
                        "-fx-cursor: hand;"
        ));
        return btn;
    }

    private Label createIconLabel(String icon) {
        Label label = new Label(icon);
        label.setFont(Font.font(18));
        return label;
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }

    // FIXED REFRESH METHOD - This was the problem!
    private void refresh() {
        try {
            // Clear existing data
            data.clear();

            // Fetch fresh data from database
            List<Vehicle> freshData = controller.getInsuredVehicles();

            // Add all fresh data to ObservableList
            data.addAll(freshData);

            // Force table to refresh
            table.refresh();

            // Debug output
            System.out.println("Refreshed data. Total vehicles: " + data.size());

        } catch (SQLException ex) {
            AlertUtil.error("Database error", "Could not refresh: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}