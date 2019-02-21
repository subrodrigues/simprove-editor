/*
 * Created by Filipe André Rodrigues on 20-02-2019 21:20
 */

package ui.scenario.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.DisplayUtils;

import java.io.IOException;
import java.util.Random;

import static javafx.animation.Interpolator.EASE_BOTH;

public class StateItemController {
    // UI Bind variables
    @FXML
    private StackPane stateItemRoot;

    @FXML
    private StackPane headerPane;

    @FXML
    private Text stateName;

    private String mHeaderColor;

    public StateItemController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/StateItem.fxml"));
        fxmlLoader.setController(this);
        try
        {
            stateItemRoot = fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        setupUI(DisplayUtils.NUM_COLORS);
    }

    /**
     * Constructor with index in order to avoid the consecutive creation of same header color
     *
     * @param indexColor
     */
    public StateItemController(int indexColor) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/StateItem.fxml"));
        fxmlLoader.setController(this);
        try
        {
            stateItemRoot = fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        setupUI(indexColor);
    }

    private void setupUI(int indexColor){
        // Random header color
        mHeaderColor = DisplayUtils.getColorByIndex(indexColor);
        headerPane.setStyle("-fx-background-radius: 5 5 0 0; -fx-background-color: " + mHeaderColor);
    }

    public Text getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName.setText(stateName);
    }

    public StackPane getStateItemRootPane(){
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
                "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 "
                        + "0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 "
                        + "0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 "
                        + "528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143"
                        + " 11.429 0 20.571 6.286z",
                Color.WHITE);
        glyph.setSize(20, 20);
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
    }
}
