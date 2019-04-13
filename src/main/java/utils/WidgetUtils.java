/*
 * Created by Filipe André Rodrigues on 13-04-2019 23:20
 */

package utils;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class WidgetUtils {

    public static void installTooltipOnNode(Node obj, String name){
        Tooltip tt = new Tooltip();
        tt.setText(name);
        tt.getStyleClass().add("tooltip-style");

        Tooltip.install(obj, tt);
    }

}
