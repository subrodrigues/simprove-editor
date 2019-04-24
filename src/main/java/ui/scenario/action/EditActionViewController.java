
/*
 * Created by Filipe André Rodrigues on 28-02-2019 18:10
 */

package ui.scenario.action;

import com.jfoenix.controls.*;
import com.sun.corba.se.impl.orbutil.closure.Constant;
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
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.widgets.AutoCompleteComboBoxListener;
import ui.widgets.JFXNumericTextField;
import utils.ConstantUtils;
import utils.TextUtils;

import java.io.IOException;
import java.util.List;

public class EditActionViewController {
    // UI Bind variables
    @FXML
    private StackPane editActionRoot;

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
    private JFXButton deleteButton;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    // Private variables
    private ActionModel mActionModel;
    private OnScenarioEditActionClickListener mListener;
    private int mActionId = -1;

    public interface OnScenarioEditActionClickListener {
        void onActionEditApplyClicked(ActionModel newActionModel);

        void onActionDeleteClicked(int actionId);
    }

    public EditActionViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditActionDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            editActionRoot = fxmlLoader.load();
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
    public EditActionViewController(ActionModel action, List<StateModel> states, List<TypeModel> actionTypes, OnScenarioEditActionClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditActionDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            editActionRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupAction(action);
        setupUI(states, actionTypes);
    }

    private void setupAction(ActionModel action) {
        this.mActionModel = action;
    }

    private void setupUI(List<StateModel> states, List<TypeModel> actionTypes) {
        this.actionTypeComboBox.setValue(new TypeModel(-1, -1, mActionModel.getName()));

        this.actionTypeComboBox.getItems().addAll(actionTypes);
        new AutoCompleteComboBoxListener<>(this.actionTypeComboBox);

        // Init Transition ComboBox
        this.transitionComboBox.getItems().add(new StateModel(-1, "NONE"));
        this.transitionComboBox.getItems().addAll(states);

        this.categoryComboBox.getItems().addAll(ConstantUtils.requestActionCategories());

        // Set selected Transition
        if (mActionModel.getTransition() != null) {
            this.transitionComboBox.getSelectionModel().select(
                    new StateModel(this.mActionModel.getTransition().getStateId(), ""));
            this.inputTransitionDuration.setText(mActionModel.getTransition().getDuration() + "");
        } else {
            // No defined transition on creation
            this.transitionComboBox.getSelectionModel().select(0);
            this.inputTransitionDuration.setDisable(true);
        }

        // Set selected Category
        this.categoryComboBox.getSelectionModel().select(mActionModel.getCategory().getId());

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

//        this.inputName.focusedProperty().addListener((o, oldVal, newVal) -> {
//            if (!newVal) {
//                this.inputName.validate();
//            }
//        });

        this.actionTypeComboBox.getEditor().textProperty()
                .addListener(TextUtils.getComboBoxTextInputMaxCharactersListener(this.actionTypeComboBox));

        this.actionTypeComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                this.actionTypeComboBox.validate();
            }
        });

        this.applyButton.disableProperty().bind(
                Bindings.or(inputTransitionDuration.textProperty().isEmpty(),
                        Bindings.or(this.actionTypeComboBox.getEditor().textProperty().isEmpty(),
                            this.categoryComboBox.valueProperty().isNull()))
        );

        this.inputTransitionDuration.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!inputTransitionDuration.isDisabled() && !newVal) {
                this.inputTransitionDuration.validate();
            }
        });

        this.applyButton.setOnAction(getApplyClickListener());

        this.deleteButton.setOnAction(getDeleteClickListener());

        this.cancelButton.setOnAction(getCancelClickListener());
        //TODO
    }

    /**
     * Method that returns the current Transition duration in case it is defined, -1 otherwise.
     *
     * @return duration value or -1
     */
    private int getCurrentTransitionDuration() {
        return inputTransitionDuration != null && inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1;
    }

    public StackPane getEditActionItemRootDialog() {
        return editActionRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) editActionRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that implements the Apply changes click listener behavior.
     * It updates the mActionModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getApplyClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // Set Action name
//                mActionModel.setName(inputName.getText());

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


                mListener.onActionEditApplyClicked(mActionModel);
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
                // Set state (type) name
                layout.setBody(new Label("Are you sure you want to delete " + actionTypeComboBox.getEditor().textProperty().getValue() + "? \n"));

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
     * Method that implements the positive confirmation to delete the action.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getDeletePositiveConfirmationClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mListener.onActionDeleteClicked(mActionModel.getId());
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
