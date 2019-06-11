/*
 * Created by Filipe André Rodrigues on 07-06-2019 17:59
 */

package ui.widgets;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
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
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class MultiSelectListController<T> {
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
    private JFXListView<T> firstList;

    @FXML
    private JFXListView<T> secondList;

    @FXML
    private JFXButton applyButton;

    @FXML
    private JFXButton cancelButton;

    private OnMultiSelectListClickListener mListener;

    public interface OnMultiSelectListClickListener {
        <T> void onMultiSelectListApplyClick(List<T> firstListIds, List<T> secondListIds);
    }

    public MultiSelectListController(String title,
                                     String firstTabTitle,
                                     String secondTabTitle,
                                     List<T> listContent,
                                     List<T> firstListSelected,
                                     List<T> secondListSelected,
                                     OnMultiSelectListClickListener listener) {
        this.mListener = listener;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ui/MultiSelectListDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            multiSelectListRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupUI(title, firstTabTitle, secondTabTitle, listContent, firstListSelected, secondListSelected);
    }


    private void setupUI(String title, String firstTabTitle, String secondTabTitle, List<T> listContent,
                         List<T> firstListSelected, List<T> secondListSelected) {
        this.title.setText(title);
        this.firstTab.setText(firstTabTitle);
        this.secondTab.setText(secondTabTitle);

        setMultipleSelectionBehavior();
        setTabPaneFitWidthBehavior();

        // Set Lists custom style
        this.firstList.setId("multi-list");
        this.secondList.setId("multi-list");

        this.firstList.getItems().addAll(listContent);
        this.secondList.getItems().addAll(listContent);

        setSelectedItems(this.firstList, firstListSelected);
        setSelectedItems(this.secondList, secondListSelected);

        this.applyButton.setOnAction(getApplyClickListener());
        this.cancelButton.setOnAction(getCancelClickListener());
    }

    private void setTabPaneFitWidthBehavior() {
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

    private void setMultipleSelectionBehavior() {
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
    }

    public StackPane getItemRoot() {
        return multiSelectListRoot;
    }

    /**
     * Method that closes this DialogWindow view
     */
    private void closeDialogWindow() {
        // get a handle to the stage
        Stage stage = (Stage) multiSelectListRoot.getScene().getWindow();
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

                mListener.onMultiSelectListApplyClick(firstList.getSelectionModel().getSelectedItems(),
                        secondList.getSelectionModel().getSelectedItems());

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
