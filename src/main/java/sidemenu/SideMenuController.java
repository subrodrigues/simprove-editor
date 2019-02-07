package sidemenu;

import com.jfoenix.controls.JFXListView;
import uicomponents.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;
import java.util.Objects;

@ViewController(value = "/fxml/SideMenu.fxml", title = "Side Menu Title")
public class SideMenuController {

    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    @ActionTrigger("new_scenario")
    private Label newScenario;
    @FXML
    @ActionTrigger("load_scenario")
    private Label loadScenario;

    @FXML
    private JFXListView<Label> sideList;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
//        Objects.requireNonNull(context, "context");
//        FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
//        sideList.propagateMouseEventsToParent();
//        sideList.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
//            new Thread(()->{
//                Platform.runLater(()->{
//                    if (newVal != null) {
//                        try {
//                            contentFlowHandler.handle(newVal.getId());
//                        } catch (VetoException exc) {
//                            exc.printStackTrace();
//                        } catch (FlowException exc) {
//                            exc.printStackTrace();
//                        }
//                    }
//                });
//            }).start();
//        });
//        Flow contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
//        bindNodeToController(newScenario, NewScenarioController.class, contentFlow, contentFlowHandler);
//        bindNodeToController(loadScenario, CheckboxController.class, contentFlow, contentFlowHandler);

    }

    private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
        flow.withGlobalLink(node.getId(), controllerClass);
    }

}
