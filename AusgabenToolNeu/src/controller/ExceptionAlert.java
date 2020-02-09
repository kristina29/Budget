package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Fenster für auftretende Exceptions.
 */
final class ExceptionAlert {

    private ExceptionAlert() {
    }

    /**
     * Fenster anzeigen.
     */
    public static void show(final String text) {
        final Alert alert = new Alert(
                Alert.AlertType.WARNING,
                text,
                ButtonType.OK);
        alert.setTitle(Messages_DE.WARNING);
        alert.showAndWait();
    }

}

