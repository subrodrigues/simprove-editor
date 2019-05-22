
/*
 * Created by Filipe André Rodrigues on 28-02-2019 18:10
 */

package ui.scenario.action;

import com.jfoenix.controls.*;
import dao.model.*;
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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import ui.scenario.signal.EditSignalViewController;
import ui.scenario.signal.NewSignalViewController;
import ui.widgets.AutoCompleteComboBoxListener;
import ui.widgets.JFXNumericTextField;
import ui.widgets.grid.TextableColorGridCell;
import utils.ConstantUtils;
import utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditActionViewController implements NewSignalViewController.OnNewSignalClickListener,
        EditSignalViewController.OnEditSignalClickListener,
        TextableColorGridCell.OnTextableColorGridClickListener {
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

    @FXML
    private StackPane signalsRootPane;

    @FXML
    private JFXButton addSignalButton;

    @FXML
    private JFXToggleButton isComplActionToggleBtn;

    @FXML
    private ToggleGroup behaviorToggleGroup;

    // Private variables
    private ActionModel mActionModel;
    private OnScenarioEditActionClickListener mListener;
    private int mActionId = -1;

    private List<SignalModel> mActionSignals;

    // Available Signals
    private List<SignalTemplateModel> mSignalTypes;

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
    public EditActionViewController(ActionModel action, List<StateModel> states, List<TypeModel> actionTypes, List<SignalTemplateModel> signalTypes, OnScenarioEditActionClickListener listener) {
        this.mListener = listener;
        this.mSignalTypes = signalTypes;

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
        this.mActionSignals = new ArrayList<SignalModel>();
        this.mActionSignals = action.getResults();

        this.mActionModel = action;

        setupSignalsGrid(action.getResults());
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
        this.categoryComboBox.getSelectionModel().select(mActionModel.getCategory());

        // Set Complementary Action flag
        this.isComplActionToggleBtn.setSelected(mActionModel.getIsComplActionToggleBtn() == 0);

        // Set behavior option
        this.behaviorToggleGroup.getToggles().forEach(toggle -> {
            if(((JFXRadioButton)toggle).getText().equals(mActionModel.getBehavior())) {
                toggle.setSelected(true);
            }
        });

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
                mActionModel.setResults(mActionSignals);

                // Set the complementary action flag
                mActionModel.setIsComplActionToggleBtn(isComplActionToggleBtn.isSelected() ? 0 : 1);

                // Set Behavior
                mActionModel.setBehavior(((JFXRadioButton)behaviorToggleGroup.getSelectedToggle()).getText());

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
        Stage stage = (Stage) editActionRoot.getScene().getWindow();

        NewSignalViewController newSignalDialog = new NewSignalViewController(signals, this.mActionSignals.size(), this);

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
        Stage stage = (Stage) editActionRoot.getScene().getWindow();

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
        this.mActionSignals.add(newSignalModel);
        this.addSignalToGridView(newSignalModel);
    }

    @Override
    public void onEditSignalAcceptClicked(SignalModel editedSignalModel) {
        if(this.mActionSignals.contains(editedSignalModel)){
            this.mActionSignals.set(this.mActionSignals.indexOf(editedSignalModel), editedSignalModel);

            updateGridViewSignal(editedSignalModel);
        }
    }

    @Override
    public void onSignalDeleteClicked(SignalModel signal) {
        int indexToRemove = this.mActionSignals.indexOf(signal);

        if(indexToRemove != -1) {
            this.mActionSignals.remove(indexToRemove); // clean data
            this.removeGridViewSignal(signal); // Refresh UI
        }
    }

    @Override
    public void onSignalGridItemClick(SignalModel clickedItem) {
        showEditSignalDialog(clickedItem, this.mSignalTypes);
    }

}
