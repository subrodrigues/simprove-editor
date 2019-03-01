/*
 * Created by Filipe André Rodrigues on 28-02-2019 18:10
 */

package ui.scenario.state;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dao.model.StateModel;
import dao.model.TransitionModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.widgets.JFXNumericTextField;

import java.io.IOException;
import java.util.List;

public class EditStateViewController {
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
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    // Private variables
    private StateModel mStateModel;
    private OnScenarioEditStateClickListener mListener;
    private int mStateId = -1;

    public interface OnScenarioEditStateClickListener {
        void onStateEditApplyClicked(StateModel newStateModel);
    }

    public EditStateViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditStateDialog.fxml"));
        fxmlLoader.setController(this);
        try
        {
            editStateRoot = fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor with respective click listener.
     *
     * @param states
     * @param listener
     */
    public EditStateViewController(StateModel state, List<StateModel> states, OnScenarioEditStateClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditStateDialog.fxml"));
        fxmlLoader.setController(this);
        try
        {
            editStateRoot = fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        setupState(state);
        setupUI(states);
    }

    private void setupState(StateModel state){
        this.mStateModel = state;
    }

    private void setupUI(List<StateModel> states){
        this.inputName.setText(mStateModel.getName());

        // Init Transition ComboBox
        this.transitionComboBox.getItems().add(new StateModel(-1, "NONE"));
        this.transitionComboBox.getItems().addAll(states);

        // Set selected Transition
        if(mStateModel.getTransition() != null) {
            this.transitionComboBox.getSelectionModel().select(new StateModel(mStateModel.getTransition().getStateId(), ""));
            this.inputTransitionDuration.setText(mStateModel.getTransition().getDuration() + "");
        }

        /*
         * Set Listeners and Bindings
         */
        this.transitionComboBox.valueProperty().addListener(new ChangeListener<StateModel>() {
            @Override
            public void changed(ObservableValue<? extends StateModel> observable, StateModel oldValue, StateModel newValue) {
                if(newValue.getId() == -1){
                    inputTransitionDuration.setDisable(true);
                } else{
                    inputTransitionDuration.setDisable(false);
                }
            }
        });

        this.inputName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                this.inputName.validate();
            }
        });

        this.applyButton.disableProperty().bind(
                Bindings.isEmpty(this.inputName.textProperty())
        );

        this.applyButton.setOnAction(getApplyClickListener());
        //TODO
    }

    /**
     * Method that returns the current Transition duration in case it is defined, -1 otherwise.
     *
     * @return duration value or -1
     */
    private int getCurrentTransitionDuration(){
        return inputTransitionDuration != null && inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1;
    }

    public StackPane getEditStateItemRootDialog(){
        return editStateRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow(){
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
    private EventHandler<ActionEvent> getApplyClickListener(){
        return new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {

                // Set state name
                mStateModel.setName(inputName.getText());

                // Set/Update transition model
                if(mStateModel.getTransition() != null){

                    // If we removed the Transition
                    if(transitionComboBox.getValue().getId() == -1){
                        mStateModel.setTransition(null);
                    }
                    else { // If we updated the existing Transition
                        mStateModel.getTransition().setDuration(getCurrentTransitionDuration());
                        mStateModel.getTransition().setStateId(transitionComboBox.getValue().getId());
                    }
                } else { // If we didn't have a transition
                    // And we are adding a Transition
                    if(transitionComboBox.getValue().getId() != -1){
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
}
