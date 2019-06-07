/*
 * Created by Filipe André Rodrigues on 04-06-2019 20:23
 */

package ui.scenario.tip;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import dao.model.TipModel;
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
import ui.widgets.JFXNumericTextField;

import java.io.IOException;
import java.util.List;

public class NewTipViewController {
    // UI Bind variables
    @FXML
    private StackPane newTipRoot;

    @FXML
    private JFXComboBox<TypeModel> actorTypeComboBox;

    @FXML
    private JFXTextField actorCustomName;

    @FXML
    private JFXTextArea inputMessage;

    @FXML
    private JFXNumericTextField duration;

    @FXML
    private JFXNumericTextField activationTime;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private JFXButton cancelButton;

    // Private variables
    private TipModel mTipModel;
    private OnNewTipClickListener mListener;
    private int mStateId = -1;

    private List<TypeModel> mActorTypes;

    public interface OnNewTipClickListener {
        void onNewTipAcceptClicked(TipModel newTipModel);
    }

    public NewTipViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewTipDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newTipRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor with respective click listener.
     *
     * @param actorTypes
     * @param listener
     */
    public NewTipViewController(List<TypeModel> actorTypes, int id, OnNewTipClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewTipDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newTipRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mTipModel = new TipModel(id);

        setupUI(actorTypes);
    }

    private void setupUI(List<TypeModel> actorTypes) {
        this.mActorTypes = actorTypes;

        // Init Actor Types ComboBox
        this.actorTypeComboBox.getItems().add(new TypeModel(-1, -1,"NONE"));
        this.actorTypeComboBox.getItems().addAll(this.mActorTypes);

        /*
         * Set Listeners and Bindings
         */
        this.actorTypeComboBox.valueProperty().addListener(new ChangeListener<TypeModel>() {
            @Override
            public void changed(ObservableValue<? extends TypeModel> observable, TypeModel oldValue, TypeModel newValue) {
                if (newValue.getId() == -1) {
                    if (actorCustomName.getText().isEmpty()) {
                        actorCustomName.clear();
                }
                    actorCustomName.setDisable(true);
                } else {
                    actorCustomName.setDisable(false);
                }
            }
        });
        actorCustomName.setDisable(true);

        this.acceptButton.setDisable(true);
        this.acceptButton.disableProperty().bind(
                Bindings.isEmpty(this.inputMessage.textProperty()));

        this.cancelButton.setOnAction(getCancelClickListener());
        this.acceptButton.setOnAction(getNewTipAcceptClickListener());
    }

    /**
     * Method that returns the root view
     *
     * @return StacePane (root view)
     */
    public StackPane getNewTipItemRootDialog() {
        return newTipRoot;
    }

    /**
     * Method that returns the root view with a specific width and height
     *
     * @param width
     * @param height
     * @return StacePane (root view)
     */
    public StackPane getNewTipItemRootDialog(double width, double height) {
        newTipRoot.setPrefWidth(width);
        newTipRoot.setPrefHeight(height);

        return newTipRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) newTipRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that implements the Accept new Tip click listener behavior.
     * It updates the mTipModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getNewTipAcceptClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // Set state (type) name
                // TODO: Update tipmodel

                mListener.onNewTipAcceptClicked(mTipModel);

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
