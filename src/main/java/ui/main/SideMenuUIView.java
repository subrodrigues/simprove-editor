/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.main;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import events.ui.SideMenuEvent;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import utils.ConstantUtils;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/SideMenu.fxml", title = "Side Menu Title")
public class SideMenuUIView {

    @FXML
    @ActionTrigger("new_scenario")
    private Label newScenario;
    @FXML
    @ActionTrigger("load_scenario")
    private Label loadScenario;

    @FXML
    private JFXListView<Label> sideList;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        sideList.propagateMouseEventsToParent();
        sideList.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            new Thread(() -> {
                Platform.runLater(() -> {
                    if (newVal == null) return;

                    switch (newVal.getId()) {
                        case ("new_scenario"):
                            confirmationNewScenarioEvent();
                            break;
                        case ("save_scenario"):
                            EventBus.getDefault().post(new SideMenuEvent(ConstantUtils.SideMenuOption.SAVE_SCENARIO));
                            break;
                        case ("load_scenario"):
                            EventBus.getDefault().post(new SideMenuEvent(ConstantUtils.SideMenuOption.LOAD_SCENARIO));
                            break;
                        case ("export_scenario"):
                            EventBus.getDefault().post(new SideMenuEvent(ConstantUtils.SideMenuOption.EXPORT_SCENARIO));
                            break;
                        case ("exit_app"):
                            Platform.exit();
                            break;
                    }
                    scheduleClearSelection(1000);
                });
            }).start();
        });

    }

    /**
     * Method that implements the new Scenario confirmation window.
     *
     * @return the EventHandler with correspondent behavior
     */
    private void confirmationNewScenarioEvent() {
        JFXAlert alert = new JFXAlert((Stage) sideList.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("New Scenario"));
        layout.setBody(new Label("Are you sure you want to discard the current Scenario? \n"));

        JFXButton noButton = new JFXButton("No");

        JFXButton yesButton = new JFXButton("Yes");

        noButton.getStyleClass().add("alert-cancel");
        noButton.setOnAction(event -> alert.hideWithAnimation());

        yesButton.getStyleClass().add("alert-accept");
        yesButton.setOnAction(launchNewScenarioEventRequest(alert));

        layout.setActions(noButton, yesButton);

        alert.setContent(layout);
        alert.show();
    }


    /**
     * Method that implements the positive confirmation to launch new scenario event.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> launchNewScenarioEventRequest(JFXAlert alert) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                EventBus.getDefault().post(new SideMenuEvent(ConstantUtils.SideMenuOption.NEW_SCENARIO));
                alert.close();
            }
        };
    }

    /**
     * Method that clears the list selection
     *
     * @param timeMs
     */
    private void scheduleClearSelection(int timeMs) {
        new java.util.Timer().
                schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                sideList.getSelectionModel().clearSelection();
                            }
                        },
                        timeMs
                );
    }
}
