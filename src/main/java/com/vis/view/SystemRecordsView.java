package com.vis.view;

import com.vis.controller.*;
import com.vis.model.*;
import com.vis.util.AlertUtil;
import com.vis.util.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SystemRecordsView {

    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String ROSE_TINT = "#FFF0F3";
    private static final String BLACK_PEARL = "#0A0A0A";

    private final VehicleController vehicleController = new VehicleController();
    private final ServiceRecordController serviceController = new ServiceRecordController();
    private final PoliceController policeController = new PoliceController();
    private final InsuranceController insuranceController = new InsuranceController();

    public Node build() {
        AppUser currentUser = SessionManager.getCurrentUser();
        AppUser.Role role = currentUser.getRole();

        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        Label title = new Label("📜 System Records (" + role + " View)");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        title.setTextFill(Color.web(DARK_SLATE));

        Label subtitle = new Label("Viewing " + getRoleSpecificSubtitle(role) + " records from the database.");
        subtitle.setFont(Font.font("Helvetica", 12));
        subtitle.setTextFill(Color.web(ROSE_GOLD));
        subtitle.setWrapText(true);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");

        // ROLE-BASED TABS - Only show what each role should see
        switch (role) {
            case ADMIN:
                // Admin sees everything
                tabPane.getTabs().addAll(
                        createTab("🚗 Vehicles", createVehiclesScrollPane(true, currentUser)),
                        createTab("🔧 Service Records", createServicesScrollPane(true, currentUser)),
                        createTab("👮 Police Reports", createPoliceScrollPane()),
                        createTab("⚠️ Violations", createViolationsScrollPane(true, currentUser)),
                        createTab("📄 Insurance Records", createInsuranceScrollPane())
                );
                break;

            case WORKSHOP:
                // Workshop sees: Vehicles + Service Records only
                tabPane.getTabs().addAll(
                        createTab("🚗 Vehicles", createVehiclesScrollPane(true, currentUser)),
                        createTab("🔧 Service Records", createServicesScrollPane(true, currentUser))
                );
                break;

            case CUSTOMER:
                // Customer sees: ONLY their own vehicles + their own service records + their own violations
                tabPane.getTabs().addAll(
                        createTab("🚗 My Vehicles", createVehiclesScrollPane(false, currentUser)),
                        createTab("🔧 My Service Records", createServicesScrollPane(false, currentUser)),
                        createTab("⚠️ My Violations", createViolationsScrollPane(false, currentUser))
                );
                break;

            case POLICE:
                // Police sees: Vehicles (for lookup) + Police Reports + Violations
                tabPane.getTabs().addAll(
                        createTab("🚗 Vehicles (Search)", createVehiclesScrollPane(true, currentUser)),
                        createTab("👮 Police Reports", createPoliceScrollPane()),
                        createTab("⚠️ Violations", createViolationsScrollPane(true, currentUser))
                );
                break;

            case INSURANCE:
                // Insurance sees: Vehicles + Insurance Records ONLY
                tabPane.getTabs().addAll(
                        createTab("🚗 Vehicles", createVehiclesScrollPane(true, currentUser)),
                        createTab("📄 Insurance Records", createInsuranceScrollPane())
                );
                break;

            default:
                tabPane.getTabs().add(createTab("🚗 Vehicles", createVehiclesScrollPane(true, currentUser)));
                break;
        }

        // Stats row at the bottom (role-based)
        HBox statsRow = createStatsRow(role);

        VBox root = new VBox(15, title, subtitle, tabPane, statsRow);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        return root;
    }

    private Tab createTab(String text, Node content) {
        Tab tab = new Tab(text, content);
        tab.setClosable(false);
        return tab;
    }

    private String getRoleSpecificSubtitle(AppUser.Role role) {
        switch (role) {
            case ADMIN: return "all system";
            case WORKSHOP: return "vehicle and service";
            case CUSTOMER: return "your personal vehicle, service, and violation";
            case POLICE: return "vehicle lookup, police reports, and violation";
            case INSURANCE: return "vehicle and insurance";
            default: return "system";
        }
    }

    private ScrollPane createVehiclesScrollPane(boolean showAll, AppUser currentUser) {
        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        try {
            List<Vehicle> vehicles = vehicleController.getAll();

            // Filter for customers (only show their own vehicles)
            if (!showAll && currentUser.getRole() == AppUser.Role.CUSTOMER) {
                String customerName = currentUser.getFullName();
                vehicles = vehicles.stream()
                        .filter(v -> customerName.equals(v.getOwnerName()))
                        .collect(Collectors.toList());
            }

            // Header
            HBox header = createHeaderRow("ID", "Registration", "Make", "Model", "Year", "Owner");
            content.getChildren().add(header);

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: " + ROSE_GOLD + ";");
            content.getChildren().add(sep);

            int count = 0;
            for (Vehicle v : vehicles) {
                HBox row = createDataRow(
                        String.valueOf(v.getVehicleId()),
                        v.getRegistrationNumber(),
                        v.getMake(),
                        v.getModel(),
                        String.valueOf(v.getYear()),
                        v.getOwnerName() == null ? "Unassigned" : v.getOwnerName()
                );

                if (count % 2 == 0) {
                    row.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-background-radius: 6;");
                }

                content.getChildren().add(row);
                count++;
            }

            Label countLabel = new Label("📊 Total Vehicles: " + count);
            countLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
            countLabel.setTextFill(Color.web(ROSE_GOLD));
            countLabel.setPadding(new Insets(10, 0, 0, 0));
            content.getChildren().add(countLabel);

        } catch (SQLException e) {
            content.getChildren().add(new Label("Error loading vehicles: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8;");
        return scrollPane;
    }

    private ScrollPane createServicesScrollPane(boolean showAll, AppUser currentUser) {
        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        try {
            List<ServiceRecord> services = serviceController.getAll();

            // Filter for customers (only show their own vehicle services)
            if (!showAll && currentUser.getRole() == AppUser.Role.CUSTOMER) {
                String customerName = currentUser.getFullName();
                List<Vehicle> myVehicles = vehicleController.getAll().stream()
                        .filter(v -> customerName.equals(v.getOwnerName()))
                        .collect(Collectors.toList());
                List<Integer> myVehicleIds = myVehicles.stream()
                        .map(Vehicle::getVehicleId)
                        .collect(Collectors.toList());
                services = services.stream()
                        .filter(s -> myVehicleIds.contains(s.getVehicleId()))
                        .collect(Collectors.toList());
            }

            HBox header = createHeaderRow("Service ID", "Registration", "Date", "Type", "Cost");
            content.getChildren().add(header);

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: " + ROSE_GOLD + ";");
            content.getChildren().add(sep);

            int count = 0;
            for (ServiceRecord s : services) {
                HBox row = createDataRow(
                        String.valueOf(s.getServiceId()),
                        s.getRegistrationNumber(),
                        s.getServiceDate() == null ? "N/A" : s.getServiceDate().toString(),
                        s.getServiceType(),
                        "L" + String.format("%.2f", s.getCost())
                );

                if (count % 2 == 0) {
                    row.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-background-radius: 6;");
                }

                content.getChildren().add(row);
                count++;
            }

            Label countLabel = new Label("📊 Total Service Records: " + count);
            countLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
            countLabel.setTextFill(Color.web(ROSE_GOLD));
            countLabel.setPadding(new Insets(10, 0, 0, 0));
            content.getChildren().add(countLabel);

        } catch (SQLException e) {
            content.getChildren().add(new Label("Error loading services: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8;");
        return scrollPane;
    }

    private ScrollPane createPoliceScrollPane() {
        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        try {
            List<PoliceReport> reports = policeController.getReports();

            HBox header = createHeaderRow("Report ID", "Registration", "Date", "Type", "Officer");
            content.getChildren().add(header);

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: " + ROSE_GOLD + ";");
            content.getChildren().add(sep);

            int count = 0;
            for (PoliceReport r : reports) {
                HBox row = createDataRow(
                        String.valueOf(r.getReportId()),
                        r.getRegistrationNumber(),
                        r.getReportDate() == null ? "N/A" : r.getReportDate().toString(),
                        r.getReportType(),
                        r.getOfficerName()
                );

                if (count % 2 == 0) {
                    row.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-background-radius: 6;");
                }

                content.getChildren().add(row);
                count++;
            }

            Label countLabel = new Label("📊 Total Police Reports: " + count);
            countLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
            countLabel.setTextFill(Color.web(ROSE_GOLD));
            countLabel.setPadding(new Insets(10, 0, 0, 0));
            content.getChildren().add(countLabel);

        } catch (SQLException e) {
            content.getChildren().add(new Label("Error loading police reports: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8;");
        return scrollPane;
    }

    private ScrollPane createViolationsScrollPane(boolean showAll, AppUser currentUser) {
        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        try {
            List<Violation> violations = policeController.getViolations();

            // Filter for customers (only show violations on their own vehicles)
            if (!showAll && currentUser.getRole() == AppUser.Role.CUSTOMER) {
                String customerName = currentUser.getFullName();
                List<Vehicle> myVehicles = vehicleController.getAll().stream()
                        .filter(v -> customerName.equals(v.getOwnerName()))
                        .collect(Collectors.toList());
                List<String> myRegistrationNumbers = myVehicles.stream()
                        .map(Vehicle::getRegistrationNumber)
                        .collect(Collectors.toList());
                violations = violations.stream()
                        .filter(v -> myRegistrationNumbers.contains(v.getRegistrationNumber()))
                        .collect(Collectors.toList());
            }

            HBox header = createHeaderRow("Violation ID", "Registration", "Date", "Type", "Fine", "Status");
            content.getChildren().add(header);

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: " + ROSE_GOLD + ";");
            content.getChildren().add(sep);

            int count = 0;
            for (Violation v : violations) {
                String statusColor = v.getStatus().equals("Paid") ? "#10B981" : "#EF4444";

                HBox row = createDataRow(
                        String.valueOf(v.getViolationId()),
                        v.getRegistrationNumber(),
                        v.getViolationDate() == null ? "N/A" : v.getViolationDate().toString(),
                        v.getViolationType(),
                        "L" + String.format("%.2f", v.getFineAmount()),
                        v.getStatus()
                );

                // Color-code the status
                for (Node node : row.getChildren()) {
                    if (node instanceof Label && ((Label) node).getText().equals(v.getStatus())) {
                        ((Label) node).setTextFill(Color.web(statusColor));
                        ((Label) node).setStyle("-fx-font-weight: bold;");
                    }
                }

                if (count % 2 == 0) {
                    row.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-background-radius: 6;");
                }

                content.getChildren().add(row);
                count++;
            }

            Label countLabel = new Label("📊 Total Violations: " + count);
            countLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
            countLabel.setTextFill(Color.web(ROSE_GOLD));
            countLabel.setPadding(new Insets(10, 0, 0, 0));
            content.getChildren().add(countLabel);

            if (count == 0 && !showAll) {
                Label emptyLabel = new Label("No violations found on your vehicles.");
                emptyLabel.setFont(Font.font("Helvetica", 12));
                emptyLabel.setTextFill(Color.web(DARK_SLATE));
                emptyLabel.setPadding(new Insets(20, 0, 0, 0));
                content.getChildren().add(emptyLabel);
            }

        } catch (SQLException e) {
            content.getChildren().add(new Label("Error loading violations: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8;");
        return scrollPane;
    }

    private ScrollPane createInsuranceScrollPane() {
        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        try {
            List<Vehicle> insuredVehicles = insuranceController.getInsuredVehicles();

            HBox header = createHeaderRow("ID", "Registration", "Make", "Model", "Owner", "Status", "Policy #");
            content.getChildren().add(header);

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: " + ROSE_GOLD + ";");
            content.getChildren().add(sep);

            int count = 0;
            for (Vehicle v : insuredVehicles) {
                String statusColor = "Active".equals(v.getInsuranceStatus()) ? "#10B981" : "#EF4444";

                HBox row = createDataRow(
                        String.valueOf(v.getVehicleId()),
                        v.getRegistrationNumber(),
                        v.getMake(),
                        v.getModel(),
                        v.getOwnerName() == null ? "Unassigned" : v.getOwnerName(),
                        v.getInsuranceStatus(),
                        v.getPolicyNumber() == null ? "Not Issued" : v.getPolicyNumber()
                );

                // Color-code the status
                for (Node node : row.getChildren()) {
                    if (node instanceof Label && ((Label) node).getText().equals(v.getInsuranceStatus())) {
                        ((Label) node).setTextFill(Color.web(statusColor));
                        ((Label) node).setStyle("-fx-font-weight: bold;");
                    }
                }

                if (count % 2 == 0) {
                    row.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-background-radius: 6;");
                }

                content.getChildren().add(row);
                count++;
            }

            Label countLabel = new Label("📊 Total Insured Vehicles: " + count);
            countLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
            countLabel.setTextFill(Color.web(ROSE_GOLD));
            countLabel.setPadding(new Insets(10, 0, 0, 0));
            content.getChildren().add(countLabel);

        } catch (SQLException e) {
            content.getChildren().add(new Label("Error loading insurance records: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: " + ROSE_GOLD + "; -fx-border-radius: 8;");
        return scrollPane;
    }

    private HBox createHeaderRow(String... headers) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 6;");

        double[] widths = {60, 100, 100, 120, 120, 100, 120, 100};

        for (int i = 0; i < headers.length; i++) {
            Label label = new Label(headers[i]);
            label.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
            label.setTextFill(Color.web(BLACK_PEARL));
            label.setPrefWidth(widths[Math.min(i, widths.length - 1)]);
            row.getChildren().add(label);
        }

        return row;
    }

    private HBox createDataRow(String... values) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 12, 6, 12));

        double[] widths = {60, 100, 100, 120, 120, 100, 120, 100};

        for (int i = 0; i < values.length; i++) {
            Label label = new Label(values[i]);
            label.setFont(Font.font("Helvetica", 12));
            label.setTextFill(Color.web(DARK_SLATE));
            label.setPrefWidth(widths[Math.min(i, widths.length - 1)]);
            row.getChildren().add(label);
        }

        return row;
    }

    private HBox createStatsRow(AppUser.Role role) {
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.setPadding(new Insets(15, 0, 0, 0));

        try {
            int vehicleCount = vehicleController.getAll().size();
            Label vehiclesStat = new Label("🚗 Vehicles: " + vehicleCount);
            vehiclesStat.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
            vehiclesStat.setTextFill(Color.web(ROSE_GOLD));
            statsRow.getChildren().add(vehiclesStat);

            // Role-specific stats
            switch (role) {
                case ADMIN:
                    int serviceCount = serviceController.getAll().size();
                    int reportCount = policeController.getReports().size();
                    int violationCount = policeController.getViolations().size();
                    int insuranceCount = insuranceController.getInsuredVehicles().size();

                    statsRow.getChildren().addAll(
                            new Label("🔧 Services: " + serviceCount),
                            new Label("📋 Reports: " + reportCount),
                            new Label("⚠️ Violations: " + violationCount),
                            new Label("📄 Insurance: " + insuranceCount)
                    );
                    break;

                case WORKSHOP:
                    int svcCount = serviceController.getAll().size();
                    statsRow.getChildren().add(new Label("🔧 Services: " + svcCount));
                    break;

                case POLICE:
                    int rptCount = policeController.getReports().size();
                    int vioCount = policeController.getViolations().size();
                    statsRow.getChildren().addAll(
                            new Label("📋 Reports: " + rptCount),
                            new Label("⚠️ Violations: " + vioCount)
                    );
                    break;

                case INSURANCE:
                    int insCount = insuranceController.getInsuredVehicles().size();
                    statsRow.getChildren().add(new Label("📄 Insured: " + insCount));
                    break;

                case CUSTOMER:
                    // For customer, show counts of their own records
                    String customerName = SessionManager.getCurrentUser().getFullName();
                    List<Vehicle> myVehicles = vehicleController.getAll().stream()
                            .filter(v -> customerName.equals(v.getOwnerName()))
                            .collect(Collectors.toList());
                    List<Integer> myVehicleIds = myVehicles.stream()
                            .map(Vehicle::getVehicleId)
                            .collect(Collectors.toList());
                    int myServiceCount = serviceController.getAll().stream()
                            .filter(s -> myVehicleIds.contains(s.getVehicleId()))
                            .collect(Collectors.toList()).size();
                    List<String> myRegNumbers = myVehicles.stream()
                            .map(Vehicle::getRegistrationNumber)
                            .collect(Collectors.toList());
                    int myViolationCount = policeController.getViolations().stream()
                            .filter(v -> myRegNumbers.contains(v.getRegistrationNumber()))
                            .collect(Collectors.toList()).size();

                    statsRow.getChildren().addAll(
                            new Label("🔧 My Services: " + myServiceCount),
                            new Label("⚠️ My Violations: " + myViolationCount)
                    );
                    break;

                default:
                    break;
            }

            // Color all stat labels
            for (Node node : statsRow.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setTextFill(Color.web(ROSE_GOLD));
                    ((Label) node).setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
                }
            }

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            statsRow.getChildren().add(spacer);

        } catch (SQLException e) {
            statsRow.getChildren().add(new Label("Could not load stats"));
        }

        return statsRow;
    }
}