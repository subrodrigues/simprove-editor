/*
 * Created by Filipe André Rodrigues on 15-04-2019 18:11
 */

package ui.scenario.signal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import dao.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class NewSignalViewController {
    // UI Bind variables
    @FXML
    private StackPane newSignalRoot;

    @FXML
    private JFXComboBox<SignalTemplateModel> signalTypeComboBox;

    @FXML
    private JFXSlider signalNumericValue;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private JFXButton cancelButton;

    // Private variables
    private SignalModel mSignalModel;
    private OnNewSignalClickListener mListener;
    private int mStateId = -1;

    public interface OnNewSignalClickListener {
        void onNewSignalAcceptClicked(SignalModel newSignalModel);
    }

    public NewSignalViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewSignalDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newSignalRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor with respective click listener.
     *
     * @param signals
     * @param listener
     */
    public NewSignalViewController(List<SignalTemplateModel> signals, int id, OnNewSignalClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewSignalDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newSignalRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mSignalModel = new SignalModel(id);

        setupUI(signals);
    }

    private void setupUI(List<SignalTemplateModel> signals) {

        // Init Signals ComboBox
        this.signalTypeComboBox.getItems().addAll(signals);

        /*
         * Set Listeners and Bindings
         */
        this.signalTypeComboBox.valueProperty().addListener(new ChangeListener<SignalTemplateModel>() {
            @Override
            public void changed(ObservableValue<? extends SignalTemplateModel> observable, SignalTemplateModel oldValue, SignalTemplateModel newValue) {
                if (newValue.getId() == -1) {
                    //TODO: Remove/disable input type
//                    inputTransitionDuration.setDisable(true);
                } else {
                    //TODO: Check type and create value input
                }
            }
        });


//        this.acceptButton.disableProperty().bind(
//                Bindings.or(this.actionTypeComboBox.getEditor().textProperty().isEmpty(),
//                        this.categoryComboBox.valueProperty().isNull())
//        );

        this.cancelButton.setOnAction(getCancelClickListener());

        // TODO


    }

    /**
     * Method that returns the root view
     *
     * @return StacePane (root view)
     */
    public StackPane getNewSignalItemRootDialog() {
        return newSignalRoot;
    }

    /**
     * Method that returns the root view with a specific width and height
     *
     * @param width
     * @param height
     * @return StacePane (root view)
     */
    public StackPane getNewSignalItemRootDialog(double width, double height) {
        newSignalRoot.setPrefWidth(width);
        newSignalRoot.setPrefHeight(height);

        return newSignalRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) newSignalRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that implements the Accept new Signal click listener behavior.
     * It updates the mSignalModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewSignalAcceptClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // Set state (type) name
                mSignalModel.setName(signalTypeComboBox.getEditor().textProperty().getValue());

                // Set/Update transition model
                // TODO: Fill the fields properly
//                if (mActionModel.getTransition() != null) {
//
//                    // If we removed the Transition
//                    if (transitionComboBox.getValue().getId() == -1) {
//                        mActionModel.setTransition(null);
//                    } else { // If we updated the existing Transition
//                        mActionModel.getTransition().setDuration(getCurrentTransitionDuration());
//                        mActionModel.getTransition().setStateId(transitionComboBox.getValue().getId());
//                    }
//                } else { // If we didn't have a transition
//                    // And we are adding a Transition
//                    if (transitionComboBox.getValue() != null && transitionComboBox.getValue().getId() != -1) {
//                        mActionModel.setTransition(new TransitionModel((inputTransitionDuration != null &&
//                                inputTransitionDuration.getLength() > 0 ? Integer.valueOf(inputTransitionDuration.getText()) : -1),
//                                transitionComboBox.getValue().getId()));
//                    }
//                }
//
//                mActionModel.setCategory(categoryComboBox.getValue());

                mListener.onNewSignalAcceptClicked(mSignalModel);

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
