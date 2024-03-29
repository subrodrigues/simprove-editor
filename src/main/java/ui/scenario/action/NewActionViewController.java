/*
 * Created by Filipe André Rodrigues on 13-03-2019 19:32
 */

package ui.scenario.action;

import com.jfoenix.controls.*;
import dao.model.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import ui.scenario.signal.EditSignalViewController;
import ui.scenario.signal.NewSignalViewController;
import ui.widgets.*;
import ui.widgets.grid.SignalTextableColorGridCell;
import utils.LogicUtils;
import utils.TextUtils;
import utils.WidgetUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewActionViewController implements NewSignalViewController.OnNewSignalClickListener,
        EditSignalViewController.OnEditSignalClickListener,
        SignalTextableColorGridCell.OnTextableColorGridClickListener, MultiSelectListController.OnMultiSelectListClickListener, SelectListController.OnMultiSelectListClickListener {
    // UI Bind variables
    @FXML
    private StackPane newActionRoot;

    @FXML
    private JFXComboBox<TypeModel> actionTypeComboBox;

    @FXML
    private JFXComboBox<StateModel> transitionComboBox;

    @FXML
    private JFXDecimalTextField inputEffectTime;

    @FXML
    private JFXNumericTextField inputUsageLimit;

    @FXML
    private JFXComboBox<TypeModel> categoryComboBox;

    @FXML
    private JFXComboBox<TypeModel> subCategoryComboBox;

    @FXML
    private JFXButton acceptButton;

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

    @FXML
    private JFXButton scoreConditions;

    @FXML
    private JFXButton complementaryActions;

    @FXML
    private JFXDecimalTextField inputLostValue;

    @FXML
    private JFXDecimalTextField inputLossOvertime;

    @FXML
    private JFXDecimalTextField inputAdminTime;

    @FXML
    private JFXComboBox<TypeModel> actorErrorMsgComboBox;

    @FXML
    private JFXTextField actorErrorMsgCustomName;

    @FXML
    private JFXTextArea inputErrorMsg;

    // Private variables
    private ActionModel mActionModel;
    private OnScenarioNewActionClickListener mListener;
    private int mStateId = -1;
    private List<SignalModel> mActionSignals;
    private List<TypeModel> mActorTypes;

    // Current States (for conditions)
    private List<StateModel> mCurrentStates;
    private List<ActionModel> mCurrentActions;

    // Available Signals
    private List<SignalTemplateModel> mSignalTypes;


    // Data related to later update of the complementary actions (when the user presses "Apply")
    private List<ActionModel> mSelectedComplementaryActions;
    private List<ActionModel> mPreviousComplementaryActions;

    public interface OnScenarioNewActionClickListener {
        void onNewActionAcceptClicked(ActionModel newActionModel);

        void updateComplementaryActions(ActionModel actionToAdd, List<ActionModel>  previousActions, List<ActionModel> actionsToUpdate);
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
    public NewActionViewController(List<ActionModel> actions, List<StateModel> states,
                                   List<TypeModel> actionTypes, List<TypeModel> actionCategories,
                                   List<TypeModel> actionSubCategories, List<SignalTemplateModel> signalTypes,
                                   List<TypeModel> actorTypes, OnScenarioNewActionClickListener listener) {
        this.mListener = listener;
        this.mSignalTypes = signalTypes;
        this.mActorTypes = actorTypes;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/NewActionDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newActionRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupAction(actions, states);
        setupUI(states, actionTypes, actionCategories, actionSubCategories);
    }


    private void setupAction(List<ActionModel> actions, List<StateModel> states) {
        this.mActionSignals = new ArrayList<SignalModel>();
        this.mActionModel = new ActionModel(actions.size());
        this.mCurrentStates = states;

        List<ActionModel> two = new ArrayList(actions);
        this.mCurrentActions = two;

        this.mSelectedComplementaryActions = new ArrayList<>();
        this.mPreviousComplementaryActions = new ArrayList<>();

        setupSignalsGrid();
    }

    private void setupUI(List<StateModel> states, List<TypeModel> actionTypes,
                         List<TypeModel> actionCategories, List<TypeModel> actionSubCategories) {

        // Init Transition ComboBox
        this.transitionComboBox.getItems().add(new StateModel(-1, "NONE"));
        this.transitionComboBox.getItems().addAll(states);

        this.categoryComboBox.getItems().addAll(actionCategories);
        this.subCategoryComboBox.getItems().addAll(actionSubCategories);


        this.actionTypeComboBox.getItems().addAll(actionTypes);
        new AutoCompleteComboBoxListener<>(this.actionTypeComboBox);

        /*
         * Set Listeners and Bindings
         */
        this.actionTypeComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                this.actionTypeComboBox.validate();
            }
        });

        this.actionTypeComboBox.getEditor().textProperty()
                .addListener(TextUtils.getComboBoxTextInputMaxCharactersListener(this.actionTypeComboBox));

        this.categoryComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                this.categoryComboBox.validate();
            }
        });

        // Init Actor Types ComboBox
        this.actorErrorMsgComboBox.getItems().add(new TypeModel(-1, -1,"NONE"));
        this.actorErrorMsgComboBox.getItems().addAll(this.mActorTypes);
        this.actorErrorMsgComboBox.getSelectionModel().select(0);

        /*
         * Set Listeners and Bindings
         */
        this.actorErrorMsgComboBox.valueProperty().addListener(new ChangeListener<TypeModel>() {
            @Override
            public void changed(ObservableValue<? extends TypeModel> observable, TypeModel oldValue, TypeModel newValue) {
                if (newValue.getId() == -1) {
                    if (actorErrorMsgCustomName.getText().isEmpty()) {
                        actorErrorMsgCustomName.clear();
                    }
                    actorErrorMsgCustomName.setDisable(true);
                } else {
                    actorErrorMsgCustomName.setDisable(false);
                }
            }
        });
        actorErrorMsgCustomName.setDisable(true);


        /** Accept Button binding conditions */
        BooleanBinding booleanBinding = this.actionTypeComboBox.getSelectionModel().selectedItemProperty().isNull()
                .or(this.transitionComboBox.getSelectionModel().selectedItemProperty().isNull())
                .or(this.categoryComboBox.getSelectionModel().selectedItemProperty().isNull());
        this.acceptButton.disableProperty().bind(booleanBinding);
        /** Accept Button binding conditions */


        this.acceptButton.setOnAction(getNewActionAcceptClickListener());

        this.cancelButton.setOnAction(getCancelClickListener());

        this.scoreConditions.setOnAction(getEditTipConditionsClickListener());

        this.complementaryActions.setOnAction(getComplementaryActionsClickListener());

        // Tooltips added
        Tooltip scoreButtonDescription = new Tooltip("Conditions required to avoid score loss");
        this.scoreConditions.setTooltip(scoreButtonDescription);

        Tooltip lossOvertimeDescription = new Tooltip("Ammount of score loss per second");
        this.inputLossOvertime.setTooltip(lossOvertimeDescription);
    }

    /**
     * // TODO: Refactor this signals behavior into a parent class
     */
    private void setupSignalsGrid() {
        final ObservableList<SignalModel> list = FXCollections.<SignalModel>observableArrayList();

        GridView<SignalModel> signalGrid = new GridView<>(list);
        signalGrid.setHorizontalCellSpacing(-4); //horizontal gap in pixels => that's what you are asking for
        signalGrid.setVerticalCellSpacing(-4); //vertical gap in pixels
        signalGrid.setPadding(new Insets(6, 6, 6, 6)); //margins around the whole grid

        //(top/right/bottom/left)
        SignalTextableColorGridCell.OnTextableColorGridClickListener context = this;
        signalGrid.setCellFactory(new Callback<GridView<SignalModel>, GridCell<SignalModel>>() {
            @Override
            public GridCell<SignalModel> call(GridView<SignalModel> arg0) {
                return new SignalTextableColorGridCell(context);
            }
        });

        this.signalsRootPane.getChildren().add(signalGrid);
        this.addSignalButton.setOnAction(getNewSignalClickListener(this.mSignalTypes));
    }

    /**
     * Method that updates the GridView UI. Adding a new Signal
     * // TODO: Refactor this signals behavior into a parent class
     *
     * @param signal
     */
    private void addSignalToGridView(SignalModel signal) {
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().add(signal);
    }

    /**
     * Method that updates the GridView specified item.
     * // TODO: Refactor this signals behavior into a parent class
     *
     * @param editedSignalModel
     */
    private void updateGridViewSignal(SignalModel editedSignalModel) {
        final int index = ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().indexOf(editedSignalModel);
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().set(index, editedSignalModel);
    }

    /**
     * Method that removes the GridView specified item.
     * // TODO: Refactor this signals behavior into a parent class
     *
     * @param signal
     */
    private void removeGridViewSignal(SignalModel signal) {
//        final int index = ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().indexOf(signal);
        ((GridView<SignalModel>) this.signalsRootPane.getChildren().get(0)).getItems().remove(signal);
    }

    /**
     * Method that returns the current effect time in case it is defined, -1 otherwise.
     *
     * @return duration value or 0
     */
    private float getCurrentEffectDuration() {
        return inputEffectTime != null && inputEffectTime.getLength() > 0 ? Float.valueOf(inputEffectTime.getText()) : 0;
    }

    /**
     * Method that returns the current adming time in case it is defined, -1 otherwise.
     *
     * @return duration value or 0
     */
    private float getCurrentAdminTime() {
        return inputAdminTime != null && inputAdminTime.getLength() > 0 ? Float.valueOf(inputAdminTime.getText()) : 0;
    }

    /**
     * Method that returns the current usage limit in case it is defined, 0 otherwise.
     *
     * @return duration value or 0
     */
    private int getUsageLimit() {
        return inputUsageLimit != null && inputUsageLimit.getLength() > 0 ? Integer.valueOf(inputUsageLimit.getText()) : 0;
    }

    /**
     * Method that returns the current Immediate Score Loss in case it is defined, 0 otherwise.
     *
     * @return duration value or 0
     */
    private float getScoreLostValue() {
        return inputLostValue != null && inputLostValue.getLength() > 0 ? Float.valueOf(inputLostValue.getText()) : 0;
    }

    /**
     * Method that returns the current Immediate Score Loss in case it is defined, 0 otherwise.
     *
     * @return duration value or 0
     */
    private float getScoreOvertimeLoss() {
        return inputLossOvertime != null && inputLossOvertime.getLength() > 0 ? Float.valueOf(inputLossOvertime.getText()) : 0.0f;
    }

    /**
     * Method that returns the root view with a specific width and height
     *
     * @param width
     * @param height
     * @return StacePane (root view)
     */
    public StackPane getNewActionItemRootDialog(double width, double height) {
        newActionRoot.setPrefWidth(width);
        newActionRoot.setPrefHeight(height);

        return newActionRoot;
    }

    /**
     * Method that returns the root view
     *
     * @return StacePane (root view)
     */
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
                if (actionTypeComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    actionTypeComboBox.getSelectionModel().clearSelection();
                    WidgetUtils.warningMessageAlert((Stage) acceptButton.getScene().getWindow(),
                            "Warning",
                            "Please select a valid Action Type from the list\n",
                            "Ok");
                    return;
                }

                // Set state (type) name
                mActionModel.setName(actionTypeComboBox.getEditor().textProperty().getValue());

                // Set/Update transition model
                if (mActionModel.getTransition() != null) {

                    // If we removed the Transition
                    if (transitionComboBox.getValue().getId() == -1) {
                        mActionModel.setTransition(null);
                    } else { // If we updated the existing Transition
                        mActionModel.getTransition().setStateId(transitionComboBox.getValue().getId());
                    }
                } else { // If we didn't have a transition
                    // And we are adding a Transition
                    if (transitionComboBox.getValue() != null && transitionComboBox.getValue().getId() != -1) {
                        mActionModel.setTransition(new TransitionModel(transitionComboBox.getValue().getId()));
                    }
                }

                // Set Action Type
                TypeModel actionType = LogicUtils.getActionTypeByName(mActionModel.getName(), actionTypeComboBox.getItems());
                mActionModel.setType(actionType);

                // Set Categories
                mActionModel.setCategory(categoryComboBox.getValue());
                mActionModel.setSubCategory(subCategoryComboBox.getValue());

                mActionModel.setResults(mActionSignals);

                mActionModel.setEffectTime(getCurrentEffectDuration());
                mActionModel.setAdminTime(getCurrentAdminTime());

                mActionModel.setUsageLimit(getUsageLimit());

                // Set Behavior
                mActionModel.setBehavior(((JFXRadioButton) behaviorToggleGroup.getSelectedToggle()).getText());

                mActionModel.getScore().setScoreLost(getScoreLostValue());
                mActionModel.getScore().setLossOvertime(getScoreOvertimeLoss());

                // Set Actor and Error Msg
                mActionModel.setActorErrorMessage(new ActorModel(actorErrorMsgCustomName.getText(),
                        actorErrorMsgComboBox.getSelectionModel().getSelectedItem()));
                mActionModel.setErrorMessage(inputErrorMsg.getText());

                mListener.updateComplementaryActions(mActionModel, mPreviousComplementaryActions , mSelectedComplementaryActions);
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

    /**
     * Method that shows the add new signal window
     * // TODO: Refactor this signals behavior into a parent class
     *
     * @param signals
     */
    private void showNewSignalDialog(List<SignalTemplateModel> signals) {
        Stage stage = (Stage) newActionRoot.getScene().getWindow();

        NewSignalViewController newSignalDialog = new NewSignalViewController(signals, this.mActionSignals.size(), this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(newSignalDialog.getNewSignalItemRootDialog(stage.getWidth() / 2, stage.getHeight() / 2));

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Method that shows the edit signal window
     * // TODO: Refactor this signals behavior into a parent class
     *
     * @param signals
     */
    private void showEditSignalDialog(SignalModel signalToEdit, List<SignalTemplateModel> signals) {
        Stage stage = (Stage) newActionRoot.getScene().getWindow();

        EditSignalViewController newSignalDialog = new EditSignalViewController(signalToEdit, signals, this);

        JFXAlert dialog = new JFXAlert(stage); // get window context

        // TODO: Set window current size with a vertical/horizontal threshold
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.setContent(newSignalDialog.getNewSignalItemRootDialog(stage.getWidth() / 2, stage.getHeight() / 2));

        dialog.setResizable(true);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(0, 50, 100, 0.5)");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.show();
    }

    /**
     * Method that implements the action behavior to launch a new signal window
     * // TODO: Refactor this signals behavior into a parent class
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

    /**
     * Method that shows the edit action conditions window
     * TODO: Customize with actions list, window and tab titles
     */
    private void showEditConditions() {
        Stage stage = (Stage) newActionRoot.getScene().getWindow();

        MultiSelectListController multiSelectList = new MultiSelectListController(
                "Select Required States",
                "Starting States",
                "Ending States",
                this.mCurrentStates,
                this.mActionModel.getScore().getStartStates(),
                this.mActionModel.getScore().getEndStates(),
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
     * Method that shows the edit action conditions window
     * TODO: Customize with actions list, window and tab titles
     */
    private void showComplementaryActions() {
        Stage stage = (Stage) newActionRoot.getScene().getWindow();

        SelectListController multiSelectList = new SelectListController(
                "Complementary Actions",
                this.mCurrentActions,
                this.mActionModel.getComplementaryActions(),
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
     * Method that launches the ScoreConditions dialog.
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
     * Method that launches the ScoreConditions dialog.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getComplementaryActionsClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showComplementaryActions();
            }
        };
    }


    /********************************************************************************************************************
     * CALLBACKS INTERFACE                                                                                              *
     ********************************************************************************************************************/

    @Override
    public void onEditSignalAcceptClicked(SignalModel editedSignalModel) {
        if (this.mActionSignals.contains(editedSignalModel)) {
            this.mActionSignals.set(this.mActionSignals.indexOf(editedSignalModel), editedSignalModel);

            updateGridViewSignal(editedSignalModel);
        }
    }

    @Override
    public void onSignalDeleteClicked(SignalModel signal) {
        int indexToRemove = this.mActionSignals.indexOf(signal);

        if (indexToRemove != -1) {
            this.mActionSignals.remove(indexToRemove); // clean data
            this.removeGridViewSignal(signal); // Refresh UI
        }
    }

    @Override
    public void onNewSignalAcceptClicked(SignalModel newSignalModel) {
        this.mActionSignals.add(newSignalModel);

        this.addSignalToGridView(newSignalModel);
    }

    @Override
    public void onSignalGridItemClick(SignalModel clickedItem) {
        showEditSignalDialog(clickedItem, this.mSignalTypes);
    }

    /**
     * On multi select list Apply click.
     * This method deals with templates, since MultiSelectListController is a generic class.
     *
     * @param firstListIds
     * @param secondListIds
     * @param <T>
     */
    @Override
    public <T> void onMultiSelectListApplyClick(List<T> firstListIds, List<T> secondListIds) {
        List<StateModel> startStates = getInstancedConditions(firstListIds);
        this.mActionModel.getScore().setStartStates(startStates);

        List<StateModel> endStates = getInstancedConditions(secondListIds);
        this.mActionModel.getScore().setEndStates(endStates);
    }

    /**
     * Method that deals with selection of Complementary Actions
     *
     * @param selectedItems
     * @param <T>
     */
    @Override
    public <T> void onSelectListApplyClick(List<T> selectedItems) {
        List<ActionModel> compActions = getInstancedComplementaryActions(selectedItems);

        this.mSelectedComplementaryActions = compActions;
        this.mPreviousComplementaryActions = this.mActionModel.getComplementaryActions();

        this.mActionModel.setComplementaryActions(compActions);
    }

    /**
     * Auxiliar method to onMultiSelectListApplyClick().
     * It creates instanced class types in order to be serialized with success.
     *
     * @param listIds
     * @param <T>
     */
    private <T> List<StateModel> getInstancedConditions(List<T> listIds) {
        List<StateModel> finalStatesList = new ArrayList<>(listIds.size());

        for (int i = 0; i < listIds.size(); i++) {
            StateModel item = ((List<StateModel>) listIds).get(i);

            finalStatesList.add(i, new StateModel(item.getId(), item.getName(), item.getType(), item.getSignals(),
                    item.getTransition(), item.getTips(), item.getIsStartState(), item.getIsEndState()));
        }

        return finalStatesList;
    }

    /**
     * Auxiliar method to onSelectListApplyClick().
     * It creates instanced class types in order to be serialized with success.
     *
     * @param listIds
     * @param <T>
     */
    private <T> List<ActionModel> getInstancedComplementaryActions(List<T> listIds) {
        List<ActionModel> finalActionsList = new ArrayList<>(listIds.size());

        for (int i = 0; i < listIds.size(); i++) {
            ActionModel item = ((List<ActionModel>) listIds).get(i);

            finalActionsList.add(i, new ActionModel(item.getId(), item.getName(), item.getType(), item.getCategory(),
                    item.getSubCategory(), item.getEffectTime(), item.getUsageLimit(), item.getComplementaryActions(),
                    item.getBehavior(), item.getStateConditions(), item.getResults(), item.getTransition(), item.getActorErrorMessage(),
                    item.getErrorMessage(), item.getScore(), item.getAdminTime()));
        }

        return finalActionsList;
    }

}
