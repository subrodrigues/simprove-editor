/*
 * Created by Filipe André Rodrigues on 13-04-2019 1:49
 */

package ui.widgets.graph;

import com.fxgraph.cells.CellGestures;
import com.fxgraph.cells.RectangleCell;
import com.fxgraph.graph.Graph;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class TextableRectangleCell extends RectangleCell {
    private int mId;
    private String mName;
    private double mPrefWidth = 0;

    public TextableRectangleCell(String name, int id) {
        this.mId = id;
        this.mName = name;
    }

    @Override
    public Region getGraphic(Graph graph) {
        final Rectangle view = new Rectangle(50, 50);

        view.setStroke(Color.valueOf("#616161"));
        view.setStrokeWidth(2);
        view.setFill(Color.valueOf("#484848"));

        Text name = new Text(this.mName);
        name.getStyleClass().add("states-graph-text");

        final StackPane pane = new StackPane();
        pane.setMinSize(name.getBoundsInLocal().getWidth(), name.getBoundsInLocal().getHeight());
        this.mPrefWidth = name.getBoundsInLocal().getWidth() + 26;
        pane.setPrefSize(this.mPrefWidth, 60);
        view.widthProperty().bind(pane.prefWidthProperty());
        view.heightProperty().bind(pane.prefHeightProperty());
        CellGestures.makeResizable(pane);
        view.getStyleClass().add("item-card-style");


        pane.getChildren().addAll(view, name);

        return pane;
    }

    public int getId(){
        return this.mId;
    }

    public double getPrefWidth(){
        return this.mPrefWidth;
    }

}
