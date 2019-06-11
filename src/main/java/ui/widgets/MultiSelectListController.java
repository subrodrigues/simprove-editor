/*
 * Created by Filipe André Rodrigues on 07-06-2019 17:59
 */

package ui.widgets;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class MultiSelectListController {
    // UI Bind variables
    @FXML
    private StackPane multiSelectListRoot;

    @FXML
    private Text title;

    @FXML
    private Tab firstTab;

    @FXML
    private Tab secondTab;

    @FXML
    private TabPane tabPane;

    @FXML
    private JFXListView firstList;

    @FXML
    private JFXListView secondList;

    public MultiSelectListController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/MultiSelectListDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            multiSelectListRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        firstList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        firstList.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != firstList && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof ListCell) {
                // prevent further handling
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                // focus the listview
                lv.requestFocus();

                if (!cell.isEmpty()) {
                    // handle selection for non-empty cells
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });

        secondList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        secondList.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != firstList && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof ListCell) {
                // prevent further handling
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                // focus the listview
                lv.requestFocus();

                if (!cell.isEmpty()) {
                    // handle selection for non-empty cells
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });

        tabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            final double tabWidth = tabPane.getWidth() / 2;

            tabPane.setTabMinWidth(tabWidth);
            tabPane.setTabMaxWidth(tabWidth);
            tabPane.setTabMaxWidth(tabWidth);
        });

        final double tabPaneWidth = (tabPane.getTabs().size()) + 55;
        tabPane.setMinWidth(tabPaneWidth);//set the tabPane's minWidth and maybe max width to the tabs combined width + a padding value
        tabPane.setPrefWidth(tabPaneWidth);//set the tabPane's minWidth and maybe max width to the tabs combined width + a padding value

    }

    public StackPane getItemRoot() {
        return multiSelectListRoot;
    }
}
