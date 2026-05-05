package com.vis.view;

import com.vis.controller.VehicleController;
import com.vis.model.Vehicle;
import com.vis.util.AlertUtil;
import com.vis.util.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerVehicleView {

    private static final String DARK_SLATE = "#1F1F2F";
    private static final String ROSE_GOLD = "#B76E79";
    private static final String ROSE_TINT = "#FFF0F3";

    private final VehicleController controller = new VehicleController();

    public Node build() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + ROSE_TINT + ";");

        // Title
        Label title = new Label("🚗 My Registered Vehicles");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_SLATE));

        // Subtitle
        Label subtitle = new Label("View only your registered vehicles. Contact your workshop for modifications.");
        subtitle.setFont(Font.font("Helvetica", 12));
        subtitle.setTextFill(Color.web(ROSE_GOLD));

        // Table
        TableView<Vehicle> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Vehicle, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().vehicleIdProperty());
        idCol.setPrefWidth(50);

        TableColumn<Vehicle, String> regCol = new TableColumn<>("Registration Number");
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

        table.getColumns().addAll(idCol, regCol, makeCol, modelCol, yearCol);

        // Load ONLY current customer's vehicles
        try {
            String currentUserName = SessionManager.getCurrentUser().getFullName();
            List<Vehicle> allVehicles = controller.getAll();

            // Filter: only show vehicles owned by this customer
            List<Vehicle> myVehicles = allVehicles.stream()
                    .filter(v -> currentUserName.equalsIgnoreCase(v.getOwnerName()))
                    .collect(Collectors.toList());

            table.getItems().setAll(myVehicles);

            if (myVehicles.isEmpty()) {
                Label emptyMsg = new Label("📭 You don't have any registered vehicles yet.\n\nTo register a vehicle, please visit your nearest workshop.");
                emptyMsg.setFont(Font.font("Helvetica", 14));
                emptyMsg.setTextFill(Color.web(DARK_SLATE));
                emptyMsg.setAlignment(Pos.CENTER);
                table.setPlaceholder(emptyMsg);
            }
        } catch (SQLException e) {
            AlertUtil.error("Database error", "Could not load your vehicles: " + e.getMessage());
            table.setPlaceholder(new Label("Error loading vehicles. Please try again."));
        }

        // Info note
        Label infoNote = new Label("ℹ️ Note: To add, edit, or delete vehicles, please contact your workshop administrator.");
        infoNote.setFont(Font.font("Helvetica", 11));
        infoNote.setTextFill(Color.web("#888888"));
        infoNote.setPadding(new Insets(15, 0, 0, 0));

        VBox.setVgrow(table, Priority.ALWAYS);
        root.getChildren().addAll(title, subtitle, table, infoNote);

        return root;
    }
}