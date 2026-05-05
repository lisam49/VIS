package com.vis.view;

import com.vis.controller.PoliceController;
import com.vis.model.PoliceReport;
import com.vis.model.Violation;
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

public class PoliceView {

    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private final PoliceController controller = new PoliceController();
    private final ObservableList<PoliceReport> reports = FXCollections.observableArrayList();
    private final ObservableList<Violation> violations = FXCollections.observableArrayList();

    public Node build() {
        Label title = new Label("Police & Enforcement");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(80);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        TabPane tabs = new TabPane(buildReportsTab(), buildViolationsTab());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-tab-text-fill: " + DARK_SLATE + ";" +
                        "-fx-tab-border-color: " + ROSE_GOLD + ";"
        );

        VBox root = new VBox(15, title, accent, tabs);
        VBox.setVgrow(tabs, Priority.ALWAYS);
        root.setPadding(new Insets(20));
        return root;
    }

    private Tab buildReportsTab() {
        TableView<PoliceReport> table = new TableView<>(reports);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle("-fx-background-color: transparent;");

        TableColumn<PoliceReport, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().reportIdProperty());
        idCol.setPrefWidth(60);

        TableColumn<PoliceReport, String> regCol = new TableColumn<>("Registration");
        regCol.setCellValueFactory(c -> c.getValue().registrationNumberProperty());
        regCol.setPrefWidth(120);

        TableColumn<PoliceReport, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> c.getValue().reportDateProperty());
        dateCol.setPrefWidth(100);

        TableColumn<PoliceReport, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> c.getValue().reportTypeProperty());
        typeCol.setPrefWidth(100);

        TableColumn<PoliceReport, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(c -> c.getValue().descriptionProperty());
        descCol.setPrefWidth(200);

        TableColumn<PoliceReport, String> offCol = new TableColumn<>("Officer");
        offCol.setCellValueFactory(c -> c.getValue().officerNameProperty());
        offCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, regCol, dateCol, typeCol, descCol, offCol);

        // Form fields
        TextField vidField = createStyledTextField("Vehicle ID");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(
                "Accident", "Theft", "Vandalism", "Stolen Vehicle", "Suspicious Activity"
        ));
        typeBox.setPromptText("Report Type");
        typeBox.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        TextField descField = createStyledTextField("Description");
        TextField offField = createStyledTextField("Officer Name");

        Button addBtn = createStyledButton("📋 File Report", ROSE_GOLD);
        Button reloadBtn = createStyledButton("⟳ Reload", DARK_SLATE);

        addBtn.setOnAction(e -> {
            try {
                int vid = Integer.parseInt(vidField.getText().trim());
                if (typeBox.getValue() == null) {
                    AlertUtil.warn("Validation", "Please select a report type.");
                    return;
                }
                controller.fileReport(vid, datePicker.getValue(), typeBox.getValue(),
                        descField.getText(), offField.getText());
                clearFields(vidField, descField, offField);
                typeBox.setValue(null);
                datePicker.setValue(LocalDate.now());
                refreshReports();
                AlertUtil.info("Success", "Police report filed successfully!");
            } catch (NumberFormatException nfe) {
                AlertUtil.warn("Validation", "Vehicle ID must be numeric.");
            } catch (SQLException ex) {
                AlertUtil.error("Database error", ex.getMessage());
            }
        });

        reloadBtn.setOnAction(e -> refreshReports());

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        form.add(createIconLabel("🚗"), 0, 0);
        form.add(vidField, 1, 0);
        form.add(createIconLabel("📅"), 2, 0);
        form.add(datePicker, 3, 0);
        form.add(createIconLabel("📋"), 0, 1);
        form.add(typeBox, 1, 1);
        form.add(createIconLabel("👮"), 2, 1);
        form.add(offField, 3, 1);
        form.add(createIconLabel("📝"), 0, 2);
        form.add(descField, 1, 2, 3, 1);

        HBox actions = new HBox(12, addBtn, reloadBtn);
        actions.setPadding(new Insets(0, 20, 20, 20));

        VBox card = new VBox(form, actions);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        VBox box = new VBox(12, card, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setPadding(new Insets(10, 0, 0, 0));

        Tab tab = new Tab("📋 Police Reports", box);
        refreshReports();
        return tab;
    }

    private Tab buildViolationsTab() {
        TableView<Violation> table = new TableView<>(violations);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle("-fx-background-color: transparent;");

        TableColumn<Violation, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().violationIdProperty());
        idCol.setPrefWidth(60);

        TableColumn<Violation, String> regCol = new TableColumn<>("Registration");
        regCol.setCellValueFactory(c -> c.getValue().registrationNumberProperty());
        regCol.setPrefWidth(120);

        TableColumn<Violation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> c.getValue().violationDateProperty());
        dateCol.setPrefWidth(100);

        TableColumn<Violation, String> typeCol = new TableColumn<>("Violation Type");
        typeCol.setCellValueFactory(c -> c.getValue().violationTypeProperty());
        typeCol.setPrefWidth(150);

        TableColumn<Violation, Number> fineCol = new TableColumn<>("Fine (M)");
        fineCol.setCellValueFactory(c -> c.getValue().fineAmountProperty());
        fineCol.setPrefWidth(80);
        fineCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Violation, String> stCol = new TableColumn<>("Status");
        stCol.setCellValueFactory(c -> c.getValue().statusProperty());
        stCol.setPrefWidth(100);
        stCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(idCol, regCol, dateCol, typeCol, fineCol, stCol);

        // Color-code rows based on status
        table.setRowFactory(tv -> new TableRow<Violation>() {
            @Override
            protected void updateItem(Violation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if ("Paid".equals(item.getStatus())) {
                    setStyle("-fx-background-color: " + ROSE_TINT + ";");
                } else {
                    setStyle("-fx-background-color: #FFF0F0;");
                }
            }
        });

        // Form fields
        TextField vidField = createStyledTextField("Vehicle ID");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(
                "Speeding", "No Insurance", "Expired Registration", "Parking Violation",
                "Reckless Driving", "DUI", "Seatbelt Violation", "Red Light Violation"
        ));
        typeBox.setPromptText("Violation Type");
        typeBox.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        TextField fineField = createStyledTextField("Fine Amount (M)");

        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("Unpaid", "Paid"));
        statusBox.setValue("Unpaid");
        statusBox.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        Button addBtn = createStyledButton("⚠️ Issue Violation", ROSE_GOLD);
        Button payBtn = createStyledButton(" Mark Selected as Paid", DARK_SLATE);
        Button reloadBtn = createStyledButton("⟳ Reload", DARK_SLATE);

        addBtn.setOnAction(e -> {
            try {
                int vid = Integer.parseInt(vidField.getText().trim());
                double fine = Double.parseDouble(fineField.getText().trim());
                if (typeBox.getValue() == null) {
                    AlertUtil.warn("Validation", "Please select a violation type.");
                    return;
                }
                controller.issueViolation(vid, datePicker.getValue(), typeBox.getValue(), fine, statusBox.getValue());
                clearFields(vidField, fineField);
                typeBox.setValue(null);
                datePicker.setValue(LocalDate.now());
                refreshViolations();
                AlertUtil.info("Success", "Violation issued successfully!");
            } catch (NumberFormatException nfe) {
                AlertUtil.warn("Validation", "Vehicle ID and fine must be numeric.");
            } catch (SQLException ex) {
                AlertUtil.error("Database error", ex.getMessage());
            }
        });

        payBtn.setOnAction(e -> {
            Violation v = table.getSelectionModel().getSelectedItem();
            if (v == null) {
                AlertUtil.warn("Nothing selected", "Please select a violation to mark as paid.");
                return;
            }
            try {
                controller.payViolation(v.getViolationId());
                refreshViolations();
                AlertUtil.info("Success", "Violation marked as paid!");
            } catch (SQLException ex) {
                AlertUtil.error("Database error", ex.getMessage());
            }
        });

        reloadBtn.setOnAction(e -> refreshViolations());

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        form.add(createIconLabel("🚗"), 0, 0);
        form.add(vidField, 1, 0);
        form.add(createIconLabel("📅"), 2, 0);
        form.add(datePicker, 3, 0);
        form.add(createIconLabel("⚡"), 0, 1);
        form.add(typeBox, 1, 1);
        form.add(createIconLabel("💰"), 2, 1);
        form.add(fineField, 3, 1);
        form.add(createIconLabel("🏷️"), 0, 2);
        form.add(statusBox, 1, 2);

        HBox actions = new HBox(12, addBtn, payBtn, reloadBtn);
        actions.setPadding(new Insets(0, 20, 20, 20));

        VBox card = new VBox(form, actions);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        VBox box = new VBox(12, card, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setPadding(new Insets(10, 0, 0, 0));

        Tab tab = new Tab("⚠️ Violations", box);
        refreshViolations();
        return tab;
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

    private void refreshReports() {
        try {
            reports.setAll(controller.getReports());
        } catch (SQLException ex) {
            AlertUtil.error("Database error", ex.getMessage());
        }
    }

    private void refreshViolations() {
        try {
            violations.setAll(controller.getViolations());
        } catch (SQLException ex) {
            AlertUtil.error("Database error", ex.getMessage());
        }
    }
}