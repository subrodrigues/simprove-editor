/*
 * Created by Filipe André Rodrigues on 02-03-2019 2:13
 */

package utils;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationUtils {

    /**
     * //this animation changes the background color
     * //of the VBox from red with opacity=1
     * //to red with opacity=0
     */
    public static void itemEaseInHighlight(Pane highlightPane) {
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(1000));
                setInterpolator(Interpolator.EASE_IN);
            }

            @Override
            protected void interpolate(double frac) {
                double opacity = frac > 0.45 ? 0.45 : frac;

                Color vColor = new Color(1, 0, 0, opacity);
                highlightPane.setBackground(new Background(new BackgroundFill(vColor, new CornerRadii(5), Insets.EMPTY)));
            }
        };
        animation.play();
    }


    /**
     * //this animation changes the background color
     * //of the VBox from red with opacity=1
     * //to red with opacity=0
     */
    public static void itemEaseOutHighlight(Pane highlightPane) {
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(1000));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                double opacity = 0.45 - frac >= 0 ? 0.45 - frac : 0;

                Color vColor = new Color(1, 0, 0, opacity);
                highlightPane.setBackground(new Background(new BackgroundFill(vColor, new CornerRadii(5), Insets.EMPTY)));
            }
        };
        animation.play();
    }

}
