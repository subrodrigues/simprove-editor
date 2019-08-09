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
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui.widgets.AutoCompleteComboBoxListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    @FXML
    private StackPane graphicalValueContainer;

    @FXML
    private LineChart<Number, Number> signalGraphicalLineChart;

    @FXML
    private Text valueTag;

    // Private variables
    private SignalModel mSignalModel;
    private OnEditSignalClickListener mListener;
    private int mStateId = -1;

    private List<SignalTemplateModel> mSignalTypes;
    private SignalTemplateModel mCurrentItem;

    private double mRootWidth;
    private double mRootHeight;
    private XYChart.Series<Number, Number> mGraphicalSeries;

    public interface OnEditSignalClickListener {
        void onEditSignalAcceptClicked(SignalModel signal);

        void onSignalDeleteClicked(SignalModel signal);
    }

    public EditSignalViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditSignalDialog.fxml"));
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
        // TODO: Code refactoring and turn this into a Widget
        this.signalTypeComboBox.setConverter(new StringConverter<SignalTemplateModel>() {

            @Override
            public String toString(SignalTemplateModel object) {
                if (object == null) return null;
                return object.toString();
            }

            @Override
            public SignalTemplateModel fromString(String signalName) {
                return new SignalTemplateModel(-1, signalName);
            }
        });

        this.signalTypeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SignalTemplateModel>() {
            @Override
            public void changed(ObservableValue<? extends SignalTemplateModel> observable, SignalTemplateModel oldValue, SignalTemplateModel newValue) {
                for (SignalTemplateModel signalTemplate : mSignalTypes) {
                    if (signalTemplate.getName().equals(newValue.getName())) {
                        mCurrentItem = signalTemplate;
                        showSignalValueData(null);

                        break;
                    }
                }

                //TODO: Check type and create value input
            }
        });
        // TODO: Code refactoring and turn this into a Widget

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
        if (mSignalModel.getTemplate() == null) {
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
     *
     * @param editedData
     */
    private void showSignalValueData(SignalModel editedData) {

        if (this.mCurrentItem.isNumericalSignal()) {
            showNumericSignalUI();

            if (editedData != null)
                this.signalNumericValue.setValue(Double.valueOf(editedData.getValue()));
        } else if (mCurrentItem.isCategoricalSignal()) {
            showCategoricalSignalUI();

            if (editedData != null)
                this.physicalOptionComboBox.getSelectionModel().select(editedData.getValue());
        } else {
            showGraphicalSignalUI(editedData != null ? editedData.getPlotYValue(): null);
        }
    }

    /**
     * Method that updates the dialog window to the Categorical interface
     */
    private void showCategoricalSignalUI() {
        this.signalGraphicalLineChart.setAnimated(true);

        this.newSignalRoot.setMinWidth(this.mRootWidth);
        this.newSignalRoot.setMinHeight(this.mRootHeight);

        this.valueTag.setVisible(true);
        this.numericValueContainer.setVisible(false);
        this.graphicalValueContainer.setVisible(false);
        this.physicalOptionComboBox.setVisible(true);

        this.physicalOptionComboBox.getItems().clear();
        this.physicalOptionComboBox.getItems().addAll(mCurrentItem.getPhysicalOptions());

        if(mCurrentItem.getPhysicalOptions().size() > 0)
            this.physicalOptionComboBox.getSelectionModel().select(0);
    }

    /**
     * Method that updates the dialog window to the Numerical interface
     */
    private void showNumericSignalUI() {
        this.signalGraphicalLineChart.setAnimated(true);

        this.newSignalRoot.setMinWidth(this.mRootWidth);
        this.newSignalRoot.setMinHeight(this.mRootHeight);

        this.valueTag.setVisible(true);
        this.numericValueContainer.setVisible(true);
        this.graphicalValueContainer.setVisible(false);
        this.physicalOptionComboBox.setVisible(false);

        numericValueUnit.setText(" ".concat(mCurrentItem.getUnit()));

        signalNumericValue.setMin(mCurrentItem.getMinRange());
        signalNumericValue.setMax(mCurrentItem.getMaxRange());

        // TODO: Find a not hammered solution
        if (mCurrentItem.getGranularity() == 0.1f) {
            signalNumericValue.setBlockIncrement(0.1f);
        } else if (mCurrentItem.getGranularity() == 0.2f) {
            signalNumericValue.setBlockIncrement(0.01f);
        } else if (mCurrentItem.getGranularity() == 0.3f) {
            signalNumericValue.setBlockIncrement(0.001f);
        } else if (mCurrentItem.getGranularity() == 0.4f) {
            signalNumericValue.setBlockIncrement(0.0001f);
        } else if (mCurrentItem.getGranularity() == 0.5f) {
            signalNumericValue.setBlockIncrement(0.00001f);
        } else {
            signalNumericValue.setBlockIncrement(1);
        }

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
     * Method that updates the dialog window to the Graphical interface
     *
     * @param plotYSavedData
     */
    private void showGraphicalSignalUI(List<Integer> plotYSavedData) {
        this.signalGraphicalLineChart.setAnimated(true);

        this.newSignalRoot.setMinWidth(this.mRootWidth * NewSignalViewController.GRAPHICAL_WINDOW_MULT_FACTOR);
        this.newSignalRoot.setMinHeight(this.mRootHeight * NewSignalViewController.GRAPHICAL_WINDOW_MULT_FACTOR);

        this.valueTag.setVisible(false);
        this.numericValueContainer.setVisible(false);
        this.graphicalValueContainer.setVisible(true);
        this.physicalOptionComboBox.setVisible(false);

        this.mGraphicalSeries = new XYChart.Series<>();
        this.mGraphicalSeries.setName(mCurrentItem.getUnit());

        int xVal = 10;
        if (plotYSavedData != null && !plotYSavedData.isEmpty()) {
            for (int graphicValue : plotYSavedData) {
                this.mGraphicalSeries.getData().add(new XYChart.Data<>(xVal, graphicValue));
                xVal += 10;
            }
        } else {
            for (int graphicValue : mCurrentItem.getPlotY()) {
                this.mGraphicalSeries.getData().add(new XYChart.Data<>(xVal, graphicValue));
                xVal += 10;
            }
        }

        this.signalGraphicalLineChart.getData().clear();
        this.signalGraphicalLineChart.getData().add(this.mGraphicalSeries);

        int maxY = Collections.max(mCurrentItem.getPlotY(), null);
        int minY = Collections.min(mCurrentItem.getPlotY(), null);

        for (XYChart.Data<Number, Number> point : this.mGraphicalSeries.getData()) {
            point.getNode().setOnMouseDragged(event -> {
                Point2D translateXY = point.getNode().screenToLocal(event.getScreenX(), event.getScreenY());

//                point.setXValue((int)translateXY.getX() + point.getXValue().intValue());
                int value = (int) (point.getYValue().floatValue() + reverseNumberInRange((float) translateXY.getY() +
                        (maxY - point.getYValue().floatValue()), minY, maxY) * 0.02);
                if (value > maxY) value = maxY;
                else if (value < minY) value = minY;
                point.setYValue(value);
            });
        }

        this.signalGraphicalLineChart.setAnimated(false);

        // Binds the values to the linechart

    }

    /**
     * Aux method for showGraphicalSignalUI().
     * y0 is top left on screen, and bottom left in chart, need to reverse.
     * TODO: Refactor this logic into a Linechart helper class
     *
     * @param value
     * @param minVal
     * @param maxVal
     * @return
     */
    private double reverseNumberInRange(double value, double minVal, double maxVal) {
        return (maxVal + minVal) - value;
    }

    /**
     * Method that returns the root view
     *
     * @return StacePane (root view)
     */
    public StackPane getNewSignalItemRootDialog() {

        // Set default values
        setDefaultSignalData();

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
        this.mRootWidth = width;
        this.mRootHeight = height;

        newSignalRoot.setPrefWidth(width);
        newSignalRoot.setPrefHeight(height);

        // Set default values
        setDefaultSignalData();

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

                if (mCurrentItem.isNumericalSignal()) {
                    mSignalModel.setValue(String.valueOf(signalNumericValue.getValue()));
                } else if (mCurrentItem.isCategoricalSignal()) {
                    mSignalModel.setValue(String.valueOf(physicalOptionComboBox.getValue()));
                } else {

                    List<Integer> yValues = new ArrayList<>(mGraphicalSeries.getData().size());
                    for (int i = 0; i < mGraphicalSeries.getData().size(); i++) {
                        yValues.add(mGraphicalSeries.getData().get(i).getYValue().intValue());
                    }
                    mSignalModel.setPlotYValue(yValues);
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
