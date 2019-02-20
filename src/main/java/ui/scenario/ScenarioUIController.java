/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import dao.model.ScenarioModel;
import dao.model.StateModel;
import io.datafx.controller.ViewController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ui.scenario.controllers.StateItemController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static javafx.animation.Interpolator.EASE_BOTH;

@ViewController(value = "/fxml/ui/Scenario.fxml", title = "New Scenario Title")
public class ScenarioUIController {

    private ScenarioPresenter mPresenter;

    // UI Bind variables
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private JFXMasonryPane masonryPane;

    @FXML
    private ScrollPane statesScrollPane;
    @FXML
    private JFXMasonryPane statesMasonryPane;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        mPresenter = new ScenarioPresenter(this);
        setupUI();
    }

    /**
     * Setups the UI and init needed variables
     */
    private void setupUI() {
        Platform.runLater(() -> scrollPane.requestLayout());
        JFXScrollPane.smoothScrolling(scrollPane);

        Platform.runLater(() -> statesScrollPane.requestLayout());
        JFXScrollPane.smoothScrolling(statesScrollPane);
    }

//    ArrayList<Node> children = getRandomCards();
//
//        masonryPane.getChildren().addAll(children);
//
//    ArrayList<Node> children2 = getRandomCards();
//
//        statesMasonryPane.getChildren().addAll(children2);

    private String getDefaultColor(int i) {
        String color = "#FFFFFF";
        switch (i) {
            case 0:
                color = "#8F3F7E";
                break;
            case 1:
                color = "#B5305F";
                break;
            case 2:
                color = "#CE584A";
                break;
            case 3:
                color = "#DB8D5C";
                break;
            case 4:
                color = "#DA854E";
                break;
            case 5:
                color = "#E9AB44";
                break;
            case 6:
                color = "#FEE435";
                break;
            case 7:
                color = "#99C286";
                break;
            case 8:
                color = "#01A05E";
                break;
            case 9:
                color = "#4A8895";
                break;
            case 10:
                color = "#16669B";
                break;
            case 11:
                color = "#2F65A5";
                break;
            case 12:
                color = "#4E6A9C";
                break;
            default:
                break;
        }
        return color;
    }

    private Node getStateUICardView(int indexDelay, StateModel state){

        StackPane child = new StackPane();
        double width = 150;
        child.setPrefWidth(width);
        double height = 150;
        child.setPrefHeight(height);
        JFXDepthManager.setDepth(child, 1);

        // create content
        Text title = new Text();
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Roboto", FontWeight.BOLD, FontPosture.REGULAR, 14));
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(10, 10, 10,10));

        title.setText(state.getName());
        //setting the position of the text
        title.setX(1);
        title.setY(1);

        StackPane header = new StackPane();
        String headerColor = getDefaultColor(new Random().nextInt(13));
        header.setStyle("-fx-background-radius: 5 5 0 0; -fx-background-color: " + headerColor);

        VBox.setVgrow(header, Priority.ALWAYS);
        StackPane body = new StackPane();
        body.setMinHeight(60);
        VBox content = new VBox();
        content.getChildren().addAll(header, body);
        body.setStyle("-fx-background-radius: 0 0 5 5; -fx-background-color: rgb(255,255,255,0.87);");

        // create button
        JFXButton button = new JFXButton("");
        button.setButtonType(ButtonType.RAISED);
        button.setStyle("-fx-background-radius: 40;-fx-background-color: " + getDefaultColor(new Random().nextInt(13)));
        button.setPrefSize(40, 40);
        button.setRipplerFill(Color.valueOf(headerColor));
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
            return header.getBoundsInParent().getHeight() - button.getHeight() / 2;
        }, header.boundsInParentProperty(), button.heightProperty()));
        StackPane.setMargin(button, new Insets(0, 12, 0, 0));
        StackPane.setAlignment(button, Pos.TOP_RIGHT);

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(240),
                new KeyValue(button.scaleXProperty(),
                        1,
                        EASE_BOTH),
                new KeyValue(button.scaleYProperty(),
                        1,
                        EASE_BOTH)));
        animation.setDelay(Duration.millis(100 * indexDelay + 1000));
        animation.play();
        child.getChildren().addAll( content, button, title);

        return child;
    }

//    Node getInflatableStateItem(StateModel state) throws IOException {
//        AnchorPane statusUICard = null;
//
//        statusUICard = (AnchorPane) FXMLLoader.load(getClass().getResource("StateItem.fxml"));
////        StateItemController controller = statusUICard.getController();
//    }

    /**
     * Invoked by the Presenter.
     * Updates the view with a whole new Scenario.
     *
     * @param scenario
     */
    void updateScenarioData(ScenarioModel scenario) {
        // TODO: fill scenario related data

        // Fill States
        for(int i = 0; i < scenario.getStates().size(); i++){
            Node state = getStateUICardView(i, scenario.getStates().get(i));
            statesMasonryPane.getChildren().add(state);
        }
    }

    /**
     * Shows the loading view.
     *
     */
    void showLoading() {
        //TODO: Deal with this
    }

    /**
     * Hides the loading view.
     *
     */
    void hideLoading() {
        //TODO: Deal with this
    }

    /**
     * Shows the connection error view.
     * When a connection error occurs, while trying to acquire data from the API.
     */
    void showConnectionError() {
        //TODO: Deal with this
    }

    /**
     * Shows the generic error view.
     * When any error occurs.
     */
    void showGenericErrorView() {
        //TODO: Deal with this
    }
}
