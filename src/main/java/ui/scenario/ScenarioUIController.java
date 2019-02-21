/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;
import dao.model.ScenarioModel;
import dao.model.StateModel;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;
import ui.scenario.controllers.StateItemController;

import javax.annotation.PostConstruct;
import java.io.IOException;

@ViewController(value = "/fxml/ui/Scenario.fxml", title = "New Scenario Title")
public class ScenarioUIController {

    private ScenarioPresenter mPresenter;

    // UI Bind variables
    @FXML
    private ScrollPane actionsScrollPane;
    @FXML
    private JFXMasonryPane actionsGridView;

    @FXML
    private ScrollPane statesScrollPane;
    @FXML
    private JFXMasonryPane statesGridView;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        mPresenter = new ScenarioPresenter(this);
        setupUI();
    }

    /**
     * Setups the UI and init needed variables
     */
    private void setupUI() {
        Platform.runLater(() -> actionsScrollPane.requestLayout());
        JFXScrollPane.smoothScrolling(actionsScrollPane);

        Platform.runLater(() -> statesScrollPane.requestLayout());
        JFXScrollPane.smoothScrolling(statesScrollPane);
    }

    /**
     * Helper method that creates a UI State Card and returns the node to be inflate in the list.
     *
     * @param indexDelay
     * @param state
     * @return
     *
     * @throws IOException
     */
    Node getInflatableStateItem(int index, StateModel state) throws IOException {
        StateItemController item = new StateItemController(index);
        item.setStateName(state.getName());

        item.setupAnimatedEditFab(Duration.millis(100 * index + 1000)); // TODO: better calculation

        return item.getStateItemRootPane();
    }

    /**
     * Invoked by the Presenter.
     * Updates the view with a whole new Scenario.
     *
     * @param scenario
     */
    void updateScenarioData(ScenarioModel scenario) {
        // TODO: fill scenario related data

        // Fill States
        for(int i = 0; i < scenario.getStates().size(); i++){
            Node state = null;

            try {
                state = getInflatableStateItem(i, scenario.getStates().get(i));
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Deal with this
            }
            statesGridView.getChildren().add(state);
        }
    }

    /**
     * Shows the loading view.
     *
     */
    void showLoading() {
        //TODO: Deal with this
    }

    /**
     * Hides the loading view.
     *
     */
    void hideLoading() {
        //TODO: Deal with this
    }

    /**
     * Shows the connection error view.
     * When a connection error occurs, while trying to acquire data from the API.
     */
    void showConnectionError() {
        //TODO: Deal with this
    }

    /**
     * Shows the generic error view.
     * When any error occurs.
     */
    void showGenericErrorView() {
        //TODO: Deal with this
    }
}
