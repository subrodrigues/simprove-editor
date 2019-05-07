/*
 * Created by Filipe André Rodrigues on 01-03-2019 23:00
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import ui.scenario.signal.NewSignalViewController;
import ui.widgets.JFXNumericTextField;
import ui.widgets.grid.TextableColorGridCell;
import utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewStateViewController implements NewSignalViewController.OnNewSignalClickListener {
    // UI Bind variables
    @FXML
    private StackPane newStateRoot;

    @FXML
    private JFXTextField inputName;

    @FXML
    private JFXComboBox<StateModel> transitionComboBox;

    @FXML
    private JFXNumericTextField inputTransitionDuration;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private StackPane signalsRootPane;

    @FXML
    private JFXButton addSignalButton;

    // Private variables
    private StateModel mStateModel;
    private OnScenarioNewStateClickListener mListener;
    private int mStateId = -1;
    private List<SignalModel> mStateSignals;

    // Available Signals
    private List<SignalTemplateModel> mSignalTypes;

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
    public NewStateViewController(List<StateModel> states, List<SignalTemplateModel> signalTypes, OnScenarioNewStateClickListener listener) {
        this.mListener = listener;
        this.mSignalTypes = signalTypes;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewStateDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newStateRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.mStateSignals = new ArrayList<SignalModel>();
        this.mStateModel = new StateModel(states.size());

        setupUI(states);
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
                    if(inputTransitionDuration.getText().isEmpty()) {
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
        //TODO

        setupSignalsGrid();
    }

    private void setupSignalsGrid() {
        final ObservableList<SignalModel> list = FXCollections.<SignalModel>observableArrayList();

        GridView<SignalModel> signalGrid = new GridView<>(list);
        signalGrid.setHorizontalCellSpacing(-4); //horizontal gap in pixels => that's what you are asking for
        signalGrid.setVerticalCellSpacing(-4); //vertical gap in pixels
        signalGrid.setPadding(new Insets(6, 6, 6, 6)); //margins around the whole grid

        //(top/right/bottom/left)
        signalGrid.setCellFactory(new Callback<GridView<SignalModel>, GridCell<SignalModel>>() {
            @Override public GridCell<SignalModel> call(GridView<SignalModel> arg0) {
                return new TextableColorGridCell();
            }
        });

        this.signalsRootPane.getChildren().add(signalGrid);
        this.addSignalButton.setOnAction(getNewSignalClickListener(this.mSignalTypes));
    }

    /**
     * Method that updates the GridView UI. Adding a new Signal
     *
     * @param signal
     */
    private void addSignalToGridView(SignalModel signal){
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0) ).getItems().add(signal);
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
     *
     * @param signals
     */
    private void showNewSignalDialog(List<SignalTemplateModel> signals) {
        Stage stage = (Stage) newStateRoot.getScene().getWindow();

        NewSignalViewController newSignalDialog = new NewSignalViewController(signals, this.mStateSignals.size(), this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(newSignalDialog.getNewSignalItemRootDialog(stage.getWidth()/1.5, stage.getHeight()/1.5));

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
}
