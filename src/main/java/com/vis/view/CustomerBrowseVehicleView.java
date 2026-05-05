package com.vis.view;

import com.vis.controller.VehicleController;
import com.vis.model.Vehicle;
import com.vis.util.AlertUtil;
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
import java.util.Optional;

public class CustomerBrowseVehicleView {

    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";
    private static final String BLACK_PEARL = "#0A0A0A";

    private static final int ITEMS_PER_PAGE = 6;
    private final VehicleController controller = new VehicleController();
    private List<Vehicle> allVehicles;

    public Node build() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        // Header
        Label title = new Label("🏠 Buy Second-Hand Vehicles");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(150);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        Label subtitle = new Label("Browse available vehicles and contact owners directly to purchase second-hand vehicles.");
        subtitle.setFont(Font.font("Helvetica", 12));
        subtitle.setTextFill(Color.web(ROSE_GOLD));
        subtitle.setWrapText(true);

        // Load vehicles
        try {
            allVehicles = controller.getAll();
        } catch (SQLException e) {
            AlertUtil.error("Database error", "Could not load vehicles: " + e.getMessage());
            allVehicles = List.of();
        }

        // Stats row
        HBox statsRow = createStatsRow();

        // Pagination
        int pageCount = Math.max(1, (int) Math.ceil(allVehicles.size() / (double) ITEMS_PER_PAGE));
        Pagination pagination = new Pagination(pageCount, 0);
        pagination.setPageFactory(this::createVehicleCardPage);
        pagination.setStyle(
                "-fx-page-information-visible: false;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-padding: 10 0 15 0;"
        );

        VBox.setVgrow(pagination, Priority.ALWAYS);
        root.getChildren().addAll(title, accent, subtitle, statsRow, pagination);
        return root;
    }

    private HBox createStatsRow() {
        HBox statsRow = new HBox(20);
        statsRow.setPadding(new Insets(10, 0, 15, 0));

        int total = allVehicles.size();
        int available = (int) allVehicles.stream().filter(v -> v.getOwnerName() != null && !v.getOwnerName().isEmpty()).count();

        Label totalLabel = new Label("🚗 Total Vehicles: " + total);
        totalLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
        totalLabel.setTextFill(Color.web(ROSE_GOLD));

        Label availableLabel = new Label("📞 Available to Contact: " + available);
        availableLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 12));
        availableLabel.setTextFill(Color.web(ROSE_GOLD));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statsRow.getChildren().addAll(totalLabel, availableLabel, spacer);
        return statsRow;
    }

    private Node createVehicleCardPage(int pageIndex) {
        FlowPane cardContainer = new FlowPane();
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(15, 0, 15, 0));
        cardContainer.setPrefWrapLength(900);

        int start = pageIndex * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allVehicles.size());

        for (int i = start; i < end; i++) {
            cardContainer.getChildren().add(createVehicleCard(allVehicles.get(i)));
        }

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;" +
                        "-fx-border-color: transparent;"
        );
        return scrollPane;
    }

    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox(0);
        card.setPrefWidth(320);
        card.setMaxWidth(320);

        // Header with vehicle image placeholder
        Region header = new Region();
        header.setPrefHeight(100);
        header.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + BLACK_PEARL + ", " + DARK_SLATE + ");" +
                        "-fx-background-radius: 12 12 0 0;"
        );

        // Car icon overlay
        StackPane headerOverlay = new StackPane();
        Label carIcon = new Label("🚗");
        carIcon.setFont(Font.font(48));
        carIcon.setTextFill(Color.web(ROSE_GOLD));
        headerOverlay.getChildren().addAll(header, carIcon);

        // Content area
        VBox content = new VBox(8);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 12 12;");

        // Registration number
        Label regNumber = new Label(vehicle.getRegistrationNumber());
        regNumber.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        regNumber.setTextFill(Color.web(BLACK_PEARL));

        // Rose gold accent
        Region accentLine = new Region();
        accentLine.setPrefHeight(2);
        accentLine.setPrefWidth(40);
        accentLine.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 1;");

        // Vehicle details
        Label vehicleDetails = new Label(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
        vehicleDetails.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 13));
        vehicleDetails.setTextFill(Color.web(DARK_SLATE));

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        // Owner info
        HBox ownerRow = new HBox(8);
        ownerRow.setAlignment(Pos.CENTER_LEFT);

        Label ownerIcon = new Label("👤");
        ownerIcon.setFont(Font.font(12));

        String ownerName = vehicle.getOwnerName() == null ? "Unknown" : vehicle.getOwnerName();
        Label ownerLabel = new Label("Owner: " + ownerName);
        ownerLabel.setFont(Font.font("Helvetica", 11));
        ownerLabel.setTextFill(Color.web(ROSE_GOLD));

        ownerRow.getChildren().addAll(ownerIcon, ownerLabel);

        // Contact button
        Button contactBtn = new Button("📞 Contact Owner");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setStyle(
                "-fx-background-color: " + ROSE_GOLD + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-cursor: hand;"
        );

        contactBtn.setOnMouseEntered(e -> contactBtn.setStyle(
                "-fx-background-color: " + SOFT_PINK + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-cursor: hand;"
        ));

        contactBtn.setOnMouseExited(e -> contactBtn.setStyle(
                "-fx-background-color: " + ROSE_GOLD + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-cursor: hand;"
        ));

        contactBtn.setOnAction(e -> showContactDialog(vehicle));

        content.getChildren().addAll(regNumber, accentLine, vehicleDetails, separator, ownerRow, contactBtn);
        card.getChildren().addAll(headerOverlay, content);

        // Shadow and hover effects
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        shadow.setColor(Color.web(BLACK_PEARL, 0.15));
        card.setEffect(shadow);
        card.setStyle("-fx-background-radius: 12; -fx-cursor: hand;");

        card.setOnMouseEntered(e -> {
            card.setScaleX(1.02);
            card.setScaleY(1.02);
            shadow.setRadius(15);
            shadow.setColor(Color.web(ROSE_GOLD, 0.25));
            content.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-background-radius: 0 0 12 12;");
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            shadow.setRadius(10);
            shadow.setColor(Color.web(BLACK_PEARL, 0.15));
            content.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 12 12;");
        });

        return card;
    }

    private void showContactDialog(Vehicle vehicle) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Contact Vehicle Owner");
        dialog.setHeaderText("Contact Owner of " + vehicle.getRegistrationNumber());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        Label infoLabel = new Label("📧 Send an inquiry about this vehicle:");
        infoLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type your message here...\n\nExample: Hello, I am interested in purchasing your " +
                vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel() +
                ". Could you please provide more details and your asking price?");
        messageArea.setPrefHeight(150);
        messageArea.setWrapText(true);

        Label ownerInfo = new Label("👤 Owner: " + (vehicle.getOwnerName() == null ? "Unknown" : vehicle.getOwnerName()));
        ownerInfo.setFont(Font.font("Helvetica", 11));
        ownerInfo.setTextFill(Color.web(ROSE_GOLD));

        Label disclaimer = new Label("ℹ️ Note: Your contact information will be shared with the vehicle owner.");
        disclaimer.setFont(Font.font("Helvetica", 10));
        disclaimer.setTextFill(Color.web("#888888"));

        content.getChildren().addAll(infoLabel, messageArea, ownerInfo, disclaimer);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Style the OK button
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Send Message");
        okButton.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-text-fill: " + BLACK_PEARL + "; -fx-font-weight: bold;");

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String message = messageArea.getText();
                if (message != null && !message.trim().isEmpty()) {
                    AlertUtil.info("Message Sent",
                            "Your inquiry about " + vehicle.getRegistrationNumber() + " has been sent to the owner.\n\n" +
                                    "The owner will contact you shortly at your registered email address.");
                } else {
                    AlertUtil.warn("Empty Message", "Please type a message before sending.");
                }
            }
        });
    }
}