/*
 * Created by Filipe André Rodrigues on 17-06-2019 11:14
 */

package ui.scenario.tip;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import dao.model.ActionModel;
import dao.model.ActorModel;
import dao.model.TipModel;
import dao.model.TypeModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EditTipViewController extends NewTipViewController {

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton acceptButton;

    protected OnEditTipClickListener mListener;

    public interface OnEditTipClickListener {
        void onEditTipAcceptClicked(TipModel mTipModel);
        void onEditTipDeleteClicked(TipModel mTipModel);
    }

    /**
     * Constructor with respective click listener.
     *
     * @param actorTypes
     * @param currentActions
     * @param listener
     */
    public EditTipViewController(TipModel tipModel, List<TypeModel> actorTypes, List<ActionModel> currentActions, OnEditTipClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditTipDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            this.newTipRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupUI(actorTypes, currentActions);
        setupTip(tipModel);
        setButtonActions();
    }

    /**
     * Method that sets the button action controls
     */
    @Override
    protected void setButtonActions() {
        this.acceptButton.setDisable(true);
        this.acceptButton.disableProperty().bind(
                Bindings.isEmpty(this.inputMessage.textProperty()));

        this.deleteButton.setOnAction(getDeleteClickListener());
        this.cancelButton.setOnAction(getCancelClickListener());
        this.acceptButton.setOnAction(getEditTipAcceptClickListener());
    }

    private void setupTip(TipModel tipModel) {
        this.mTipModel = tipModel;

        this.actorTypeComboBox.setValue(this.mTipModel.getActor().getType());
        this.actorCustomName.setText(this.mTipModel.getActor().getName());
        this.durationTime.setText(String.valueOf(this.mTipModel.getDuration()));

        if(this.mTipModel.getTime() != -1)
            this.activationTime.setText(String.valueOf(this.mTipModel.getTime()));

        this.inputMessage.setText(mTipModel.getMessage());
    }

    public StackPane getEditSignalItemRootDialog() {
        return getNewTipItemRootDialog();
    }

    /**
     * Method that implements the Accept new Tip click listener behavior.
     * It updates the mTipModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getEditTipAcceptClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mTipModel.setActor(new ActorModel(actorCustomName.getText(),
                        actorTypeComboBox.getSelectionModel().getSelectedItem()));

                mTipModel.setDuration((durationTime != null &&
                        durationTime.getLength() > 0 ? Integer.valueOf(durationTime.getText()) : 0));

                mTipModel.setTime((activationTime != null &&
                        activationTime.getLength() > 0 ? Integer.valueOf(activationTime.getText()) : -1));

                mTipModel.setMessage(inputMessage.getText());

                mListener.onEditTipAcceptClicked(mTipModel);

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
                layout.setBody(new Label("Are you sure you want to delete this Tip? \n"));

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
     * Method that implements the positive confirmation to delete the tip.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getDeletePositiveConfirmationClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mListener.onEditTipDeleteClicked(mTipModel);
                closeDialogWindow();
            }
        };
    }

}
