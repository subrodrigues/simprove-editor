/*
 * Created by Filipe André Rodrigues on 13-03-2019 19:32
 */

package ui.scenario.action;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dao.model.ActionModel;
import dao.model.StateModel;
import dao.model.TransitionModel;
import dao.model.TypeModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.widgets.AutoCompleteComboBoxListener;
import ui.widgets.JFXNumericTextField;
import utils.ConstantUtils;
import utils.TextUtils;

import java.io.IOException;
import java.util.List;

public class NewActionViewController {
    // UI Bind variables
    @FXML
    private StackPane newActionRoot;

//    @FXML
//    private JFXTextField inputName;

    @FXML
    private JFXComboBox<TypeModel> actionTypeComboBox;

    @FXML
    private JFXComboBox<StateModel> transitionComboBox;

    @FXML
    private JFXNumericTextField inputTransitionDuration;

    @FXML
    private JFXComboBox<TypeModel> categoryComboBox;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private JFXButton cancelButton;

    // Private variables
    private ActionModel mActionModel;
    private OnScenarioNewActionClickListener mListener;
    private int mStateId = -1;

    public interface OnScenarioNewActionClickListener {
        void onNewActionAcceptClicked(ActionModel newActionModel);
    }

    public NewActionViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewActionDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newActionRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor with respective click listener.
     *
     * @param actions
     * @param actionTypes
     * @param listener
     */
    public NewActionViewController(List<ActionModel> actions, List<StateModel> states, List<TypeModel> actionTypes, OnScenarioNewActionClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewActionDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newActionRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mActionModel = new ActionModel(actions.size());

        setupUI(states, actionTypes);
    }

    private void setupUI(List<StateModel> states, List<TypeModel> actionTypes) {
//        this.inputName.setText(mActionModel.getName() != null ? mActionModel.getName() : "");

        // Init Transition ComboBox
        this.transitionComboBox.getItems().add(new StateModel(-1, "NONE"));
        this.transitionComboBox.getItems().addAll(states);

        this.categoryComboBox.getItems().addAll(ConstantUtils.requestActionCategories());

        this.actionTypeComboBox.getItems().addAll(actionTypes);
        new AutoCompleteComboBoxListener<>(this.actionTypeComboBox);

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

        this.actionTypeComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                this.actionTypeComboBox.validate();
            }
        });

        this.actionTypeComboBox.getEditor().textProperty()
                .addListener(TextUtils.getComboBoxTextInputMaxCharactersListener(this.actionTypeComboBox));

        this.acceptButton.setOnAction(getNewActionAcceptClickListener());

        this.categoryComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                this.categoryComboBox.validate();
            }
        });

        this.acceptButton.disableProperty().bind(
                Bindings.or(this.actionTypeComboBox.getEditor().textProperty().isEmpty(),
                        this.categoryComboBox.valueProperty().isNull())
        );

        this.cancelButton.setOnAction(getCancelClickListener());

        // TODO


    }

    /**
     * Method that returns the current Transition duration in case it is defined, -1 otherwise.
     *
     * @return duration value or -1
     */
    private int getCurrentTransitionDuration() {
        return inputTransitionDuration != null && inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1;
    }

    public StackPane getNewActionItemRootDialog() {
        return newActionRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) newActionRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that implements the Accept new State click listener behavior.
     * It updates the mStateModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewActionAcceptClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // Set state (type) name
                mActionModel.setName(actionTypeComboBox.getEditor().textProperty().getValue());

                // Set/Update transition model
                if (mActionModel.getTransition() != null) {

                    // If we removed the Transition
                    if (transitionComboBox.getValue().getId() == -1) {
                        mActionModel.setTransition(null);
                    } else { // If we updated the existing Transition
                        mActionModel.getTransition().setDuration(getCurrentTransitionDuration());
                        mActionModel.getTransition().setStateId(transitionComboBox.getValue().getId());
                    }
                } else { // If we didn't have a transition
                    // And we are adding a Transition
                    if (transitionComboBox.getValue() != null && transitionComboBox.getValue().getId() != -1) {
                        mActionModel.setTransition(new TransitionModel((inputTransitionDuration != null &&
                                inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1),
                                transitionComboBox.getValue().getId()));
                    }
                }

                mActionModel.setCategory(categoryComboBox.getValue());

                mListener.onNewActionAcceptClicked(mActionModel);

                closeDialogWindow();
            }
        };
    }

    /**
     * Method that implements the positive confirmation to cancel the action edition
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
