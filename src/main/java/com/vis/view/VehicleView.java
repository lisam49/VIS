package com.vis.view;

import com.vis.controller.CustomerController;
import com.vis.controller.VehicleController;
import com.vis.model.Customer;
import com.vis.model.Vehicle;
import com.vis.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;

public class VehicleView {

    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private final VehicleController controller = new VehicleController();
    private final CustomerController customerController = new CustomerController();
    private TableView<Vehicle> table;
    private final ObservableList<Vehicle> data = FXCollections.observableArrayList();

    public Node build() {
        Label title = new Label("Vehicle Registry");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(80);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle("-fx-background-color: transparent;");

        TableColumn<Vehicle, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().vehicleIdProperty());
        idCol.setPrefWidth(60);

        TableColumn<Vehicle, String> regCol = new TableColumn<>("Registration");
        regCol.setCellValueFactory(c -> c.getValue().registrationNumberProperty());
        regCol.setPrefWidth(120);

        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(c -> c.getValue().makeProperty());
        makeCol.setPrefWidth(100);

        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(c -> c.getValue().modelProperty());
        modelCol.setPrefWidth(120);

        TableColumn<Vehicle, Number> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(c -> c.getValue().yearProperty());
        yearCol.setPrefWidth(80);

        TableColumn<Vehicle, String> ownerCol = new TableColumn<>("Owner");
        ownerCol.setCellValueFactory(c -> c.getValue().ownerNameProperty());
        ownerCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, regCol, makeCol, modelCol, yearCol, ownerCol);

        // Form fields
        TextField regField = createStyledTextField("Registration Number");
        TextField makeField = createStyledTextField("Make");
        TextField modelField = createStyledTextField("Model");
        TextField yearField = createStyledTextField("Year");

        ComboBox<Customer> ownerBox = new ComboBox<>();
        ownerBox.setPromptText("Select Owner");
        ownerBox.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );
        ownerBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Customer c) {
                return c == null ? "" : c.getId() + " — " + c.getName();
            }
            @Override public Customer fromString(String s) { return null; }
        });

        Button addBtn = createStyledButton("➕ Add Vehicle", ROSE_GOLD);
        Button delBtn = createStyledButton("🗑 Delete Selected", DARK_SLATE);
        Button reloadBtn = createStyledButton("⟳ Reload", DARK_SLATE);

        addBtn.setOnAction(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                Customer owner = ownerBox.getValue();
                controller.addVehicle(regField.getText(), makeField.getText(),
                        modelField.getText(), year, owner == null ? null : owner.getId());
                clearFields(regField, makeField, modelField, yearField);
                ownerBox.setValue(null);
                refresh();
                AlertUtil.info("Success", "Vehicle added successfully!");
            } catch (NumberFormatException nfe) {
                AlertUtil.warn("Validation", "Year must be a valid number.");
            } catch (IllegalArgumentException iae) {
                AlertUtil.warn("Validation", iae.getMessage());
            } catch (SQLException ex) {
                AlertUtil.error("Database error", ex.getMessage());
            }
        });

        delBtn.setOnAction(e -> {
            Vehicle v = table.getSelectionModel().getSelectedItem();
            if (v == null) {
                AlertUtil.warn("Nothing selected", "Please select a row first.");
                return;
            }
            try {
                controller.delete(v.getVehicleId());
                refresh();
                AlertUtil.info("Deleted", "Vehicle removed successfully.");
            } catch (SQLException ex) {
                AlertUtil.error("Database error", ex.getMessage());
            }
        });

        reloadBtn.setOnAction(e -> refresh());

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        form.add(createIconLabel("🚗"), 0, 0);
        form.add(regField, 1, 0);
        form.add(createIconLabel("🏭"), 2, 0);
        form.add(makeField, 3, 0);
        form.add(createIconLabel("📐"), 0, 1);
        form.add(modelField, 1, 1);
        form.add(createIconLabel("📅"), 2, 1);
        form.add(yearField, 3, 1);
        form.add(createIconLabel("👤"), 0, 2);
        form.add(ownerBox, 1, 2, 3, 1);

        HBox actions = new HBox(12, addBtn, delBtn, reloadBtn);
        actions.setPadding(new Insets(0, 20, 20, 20));

        VBox card = new VBox(form, actions);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        VBox root = new VBox(15, title, accent, card, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setPadding(new Insets(20));

        try {
            ownerBox.getItems().setAll(customerController.getAll());
        } catch (SQLException ex) {
            AlertUtil.error("Database error", "Could not load customers: " + ex.getMessage());
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
                        "-fx-text-fill: " + (bgColor.equals(ROSE_GOLD) ? BLACK_PEARL : PEARL_WHITE) + ";" +
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
                        "-fx-text-fill: " + (bgColor.equals(ROSE_GOLD) ? BLACK_PEARL : PEARL_WHITE) + ";" +
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

    private void refresh() {
        try {
            data.setAll(controller.getAll());
        } catch (SQLException ex) {
            AlertUtil.error("Database error", "Could not load vehicles: " + ex.getMessage());
        }
    }
}