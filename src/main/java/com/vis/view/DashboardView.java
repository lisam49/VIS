package com.vis.view;

import com.vis.model.AppUser;
import com.vis.util.AlertUtil;
import com.vis.util.SessionManager;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardView {

    // Color scheme
    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private BorderPane root;
    private StackPane content;
    private Label statusLabel;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;

    public void show(Stage stage, AppUser user) {
        root = new BorderPane();
        root.setTop(buildStyledMenuBar(stage, user));
        root.setLeft(buildSidebar(user));

        content = new StackPane();
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + ROSE_TINT + ";");
        root.setCenter(content);
        root.setBottom(buildStatusBar(user));

        showHome(user);

        Scene scene = new Scene(root, 1400, 900);
        stage.setTitle("VIS — Dashboard (" + user.getRole() + ")");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.show();
    }

    private HBox buildStyledMenuBar(Stage stage, AppUser user) {
        HBox menuContainer = new HBox();
        menuContainer.setAlignment(Pos.CENTER_LEFT);
        menuContainer.setPadding(new Insets(10, 24, 10, 24));
        menuContainer.setStyle(
                "-fx-background-color: " + BLACK_PEARL + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);"
        );

        Label menuLogo = new Label("🚗");
        menuLogo.setFont(Font.font(20));
        menuLogo.setTextFill(Color.web(ROSE_GOLD));
        menuLogo.setPadding(new Insets(0, 15, 0, 0));

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        Menu fileMenu = new Menu("  File  ");
        fileMenu.setStyle("-fx-text-fill: " + PEARL_WHITE + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        MenuItem refresh = new MenuItem("⟳ Refresh");
        styleMenuItem(refresh);
        refresh.setOnAction(e -> setStatus("E nchafalitsoe"));

        MenuItem logout = new MenuItem("🚪 Sign Out");
        styleMenuItem(logout);
        logout.setOnAction(e -> {
            SessionManager.clear();
            new LoginView().show(stage);
        });

        MenuItem exit = new MenuItem("✕ Exit");
        styleMenuItem(exit);
        exit.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(refresh, new SeparatorMenuItem(), logout, exit);

        Menu viewMenu = new Menu("  View  ");
        viewMenu.setStyle("-fx-text-fill: " + PEARL_WHITE + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        MenuItem fullscreen = new MenuItem("⛶ Toggle Full Screen");
        styleMenuItem(fullscreen);
        fullscreen.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));

        viewMenu.getItems().add(fullscreen);

        Menu helpMenu = new Menu("  Help  ");
        helpMenu.setStyle("-fx-text-fill: " + PEARL_WHITE + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        MenuItem about = new MenuItem("ℹ About");
        styleMenuItem(about);
        about.setOnAction(e -> AlertUtil.info("About",
                "Vehicle Identification System v2.0\n\n" +
                        "Black Pearl Edition\n" +
                        "JavaFX • PostgreSQL • MVC architecture\n\n" +
                        "© 2025 VIS - All Rights Reserved"));

        helpMenu.getItems().add(about);

        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // SEARCH BAR ADDED HERE - TOP RIGHT CORNER
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        TextField searchField = new TextField();
        searchField.setPromptText("Batla koloi...");
        searchField.setPrefWidth(220);
        searchField.setStyle(
                "-fx-background-color: " + DARK_SLATE + ";" +
                        "-fx-text-fill: " + PEARL_WHITE + ";" +
                        "-fx-prompt-text-fill: #888888;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-padding: 6 15 6 15;"
        );

        Button searchBtn = new Button("🔍");
        searchBtn.setStyle(
                "-fx-background-color: " + ROSE_GOLD + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 12;"
        );
        searchBtn.setOnAction(e -> {
            if (!searchField.getText().trim().isEmpty()) {
                setStatus("Ho batloa: " + searchField.getText());
                AlertUtil.info("Phatlalatso", "Ho batloa '" + searchField.getText() + "' - ts'ebetso e tla tsoela pele");
            }
        });

        searchBox.getChildren().addAll(searchField, searchBtn);

        Region decorativeLine = new Region();
        decorativeLine.setPrefWidth(60);
        decorativeLine.setPrefHeight(2);
        decorativeLine.setStyle("-fx-background-color: " + ROSE_GOLD + "; -fx-background-radius: 1;");

        Label versionBadge = new Label("v2.0");
        versionBadge.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
        versionBadge.setTextFill(Color.web(ROSE_GOLD));
        versionBadge.setStyle(
                "-fx-background-color: rgba(183,110,121,0.2);" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 4 12 4 12;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        menuContainer.getChildren().addAll(menuLogo, menuBar, spacer, searchBox, decorativeLine, versionBadge);

        return menuContainer;
    }

    private void styleMenuItem(MenuItem item) {
        item.setStyle(
                "-fx-text-fill: " + PEARL_WHITE + ";" +
                        "-fx-background-color: transparent;" +
                        "-fx-padding: 8 15 8 15;"
        );

        item.setStyle(
                "-fx-text-fill: " + PEARL_WHITE + ";" +
                        "-fx-padding: 8 15 8 15;"
        );

        item.setOnAction(e -> {
            item.setStyle(
                    "-fx-text-fill: " + BLACK_PEARL + ";" +
                            "-fx-background-color: " + ROSE_GOLD + ";"
            );
        });
    }

    private VBox buildSidebar(AppUser user) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(280);
        sidebar.setStyle(
                "-fx-background-color: " + BLACK_PEARL + ";" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 3, 0);"
        );

        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(25, 20, 20, 20));
        logoBox.setStyle("-fx-border-color: " + ROSE_GOLD + "; -fx-border-width: 0 0 1 0;");

        StackPane logoCircle = new StackPane();
        logoCircle.setPrefSize(80, 80);
        logoCircle.setStyle(
                "-fx-background-color: " + ROSE_GOLD + ";" +
                        "-fx-background-radius: 40;" +
                        "-fx-effect: dropshadow(gaussian, rgba(183,110,121,0.5), 15, 0, 0, 0);"
        );

        Label carIcon = new Label("🚗");
        carIcon.setFont(Font.font(40));
        carIcon.setTextFill(Color.web(BLACK_PEARL));
        logoCircle.getChildren().add(carIcon);

        Label brand = new Label("VIS");
        brand.setFont(Font.font("Helvetica", FontWeight.BOLD, 28));
        brand.setTextFill(Color.web(ROSE_GOLD));

        Label brandSub = new Label("Sisteme ea Boitsebiso ba Likoloi");
        brandSub.setFont(Font.font("Helvetica", FontWeight.LIGHT, 10));
        brandSub.setTextFill(Color.web(PEARL_WHITE));
        brandSub.setWrapText(true);
        brandSub.setAlignment(Pos.CENTER);

        logoBox.getChildren().addAll(logoCircle, brand, brandSub);

        VBox userBox = new VBox(8);
        userBox.setAlignment(Pos.CENTER);
        userBox.setPadding(new Insets(20, 20, 25, 20));
        userBox.setStyle("-fx-border-color: " + ROSE_GOLD + "; -fx-border-width: 0 0 1 0;");

        StackPane avatarCircle = new StackPane();
        avatarCircle.setPrefSize(60, 60);
        avatarCircle.setStyle(
                "-fx-background-color: " + ROSE_GOLD + ";" +
                        "-fx-background-radius: 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(183,110,121,0.3), 10, 0, 0, 2);"
        );

        Label userIcon = new Label("👤");
        userIcon.setFont(Font.font(28));
        userIcon.setTextFill(Color.web(BLACK_PEARL));
        avatarCircle.getChildren().add(userIcon);

        Label who = new Label(user.getFullName());
        who.setTextFill(Color.web(SOFT_PINK));
        who.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        who.setWrapText(true);
        who.setAlignment(Pos.CENTER);

        Label roleLabel = new Label(user.getRole().name());
        roleLabel.setTextFill(Color.web(ROSE_GOLD));
        roleLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 11));

        HBox roleBadge = new HBox(roleLabel);
        roleBadge.setAlignment(Pos.CENTER);
        roleBadge.setPadding(new Insets(5, 12, 5, 12));
        roleBadge.setStyle(
                "-fx-background-color: rgba(183,110,121,0.2);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-width: 1;"
        );

        userBox.getChildren().addAll(avatarCircle, who, roleBadge);

        VBox nav = new VBox(5);
        nav.setPadding(new Insets(25, 16, 20, 16));

        Label navTitle = new Label("MENU");
        navTitle.setFont(Font.font("Helvetica", FontWeight.BOLD, 11));
        navTitle.setTextFill(Color.web(ROSE_GOLD));
        navTitle.setPadding(new Insets(0, 0, 10, 10));

        nav.getChildren().add(navTitle);
        nav.getChildren().add(createNavButton("🏠", "Dashboard", () -> showHome(user)));

        switch (user.getRole()) {
            case ADMIN:
                nav.getChildren().addAll(
                        createNavButton("👑", "Tsamaiso ea Basebelisi", () -> swap(new UserManagementView().build())),
                        createNavButton("🚗", "Rejistara ea Likoloi", () -> swap(new VehicleView().build())),
                        createNavButton("👥", "Tsamaiso ea Bareki", () -> swap(new CustomerView().build())),
                        createNavButton("🔍", "Batla Likoloi", () -> swap(new BrowseVehiclesView().build())),
                        createNavButton("📊", "Lirekoto tsa Sisteme", () -> swap(new SystemRecordsView().build()))
                );
                break;

            case WORKSHOP:
                nav.getChildren().addAll(
                        createNavButton("🚗", "Rejistara ea Likoloi", () -> swap(new VehicleView().build())),
                        createNavButton("🔧", "Litšebeletso tsa Workshop", () -> swap(new WorkshopView().build())),
                        createNavButton("🔍", "Batla Likoloi", () -> swap(new BrowseVehiclesView().build())),
                        createNavButton("📊", "Lirekoto tsa Sisteme", () -> swap(new SystemRecordsView().build()))
                );
                break;

            case CUSTOMER:
                nav.getChildren().addAll(
                        createNavButton("🚗", "Likoloi tsa Ka", () -> swap(new CustomerVehicleView().build())),
                        createNavButton("🛒", "Reka Likoloi", () -> swap(new CustomerBrowseVehicleView().build())),
                        createNavButton("📊", "Lirekoto tsa Sisteme", () -> swap(new SystemRecordsView().build()))
                );
                break;

            case POLICE:
                nav.getChildren().addAll(
                        createNavButton("👮", "Litlaleho tsa Sepolesa", () -> swap(new PoliceView().build())),
                        createNavButton("🔍", "Batla Likoloi", () -> swap(new BrowseVehiclesView().build())),
                        createNavButton("📊", "Lirekoto tsa Sisteme", () -> swap(new SystemRecordsView().build()))
                );
                break;

            case INSURANCE:
                nav.getChildren().addAll(
                        createNavButton("📄", "Rejistara ea Inshorense", () -> swap(new InsuranceView().build())),
                        createNavButton("🔍", "Batla Likoloi", () -> swap(new BrowseVehiclesView().build())),
                        createNavButton("📊", "Lirekoto tsa Sisteme", () -> swap(new SystemRecordsView().build()))
                );
                break;

            default:
                nav.getChildren().add(
                        createNavButton("🔍", "Batla Likoloi", () -> swap(new BrowseVehiclesView().build()))
                );
                break;
        }

        sidebar.getChildren().addAll(logoBox, userBox, nav);
        return sidebar;
    }

    private Button createNavButton(String icon, String text, Runnable action) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 16, 12, 16));
        btn.setFont(Font.font("Helvetica", 13));
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + PEARL_WHITE + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + ROSE_GOLD + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + PEARL_WHITE + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));

        btn.setOnAction(e -> {
            setStatus("Ho laela " + text + "...");
            action.run();
            setStatus("E lokile");

            if (progressIndicator != null) {
                progressIndicator.setVisible(true);
                new Thread(() -> {
                    try { Thread.sleep(500); } catch (InterruptedException ex) {}
                    Platform.runLater(() -> {
                        if (progressIndicator != null) progressIndicator.setVisible(false);
                    });
                }).start();
            }
        });
        return btn;
    }

    private HBox buildStatusBar(AppUser user) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 24, 12, 24));
        bar.setStyle(
                "-fx-background-color: " + PEARL_WHITE + ";" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-width: 1 0 0 0;"
        );

        statusLabel = new Label("✓ E lokile");
        statusLabel.setTextFill(Color.web(DARK_SLATE));
        statusLabel.setFont(Font.font("Helvetica", 11));

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(150);
        progressBar.setStyle("-fx-accent: " + ROSE_GOLD + ";");

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(16, 16);
        progressIndicator.setVisible(false);
        progressIndicator.setStyle("-fx-progress-color: " + ROSE_GOLD + ";");

        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(Font.font("Helvetica", 10));
        dateLabel.setTextFill(Color.web("#888888"));

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label session = new Label("👤 " + user.getUserId() + "  •  " + user.getRole());
        session.setTextFill(Color.web(DARK_SLATE));
        session.setFont(Font.font("Helvetica", 11));

        bar.getChildren().addAll(statusLabel, progressBar, progressIndicator, spacer1, dateLabel, spacer2, session);
        return bar;
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            if (text.equals("E lokile")) {
                statusLabel.setText("✓ E lokile");
            } else {
                statusLabel.setText("⏳ " + text);
            }
        }
    }

    private void swap(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        content.getChildren().setAll(node);
    }

    private void showHome(AppUser user) {
        VBox dashboard = new VBox(25);
        dashboard.setPadding(new Insets(10));

        VBox headerBox = new VBox(15);
        headerBox.setPadding(new Insets(30, 30, 30, 30));
        headerBox.setStyle(
                "-fx-background-color: linear-gradient(to right, " + BLACK_PEARL + ", " + DARK_SLATE + ");" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);"
        );

        Label welcome = new Label("Rea u amohela hape, " + user.getFullName() + "!");
        welcome.setFont(Font.font("Helvetica", FontWeight.BOLD, 28));
        welcome.setTextFill(Color.web(PEARL_WHITE));

        Label roleMessage = new Label(getRoleMessage(user.getRole()));
        roleMessage.setFont(Font.font("Helvetica", 14));
        roleMessage.setTextFill(Color.web(ROSE_GOLD));

        headerBox.getChildren().addAll(welcome, roleMessage);

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);

        String[] stats = getRoleStats(user.getRole());

        statsRow.getChildren().addAll(
                createStatCard("📊", stats[0], stats[1], ROSE_GOLD),
                createStatCard("✅", stats[2], stats[3], SOFT_PINK),
                createStatCard("⏳", stats[4], stats[5], ROSE_GOLD),
                createStatCard("🎯", stats[6], stats[7], SOFT_PINK)
        );

        Label quickActionsTitle = new Label("Liketsahalo tse Potlakileng");
        quickActionsTitle.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        quickActionsTitle.setTextFill(Color.web(DARK_SLATE));

        HBox quickActions = new HBox(20);
        quickActions.setAlignment(Pos.CENTER);
        quickActions.getChildren().addAll(getQuickActions(user.getRole()));

        VBox tipsBox = new VBox(10);
        tipsBox.setPadding(new Insets(20));
        tipsBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );

        Label tipsTitle = new Label("💡 Likeletso & Tlhahisoleseling");
        tipsTitle.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        tipsTitle.setTextFill(Color.web(DARK_SLATE));

        Label tip1 = new Label("• " + getTip(user.getRole()));
        tip1.setFont(Font.font("Helvetica", 12));
        tip1.setTextFill(Color.web("#555555"));

        tipsBox.getChildren().addAll(tipsTitle, tip1);

        dashboard.getChildren().addAll(headerBox, statsRow, quickActionsTitle, quickActions, tipsBox);

        ScrollPane scroll = new ScrollPane(dashboard);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        swap(scroll);
    }

    private String getRoleMessage(AppUser.Role role) {
        switch (role) {
            case ADMIN: return "U na le taolo e felletseng ea sisteme. Laola basebelisi, likoloi, le bareki.";
            case WORKSHOP: return "Laola rejistaro ea likoloi le lirekoto tsa litšebeletso ka bokhabane.";
            case CUSTOMER: return "Sheba likoloi tsa hao tse ngolisitsoeng 'me u phenye lethathamo la rona.";
            case POLICE: return "Fumana lirekoto tsa likoloi, kenya litlaleho, 'me u laole litlolo.";
            case INSURANCE: return "Laola melaoana ea inshorense 'me u shebe likoloi tse inshorenste.";
            default: return "Khetha karolo ho tsoa ka lehlakoreng ho qala.";
        }
    }

    private String[] getRoleStats(AppUser.Role role) {
        switch (role) {
            case ADMIN: return new String[]{"Basebelisi Bohle", "5", "Likoloi Bohle", "25", "Bareki Bohle", "16", "Bophelo ba Sisteme", "Bo botle"};
            case WORKSHOP: return new String[]{"Ka Workshop", "8", "Litšebeletso tsa Kajeno", "3", "Tse Phethiloeng", "156", "Tse Emetseng", "12"};
            case CUSTOMER: return new String[]{"Likoloi tsa Ka", "2", "Lirekoto tsa Litšebeletso", "4", "Melaoana e Sebetsang", "1", "Lipotso tse Butsoeng", "0"};
            case POLICE: return new String[]{"Litlaleho tse Kentsoeng", "47", "Litlolo", "156", "Litekete tse sa Lefuoeng", "23", "Tse Rarollotsoeng", "133"};
            case INSURANCE: return new String[]{"Melaoana e Sebetsang", "342", "E Felloang ke Nako Haufinyane", "18", "Ba Inshorenste", "1,122", "Likopo", "7"};
            default: return new String[]{"Kakaretso", "0", "E Sebetsang", "0", "E Emetse", "0", "E Phethiloe", "0"};
        }
    }

    private VBox createStatCard(String emoji, String title, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(180);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 16;" +
                        "-fx-cursor: hand;"
        );

        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(28));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Helvetica", 11));
        titleLabel.setTextFill(Color.web(DARK_SLATE));

        card.getChildren().addAll(emojiLabel, valueLabel, titleLabel);

        DropShadow ds = new DropShadow();
        ds.setRadius(10);
        ds.setOffsetY(3);
        ds.setColor(Color.web(BLACK_PEARL, 0.1));
        card.setEffect(ds);

        card.setOnMouseEntered(e -> {
            card.setScaleX(1.02);
            card.setScaleY(1.02);
            ds.setRadius(15);
            ds.setColor(Color.web(color, 0.2));
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            ds.setRadius(10);
            ds.setColor(Color.web(BLACK_PEARL, 0.1));
        });

        return card;
    }

    private Node[] getQuickActions(AppUser.Role role) {
        switch (role) {
            case ADMIN:
                return new Node[]{
                        createQuickActionButton("👑", "Laola Basebelisi", () -> swap(new UserManagementView().build())),
                        createQuickActionButton("🚗", "Laola Likoloi", () -> swap(new VehicleView().build())),
                        createQuickActionButton("👥", "Laola Bareki", () -> swap(new CustomerView().build())),
                        createQuickActionButton("📊", "Sheba Litlaleho", () -> swap(new SystemRecordsView().build()))
                };
            case WORKSHOP:
                return new Node[]{
                        createQuickActionButton("🚗", "Koloi e Ncha", () -> swap(new VehicleView().build())),
                        createQuickActionButton("🔧", "Kenya Tšebeletso", () -> swap(new WorkshopView().build())),
                        createQuickActionButton("🔍", "Batla", () -> swap(new BrowseVehiclesView().build())),
                        createQuickActionButton("📊", "Lirekoto", () -> swap(new SystemRecordsView().build()))
                };
            case CUSTOMER:
                return new Node[]{
                        createQuickActionButton("🚗", "Likoloi tsa Ka", () -> swap(new CustomerVehicleView().build())),
                        createQuickActionButton("🛒", "Reka Koloi", () -> swap(new CustomerBrowseVehicleView().build())),
                        createQuickActionButton("🔍", "Fetla", () -> swap(new BrowseVehiclesView().build())),
                        createQuickActionButton("📊", "Lirekoto", () -> swap(new SystemRecordsView().build()))
                };
            case POLICE:
                return new Node[]{
                        createQuickActionButton("📋", "Kenya Tlaleho", () -> swap(new PoliceView().build())),
                        createQuickActionButton("⚠️", "Fana ka Tekete", () -> swap(new PoliceView().build())),
                        createQuickActionButton("🔍", "Batla", () -> swap(new BrowseVehiclesView().build())),
                        createQuickActionButton("📊", "Lirekoto", () -> swap(new SystemRecordsView().build()))
                };
            case INSURANCE:
                return new Node[]{
                        createQuickActionButton("📄", "Ngolisa", () -> swap(new InsuranceView().build())),
                        createQuickActionButton("✅", "Kenya Tšebetsong", () -> swap(new InsuranceView().build())),
                        createQuickActionButton("🔍", "Fetla", () -> swap(new BrowseVehiclesView().build())),
                        createQuickActionButton("📊", "Lirekoto", () -> swap(new SystemRecordsView().build()))
                };
            default:
                return new Node[]{
                        createQuickActionButton("🔍", "Fetla", () -> swap(new BrowseVehiclesView().build())),
                        createQuickActionButton("📊", "Lirekoto", () -> swap(new SystemRecordsView().build()))
                };
        }
    }

    private VBox createQuickActionButton(String emoji, String text, Runnable action) {
        VBox btn = new VBox(8);
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(new Insets(15, 25, 15, 25));
        btn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;"
        );

        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(24));

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Helvetica", 11));
        textLabel.setTextFill(Color.web(DARK_SLATE));

        btn.getChildren().addAll(emojiLabel, textLabel);

        DropShadow ds = new DropShadow();
        ds.setRadius(8);
        ds.setOffsetY(2);
        ds.setColor(Color.web(BLACK_PEARL, 0.1));
        btn.setEffect(ds);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: " + ROSE_TINT + ";" +
                            "-fx-background-radius: 12;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: " + SOFT_PINK + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 12;"
            );
            ds.setRadius(12);
            ds.setColor(Color.web(ROSE_GOLD, 0.2));
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 12;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: " + ROSE_GOLD + ";" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 12;"
            );
            ds.setRadius(8);
            ds.setColor(Color.web(BLACK_PEARL, 0.1));
        });

        btn.setOnMouseClicked(e -> action.run());

        return btn;
    }

    private String getTip(AppUser.Role role) {
        switch (role) {
            case ADMIN: return "U le Mo admin, u ka laola basebelisi, likoloi, le bareki. U ka sheba lirekoto tsohle.";
            case WORKSHOP: return "Ngolisa likoloi tse ncha 'me u latele nalane ea litšebeletso. Tšebeletso e 'ngoe le e 'ngoe e lokela ho kenyelletsa litšenyehelo le tlhaloso.";
            case CUSTOMER: return "U ka batla likoloi tse rekisoang 'me u ikopanye le beng ba tsona ka kotloloho ka module ea Reka Likoloi.";
            case POLICE: return "Kenya litlaleho tsa sepolesa 'me u fane ka litlolo. Sebelisa mokhoa oa ho batla ho fumana tlhahisoleseling ea koloi kapele.";
            case INSURANCE: return "Ngolisa likoloi bakeng sa melaoana ea inshorense. Kenya melaoana tšebetsong 'me u e nchafatse selemo le selemo.";
            default: return "Sebelisa bara e ka lehlakoreng ho tsamaea likarolong tse fapaneng tsa sisteme.";
        }
    }
}