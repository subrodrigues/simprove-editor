/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import com.jfoenix.controls.*;
import dao.model.ScenarioModel;
import dao.model.StateModel;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import ui.scenario.state.EditStateViewController;
import ui.scenario.inflatables.StateItemViewController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@ViewController(value = "/fxml/ui/Scenario.fxml", title = "New Scenario Title")
public class ScenarioUIView implements StateItemViewController.OnScenarioStateClickListener,
        EditStateViewController.OnScenarioEditStateClickListener {

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
     * @param index the element position index on the Grid (used to create the alternate colors)
     * @param state the StateModel to fill the UIController
     * @return Node
     *
     * @throws IOException
     */
    Node getInflatableStateItem(int index, StateModel state) throws IOException {
        StateItemViewController item = new StateItemViewController(index, this);
        item.setupState(state);
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
     * Invoked by the Presenter.
     * Notifies the view of an edit state event.
     *
     * @param state
     * @param states
     */
    void showStateEditDialog(StateModel state, List<StateModel> states){
        EditStateViewController editStateDialog = new EditStateViewController(state, states, this);

        JFXAlert dialog = new JFXAlert((Stage) statesGridView.getScene().getWindow()); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);
//        alert.setOverlayClose(false);
        dialog.setContent(editStateDialog.getEditStateItemRootDialog());
        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");

        dialog.show();
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

    /**
     * Gets notified by the grid items when an element is clicked with a request to edit the StateModel with id {stateId}.
     *
     * @param stateId
     */
    @Override
    public void onStateEditClicked(int stateId) {
        mPresenter.requestStateEdit(stateId);
    }

    @Override
    public void onStateSelectClicked(String stateId) {

    }
}
