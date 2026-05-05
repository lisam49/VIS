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

/**
 * Browse Vehicles view with Black Pearl & Rose Gold color scheme.
 * Card-based vehicle browsing with pagination.
 */
public class BrowseVehiclesView {

    // Color scheme: Black Pearl & Rose Gold (#16)
    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private static final int ITEMS_PER_PAGE = 6;

    private final VehicleController controller = new VehicleController();
    private List<Vehicle> all;

    public Node build() {
        // Header
        Label title = new Label("Browse Vehicles");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setMaxWidth(100);
        accent.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 2;");

        Label hint = new Label("Browse through all registered vehicles in the system. Use the pagination controls below to navigate.");
        hint.setFont(Font.font("Helvetica", FontWeight.LIGHT, 12));
        hint.setTextFill(Color.web(ROSE_GOLD));
        hint.setWrapText(true);

        try {
            all = controller.getAll();
        } catch (SQLException e) {
            AlertUtil.error("Database error", e.getMessage());
            all = List.of();
        }

        int pageCount = Math.max(1, (int) Math.ceil(all.size() / (double) ITEMS_PER_PAGE));

        Pagination pagination = new Pagination(pageCount, 0);
        pagination.setPageFactory(this::createPage);

        // Style the pagination controls
        pagination.setStyle(
                "-fx-page-information-visible: false;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-padding: 5 0 15 0;"
        );

        // Style pagination buttons
        pagination.lookupAll(".button").forEach(btn -> {
            if (btn instanceof Button) {
                Button b = (Button) btn;
                b.setStyle(
                        "-fx-background-color: " + ROSE_TINT + ";" +
                                "-fx-text-fill: " + DARK_SLATE + ";" +
                                "-fx-background-radius: 6;" +
                                "-fx-border-color: " + ROSE_GOLD + ";" +
                                "-fx-border-radius: 6;" +
                                "-fx-cursor: hand;"
                );
                b.setOnMouseEntered(e -> b.setStyle(
                        "-fx-background-color: " + ROSE_GOLD + ";" +
                                "-fx-text-fill: " + BLACK_PEARL + ";" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 6;" +
                                "-fx-border-color: " + ROSE_GOLD + ";" +
                                "-fx-border-radius: 6;" +
                                "-fx-cursor: hand;"
                ));
                b.setOnMouseExited(e -> b.setStyle(
                        "-fx-background-color: " + ROSE_TINT + ";" +
                                "-fx-text-fill: " + DARK_SLATE + ";" +
                                "-fx-background-radius: 6;" +
                                "-fx-border-color: " + ROSE_GOLD + ";" +
                                "-fx-border-radius: 6;" +
                                "-fx-cursor: hand;"
                ));
            }
        });

        VBox.setVgrow(pagination, Priority.ALWAYS);

        // Stats bar
        HBox statsBar = new HBox(15);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(15, 0, 0, 0));

        Label totalIcon = new Label("🚗");
        totalIcon.setFont(Font.font(16));

        Label total = new Label("Total Vehicles: " + all.size());
        total.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 13));
        total.setTextFill(Color.web(ROSE_GOLD));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label pagesLabel = new Label("Pages: " + pageCount);
        pagesLabel.setFont(Font.font("Helvetica", 11));
        pagesLabel.setTextFill(Color.web(DARK_SLATE));

        statsBar.getChildren().addAll(totalIcon, total, spacer, pagesLabel);

        VBox root = new VBox(12, title, accent, hint, pagination, statsBar);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + PEARL_WHITE + ";");
        return root;
    }

    private Node createPage(int pageIndex) {
        FlowPane cards = new FlowPane();
        cards.setHgap(20);
        cards.setVgap(20);
        cards.setPadding(new Insets(15, 0, 15, 0));

        // Make cards wrap responsively
        cards.setPrefWrapLength(900);

        int start = pageIndex * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, all.size());
        for (int i = start; i < end; i++) {
            cards.getChildren().add(buildCard(all.get(i)));
        }

        ScrollPane scroll = new ScrollPane(cards);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;" +
                        "-fx-border-color: transparent;"
        );
        return scroll;
    }

    private Node buildCard(Vehicle v) {
        // Card container
        VBox card = new VBox(0);
        card.setPrefWidth(280);
        card.setMaxWidth(280);

        // Vehicle image placeholder (colored bar)
        Region imagePlaceholder = new Region();
        imagePlaceholder.setPrefHeight(120);
        imagePlaceholder.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + BLACK_PEARL + ", " + DARK_SLATE + ");" +
                        "-fx-background-radius: 12 12 0 0;"
        );

        // Vehicle icon overlay
        StackPane imageOverlay = new StackPane();
        Label carIcon = new Label("🚗");
        carIcon.setFont(Font.font(48));
        carIcon.setTextFill(Color.web(ROSE_GOLD));
        imageOverlay.getChildren().addAll(imagePlaceholder, carIcon);

        // Content area
        VBox content = new VBox(8);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 12 12;");

        // Registration number (prominent)
        Label reg = new Label(v.getRegistrationNumber());
        reg.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        reg.setTextFill(Color.web(BLACK_PEARL));
        reg.setWrapText(true);

        // Rose gold accent line
        Region accentLine = new Region();
        accentLine.setPrefHeight(2);
        accentLine.setPrefWidth(40);
        accentLine.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 1;");

        // Vehicle description
        Label desc = new Label(v.getYear() + " " + v.getMake() + " " + v.getModel());
        desc.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 13));
        desc.setTextFill(Color.web(DARK_SLATE));
        desc.setWrapText(true);

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        // Owner info with icon
        HBox ownerRow = new HBox(8);
        ownerRow.setAlignment(Pos.CENTER_LEFT);

        Label ownerIcon = new Label("👤");
        ownerIcon.setFont(Font.font(12));

        Label owner = new Label((v.getOwnerName() == null ? "Unassigned" : v.getOwnerName()));
        owner.setFont(Font.font("Helvetica", 11));
        owner.setTextFill(Color.web(ROSE_GOLD));

        ownerRow.getChildren().addAll(ownerIcon, owner);

        // Optional: VIN badge if available
        HBox bottomRow = new HBox(8);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        if (v.getVin() != null && !v.getVin().isEmpty()) {
            Label vinBadge = new Label(v.getVin().substring(0, Math.min(8, v.getVin().length())) + "...");
            vinBadge.setFont(Font.font("Monospaced", 9));
            vinBadge.setTextFill(Color.web(PEARL_WHITE));
            vinBadge.setStyle(
                    "-fx-background-color: " + DARK_SLATE + ";" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 2 6;"
            );
            bottomRow.getChildren().add(vinBadge);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bottomRow.getChildren().add(spacer);

        content.getChildren().addAll(reg, accentLine, desc, separator, ownerRow, bottomRow);
        card.getChildren().addAll(imageOverlay, content);

        // Card shadow and hover effect
        DropShadow ds = new DropShadow();
        ds.setRadius(12);
        ds.setOffsetY(4);
        ds.setColor(Color.web(BLACK_PEARL, 0.15));
        card.setEffect(ds);
        card.setStyle("-fx-background-radius: 12; -fx-cursor: hand;");

        // Hover animation
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.02);
            card.setScaleY(1.02);
            ds.setRadius(18);
            ds.setOffsetY(6);
            ds.setColor(Color.web(ROSE_GOLD, 0.25));
            content.setStyle("-fx-background-color: " + ROSE_TINT + "; -fx-background-radius: 0 0 12 12;");
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            ds.setRadius(12);
            ds.setOffsetY(4);
            ds.setColor(Color.web(BLACK_PEARL, 0.15));
            content.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 12 12;");
        });

        return card;
    }
}