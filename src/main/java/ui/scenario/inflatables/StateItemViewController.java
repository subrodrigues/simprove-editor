/*
 * Created by Filipe André Rodrigues on 20-02-2019 21:20
 */

package ui.scenario.inflatables;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import dao.model.StateModel;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.DisplayUtils;

import java.io.IOException;

import static javafx.animation.Interpolator.EASE_BOTH;

public class StateItemViewController {
    // UI Bind variables
    @FXML
    private StackPane stateItemRoot;

    @FXML
    private StackPane headerPane;

    @FXML
    private Text stateName;

    @FXML
    private Pane highlightCard;

    // Private variables
    private StateModel mStateModel;
    private OnScenarioStateClickListener mListener;
    private int mStateId = -1;
    private String mHeaderColor;

    public interface OnScenarioStateClickListener {
        void onStateEditClicked(int stateId);

        void onStateSelectClicked(int stateId);

    }

    public StateItemViewController(int stateId) {
        mStateId = stateId;
    }

    public StateItemViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/StateItem.fxml"));
        fxmlLoader.setController(this);
        try {
            stateItemRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupUI(DisplayUtils.NUM_COLORS);
    }

    /**
     * Constructor with index in order to avoid the consecutive creation of same header color
     *
     * @param indexColor
     */
    public StateItemViewController(int indexColor, OnScenarioStateClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/StateItem.fxml"));
        fxmlLoader.setController(this);
        try {
            stateItemRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupUI(indexColor);
    }

    private void setupUI(int indexColor) {
        // Random header color
        mHeaderColor = DisplayUtils.getRandomBrightPastelColor();
        headerPane.setStyle("-fx-background-radius: 5 5 0 0; -fx-background-color: " + mHeaderColor);

        stateItemRoot.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mListener.onStateSelectClicked(mStateModel.getId());
            }
        });
    }

    public void setupState(StateModel state) {
        this.mStateModel = state;
        this.mStateId = mStateModel.getId();
        this.setStateName(this.mStateModel.getName());
    }

    private void setStateName(String stateName) {
        this.stateName.setText(stateName);
    }

    public StackPane getStateItemRootPane() {
        return stateItemRoot;
    }

    public void setupAnimatedEditFab(Duration delayToAnimate) {
        JFXButton button = new JFXButton("");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setStyle("-fx-background-radius: 40;-fx-background-color: " + DisplayUtils.getRandomColor());
        button.setPrefSize(40, 40);
        button.setRipplerFill(Color.valueOf(mHeaderColor));

        button.setScaleX(0);
        button.setScaleY(0);
        SVGGlyph glyph = new SVGGlyph(-1,
                "test",
                "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z",
                Color.WHITE);
        glyph.setSize(18, 18);
        button.setGraphic(glyph);
        button.translateYProperty().bind(Bindings.createDoubleBinding(() -> {
            return headerPane.getBoundsInParent().getHeight() - button.getHeight() / 2;
        }, headerPane.boundsInParentProperty(), button.heightProperty()));

        StackPane.setMargin(button, new Insets(0, 12, 0, 0));
        StackPane.setAlignment(button, Pos.TOP_RIGHT);

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(240),
                new KeyValue(button.scaleXProperty(),
                        1,
                        EASE_BOTH),
                new KeyValue(button.scaleYProperty(),
                        1,
                        EASE_BOTH)));


        animation.setDelay(delayToAnimate);
        animation.play();

        stateItemRoot.getChildren().add(button);

        button.setOnAction(getStateEditClickListener());
    }

    /**
     * Method that implements the StateEdit click listener behavior
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getStateEditClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mListener.onStateEditClicked(mStateModel.getId());
            }
        };
    }

}
