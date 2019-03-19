/*
 * Created by Filipe André Rodrigues on 06-03-2019 22:35
 */

package ui.scenario.inflatables;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import dao.model.ActionModel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.DisplayUtils;

import java.io.IOException;

import static javafx.animation.Interpolator.EASE_BOTH;

public class ActionItemViewController {
    // UI Bind variables
    @FXML
    private StackPane actionItemRoot;

    @FXML
    private StackPane headerPane;

    @FXML
    private Text actionName;

    @FXML
    private Text actionType;

    @FXML
    private Text actionCategory;

    @FXML
    private Pane highlightCard;

    // Private variables
    private ActionModel mActionModel;
    private OnScenarioActionClickListener mListener;
    private int mActionId = -1;
    private String mHeaderColor;

    public interface OnScenarioActionClickListener {
        void onActionEditClicked(int actionId);

        void onActionSelectClicked(int actionId);

    }

    public ActionItemViewController(int actionId) {
        this.mActionId = actionId;
    }

    public ActionItemViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/ActionItem.fxml"));
        fxmlLoader.setController(this);
        try {
            this.actionItemRoot = fxmlLoader.load();
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
    public ActionItemViewController(int indexColor, OnScenarioActionClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/ActionItem.fxml"));
        fxmlLoader.setController(this);
        try {
            this.actionItemRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupUI(indexColor);
    }

    private void setupUI(int indexColor) {
        // Random header color
        mHeaderColor = DisplayUtils.getRandomBrightPastelColor();
        headerPane.setStyle("-fx-background-radius: 5 5 0 0; -fx-background-color: " + mHeaderColor);

        this.actionItemRoot.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mListener.onActionSelectClicked(mActionModel.getId());
            }
        });
    }

    public void setupAction(ActionModel action) {
        this.mActionModel = action;
        this.mActionId = this.mActionModel.getId();
        this.setActionName(this.mActionModel.getName());

//        this.actionType.setText(action.getTypeId().getName());
        this.actionCategory.setText(action.getCategory().getName());
    }

    private void setActionName(String actionName) {
        this.actionName.setText(actionName);
    }

    public StackPane getStateItemRootPane() {
        return actionItemRoot;
    }

    public void setupAnimatedEditFab(Duration delayToAnimate) {
        JFXButton button = new JFXButton("");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setStyle("-fx-background-radius: 40;-fx-background-color: -fx-decorator-lighter-color");
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

        this.actionItemRoot.getChildren().add(button);

        button.setOnAction(getActionEditClickListener());
    }

    /**
     * Method that implements the StateEdit click listener behavior
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getActionEditClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mListener.onActionEditClicked(mActionModel.getId());
            }
        };
    }

}
