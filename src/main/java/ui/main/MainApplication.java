/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package ui.main;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Flow flow = new Flow(MainApplicationUIView.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        decorator.setCustomMaximize(true);
        decorator.setGraphic(new SVGGlyph(
                ""));
        
        stage.setTitle("Simprove");

        double width = 1366;
        double height = 768;
        try {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            width = bounds.getWidth() / 1.5;
            height = bounds.getHeight() / 1.35;
//            stage.setX(bounds.getMinX());
//            stage.setY(bounds.getMinY());
        }catch (Exception e){ }

        Scene scene = new Scene(decorator, width, height);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(MainApplication.class.getResource(       "/css/jfoenix-fonts.css").toExternalForm(),
                MainApplication.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                MainApplication.class.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }

}
