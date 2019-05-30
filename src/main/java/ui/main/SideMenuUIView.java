/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.main;

import com.jfoenix.controls.JFXListView;
import events.ui.SideMenuEvent;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
                    if(newVal == null)return;

                    switch (newVal.getId()) {
                        case ("new_scenario"):
                            EventBus.getDefault().post(new SideMenuEvent(ConstantUtils.SideMenuOption.NEW_SCENARIO));
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
                    }
                    scheduleClearSelection(1000);
                });
            }).start();
        });

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
