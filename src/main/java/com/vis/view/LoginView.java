package com.vis.view;

import com.vis.controller.LoginController;
import com.vis.model.AppUser;
import com.vis.util.AlertUtil;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Optional;

public class LoginView {

    private static final String BLACK_PEARL = "#0A0A0A";
    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String SOFT_PINK = "#FF9EB5";
    private static final String PEARL_WHITE = "#EAEAEA";
    private static final String ROSE_TINT = "#FFF0F3";

    private final LoginController controller = new LoginController();

    public void show(Stage stage) {
        // Create a Pane for the slanted layout
        Pane root = new Pane();
        root.setPrefSize(1000, 650);


        Polygon slantedPanel = new Polygon();
        slantedPanel.getPoints().addAll(
                0.0, 0.0,      // top-left
                380.0, 0.0,    // top-right of left panel
                280.0, 650.0,  // bottom-right of left panel (slanted)
                0.0, 650.0     // bottom-left
        );
        slantedPanel.setFill(Color.web(DARK_SLATE));
        slantedPanel.setStroke(Color.web(ROSE_GOLD));
        slantedPanel.setStrokeWidth(1.5);


        Polygon rightPanel = new Polygon();
        rightPanel.getPoints().addAll(
                380.0, 0.0,    // top-left
                1000.0, 0.0,   // top-right
                1000.0, 650.0, // bottom-right
                280.0, 650.0   // bottom-left (slanted edge)
        );
        rightPanel.setFill(Color.web(BLACK_PEARL));


        Polygon accentLine = new Polygon();
        accentLine.getPoints().addAll(
                382.0, 0.0,
                385.0, 0.0,
                285.0, 650.0,
                282.0, 650.0
        );
        accentLine.setFill(Color.web(ROSE_GOLD));

        VBox welcomeBox = createWelcomePanel();
        welcomeBox.setLayoutX(30);
        welcomeBox.setLayoutY(80);
        welcomeBox.setPrefWidth(320);


        VBox loginBox = createLoginForm(stage);
        loginBox.setLayoutX(420);
        loginBox.setLayoutY(50);
        loginBox.setPrefWidth(500);


        Button closeBtn = createCloseButton(stage);
        closeBtn.setLayoutX(950);
        closeBtn.setLayoutY(20);


        root.getChildren().addAll(rightPanel, slantedPanel, accentLine, welcomeBox, loginBox, closeBtn);

        Scene scene = new Scene(root, 1000, 650);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("VIS - Vehicle Identification System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        makeWindowDraggable(stage, root);
    }

    private VBox createWelcomePanel() {
        // Small brand tag
        Label brand = new Label("VIS");
        brand.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
        brand.setTextFill(Color.web(ROSE_GOLD));

        // Large "WELCOME" text
        Text welcomeText = new Text("WELCOME");
        welcomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 58));
        welcomeText.setFill(Color.web(ROSE_GOLD));
        welcomeText.setEffect(new Glow(0.3));

        // Decorative line
        Region line = new Region();
        line.setPrefHeight(2);
        line.setPrefWidth(60);
        line.setStyle("-fx-background-color: " + ROSE_GOLD + ";");

        // Decorative text (like lorem ipsum)
        Text decorative1 = new Text("Trusted since 2024");
        decorative1.setFont(Font.font("Helvetica", FontWeight.LIGHT, 16));
        decorative1.setFill(Color.web(PEARL_WHITE));

        Text decorative2 = new Text("amet consectetur");
        decorative2.setFont(Font.font("Helvetica", FontWeight.LIGHT, 16));
        decorative2.setFill(Color.web(PEARL_WHITE));

        Text decorative3 = new Text("adipisicing.");
        decorative3.setFont(Font.font("Helvetica", FontWeight.LIGHT, 16));
        decorative3.setFill(Color.web(SOFT_PINK));

        VBox textBlock = new VBox(5, decorative1, decorative2, decorative3);

        VBox welcomeBox = new VBox(25, brand, welcomeText, line, textBlock);
        welcomeBox.setAlignment(Pos.TOP_LEFT);

        return welcomeBox;
    }

    private VBox createLoginForm(Stage stage) {
        // "Login" title with back arrow inspired by your reference
        HBox titleBox = new HBox(10);
        Label backArrow = new Label("←");
        backArrow.setFont(Font.font("Helvetica", FontWeight.BOLD, 28));
        backArrow.setTextFill(Color.web(ROSE_GOLD));
        backArrow.setStyle("-fx-cursor: hand;");
        backArrow.setOnMouseClicked(e -> {
            // Optional: go back or just for design
        });

        Text loginTitle = new Text("Login");
        loginTitle.setFont(Font.font("Helvetica", FontWeight.BOLD, 36));
        loginTitle.setFill(Color.web(PEARL_WHITE));

        titleBox.getChildren().addAll(backArrow, loginTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Username field
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 11));
        usernameLabel.setTextFill(Color.web(ROSE_GOLD));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(45);
        usernameField.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-prompt-text-fill: #999999;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-bottom-color: " + ROSE_GOLD + ";" +
                        "-fx-border-width: 0 0 2 0;" +
                        "-fx-background-radius: 0;" +
                        "-fx-padding: 0 15;"
        );

        // Password field
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setFont(Font.font("Helvetica", FontWeight.MEDIUM, 11));
        passwordLabel.setTextFill(Color.web(ROSE_GOLD));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(45);
        passwordField.setStyle(
                "-fx-background-color: " + ROSE_TINT + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-prompt-text-fill: #999999;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-bottom-color: " + ROSE_GOLD + ";" +
                        "-fx-border-width: 0 0 2 0;" +
                        "-fx-background-radius: 0;" +
                        "-fx-padding: 0 15;"
        );

        // Show password checkbox
        CheckBox showPassword = new CheckBox("Show password");
        showPassword.setTextFill(Color.web(PEARL_WHITE));
        showPassword.setStyle("-fx-cursor: hand;");
        showPassword.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                TextField visiblePass = new TextField();
                visiblePass.setText(passwordField.getText());
                visiblePass.setStyle(passwordField.getStyle());
                visiblePass.setPrefHeight(45);
                passwordField.setVisible(false);
                VBox parent = (VBox) passwordField.getParent();
                int index = parent.getChildren().indexOf(passwordField);
                parent.getChildren().add(index, visiblePass);
                passwordField.setUserData(visiblePass);
                visiblePass.textProperty().addListener((obs2, oldText, newText) -> {
                    passwordField.setText(newText);
                });
            } else {
                Object visiblePassObj = passwordField.getUserData();
                if (visiblePassObj instanceof TextField) {
                    TextField visiblePass = (TextField) visiblePassObj;
                    VBox parent = (VBox) passwordField.getParent();
                    parent.getChildren().remove(visiblePass);
                    passwordField.setVisible(true);
                }
            }
        });

        // Login button
        Button loginBtn = new Button("LOGIN");
        loginBtn.setPrefSize(180, 48);
        loginBtn.setStyle(
                "-fx-background-color: " + ROSE_GOLD + ";" +
                        "-fx-text-fill: " + BLACK_PEARL + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 30;" +
                        "-fx-cursor: hand;"
        );

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), loginBtn);
        loginBtn.setOnMouseEntered(e -> {
            loginBtn.setStyle(
                    "-fx-background-color: " + SOFT_PINK + ";" +
                            "-fx-text-fill: " + BLACK_PEARL + ";" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 30;" +
                            "-fx-cursor: hand;"
            );
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.playFromStart();
        });

        loginBtn.setOnMouseExited(e -> {
            loginBtn.setStyle(
                    "-fx-background-color: " + ROSE_GOLD + ";" +
                            "-fx-text-fill: " + BLACK_PEARL + ";" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 30;" +
                            "-fx-cursor: hand;"
            );
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.playFromStart();
        });

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(ROSE_GOLD, 0.4));
        shadow.setRadius(18);
        shadow.setOffsetY(4);
        loginBtn.setEffect(shadow);

        // Sign up section
        HBox signupBox = new HBox(5);
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setTextFill(Color.web(PEARL_WHITE));
        Hyperlink signupLink = new Hyperlink("Sign Up");
        signupLink.setTextFill(Color.web(ROSE_GOLD));
        signupLink.setStyle("-fx-cursor: hand;");
        signupLink.setOnAction(e -> AlertUtil.info("Sign Up",
                "Please contact your system administrator to create an account."));
        signupBox.getChildren().addAll(noAccountLabel, signupLink);
        signupBox.setAlignment(Pos.CENTER);

        // Demo accounts (collapsible)
        TitledPane demoPane = createDemoPane();

        VBox formBox = new VBox(20,
                titleBox,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                showPassword,
                loginBtn,
                signupBox,
                demoPane
        );

        formBox.setAlignment(Pos.TOP_LEFT);
        formBox.setPadding(new Insets(40, 0, 0, 0));

        loginBtn.setOnAction(e -> handleLogin(stage, usernameField.getText(), passwordField.getText()));
        passwordField.setOnAction(e -> loginBtn.fire());

        return formBox;
    }

    private TitledPane createDemoPane() {
        TextArea demoCreds = new TextArea(
                "┌─────────────────────────────────────┐\n" +
                        "  admin / admin123                    │\n" +
                        "  customer1 / customer123             │\n" +
                        "  workshop1 / workshop123              │\n" +
                        "  police1 / police123                 │\n" +
                        "  insurance1 / insurance123           │\n" +
                        "─────────────────────────────────────┘"
        );
        demoCreds.setEditable(false);
        demoCreds.setPrefHeight(130);
        demoCreds.setStyle(
                "-fx-background-color: " + DARK_SLATE + ";" +
                        "-fx-text-fill: " + SOFT_PINK + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-family: monospace;" +
                        "-fx-border-color: " + ROSE_GOLD + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        TitledPane pane = new TitledPane("📋 Demo Accounts", demoCreds);
        pane.setStyle(
                "-fx-text-fill: " + ROSE_GOLD + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );
        pane.setAnimated(true);

        return pane;
    }

    private Button createCloseButton(Stage stage) {
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + ROSE_GOLD + ";" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 5 10 5 10;"
        );
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
                "-fx-background-color: #E74C3C;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 5 10 5 10;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + ROSE_GOLD + ";" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 5 10 5 10;"
        ));
        closeBtn.setOnAction(e -> stage.close());
        return closeBtn;
    }

    private void handleLogin(Stage stage, String userId, String password) {
        try {
            Optional<AppUser> user = controller.login(userId, password);
            if (user.isPresent()) {
                new DashboardView().show(stage, user.get());
            } else {
                AlertUtil.error("Login Failed", "Invalid user ID or password.");
            }
        } catch (IllegalArgumentException ex) {
            AlertUtil.warn("Validation Error", ex.getMessage());
        } catch (SQLException ex) {
            AlertUtil.error("Database Connection Error",
                    "Unable to connect to the database.\n\n" + ex.getMessage() +
                            "\n\nPlease check your database configuration.");
        } catch (Exception ex) {
            AlertUtil.error("System Error", "An unexpected error occurred:\n" + ex.getMessage());
        }
    }

    private void makeWindowDraggable(Stage stage, Pane root) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};

        root.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
}