/*
 * Created by Filipe André Rodrigues on 10-07-2019 19:32
 */

package ui.widgets;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SelectListController<T> {
    // UI Bind variables
    @FXML
    private StackPane selectListRoot;

    @FXML
    private Text title;

    @FXML
    private JFXListView<T> list;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    private OnMultiSelectListClickListener mListener;

    public interface OnMultiSelectListClickListener {
        <T> void onSelectListApplyClick(List<T> selectedItems);
    }

    public SelectListController(String title,
                                List<T> listContent,
                                List<T> listSelected,
                                OnMultiSelectListClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/SelectListDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            selectListRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupUI(title, listContent, listSelected);
    }


    private void setupUI(String title, List<T> listContent, List<T> listSelected) {
        this.title.setText(title);

        setMultipleSelectionBehavior();

        // Set Lists custom style
        this.list.setId("multi-list");

        this.list.getItems().addAll(listContent);
        setSelectedItems(this.list, listSelected);

        this.applyButton.setOnAction(getApplyClickListener());
        this.cancelButton.setOnAction(getCancelClickListener());
    }

    private void setMultipleSelectionBehavior() {
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != list && !(node instanceof ListCell)) {
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
    }

    public StackPane getItemRoot() {
        return selectListRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) selectListRoot.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    /**
     * Method that implements the Apply changes click listener behavior.
     * It creates the lists os conditions, and return it.
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getApplyClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mListener.onSelectListApplyClick(list.getSelectionModel().getSelectedItems());

                closeDialogWindow();
            }
        };
    }

    /**
     * Method that implements the positive confirmation to cancel
     *
     * @return the EventHandler with correspondent behavior
     */
    private EventHandler<ActionEvent> getCancelClickListener() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                closeDialogWindow();
            }
        };
    }

    /**
     * Method that set the already selected items on a JFXListView
     *
     * @param listItems
     * @param listSelectedItems
     */
    private void setSelectedItems(JFXListView<T> listItems, List<T> listSelectedItems) {
        int i = 0;
        for(T item: listItems.getItems()){
            for(T selectedItem: listSelectedItems) {
                if (item.equals(selectedItem)){
                    listItems.getSelectionModel().selectIndices(i);
                    break;
                }
            }
            i++;
        }
    }

}
