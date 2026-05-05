package com.vis.view;

import com.vis.controller.WorkshopController;
import com.vis.model.ServiceRecord;
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
import java.time.LocalDate;

public class WorkshopView {

    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private final WorkshopController controller = new WorkshopController();
    private final ObservableList<ServiceRecord> data = FXCollections.observableArrayList();
    private TableView<ServiceRecord> table;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;

    public Node build() {
        Label title = new Label("Workshop Services");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(80);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle("-fx-background-color: transparent;");

        TableColumn<ServiceRecord, Number> idCol = new TableColumn<>("Service ID");
        idCol.setCellValueFactory(c -> c.getValue().serviceIdProperty());
        idCol.setPrefWidth(80);

        TableColumn<ServiceRecord, String> regCol = new TableColumn<>("Registration");
        regCol.setCellValueFactory(c -> c.getValue().registrationNumberProperty());
        regCol.setPrefWidth(120);

        TableColumn<ServiceRecord, String> dateCol = new TableColumn<>("Service Date");
        dateCol.setCellValueFactory(c -> c.getValue().serviceDateProperty());
        dateCol.setPrefWidth(100);

        TableColumn<ServiceRecord, String> typeCol = new TableColumn<>("Service Type");
        typeCol.setCellValueFactory(c -> c.getValue().serviceTypeProperty());
        typeCol.setPrefWidth(120);

        TableColumn<ServiceRecord, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(c -> c.getValue().descriptionProperty());
        descCol.setPrefWidth(200);

        TableColumn<ServiceRecord, Number> costCol = new TableColumn<>("Cost ($)");
        costCol.setCellValueFactory(c -> c.getValue().costProperty());
        costCol.setPrefWidth(100);
        costCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        table.getColumns().addAll(idCol, regCol, dateCol, typeCol, descCol, costCol);

        // Progress section
        Label progressLabel = new Label("Today's Workshop Progress");
        progressLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
        progressLabel.setTextFill(Color.web(DARK_SLATE));

        progressBar = new ProgressBar(0.45);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: " + ROSE_GOLD + ";");

        progressIndicator = new ProgressIndicator(0.45);
        progressIndicator.setPrefSize(32, 32);
        progressIndicator.setStyle("-fx-progress-color: " + ROSE_GOLD + ";");

        Label progressPercent = new Label("45%");
        progressPercent.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        progressPercent.setTextFill(Color.web(ROSE_GOLD));

        HBox progressBox = new HBox(15, progressLabel, progressBar, progressIndicator, progressPercent);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.setPadding(new Insets(10, 0, 0, 0));

        // Form fields
        TextField vehicleIdField = createStyledTextField("Vehicle ID");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(
                "Oil Change", "Tire Rotation", "Brake Service", "Engine Tune-up",
                "Transmission Service", "Battery Replacement", "AC Service", "Inspection"
        ));
        typeBox.setPromptText("Service Type");
        typeBox.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        TextField descField = createStyledTextField("Description");
        TextField costField = createStyledTextField("Cost ($)");

        Button addBtn = createStyledButton("🔧 Register Service", ROSE_GOLD);
        Button reloadBtn = createStyledButton("⟳ Reload", DARK_SLATE);

        addBtn.setOnAction(e -> {
            try {
                int vid = Integer.parseInt(vehicleIdField.getText().trim());
                double cost = Double.parseDouble(costField.getText().trim());
                if (typeBox.getValue() == null) {
                    AlertUtil.warn("Validation", "Please select a service type.");
                    return;
                }
                controller.registerService(vid, datePicker.getValue(),
                        typeBox.getValue(), descField.getText(), cost);
                clearFields(vehicleIdField, descField, costField);
                typeBox.setValue(null);
                datePicker.setValue(LocalDate.now());
                refresh();
                updateProgress();
                AlertUtil.info("Success", "Service record added successfully!");
            } catch (NumberFormatException nfe) {
                AlertUtil.warn("Validation", "Vehicle ID and Cost must be numeric.");
            } catch (IllegalArgumentException iae) {
                AlertUtil.warn("Validation", iae.getMessage());
            } catch (SQLException ex) {
                AlertUtil.error("Database error", ex.getMessage());
            }
        });

        reloadBtn.setOnAction(e -> {
            refresh();
            updateProgress();
        });

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        form.add(createIconLabel("🚗"), 0, 0);
        form.add(vehicleIdField, 1, 0);
        form.add(createIconLabel("📅"), 2, 0);
        form.add(datePicker, 3, 0);
        form.add(createIconLabel("🔧"), 0, 1);
        form.add(typeBox, 1, 1);
        form.add(createIconLabel("💰"), 2, 1);
        form.add(costField, 3, 1);
        form.add(createIconLabel("📝"), 0, 2);
        form.add(descField, 1, 2, 3, 1);

        HBox actions = new HBox(12, addBtn, reloadBtn);
        actions.setPadding(new Insets(0, 20, 20, 20));

        VBox card = new VBox(form, actions, progressBox);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        VBox root = new VBox(15, title, accent, card, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setPadding(new Insets(20));

        refresh();
        updateProgress();
        return root;
    }

    private void updateProgress() {
        // Simulate progress based on number of records today
        try {
            int todayCount = controller.getTodayServiceCount();
            int target = 10;
            double progress = Math.min(1.0, todayCount / (double) target);
            progressBar.setProgress(progress);
            progressIndicator.setProgress(progress);

            // Find the percent label in the UI
            if (progressBar.getParent() instanceof HBox) {
                HBox parent = (HBox) progressBar.getParent();
                for (Node node : parent.getChildren()) {
                    if (node instanceof Label && ((Label) node).getText().contains("%")) {
                        ((Label) node).setText(Math.round(progress * 100) + "%");
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            // Ignore progress update errors
        }
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
            AlertUtil.error("Database error", ex.getMessage());
        }
    }
}