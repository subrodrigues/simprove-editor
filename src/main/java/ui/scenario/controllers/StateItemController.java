/*
 * Created by Filipe André Rodrigues on 20-02-2019 21:20
 */

package ui.scenario.controllers;

import com.jfoenix.controls.JFXMasonryPane;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import ui.scenario.ScenarioPresenter;

import javax.annotation.PostConstruct;

//@ViewController(value = "/fxml/ui/StateItem.fxml", title="cenas")
public class StateItemController {
    // UI Bind variables
    @FXML
    private Text stateName;

    public Text getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName.setText(stateName);
    }
}
