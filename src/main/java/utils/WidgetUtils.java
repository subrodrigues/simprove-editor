/*
 * Created by Filipe André Rodrigues on 13-04-2019 23:20
 */

package utils;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WidgetUtils {

    public static void installTooltipOnNode(Node obj, String name){
        Tooltip tt = new Tooltip();
        tt.setText(name);
        tt.getStyleClass().add("tooltip-style");

        Tooltip.install(obj, tt);
    }

    public static void warningMessageAlert(Stage stage, String header, String content, String actionButtonText){
        JFXAlert alert = new JFXAlert(stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(header));
        layout.setBody(new Label(content));

        JFXButton okButton = new JFXButton(actionButtonText);

        okButton.getStyleClass().add("alert-accept");
        okButton.setOnAction(event -> alert.hideWithAnimation());

        layout.setActions(okButton);

        alert.setContent(layout);
        alert.show();
    }

}
