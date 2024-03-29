/*
 * Created by Filipe André Rodrigues on 13-04-2019 3:35
 */

package ui.widgets.graph;

import com.fxgraph.edges.AbstractEdge;
import com.fxgraph.edges.DoubleCorneredEdge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

public class DirectionalCorneredEdge extends AbstractEdge {

    private final StringProperty textProperty;
    private final Orientation orientation;
    private final boolean isVisible;

    public DirectionalCorneredEdge(ICell source, ICell target, boolean isVisible, Orientation orientation) {
        super(source, target);
        this.orientation = orientation;
        this.textProperty = new SimpleStringProperty();
        this.isVisible = isVisible;
    }

    @Override
    public DirectionalCorneredEdge.EdgeGraphic getGraphic(Graph graph) {
        return new DirectionalCorneredEdge.EdgeGraphic(graph, this, isVisible, orientation, textProperty);
    }

    public StringProperty textProperty() {
        return textProperty;
    }

    public static class EdgeGraphic extends Pane {

        private final DoubleBinding sourceX;
        private final DoubleBinding sourceY;
        private final DoubleBinding targetX;
        private final DoubleBinding targetY;
        private final DoubleBinding centerX;
        private final DoubleBinding centerY;
        private final Group group;
        private final Line lineA;
        private final Line lineB;
        private final Line lineC;
        private Polygon triangle = null;
        private final Text text;

        private DoubleBinding dx;
        private DoubleBinding dy;

        private final boolean isVisible;

        public EdgeGraphic(Graph graph, DirectionalCorneredEdge edge, boolean isVisible, Orientation orientation, StringProperty textProperty) {
            this.isVisible = isVisible;

            sourceX = edge.getSource().getXAnchor(graph, edge);
            sourceY = edge.getSource().getYAnchor(graph, edge);
            targetX = edge.getTarget().getXAnchor(graph, edge);
            targetY = edge.getTarget().getYAnchor(graph, edge);

            centerX = sourceX.add(targetX).divide(2);
            centerY = sourceY.add(targetY).divide(2);

            text = new Text();
            text.textProperty().bind(textProperty);
            text.getStyleClass().add("states-edge-graph-text");
            final DoubleProperty textWidth = new SimpleDoubleProperty();
            final DoubleProperty textHeight = new SimpleDoubleProperty();
            final Runnable recalculateWidth = () -> {
                textWidth.set(text.getLayoutBounds().getWidth());
                textHeight.set(text.getLayoutBounds().getHeight());
            };
            text.parentProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());
            text.textProperty().addListener((obs, oldVal, newVal) -> recalculateWidth.run());
            text.setStroke(Color.WHITE);
            text.setStrokeWidth(0.15);

            group = new Group();
            lineA = new Line();
            lineB = new Line();
            lineC = new Line();

            if (isVisible) {
                drawLine(orientation);
            }
            text.xProperty().bind(centerX.subtract(textWidth.divide(2)));
            text.yProperty().bind(centerY.subtract(textHeight.divide(2)));

            group.getChildren().add(text);
            getChildren().add(group);
        }

        private void drawLine(Orientation orientation) {
            if (orientation == Orientation.HORIZONTAL) {

                lineA.startXProperty().bind(sourceX);
                lineA.startYProperty().bind(sourceY);
                lineA.endXProperty().bind(centerX);
                lineA.endYProperty().bind(sourceY);
                lineA.setStrokeWidth(1);
                lineA.setStroke(Color.rgb(97, 97, 97, 0.60));

                group.getChildren().add(lineA);

                lineB.startXProperty().bind(centerX);
                lineB.startYProperty().bind(sourceY);
                lineB.endXProperty().bind(centerX);
                lineB.endYProperty().bind(targetY);
                lineB.setStrokeWidth(2);
                lineB.setStroke(Color.rgb(97, 97, 97, 0.80));

                group.getChildren().add(lineB);

                lineC.startXProperty().bind(centerX);
                lineC.startYProperty().bind(targetY);
                lineC.endXProperty().bind(targetX);
                lineC.endYProperty().bind(targetY);
                lineC.setStrokeWidth(4);
                lineC.setStroke(Color.rgb(100, 149, 237, 1.00));


                /** Create Triangle (Arrow) */

                dx = lineB.endXProperty().add(lineB.startXProperty().negate());
                dy = lineB.endYProperty().add(lineB.startYProperty().negate());

                triangle = new Polygon(0, 0, -32, 16, -32, -16);
                triangle.setFill(Color.rgb(100, 149, 237, 1.00));
                Rotate rotate = new Rotate(0, 0, 0, 1, Rotate.Z_AXIS);
                triangle.getTransforms().add(rotate);

                dx.addListener((observable, oldValue, newValue) -> {
                    rotate.setAngle(getAngle(dy.doubleValue(), newValue.doubleValue()));
                });

                dy.addListener((observable, oldValue, newValue) -> {
                    rotate.setAngle(getAngle(newValue.doubleValue(), dx.doubleValue()));
                });

                triangle.layoutXProperty().bind(centerX);
                triangle.layoutYProperty().bind(centerY);

                group.getChildren().addAll(lineC, triangle);


                /** Create Triangle (Arrow) */

            } else {

                lineA.startXProperty().bind(sourceX);
                lineA.startYProperty().bind(sourceY);
                lineA.endXProperty().bind(sourceX);
                lineA.endYProperty().bind(centerY);
                group.getChildren().add(lineA);

                lineB.startXProperty().bind(sourceX);
                lineB.startYProperty().bind(centerY);
                lineB.endXProperty().bind(targetX);
                lineB.endYProperty().bind(centerY);
                group.getChildren().add(lineB);

                lineC.startXProperty().bind(targetX);
                lineC.startYProperty().bind(centerY);
                lineC.endXProperty().bind(targetX);
                lineC.endYProperty().bind(targetY);
                group.getChildren().add(lineC);
            }
        }


        private double getAngle(double dy, double dx) {
            return Math.toDegrees(Math.atan2(dy, dx));
        }

        public DoubleBinding getSourceX() {
            return sourceX;
        }

        public DoubleBinding getSourceY() {
            return sourceY;
        }

        public DoubleBinding getTargetX() {
            return targetX;
        }

        public DoubleBinding getTargetY() {
            return targetY;
        }

        public DoubleBinding getCenterX() {
            return centerX;
        }

        public DoubleBinding getCenterY() {
            return centerY;
        }

        public Group getGroup() {
            return group;
        }

        public Line getLineA() {
            return lineA;
        }

        public Line getLineB() {
            return lineB;
        }

        public Line getLineC() {
            return lineC;
        }

        public Text getText() {
            return text;
        }

    }

}