/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import com.jfoenix.controls.*;
import dao.model.*;
import io.datafx.controller.ViewController;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import ui.scenario.action.EditActionViewController;
import ui.scenario.action.NewActionViewController;
import ui.scenario.inflatables.ActionItemViewController;
import ui.scenario.state.EditStateViewController;
import ui.scenario.inflatables.StateItemViewController;
import ui.scenario.state.NewStateViewController;
import ui.widgets.JFXNumericTextField;
import ui.widgets.graph.CustomLayoutGrid;
import ui.widgets.graph.DirectionalCorneredEdge;
import ui.widgets.graph.LineEdge;
import ui.widgets.graph.TextableRectangleCell;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@ViewController(value = "/fxml/ui/Scenario.fxml", title = "New Scenario Title")
public class ScenarioUIView implements StateItemViewController.OnScenarioStateClickListener,
        ActionItemViewController.OnScenarioActionClickListener,
        EditStateViewController.OnScenarioEditStateClickListener,
        NewStateViewController.OnScenarioNewStateClickListener,
        EditActionViewController.OnScenarioEditActionClickListener,
        NewActionViewController.OnScenarioNewActionClickListener {

    private ScenarioPresenter mPresenter;

    // UI Bind variables
    @FXML
    StackPane scenarioRoot;

    @FXML
    StackPane scenarioContent;

    @FXML
    StackPane statesGraphPane;

    @FXML
    private ScrollPane actionsScrollPane;

    @FXML
    private JFXMasonryPane actionsGridView;

    @FXML
    private ScrollPane statesScrollPane;

    @FXML
    private JFXMasonryPane statesGridView;

    @FXML
    private JFXButton addStateButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private HBox slider;

    @FXML
    private VBox sliderContent;

    @FXML
    private JFXHamburger expandButton;

    @FXML
    private JFXButton addActionButton;

    @FXML
    private JFXTextField inputScenarioName;

    @FXML
    private JFXTextArea inputBriefing;

    @FXML
    private JFXComboBox<TypeModel> actorDefaultErrorMsg;

    @FXML
    private JFXTextArea inputDefaultErrorMsg;

    @FXML
    private JFXNumericTextField inputScore;

    @FXML
    private JFXNumericTextField inputMinScore;

    @FXML
    private JFXButton refreshGraphButton;

    private boolean isSlidingContentVisible = true;

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
        Platform.runLater(() -> this.actionsScrollPane.requestLayout());
        JFXScrollPane.smoothScrolling(this.actionsScrollPane);

        Platform.runLater(() -> this.statesScrollPane.requestLayout());
        JFXScrollPane.smoothScrolling(this.statesScrollPane);

        setupRefreshGraphButton();

        this.addStateButton.setOnAction(getNewStateClickListener());
        this.addActionButton.setOnAction(getNewActionClickListener());

        setupSlidingMenu();
    }

    /**
     * Method that:
     * - Sets the refresh graph button listener and logic.
     * - Adds animation on interaction to the Refresh Graph Button.
     *
     */
    private void setupRefreshGraphButton() {
        final RotateTransition rotate = new RotateTransition(Duration.seconds(1.5), this.refreshGraphButton);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        this.refreshGraphButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refreshGraphButton.setButtonType(JFXButton.ButtonType.FLAT);
                rotate.play();
            }
        });

        this.refreshGraphButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refreshGraphButton.setButtonType(JFXButton.ButtonType.RAISED);
                rotate.stop();
                refreshGraphButton.setRotate(0);
            }
        });

        this.refreshGraphButton.setOnAction(this.getRefreshGraphButtonClickListener());
    }

    /**
     * Method that setups the behavior of the scrolling menu, that contains the Scenario general data
     * TODO: Refactoring on this method
     */
    private void setupSlidingMenu() {
        sliderContent.setPrefWidth(300);
        StackPane.setMargin(scenarioContent, new Insets(0, 0, 0, sliderContent.getPrefWidth()));

        final Transition animateHamburguer = expandButton.getAnimation();
        animateHamburguer.setRate(1);
        animateHamburguer.play();

        // animation for moving the slider
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(slider.translateXProperty(), -sliderContent.getPrefWidth() + 16)),
                new KeyFrame(Duration.millis(100), new KeyValue(slider.translateXProperty(), 0d))
        );

        expandButton.setOnMouseClicked(evt -> {
            // adjust the direction of play and start playing, if not already done
            boolean playing = timeline.getStatus() == Animation.Status.RUNNING;
            if (!isSlidingContentVisible) {
                animateHamburguer.setRate(1);
                animateHamburguer.play();

                timeline.setRate(1);
                if (!playing) {
                    timeline.playFromStart();
                }

                timeline.setOnFinished(event -> {
                    if (isSlidingContentVisible)
                        StackPane.setMargin(scenarioContent, new Insets(0, 0, 0, sliderContent.getPrefWidth()));
                });

                isSlidingContentVisible = true;
            } else {
                animateHamburguer.setRate(-1);
                animateHamburguer.play();

                timeline.setRate(-1);
                if (!playing) {
                    timeline.playFrom("end");
                }
                StackPane.setMargin(scenarioContent, new Insets(0, 0, 0, 16));

                isSlidingContentVisible = false;
            }
        });

        inputScenarioName.textProperty().addListener((observable, oldValue, newValue) -> {
            this.mPresenter.updateScenarioName(newValue);
        });

        inputBriefing.textProperty().addListener((observable, oldValue, newValue) -> {
            this.mPresenter.updateBriefingContent(newValue);
        });

        actorDefaultErrorMsg.valueProperty().addListener(new ChangeListener<TypeModel>() {
            @Override public void changed(ObservableValue ov, TypeModel oldActor, TypeModel newActor) {
                mPresenter.updateGeneralErrorMsgActor(newActor);
            }
        });

        inputDefaultErrorMsg.textProperty().addListener((observable, oldValue, newValue) -> {
            this.mPresenter.updateGeneralErrorMsg(newValue);
        });

        inputScore.textProperty().addListener((observable, oldValue, newValue) -> {
            this.mPresenter.updateScoreContent(newValue);
        });

        inputMinScore.textProperty().addListener((observable, oldValue, newValue) -> {
            this.mPresenter.updateMinScoreContent(newValue);
        });
    }

    /**
     * Helper method that creates a UI State Card and returns the node to be inflate in the list.
     *
     * @param index the element position index on the Grid (used to create the alternate colors)
     * @param state the StateModel to fill the UIController
     * @return Node
     * @throws IOException
     */
    Node getInflatableStateItem(int index, int animEnterDelay, StateModel state) throws IOException {
        StateItemViewController item = new StateItemViewController(index, this);
        item.setupState(state);
        item.setupAnimatedEditFab(Duration.millis(100 * animEnterDelay + 600)); // TODO: better calculation

        return item.getStateItemRootPane();
    }

    /**
     * Helper method that creates a UI Action Card and returns the node to be inflate in the list.
     *
     * @param index  the element position index on the Grid (used to create the alternate colors)
     * @param action the ActionModel to fill the UIController
     * @return Node
     * @throws IOException
     */
    Node getInflatableActionItem(int index, int animEnterDelay, ActionModel action) throws IOException {
        ActionItemViewController item = new ActionItemViewController(index, this);
        item.setupAction(action);
        item.setupAnimatedEditFab(Duration.millis(100 * animEnterDelay + 600)); // TODO: better calculation

        return item.getStateItemRootPane();
    }

    /**
     * Invoked by the Presenter.
     * Updates the view with a whole new Scenario.
     *
     * @param scenario
     */
    void updateScenarioData(ScenarioModel scenario) {

        this.inputScenarioName.setText(scenario.getName());
        this.inputBriefing.setText(scenario.getBriefing());

        if(scenario.getActorDefaultErrorMessage() != null)
            this.actorDefaultErrorMsg.getSelectionModel().select(scenario.getActorDefaultErrorMessage());

        this.inputDefaultErrorMsg.setText(scenario.getDefaultErrorMessage());

        this.inputScore.setText(String.valueOf(scenario.getScore()));
        this.inputMinScore.setText(String.valueOf(scenario.getMinScore()));

        this.statesGridView.getChildren().clear();
        this.actionsGridView.getChildren().clear();

        // Fill States
        for (int i = 0; i < scenario.getStates().size(); i++) {
            Node state = null;

            try {
                StateModel stateTemp = scenario.getStates().get(i);
                state = getInflatableStateItem(i, i, stateTemp);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Deal with this
            }
            statesGridView.getChildren().add(state);
        }

        // Fill Actions
        for (int i = 0; i < scenario.getActions().size(); i++) {
            Node action = null;

            try {
                ActionModel actionTemp = scenario.getActions().get(i);
                action = getInflatableActionItem(i, i, actionTemp);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Deal with this
            }
            actionsGridView.getChildren().add(action);
        }

        statesGridView.requestLayout();
        actionsGridView.requestLayout();

        updateGraphView(scenario.getStates(), scenario.getActions());
    }

    /**
     * Method that redraws the States Graph View
     *
     * @param states
     * @param actions
     */
    void updateGraphView(List<StateModel> states, List<ActionModel> actions) {
        statesGraphPane.getChildren().clear();

        Graph graph = new Graph();
        final Model model = graph.getModel();

        graph.beginUpdate();

        for (StateModel state : states) {
            final ICell stateCell = new TextableRectangleCell(state.getName(), state.getId());
            model.addCell(stateCell);
        }

        for (StateModel state : states) {
            if (state.getTransition() != null) {
                addTransitionEdge(model, state.getId(), state.getTransition(), true,false, null);
            }

            // Action transitions
            boolean isVisible = true;
            String newLineAppend = "";
            for (ActionModel action : actions) {
                if (action.getStateConditions().contains(state) &&
                        action.getTransition() != null) {
                    addTransitionEdge(model, state.getId(), action.getTransition(), isVisible,
                            true, newLineAppend.concat(action.getName()));
                    isVisible = false;
                    newLineAppend += "\n\n";
                }
            }
        }

        graph.endUpdate();
        graph.layout(new CustomLayoutGrid()); // TODO: Maybe a tree

        statesGraphPane.getChildren().add(graph.getCanvas());
    }

    /**
     * Aux method to the updateGraphView().
     * It adds Edges to the Model accordingly to the received data.
     *
     * @param model
     * @param sourceId
     * @param isActionTransition
     * @param actionName         // Can be null
     */
    private void addTransitionEdge(Model model, int sourceId, TransitionModel transition, boolean isVisible, boolean isActionTransition, String actionName) {
        ICell source = null, target = null;

        for (ICell cell : model.getAddedCells()) {
            if (((TextableRectangleCell) cell).getId() == sourceId)
                source = cell;
            else if (((TextableRectangleCell) cell).getId() == transition.getStateId())
                target = cell;

            if (source != null && target != null) {
                if (!isActionTransition) {
                    final LineEdge edgeAB = new LineEdge(source, target, Orientation.HORIZONTAL);

                    edgeAB.textProperty().set(transition.getDuration() != -1 ?
                            transition.getDuration() + " sec" : "no timer");
                    model.addEdge(edgeAB);
                } else {
                    final DirectionalCorneredEdge edgeAB = new DirectionalCorneredEdge(source, target, isVisible, Orientation.HORIZONTAL);

                    edgeAB.textProperty().set(actionName);
                    model.addEdge(edgeAB);
                }
            }
        }
    }

    /**
     * Method that updates a specific State Item on the StateGridView
     *
     * @param index      to be update
     * @param state      new StateModel data
     * @param isSelected flags if the state is selected, in order to reselect after the update
     */
    void updateStateViewItem(int index, StateModel state, boolean isSelected) {
        try {
            statesGridView.getChildren().set(index, getInflatableStateItem(index, 0, state));

            if (isSelected)
                mPresenter.showSelectedStateUIItem(state.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that ease in an highlight on a specific State Item on the StateGridView
     *
     * @param indexToHighlight to be update
     */
    void selectStateViewItem(int indexToHighlight) {
        if (statesGridView.getChildren().size() == 0) return;

        Node stateView = statesGridView.getChildren().get(indexToHighlight);
        //        dropshadow(gaussian, rgba(0,0,0,0.26), 10, 0.12, -1, 2)
        stateView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(13, 213, 252,0.7), 10, 0.8, 0, 0)");
    }

    /**
     * Method that ease out an highlight on a specific State Item in the StateGridView
     *
     * @param indexToHighlight to be update
     */
    public void deselectStateViewItem(int indexToHighlight) {
        if (statesGridView.getChildren().size() == 0 || indexToHighlight >= statesGridView.getChildren().size()) return;

        Node stateView = statesGridView.getChildren().get(indexToHighlight);
        stateView.setStyle("item-card-style");
    }

    /**
     * Method that ease in an highlight on a specific Action Item in the ActionGridView
     *
     * @param indexToHighlight to be update
     */
    void selectActionViewItem(int indexToHighlight) {
        if (actionsGridView.getChildren().size() == 0) return;

        Node actionView = actionsGridView.getChildren().get(indexToHighlight);
        actionView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(240,0,0,0.7), 10, 0.8, 0, 0)");
//        actionView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(13, 213, 252,0.7), 10, 0.8, 0, 0)");
    }

    /**
     * Method that ease out an highlight on a specific Action Item in the ActionGridView
     *
     * @param indexToHighlight to be update
     */
    public void deselectActionViewItem(int indexToHighlight) {
        if (actionsGridView.getChildren().size() == 0) return;

        Node stateView = actionsGridView.getChildren().get(indexToHighlight);
        stateView.setStyle("item-card-style");
    }

    /**
     * Method that creates a specific State Item on the StateGridView
     *
     * @param newStateModel new StateModel data
     */
    void addStateViewItem(StateModel newStateModel) {
        try {
            statesGridView.getChildren().add(getInflatableStateItem(statesGridView.getChildren().size(), 0, newStateModel));

            Platform.runLater(() -> statesScrollPane.requestLayout());
            JFXScrollPane.smoothScrolling(statesScrollPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that removes a specific Action Item on the ActionGridView by index
     *
     * @param index to be removed
     */
    void removeActionViewItem(int index) {
        actionsGridView.getChildren().remove(index);
    }

    /**
     * Method that updates a specific Action Item on the ActionGridView
     *
     * @param index      to be update
     * @param action     new ActionModel data
     * @param isSelected is used to reselect the action after the update
     */
    void updateActionViewItem(int index, ActionModel action, boolean isSelected) {
        try {
            actionsGridView.getChildren().set(index, getInflatableActionItem(index, 0, action));

            // If it is a selected Action, reselect it.
            if (isSelected)
                mPresenter.showSelectedActionUIItem(action.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that creates a specific Action Item on the ActionGridView
     *
     * @param newActionModel new ActionModel data
     */
    void addActionViewItem(ActionModel newActionModel) {
        try {
            actionsGridView.getChildren().add(getInflatableActionItem(actionsGridView.getChildren().size(), 0, newActionModel));

            Platform.runLater(() -> actionsScrollPane.requestLayout());
            JFXScrollPane.smoothScrolling(actionsScrollPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that removes a specific State Item on the StateGridView by index
     *
     * @param index to be removed
     */
    void removeStateViewItem(int index) {
        statesGridView.getChildren().remove(index);
    }

    /**
     * Shows the loading view.
     */
    void showLoading() {
        //TODO: Deal with this
    }

    /**
     * Hides the loading view.
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

        JFXAlert alert = new JFXAlert((Stage) scenarioRoot.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Oops"));

        // Set state (type) name
        layout.setBody(new Label("An error has occured, please try again. \n"));

        JFXButton okButton = new JFXButton("Ok");

        okButton.getStyleClass().add("alert-cancel");
        okButton.setOnAction(event -> alert.hideWithAnimation());

        layout.setActions(okButton);

        alert.setContent(layout);
        alert.show();
    }

    /**
     * Method that implements the new State click listener behavior
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewStateClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mPresenter.requestLaunchNewStateView();
            }
        };
    }

    /**
     * Method that implements the new Action click listener behavior
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewActionClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mPresenter.requestLaunchNewActionView();
            }
        };
    }

    /**
     * Method that implements the Refresh Graph Button click listener behavior
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getRefreshGraphButtonClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mPresenter.requestRefreshGraphView();
            }
        };
    }

    /********************************************************************************************************************
     * PRESENTER INTERFACE                                                                                              *
     ********************************************************************************************************************/

    /**
     * Invoked by the Presenter.
     * Notifies the view of an edit state event.
     *
     * @param state
     * @param states
     */
    void showStateEditDialog(StateModel state, List<StateModel> states,
                             List<SignalTemplateModel> signalTypes, List<TypeModel> actorTypes, List<ActionModel> actions) {
        EditStateViewController editStateDialog = new EditStateViewController(state, states, signalTypes, actorTypes, actions, this);

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
     * Invoked by the Presenter.
     * Notifies the view of a new state event.
     *
     * @param states
     * @param signalTypes
     * @param actorTypes
     * @param actions
     */
    void showNewStateDialog(List<StateModel> states, List<SignalTemplateModel> signalTypes,
                            List<TypeModel> actorTypes, List<ActionModel> actions) {
        NewStateViewController newStateDialog = new NewStateViewController(states, signalTypes, actorTypes, actions, this);

        JFXAlert dialog = new JFXAlert((Stage) statesGridView.getScene().getWindow()); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);


//        Stage stage = (Stage) scenarioRoot.getScene().getWindow();
//        dialog.setContent(newStateDialog.getNewStateItemRootDialog(stage.getWidth()/2, stage.getHeight()/2));
        dialog.setContent(newStateDialog.getNewStateItemRootDialog());

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Gets notified by the grid items when an element is clicked with a request to edit the StateModel with id {stateId}.
     *
     * @param stateId
     */
    @Override
    public void onStateEditClicked(int stateId) {
        mPresenter.requestLaunchStateEditView(stateId);
    }

    @Override
    public void onStateSelectClicked(int stateId) {
        mPresenter.requestSelectStateItem(stateId);
    }

    /**
     * Gets notifies of a StateModel edition
     *
     * @param newStateModel
     */
    @Override
    public void onStateEditApplyClicked(StateModel newStateModel) {
        this.mPresenter.requestStateUpdate(newStateModel);
    }

    /**
     * Gets notifies of a delete StateModel action
     *
     * @param stateId is the ID to be deleted
     */
    @Override
    public void onStateDeleteClicked(int stateId) {
        mPresenter.requestDeleteStateById(stateId);
    }

    /**
     * Gets notifies of the creation of a new StateModel
     *
     * @param newStateModel
     */
    @Override
    public void onNewStateAcceptClicked(StateModel newStateModel) {
        mPresenter.requestStateCreation(newStateModel);
    }

    /**
     * Gets notified by the grid items when an element is clicked with a request to edit the EditModel with id {actionId}.
     *
     * @param actionId
     */
    @Override
    public void onActionEditClicked(int actionId) {
        mPresenter.requestLaunchActionEditView(actionId);
    }

    @Override
    public void onActionSelectClicked(int actionId) {
        mPresenter.requestSelectActionItem(actionId);

    }

    /**
     * Invoked by the Presenter.
     * Notifies the view of an edit action event.
     *  @param action
     * @param actions
     * @param states
     */
    public void showActionEditDialog(ActionModel action, List<ActionModel> actions, List<StateModel> states, List<TypeModel> actionTypes,
                                     List<TypeModel> actionCategories, List<TypeModel> actionSubcategories,
                                     List<SignalTemplateModel> signalTypes, List<TypeModel> actorTypes) {

        EditActionViewController editActionDialog = new EditActionViewController(action, actions, states, actionTypes,
                actionCategories, actionSubcategories,
                signalTypes, actorTypes, this);

        JFXAlert dialog = new JFXAlert((Stage) actionsGridView.getScene().getWindow()); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);
//        alert.setOverlayClose(false);
        dialog.setContent(editActionDialog.getEditActionItemRootDialog());
        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");

        dialog.show();
    }

    /**
     * Requested by the invoker, to launch the select scenario window
     */
    void requestLoadScenarioDialog() {
        File recordsDir = new File(Paths.get("./scenarios").toAbsolutePath().normalize().toString());
        if (!recordsDir.exists()) {
            recordsDir.mkdirs();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Scenario File");
        fileChooser.setInitialDirectory(recordsDir);
        File file = fileChooser.showOpenDialog(scenarioRoot.getScene().getWindow());

        if (file != null) {
            this.mPresenter.requestOpenScenarioFile(file.getAbsolutePath());
        }
    }

    void requestSaveScenarioDialog(String defaultFilename) {
        File recordsDir = new File(Paths.get("./scenarios").toAbsolutePath().normalize().toString());
        if (!recordsDir.exists()) {
            recordsDir.mkdirs();
        }

        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "Scenario file", "*.bin");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Scenario File");
        fileChooser.setInitialFileName(defaultFilename);
        fileChooser.setInitialDirectory(recordsDir);
        fileChooser.getExtensionFilters().add(fileExtensions);

        File file = fileChooser.showSaveDialog(scenarioRoot.getScene().getWindow());
        if (file != null) {
            this.mPresenter.requestSaveScenarioWithPath(file.getAbsolutePath());
        }
    }

    void requestExportJSONScenario(String defaultFilename) {
        File recordsDir = new File(Paths.get("./scenarios/exported").toAbsolutePath().normalize().toString());
        if (!recordsDir.exists()) {
            recordsDir.mkdirs();
        }

        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "Scenario exported file", "*.json");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export JSON File");
        fileChooser.setInitialFileName(defaultFilename);
        fileChooser.setInitialDirectory(recordsDir);
        fileChooser.getExtensionFilters().add(fileExtensions);

        File file = fileChooser.showSaveDialog(scenarioRoot.getScene().getWindow());
        if (file != null) {
            this.mPresenter.requestExportJSONWithPath(file.getAbsolutePath());
        }
    }

    /**
     * Invoked by the Presenter.
     * Notifies the view of a new state event.
     * @param actions
     * @param states
     * @param actionTypes
     * @param actionCategories
     * @param actionSubcategories
     * @param signalTypes
     * @param actorTypes
     */
    void showNewActionDialog(List<ActionModel> actions, List<StateModel> states, List<TypeModel> actionTypes,
                             List<TypeModel> actionCategories, List<TypeModel> actionSubcategories,
                             List<SignalTemplateModel> signalTypes, List<TypeModel> actorTypes) {

        NewActionViewController newActDialog = new NewActionViewController(actions, states, actionTypes,
                actionCategories, actionSubcategories, signalTypes, actorTypes, this);

        JFXAlert dialog = new JFXAlert((Stage) actionsGridView.getScene().getWindow()); // get window context

        dialog.initModality(Modality.APPLICATION_MODAL);

        // TODO: Set window current size with a vertical/horizontal threshold
//        Stage stage = (Stage) scenarioRoot.getScene().getWindow();
//        dialog.setContent(newActDialog.getNewActionItemRootDialog(stage.getWidth()/2, stage.getHeight()/2));
        dialog.setContent(newActDialog.getNewActionItemRootDialog());

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Invoked by the Presenter.
     * Notifies the view that the Actor Types are loaded and update the respective combo box.
     *  @param actorTypes
     *
     */
    void updateActorErrorMsg(List<TypeModel> actorTypes) {
        // Init Actor Types ComboBox
        this.actorDefaultErrorMsg.getItems().add(new TypeModel(-1, -1,"NONE"));
        this.actorDefaultErrorMsg.getItems().addAll(actorTypes);
        this.actorDefaultErrorMsg.getSelectionModel().select(0);
    }

    /**
     * Gets notifies of a ActionModel edition
     *
     * @param newActionModel
     */
    @Override
    public void onActionEditApplyClicked(ActionModel newActionModel) {
        this.mPresenter.requestActionUpdate(newActionModel);
    }

    /**
     * Gets notifies of a delete ActionModel action
     *
     * @param actionId is the ID to be deleted
     */
    @Override
    public void onActionDeleteClicked(int actionId) {
        mPresenter.requestDeleteActionById(actionId);
    }


    /**
     * Method that asks presenter to update complementary Actions when dependencies change
     *
     * @param actionToChange
     * @param actionsToUpdate
     */
    @Override
    public void updateComplementaryActions(ActionModel actionToChange, List<ActionModel> previousCompActions, List<ActionModel> actionsToUpdate) {
        this.mPresenter.requestComplementaryActionsUpdate(actionToChange, previousCompActions, actionsToUpdate);
    }

    /**
     * Gets notifies of the creation of a new ActionModel
     *
     * @param newActionModel
     */
    @Override
    public void onNewActionAcceptClicked(ActionModel newActionModel) {
        mPresenter.requestActionCreation(newActionModel);
    }

}
