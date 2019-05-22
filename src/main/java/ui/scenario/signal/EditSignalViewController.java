/*
 * Created by Filipe André Rodrigues on 07-05-2019 11:41
 */

package ui.scenario.signal;

import com.jfoenix.controls.*;
import dao.model.SignalModel;
import dao.model.SignalTemplateModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.widgets.AutoCompleteComboBoxListener;

import java.io.IOException;
import java.util.List;

public class EditSignalViewController {
    // UI Bind variables
    @FXML
    private StackPane newSignalRoot;

    @FXML
    private JFXComboBox<SignalTemplateModel> signalTypeComboBox;

    @FXML
    private JFXSlider signalNumericValue;

    @FXML
    private Text numericValue;

    @FXML
    private Text numericValueUnit;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private GridPane numericValueContainer;

    @FXML
    private JFXComboBox<String> physicalOptionComboBox;

    // Private variables
    private SignalModel mSignalModel;
    private OnEditSignalClickListener mListener;
    private int mStateId = -1;

    private List<SignalTemplateModel> mSignalTypes;
    private SignalTemplateModel mCurrentItem;


    public interface OnEditSignalClickListener {
        void onEditSignalAcceptClicked(SignalModel signal);

        void onSignalDeleteClicked(SignalModel signal);
    }

    public EditSignalViewController() {
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
    public EditSignalViewController(SignalModel signal, List<SignalTemplateModel> signals, OnEditSignalClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditSignalDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            newSignalRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupSignal(signal);
        setupUI(signals);

        // Set default values
        setDefaultSignalData();
    }

    private void setupSignal(SignalModel signal) {
        this.mSignalModel = signal;
    }

    private void setupUI(List<SignalTemplateModel> signals) {
        this.mSignalTypes = signals;

        // Init Signals ComboBox
        this.signalTypeComboBox.getItems().addAll(this.mSignalTypes);
        new AutoCompleteComboBoxListener<>(this.signalTypeComboBox);
        this.signalTypeComboBox.setEditable(true);

        /*
         * Set Listeners and Bindings
         */
        this.signalTypeComboBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mCurrentItem = mSignalTypes.get((Integer) newValue);

                showSignalValueData(null);

                //TODO: Check type and create value input
            }
        });

        this.applyButton.setDisable(true);
        this.applyButton.disableProperty().bind(
                Bindings.isEmpty(this.signalTypeComboBox.getEditor().textProperty()));

        this.cancelButton.setOnAction(this.getCancelClickListener());
        this.applyButton.setOnAction(this.getEditSignalAcceptClickListener());

        this.deleteButton.setOnAction(this.getDeleteClickListener());
    }

    /**
     * Method that sets the default data values
     */
    private void setDefaultSignalData() {
        if(mSignalModel.getTemplate() == null){
            closeDialogWindow();
        }

        mCurrentItem = mSignalModel.getTemplate();

        this.mSignalModel.setType(mCurrentItem.getType());
        this.mSignalModel.setName(mCurrentItem.getName());
        this.signalTypeComboBox.setValue(mCurrentItem);

        showSignalValueData(mSignalModel);
    }

    /**
     * Method that checks current item type and fill the value UI accordingly
     * @param defaultData
     */
    private void showSignalValueData(SignalModel defaultData) {

        if (this.mCurrentItem.isNumericalSignal()) {
            showNumericSignalUI();

            if(defaultData != null)
                this.signalNumericValue.setValue(Double.valueOf(defaultData.getValue()));
        } else {
            showCategoricalSignalUI();

            if(defaultData != null)
                this.physicalOptionComboBox.getSelectionModel().select(defaultData.getValue());
        }

    }

    /**
     * Method that updates the dialog window to the Categorical interface
     */
    private void showCategoricalSignalUI() {
        this.numericValueContainer.setVisible(false);
        this.physicalOptionComboBox.setVisible(true);

        this.physicalOptionComboBox.getItems().clear();
        this.physicalOptionComboBox.getItems().addAll(mCurrentItem.getPhysicalOptions());
    }

    /**
     * Method that updates the dialog window to the Numerical interface
     */
    private void showNumericSignalUI() {
        this.numericValueContainer.setVisible(true);
        this.physicalOptionComboBox.setVisible(false);

        numericValueUnit.setText(" ".concat(mCurrentItem.getUnit()));

        signalNumericValue.setMin(mCurrentItem.getMinRange());
        signalNumericValue.setMax(mCurrentItem.getMaxRange());

        // Binds the label value to the slider changes
        String granularity = String.valueOf(mCurrentItem.getGranularity());
        String fractionalStr = granularity.substring(granularity.indexOf('.') + 1);

        numericValue.textProperty().bind(
                Bindings.format(
                        "%." + fractionalStr + "f", // "%.2f"
                        signalNumericValue.valueProperty()
                )
        );

        signalNumericValue.requestLayout();
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
     * Method that implements the Accept edition of the Signal click listener behavior.
     * It updates the mSignalModel with new content and returns it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getEditSignalAcceptClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // Set state (type) name
                mSignalModel.setType(mCurrentItem.getType());
                mSignalModel.setName(signalTypeComboBox.getEditor().textProperty().getValue());

                if(mCurrentItem.isNumericalSignal()){
                    mSignalModel.setValue(String.valueOf(signalNumericValue.getValue()));
                } else {
                    mSignalModel.setValue(String.valueOf(physicalOptionComboBox.getValue()));
                }

                mSignalModel.setTemplate(mCurrentItem);

                mListener.onEditSignalAcceptClicked(mSignalModel);
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
                layout.setBody(new Label("Are you sure you want to delete " + mSignalModel.getName() + "? \n"));

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
     * Method that implements the positive confirmation to delete the signal.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getDeletePositiveConfirmationClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mListener.onSignalDeleteClicked(mSignalModel);
                closeDialogWindow();
            }
        };
    }

}
