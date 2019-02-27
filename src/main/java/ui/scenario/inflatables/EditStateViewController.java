/*
 * Created by Filipe André Rodrigues on 27-02-2019 15:00
 */

package ui.scenario.inflatables;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.svg.SVGGlyph;
import dao.model.StateModel;
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
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.DisplayUtils;

import java.io.IOException;

import static javafx.animation.Interpolator.EASE_BOTH;

public class EditStateViewController {
    // UI Bind variables
    @FXML
    private StackPane editStateRoot;

    @FXML
    private JFXTextField inputName;
    @FXML
    private JFXTextField inputType;
    @FXML
    private JFXTextField inputTransition;

    // Private variables
    private StateModel mStateModel;
    private OnScenarioEditStateClickListener mListener;
    private int mStateId = -1;

    public interface OnScenarioEditStateClickListener {
        // TODO
    }

    public EditStateViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditStateDialog.fxml"));
        fxmlLoader.setController(this);
        try
        {
            editStateRoot = fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor with respective click listener.
     *
     * @param listener
     */
    public EditStateViewController(StateModel state, OnScenarioEditStateClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/EditStateDialog.fxml"));
        fxmlLoader.setController(this);
        try
        {
            editStateRoot = fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        setupState(state);
        setupUI();
    }

    private void setupState(StateModel state){
        this.mStateModel = state;
    }

    private void setupUI(){
        this.inputName.setText(mStateModel.getName());
        this.inputType.setText(mStateModel.getType().getType() + "");
        //TODO
    }

    public StackPane getEditStateItemRootDialog(){
        return editStateRoot;
    }

    public void setupAnimatedEditFab(Duration delayToAnimate) {
//        JFXButton button = new JFXButton("");
//        button.setButtonType(JFXButton.ButtonType.RAISED);
//        button.setStyle("-fx-background-radius: 40;-fx-background-color: " + DisplayUtils.getRandomColor());
//        button.setPrefSize(40, 40);
//        button.setRipplerFill(Color.valueOf(mHeaderColor));
//
//        button.setScaleX(0);
//        button.setScaleY(0);
//        SVGGlyph glyph = new SVGGlyph(-1,
//                "test",
//                "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z",
//                Color.WHITE);
//        glyph.setSize(18, 18);
//        button.setGraphic(glyph);
//        button.translateYProperty().bind(Bindings.createDoubleBinding(() -> {
//            return headerPane.getBoundsInParent().getHeight() - button.getHeight() / 2;
//        }, headerPane.boundsInParentProperty(), button.heightProperty()));
//
//        StackPane.setMargin(button, new Insets(0, 12, 0, 0));
//        StackPane.setAlignment(button, Pos.TOP_RIGHT);
//
//        Timeline animation = new Timeline(new KeyFrame(Duration.millis(240),
//                new KeyValue(button.scaleXProperty(),
//                        1,
//                        EASE_BOTH),
//                new KeyValue(button.scaleYProperty(),
//                        1,
//                        EASE_BOTH)));
//
//
//        animation.setDelay(delayToAnimate);
//        animation.play();
//
//        stateItemRoot.getChildren().add(button);

//        button.setOnAction(getStateEditClickListener());
    }

    /**
     * Method that implements the StateEdit click listener behavior
     * @return the EventHandler with correspondent behavior
     */
//    private EventHandler<ActionEvent> getStateEditClickListener(){
//        return new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent e) {
//                mListener.onStateEditClicked(mStateModel.getId());
//            }
//        };
//    }
}