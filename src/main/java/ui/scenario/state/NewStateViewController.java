/*
 * Created by Filipe André Rodrigues on 01-03-2019 23:00
 */


package ui.scenario.state;

import com.jfoenix.controls.*;
import dao.model.SignalModel;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.cell.ColorGridCell;
import ui.widgets.JFXNumericTextField;
import ui.widgets.grid.TextableColorGridCell;
import utils.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class NewStateViewController {
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
    private JFXScrollPane signalsRootPane;

    // Private variables
    private StateModel mStateModel;
    private OnScenarioNewStateClickListener mListener;
    private int mStateId = -1;

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
    public NewStateViewController(List<StateModel> states, OnScenarioNewStateClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewStateDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newStateRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mStateModel = new StateModel(states.size());

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

        this.inputName.textProperty()
                .addListener(TextUtils.getComboBoxTextInputMaxCharactersListener(inputName));


        this.acceptButton.disableProperty().bind(
                Bindings.isEmpty(this.inputName.textProperty())
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

        for(int i = 0; i < 15; i++) {
            SignalModel s = new SignalModel(i, 1, "Sig " + i, 10);
            list.add(s);
        }

        this.signalsRootPane.getChildren().add(signalGrid);
    }

    /**
     * Method that returns the current Transition duration in case it is defined, -1 otherwise.
     *
     * @return duration value or -1
     */
    private int getCurrentTransitionDuration() {
        return inputTransitionDuration != null && inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1;
    }

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

}
