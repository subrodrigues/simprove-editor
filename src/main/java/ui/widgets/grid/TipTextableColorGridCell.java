/*
 * Created by Filipe André Rodrigues on 11-06-2019 18:34
 */

package ui.widgets.grid;

import dao.model.SignalModel;
import dao.model.TipModel;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.controlsfx.control.GridCell;
import utils.DisplayUtils;
import utils.WidgetUtils;

public class TipTextableColorGridCell extends GridCell<TipModel> {
    private static final boolean debug = false;
    final String IDLE_BUTTON_STYLE = "-fx-effect: null;";
    final String HOVERED_BUTTON_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(13, 213, 252,0.7), 10, 0.8, 0, 0)";

    private int mId;
    private Rectangle mColorRect;
    private String mName;

    private OnTextableColorGridClickListener mListener;

    public interface OnTextableColorGridClickListener {
        void onTipGridItemClick(TipModel clickedItem);
    }

    /**
     * Creates a default ColorGridCell instance.
     */
    public TipTextableColorGridCell(OnTextableColorGridClickListener listener) {
        this.mListener = listener;

        this.mColorRect = new Rectangle(60, 60);

        this.mColorRect.setStroke(Color.valueOf("#616161"));
        this.mColorRect.setStrokeWidth(2);

        this.mColorRect.setFill(Color.valueOf(DisplayUtils.getRandomBrightPastelColor()));

        Text textName = new Text("");

        final StackPane pane = new StackPane();
        pane.setMinSize(textName.getBoundsInLocal().getWidth(), textName.getBoundsInLocal().getHeight());

        double width = textName.getBoundsInLocal().getWidth() + 26;
        pane.setPrefSize(width, 60);
        this.mColorRect.widthProperty().bind(pane.prefWidthProperty());
        this.mColorRect.heightProperty().bind(pane.prefHeightProperty());

        pane.getChildren().addAll(this.mColorRect, textName);
        setGraphic(pane);

        setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {

            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getClickCount() >= 2) {
                    mListener.onTipGridItemClick(getItem());
                }
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override protected void updateItem(TipModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            this.mId = item.getId();
            this.mName = item.getMessage();

            final StackPane pane = new StackPane();
            Text textName = new Text("#" + String.valueOf(this.mId));
            textName.getStyleClass().add("states-graph-text");

            pane.setMinSize(textName.getBoundsInLocal().getWidth(), textName.getBoundsInLocal().getHeight());
            pane.setPrefSize(40, 40);
            mColorRect.widthProperty().bind(pane.prefWidthProperty());
            mColorRect.heightProperty().bind(pane.prefHeightProperty());
            mColorRect.getStyleClass().add("item-grid-style");

            pane.getChildren().addAll(mColorRect, textName);
            pane.setStyle(IDLE_BUTTON_STYLE);
            pane.setOnMouseEntered(e -> mColorRect.setStyle(HOVERED_BUTTON_STYLE));
            pane.setOnMouseExited(e -> mColorRect.setStyle(IDLE_BUTTON_STYLE));

            setGraphic(pane);

            WidgetUtils.installTooltipOnNode(pane, this.mName);
        }

        if (debug) {
            setText(getIndex() + ""); //$NON-NLS-1$
        }
    }
}
