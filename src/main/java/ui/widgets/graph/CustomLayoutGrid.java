/*
 * Created by Filipe André Rodrigues on 13-04-2019 2:53
 */

package ui.widgets.graph;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.layout.RandomLayout;

import java.util.List;
import java.util.Random;

public class CustomLayoutGrid extends RandomLayout {

    public CustomLayoutGrid(){
    }

    @Override
    public void execute(Graph graph) {
        // TODO: Improve this algorithm to guarantee not to overlap
        final List<ICell> cells = graph.getModel().getAllCells();

        int xInc = 100, yPos = 50, index = 0;
        for (final ICell cell : cells) {
            final double x = xInc;
            final double y = yPos;

            graph.getGraphic(cell).relocate(x, y);
            index++;

            if(index < 5){
                xInc += ((TextableRectangleCell)cell).getPrefWidth() + 80;
            } else{
                index = 0;
                yPos += 160;
                xInc = 150 + yPos / 2;

            }
        }
    }

}
