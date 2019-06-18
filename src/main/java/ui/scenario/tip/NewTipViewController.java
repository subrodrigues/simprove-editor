/*
 * Created by Filipe André Rodrigues on 04-06-2019 20:23
 */

package ui.scenario.tip;

import com.jfoenix.controls.*;
import dao.model.ActionModel;
import dao.model.ActorModel;
import dao.model.TipModel;
import dao.model.TypeModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.widgets.JFXNumericTextField;
import ui.widgets.MultiSelectListController;

import java.io.IOException;
import java.util.List;

public class NewTipViewController implements MultiSelectListController.OnMultiSelectListClickListener {
    // UI Bind variables
    @FXML
    protected StackPane newTipRoot;

    @FXML
    protected JFXComboBox<TypeModel> actorTypeComboBox;

    @FXML
    protected JFXTextField actorCustomName;

    @FXML
    protected JFXTextArea inputMessage;

    @FXML
    protected JFXNumericTextField durationTime;

    @FXML
    protected JFXNumericTextField activationTime;

    @FXML
    private JFXButton acceptButton;

    @FXML
    protected JFXButton cancelButton;

    @FXML
    private JFXButton tipConditions;

    // Private variables
    protected TipModel mTipModel;
    protected OnNewTipClickListener mListener;
    private int mStateId = -1;

    private List<TypeModel> mActorTypes;

    // Current Actions (for conditions)
    private List<ActionModel> mCurrentActions;

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
     * @param currentActions
     * @param listener
     */
    public NewTipViewController(List<TypeModel> actorTypes, int id, List<ActionModel> currentActions, OnNewTipClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewTipDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newTipRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mTipModel = new TipModel(id);

        setupUI(actorTypes, currentActions);
        setButtonActions();
    }

    protected void setupUI(List<TypeModel> actorTypes, List<ActionModel> currentActions) {
        this.mActorTypes = actorTypes;
        this.mCurrentActions = currentActions;

        // Init Actor Types ComboBox
        this.actorTypeComboBox.getItems().add(new TypeModel(-1, -1,"NONE"));
        this.actorTypeComboBox.getItems().addAll(this.mActorTypes);
        this.actorTypeComboBox.getSelectionModel().select(0);

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

        tipConditions.setOnAction(getEditTipConditionsClickListener());
    }

    /**
     * Method that sets the button action controls
     */
    protected void setButtonActions() {
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
     * Method that implements the Accept new Tip click listener behavior.
     * It updates the mTipModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getEditTipConditionsClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showEditConditions();
            }
        };
    }

    /**
     * Method that closes this DialogWindow view
     */
    protected void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) newTipRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that shows the edit action conditions window
     * TODO: Customize with actions list, window and tab titles
     */
    private void showEditConditions() {
        Stage stage = (Stage) newTipRoot.getScene().getWindow();

        MultiSelectListController multiSelectList = new MultiSelectListController(
                "Select Conditions",
                "Actions Required",
                "Actions Missing",
                this.mCurrentActions,
                this.mTipModel.getActionsDone(),
                this.mTipModel.getActionsTodo(),
                this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(multiSelectList.getItemRoot());

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
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
                mTipModel.setActor(new ActorModel(actorCustomName.getText(),
                        actorTypeComboBox.getSelectionModel().getSelectedItem()));

                mTipModel.setDuration((durationTime != null &&
                        durationTime.getLength() > 0 ? Integer.valueOf(durationTime.getText()) : 0));

                mTipModel.setTime((activationTime != null &&
                        activationTime.getLength() > 0 ? Integer.valueOf(activationTime.getText()) : -1));

                mTipModel.setMessage(inputMessage.getText());

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
    protected EventHandler<ActionEvent> getCancelClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                closeDialogWindow();
            }
        };
    }

    /**
     * On multi select list Apply click.
     * This method deals with templates, since MultiSelectListController is a generic class.
     *
     * @param firstListIds
     * @param secondListIds
     * @param <T>
     */
    public <T> void onMultiSelectListApplyClick(List<T> firstListIds, List<T> secondListIds) {
        this.mTipModel.setActionsDone((List<ActionModel>) firstListIds);
        this.mTipModel.setActionsTodo((List<ActionModel>) secondListIds);
    }
}
