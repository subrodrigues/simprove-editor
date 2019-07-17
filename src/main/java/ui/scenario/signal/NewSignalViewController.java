/*
 * Created by Filipe André Rodrigues on 15-04-2019 18:11
 */

package ui.scenario.signal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import dao.model.*;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.widgets.AutoCompleteComboBoxListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewSignalViewController {
    public static final double GRAPHICAL_WINDOW_MULT_FACTOR = 1.3;

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
    private JFXButton acceptButton;

    @FXML
    private JFXButton cancelButton;

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
    private OnNewSignalClickListener mListener;
    private int mStateId = -1;

    private List<SignalTemplateModel> mSignalTypes;
    private SignalTemplateModel mCurrentItem;

    private double mRootWidth;
    private double mRootHeight;
    private XYChart.Series<Number, Number> mGraphicalSeries;

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

                if (mCurrentItem.isNumericalSignal()) {
                    showNumericSignalUI();
                } else if (mCurrentItem.isCategoricalSignal()) {
                    showCategoricalSignalUI();
                } else {
                    showGraphicalSignalUI();
                }

                //TODO: Check type and create value input
            }
        });

        this.acceptButton.setDisable(true);
        this.acceptButton.disableProperty().bind(
                Bindings.isEmpty(this.signalTypeComboBox.getEditor().textProperty()));

        this.cancelButton.setOnAction(getCancelClickListener());
        this.acceptButton.setOnAction(getNewSignalAcceptClickListener());
    }

    /**
     * Method that updates the dialog window to the Graphical interface
     */
    private void showGraphicalSignalUI() {
        this.signalGraphicalLineChart.setAnimated(true);

        this.newSignalRoot.setMinWidth(this.mRootWidth * GRAPHICAL_WINDOW_MULT_FACTOR);
        this.newSignalRoot.setMinHeight(this.mRootHeight * GRAPHICAL_WINDOW_MULT_FACTOR);

        this.valueTag.setVisible(false);
        this.numericValueContainer.setVisible(false);
        this.graphicalValueContainer.setVisible(true);
        this.physicalOptionComboBox.setVisible(false);

        this.mGraphicalSeries = new XYChart.Series<>();
        this.mGraphicalSeries.setName(mCurrentItem.getUnit());

        int xVal = 10;
        for (int graphicValue : mCurrentItem.getPlotY()) {
            this.mGraphicalSeries.getData().add(new XYChart.Data<>(xVal, graphicValue));
            xVal += 10;
        }

        this.signalGraphicalLineChart.getData().clear();
        this.signalGraphicalLineChart.getData().add(this.mGraphicalSeries);

        int maxY = Collections.max(mCurrentItem.getPlotY(), null);
        int minY = Collections.min(mCurrentItem.getPlotY(), null);

        for (XYChart.Data<Number, Number> point : this.mGraphicalSeries.getData()) {
            point.getNode().setOnMouseDragged(event -> {
                Point2D translateXY = point.getNode().screenToLocal(event.getScreenX(), event.getScreenY());

//                point.setXValue((int)translateXY.getX() + point.getXValue().intValue());
                int value = (int)(point.getYValue().floatValue() + reverseNumberInRange((float)translateXY.getY() +
                        (maxY - point.getYValue().floatValue()), minY, maxY) * 0.02);
                if(value > maxY) value = maxY;
                else if (value < minY) value = minY;
                point.setYValue(value);
            });
        }

        this.signalGraphicalLineChart.setAnimated(false);
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
        this.mRootWidth = width;
        this.mRootHeight = height;

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
                mSignalModel.setType(mCurrentItem.getType());
                mSignalModel.setName(signalTypeComboBox.getEditor().textProperty().getValue());

                if (mCurrentItem.isNumericalSignal()) {
                    mSignalModel.setValue(String.valueOf(signalNumericValue.getValue()));
                } else if(mCurrentItem.isCategoricalSignal()){
                    mSignalModel.setValue(String.valueOf(physicalOptionComboBox.getValue()));
                } else{

                    List<Integer> yValues = new ArrayList<>(mGraphicalSeries.getData().size());
                    for(int i = 0; i < mGraphicalSeries.getData().size(); i++){
                        yValues.add(mGraphicalSeries.getData().get(i).getYValue().intValue());
                    }
                    mSignalModel.setPlotYValue(yValues);
                }

                mSignalModel.setTemplate(mCurrentItem);

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
