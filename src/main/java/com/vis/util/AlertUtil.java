package com.vis.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class AlertUtil {

    private AlertUtil() {}

    public static void info(String title, String message) {
        show(AlertType.INFORMATION, title, message);
    }

    public static void warn(String title, String message) {
        show(AlertType.WARNING, title, message);
    }

    public static void error(String title, String message) {
        show(AlertType.ERROR, title, message);
    }

    private static void show(AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
