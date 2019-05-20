/*
 * Created by Filipe André Rodrigues on 28-02-2019 18:10
 */

package ui.scenario.state;

import com.jfoenix.controls.*;
import dao.model.SignalModel;
import dao.model.SignalTemplateModel;
import dao.model.StateModel;
import dao.model.TransitionModel;
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
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import ui.scenario.signal.EditSignalViewController;
import ui.scenario.signal.NewSignalViewController;
import ui.widgets.JFXNumericTextField;
import ui.widgets.grid.TextableColorGridCell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditStateViewController implements NewSignalViewController.OnNewSignalClickListener,
        EditSignalViewController.OnEditSignalClickListener,
        TextableColorGridCell.OnTextableColorGridClickListener {
    // UI Bind variables
    @FXML
    private StackPane editStateRoot;

    @FXML
    private JFXTextField inputName;

    @FXML
    private JFXComboBox<StateModel> transitionComboBox;

    @FXML
    private JFXNumericTextField inputTransitionDuration;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private StackPane signalsRootPane;

    @FXML
    private JFXButton addSignalButton;

    // Private variables
    private StateModel mStateModel;
    private OnScenarioEditStateClickListener mListener;
    private int mStateId = -1;

    private List<SignalModel> mStateSignals;

    // Available Signals
    private List<SignalTemplateModel> mSignalTypes;


    public interface OnScenarioEditStateClickListener {
        void onStateEditApplyClicked(StateModel newStateModel);

        void onStateDeleteClicked(int stateId);
    }

    public EditStateViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditStateDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            editStateRoot = fxmlLoader.load();
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
    public EditStateViewController(StateModel state, List<StateModel> states, List<SignalTemplateModel> signalTypes, OnScenarioEditStateClickListener listener) {
        this.mListener = listener;
        this.mSignalTypes = signalTypes;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditStateDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            editStateRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupState(state);
        setupUI(states);
    }

    private void setupState(StateModel state) {
        this.mStateSignals = new ArrayList<SignalModel>();
        this.mStateModel = state;

        setupSignalsGrid(state.getSignals());
    }

    private void setupUI(List<StateModel> states) {
        this.inputName.setText(mStateModel.getName());

        // Init Transition ComboBox
        this.transitionComboBox.getItems().add(new StateModel(-1, "NONE"));
        this.transitionComboBox.getItems().addAll(states);

        // Set selected Transition
        if (mStateModel.getTransition() != null) {
            this.transitionComboBox.getSelectionModel().select(new StateModel(mStateModel.getTransition().getStateId(), ""));
            this.inputTransitionDuration.setText(mStateModel.getTransition().getDuration() + "");
        }

        /*
         * Set Listeners and Bindings
         */
        this.transitionComboBox.valueProperty().addListener(new ChangeListener<StateModel>() {
            @Override
            public void changed(ObservableValue<? extends StateModel> observable, StateModel oldValue, StateModel newValue) {
                if (newValue.getId() == -1) {
                    if(inputTransitionDuration.getText().isEmpty()){
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

        this.applyButton.disableProperty().bind(
                Bindings.or(this.inputName.textProperty().isEmpty(), inputTransitionDuration.textProperty().isEmpty())
        );

        this.applyButton.setOnAction(getApplyClickListener());

        this.deleteButton.setOnAction(getDeleteClickListener());

        this.cancelButton.setOnAction(getCancelClickListener());
    }

    private void setupSignalsGrid(List<SignalModel> signals) {
        final ObservableList<SignalModel> list = FXCollections.<SignalModel>observableArrayList(signals);

        GridView<SignalModel> signalGrid = new GridView<>(list);
        signalGrid.setHorizontalCellSpacing(-4); //horizontal gap in pixels => that's what you are asking for
        signalGrid.setVerticalCellSpacing(-4); //vertical gap in pixels
        signalGrid.setPadding(new Insets(6, 6, 6, 6)); //margins around the whole grid

        //(top/right/bottom/left)
        TextableColorGridCell.OnTextableColorGridClickListener context = this;
        signalGrid.setCellFactory(new Callback<GridView<SignalModel>, GridCell<SignalModel>>() {
            @Override
            public GridCell<SignalModel> call(GridView<SignalModel> arg0) {
                return new TextableColorGridCell(context);
            }
        });

        this.signalsRootPane.getChildren().add(signalGrid);
        this.addSignalButton.setOnAction(getNewSignalClickListener(this.mSignalTypes));
    }

    /**
     * Method that returns the current Transition duration in case it is defined, -1 otherwise.
     *
     * @return duration value or -1
     */
    private int getCurrentTransitionDuration() {
        return inputTransitionDuration != null && inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1;
    }

    public StackPane getEditStateItemRootDialog() {
        return editStateRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) editStateRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that implements the Apply changes click listener behavior.
     * It updates the mStateModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getApplyClickListener() {
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

                mListener.onStateEditApplyClicked(mStateModel);

                closeDialogWindow();
            }
        };
    }

    /**
     * Method that implements the Delete click listener behavior with confirmation alert.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getDeleteClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                JFXAlert alert = new JFXAlert((Stage) deleteButton.getScene().getWindow());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setOverlayClose(false);
                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label("Confirmation"));
                layout.setBody(new Label("Are you sure you want to delete " + inputName.getText() + "? \n"));

                JFXButton noButton = new JFXButton("No");

                JFXButton yesButton = new JFXButton("Yes");

                noButton.getStyleClass().add("alert-cancel");
                noButton.setOnAction(event -> alert.hideWithAnimation());

                yesButton.getStyleClass().add("alert-accept");
                yesButton.setOnAction(getDeletePositiveConfirmationClickListener());

                layout.setActions(noButton, yesButton);

                alert.setContent(layout);
                alert.show();
            }
        };
    }

    /**
     * Method that implements the positive confirmation to delete the state.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getDeletePositiveConfirmationClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mListener.onStateDeleteClicked(mStateModel.getId());
                closeDialogWindow();
            }
        };
    }

    /**
     * Method that implements the positive confirmation to cancel the state edition
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
     * Method that updates the GridView UI. Adding a new Signal
     *
     * @param signal
     */
    private void addSignalToGridView(SignalModel signal) {
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().add(signal);
    }

    /**
     * Method that updates the GridView specified item.
     *
     * @param editedSignalModel
     */
    private void updateGridViewSignal(SignalModel editedSignalModel) {
        final int index = ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().indexOf(editedSignalModel);
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().set(index, editedSignalModel);
    }

    /**
     * Method that removes the GridView specified item.
     *
     * @param signal
     */
    private void removeGridViewSignal(SignalModel signal) {
//        final int index = ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().indexOf(signal);
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().remove(signal);
    }

    /**
     * Method that shows the add new signal window
     *
     * @param signals
     */
    private void showNewSignalDialog(List<SignalTemplateModel> signals) {
        Stage stage = (Stage) editStateRoot.getScene().getWindow();

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
     *
     * @param signals
     */
    private void showEditSignalDialog(SignalModel signalToEdit, List<SignalTemplateModel> signals) {
        Stage stage = (Stage) editStateRoot.getScene().getWindow();

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

}
