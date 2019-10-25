package app.ultilies;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class BypassMessage {
    private Label label;

    public BypassMessage(Label label) {
        this.setLabel(label);
    }

    public void setBypassMessage(String message, Boolean isSuccess) throws Exception {
        if (this.label == null) {
            throw new Exception("Label is Null!");
        }

        this.label.setText(message);
        if (isSuccess) {
            this.label.setStyle("-fx-text-fill: derive(green, -15%)");
        } else {
            this.label.setStyle("-fx-text-fill: derive(red, 15%)");
        }
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
