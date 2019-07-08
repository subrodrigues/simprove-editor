/*
 * Created by Filipe André Rodrigues on 01-03-2019 23:00
 */

package ui.scenario.state;

import com.jfoenix.controls.*;
import dao.model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import ui.scenario.signal.EditSignalViewController;
import ui.scenario.signal.NewSignalViewController;
import ui.scenario.tip.EditTipViewController;
import ui.scenario.tip.NewTipViewController;
import ui.widgets.JFXDecimalTextField;
import ui.widgets.JFXNumericTextField;
import ui.widgets.grid.SignalTextableColorGridCell;
import ui.widgets.grid.TipTextableColorGridCell;
import utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewStateViewController implements NewSignalViewController.OnNewSignalClickListener,
        EditSignalViewController.OnEditSignalClickListener,
        SignalTextableColorGridCell.OnTextableColorGridClickListener,
        TipTextableColorGridCell.OnTextableColorGridClickListener,
        NewTipViewController.OnNewTipClickListener,
        EditTipViewController.OnEditTipClickListener {

    // UI Bind variables
    @FXML
    private StackPane newStateRoot;

    @FXML
    private JFXTextField inputName;

    @FXML
    private JFXComboBox<StateModel> transitionComboBox;

    @FXML
    private JFXDecimalTextField inputTransitionDuration;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private StackPane signalsRootPane;

    @FXML
    private JFXButton addSignalButton;

    @FXML
    private StackPane tipsRootPane;

    @FXML
    private JFXButton addTipButton;

    // Private variables
    private StateModel mStateModel;
    private OnScenarioNewStateClickListener mListener;
    private int mStateId = -1;
    private List<SignalModel> mStateSignals;
    private List<TipModel> mStateTips;

    // Available Signals
    private List<SignalTemplateModel> mSignalTypes;

    // Available Actors
    private List<TypeModel> mActorTypes;

    // Current Actions (for conditions)
    private List<ActionModel> mCurrentActions;

    public interface OnScenarioNewStateClickListener {
        void onNewStateAcceptClicked(StateModel newStateModel);
    }

    public NewStateViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewStateDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newStateRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor with respective click listener.
     *
     * @param states
     * @param listener
     */
    public NewStateViewController(List<StateModel> states, List<SignalTemplateModel> signalTypes, List<TypeModel> actorTypes, List<ActionModel> actions, OnScenarioNewStateClickListener listener) {
        this.mListener = listener;
        this.mSignalTypes = signalTypes;
        this.mActorTypes = actorTypes;
        this.mCurrentActions = actions;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewStateDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newStateRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupState(new StateModel(states.size()), actions); // Create new state with index = states.size
        setupUI(states);
    }

    private void setupState(StateModel state, List<ActionModel> actions) {
        this.mStateSignals = new ArrayList<SignalModel>();
        this.mStateTips = new ArrayList<>();

        this.mStateModel = state;

        setupSignalsGrid();
        setupTipsGrid();
    }

    private void setupUI(List<StateModel> states) {
        this.inputName.setText(mStateModel.getName() != null ? mStateModel.getName() : "");

        // Init Transition ComboBox
        this.transitionComboBox.getItems().add(new StateModel(-1, "NONE"));
        this.transitionComboBox.getItems().addAll(states);

        /*
         * Set Listeners and Bindings
         */
        this.transitionComboBox.valueProperty().addListener(new ChangeListener<StateModel>() {
            @Override
            public void changed(ObservableValue<? extends StateModel> observable, StateModel oldValue, StateModel newValue) {
                if (newValue.getId() == -1) {
                    if (inputTransitionDuration.getText().isEmpty()) {
                        inputTransitionDuration.setText("0");
                        inputTransitionDuration.validate();
                    }

                    inputTransitionDuration.setDisable(true);
                } else {
                    inputTransitionDuration.setDisable(false);
                }
            }
        });

        this.inputName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                this.inputName.validate();
            }
        });

        this.inputTransitionDuration.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!inputTransitionDuration.isDisabled() && !newVal) {
                this.inputTransitionDuration.validate();
            }
        });

        this.inputName.textProperty()
                .addListener(TextUtils.getComboBoxTextInputMaxCharactersListener(inputName));

        this.acceptButton.disableProperty().bind(
                Bindings.or(this.inputName.textProperty().isEmpty(), inputTransitionDuration.textProperty().isEmpty())
        );

        this.acceptButton.setOnAction(getNewStateAcceptClickListener());

        this.cancelButton.setOnAction(getCancelClickListener());
        // TODO

    }

    /**
     *  // TODO: Refactor this tips behavior into a parent class (maybe with templates to be used by the signals logic too)
     */
    private void setupTipsGrid() {
        final ObservableList<TipModel> list = FXCollections.<TipModel>observableArrayList();

        GridView<TipModel> tipGrid = new GridView<>(list);
        tipGrid.setHorizontalCellSpacing(-4); //horizontal gap in pixels => that's what you are asking for
        tipGrid.setVerticalCellSpacing(-4); //vertical gap in pixels
        tipGrid.setPadding(new Insets(6, 6, 6, 6)); //margins around the whole grid

        //(top/right/bottom/left)
        TipTextableColorGridCell.OnTextableColorGridClickListener context = this;
        tipGrid.setCellFactory(new Callback<GridView<TipModel>, GridCell<TipModel>>() {
            @Override
            public GridCell<TipModel> call(GridView<TipModel> arg0) {
                return new TipTextableColorGridCell(context);
            }
        });

        this.tipsRootPane.getChildren().add(tipGrid);
        this.addTipButton.setOnAction(getNewTipClickListener(this.mActorTypes));
    }

    /**
     * Method that updates the GridView UI. Adding a new Tip
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param tip
     */
    private void addTipToGridView(TipModel tip) {
        ((GridView<TipModel>) this.tipsRootPane.getChildren().get(0)).getItems().add(tip);
    }

    /**
     * Method that updates the GridView specified item.
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param editedTip
     */
    private void updateGridViewTip(TipModel editedTip) {
        final int index = ((GridView<TipModel>) this.tipsRootPane.getChildren().get(0)).getItems().indexOf(editedTip);
        ((GridView<TipModel>) this.tipsRootPane.getChildren().get(0)).getItems().set(index, editedTip);
    }

    /**
     * Method that removes the GridView specified item.
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param tip
     */
    private void removeGridViewTip(TipModel tip) {
        ((GridView<TipModel>) this.tipsRootPane.getChildren().get(0)).getItems().remove(tip);
    }

    /**
     *  // TODO: Refactor this signals behavior into a parent class
     */
     private void setupSignalsGrid() {
        final ObservableList<SignalModel> list = FXCollections.<SignalModel>observableArrayList();

        GridView<SignalModel> signalGrid = new GridView<>(list);
        signalGrid.setHorizontalCellSpacing(-4); //horizontal gap in pixels => that's what you are asking for
        signalGrid.setVerticalCellSpacing(-4); //vertical gap in pixels
        signalGrid.setPadding(new Insets(6, 6, 6, 6)); //margins around the whole grid

        //(top/right/bottom/left)
        SignalTextableColorGridCell.OnTextableColorGridClickListener context = this;
        signalGrid.setCellFactory(new Callback<GridView<SignalModel>, GridCell<SignalModel>>() {
            @Override
            public GridCell<SignalModel> call(GridView<SignalModel> arg0) {
                return new SignalTextableColorGridCell(context);
            }
        });

        this.signalsRootPane.getChildren().add(signalGrid);
        this.addSignalButton.setOnAction(getNewSignalClickListener(this.mSignalTypes));
    }

    /**
     * Method that updates the GridView UI. Adding a new Signal
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param signal
     */
    private void addSignalToGridView(SignalModel signal) {
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().add(signal);
    }

    /**
     * Method that updates the GridView specified item.
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param editedSignalModel
     */
    private void updateGridViewSignal(SignalModel editedSignalModel) {
        final int index = ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().indexOf(editedSignalModel);
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().set(index, editedSignalModel);
    }

    /**
     * Method that removes the GridView specified item.
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param signal
     */
    private void removeGridViewSignal(SignalModel signal) {
//        final int index = ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().indexOf(signal);
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().remove(signal);
    }

    /**
     * Method that returns the current Transition duration in case it is defined, -1 otherwise.
     *
     * @return duration value or -1
     */
    private int getCurrentTransitionDuration() {
        return inputTransitionDuration != null && inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1;
    }

    /**
     * Method that returns the root view with a specific width and height
     *
     * @param width
     * @param height
     * @return StacePane (root view)
     */
    public StackPane getNewStateItemRootDialog(double width, double height) {
        newStateRoot.setPrefWidth(width);
        newStateRoot.setPrefHeight(height);

        return newStateRoot;
    }

    /**
     * Method that returns the root view
     *
     * @return StacePane (root view)
     */
    public StackPane getNewStateItemRootDialog() {
        return newStateRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) newStateRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that implements the Accept new State click listener behavior.
     * It updates the mStateModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewStateAcceptClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // Set state name
                mStateModel.setName(inputName.getText());

                // Set/Update transition model
                if (mStateModel.getTransition() != null) {

                    // If we removed the Transition
                    if (transitionComboBox.getValue().getId() == -1) {
                        mStateModel.setTransition(null);
                    } else { // If we updated the existing Transition
                        mStateModel.getTransition().setDuration(getCurrentTransitionDuration());
                        mStateModel.getTransition().setStateId(transitionComboBox.getValue().getId());
                    }
                } else { // If we didn't have a transition
                    // And we are adding a Transition
                    if (transitionComboBox.getValue() != null && transitionComboBox.getValue().getId() != -1) {
                        mStateModel.setTransition(new TransitionModel((inputTransitionDuration != null &&
                                inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1),
                                transitionComboBox.getValue().getId()));
                    }
                }

                mStateModel.setSignals(mStateSignals);
                mStateModel.setTips(mStateTips);

                mListener.onNewStateAcceptClicked(mStateModel);

                closeDialogWindow();
            }
        };
    }

    /**
     * Method that implements the positive confirmation to cancel the new state window
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getCancelClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                closeDialogWindow();
            }
        };
    }

    /**
     * Method that shows the add new signal window
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param signals
     */
    private void showNewSignalDialog(List<SignalTemplateModel> signals) {
        Stage stage = (Stage) newStateRoot.getScene().getWindow();

        NewSignalViewController newSignalDialog = new NewSignalViewController(signals, this.mStateSignals.size(), this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(newSignalDialog.getNewSignalItemRootDialog(stage.getWidth() / 1.5, stage.getHeight() / 1.5));

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Method that shows the edit signal window
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param signals
     */
    private void showEditSignalDialog(SignalModel signalToEdit, List<SignalTemplateModel> signals) {
        Stage stage = (Stage) newStateRoot.getScene().getWindow();

        EditSignalViewController newSignalDialog = new EditSignalViewController(signalToEdit, signals, this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(newSignalDialog.getNewSignalItemRootDialog(stage.getWidth() / 1.5, stage.getHeight() / 1.5));

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Method that implements the action behavior to launch a new signal window
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewSignalClickListener(List<SignalTemplateModel> signals) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showNewSignalDialog(signals);
            }
        };
    }

    /**
     * Method that shows the "Add New Tip" window
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @param actorTypes
     */
    private void showNewTipDialog(List<TypeModel> actorTypes) {
        Stage stage = (Stage) newStateRoot.getScene().getWindow();

        NewTipViewController newTipDialog = new NewTipViewController(actorTypes, this.mStateTips.size(), this.mCurrentActions, this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(newTipDialog.getNewTipItemRootDialog());

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Method that shows the "Edit Tip" window
     *
     * @param tipToEdit
     * @param actorTypes
     */
    private void showEditTipDialog(TipModel tipToEdit, List<TypeModel> actorTypes) {
        Stage stage = (Stage) newStateRoot.getScene().getWindow();

        EditTipViewController tipDialog = new EditTipViewController(tipToEdit, actorTypes, this.mCurrentActions, this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(tipDialog.getEditSignalItemRootDialog());

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Method that implements the action behavior to launch a new signal window
     *  // TODO: Refactor this signals behavior into a parent class
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewTipClickListener(List<TypeModel> actorTypes) {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showNewTipDialog(actorTypes);
            }
        };
    }


    /********************************************************************************************************************
     * CALLBACKS INTERFACE                                                                                              *
     ********************************************************************************************************************/

    @Override
    public void onNewSignalAcceptClicked(SignalModel newSignalModel) {
        this.mStateSignals.add(newSignalModel);

        this.addSignalToGridView(newSignalModel);
    }

    @Override
    public void onEditSignalAcceptClicked(SignalModel editedSignalModel) {
        if(this.mStateSignals.contains(editedSignalModel)){
            this.mStateSignals.set(this.mStateSignals.indexOf(editedSignalModel), editedSignalModel);

            updateGridViewSignal(editedSignalModel);
        }
    }

    @Override
    public void onSignalDeleteClicked(SignalModel signal) {
        int indexToRemove = this.mStateSignals.indexOf(signal);

        if(indexToRemove != -1) {
            this.mStateSignals.remove(indexToRemove); // clean data
            this.removeGridViewSignal(signal); // Refresh UI
        }
    }

    @Override
    public void onSignalGridItemClick(SignalModel clickedItem) {
        showEditSignalDialog(clickedItem, this.mSignalTypes);
    }

    @Override
    public void onNewTipAcceptClicked(TipModel newTipModel) {
        this.mStateTips.add(newTipModel);
        this.addTipToGridView(newTipModel);
    }

    @Override
    public void onTipGridItemClick(TipModel clickedItem) {
        this.showEditTipDialog(clickedItem, mActorTypes);
    }

    @Override
    public void onEditTipAcceptClicked(TipModel mTipModel) {
        if(this.mStateTips.contains(mTipModel)){
            this.mStateTips.set(this.mStateTips.indexOf(mTipModel), mTipModel);

            updateGridViewTip(mTipModel);
        }
    }

    @Override
    public void onEditTipDeleteClicked(TipModel mTipModel) {
        int indexToRemove = this.mStateTips.indexOf(mTipModel);

        if(indexToRemove != -1) {
            this.mStateTips.remove(indexToRemove); // clean data
            this.removeGridViewTip(mTipModel); // Refresh UI
        }
    }
}
